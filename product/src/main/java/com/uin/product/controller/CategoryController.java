package com.uin.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.uin.utils.PageUtils;
import com.uin.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uin.product.entity.CategoryEntity;
import com.uin.product.service.CategoryService;


/**
 * 商品三级分类
 *
 * @author wanglufei
 * @email 1634060836@qq.com
 * @date 2022-04-18 18:05:16
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查询所有分类以及子分类，以树形结构组装起来
     */
    @RequestMapping("/list/tree")
    /**
     *@RequiresPermissions("product:category:list")
     */
    public R list() {
        List<CategoryEntity> listTree = categoryService.listWithTree();
        return R.ok().put("data", listTree);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    /**
     *@RequiresPermissions("product:category:info")
     */
    public R info(@PathVariable("catId") Long catId) {
        CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    /**
     *@RequiresPermissions("product:category:save")
     */
    public R save(@RequestBody CategoryEntity category) {
        categoryService.save(category);

        return R.ok();
    }

    /**
     * 批量修改
     */
    @RequestMapping("/update/sort")
    /**
     *@RequiresPermissions("product:category:update")
     */
    public R updateSort(@RequestBody CategoryEntity[] category) {
        categoryService.updateBatchById(Arrays.asList(category));

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    /**
     *@RequiresPermissions("product:category:update")
     */
    public R update(@RequestBody CategoryEntity category) {
        categoryService.updateById(category);

        return R.ok();
    }

    /**
     * 删除
     * RequestBody 获取请求体，只有post请求体，get请求没有请求体
     * springmvc会自动将请求体中的数据（json），转为对应的对象
     */
    @RequestMapping("/delete")
    /**
     *@RequiresPermissions("product:category:delete")
     */
    public R delete(@RequestBody Long[] catIds) {
        //categoryService.removeByIds(Arrays.asList(catIds));
        //批量删除的时候需要检查有没有其他地方引用
        categoryService.removeMenuByIds(Arrays.asList(catIds));
        return R.ok();
    }

}
