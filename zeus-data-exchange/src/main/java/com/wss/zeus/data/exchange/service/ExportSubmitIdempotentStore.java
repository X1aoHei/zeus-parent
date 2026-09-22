package com.wss.zeus.data.exchange.service;

import com.wss.zeus.data.exchange.support.ExportIdempotentKeyBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;

/**
 * 导出提交 Redis SETNX 幂等 - 系AI自动生成
 *
 * @author WSS AI Agent @Date 2026-09-22
 */
@Component
@RequiredArgsConstructor
public class ExportSubmitIdempotentStore {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * SETNX 占用幂等 key - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    public boolean tryAcquire(String key, String taskId) {
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(key, taskId,
                Duration.ofSeconds(ExportIdempotentKeyBuilder.ttlSeconds()));
        return Boolean.TRUE.equals(acquired);
    }

    /**
     * 读取已占用的任务 ID - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    public String getTaskId(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof String text && StringUtils.isNotBlank(text)) {
            return text;
        }
        return null;
    }

    /**
     * 把幂等 key 绑定到已有任务 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    public void bind(String key, String taskId) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(taskId)) {
            return;
        }
        redisTemplate.opsForValue().set(key, taskId, Duration.ofSeconds(ExportIdempotentKeyBuilder.ttlSeconds()));
    }

    /**
     * 释放幂等 key - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    public void release(String key) {
        if (Objects.isNull(key)) {
            return;
        }
        redisTemplate.delete(key);
    }
}
