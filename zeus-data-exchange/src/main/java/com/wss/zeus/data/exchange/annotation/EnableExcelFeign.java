package com.wss.zeus.data.exchange.annotation;

import com.wss.zeus.data.exchange.config.ExcelFeignConfiguration;
import com.wss.zeus.data.exchange.enums.TemplateConfig;
import com.wss.zeus.data.exchange.registrar.ExcelFeignImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 ExcelFeign 功能的注解
 * <p>
 * 在应用启动类上添加此注解，即可启用 ExcelFeign 功能。
 * </p>
 *
 * <p>使用示例：</p>
 * <pre>
 * &#64;SpringBootApplication
 * &#64;EnableExcelFeign(template = MyTemplateConfig.class)
 * public class Application {
 *     public static void main(String[] args) {
 *         SpringApplication.run(Application.class, args);
 *     }
 * }
 * </pre>
 *
 * @author wangshusheng
 * @see ExcelFeignConfiguration
 * @see ExcelFeignImportBeanDefinitionRegistrar
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ExcelFeignImportBeanDefinitionRegistrar.class, ExcelFeignConfiguration.class})
public @interface EnableExcelFeign {

    /**
     * 模板配置类
     * <p>
     * 指定 TemplateConfig 的实现类，用于 Excel 模板配置
     * </p>
     *
     * @return TemplateConfig 实现类
     */
    Class<? extends TemplateConfig> template();
}
