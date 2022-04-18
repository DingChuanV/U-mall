package com.uin.coupon.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.coupon.entity.SpuBoundsEntity;
import com.uin.utils.PageUtils;

import java.util.Map;

/**
 * 商品spu积分设置
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 19:19:09
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

