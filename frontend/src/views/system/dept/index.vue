<template>
  <div class="dept-container">
    <el-row :gutter="16">
      <!-- 左侧：部门树 -->
      <el-col :span="8">
        <el-card shadow="never">
          <template #header>
            <div class="flex justify-between items-center">
              <span>组织架构</span>
              <el-button type="primary" size="small" @click="handleAdd(0)"><el-icon><Plus /></el-icon>新增</el-button>
            </div>
          </template>
          <el-input v-model="filterText" placeholder="搜索部门" clearable class="mb-3">
            <template #prefix><el-icon><Search /></el-icon></template>
          </el-input>
          <el-tree
            ref="treeRef"
            :data="deptTree"
            :props="{ label: 'deptName', children: 'children' }"
            node-key="id"
            default-expand-all
            highlight-current
            :filter-node-method="filterNode"
            @current-change="handleNodeClick"
          >
            <template #default="{ node, data }">
              <div class="tree-node">
                <span class="node-label">
                  <el-icon><OfficeBuilding /></el-icon>
                  {{ node.label }}
                </span>
                <span class="node-actions">
                  <el-tag size="small" type="info">{{ data.userCount || 0 }}人</el-tag>
                  <el-dropdown trigger="click" @command="(cmd: string) => handleCommand(cmd, data)">
                    <el-icon class="ml-2 cursor-pointer"><MoreFilled /></el-icon>
                    <template #dropdown>
                      <el-dropdown-menu>
                        <el-dropdown-item command="add"><el-icon><Plus /></el-icon>新增子部门</el-dropdown-item>
                        <el-dropdown-item command="edit"><el-icon><Edit /></el-icon>编辑</el-dropdown-item>
                        <el-dropdown-item command="move"><el-icon><Rank /></el-icon>移动</el-dropdown-item>
                        <el-dropdown-item command="delete" divided><el-icon><Delete /></el-icon>删除</el-dropdown-item>
                      </el-dropdown-menu>
                    </template>
                  </el-dropdown>
                </span>
              </div>
            </template>
          </el-tree>
        </el-card>
      </el-col>

      <!-- 右侧：部门详情 -->
      <el-col :span="16">
        <el-card shadow="never">
          <template #header><span>部门详情</span></template>
          <div v-if="currentDept" class="dept-detail">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="部门名称">{{ currentDept.deptName }}</el-descriptions-item>
              <el-descriptions-item label="部门ID">{{ currentDept.id }}</el-descriptions-item>
              <el-descriptions-item label="负责人">{{ currentDept.leader || '-' }}</el-descriptions-item>
              <el-descriptions-item label="联系电话">{{ currentDept.phone || '-' }}</el-descriptions-item>
              <el-descriptions-item label="邮箱">{{ currentDept.email || '-' }}</el-descriptions-item>
              <el-descriptions-item label="用户数">{{ currentDept.userCount || 0 }}</el-descriptions-item>
              <el-descriptions-item label="排序">{{ currentDept.sort }}</el-descriptions-item>
              <el-descriptions-item label="状态">
                <el-tag :type="currentDept.status === 0 ? 'success' : 'danger'">{{ currentDept.status === 0 ? '正常' : '停用' }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="创建时间" :span="2">{{ currentDept.createTime }}</el-descriptions-item>
            </el-descriptions>

            <div class="mt-4">
              <h4>下级部门</h4>
              <el-table :data="currentDept.children || []" border stripe size="small">
                <el-table-column prop="deptName" label="部门名称" />
                <el-table-column prop="leader" label="负责人" width="120" />
                <el-table-column prop="userCount" label="人数" width="80" align="center" />
                <el-table-column prop="status" label="状态" width="80" align="center">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="small">{{ row.status === 0 ? '正常' : '停用' }}</el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
          <el-empty v-else description="请在左侧选择部门" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="550px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上级部门">
          <el-tree-select v-model="form.parentId" :data="deptTree" :props="{ label: 'deptName', value: 'id', children: 'children' }" placeholder="选择上级部门" check-strictly clearable style="width:100%" />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="form.deptName" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="form.leader" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="显示排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">正常</el-radio>
            <el-radio :value="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 移动部门弹窗 -->
    <el-dialog v-model="moveDialogVisible" title="移动部门" width="450px">
      <el-form label-width="100px">
        <el-form-item label="当前部门">
          <el-input :model-value="moveDept?.deptName" disabled />
        </el-form-item>
        <el-form-item label="移动到">
          <el-tree-select v-model="moveTargetId" :data="deptTree" :props="{ label: 'deptName', value: 'id', children: 'children' }" placeholder="选择目标部门" check-strictly style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="moveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleMove">确定移动</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type ElTree } from 'element-plus'
