package com.wss.zeus.core.common.file;

import lombok.Data;

import java.io.InputStream;

/**
 * 文件上传请求
 *
 * @author wangshusheng
 */
@Data
public class FileUploadRequest {

    /**
     * 文件流
     */
    private InputStream inputStream;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件类型（MIME）
     */
    private String contentType;

    /**
     * 存储目录（可选）
     */
    private String directory;

    /**
     * 存储桶（可选）
     */
    private String bucket;
}
