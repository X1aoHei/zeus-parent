package com.wss.zeus.feign.interceptor;

import com.alibaba.fastjson2.JSON;
import com.wss.zeus.core.trace.TraceLog;
import feign.InvocationContext;
import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.ResponseInterceptor;
import feign.ResponseInterceptor.Chain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

/**
 * Feign 请求/响应 trace 日志拦截器
 *
 * @author wangshusheng
 */
public class FeignTraceInterceptor implements RequestInterceptor, ResponseInterceptor {

    /**
     * 与 logback-spring.xml 中 logger name="trace" / traceAppender 对应
     */
    private static final Logger TRACE_LOG = LoggerFactory.getLogger("trace");

    private static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();

    @Override
    public void apply(RequestTemplate template) {
        START_TIME.set(System.currentTimeMillis());
    }

    @Override
    public Object intercept(InvocationContext invocationContext, Chain chain) throws Exception {
        Request request = invocationContext.response().request();
        TraceLog traceLog = new TraceLog();
        traceLog.setUrl(request.url());
        traceLog.setRequest(resolveRequest(request));
        try {
            Object result = chain.next(invocationContext);
            traceLog.setResponse(toLogValue(result));
            return result;
        } catch (Exception ex) {
            traceLog.setResponse(ex.getClass().getName() + ": " + ex.getMessage());
            throw ex;
        } finally {
            Long start = START_TIME.get();
            if (start != null) {
                traceLog.setCost(System.currentTimeMillis() - start);
            }
            START_TIME.remove();
            TRACE_LOG.info(JSON.toJSONString(traceLog));
        }
    }

    private Object resolveRequest(Request request) {
        byte[] body = request.body();
        if (body != null && body.length > 0) {
            if (request.isBinary() || isBinary(request.headers())) {
                return "binary[" + body.length + "]";
            }
            Charset charset = request.charset() != null ? request.charset() : StandardCharsets.UTF_8;
            String text = new String(body, charset);
            try {
                return JSON.parse(text);
            } catch (Exception e) {
                return text;
            }
        }
        RequestTemplate template = request.requestTemplate();
        if (template != null && template.queries() != null && !template.queries().isEmpty()) {
            return template.queries();
        }
        return null;
    }

    private boolean isBinary(Map<String, Collection<String>> headers) {
        Collection<String> contentTypes = headers.get("Content-Type");
        if (contentTypes == null) {
            contentTypes = headers.get("content-type");
        }
        if (contentTypes == null) {
            return false;
        }
        for (String contentType : contentTypes) {
            String lower = contentType.toLowerCase();
            if (lower.contains("octet-stream") || lower.contains("multipart")
                    || lower.startsWith("image/") || lower.startsWith("audio/") || lower.startsWith("video/")) {
                return true;
            }
        }
        return false;
    }

    private Object toLogValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof byte[] bytes) {
            return "byte[" + bytes.length + "]";
        }
        return value;
    }
}
