package com.wss.zeus.data.exchange.center.example.convertor;

import com.wss.zeus.core.body.TableRowBaseData;
import com.wss.zeus.core.common.PageData;
import com.wss.zeus.data.exchange.business.example.api.resp.TestExcelTableData;
import com.wss.zeus.data.exchange.center.example.excel.TestTable;
import com.wss.zeus.data.exchange.handler.ExcelTableRowConvertor;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

/**
 * @author: wangshusheng
 * @Date: 2026-09-22 18:07
 */
@Mapper
public interface TestTableConvertor extends ExcelTableRowConvertor<TableRowBaseData, TestTable> {

    /**
     * 分页结果转成 Excel 行 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-28
     */
    @Override
    PageData<TestTable> convert(PageData<TableRowBaseData> pageData);

    /**
     * 按运行时类型转成 Excel 行 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-28
     */
    @SubclassMapping(source = TestExcelTableData.class, target = TestTable.class)
    TestTable toTable(TableRowBaseData row);

    /**
     * 业务行转成 Excel 行 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-28
     */
    TestTable toTable(TestExcelTableData data);
}
