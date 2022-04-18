package com.uin.order.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.order.entity.PaymentInfoEntity;
import com.uin.utils.PageUtils;

import java.util.Map;

/**
 * 支付信息表
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-17 20:42:34
 */
public interface PaymentInfoService extends IService<PaymentInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

