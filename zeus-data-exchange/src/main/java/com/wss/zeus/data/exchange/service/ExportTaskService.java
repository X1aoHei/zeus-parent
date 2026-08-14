package com.wss.zeus.data.exchange.service;

import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;
import com.wss.zeus.data.exchange.repository.ExcelExportTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 导出任务Service
 *
 * @author wangshusheng
 */
@Service
@RequiredArgsConstructor
public class ExportTaskService {

    private final ExcelExportTaskRepository excelExportTaskRepository;

    /**
     * 根据操作人ID查询导出任务列表
     *
     * @param operatorUserId 操作人ID
     * @return 导出任务列表
     */
    public List<ExcelExportTaskEntity> listByOperatorUserId(Long operatorUserId) {
        return excelExportTaskRepository.listByOperatorUserId(operatorUserId);
    }

    /**
     * 查询待处理的导出任务
     *
     * @param limit 查询数量
     * @return 待处理任务列表
     */
    public List<ExcelExportTaskEntity> listPendingTasks(int limit) {
        return excelExportTaskRepository.listPendingTasks(limit);
    }
}
