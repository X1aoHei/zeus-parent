package com.wss.zeus.data.exchange.support;

import com.wss.zeus.data.exchange.constant.ExportTaskConstant;
import com.wss.zeus.data.exchange.dto.ExportTaskSubmitReq;
import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;
import com.wss.zeus.data.exchange.enums.ExportTaskStatusEnum;

/**
 * 组装待处理导出任务 - 系AI自动生成
 *
 * @author WSS AI Agent @Date 2026-09-24
 */
public final class ExportPendingTaskFactory {

    private ExportPendingTaskFactory() {
    }

    /**
     * 按提交请求组装待处理任务 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-24
     */
    public static ExcelExportTaskEntity build(ExportTaskSubmitReq req, String taskId) {
        ExcelExportTaskEntity task = new ExcelExportTaskEntity();
        task.setTaskId(taskId);
        task.setRequestId(req.getRequestId());
        task.setTemplateCode(req.getTemplateCode());
        task.setTaskParam(req.getTaskParam());
        task.setOperatorUserId(req.getOperatorUserId());
        task.setOperatorUserName(req.getOperatorUserName());
        task.setStatus(ExportTaskStatusEnum.PENDING.getValue());
        task.setFileType(ExportTaskConstant.FILE_TYPE_XLSX);
        return task;
    }
}
