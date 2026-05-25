import request from '../request'
import type { ApiResponse, PageResult, PageQuery } from '@/types/api'
import type { RoleInfo } from '@/types/system'

/** 查询角色列表 */
export function listRole(params: PageQuery): Promise<ApiResponse<PageResult<RoleInfo>>> {
  return request.get('/system/role', { params })
}

/** 查询角色详情 */
export function getRole(roleId: number): Promise<ApiResponse<RoleInfo>> {
  return request.get(`/system/role/${roleId}`)
}

/** 新增角色 */
export function addRole(data: RoleInfo): Promise<ApiResponse<void>> {
  return request.post('/system/role', data)
}

/** 修改角色 */
export function updateRole(data: RoleInfo): Promise<ApiResponse<void>> {
  return request.put('/system/role', data)
}

/** 删除角色 */
export function deleteRole(roleIds: string): Promise<ApiResponse<void>> {
  return request.delete(`/system/role/${roleIds}`)
}

/** 修改角色状态 */
export function changeRoleStatus(roleId: number, status: string): Promise<ApiResponse<void>> {
  return request.put('/system/role/changeStatus', { roleId, status })
}

/** 获取角色选项 */
export function getRoleOptions(): Promise<ApiResponse<RoleInfo[]>> {
  return request.get('/system/role/optionselect')
}
