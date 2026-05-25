import request from '@/api/request'

// ========== Flowable 工作流引擎 ==========

/** 获取所有流程定义 */
export function getFlowableDefinitions() {
  return request.get('/flowable/definitions')
}

/** 获取流程定义 XML */
export function getDefinitionXml(id: string) {
  return request.get(`/flowable/definitions/${id}/xml`)
}

/** 启动流程实例 */
export function startProcess(processKey: string, businessKey?: string, variables?: any) {
  return request.post('/flowable/process/start', variables, { params: { processKey, businessKey } })
}

/** 我的待办任务 */
export function getMyFlowableTasks() {
  return request.get('/flowable/task/todo')
}

/** 候选任务 */
export function getCandidateTasks() {
  return request.get('/flowable/task/candidate')
}

/** 签收任务 */
export function claimTask(taskId: string) {
  return request.post(`/flowable/task/${taskId}/claim`)
}

/** 审批通过 */
export function approveTask(taskId: string, comment?: string) {
  return request.post(`/flowable/task/${taskId}/approve`, null, { params: { comment } })
}

/** 审批驳回 */
export function rejectTask(taskId: string, comment?: string) {
  return request.post(`/flowable/task/${taskId}/reject`, null, { params: { comment } })
}

/** 完成任务（通用） */
export function completeTask(taskId: string, variables?: any) {
  return request.post(`/flowable/task/${taskId}/complete`, variables)
}

/** 终止流程 */
export function terminateProcess(processInstanceId: string, reason?: string) {
  return request.post(`/flowable/process/${processInstanceId}/terminate`, null, { params: { reason } })
}
