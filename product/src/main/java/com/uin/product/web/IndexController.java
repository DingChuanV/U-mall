package com.uin.product.web;

import com.sun.corba.se.spi.ior.ObjectKey;
import com.uin.product.entity.CategoryEntity;
import com.uin.product.service.CategoryService;
import com.uin.product.vo.Catalog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;
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
}
