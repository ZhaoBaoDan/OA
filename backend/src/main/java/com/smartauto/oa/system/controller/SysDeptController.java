package com.smartauto.oa.system.controller;

import com.smartauto.oa.common.Result;
import com.smartauto.oa.system.annotation.OperLog;
import com.smartauto.oa.system.entity.SysDept;
import com.smartauto.oa.system.service.ISysDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理（深度增强版）
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/system/dept")
public class SysDeptController {

    private final ISysDeptService deptService;

    public SysDeptController(ISysDeptService deptService) {
        this.deptService = deptService;
    }

    @Operation(summary = "部门列表")
    @GetMapping
    public Result<List<SysDept>> list(SysDept query) {
        return Result.success(deptService.list(query));
    }

    @Operation(summary = "部门详情")
    @GetMapping("/{id}")
    public Result<SysDept> getById(@PathVariable Long id) {
        return Result.success(deptService.getById(id));
    }

    @OperLog(title = "新增部门", businessType = "INSERT")
    @Operation(summary = "新增部门")
    @PostMapping
    public Result<Void> create(@RequestBody SysDept dept) {
        deptService.create(dept);
        return Result.success("新增成功");
    }

    @OperLog(title = "修改部门", businessType = "UPDATE")
    @Operation(summary = "修改部门")
    @PutMapping
    public Result<Void> update(@RequestBody SysDept dept) {
        deptService.update(dept);
        return Result.success("修改成功");
    }

    @OperLog(title = "删除部门", businessType = "DELETE")
    @Operation(summary = "删除部门")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        deptService.delete(id);
        return Result.success("删除成功");
    }

    @Operation(summary = "部门树")
    @GetMapping("/tree")
    public Result<List<SysDept>> tree() {
        return Result.success(deptService.selectDeptTree());
    }

    @OperLog(title = "移动部门", businessType = "UPDATE")
    @Operation(summary = "移动部门")
    @PutMapping("/move")
    public Result<Void> moveDept(@Parameter(description = "部门ID", required = true) @RequestParam Long deptId,
                                  @Parameter(description = "新父部门ID", required = true) @RequestParam Long newParentId) {
        deptService.move(deptId, newParentId);
        return Result.success("移动成功");
    }
}
