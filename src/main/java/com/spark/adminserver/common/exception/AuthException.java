package com.spark.adminserver.common.exception;

/**
 * 认证异常
 */
public class AuthException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 错误码
     */
    private Integer code;
    
    /**
     * 错误消息
     */
    private String message;
    
    /**
     * 构造函数
     *
     * @param message 错误消息
     */
    public AuthException(String message) {
        this(401, message);
    }
    
    /**
     * 构造函数
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public AuthException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    public Integer getCode() {
        return code;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
} 