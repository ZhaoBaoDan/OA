<template>
  <div class="checkin-container">
    <el-row :gutter="20">
      <!-- 左侧打卡区域 -->
      <el-col :span="10">
        <el-card shadow="never" class="checkin-card">
          <div class="checkin-main">
            <!-- 当前时间 -->
            <div class="current-time">
              <div class="time-display">{{ currentTime }}</div>
              <div class="date-display">{{ currentDate }}</div>
            </div>

            <!-- 打卡按钮 -->
            <div
              class="checkin-button"
              :class="checkinButtonClass"
              @click="handleCheckin"
            >
              <el-icon :size="48">
                <CircleCheck v-if="todayRecord?.clockInTime && todayRecord?.clockOutTime" />
                <Clock v-else />
              </el-icon>
              <span class="btn-text">{{ checkinButtonText }}</span>
            </div>

            <!-- 今日打卡记录 -->
            <div class="today-records">
              <h4>今日打卡记录</h4>
              <div class="record-row">
                <div class="record-item" :class="{ active: todayRecord?.clockInTime }">
                  <el-icon><Sunrise /></el-icon>
                  <div class="record-info">
                    <span class="record-label">上班打卡</span>
                    <span class="record-time" :class="{ empty: !todayRecord?.clockInTime, late: todayRecord?.status === 1 }">
                      {{ formatTime(todayRecord?.clockInTime) || '未打卡' }}
                    </span>
                    <span v-if="todayRecord?.status === 1" class="record-tag late">迟到</span>
                  </div>
                </div>
                <div class="record-item" :class="{ active: todayRecord?.clockOutTime }">
                  <el-icon><Sunset /></el-icon>
                  <div class="record-info">
                    <span class="record-label">下班打卡</span>
                    <span class="record-time" :class="{ empty: !todayRecord?.clockOutTime, early: todayRecord?.status === 2 }">
                      {{ formatTime(todayRecord?.clockOutTime) || '未打卡' }}
                    </span>
                    <span v-if="todayRecord?.status === 2" class="record-tag early">早退</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- 工作时长 -->
            <div v-if="todayRecord?.workHours" class="work-hours">
              <el-icon><Timer /></el-icon>
              <span>今日工时: <strong>{{ todayRecord.workHours }}h</strong></span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧统计区域 -->
      <el-col :span="14">
        <div class="stats-section">
          <h3>{{ currentYear }}年{{ currentMonth }}月 考勤统计</h3>
          <el-row :gutter="16">
            <el-col :span="8" v-for="stat in statsCards" :key="stat.label">
              <el-card shadow="hover" class="stat-card" :style="{ borderTop: `3px solid ${stat.color}` }">
                <div class="stat-value" :style="{ color: stat.color }">{{ stat.value }}</div>
                <div class="stat-label">{{ stat.label }}</div>
              </el-card>
            </el-col>
          </el-row>

          <!-- 考勤日历 -->
          <el-card shadow="never" class="calendar-card">
            <template #header>
              <div class="calendar-header">
                <span>考勤日历</span>
                <div>
                  <el-button :icon="ArrowLeft" circle size="small" @click="prevMonth" />
                  <span class="calendar-title">{{ calendarYear }}年{{ calendarMonth }}月</span>
                  <el-button :icon="ArrowRight" circle size="small" @click="nextMonth" />
                </div>
              </div>
            </template>
            <div class="mini-calendar">
              <div class="mc-weekday" v-for="d in ['一','二','三','四','五','六','日']" :key="d">{{ d }}</div>
              <div
                v-for="(cell, idx) in calendarCells"
                :key="idx"
                class="mc-cell"
                :class="getMcCellClass(cell)"
              >
                <span class="mc-day">{{ cell.day }}</span>
                <span v-if="cell.dot" class="mc-dot" :class="cell.dotClass" />
              </div>
            </div>
            <div class="calendar-legend">
              <span class="legend-item"><span class="legend-dot normal"></span>正常</span>
              <span class="legend-item"><span class="legend-dot late"></span>迟到</span>
              <span class="legend-item"><span class="legend-dot early"></span>早退</span>
              <span class="legend-item"><span class="legend-dot absent"></span>缺卡</span>
            </div>
          </el-card>

          <!-- 最近打卡记录 -->
          <el-card shadow="never" class="recent-card">
            <template #header>
              <span>最近打卡记录</span>
            </template>
            <el-table :data="recentRecords" stripe size="small">
              <el-table-column prop="workDate" label="日期" width="120" />
              <el-table-column prop="dayOfWeek" label="星期" width="80" align="center" />
              <el-table-column prop="clockInTime" label="上班打卡" width="100">
                <template #default="{ row }">
                  <span :class="{ 'late': row.status === 1 }">{{ row.clockInTime || '-' }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="clockOutTime" label="下班打卡" width="100">
                <template #default="{ row }">
                  <span :class="{ 'early': row.status === 2 }">{{ row.clockOutTime || '-' }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="status" label="状态" width="80" align="center">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusLabel(row.status) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="workHours" label="工时" width="80" />
            </el-table>
          </el-card>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { CircleCheck, Clock, Sunrise, Sunset, Timer, ArrowLeft, ArrowRight } from '@element-plus/icons-vue'

interface TodayRecord {
  clockInTime: string | null
  clockOutTime: string | null
  status: number
  workHours: string | null
}

interface RecordItem {
  workDate: string
  dayOfWeek: string
  clockInTime: string
  clockOutTime: string
  workHours: string
  status: number
}

const currentTime = ref('')
const currentDate = ref('')
const currentYear = ref(new Date().getFullYear())
const currentMonth = ref(new Date().getMonth() + 1)
const calendarYear = ref(new Date().getFullYear())
const calendarMonth = ref(new Date().getMonth() + 1)
const todayRecord = ref<TodayRecord | null>(null)
let timer: ReturnType<typeof setInterval> | null = null

const checkinButtonClass = computed(() => {
  if (!todayRecord.value?.clockInTime) return 'pulse-animation'
  if (todayRecord.value?.clockInTime && !todayRecord.value?.clockOutTime) return 'checked-in'
  return 'checked-all'
})

const checkinButtonText = computed(() => {
  if (!todayRecord.value?.clockInTime) return '上班打卡'
  if (todayRecord.value?.clockInTime && !todayRecord.value?.clockOutTime) return '下班打卡'
  return '已完成'
})

const statsCards = computed(() => [
  { label: '正常出勤', value: '18天', color: '#67c23a' },
  { label: '迟到', value: '2次', color: '#f56c6c' },
  { label: '早退', value: '0次', color: '#e6a23c' },
  { label: '缺勤', value: '0天', color: '#909399' },
  { label: '请假', value: '1天', color: '#409eff' },
  { label: '加班', value: '5.5h', color: '#b37feb' },
])

const recentRecords = ref<RecordItem[]>([
  { workDate: '2024-05-24', dayOfWeek: '周五', clockInTime: '08:55', clockOutTime: '18:05', workHours: '8.2h', status: 0 },
  { workDate: '2024-05-23', dayOfWeek: '周四', clockInTime: '09:12', clockOutTime: '18:30', workHours: '8.3h', status: 1 },
  { workDate: '2024-05-22', dayOfWeek: '周三', clockInTime: '08:48', clockOutTime: '18:10', workHours: '8.4h', status: 0 },
  { workDate: '2024-05-21', dayOfWeek: '周二', clockInTime: '08:30', clockOutTime: '17:55', workHours: '8.4h', status: 0 },
  { workDate: '2024-05-20', dayOfWeek: '周一', clockInTime: '09:05', clockOutTime: '18:00', workHours: '7.9h', status: 1 },
])

// 日历数据
const calendarData = ref<Record<number, { status: number }>>({
  2: { status: 0 }, 3: { status: 1 }, 6: { status: 0 }, 7: { status: 0 },
  8: { status: 1 }, 9: { status: 0 }, 13: { status: 0 }, 14: { status: 0 },
  15: { status: 0 }, 16: { status: 0 }, 17: { status: 3 }, 20: { status: 0 },
  21: { status: 0 }, 22: { status: 0 }, 23: { status: 1 }, 24: { status: 0 },
})

interface CalendarCell {
  day: number | ''
  dot?: boolean
  dotClass?: string
}

const calendarCells = computed(() => {
  const cells: CalendarCell[] = []
  const firstDay = new Date(calendarYear.value, calendarMonth.value - 1, 1)
  let startWeekday = firstDay.getDay() || 7
  const daysInMonth = new Date(calendarYear.value, calendarMonth.value, 0).getDate()

  for (let i = 1; i < startWeekday; i++) cells.push({ day: '' })

  for (let d = 1; d <= daysInMonth; d++) {
    const data = calendarData.value[d]
    const date = new Date(calendarYear.value, calendarMonth.value - 1, d)
    const isWeekend = date.getDay() === 0 || date.getDay() === 6
    let dotClass = ''
    let dot = false
    if (data) {
      dot = true
      if (data.status === 0) dotClass = 'normal'
      else if (data.status === 1) dotClass = 'late'
      else if (data.status === 2) dotClass = 'early'
      else if (data.status === 3) dotClass = 'absent'
    } else if (!isWeekend && date <= new Date()) {
      dot = true
      dotClass = 'absent'
    }
    cells.push({ day: d, dot, dotClass })
  }
  return cells
})

function getMcCellClass(cell: CalendarCell) {
  if (cell.day === '') return 'empty'
  const date = new Date(calendarYear.value, calendarMonth.value - 1, cell.day as number)
  const dow = date.getDay()
  if (dow === 0 || dow === 6) return 'weekend'
  return ''
}

function formatTime(time: string | null | undefined) {
  if (!time) return ''
  // 如果是完整的 datetime，提取 HH:mm:ss
  if (time.includes('T') || time.includes(' ')) {
    return time.split('T')[1]?.substring(0, 8) || time.split(' ')[1] || time
  }
  return time
}

function updateTime() {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour12: false })
  currentDate.value = now.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })
}

