package com.spark.adminserver.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    /**
     * 创建JWT token
     *
     * @param subject 主题（通常是用户ID）
     * @param claims 自定义声明信息
     * @param expirationMillis 过期时间（毫秒）
     * @return JWT token
     */
    public String createToken(String subject, Map<String, Object> claims, long expirationMillis) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMillis);
        
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        
        JwtBuilder builder = Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate);
        
        if (claims != null) {
            claims.forEach(builder::claim);
        }
        
        return builder
                .signWith(key)
                .compact();
    }
    
    /**
     * 创建访问令牌
     *
     * @param userId 用户ID
     * @param claims 自定义声明信息
     * @return 访问令牌
     */
    public String createAccessToken(String userId, Map<String, Object> claims) {
        return createToken(userId, claims, expiration);
    }
    
    /**
     * 创建刷新令牌
     *
     * @param userId 用户ID
     * @return 刷新令牌
     */
    public String createRefreshToken(String userId) {
        return createToken(userId, null, refreshExpiration);
    }
    
    /**
     * 解析JWT token
     *
     * @param token JWT token
     * @return 声明信息
     */
    public Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    /**
     * 验证令牌是否有效
     *
     * @param token JWT token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            log.error("JWT token验证失败：", e);
            return false;
        }
    }
    
    /**
     * 从令牌中获取用户ID
     *
     * @param token JWT token
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }
} 