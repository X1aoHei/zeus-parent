package com.wss.zeus.data.exchange.config;

import com.wss.zeus.core.common.file.FileStorageService;
import com.wss.zeus.data.exchange.factory.ExcelFeignBeanFactory;
import com.wss.zeus.data.exchange.handler.ExcelExportExecutor;
import com.wss.zeus.data.exchange.handler.impl.DefaultExcelExportExecutor;
import com.wss.zeus.data.exchange.handler.impl.DefaultExcelFeignHandler;
import com.wss.zeus.data.exchange.processor.ExcelFeignPostProcessor;
import com.wss.zeus.data.exchange.repository.ExcelExportTaskRepository;
import com.wss.zeus.data.exchange.service.ExportSubmitIdempotentStore;
import com.wss.zeus.redis.lock.DistributedLockExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ExcelFeign 配置
 * <p>
 * 由 {@code @EnableExcelFeign} 导入，注册导出执行链。
 * </p>
 *
 * @author wangshusheng
 */
@Configuration
public class ExcelFeignConfiguration {

    /**
     * 注册 ExcelFeignPostProcessor
     */
    @Bean
    public ExcelFeignPostProcessor excelFeignPostProcessor(ExcelFeignBeanFactory excelFeignBeanFactory) {
        return new ExcelFeignPostProcessor(excelFeignBeanFactory);
    }

    /**
     * 注册 DefaultExcelFeignHandler
     */
    @Bean
    public DefaultExcelFeignHandler defaultExcelFeignHandler(ExcelFeignBeanFactory excelFeignBeanFactory,
                                                             FileStorageService fileStorageService) {
        return new DefaultExcelFeignHandler(excelFeignBeanFactory, fileStorageService);
    }

    /**
     * 注册 ExcelExportExecutor（带互斥保护的执行器）
     */
    @Bean
    public ExcelExportExecutor excelExportExecutor(DefaultExcelFeignHandler defaultExcelFeignHandler,
                                                   ExcelExportTaskRepository excelExportTaskRepository,
                                                   DistributedLockExecutor distributedLockExecutor,
                                                   ExportSubmitIdempotentStore exportSubmitIdempotentStore) {
        return new DefaultExcelExportExecutor(defaultExcelFeignHandler, excelExportTaskRepository,
                distributedLockExecutor, exportSubmitIdempotentStore);
    }
}
