<template>
  <div class="workflow-task-container">
    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="queryParams" ref="queryFormRef" :inline="true">
        <el-form-item label="任务名称" prop="taskName">
          <el-input v-model="queryParams.taskName" placeholder="请输入任务名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="流程名称" prop="processName">
          <el-input v-model="queryParams.processName" placeholder="请输入流程名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Tab 切换 -->
    <el-card shadow="never" class="table-card">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="我的待办" name="todo">
          <el-table v-loading="loading" :data="taskList" border stripe>
            <el-table-column prop="taskName" label="任务名称" min-width="160" show-overflow-tooltip />
            <el-table-column prop="processName" label="流程名称" min-width="140" show-overflow-tooltip />
            <el-table-column prop="startUser" label="发起人" width="100" />
            <el-table-column prop="createTime" label="接收时间" min-width="160" sortable />
            <el-table-column prop="dueTime" label="截止时间" min-width="160">
              <template #default="{ row }">
                <span :class="{ 'overdue': isOverdue(row.dueTime) }">{{ row.dueTime || '无限制' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="priority" label="优先级" width="90" align="center">
              <template #default="{ row }">
                <el-tag :type="getPriorityType(row.priority)" size="small">{{ getPriorityLabel(row.priority) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" align="center" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link :icon="Check" @click="handleApprove(row)">审批</el-button>
                <el-button type="warning" link :icon="Switch" @click="handleTransfer(row)">转办</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="我的已办" name="done">
          <el-table v-loading="loading" :data="taskList" border stripe>
            <el-table-column prop="taskName" label="任务名称" min-width="160" show-overflow-tooltip />
            <el-table-column prop="processName" label="流程名称" min-width="140" show-overflow-tooltip />
            <el-table-column prop="startUser" label="发起人" width="100" />
            <el-table-column prop="completeTime" label="完成时间" min-width="160" sortable />
            <el-table-column prop="status" label="结果" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getTaskStatusType(row.status)" size="small">{{ getTaskStatusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="comment" label="审批意见" min-width="160" show-overflow-tooltip />
            <el-table-column label="操作" width="100" align="center" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" link :icon="View" @click="handleViewDetail(row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>

      <Pagination
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        :total="total"
        @pagination="getList"
      />
    </el-card>

    <!-- 审批弹窗 -->
    <el-dialog v-model="approveVisible" title="审批" width="500px" destroy-on-close>
      <template v-if="currentTask">
        <el-descriptions :column="1" border size="small" class="mb-16">
          <el-descriptions-item label="任务名称">{{ currentTask.taskName }}</el-descriptions-item>
          <el-descriptions-item label="流程名称">{{ currentTask.processName }}</el-descriptions-item>
          <el-descriptions-item label="发起人">{{ currentTask.startUser }}</el-descriptions-item>
          <el-descriptions-item label="接收时间">{{ currentTask.createTime }}</el-descriptions-item>
        </el-descriptions>
        <el-form label-width="80px">
          <el-form-item label="审批意见">
            <el-input v-model="approveForm.comment" type="textarea" :rows="4" placeholder="请输入审批意见" />
          </el-form-item>
        </el-form>
      </template>
      <template #footer>
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button type="danger" :loading="approving" @click="doReject">驳回</el-button>
        <el-button type="primary" :loading="approving" @click="doApprove">通过</el-button>
      </template>
    </el-dialog>

    <!-- 转办弹窗 -->
    <el-dialog v-model="transferVisible" title="转办" width="450px" destroy-on-close>
      <el-form label-width="80px">
        <el-form-item label="转办给">
          <el-select v-model="transferForm.targetUserId" placeholder="请选择转办人" filterable style="width: 100%">
            <el-option v-for="u in userOptions" :key="u.id" :label="u.name" :value="u.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="transferVisible = false">取消</el-button>
        <el-button type="primary" :loading="transferring" @click="doTransfer">确认转办</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="任务详情" width="600px" destroy-on-close>
      <template v-if="currentTask">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="任务名称" :span="2">{{ currentTask.taskName }}</el-descriptions-item>
          <el-descriptions-item label="流程名称">{{ currentTask.processName }}</el-descriptions-item>
          <el-descriptions-item label="发起人">{{ currentTask.startUser }}</el-descriptions-item>
          <el-descriptions-item label="接收时间">{{ currentTask.createTime }}</el-descriptions-item>
          <el-descriptions-item label="完成时间">{{ currentTask.completeTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审批结果" :span="2">
            <el-tag :type="getTaskStatusType(currentTask.status)" size="small">{{ getTaskStatusLabel(currentTask.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="审批意见" :span="2">{{ currentTask.comment || '无' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { Search, Refresh, Check, Switch, View } from '@element-plus/icons-vue'
import Pagination from '@/components/Pagination.vue'

interface TaskItem {
  id: string
  taskId: string
  taskName: string
  processName: string
  processInstanceId: string
  startUser: string
  createTime: string
  dueTime: string
  completeTime: string
  priority: string
  status: number
  comment: string
}

const activeTab = ref('todo')
const loading = ref(false)
const taskList = ref<TaskItem[]>([])
const total = ref(0)
const approveVisible = ref(false)
const transferVisible = ref(false)
const detailVisible = ref(false)
const currentTask = ref<TaskItem | null>(null)
const approving = ref(false)
const transferring = ref(false)

const queryParams = reactive({
  taskName: '',
  processName: '',
  pageNum: 1,
  pageSize: 20,
})

const approveForm = reactive({ comment: '' })
const transferForm = reactive({ targetUserId: null as number | null })
const queryFormRef = ref<FormInstance>()

const userOptions = ref([
  { id: 2, name: '张三' },
  { id: 3, name: '李四' },
  { id: 4, name: '王五' },
  { id: 5, name: '赵六' },
])

const mockTodo: TaskItem[] = [
  { id: '1', taskId: 'task_001', taskName: '部门经理审批', processName: '请假审批流程', processInstanceId: 'pi_001', startUser: '张三', createTime: '2024-05-24 09:00:00', dueTime: '2024-05-26 18:00:00', completeTime: '', priority: 'high', status: 1, comment: '' },
  { id: '2', taskId: 'task_002', taskName: '财务审批', processName: '报销审批流程', processInstanceId: 'pi_002', startUser: '赵六', createTime: '2024-05-23 14:30:00', dueTime: '2024-05-25 18:00:00', completeTime: '', priority: 'medium', status: 1, comment: '' },
  { id: '3', taskId: 'task_003', taskName: 'HR确认', processName: '转正审批流程', processInstanceId: 'pi_003', startUser: '周九', createTime: '2024-05-22 10:00:00', dueTime: '', completeTime: '', priority: 'low', status: 1, comment: '' },
]

const mockDone: TaskItem[] = [
  { id: '4', taskId: 'task_004', taskName: '部门经理审批', processName: '采购申请流程', processInstanceId: 'pi_004', startUser: '孙八', createTime: '2024-05-20 09:00:00', dueTime: '2024-05-22 18:00:00', completeTime: '2024-05-20 15:30:00', priority: 'high', status: 2, comment: '同意，请按预算执行' },
  { id: '5', taskId: 'task_005', taskName: '人事确认', processName: '请假审批流程', processInstanceId: 'pi_005', startUser: '李四', createTime: '2024-05-19 14:00:00', dueTime: '', completeTime: '2024-05-19 16:00:00', priority: 'medium', status: 2, comment: '已确认' },
]

async function getList() {
  loading.value = true
  try {
    await new Promise(r => setTimeout(r, 300))
    const data = activeTab.value === 'todo' ? [...mockTodo] : [...mockDone]
    let filtered = data
    if (queryParams.taskName) filtered = filtered.filter(t => t.taskName.includes(queryParams.taskName))
    if (queryParams.processName) filtered = filtered.filter(t => t.processName.includes(queryParams.processName))
    total.value = filtered.length
    const start = (queryParams.pageNum - 1) * queryParams.pageSize
    taskList.value = filtered.slice(start, start + queryParams.pageSize)
  } finally {
    loading.value = false
  }
}

function handleQuery() {
  queryParams.pageNum = 1
  getList()
}

function handleReset() {
  queryFormRef.value?.resetFields()
  queryParams.pageNum = 1
  getList()
}

function handleTabChange() {
  queryParams.pageNum = 1
  getList()
}

function isOverdue(dueTime: string) {
  if (!dueTime) return false
  return new Date(dueTime) < new Date()
}

function getPriorityType(p: string) {
  const map: Record<string, string> = { low: 'info', medium: '', high: 'warning', urgent: 'danger' }
  return (map[p] || '') as any
}

function getPriorityLabel(p: string) {
  const map: Record<string, string> = { low: '低', medium: '中', high: '高', urgent: '紧急' }
  return map[p] || p
}

function getTaskStatusType(s: number) {
  const map: Record<number, string> = { 2: 'success', 3: 'danger', 4: 'warning', 5: 'info' }
  return (map[s] || '') as any
}

function getTaskStatusLabel(s: number) {
  const map: Record<number, string> = { 2: '通过', 3: '驳回', 4: '转办', 5: '委派' }
  return map[s] || '未知'
}

function handleApprove(row: TaskItem) {
  currentTask.value = row
  approveForm.comment = ''
  approveVisible.value = true
}

function handleTransfer(row: TaskItem) {
  currentTask.value = row
  transferForm.targetUserId = null
  transferVisible.value = true
}

function handleViewDetail(row: TaskItem) {
  currentTask.value = row
  detailVisible.value = true
}

async function doApprove() {
  approving.value = true
  try {
    await new Promise(r => setTimeout(r, 500))
    ElMessage.success('审批通过')
    approveVisible.value = false
    getList()
  } finally {
    approving.value = false
  }
}

async function doReject() {
  if (!approveForm.comment) {
    ElMessage.warning('请输入驳回意见')
    return
  }
  approving.value = true
  try {
    await new Promise(r => setTimeout(r, 500))
    ElMessage.success('已驳回')
    approveVisible.value = false
    getList()
  } finally {
    approving.value = false
  }
}

async function doTransfer() {
  if (!transferForm.targetUserId) {
    ElMessage.warning('请选择转办人')
    return
  }
  transferring.value = true
  try {
    await new Promise(r => setTimeout(r, 500))
    ElMessage.success('转办成功')
    transferVisible.value = false
    getList()
  } finally {
    transferring.value = false
  }
}

onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.workflow-task-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-card,
.table-card {
  border-radius: 8px;
}

.mb-16 {
  margin-bottom: 16px;
}

.overdue {
  color: #f56c6c;
  font-weight: 600;
}
</style>
