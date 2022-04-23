package com.uin.product.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uin.product.dao.CategoryDao;
import com.uin.product.entity.CategoryEntity;
import com.uin.product.service.CategoryService;
import com.uin.utils.PageUtils;
import com.uin.utils.Query;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
//    @Autowired
//    CategoryDao categoryDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }


    /**
     * 查询所有分类以及子分类，以树形结构组装起来
     *
     * @return java.util.List<com.uin.product.entity.CategoryEntity>
     * @author wanglufei
     * @date 2022/4/20 4:04 PM
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        /**
         * 查询所有分裂，并按父子tree形结构
         */
        //也可以使用baseMapper 查询数据库中所有的分类
        List<CategoryEntity> list = baseMapper.selectList(null);

        // 先找到一级分类
        List<CategoryEntity> level1menu = list.stream().filter((categoryEntity) -> {
            //找到一级分类
            return categoryEntity.getParentCid() == 0;
        }).map((menu) -> {
            //进行映射 递归查询
            menu.setChildren(getChildrens(menu, list));
            return menu;
        }).sorted((menu1, menu2) -> {
            //排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ?
                    0 : menu2.getSort());
        }).collect(Collectors.toList());
        return level1menu;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO 需要检查是否其他地方被引用
        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatcatelogPath(Long catelogId) {
        List<Long> path = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, path);
        //换一下方向
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    //225 25 1
    private List<Long> findParentPath(Long catelogId, List<Long> path) {
        //先把的自己的分类id收集
        path.add(catelogId);
        //再去看他父亲的
        CategoryEntity categoryEntity = this.getById(catelogId);
        if (categoryEntity.getParentCid() != 0) {
            findParentPath(categoryEntity.getParentCid(), path);
        }
        return path;
    }

    /**
     * 递归查询当前分类包含的子分类
     *
     * @param root
     * @param all
     * @return java.util.List<com.uin.product.entity.CategoryEntity>
     * @author wanglufei
     * @date 2022/4/20 4:24 PM
     */
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        // root 相当于当前的分类
        List<CategoryEntity> children = all.stream().filter((categoryEntity) -> {
            //找到他的子分类
            return categoryEntity.getParentCid() == root.getCatId();
        }).map((categoryEntity) -> {
            //对一级分类和二级分类进行映射 递归
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            //排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ?
                    0 : menu2.getSort());
        }).collect(Collectors.toList());
        return children;
    }

}
