package com.spark.adminserver.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 当前登录用户信息视图对象
 */
@Schema(description = "当前登录用户信息视图对象")
@Data
public class UserInfoVO {
    
    @Schema(description = "用户基本信息")
    private UserVO userInfo;
    
    @Schema(description = "用户角色标识列表", example = "[\"superadmin\", \"user\"]")
    private List<String> roles;
    
    @Schema(description = "用户权限标识列表", example = "[\"system:user:list\", \"system:user:add\"]")
    private List<String> permissions;
} 