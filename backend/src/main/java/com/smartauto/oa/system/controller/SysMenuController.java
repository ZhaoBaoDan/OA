package com.smartauto.oa.system.controller;

import com.smartauto.oa.common.Result;
import com.smartauto.oa.system.annotation.OperLog;
import com.smartauto.oa.system.entity.SysMenu;
import com.smartauto.oa.system.service.ISysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/system/menu")
public class SysMenuController {

    private final ISysMenuService menuService;

    public SysMenuController(ISysMenuService menuService) {
        this.menuService = menuService;
    }

    @Operation(summary = "查询菜单列表")
    @GetMapping
    public Result<List<SysMenu>> list(SysMenu query) {
        List<SysMenu> list = menuService.list(query);
        return Result.success(list);
    }

    @Operation(summary = "根据ID查询菜单")
    @GetMapping("/{id}")
    public Result<SysMenu> getById(@Parameter(description = "菜单ID", required = true) @PathVariable Long id) {
        SysMenu menu = menuService.getById(id);
        return Result.success(menu);
    }

    @OperLog(title = "新增菜单", businessType = "INSERT")
    @Operation(summary = "新增菜单")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody SysMenu menu) {
        menuService.create(menu);
        return Result.success();
    }

    @OperLog(title = "修改菜单", businessType = "UPDATE")
    @Operation(summary = "修改菜单")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody SysMenu menu) {
        menuService.update(menu);
        return Result.success();
    }

    @OperLog(title = "删除菜单", businessType = "DELETE")
    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@Parameter(description = "菜单ID", required = true) @PathVariable Long id) {
        menuService.delete(id);
        return Result.success();
    }

    @Operation(summary = "查询菜单树")
    @GetMapping("/tree")
    public Result<List<SysMenu>> tree() {
        List<SysMenu> tree = menuService.selectMenuTree();
        return Result.success(tree);
    }

    @Operation(summary = "根据角色ID查询菜单树")
    @GetMapping("/roleMenuTree/{roleId}")
    public Result<List<Long>> roleMenuTree(@Parameter(description = "角色ID", required = true) @PathVariable Long roleId) {
        List<Long> menuIds = menuService.selectMenuTreeByRoleId(roleId);
        return Result.success(menuIds);
    }
}
