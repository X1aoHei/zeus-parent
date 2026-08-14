package com.wss.zeus.redis.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Zeus Redis 配置属性
 *
 * @author wangshusheng
 */
@Data
@ConfigurationProperties(prefix = "zeus.redis")
public class ZeusRedisProperties {

    /**
     * 是否启用 Redis
     */
    private boolean enabled = true;

    /**
     * 序列化类型：json（默认）、jdk、string
     */
    private String serializerType = "json";

    /**
     * 是否启用分布式锁
     */
    private boolean lockEnabled = true;
}
