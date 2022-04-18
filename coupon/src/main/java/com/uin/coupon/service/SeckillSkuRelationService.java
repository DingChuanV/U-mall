package com.uin.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.uin.coupon.entity.SeckillSkuRelationEntity;
import com.uin.utils.PageUtils;

import java.util.Map;

/**
 * 秒杀活动商品关联
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 19:19:09
 */
public interface SeckillSkuRelationService extends IService<SeckillSkuRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

