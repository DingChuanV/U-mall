package com.uin.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.coupon.entity.CouponSpuCategoryRelationEntity;
import com.uin.utils.PageUtils;

import java.util.Map;

/**
 * 优惠券分类关联
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 19:19:09
 */
public interface CouponSpuCategoryRelationService extends IService<CouponSpuCategoryRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

