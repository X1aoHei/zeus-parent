package com.wss.zeus.core.trace;

import org.slf4j.MDC;

import java.util.UUID;

/**
 * Trace 上下文：线程本地变量 + MDC 同步
 *
 * @author wangshusheng
 */
public final class TraceContext {

    private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();

    private TraceContext() {
    }

    /**
     * 设置 traceId 到线程本地变量与 MDC
     */
    public static void setTraceId(String traceId) {
        if (traceId == null || traceId.isBlank()) {
            clear();
            return;
        }
        TRACE_ID.set(traceId);
        MDC.put(TraceConstants.TRACE_ID, traceId);
    }

    /**
     * 获取当前线程 traceId
     */
    public static String getTraceId() {
        return TRACE_ID.get();
    }

    /**
     * 生成新的 traceId
     */
    public static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 若当前无线程本地 traceId 则初始化一个并返回；已有则直接返回
     */
    public static String initIfAbsent() {
        String traceId = TRACE_ID.get();
        if (traceId != null && !traceId.isBlank()) {
            return traceId;
        }
        traceId = generateTraceId();
        setTraceId(traceId);
        return traceId;
    }

    /**
     * 使用上游传入的 traceId；为空则新生成
     */
    public static String setOrInit(String incomingTraceId) {
        String traceId = (incomingTraceId == null || incomingTraceId.isBlank())
                ? generateTraceId()
                : incomingTraceId.trim();
        setTraceId(traceId);
        return traceId;
    }

    /**
     * 清理线程本地变量与 MDC，避免线程复用串扰
     */
    public static void clear() {
        TRACE_ID.remove();
        MDC.remove(TraceConstants.TRACE_ID);
    }
}
