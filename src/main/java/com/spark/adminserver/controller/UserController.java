package com.spark.adminserver.controller;

import com.spark.adminserver.common.PageResult;
import com.spark.adminserver.common.Result;
import com.spark.adminserver.model.dto.UserDTO;
import com.spark.adminserver.model.dto.UserPageQueryDTO;
import com.spark.adminserver.model.vo.UserVO;
import com.spark.adminserver.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理接口
 */
@Tag(name = "用户管理", description = "用户信息的增删改查")
@RestController
@RequestMapping("/users")
public class UserController {

    @Resource
    private IUserService userService;

    @Operation(summary = "分页查询用户列表")
    @GetMapping("/page")
    public Result<PageResult<UserVO>> getUserPage(UserPageQueryDTO queryDTO) {
        PageResult<UserVO> userPage = userService.getUserPage(queryDTO);
        return Result.ok(userPage);
    }

    @Operation(summary = "根据ID获取用户信息")
    @GetMapping("/{id}")
    public Result<UserVO> getUserById(@Parameter(description = "用户ID") @PathVariable Long id) {
        UserVO userVO = userService.getUserVOById(id);
        if (userVO == null) {
            return Result.fail("用户不存在");
        }
        return Result.ok(userVO);
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public Result<Long> createUser(@Validated @RequestBody UserDTO userDTO) {
        try {
            Long userId = userService.createUser(userDTO);
            return Result.ok(userId, "用户创建成功");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }

    @Operation(summary = "修改用户信息")
    @PutMapping("/{id}")
    public Result<Void> updateUser(@Parameter(description = "用户ID") @PathVariable Long id,
                              @Validated @RequestBody UserDTO userDTO) {
        try {
            boolean success = userService.updateUser(id, userDTO);
            return success ? Result.ok(null, "用户更新成功") : Result.fail("用户更新失败");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@Parameter(description = "用户ID") @PathVariable Long id) {
        boolean success = userService.deleteUser(id);
        return success ? Result.ok(null, "用户删除成功") : Result.fail("用户删除失败");
    }

    @Operation(summary = "修改用户状态")
    @PutMapping("/{id}/status")
    @Parameters({
            @Parameter(name = "id", description = "用户ID", required = true),
            @Parameter(name = "status", description = "状态（0正常 1停用）", required = true)
    })
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            boolean success = userService.updateUserStatus(id, status);
            return success ? Result.ok(null, "状态更新成功") : Result.fail("状态更新失败");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }

    @Operation(summary = "重置用户密码")
    @PutMapping("/{id}/reset-password")
    @Parameters({
            @Parameter(name = "id", description = "用户ID", required = true),
            @Parameter(name = "newPassword", description = "新密码", required = true)
    })
    public Result<Void> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
         try {
            boolean success = userService.resetPassword(id, newPassword);
            return success ? Result.ok(null, "密码重置成功") : Result.fail("密码重置失败");
        } catch (IllegalArgumentException e) {
            return Result.fail(e.getMessage());
        }
    }

    // TODO: 添加用户分配角色接口
    // @PutMapping("/{id}/roles")

} 