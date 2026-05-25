import request from '../request'
import type { ApiResponse, PageResult } from '@/types/api'
import type { UserQuery, UserForm, UserInfo } from '@/types/user'

/** 查询用户列表 */
export function listUser(params: UserQuery): Promise<ApiResponse<PageResult<UserInfo>>> {
  return request.get('/system/user', { params })
}

/** 查询用户详情 */
export function getUser(userId: number): Promise<ApiResponse<UserForm>> {
  return request.get(`/system/user/${userId}`)
}

/** 新增用户 */
export function addUser(data: UserForm): Promise<ApiResponse<void>> {
  return request.post('/system/user', data)
}

/** 修改用户 */
export function updateUser(data: UserForm): Promise<ApiResponse<void>> {
  return request.put('/system/user', data)
}

/** 删除用户 */
export function deleteUser(userIds: string): Promise<ApiResponse<void>> {
  return request.delete(`/system/user/${userIds}`)
}

/** 重置用户密码 */
export function resetUserPwd(userId: number, password: string): Promise<ApiResponse<void>> {
  return request.put('/system/user/resetPwd', { userId, password })
}

/** 修改用户状态 */
export function changeUserStatus(userId: number, status: string): Promise<ApiResponse<void>> {
  return request.put('/system/user/changeStatus', { userId, status })
}

/** 获取用户授权角色 */
export function getAuthRole(userId: number): Promise<ApiResponse<{ user: UserInfo; roles: any[] }>> {
  return request.get(`/system/user/authRole/${userId}`)
}

/** 导出用户 */
export function exportUser(params: UserQuery): Promise<AxiosResponse> {
  return request.get('/system/user/export', { params, responseType: 'blob' })
}
