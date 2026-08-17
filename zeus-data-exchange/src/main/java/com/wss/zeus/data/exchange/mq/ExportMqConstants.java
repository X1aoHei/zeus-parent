package com.wss.zeus.data.exchange.mq;

/**
 * 导出任务 MQ 常量
 *
 * @author wangshusheng
 */
public final class ExportMqConstants {

    private ExportMqConstants() {
    }

    /**
     * Topic
     */
    public static final String TOPIC = "zeus-data-exchange";

    /**
     * Tag
     */
    public static final String TAG_EXPORT_TASK = "export-task";

    /**
     * Redis 分布式锁 Key 前缀（提交阶段）
     */
    public static final String SUBMIT_LOCK_KEY_PREFIX = "lock:export:task:submit:";

    /**
     * Redis 分布式锁 Key 前缀（执行阶段）
     */
    public static final String EXECUTE_LOCK_KEY_PREFIX = "lock:export:task:execute:";

    /**
     * 分布式锁等待时间（秒）
     */
    public static final long LOCK_WAIT_TIME = 1;
}
