package com.wss.zeus.data.exchange.starter;

import com.wss.zeus.data.exchange.enums.TemplateConfig;
import com.wss.zeus.data.exchange.factory.ExcelFeignBeanFactory;
import com.wss.zeus.core.registrar.AbstractAnnotationRegistrar;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;

/**
 * ExcelFeign Bean 定义注册器
 * <p>
 * 继承 {@link AbstractAnnotationRegistrar}，实现从 @EnableExcelFeign 注解获取 template 属性并注册 ExcelFeignBeanFactory
 * </p>
 *
 * @author wangshusheng
 * @see EnableExcelFeign
 * @see AbstractAnnotationRegistrar
 */
public class ExcelFeignImportBeanDefinitionRegistrar extends AbstractAnnotationRegistrar<EnableExcelFeign> {

    @Override
    protected Class<EnableExcelFeign> getAnnotationType() {
        return EnableExcelFeign.class;
    }

    @Override
    protected void doRegister(AnnotationAttributes attributes, BeanDefinitionRegistry registry) {
        // 获取 template 属性值
        getClassAttribute(attributes, "template", TemplateConfig.class)
                .ifPresent(templateConfigClass -> {
                    // 注册 ExcelFeignBeanFactory，通过构造注入 templateConfigClass
                    registerBeanDefinition(registry, "excelFeignBeanFactory",
                            ExcelFeignBeanFactory.class, templateConfigClass);
                });
    }
}
