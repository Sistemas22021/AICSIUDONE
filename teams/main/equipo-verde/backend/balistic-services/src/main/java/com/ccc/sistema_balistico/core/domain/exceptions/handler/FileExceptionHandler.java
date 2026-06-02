package com.ccc.sistema_balistico.core.domain.exceptions.handler;

import com.ccc.sistema_balistico.core.domain.exceptions.custom.storage.FileTooLargeException;
import com.ccc.sistema_balistico.core.domain.exceptions.custom.storage.ImageNotFoundException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class FileExceptionHandler {
    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleImageNotFound(ImageNotFoundException ex, WebRequest request) {


        ApiErrorResponse apiError = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("File Not Found [NOT_FOUND]")
                .message("File Not Found")
                .path(request.getDescription(false))
                .build();

        return new ResponseEntity<>(apiError,HttpStatus.CONTENT_TOO_LARGE);
    }
    @ExceptionHandler(FileTooLargeException.class)
    public ResponseEntity<ApiErrorResponse> handleFileTooLarge(FileTooLargeException ex, WebRequest request) {


        ApiErrorResponse apiError = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONTENT_TOO_LARGE.value())
                .error("File size not allowed [CONTENT_TOO_LARGE]")
                .message("File size exceed 5MB")
                .path(request.getDescription(false))
                .build();

        return new ResponseEntity<>(apiError,HttpStatus.CONTENT_TOO_LARGE);
    }
    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ApiErrorResponse> handleFileUpload(FileUploadException ex, WebRequest request) {


        ApiErrorResponse apiError = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .error("File not allowed [UNSUPPORTED_MEDIA_TYPE]")
                .message("File type is not allowed. Only .JPG , .JPEG and PNG")
                .path(request.getDescription(false))
                .build();

        return new ResponseEntity<>(apiError,HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

}
