package com.spark.adminserver.model.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户视图对象 (用于API响应)
 */
@Data
public class UserVO {

    private Long userId;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    // 可以添加关联的角色信息等
    // private List<String> roleNames;
} 