package com.wss.zeus.data.exchange.controller;

import com.wss.zeus.data.exchange.dto.ExportTaskSubmitReq;
import com.wss.zeus.data.exchange.dto.ExportTaskSubmitRes;
import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;
import com.wss.zeus.data.exchange.service.ExportTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 导出任务 Controller
 *
 * @author wangshusheng
 */
@RestController
@RequestMapping("/export")
@RequiredArgsConstructor
public class ExportTaskController {

    private final ExportTaskService exportTaskService;

    /**
     * 提交导出任务
     *
     * @param req 提交请求
     * @return 提交响应
     */
    @PostMapping("/submit")
    public ExportTaskSubmitRes submit(@RequestBody ExportTaskSubmitReq req) {
        return exportTaskService.submit(req);
    }

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