function handleCheckin() {
  const now = new Date()
  const timeStr = now.toLocaleTimeString('zh-CN', { hour12: false })

  if (!todayRecord.value || !todayRecord.value.clockInTime) {
    // 上班打卡
    const hour = now.getHours()
    const isLate = hour >= 9
    todayRecord.value = {
      clockInTime: timeStr,
      clockOutTime: null,
      status: isLate ? 1 : 0,
      workHours: null,
    }
    if (isLate) {
      ElMessage.warning(`上班打卡成功，但已迟到！时间：${timeStr}`)
    } else {
      ElMessage.success(`上班打卡成功，时间：${timeStr}`)
    }
  } else if (!todayRecord.value.clockOutTime) {
    // 下班打卡
    const hour = now.getHours()
    const isEarly = hour < 18
    todayRecord.value.clockOutTime = timeStr
    todayRecord.value.status = isEarly ? 2 : todayRecord.value.status
    // 计算工时
    const inTime = todayRecord.value.clockInTime
    if (inTime) {
      const [inH, inM] = inTime.split(':').map(Number)
      const workMin = (now.getHours() * 60 + now.getMinutes()) - (inH * 60 + inM)
      todayRecord.value.workHours = (workMin / 60).toFixed(1)
    }
    if (isEarly) {
      ElMessage.warning(`下班打卡成功，但早退了！时间：${timeStr}`)
    } else {
      ElMessage.success(`下班打卡成功，时间：${timeStr}`)
    }
  } else {
    ElMessage.info('今日打卡已完成')
  }
}

