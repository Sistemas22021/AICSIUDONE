package com.ccc.sistema_balistico.core.domain.exceptions.handler;

import com.ccc.sistema_balistico.core.domain.exceptions.custom.BulletIsDeleted;
import com.ccc.sistema_balistico.core.domain.exceptions.custom.BulletNotFound;
import com.ccc.sistema_balistico.core.domain.exceptions.custom.caliber.CaliberIsDeleted;
import com.ccc.sistema_balistico.core.domain.exceptions.custom.caliber.CaliberNotFound;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {


        ApiErrorResponse apiError = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request [HttpMessageNotReadableException]")
                .message("Malformed JSON request or invalid field format")
                .path(request.getDescription(false))
                .build();

        return new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();

        // Recorremos todos los errores de los campos
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        ApiErrorResponse apiError = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Validation failed for the provided data")
                .path(request.getDescription(false))
                .validationErrors(errors) // Aquí metemos el mapa
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String propertyName = violation.getPropertyPath().toString();
            String fieldName = propertyName.substring(propertyName.lastIndexOf('.') + 1);
            errors.put(fieldName, violation.getMessage());
        });

        ApiErrorResponse apiError = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Validation failed for the provided data")
                .path(request.getDescription(false))
                .validationErrors(errors)
                .build();

        return new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodValidation(HandlerMethodValidationException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getValueResults().forEach(result -> {
            String parameterName = result.getMethodParameter().getParameterName();
            result.getResolvableErrors().forEach(error ->
                    errors.put(parameterName, error.getDefaultMessage())
            );
        });
        ApiErrorResponse apiError = ApiErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Validation failed for the provided data")
                .path(request.getDescription(false))
                .validationErrors(errors)
                .build();

        return new ResponseEntity<>(apiError,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BulletNotFound.class)
    public ResponseEntity<ApiErrorResponse> handlerBulletNotFound(BulletNotFound exception, WebRequest request) {
        ApiErrorResponse errorResponse =
                ApiErrorResponse.
                        builder().
                        timestamp(LocalDateTime.now()).
                        status(HttpStatus.NOT_FOUND.value()).
                        error("Bullet Not Found").
                        message(exception.getMessage()).
                        path(request.getDescription(false)).
                        build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BulletIsDeleted.class)
    public ResponseEntity<ApiErrorResponse> handlerBulletIsDeleted(BulletIsDeleted exception, WebRequest request) {
        ApiErrorResponse errorResponse =
                ApiErrorResponse.
                        builder().
                        timestamp(LocalDateTime.now()).
                        status(HttpStatus.GONE.value()).
                        error("Bullet Is Deleted").
                        message(exception.getMessage()).
                        path(request.getDescription(false)).
                        build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CaliberIsDeleted.class)
    public ResponseEntity<ApiErrorResponse> handlerCaliberIsDeleted(CaliberIsDeleted exception, WebRequest request) {
        ApiErrorResponse errorResponse =
                ApiErrorResponse.
                        builder().
                        timestamp(LocalDateTime.now()).
                        status(HttpStatus.GONE.value()).
                        error("Caliber Is Deleted").
                        message(exception.getMessage()).
                        path(request.getDescription(false)).
                        build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CaliberNotFound.class)
    public ResponseEntity<ApiErrorResponse> handlerCaliberNotFound(CaliberNotFound exception, WebRequest request) {
        ApiErrorResponse errorResponse =
                ApiErrorResponse.
                        builder().
                        timestamp(LocalDateTime.now()).
                        status(HttpStatus.NOT_FOUND.value()).
                        error("Caliber Not Found").
                        message(exception.getMessage()).
                        path(request.getDescription(false)).
                        build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
