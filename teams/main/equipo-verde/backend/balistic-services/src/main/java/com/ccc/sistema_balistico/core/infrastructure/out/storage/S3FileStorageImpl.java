package com.ccc.sistema_balistico.core.infrastructure.out.storage;

import com.ccc.sistema_balistico.core.application.services.FileStorageService;
import com.ccc.sistema_balistico.core.domain.exceptions.custom.storage.FileTooLargeException;
import com.ccc.sistema_balistico.core.domain.exceptions.custom.storage.ImageNotFoundException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Service
@ConditionalOnProperty(name = "storage.type", havingValue = "s3")
public class S3FileStorageImpl implements FileStorageService {

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Override
    public String saveImageFile(MultipartFile file, String name) {
        try {
            String newFileName = getNewFileName(file, name);
            byte[] bytes = file.getBytes();

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(newFileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(bytes));

            return newFileName;
        } catch (IOException e) {
            throw new RuntimeException("Error uploading the file to S3: " + e.getMessage(), e);
        }
    }

    @Override
    public Resource loadImageFile(String img) {
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(img)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getRequest);
            return new S3Resource(objectBytes.asByteArray(), img);

        } catch (NoSuchKeyException e) {
            throw new ImageNotFoundException("Image Not Found in S3: " + img);
        } catch (Exception e) {
            throw new ImageNotFoundException("Error loading image from S3: " + e.getMessage());
        }
    }

    @Override
    public String getContentType(String path) {
        if (path == null) {
            return "application/octet-stream";
        }
        String lowerPath = path.toLowerCase();
        if (lowerPath.endsWith(".png")) {
            return "image/png";
        } else if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        return "application/octet-stream";
    }

    @Override
    public void deleteImage() {
        // Placeholder as per interface definition
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

        if (fileSize > sizeMax) {
            throw new FileTooLargeException("The file exceeds the allowed limit. (5 MB)");
        }
        String lowerName = originalName.toLowerCase();
        if (!lowerName.endsWith(".jpg") && !lowerName.endsWith(".jpeg") && !lowerName.endsWith(".png")) {
            throw new FileUploadException("File Type not Allowed. Only JPG, JPEG, or PNG are accepted.");
        }

        String fileExtension = originalName.substring(originalName.lastIndexOf("."));
        return name + fileExtension;
    }

    // Custom Resource implementation that returns a filename
    private static class S3Resource extends ByteArrayResource {
        private final String filename;

        public S3Resource(byte[] byteArray, String filename) {
            super(byteArray);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }
    }
}