function getStatusType(status: number) {
  const map: Record<number, string> = { 0: 'success', 1: 'danger', 2: 'warning', 3: 'info', 4: 'danger' }
  return (map[status] || '') as any
}

function getStatusLabel(status: number) {
  const map: Record<number, string> = { 0: '正常', 1: '迟到', 2: '早退', 3: '缺卡', 4: '异常' }
  return map[status] || '未知'
}

function prevMonth() {
  if (calendarMonth.value === 1) { calendarMonth.value = 12; calendarYear.value-- }
  else calendarMonth.value--
}

function nextMonth() {
  if (calendarMonth.value === 12) { calendarMonth.value = 1; calendarYear.value++ }
  else calendarMonth.value++
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
})

onBeforeUnmount(() => {
  if (timer) clearInterval(timer)
})
</script>

<style lang="scss" scoped>
.checkin-container {
  padding: 4px;
}

.checkin-card {
  border-radius: 8px;
}

.checkin-main {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 28px;
  padding: 20px 0;
}

.current-time {
  text-align: center;

  .time-display {
    font-size: 48px;
    font-weight: 700;
    color: #303133;
    font-variant-numeric: tabular-nums;
  }

  .date-display {
    font-size: 14px;
    color: #909399;
    margin-top: 8px;
  }
}

