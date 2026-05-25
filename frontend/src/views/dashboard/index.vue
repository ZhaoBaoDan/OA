<template>
  <div class="dashboard-container">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="16" class="stat-cards">
      <el-col :xs="12" :sm="6" v-for="card in statCards" :key="card.title">
        <el-card shadow="hover" class="stat-card" :body-style="{ padding: '20px' }">
          <div class="stat-card-inner">
            <div class="stat-info">
              <span class="stat-title">{{ card.title }}</span>
              <span class="stat-value">{{ card.value }}</span>
              <span class="stat-desc" :class="card.trend > 0 ? 'up' : 'down'">
                <el-icon v-if="card.trend > 0"><Top /></el-icon>
                <el-icon v-else><Bottom /></el-icon>
                较昨日 {{ Math.abs(card.trend) }}%
              </span>
            </div>
            <div class="stat-icon" :style="{ background: card.color }">
              <el-icon :size="28"><component :is="card.icon" /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 中部内容区 -->
    <el-row :gutter="16" class="main-content">
      <!-- 待办事项 -->
      <el-col :xs="24" :sm="12">
        <el-card shadow="hover" class="content-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><Bell /></el-icon>
                待办事项
              </span>
              <el-button type="primary" link @click="router.push('/task/board')">查看全部</el-button>
            </div>
          </template>
          <div class="todo-list">
            <div v-for="item in todoList" :key="item.id" class="todo-item">
              <div class="todo-left">
                <el-tag :type="getPriorityType(item.priority)" size="small" class="todo-tag">
                  {{ getPriorityLabel(item.priority) }}
                </el-tag>
                <span class="todo-title">{{ item.title }}</span>
              </div>
              <span class="todo-time">{{ item.dueDate || '无截止日' }}</span>
            </div>
            <el-empty v-if="!todoList.length" description="暂无待办事项" :image-size="80" />
          </div>
        </el-card>
      </el-col>

      <!-- 最近文档 -->
      <el-col :xs="24" :sm="12">
        <el-card shadow="hover" class="content-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><Document /></el-icon>
                最近文档
              </span>
              <el-button type="primary" link @click="router.push('/document/list')">查看全部</el-button>
            </div>
          </template>
          <div class="doc-list">
            <div v-for="doc in recentDocs" :key="doc.id" class="doc-item">
              <el-icon class="doc-icon" :style="{ color: getDocColor(doc.fileType) }">
                <Document />
              </el-icon>
              <div class="doc-info">
                <span class="doc-name">{{ doc.fileName }}</span>
                <span class="doc-meta">{{ doc.uploadName }} · {{ formatTime(doc.createTime) }}</span>
              </div>
              <el-icon class="doc-action"><MoreFilled /></el-icon>
            </div>
            <el-empty v-if="!recentDocs.length" description="暂无最近文档" :image-size="80" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 底部快捷入口 -->
    <el-card shadow="hover" class="quick-entry-card">
      <template #header>
        <div class="card-header">
          <span class="card-title">
            <el-icon><Grid /></el-icon>
            快捷入口
          </span>
        </div>
      </template>
      <el-row :gutter="20" class="quick-entries">
        <el-col
          :xs="12"
          :sm="6"
          v-for="entry in quickEntries"
          :key="entry.title"
        >
          <div class="entry-item" @click="router.push(entry.path)">
            <div class="entry-icon" :style="{ background: entry.bgColor }">
              <el-icon :size="32" :style="{ color: entry.color }">
                <component :is="entry.icon" />
              </el-icon>
            </div>
            <span class="entry-title">{{ entry.title }}</span>
            <span class="entry-desc">{{ entry.desc }}</span>
          </div>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import {
  Document, Bell, Clock, Notification, Operation,
  Finished, Folder, VideoCamera, AlarmClock, Grid,
  MoreFilled, Top, Bottom,
} from '@element-plus/icons-vue'
import { getDashboard } from '@/api/report'
import { listDocument } from '@/api/document'

const router = useRouter()

const statCards = ref([
  { title: '待办任务', value: 0, trend: 0, color: 'linear-gradient(135deg, #409eff, #337ecc)', icon: markRaw(Finished) },
  { title: '今日考勤', value: '--', trend: 0, color: 'linear-gradient(135deg, #67c23a, #529b2e)', icon: markRaw(Clock) },
  { title: '未读通知', value: 0, trend: 0, color: 'linear-gradient(135deg, #e6a23c, #b88230)', icon: markRaw(Notification) },
  { title: '进行中流程', value: 0, trend: 0, color: 'linear-gradient(135deg, #f56c6c, #c45656)', icon: markRaw(Operation) },
])

const todoList = ref<any[]>([])
const recentDocs = ref<any[]>([])

