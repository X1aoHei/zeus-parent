package com.wss.zeus.core.logging;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * 未配置 log.home 时，使用当前用户目录下的 logs，避免系统盘 /logs 无写权限
 *
 * @author wangshusheng
 */
public class ZeusLogHomeEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    static final String LOG_HOME_PROPERTY = "log.home";

    static final String PROPERTY_SOURCE_NAME = "zeusLogHome";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (StringUtils.hasText(environment.getProperty(LOG_HOME_PROPERTY))) {
            return;
        }
        Map<String, Object> source = new HashMap<>(1);
        source.put(LOG_HOME_PROPERTY, defaultLogHome(environment));
        environment.getPropertySources().addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, source));
    }

    private String defaultLogHome(ConfigurableEnvironment environment) {
        String userHome = environment.getProperty("user.home", ".");
        return Path.of(userHome, "logs").toString();
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
