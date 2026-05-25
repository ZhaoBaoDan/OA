<template>
  <div class="role-container">
    <el-card shadow="never">
      <el-form :model="queryParams" inline>
        <el-form-item label="角色名称">
          <el-input v-model="queryParams.roleName" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="角色标识">
          <el-input v-model="queryParams.roleKey" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable>
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
          <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增角色</el-button>
        </el-col>
      </el-row>

      <el-table :data="roleList" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="roleName" label="角色名称" width="150" />
        <el-table-column prop="roleKey" label="角色标识" width="150">
          <template #default="{ row }"><el-tag>{{ row.roleKey }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="70" align="center" />
        <el-table-column prop="dataScope" label="数据权限" width="130">
          <template #default="{ row }">
            <el-tag type="info" size="small">{{ dataScopeLabel(row.dataScope) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="0" :inactive-value="1" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="success" @click="handleMenuPerm(row)">菜单权限</el-button>
            <el-button link type="warning" @click="handleDataScope(row)">数据权限</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="mt-4 justify-end" v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize" :total="total" layout="total, prev, pager, next" @change="getList" />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="如: 系统管理员" />
        </el-form-item>
        <el-form-item label="角色标识" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="如: admin" :disabled="!!form.id" />
          <div class="form-tip">唯一标识，用于权限判断</div>
        </el-form-item>
        <el-form-item label="显示排序">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">正常</el-radio>
            <el-radio :value="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 菜单权限弹窗 -->
    <el-dialog v-model="menuDialogVisible" title="分配菜单权限" width="550px">
      <div class="mb-3">
        <el-checkbox v-model="menuCheckAll" :indeterminate="menuIndeterminate" @change="handleMenuCheckAll">全选/全不选</el-checkbox>
        <el-checkbox v-model="menuExpandAll" @change="handleMenuExpandAll">展开/折叠</el-checkbox>
      </div>
      <el-tree
        ref="menuTreeRef"
        :data="menuTree"
        :props="{ label: 'menuName', children: 'children' }"
        show-checkbox
        node-key="id"
        :default-expand-all="menuExpandAll"
        check-strictly
        class="menu-tree"
      />
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitMenuPerm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 数据权限弹窗 -->
    <el-dialog v-model="dataScopeDialogVisible" title="分配数据权限" width="550px">
      <el-form label-width="100px">
        <el-form-item label="角色名称">
          <el-input :model-value="dataScopeRole?.roleName" disabled />
        </el-form-item>
        <el-form-item label="数据权限">
          <el-select v-model="dataScopeValue" style="width:100%">
            <el-option :value="1" label="全部数据权限" />
            <el-option :value="2" label="自定义数据权限" />
            <el-option :value="3" label="本部门数据权限" />
            <el-option :value="4" label="本部门及以下数据权限" />
            <el-option :value="5" label="仅本人数据权限" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="dataScopeValue === 2" label="选择部门">
          <el-tree
            ref="deptTreeRef"
            :data="deptTree"
            :props="{ label: 'deptName', children: 'children' }"
            show-checkbox
            node-key="id"
            default-expand-all
            class="dept-tree"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dataScopeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitDataScope">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type ElTree } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const roleList = ref<any[]>([])
const total = ref(0)
const queryParams = reactive({ roleName: '', roleKey: '', status: undefined as number | undefined, pageNum: 1, pageSize: 10 })
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const form = reactive({ id: undefined as number | undefined, roleName: '', roleKey: '', sort: 0, status: 0, remark: '' })
const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleKey: [{ required: true, message: '请输入角色标识', trigger: 'blur' }],
}

const dataScopeLabel = (scope: number) => ({ 1: '全部', 2: '自定义', 3: '本部门', 4: '本部门及以下', 5: '仅本人' }[scope] || '未设置')

// 菜单权限
const menuDialogVisible = ref(false)
const menuTree = ref<any[]>([])
const menuTreeRef = ref<InstanceType<typeof ElTree>>()
const menuCheckAll = ref(false)
const menuExpandAll = ref(true)
const menuIndeterminate = ref(false)
const currentRoleId = ref<number>(0)

// 数据权限
const dataScopeDialogVisible = ref(false)
const dataScopeRole = ref<any>(null)
const dataScopeValue = ref(3)
const deptTree = ref<any[]>([])
const deptTreeRef = ref<InstanceType<typeof ElTree>>()

const getList = async () => {
  loading.value = true
  try {
    const res = await fetch(`/api/system/role?pageNum=${queryParams.pageNum}&pageSize=${queryParams.pageSize}&roleName=${queryParams.roleName}&roleKey=${queryParams.roleKey}&status=${queryParams.status ?? ''}`).then(r => r.json())
    if (res.code === 200) { roleList.value = res.data.records; total.value = res.data.total }
  } finally { loading.value = false }
}

const handleQuery = () => { queryParams.pageNum = 1; getList() }
const resetQuery = () => { queryParams.roleName = ''; queryParams.roleKey = ''; queryParams.status = undefined; handleQuery() }

const handleAdd = () => {
  Object.assign(form, { id: undefined, roleName: '', roleKey: '', sort: 0, status: 0, remark: '' })
  dialogTitle.value = '新增角色'
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  Object.assign(form, row)
  dialogTitle.value = '编辑角色'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  const method = form.id ? 'PUT' : 'POST'
  const res = await fetch('/api/system/role', { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(form) }).then(r => r.json())
  if (res.code === 200) { ElMessage.success(form.id ? '修改成功' : '新增成功'); dialogVisible.value = false; getList() }
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确认删除角色"${row.roleName}"?`, '提示', { type: 'warning' }).then(async () => {
    const res = await fetch(`/api/system/role/${row.id}`, { method: 'DELETE' }).then(r => r.json())
    if (res.code === 200) { ElMessage.success('删除成功'); getList() }
  }).catch(() => {})
}

const handleStatusChange = async (row: any) => {
  await fetch('/api/system/role/status', { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ id: row.id, status: row.status }) })
  ElMessage.success('状态已更新')
}

// 菜单权限
const loadMenuTree = async () => {
  const res = await fetch('/api/system/menu/tree').then(r => r.json()).catch(() => ({ code: 200, data: [] }))
  if (res.code === 200) menuTree.value = res.data
}

const handleMenuPerm = async (row: any) => {
  currentRoleId.value = row.id
  await loadMenuTree()
  menuDialogVisible.value = true
  // 加载角色已有菜单
  const res = await fetch(`/api/system/role/${row.id}`).then(r => r.json()).catch(() => ({ data: { menuIds: [] } }))
  setTimeout(() => {
    menuTreeRef.value?.setCheckedKeys(res.data?.menuIds || [], false)
  }, 100)
}

const handleMenuCheckAll = (val: boolean) => {
  if (val) {
    const allKeys = getAllMenuIds(menuTree.value)
    menuTreeRef.value?.setCheckedKeys(allKeys, false)
  } else {
    menuTreeRef.value?.setCheckedKeys([], false)
  }
  menuIndeterminate.value = false
}

const handleMenuExpandAll = (val: boolean) => {
  // Toggle expand/collapse
  const setExpand = (nodes: any[], expand: boolean) => {
    nodes.forEach(n => {
      const node = menuTreeRef.value?.getNode(n.id)
      if (node) node.expanded = expand
      if (n.children) setExpand(n.children, expand)
    })
  }
  setExpand(menuTree.value, val)
}

const getAllMenuIds = (menus: any[]): number[] => {
  const ids: number[] = []
  const walk = (list: any[]) => list.forEach(m => { ids.push(m.id); if (m.children) walk(m.children) })
  walk(menus)
  return ids
}

const handleSubmitMenuPerm = async () => {
  const checkedKeys = menuTreeRef.value?.getCheckedKeys(false) as number[]
  const halfCheckedKeys = menuTreeRef.value?.getHalfCheckedKeys() as number[]
  const menuIds = [...checkedKeys, ...halfCheckedKeys]
  const res = await fetch('/api/system/role/roleMenu', { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ roleId: currentRoleId.value, menuIds }) }).then(r => r.json())
  if (res.code === 200) { ElMessage.success('菜单权限分配成功'); menuDialogVisible.value = false }
}

// 数据权限
const loadDeptTree = async () => {
  const res = await fetch('/api/system/dept/tree').then(r => r.json()).catch(() => ({ code: 200, data: [] }))
  if (res.code === 200) deptTree.value = res.data
}

const handleDataScope = async (row: any) => {
  dataScopeRole.value = row
  dataScopeValue.value = row.dataScope || 3
  await loadDeptTree()
  dataScopeDialogVisible.value = true
  // 加载角色已有部门
  setTimeout(() => {
    if (row.deptIds) deptTreeRef.value?.setCheckedKeys(row.deptIds, false)
  }, 100)
}

const handleSubmitDataScope = async () => {
  const deptIds = dataScopeValue.value === 2 ? (deptTreeRef.value?.getCheckedKeys(false) as number[] || []) : []
  const res = await fetch('/api/system/role/dataScope', { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ roleId: dataScopeRole.value.id, dataScope: dataScopeValue.value, deptIds }) }).then(r => r.json())
  if (res.code === 200) { ElMessage.success('数据权限分配成功'); dataScopeDialogVisible.value = false; getList() }
}

onMounted(() => getList())
</script>

<style scoped>
.role-container { padding: 16px; }
.mb-3 { margin-bottom: 12px; }
.mb-4 { margin-bottom: 16px; }
.mt-4 { margin-top: 16px; }
.form-tip { font-size: 12px; color: #909399; }
.menu-tree, .dept-tree { max-height: 400px; overflow-y: auto; border: 1px solid #ebeef5; border-radius: 4px; padding: 8px; }
</style>
