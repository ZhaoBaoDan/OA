package com.smartauto.oa.system.controller;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.common.Result;
import com.smartauto.oa.system.annotation.OperLog;
import com.smartauto.oa.system.entity.SysPost;
import com.smartauto.oa.system.service.ISysPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位管理
 */
@Tag(name = "岗位管理")
@RestController
@RequestMapping("/system/post")
public class SysPostController {

    private final ISysPostService postService;

    public SysPostController(ISysPostService postService) {
        this.postService = postService;
    }

    @Operation(summary = "分页查询岗位列表")
    @GetMapping
    public Result<PageResult<SysPost>> page(SysPost query,
                                             @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
                                             @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<SysPost> result = postService.page(query, pageNum, pageSize);
        return Result.success(result);
    }

    @OperLog(title = "新增岗位", businessType = "INSERT")
    @Operation(summary = "新增岗位")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody SysPost post) {
        postService.create(post);
        return Result.success();
    }

    @OperLog(title = "修改岗位", businessType = "UPDATE")
    @Operation(summary = "修改岗位")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody SysPost post) {
        postService.update(post);
        return Result.success();
    }

    @OperLog(title = "删除岗位", businessType = "DELETE")
    @Operation(summary = "删除岗位")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@Parameter(description = "岗位ID列表", required = true) @PathVariable List<Long> ids) {
        postService.delete(ids);
        return Result.success();
    }
}
