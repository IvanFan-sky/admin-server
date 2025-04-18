package com.spark.adminserver.service.impl;

import com.spark.adminserver.common.exception.BusinessException;
import com.spark.adminserver.model.dto.LoginDTO;
import com.spark.adminserver.model.entity.User;
import com.spark.adminserver.model.vo.CaptchaVO;
import com.spark.adminserver.model.vo.TokenVO;
import com.spark.adminserver.model.vo.UserInfoVO;
import com.spark.adminserver.model.vo.UserVO;
import com.spark.adminserver.service.IAuthService;
import com.spark.adminserver.service.IUserService;
import com.spark.adminserver.util.CaptchaUtil;
import com.spark.adminserver.util.JwtUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现
 */
@Service
@Slf4j
public class AuthServiceImpl implements IAuthService {
    
    @Resource
    private CaptchaUtil captchaUtil;
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    @Resource
    private AuthenticationManager authenticationManager;
    
    @Resource
    private JwtUtil jwtUtil;
    
    @Resource
    private IUserService userService;
    
    // 验证码缓存前缀
    private static final String CAPTCHA_PREFIX = "captcha:";
    
    // 验证码有效期（分钟）
    private static final int CAPTCHA_EXPIRATION = 5;
    
    /**
     * 生成验证码
     */
    @Override
    public CaptchaVO generateCaptcha() {
        // 使用EasyCaptcha生成验证码
        String[] captchaInfo = captchaUtil.generateCaptcha();
        String code = captchaInfo[0];  // 验证码文本
        String captchaImage = captchaInfo[1];  // 验证码图片Base64
        
        // 生成唯一ID
        String captchaId = UUID.randomUUID().toString();
        
        // 将验证码存入Redis，设置过期时间
        redisTemplate.opsForValue().set(CAPTCHA_PREFIX + captchaId, code, CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        
        // 记录日志
        log.debug("生成验证码: id={}, code={}", captchaId, code);
        
        // 返回验证码信息
        return CaptchaVO.builder()
                .captchaId(captchaId)
                .captchaImage(captchaImage)
                .build();
    }
    
    /**
     * 用户登录
     */
    @Override
    public TokenVO login(LoginDTO loginDTO) {
        // 校验验证码
        String captchaKey = CAPTCHA_PREFIX + loginDTO.getCaptchaId();
        Object cachedCode = redisTemplate.opsForValue().get(captchaKey);
        
        if (cachedCode == null) {
            throw new BusinessException(40101, "验证码已过期");
        }
        
        if (!cachedCode.toString().equalsIgnoreCase(loginDTO.getCaptchaCode())) {
            throw new BusinessException(40101, "验证码错误");
        }
        
        // 验证码使用后删除
        redisTemplate.delete(captchaKey);
        
        try {
            // 使用Spring Security进行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getPrincipal(),
                            loginDTO.getPassword()
                    )
            );
            
            // 认证成功后，设置认证信息到上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 获取认证用户信息
            org.springframework.security.core.userdetails.User principal = 
                    (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            
            // 获取系统用户信息
            User user = userService.getByUsername(principal.getUsername());
            
            // 创建Token
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", user.getUsername());
            
            String accessToken = jwtUtil.createAccessToken(user.getUserId().toString(), claims);
            String refreshToken = jwtUtil.createRefreshToken(user.getUserId().toString());
            
            // 记录登录成功日志（这里可以通过AOP或事件监听器实现）
            log.info("用户登录成功：{}", user.getUsername());
            
            // 返回Token信息
            return TokenVO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .expiresIn(7200) // 通常这个值应该和JWT配置中的过期时间保持一致
                    .build();
            
        } catch (Exception e) {
            // 记录登录失败日志
            log.error("用户登录失败：{}", loginDTO.getPrincipal(), e);
            throw new BusinessException(40103, "用户名或密码错误");
        }
    }
    
    /**
     * 用户登出
     */
    @Override
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            // 清除认证上下文
            SecurityContextHolder.clearContext();
            
            // 这里可以记录登出日志，或者将当前token加入黑名单
            log.info("用户登出成功");
        }
    }
    
    /**
     * 获取当前登录用户信息
     */
    @Override
    public UserInfoVO getCurrentUserInfo() {
        // 获取当前认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(40101, "未登录");
        }
        
        // 获取认证主体
        org.springframework.security.core.userdetails.User principal = 
                (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
        
        // 获取系统用户信息
        User user = userService.getByUsername(principal.getUsername());
        UserVO userVO = userService.getUserVOById(user.getUserId());
        
        // 获取用户角色和权限
        List<String> roles = userService.getUserRoles(user.getUserId());
        List<String> permissions = userService.getUserPermissions(user.getUserId());
        
        // 组装用户信息
        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setUserInfo(userVO);
        userInfoVO.setRoles(roles);
        userInfoVO.setPermissions(permissions);
        
        return userInfoVO;
    }
} 