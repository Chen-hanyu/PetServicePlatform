package com.petplatform.service;

import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.config.FileStorageProperties;
import com.petplatform.dto.file.FileUploadResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final long MAX_IMAGE_SIZE = 5L * 1024 * 1024;
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );

    private final FileStorageProperties fileStorageProperties;

    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
    }

    public FileUploadResponse uploadImage(MultipartFile file) {
        validateLocalStorage();
        validateFile(file);

        String extension = getExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + extension;
        String datePath = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        Path storageDirectory = Paths.get(fileStorageProperties.getLocalPath(), datePath).toAbsolutePath().normalize();
        Path targetFile = storageDirectory.resolve(fileName);

        try {
            Files.createDirectories(storageDirectory);
            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new BusinessException(ResultCode.INVALID_OPERATION, "文件保存失败");
        }

        String accessPath = ensureEndsWithSlash(fileStorageProperties.getAccessPath());
        String url = accessPath + datePath + "/" + fileName;
        return new FileUploadResponse(url, file.getOriginalFilename(), file.getContentType(), file.getSize());
    }

    private void validateLocalStorage() {
        if (!"local".equalsIgnoreCase(fileStorageProperties.getType())) {
            throw new BusinessException(ResultCode.INVALID_OPERATION, "当前仅支持本地文件存储");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "上传文件不能为空");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "图片大小不能超过 5MB");
        }
        String contentType = file.getContentType();
        if (!StringUtils.hasText(contentType) || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "仅支持 jpg、png、webp、gif 图片");
        }
    }

    private String getExtension(String originalFilename) {
        if (!StringUtils.hasText(originalFilename)) {
            return ".bin";
        }
        String extension = StringUtils.getFilenameExtension(originalFilename);
        return StringUtils.hasText(extension) ? "." + extension.toLowerCase(Locale.ROOT) : ".bin";
    }

    private String ensureEndsWithSlash(String value) {
        return value.endsWith("/") ? value : value + "/";
    }
}
