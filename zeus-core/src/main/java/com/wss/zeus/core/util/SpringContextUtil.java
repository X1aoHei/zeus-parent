package com.wss.zeus.core.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * Spring 上下文工具类
 * <p>
 * 提供从 ApplicationContext 获取 Bean 的静态方法
 * </p>
 *
 * @author wangshusheng
 */
@Slf4j
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        SpringContextUtil.applicationContext = context;
        log.info("SpringContextUtil initialized with ApplicationContext: {}", context);
    }

    /**
     * 获取 ApplicationContext
     *
     * @return ApplicationContext
     * @throws IllegalStateException 如果 ApplicationContext 未初始化
     */
    public static ApplicationContext getApplicationContext() {
        if (Objects.isNull(applicationContext)) {
            throw new IllegalStateException("ApplicationContext is not initialized");
        }
        return applicationContext;
    }

    /**
     * 根据 Bean 名称获取 Bean
     *
     * @param name Bean 名称
     * @return Bean 实例
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
     * 根据 Bean 类型获取 Bean
     *
     * @param clazz Bean 类型
     * @param <T>   Bean 类型
     * @return Bean 实例
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 根据 Bean 名称和类型获取 Bean
     *
     * @param name  Bean 名称
     * @param clazz Bean 类型
     * @param <T>   Bean 类型
     * @return Bean 实例
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    /**
     * 根据 Bean 名称获取 Bean（Optional 方式）
     *
     * @param name Bean 名称
     * @return Bean 实例 Optional
     */
    public static Optional<Object> getBeanOptional(String name) {
        try {
            return Optional.ofNullable(getApplicationContext().getBean(name));
        } catch (Exception e) {
            log.debug("Bean not found: {}", name);
            return Optional.empty();
        }
    }

    /**
     * 根据 Bean 类型获取 Bean（Optional 方式）
     *
     * @param clazz Bean 类型
     * @param <T>   Bean 类型
     * @return Bean 实例 Optional
     */
    public static <T> Optional<T> getBeanOptional(Class<T> clazz) {
        try {
            return Optional.ofNullable(getApplicationContext().getBean(clazz));
        } catch (Exception e) {
            log.debug("Bean not found for type: {}", clazz.getName());
            return Optional.empty();
        }
    }

    /**
     * 检查 Bean 是否存在
     *
     * @param name Bean 名称
     * @return 是否存在
     */
    public static boolean containsBean(String name) {
        return getApplicationContext().containsBean(name);
    }

    /**
     * 检查 Bean 是否存在
     *
     * @param clazz Bean 类型
     * @return 是否存在
     */
    public static boolean containsBean(Class<?> clazz) {
        try {
            getApplicationContext().getBean(clazz);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
