package com.wss.zeus.data.exchange.handler.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.wss.zeus.core.annotation.ExcelFeign;
import com.wss.zeus.core.body.TableRowBaseData;
import com.wss.zeus.core.common.PageData;
import com.wss.zeus.core.common.Result;
import com.wss.zeus.core.common.file.FileStorageService;
import com.wss.zeus.core.common.file.FileUploadRequest;
import com.wss.zeus.core.util.SpringContextUtil;
import com.wss.zeus.data.exchange.beans.ExcelFeignBean;
import com.wss.zeus.data.exchange.constant.ExportTaskConstant;
import com.wss.zeus.data.exchange.entity.ExcelExportTaskEntity;
import com.wss.zeus.data.exchange.enums.TemplateConfig;
import com.wss.zeus.data.exchange.factory.ExcelFeignBeanFactory;
import com.wss.zeus.data.exchange.handler.ExcelExportResult;
import com.wss.zeus.data.exchange.handler.ExcelFeignHandler;
import com.wss.zeus.data.exchange.handler.ExcelTableRowConvertor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 默认 Excel Feign 执行器
 * <p>
 * 只负责导出逻辑：调用 Feign 接口获取数据 → 写入 OutputStream → 通过 FileStorageService 上传
 * 不处理任务状态更新
 * </p>
 *
 * @author wangshusheng
 */
@Slf4j
public class DefaultExcelFeignHandler implements ExcelFeignHandler {

    private final ExcelFeignBeanFactory excelFeignBeanFactory;
    private final FileStorageService fileStorageService;

    public DefaultExcelFeignHandler(ExcelFeignBeanFactory excelFeignBeanFactory,
                                    FileStorageService fileStorageService) {
        this.excelFeignBeanFactory = excelFeignBeanFactory;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public ExcelExportResult execute(ExcelExportTaskEntity task) {
        String bizName = task.getTemplateCode();
        ExcelFeignBean excelFeignBean = excelFeignBeanFactory.get(bizName);
        if (Objects.isNull(excelFeignBean)) {
            throw new IllegalArgumentException(ExportTaskConstant.TEMPLATE_NOT_FOUND + ": " + bizName);
        }
        JSONObject param = doParseParam(task.getTaskParam());
        List<?> rows = doCollectRows(excelFeignBean, param);
        TemplateConfig templateConfig = excelFeignBeanFactory.getTemplateConfig(bizName);
        Class<? extends TableRowBaseData> clazz = doChooseTemplate(excelFeignBean.getExcelFeign(), templateConfig);
        if (Objects.isNull(clazz)) {
            throw new IllegalArgumentException(ExportTaskConstant.TEMPLATE_CLASS_NOT_FOUND + ": " + bizName);
        }
        if (Objects.nonNull(templateConfig) && Objects.nonNull(templateConfig.getConvertor())) {
            ExcelTableRowConvertor convertor = SpringContextUtil.getBean(templateConfig.getConvertor());
            PageData converted = convertor.convert(doToPageData(rows));
            rows = Objects.isNull(converted) ? rows : converted.getList();
        }
        String fileName = bizName + "_" + System.currentTimeMillis() + ExcelTypeEnum.XLSX.getValue();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, clazz).sheet().doWrite(rows);
        FileUploadRequest uploadRequest = new FileUploadRequest();
        uploadRequest.setInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
        uploadRequest.setFileName(fileName);
        uploadRequest.setContentType(ExportTaskConstant.EXCEL_CONTENT_TYPE);
        String fileId = fileStorageService.upload(uploadRequest);
        if (StringUtils.isBlank(fileId)) {
            throw new IllegalStateException(ExportTaskConstant.FILE_ID_EMPTY);
        }
        log.info("导出文件生成成功, taskId={}, fileId={}", task.getTaskId(), fileId);
        return ExcelExportResult.of(fileId, fileName);
    }

    /**
     * 按页拉取，直到不足一页或接口忽略分页 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    private List<?> doCollectRows(ExcelFeignBean excelFeignBean, JSONObject param) {
        int pageSize = doResolvePageSize(param);
        List<Object> rows = new ArrayList<>();
        String previousHead = null;
        for (int pageNo = ExportTaskConstant.FIRST_PAGE_NO; pageNo <= ExportTaskConstant.MAX_EXPORT_PAGES; pageNo++) {
            doFillPage(param, pageNo, pageSize);
            PageData pageData = doInvokeFeign(excelFeignBean, param);
            if (Objects.isNull(pageData) || CollectionUtils.isEmpty(pageData.getList())) {
                break;
            }
            List<?> pageRows = pageData.getList();
            String head = JSON.toJSONString(pageRows.get(0));
            if (Objects.equals(head, previousHead)) {
                break;
            }
            previousHead = head;
            rows.addAll(pageRows);
            if (pageRows.size() < pageSize) {
                break;
            }
        }
        return rows;
    }

    /**
     * 调用 Feign 接口 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    private PageData doInvokeFeign(ExcelFeignBean excelFeignBean, JSONObject param) {
        Method method = excelFeignBean.getMethod();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (Objects.isNull(parameterTypes) || parameterTypes.length == 0) {
            throw new IllegalStateException(ExportTaskConstant.FEIGN_RESULT_EMPTY);
        }
        Object javaObject = param.toJavaObject(parameterTypes[0]);
        try {
            Result result = (Result) method.invoke(excelFeignBean.getObj(), javaObject);
            if (Objects.isNull(result) || Objects.isNull(result.getData())) {
                throw new IllegalStateException(ExportTaskConstant.FEIGN_RESULT_EMPTY);
            }
            return (PageData) result.getData();
        } catch (InvocationTargetException exception) {
            if (exception.getCause() instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw new IllegalStateException(exception.getCause());
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException(exception);
        }
    }

    /**
     * 选择模板类
     */
    private Class<? extends TableRowBaseData> doChooseTemplate(ExcelFeign excelFeign, TemplateConfig templateConfig) {
        if (Objects.nonNull(excelFeign.template())
                && !Objects.equals(excelFeign.template(), TableRowBaseData.class)) {
            return excelFeign.template();
        }
        if (Objects.nonNull(templateConfig) && Objects.nonNull(templateConfig.getTemplate())) {
            return templateConfig.getTemplate();
        }
        return null;
    }

    /**
     * 解析任务参数 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    private JSONObject doParseParam(String taskParam) {
        if (StringUtils.isBlank(taskParam)) {
            return new JSONObject();
        }
        JSONObject param = JSONObject.parseObject(taskParam);
        return Objects.isNull(param) ? new JSONObject() : param;
    }

    /**
     * 读取分页大小 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    private int doResolvePageSize(JSONObject param) {
        Integer pageSize = param.getInteger("pageSize");
        if (Objects.isNull(pageSize) || pageSize <= 0) {
            pageSize = param.getInteger("size");
        }
        if (Objects.isNull(pageSize) || pageSize <= 0) {
            return ExportTaskConstant.DEFAULT_PAGE_SIZE;
        }
        return pageSize;
    }

    /**
     * 写入当前页码，兼容常见分页字段名 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    private void doFillPage(JSONObject param, int pageNo, int pageSize) {
        param.put("pageNo", pageNo);
        param.put("pageNum", pageNo);
        param.put("current", pageNo);
        param.put("pageSize", pageSize);
        param.put("size", pageSize);
    }

    /**
     * 组装转换器入参 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    private PageData doToPageData(List<?> rows) {
        PageData pageData = new PageData();
        pageData.setList(rows);
        return pageData;
    }
}
