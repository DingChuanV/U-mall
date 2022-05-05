package com.uin.product.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uin.product.dao.SkuInfoDao;
import com.uin.product.entity.SkuInfoEntity;
import com.uin.product.service.SkuInfoService;
import com.uin.utils.PageUtils;
import com.uin.utils.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

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

}
