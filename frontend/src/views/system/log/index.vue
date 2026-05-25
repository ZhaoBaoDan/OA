<template>
  <div class="log-container">
    <el-card shadow="never" class="search-card">
      <el-form :model="queryParams" ref="queryFormRef" :inline="true">
        <el-form-item label="操作人" prop="operName">
          <el-input v-model="queryParams.operName" placeholder="请输入操作人" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="操作状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作时间" prop="dateRange">
          <el-date-picker v-model="queryParams.dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" />
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
          <div>
            <el-button type="danger" :icon="Delete" :disabled="!selectedIds.length" @click="handleBatchDelete">批量删除</el-button>
            <el-button type="danger" :icon="Delete" plain @click="handleClearAll">清空全部</el-button>
          </div>
        </div>
      </template>

      <el-table v-loading="loading" :data="logList" border stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="title" label="模块标题" min-width="120" show-overflow-tooltip />
        <el-table-column prop="method" label="方法" min-width="100" show-overflow-tooltip />
        <el-table-column prop="operUrl" label="请求URL" min-width="160" show-overflow-tooltip />
        <el-table-column prop="operName" label="操作人" width="100" />
        <el-table-column prop="operIp" label="操作IP" width="130" />
        <el-table-column prop="operTime" label="操作时间" min-width="160" sortable />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '成功' : '失败' }}</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <Pagination v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import { Search, Refresh, Delete } from '@element-plus/icons-vue'
import { listLog, deleteLog, cleanLog } from '@/api/system/log'
import type { OperLog } from '@/types/system'
import Pagination from '@/components/Pagination.vue'

const loading = ref(false)
const logList = ref<OperLog[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])

const queryParams = reactive({ operName: '', status: undefined as number | undefined, dateRange: [] as string[], pageNum: 1, pageSize: 20 })
const queryFormRef = ref<FormInstance>()

async function getList() {
  loading.value = true
  try {
    const res = await listLog(queryParams)
    logList.value = res.data.rows
    total.value = res.data.total
  } catch { /* handled by interceptor */ } finally {
    loading.value = false
  }
}

function handleQuery() { queryParams.pageNum = 1; getList() }
function handleReset() { queryFormRef.value?.resetFields(); queryParams.pageNum = 1; getList() }
function handleSelectionChange(rows: OperLog[]) { selectedIds.value = rows.map(r => r.operId) }

async function handleBatchDelete() {
  await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 条日志吗？`, '提示', { type: 'warning' })
  try {
    await deleteLog(selectedIds.value.join(','))
    ElMessage.success('批量删除成功')
    getList()
  } catch { /* handled by interceptor */ }
}

async function handleClearAll() {
  await ElMessageBox.confirm('确定要清空全部操作日志吗？此操作不可恢复！', '警告', { type: 'error' })
  try {
    await cleanLog()
    ElMessage.success('清空成功')
    getList()
  } catch { /* handled by interceptor */ }
}

onMounted(() => { getList() })
</script>

<style lang="scss" scoped>
.log-container { display: flex; flex-direction: column; gap: 16px; }
.search-card, .table-card { border-radius: 8px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
