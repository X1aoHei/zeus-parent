package com.wss.zeus.core.common.file;

import com.wss.zeus.core.exception.BizException;
import com.wss.zeus.core.exception.SystemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

/**
 * 本地文件存储 - 系AI自动生成
 *
 * @author WSS AI Agent @Date 2026-09-28
 */
@Slf4j
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {

    private final LocalFileProperties localFileProperties;

    /**
     * 把文件写入本地目录，返回相对路径作为文件标识 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-28
     */
    @Override
    public String upload(FileUploadRequest request) {
        doValidate(request);
        String fileId = doBuildFileId(request);
        Path target = doResolve(fileId);
        doWrite(request.getInputStream(), target);
        log.info("本地文件已写入, fileId={}, path={}", fileId, target);
        return fileId;
    }

    /**
     * 按文件标识返回下载地址 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-28
     */
    @Override
    public String getUrl(String fileId) {
        Path target = doResolve(fileId);
        if (StringUtils.isBlank(localFileProperties.getUrlPrefix())) {
            return target.toUri().toString();
        }
        String prefix = StringUtils.removeEnd(localFileProperties.getUrlPrefix(), FileStorageConstant.PATH_SEPARATOR);
        return prefix + FileStorageConstant.PATH_SEPARATOR + fileId;
    }

    /**
     * 校验上传请求 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-28
     */
    private void doValidate(FileUploadRequest request) {
        if (Objects.isNull(request) || Objects.isNull(request.getInputStream())) {
            log.warn("本地文件上传参数不完整");
            throw new BizException(SystemException.PARAM_ERROR);
        }
    }

    /**
     * 生成相对路径，目录和文件名去掉路径字符 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-28
     */
    private String doBuildFileId(FileUploadRequest request) {
        String fileName = doSanitize(StringUtils.defaultIfBlank(request.getFileName(), FileStorageConstant.DEFAULT_FILE_NAME));
        String storedName = UUID.randomUUID().toString().replace(FileStorageConstant.UUID_DASH, "")
                + FileStorageConstant.NAME_SEPARATOR + fileName;
        String directory = doSanitizeDirectory(request.getDirectory());
        if (StringUtils.isBlank(directory)) {
            return storedName;
        }
        return directory + FileStorageConstant.PATH_SEPARATOR + storedName;
    }

    /**
     * 解析到根目录之内的绝对路径 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-28
     */
    private Path doResolve(String fileId) {
        if (StringUtils.isBlank(fileId) || StringUtils.contains(fileId, FileStorageConstant.PARENT_DIR)) {
            throw new BizException(SystemException.PARAM_ERROR);
        }
        Path base = doBasePath();
        Path target = base.resolve(fileId).normalize();
        if (!target.startsWith(base)) {
            throw new BizException(SystemException.PARAM_ERROR);
        }
        return target;
    }

    /**
     * 写入文件 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-28
     */
    private void doWrite(InputStream inputStream, Path target) {
        try {
            Files.createDirectories(target.getParent());
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            log.error("本地文件写入失败, path={}", target, ex);
            throw new BizException(SystemException.SYSTEM_ERROR);
        }
    }

    /**
     * 本地根目录，未配置时使用用户目录下的默认文件夹 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-28
     */
    private Path doBasePath() {
        String configured = localFileProperties.getBasePath();
        Path base;
        if (StringUtils.isBlank(configured)) {
            base = Path.of(System.getProperty(FileStorageConstant.USER_HOME_PROPERTY), FileStorageConstant.DEFAULT_BASE_DIR);
        } else {
            base = Path.of(configured);
        }
        return base.toAbsolutePath().normalize();
    }

    /**
     * 只保留目录中的安全片段 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-28
     */
    private String doSanitizeDirectory(String directory) {
        if (StringUtils.isBlank(directory)) {
            return null;
        }
        String[] parts = StringUtils.split(directory, "/\\");
        if (Objects.isNull(parts) || parts.length == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            String cleaned = doSanitize(part);
            if (StringUtils.isBlank(cleaned) || Objects.equals(cleaned, FileStorageConstant.PARENT_DIR)) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append(FileStorageConstant.PATH_SEPARATOR);
            }
            builder.append(cleaned);
        }
        return builder.toString();
    }

    /**
     * 去掉文件名中的路径字符 - 系AI自动生成
     *
     * @author WSS AI Agent @Date 2026-09-28
     */
    private String doSanitize(String name) {
        String cleaned = StringUtils.replace(name, FileStorageConstant.PARENT_DIR, FileStorageConstant.NAME_SEPARATOR);
        return cleaned.replaceAll(FileStorageConstant.ILLEGAL_NAME_PATTERN, FileStorageConstant.NAME_SEPARATOR);
    }
}
