package com.wss.zeus.core.config;

import com.wss.zeus.core.util.SpringContextUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Zeus 核心自动配置 - 系AI自动生成
 *
 * @author WSS AI Agent @Date 2026-09-22
 */
@AutoConfiguration
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
}
