package com.wss.zeus.concurrent.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池自动配置
 *
 * @author wangshusheng
 */
@AutoConfiguration
public class ThreadPoolAutoConfiguration {

    /**
     * 导出任务线程池
     */
    @Bean("exportTaskExecutor")
    @ConditionalOnMissingBean(name = "exportTaskExecutor")
    @ConditionalOnProperty(prefix = "zeus.thread-pool", name = "export-enabled", havingValue = "true", matchIfMissing = true)
    public ThreadPoolTaskExecutor exportTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(0);
        executor.setThreadNamePrefix("zeus-export-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

}
