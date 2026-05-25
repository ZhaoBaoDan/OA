import request from './request'
import type { ApiResponse, PageResult } from '@/types/api'
import type { MeetingRoom, MeetingBooking } from '@/types/business'

// ========== 会议室 ==========

/** 分页查询会议室 */
export function listMeetingRoom(params?: any): Promise<ApiResponse<PageResult<MeetingRoom>>> {
  return request.get('/meeting/room', { params })
}

/** 查询所有可用会议室 */
export function listAllMeetingRoom(): Promise<ApiResponse<MeetingRoom[]>> {
  return request.get('/meeting/room/list')
}

/** 查询会议室详情 */
export function getMeetingRoom(id: number): Promise<ApiResponse<MeetingRoom>> {
  return request.get(`/meeting/room/${id}`)
}

/** 新增会议室 */
export function addMeetingRoom(data: Partial<MeetingRoom>): Promise<ApiResponse<void>> {
  return request.post('/meeting/room', data)
}

/** 修改会议室 */
export function updateMeetingRoom(data: Partial<MeetingRoom>): Promise<ApiResponse<void>> {
  return request.put('/meeting/room', data)
}

/** 删除会议室 */
export function deleteMeetingRoom(ids: string): Promise<ApiResponse<void>> {
  return request.delete(`/meeting/room/${ids}`)
}

// ========== 会议室预约 ==========

/** 查询指定会议室指定日期的预约 */
export function listBooking(roomId: number, date: string): Promise<ApiResponse<MeetingBooking[]>> {
  return request.get('/meeting/booking', { params: { roomId, date } })
}

/** 查询指定会议室指定月份的预约 */
export function listBookingByMonth(roomId: number, year: number, month: number): Promise<ApiResponse<MeetingBooking[]>> {
  return request.get('/meeting/booking/month', { params: { roomId, year, month } })
}

/** 新增预约 */
export function addBooking(data: Partial<MeetingBooking>): Promise<ApiResponse<void>> {
  return request.post('/meeting/booking', data)
}

/** 取消预约 */
export function deleteBooking(id: number): Promise<ApiResponse<void>> {
  return request.delete(`/meeting/booking/${id}`)
}

// ========== 会议纪要 ==========

/** 纪要列表（分页） */
export function listMinutes(params?: any) {
  return request.get('/meeting/minutes', { params })
}

/** 纪要详情 */
export function getMinutes(id: number) {
  return request.get(`/meeting/minutes/${id}`)
}

/** 新增纪要 */
export function createMinutes(data: any) {
  return request.post('/meeting/minutes', data)
}

/** 修改纪要 */
export function updateMinutes(data: any) {
  return request.put('/meeting/minutes', data)
}

/** 删除纪要 */
export function deleteMinutes(id: number) {
  return request.delete(`/meeting/minutes/${id}`)
}
