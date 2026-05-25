import request from '../request'
import type { ApiResponse } from '@/types/api'
import type { DeptInfo } from '@/types/system'

/** 查询部门列表 */
export function listDept(params?: { deptName?: string; status?: string }): Promise<ApiResponse<DeptInfo[]>> {
  return request.get('/system/dept', { params })
}

/** 查询部门详情 */
export function getDept(deptId: number): Promise<ApiResponse<DeptInfo>> {
  return request.get(`/system/dept/${deptId}`)
}

/** 新增部门 */
export function addDept(data: DeptInfo): Promise<ApiResponse<void>> {
  return request.post('/system/dept', data)
}

/** 修改部门 */
export function updateDept(data: DeptInfo): Promise<ApiResponse<void>> {
  return request.put('/system/dept', data)
}

/** 删除部门 */
export function deleteDept(deptId: number): Promise<ApiResponse<void>> {
  return request.delete(`/system/dept/${deptId}`)
}

/** 获取部门树 */
export function getDeptTree(): Promise<ApiResponse<any[]>> {
  return request.get('/system/dept/treeselect')
}

/** 获取部门下拉列表（排除指定节点） */
export function getDeptTreeExclude(deptId: number): Promise<ApiResponse<any[]>> {
  return request.get(`/system/dept/list/exclude/${deptId}`)
}
