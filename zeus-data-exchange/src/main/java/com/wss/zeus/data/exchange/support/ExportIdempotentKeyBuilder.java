package com.wss.zeus.data.exchange.support;

import com.wss.zeus.data.exchange.constant.ExportTaskConstant;
import com.wss.zeus.data.exchange.constant.RedisConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * 导出提交幂等 key - 系AI自动生成
 *
 * @author WSS AI Agent @Date 2026-09-22
 */
public final class ExportIdempotentKeyBuilder {

    private ExportIdempotentKeyBuilder() {
    }

    /**
     * 按模板、操作人和参数摘要生成幂等 key - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    public static String build(String templateCode, Long operatorUserId, String taskParam) {
        String digest = DigestUtils.md5DigestAsHex(StringUtils.defaultString(taskParam).getBytes(StandardCharsets.UTF_8));
        return String.format(RedisConstant.EXPORT_SUBMIT_IDEMPOTENT_KEY, templateCode, operatorUserId, digest);
    }

    /**
     * 幂等 key 过期秒数 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    public static int ttlSeconds() {
        return ExportTaskConstant.IDEMPOTENT_TTL_SECONDS;
    }
}
