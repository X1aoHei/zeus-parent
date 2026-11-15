package com.wss.zeus.core.trace;

import lombok.Data;

/**
 * Controller / Feign 调用链路日志
 *
 * @author wangshusheng
 */
@Data
public class TraceLog {

    /**
     * 链路追踪 ID
     */
    private String traceId;

    /**
     * 接口完整 URL
     */
    private String url;

    /**
     * 耗时（毫秒）
     */
    private Long cost;

    /**
     * 请求参数
     */
    private Object request;

    /**
     * 响应
     */
    private Object response;
}
