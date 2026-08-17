package com.wss.zeus.core.exception;

import lombok.Getter;

/**
 * @author: wangshusheng
 * @Date: 2026-08-17 15:11
 */
@Getter
public class BizException extends RuntimeException {
    private Long code;

    private String errorMsg;


    public BizException(BizError bizError) {
        super(bizError.getErrorMsg());
        this.code = bizError.getCode();
        this.errorMsg = bizError.getErrorMsg();
    }

}
