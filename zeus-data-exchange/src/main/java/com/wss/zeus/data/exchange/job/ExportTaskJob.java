package com.wss.zeus.data.exchange.job;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;
import com.wss.zeus.data.exchange.handler.ExcelFeignHandler;
import com.wss.zeus.data.exchange.service.ExportTaskService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

/**
 * 导出任务Job
 *
 * @author wangshusheng
 */
@Slf4j
@RequiredArgsConstructor
public class ExportTaskJob {

    private final ExportTaskService exportTaskService;
    private final ExcelFeignHandler excelFeignHandler;
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
            JSONObject param = JSON.parseObject(task.getTaskParam());
            excelFeignHandler.execute(task.getTemplateCode(), param);
        } catch (Exception e) {
            log.error("导出任务执行失败, taskId={}", task.getTaskId(), e);
        }
    }
}
