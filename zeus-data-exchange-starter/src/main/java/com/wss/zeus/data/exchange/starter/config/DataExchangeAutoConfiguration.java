package com.wss.zeus.data.exchange.starter.config;

import com.wss.zeus.data.exchange.config.ExcelFeignConfiguration;
import com.wss.zeus.data.exchange.handler.ExcelExportExecutor;
import com.wss.zeus.data.exchange.job.ExportTaskJob;
import com.wss.zeus.data.exchange.repository.ExcelExportTaskRepository;
import com.wss.zeus.data.exchange.service.ExportTaskService;
import com.wss.zeus.data.exchange.starter.mq.ExportTaskMqConsumer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 数据交换模块自动配置
 *
 * @author wangshusheng
 */
@AutoConfiguration
@MapperScan("com.wss.zeus.data.exchange.mapper")
@ComponentScan(
        basePackages = "com.wss.zeus.data.exchange",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = ExcelFeignConfiguration.class
        )
)
public class DataExchangeAutoConfiguration {

    /**
     * 有导出执行器时才注册定时兜底 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    @Bean
    @ConditionalOnBean(ExcelExportExecutor.class)
    public ExportTaskJob exportTaskJob(ExportTaskService exportTaskService,
                                       ExcelExportExecutor excelExportExecutor,
                                       ThreadPoolTaskExecutor exportTaskExecutor) {
        return new ExportTaskJob(exportTaskService, excelExportExecutor, exportTaskExecutor);
    }

    /**
     * 有导出执行器时才注册 MQ 消费者 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    @Bean
    @ConditionalOnBean(ExcelExportExecutor.class)
    public ExportTaskMqConsumer exportTaskMqConsumer(ExcelExportTaskRepository excelExportTaskRepository,
                                                     ExcelExportExecutor excelExportExecutor) {
        return new ExportTaskMqConsumer(excelExportTaskRepository, excelExportExecutor);
    }
}
