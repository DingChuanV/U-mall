package com.uin.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.coupon.entity.CouponEntity;
import com.uin.utils.PageUtils;

import java.util.Map;

/**
 * 优惠券信息
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 19:19:09
 */
public interface CouponService extends IService<CouponEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

