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
     * 导出任务消费者组
     */
    public static final String EXPORT_TASK_CONSUMER_GROUP = "zeus-data-exchange-export-task-consumer";

    /**
     * 分布式锁等待时间（秒）
     */
    public static final long LOCK_WAIT_TIME = 1;
}
