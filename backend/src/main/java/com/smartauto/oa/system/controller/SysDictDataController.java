package com.smartauto.oa.system.controller;

import com.smartauto.oa.common.Result;
import com.smartauto.oa.system.annotation.OperLog;
import com.smartauto.oa.system.entity.SysDictData;
import com.smartauto.oa.system.service.ISysDictDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据管理
 */
@Tag(name = "字典数据管理")
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController {

    private final ISysDictDataService dictDataService;

    public SysDictDataController(ISysDictDataService dictDataService) {
        this.dictDataService = dictDataService;
    }

    @Operation(summary = "查询字典数据列表")
    @GetMapping
    public Result<List<SysDictData>> list(SysDictData query) {
        List<SysDictData> list = dictDataService.list(query);
        return Result.success(list);
    }

    @Operation(summary = "根据字典类型查询字典数据")
    @GetMapping("/type/{dictType}")
    public Result<List<SysDictData>> listByDictType(@Parameter(description = "字典类型", required = true) @PathVariable String dictType) {
        List<SysDictData> list = dictDataService.listByDictType(dictType);
        return Result.success(list);
    }

    @OperLog(title = "新增字典数据", businessType = "INSERT")
    @Operation(summary = "新增字典数据")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody SysDictData dictData) {
        dictDataService.create(dictData);
        return Result.success();
    }

    @OperLog(title = "修改字典数据", businessType = "UPDATE")
    @Operation(summary = "修改字典数据")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody SysDictData dictData) {
        dictDataService.update(dictData);
        return Result.success();
    }

    @OperLog(title = "删除字典数据", businessType = "DELETE")
    @Operation(summary = "删除字典数据")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@Parameter(description = "字典数据ID列表", required = true) @PathVariable List<Long> ids) {
        dictDataService.delete(ids);
        return Result.success();
    }
}
