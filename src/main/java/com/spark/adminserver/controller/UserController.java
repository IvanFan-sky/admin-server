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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理接口
 */
@Tag(name = "UserController", description = "用户管理")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @Operation(summary = "分页查询用户列表", description = "根据条件分页获取用户列表信息")
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResult.class)))
    @GetMapping("/page")
    public Result<PageResult<UserVO>> getUserPage(@Validated UserPageQueryDTO queryDTO) {
        PageResult<UserVO> userPage = userService.getUserPage(queryDTO);
        return Result.ok(userPage);
    }

    @Operation(summary = "根据ID获取用户信息", description = "获取指定ID的用户详细信息")
    @ApiResponse(responseCode = "200", description = "成功", content = @Content(schema = @Schema(implementation = UserVO.class)))
    @ApiResponse(responseCode = "404", description = "用户不存在")
    @GetMapping("/{id}")
    public Result<UserVO> getUserById(
            @Parameter(description = "用户ID", required = true, example = "1") @PathVariable Long id) {
        UserVO userVO = userService.getUserVOById(id);
        if (userVO == null) {
            return Result.fail(404, "用户不存在");
        }
        return Result.ok(userVO);
    }

    @Operation(summary = "新增用户", description = "创建一个新用户")
    @ApiResponse(responseCode = "200", description = "创建成功", content = @Content(schema = @Schema(type = "long", example = "1")))
    @ApiResponse(responseCode = "400", description = "参数校验失败或业务逻辑错误 (如用户名已存在)")
    @PostMapping
    public Result<Long> createUser(@Validated @RequestBody UserDTO userDTO) {
        Long userId = userService.createUser(userDTO);
        return Result.ok(userId, "用户创建成功");
    }

    @Operation(summary = "修改用户信息", description = "更新指定ID的用户信息")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @ApiResponse(responseCode = "400", description = "参数校验失败或业务逻辑错误 (如用户名已存在)")
    @ApiResponse(responseCode = "404", description = "用户不存在")
    @PutMapping("/{id}")
    public Result<Void> updateUser(
            @Parameter(description = "用户ID", required = true, example = "1") @PathVariable Long id,
            @Validated @RequestBody UserDTO userDTO) {
        userService.updateUser(id, userDTO);
        return Result.ok(null, "用户更新成功");
    }

    @Operation(summary = "删除用户", description = "逻辑删除指定ID的用户")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @ApiResponse(responseCode = "404", description = "用户不存在")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID", required = true, example = "1") @PathVariable Long id) {
        userService.deleteUser(id);
        return Result.ok(null, "用户删除成功");
    }

    @Operation(summary = "修改用户状态", description = "启用或禁用指定ID的用户")
    @Parameters({
            @Parameter(name = "id", description = "用户ID", required = true, example = "1"),
            @Parameter(name = "status", description = "状态（0启用 1禁用）", required = true, schema = @Schema(type = "integer", allowableValues = {"0", "1"}))
    })
    @ApiResponse(responseCode = "200", description = "状态更新成功")
    @ApiResponse(responseCode = "400", description = "无效的状态值")
    @ApiResponse(responseCode = "404", description = "用户不存在")
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return Result.ok(null, "状态更新成功");
    }

    @Operation(summary = "重置用户密码", description = "重置指定ID用户的密码")
    @Parameters({
            @Parameter(name = "id", description = "用户ID", required = true, example = "1"),
            @Parameter(name = "newPassword", description = "新密码", required = true, example = "newPwd123")
    })
    @ApiResponse(responseCode = "200", description = "密码重置成功")
    @ApiResponse(responseCode = "400", description = "新密码不能为空")
    @ApiResponse(responseCode = "404", description = "用户不存在")
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        userService.resetPassword(id, newPassword);
        return Result.ok(null, "密码重置成功");
    }

    // TODO: 添加用户分配角色接口
    // @PutMapping("/{id}/roles")

} 