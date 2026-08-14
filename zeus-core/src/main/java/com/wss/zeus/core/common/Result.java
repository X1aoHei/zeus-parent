package com.wss.zeus.core.common;

import lombok.Data;

/**
 * @author: wangshusheng
 * @Date: 2026-08-12 16:29
 */
@Data
public class Result<T> {
    private T data;
}
