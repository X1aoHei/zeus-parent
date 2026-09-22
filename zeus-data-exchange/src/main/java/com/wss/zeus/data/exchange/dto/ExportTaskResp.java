package com.wss.zeus.data.exchange.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 导出任务查询结果
 *
 * @author wangshusheng
 */
@Data
public class ExportTaskResp {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 模版CODE
     */
    private String templateCode;

    /**
     * 文件ID
     */
    private String fileId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 状态：待处理=Pending、处理中=Processing、成功=Success、失败=Fail
     */
    private String status;

    /**
     * 完成时间
     */
    private LocalDateTime finishTime;

    /**
     * 操作人ID
     */
    private Long operatorUserId;

    /**
     * 操作人昵称
     */
    private String operatorUserName;

    /**
     * 错误原因
     */
    private String errorReason;

    /**
     * 失败次数
     */
    private Integer failCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
