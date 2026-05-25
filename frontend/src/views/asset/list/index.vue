<template>
  <div class="asset-container">
    <!-- 统计卡片 -->
    <el-row :gutter="16" class="mb-4">
      <el-col :span="6" v-for="stat in stats" :key="stat.label">
        <el-card shadow="hover">
          <el-statistic :title="stat.label" :value="stat.value">
            <template #prefix><el-icon :style="{ color: stat.color }"><component :is="stat.icon" /></el-icon></template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="资产编码">
          <el-input v-model="queryParams.assetCode" placeholder="请输入" clearable style="width:150px" />
        </el-form-item>
        <el-form-item label="资产名称">
          <el-input v-model="queryParams.name" placeholder="请输入" clearable style="width:150px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:100px">
            <el-option label="闲置" :value="0" />
            <el-option label="在用" :value="1" />
            <el-option label="维修" :value="2" />
            <el-option label="报废" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
          <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
        </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb-4">
        <el-col :span="1.5">
          <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增资产</el-button>
        </el-col>
      </el-row>

      <el-table :data="assetList" v-loading="loading" border stripe>
        <el-table-column prop="assetCode" label="资产编码" width="150">
          <template #default="{ row }"><el-tag>{{ row.assetCode }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="name" label="资产名称" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="purchasePrice" label="原值" width="100" align="right">
          <template #default="{ row }">¥{{ row.purchasePrice?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="currentValue" label="净值" width="100" align="right">
          <template #default="{ row }">¥{{ row.currentValue?.toLocaleString() }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="使用人" width="100" />
        <el-table-column prop="location" label="位置" width="120" />
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 0" link type="success" @click="handleAssign(row)">领用</el-button>
            <el-button v-if="row.status === 1" link type="warning" @click="handleReturn(row)">归还</el-button>
            <el-button v-if="row.status !== 3" link type="danger" @click="handleScrap(row)">报废</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="mt-4 justify-end" v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize" :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next" @change="getList" />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="资产名称" prop="name"><el-input v-model="form.name" placeholder="请输入" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="选择分类" style="width:100%">
                <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格型号"><el-input v-model="form.specification" placeholder="请输入" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品牌"><el-input v-model="form.brand" placeholder="请输入" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="购入价格" prop="purchasePrice"><el-input-number v-model="form.purchasePrice" :min="0" :precision="2" style="width:100%" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="购入日期"><el-date-picker v-model="form.purchaseDate" type="date" value-format="YYYY-MM-DD" style="width:100%" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="存放位置"><el-input v-model="form.location" placeholder="请输入" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商"><el-input v-model="form.supplier" placeholder="请输入" /></el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { Search, Refresh, Plus, Box, Warning, CircleCheck, Goods } from '@element-plus/icons-vue'

const loading = ref(false)
const assetList = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({ assetCode: '', name: '', status: undefined as number | undefined, pageNum: 1, pageSize: 10 })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const form = reactive({ id: undefined as number | undefined, name: '', categoryId: undefined as number | undefined, specification: '', brand: '', purchasePrice: 0, purchaseDate: '', location: '', supplier: '', remark: '' })
const rules = { name: [{ required: true, message: '请输入资产名称', trigger: 'blur' }], purchasePrice: [{ required: true, message: '请输入价格', trigger: 'blur' }] }
const categories = ref([{ id: 1, name: '电子设备' }, { id: 2, name: '办公家具' }, { id: 3, name: '车辆' }, { id: 4, name: '其他' }])

const stats = [
  { label: '总资产', value: 156, color: '#409eff', icon: Box },
  { label: '闲置中', value: 42, color: '#67c23a', icon: CircleCheck },
  { label: '使用中', value: 98, color: '#e6a23c', icon: Goods },
  { label: '已报废', value: 16, color: '#909399', icon: Warning },
]

const statusType = (s: number) => ['', 'success', 'warning', 'info'][s] as any
const statusLabel = (s: number) => ['闲置', '在用', '维修', '报废'][s]

const getList = async () => {
  loading.value = true
  try {
    const res = await fetch(`/api/asset?pageNum=${queryParams.pageNum}&pageSize=${queryParams.pageSize}&assetCode=${queryParams.assetCode}&name=${queryParams.name}&status=${queryParams.status ?? ''}`).then(r => r.json()).catch(() => ({
      code: 200, data: { records: [
        { id: 1, assetCode: 'AST20260501001', name: 'MacBook Pro 16寸', categoryName: '电子设备', purchasePrice: 18999, currentValue: 15199, status: 1, userName: '张三', location: 'A座3楼' },
        { id: 2, assetCode: 'AST20260501002', name: 'ThinkPad X1', categoryName: '电子设备', purchasePrice: 12999, currentValue: 10399, status: 0, userName: '', location: '仓库A' },
        { id: 3, assetCode: 'AST20260502001', name: '办公桌', categoryName: '办公家具', purchasePrice: 2500, currentValue: 2000, status: 1, userName: '李四', location: 'B座2楼' },
      ], total: 3 }
    }))
    if (res.code === 200) { assetList.value = res.data.records; total.value = res.data.total }
  } finally { loading.value = false }
}

const handleQuery = () => { queryParams.pageNum = 1; getList() }
const resetQuery = () => { queryParams.assetCode = ''; queryParams.name = ''; queryParams.status = undefined; handleQuery() }

const handleAdd = () => {
  Object.assign(form, { id: undefined, name: '', categoryId: undefined, specification: '', brand: '', purchasePrice: 0, purchaseDate: '', location: '', supplier: '', remark: '' })
  dialogTitle.value = '新增资产'
  dialogVisible.value = true
}

const handleEdit = (row: any) => { Object.assign(form, row); dialogTitle.value = '编辑资产'; dialogVisible.value = true }

const handleSubmit = async () => {
  await formRef.value?.validate()
  const method = form.id ? 'PUT' : 'POST'
  const res = await fetch('/api/asset', { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(form) }).then(r => r.json())
  if (res.code === 200) { ElMessage.success(form.id ? '修改成功' : '新增成功'); dialogVisible.value = false; getList() }
}

const handleAssign = (row: any) => {
  ElMessageBox.prompt('请输入领用人ID', '资产领用', { confirmButtonText: '确定', cancelButtonText: '取消' }).then(async ({ value }) => {
    const res = await fetch(`/api/asset/${row.id}/assign?userId=${value}&userName=用户${value}`, { method: 'POST' }).then(r => r.json())
    if (res.code === 200) { ElMessage.success('领用成功'); getList() }
  }).catch(() => {})
}

const handleReturn = (row: any) => {
  ElMessageBox.confirm(`确认归还资产"${row.name}"?`, '提示', { type: 'warning' }).then(async () => {
    const res = await fetch(`/api/asset/${row.id}/return`, { method: 'POST' }).then(r => r.json())
    if (res.code === 200) { ElMessage.success('归还成功'); getList() }
  }).catch(() => {})
}

const handleScrap = (row: any) => {
  ElMessageBox.confirm(`确认报废资产"${row.name}"? 此操作不可逆。`, '危险操作', { type: 'error' }).then(async () => {
    const res = await fetch(`/api/asset/${row.id}/scrap`, { method: 'POST' }).then(r => r.json())
    if (res.code === 200) { ElMessage.success('报废成功'); getList() }
  }).catch(() => {})
}

onMounted(() => getList())
</script>

<style scoped>
.asset-container { padding: 16px; }
.mb-4 { margin-bottom: 16px; }
.mt-4 { margin-top: 16px; }
</style>
