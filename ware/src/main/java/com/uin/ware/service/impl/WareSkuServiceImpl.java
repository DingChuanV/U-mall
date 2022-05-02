package com.uin.ware.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uin.utils.PageUtils;
import com.uin.utils.Query;
import com.uin.utils.R;
import com.uin.ware.dao.WareSkuDao;
import com.uin.ware.entity.WareSkuEntity;
import com.uin.ware.feign.ProductFeign;
import com.uin.ware.service.WareSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    WareSkuDao skuDao;
    @Autowired
    ProductFeign productFeign;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        //skuId: 1
        //wareId: 1
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();

        String skuId = (String) params.get("skuId");
        if (!StringUtils.isEmpty(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }

        String wareId = (String) params.get("wareId");
        if (!StringUtils.isEmpty(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void addStcok(Long skuId, Long wareId, Integer skuNum) {
        //如果还没有库存 那就是新增的操作
        List<WareSkuEntity> skuEntities = skuDao.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id",
                wareId));
        if (skuEntities == null && skuEntities.size() == 0) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //当然这里需要远程查询product服务的商品名称 根据skuId
            //如果失败 事务不需要回滚
            //1. 自己catch异常
            //2。
            try {
                R info = productFeign.info(skuId);
                //skuInfo
                Map<String, Object> skuInfo = (Map<String, Object>) info.get("skuInfo");
                String skuName = (String) skuInfo.get("skuName");
                wareSkuEntity.setSkuName(skuName);
            } catch (Exception e) {

            }
            //那就是新增的操作
            skuDao.insert(wareSkuEntity);
        } else {
            //如果有就是更新操作
            skuDao.updateStock(skuId, wareId, skuNum);
        }
    }

}
