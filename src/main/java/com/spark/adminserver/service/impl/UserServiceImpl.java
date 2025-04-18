package com.spark.adminserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.adminserver.common.PageResult;
import com.spark.adminserver.converter.UserConverter;
import com.spark.adminserver.mapper.UserMapper;
import com.spark.adminserver.model.dto.UserDTO;
import com.spark.adminserver.model.dto.UserPageQueryDTO;
import com.spark.adminserver.model.entity.User;
import com.spark.adminserver.model.vo.UserVO;
import com.spark.adminserver.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserMapper userMapper;
    private final UserConverter userConverter;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserVO getUserVOById(Long userId) {
        User user = this.getById(userId);
        if (user == null || user.getDeleted() == 1) {
            return null;
        }
        return userConverter.userToUserVO(user);
        // TODO: 查询并设置用户的角色信息到 userVO
    }

    @Override
    public PageResult<UserVO> getUserPage(UserPageQueryDTO queryDTO) {
        // 创建分页对象
        Page<User> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        // 执行分页查询
        Page<User> userPage = userMapper.selectUserPage(page, queryDTO);
        
        // 转换结果
        List<UserVO> userVOList = userConverter.usersToUserVOs(userPage.getRecords());
        // TODO: 查询并设置用户的角色信息

        return new PageResult<>(userPage.getTotal(), userVOList);
    }

    @Override
    @Transactional
    public Long createUser(UserDTO userDTO) {
        // 检查用户名是否已存在
        if (checkUsernameExists(userDTO.getUsername(), null)) {
            throw new IllegalArgumentException("用户名已存在");
        }
        // 检查邮箱是否已存在 (如果邮箱非空)
        if (StringUtils.isNotBlank(userDTO.getEmail()) && checkEmailExists(userDTO.getEmail(), null)) {
            throw new IllegalArgumentException("邮箱已存在");
        }
        // 检查手机号是否已存在 (如果手机号非空)
        if (StringUtils.isNotBlank(userDTO.getPhone()) && checkPhoneExists(userDTO.getPhone(), null)) {
            throw new IllegalArgumentException("手机号已存在");
        }

        // 使用 MapStruct 转换 DTO 到实体
        User user = userConverter.userDTOToUser(userDTO);

        // 设置默认状态和密码加密
        user.setStatus(0); // 默认正常状态
        if (StringUtils.isNotBlank(userDTO.getPassword())) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        } else {
            // 可以设置默认密码，或要求密码必填
            throw new IllegalArgumentException("创建用户时密码不能为空");
        }

        // 使用 MybatisPlus 的 save 方法插入
        this.save(user);

        // TODO: 处理用户与角色的关联关系 (userDTO.getRoleIds())

        return user.getUserId();
    }

    @Override
    @Transactional
    public boolean updateUser(Long userId, UserDTO userDTO) {
        User existingUser = this.getById(userId);
        if (existingUser == null || existingUser.getDeleted() == 1) {
            throw new IllegalArgumentException("用户不存在或已被删除");
        }

        // 检查用户名是否冲突 (排除自身)
        if (StringUtils.isNotBlank(userDTO.getUsername()) && !Objects.equals(existingUser.getUsername(), userDTO.getUsername()) && checkUsernameExists(userDTO.getUsername(), userId)) {
            throw new IllegalArgumentException("用户名已存在");
        }
        // 检查邮箱是否冲突 (排除自身)
        if (StringUtils.isNotBlank(userDTO.getEmail()) && !Objects.equals(existingUser.getEmail(), userDTO.getEmail()) && checkEmailExists(userDTO.getEmail(), userId)) {
            throw new IllegalArgumentException("邮箱已存在");
        }
        // 检查手机号是否冲突 (排除自身)
        if (StringUtils.isNotBlank(userDTO.getPhone()) && !Objects.equals(existingUser.getPhone(), userDTO.getPhone()) && checkPhoneExists(userDTO.getPhone(), userId)) {
            throw new IllegalArgumentException("手机号已存在");
        }

        // 创建要更新的用户对象
        User userToUpdate = new User();
        userToUpdate.setUserId(userId);
        
        // 使用 MapStruct 更新对象，忽略 null 值
        userConverter.updateUserFromDTO(userDTO, userToUpdate);

        // 密码处理：如果 DTO 中密码非空，则更新密码
        if (StringUtils.isNotBlank(userDTO.getPassword())) {
            userToUpdate.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        // 使用 MybatisPlus 的 updateById 方法更新 (null 值字段不会被更新)
        boolean success = this.updateById(userToUpdate);

        // TODO: 处理用户与角色的关联关系 (userDTO.getRoleIds())
        // 可能需要先删除旧关联，再插入新关联

        return success;
    }

    @Override
    @Transactional // 涉及写操作，添加事务
    public boolean deleteUser(Long userId) {
        // 逻辑删除，使用 MybatisPlus 的 removeById
        // ServiceImpl<M, T> 默认实现了逻辑删除
        return this.removeById(userId);
        // 如果需要物理删除，则需要调用 mapper.deleteById(userId);
    }

    @Override
    @Transactional
    public boolean updateUserStatus(Long userId, Integer status) {
        User user = this.getById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new IllegalArgumentException("用户不存在或已被删除");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new IllegalArgumentException("无效的状态值");
        }

        User userToUpdate = new User();
        userToUpdate.setUserId(userId);
        userToUpdate.setStatus(status);
        return this.updateById(userToUpdate);
    }

    @Override
    @Transactional
    public boolean resetPassword(Long userId, String newPassword) {
        User user = this.getById(userId);
        if (user == null || user.getDeleted() == 1) {
            throw new IllegalArgumentException("用户不存在或已被删除");
        }
        if (StringUtils.isBlank(newPassword)) {
            throw new IllegalArgumentException("新密码不能为空");
        }

        User userToUpdate = new User();
        userToUpdate.setUserId(userId);
        userToUpdate.setPassword(passwordEncoder.encode(newPassword));
        return this.updateById(userToUpdate);
    }

    @Override
    public User getByUsername(String username) {
        return null;
    }

    @Override
    public List<String> getUserRoles(Long userId) {
        return List.of();
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        return List.of();
    }

    // --- 辅助方法 --- //

    private boolean checkUsernameExists(String username, Long excludeUserId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        if (excludeUserId != null) {
            queryWrapper.ne(User::getUserId, excludeUserId);
        }
        return userMapper.exists(queryWrapper);
    }

    private boolean checkEmailExists(String email, Long excludeUserId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getEmail, email);
        if (excludeUserId != null) {
            queryWrapper.ne(User::getUserId, excludeUserId);
        }
        return userMapper.exists(queryWrapper);
    }

    private boolean checkPhoneExists(String phone, Long excludeUserId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, phone);
        if (excludeUserId != null) {
            queryWrapper.ne(User::getUserId, excludeUserId);
        }
        return userMapper.exists(queryWrapper);
    }
} 