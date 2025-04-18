package com.spark.adminserver.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码视图对象
 */
@Schema(description = "验证码视图对象")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaVO {
    
    @Schema(description = "验证码ID", example = "uuid-string")
    private String captchaId;
    
    @Schema(description = "Base64编码的验证码图片", example = "data:image/png;base64,...")
    private String captchaImage;
} 