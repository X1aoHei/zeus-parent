package com.wss.zeus.data.exchange.service;

import com.wss.zeus.data.exchange.dto.ExportTaskSubmitReq;
import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;
import com.wss.zeus.data.exchange.mq.ExportMqConstants;
import com.wss.zeus.data.exchange.repository.ExcelExportTaskRepository;
import com.wss.zeus.redis.lock.DistributedLockExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
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

    /**
     * 提交导出任务（事务消息 + 分布式锁）
     *
     * @param req 提交请求
     * @return 任务ID
     */
    public String submit(ExportTaskSubmitReq req) {
        String lockKey = ExportMqConstants.SUBMIT_LOCK_KEY_PREFIX
                + req.getTemplateCode() + ":" + req.getOperatorUserId();

        return distributedLockExecutor.executeWithLock(lockKey, ExportMqConstants.LOCK_WAIT_TIME, () -> doSubmit(req));
    }

    /**
     * 执行导出任务提交（业务逻辑）
     *
     * @param req 提交请求
     * @return 任务ID
     */
    private String doSubmit(ExportTaskSubmitReq req) {
        String taskId = UUID.randomUUID().toString().replace("-", "");
        String destination = ExportMqConstants.TOPIC + ":" + ExportMqConstants.TAG_EXPORT_TASK;
        Message<String> msg = MessageBuilder.withPayload(taskId)
                .setHeader("KEYS", taskId)
                .build();

        // 发送事务消息，arg 传入任务参数
        rocketMQTemplate.sendMessageInTransaction(destination, msg, req);

        log.info("导出任务提交成功, taskId={}", taskId);
        return taskId;
    }
}
