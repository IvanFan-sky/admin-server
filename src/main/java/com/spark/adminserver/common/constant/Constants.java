package com.spark.adminserver.common.constant;

/**
 * 系统常量
 */
public class Constants {
    
    /**
     * 验证码 Redis Key 前缀
     */
    public static final String CAPTCHA_CODE_KEY = "captcha:code:";
    
    /**
     * 验证码有效期（分钟）
     */
    public static final int CAPTCHA_EXPIRATION = 5;
    
    /**
     * 是否启用验证码
     */
    public static final boolean CAPTCHA_ENABLED = true;
    
    /**
     * 登录用户 Redis Key 前缀
     */
    public static final String LOGIN_USER_KEY = "login:user:";
    
    /**
     * 令牌有效期（毫秒）
     */
    public static final long TOKEN_EXPIRATION = 30 * 60 * 1000L;
    
    /**
     * 令牌有效期（秒）
     */
    public static final int TOKEN_EXPIRATION_SECONDS = 30 * 60;
} 