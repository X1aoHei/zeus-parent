package com.wss.zeus.data.exchange.dto;

import lombok.Data;

/**
 * 导出任务提交请求
 *
 * @author wangshusheng
 */
@Data
public class ExportTaskSubmitReq {

    /**
     * 模版CODE（对应 @ExcelFeign 的 name）
     */
    private String templateCode;

    /**
     * 任务参数（JSON）
     */
    private String taskParam;

    /**
     * 操作人ID
     */
    private Long operatorUserId;

    /**
     * 操作人昵称
     */
    private String operatorUserName;
}
