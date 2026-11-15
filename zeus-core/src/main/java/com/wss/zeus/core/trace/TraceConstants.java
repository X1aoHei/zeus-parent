package com.wss.zeus.core.trace;

/**
 * Trace 相关常量
 *
 * @author wangshusheng
 */
public final class TraceConstants {

    private TraceConstants() {
    }

    /**
     * 请求头 / MDC / ThreadLocal 统一使用的 traceId 键名
     */
    public static final String TRACE_ID = "traceId";
}
