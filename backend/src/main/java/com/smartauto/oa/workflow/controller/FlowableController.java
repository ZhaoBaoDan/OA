package com.smartauto.oa.workflow.controller;

import com.smartauto.oa.common.Result;
import com.smartauto.oa.common.SecurityUtils;
import com.smartauto.oa.workflow.service.FlowableEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Flowable 工作流引擎控制器
 * 提供真正的 BPMN 流程操作
 */
@Tag(name = "Flowable 工作流引擎")
@RestController
@RequestMapping("/flowable")
public class FlowableController {

    private final FlowableEngineService engineService;

    public FlowableController(FlowableEngineService engineService) {
        this.engineService = engineService;
    }

    // ========== 流程定义 ==========

    @Operation(summary = "获取所有流程定义")
    @GetMapping("/definitions")
    public Result<List<Map<String, Object>>> getDefinitions() {
        List<ProcessDefinition> defs = engineService.getAllProcessDefinitions();
        List<Map<String, Object>> result = defs.stream().map(d -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", d.getId());
            map.put("key", d.getKey());
            map.put("name", d.getName());
            map.put("version", d.getVersion());
            map.put("deploymentId", d.getDeploymentId());
            map.put("description", d.getDescription());
            return map;
        }).collect(Collectors.toList());
        return Result.success(result);
    }

    @Operation(summary = "获取流程定义 XML")
    @GetMapping("/definitions/{id}/xml")
    public Result<String> getDefinitionXml(@PathVariable String id) {
        return Result.success(engineService.getProcessDefinitionXml(id));
    }

    // ========== 启动流程 ==========

    @Operation(summary = "启动流程实例")
    @PostMapping("/process/start")
    public Result<Map<String, Object>> startProcess(
            @Parameter(description = "流程 Key", required = true) @RequestParam String processKey,
            @Parameter(description = "业务关联 ID") @RequestParam(required = false) String businessKey,
            @RequestBody(required = false) Map<String, Object> variables) {
        String startUserId = String.valueOf(SecurityUtils.getUserId());
        ProcessInstance instance = engineService.startProcess(processKey, businessKey, startUserId, variables);

        Map<String, Object> result = new HashMap<>();
        result.put("processInstanceId", instance.getId());
        result.put("processDefinitionId", instance.getProcessDefinitionId());
        result.put("businessKey", instance.getBusinessKey());
        return Result.success(result);
    }

    // ========== 我的待办 ==========

    @Operation(summary = "我的待办任务")
    @GetMapping("/task/todo")
    public Result<List<Map<String, Object>>> myTasks() {
        String userId = String.valueOf(SecurityUtils.getUserId());
        List<Task> tasks = engineService.getTodoTasks(userId);
        List<Map<String, Object>> result = tasks.stream().map(t -> {
            Map<String, Object> map = new HashMap<>();
            map.put("taskId", t.getId());
            map.put("taskName", t.getName());
            map.put("processInstanceId", t.getProcessInstanceId());
            map.put("processDefinitionId", t.getProcessDefinitionId());
            map.put("assignee", t.getAssignee());
            map.put("createTime", t.getCreateTime());
            map.put("dueDate", t.getDueDate());
            map.put("description", t.getDescription());
            return map;
        }).collect(Collectors.toList());
        return Result.success(result);
    }

    @Operation(summary = "候选任务（待签收）")
    @GetMapping("/task/candidate")
    public Result<List<Map<String, Object>>> candidateTasks() {
        String userId = String.valueOf(SecurityUtils.getUserId());
        List<Task> tasks = engineService.getCandidateTasks(userId);
        List<Map<String, Object>> result = tasks.stream().map(t -> {
            Map<String, Object> map = new HashMap<>();
            map.put("taskId", t.getId());
            map.put("taskName", t.getName());
            map.put("processInstanceId", t.getProcessInstanceId());
            map.put("createTime", t.getCreateTime());
            return map;
        }).collect(Collectors.toList());
        return Result.success(result);
    }

    // ========== 任务操作 ==========

    @Operation(summary = "签收任务")
    @PostMapping("/task/{taskId}/claim")
    public Result<Void> claimTask(@PathVariable String taskId) {
        String userId = String.valueOf(SecurityUtils.getUserId());
        engineService.claimTask(taskId, userId);
        return Result.success("签收成功");
    }

    @Operation(summary = "审批通过")
    @PostMapping("/task/{taskId}/approve")
    public Result<Void> approveTask(
            @PathVariable String taskId,
            @Parameter(description = "审批意见") @RequestParam(required = false) String comment) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("approved", true);
        engineService.completeTask(taskId, comment, vars);
        return Result.success("审批通过");
    }

    @Operation(summary = "审批驳回")
    @PostMapping("/task/{taskId}/reject")
    public Result<Void> rejectTask(
            @PathVariable String taskId,
            @Parameter(description = "驳回原因") @RequestParam(required = false) String comment) {
        engineService.rejectTask(taskId, comment);
        return Result.success("已驳回");
    }

    @Operation(summary = "完成任务（通用）")
    @PostMapping("/task/{taskId}/complete")
    public Result<Void> completeTask(
            @PathVariable String taskId,
            @RequestBody(required = false) Map<String, Object> variables) {
        engineService.completeTask(taskId, null, variables);
        return Result.success("任务已完成");
    }

    // ========== 流程实例操作 ==========

    @Operation(summary = "终止流程实例")
    @PostMapping("/process/{processInstanceId}/terminate")
    public Result<Void> terminate(
            @PathVariable String processInstanceId,
            @Parameter(description = "终止原因") @RequestParam(required = false) String reason) {
        engineService.terminateProcess(processInstanceId, reason);
        return Result.success("流程已终止");
    }
}
