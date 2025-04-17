package com.spark.adminserver.converter;

import com.spark.adminserver.model.dto.UserDTO;
import com.spark.adminserver.model.entity.User;
import com.spark.adminserver.model.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户对象转换器
 */
@Mapper(componentModel = "spring") // 让 Spring 能够扫描并注入
public interface UserConverter {

    UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

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
     * 注意：MapStruct 默认行为是覆盖，需要自定义或使用 @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
     * 但对于密码等字段，通常需要特殊处理，建议在 Service 层处理
     */
    // void updateUserFromDTO(UserDTO userDTO, @MappingTarget User user);

} 