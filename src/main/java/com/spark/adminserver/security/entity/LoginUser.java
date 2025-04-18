package com.spark.adminserver.security.entity;

import com.spark.adminserver.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 登录用户实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUser implements UserDetails {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户信息
     */
    private User user;
    
    /**
     * 权限列表
     */
    private Set<String> permissions;
    
    /**
     * 角色列表
     */
    private List<String> roles;
    
    /**
     * 构造函数
     *
     * @param user  用户信息
     */
    public LoginUser(User user) {
        this.user = user;
    }
    
    /**
     * 获取权限列表
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (permissions == null || permissions.isEmpty()) {
            return List.of();
        }
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取密码
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    
    /**
     * 获取用户名
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }
    
    /**
     * 账号是否未过期
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    /**
     * 账号是否未锁定
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    /**
     * 凭证是否未过期
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    /**
     * 账号是否启用
     */
    @Override
    public boolean isEnabled() {
        return user.getStatus() == 0;
    }
} 