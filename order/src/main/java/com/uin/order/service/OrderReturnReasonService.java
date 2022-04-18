package com.uin.order.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.order.entity.OrderReturnReasonEntity;
import com.uin.utils.PageUtils;

import java.util.Map;

/**
 * 退货原因
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-17 20:42:34
 */
public interface OrderReturnReasonService extends IService<OrderReturnReasonEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

