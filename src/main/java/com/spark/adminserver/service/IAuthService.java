package com.spark.adminserver.service;

import com.spark.adminserver.model.dto.LoginDTO;
import com.spark.adminserver.model.vo.CaptchaVO;
import com.spark.adminserver.model.vo.TokenVO;
import com.spark.adminserver.model.vo.UserInfoVO;

/**
 * 认证服务接口
 */
public interface IAuthService {
    
    /**
     * 生成验证码
     *
     * @return 验证码信息
     */
    CaptchaVO generateCaptcha();
    
    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录成功后的令牌信息
     */
    TokenVO login(LoginDTO loginDTO);
    
    /**
     * 用户登出
     */
    void logout();
    
    /**
     * 获取当前登录用户信息
     *
     * @return 当前登录用户信息
     */
    UserInfoVO getCurrentUserInfo();
} 