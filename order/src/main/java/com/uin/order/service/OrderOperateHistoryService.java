package com.uin.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.order.entity.OrderOperateHistoryEntity;
import com.uin.utils.PageUtils;

import java.util.Map;

/**
 * 订单操作历史记录
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-17 20:42:34
 */
public interface OrderOperateHistoryService extends IService<OrderOperateHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

