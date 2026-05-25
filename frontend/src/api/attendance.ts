import request from '@/api/request'

/** 上班打卡 */
export function clockIn(data?: { ip?: string; location?: string }) {
  return request.post('/attendance/checkin', data)
}

/** 下班打卡 */
export function clockOut(data?: { ip?: string; location?: string }) {
  return request.post('/attendance/checkout', data)
}

/** 今日打卡记录 */
export function getTodayRecord() {
  return request.get('/attendance/today')
}

/** 月度考勤统计 */
export function getMonthStats(params: { year: number; month: number }) {
  return request.get('/attendance/stats', { params })
}
