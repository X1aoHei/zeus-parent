package com.wss.zeus.data.exchange.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wss.zeus.data.exchange.constant.ExportTaskConstant;
import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;
import com.wss.zeus.data.exchange.enums.ExportTaskStatusEnum;
import com.wss.zeus.data.exchange.mapper.ExcelExportTaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
     * 查询可执行任务：Pending、未超限的 Fail，以及超时仍停在 Processing 的任务
     *
     * @param limit 查询数量
     * @return 待处理任务列表
     */
    public List<ExcelExportTaskEntity> listPendingTasks(int limit) {
        int size = limit <= 0 ? ExportTaskConstant.RECOVER_BATCH_SIZE : limit;
        LocalDateTime staleBefore = LocalDateTime.now().minusMinutes(ExportTaskConstant.STALE_PROCESSING_MINUTES);
        List<ExcelExportTaskEntity> pendingOrFail = list(new LambdaQueryWrapper<ExcelExportTaskEntity>()
                .in(ExcelExportTaskEntity::getStatus,
                        ExportTaskStatusEnum.PENDING.getValue(),
                        ExportTaskStatusEnum.FAIL.getValue())
                .and(wrapper -> wrapper
                        .le(ExcelExportTaskEntity::getFailCount, ExportTaskConstant.MAX_FAIL_COUNT)
                        .or()
                        .isNull(ExcelExportTaskEntity::getFailCount))
                .orderByAsc(ExcelExportTaskEntity::getCreateTime)
                .last("LIMIT " + size));
        List<ExcelExportTaskEntity> staleProcessing = list(new LambdaQueryWrapper<ExcelExportTaskEntity>()
                .eq(ExcelExportTaskEntity::getStatus, ExportTaskStatusEnum.PROCESSING.getValue())
                .le(ExcelExportTaskEntity::getUpdateTime, staleBefore)
                .orderByAsc(ExcelExportTaskEntity::getUpdateTime)
                .last("LIMIT " + size));
        List<ExcelExportTaskEntity> merged = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(pendingOrFail)) {
            merged.addAll(pendingOrFail);
        }
        if (CollectionUtils.isNotEmpty(staleProcessing)) {
            merged.addAll(staleProcessing);
        }
        if (merged.size() <= size) {
            return merged;
        }
        return merged.subList(0, size);
    }

    /**
     * 根据模版参数和操作人查询已有任务（幂等查询）
     *
     * @param templateCode   模版CODE
     * @param taskParam      任务参数
     * @param operatorUserId 操作人ID
     * @return 已有任务
     */
    public ExcelExportTaskEntity getByTemplateAndParam(String templateCode, String taskParam, Long operatorUserId) {
        LambdaQueryWrapper<ExcelExportTaskEntity> wrapper = new LambdaQueryWrapper<ExcelExportTaskEntity>()
                .eq(ExcelExportTaskEntity::getTemplateCode, templateCode)
                .eq(ExcelExportTaskEntity::getOperatorUserId, operatorUserId)
                .ne(ExcelExportTaskEntity::getStatus, ExportTaskStatusEnum.FAIL.getValue())
                .orderByDesc(ExcelExportTaskEntity::getCreateTime);
        if (StringUtils.isBlank(taskParam)) {
            wrapper.and(item -> item.isNull(ExcelExportTaskEntity::getTaskParam)
                    .or()
                    .eq(ExcelExportTaskEntity::getTaskParam, ""));
        } else {
            wrapper.eq(ExcelExportTaskEntity::getTaskParam, taskParam);
        }
        return doFirst(wrapper);
    }

    /**
     * 写入待处理任务 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    @Transactional(rollbackFor = Throwable.class)
    public void savePendingTask(ExcelExportTaskEntity task) {
        if (Objects.isNull(task.getVersion())) {
            task.setVersion(ExportTaskConstant.INIT_VERSION);
        }
        if (Objects.isNull(task.getFailCount())) {
            task.setFailCount(0);
        }
        save(task);
    }

    /**
     * 按 version 把状态切到处理中 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    @Transactional(rollbackFor = Throwable.class)
    public boolean compareAndSetProcessing(String taskId, Integer version, String expectStatus) {
        LambdaUpdateWrapper<ExcelExportTaskEntity> wrapper = new LambdaUpdateWrapper<ExcelExportTaskEntity>()
                .eq(ExcelExportTaskEntity::getTaskId, taskId)
                .eq(ExcelExportTaskEntity::getStatus, expectStatus)
                .set(ExcelExportTaskEntity::getStatus, ExportTaskStatusEnum.PROCESSING.getValue());
        doEqVersion(wrapper, version);
        return update(wrapper);
    }

    /**
     * 按 version 把状态切到成功 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    @Transactional(rollbackFor = Throwable.class)
    public boolean compareAndSetSuccess(String taskId, Integer version, String fileId, String fileName) {
        LambdaUpdateWrapper<ExcelExportTaskEntity> wrapper = new LambdaUpdateWrapper<ExcelExportTaskEntity>()
                .eq(ExcelExportTaskEntity::getTaskId, taskId)
                .eq(ExcelExportTaskEntity::getStatus, ExportTaskStatusEnum.PROCESSING.getValue())
                .set(ExcelExportTaskEntity::getStatus, ExportTaskStatusEnum.SUCCESS.getValue())
                .set(ExcelExportTaskEntity::getFileId, fileId)
                .set(ExcelExportTaskEntity::getFileName, fileName)
                .set(ExcelExportTaskEntity::getFinishTime, LocalDateTime.now());
        doEqVersion(wrapper, version);
        return update(wrapper);
    }

    /**
     * 按 version 把状态切到失败，并增加失败次数 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    @Transactional(rollbackFor = Throwable.class)
    public boolean compareAndSetFail(String taskId, Integer version, String errorReason) {
        LambdaUpdateWrapper<ExcelExportTaskEntity> wrapper = new LambdaUpdateWrapper<ExcelExportTaskEntity>()
                .eq(ExcelExportTaskEntity::getTaskId, taskId)
                .set(ExcelExportTaskEntity::getStatus, ExportTaskStatusEnum.FAIL.getValue())
                .set(ExcelExportTaskEntity::getErrorReason, StringUtils.left(errorReason, ExportTaskConstant.ERROR_REASON_MAX_LENGTH))
                .set(ExcelExportTaskEntity::getFinishTime, LocalDateTime.now())
                .setSql("fail_count = COALESCE(fail_count, 0) + 1");
        doEqVersion(wrapper, version);
        return update(wrapper);
    }

    /**
     * 根据任务ID查询任务
     *
     * @param taskId 任务ID
     * @return 任务实体
     */
    public ExcelExportTaskEntity getByTaskId(String taskId) {
        return doFirst(new LambdaQueryWrapper<ExcelExportTaskEntity>()
                .eq(ExcelExportTaskEntity::getTaskId, taskId));
    }

    /**
     * 取查询结果第一条 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    private ExcelExportTaskEntity doFirst(LambdaQueryWrapper<ExcelExportTaskEntity> wrapper) {
        List<ExcelExportTaskEntity> rows = list(wrapper.last("LIMIT 1"));
        if (CollectionUtils.isEmpty(rows)) {
            return null;
        }
        return rows.get(0);
    }

    /**
     * 乐观锁条件：WHERE version = ? 并写回 version + 1 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    private void doEqVersion(LambdaUpdateWrapper<ExcelExportTaskEntity> wrapper, Integer version) {
        int nextVersion = ExportTaskConstant.nextVersion(version);
        if (Objects.isNull(version)) {
            wrapper.isNull(ExcelExportTaskEntity::getVersion)
                    .set(ExcelExportTaskEntity::getVersion, nextVersion);
            return;
        }
        wrapper.eq(ExcelExportTaskEntity::getVersion, version)
                .set(ExcelExportTaskEntity::getVersion, nextVersion);
    }
}
