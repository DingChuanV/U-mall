package com.uin;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;

/**
 * @author wanglufei
 * @description: TODO
 * @date 2022/5/11/9:20 PM
 */
@SpringBootTest
@Slf4j
class RedisTest {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    /**
     * 测试stringRedisTemplate
     *
     * @author wanglufei
     * @date 2022/5/11 9:27 PM
     */
    @Test
    public void test01() {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        //存值的操作
        ops.set("uin", "niupi_" + UUID.randomUUID());
        //查询操作
        System.out.println(ops.get("uin"));
    }
}
