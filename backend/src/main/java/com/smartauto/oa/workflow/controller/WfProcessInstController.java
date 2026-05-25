package com.smartauto.oa.workflow.controller;

import com.smartauto.oa.common.PageResult;
import com.smartauto.oa.common.Result;
import com.smartauto.oa.system.annotation.OperLog;
import com.smartauto.oa.workflow.entity.WfApprovalRecord;
import com.smartauto.oa.workflow.entity.WfProcessInst;
import com.smartauto.oa.workflow.service.IWfProcessInstService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程实例管理
 */
@Tag(name = "流程实例管理")
@RestController
@RequestMapping("/workflow/instance")
public class WfProcessInstController {

    private final IWfProcessInstService processInstService;

    public WfProcessInstController(IWfProcessInstService processInstService) {
        this.processInstService = processInstService;
    }

    @Operation(summary = "分页查询流程实例")
    @GetMapping
    public Result<PageResult<WfProcessInst>> page(WfProcessInst query,
                                                    @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
                                                    @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(processInstService.page(query, pageNum, pageSize));
    }

    @Operation(summary = "根据ID查询流程实例")
    @GetMapping("/{id}")
    public Result<WfProcessInst> getById(@PathVariable Long id) {
        return Result.success(processInstService.getById(id));
    }

    @Operation(summary = "查询审批记录")
    @GetMapping("/{id}/records")
    public Result<List<WfApprovalRecord>> getApprovalRecords(@PathVariable Long id) {
        return Result.success(processInstService.getApprovalRecords(id));
    }

    @OperLog(title = "终止流程", businessType = "UPDATE")
    @Operation(summary = "终止流程实例")
    @PutMapping("/terminate/{id}")
    public Result<Void> terminate(@PathVariable Long id) {
        processInstService.terminate(id);
        return Result.success();
    }

    @OperLog(title = "删除流程实例", businessType = "DELETE")
    @Operation(summary = "删除流程实例")
    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable List<Long> ids) {
        processInstService.delete(ids);
        return Result.success();
    }
}
