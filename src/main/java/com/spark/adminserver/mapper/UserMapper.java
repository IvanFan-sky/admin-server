package com.spark.adminserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.adminserver.model.entity.User;
import com.spark.adminserver.model.dto.UserPageQueryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 用户表数据访问层
 */
@Repository // 遵循 java.mdc
// @UsingDefaultDB // 如果有此注解，也需要加上 (根据 java.mdc)
@Mapper // 确保 Spring Boot 能扫描到
public interface UserMapper extends BaseMapper<User> {

    /**
     * 分页查询用户信息 (使用 MybatisPlus 分页)
     *
     * @param page 分页对象
     * @param query 查询条件 DTO
     * @return 分页用户列表
     */
    Page<User> selectUserPage(Page<User> page, @Param("query") UserPageQueryDTO query);

    // 后续可以根据需要添加其他自定义查询方法，例如：
    // User selectUserWithRoles(Long userId);

} 