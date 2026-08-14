package com.wss.zeus.data.exchange.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;
import com.wss.zeus.data.exchange.mapper.ExcelExportTaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 文件导出任务Repository
 *
 * @author wangshusheng
 */
@Slf4j
@Repository
public class ExcelExportTaskRepository extends ServiceImpl<ExcelExportTaskMapper, ExcelExportTaskEntity> {

    /**
     * 根据操作人ID查询导出任务列表
     *
     * @param operatorUserId 操作人ID
     * @return 导出任务列表
     */
    public List<ExcelExportTaskEntity> listByOperatorUserId(Long operatorUserId) {
        return list(new LambdaQueryWrapper<ExcelExportTaskEntity>()
                .eq(ExcelExportTaskEntity::getOperatorUserId, operatorUserId)
                .orderByDesc(ExcelExportTaskEntity::getCreateTime));
    }

    /**
     * 查询待处理的导出任务
     *
     * @param limit 查询数量
     * @return 待处理任务列表
     */
    public List<ExcelExportTaskEntity> listPendingTasks(int limit) {
        return list(new LambdaQueryWrapper<ExcelExportTaskEntity>()
                .eq(ExcelExportTaskEntity::getStatus, "Pending")
                .orderByAsc(ExcelExportTaskEntity::getCreateTime)
                .last("LIMIT " + limit));
    }
}
