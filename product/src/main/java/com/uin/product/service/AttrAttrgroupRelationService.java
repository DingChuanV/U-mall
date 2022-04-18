package com.uin.product.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.uin.product.entity.AttrAttrgroupRelationEntity;
import com.uin.utils.PageUtils;

import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 18:05:16
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

