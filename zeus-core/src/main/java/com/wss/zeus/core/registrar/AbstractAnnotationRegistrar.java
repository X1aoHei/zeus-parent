package com.wss.zeus.core.registrar;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Optional;

/**
 * 注解驱动的 Bean 定义注册器抽象基类
 * <p>
 * 提供从注解获取属性并注册 Bean 的通用能力
 * </p>
 *
 * @param <T> 注解类型
 * @author wangshusheng
 */
@Slf4j
public abstract class AbstractAnnotationRegistrar<T extends Annotation> implements ImportBeanDefinitionRegistrar {

    /**
     * 获取注解类型
     *
     * @return 注解类型
     */
    protected abstract Class<T> getAnnotationType();

    /**
     * 执行 Bean 注册逻辑
     *
     * @param attributes 注解属性
     * @param registry   Bean 定义注册器
     */
    protected abstract void doRegister(AnnotationAttributes attributes, BeanDefinitionRegistry registry);

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        Class<T> annotationType = getAnnotationType();
        String annotationName = annotationType.getName();

        // 获取注解属性
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(annotationName));

        if (Objects.isNull(attributes)) {
            log.debug("@{} not found on {}", annotationType.getSimpleName(), metadata.getClassName());
            return;
        }

        log.info("Processing @{} on {}", annotationType.getSimpleName(), metadata.getClassName());
        doRegister(attributes, registry);
    }

    /**
     * 获取注解属性值（Class 类型）
     *
     * @param attributes    注解属性
     * @param attributeName 属性名
     * @param defaultClass  默认值（用于过滤）
     * @param <V>           属性值类型
     * @return 属性值 Optional
     */
    @SuppressWarnings("unchecked")
    protected <V> Optional<Class<? extends V>> getClassAttribute(AnnotationAttributes attributes,
                                                                  String attributeName,
                                                                  Class<V> defaultClass) {
        Class<?> value = attributes.getClass(attributeName);
        if (Objects.isNull(value) || Objects.equals(value, defaultClass)) {
            return Optional.empty();
        }
        return Optional.of((Class<? extends V>) value);
    }

    /**
     * 获取字符串属性值
     *
     * @param attributes    注解属性
     * @param attributeName 属性名
     * @return 属性值 Optional
     */
    protected Optional<String> getStringAttribute(AnnotationAttributes attributes, String attributeName) {
        String value = attributes.getString(attributeName);
        return Optional.ofNullable(value)
                .filter(v -> !v.isEmpty());
    }

    /**
     * 注册 Bean 定义
     *
     * @param registry    Bean 定义注册器
     * @param beanName    Bean 名称
     * @param beanClass   Bean 类
     * @param constructorArgs 构造参数
     */
    protected void registerBeanDefinition(BeanDefinitionRegistry registry, String beanName,
                                          Class<?> beanClass, Object... constructorArgs) {
        if (registry.containsBeanDefinition(beanName)) {
            log.warn("Bean '{}' already exists, skipping registration", beanName);
            return;
        }

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(beanClass);
        for (Object arg : constructorArgs) {
            builder.addConstructorArgValue(arg);
        }

        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
        log.info("Registered bean '{}' of type {}", beanName, beanClass.getName());
    }

    /**
     * 注册 Bean 定义（使用类名作为 Bean 名称）
     *
     * @param registry       Bean 定义注册器
     * @param beanClass      Bean 类
     * @param constructorArgs 构造参数
     */
    protected void registerBeanDefinition(BeanDefinitionRegistry registry, Class<?> beanClass,
                                          Object... constructorArgs) {
        String beanName = beanClass.getSimpleName();
        registerBeanDefinition(registry, beanName, beanClass, constructorArgs);
    }
}
