package com.wss.zeus.core.common.file;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 本地文件存储配置 - 系AI自动生成
 *
 * @author WSS AI Agent @Date 2026-09-28
 */
@Data
@ConfigurationProperties(prefix = "zeus.file.local")
public class LocalFileProperties {

    /**
     * 是否启用本地存储。已有其他 FileStorageService 时不会覆盖。
     */
    private boolean enabled = true;

    /**
     * 本地根目录
     */
    private String basePath;

    /**
     * 下载地址前缀。为空时返回本地文件 URI。
     */
    private String urlPrefix;
}
