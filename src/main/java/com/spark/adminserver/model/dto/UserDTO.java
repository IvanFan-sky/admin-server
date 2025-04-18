package com.spark.adminserver.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 用户数据传输对象 (用于创建/更新请求)
 */
@Schema(description = "用户数据传输对象 (用于创建/更新请求)")
@Data
public class UserDTO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在 3 到 20 个字符之间")
    @Schema(description = "用户名 (登录账号)", requiredMode = Schema.RequiredMode.REQUIRED, example = "john.doe")
    private String username;

    // 创建用户时需要密码，更新时可选
    // 使用分组校验或在 service 层处理密码逻辑
    @Schema(description = "密码 (创建时必填，更新时可选，不填则不修改)", example = "password123")
    private String password;

    @Size(max = 50, message = "昵称长度不能超过 50 个字符")
    @Schema(description = "用户昵称", example = "Johnny")
    private String nickname;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过 100 个字符")
    @Schema(description = "用户邮箱", example = "john.doe@example.com")
    private String email;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号码格式不正确")
    @Size(max = 11, message = "手机号码长度不能超过 11 个字符")
    @Schema(description = "手机号码", example = "13800138000")
    private String phone;

    // 状态通常不由前端直接指定，或通过特定接口修改
    // private Integer status;

    // 关联的角色ID列表，用于创建/更新时设置
    @Schema(description = "关联的角色ID列表")
    private List<Long> roleIds;
} 