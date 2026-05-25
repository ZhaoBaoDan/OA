<template>
  <div class="doc-template-container">
    <el-card shadow="never">
      <template #header>
        <div class="flex justify-between items-center">
          <span>文档模板</span>
          <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增模板</el-button>
        </div>
      </template>

      <el-table :data="templateList" v-loading="loading" border stripe>
        <el-table-column prop="name" label="模板名称" />
        <el-table-column prop="category" label="分类" width="120">
          <template #default="{ row }"><el-tag>{{ row.category }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handlePreview(row)">预览</el-button>
            <el-button link type="success" @click="handleUse(row)">使用</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const templateList = ref([
  { id: 1, name: '劳动合同模板', category: '人事', description: '标准劳动合同模板', createTime: '2026-05-01 10:00:00' },
  { id: 2, name: '项目需求文档', category: '技术', description: '项目需求规格说明书模板', createTime: '2026-05-02 10:00:00' },
  { id: 3, name: '会议纪要模板', category: '行政', description: '标准会议纪要模板', createTime: '2026-05-03 10:00:00' },
  { id: 4, name: '周报模板', category: '通用', description: '每周工作报告模板', createTime: '2026-05-04 10:00:00' },
])

const handleAdd = () => { ElMessage.info('新增模板功能开发中') }
const handlePreview = (row: any) => { ElMessage.info('预览: ' + row.name) }
const handleUse = (row: any) => { ElMessage.info('使用模板: ' + row.name) }
const handleDelete = (row: any) => {
  ElMessageBox.confirm(`确认删除模板"${row.name}"?`, '提示', { type: 'warning' }).then(() => {
    templateList.value = templateList.value.filter(t => t.id !== row.id)
    ElMessage.success('删除成功')
  }).catch(() => {})
}

onMounted(() => {})
</script>

<style scoped>
.doc-template-container { padding: 16px; }
</style>
