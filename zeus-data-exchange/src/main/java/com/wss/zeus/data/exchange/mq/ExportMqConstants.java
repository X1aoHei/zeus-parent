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
     * Redis 幂等 Key 前缀
     */
    public static final String IDEMPOTENT_KEY_PREFIX = "export:task:idempotent:";

    /**
     * Redis 分布式锁 Key 前缀
     */
    public static final String LOCK_KEY_PREFIX = "lock:export:task:execute:";

    /**
     * 幂等 Key 过期时间（秒）
     */
    public static final int IDEMPOTENT_EXPIRE_SECONDS = 300;

    /**
     * 分布式锁等待时间（秒）
     */
    public static final long LOCK_WAIT_TIME = 1;

    /**
     * 分布式锁持有时间（秒）
     */
    public static final long LOCK_LEASE_TIME = 60;
}
