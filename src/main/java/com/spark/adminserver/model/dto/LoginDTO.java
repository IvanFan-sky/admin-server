package com.spark.adminserver.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录数据传输对象
 */
@Schema(description = "用户登录数据传输对象")
@Data
public class LoginDTO {

    @NotBlank(message = "登录账号不能为空")
    @Schema(description = "登录账号（用户名/邮箱/手机号）", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin")
    private String principal;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "encrypted_password_string")
    private String password;
    
    @NotBlank(message = "验证码ID不能为空")
    @Schema(description = "验证码ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "uuid-string-from-captcha-api")
    private String captchaId;
    
    @NotBlank(message = "验证码不能为空")
    @Schema(description = "验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "abcd")
    private String captchaCode;
} 