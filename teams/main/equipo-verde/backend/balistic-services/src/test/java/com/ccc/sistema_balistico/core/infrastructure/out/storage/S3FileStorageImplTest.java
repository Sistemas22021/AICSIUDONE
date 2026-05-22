package com.ccc.sistema_balistico.core.infrastructure.out.storage;

import com.ccc.sistema_balistico.core.domain.exceptions.custom.storage.FileTooLargeException;
import com.ccc.sistema_balistico.core.domain.exceptions.custom.storage.ImageNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3FileStorageImplTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3FileStorageImpl s3FileStorage;

    private static final String BUCKET_NAME = "sistema-balistico-images";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(s3FileStorage, "bucketName", BUCKET_NAME);
    }

    @Test
    void testSaveImageFileSuccess() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.png",
                "image/png",
                "test image bytes".getBytes()
        );

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        String result = s3FileStorage.saveImageFile(file, "uuid-test");

        assertEquals("uuid-test.png", result);
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void testSaveImageFileFileTooLarge() {
        byte[] bytes = new byte[6 * 1024 * 1024]; // 6 MB
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "large.png",
                "image/png",
                bytes
        );

        assertThrows(FileTooLargeException.class, () -> s3FileStorage.saveImageFile(file, "uuid-test"));
    }

    @Test
    void testSaveImageFileInvalidExtension() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "invalid.txt",
                "text/plain",
                "test bytes".getBytes()
        );

        assertThrows(RuntimeException.class, () -> s3FileStorage.saveImageFile(file, "uuid-test"));
    }

    @Test
    void testLoadImageFileSuccess() {
        byte[] expectedBytes = "image content".getBytes();
        GetObjectResponse response = GetObjectResponse.builder().build();
        ResponseBytes<GetObjectResponse> responseBytes = ResponseBytes.fromByteArray(response, expectedBytes);

        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(responseBytes);

        Resource resource = s3FileStorage.loadImageFile("uuid-test.png");

        assertNotNull(resource);
        assertEquals("uuid-test.png", resource.getFilename());
        verify(s3Client, times(1)).getObjectAsBytes(any(GetObjectRequest.class));
    }

    @Test
    void testLoadImageFileNotFound() {
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenThrow(NoSuchKeyException.builder().build());

        assertThrows(ImageNotFoundException.class, () -> s3FileStorage.loadImageFile("nonexistent.png"));
    }

    @Test
    void testGetContentType() {
        assertEquals("image/png", s3FileStorage.getContentType("image.png"));
        assertEquals("image/jpeg", s3FileStorage.getContentType("image.jpg"));
        assertEquals("image/jpeg", s3FileStorage.getContentType("image.JPEG"));
        assertEquals("application/octet-stream", s3FileStorage.getContentType("file.bin"));
        assertEquals("application/octet-stream", s3FileStorage.getContentType(null));
    }
}
