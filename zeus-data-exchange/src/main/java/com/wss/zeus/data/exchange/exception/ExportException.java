package com.wss.zeus.data.exchange.exception;

import com.wss.zeus.core.exception.BizError;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: wangshusheng
 * @Date: 2026-08-17 15:17
 */
@Getter
@AllArgsConstructor
public enum ExportException implements BizError {

    SUBMIT_ERROR(1100501L, "提交失败");

    private long code;

    private String errorMsg;

}
