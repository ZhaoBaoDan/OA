<template>
  <div class="report-container">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col :span="6" v-for="card in statCards" :key="card.label">
        <el-card shadow="hover" class="stat-card" :style="{ borderTop: `3px solid ${card.color}` }">
          <div class="stat-content">
            <div>
              <div class="stat-value" :style="{ color: card.color }">{{ card.value }}</div>
              <div class="stat-label">{{ card.label }}</div>
            </div>
            <el-icon :size="40" :style="{ color: card.color, opacity: 0.3 }">
              <component :is="card.icon" />
            </el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第一行图表 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="14">
        <el-card shadow="never" class="chart-card">
          <template #header><span>考勤趋势</span></template>
          <div ref="attendanceChartRef" class="chart-area" />
        </el-card>
      </el-col>
      <el-col :span="10">
        <el-card shadow="never" class="chart-card">
          <template #header><span>任务完成率</span></template>
          <div ref="taskChartRef" class="chart-area" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 第二行图表 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header><span>部门人数分布</span></template>
          <div ref="deptChartRef" class="chart-area" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" class="chart-card">
          <template #header><span>待办事项趋势</span></template>
          <div ref="todoChartRef" class="chart-area" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, markRaw } from 'vue'
import { User, Checked, Timer, DataLine } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getDashboard, getAttendanceTrend, getTaskStats, getDeptDistribution, getTodoTrend } from '@/api/report'

const statCards = reactive([
  { label: '总用户数', value: '0', color: '#409eff', icon: markRaw(User) },
  { label: '本月出勤率', value: '--', color: '#67c23a', icon: markRaw(Checked) },
  { label: '待处理任务', value: '0', color: '#e6a23c', icon: markRaw(Timer) },
  { label: '进行中流程', value: '0', color: '#f56c6c', icon: markRaw(DataLine) },
])

const attendanceChartRef = ref<HTMLElement>()
const taskChartRef = ref<HTMLElement>()
const deptChartRef = ref<HTMLElement>()
const todoChartRef = ref<HTMLElement>()

let attendanceChart: echarts.ECharts | null = null
let taskChart: echarts.ECharts | null = null
let deptChart: echarts.ECharts | null = null
let todoChart: echarts.ECharts | null = null

function initAttendanceChart(data: any[]) {
  if (!attendanceChartRef.value) return
  attendanceChart = echarts.init(attendanceChartRef.value)
  const months = data.map((d: any) => d.month + '月')
  attendanceChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['正常', '迟到', '缺勤'], bottom: 0 },
    grid: { top: 20, right: 20, bottom: 40, left: 50 },
    xAxis: { type: 'category', data: months },
    yAxis: { type: 'value' },
    series: [
      { name: '正常', type: 'line', smooth: true, data: data.map((d: any) => d.normal), areaStyle: { opacity: 0.15 }, itemStyle: { color: '#409eff' } },
      { name: '迟到', type: 'line', smooth: true, data: data.map((d: any) => d.late), areaStyle: { opacity: 0.1 }, itemStyle: { color: '#e6a23c' } },
      { name: '缺勤', type: 'line', smooth: true, data: data.map((d: any) => d.absent), areaStyle: { opacity: 0.1 }, itemStyle: { color: '#f56c6c' } },
    ],
  })
}

function initTaskChart(data: any) {
  if (!taskChartRef.value) return
  taskChart = echarts.init(taskChartRef.value)
  const total = (data.todo || 0) + (data.progress || 0) + (data.done || 0) + (data.archive || 0)
  taskChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0, itemWidth: 10, itemHeight: 10 },
    series: [
      {
        type: 'pie',
        radius: ['40%', '65%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
        label: { show: true, formatter: '{b}\n{d}%' },
        data: [
          { value: data.done || 0, name: '已完成', itemStyle: { color: '#67c23a' } },
          { value: data.progress || 0, name: '进行中', itemStyle: { color: '#409eff' } },
          { value: data.todo || 0, name: '待办', itemStyle: { color: '#909399' } },
          { value: data.overdue || 0, name: '已逾期', itemStyle: { color: '#f56c6c' } },
        ],
      },
    ],
  })
}

