package com.wss.zeus.feign.config;

import com.wss.zeus.feign.interceptor.FeignTraceInterceptor;
import feign.RequestInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Zeus Feign 自动配置
 *
 * @author wangshusheng
 */
@AutoConfiguration
@ConditionalOnClass(RequestInterceptor.class)
public class ZeusFeignAutoConfiguration {

    /**
     * Feign 请求/响应 trace 日志拦截器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "zeus.feign.trace", name = "enabled", havingValue = "true", matchIfMissing = true)
    public FeignTraceInterceptor feignTraceInterceptor() {
        return new FeignTraceInterceptor();
    }
}
