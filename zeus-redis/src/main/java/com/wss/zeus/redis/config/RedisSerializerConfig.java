package com.wss.zeus.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * Redis 序列化配置
 *
 * @author wangshusheng
 */
@Configuration
@ConditionalOnProperty(prefix = "zeus.redis", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RedisSerializerConfig {

    /**
     * JSON 序列化器，不写入 @class - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-28
     */
    @Bean
    @ConditionalOnProperty(prefix = "zeus.redis", name = "serializer-type", havingValue = "json", matchIfMissing = true)
    public RedisSerializer<Object> jsonRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer(doCreateObjectMapper());
    }

    /**
     * Redis 专用 ObjectMapper，不注册为全局 Bean，避免接口 JSON 要求 @class - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-28
     */
    private ObjectMapper doCreateObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    /**
     * 配置 RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory,
            RedisSerializer<Object> redisSerializer) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(redisSerializer);
        template.setHashKeySerializer(RedisSerializer.string());
        template.setHashValueSerializer(redisSerializer);
        template.afterPropertiesSet();
        return template;
    }
}
