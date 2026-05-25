<template>
  <div class="dict-container">
    <el-row :gutter="16">
      <!-- 左侧：字典类型 -->
      <el-col :span="10">
        <el-card shadow="never">
          <template #header>
            <div class="flex justify-between items-center">
              <span>字典类型</span>
              <div>
                <el-button size="small" @click="refreshCache" :loading="cacheLoading"><el-icon><Refresh /></el-icon>刷新缓存</el-button>
                <el-button type="primary" size="small" @click="handleAddType"><el-icon><Plus /></el-icon>新增</el-button>
              </div>
            </div>
          </template>
          <el-input v-model="typeFilter" placeholder="搜索字典类型" clearable class="mb-3">
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <el-table :data="filteredTypeList" v-loading="typeLoading" highlight-current-row @current-change="handleTypeSelect" border stripe max-height="500">
            <el-table-column prop="dictName" label="字典名称" />
            <el-table-column prop="dictType" label="字典类型">
              <template #default="{ row }">
                <el-tag size="small">{{ row.dictType }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="70" align="center">
              <template #default="{ row }">
                <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="small">{{ row.status === 0 ? '正常' : '停用' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" align="center">
              <template #default="{ row }">
                <el-button link type="primary" size="small" @click.stop="handleEditType(row)">编辑</el-button>
                <el-button link type="danger" size="small" @click.stop="handleDeleteType(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 右侧：字典数据 -->
      <el-col :span="14">
        <el-card shadow="never">
          <template #header>
            <div class="flex justify-between items-center">
              <span>字典数据
                <el-tag v-if="currentType" class="ml-2" type="primary">{{ currentType.dictName }}</el-tag>
                <el-tag v-if="currentType" class="ml-1" size="small">{{ currentType.dictType }}</el-tag>
              </span>
              <el-button type="primary" size="small" :disabled="!currentType" @click="handleAddData"><el-icon><Plus /></el-icon>新增</el-button>
            </div>
          </template>

          <div v-if="!currentType" class="empty-hint">
            <el-empty description="请在左侧选择字典类型" />
          </div>
          <template v-else>
            <el-table :data="dataList" v-loading="dataLoading" border stripe row-key="id">
              <el-table-column prop="dictLabel" label="字典标签" width="150">
                <template #default="{ row }">
                  <span :style="{ color: row.cssClass || '' }">{{ row.dictLabel }}</span>
                  <el-tag v-if="row.cssClass" :color="row.cssClass" size="small" class="ml-1" style="color:#fff">{{ row.dictLabel }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="dictValue" label="字典值" width="120">
                <template #default="{ row }"><el-tag>{{ row.dictValue }}</el-tag></template>
              </el-table-column>
              <el-table-column prop="sort" label="排序" width="70" align="center" />
              <el-table-column prop="status" label="状态" width="70" align="center">
                <template #default="{ row }">
                  <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="small">{{ row.status === 0 ? '正常' : '停用' }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="remark" label="备注" show-overflow-tooltip />
              <el-table-column label="操作" width="120" align="center">
                <template #default="{ row }">
                  <el-button link type="primary" size="small" @click="handleEditData(row)">编辑</el-button>
                  <el-button link type="danger" size="small" @click="handleDeleteData(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </template>
        </el-card>
      </el-col>
    </el-row>

    <!-- 字典类型弹窗 -->
    <el-dialog v-model="typeDialogVisible" :title="typeDialogTitle" width="550px" destroy-on-close>
      <el-form ref="typeFormRef" :model="typeForm" :rules="typeRules" label-width="100px">
        <el-form-item label="字典名称" prop="dictName"><el-input v-model="typeForm.dictName" placeholder="请输入" /></el-form-item>
        <el-form-item label="字典类型" prop="dictType">
          <el-input v-model="typeForm.dictType" placeholder="如: sys_user_sex" :disabled="!!typeForm.id" />
          <div class="form-tip">建议格式: 模块_功能_类型，如 sys_user_sex</div>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="typeForm.status"><el-radio :value="0">正常</el-radio><el-radio :value="1">停用</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="typeForm.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="typeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitType">确定</el-button>
      </template>
    </el-dialog>

    <!-- 字典数据弹窗 -->
    <el-dialog v-model="dataDialogVisible" :title="dataDialogTitle" width="550px" destroy-on-close>
      <el-form ref="dataFormRef" :model="dataForm" :rules="dataRules" label-width="100px">
        <el-form-item label="字典标签" prop="dictLabel"><el-input v-model="dataForm.dictLabel" placeholder="显示名称" /></el-form-item>
        <el-form-item label="字典值" prop="dictValue"><el-input v-model="dataForm.dictValue" placeholder="存储值" /></el-form-item>
        <el-form-item label="标签颜色">
          <el-color-picker v-model="dataForm.cssClass" />
        </el-form-item>
        <el-form-item label="显示排序" prop="sort"><el-input-number v-model="dataForm.sort" :min="0" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="dataForm.status"><el-radio :value="0">正常</el-radio><el-radio :value="1">停用</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item label="备注"><el-input v-model="dataForm.remark" type="textarea" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dataDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitData">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'

// --- 字典类型 ---
const typeLoading = ref(false)
const cacheLoading = ref(false)
const typeList = ref<any[]>([])
const typeFilter = ref('')
const currentType = ref<any>(null)
const typeDialogVisible = ref(false)
const typeDialogTitle = ref('')
const typeFormRef = ref<FormInstance>()
const typeForm = reactive({ id: undefined as number | undefined, dictName: '', dictType: '', status: 0, remark: '' })
const typeRules = {
  dictName: [{ required: true, message: '请输入字典名称', trigger: 'blur' }],
  dictType: [{ required: true, message: '请输入字典类型', trigger: 'blur' }, { pattern: /^[a-zA-Z_]+$/, message: '只能包含字母和下划线', trigger: 'blur' }]
}

const filteredTypeList = computed(() => {
  if (!typeFilter.value) return typeList.value
  return typeList.value.filter(t => t.dictName.includes(typeFilter.value) || t.dictType.includes(typeFilter.value))
})

const getTypeList = async () => {
  typeLoading.value = true
  try {
    const res = await fetch('/api/system/dict/type?pageNum=1&pageSize=200').then(r => r.json())
    if (res.code === 200) typeList.value = res.data.records || res.data
  } finally { typeLoading.value = false }
}

const handleTypeSelect = (row: any) => { if (row) { currentType.value = row; getDataList() } }
const handleAddType = () => { Object.assign(typeForm, { id: undefined, dictName: '', dictType: '', status: 0, remark: '' }); typeDialogTitle.value = '新增字典类型'; typeDialogVisible.value = true }
const handleEditType = (row: any) => { Object.assign(typeForm, row); typeDialogTitle.value = '编辑字典类型'; typeDialogVisible.value = true }

const handleSubmitType = async () => {
  await typeFormRef.value?.validate()
  const method = typeForm.id ? 'PUT' : 'POST'
  const res = await fetch('/api/system/dict/type', { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(typeForm) }).then(r => r.json())
  if (res.code === 200) { ElMessage.success('成功'); typeDialogVisible.value = false; getTypeList() }
}

const handleDeleteType = (row: any) => {
  ElMessageBox.confirm(`确认删除字典类型"${row.dictName}"? 删除后关联的字典数据也将被清除。`, '警告', { type: 'warning' }).then(async () => {
    const res = await fetch(`/api/system/dict/type/${row.id}`, { method: 'DELETE' }).then(r => r.json())
    if (res.code === 200) { ElMessage.success('删除成功'); getTypeList(); currentType.value = null; dataList.value = [] }
  }).catch(() => {})
}

const refreshCache = async () => {
  cacheLoading.value = true
  try {
    const res = await fetch('/api/system/dict/cache/refresh', { method: 'PUT' }).then(r => r.json())
    if (res.code === 200) ElMessage.success('缓存刷新成功')
  } finally { cacheLoading.value = false }
}

// --- 字典数据 ---
const dataLoading = ref(false)
const dataList = ref<any[]>([])
const dataDialogVisible = ref(false)
const dataDialogTitle = ref('')
const dataFormRef = ref<FormInstance>()
const dataForm = reactive({ id: undefined as number | undefined, dictType: '', dictLabel: '', dictValue: '', cssClass: '', sort: 0, status: 0, remark: '' })
const dataRules = {
  dictLabel: [{ required: true, message: '请输入字典标签', trigger: 'blur' }],
  dictValue: [{ required: true, message: '请输入字典值', trigger: 'blur' }]
}

const getDataList = async () => {
  if (!currentType.value) return
  dataLoading.value = true
  try {
    const res = await fetch(`/api/system/dict/data/type/${currentType.value.dictType}`).then(r => r.json())
    if (res.code === 200) dataList.value = res.data
  } finally { dataLoading.value = false }
}

const handleAddData = () => {
  Object.assign(dataForm, { id: undefined, dictType: currentType.value.dictType, dictLabel: '', dictValue: '', cssClass: '', sort: 0, status: 0, remark: '' })
  dataDialogTitle.value = '新增字典数据'
  dataDialogVisible.value = true
}
const handleEditData = (row: any) => { Object.assign(dataForm, row); dataDialogTitle.value = '编辑字典数据'; dataDialogVisible.value = true }

const handleSubmitData = async () => {
  await dataFormRef.value?.validate()
  const method = dataForm.id ? 'PUT' : 'POST'
  const res = await fetch('/api/system/dict/data', { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(dataForm) }).then(r => r.json())
  if (res.code === 200) { ElMessage.success('成功'); dataDialogVisible.value = false; getDataList() }
}

const handleDeleteData = (row: any) => {
  ElMessageBox.confirm(`确认删除"${row.dictLabel}"?`, '提示', { type: 'warning' }).then(async () => {
    const res = await fetch(`/api/system/dict/data/${row.id}`, { method: 'DELETE' }).then(r => r.json())
    if (res.code === 200) { ElMessage.success('删除成功'); getDataList() }
  }).catch(() => {})
}

onMounted(() => getTypeList())
</script>

<style scoped>
.dict-container { padding: 16px; }
.mb-3 { margin-bottom: 12px; }
.ml-1 { margin-left: 4px; }
.ml-2 { margin-left: 8px; }
.form-tip { font-size: 12px; color: #909399; line-height: 1.5; }
.empty-hint { padding: 40px 0; }
</style>
