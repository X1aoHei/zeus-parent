package com.wss.zeus.data.exchange.handler.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson2.JSONObject;
import com.wss.zeus.core.annotation.ExcelFeign;
import com.wss.zeus.core.body.TableRowBaseData;
import com.wss.zeus.core.common.PageData;
import com.wss.zeus.core.common.Result;
import com.wss.zeus.core.common.file.FileStorageService;
import com.wss.zeus.core.common.file.FileUploadRequest;
import com.wss.zeus.core.util.SpringContextUtil;
import com.wss.zeus.data.exchange.beans.ExcelFeignBean;
import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;
import com.wss.zeus.data.exchange.enums.TemplateConfig;
import com.wss.zeus.data.exchange.factory.ExcelFeignBeanFactory;
import com.wss.zeus.data.exchange.handler.ExcelFeignHandler;
import com.wss.zeus.data.exchange.handler.ExcelTableRowConvertor;
import com.wss.zeus.data.exchange.repository.ExcelExportTaskRepository;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;

/**
 * 默认 Excel Feign 执行器
 * <p>
 * 调用 Feign 接口获取数据 → 写入 OutputStream → 通过 FileStorageService 上传
 * </p>
 *
 * @author wangshusheng
 */
@Slf4j
public class DefaultExcelFeignHandler implements ExcelFeignHandler {

    private final ExcelFeignBeanFactory excelFeignBeanFactory;
    private final FileStorageService fileStorageService;
    private final ExcelExportTaskRepository excelExportTaskRepository;

    public DefaultExcelFeignHandler(ExcelFeignBeanFactory excelFeignBeanFactory,
                                    FileStorageService fileStorageService,
                                    ExcelExportTaskRepository excelExportTaskRepository) {
        this.excelFeignBeanFactory = excelFeignBeanFactory;
        this.fileStorageService = fileStorageService;
        this.excelExportTaskRepository = excelExportTaskRepository;
    }

    @Override
    public void execute(ExcelExportTaskEntity task) {
        String bizName = task.getTemplateCode();
        ExcelFeignBean excelFeignBean = excelFeignBeanFactory.get(bizName);
        if (Objects.isNull(excelFeignBean)) {
            excelExportTaskRepository.updateStatusToFail(task.getTaskId(), "未找到对应的ExcelFeign配置: " + bizName);
            return;
        }

        try {
            // 1. 调用 Feign 接口获取数据
            JSONObject param = JSONObject.parseObject(task.getTaskParam());
            PageData pageData = doInvokeFeign(excelFeignBean, param);

            // 2. 获取模板配置
            TemplateConfig templateConfig = excelFeignBeanFactory.getTemplateConfig(bizName);
            Class<? extends TableRowBaseData> clazz = chooseTemplate(excelFeignBean.getExcelFeign(), templateConfig);
            if (Objects.isNull(clazz)) {
                excelExportTaskRepository.updateStatusToFail(task.getTaskId(), "未找到对应的Excel模板配置: " + bizName);
                return;
            }

            // 3. 数据转换
            if (Objects.nonNull(templateConfig) && Objects.nonNull(templateConfig.getConvertor())) {
                ExcelTableRowConvertor convertor = SpringContextUtil.getBean(templateConfig.getConvertor());
                pageData = convertor.convert(pageData);
            }

            // 4. 写入 OutputStream
            String fileName = bizName + "_" + System.currentTimeMillis() + ExcelTypeEnum.XLSX.getValue();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            EasyExcel.write(outputStream, clazz).sheet().doWrite(pageData.getList());

            // 5. 上传到文件存储
            FileUploadRequest uploadRequest = new FileUploadRequest();
            uploadRequest.setInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
            uploadRequest.setFileName(fileName);
            uploadRequest.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileId = fileStorageService.upload(uploadRequest);

            // 6. 更新任务状态为成功
            excelExportTaskRepository.updateStatusToSuccess(task.getTaskId(), fileId, fileName);

            log.info("导出任务执行成功, taskId={}, fileId={}", task.getTaskId(), fileId);

        } catch (Exception e) {
            log.error("导出任务执行失败, taskId={}", task.getTaskId(), e);
            throw new RuntimeException("导出任务执行失败", e);
        }
    }

    /**
     * 调用 Feign 接口
     */
    private PageData doInvokeFeign(ExcelFeignBean excelFeignBean, JSONObject param)
            throws IllegalAccessException, InvocationTargetException {
        Method method = excelFeignBean.getMethod();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object javaObject = param.toJavaObject(parameterTypes[0]);
        Object obj = excelFeignBean.getObj();

        Result<PageData> result = (Result<PageData>) method.invoke(obj, javaObject);
        return result.getData();
    }

    /**
     * 选择模板类
     */
    private Class<? extends TableRowBaseData> chooseTemplate(ExcelFeign excelFeign, TemplateConfig templateConfig) {
        if (Objects.nonNull(excelFeign.template())
                && !Objects.equals(excelFeign.template(), TableRowBaseData.class)) {
            return excelFeign.template();
        }

        if (Objects.nonNull(templateConfig) && Objects.nonNull(templateConfig.getTemplate())) {
            return templateConfig.getTemplate();
        }

        return null;
    }
}
