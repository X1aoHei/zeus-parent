package com.wss.zeus.data.exchange.handler;

import lombok.Data;

/**
 * Excel 导出结果
 *
 * @author wangshusheng
 */
@Data
public class ExcelExportResult {

    /**
     * 文件ID
     */
    private String fileId;

    /**
     * 文件名称
     */
    private String fileName;

    public ExcelExportResult() {
    }

    public ExcelExportResult(String fileId, String fileName) {
        this.fileId = fileId;
        this.fileName = fileName;
    }

    public static ExcelExportResult of(String fileId, String fileName) {
        return new ExcelExportResult(fileId, fileName);
    }
}
