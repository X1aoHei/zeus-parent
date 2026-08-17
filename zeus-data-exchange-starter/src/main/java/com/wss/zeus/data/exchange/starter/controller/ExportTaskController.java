package com.wss.zeus.data.exchange.starter.controller;

import com.wss.zeus.core.common.Result;
import com.wss.zeus.data.exchange.dto.ExportTaskSubmitReq;
import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;
import com.wss.zeus.data.exchange.service.ExportTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 导出任务 Controller
 * <p>
 * 放在 starter 包下，只有引入 starter 才会被组件扫描注册
 * </p>
 *
 * @author wangshusheng
 */
@RestController
@RequiredArgsConstructor
public class ExportTaskController {

    private final ExportTaskService exportTaskService;

    /**
     * 提交导出任务
     *
     * @param req 提交请求
     * @return 任务ID
     */
    @PostMapping("/export/submit")
    public Result<String> submit(@RequestBody ExportTaskSubmitReq req) {
        return Result.success(exportTaskService.submit(req));
    }

    /**
     * 根据操作人ID查询导出任务列表
     *
     * @param operatorUserId 操作人ID
     * @return 导出任务列表
     */
    @GetMapping("/export/list")
    public Result<List<ExcelExportTaskEntity>> listByOperatorUserId(@RequestParam Long operatorUserId) {
        return Result.success(exportTaskService.listByOperatorUserId(operatorUserId));
    }
}
