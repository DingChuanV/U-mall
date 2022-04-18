package com.uin.coupon.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.coupon.entity.SkuLadderEntity;
import com.uin.utils.PageUtils;

import java.util.Map;

/**
 * 商品阶梯价格
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 19:19:09
 */
public interface SkuLadderService extends IService<SkuLadderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

