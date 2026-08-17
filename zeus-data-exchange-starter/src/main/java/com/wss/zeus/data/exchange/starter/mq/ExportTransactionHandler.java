package com.wss.zeus.data.exchange.starter.mq;

import com.wss.zeus.data.exchange.dto.ExportTaskSubmitReq;
import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;
import com.wss.zeus.data.exchange.enums.ExportTaskStatusEnum;
import com.wss.zeus.data.exchange.mq.ExportMqConstants;
import com.wss.zeus.data.exchange.repository.ExcelExportTaskRepository;
import com.wss.zeus.mq.annotation.TransactionTopic;
import com.wss.zeus.mq.handler.TransactionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

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
        ExportTaskSubmitReq req = (ExportTaskSubmitReq) arg;
        String taskId = (String) message.getHeaders().get("KEYS");

        try {
            ExcelExportTaskEntity task = new ExcelExportTaskEntity();
            task.setTaskId(taskId);
            task.setTemplateCode(req.getTemplateCode());
            task.setTaskParam(req.getTaskParam());
            task.setOperatorUserId(req.getOperatorUserId());
            task.setOperatorUserName(req.getOperatorUserName());
            task.setStatus(ExportTaskStatusEnum.PENDING.getValue());
            task.setFileType("XLSX");
            excelExportTaskRepository.save(task);

            log.info("本地事务执行成功, taskId={}", taskId);
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            log.error("本地事务执行失败, taskId={}", taskId, e);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState check(Message message) {
        String taskId = (String) message.getHeaders().get("KEYS");
        log.info("事务回查, taskId={}", taskId);

        if (Objects.isNull(taskId)) {
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
}
