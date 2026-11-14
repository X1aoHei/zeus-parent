package com.wss.zeus.data.exchange.business.example.api.req;

import lombok.Data;

/**
 * @author: wangshusheng
 * @Date: 2026-09-22 18:04
 */
@Data
public class TestExcelTableDataReq {

    /**
     * 页码
     */
    private Integer pageNo;

    /**
     * 每页条数
     */
    private Integer pageSize;
}
