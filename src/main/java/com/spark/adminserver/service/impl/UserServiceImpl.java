package com.spark.adminserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
// import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // Removed for PageHelper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.spark.adminserver.common.PageResult;
import com.spark.adminserver.converter.UserConverter; // Import the converter
import com.spark.adminserver.mapper.UserMapper;
import com.spark.adminserver.model.dto.UserDTO;
import com.spark.adminserver.model.dto.UserPageQueryDTO;
import com.spark.adminserver.model.entity.User;
import com.spark.adminserver.model.vo.UserVO;
import com.spark.adminserver.service.IUserService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.springframework.beans.BeanUtils; // No longer needed
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
// import java.util.stream.Collectors; // No longer needed for basic conversion

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserConverter userConverter; // Inject UserConverter

    // PasswordEncoder Bean 需要在 SecurityConfig 中配置
    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public UserVO getUserVOById(Long userId) {
        User user = this.getById(userId);
        if (user == null || user.getDeleted() == 1) {
            return null;
        }
        UserVO userVO = userConverter.userToUserVO(user); // Use MapStruct
        // TODO: 查询并设置用户的角色信息到 userVO
        return userVO;
    }

    @Override
    public PageResult<UserVO> getUserPage(UserPageQueryDTO queryDTO) {
        // 1. 设置分页参数 (PageHelper)
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 2. 执行查询 (注意：紧跟 PageHelper.startPage 后面的第一个 Mybatis 查询会被分页)
        List<User> userList = userMapper.selectUserPage(queryDTO);

        // 3. 封装 PageInfo 对象 (获取分页信息)
        PageInfo<User> pageInfo = new PageInfo<>(userList);

        // 4. 转换 User 列表为 UserVO 列表 (Placeholder, MapStruct will handle this)
        List<UserVO> userVOList = userConverter.usersToUserVOs(pageInfo.getList()); // Use MapStruct
        // userVOList = userConverter.usersToUserVOs(pageInfo.getList()); // MapStruct usage

        // 5. 封装 PageResult 返回
        return new PageResult<>(pageInfo.getTotal(), userVOList);
    }

    @Override
    @Transactional // 涉及写操作，添加事务
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

        User user = userConverter.userDTOToUser(userDTO); // Use MapStruct

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
    @Transactional // 涉及写操作，添加事务
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

        User userToUpdate = userConverter.userDTOToUser(userDTO); // Use MapStruct
        userToUpdate.setUserId(userId); // 确保 ID 被设置

        // 密码处理：如果 DTO 中密码非空，则更新密码，否则保持原密码
        if (StringUtils.isNotBlank(userDTO.getPassword())) {
            userToUpdate.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        } else {
            userToUpdate.setPassword(null); // Prevent MapStruct from overwriting with null if password wasn't in DTO
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