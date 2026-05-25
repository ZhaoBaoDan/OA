<template>
  <div class="inventory-container">
    <el-card shadow="never">
      <template #header>
        <div class="flex justify-between items-center">
          <span>资产盘点</span>
          <el-button type="primary" @click="handleStartInventory"><el-icon><VideoPlay /></el-icon>开始盘点</el-button>
        </div>
      </template>

      <!-- 盘点统计 -->
      <el-row :gutter="16" class="mb-4">
        <el-col :span="6">
          <el-statistic title="应盘数量" :value="156" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="已盘数量" :value="120" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="盘盈数量" :value="2" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="盘亏数量" :value="1" />
        </el-col>
      </el-row>

      <!-- 盘点记录表格 -->
      <el-table :data="inventoryList" v-loading="loading" border stripe>
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="assetCode" label="资产编码" width="150" />
        <el-table-column prop="name" label="资产名称" />
        <el-table-column prop="location" label="账面位置" width="120" />
        <el-table-column prop="actualLocation" label="实际位置" width="120">
          <template #default="{ row }">
            <el-input v-model="row.actualLocation" size="small" placeholder="扫码/输入" />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="盘点状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.inventoryStatus === 'matched' ? 'success' : row.inventoryStatus === 'missing' ? 'danger' : 'warning'" size="small">
              {{ row.inventoryStatus === 'matched' ? '相符' : row.inventoryStatus === 'missing' ? '盘亏' : '待盘' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleConfirm(row)">确认</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="mt-4 justify-end" v-model:current-page="pageNum" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { VideoPlay } from '@element-plus/icons-vue'

const loading = ref(false)
const inventoryList = ref<any[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(20)

const getList = async () => {
  loading.value = true
  try {
    // 模拟数据
    inventoryList.value = [
      { id: 1, assetCode: 'AST20260501001', name: 'MacBook Pro 16寸', location: 'A座3楼', actualLocation: 'A座3楼', inventoryStatus: 'matched' },
      { id: 2, assetCode: 'AST20260501002', name: 'ThinkPad X1', location: '仓库A', actualLocation: '', inventoryStatus: 'pending' },
      { id: 3, assetCode: 'AST20260502001', name: '办公桌', location: 'B座2楼', actualLocation: 'B座2楼', inventoryStatus: 'matched' },
      { id: 4, assetCode: 'AST20260502002', name: '打印机', location: 'A座1楼', actualLocation: '', inventoryStatus: 'missing' },
    ]
    total.value = inventoryList.value.length
  } finally { loading.value = false }
}

const handleStartInventory = () => { ElMessage.info('盘点任务已创建') }
const handleConfirm = (row: any) => {
  row.inventoryStatus = row.actualLocation ? 'matched' : 'missing'
  ElMessage.success('盘点确认完成')
}

onMounted(() => getList())
</script>

<style scoped>
.inventory-container { padding: 16px; }
.mb-4 { margin-bottom: 16px; }
.mt-4 { margin-top: 16px; }
</style>
