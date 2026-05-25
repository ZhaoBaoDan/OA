import request from './request'
import type { ApiResponse, PageResult } from '@/types/api'
import type { NotificationItem } from '@/types/business'

/** 分页查询通知 */
export function listNotification(filter: string, pageNum: number, pageSize: number): Promise<ApiResponse<PageResult<NotificationItem>>> {
  return request.get('/notification', { params: { filter, pageNum, pageSize } })
}

/** 获取未读通知数量 */
export function getUnreadCount(): Promise<ApiResponse<number>> {
  return request.get('/notification/unread-count')
}

/** 标记单条通知已读 */
export function markRead(id: number): Promise<ApiResponse<void>> {
  return request.put(`/notification/${id}/read`)
}

/** 全部标记已读 */
export function markAllRead(): Promise<ApiResponse<void>> {
  return request.put('/notification/read-all')
}

/** 删除通知 */
export function deleteNotification(id: number): Promise<ApiResponse<void>> {
  return request.delete(`/notification/${id}`)
}
