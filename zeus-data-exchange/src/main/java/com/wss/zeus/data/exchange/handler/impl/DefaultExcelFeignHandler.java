package com.wss.zeus.data.exchange.handler.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson2.JSONObject;
import com.wss.zeus.core.annotation.ExcelFeign;
import com.wss.zeus.core.body.TableRowBaseData;
import com.wss.zeus.core.common.PageData;
import com.wss.zeus.core.common.Result;
import com.wss.zeus.core.util.SpringContextUtil;
import com.wss.zeus.data.exchange.beans.ExcelFeignBean;
import com.wss.zeus.data.exchange.enums.TemplateConfig;
import com.wss.zeus.data.exchange.factory.ExcelFeignBeanFactory;
import com.wss.zeus.data.exchange.handler.ExcelFeignHandler;
import com.wss.zeus.data.exchange.handler.ExcelTableRowConvertor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
public class DefaultExcelFeignHandler implements ExcelFeignHandler {

    private ExcelFeignBeanFactory excelFeignBeanFactory;

    public DefaultExcelFeignHandler(ExcelFeignBeanFactory excelFeignBeanFactory) {
        this.excelFeignBeanFactory = excelFeignBeanFactory;
    }

    public void execute(String bizName, JSONObject param) {
        ExcelFeignBean excelFeignBean = excelFeignBeanFactory.get(bizName);
        if (Objects.isNull(excelFeignBean)) {
            return;
        }
        Method method = excelFeignBean.getMethod();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object javaObject = param.toJavaObject(parameterTypes[0]);

        Object obj = excelFeignBean.getObj();
        try {
            Result<PageData> result = (Result<PageData>) method.invoke(obj, javaObject);

            PageData pageData = result.getData();

            TemplateConfig templateConfig = excelFeignBeanFactory.getTemplateConfig(bizName);
            Class<? extends TableRowBaseData> clazz = chooseTemplate(excelFeignBean.getExcelFeign(), templateConfig);
            if (Objects.isNull(clazz)) {
                return;
            }

            if (Objects.nonNull(templateConfig.getConvertor())) {
                ExcelTableRowConvertor convertor = SpringContextUtil.getBean(templateConfig.getConvertor());
                pageData = convertor.convert(pageData);
            }

            File file = new File("/Users/a1-6/logs/a.xlsx");
            file.deleteOnExit();
            file.createNewFile();

            EasyExcel.write(file, clazz).sheet().doWrite(pageData.getList());

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Class<? extends TableRowBaseData> chooseTemplate(ExcelFeign excelFeign, TemplateConfig templateConfig) {
        if (Objects.nonNull(excelFeign.template()) && Objects.nonNull(excelFeign.template()) && !Objects.equals(excelFeign.template(), TableRowBaseData.class)) {
            return excelFeign.template();
        }

        if (Objects.nonNull(templateConfig) && Objects.nonNull(templateConfig.getTemplate())) {
            return templateConfig.getTemplate();
        }

        return null;
    }

}
