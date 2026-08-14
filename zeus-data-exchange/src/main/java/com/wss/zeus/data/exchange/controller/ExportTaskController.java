package com.wss.zeus.data.exchange.controller;

import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;
import com.wss.zeus.data.exchange.service.ExportTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: wangshusheng
 * @Date: 2026-08-14 15:56
 */
@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
public class ExportTaskController {

    private final ExportTaskService exportTaskService;

    /**
     * 根据操作人ID查询导出任务列表
     *
     * @param operatorUserId 操作人ID
     * @return 导出任务列表
     */
    @GetMapping("/list")
    public List<ExcelExportTaskEntity> listByOperatorUserId(@RequestParam Long operatorUserId) {
        return exportTaskService.listByOperatorUserId(operatorUserId);
    }
}
