package com.wss.zeus.core.aspect;

import com.alibaba.fastjson2.JSON;
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
        long start = System.currentTimeMillis();
        TraceLog traceLog = new TraceLog();
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
