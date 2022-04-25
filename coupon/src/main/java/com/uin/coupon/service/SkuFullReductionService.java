package com.uin.coupon.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.coupon.entity.SkuFullReductionEntity;
import com.uin.to.SkuReductionTo;
import com.uin.utils.PageUtils;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 19:19:09
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(SkuReductionTo skuReductionTo);
}

