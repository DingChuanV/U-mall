package com.uin.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.order.entity.RefundInfoEntity;
import com.uin.utils.PageUtils;

import java.util.Map;

/**
 * 退款信息
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-17 20:42:34
 */
public interface RefundInfoService extends IService<RefundInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

