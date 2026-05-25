import request from '@/api/request'

/** 仪表盘统计数据 */
export function getDashboard() {
  return request.get('/report/dashboard')
}

/** 考勤趋势（12个月） */
export function getAttendanceTrend(year?: number) {
  return request.get('/report/attendance/trend', { params: { year } })
}

/** 任务统计 */
export function getTaskStats() {
  return request.get('/report/task/stats')
}

/** 部门人数分布 */
export function getDeptDistribution() {
  return request.get('/report/dept/distribution')
}

/** 每日待办趋势（最近7天） */
export function getTodoTrend() {
  return request.get('/report/todo/trend')
}
