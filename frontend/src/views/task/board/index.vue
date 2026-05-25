<template>
  <div class="task-board-container">
    <div class="board-header">
      <h3>任务看板</h3>
      <el-button type="primary" :icon="Plus" @click="handleAddTask">添加任务</el-button>
    </div>

    <div class="kanban-board">
      <div v-for="column in columns" :key="column.key" class="kanban-column">
        <div class="column-header" :style="{ borderColor: column.color }">
          <div class="column-title">
            <span class="dot" :style="{ background: column.color }" />
            <span>{{ column.title }}</span>
            <el-badge :value="getColumnTasks(column.key).length" type="info" class="count-badge" />
          </div>
        </div>
        <div class="column-body">
          <div
            v-for="task in getColumnTasks(column.key)"
            :key="task.id"
            class="task-card"
            @click="handleViewTask(task)"
          >
            <div class="card-header">
              <el-tag :type="getPriorityType(task.priority)" size="small" effect="dark">
                {{ getPriorityLabel(task.priority) }}
              </el-tag>
              <el-dropdown trigger="click" @click.stop>
                <el-icon class="more-icon"><MoreFilled /></el-icon>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click.stop="handleEditTask(task)">编辑</el-dropdown-item>
                    <el-dropdown-item @click.stop="handleMoveTask(task, 'todo')" :disabled="column.key === 'todo'">移至待办</el-dropdown-item>
                    <el-dropdown-item @click.stop="handleMoveTask(task, 'progress')" :disabled="column.key === 'progress'">移至进行中</el-dropdown-item>
                    <el-dropdown-item @click.stop="handleMoveTask(task, 'done')" :disabled="column.key === 'done'">移至已完成</el-dropdown-item>
                    <el-dropdown-item @click.stop="handleMoveTask(task, 'archive')" :disabled="column.key === 'archive'">移至已归档</el-dropdown-item>
                    <el-dropdown-item divided @click.stop="handleDeleteTask(task)">删除</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
            <h4 class="card-title">{{ task.title }}</h4>
            <p class="card-desc">{{ task.description }}</p>
            <div class="card-footer">
              <div class="card-meta">
                <el-icon><Calendar /></el-icon>
                <span :class="{ 'overdue': isOverdue(task.dueDate) }">{{ task.dueDate }}</span>
              </div>
              <el-avatar :size="24" :style="{ background: getAvatarColor(task.assignee) }">
                {{ task.assignee?.charAt(0) || '?' }}
              </el-avatar>
            </div>
          </div>
          <div v-if="!getColumnTasks(column.key).length" class="empty-column">
            <el-empty description="暂无任务" :image-size="60" />
          </div>
        </div>
      </div>
    </div>

    <!-- 任务详情弹窗 -->
    <el-dialog v-model="detailVisible" title="任务详情" width="550px" destroy-on-close>
      <template v-if="currentTask">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="任务标题">{{ currentTask.title }}</el-descriptions-item>
          <el-descriptions-item label="任务描述">{{ currentTask.description || '无' }}</el-descriptions-item>
          <el-descriptions-item label="优先级">
            <el-tag :type="getPriorityType(currentTask.priority)" size="small">{{ getPriorityLabel(currentTask.priority) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentTask.status)" size="small">{{ getStatusLabel(currentTask.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="负责人">{{ currentTask.assignee }}</el-descriptions-item>
          <el-descriptions-item label="截止日期">
            <span :class="{ 'overdue': isOverdue(currentTask.dueDate) }">{{ currentTask.dueDate }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentTask.createTime }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </el-dialog>

    <!-- 新增/编辑任务弹窗 -->
    <el-dialog
      v-model="taskDialogVisible"
      :title="taskDialogType === 'add' ? '添加任务' : '编辑任务'"
      width="500px"
      destroy-on-close
    >
      <el-form ref="taskFormRef" :model="taskForm" :rules="taskFormRules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="taskForm.title" placeholder="请输入任务标题" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="taskForm.description" type="textarea" :rows="3" placeholder="请输入任务描述" />
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="taskForm.priority" placeholder="请选择优先级">
            <el-option label="低" value="low" />
            <el-option label="中" value="medium" />
            <el-option label="高" value="high" />
            <el-option label="紧急" value="urgent" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="taskForm.status" placeholder="请选择状态">
            <el-option label="待办" value="todo" />
            <el-option label="进行中" value="progress" />
            <el-option label="已完成" value="done" />
            <el-option label="已归档" value="archive" />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人" prop="assignee">
          <el-input v-model="taskForm.assignee" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="截止日期" prop="dueDate">
          <el-date-picker v-model="taskForm.dueDate" type="date" placeholder="请选择截止日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="taskDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleTaskSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Plus, Calendar, MoreFilled } from '@element-plus/icons-vue'
import { getBoard, addTask, updateTask, updateTaskStatus, deleteTask } from '@/api/task'
import type { TaskItem, BoardData } from '@/types/business'

const columns = [
  { key: 'todo', title: '待办', color: '#909399' },
  { key: 'progress', title: '进行中', color: '#409eff' },
  { key: 'done', title: '已完成', color: '#67c23a' },
  { key: 'archive', title: '已归档', color: '#e6a23c' },
]

