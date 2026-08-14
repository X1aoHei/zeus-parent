package com.wss.zeus.core.annotation;


import com.wss.zeus.core.body.TableRowBaseData;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelFeign {

    String name();

    Class<? extends TableRowBaseData> template() default TableRowBaseData.class;
}
