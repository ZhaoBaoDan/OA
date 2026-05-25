package com.smartauto.oa.workflow.service;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Flowable 工作流引擎服务
 * 封装 Flowable 核心 API，供业务层调用
 */
@Service
public class FlowableEngineService {

    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;
    private final TaskService taskService;

    public FlowableEngineService(RepositoryService repositoryService,
                                  RuntimeService runtimeService,
                                  TaskService taskService) {
        this.repositoryService = repositoryService;
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

    // ========== 流程定义 ==========

    /**
     * 获取所有已部署的流程定义
     */
    public List<ProcessDefinition> getAllProcessDefinitions() {
        return repositoryService.createProcessDefinitionQuery()
                .latestVersion()
                .orderByProcessDefinitionName().asc()
                .list();
    }

    /**
     * 按 Key 获取最新版本流程定义
     */
    public ProcessDefinition getProcessDefinitionByKey(String processKey) {
        return repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processKey)
                .latestVersion()
                .singleResult();
    }

    /**
     * 获取流程定义 XML
     */
    public String getProcessDefinitionXml(String processDefinitionId) {
        try {
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(processDefinitionId)
                    .singleResult();
            if (pd == null) return "";
            InputStream resource = repositoryService.getResourceAsStream(
                    pd.getDeploymentId(), pd.getResourceName());
            return new String(resource.readAllBytes());
        } catch (Exception e) {
            return "";
        }
    }

    // ========== 流程实例 ==========

    /**
     * 启动流程实例
     * @param processKey 流程定义 Key
     * @param businessKey 业务关联 ID
     * @param startUserId 发起人 ID
     * @param variables 流程变量
     * @return 流程实例
     */
    public ProcessInstance startProcess(String processKey, String businessKey,
                                         String startUserId, Map<String, Object> variables) {
        if (variables == null) variables = new HashMap<>();
        variables.put("startUserId", startUserId);
        return runtimeService.startProcessInstanceByKey(processKey, businessKey, variables);
    }

    /**
     * 获取运行中的流程实例
     */
    public List<ProcessInstance> getRunningInstances() {
        return runtimeService.createProcessInstanceQuery()
                .orderByProcessInstanceId().desc()
                .list();
    }

    /**
     * 终止流程实例
     */
    public void terminateProcess(String processInstanceId, String reason) {
        runtimeService.deleteProcessInstance(processInstanceId, reason);
    }

    // ========== 任务 ==========

    /**
     * 获取指定用户的待办任务
     */
    public List<Task> getTodoTasks(String assignee) {
        return taskService.createTaskQuery()
                .taskAssignee(assignee)
                .orderByTaskCreateTime().desc()
                .list();
    }

    /**
     * 获取候选任务（用户组或候选人）
     */
    public List<Task> getCandidateTasks(String userId) {
        return taskService.createTaskQuery()
                .taskCandidateUser(userId)
                .orderByTaskCreateTime().desc()
                .list();
    }

    /**
     * 签收任务
     */
    public void claimTask(String taskId, String userId) {
        taskService.claim(taskId, userId);
    }

    /**
     * 完成任务（审批通过）
     */
    public void completeTask(String taskId, String comment, Map<String, Object> variables) {
        if (comment != null && !comment.isEmpty()) {
            taskService.addComment(taskId, null, comment);
        }
        if (variables != null) {
            taskService.complete(taskId, variables);
        } else {
            taskService.complete(taskId);
        }
    }

    /**
     * 驳回任务（设置变量后完成，由网关路由）
     */
    public void rejectTask(String taskId, String comment) {
        if (comment != null && !comment.isEmpty()) {
            taskService.addComment(taskId, null, "驳回: " + comment);
        }
        Map<String, Object> vars = new HashMap<>();
        vars.put("approved", false);
        taskService.complete(taskId, vars);
    }

    /**
     * 获取任务详情
     */
    public Task getTaskById(String taskId) {
        return taskService.createTaskQuery().taskId(taskId).singleResult();
    }

    /**
     * 获取流程实例的历史任务
     */
    public List<Task> getTasksByProcessInstance(String processInstanceId) {
        return taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .orderByTaskCreateTime().asc()
                .list();
    }
}
