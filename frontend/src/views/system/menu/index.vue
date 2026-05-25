<template>
  <div class="menu-container">
    <el-card shadow="never">
      <template #header>
        <div class="flex justify-between items-center">
          <span>菜单管理</span>
          <div>
            <el-button @click="toggleExpand"><el-icon><component :is="isExpand ? 'ArrowUp' : 'ArrowDown'" /></el-icon>{{ isExpand ? '折叠' : '展开' }}</el-button>
            <el-button type="primary" @click="handleAdd(0)"><el-icon><Plus /></el-icon>新增菜单</el-button>
          </div>
        </div>
      </template>

      <el-table :data="menuList" v-loading="loading" row-key="id" :tree-props="{ children: 'children' }" :default-expand-all="isExpand" border>
        <el-table-column prop="menuName" label="菜单名称" min-width="180">
          <template #default="{ row }">
            <el-icon v-if="row.icon" class="mr-1"><component :is="row.icon" /></el-icon>
            {{ row.menuName }}
          </template>
        </el-table-column>
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column prop="menuType" label="类型" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="menuTypeTag(row.menuType)" size="small">{{ menuTypeLabel(row.menuType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" width="160" show-overflow-tooltip />
        <el-table-column prop="component" label="组件路径" width="180" show-overflow-tooltip />
        <el-table-column prop="perms" label="权限标识" width="160" show-overflow-tooltip>
          <template #default="{ row }">
            <code v-if="row.perms">{{ row.perms }}</code>
            <span v-else class="text-gray">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="70" align="center" />
        <el-table-column prop="visible" label="可见" width="70" align="center">
          <template #default="{ row }">
            <el-tag :type="row.visible === 0 ? 'success' : 'info'" size="small">{{ row.visible === 0 ? '是' : '否' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="70" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="small">{{ row.status === 0 ? '正常' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.menuType !== 'B'" link type="primary" size="small" @click="handleAdd(row.id)">新增</el-button>
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="650px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="24">
            <el-form-item label="上级菜单">
              <el-tree-select v-model="form.parentId" :data="menuTreeOptions" :props="{ label: 'menuName', value: 'id', children: 'children' }" placeholder="选择上级菜单" check-strictly clearable style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单类型" prop="menuType">
              <el-radio-group v-model="form.menuType">
                <el-radio-button value="M">目录</el-radio-button>
                <el-radio-button value="C">菜单</el-radio-button>
                <el-radio-button value="B">按钮</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单名称" prop="menuName">
              <el-input v-model="form.menuName" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType !== 'B'">
            <el-form-item label="路由路径">
              <el-input v-model="form.path" placeholder="如: system/user" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType === 'C'">
            <el-form-item label="组件路径">
              <el-input v-model="form.component" placeholder="如: system/user/index" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType !== 'M'">
            <el-form-item label="权限标识">
              <el-input v-model="form.perms" placeholder="如: system:user:list" />
              <div class="form-tip">格式: 模块:功能:操作</div>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType !== 'B'">
            <el-form-item label="图标">
              <el-input v-model="form.icon" placeholder="如: User" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示排序">
              <el-input-number v-model="form.sort" :min="0" :max="999" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType !== 'B'">
            <el-form-item label="是否可见">
              <el-radio-group v-model="form.visible">
                <el-radio :value="0">显示</el-radio>
                <el-radio :value="1">隐藏</el-radio>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { Plus, ArrowUp, ArrowDown } from '@element-plus/icons-vue'

const loading = ref(false)
const menuList = ref<any[]>([])
const isExpand = ref(true)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const form = reactive({
  id: undefined as number | undefined, parentId: 0, menuName: '', menuType: 'M' as 'M' | 'C' | 'B',
  path: '', component: '', perms: '', icon: '', sort: 0, visible: 0, status: 0
})
const rules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }],
}

const menuTypeLabel = (type: string) => ({ M: '目录', C: '菜单', B: '按钮' }[type] || type)
const menuTypeTag = (type: string) => ({ M: '', C: 'success', B: 'warning' }[type] || 'info') as any

const menuTreeOptions = computed(() => {
  const buildOptions = (menus: any[]): any[] => menus.map(m => ({
    id: m.id, menuName: m.menuName, children: m.children ? buildOptions(m.children) : []
  }))
  return [{ id: 0, menuName: '根目录', children: buildOptions(menuList.value) }]
})

const toggleExpand = () => {
  isExpand.value = !isExpand.value
  // 重新加载表格以应用展开状态
  const data = menuList.value
  menuList.value = []
  setTimeout(() => { menuList.value = data }, 0)
}

const getList = async () => {
  loading.value = true
  try {
    const res = await fetch('/api/system/menu').then(r => r.json())
    if (res.code === 200) menuList.value = res.data
  } finally { loading.value = false }
}

const handleAdd = (parentId: number) => {
  Object.assign(form, { id: undefined, parentId, menuName: '', menuType: parentId === 0 ? 'M' : 'C', path: '', component: '', perms: '', icon: '', sort: 0, visible: 0, status: 0 })
  dialogTitle.value = '新增菜单'
  dialogVisible.value = true
}

const handleEdit = (row: any) => {
  Object.assign(form, row)
  dialogTitle.value = '编辑菜单'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  const method = form.id ? 'PUT' : 'POST'
  const res = await fetch('/api/system/menu', { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(form) }).then(r => r.json())
  if (res.code === 200) { ElMessage.success(form.id ? '修改成功' : '新增成功'); dialogVisible.value = false; getList() }
}

const handleDelete = (row: any) => {
  if (row.children && row.children.length > 0) {
    ElMessage.warning('请先删除子菜单')
    return
  }
  ElMessageBox.confirm(`确认删除菜单"${row.menuName}"?`, '提示', { type: 'warning' }).then(async () => {
    const res = await fetch(`/api/system/menu/${row.id}`, { method: 'DELETE' }).then(r => r.json())
    if (res.code === 200) { ElMessage.success('删除成功'); getList() }
  }).catch(() => {})
}

onMounted(() => getList())
</script>

<style scoped>
.menu-container { padding: 16px; }
.mr-1 { margin-right: 4px; vertical-align: middle; }
.form-tip { font-size: 12px; color: #909399; }
code { background: #f5f7fa; padding: 1px 4px; border-radius: 3px; font-size: 12px; }
.text-gray { color: #c0c4cc; }
</style>
