package com.uin.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uin.product.dao.SkuInfoDao;
import com.uin.product.entity.SkuImagesEntity;
import com.uin.product.entity.SkuInfoEntity;
import com.uin.product.entity.SpuInfoDescEntity;
import com.uin.product.service.*;
import com.uin.product.vo.SkuItemVo;
import com.uin.product.vo.itemVo.SkuItemSaleAttrVo;
import com.uin.product.vo.itemVo.SpuBaseAttrGroupVo;
import com.uin.utils.PageUtils;
import com.uin.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    AttrGroupService attrGroupService;
    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * SkU的检索
     *
     * @param params
     * @return com.uin.utils.PageUtils
     * @author wanglufei
     * @date 2022/4/29 8:35 AM
     */
    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();

        //key:
        //catelogId: 0
        //brandId: 0
        //min: 0
        //max: 0

        //检索关键字 key
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and((wrapper) -> {
                wrapper.eq("sku_id", key).or().like("sku_title", key);
            });
        }

        //价格区间 min:0  max:0
        String min = (String) params.get("min");
        if (!StringUtils.isEmpty(min)) {
            //ge 大于等于 >=
            queryWrapper.ge("price", min);
        }

        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max)) {
            BigDecimal decimal = new BigDecimal(max);
            try {
                if (decimal.compareTo(new BigDecimal("0")) == 1) {
                    //小于等于 <=
                    queryWrapper.le("price", max);
                }
            } catch (Exception e) {

            }
        }
        //分类 catelogId
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq("catalog_id", catelogId);
        }
        //品牌 brandId
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq("brand_id", brandId);
        }

        //分页返回
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusById(Long spuId) {
        List<SkuInfoEntity> list = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return list;
    }

    /**
     * 展示当前sku的详情
     *
     * @param skuId
     * @return com.uin.product.vo.SkuItemVo
     * @author wanglufei
     * @date 2022/9/27 3:16 PM
     */
    @Override
    public SkuItemVo item(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();

        // 1.skuId的基本信息 pms_sku_info
        SkuInfoEntity skuInfoEntity = getById(skuId);
        Long catalogId = skuInfoEntity.getCatalogId();
        skuItemVo.setSkuInfoEntity(skuInfoEntity);
        Long spuId = skuInfoEntity.getSpuId();

        // 2.sku的图片信息 pms_sku_images
        List<SkuImagesEntity> entityList = skuImagesService.getImageBySkuId(skuId);
        skuItemVo.setImagesEntities(entityList);

        // 3.获取spu的销售属性组合
        List<SkuItemSaleAttrVo> saleAttrVoList = skuSaleAttrValueService.getSaleAttrBySpuId(spuId);
        skuItemVo.setSaleAttrVos(saleAttrVoList);

        // 4.获取spu的介绍
        SpuInfoDescEntity descEntity = spuInfoDescService.getById(spuId);
        skuItemVo.setDesc(descEntity);

        // 5.获取spu的规格参数信息
        List<SpuBaseAttrGroupVo> groupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
        skuItemVo.setGroupVos(groupVos);

        return skuItemVo;
    }

}
