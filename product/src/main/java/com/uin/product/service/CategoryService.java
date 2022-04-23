package com.uin.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.uin.product.entity.CategoryEntity;
import com.uin.utils.PageUtils;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 18:05:16
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询所有分类以及子分类，以树形结构组装起来
     *
     * @return java.util.List<com.uin.product.entity.CategoryEntity>
     * @author wanglufei
     * @date 2022/4/20 4:04 PM
     */
    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> asList);

    Long[] findCatcatelogPath(Long catelogId);

    void updateRelationCatgory(CategoryEntity category);
}

