package com.wss.zeus.data.exchange.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件导出任务实体
 *
 * @author wangshusheng
 */
@Data
@TableName("excel_export_task_copy")
public class ExcelExportTaskEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

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
     * 文件类型 XLSX
     */
    private String fileType;

    /**
     * 任务参数
     */
    private String taskParam;

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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标识 0未删除 1已删除
     */
    @TableLogic
    private Integer isDelete;
}
