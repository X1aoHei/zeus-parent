package com.wss.zeus.data.exchange.handler;

import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;

/**
 * Excel Feign 执行器接口
 * <p>
 * 只负责导出逻辑（调用 Feign + 写 Excel + 上传文件），不处理状态更新
 * </p>
 *
 * @author wangshusheng
 */
public interface ExcelFeignHandler {

    /**
     * 执行导出任务
     *
     * @param task 导出任务实体
     * @return 导出结果（fileId、fileName）
     */
    ExcelExportResult execute(ExcelExportTaskEntity task);
}
