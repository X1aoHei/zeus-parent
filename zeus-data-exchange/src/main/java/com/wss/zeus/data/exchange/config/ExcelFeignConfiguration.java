package com.wss.zeus.data.exchange.config;

import com.wss.zeus.core.common.file.FileStorageService;
import com.wss.zeus.data.exchange.factory.ExcelFeignBeanFactory;
import com.wss.zeus.data.exchange.handler.ExcelExportExecutor;
import com.wss.zeus.data.exchange.handler.impl.DefaultExcelExportExecutor;
import com.wss.zeus.data.exchange.handler.impl.DefaultExcelFeignHandler;
import com.wss.zeus.data.exchange.processor.ExcelFeignPostProcessor;
import com.wss.zeus.data.exchange.repository.ExcelExportTaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ExcelFeign 自动配置类
 * <p>
 * 当配置 {@code excel-feign.enabled=true} 时（默认为 true），
 * 会自动注入 ExcelFeignPostProcessor 和 ExcelFeignBeanFactory
 * </p>
 *
 * @author wangshusheng
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "excel-feign", name = "enabled", havingValue = "true", matchIfMissing = true)
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
                                                   RedissonClient redissonClient) {
        return new DefaultExcelExportExecutor(defaultExcelFeignHandler, excelExportTaskRepository, redissonClient);
    }
}
