package com.uin.product.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uin.constant.ProductConstant;
import com.uin.product.dao.SpuInfoDao;
import com.uin.product.entity.*;
import com.uin.product.feign.SearchFeignService;
import com.uin.product.feign.SpuFeignService;
import com.uin.product.feign.WareFeignService;
import com.uin.product.service.*;
import com.uin.product.vo.*;
import com.uin.to.SkuHasStcokVo;
import com.uin.to.SkuReductionTo;
import com.uin.to.SpuBoundsTo;
import com.uin.to.es.SpuEsTO;
import com.uin.utils.PageUtils;
import com.uin.utils.Query;
import com.uin.utils.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    SpuImagesService spuImagesService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private SpuFeignService spuFeignService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    //TODO 失败处理高级部分
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSpuSaveVo(SpuSaveVo spuSaveVo) {
        //1.保存spu的基本信息 pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.baseMapper.insert(spuInfoEntity);

        //2.保存spu的描述图片 pms_spu_info_desc
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        List<String> decript = spuSaveVo.getDecript();
        spuInfoDescEntity.setDecript(String.join(",", decript));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDescEntity);

        //3.保存spu的图片集 pms_spu_images
        List<String> images = spuSaveVo.getImages();
        spuImagesService.saveImages(spuInfoEntity.getId(), images);

        //4.保存spu的规格参数 pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map((item) -> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            productAttrValueEntity.setSpuId(spuInfoEntity.getId());
            productAttrValueEntity.setAttrId(item.getAttrId());
            AttrEntity byId = attrService.getById(item.getAttrId());
            productAttrValueEntity.setAttrName(byId.getAttrName());
            productAttrValueEntity.setAttrValue(item.getAttrValues());
            productAttrValueEntity.setQuickShow(item.getShowDesc());
            return productAttrValueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveBatch(collect);

        //5.保存spu的积分信息 gulimall_sms->sms_spu_bounds
        Bounds bounds = spuSaveVo.getBounds();
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(bounds, spuBoundsTo);
        spuBoundsTo.setSpuId(spuInfoEntity.getId());
        R r = spuFeignService.saveSpuBounds(spuBoundsTo);
        if (r.getCode() != 0) {
            log.error("远程保存spu积分信息失败");
        }

        //5.保存当前spu对应的所有spu信息
        List<Skus> skus = spuSaveVo.getSkus();
        if (skus != null && skus.size() > 0) {
            skus.forEach((sku) -> {

                //5.1 sku的基本信息 pms_sku_info
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, spuInfoEntity);
                //分布式策略的主键自增
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setSaleCount(0L);
                for (Images image : sku.getImages()) {
                    skuInfoEntity.setSkuDefaultImg(image.getImgUrl());
                    break;
                }
                skuInfoService.save(skuInfoEntity);

                //5.2 sku的图片信息 pms_sku_images
                List<SkuImagesEntity> collect1 = sku.getImages().stream().map(item -> {
                    SkuImagesEntity imagesEntity = new SkuImagesEntity();
                    BeanUtils.copyProperties(item, imagesEntity);
                    imagesEntity.setSkuId(skuInfoEntity.getSkuId());
                    return imagesEntity;
                }).filter(item -> {
                    return !StringUtils.isEmpty(item.getSkuId());
                }).collect(Collectors.toList());
                skuImagesService.saveBatch(collect1);

                //5.3 sku的销售属性信息 pms_sku_sale_attr_value
                List<Attr> attr = sku.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(item -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(item, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuInfoEntity.getSkuId());
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);


                //5.4 sku的优惠、满减等信息 gulimall_sms->sms_sku_ladder\sms_sku_full_reduction
                // \sms_member_prce
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(sku, skuReductionTo);
                skuReductionTo.setSkuId(skuInfoEntity.getSkuId());

                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
                    R r1 = spuFeignService.saveSkuReduction(skuReductionTo);
                    if (r1.getCode() != 0) {
                        log.error("远程保存sku积分信息失败");
                    }
                }
            });
        }
    }

    /**
     * SPU检索
     *
     * @param params
     * @return com.uin.utils.PageUtils
     * @author wanglufei
     * @date 2022/4/25 2:42 PM
     */
    @Override
    public PageUtils queryPageByCodition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();
        //模糊查询
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and((item) -> {
                item.eq("id", key).or().like("spu_name", key);
            });
        }
        //状态
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            queryWrapper.eq("publish_status", status);
        }

        //brand_id
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }
        //catalog_id
        String catalogId = (String) params.get("catalog_id");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(catalogId)) {
            queryWrapper.eq("catalog_id", catalogId);
        }
        //分页查询
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void upSpuToES(Long spuId) {

        //1.根据当前的spuId查询出sku信息
        List<SkuInfoEntity> skus = skuInfoService.getSkusById(spuId);

        //查询当前sku的规格属性 可以被检索的规格属性
        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.list(new QueryWrapper<ProductAttrValueEntity>()
                .eq("spu_id", spuId));
        List<Long> list = productAttrValueEntities.stream()
                .map(attr -> {
                    return attr.getAttrId();
                }).collect(Collectors.toList());
        List<Long> searchAttrIds = attrService.selectSearchAttrIds(list);

        Set<Long> ids = new HashSet<>(searchAttrIds);

        List<SpuEsTO.Attrs> attrList = productAttrValueEntities.stream().filter(item -> {
            return ids.contains(item.getAttrId());
        }).map(entity -> {
            SpuEsTO.Attrs attrs = new SpuEsTO.Attrs();
            BeanUtils.copyProperties(entity, attrs);
            return attrs;
        }).collect(Collectors.toList());

        //远程服务调用
        Map<Long, Boolean> stockMap = null;
        try {
            List<Long> longList = skus.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
            List<SkuHasStcokVo> skuHasStock = wareFeignService.getSkuHasStock(longList);
            stockMap = skuHasStock.stream().collect(Collectors.toMap(SkuHasStcokVo::getSkuId,
                    SkuHasStcokVo::getHasStock));
        } catch (Exception e) {
            log.error("远程调用库存服务失败，原因是{}", e);
        }


        //2.封装每个SKu的信息
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SpuEsTO> collect = skus.stream().map(skuInfo -> {
            //组装ES需要的数据
            SpuEsTO spuEsTO = new SpuEsTO();
            BeanUtils.copyProperties(skuInfo, spuEsTO);
            //处理属性不一致的字段
            spuEsTO.setSkuPrice(skuInfo.getPrice());
            spuEsTO.setSkuImg(skuInfo.getSkuDefaultImg());

            //远程调用--需要向库存服务发送请求，查询当前spuId的是否有库存
            spuEsTO.setHasStock(finalStockMap == null ? false : finalStockMap.get(skuInfo.getSkuId()));

            //热度评分
            spuEsTO.setHotScore(0L);
            //品牌和分类的名字
            BrandEntity brandEntity = brandService.getById(spuEsTO.getBrandId());
            if (brandEntity != null) {
                spuEsTO.setBrandName(brandEntity.getName());
                spuEsTO.setBrandImg(brandEntity.getLogo());
            }
            CategoryEntity categoryEntity = categoryService.getById(spuEsTO.getCatalogId());
            if (categoryEntity != null) {
                spuEsTO.setCatalogName(categoryEntity.getName());
            }
            //设置检索属性
            spuEsTO.setAttrs(attrList);
            return spuEsTO;
        }).collect(Collectors.toList());

        //发给ES服务器
        R r = searchFeignService.productStatusUp(collect);
        if (r.getCode() == 0) {
            this.baseMapper.upSpuStatus(spuId, ProductConstant.ProductStatusEnum.SPU_UP.getCode());
        } else {
            log.error("商品远程保存到ES失败");
            //TODO 重复操作接口 接口幂等性的问题：如果远程服务失败，会有重试机制
            /**
             * feign的调用流程
             * 1.调用构造数据，将对象转化为JSON
             * 2.发送请求进行执行（执行成功会解码响应数据）
             * 3.如果执行有错误，就会进行重试机制（默认重试5次）
             */
        }

    }

}