const boardData = ref<BoardData>({ todo: [], progress: [], done: [], archive: [] })
const detailVisible = ref(false)
const currentTask = ref<TaskItem | null>(null)
const submitLoading = ref(false)

const taskDialogVisible = ref(false)
const taskDialogType = ref<'add' | 'edit'>('add')
const taskFormRef = ref<FormInstance>()
const taskForm = ref<Partial<TaskItem>>({
  title: '', description: '', priority: 'medium', status: 'todo', assignee: '', dueDate: '',
})

const taskFormRules: FormRules = {
  title: [{ required: true, message: '请输入任务标题', trigger: 'blur' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
  assignee: [{ required: true, message: '请输入负责人', trigger: 'blur' }],
  dueDate: [{ required: true, message: '请选择截止日期', trigger: 'change' }],
}

function getColumnTasks(status: string): TaskItem[] {
  return (boardData.value as any)[status] || []
}

const priorityLabelMap: Record<string, string> = { low: '低', medium: '中', high: '高', urgent: '紧急' }
function getPriorityLabel(p: string) { return priorityLabelMap[p] || p }
function getPriorityType(p: string) {
  const map: Record<string, string> = { low: 'info', medium: '', high: 'warning', urgent: 'danger' }
  return (map[p] || '') as any
}
function getStatusType(s: string) {
  const map: Record<string, string> = { todo: 'info', progress: '', done: 'success', archive: 'warning' }
  return (map[s] || '') as any
}
function getStatusLabel(s: string) {
  const map: Record<string, string> = { todo: '待办', progress: '进行中', done: '已完成', archive: '已归档' }
  return map[s] || s
}
function getAvatarColor(name: string) {
  const colors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#b37feb']
  let hash = 0
  for (const ch of (name || '')) hash = ch.charCodeAt(0) + ((hash << 5) - hash)
  return colors[Math.abs(hash) % colors.length]
}
function isOverdue(date: string) {
  return date && new Date(date) < new Date(new Date().toDateString())
}

async function loadBoard() {
  try {
    const res = await getBoard()
    boardData.value = res.data
  } catch { /* handled by interceptor */ }
}

function handleViewTask(task: TaskItem) {
  currentTask.value = task
  detailVisible.value = true
}

function handleAddTask() {
  taskDialogType.value = 'add'
  taskForm.value = { title: '', description: '', priority: 'medium', status: 'todo', assignee: '', dueDate: '' }
  taskDialogVisible.value = true
}

function handleEditTask(task: TaskItem) {
  taskDialogType.value = 'edit'
  taskForm.value = { ...task }
  taskDialogVisible.value = true
}

async function handleTaskSubmit() {
  if (!taskFormRef.value) return
  await taskFormRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (taskDialogType.value === 'add') {
        await addTask(taskForm.value)
        ElMessage.success('任务添加成功')
      } else {
        await updateTask(taskForm.value)
        ElMessage.success('任务更新成功')
      }
      taskDialogVisible.value = false
      loadBoard()
    } catch { /* handled by interceptor */ } finally {
      submitLoading.value = false
    }
  })
}

async function handleMoveTask(task: TaskItem, newStatus: string) {
  try {
    await updateTaskStatus(task.id, newStatus)
    ElMessage.success(`已移至「${getStatusLabel(newStatus)}」`)
    loadBoard()
  } catch { /* handled by interceptor */ }
}

async function handleDeleteTask(task: TaskItem) {
  await ElMessageBox.confirm(`确定要删除任务「${task.title}」吗？`, '提示', { type: 'warning' })
  try {
    await deleteTask(String(task.id))
    ElMessage.success('删除成功')
    loadBoard()
  } catch { /* handled by interceptor */ }
}

onMounted(() => {
  loadBoard()
})
</script>

<style lang="scss" scoped>
.task-board-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: calc(100vh - 110px);
}

.board-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  h3 { margin: 0; font-size: 18px; color: #303133; }
}

.kanban-board {
  display: flex;
  gap: 16px;
  flex: 1;
  overflow-x: auto;
}

.kanban-column {
  flex: 1;
  min-width: 280px;
  background: #f5f7fa;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
}

.column-header {
  padding: 14px 16px;
  border-top: 3px solid;
  border-radius: 12px 12px 0 0;
  background: #fff;
}

.column-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 14px;
  color: #303133;
  .dot { width: 8px; height: 8px; border-radius: 50%; }
  .count-badge { margin-left: auto; }
}

.column-body {
  flex: 1;
  padding: 12px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.task-card {
  background: #fff;
  border-radius: 8px;
  padding: 14px;
  cursor: pointer;
  transition: box-shadow 0.2s;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  &:hover { box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12); }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  .more-icon { cursor: pointer; color: #909399; font-size: 16px; &:hover { color: #409eff; } }
}

.card-title { margin: 0 0 6px; font-size: 14px; font-weight: 600; color: #303133; }
.card-desc { margin: 0 0 12px; font-size: 12px; color: #909399; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
  .overdue { color: #f56c6c; font-weight: 600; }
}

.empty-column { flex: 1; display: flex; align-items: center; justify-content: center; }
</style>
