package com.smartauto.oa.system.controller;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.common.Result;
import com.smartauto.oa.common.SecurityUtils;
import com.smartauto.oa.system.annotation.OperLog;
import com.smartauto.oa.system.entity.SysUser;
import com.smartauto.oa.system.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 用户管理
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    private final ISysUserService userService;

    public SysUserController(ISysUserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "分页查询用户列表")
    @GetMapping
    public Result<PageResult<SysUser>> page(SysUser query,
                                             @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
                                             @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<SysUser> result = userService.page(query, pageNum, pageSize);
        return Result.success(result);
    }

    @Operation(summary = "根据ID查询用户")
    @GetMapping("/{id}")
    public Result<SysUser> getById(@Parameter(description = "用户ID", required = true) @PathVariable Long id) {
        SysUser user = userService.getById(id);
        return Result.success(user);
    }

    @OperLog(title = "新增用户", businessType = "INSERT")
    @Operation(summary = "新增用户")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody SysUser user) {
        userService.create(user);
        return Result.success();
    }

    @OperLog(title = "修改用户", businessType = "UPDATE")
    @Operation(summary = "修改用户")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody SysUser user) {
        userService.update(user);
        return Result.success();
    }

    @OperLog(title = "删除用户", businessType = "DELETE")
    @Operation(summary = "删除用户")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@Parameter(description = "用户ID列表", required = true) @PathVariable List<Long> ids) {
        userService.delete(ids);
        return Result.success();
    }

    @OperLog(title = "修改用户状态", businessType = "UPDATE")
    @Operation(summary = "修改用户状态")
    @PutMapping("/status")
    public Result<Void> updateStatus(@Parameter(description = "用户ID", required = true) @RequestParam Long id,
                                      @Parameter(description = "状态", required = true) @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success();
    }

    @OperLog(title = "重置密码", businessType = "UPDATE")
    @Operation(summary = "重置密码")
    @PutMapping("/resetPwd")
    public Result<Void> resetPwd(@Parameter(description = "用户ID", required = true) @RequestParam Long id,
                                  @Parameter(description = "新密码", required = true) @RequestParam String password) {
        userService.resetPwd(id, password);
        return Result.success();
    }

    @OperLog(title = "导出用户", businessType = "EXPORT")
    @Operation(summary = "导出用户列表")
    @GetMapping("/export")
    public Result<List<SysUser>> export(SysUser query) {
        List<SysUser> list = userService.exportUsers(query);
        return Result.success(list);
    }

    @OperLog(title = "导入用户", businessType = "IMPORT")
    @Operation(summary = "导入用户")
    @PostMapping("/import")
    public Result<String> importUsers(@Parameter(description = "用户数据", required = true) @RequestBody List<SysUser> users) {
        String msg = userService.importUsers(users);
        return Result.success(msg);
    }

    @OperLog(title = "上传头像", businessType = "UPDATE")
    @Operation(summary = "上传头像")
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@Parameter(description = "头像文件", required = true) @RequestParam("file") MultipartFile file) {
        Long userId = SecurityUtils.getUserId();
        String avatarUrl = userService.uploadAvatar(userId, file);
        return Result.success("上传成功", avatarUrl);
    }

    @OperLog(title = "修改密码", businessType = "UPDATE")
    @Operation(summary = "修改密码")
    @PutMapping("/changePwd")
    public Result<Void> changePassword(@RequestBody Map<String, String> params) {
        Long userId = SecurityUtils.getUserId();
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        userService.changePassword(userId, oldPassword, newPassword);
        return Result.success();
    }

    @Operation(summary = "获取个人信息")
    @GetMapping("/profile")
    public Result<SysUser> getProfile() {
        Long userId = SecurityUtils.getUserId();
        SysUser user = userService.getProfile(userId);
        return Result.success(user);
    }

    @OperLog(title = "修改个人信息", businessType = "UPDATE")
    @Operation(summary = "修改个人信息")
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody SysUser user) {
        userService.updateProfile(user);
        return Result.success();
    }
}
