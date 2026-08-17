package com.wss.zeus.data.exchange.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;
import com.wss.zeus.data.exchange.enums.ExportTaskStatusEnum;
import com.wss.zeus.data.exchange.mapper.ExcelExportTaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
                .eq(ExcelExportTaskEntity::getStatus, ExportTaskStatusEnum.PENDING.getValue())
                .orderByAsc(ExcelExportTaskEntity::getCreateTime)
                .last("LIMIT " + limit));
    }

    /**
     * 根据模版参数和操作人查询已有任务（幂等查询）
     *
     * @param templateCode    模版CODE
     * @param taskParam       任务参数
     * @param operatorUserId  操作人ID
     * @return 已有任务
     */
    public ExcelExportTaskEntity getByTemplateAndParam(String templateCode, String taskParam, Long operatorUserId) {
        return getOne(new LambdaQueryWrapper<ExcelExportTaskEntity>()
                .eq(ExcelExportTaskEntity::getTemplateCode, templateCode)
                .eq(ExcelExportTaskEntity::getTaskParam, taskParam)
                .eq(ExcelExportTaskEntity::getOperatorUserId, operatorUserId)
                .ne(ExcelExportTaskEntity::getStatus, ExportTaskStatusEnum.FAIL.getValue())
                .orderByDesc(ExcelExportTaskEntity::getCreateTime)
                .last("LIMIT 1"));
    }

    /**
     * 更新任务状态为处理中
     * <p>
     * 调用前需确保已获取分布式锁，并已确认状态为 Pending
     * </p>
     *
     * @param taskId 任务ID
     */
    public void updateStatusToProcessing(String taskId) {
        update(new LambdaUpdateWrapper<ExcelExportTaskEntity>()
                .eq(ExcelExportTaskEntity::getTaskId, taskId)
                .set(ExcelExportTaskEntity::getStatus, ExportTaskStatusEnum.PROCESSING.getValue()));
    }

    /**
     * 更新任务状态为成功
     *
     * @param taskId   任务ID
     * @param fileId   文件ID
     * @param fileName 文件名
     */
    public void updateStatusToSuccess(String taskId, String fileId, String fileName) {
        update(new LambdaUpdateWrapper<ExcelExportTaskEntity>()
                .eq(ExcelExportTaskEntity::getTaskId, taskId)
                .set(ExcelExportTaskEntity::getStatus, ExportTaskStatusEnum.SUCCESS.getValue())
                .set(ExcelExportTaskEntity::getFileId, fileId)
                .set(ExcelExportTaskEntity::getFileName, fileName)
                .set(ExcelExportTaskEntity::getFinishTime, LocalDateTime.now()));
    }

    /**
     * 更新任务状态为失败
     *
     * @param taskId      任务ID
     * @param errorReason 错误原因
     */
    public void updateStatusToFail(String taskId, String errorReason) {
        update(new LambdaUpdateWrapper<ExcelExportTaskEntity>()
                .eq(ExcelExportTaskEntity::getTaskId, taskId)
                .set(ExcelExportTaskEntity::getStatus, ExportTaskStatusEnum.FAIL.getValue())
                .set(ExcelExportTaskEntity::getErrorReason, errorReason)
                .set(ExcelExportTaskEntity::getFinishTime, LocalDateTime.now()));
    }

    /**
     * 根据任务ID查询任务
     *
     * @param taskId 任务ID
     * @return 任务实体
     */
    public ExcelExportTaskEntity getByTaskId(String taskId) {
        return getOne(new LambdaQueryWrapper<ExcelExportTaskEntity>()
                .eq(ExcelExportTaskEntity::getTaskId, taskId));
    }
}
