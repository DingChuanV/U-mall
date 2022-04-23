package com.uin.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.product.entity.AttrGroupEntity;
import com.uin.utils.PageUtils;

import java.util.Map;

/**
 * 属性分组
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 18:05:16
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);
    /**
     * 获取对应分类ID下的属性分组
     */
    PageUtils queryPage(Map<String, Object> params, Long catelogId);


}

