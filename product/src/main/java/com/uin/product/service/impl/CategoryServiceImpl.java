package com.uin.product.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.uin.product.dao.CategoryDao;
import com.uin.product.entity.CategoryEntity;
import com.uin.product.service.CategoryBrandRelationService;
import com.uin.product.service.CategoryService;
import com.uin.product.vo.Catalog2Vo;
import com.uin.utils.PageUtils;
import com.uin.utils.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
//    @Autowired
//    CategoryDao categoryDao;

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

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

    @Transactional

    @Override
    public void updateRelationCatgory(CategoryEntity category) {
        //更新自己
        this.updateById(category);
        Long catId = category.getCatId();
        String name = category.getName();
        categoryBrandRelationService.updateCategory(catId, name);
    }

    @Override
    public List<CategoryEntity> getLevel_one() {
        long l = System.currentTimeMillis();
        List<CategoryEntity> categoryEntityList = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        System.out.println("消耗时间：" + (System.currentTimeMillis() - l));
        return categoryEntityList;
    }

    /**
     * 本地缓存
     */
    //private static Map<String, Object> cacheMap = new HashMap<>();
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        //1.加入redis缓存 缓存中存的数据都是json数据格式
        //json数据跨平台 兼容性好
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.isEmpty(catalogJson)) {
            //2.如果缓存中没有的话 就去数据库查询
            Map<String, List<Catalog2Vo>> fromDB = getCatalogJsonFromDB();
            //将从数据库查询出来的数据转为json放到缓存中
            stringRedisTemplate.opsForValue().set("catalogJson", JSON.toJSONString(fromDB));
        }
        //需要注意的是 给缓存中放的是json数据 但是我们要返回给前台的是对象 所以还要转换过来
        //其实这个操作也就是序列化和反序列化的过程
        Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJson,
                new TypeReference<Map<String, List<Catalog2Vo>>>(){});
        return result;
    }

    //从数据库查询并封装数据的过程
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDB() {
        /**
         * 优化三级分类的数据的获取
         *  优化步骤
         *   1. 先从数据库从查询所有
         *   2. 根据查询的结果，在挑选出
         */
        List<CategoryEntity> entities = baseMapper.selectList(null);

        /**
         * 使用Map<String,Object>来做缓存
         */
        // 如果有，就用缓存的
        //Map<String, List<Catalog2Vo>> catalogJson =
        //(Map<String, List<Catalog2Vo>>) cacheMap.get("catalogJson");

        //  如果缓存中没有，就去查询数据库
//        if (catalogJson.get("catalogJson") == null) {
//
//        }
        //1.查出所有一级分类的数据
        List<CategoryEntity> level_one = getParent_cid(entities, 0L);
        //2.封装数据
        Map<String, List<Catalog2Vo>> stringListMap = level_one.stream().collect(Collectors.toMap(key -> key.getCatId().toString(), value -> {
            //1.每一个一级分类，查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntityList = getParent_cid(entities, value.getCatId());
            List<Catalog2Vo> catalog2Vos = null;
            if (categoryEntityList != null) {
                catalog2Vos = categoryEntityList.stream().map(l2 -> {
                    //2.二级菜单封装
                    Catalog2Vo catalog2Vo = new Catalog2Vo(value.getCatId().toString(), null,
                            l2.getCatId().toString(),
                            l2.getName());
                    //3.分装三级菜单 找当前二级菜单的三级菜单
                    List<CategoryEntity> categoryEntities = getParent_cid(entities, l2.getCatId());
                    if (categoryEntities != null) {
                        List<Catalog2Vo.Catalog3Vo> vos = categoryEntities
                                .stream()
                                .map(l3 -> {
                                    Catalog2Vo.Catalog3Vo catalog3Vo =
                                            new Catalog2Vo.Catalog3Vo(l2.getCatId().toString(),
                                                    l3.getCatId().toString(), l3.getName());
                                    return catalog3Vo;
                                }).collect(Collectors.toList());
                        catalog2Vo.setCatalog3List(vos);
                    }
                    return catalog2Vo;
                }).collect(Collectors.toList());
            }
            return catalog2Vos;
        }));
        //查到之后给本地缓存中放一份
        //catalogJson.put("catalogJson", (List<Catalog2Vo>) stringListMap);
        return stringListMap;
        //return catalogJson;
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> entities, Long parent_cid) {
        List<CategoryEntity> collect = entities.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
        return collect;
//        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", value.getCatId()));
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
