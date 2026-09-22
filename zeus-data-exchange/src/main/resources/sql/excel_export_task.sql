CREATE TABLE `excel_export_task` (
    `id`                  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `task_id`             VARCHAR(32)  NOT NULL COMMENT '任务ID',
    `template_code`       VARCHAR(64)  NOT NULL COMMENT '模版CODE',
    `file_id`             VARCHAR(128)          DEFAULT NULL COMMENT '文件ID',
    `file_name`           VARCHAR(255)          DEFAULT NULL COMMENT '文件名称',
    `file_type`           VARCHAR(16)           DEFAULT NULL COMMENT '文件类型，如 XLSX',
    `task_param`          TEXT                  DEFAULT NULL COMMENT '任务参数（JSON）',
    `status`              VARCHAR(16)  NOT NULL COMMENT '状态：Pending、Processing、Success、Fail',
    `finish_time`         DATETIME              DEFAULT NULL COMMENT '完成时间',
    `operator_user_id`    BIGINT       NOT NULL COMMENT '操作人ID',
    `operator_user_name`  VARCHAR(64)           DEFAULT NULL COMMENT '操作人昵称',
    `error_reason`        VARCHAR(500)          DEFAULT NULL COMMENT '错误原因',
    `fail_count`          INT          NOT NULL DEFAULT 0 COMMENT '失败次数',
    `create_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `version`             INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
    `is_delete`           TINYINT      NOT NULL DEFAULT 0 COMMENT '删除标识：0未删除，1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_task_id` (`task_id`),
    KEY `idx_operator_create` (`operator_user_id`, `create_time`),
    KEY `idx_template_operator` (`template_code`, `operator_user_id`, `create_time`),
    KEY `idx_status_create` (`status`, `fail_count`, `create_time`),
    KEY `idx_status_update` (`status`, `update_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = 'Excel导出任务';
