package com.wss.zeus.data.exchange.starter.mq;

import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;
import com.wss.zeus.data.exchange.handler.ExcelExportExecutor;
import com.wss.zeus.data.exchange.mq.ExportMqConstants;
import com.wss.zeus.data.exchange.repository.ExcelExportTaskRepository;
import com.wss.zeus.mq.annotation.ZeusMessageListener;
import com.wss.zeus.mq.handler.MessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 导出任务 MQ 消费者
 *
 * @author wangshusheng
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ZeusMessageListener(
        topic = ExportMqConstants.TOPIC,
        tag = ExportMqConstants.TAG_EXPORT_TASK,
        consumerGroup = "zeus-data-exchange-export-task-consumer"
)
public class ExportTaskMqConsumer implements MessageHandler<String> {

    private final ExcelExportTaskRepository excelExportTaskRepository;
    private final ExcelExportExecutor excelExportExecutor;

    @Override
    public void onMessage(String taskId) {
        log.info("收到导出任务MQ消息, taskId={}", taskId);

        // 1. 查询任务
        ExcelExportTaskEntity task = excelExportTaskRepository.getByTaskId(taskId);
        if (Objects.isNull(task)) {
            log.warn("导出任务不存在, taskId={}", taskId);
            return;
        }

        // 2. 执行任务（内部包含互斥逻辑）
        excelExportExecutor.execute(task);
    }
}
