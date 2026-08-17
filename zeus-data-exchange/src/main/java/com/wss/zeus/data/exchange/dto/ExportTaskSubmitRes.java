package com.wss.zeus.data.exchange.dto;

import lombok.Data;

/**
 * 导出任务提交响应
 *
 * @author wangshusheng
 */
@Data
public class ExportTaskSubmitRes {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务状态
     */
    private String status;

    /**
     * 是否重复提交（幂等命中）
     */
    private Boolean duplicate;
}
