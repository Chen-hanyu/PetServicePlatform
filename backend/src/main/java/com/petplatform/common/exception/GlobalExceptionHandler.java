package com.petplatform.common.exception;

import com.petplatform.common.ApiResponse;
import com.petplatform.common.ResultCode;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException exception) {
        return ResponseEntity
                .status(exception.getHttpStatus())
                .body(ApiResponse.failure(exception.getCode(), exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage() == null ? ResultCode.VALIDATION_ERROR.getMessage() : error.getDefaultMessage())
                .orElse(ResultCode.VALIDATION_ERROR.getMessage());
        return buildErrorResponse(ResultCode.VALIDATION_ERROR.getHttpStatus(), ResultCode.VALIDATION_ERROR.getCode(), message);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBindException(BindException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage() == null ? ResultCode.PARAM_ERROR.getMessage() : error.getDefaultMessage())
                .orElse(ResultCode.PARAM_ERROR.getMessage());
        return buildErrorResponse(ResultCode.PARAM_ERROR.getHttpStatus(), ResultCode.PARAM_ERROR.getCode(), message);
    }

    @ExceptionHandler({
            ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception exception) {
        return buildErrorResponse(ResultCode.PARAM_ERROR.getHttpStatus(), ResultCode.PARAM_ERROR.getCode(), exception.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        return buildErrorResponse(
                ResultCode.PARAM_ERROR.getHttpStatus(),
                ResultCode.PARAM_ERROR.getCode(),
                "请求体格式错误，请检查 JSON 结构"
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
        log.error("Unhandled exception", exception);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "服务器异常"
        );
    }

    private ResponseEntity<ApiResponse<Void>> buildErrorResponse(HttpStatus httpStatus, int code, String message) {
        return ResponseEntity.status(httpStatus).body(ApiResponse.failure(code, message));
    }
}
