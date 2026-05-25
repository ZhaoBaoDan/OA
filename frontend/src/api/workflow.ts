import request from '@/api/request'

/** 流程定义 */
export function listDefinition(params: any) {
  return request.get('/workflow/def', { params })
}

export function getDefinition(id: number) {
  return request.get(`/workflow/def/${id}`)
}

export function getDefinitionXml(id: number) {
  return request.get(`/flowable/definitions/${id}/xml`)
}

export function deployDefinition(id: number) {
  return request.put(`/workflow/def/deploy/${id}`)
}

export function deleteDefinition(id: number) {
  return request.delete(`/workflow/def/${id}`)
}

export function suspendDefinition(id: number) {
  return request.put(`/workflow/def/suspend/${id}`)
}

/** 流程实例 */
export function listInstance(params: any) {
  return request.get('/workflow/instance', { params })
}

export function getInstance(id: number) {
  return request.get(`/workflow/instance/${id}`)
}

export function startInstance(data: any) {
  return request.post('/flowable/process/start', data)
}

export function terminateInstance(id: number) {
  return request.put(`/workflow/instance/terminate/${id}`)
}

export function getInstanceHistory(id: number) {
  return request.get(`/workflow/instance/${id}/records`)
}

/** 流程任务 */
export function getTodoList(params: any) {
  return request.get('/flowable/task/todo', { params })
}

export function getCandidateList(params: any) {
  return request.get('/flowable/task/candidate', { params })
}

export function claimTask(id: string) {
  return request.post(`/flowable/task/${id}/claim`)
}

export function completeTask(id: string, data: { comment?: string }) {
  return request.post(`/flowable/task/${id}/complete`, data)
}

export function rejectTask(id: string, data: { comment?: string }) {
  return request.post(`/flowable/task/${id}/reject`, data)
}
