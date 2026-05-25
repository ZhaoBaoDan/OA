<template>
  <div class="post-container">
    <el-card shadow="never" class="search-card">
      <el-form :model="queryParams" ref="queryFormRef" :inline="true">
        <el-form-item label="岗位编码" prop="postCode">
          <el-input v-model="queryParams.postCode" placeholder="请输入岗位编码" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="岗位名称" prop="postName">
          <el-input v-model="queryParams.postName" placeholder="请输入岗位名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="停用" :value="0" />
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
          <el-button type="primary" :icon="Plus" @click="handleAdd">新增岗位</el-button>
          <el-button type="danger" :icon="Delete" :disabled="!selectedIds.length" @click="handleBatchDelete">批量删除</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="postList" border stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="postCode" label="岗位编码" min-width="120" show-overflow-tooltip />
        <el-table-column prop="postName" label="岗位名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <Pagination v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" :total="total" @pagination="getList" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增岗位' : '编辑岗位'" width="500px" append-to-body destroy-on-close>
      <el-form ref="postFormRef" :model="postForm" :rules="postFormRules" label-width="80px">
        <el-form-item label="岗位名称" prop="postName">
          <el-input v-model="postForm.postName" placeholder="请输入岗位名称" />
        </el-form-item>
        <el-form-item label="岗位编码" prop="postCode">
          <el-input v-model="postForm.postCode" placeholder="请输入岗位编码" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="postForm.sort" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="postForm.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="postForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { listPost, addPost, updatePost, deletePost } from '@/api/system/post'
import type { PostInfo } from '@/types/system'
import Pagination from '@/components/Pagination.vue'

const loading = ref(false)
const submitLoading = ref(false)
const postList = ref<PostInfo[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])

const queryParams = reactive({ postCode: '', postName: '', status: undefined as number | undefined, pageNum: 1, pageSize: 20 })
const queryFormRef = ref<FormInstance>()

const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const postFormRef = ref<FormInstance>()
const postForm = ref<Partial<PostInfo>>({ postCode: '', postName: '', sort: 0, status: 1, remark: '' })

const postFormRules: FormRules = {
  postName: [{ required: true, message: '请输入岗位名称', trigger: 'blur' }],
  postCode: [{ required: true, message: '请输入岗位编码', trigger: 'blur' }],
  sort: [{ required: true, message: '请输入排序', trigger: 'blur' }],
}

async function getList() {
  loading.value = true
  try {
    const res = await listPost(queryParams)
    postList.value = res.data.rows
    total.value = res.data.total
  } catch { /* handled by interceptor */ } finally {
    loading.value = false
  }
}

function handleQuery() { queryParams.pageNum = 1; getList() }
function handleReset() { queryFormRef.value?.resetFields(); queryParams.pageNum = 1; getList() }
function handleSelectionChange(rows: PostInfo[]) { selectedIds.value = rows.map(r => r.postId) }

function handleAdd() {
  dialogType.value = 'add'
  postForm.value = { postCode: '', postName: '', sort: 0, status: 1, remark: '' }
  dialogVisible.value = true
}

function handleEdit(row: PostInfo) {
  dialogType.value = 'edit'
  postForm.value = { ...row }
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!postFormRef.value) return
  await postFormRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (dialogType.value === 'add') {
        await addPost(postForm.value as any)
        ElMessage.success('新增成功')
      } else {
        await updatePost(postForm.value as any)
        ElMessage.success('修改成功')
      }
      dialogVisible.value = false
      getList()
    } catch { /* handled by interceptor */ } finally {
      submitLoading.value = false
    }
  })
}

async function handleDelete(row: PostInfo) {
  await ElMessageBox.confirm(`确定要删除岗位「${row.postName}」吗？`, '提示', { type: 'warning' })
  try {
    await deletePost(String(row.postId))
    ElMessage.success('删除成功')
    getList()
  } catch { /* handled by interceptor */ }
}

async function handleBatchDelete() {
  await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个岗位吗？`, '提示', { type: 'warning' })
  try {
    await deletePost(selectedIds.value.join(','))
    ElMessage.success('批量删除成功')
    getList()
  } catch { /* handled by interceptor */ }
}

async function handleStatusChange(row: PostInfo) {
  try {
    await updatePost(row as any)
    ElMessage.success('状态修改成功')
  } catch { row.status = row.status === 1 ? 0 : 1 }
}

onMounted(() => { getList() })
</script>

<style lang="scss" scoped>
.post-container { display: flex; flex-direction: column; gap: 16px; }
.search-card, .table-card { border-radius: 8px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
