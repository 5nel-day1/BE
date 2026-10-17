package com.sku.backend.global.storage;

import com.sku.backend.global.exception.CustomException;
import com.sku.backend.global.exception.GlobalErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class LocalFileStorageService implements FileStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final Path baseDir;
    private final String baseUrl;

    public LocalFileStorageService(
            @Value("${app.storage.local.base-dir}") String baseDir,
            @Value("${app.storage.local.base-url}") String baseUrl) {
        this.baseDir = Paths.get(baseDir).toAbsolutePath().normalize();
        this.baseUrl = trimTrailingSlash(baseUrl);
    }

    @Override
    public String upload(MultipartFile file, String directory) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(GlobalErrorCode.FILE_UPLOAD_FAILED);
        }
        validateContentType(file);

        String ext = resolveExtension(file);
        String filename = UUID.randomUUID() + ext;
        Path dir = baseDir.resolve(sanitizeDirectory(directory)).normalize();
        Path target = dir.resolve(filename).normalize();

        if (!target.startsWith(baseDir)) {
            throw new CustomException(GlobalErrorCode.FILE_UPLOAD_FAILED);
        }

        try {
            Files.createDirectories(dir);
            file.transferTo(target);
        } catch (IOException e) {
            log.error("로컬 파일 저장 실패: {}", target, e);
            throw new CustomException(GlobalErrorCode.FILE_UPLOAD_FAILED);
        }

        return baseUrl + "/" + sanitizeDirectory(directory) + "/" + filename;
    }

    @Override
    public void deleteByUrl(String fileUrl) {
        Path target = resolvePath(fileUrl);
        if (target == null) {
            return;
        }
        try {
            Files.deleteIfExists(target);
        } catch (IOException e) {
            log.warn("로컬 파일 삭제 실패: {}", target, e);
        }
    }

    private Path resolvePath(String fileUrl) {
        if (!StringUtils.hasText(fileUrl) || !fileUrl.startsWith(baseUrl + "/")) {
            return null;
        }
        String relative = fileUrl.substring((baseUrl + "/").length());
        Path target = baseDir.resolve(relative).normalize();
        if (!target.startsWith(baseDir)) {
            return null;
        }
        return target;
    }

    private void validateContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new CustomException(GlobalErrorCode.INVALID_IMAGE_TYPE);
        }
    }

    private String resolveExtension(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            return ".jpg";
        }
        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }

    private String sanitizeDirectory(String directory) {
        if (!StringUtils.hasText(directory)) {
            return "misc";
        }
        return directory.replace("\\", "/").replaceAll("^/+|/+$", "");
    }

    private static String trimTrailingSlash(String url) {
        if (url == null) {
            return "";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}