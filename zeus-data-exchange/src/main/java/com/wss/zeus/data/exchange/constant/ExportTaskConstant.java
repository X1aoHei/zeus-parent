package com.wss.zeus.data.exchange.constant;

import java.util.Objects;

/**
 * 导出任务常量 - 系AI自动生成
 *
 * @author WSS AI Agent @Date 2026-09-22
 */
public final class ExportTaskConstant {

    private ExportTaskConstant() {
    }

    public static final String FILE_TYPE_XLSX = "XLSX";

    public static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static final int INIT_VERSION = 0;

    public static final int MAX_FAIL_COUNT = 5;

    public static final int RECOVER_BATCH_SIZE = 5;

    public static final int IDEMPOTENT_TTL_SECONDS = 300;

    public static final int STALE_PROCESSING_MINUTES = 10;

    public static final int DEFAULT_PAGE_SIZE = 200;

    public static final int FIRST_PAGE_NO = 1;

    public static final int MAX_EXPORT_PAGES = 500;

    public static final int ERROR_REASON_MAX_LENGTH = 500;

    public static final String TEMPLATE_NOT_FOUND = "未找到对应的ExcelFeign配置";

    public static final String TEMPLATE_CLASS_NOT_FOUND = "未找到对应的Excel模板配置";

    public static final String FEIGN_RESULT_EMPTY = "导出数据接口返回为空";

    public static final String FILE_ID_EMPTY = "文件上传未返回fileId";

    /**
     * 计算下一次乐观锁版本 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-22
     */
    public static int nextVersion(Integer current) {
        if (Objects.isNull(current)) {
            return INIT_VERSION + 1;
        }
        return current + 1;
    }
}
