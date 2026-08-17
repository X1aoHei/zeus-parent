package com.wss.zeus.data.exchange.job;

import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;
import com.wss.zeus.data.exchange.handler.ExcelExportExecutor;
import com.wss.zeus.data.exchange.service.ExportTaskService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

/**
 * 导出任务Job（兜底定时任务）
 * <p>
 * 轮询 Pending 状态的任务，通过 ExcelExportExecutor 执行（内部包含互斥逻辑）
 * </p>
 *
 * @author wangshusheng
 */
@Slf4j
@RequiredArgsConstructor
public class ExportTaskJob {

    private final ExportTaskService exportTaskService;
    private final ExcelExportExecutor excelExportExecutor;
    private final ThreadPoolTaskExecutor exportTaskExecutor;

    @XxlJob("exportTaskForceExecute")
    public void exportTaskForceExecute() {
        List<ExcelExportTaskEntity> tasks = exportTaskService.listPendingTasks(5);
        if (tasks.isEmpty()) {
            return;
        }

        for (ExcelExportTaskEntity task : tasks) {
            exportTaskExecutor.submit(() -> doExecuteTask(task));
        }
    }

    private void doExecuteTask(ExcelExportTaskEntity task) {
        try {
            excelExportExecutor.execute(task);
        } catch (Exception e) {
            log.error("导出任务执行失败, taskId={}", task.getTaskId(), e);
        }
    }
}
