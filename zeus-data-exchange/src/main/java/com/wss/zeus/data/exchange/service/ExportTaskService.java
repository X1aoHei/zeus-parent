package com.wss.zeus.data.exchange.service;

import com.wss.zeus.data.exchange.dto.ExportTaskSubmitReq;
import com.wss.zeus.data.exchange.dto.ExportTaskSubmitRes;
import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;
import com.wss.zeus.data.exchange.enums.ExportTaskStatusEnum;
import com.wss.zeus.data.exchange.mq.ExportMqConstants;
import com.wss.zeus.data.exchange.repository.ExcelExportTaskRepository;
import com.wss.zeus.mq.model.MqMessage;
import com.wss.zeus.mq.producer.ZeusMqProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
    private final ZeusMqProducer zeusMqProducer;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedissonClient redissonClient;

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
     * 提交导出任务
     * <p>
     * 1. Redis SETNX 拦截重复请求
     * 2. 创建任务记录（数据库唯一索引兜底）
     * 3. 发送 MQ 消息
     * </p>
     *
     * @param req 提交请求
     * @return 提交响应
     */
    @Transactional(rollbackFor = Exception.class)
    public ExportTaskSubmitRes submit(ExportTaskSubmitReq req) {
        // 1. 生成幂等 Key
        String idempotentKey = buildIdempotentKey(req);

        // 2. Redis SETNX 快速拦截
        Boolean absent = stringRedisTemplate.opsForValue()
                .setIfAbsent(idempotentKey, "1", ExportMqConstants.IDEMPOTENT_EXPIRE_SECONDS, TimeUnit.SECONDS);
        if (Objects.equals(absent, Boolean.FALSE)) {
            // 幂等命中，查询已有任务返回
            ExcelExportTaskEntity existTask = excelExportTaskRepository.getByTemplateAndParam(
                    req.getTemplateCode(), req.getTaskParam(), req.getOperatorUserId());
            if (Objects.nonNull(existTask)) {
                ExportTaskSubmitRes res = new ExportTaskSubmitRes();
                res.setTaskId(existTask.getTaskId());
                res.setStatus(existTask.getStatus());
                res.setDuplicate(true);
                return res;
            }
        }

        // 3. 检查数据库是否存在（唯一索引兜底）
        ExcelExportTaskEntity existTask = excelExportTaskRepository.getByTemplateAndParam(
                req.getTemplateCode(), req.getTaskParam(), req.getOperatorUserId());
        if (Objects.nonNull(existTask)) {
            ExportTaskSubmitRes res = new ExportTaskSubmitRes();
            res.setTaskId(existTask.getTaskId());
            res.setStatus(existTask.getStatus());
            res.setDuplicate(true);
            return res;
        }

        // 4. 创建任务记录
        String taskId = UUID.randomUUID().toString().replace("-", "");
        ExcelExportTaskEntity task = new ExcelExportTaskEntity();
        task.setTaskId(taskId);
        task.setTemplateCode(req.getTemplateCode());
        task.setTaskParam(req.getTaskParam());
        task.setOperatorUserId(req.getOperatorUserId());
        task.setOperatorUserName(req.getOperatorUserName());
        task.setStatus(ExportTaskStatusEnum.PENDING.getValue());
        task.setFileType("XLSX");
        task.setVersion(0);
        excelExportTaskRepository.save(task);

        // 5. 发送 MQ 消息
        MqMessage<String> message = MqMessage.of(taskId, taskId);
        zeusMqProducer.syncSend(ExportMqConstants.TOPIC, ExportMqConstants.TAG_EXPORT_TASK, message);

        log.info("导出任务提交成功, taskId={}", taskId);

        ExportTaskSubmitRes res = new ExportTaskSubmitRes();
        res.setTaskId(taskId);
        res.setStatus(ExportTaskStatusEnum.PENDING.getValue());
        res.setDuplicate(false);
        return res;
    }

    /**
     * 构建幂等 Key
     */
    private String buildIdempotentKey(ExportTaskSubmitReq req) {
        return ExportMqConstants.IDEMPOTENT_KEY_PREFIX
                + req.getTemplateCode() + ":"
                + req.getTaskParam().hashCode() + ":"
                + req.getOperatorUserId();
    }
}
