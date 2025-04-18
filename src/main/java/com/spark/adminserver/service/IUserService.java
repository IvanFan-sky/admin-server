package com.spark.adminserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.adminserver.common.PageResult;
import com.spark.adminserver.model.entity.User;
import com.spark.adminserver.model.dto.UserDTO;
import com.spark.adminserver.model.dto.UserPageQueryDTO;
import com.spark.adminserver.model.vo.UserVO;

import java.util.List;

/**
 * 用户服务接口
 */
public interface IUserService extends IService<User> {

    /**
     * 根据ID获取用户视图对象
     *
     * @param userId 用户ID
     * @return 用户视图对象，如果不存在则返回 null
     */
    UserVO getUserVOById(Long userId);

    /**
     * 分页查询用户
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    PageResult<UserVO> getUserPage(UserPageQueryDTO queryDTO);

    /**
     * 创建用户
     *
     * @param userDTO 用户数据
     * @return 创建后的用户ID
     */
    Long createUser(UserDTO userDTO);

    /**
     * 更新用户
     *
     * @param userId 用户ID
     * @param userDTO 用户数据
     * @return 是否成功
     */
    boolean updateUser(Long userId, UserDTO userDTO);

    /**
     * 删除用户 (逻辑删除)
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteUser(Long userId);

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 新状态
     * @return 是否成功
     */
    boolean updateUserStatus(Long userId, Integer status);

    /**
     * 重置用户密码
     *
     * @param userId 用户ID
     * @param newPassword 新密码 (明文，将在服务层加密)
     * @return 是否成功
     */
    boolean resetPassword(Long userId, String newPassword);

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    User getByUsername(String username);

    /**
     * 获取用户角色
     *
     * @param userId 用户ID
     * @return 角色标识列表
     */
    List<String> getUserRoles(Long userId);

    /**
     * 获取用户权限
     *
     * @param userId 用户ID
     * @return 权限标识列表
     */
    List<String> getUserPermissions(Long userId);

    // TODO: 添加更新用户角色等方法
    // boolean updateUserRoles(Long userId, List<Long> roleIds);
} 