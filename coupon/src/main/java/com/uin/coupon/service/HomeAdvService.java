package com.uin.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.coupon.entity.HomeAdvEntity;
import com.uin.utils.PageUtils;

import java.util.Map;

/**
 * 首页轮播广告
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 19:19:09
 */
public interface HomeAdvService extends IService<HomeAdvEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

