package com.wss.zeus.data.exchange.starter.mq;

import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;
import com.wss.zeus.data.exchange.handler.ExcelExportExecutor;
import com.wss.zeus.data.exchange.mq.ExportMqConstants;
import com.wss.zeus.data.exchange.repository.ExcelExportTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;

import java.util.Objects;

/**
 * 导出任务 MQ 消费者
 *
 * @author wangshusheng
 */
@Slf4j
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = ExportMqConstants.TOPIC,
        selectorExpression = ExportMqConstants.TAG_EXPORT_TASK,
        consumerGroup = ExportMqConstants.EXPORT_TASK_CONSUMER_GROUP
)
public class ExportTaskMqConsumer implements RocketMQListener<String> {

    private final ExcelExportTaskRepository excelExportTaskRepository;
    private final ExcelExportExecutor excelExportExecutor;

    @Override
    public void onMessage(String taskId) {
        log.info("收到导出任务MQ消息, taskId={}", taskId);

        ExcelExportTaskEntity task = excelExportTaskRepository.getByTaskId(taskId);
        if (Objects.isNull(task)) {
            log.warn("导出任务不存在, taskId={}", taskId);
            return;
        }

        excelExportExecutor.execute(task);
    }
}
