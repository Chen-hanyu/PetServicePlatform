package com.petplatform.common;

import org.springframework.http.HttpStatus;

public enum ResultCode {
    SUCCESS(0, "ok", HttpStatus.OK),
    PARAM_ERROR(10001, "参数错误", HttpStatus.BAD_REQUEST),
    VALIDATION_ERROR(10002, "数据校验失败", HttpStatus.UNPROCESSABLE_ENTITY),
    RESOURCE_NOT_FOUND(10003, "资源不存在", HttpStatus.NOT_FOUND),
    UNAUTHORIZED(10004, "登录失效", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(10005, "无权限", HttpStatus.FORBIDDEN),
    INVALID_OPERATION(10006, "状态不允许当前操作", HttpStatus.CONFLICT),
    DUPLICATE_DATA(10007, "数据重复", HttpStatus.CONFLICT),
    OUT_OF_STOCK(10008, "库存不足", HttpStatus.CONFLICT),
    ALREADY_REVIEWED(10009, "审核已处理", HttpStatus.CONFLICT),
    BOOKING_TIME_CONFLICT(10010, "预约时间冲突", HttpStatus.CONFLICT),
    AI_NOT_CONFIGURED(10011, "AI 服务未配置", HttpStatus.SERVICE_UNAVAILABLE),
    AI_SERVICE_ERROR(10012, "AI 服务调用失败", HttpStatus.SERVICE_UNAVAILABLE);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ResultCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
