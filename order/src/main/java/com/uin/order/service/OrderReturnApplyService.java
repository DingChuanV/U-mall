package com.uin.order.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.order.entity.OrderReturnApplyEntity;
import com.uin.utils.PageUtils;

import java.util.Map;

/**
 * 订单退货申请
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-17 20:42:34
 */
public interface OrderReturnApplyService extends IService<OrderReturnApplyEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

