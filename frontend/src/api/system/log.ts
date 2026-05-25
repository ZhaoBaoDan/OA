import request from '../request'
import type { ApiResponse, PageResult, PageQuery } from '@/types/api'
import type { OperLog } from '@/types/system'

/** 查询操作日志列表 */
export function listLog(params: PageQuery): Promise<ApiResponse<PageResult<OperLog>>> {
  return request.get('/system/log', { params })
}

/** 查询操作日志详情 */
export function getLog(operId: number): Promise<ApiResponse<OperLog>> {
  return request.get(`/system/log/${operId}`)
}

/** 删除操作日志 */
export function deleteLog(operIds: string): Promise<ApiResponse<void>> {
  return request.delete(`/system/log/${operIds}`)
}

/** 清空操作日志 */
export function cleanLog(): Promise<ApiResponse<void>> {
  return request.delete('/system/log/clean')
}

/** 导出操作日志 */
export function exportLog(params: PageQuery): Promise<any> {
  return request.get('/system/log/export', { params, responseType: 'blob' })
}
