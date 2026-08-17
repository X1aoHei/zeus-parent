package com.wss.zeus.data.exchange.handler;

import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;

/**
 * 导出任务执行器
 * <p>
 * 封装互斥执行逻辑（Redis 分布式锁 + 乐观锁）
 * </p>
 *
 * @author wangshusheng
 */
public interface ExcelExportExecutor {

    /**
     * 执行导出任务（带互斥保护）
     *
     * @param task 导出任务
     */
    void execute(ExcelExportTaskEntity task);
}
