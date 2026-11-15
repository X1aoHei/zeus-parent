package com.wss.zeus.core.aspect;

import com.alibaba.fastjson2.JSON;
import com.wss.zeus.core.trace.TraceConstants;
import com.wss.zeus.core.trace.TraceContext;
import com.wss.zeus.core.trace.TraceLog;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 拦截 Controller 接口方法，将请求参数与响应写入 trace 日志
 *
 * @author wangshusheng
 */
@Aspect
public class ControllerTraceAspect {

    /**
     * 与 logback-spring.xml 中 logger name="trace" / traceAppender 对应
     */
    private static final Logger TRACE_LOG = LoggerFactory.getLogger("trace");

    @Around("execution(public * com.wss.zeus..controller..*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        // Controller 入口通常无上游 traceId：Filter 已处理；此处兜底初始化
        ensureControllerTraceId();
        long start = System.currentTimeMillis();
        TraceLog traceLog = new TraceLog();
        traceLog.setTraceId(TraceContext.getTraceId());
        traceLog.setUrl(resolveUrl());
        traceLog.setRequest(resolveRequest(pjp));
        try {
            Object result = pjp.proceed();
            traceLog.setResponse(toLogValue(result));
            return result;
        } catch (Throwable ex) {
            traceLog.setResponse(ex.getClass().getName() + ": " + ex.getMessage());
            throw ex;
        } finally {
            traceLog.setCost(System.currentTimeMillis() - start);
            TRACE_LOG.info(JSON.toJSONString(traceLog));
        }
    }

    /**
     * Controller 入口：请求头没有 traceId 时初始化；有则沿用（一般由 TraceIdFilter 完成）
     */
    private void ensureControllerTraceId() {
        if (TraceContext.getTraceId() != null) {
            return;
        }
        String incoming = null;
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            incoming = attributes.getRequest().getHeader(TraceConstants.TRACE_ID);
        }
        TraceContext.setOrInit(incoming);
    }

    private String resolveUrl() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
            return null;
        }
        HttpServletRequest request = attributes.getRequest();
        StringBuffer url = request.getRequestURL();
        String query = request.getQueryString();
        if (query != null && !query.isEmpty()) {
            url.append('?').append(query);
        }
        return url.toString();
    }

    private Object resolveRequest(ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();
        if (args == null || args.length == 0) {
            return null;
        }
        if (!(pjp.getSignature() instanceof MethodSignature signature)) {
            return toLogValue(args);
        }
        String[] names = signature.getParameterNames();
        Map<String, Object> params = new LinkedHashMap<>();
        for (int i = 0; i < args.length; i++) {
            String name = (names != null && i < names.length) ? names[i] : "arg" + i;
            params.put(name, toLogValue(args[i]));
        }
        return params;
    }

    private Object toLogValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof byte[] bytes) {
            return "byte[" + bytes.length + "]";
        }
        if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            List<Object> list = new ArrayList<>(length);
            for (int i = 0; i < length; i++) {
                list.add(toLogValue(Array.get(value, i)));
            }
            return list;
        }
        if (value instanceof InputStream || value instanceof OutputStream
                || value instanceof ServletRequest || value instanceof ServletResponse) {
            return value.getClass().getSimpleName();
        }
        if (value instanceof MultipartFile file) {
            Map<String, Object> fileLog = new LinkedHashMap<>(4);
            fileLog.put("name", file.getName());
            fileLog.put("originalFilename", file.getOriginalFilename());
            fileLog.put("size", file.getSize());
            return fileLog;
        }
        return value;
    }
}
