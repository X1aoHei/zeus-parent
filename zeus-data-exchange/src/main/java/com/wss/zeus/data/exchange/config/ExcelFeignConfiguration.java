package com.wss.zeus.data.exchange.config;

import com.wss.zeus.data.exchange.factory.ExcelFeignBeanFactory;
import com.wss.zeus.data.exchange.handler.impl.DefaultExcelFeignHandler;
import com.wss.zeus.data.exchange.processor.ExcelFeignPostProcessor;
import lombok.extern.slf4j.Slf4j;
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
     *
     * @param excelFeignBeanFactory
     * @return ExcelFeignPostProcessor 实例
     */
    @Bean
    public ExcelFeignPostProcessor excelFeignPostProcessor(ExcelFeignBeanFactory excelFeignBeanFactory) {
        return new ExcelFeignPostProcessor(excelFeignBeanFactory);
    }

    @Bean
    public DefaultExcelFeignHandler defaultExcelFeignHandler(ExcelFeignBeanFactory excelFeignBeanFactory) {
        return new DefaultExcelFeignHandler(excelFeignBeanFactory);
    }
}
