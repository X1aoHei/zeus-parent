package com.wss.zeus.web.config;

import com.wss.zeus.web.handler.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Web 模块自动配置
 *
 * @author wangshusheng
 */
@AutoConfiguration
public class WebAutoConfiguration {

    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}
