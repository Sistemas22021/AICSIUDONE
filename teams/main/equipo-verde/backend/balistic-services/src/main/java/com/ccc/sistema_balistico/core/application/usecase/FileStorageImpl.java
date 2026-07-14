package com.ccc.sistema_balistico.core.application.usecase;

import com.ccc.sistema_balistico.core.application.services.FileStorageService;
import com.ccc.sistema_balistico.core.domain.exceptions.custom.storage.FileTooLargeException;
import com.ccc.sistema_balistico.core.domain.exceptions.custom.storage.ImageNotFoundException;
import jakarta.annotation.PostConstruct;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "local", matchIfMissing = true)
public class FileStorageImpl implements FileStorageService {

    private final Path rootLocation;

    public FileStorageImpl(@Value("${storage.location}") String location) {
        if (location.trim().isEmpty()) {
            throw new IllegalArgumentException("Storage location path cannot be empty or null");
        }
        this.rootLocation = Paths.get(location);
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory at: " + rootLocation, e);
        }
    }

    @Override
    public String saveImageFile(MultipartFile file, String name) {
        try {
            String newFileName = getNewFileName(file, name);

            byte[] bytes = file.getBytes();

            Path path = this.rootLocation.resolve(newFileName)
                    .normalize().toAbsolutePath();

            Files.write(path, bytes);


            return newFileName;
        } catch (IOException e) {
            throw new RuntimeException("Error uploading the file " + e);
        }
    }


    @Override
    public Resource loadImageFile(String img) {
        try {
            Path file = rootLocation.resolve(img);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ImageNotFoundException("Image Not Found: " + img);
            }

        } catch (IOException e) {
            throw new ImageNotFoundException("URL not valid");
        }
    }

    @Override
    public String getContentType(String path) {
        Path file = rootLocation.resolve(path);

        try {
            String contentType = Files.probeContentType(file);
            if (contentType == null) contentType = "application/octet-stream";

            return contentType;
        } catch (IOException e) {
            throw new ImageNotFoundException("URL not valid");
        }
    }

    private static @NonNull String getNewFileName(MultipartFile file, String name) throws FileUploadException {
        String originalName = file.getOriginalFilename();
        if (file.isEmpty()) {
            throw new FileUploadException("No file was provided or the file is empty");
        }
        if (originalName == null) {
            throw new FileUploadException("Could not determine file name");
        }
        long fileSize = file.getSize();

        int sizeMax = 5 * 1024 * 1024;

        if (fileSize > sizeMax) throw new FileTooLargeException("The file exceeds the allowed limit. (5 MB)");
        if (!originalName.endsWith(".jpg") && !originalName.endsWith(".jpeg") && !originalName.endsWith(".png")) {
            throw new FileUploadException("File Type not Allowed. Only JPG, JPEG, or PNG are accepted.");
        }

        String fileExtension = originalName.substring(originalName.lastIndexOf("."));

        return name + fileExtension;
    }

    @Override
    public void deleteImage(String path) {
        if (path == null || path.trim().isEmpty()) return;
        try {
            Path file = rootLocation.resolve(path).normalize().toAbsolutePath();
            if (Files.exists(file)) {
                Files.delete(file);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error deleting file: " + e.getMessage(), e);
        }
    }
}