.checkin-button {
  width: 160px;
  height: 160px;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s;
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
  color: #fff;
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.4);

  &:hover {
    transform: scale(1.05);
    box-shadow: 0 12px 32px rgba(64, 158, 255, 0.5);
  }

  &:active {
    transform: scale(0.98);
  }

  &.checked-in {
    background: linear-gradient(135deg, #e6a23c 0%, #cf9236 100%);
    box-shadow: 0 8px 24px rgba(230, 162, 60, 0.4);
  }

  &.checked-all {
    background: linear-gradient(135deg, #67c23a 0%, #529b2e 100%);
    box-shadow: 0 8px 24px rgba(103, 194, 58, 0.4);
    cursor: default;
  }

  &.pulse-animation {
    animation: pulse 2s infinite;
  }

  .btn-text {
    font-size: 18px;
    font-weight: 600;
  }
}

@keyframes pulse {
  0% { box-shadow: 0 8px 24px rgba(64, 158, 255, 0.4); }
  50% { box-shadow: 0 8px 24px rgba(64, 158, 255, 0.2), 0 0 0 12px rgba(64, 158, 255, 0.1); }
  100% { box-shadow: 0 8px 24px rgba(64, 158, 255, 0.4); }
}

.today-records {
  width: 100%;

  h4 {
    text-align: center;
    margin: 0 0 16px;
    color: #606266;
    font-size: 14px;
  }
}

.record-row {
  display: flex;
  gap: 16px;
}

.record-item {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  transition: all 0.3s;

  &.active {
    background: #ecf5ff;
    border: 1px solid #d9ecff;
  }

  .el-icon {
    font-size: 28px;
    color: #409eff;
  }
}

.record-info {
  display: flex;
  flex-direction: column;
  gap: 4px;

  .record-label {
    font-size: 12px;
    color: #909399;
  }

  .record-time {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    font-variant-numeric: tabular-nums;

    &.empty {
      color: #c0c4cc;
      font-weight: 400;
      font-size: 14px;
    }

    &.late { color: #f56c6c; }
    &.early { color: #e6a23c; }
  }

  .record-tag {
    font-size: 11px;
    font-weight: 600;

    &.late { color: #f56c6c; }
    &.early { color: #e6a23c; }
  }
}

.work-hours {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #f0f9eb;
  border-radius: 20px;
  color: #67c23a;
  font-size: 14px;

  strong {
    font-size: 18px;
  }
}

.stats-section {
  h3 {
    margin: 0 0 16px;
    font-size: 16px;
    color: #303133;
  }
}

.stat-card {
  border-radius: 8px;
  text-align: center;
  margin-bottom: 16px;

  .stat-value {
    font-size: 24px;
    font-weight: 700;
    margin-bottom: 4px;
  }

  .stat-label {
    font-size: 13px;
    color: #909399;
  }
}

.calendar-card {
  border-radius: 8px;
  margin-bottom: 16px;

  .calendar-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .calendar-title {
      margin: 0 12px;
      font-weight: 600;
      font-size: 14px;
    }
  }
}

.mini-calendar {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 2px;

  .mc-weekday {
    text-align: center;
    padding: 8px 0;
    font-size: 12px;
    font-weight: 600;
    color: #909399;
  }

  .mc-cell {
    position: relative;
    text-align: center;
    padding: 8px 0;
    border-radius: 4px;

    &.empty { visibility: hidden; }
    &.weekend .mc-day { color: #c0c4cc; }

    .mc-day {
      font-size: 13px;
      color: #606266;
    }

    .mc-dot {
      position: absolute;
      bottom: 2px;
      left: 50%;
      transform: translateX(-50%);
      width: 6px;
      height: 6px;
      border-radius: 50%;

      &.normal { background: #67c23a; }
      &.late { background: #f56c6c; }
      &.early { background: #e6a23c; }
      &.absent { background: #909399; }
    }
  }
}

.calendar-legend {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;

  .legend-item {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 12px;
    color: #606266;
  }

  .legend-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;

    &.normal { background: #67c23a; }
    &.late { background: #f56c6c; }
    &.early { background: #e6a23c; }
    &.absent { background: #909399; }
  }
}

.recent-card {
  border-radius: 8px;
}

.late {
  color: #f56c6c;
  font-weight: 600;
}

.early {
  color: #e6a23c;
  font-weight: 600;
}
</style>
