<template>
  <div class="meeting-record">
    <el-card shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="会议主题">
          <el-input v-model="queryParams.title" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
          <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
          <el-button type="success" @click="handleAdd"><el-icon><Plus /></el-icon>新增纪要</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="recordList" v-loading="loading" border stripe>
        <el-table-column prop="title" label="会议主题" min-width="180" />
        <el-table-column prop="roomName" label="会议室" width="120" />
        <el-table-column prop="organizer" label="组织者" width="100" />
        <el-table-column prop="attendees" label="参会人" width="180" show-overflow-tooltip />
        <el-table-column prop="meetingDate" label="会议日期" width="120" />
        <el-table-column label="时间" width="130">
          <template #default="{ row }">{{ row.startTime }} ~ {{ row.endTime }}</template>
        </el-table-column>
        <el-table-column prop="recorderName" label="记录人" width="100" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button link type="warning" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="mt-4 justify-end" v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize" :total="total" layout="total, prev, pager, next, sizes" @change="getList" />
    </el-card>

    <!-- 纪要弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" destroy-on-close>
      <!-- 查看模式 -->
      <el-descriptions v-if="mode === 'view'" :column="2" border>
        <el-descriptions-item label="会议主题" :span="2">{{ currentRecord.title }}</el-descriptions-item>
        <el-descriptions-item label="会议室">{{ currentRecord.roomName }}</el-descriptions-item>
        <el-descriptions-item label="组织者">{{ currentRecord.organizer }}</el-descriptions-item>
        <el-descriptions-item label="会议日期">{{ currentRecord.meetingDate }}</el-descriptions-item>
        <el-descriptions-item label="时间">{{ currentRecord.startTime }} ~ {{ currentRecord.endTime }}</el-descriptions-item>
        <el-descriptions-item label="参会人" :span="2">{{ currentRecord.attendees }}</el-descriptions-item>
        <el-descriptions-item label="记录人">{{ currentRecord.recorderName }}</el-descriptions-item>
        <el-descriptions-item label="纪要内容" :span="2">
          <div style="white-space:pre-wrap;min-height:60px">{{ currentRecord.content || '暂无纪要' }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="待办事项" :span="2">
          <div style="white-space:pre-wrap">{{ currentRecord.actionItems || '无' }}</div>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 编辑/新增模式 -->
      <el-form v-else ref="formRef" :model="formData" :rules="rules" label-width="90px">
        <el-form-item label="会议主题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入会议主题" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="会议日期" prop="meetingDate">
              <el-date-picker v-model="formData.meetingDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="开始" prop="startTime">
              <el-time-picker v-model="formData.startTime" format="HH:mm" value-format="HH:mm" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="结束" prop="endTime">
              <el-time-picker v-model="formData.endTime" format="HH:mm" value-format="HH:mm" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="参会人">
          <el-input v-model="formData.attendees" placeholder="请输入参会人" />
        </el-form-item>
        <el-form-item label="纪要内容">
          <el-input v-model="formData.content" type="textarea" :rows="6" placeholder="请输入会议纪要..." />
        </el-form-item>
        <el-form-item label="待办事项">
          <el-input v-model="formData.actionItems" type="textarea" :rows="3" placeholder="请输入待办事项..." />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">{{ mode === 'view' ? '关闭' : '取消' }}</el-button>
        <el-button v-if="mode !== 'view'" type="primary" @click="handleSubmit" :loading="submitting">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { listMinutes, createMinutes, updateMinutes, deleteMinutes } from '@/api/meeting'

const loading = ref(false)
const submitting = ref(false)
const recordList = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({ title: '', pageNum: 1, pageSize: 10 })
const dialogVisible = ref(false)
const mode = ref<'view' | 'add' | 'edit'>('view')
const currentRecord = ref<any>({})
const formRef = ref()

const dialogTitle = computed(() => {
  const map = { view: '会议纪要', add: '新增纪要', edit: '编辑纪要' }
  return map[mode.value]
})

const formData = reactive({
  id: null as number | null,
  title: '',
  meetingDate: '',
  startTime: '',
  endTime: '',
  attendees: '',
  content: '',
  actionItems: '',
})

const rules = {
  title: [{ required: true, message: '请输入会议主题', trigger: 'blur' }],
  meetingDate: [{ required: true, message: '请选择会议日期', trigger: 'change' }],
}

async function getList() {
  loading.value = true
  try {
    const { data } = await listMinutes({ ...queryParams })
    recordList.value = data?.records || []
    total.value = data?.total || 0
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function handleQuery() { queryParams.pageNum = 1; getList() }
function resetQuery() { queryParams.title = ''; handleQuery() }

function handleView(row: any) {
  currentRecord.value = row
  mode.value = 'view'
  dialogVisible.value = true
}

function handleAdd() {
  Object.assign(formData, { id: null, title: '', meetingDate: '', startTime: '', endTime: '', attendees: '', content: '', actionItems: '' })
  mode.value = 'add'
  dialogVisible.value = true
}

function handleEdit(row: any) {
  Object.assign(formData, { ...row })
  mode.value = 'edit'
  dialogVisible.value = true
}

async function handleDelete(row: any) {
  await ElMessageBox.confirm('确定删除该会议纪要？', '提示', { type: 'warning' })
  try {
    await deleteMinutes(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

async function handleSubmit() {
  try {
    await formRef.value?.validate()
  } catch {
    return
  }
  submitting.value = true
  try {
    if (mode.value === 'add') {
      await createMinutes(formData)
      ElMessage.success('新增成功')
    } else {
      await updateMinutes(formData)
      ElMessage.success('修改成功')
    }
    dialogVisible.value = false
    getList()
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => getList())
</script>

<style scoped>
.meeting-record { padding: 16px; }
.mt-4 { margin-top: 16px; }
.justify-end { display: flex; justify-content: flex-end; }
</style>
