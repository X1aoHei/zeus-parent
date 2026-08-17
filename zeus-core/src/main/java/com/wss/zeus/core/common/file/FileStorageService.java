package com.wss.zeus.core.common.file;

/**
 * 文件存储服务接口
 * <p>
 * 抽象文件上传能力，业务方自行实现（OSS / MinIO / 本地等）
 * </p>
 *
 * @author wangshusheng
 */
public interface FileStorageService {

    /**
     * 上传文件
     *
     * @param request 上传请求
     * @return 文件标识（fileId）
     */
    String upload(FileUploadRequest request);

    /**
     * 获取文件下载地址
     *
     * @param fileId 文件标识
     * @return 下载URL
     */
    String getUrl(String fileId);
}
