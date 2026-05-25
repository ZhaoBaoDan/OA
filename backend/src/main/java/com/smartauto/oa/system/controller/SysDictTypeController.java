package com.smartauto.oa.system.controller;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.common.Result;
import com.smartauto.oa.system.annotation.OperLog;
import com.smartauto.oa.system.entity.SysDictType;
import com.smartauto.oa.system.service.ISysDictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典类型管理
 */
@Tag(name = "字典类型管理")
@RestController
@RequestMapping("/system/dict/type")
public class SysDictTypeController {

    private final ISysDictTypeService dictTypeService;

    public SysDictTypeController(ISysDictTypeService dictTypeService) {
        this.dictTypeService = dictTypeService;
    }

    @Operation(summary = "分页查询字典类型列表")
    @GetMapping
    public Result<PageResult<SysDictType>> page(SysDictType query,
                                                  @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
                                                  @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<SysDictType> result = dictTypeService.page(query, pageNum, pageSize);
        return Result.success(result);
    }

    @OperLog(title = "新增字典类型", businessType = "INSERT")
    @Operation(summary = "新增字典类型")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody SysDictType dictType) {
        dictTypeService.create(dictType);
        return Result.success();
    }

    @OperLog(title = "修改字典类型", businessType = "UPDATE")
    @Operation(summary = "修改字典类型")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody SysDictType dictType) {
        dictTypeService.update(dictType);
        return Result.success();
    }

    @OperLog(title = "删除字典类型", businessType = "DELETE")
    @Operation(summary = "删除字典类型")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@Parameter(description = "字典类型ID列表", required = true) @PathVariable List<Long> ids) {
        dictTypeService.delete(ids);
        return Result.success();
    }
}
