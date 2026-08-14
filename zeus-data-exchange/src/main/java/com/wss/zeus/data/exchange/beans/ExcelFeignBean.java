package com.wss.zeus.data.exchange.beans;

import com.wss.zeus.core.annotation.ExcelFeign;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author: wangshusheng
 * @Date: 2026-08-12 16:05
 */
@Data
@AllArgsConstructor
public class ExcelFeignBean {

    /**
     * 业务标识
     */
    private String name;

    /**
     * 方法定义
     */
    private Method method;

    /**
     * feign接口bean对象
     */
    private Object obj;

    /**
     * 注解
     */
    private transient ExcelFeign excelFeign;

}
