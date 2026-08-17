package com.wss.zeus.core.common;

import lombok.Data;

/**
 * @author: wangshusheng
 * @Date: 2026-08-12 16:29
 */
@Data
public class Result<T> {

    private T data;

    private Long code;

    private String errorMsg;

    public static final Long SUCCESS_CODE = 200L;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setData(data);
        result.setCode(SUCCESS_CODE);
        return result;
    }
}
