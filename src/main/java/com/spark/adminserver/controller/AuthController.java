package com.spark.adminserver.controller;

import com.spark.adminserver.common.Result;
import com.spark.adminserver.model.dto.LoginDTO;
import com.spark.adminserver.model.vo.CaptchaVO;
import com.spark.adminserver.model.vo.TokenVO;
import com.spark.adminserver.model.vo.UserInfoVO;
import com.spark.adminserver.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "AuthController", description = "认证授权")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final IAuthService authService;
    
    @Operation(summary = "获取图形验证码", description = "获取用于登录验证的图形验证码")
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CaptchaVO.class)))
    @GetMapping("/captcha")
    public Result<CaptchaVO> getCaptcha() {
        CaptchaVO captchaVO = authService.generateCaptcha();
        return Result.ok(captchaVO, "获取成功");
    }
    
    @Operation(summary = "用户登录", description = "使用用户名/邮箱/手机号和密码进行登录，获取身份认证Token")
    @ApiResponse(responseCode = "200", description = "登录成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenVO.class)))
    @ApiResponse(responseCode = "400", description = "参数校验失败")
    @ApiResponse(responseCode = "401", description = "认证失败")
    @PostMapping("/login")
    public Result<TokenVO> login(@Validated @RequestBody LoginDTO loginDTO) {
        TokenVO tokenVO = authService.login(loginDTO);
        return Result.ok(tokenVO, "登录成功");
    }
    
    @Operation(summary = "用户登出", description = "退出登录，使当前用户的Token失效")
    @ApiResponse(responseCode = "200", description = "登出成功")
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.ok(null, "登出成功");
    }
    
    @Operation(summary = "获取当前登录用户信息", description = "获取当前已认证用户的基本信息、角色和权限")
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserInfoVO.class)))
    @ApiResponse(responseCode = "401", description = "未登录")
    @GetMapping("/me")
    public Result<UserInfoVO> getCurrentUserInfo() {
        UserInfoVO userInfoVO = authService.getCurrentUserInfo();
        return Result.ok(userInfoVO, "查询成功");
    }
} 