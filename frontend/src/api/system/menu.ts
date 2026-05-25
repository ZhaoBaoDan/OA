import request from '../request'
import type { ApiResponse } from '@/types/api'
import type { MenuInfo } from '@/types/system'

/** 查询菜单列表 */
export function listMenu(params?: { menuName?: string; status?: string }): Promise<ApiResponse<MenuInfo[]>> {
  return request.get('/system/menu', { params })
}

/** 查询菜单详情 */
export function getMenu(menuId: number): Promise<ApiResponse<MenuInfo>> {
  return request.get(`/system/menu/${menuId}`)
}

/** 新增菜单 */
export function addMenu(data: MenuInfo): Promise<ApiResponse<void>> {
  return request.post('/system/menu', data)
}

/** 修改菜单 */
export function updateMenu(data: MenuInfo): Promise<ApiResponse<void>> {
  return request.put('/system/menu', data)
}

/** 删除菜单 */
export function deleteMenu(menuId: number): Promise<ApiResponse<void>> {
  return request.delete(`/system/menu/${menuId}`)
}

/** 获取菜单树 */
export function getMenuTree(): Promise<ApiResponse<any[]>> {
  return request.get('/system/menu/treeselect')
}

/** 获取角色菜单树 */
export function getRoleMenuTree(roleId: number): Promise<ApiResponse<{ menus: any[]; checkedKeys: number[] }>> {
  return request.get(`/system/menu/roleMenuTreeselect/${roleId}`)
}
