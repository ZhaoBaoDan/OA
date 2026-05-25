<template>
  <div class="attendance-record-container">
    <!-- 月份选择 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true">
        <el-form-item label="年月">
          <el-date-picker
            v-model="currentMonth"
            type="month"
            placeholder="选择月份"
            format="YYYY年MM月"
            value-format="YYYY-MM"
            @change="handleMonthChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="fetchData">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="16">
      <el-col :span="4" v-for="stat in statCards" :key="stat.label">
        <el-card shadow="hover" class="stat-card" :style="{ borderTop: `3px solid ${stat.color}` }">
          <div class="stat-value" :style="{ color: stat.color }">{{ stat.value }}</div>
          <div class="stat-label">{{ stat.label }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 视图切换 -->
    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <span>考勤记录</span>
          <el-radio-group v-model="viewMode" size="small">
            <el-radio-button label="table">表格视图</el-radio-button>
            <el-radio-button label="calendar">日历视图</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <!-- 表格视图 -->
      <template v-if="viewMode === 'table'">
        <el-table v-loading="loading" :data="recordList" border stripe>
          <el-table-column prop="workDate" label="日期" width="120" />
          <el-table-column prop="dayOfWeek" label="星期" width="80" align="center" />
          <el-table-column prop="clockInTime" label="上班打卡" width="120">
            <template #default="{ row }">
              <span :class="{ 'text-danger': row.late }">{{ row.clockInTime || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="clockOutTime" label="下班打卡" width="120">
            <template #default="{ row }">
              <span :class="{ 'text-warning': row.earlyLeave }">{{ row.clockOutTime || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="workHours" label="工时(h)" width="100" align="center" />
          <el-table-column prop="status" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusLabel(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
        </el-table>
      </template>

      <!-- 日历视图 -->
      <template v-else>
        <div class="calendar-header">
          <el-button :icon="ArrowLeft" circle size="small" @click="prevMonth" />
          <span class="calendar-title">{{ calendarYear }}年 {{ calendarMonth }}月</span>
          <el-button :icon="ArrowRight" circle size="small" @click="nextMonth" />
        </div>
        <div class="calendar-grid">
          <div class="calendar-weekday" v-for="day in weekdays" :key="day">{{ day }}</div>
          <div
            v-for="(cell, idx) in calendarCells"
            :key="idx"
            class="calendar-cell"
            :class="getCalendarCellClass(cell)"
          >
            <div class="cell-date">{{ cell.day }}</div>
            <template v-if="cell.status !== undefined">
              <div class="cell-status">
                <el-icon v-if="cell.status === 0" color="#67c23a"><CircleCheck /></el-icon>
                <el-icon v-else-if="cell.status === 1" color="#f56c6c"><Warning /></el-icon>
                <el-icon v-else-if="cell.status === 2" color="#e6a23c"><Warning /></el-icon>
                <el-icon v-else-if="cell.status === 3" color="#909399"><CircleClose /></el-icon>
              </div>
              <div class="cell-time" v-if="cell.clockIn">{{ cell.clockIn }}</div>
            </template>
          </div>
        </div>
        <div class="calendar-legend">
          <span class="legend-item"><span class="legend-dot normal"></span>正常</span>
          <span class="legend-item"><span class="legend-dot late"></span>迟到</span>
          <span class="legend-item"><span class="legend-dot early"></span>早退</span>
          <span class="legend-item"><span class="legend-dot absent"></span>缺卡</span>
          <span class="legend-item"><span class="legend-dot weekend"></span>周末</span>
        </div>
      </template>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Search, Refresh, ArrowLeft, ArrowRight, CircleCheck, Warning, CircleClose } from '@element-plus/icons-vue'

interface RecordItem {
  workDate: string
  dayOfWeek: string
  clockInTime: string
  clockOutTime: string
  workHours: string
  status: number
  late: boolean
  earlyLeave: boolean
  remark: string
}

interface CalendarCell {
  day: number | ''
  status?: number
  clockIn?: string
  clockOut?: string
  isWeekend?: boolean
  isFuture?: boolean
}

const loading = ref(false)
const viewMode = ref<'table' | 'calendar'>('table')
const currentMonth = ref('')
const recordList = ref<RecordItem[]>([])

const calendarYear = ref(new Date().getFullYear())
const calendarMonth = ref(new Date().getMonth() + 1)
const weekdays = ['一', '二', '三', '四', '五', '六', '日']

const statCards = computed(() => [
  { label: '正常出勤', value: '18天', color: '#67c23a' },
  { label: '迟到', value: '2次', color: '#f56c6c' },
  { label: '早退', value: '0次', color: '#e6a23c' },
  { label: '缺勤', value: '0天', color: '#909399' },
  { label: '请假', value: '1天', color: '#409eff' },
  { label: '加班', value: '5.5h', color: '#b37feb' },
])

const mockRecords: RecordItem[] = [
  { workDate: '2024-05-02', dayOfWeek: '周四', clockInTime: '08:55', clockOutTime: '18:05', workHours: '8.2', status: 0, late: false, earlyLeave: false, remark: '' },
  { workDate: '2024-05-03', dayOfWeek: '周五', clockInTime: '09:12', clockOutTime: '18:30', workHours: '8.3', status: 1, late: true, earlyLeave: false, remark: '迟到12分钟' },
  { workDate: '2024-05-06', dayOfWeek: '周一', clockInTime: '08:48', clockOutTime: '18:10', workHours: '8.4', status: 0, late: false, earlyLeave: false, remark: '' },
  { workDate: '2024-05-07', dayOfWeek: '周二', clockInTime: '08:30', clockOutTime: '17:55', workHours: '8.4', status: 0, late: false, earlyLeave: false, remark: '' },
  { workDate: '2024-05-08', dayOfWeek: '周三', clockInTime: '09:05', clockOutTime: '18:00', workHours: '7.9', status: 1, late: true, earlyLeave: false, remark: '迟到5分钟' },
  { workDate: '2024-05-09', dayOfWeek: '周四', clockInTime: '08:50', clockOutTime: '18:15', workHours: '8.4', status: 0, late: false, earlyLeave: false, remark: '' },
  { workDate: '2024-05-10', dayOfWeek: '周五', clockInTime: '', clockOutTime: '', workHours: '0', status: 3, late: false, earlyLeave: false, remark: '年假' },
  { workDate: '2024-05-13', dayOfWeek: '周一', clockInTime: '08:55', clockOutTime: '18:20', workHours: '8.4', status: 0, late: false, earlyLeave: false, remark: '' },
  { workDate: '2024-05-14', dayOfWeek: '周二', clockInTime: '08:40', clockOutTime: '18:05', workHours: '8.4', status: 0, late: false, earlyLeave: false, remark: '' },
]

// 模拟日历数据
const calendarData = ref<Record<number, { status: number; clockIn?: string; clockOut?: string }>>({
  2: { status: 0, clockIn: '08:55', clockOut: '18:05' },
  3: { status: 1, clockIn: '09:12', clockOut: '18:30' },
  6: { status: 0, clockIn: '08:48', clockOut: '18:10' },
  7: { status: 0, clockIn: '08:30', clockOut: '17:55' },
  8: { status: 1, clockIn: '09:05', clockOut: '18:00' },
  9: { status: 0, clockIn: '08:50', clockOut: '18:15' },
  10: { status: 3 },
  13: { status: 0, clockIn: '08:55', clockOut: '18:20' },
  14: { status: 0, clockIn: '08:40', clockOut: '18:05' },
})

const calendarCells = computed(() => {
  const cells: CalendarCell[] = []
  const firstDay = new Date(calendarYear.value, calendarMonth.value - 1, 1)
  let startWeekday = firstDay.getDay() || 7 // 周一=1
  const daysInMonth = new Date(calendarYear.value, calendarMonth.value, 0).getDate()

  // 前面空白
  for (let i = 1; i < startWeekday; i++) {
    cells.push({ day: '' })
  }

  const today = new Date()
  for (let d = 1; d <= daysInMonth; d++) {
    const date = new Date(calendarYear.value, calendarMonth.value - 1, d)
    const dow = date.getDay()
    const isWeekend = dow === 0 || dow === 6
    const isFuture = date > today
    const data = calendarData.value[d]

    cells.push({
      day: d,
      status: data?.status ?? (isWeekend ? -1 : (isFuture ? -2 : undefined)),
      clockIn: data?.clockIn,
      clockOut: data?.clockOut,
      isWeekend,
      isFuture,
    })
  }
  return cells
})

function getCalendarCellClass(cell: CalendarCell) {
  if (cell.day === '') return 'empty'
  const classes: string[] = []
  if (cell.isWeekend) classes.push('weekend')
  if (cell.isFuture) classes.push('future')
  if (cell.status === 0) classes.push('normal')
  if (cell.status === 1) classes.push('late')
  if (cell.status === 2) classes.push('early')
  if (cell.status === 3) classes.push('absent')
  return classes.join(' ')
}

function getStatusType(status: number) {
  const map: Record<number, string> = { 0: 'success', 1: 'danger', 2: 'warning', 3: 'info', 4: 'danger' }
  return (map[status] || '') as any
}

function getStatusLabel(status: number) {
  const map: Record<number, string> = { 0: '正常', 1: '迟到', 2: '早退', 3: '缺卡', 4: '异常' }
  return map[status] || '未知'
}

function handleMonthChange(val: string) {
  if (val) {
    const [y, m] = val.split('-')
    calendarYear.value = parseInt(y)
    calendarMonth.value = parseInt(m)
  }
  fetchData()
}

function prevMonth() {
  if (calendarMonth.value === 1) {
    calendarMonth.value = 12
    calendarYear.value--
  } else {
    calendarMonth.value--
  }
}

function nextMonth() {
  if (calendarMonth.value === 12) {
    calendarMonth.value = 1
    calendarYear.value++
  } else {
    calendarMonth.value++
  }
}

function handleReset() {
  const now = new Date()
  currentMonth.value = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
  calendarYear.value = now.getFullYear()
  calendarMonth.value = now.getMonth() + 1
  fetchData()
}

async function fetchData() {
  loading.value = true
  try {
    await new Promise(r => setTimeout(r, 300))
    recordList.value = [...mockRecords]
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const now = new Date()
  currentMonth.value = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
  fetchData()
})
</script>

<style lang="scss" scoped>
.attendance-record-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-card,
.table-card {
  border-radius: 8px;
}

.stat-card {
  border-radius: 8px;
  text-align: center;

  .stat-value {
    font-size: 22px;
    font-weight: 700;
    margin-bottom: 4px;
  }

  .stat-label {
    font-size: 13px;
    color: #909399;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.text-danger {
  color: #f56c6c;
  font-weight: 600;
}

.text-warning {
  color: #e6a23c;
  font-weight: 600;
}

/* 日历视图 */
.calendar-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-bottom: 16px;

  .calendar-title {
    font-size: 16px;
    font-weight: 600;
    min-width: 120px;
    text-align: center;
  }
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
}

.calendar-weekday {
  text-align: center;
  padding: 8px;
  font-size: 13px;
  font-weight: 600;
  color: #606266;
  background: #f5f7fa;
  border-radius: 4px;
}

.calendar-cell {
  min-height: 80px;
  padding: 8px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  transition: background 0.2s;

  &.empty {
    border-color: transparent;
    background: transparent;
  }

  &.weekend {
    background: #fafafa;
    .cell-date { color: #c0c4cc; }
  }

  &.normal {
    background: #f0f9eb;
    border-color: #e1f3d8;
  }

  &.late {
    background: #fef0f0;
    border-color: #fde2e2;
  }

  &.early {
    background: #fdf6ec;
    border-color: #faecd8;
  }

  &.absent {
    background: #f4f4f5;
    border-color: #e9e9eb;
  }

  .cell-date {
    font-size: 14px;
    font-weight: 500;
    color: #303133;
  }

  .cell-status {
    .el-icon {
      font-size: 16px;
    }
  }

  .cell-time {
    font-size: 11px;
    color: #909399;
    font-variant-numeric: tabular-nums;
  }
}

.calendar-legend {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;

  .legend-item {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    color: #606266;
  }

  .legend-dot {
    width: 10px;
    height: 10px;
    border-radius: 2px;

    &.normal { background: #f0f9eb; border: 1px solid #e1f3d8; }
    &.late { background: #fef0f0; border: 1px solid #fde2e2; }
    &.early { background: #fdf6ec; border: 1px solid #faecd8; }
    &.absent { background: #f4f4f5; border: 1px solid #e9e9eb; }
    &.weekend { background: #fafafa; border: 1px solid #ebeef5; }
  }
}
</style>
