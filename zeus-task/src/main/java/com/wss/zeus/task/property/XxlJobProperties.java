package com.wss.zeus.task.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: wangshusheng
 * @Date: 2026-08-14 16:13
 */
@Data
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobProperties {
    /**
     * 是否启用 XXL-JOB 执行器
     */
    private boolean enabled = false;

    /**
     * 调度中心部署根地址 [选填]
     */
    private String adminAddresses;

    /**
     * 执行器 AppName [选填]
     */
    private String appName;

    /**
     * 执行器 IP [选填]
     */
    private String ip;

    /**
     * 执行器端口号 [选填]
     */
    private int port = 9999;

    /**
     * 执行器通讯TOKEN [选填]
     */
    private String accessToken;

    /**
     * 执行器运行日志文件存储磁盘路径 [选填]
     */
    private String logPath = "/data/applogs/xxl-job/jobhandler";

    /**
     * 执行器日志文件保存天数 [选填]
     */
    private int logRetentionDays = 30;
}
