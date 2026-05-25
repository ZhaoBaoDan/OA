<template>
  <div class="instance-container">
    <el-card shadow="never" class="search-card">
      <el-form :model="queryParams" ref="queryFormRef" :inline="true">
        <el-form-item label="流程名称" prop="processName">
          <el-input v-model="queryParams.processName" placeholder="请输入流程名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="发起人" prop="startUserName">
          <el-input v-model="queryParams.startUserName" placeholder="请输入发起人" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="进行中" value="running" />
            <el-option label="已完成" value="completed" />
            <el-option label="已终止" value="terminated" />
            <el-option label="已挂起" value="suspended" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <template #header>
        <div class="card-header">
          <el-button type="danger" :icon="Delete" :disabled="!selectedIds.length" @click="handleBatchDelete">批量删除</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="instanceList" border stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="processName" label="流程名称" min-width="140" show-overflow-tooltip />
        <el-table-column prop="businessKey" label="业务标识" min-width="120" show-overflow-tooltip />
        <el-table-column prop="startUserName" label="发起人" width="100" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" min-width="160" sortable />
        <el-table-column prop="endTime" label="结束时间" min-width="160" />
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">详情</el-button>
            <el-button type="warning" link :icon="RefreshRight" @click="handleTerminate(row)" v-if="row.status === 'running'">终止</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <Pagination v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="流程实例详情" width="650px" destroy-on-close>
      <template v-if="currentInstance">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="流程名称" :span="2">{{ currentInstance.processName }}</el-descriptions-item>
          <el-descriptions-item label="业务标识">{{ currentInstance.businessKey || '-' }}</el-descriptions-item>
          <el-descriptions-item label="发起人">{{ currentInstance.startUserName }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentInstance.status)" size="small">{{ getStatusLabel(currentInstance.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="开始时间">{{ currentInstance.startTime }}</el-descriptions-item>
          <el-descriptions-item label="结束时间">{{ currentInstance.endTime || '进行中' }}</el-descriptions-item>
        </el-descriptions>

        <h4 style="margin: 20px 0 12px">审批记录</h4>
        <el-timeline>
          <el-timeline-item
            v-for="node in approvalRecords"
            :key="node.id"
            :timestamp="node.createTime"
            :color="getRecordColor(node.status)"
            placement="top"
          >
            <div class="timeline-content">
              <strong>{{ node.nodeName }}</strong>
              <span class="timeline-user"> - {{ node.assigneeName }}</span>
              <el-tag :type="getRecordTagType(node.status)" size="small" style="margin-left: 8px">{{ getRecordLabel(node.status) }}</el-tag>
              <p v-if="node.comment" class="timeline-comment">{{ node.comment }}</p>
            </div>
          </el-timeline-item>
        </el-timeline>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { Search, Refresh, Delete, View, RefreshRight } from '@element-plus/icons-vue'
import { listProcessInst, getProcessInst, getApprovalRecords, terminateProcessInst, deleteProcessInst } from '@/api/workflow'
import type { ProcessInst, ApprovalRecord } from '@/types/business'
import Pagination from '@/components/Pagination.vue'

const loading = ref(false)
const instanceList = ref<ProcessInst[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])
const detailVisible = ref(false)
const currentInstance = ref<ProcessInst | null>(null)
const approvalRecords = ref<ApprovalRecord[]>([])

const queryParams = reactive({ processName: '', startUserName: '', status: '', pageNum: 1, pageSize: 20 })
const queryFormRef = ref<FormInstance>()

async function getList() {
  loading.value = true
  try {
    const res = await listProcessInst(queryParams)
    instanceList.value = res.data.rows
    total.value = res.data.total
  } catch { /* handled by interceptor */ } finally {
    loading.value = false
  }
}

function handleQuery() { queryParams.pageNum = 1; getList() }
function handleReset() { queryFormRef.value?.resetFields(); queryParams.pageNum = 1; getList() }
function handleSelectionChange(rows: ProcessInst[]) { selectedIds.value = rows.map(r => r.id) }

function getStatusType(status: string) {
  const map: Record<string, string> = { running: '', completed: 'success', terminated: 'danger', suspended: 'warning' }
  return (map[status] || 'info') as any
}
function getStatusLabel(status: string) {
  const map: Record<string, string> = { running: '进行中', completed: '已完成', terminated: '已终止', suspended: '已挂起' }
  return map[status] || status
}
function getRecordColor(status: string) {
  const map: Record<string, string> = { pending: '#e6a23c', approved: '#67c23a', rejected: '#f56c6c' }
  return map[status] || '#909399'
}
function getRecordTagType(status: string) {
  const map: Record<string, string> = { pending: 'warning', approved: 'success', rejected: 'danger' }
  return (map[status] || 'info') as any
}
function getRecordLabel(status: string) {
  const map: Record<string, string> = { pending: '待审批', approved: '已通过', rejected: '已驳回' }
  return map[status] || status
}

async function handleView(row: ProcessInst) {
  currentInstance.value = row
  try {
    const res = await getApprovalRecords(row.id)
    approvalRecords.value = res.data
  } catch { approvalRecords.value = [] }
  detailVisible.value = true
}

async function handleTerminate(row: ProcessInst) {
  await ElMessageBox.confirm(`确定要终止流程「${row.processName}」吗？`, '提示', { type: 'warning' })
  try {
    await terminateProcessInst(row.id)
    ElMessage.success('流程已终止')
    getList()
  } catch { /* handled by interceptor */ }
}

async function handleDelete(row: ProcessInst) {
  await ElMessageBox.confirm('确定要删除该流程实例吗？', '提示', { type: 'warning' })
  try {
    await deleteProcessInst(String(row.id))
    ElMessage.success('删除成功')
    getList()
  } catch { /* handled by interceptor */ }
}

async function handleBatchDelete() {
  await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 条记录吗？`, '提示', { type: 'warning' })
  try {
    await deleteProcessInst(selectedIds.value.join(','))
    ElMessage.success('批量删除成功')
    getList()
  } catch { /* handled by interceptor */ }
}

onMounted(() => { getList() })
</script>

<style lang="scss" scoped>
.instance-container { display: flex; flex-direction: column; gap: 16px; }
.search-card, .table-card { border-radius: 8px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }

.timeline-content {
  .timeline-user { color: #909399; font-size: 13px; }
  .timeline-comment { margin: 6px 0 0; font-size: 13px; color: #606266; }
}
</style>
