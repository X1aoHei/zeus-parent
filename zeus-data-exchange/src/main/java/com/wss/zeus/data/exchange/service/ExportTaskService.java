package com.wss.zeus.data.exchange.service;

import com.wss.zeus.core.exception.BizException;
import com.wss.zeus.core.exception.SystemException;
import com.wss.zeus.data.exchange.constant.RedisConstant;
import com.wss.zeus.data.exchange.dto.ExportTaskResp;
import com.wss.zeus.data.exchange.dto.ExportTaskSubmitReq;
import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;
import com.wss.zeus.data.exchange.mq.ExportMqConstants;
import com.wss.zeus.data.exchange.repository.ExcelExportTaskRepository;
import com.wss.zeus.data.exchange.support.ExportIdempotentKeyBuilder;
import com.wss.zeus.redis.lock.DistributedLockExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 导出任务Service
 *
 * @author wangshusheng
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExportTaskService {

    private final ExcelExportTaskRepository excelExportTaskRepository;
    private final RocketMQTemplate rocketMQTemplate;
    private final DistributedLockExecutor distributedLockExecutor;
    private final ExportSubmitIdempotentStore exportSubmitIdempotentStore;

    /**
     * 根据操作人ID查询导出任务列表
     *
     * @param operatorUserId 操作人ID
     * @return 导出任务列表
     */
    public List<ExportTaskResp> listByOperatorUserId(Long operatorUserId) {
        List<ExcelExportTaskEntity> tasks = excelExportTaskRepository.listByOperatorUserId(operatorUserId);
        if (CollectionUtils.isEmpty(tasks)) {
            return Collections.emptyList();
        }
        return tasks.stream().map(this::doToResp).toList();
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

    /**
     * 提交导出任务（事务消息 + 分布式锁 + 幂等）
     *
     * @param req 提交请求
     * @return 任务ID
     */
    public String submit(ExportTaskSubmitReq req) {
        doValidate(req);
        log.info("提交导出任务, templateCode={}, operatorUserId={}", req.getTemplateCode(), req.getOperatorUserId());
        String lockKey = String.format(RedisConstant.EXPORT_SUBMIT_LOCK_KEY, req.getTemplateCode(), req.getOperatorUserId());
        return distributedLockExecutor.executeWithLock(lockKey, ExportMqConstants.LOCK_WAIT_TIME, () -> doSubmit(req));
    }

    /**
     * 实体转为查询结果，不带出主键、任务参数和乐观锁字段 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    private ExportTaskResp doToResp(ExcelExportTaskEntity task) {
        ExportTaskResp resp = new ExportTaskResp();
        resp.setTaskId(task.getTaskId());
        resp.setTemplateCode(task.getTemplateCode());
        resp.setFileId(task.getFileId());
        resp.setFileName(task.getFileName());
        resp.setFileType(task.getFileType());
        resp.setStatus(task.getStatus());
        resp.setFinishTime(task.getFinishTime());
        resp.setOperatorUserId(task.getOperatorUserId());
        resp.setOperatorUserName(task.getOperatorUserName());
        resp.setErrorReason(task.getErrorReason());
        resp.setFailCount(task.getFailCount());
        resp.setCreateTime(task.getCreateTime());
        return resp;
    }

    /**
     * 校验提交参数 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    private void doValidate(ExportTaskSubmitReq req) {
        if (Objects.isNull(req) || StringUtils.isBlank(req.getTemplateCode()) || Objects.isNull(req.getOperatorUserId())) {
            log.warn("导出任务参数不完整, templateCode={}", Objects.isNull(req) ? null : req.getTemplateCode());
            throw new BizException(SystemException.PARAM_ERROR);
        }
    }

    /**
     * 幂等检查后发送事务消息 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    private String doSubmit(ExportTaskSubmitReq req) {
        String idempotentKey = ExportIdempotentKeyBuilder.build(req.getTemplateCode(), req.getOperatorUserId(), req.getTaskParam());
        String existedTaskId = doFindExistedTaskId(req, idempotentKey);
        if (StringUtils.isNotBlank(existedTaskId)) {
            log.info("导出任务命中幂等, taskId={}", existedTaskId);
            return existedTaskId;
        }
        String taskId = UUID.randomUUID().toString().replace("-", "");
        if (!exportSubmitIdempotentStore.tryAcquire(idempotentKey, taskId)) {
            return doReadLockedTaskId(idempotentKey);
        }
        return doSendTransactionMessage(req, taskId, idempotentKey);
    }

    /**
     * Redis SETNX 未命中时再查库 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    private String doFindExistedTaskId(ExportTaskSubmitReq req, String idempotentKey) {
        String cachedTaskId = exportSubmitIdempotentStore.getTaskId(idempotentKey);
        if (StringUtils.isNotBlank(cachedTaskId)) {
            return cachedTaskId;
        }
        ExcelExportTaskEntity existed = excelExportTaskRepository.getByTemplateAndParam(
                req.getTemplateCode(), req.getTaskParam(), req.getOperatorUserId());
        if (Objects.isNull(existed) || StringUtils.isBlank(existed.getTaskId())) {
            return null;
        }
        exportSubmitIdempotentStore.bind(idempotentKey, existed.getTaskId());
        return existed.getTaskId();
    }

    /**
     * 读取并发提交已经占用的任务 ID - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    private String doReadLockedTaskId(String idempotentKey) {
        String existedTaskId = exportSubmitIdempotentStore.getTaskId(idempotentKey);
        if (StringUtils.isBlank(existedTaskId)) {
            throw new BizException(SystemException.REPEAT);
        }
        return existedTaskId;
    }

    /**
     * 发送半消息，本地事务未提交则释放幂等 key - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    private String doSendTransactionMessage(ExportTaskSubmitReq req, String taskId, String idempotentKey) {
        String destination = ExportMqConstants.TOPIC + ":" + ExportMqConstants.TAG_EXPORT_TASK;
        Message<String> message = MessageBuilder.withPayload(taskId)
                .setHeader(RocketMQHeaders.KEYS, taskId)
                .build();
        boolean committed = false;
        try {
            TransactionSendResult sendResult = rocketMQTemplate.sendMessageInTransaction(destination, message, req);
            committed = Objects.nonNull(sendResult)
                    && Objects.equals(sendResult.getLocalTransactionState(), LocalTransactionState.COMMIT_MESSAGE);
        } finally {
            if (!committed) {
                exportSubmitIdempotentStore.release(idempotentKey);
            }
        }
        if (!committed) {
            throw new BizException(SystemException.SUBMIT_FAILED);
        }
        log.info("导出任务提交成功, taskId={}", taskId);
        return taskId;
    }
}
