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
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
//    @Autowired
//    CategoryDao categoryDao;

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    RedissonClient redissonClient;

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
        List<CategoryEntity> level1menu = list.stream()
                .filter((categoryEntity) -> {
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

    @CacheEvict(value = {"catalog"}, key = "'cataloglevel_one'")   //删除指定区域的缓存  针对缓存失效模式
    @Caching(evict = {
            @CacheEvict(value = {"catalog"}, key = "'cataloglevel_one'"),
            @CacheEvict(value = {"catalog"}, key = "'getCatlogJson'")
    })//使用组合注解 删除多个缓存
    @Transactional
    @Override
    public void updateRelationCatgory(CategoryEntity category) {
        //更新自己
        this.updateById(category);
        Long catId = category.getCatId();
        String name = category.getName();
        categoryBrandRelationService.updateCategory(catId, name);
    }

    /**
     * @Cacheable() 需要指定我们的缓存数据放到哪里（缓存分区(按照业务的类型区分)）
     * 就好像Spring-cache是我们的陕西省，@Cacheable({"xian_cache"})，我们的缓存数据要放到西安。
     */
    @Cacheable(value = {"catalog"}, key = "#root.method.name")
    //代表当前方法返回结果需要被缓存，如果缓存中有，就不缓存，如果没有，就缓存

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

    /**
     * 解决缓存穿透--将null结果缓存，
     * 并设置过期随机时间，来解决缓存雪崩，加锁，防止缓存击穿的问题
     */

    @Override
    @Cacheable(value = {"category"}, key = "#root.method.name")
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        //1.加入redis缓存 缓存中存的数据都是json数据格式
        //json数据跨平台 兼容性好
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        if (StringUtils.isEmpty(catalogJson)) {
            //2.如果缓存中没有的话 就去数据库查询
            System.out.println("没有命中缓存。。。查询数据库");
            //Map<String, List<Catalog2Vo>> fromDB = getCatalogJsonFromDB(); 使用本地锁
            Map<String, List<Catalog2Vo>> fromDB = getCatalogJsonFromDB(); //使用redis分布式锁
            //将从数据库查询出来的数据转为json放到缓存中
            //stringRedisTemplate.opsForValue().set("catalogJson", JSON.toJSONString(fromDB));
            //来设置key的过期时间(随机)
            stringRedisTemplate.opsForValue().set("catalogJson", JSON.toJSONString(fromDB),
                    1,
                    TimeUnit.DAYS);
        }
        System.out.println("命中了缓存");
        //需要注意的是 给缓存中放的是json数据 但是我们要返回给前台的是对象 所以还要转换过来
        //其实这个操作也就是序列化和反序列化的过程
        Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJson,
                new TypeReference<Map<String, List<Catalog2Vo>>>() {
                });
        return result;
    }

    /**
     * 使用本地锁
     */
    //从数据库查询并封装数据的过程
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDB() {
        /**
         * 优化三级分类的数据的获取
         *  优化步骤
         *   1. 先从数据库从查询所有
         *   2. 根据查询的结果，在挑选出
         */
        /**
         * 需要加锁的位置 来解决缓存击穿的问题
         * 只要是同一把锁，就能锁住需要这个锁的线程
         *   1.使用synchronized(this)，锁的是当前对象,SpringBoot所有组件在容器中都是单例的
         *   2.加锁的方式，如果在单体项目中，加锁的分布式还可以，如果是分布式下加锁的话
         */
        synchronized (this) {
            System.out.println("拿到了锁");
            /**
             * 得到锁以后，我们应该在去缓存中确定一次，如果没有才去数据库中查询
             */
            /**
             * TODO 使用本地锁（只能锁住我们当前进程里的资源），在分布式的情况下，我们想要合理的控制资源，就必须使用分布式锁
             */
            String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
            //如果缓存中不是空的，我们将这个json数据格式转换成对象，就直接返回
            if (!StringUtils.isEmpty(catalogJson)) {
                Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJson,
                        new TypeReference<Map<String, List<Catalog2Vo>>>() {
                        });
                return result;
            }
            //使用本地锁的测试
            System.out.println("没有走缓存，准备去查询数据库");
            /**
             * 1.将数据库中的多次查询变为一次查询
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
            String s = JSON.toJSONString(stringListMap);
            stringRedisTemplate.opsForValue().set("catalogJson", s, 1, TimeUnit.DAYS);
            return stringListMap;
            //return catalogJson;
        }
    }

    /**
     * 使用分布式锁
     * 1.使用setnx
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDBWithRedisSetnx() {
        /**
         * 怎么使用分布式锁
         *  1.占坑
         */
        //Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", "111");
        /**
         * 5.删除锁直接删除??? 如果由于业务时间很长，锁自己过期了，我们 直接删除，有可能把别人正在持有的锁删除了。
         * 解决办法：占锁的时候，值指定为uuid，每个人匹配是自己 的锁才删除。
         */
        String token = UUID.randomUUID().toString();
        /**
         *  4.还有一种可能产生的问题，如果我们还没来的及给锁设置过期时间 就崩了 也会造成死锁
         *  主要造成的原因是：我们的设置过期时间和加锁 他不是一个原子性的操作
         *     在Redis总cli中有这样一个命令：set lock 111 EX 300 NX
         *     意思就是set<lock,111> EX 过期时间300s 不存在才添加
         */
        Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock", token, 300, TimeUnit.SECONDS);
        if (lock) {
            System.out.println("获取分布式锁成功，执行业务中....");
            //2.加锁成功 为了防止因为网路问题或者其他的问题 导致我们没有释放锁 造成死锁问题 我们需要设置锁的过期时间
            stringRedisTemplate.expire("lock", 30, TimeUnit.SECONDS);
            Map<String, List<Catalog2Vo>> fromDB = null;

            try {
                fromDB = getFromDB();
            } finally {
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                Long result = stringRedisTemplate.execute(new DefaultRedisScript<Long>(script,
                        Long.class), Arrays.asList("lock"), token);
            }
            //2.1 数据返回成功的我们还要解锁
            //stringRedisTemplate.delete("lock");
            //我们需要根据获取当前锁的线程 获取到它的value 来和一开始uuid来匹配 如果相等 说明在释放自己的锁
            //String value = stringRedisTemplate.opsForValue().get("lock");
            /**
             * 6.如果在比较的前面（也就是去查询redis redis给我门返回数据的途中 我们redis中的数据过期了 那别人就有机会进来 重新占了个锁）
             * 此时别人也叫lock，但是value，是不一样的值 那我们此时 在走我们判断的业务逻辑 进不去 就又造成了 死锁
             * 造成的主要原因是：他们不是一个原子性的操作（获取值和和值进行对比）
             * 解决办法：删除锁必须保证原子性。使用redis+Lua脚本完成
             */
            //if (value.equals(token)) {
            //说明在释放自己的锁 那就放心释放
            //stringRedisTemplate.delete("lock");
            //}
            //Lua脚本
            //String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call" +
            //"('del', KEYS[1]) else return 0 end";
            /**
             * 删除成功返回 1 不成功0
             * Lua脚本解锁保证原子性操作
             */
            //Integer result = stringRedisTemplate.execute(new DefaultRedisScript<Integer>(script,
            //Integer.class),
            //Arrays.asList("lock"), token);
            return fromDB;
        } else {
            //3.加锁失败（等一会儿，再去重试） 类似与自旋
            /**
             * 7.保证加锁【占位+过期时间】和删除锁【判断+删除】的原子性。 更难的事情，锁的自动续期
             * 也就是我们的业务还没执行完 我们的锁过期了 那不就bbq了。就好比 我们在网吧 我们正打团 机子给我们提示余额用完了
             * 给我们锁机了。
             * 所以为了解决这个问题：我们需要解决在业务的执行期间 需要给锁自动续期
             * 最简单的方法就是过期时间 给多一点（合理的业务时间）使用try{} finally{}
             */
            System.out.println("获取分布式锁失败，自旋等待中....");
            //可以设置休眠100ms在重试
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatalogJsonFromDBWithRedisSetnx();
        }
    }

    /**
     * 使用redisson分布式锁
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDBWithRedisson() {
        /**
         * 怎么使用分布式锁
         *  1.占坑
         *  需要注意锁的名字。锁的粒度，越细越快
         *  锁的粒度：具体缓存的是某个数据
         */
        RLock lock = redissonClient.getLock("catalogJson-Lock");
        //加锁
        lock.lock();
        Map<String, List<Catalog2Vo>> fromDB = null;

        try {
            fromDB = getFromDB();
        } finally {
            //释放锁
            lock.unlock();
        }
        return fromDB;
    }


    //抽取出来的方法
    private Map<String, List<Catalog2Vo>> getFromDB() {
        String catalogJson = stringRedisTemplate.opsForValue().get("catalogJson");
        //如果缓存中不是空的，我们将这个json数据格式转换成对象，就直接返回
        if (!StringUtils.isEmpty(catalogJson)) {
            Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJson,
                    new TypeReference<Map<String, List<Catalog2Vo>>>() {
                    });
            return result;
        }
        /**
         * 1.将数据库中的多次查询变为一次查询
         */
        List<CategoryEntity> entities = baseMapper.selectList(null);

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
        String s = JSON.toJSONString(stringListMap);
        stringRedisTemplate.opsForValue().set("catalogJson", s, 1, TimeUnit.DAYS);
        return stringListMap;
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> entities, Long parent_cid) {
        List<CategoryEntity> collect = entities.stream()
                .filter(item -> item.getParentCid() == parent_cid)
                .collect(Collectors.toList());
        return collect;
        //return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid",
        //value.getCatId()));
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
