import request from './request'
import type { ApiResponse, PageResult } from '@/types/api'
import type { TaskItem, BoardData } from '@/types/business'

/** 分页查询任务 */
export function listTask(params: any): Promise<ApiResponse<PageResult<TaskItem>>> {
  return request.get('/task', { params })
}

/** 获取看板数据（按状态分组） */
export function getBoard(): Promise<ApiResponse<BoardData>> {
  return request.get('/task/board')
}

/** 查询任务详情 */
export function getTask(id: number): Promise<ApiResponse<TaskItem>> {
  return request.get(`/task/${id}`)
}

/** 新增任务 */
export function addTask(data: Partial<TaskItem>): Promise<ApiResponse<void>> {
  return request.post('/task', data)
}

/** 修改任务 */
export function updateTask(data: Partial<TaskItem>): Promise<ApiResponse<void>> {
  return request.put('/task', data)
}

/** 修改任务状态 */
export function updateTaskStatus(id: number, status: string): Promise<ApiResponse<void>> {
  return request.put(`/task/${id}/status`, null, { params: { status } })
}

/** 删除任务 */
export function deleteTask(ids: string): Promise<ApiResponse<void>> {
  return request.delete(`/task/${ids}`)
}
