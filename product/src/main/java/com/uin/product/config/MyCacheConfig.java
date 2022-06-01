package com.uin.product.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class) //开启属性配置的绑定的功能
public class MyCacheConfig {
    @Autowired
    CacheProperties cacheProperties;

    /**
     * 在配置文件中配置cache的缓存过期时间没生效
     */
    @Bean
    RedisCacheConfiguration redisCacheConfiguration() {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        //cacheConfiguration.entryTtl();
        // 自定义key的序列化
        config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        // 自定义value的序列化
        config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        // 去配置文件获取缓存的过期时间 配置配置文件中的过期时间生效
        CacheProperties.Redis redis = cacheProperties.getRedis();
        if (redis.getTimeToLive() != null) {
            config = config.entryTtl(redis.getTimeToLive());
        }
        if (redis.getKeyPrefix() != null) {
            config = config.prefixKeysWith(redis.getKeyPrefix());
        }
        if (redis.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (redis.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    }

}
