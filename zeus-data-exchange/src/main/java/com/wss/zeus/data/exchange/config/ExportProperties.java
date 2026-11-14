package com.wss.zeus.data.exchange.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 导出任务配置 - 系AI自动生成
 *
 * @author WSS AI Agent @Date 2026-09-24
 */
@Data
@ConfigurationProperties(prefix = "zeus.export")
public class ExportProperties {

    /**
     * 是否通过 RocketMQ 事务消息提交任务。关闭后直接写入待处理任务，由定时任务执行。
     */
    private boolean mqTransactionEnabled = true;
}
