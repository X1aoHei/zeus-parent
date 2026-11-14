package com.wss.zeus.data.exchange.constant;

/**
 * 导出任务 Redis key 模板 - 系AI自动生成
 *
 * @author WSS AI Agent @Date 2026-09-22
 */
public final class RedisConstant {

    private RedisConstant() {
    }

    /**
     * 提交锁 lock:export:task:submit:{requestId}
     */
    public static final String EXPORT_SUBMIT_LOCK_KEY = "lock:export:task:submit:%s";

    /**
     * 执行锁 lock:export:task:execute:{taskId}
     */
    public static final String EXPORT_EXECUTE_LOCK_KEY = "lock:export:task:execute:%s";
}
