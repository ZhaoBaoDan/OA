<template>
  <div class="user-container">
    <el-row :gutter="16">
      <!-- 左侧部门树 -->
      <el-col :span="5">
        <el-card shadow="never" class="dept-card">
          <template #header><span>部门筛选</span></template>
          <el-input v-model="deptFilter" placeholder="搜索部门" clearable size="small" class="mb-2">
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <el-tree
            ref="deptTreeRef"
            :data="deptTree"
            :props="{ label: 'deptName', children: 'children' }"
            node-key="id"
            default-expand-all
            highlight-current
            :filter-node-method="filterDeptNode"
            @current-change="handleDeptSelect"
          />
        </el-card>
      </el-col>

      <!-- 右侧用户列表 -->
      <el-col :span="19">
        <el-card shadow="never">
          <el-form :model="queryParams" inline>
            <el-form-item label="用户名">
              <el-input v-model="queryParams.username" placeholder="请输入" clearable style="width:150px" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="queryParams.phone" placeholder="请输入" clearable style="width:150px" />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:100px">
                <el-option label="正常" :value="0" />
                <el-option label="停用" :value="1" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
              <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
            </el-form-item>
          </el-form>

          <el-row :gutter="10" class="mb-4">
            <el-col :span="1.5">
              <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增用户</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button type="success" @click="handleImport"><el-icon><Upload /></el-icon>导入</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button type="warning" @click="handleExport"><el-icon><Download /></el-icon>导出</el-button>
            </el-col>
          </el-row>

          <el-table :data="userList" v-loading="loading" border stripe @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column prop="avatar" label="头像" width="60" align="center">
              <template #default="{ row }">
                <el-avatar :size="32" :src="row.avatar">{{ row.nickname?.[0] }}</el-avatar>
              </template>
            </el-table-column>
            <el-table-column prop="username" label="用户名" width="120">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleDetail(row)">{{ row.username }}</el-button>
              </template>
            </el-table-column>
            <el-table-column prop="nickname" label="昵称" width="120" />
            <el-table-column prop="deptName" label="部门" width="120" />
            <el-table-column prop="phone" label="手机号" width="130" />
            <el-table-column prop="email" label="邮箱" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="80" align="center">
              <template #default="{ row }">
                <el-switch v-model="row.status" :active-value="0" :inactive-value="1" @change="handleStatusChange(row)" />
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="170" />
            <el-table-column label="操作" width="200" align="center" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
                <el-button link type="warning" @click="handleResetPwd(row)">重置密码</el-button>
                <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination class="mt-4 justify-end" v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize" :total="total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" @change="getList" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" placeholder="请输入" :disabled="!!form.id" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="form.nickname" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="!form.id">
            <el-form-item label="密码" prop="password">
              <el-input v-model="form.password" type="password" placeholder="请输入" show-password />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门" prop="deptId">
              <el-tree-select v-model="form.deptId" :data="deptTree" :props="{ label: 'deptName', value: 'id', children: 'children' }" placeholder="选择部门" check-strictly clearable style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-radio-group v-model="form.gender">
                <el-radio :value="0">未知</el-radio>
                <el-radio :value="1">男</el-radio>
                <el-radio :value="2">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio :value="0">正常</el-radio>
                <el-radio :value="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="角色" prop="roleIds">
              <el-select v-model="form.roleIds" multiple placeholder="选择角色" style="width:100%">
                <el-option v-for="role in roleOptions" :key="role.id" :label="role.roleName" :value="role.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位">
              <el-select v-model="form.postIds" multiple placeholder="选择岗位" style="width:100%">
                <el-option v-for="post in postOptions" :key="post.id" :label="post.postName" :value="post.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" :rows="2" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 用户详情抽屉 -->
    <el-drawer v-model="detailVisible" :title="detailUser?.nickname" size="450px">
      <template v-if="detailUser">
        <div class="detail-header">
          <el-avatar :size="64" :src="detailUser.avatar">{{ detailUser.nickname?.[0] }}</el-avatar>
          <h3>{{ detailUser.nickname }}</h3>
          <el-tag :type="detailUser.status === 0 ? 'success' : 'danger'">{{ detailUser.status === 0 ? '正常' : '停用' }}</el-tag>
        </div>
        <el-divider />
        <el-descriptions :column="1" border>
          <el-descriptions-item label="用户ID">{{ detailUser.id }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ detailUser.username }}</el-descriptions-item>
          <el-descriptions-item label="部门">{{ detailUser.deptName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ detailUser.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ detailUser.email || '-' }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ ['未知', '男', '女'][detailUser.gender || 0] }}</el-descriptions-item>
          <el-descriptions-item label="角色">
            <el-tag v-for="role in detailUser.roles" :key="role.id" class="mr-1" size="small">{{ role.roleName }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ detailUser.createTime }}</el-descriptions-item>
          <el-descriptions-item label="最后登录">{{ detailUser.loginTime || '从未登录' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </el-drawer>

    <!-- 导入弹窗 -->
    <el-dialog v-model="importVisible" title="导入用户" width="450px">
      <el-upload drag action="/api/system/user/import" :on-success="handleImportSuccess" :on-error="handleImportError" accept=".xlsx,.xls" :headers="uploadHeaders">
        <el-icon :size="48"><Upload /></el-icon>
        <div>将文件拖到此处，或<em>点击上传</em></div>
        <template #tip>
          <div class="el-upload__tip">仅支持 .xlsx / .xls 格式文件，请按模板格式填写</div>
        </template>
      </el-upload>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type ElTree } from 'element-plus'
import { Search, Refresh, Plus, Upload, Download } from '@element-plus/icons-vue'

const loading = ref(false)
const userList = ref<any[]>([])
const total = ref(0)
const ids = ref<number[]>([])
const deptFilter = ref('')
const deptTreeRef = ref<InstanceType<typeof ElTree>>()
const deptTree = ref<any[]>([])
const queryParams = reactive({ username: '', phone: '', status: undefined as number | undefined, deptId: undefined as number | undefined, pageNum: 1, pageSize: 20 })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const form = reactive({ id: undefined as number | undefined, username: '', password: '', nickname: '', deptId: undefined as number | undefined, phone: '', email: '', gender: 0, status: 0, roleIds: [] as number[], postIds: [] as number[], remark: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}
const detailVisible = ref(false)
const detailUser = ref<any>(null)
const importVisible = ref(false)
const roleOptions = ref<any[]>([])
const postOptions = ref<any[]>([])
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${localStorage.getItem('token')}` }))

watch(deptFilter, (val) => deptTreeRef.value?.filter(val))
const filterDeptNode = (value: string, data: any) => !value || data.deptName.includes(value)

const getDeptTree = async () => {
  const res = await fetch('/api/system/dept/tree').then(r => r.json()).catch(() => ({ code: 200, data: [] }))
  if (res.code === 200) deptTree.value = res.data
}

const getOptions = async () => {
  const [roleRes, postRes] = await Promise.all([
    fetch('/api/system/role?pageNum=1&pageSize=200').then(r => r.json()).catch(() => ({ data: { records: [] } })),
    fetch('/api/system/post?pageNum=1&pageSize=200').then(r => r.json()).catch(() => ({ data: { records: [] } }))
  ])
  roleOptions.value = roleRes.data?.records || []
  postOptions.value = postRes.data?.records || []
}

const handleDeptSelect = (data: any) => { queryParams.deptId = data?.id; queryParams.pageNum = 1; getList() }

const getList = async () => {
  loading.value = true
  try {
    const params = new URLSearchParams({ pageNum: String(queryParams.pageNum), pageSize: String(queryParams.pageSize) })
    if (queryParams.username) params.append('username', queryParams.username)
    if (queryParams.phone) params.append('phone', queryParams.phone)
    if (queryParams.status !== undefined) params.append('status', String(queryParams.status))
    if (queryParams.deptId) params.append('deptId', String(queryParams.deptId))
    const res = await fetch(`/api/system/user?${params}`).then(r => r.json())
    if (res.code === 200) { userList.value = res.data.records; total.value = res.data.total }
  } finally { loading.value = false }
}

const handleQuery = () => { queryParams.pageNum = 1; getList() }
const resetQuery = () => { queryParams.username = ''; queryParams.phone = ''; queryParams.status = undefined; queryParams.deptId = undefined; deptTreeRef.value?.setCurrentKey(null); handleQuery() }
const handleSelectionChange = (rows: any[]) => { ids.value = rows.map(r => r.id) }

const handleAdd = () => {
  Object.assign(form, { id: undefined, username: '', password: '', nickname: '', deptId: undefined, phone: '', email: '', gender: 0, status: 0, roleIds: [], postIds: [], remark: '' })
  dialogTitle.value = '新增用户'
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  Object.assign(form, { ...row, password: '' })
  dialogTitle.value = '编辑用户'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  const method = form.id ? 'PUT' : 'POST'
  const res = await fetch('/api/system/user', { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(form) }).then(r => r.json())
  if (res.code === 200) { ElMessage.success(form.id ? '修改成功' : '新增成功'); dialogVisible.value = false; getList() }
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确认删除用户"${row.username}"?`, '提示', { type: 'warning' }).then(async () => {
    const res = await fetch(`/api/system/user/${row.id}`, { method: 'DELETE' }).then(r => r.json())
    if (res.code === 200) { ElMessage.success('删除成功'); getList() }
  }).catch(() => {})
}