function initDeptChart(data: any[]) {
  if (!deptChartRef.value) return
  deptChart = echarts.init(deptChartRef.value)
  deptChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { top: 20, right: 20, bottom: 30, left: 80 },
    xAxis: { type: 'value' },
    yAxis: { type: 'category', data: data.map((d: any) => d.deptName) },
    series: [
      {
        type: 'bar',
        barWidth: 20,
        data: data.map((d: any, i: number) => ({
          value: d.count,
          itemStyle: { color: ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#b37feb'][i % 6] },
        })),
        label: { show: true, position: 'right', formatter: '{c}人' },
      },
    ],
  })
}

function initTodoChart(data: any[]) {
  if (!todoChartRef.value) return
  todoChart = echarts.init(todoChartRef.value)
  const dayMap = ['', '周一', '周二', '周三', '周四', '周五', '周六', '周日']
  todoChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['新增待办', '完成待办'], bottom: 0 },
    grid: { top: 20, right: 20, bottom: 40, left: 50 },
    xAxis: { type: 'category', data: data.map((d: any) => dayMap[d.dayOfWeek] || d.date) },
    yAxis: { type: 'value' },
    series: [
      { name: '新增待办', type: 'bar', data: data.map((d: any) => d.created), itemStyle: { color: '#409eff', borderRadius: [4, 4, 0, 0] } },
      { name: '完成待办', type: 'bar', data: data.map((d: any) => d.completed), itemStyle: { color: '#67c23a', borderRadius: [4, 4, 0, 0] } },
    ],
  })
}

function handleResize() {
  attendanceChart?.resize()
  taskChart?.resize()
  deptChart?.resize()
  todoChart?.resize()
}

onMounted(async () => {
  window.addEventListener('resize', handleResize)

  // 并行加载所有数据
  try {
    const [dashRes, trendRes, taskRes, deptRes, todoRes] = await Promise.all([
      getDashboard(),
      getAttendanceTrend(),
      getTaskStats(),
      getDeptDistribution(),
      getTodoTrend(),
    ])

    // 统计卡片
    const dash = dashRes.data
    statCards[0].value = String(dash.tasks?.report?.total || 0)
    const ma = dash.monthlyAttendance
    if (ma?.normalDays && ma?.totalDays) {
      statCards[1].value = ((ma.normalDays / ma.totalDays) * 100).toFixed(1) + '%'
    }
    statCards[2].value = String((dash.tasks?.report?.todo || 0) + (dash.tasks?.report?.progress || 0))
    statCards[3].value = String(dash.runningProcesses || 0)

    // 图表
    if (trendRes.data?.trend) initAttendanceChart(trendRes.data.trend)
    if (taskRes.data) initTaskChart(taskRes.data)
    if (deptRes.data?.departments) initDeptChart(deptRes.data.departments)
    if (todoRes.data?.trend) initTodoChart(todoRes.data.trend)
  } catch (e) {
    console.error('Report load failed:', e)
  }
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  attendanceChart?.dispose()
  taskChart?.dispose()
  deptChart?.dispose()
  todoChart?.dispose()
})
</script>

<style lang="scss" scoped>
.report-container { display: flex; flex-direction: column; gap: 16px; }
.stat-row { margin-bottom: 0; }
.stat-card { border-radius: 8px; }
.stat-content { display: flex; justify-content: space-between; align-items: center; }
.stat-value { font-size: 28px; font-weight: 700; line-height: 1.2; }
.stat-label { font-size: 13px; color: #909399; margin-top: 4px; }
.chart-row { margin-bottom: 0; }
.chart-card { border-radius: 8px; margin-bottom: 0; }
.chart-area { width: 100%; height: 300px; }
</style>
