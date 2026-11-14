package com.wss.zeus.data.exchange.factory;

import com.wss.zeus.data.exchange.beans.ExcelFeignBean;
import com.wss.zeus.data.exchange.constant.ExportTaskConstant;
import com.wss.zeus.data.exchange.enums.TemplateConfig;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ExcelFeign Bean 工厂
 * <p>
 * 存储所有被 @ExcelFeign 注解标记的方法信息
 * </p>
 *
 * @author wangshusheng
 */
@Getter
public class ExcelFeignBeanFactory<T extends Enum<T> & TemplateConfig> {

    private final Map<String, ExcelFeignBean> methodMap;

    /**
     * 模板配置枚举类
     * <p>
     * 从 @EnableExcelFeign 注解的 template 属性获取，必须是实现了 TemplateConfig 的枚举
     * </p>
     */
    private Class<T> templateConfigClass;

    public ExcelFeignBeanFactory() {
        this.methodMap = new ConcurrentHashMap<>(256);
    }

    /**
     * 构造函数，用于构造注入
     *
     * @param templateConfigClass TemplateConfig 枚举类
     */
    public ExcelFeignBeanFactory(Class<T> templateConfigClass) {
        this();
        this.templateConfigClass = templateConfigClass;
    }

    public void put(String name, ExcelFeignBean excelFeignBean) {
        this.methodMap.put(name, excelFeignBean);
    }

    public ExcelFeignBean get(String name) {
        return this.methodMap.get(name);
    }

    /**
     * 先按类型标识查找，再按注解上的模板类查找 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-28
     */
    public TemplateConfig getTemplateConfig(String type, Class<?> templateClass) {
        if (Objects.isNull(templateConfigClass)) {
            throw new IllegalStateException(ExportTaskConstant.TEMPLATE_ENUM_INVALID);
        }
        TemplateConfig matched = TemplateConfig.find(templateConfigClass, type);
        if (Objects.isNull(matched)) {
            matched = TemplateConfig.ofTemplate(templateConfigClass, templateClass);
        }
        if (Objects.isNull(matched)) {
            throw new IllegalArgumentException(ExportTaskConstant.TEMPLATE_CODE_INVALID + ": " + type);
        }
        return matched;
    }

}
