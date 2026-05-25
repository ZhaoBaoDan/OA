package com.smartauto.oa.task.controller;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.common.Result;
import com.smartauto.oa.system.annotation.OperLog;
import com.smartauto.oa.task.entity.BizTask;
import com.smartauto.oa.task.service.IBizTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 任务管理
 */
@Tag(name = "任务管理")
@RestController
@RequestMapping("/task")
public class BizTaskController {

    private final IBizTaskService taskService;

    public BizTaskController(IBizTaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "分页查询任务")
    @GetMapping
    public Result<PageResult<BizTask>> page(BizTask query,
                                             @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
                                             @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(taskService.page(query, pageNum, pageSize));
    }

    @Operation(summary = "看板数据（按状态分组）")
    @GetMapping("/board")
    public Result<Map<String, List<BizTask>>> board() {
        return Result.success(taskService.board());
    }

    @Operation(summary = "根据ID查询任务")
    @GetMapping("/{id}")
    public Result<BizTask> getById(@PathVariable Long id) {
        return Result.success(taskService.getById(id));
    }

    @OperLog(title = "新增任务", businessType = "INSERT")
    @Operation(summary = "新增任务")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody BizTask task) {
        taskService.create(task);
        return Result.success();
    }

    @OperLog(title = "修改任务", businessType = "UPDATE")
    @Operation(summary = "修改任务")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody BizTask task) {
        taskService.update(task);
        return Result.success();
    }

    @OperLog(title = "修改任务状态", businessType = "UPDATE")
    @Operation(summary = "修改任务状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        taskService.updateStatus(id, status);
        return Result.success();
    }

    @OperLog(title = "删除任务", businessType = "DELETE")
    @Operation(summary = "删除任务")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable List<Long> ids) {
        taskService.delete(ids);
        return Result.success();
    }
}
