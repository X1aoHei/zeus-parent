package com.wss.zeus.data.exchange.starter.config;

import com.wss.zeus.data.exchange.handler.ExcelFeignHandler;
import com.wss.zeus.data.exchange.job.ExportTaskJob;
import com.wss.zeus.data.exchange.service.ExportTaskService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author: wangshusheng
 * @Date: 2026-08-14 15:24
 */
@AutoConfiguration
@MapperScan("com.wss.zeus.data.exchange.mapper")
public class DataExchangeAutoConfiguration {

    @Bean
    public ExportTaskJob exportTaskJob(ExportTaskService exportTaskService, ExcelFeignHandler excelFeignHandler, ThreadPoolTaskExecutor exportTaskExecutor) {
        return new ExportTaskJob(exportTaskService, excelFeignHandler, exportTaskExecutor);
    }

}
