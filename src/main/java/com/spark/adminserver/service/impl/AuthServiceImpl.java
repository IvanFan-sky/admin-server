package com.spark.adminserver.service.impl;

import com.spark.adminserver.common.constant.Constants;
import com.spark.adminserver.common.exception.AuthException;
import com.spark.adminserver.common.exception.CaptchaException;
import com.spark.adminserver.common.exception.ServiceException;
import com.spark.adminserver.model.dto.LoginDTO;
import com.spark.adminserver.model.vo.CaptchaVO;
import com.spark.adminserver.model.vo.TokenVO;
import com.spark.adminserver.model.vo.UserInfoVO;
import com.spark.adminserver.model.vo.UserVO;
import com.spark.adminserver.security.entity.LoginUser;
import com.spark.adminserver.service.IAuthService;
import com.spark.adminserver.service.IUserService;
import com.spark.adminserver.util.CaptchaUtil;
import com.spark.adminserver.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final CaptchaUtil captchaUtil;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final IUserService userService;

    /**
     * 生成验证码
     *
     * @return 验证码信息
     */
    @Override
    public CaptchaVO generateCaptcha() {
        // 生成验证码
        String[] captchaArray = captchaUtil.generateCaptcha();
        String captchaCode = captchaArray[0];
        String captchaImage = captchaArray[1];

        // 生成验证码唯一标识
        String uuid = UUID.randomUUID().toString();
        String captchaKey = Constants.CAPTCHA_CODE_KEY + uuid;

        // 将验证码存入Redis，设置过期时间为5分钟
        redisTemplate.opsForValue().set(captchaKey, captchaCode, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);

        // 返回验证码信息
        return CaptchaVO.builder()
                .captchaId(uuid)
                .captchaImage(captchaImage)
                .build();
    }

    /**
     * 用户登录
     *
     * @param loginDTO 登录信息
     * @return 登录成功后的令牌信息
     */
    @Override
    public TokenVO login(LoginDTO loginDTO) {
        try {
            // 验证码校验
            validateCaptcha(loginDTO.getCaptchaId(), loginDTO.getCaptchaCode());

            // 创建身份验证令牌
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginDTO.getPrincipal(), loginDTO.getPassword());

            // 认证
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // 获取认证成功的用户信息
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();

            // 生成token
            String userId = loginUser.getUser().getUserId().toString();
            Map<String, Object> claims = new HashMap<>(2);
            claims.put("username", loginUser.getUsername());

            String accessToken = jwtUtil.createAccessToken(userId, claims);
            String refreshToken = jwtUtil.createRefreshToken(userId);

            // 将用户信息存入Redis
            String userKey = Constants.LOGIN_USER_KEY + userId;
            redisTemplate.opsForValue().set(userKey, loginUser, Constants.TOKEN_EXPIRATION, TimeUnit.MILLISECONDS);

            // 返回token
            return TokenVO.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .expiresIn(Constants.TOKEN_EXPIRATION_SECONDS)
                    .build();
        } catch (BadCredentialsException | InternalAuthenticationServiceException e) {
            log.info("登录失败: {}", e.getMessage());
            throw new AuthException("用户名或密码错误");
        } catch (Exception e) {
            log.error("登录异常: ", e);
            throw new ServiceException("登录失败，请联系管理员");
        }
    }

    /**
     * 退出登录
     */
    @Override
    public void logout() {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            LoginUser loginUser = (LoginUser) authentication.getPrincipal();

            // 清除Redis中的用户信息
            if (loginUser != null && loginUser.getUser() != null) {
                String userId = loginUser.getUser().getUserId().toString();
                String userKey = Constants.LOGIN_USER_KEY + userId;
                redisTemplate.delete(userKey);
            }

            // 清除Security上下文
            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            log.error("退出登录异常: ", e);
        }
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息
     */
    @Override
    public UserInfoVO getCurrentUserInfo() {
        try {
            // 获取当前登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                throw new AuthException("用户未登录");
            }

            LoginUser loginUser = (LoginUser) authentication.getPrincipal();
            if (loginUser == null || loginUser.getUser() == null) {
                throw new AuthException("获取用户信息失败");
            }

            Long userId = loginUser.getUser().getUserId();
            
            // 从数据库获取最新的用户信息
            UserVO userVO = userService.getUserVOById(userId);
            List<String> roles = userService.getUserRoles(userId);
            List<String> permissions = userService.getUserPermissions(userId);
            
            // 构建UserInfoVO对象
            UserInfoVO userInfoVO = new UserInfoVO();
            userInfoVO.setUserInfo(userVO);
            userInfoVO.setRoles(roles);
            userInfoVO.setPermissions(permissions);
            
            return userInfoVO;
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取当前登录用户信息异常: ", e);
            throw new ServiceException("获取用户信息失败");
        }
    }

    /**
     * 验证码校验
     *
     * @param captchaId   验证码唯一标识
     * @param captchaCode 用户输入的验证码
     */
    private void validateCaptcha(String captchaId, String captchaCode) {
        // 如果未配置验证码，则跳过验证
        if (!Constants.CAPTCHA_ENABLED) {
            return;
        }

        if (!StringUtils.hasText(captchaId) || !StringUtils.hasText(captchaCode)) {
            throw new CaptchaException("验证码不能为空");
        }

        // 从Redis中获取验证码
        String captchaKey = Constants.CAPTCHA_CODE_KEY + captchaId;
        String correctCode = (String) redisTemplate.opsForValue().get(captchaKey);

        // 验证码已过期或不存在
        if (correctCode == null) {
            throw new CaptchaException("验证码已过期");
        }

        // 验证后删除验证码
        redisTemplate.delete(captchaKey);

        // 验证码错误
        if (!captchaCode.equalsIgnoreCase(correctCode)) {
            throw new CaptchaException("验证码错误");
        }
    }
} 