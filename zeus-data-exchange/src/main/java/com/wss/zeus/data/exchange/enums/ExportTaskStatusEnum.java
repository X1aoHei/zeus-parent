package com.wss.zeus.data.exchange.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 导出任务状态枚举
 *
 * @author wangshusheng
 */
@Getter
@AllArgsConstructor
public enum ExportTaskStatusEnum {

    /**
     * 待处理
     */
    PENDING("Pending", "待处理"),

    /**
     * 处理中
     */
    PROCESSING("Processing", "处理中"),

    /**
     * 成功
     */
    SUCCESS("Success", "成功"),

    /**
     * 失败
     */
    FAIL("Fail", "失败");

    /**
     * 状态值（存库）
     */
    private final String value;

    /**
     * 状态描述
     */
    private final String desc;

    /**
     * 根据值查找枚举
     *
     * @param value 状态值
     * @return 枚举实例
     */
    public static ExportTaskStatusEnum of(String value) {
        for (ExportTaskStatusEnum status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("非法的导出任务状态: " + value);
    }
}