const quickEntries = ref([
  { title: '考勤打卡', desc: '记录每日出勤', path: '/attendance/checkin', icon: markRaw(AlarmClock), color: '#409eff', bgColor: 'rgba(64, 158, 255, 0.1)' },
  { title: '任务看板', desc: '管理个人任务', path: '/task/board', icon: markRaw(Finished), color: '#67c23a', bgColor: 'rgba(103, 194, 58, 0.1)' },
  { title: '文档中心', desc: '查看共享文档', path: '/document/list', icon: markRaw(Folder), color: '#e6a23c', bgColor: 'rgba(230, 162, 60, 0.1)' },
  { title: '会议预约', desc: '预约会议室', path: '/meeting/room', icon: markRaw(VideoCamera), color: '#f56c6c', bgColor: 'rgba(245, 108, 108, 0.1)' },
])

function getPriorityType(p: string) {
  const map: Record<string, string> = { urgent: 'danger', high: 'warning', medium: '', low: 'info' }
  return (map[p] || '') as any
}

function getPriorityLabel(p: string) {
  const map: Record<string, string> = { urgent: '紧急', high: '高', medium: '中', low: '低' }
  return map[p] || p
}

function getDocColor(type: string) {
  const map: Record<string, string> = { image: '#409eff', pdf: '#f56c6c', document: '#67c23a', spreadsheet: '#e6a23c' }
  return map[type] || '#909399'
}

function formatTime(t: string) {
  if (!t) return ''
  return t.replace(/T/g, ' ').substring(0, 16)
}

onMounted(async () => {
  try {
    const { data } = await getDashboard()
    // 待办任务数
    const todoCount = data.tasks?.report?.todo || 0
    const progressCount = data.tasks?.report?.progress || 0
    statCards.value[0].value = todoCount + progressCount

    // 今日考勤
    const today = data.todayAttendance
    statCards.value[1].value = today ? (today.status === 'normal' ? '正常' : today.status === 'late' ? '迟到' : today.status) : '未打卡'

    // 未读通知
    statCards.value[2].value = data.unreadNotifications || 0

    // 进行中流程
    statCards.value[3].value = data.runningProcesses || 0

    // 待办列表
    const board = data.tasks?.board
    if (board?.todo) {
      todoList.value = board.todo.slice(0, 5)
    }
  } catch (e) {
    console.error('Dashboard load failed:', e)
  }

  // 加载最近文档
  try {
    const { data } = await listDocument({ pageNum: 1, pageSize: 5 })
    if (data?.records) {
      recentDocs.value = data.records
    }
  } catch (e) {
    console.error('Documents load failed:', e)
  }
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.stat-cards {
  .stat-card {
    border-radius: 8px;
    transition: transform 0.3s;
    &:hover { transform: translateY(-4px); }
  }
}

.stat-card-inner {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-title { font-size: 14px; color: #909399; }
.stat-value { font-size: 28px; font-weight: 700; color: #303133; }

.stat-desc {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 2px;
  &.up { color: #f56c6c; }
  &.down { color: #67c23a; }
}

.stat-icon {
  width: 56px; height: 56px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center; color: #fff;
}

.content-card {
  border-radius: 8px; height: 100%;
  .card-header { display: flex; justify-content: space-between; align-items: center; }
  .card-title { display: flex; align-items: center; gap: 8px; font-size: 16px; font-weight: 600; }
}

.todo-list { display: flex; flex-direction: column; }
.todo-item {
  display: flex; justify-content: space-between; align-items: center;
  padding: 12px 0; border-bottom: 1px solid #f0f0f0;
  &:last-child { border-bottom: none; }
}
.todo-left { display: flex; align-items: center; gap: 10px; }
.todo-tag { flex-shrink: 0; }
.todo-title { font-size: 14px; color: #303133; }
.todo-time { font-size: 12px; color: #909399; flex-shrink: 0; }

.doc-list { display: flex; flex-direction: column; }
.doc-item {
  display: flex; align-items: center; gap: 12px;
  padding: 10px 0; border-bottom: 1px solid #f0f0f0; cursor: pointer; transition: background 0.2s;
  &:last-child { border-bottom: none; }
  &:hover { background: #f5f7fa; margin: 0 -20px; padding-left: 20px; padding-right: 20px; }
}
.doc-icon { font-size: 24px; flex-shrink: 0; }
.doc-info { flex: 1; display: flex; flex-direction: column; gap: 2px; min-width: 0; }
.doc-name { font-size: 14px; color: #303133; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.doc-meta { font-size: 12px; color: #909399; }
.doc-action { color: #c0c4cc; cursor: pointer; &:hover { color: #409eff; } }

.quick-entry-card {
  border-radius: 8px;
  .card-header { display: flex; justify-content: space-between; align-items: center; }
  .card-title { display: flex; align-items: center; gap: 8px; font-size: 16px; font-weight: 600; }
}
.quick-entries { text-align: center; }
.entry-item {
  display: flex; flex-direction: column; align-items: center; gap: 8px;
  padding: 24px 16px; border-radius: 8px; cursor: pointer; transition: all 0.3s;
  &:hover { background: #f5f7fa; transform: translateY(-2px); }
}
.entry-icon { width: 64px; height: 64px; border-radius: 16px; display: flex; align-items: center; justify-content: center; }
.entry-title { font-size: 15px; font-weight: 600; color: #303133; }
.entry-desc { font-size: 12px; color: #909399; }
</style>