const handleStatusChange = async (row: any) => {
  await fetch('/api/system/user/status', { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ id: row.id, status: row.status }) })
  ElMessage.success('状态已更新')
}

const handleResetPwd = (row: any) => {
  ElMessageBox.prompt(`重置用户"${row.username}"的密码`, '重置密码', { confirmButtonText: '确定', cancelButtonText: '取消', inputPattern: /^.{6,20}$/, inputErrorMessage: '密码长度6-20位', inputValue: '123456' }).then(async ({ value }) => {
    const res = await fetch('/api/system/user/resetPwd', { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ id: row.id, password: value }) }).then(r => r.json())
    if (res.code === 200) ElMessage.success('密码已重置')
  }).catch(() => {})
}

const handleDetail = async (row: any) => {
  const res = await fetch(`/api/system/user/${row.id}`).then(r => r.json()).catch(() => ({ data: row }))
  detailUser.value = res.data || row
  detailVisible.value = true
}

const handleImport = () => { importVisible.value = true }
const handleImportSuccess = (res: any) => { ElMessage.success(res.msg || '导入成功'); importVisible.value = false; getList() }
const handleImportError = () => { ElMessage.error('导入失败') }
const handleExport = () => { ElMessage.info('导出功能开发中') }

onMounted(() => { getDeptTree(); getOptions(); getList() })
</script>

<style scoped>
.user-container { padding: 16px; }
.dept-card :deep(.el-card__body) { padding: 8px; }
.mb-2 { margin-bottom: 8px; }
.mb-4 { margin-bottom: 16px; }
.mt-4 { margin-top: 16px; }
.mr-1 { margin-right: 4px; }
.detail-header { text-align: center; margin-bottom: 16px; }
.detail-header h3 { margin: 8px 0 4px; }
</style>
