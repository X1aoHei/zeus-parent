package com.wss.zeus.data.exchange.center.example.template;

import com.wss.zeus.core.body.TableRowBaseData;
import com.wss.zeus.data.exchange.business.example.api.constant.ExcelConstant;
import com.wss.zeus.data.exchange.center.example.convertor.TestTableConvertor;
import com.wss.zeus.data.exchange.center.example.excel.TestTable;
import com.wss.zeus.data.exchange.enums.TemplateConfig;
import com.wss.zeus.data.exchange.handler.ExcelTableRowConvertor;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: wangshusheng
 * @Date: 2026-09-22 17:52
 */
@Getter
@AllArgsConstructor
public enum NormalTemplateConfig implements TemplateConfig {

    EXAMPLE(ExcelConstant.BIZ_NAME, TestTable.class, TestTableConvertor.class),
    ;

    private String type;

    private Class<? extends TableRowBaseData> template;

    private Class<? extends ExcelTableRowConvertor> convertor;
}
