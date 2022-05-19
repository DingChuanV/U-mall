package com.uin;

import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author wanglufei
 * @description:
 * @date 2022/5/18/8:24 PM
 */
@SpringBootTest
public class RedissonClientTest {
    @Autowired
    RedissonClient redissonClient;

    @Test
    public void test01() {
        System.out.println(redissonClient);
    }

}
