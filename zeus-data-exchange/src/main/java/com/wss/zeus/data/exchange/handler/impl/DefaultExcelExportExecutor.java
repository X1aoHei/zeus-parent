package com.wss.zeus.data.exchange.handler.impl;

import com.wss.zeus.data.exchange.constant.ExportTaskConstant;
import com.wss.zeus.data.exchange.constant.RedisConstant;
import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;
import com.wss.zeus.data.exchange.enums.ExportTaskStatusEnum;
import com.wss.zeus.data.exchange.handler.ExcelExportExecutor;
import com.wss.zeus.data.exchange.handler.ExcelExportResult;
import com.wss.zeus.data.exchange.handler.ExcelFeignHandler;
import com.wss.zeus.data.exchange.mq.ExportMqConstants;
import com.wss.zeus.data.exchange.repository.ExcelExportTaskRepository;
import com.wss.zeus.data.exchange.service.ExportSubmitIdempotentStore;
import com.wss.zeus.data.exchange.support.ExportIdempotentKeyBuilder;
import com.wss.zeus.redis.lock.DistributedLockExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 导出任务执行器（带互斥保护）
 * <p>
 * 职责：分布式锁 → 状态判断 → 乐观锁抢占 → 调用 handler → 状态回写
 * </p>
 *
 * @author wangshusheng
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultExcelExportExecutor implements ExcelExportExecutor {

    private final ExcelFeignHandler excelFeignHandler;
    private final ExcelExportTaskRepository excelExportTaskRepository;
    private final DistributedLockExecutor distributedLockExecutor;
    private final ExportSubmitIdempotentStore exportSubmitIdempotentStore;

    @Override
    public void execute(ExcelExportTaskEntity task) {
        if (Objects.isNull(task) || StringUtils.isBlank(task.getTaskId())) {
            log.warn("导出任务为空，跳过执行");
            return;
        }
        String lockKey = String.format(RedisConstant.EXPORT_EXECUTE_LOCK_KEY, task.getTaskId());
        distributedLockExecutor.tryExecuteWithLock(lockKey, ExportMqConstants.LOCK_WAIT_TIME, () -> doExecute(task));
    }

    /**
     * 抢到执行锁后按 version 推进状态 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    private void doExecute(ExcelExportTaskEntity task) {
        ExcelExportTaskEntity latestTask = excelExportTaskRepository.getByTaskId(task.getTaskId());
        if (Objects.isNull(latestTask) || !doCanExecute(latestTask.getStatus())) {
            log.info("任务不存在或状态不可执行, taskId={}, status={}",
                    task.getTaskId(), Objects.isNull(latestTask) ? null : latestTask.getStatus());
            return;
        }
        if (!excelExportTaskRepository.compareAndSetProcessing(latestTask.getTaskId(), latestTask.getVersion(), latestTask.getStatus())) {
            log.info("任务乐观锁抢占失败, taskId={}", latestTask.getTaskId());
            return;
        }
        Integer processingVersion = ExportTaskConstant.nextVersion(latestTask.getVersion());
        try {
            ExcelExportResult result = excelFeignHandler.execute(latestTask);
            doMarkSuccess(latestTask, processingVersion, result);
        } catch (Exception exception) {
            doMarkFail(latestTask, processingVersion, exception);
        }
    }

    /**
     * Pending、Fail，以及兜底扫到的 Processing 可以执行 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    private boolean doCanExecute(String status) {
        return Objects.equals(status, ExportTaskStatusEnum.PENDING.getValue())
                || Objects.equals(status, ExportTaskStatusEnum.FAIL.getValue())
                || Objects.equals(status, ExportTaskStatusEnum.PROCESSING.getValue());
    }

    /**
     * 回写成功 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    private void doMarkSuccess(ExcelExportTaskEntity task, Integer version, ExcelExportResult result) {
        if (Objects.isNull(result) || StringUtils.isBlank(result.getFileId())) {
            doMarkFail(task, version, new IllegalStateException(ExportTaskConstant.FILE_ID_EMPTY));
            return;
        }
        boolean updated = excelExportTaskRepository.compareAndSetSuccess(
                task.getTaskId(), version, result.getFileId(), result.getFileName());
        if (!updated) {
            log.error("导出成功但状态回写失败, taskId={}", task.getTaskId());
            return;
        }
        log.info("导出任务执行成功, taskId={}, fileId={}", task.getTaskId(), result.getFileId());
    }

    /**
     * 回写失败并释放提交幂等，允许重新提交 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    private void doMarkFail(ExcelExportTaskEntity task, Integer version, Exception exception) {
        log.error("导出任务执行失败, taskId={}", task.getTaskId(), exception);
        excelExportTaskRepository.compareAndSetFail(task.getTaskId(), version, exception.getMessage());
        exportSubmitIdempotentStore.release(ExportIdempotentKeyBuilder.build(
                task.getTemplateCode(), task.getOperatorUserId(), task.getTaskParam()));
    }
}
