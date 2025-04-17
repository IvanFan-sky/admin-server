package com.spark.adminserver.common.exception;

/**
 * 基础服务异常类
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Integer code;

    public ServiceException(String message) {
        super(message);
        this.code = 500; // 默认错误码
    }

    public ServiceException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
} 