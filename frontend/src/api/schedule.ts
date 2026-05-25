import request from '@/api/request'

// ========== 考勤班次 ==========

/** 班次列表（分页） */
export function listSchedule(params: any) {
  return request.get('/attendance/schedule', { params })
}

/** 我的班次列表 */
export function getMySchedules() {
  return request.get('/attendance/schedule/mine')
}

/** 获取当前有效班次 */
export function getEffectiveSchedule() {
  return request.get('/attendance/schedule/effective')
}

/** 班次详情 */
export function getSchedule(id: number) {
  return request.get(`/attendance/schedule/${id}`)
}

/** 新增班次 */
export function createSchedule(data: any) {
  return request.post('/attendance/schedule', data)
}

/** 修改班次 */
export function updateSchedule(data: any) {
  return request.put('/attendance/schedule', data)
}

/** 删除班次 */
export function deleteSchedule(id: number) {
  return request.delete(`/attendance/schedule/${id}`)
}

// ========== 假期余额 ==========

/** 我的假期余额 */
export function getMyLeaveBalance(year?: number) {
  return request.get('/attendance/leave/mine', { params: { year } })
}

/** 指定用户假期余额 */
export function getUserLeaveBalance(userId: number, year?: number) {
  return request.get(`/attendance/leave/user/${userId}`, { params: { year } })
}

/** 假期余额详情 */
export function getLeaveBalance(id: number) {
  return request.get(`/attendance/leave/${id}`)
}

/** 新增假期余额 */
export function createLeaveBalance(data: any) {
  return request.post('/attendance/leave', data)
}

/** 修改假期余额 */
export function updateLeaveBalance(data: any) {
  return request.put('/attendance/leave', data)
}
