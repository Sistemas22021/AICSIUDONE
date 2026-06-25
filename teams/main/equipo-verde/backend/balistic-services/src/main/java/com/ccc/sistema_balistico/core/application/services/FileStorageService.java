package com.ccc.sistema_balistico.core.application.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String saveImageFile(MultipartFile file,String name);
    Resource loadImageFile(String img);
    String getContentType(String path);
    void deleteImage();
}
