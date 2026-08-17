package com.wss.zeus.core.exception;

/**
 * @author: wangshusheng
 * @Date: 2026-08-17 15:14
 */
public interface BizError {
    Long getCode();
    String getErrorMsg();
}
