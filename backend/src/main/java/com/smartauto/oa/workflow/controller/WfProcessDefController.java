package com.smartauto.oa.workflow.controller;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.common.Result;
import com.smartauto.oa.system.annotation.OperLog;
import com.smartauto.oa.workflow.entity.WfProcessDef;
import com.smartauto.oa.workflow.service.IWfProcessDefService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程定义管理
 */
@Tag(name = "流程定义管理")
@RestController
@RequestMapping("/workflow/def")
public class WfProcessDefController {

    private final IWfProcessDefService processDefService;

    public WfProcessDefController(IWfProcessDefService processDefService) {
        this.processDefService = processDefService;
    }

    @Operation(summary = "分页查询流程定义列表")
    @GetMapping
    public Result<PageResult<WfProcessDef>> page(WfProcessDef query,
                                                   @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
                                                   @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(processDefService.page(query, pageNum, pageSize));
    }

    @Operation(summary = "查询所有已发布流程定义")
    @GetMapping("/list")
    public Result<List<WfProcessDef>> listAll() {
        return Result.success(processDefService.listAll());
    }

    @Operation(summary = "根据ID查询流程定义")
    @GetMapping("/{id}")
    public Result<WfProcessDef> getById(@PathVariable Long id) {
        return Result.success(processDefService.getById(id));
    }

    @OperLog(title = "新增流程定义", businessType = "INSERT")
    @Operation(summary = "新增流程定义")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody WfProcessDef processDef) {
        processDefService.create(processDef);
        return Result.success();
    }

    @OperLog(title = "修改流程定义", businessType = "UPDATE")
    @Operation(summary = "修改流程定义")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody WfProcessDef processDef) {
        processDefService.update(processDef);
        return Result.success();
    }

    @OperLog(title = "删除流程定义", businessType = "DELETE")
    @Operation(summary = "删除流程定义")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable List<Long> ids) {
        processDefService.delete(ids);
        return Result.success();
    }

    @OperLog(title = "部署流程", businessType = "UPDATE")
    @Operation(summary = "部署流程")
    @PutMapping("/deploy/{id}")
    public Result<Void> deploy(@PathVariable Long id) {
        processDefService.deploy(id);
        return Result.success();
    }

    @OperLog(title = "挂起流程", businessType = "UPDATE")
    @Operation(summary = "挂起流程")
    @PutMapping("/suspend/{id}")
    public Result<Void> suspend(@PathVariable Long id) {
        processDefService.suspend(id);
        return Result.success();
    }
}
