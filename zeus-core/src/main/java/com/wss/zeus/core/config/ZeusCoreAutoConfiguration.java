package com.wss.zeus.core.config;

import com.wss.zeus.core.aspect.ControllerTraceAspect;
import com.wss.zeus.core.common.file.FileStorageService;
import com.wss.zeus.core.common.file.LocalFileProperties;
import com.wss.zeus.core.common.file.LocalFileStorageService;
import com.wss.zeus.core.util.SpringContextUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

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
