package com.spark.adminserver.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MybatisPlus 字段自动填充处理器
 */
@Component
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    private static final Logger log = LoggerFactory.getLogger(MybatisPlusMetaObjectHandler.class);

    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("start insert fill ....");
        // strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now()); // 起始版本 3.3.0(推荐)
        // 或者
        // setFieldValByName(metaObject, "createTime", LocalDateTime.now(), Class<?>); // 旧版本方式
        this.strictInsertFill(metaObject, "createTime", () -> LocalDateTime.now(), LocalDateTime.class); // 更兼容的方式
        this.strictInsertFill(metaObject, "updateTime", () -> LocalDateTime.now(), LocalDateTime.class);

        // 如果有 createBy, updateBy 字段，也在此处填充
        // this.strictInsertFill(metaObject, "createBy", () -> getCurrentUsername(), String.class);
        // this.strictInsertFill(metaObject, "updateBy", () -> getCurrentUsername(), String.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("start update fill ....");
        // strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now()); // 起始版本 3.3.0(推荐)
        // 或者
        // setFieldValByName(metaObject, "updateTime", LocalDateTime.now(), Class<?>); // 旧版本方式
        this.strictUpdateFill(metaObject, "updateTime", () -> LocalDateTime.now(), LocalDateTime.class); // 更兼容的方式

        // this.strictUpdateFill(metaObject, "updateBy", () -> getCurrentUsername(), String.class);
    }

    /*
     * 获取当前登录用户名的方法 (示例)
     *
    private String getCurrentUsername() {
        // 这里需要根据你的 Spring Security 配置获取当前认证的用户信息
        // 例如:
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
        //     if (authentication.getPrincipal() instanceof UserDetails) {
        //         return ((UserDetails) authentication.getPrincipal()).getUsername();
        //     } else {
        //         return authentication.getPrincipal().toString();
        //     }
        // }
        return "system"; // 或者返回默认值
    }
    */
} 