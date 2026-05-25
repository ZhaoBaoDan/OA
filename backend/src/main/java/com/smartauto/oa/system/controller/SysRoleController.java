package com.smartauto.oa.system.controller;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.common.Result;
import com.smartauto.oa.system.annotation.OperLog;
import com.smartauto.oa.system.entity.SysRole;
import com.smartauto.oa.system.service.ISysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 角色管理
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    private final ISysRoleService roleService;

    public SysRoleController(ISysRoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "分页查询角色列表")
    @GetMapping
    public Result<PageResult<SysRole>> page(SysRole query,
                                             @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
                                             @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<SysRole> result = roleService.page(query, pageNum, pageSize);
        return Result.success(result);
    }

    @Operation(summary = "根据ID查询角色")
    @GetMapping("/{id}")
    public Result<SysRole> getById(@Parameter(description = "角色ID", required = true) @PathVariable Long id) {
        SysRole role = roleService.getById(id);
        return Result.success(role);
    }

    @OperLog(title = "新增角色", businessType = "INSERT")
    @Operation(summary = "新增角色")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody SysRole role) {
        roleService.create(role);
        return Result.success();
    }

    @OperLog(title = "修改角色", businessType = "UPDATE")
    @Operation(summary = "修改角色")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody SysRole role) {
        roleService.update(role);
        return Result.success();
    }

    @OperLog(title = "删除角色", businessType = "DELETE")
    @Operation(summary = "删除角色")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@Parameter(description = "角色ID列表", required = true) @PathVariable List<Long> ids) {
        roleService.delete(ids);
        return Result.success();
    }

    @OperLog(title = "修改角色状态", businessType = "UPDATE")
    @Operation(summary = "修改角色状态")
    @PutMapping("/status")
    public Result<Void> updateStatus(@Parameter(description = "角色ID", required = true) @RequestParam Long id,
                                      @Parameter(description = "状态", required = true) @RequestParam Integer status) {
        roleService.updateStatus(id, status);
        return Result.success();
    }

    @OperLog(title = "修改数据权限", businessType = "UPDATE")
    @Operation(summary = "修改数据权限")
    @PutMapping("/dataScope")
    public Result<Void> updateDataScope(@Parameter(description = "角色ID", required = true) @RequestParam Long id,
                                         @Parameter(description = "数据权限范围") @RequestParam String dataScope,
                                         @Parameter(description = "部门ID列表") @RequestBody(required = false) List<Long> deptIds) {
        roleService.updateDataScope(id, dataScope, deptIds);
        return Result.success();
    }

    @Operation(summary = "查询角色菜单树（已选中）")
    @GetMapping("/roleMenuTree/{roleId}")
    public Result<Set<Long>> roleMenuTree(@Parameter(description = "角色ID", required = true) @PathVariable Long roleId) {
        Set<Long> checkedKeys = roleService.selectRoleMenuTree(roleId);
        return Result.success(checkedKeys);
    }
}
