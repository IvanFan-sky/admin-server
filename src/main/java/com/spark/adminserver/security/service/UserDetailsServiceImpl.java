package com.spark.adminserver.security.service;

import com.spark.adminserver.model.entity.User;
import com.spark.adminserver.service.IUserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户详情服务实现类
 */
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    
    @Resource
    private IUserService userService;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            // 根据用户名获取用户
            User user = userService.getByUsername(username);
            if (user == null) {
                log.error("用户不存在：{}", username);
                throw new UsernameNotFoundException("用户名或密码错误");
            }
            
            // 判断用户状态
            if (user.getStatus() != null && user.getStatus() == 1) {
                log.error("用户已被禁用：{}", username);
                throw new UsernameNotFoundException("账号已被禁用");
            }
            
            // 获取用户权限
            List<String> permissions = userService.getUserPermissions(user.getUserId());
            
            // 转换为SimpleGrantedAuthority
            List<SimpleGrantedAuthority> authorities = permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            
            // 返回UserDetails
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    authorities
            );
        } catch (Exception e) {
            // 如果不是UsernameNotFoundException，则包装一下
            if (!(e instanceof UsernameNotFoundException)) {
                log.error("获取用户信息异常", e);
                throw new UsernameNotFoundException("获取用户信息异常");
            }
            throw e;
        }
    }
    
    /**
     * 根据用户ID加载用户
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    public UserDetails loadUserById(Long userId) {
        try {
            // 根据ID获取用户
            User user = userService.getById(userId);
            if (user == null) {
                log.error("用户不存在：ID={}", userId);
                throw new UsernameNotFoundException("无效的令牌");
            }
            
            // 判断用户状态
            if (user.getStatus() != null && user.getStatus() == 1) {
                log.error("用户已被禁用：ID={}", userId);
                throw new UsernameNotFoundException("账号已被禁用");
            }
            
            // 获取用户权限
            List<String> permissions = userService.getUserPermissions(user.getUserId());
            
            // 转换为SimpleGrantedAuthority
            List<SimpleGrantedAuthority> authorities = permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            
            // 返回UserDetails
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    authorities
            );
        } catch (Exception e) {
            // 如果不是UsernameNotFoundException，则包装一下
            if (!(e instanceof UsernameNotFoundException)) {
                log.error("获取用户信息异常", e);
                throw new UsernameNotFoundException("获取用户信息异常");
            }
            throw e;
        }
    }
} 