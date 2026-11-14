package com.wss.zeus.data.exchange.service;

import com.wss.zeus.core.exception.BizException;
import com.wss.zeus.core.exception.SystemException;
import com.wss.zeus.data.exchange.config.ExportProperties;
import com.wss.zeus.data.exchange.constant.RedisConstant;
import com.wss.zeus.data.exchange.dto.ExportTaskResp;
import com.wss.zeus.data.exchange.dto.ExportTaskSubmitReq;
import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;
import com.wss.zeus.data.exchange.mq.ExportMqConstants;
import com.wss.zeus.data.exchange.repository.ExcelExportTaskRepository;
import com.wss.zeus.data.exchange.support.ExportPendingTaskFactory;
import com.wss.zeus.redis.lock.DistributedLockExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.ObjectProvider;
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
public class ExportTaskService {

    private final ExcelExportTaskRepository excelExportTaskRepository;
    private final ObjectProvider<RocketMQTemplate> rocketMQTemplateProvider;
    private final DistributedLockExecutor distributedLockExecutor;
    private final ExportProperties exportProperties;

    /**
     * 事务消息模板允许缺失，关闭事务消息时不要求容器里有 RocketMQTemplate - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-24
     */
    public ExportTaskService(ExcelExportTaskRepository excelExportTaskRepository,
                             ObjectProvider<RocketMQTemplate> rocketMQTemplateProvider,
                             DistributedLockExecutor distributedLockExecutor,
                             ExportProperties exportProperties) {
        this.excelExportTaskRepository = excelExportTaskRepository;
        this.rocketMQTemplateProvider = rocketMQTemplateProvider;
        this.distributedLockExecutor = distributedLockExecutor;
        this.exportProperties = exportProperties;
    }

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
     * 提交导出任务（分布式锁 + 幂等，按配置走事务消息或直接落库）
     *
     * @param req 提交请求
     * @return 任务ID
     */
    public String submit(ExportTaskSubmitReq req) {
        doValidate(req);
        log.info("提交导出任务, requestId={}, templateCode={}, operatorUserId={}",
                req.getRequestId(), req.getTemplateCode(), req.getOperatorUserId());
        String lockKey = String.format(RedisConstant.EXPORT_SUBMIT_LOCK_KEY, req.getRequestId());
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
        resp.setRequestId(task.getRequestId());
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
        if (Objects.isNull(req) || StringUtils.isBlank(req.getRequestId())
                || StringUtils.isBlank(req.getTemplateCode()) || Objects.isNull(req.getOperatorUserId())) {
            log.warn("导出任务参数不完整, requestId={}, templateCode={}",
                    Objects.isNull(req) ? null : req.getRequestId(),
                    Objects.isNull(req) ? null : req.getTemplateCode());
            throw new BizException(SystemException.PARAM_ERROR);
        }
    }

    /**
     * 幂等检查后按配置提交任务 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    private String doSubmit(ExportTaskSubmitReq req) {
        String existedTaskId = doFindExistedTaskId(req);
        if (StringUtils.isNotBlank(existedTaskId)) {
            log.info("导出任务命中幂等, taskId={}", existedTaskId);
            return existedTaskId;
        }
        String taskId = UUID.randomUUID().toString().replace("-", "");
        return doDispatchSubmit(req, taskId);
    }

    /**
     * 按配置选择事务消息或直接落库 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-24
     */
    private String doDispatchSubmit(ExportTaskSubmitReq req, String taskId) {
        if (exportProperties.isMqTransactionEnabled()) {
            return doSendTransactionMessage(req, taskId);
        }
        return doSavePendingTask(req, taskId);
    }

    /**
     * 不发事务消息，直接写入待处理任务 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-24
     */
    private String doSavePendingTask(ExportTaskSubmitReq req, String taskId) {
        excelExportTaskRepository.savePendingTask(ExportPendingTaskFactory.build(req, taskId));
        log.info("导出任务直接落库成功, taskId={}", taskId);
        return taskId;
    }

    /**
     * 按请求号查已有任务，请求号即幂等键 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-28
     */
    private String doFindExistedTaskId(ExportTaskSubmitReq req) {
        ExcelExportTaskEntity existed = excelExportTaskRepository.getByRequestId(req.getRequestId());
        if (Objects.isNull(existed) || StringUtils.isBlank(existed.getTaskId())) {
            return null;
        }
        return existed.getTaskId();
    }

    /**
     * 发送半消息，本地事务直接插入任务行 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-28
     */
    private String doSendTransactionMessage(ExportTaskSubmitReq req, String taskId) {
        RocketMQTemplate rocketMQTemplate = rocketMQTemplateProvider.getIfAvailable();
        if (Objects.isNull(rocketMQTemplate)) {
            log.error("已开启事务消息但未找到 RocketMQTemplate, taskId={}", taskId);
            throw new BizException(SystemException.SUBMIT_FAILED);
        }
        String destination = ExportMqConstants.TOPIC + ":" + ExportMqConstants.TAG_EXPORT_TASK;
        Message<String> message = MessageBuilder.withPayload(taskId)
                .setHeader(RocketMQHeaders.KEYS, taskId)
                .build();
        TransactionSendResult sendResult = rocketMQTemplate.sendMessageInTransaction(destination, message, req);
        if (Objects.isNull(sendResult)
                || !Objects.equals(sendResult.getLocalTransactionState(), LocalTransactionState.COMMIT_MESSAGE)) {
            throw new BizException(SystemException.SUBMIT_FAILED);
        }
        log.info("导出任务提交成功, taskId={}", taskId);
        return taskId;
    }
}
