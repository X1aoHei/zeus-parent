package com.wss.zeus.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: wangshusheng
 * @Date: 2026-08-17 15:17
 */
@Getter
@AllArgsConstructor
public enum SystemException implements BizError {

    PARAM_ERROR(100400L, "请求参数不完整"),

    REPEAT(100501L, "请勿重复提交"),

    SUBMIT_FAILED(100502L, "导出任务提交失败"),

    SYSTEM_ERROR(500L, "系统异常"),
    ;

    private Long code;

    private String errorMsg;

}
