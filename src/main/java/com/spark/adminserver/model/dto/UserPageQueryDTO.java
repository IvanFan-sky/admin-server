package com.spark.adminserver.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询参数
 */
@Schema(description = "用户分页查询参数")
@Data
@EqualsAndHashCode(callSuper = false)
public class UserPageQueryDTO {

    // 假设继承通用的分页参数类或直接定义
    @Schema(description = "当前页码", defaultValue = "1")
    private Integer pageNum = 1; // 当前页码
    @Schema(description = "每页数量", defaultValue = "10")
    private Integer pageSize = 10; // 每页数量

    // 查询条件
    @Schema(description = "按用户名模糊查询", example = "john")
    private String username; // 按用户名模糊查询
    @Schema(description = "按手机号精确查询", example = "13800138000")
    private String phone;    // 按手机号精确查询
    @Schema(description = "按状态查询（0正常 1停用）", example = "0")
    private Integer status;  // 按状态查询

    // 可能需要的其他查询条件，例如日期范围
    // @Schema(description = "查询起始时间", example = "2023-01-01 00:00:00")
    // private String beginTime;
    // @Schema(description = "查询结束时间", example = "2023-12-31 23:59:59")
    // private String endTime;
} 