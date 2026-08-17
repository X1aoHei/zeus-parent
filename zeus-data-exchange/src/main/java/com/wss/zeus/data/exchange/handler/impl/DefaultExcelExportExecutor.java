package com.wss.zeus.data.exchange.handler.impl;

import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;
import com.wss.zeus.data.exchange.handler.ExcelExportExecutor;
import com.wss.zeus.data.exchange.handler.ExcelExportResult;
import com.wss.zeus.data.exchange.handler.ExcelFeignHandler;
import com.wss.zeus.data.exchange.mq.ExportMqConstants;
import com.wss.zeus.data.exchange.repository.ExcelExportTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 导出任务执行器（带互斥保护）
 * <p>
 * 职责：分布式锁 → 状态判断 → 状态更新 → 调用 handler → 状态回写
 * </p>
 *
 * @author wangshusheng
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultExcelExportExecutor implements ExcelExportExecutor {

    private final ExcelFeignHandler excelFeignHandler;
    private final ExcelExportTaskRepository excelExportTaskRepository;
    private final RedissonClient redissonClient;

    @Override
    public void execute(ExcelExportTaskEntity task) {
        String taskId = task.getTaskId();
        String lockKey = ExportMqConstants.EXECUTE_LOCK_KEY_PREFIX + taskId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 1. 获取分布式锁（看门狗自动续期）
            boolean locked = lock.tryLock(
                    ExportMqConstants.LOCK_WAIT_TIME,
                    TimeUnit.SECONDS
            );
            if (!locked) {
                log.info("获取分布式锁失败，跳过执行, taskId={}", taskId);
                return;
            }

            // 2. 查询最新任务状态
            ExcelExportTaskEntity latestTask = excelExportTaskRepository.getByTaskId(taskId);
            if (Objects.isNull(latestTask)) {
                log.warn("任务不存在, taskId={}", taskId);
                return;
            }

            // 3. 判断状态是否为 Pending
            if (!"Pending".equals(latestTask.getStatus())) {
                log.info("任务状态不是Pending，跳过执行, taskId={}, status={}", taskId, latestTask.getStatus());
                return;
            }

            // 4. 更新状态为 Processing
            excelExportTaskRepository.updateStatusToProcessing(taskId);

            // 5. 调用 handler 执行导出（只做导出逻辑，不更新状态）
            ExcelExportResult result = excelFeignHandler.execute(latestTask);

            // 6. 更新状态为 Success，回写文件信息
            excelExportTaskRepository.updateStatusToSuccess(taskId, result.getFileId(), result.getFileName());
            log.info("导出任务执行成功, taskId={}, fileId={}", taskId, result.getFileId());

        } catch (Exception e) {
            log.error("导出任务执行失败, taskId={}", taskId, e);
            excelExportTaskRepository.updateStatusToFail(taskId, e.getMessage());
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
