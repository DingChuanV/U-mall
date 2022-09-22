package com.uin.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wanglufei
 * @description: TODO
 * @date 2022/5/18/8:00 PM
 */
@Configuration
public class MyRedissonConfig {
    /**
     * 所有对Redisson的使用都是通过RedissonClient对象来操作
     *
     * @return org.redisson.api.RedissonClient
     * @author wanglufei
     * @date 2022/5/18 8:20 PM
     */
    @Bean
    public RedissonClient redisson() {
        //1.创建配置对象
        Config config = new Config();
        //单集群模式
        config.useSingleServer().setAddress("redis://10.100.114.200:6379");
        //2.根据配置对象创建出RedissonClient实例对象
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
