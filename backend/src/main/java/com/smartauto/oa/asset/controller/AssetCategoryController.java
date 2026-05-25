package com.smartauto.oa.asset.controller;

import com.smartauto.oa.asset.entity.AssetCategory;
import com.smartauto.oa.asset.service.AssetCategoryService;
import com.smartauto.oa.common.Result;
import com.smartauto.oa.system.annotation.OperLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资产分类控制器
 */
@Tag(name = "资产分类管理")
@RestController
@RequestMapping("/asset/category")
public class AssetCategoryController {

    private final AssetCategoryService categoryService;

    public AssetCategoryController(AssetCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "分类树形列表")
    @GetMapping("/tree")
    public Result<List<AssetCategory>> tree() {
        return Result.success(categoryService.tree());
    }

    @Operation(summary = "分类详情")
    @GetMapping("/{id}")
    public Result<AssetCategory> getById(@PathVariable Long id) {
        return Result.success(categoryService.getById(id));
    }

    @OperLog(title = "新增资产分类", businessType = "INSERT")
    @Operation(summary = "新增分类")
    @PostMapping
    public Result<Void> create(@RequestBody AssetCategory category) {
        categoryService.create(category);
        return Result.success("新增成功");
    }

    @OperLog(title = "修改资产分类", businessType = "UPDATE")
    @Operation(summary = "修改分类")
    @PutMapping
    public Result<Void> update(@RequestBody AssetCategory category) {
        categoryService.update(category);
        return Result.success("修改成功");
    }

    @OperLog(title = "删除资产分类", businessType = "DELETE")
    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success("删除成功");
    }
}
