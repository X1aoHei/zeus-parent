package com.wss.zeus.data.exchange.processor;

import com.wss.zeus.core.annotation.ExcelFeign;
import com.wss.zeus.data.exchange.beans.ExcelFeignBean;
import com.wss.zeus.data.exchange.factory.ExcelFeignBeanFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Map;

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
        Class<?> beanClass = bean.getClass();
        // 使用 Spring 的工具方法查找带有特定注解的方法
        Map<Method, ExcelFeign> annotatedMethods = MethodIntrospector.selectMethods(beanClass, (MethodIntrospector.MetadataLookup<ExcelFeign>) method -> AnnotatedElementUtils.findMergedAnnotation(method, ExcelFeign.class));
        if (CollectionUtils.isEmpty(annotatedMethods)) {
            return bean;
        }
        for (Map.Entry<Method, ExcelFeign> methodExcelFeignEntry : annotatedMethods.entrySet()) {
            Method method = methodExcelFeignEntry.getKey();
            ExcelFeign excelFeign = methodExcelFeignEntry.getValue();
            String name = excelFeign.name();
            ExcelFeignBean excelFeignBean = new ExcelFeignBean(name, method, bean, excelFeign);
            excelFeignBeanFactory.put(name, excelFeignBean);
        }
        return bean;
    }
}
