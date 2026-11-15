package com.wss.zeus.core.config;

import com.wss.zeus.core.aspect.ControllerTraceAspect;
import com.wss.zeus.core.common.file.FileStorageService;
import com.wss.zeus.core.common.file.LocalFileProperties;
import com.wss.zeus.core.common.file.LocalFileStorageService;
import com.wss.zeus.core.trace.TraceIdFilter;
import com.wss.zeus.core.util.SpringContextUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

/**
 * Zeus 核心自动配置 - 系AI自动生成
 *
 * @author WSS AI Agent @Date 2026-09-22
 */
@AutoConfiguration
@EnableConfigurationProperties(LocalFileProperties.class)
public class ZeusCoreAutoConfiguration {

    /**
     * 注册 Spring 上下文工具 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    @Bean
    @ConditionalOnMissingBean
    public SpringContextUtil springContextUtil() {
        return new SpringContextUtil();
    }

    /**
     * 没有自定义文件存储时使用本地目录 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-28
     */
    @Bean
    @ConditionalOnMissingBean(FileStorageService.class)
    @ConditionalOnProperty(prefix = "zeus.file.local", name = "enabled", havingValue = "true", matchIfMissing = true)
    public LocalFileStorageService localFileStorageService(LocalFileProperties localFileProperties) {
        return new LocalFileStorageService(localFileProperties);
    }

    /**
     * HTTP 统一入口：恢复或初始化 traceId 到线程本地变量 / MDC
     */
    @Bean
    @ConditionalOnMissingBean(name = "traceIdFilterRegistration")
    @ConditionalOnClass(name = "jakarta.servlet.Filter")
    @ConditionalOnProperty(prefix = "zeus.trace", name = "enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<TraceIdFilter> traceIdFilterRegistration() {
        FilterRegistrationBean<TraceIdFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TraceIdFilter());
        registration.addUrlPatterns("/*");
        registration.setName("zeusTraceIdFilter");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 100);
        return registration;
    }

    /**
     * Controller 接口的请求/响应 trace 日志切面
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = "org.springframework.web.bind.annotation.RestController")
    @ConditionalOnProperty(prefix = "zeus.controller.trace", name = "enabled", havingValue = "true", matchIfMissing = true)
    public ControllerTraceAspect controllerTraceAspect() {
        return new ControllerTraceAspect();
    }
}
