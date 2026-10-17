package com.sku.backend.global.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    // 파일을 저장하고 클라이언트가 접근 가능한 URL을 반환
    String upload(MultipartFile file, String directory);

    // 저장된 파일 URL에 해당하는 파일을 삭제 (없으면 무시)
    void deleteByUrl(String fileUrl);
}