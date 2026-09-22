package com.wss.zeus.data.exchange.processor;

import com.wss.zeus.core.annotation.ExcelFeign;
import com.wss.zeus.data.exchange.beans.ExcelFeignBean;
import com.wss.zeus.data.exchange.factory.ExcelFeignBeanFactory;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author: wangshusheng
 * @Date: 2026-08-12 16:03
 */
public class ExcelFeignPostProcessor implements BeanPostProcessor {

    private ExcelFeignBeanFactory excelFeignBeanFactory;

    public ExcelFeignPostProcessor(ExcelFeignBeanFactory excelFeignBeanFactory) {
        this.excelFeignBeanFactory = excelFeignBeanFactory;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        for (Class<?> type : doResolveTypes(bean)) {
            doRegister(bean, type);
        }
        return bean;
    }

    /**
     * 同时扫描目标类和接口，避免 JDK 代理上看不到 @ExcelFeign - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    private Set<Class<?>> doResolveTypes(Object bean) {
        Set<Class<?>> types = new LinkedHashSet<>();
        Class<?> target = AopUtils.getTargetClass(bean);
        if (Objects.nonNull(target)) {
            types.add(target);
        }
        types.addAll(ClassUtils.getAllInterfacesForClassAsSet(bean.getClass()));
        return types;
    }

    /**
     * 注册带注解的方法 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    private void doRegister(Object bean, Class<?> type) {
        Map<Method, ExcelFeign> annotatedMethods = MethodIntrospector.selectMethods(type,
                (MethodIntrospector.MetadataLookup<ExcelFeign>) method ->
                        AnnotatedElementUtils.findMergedAnnotation(method, ExcelFeign.class));
        if (MapUtils.isEmpty(annotatedMethods)) {
            return;
        }
        for (Map.Entry<Method, ExcelFeign> entry : annotatedMethods.entrySet()) {
            ExcelFeign excelFeign = entry.getValue();
            if (Objects.isNull(excelFeign) || StringUtils.isBlank(excelFeign.name())) {
                continue;
            }
            if (Objects.nonNull(excelFeignBeanFactory.get(excelFeign.name()))) {
                continue;
            }
            excelFeignBeanFactory.put(excelFeign.name(),
                    new ExcelFeignBean(excelFeign.name(), entry.getKey(), bean, excelFeign));
        }
    }
}
