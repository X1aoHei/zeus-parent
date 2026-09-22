package com.wss.zeus.data.exchange.job;

import com.wss.zeus.data.exchange.constant.ExportTaskConstant;
import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;
import com.wss.zeus.data.exchange.handler.ExcelExportExecutor;
import com.wss.zeus.data.exchange.service.ExportTaskService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

/**
 * 导出任务Job（兜底定时任务）
 * <p>
 * 轮询 Pending、未超限的 Fail，以及超时仍停在 Processing 的任务。执行器内部用锁和 version 互斥。
 * </p>
 *
 * @author wangshusheng
 */
@RequiredArgsConstructor
public class ExportTaskJob {

    private final ExportTaskService exportTaskService;
    private final ExcelExportExecutor excelExportExecutor;
    private final ThreadPoolTaskExecutor exportTaskExecutor;

    @XxlJob("exportTaskForceExecute")
    public void exportTaskForceExecute() {
        List<ExcelExportTaskEntity> tasks = exportTaskService.listPendingTasks(ExportTaskConstant.RECOVER_BATCH_SIZE);
        if (CollectionUtils.isEmpty(tasks)) {
            return;
        }

        for (ExcelExportTaskEntity task : tasks) {
            exportTaskExecutor.submit(() -> excelExportExecutor.execute(task));
        }
    }
}
