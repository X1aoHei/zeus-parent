package com.wss.zeus.web.handler;

import com.wss.zeus.core.common.Result;
import com.wss.zeus.core.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * <p>
 * 统一处理 BizException，返回标准 Result 格式
 * </p>
 *
 * @author wangshusheng
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     *
     * @param e 业务异常
     * @return 标准响应
     */
    @ExceptionHandler(BizException.class)
    public Result<Void> handleBizException(BizException e) {
        log.warn("业务异常: code={}, msg={}", e.getCode(), e.getErrorMsg());
        Result<Void> result = new Result<>();
        result.setCode(e.getCode());
        result.setErrorMsg(e.getErrorMsg());
        return result;
    }

    /**
     * 处理未知异常
     *
     * @param e 异常
     * @return 标准响应
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        Result<Void> result = new Result<>();
        result.setCode(500L);
        result.setErrorMsg("系统异常");
        return result;
    }
}
