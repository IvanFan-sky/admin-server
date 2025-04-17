package com.spark.adminserver.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询参数
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserPageQueryDTO {

    // 假设继承通用的分页参数类或直接定义
    private Integer pageNum = 1; // 当前页码
    private Integer pageSize = 10; // 每页数量

    // 查询条件
    private String username; // 按用户名模糊查询
    private String phone;    // 按手机号精确查询
    private Integer status;  // 按状态查询

    // 可能需要的其他查询条件，例如日期范围
    // private String beginTime;
    // private String endTime;
} 