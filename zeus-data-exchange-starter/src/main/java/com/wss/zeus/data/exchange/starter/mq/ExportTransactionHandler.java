package com.wss.zeus.data.exchange.starter.mq;

import com.wss.zeus.data.exchange.constant.ExportTaskConstant;
import com.wss.zeus.data.exchange.dto.ExportTaskSubmitReq;
import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;
import com.wss.zeus.data.exchange.enums.ExportTaskStatusEnum;
import com.wss.zeus.data.exchange.mq.ExportMqConstants;
import com.wss.zeus.data.exchange.repository.ExcelExportTaskRepository;
import com.wss.zeus.mq.annotation.TransactionTopic;
import com.wss.zeus.mq.handler.TransactionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 导出任务事务处理器
 *
 * @author wangshusheng
 */
@Slf4j
@Component
@RequiredArgsConstructor
@TransactionTopic(topic = ExportMqConstants.TOPIC, tag = ExportMqConstants.TAG_EXPORT_TASK)
public class ExportTransactionHandler implements TransactionHandler {

    private final ExcelExportTaskRepository excelExportTaskRepository;

    @Override
    public RocketMQLocalTransactionState execute(Message message, Object arg) {
        String taskId = doResolveTaskId(message);
        if (!(arg instanceof ExportTaskSubmitReq req) || StringUtils.isBlank(taskId)) {
            log.error("本地事务参数无效, taskId={}", taskId);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        ExcelExportTaskEntity task = new ExcelExportTaskEntity();
        task.setTaskId(taskId);
        task.setTemplateCode(req.getTemplateCode());
        task.setTaskParam(req.getTaskParam());
        task.setOperatorUserId(req.getOperatorUserId());
        task.setOperatorUserName(req.getOperatorUserName());
        task.setStatus(ExportTaskStatusEnum.PENDING.getValue());
        task.setFileType(ExportTaskConstant.FILE_TYPE_XLSX);
        excelExportTaskRepository.savePendingTask(task);
        log.info("本地事务执行成功, taskId={}", taskId);
        return RocketMQLocalTransactionState.COMMIT;
    }

    @Override
    public RocketMQLocalTransactionState check(Message message) {
        String taskId = doResolveTaskId(message);
        log.info("事务回查, taskId={}", taskId);
        if (StringUtils.isBlank(taskId)) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        ExcelExportTaskEntity task = excelExportTaskRepository.getByTaskId(taskId);
        if (Objects.nonNull(task)) {
            log.info("事务回查: 任务存在, taskId={}, status={}", taskId, task.getStatus());
            return RocketMQLocalTransactionState.COMMIT;
        }
        log.info("事务回查: 任务不存在, taskId={}", taskId);
        return RocketMQLocalTransactionState.ROLLBACK;
    }

    /**
     * 优先读消息体里的 taskId - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    private String doResolveTaskId(Message<?> message) {
        if (Objects.isNull(message)) {
            return null;
        }
        Object payload = message.getPayload();
        if (payload instanceof String text && StringUtils.isNotBlank(text)) {
            return text;
        }
        if (payload instanceof byte[] body && body.length > 0) {
            String text = new String(body, StandardCharsets.UTF_8);
            if (StringUtils.isNotBlank(text)) {
                return text;
            }
        }
        Object keys = message.getHeaders().get(RocketMQHeaders.KEYS);
        if (keys instanceof String text && StringUtils.isNotBlank(text)) {
            return text;
        }
        return null;
    }
}
