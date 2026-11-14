package com.wss.zeus.data.exchange.center.example.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.wss.zeus.core.body.TableRowBaseData;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: wangshusheng
 * @Date: 2026-09-22 18:09
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TestTable extends TableRowBaseData {
    @ExcelProperty("订单编号")
    private String orderNo;
    @ExcelProperty("用户编号")
    private String userNo;

}
