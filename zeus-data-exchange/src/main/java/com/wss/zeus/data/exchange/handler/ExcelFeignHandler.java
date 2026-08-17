package com.wss.zeus.data.exchange.handler;

import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;

/**
 * Excel Feign 执行器接口
 *
 * @author wangshusheng
 */
public interface ExcelFeignHandler {

    /**
     * 执行导出任务
     *
     * @param task 导出任务实体
     */
    void execute(ExcelExportTaskEntity task);
}
