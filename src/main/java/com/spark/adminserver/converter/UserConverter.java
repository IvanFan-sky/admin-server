package com.spark.adminserver.converter;

import com.spark.adminserver.model.dto.UserDTO;
import com.spark.adminserver.model.entity.User;
import com.spark.adminserver.model.vo.UserVO;
import org.mapstruct.*;

import java.util.List;

/**
 * 用户对象转换器
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    /**
     * User 转换为 UserVO
     */
    UserVO userToUserVO(User user);

    /**
     * UserDTO 转换为 User
     * @param userDTO
     * @return
     */
    User userDTOToUser(UserDTO userDTO);

    /**
     * User 列表转换为 UserVO 列表
     */
    List<UserVO> usersToUserVOs(List<User> users);

    /**
     * 使用 UserDTO 更新 User 对象 (忽略 DTO 中的 null 值)
     * @param userDTO 用户DTO对象
     * @param user 待更新的用户对象
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "password", ignore = true) // 密码需要在服务层单独处理加密
    @Mapping(target = "deleted", ignore = true) // 逻辑删除标记不通过DTO更新
    @Mapping(target = "createTime", ignore = true) // 创建时间不应通过DTO更新
    @Mapping(target = "updateTime", ignore = true) // 更新时间由MybatisPlus自动处理
    void updateUserFromDTO(UserDTO userDTO, @MappingTarget User user);
} 