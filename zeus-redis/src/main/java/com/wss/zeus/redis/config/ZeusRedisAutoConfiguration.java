package com.wss.zeus.redis.config;

import com.wss.zeus.redis.properties.ZeusRedisProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * Zeus Redis 自动配置
 *
 * @author wangshusheng
 */
@AutoConfiguration
@ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
@EnableConfigurationProperties(ZeusRedisProperties.class)
@Import({RedisSerializerConfig.class, RedissonConfig.class})
public class ZeusRedisAutoConfiguration {
}