import { Search, Plus, Edit, Delete, MoreFilled, OfficeBuilding, Rank } from '@element-plus/icons-vue'

const filterText = ref('')
const treeRef = ref<InstanceType<typeof ElTree>>()
const deptTree = ref<any[]>([])
const currentDept = ref<any>(null)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref<FormInstance>()
const form = reactive({ id: undefined as number | undefined, parentId: 0, deptName: '', leader: '', phone: '', email: '', sort: 0, status: 0 })
const rules = { deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }] }
const moveDialogVisible = ref(false)
const moveDept = ref<any>(null)
const moveTargetId = ref<number>(0)

watch(filterText, (val) => treeRef.value?.filter(val))

const filterNode = (value: string, data: any) => {
  if (!value) return true
  return data.deptName.includes(value)
}

const getTree = async () => {
  const res = await fetch('/api/system/dept/tree').then(r => r.json()).catch(() => ({ code: 200, data: [] }))
  if (res.code === 200) deptTree.value = res.data
}

const handleNodeClick = (data: any) => { currentDept.value = data }

const handleCommand = (cmd: string, data: any) => {
  if (cmd === 'add') handleAdd(data.id)
  else if (cmd === 'edit') handleEdit(data)
  else if (cmd === 'move') handleMoveDialog(data)
  else if (cmd === 'delete') handleDelete(data)
}

const handleAdd = (parentId: number) => {
  Object.assign(form, { id: undefined, parentId, deptName: '', leader: '', phone: '', email: '', sort: 0, status: 0 })
  dialogTitle.value = '新增部门'
  dialogVisible.value = true
}

const handleEdit = (data: any) => {
  Object.assign(form, data)
  dialogTitle.value = '编辑部门'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  const method = form.id ? 'PUT' : 'POST'
  const res = await fetch('/api/system/dept', { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(form) }).then(r => r.json())
  if (res.code === 200) { ElMessage.success(form.id ? '修改成功' : '新增成功'); dialogVisible.value = false; getTree() }
}

const handleDelete = (data: any) => {
  ElMessageBox.confirm(`确认删除部门"${data.deptName}"?`, '提示', { type: 'warning' }).then(async () => {
    const res = await fetch(`/api/system/dept/${data.id}`, { method: 'DELETE' }).then(r => r.json())
    if (res.code === 200) { ElMessage.success('删除成功'); getTree(); currentDept.value = null }
  }).catch(() => {})
}

const handleMoveDialog = (data: any) => {
  moveDept.value = data
  moveTargetId.value = data.parentId || 0
  moveDialogVisible.value = true
}

const handleMove = async () => {
  if (!moveTargetId.value && moveTargetId.value !== 0) { ElMessage.warning('请选择目标部门'); return }
  const res = await fetch(`/api/system/dept/move?deptId=${moveDept.value.id}&newParentId=${moveTargetId.value}`, { method: 'PUT' }).then(r => r.json())
  if (res.code === 200) { ElMessage.success('移动成功'); moveDialogVisible.value = false; getTree() }
}

onMounted(() => getTree())
</script>

<style scoped>
.dept-container { padding: 16px; }
.mb-3 { margin-bottom: 12px; }
.mt-4 { margin-top: 16px; }
.tree-node { display: flex; justify-content: space-between; align-items: center; width: 100%; padding-right: 8px; }
.node-label { display: flex; align-items: center; gap: 6px; font-size: 14px; }
.node-actions { display: flex; align-items: center; gap: 4px; }
.cursor-pointer { cursor: pointer; }
.ml-2 { margin-left: 8px; }
.dept-detail h4 { margin: 0 0 12px; color: #303133; }
</style>
