package com.uin.product.web;

import com.uin.product.entity.CategoryEntity;
import com.uin.product.service.CategoryService;
import com.uin.product.vo.Catalog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {
        List<CategoryEntity> categoryEntityList = categoryService.getLevel_one();
        model.addAttribute("categorys", categoryEntityList);
        return "index";
    }

    //index/catalog.json
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        Map<String, List<Catalog2Vo>> map = categoryService.getCatalogJson();
        return map;
    }

    /**
     * 简单服务
     *
     * @return java.lang.String
     * @author wanglufei
     * @date 2022/5/8 11:28 PM
     */
    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        RLock lock = redissonClient.getLock("mylock");

        //1.加锁
        /**
         *  1.redisson的lock锁，他会为我们的锁自动续期，如果我们的业务代码执行时间比较长
         *  2. 加锁的业务只要运行完成，就不会给锁自动续期，即使不手动释放锁，锁默认在30s之后会自动删除
         */
        lock.lock();//阻塞式等

        try {
            //2.执行业务代码
            System.out.println("加锁成功,执行业务代码..." + Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //3.释放锁
            /**
             * 假设我们服务直接停机了 我们的解锁代码 没有执行 会不会导致死锁问题
             * 解释：其实不会，为了避免这种情况的发生，Redisson内部提供了一个监控锁的看门狗，
             * 它的作用是在Redisson实例被关闭前，不断的延长锁的有效期。默认情况下，
             * 看门狗的检查锁的超时时间是30秒钟
             */
            lock.unlock();
            System.out.println("释放锁" + Thread.currentThread().getId());
        }
        return "hello";
    }

    @ResponseBody
    @PostMapping("/write")
    public String write() {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("write_lock");
        String s = "";
        //写锁
        RLock lock = readWriteLock.writeLock();
        try {
            s = UUID.randomUUID().toString();
            Thread.sleep(30000);
            redisTemplate.opsForValue().set("writeValue", s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return s;
    }

    @ResponseBody
    @PostMapping("/read")
    public String read() {
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock("write_lock");
        String s = "";
        //读锁
        RLock lock = readWriteLock.readLock();
        try {
            s = UUID.randomUUID().toString();
            Thread.sleep(30000);
            s = (String) redisTemplate.opsForValue().get("writeValue");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return s;
    }

    /**
     * 车库停车
     * 3车位
     * 信号量可以用来做限流
     */
    @ResponseBody
    @RequestMapping("/park")
    public String park() throws InterruptedException {
        RSemaphore park = redissonClient.getSemaphore("park");
        park.acquire();
        //park.tryAcquire();
        return "ok";
    }

    @ResponseBody
    @RequestMapping("/go")
    public String go() throws InterruptedException {
        RSemaphore park = redissonClient.getSemaphore("park");
        park.release();
        return "ok";
    }

    @ResponseBody
    @RequestMapping("/lockDoor")
    public String lockDoor() throws InterruptedException {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.trySetCount(5);
        door.await();
        return "放假了";
    }

    @ResponseBody
    @RequestMapping("/goG/{id}")
    public String goG(@PathVariable int id) throws InterruptedException {
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        door.countDown();
        return "走了" + id;
    }


}
