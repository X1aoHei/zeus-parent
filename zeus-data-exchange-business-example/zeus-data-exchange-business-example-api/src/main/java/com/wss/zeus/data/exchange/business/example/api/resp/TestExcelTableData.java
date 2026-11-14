package com.wss.zeus.data.exchange.business.example.api.resp;

import com.wss.zeus.core.body.TableRowBaseData;
import lombok.Data;

/**
 * @author: wangshusheng
 * @Date: 2026-09-22 17:58
 */
@Data
public class TestExcelTableData extends TableRowBaseData {
    /**
     * 订单编号
     */
    private String orderNo;
    /**
     * 用户编号
     */
    private String userNo;
}
