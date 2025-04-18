package com.spark.adminserver.security.filter;

import com.spark.adminserver.model.entity.User;
import com.spark.adminserver.security.service.UserDetailsServiceImpl;
import com.spark.adminserver.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Resource
    private JwtUtil jwtUtil;
    
    @Resource
    private UserDetailsServiceImpl userDetailsService;
    
    /**
     * Token前缀
     */
    private static final String TOKEN_PREFIX = "Bearer ";
    
    /**
     * 请求头名称
     */
    private static final String HEADER_NAME = "Authorization";
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // 获取token
            String token = getTokenFromRequest(request);
            
            // 验证token
            if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
                // 从token中获取用户ID
                String userId = jwtUtil.getUserIdFromToken(token);
                
                // 加载用户信息
                UserDetails userDetails = userDetailsService.loadUserById(Long.valueOf(userId));
                
                // 创建认证信息
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 设置到SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("无法设置用户认证：{}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * 从请求中获取token
     *
     * @param request HTTP请求
     * @return JWT token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(HEADER_NAME);
        if (StringUtils.hasText(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }
        return null;
    }
} 