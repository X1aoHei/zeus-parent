package com.wss.zeus.data.exchange.starter.config;

import com.wss.zeus.data.exchange.handler.ExcelExportExecutor;
import com.wss.zeus.data.exchange.job.ExportTaskJob;
import com.wss.zeus.data.exchange.service.ExportTaskService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 数据交换模块自动配置
 *
 * @author wangshusheng
 */
@AutoConfiguration
@MapperScan("com.wss.zeus.data.exchange.mapper")
public class DataExchangeAutoConfiguration {

    @Bean
    public ExportTaskJob exportTaskJob(ExportTaskService exportTaskService,
                                       ExcelExportExecutor excelExportExecutor,
                                       ThreadPoolTaskExecutor exportTaskExecutor) {
        return new ExportTaskJob(exportTaskService, excelExportExecutor, exportTaskExecutor);
    }
}
