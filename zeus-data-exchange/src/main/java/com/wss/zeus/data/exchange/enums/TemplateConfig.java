package com.wss.zeus.data.exchange.enums;

import com.wss.zeus.core.body.TableRowBaseData;
import com.wss.zeus.data.exchange.handler.ExcelTableRowConvertor;

import java.util.Arrays;

/**
 * @author: wangshusheng
 * @Date: 2026-08-12 18:26
 */
public interface TemplateConfig {

    String getType();

    Class<? extends TableRowBaseData> getTemplate();

    Class<? extends ExcelTableRowConvertor> getConvertor();

    // 通用查找逻辑（模板方法模式）
    static <E extends Enum<E> & TemplateConfig> E of(Class<E> enumClass, String code) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.getType().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("code非法"));
    }

}
