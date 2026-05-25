<template>
  <div class="gantt-container">
    <el-card shadow="never">
      <template #header>
        <div class="flex justify-between items-center">
          <span>甘特图</span>
          <el-select v-model="projectId" placeholder="选择项目" style="width:200px">
            <el-option v-for="p in projects" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
        </div>
      </template>

      <div class="gantt-chart" v-loading="loading">
        <div class="gantt-header">
          <div class="gantt-task-col">任务名称</div>
          <div class="gantt-timeline">
            <div v-for="day in timelineDays" :key="day.date" :class="['gantt-day', { today: day.isToday, weekend: day.isWeekend }]">
              <div class="day-num">{{ day.day }}</div>
              <div class="day-week">{{ day.weekday }}</div>
            </div>
          </div>
        </div>
        <div v-for="task in ganttTasks" :key="task.id" class="gantt-row">
          <div class="gantt-task-col">
            <el-tag :type="priorityType(task.priority)" size="small" class="mr-1">{{ task.title }}</el-tag>
          </div>
          <div class="gantt-timeline">
            <div v-for="day in timelineDays" :key="day.date" :class="['gantt-cell', { weekend: day.isWeekend }]"></div>
            <div class="gantt-bar" :style="barStyle(task)" :class="'priority-' + task.priority">
              <span class="bar-label">{{ task.title }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'

const loading = ref(false)
const projectId = ref(1)
const projects = ref([{ id: 1, name: 'SmartAuto OA 项目' }])

const ganttTasks = ref([
  { id: 1, title: '需求分析', startDate: '2026-05-01', endDate: '2026-05-05', priority: 1, progress: 100 },
  { id: 2, title: '数据库设计', startDate: '2026-05-06', endDate: '2026-05-10', priority: 2, progress: 80 },
  { id: 3, title: '后端开发', startDate: '2026-05-11', endDate: '2026-05-25', priority: 2, progress: 60 },
  { id: 4, title: '前端开发', startDate: '2026-05-15', endDate: '2026-05-28', priority: 2, progress: 50 },
  { id: 5, title: '测试', startDate: '2026-05-26', endDate: '2026-05-30', priority: 1, progress: 0 },
])

const priorityType = (p: number) => ['info', '', 'warning', 'danger'][p] as any

const timelineDays = computed(() => {
  const days = []
  const start = new Date('2026-05-01')
  const end = new Date('2026-05-31')
  const today = new Date().toISOString().split('T')[0]
  const weekdays = ['日', '一', '二', '三', '四', '五', '六']

  for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
    const dateStr = d.toISOString().split('T')[0]
    days.push({
      date: dateStr,
      day: d.getDate(),
      weekday: weekdays[d.getDay()],
      isToday: dateStr === today,
      isWeekend: d.getDay() === 0 || d.getDay() === 6
    })
  }
  return days
})

const barStyle = (task: any) => {
  const start = new Date('2026-05-01')
  const taskStart = new Date(task.startDate)
  const taskEnd = new Date(task.endDate)
  const dayWidth = 28

  const left = Math.floor((taskStart.getTime() - start.getTime()) / 86400000) * dayWidth
  const width = Math.floor((taskEnd.getTime() - taskStart.getTime()) / 86400000) * dayWidth + dayWidth

  return { left: left + 'px', width: width + 'px' }
}

onMounted(() => {})
</script>

<style scoped>
.gantt-container { padding: 16px; }
.gantt-chart { overflow-x: auto; }
.gantt-header, .gantt-row { display: flex; min-height: 40px; border-bottom: 1px solid #ebeef5; }
.gantt-task-col { width: 150px; min-width: 150px; padding: 8px; font-size: 13px; display: flex; align-items: center; border-right: 1px solid #ebeef5; }
.gantt-timeline { flex: 1; display: flex; position: relative; }
.gantt-day { width: 28px; min-width: 28px; text-align: center; font-size: 11px; border-right: 1px solid #f0f0f0; }
.gantt-day.today { background: #ecf5ff; }
.gantt-day.weekend { background: #fafafa; }
.day-num { font-weight: 600; }
.day-week { color: #909399; font-size: 10px; }
.gantt-cell { width: 28px; min-width: 28px; border-right: 1px solid #f0f0f0; }
.gantt-cell.weekend { background: #fafafa; }
.gantt-bar { position: absolute; height: 24px; top: 8px; border-radius: 4px; display: flex; align-items: center; padding: 0 6px; font-size: 11px; color: #fff; overflow: hidden; white-space: nowrap; }
.gantt-bar.priority-0 { background: #909399; }
.gantt-bar.priority-1 { background: #409eff; }
.gantt-bar.priority-2 { background: #e6a23c; }
.gantt-bar.priority-3 { background: #f56c6c; }
.bar-label { overflow: hidden; text-overflow: ellipsis; }
.mr-1 { margin-right: 4px; }
</style>
