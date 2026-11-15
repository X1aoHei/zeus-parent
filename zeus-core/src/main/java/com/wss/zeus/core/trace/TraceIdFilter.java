package com.wss.zeus.core.trace;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * HTTP 统一入口：从请求头恢复或初始化 traceId，并写入线程本地变量 / MDC。
 * <p>
 * 覆盖 Controller 与 Feign 实现类（同为 Servlet 入口）：
 * <ul>
 *   <li>Feign/服务间调用：请求头通常带有上游 traceId，直接复用</li>
 *   <li>Controller/外部入口：请求头通常没有，则初始化新的 traceId</li>
 * </ul>
 *
 * @author wangshusheng
 */
public class TraceIdFilter extends OncePerRequestFilter implements Ordered {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String incoming = request.getHeader(TraceConstants.TRACE_ID);
        String traceId = TraceContext.setOrInit(incoming);
        response.setHeader(TraceConstants.TRACE_ID, traceId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            TraceContext.clear();
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }
}
