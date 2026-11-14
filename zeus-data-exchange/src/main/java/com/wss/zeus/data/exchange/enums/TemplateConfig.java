package com.wss.zeus.data.exchange.enums;

import com.wss.zeus.core.body.TableRowBaseData;
import com.wss.zeus.data.exchange.constant.ExportTaskConstant;
import com.wss.zeus.data.exchange.handler.ExcelTableRowConvertor;

import java.util.Objects;

/**
 * @author: wangshusheng
 * @Date: 2026-08-12 18:26
 */
public interface TemplateConfig {

    String getType();

    Class<? extends TableRowBaseData> getTemplate();

    Class<? extends ExcelTableRowConvertor> getConvertor();

    /**
     * 按类型标识或枚举名查找模板配置 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-28
     */
    static <E extends Enum<E> & TemplateConfig> E of(Class<E> enumClass, String code) {
        E matched = find(enumClass, code);
        if (Objects.isNull(matched)) {
            throw new IllegalArgumentException(ExportTaskConstant.TEMPLATE_CODE_INVALID + ": " + code);
        }
        return matched;
    }

    /**
     * 按类型标识或枚举名查找，未命中返回空 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-28
     */
    static <E extends Enum<E> & TemplateConfig> E find(Class<E> enumClass, String code) {
        E[] constants = doConstants(enumClass);
        for (E item : constants) {
            if (Objects.equals(item.getType(), code) || Objects.equals(item.name(), code)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 按导出模板类查找配置，类型标识对不上时使用 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-28
     */
    static <E extends Enum<E> & TemplateConfig> E ofTemplate(Class<E> enumClass, Class<?> templateClass) {
        if (Objects.isNull(templateClass) || Objects.equals(templateClass, TableRowBaseData.class)) {
            return null;
        }
        E[] constants = doConstants(enumClass);
        for (E item : constants) {
            if (Objects.equals(item.getTemplate(), templateClass)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 读取枚举常量，类不是枚举时直接失败 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-28
     */
    private static <E extends Enum<E> & TemplateConfig> E[] doConstants(Class<E> enumClass) {
        if (Objects.isNull(enumClass) || !enumClass.isEnum()) {
            throw new IllegalArgumentException(ExportTaskConstant.TEMPLATE_ENUM_INVALID);
        }
        E[] constants = enumClass.getEnumConstants();
        if (Objects.isNull(constants)) {
            throw new IllegalArgumentException(ExportTaskConstant.TEMPLATE_ENUM_INVALID + ": " + enumClass.getName());
        }
        return constants;
    }
}
