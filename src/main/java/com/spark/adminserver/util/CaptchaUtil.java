package com.spark.adminserver.util;

import com.wf.captcha.*;
import com.wf.captcha.base.Captcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;
import java.awt.FontFormatException;

/**
 * 验证码工具类 - 使用EasyCaptcha实现
 */
@Component
@Slf4j
public class CaptchaUtil {
    
    private final Random random = new Random();
    
    @Value("${captcha.type:arithmetic}")
    private String captchaType;
    
    @Value("${captcha.length:4}")
    private Integer length;
    
    @Value("${captcha.width:130}")
    private Integer width;
    
    @Value("${captcha.height:48}")
    private Integer height;
    
    /**
     * 生成验证码
     *
     * @return 包含验证码文本和图片的数组，第一个元素是验证码文本，第二个元素是Base64编码的图片
     */
    public String[] generateCaptcha() {
        // 创建验证码
        Captcha captcha = createCaptcha();
        
        // 转为Base64编码
        String base64 = captchaToBase64(captcha);
        
        // 返回验证码文本和图片
        return new String[] {captcha.text(), base64};
    }
    
    /**
     * 创建不同类型的验证码
     *
     * @return 验证码实例
     */
    private Captcha createCaptcha() {
        Captcha captcha;
        
        switch (captchaType.toLowerCase()) {
            case "gif":
                // 动态GIF验证码
                captcha = new GifCaptcha(width, height, length);
                break;
            case "spec":
                // 滑块验证码
                captcha = new SpecCaptcha(width, height, length);
                break;
            case "chinese":
                // 中文验证码
                captcha = new ChineseCaptcha(width, height, length);
                break;
            case "chinese_gif":
                // 中文动态验证码
                captcha = new ChineseGifCaptcha(width, height, length);
                break;
            case "arithmetic":
            default:
                // 算术验证码
                captcha = new ArithmeticCaptcha(width, height);
                ((ArithmeticCaptcha) captcha).setLen(length);
                break;
        }
        
        try {
            // 设置字体
            captcha.setFont(Captcha.FONT_1);
            // 设置字体颜色
            captcha.setCharType(Captcha.TYPE_DEFAULT);
        } catch (IOException | FontFormatException e) {
            log.error("设置验证码字体失败", e);
            // 可以尝试使用默认字体
        }
        
        return captcha;
    }
    
    /**
     * 将验证码转换为Base64编码
     *
     * @param captcha 验证码
     * @return Base64编码的图片
     */
    private String captchaToBase64(Captcha captcha) {
        String base64 = captcha.toBase64();
        if (!base64.startsWith("data:")) {
            base64 = "data:image/png;base64," + base64;
        }
        return base64;
    }
} 