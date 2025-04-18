package com.spark.adminserver.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 令牌视图对象
 */
@Schema(description = "令牌视图对象")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenVO {
    
    @Schema(description = "访问令牌", example = "jwt.token.string")
    private String accessToken;
    
    @Schema(description = "刷新令牌", example = "jwt.refresh.token.string")
    private String refreshToken;
    
    @Schema(description = "令牌有效期（秒）", example = "7200")
    private Integer expiresIn;
} 