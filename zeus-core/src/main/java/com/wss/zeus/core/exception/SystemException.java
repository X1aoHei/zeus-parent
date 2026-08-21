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

    REPEAT(100501L, "请勿重复提交");

    private Long code;

    private String errorMsg;

}
