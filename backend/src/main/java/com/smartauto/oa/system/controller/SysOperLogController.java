package com.smartauto.oa.system.controller;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.common.Result;
import com.smartauto.oa.system.annotation.OperLog;
import com.smartauto.oa.system.entity.SysOperLog;
import com.smartauto.oa.system.service.ISysOperLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 操作日志管理
 */
@Tag(name = "操作日志管理")
@RestController
@RequestMapping("/system/log")
public class SysOperLogController {

    private final ISysOperLogService operLogService;

    public SysOperLogController(ISysOperLogService operLogService) {
        this.operLogService = operLogService;
    }

    @Operation(summary = "分页查询操作日志")
    @GetMapping
    public Result<PageResult<SysOperLog>> page(SysOperLog query,
                                                @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
                                                @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<SysOperLog> result = operLogService.page(query, pageNum, pageSize);
        return Result.success(result);
    }

    @OperLog(title = "删除操作日志", businessType = "DELETE")
    @Operation(summary = "删除操作日志")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@Parameter(description = "日志ID列表", required = true) @PathVariable List<Long> ids) {
        operLogService.delete(ids);
        return Result.success();
    }

    @OperLog(title = "清空操作日志", businessType = "DELETE")
    @Operation(summary = "清空操作日志")
    @DeleteMapping("/clean")
    public Result<Void> clean() {
        operLogService.clean();
        return Result.success();
    }
}
