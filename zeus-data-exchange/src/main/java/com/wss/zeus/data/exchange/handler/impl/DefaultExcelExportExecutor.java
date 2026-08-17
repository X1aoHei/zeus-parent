package com.wss.zeus.data.exchange.handler.impl;

import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;
import com.wss.zeus.data.exchange.handler.ExcelExportExecutor;
import com.wss.zeus.data.exchange.handler.ExcelFeignHandler;
import com.wss.zeus.data.exchange.mq.ExportMqConstants;
import com.wss.zeus.data.exchange.repository.ExcelExportTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * 导出任务执行器（带互斥保护）
 * <p>
 * Redis 分布式锁做第一道拦截（快速失败）+ 乐观锁做第二道保障（数据库层面兜底）
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
        String lockKey = ExportMqConstants.LOCK_KEY_PREFIX + taskId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 第一道：Redis 分布式锁，快速失败
            boolean locked = lock.tryLock(
                    ExportMqConstants.LOCK_WAIT_TIME,
                    ExportMqConstants.LOCK_LEASE_TIME,
                    TimeUnit.SECONDS
            );
            if (!locked) {
                log.info("获取分布式锁失败，跳过执行, taskId={}", taskId);
                return;
            }

            // 第二道：乐观锁更新状态为 Processing
            boolean updated = excelExportTaskRepository.updateStatusToProcessing(taskId, task.getVersion());
            if (!updated) {
                log.info("乐观锁更新失败，任务已被处理, taskId={}", taskId);
                return;
            }

            // 执行导出
            excelFeignHandler.execute(task);

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
