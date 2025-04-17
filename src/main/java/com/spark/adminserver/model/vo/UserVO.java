package com.spark.adminserver.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户视图对象 (用于API响应)
 */
@Schema(description = "用户视图对象 (API响应)")
@Data
public class UserVO {

    @Schema(description = "用户ID", example = "173658927349872634")
    private Long userId;

    @Schema(description = "用户名", example = "john.doe")
    private String username;

    @Schema(description = "用户昵称", example = "Johnny")
    private String nickname;

    @Schema(description = "用户邮箱", example = "john.doe@example.com")
    private String email;

    @Schema(description = "手机号码", example = "13800138000")
    private String phone;

    @Schema(description = "用户状态（0正常 1停用）", example = "0")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    // @Schema(description = "用户关联的角色名称列表")
    // private List<String> roleNames;
}