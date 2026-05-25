<template>
  <div class="document-container">
    <el-row :gutter="16">
      <!-- 左侧文件夹树 -->
      <el-col :span="5">
        <el-card shadow="never" class="folder-card">
          <template #header>
            <div class="folder-header">
              <span>文件夹</span>
              <el-button type="primary" link :icon="FolderAdd" @click="handleCreateFolder" />
            </div>
          </template>
          <el-tree
            ref="folderTreeRef"
            :data="folderTree"
            :props="{ label: 'name', children: 'children' }"
            node-key="id"
            highlight-current
            default-expand-all
            @node-click="handleFolderClick"
          >
            <template #default="{ data }">
              <div class="folder-node">
                <el-icon><Folder /></el-icon>
                <span>{{ data.name }}</span>
                <span class="folder-count">{{ data.docCount || 0 }}</span>
              </div>
            </template>
          </el-tree>
        </el-card>
      </el-col>

      <!-- 右侧文件列表 -->
      <el-col :span="19">
        <!-- 搜索栏 -->
        <el-card shadow="never" class="search-card">
          <el-form :model="queryParams" ref="queryFormRef" :inline="true">
            <el-form-item label="文件名" prop="fileName">
              <el-input v-model="queryParams.name" placeholder="请输入文件名" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="文件类型" prop="fileType">
              <el-select v-model="queryParams.fileType" placeholder="请选择" clearable>
                <el-option label="图片" value="image" />
                <el-option label="文档" value="document" />
                <el-option label="表格" value="spreadsheet" />
                <el-option label="PDF" value="pdf" />
                <el-option label="演示" value="presentation" />
                <el-option label="其他" value="other" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
              <el-button :icon="Refresh" @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 操作栏 + 表格 -->
        <el-card shadow="never" class="table-card">
          <template #header>
            <div class="card-header">
              <div class="header-left">
                <el-upload
                  ref="uploadRef"
                  action="#"
                  :show-file-list="false"
                  :before-upload="handleBeforeUpload"
                  :on-change="handleFileChange"
                  multiple
                  drag
                  accept="*"
                  class="inline-upload"
                >
                  <el-button type="primary" :icon="Upload">上传文件</el-button>
                </el-upload>
                <el-button type="danger" :icon="Delete" :disabled="!selectedIds.length" @click="handleBatchDelete">
                  批量删除 ({{ selectedIds.length }})
                </el-button>
              </div>
              <div class="header-right">
                <el-radio-group v-model="viewMode" size="small">
                  <el-radio-button label="list"><el-icon><List /></el-icon></el-radio-button>
                  <el-radio-button label="grid"><el-icon><Grid /></el-icon></el-radio-button>
                </el-radio-group>
              </div>
            </div>
          </template>

          <!-- 上传进度 -->
          <div v-if="uploadProgress > 0 && uploadProgress < 100" class="upload-progress">
            <el-progress :percentage="uploadProgress" :stroke-width="8" />
          </div>

          <!-- 列表视图 -->
          <template v-if="viewMode === 'list'">
            <el-table v-loading="loading" :data="fileList" border stripe @selection-change="handleSelectionChange">
              <el-table-column type="selection" width="50" align="center" />
              <el-table-column prop="name" label="文件名" min-width="240" show-overflow-tooltip>
                <template #default="{ row }">
                  <div class="file-name-cell">
                    <el-icon :style="{ color: getFileIconColor(row.fileType) }">
                      <component :is="getFileIcon(row.fileType)" />
                    </el-icon>
                    <span class="file-name" @click="handlePreview(row)">{{ row.name }}</span>
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="fileType" label="类型" width="90" align="center">
                <template #default="{ row }">
                  <el-tag size="small" :type="getFileTypeTag(row.fileType)">{{ getFileTypeLabel(row.fileType) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="fileSize" label="大小" width="100" align="center">
                <template #default="{ row }">{{ formatFileSize(row.fileSize) }}</template>
              </el-table-column>
              <el-table-column prop="version" label="版本" width="70" align="center">
                <template #default="{ row }">v{{ row.version }}</template>
              </el-table-column>
              <el-table-column prop="uploaderName" label="上传人" width="100" />
              <el-table-column prop="createTime" label="上传时间" min-width="160" sortable />
              <el-table-column prop="downloadCount" label="下载" width="70" align="center" />
              <el-table-column label="操作" width="260" align="center" fixed="right">
                <template #default="{ row }">
                  <el-button type="primary" link :icon="View" @click="handlePreview(row)">预览</el-button>
                  <el-button type="success" link :icon="Download" @click="handleDownload(row)">下载</el-button>
                  <el-button type="info" link :icon="Clock" @click="handleVersionHistory(row)">版本</el-button>
                  <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </template>

          <!-- 网格视图 -->
          <template v-else>
            <div class="grid-view" v-loading="loading">
              <div v-for="file in fileList" :key="file.id" class="grid-item" @click="handlePreview(file)">
                <div class="grid-item-icon">
                  <el-icon :size="40" :style="{ color: getFileIconColor(file.fileType) }">
                    <component :is="getFileIcon(file.fileType)" />
                  </el-icon>
                </div>
                <div class="grid-item-name">{{ file.name }}</div>
                <div class="grid-item-meta">{{ formatFileSize(file.fileSize) }}</div>
                <div class="grid-item-actions">
                  <el-button type="primary" link size="small" @click.stop="handleDownload(file)">
                    <el-icon><Download /></el-icon>
                  </el-button>
                  <el-button type="danger" link size="small" @click.stop="handleDelete(file)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>
          </template>

          <Pagination
            v-model:page="queryParams.pageNum"
            v-model:limit="queryParams.pageSize"
            :total="total"
            @pagination="getList"
          />
        </el-card>
      </el-col>
    </el-row>

    <!-- 预览弹窗 -->
    <el-dialog v-model="previewVisible" :title="previewFile?.name" width="75%" destroy-on-close>
      <div class="preview-content">
        <img v-if="previewFile && previewFile.fileType === 'image'" :src="previewUrl" class="preview-image" alt="预览" />
        <iframe v-else-if="previewFile && previewFile.fileType === 'pdf'" :src="previewUrl" class="preview-pdf" />
        <div v-else class="preview-unsupported">
          <el-icon :size="64"><Document /></el-icon>
          <p>该文件类型暂不支持在线预览</p>
          <el-button type="primary" :icon="Download" @click="previewFile && handleDownload(previewFile)">下载查看</el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 版本历史弹窗 -->
    <el-dialog v-model="versionVisible" title="版本历史" width="650px" destroy-on-close>
      <el-table :data="versionList" stripe size="small">
        <el-table-column prop="version" label="版本" width="80" align="center">
          <template #default="{ row }">v{{ row.version }}</template>
        </el-table-column>
        <el-table-column prop="fileSize" label="大小" width="100">
          <template #default="{ row }">{{ formatFileSize(row.fileSize) }}</template>
        </el-table-column>
        <el-table-column prop="uploaderName" label="上传人" width="100" />
        <el-table-column prop="uploadTime" label="上传时间" min-width="160" />
        <el-table-column prop="remark" label="说明" min-width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleRollback(row)">回滚</el-button>
            <el-button type="success" link size="small" @click="handleDownloadVersion(row)">下载</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 创建文件夹弹窗 -->
    <el-dialog v-model="folderDialogVisible" title="新建文件夹" width="400px" destroy-on-close>
      <el-form label-width="80px">
        <el-form-item label="文件夹名">
          <el-input v-model="folderForm.name" placeholder="请输入文件夹名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="folderDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="doCreateFolder">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, UploadFile } from 'element-plus'
import {
  Search, Refresh, Upload, Delete, Download, View, Document,
  Picture, Notebook, Ticket, Folder, FolderAdd, Clock, List, Grid,
} from '@element-plus/icons-vue'
import Pagination from '@/components/Pagination.vue'

interface FileItem {
  id: number
  name: string
  fileType: string
  fileSize: number
  version: number
  uploaderName: string
  createTime: string
  downloadCount: number
  filePath: string
}

interface VersionItem {
  id: number
  version: number
  fileSize: number
  uploaderName: string
  uploadTime: string
  remark: string
}

const loading = ref(false)
const viewMode = ref<'list' | 'grid'>('list')
const fileList = ref<FileItem[]>([])
const total = ref(0)
const selectedIds = ref<number[]>([])
const previewVisible = ref(false)
const previewFile = ref<FileItem | null>(null)
const previewUrl = ref('')
const versionVisible = ref(false)
const versionList = ref<VersionItem[]>([])
const folderDialogVisible = ref(false)
const folderForm = reactive({ name: '' })
const currentFolderId = ref<number | null>(null)
const uploadProgress = ref(0)

const queryParams = reactive({
  name: '',
  fileType: '',
  folderId: null as number | null,
  pageNum: 1,
  pageSize: 20,
})

const queryFormRef = ref<FormInstance>()
const folderTreeRef = ref()

const folderTree = ref([
  { id: 0, name: '全部文件', docCount: 6, children: [] },
  { id: 1, name: '产品文档', docCount: 2, children: [] },
  { id: 2, name: '技术文档', docCount: 2, children: [] },
  { id: 3, name: '人事资料', docCount: 1, children: [] },
  { id: 4, name: '会议纪要', docCount: 1, children: [] },
])

const mockData: FileItem[] = [
  { id: 1, name: '2024年Q3产品需求文档.docx', fileType: 'document', fileSize: 2621440, version: 2, uploaderName: '张三', createTime: '2024-05-20 14:30:00', downloadCount: 5, filePath: '' },
  { id: 2, name: '项目进度表.xlsx', fileType: 'spreadsheet', fileSize: 1258291, version: 1, uploaderName: '李四', createTime: '2024-05-19 10:15:00', downloadCount: 3, filePath: '' },
  { id: 3, name: '系统架构图.png', fileType: 'image', fileSize: 876544, version: 1, uploaderName: '王五', createTime: '2024-05-18 16:45:00', downloadCount: 8, filePath: 'https://via.placeholder.com/800x600' },
  { id: 4, name: '员工手册.pdf', fileType: 'pdf', fileSize: 6081741, version: 3, uploaderName: '赵六', createTime: '2024-05-17 09:00:00', downloadCount: 12, filePath: '' },
  { id: 5, name: '会议纪要-0520.docx', fileType: 'document', fileSize: 389120, version: 1, uploaderName: '张三', createTime: '2024-05-20 17:00:00', downloadCount: 2, filePath: '' },
  { id: 6, name: 'logo设计稿.png', fileType: 'image', fileSize: 3355443, version: 2, uploaderName: '李四', createTime: '2024-05-16 11:20:00', downloadCount: 6, filePath: 'https://via.placeholder.com/400x400' },
]

const mockVersions: VersionItem[] = [
  { id: 1, version: 2, fileSize: 2621440, uploaderName: '张三', uploadTime: '2024-05-20 14:30:00', remark: '更新需求内容' },
  { id: 2, version: 1, fileSize: 2097152, uploaderName: '张三', uploadTime: '2024-05-15 10:00:00', remark: '初始版本' },
]

async function getList() {
  loading.value = true
  try {
    await new Promise(r => setTimeout(r, 300))
    let filtered = [...mockData]
    if (queryParams.name) filtered = filtered.filter(f => f.name.toLowerCase().includes(queryParams.name.toLowerCase()))
    if (queryParams.fileType) filtered = filtered.filter(f => f.fileType === queryParams.fileType)
    total.value = filtered.length
    const start = (queryParams.pageNum - 1) * queryParams.pageSize
    fileList.value = filtered.slice(start, start + queryParams.pageSize)
  } finally {
    loading.value = false
  }
}

function handleQuery() {
  queryParams.pageNum = 1
  getList()
}

function handleReset() {
  queryFormRef.value?.resetFields()
  queryParams.name = ''
  queryParams.fileType = ''
  queryParams.folderId = null
  queryParams.pageNum = 1
  getList()
}

function handleFolderClick(data: any) {
  currentFolderId.value = data.id === 0 ? null : data.id
  queryParams.folderId = currentFolderId.value
  queryParams.pageNum = 1
  getList()
}

function handleSelectionChange(rows: FileItem[]) {
  selectedIds.value = rows.map(r => r.id)
}

function handleBeforeUpload(file: File) {
  if (file.size > 50 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过50MB')
    return false
  }
  return true
}

function handleFileChange(file: UploadFile) {
  // 模拟上传进度
  uploadProgress.value = 0
  const interval = setInterval(() => {
    uploadProgress.value += 10
    if (uploadProgress.value >= 100) {
      clearInterval(interval)
      setTimeout(() => {
        uploadProgress.value = 0
        ElMessage.success(`文件「${file.name}」上传成功`)
        getList()
      }, 300)
    }
  }, 100)
}

function handlePreview(row: FileItem) {
  previewFile.value = row
  previewUrl.value = row.filePath || ''
  previewVisible.value = true
}

function handleDownload(row: FileItem) {
  ElMessage.success(`开始下载「${row.name}」`)
}

function handleVersionHistory(row: FileItem) {
  versionList.value = [...mockVersions]
  versionVisible.value = true
}

function handleRollback(row: VersionItem) {
  ElMessageBox.confirm(`确定要回滚到 v${row.version} 吗？`, '提示', { type: 'warning' }).then(() => {
    ElMessage.success(`已回滚到 v${row.version}`)
    versionVisible.value = false
    getList()
  })
}

function handleDownloadVersion(row: VersionItem) {
  ElMessage.success(`开始下载 v${row.version}`)
}

async function handleDelete(row: FileItem) {
  await ElMessageBox.confirm(`确定要删除文件「${row.name}」吗？`, '提示', { type: 'warning' })
  ElMessage.success('删除成功')
  getList()
}

async function handleBatchDelete() {
  await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个文件吗？`, '提示', { type: 'warning' })
  ElMessage.success('批量删除成功')
  getList()
}

function handleCreateFolder() {
  folderForm.name = ''
  folderDialogVisible.value = true
}

function doCreateFolder() {
  if (!folderForm.name) {
    ElMessage.warning('请输入文件夹名称')
    return
  }
  folderTree.value.push({
    id: Date.now(),
    name: folderForm.name,
    docCount: 0,
    children: [],
  })
  folderDialogVisible.value = false
  ElMessage.success('文件夹创建成功')
}

function formatFileSize(bytes: number) {
  if (!bytes) return '-'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
  return (bytes / (1024 * 1024 * 1024)).toFixed(2) + ' GB'
}

function getFileIcon(type: string) {
  const map: Record<string, any> = {
    image: Picture, document: Document, spreadsheet: Notebook,
    pdf: Document, presentation: Ticket, video: Ticket,
    audio: Ticket, archive: Ticket, text: Document, other: Ticket,
  }
  return map[type] || Document
}

function getFileIconColor(type: string) {
  const map: Record<string, string> = {
    image: '#67c23a', document: '#409eff', spreadsheet: '#e6a23c',
    pdf: '#f56c6c', presentation: '#b37feb', video: '#909399',
    audio: '#909399', archive: '#909399', text: '#606266', other: '#c0c4cc',
  }
  return map[type] || '#909399'
}

function getFileTypeLabel(type: string) {
  const map: Record<string, string> = {
    image: '图片', document: '文档', spreadsheet: '表格',
    pdf: 'PDF', presentation: '演示', video: '视频',
    audio: '音频', archive: '压缩', text: '文本', other: '其他',
  }
  return map[type] || type
}

function getFileTypeTag(type: string) {
  const map: Record<string, string> = {
    image: 'success', document: '', spreadsheet: 'warning',
    pdf: 'danger', presentation: '', video: 'info',
    audio: 'info', archive: 'info', text: 'info', other: 'info',
  }
  return (map[type] || 'info') as any
}

onMounted(() => {
  getList()
})
</script>

<style lang="scss" scoped>
.document-container {
  display: flex;
  flex-direction: column;
}

.folder-card {
  border-radius: 8px;
  height: calc(100vh - 170px);

  .folder-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  :deep(.el-card__body) {
    padding: 8px;
  }
}

.folder-node {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  flex: 1;

  .el-icon {
    color: #e6a23c;
  }

  .folder-count {
    margin-left: auto;
    font-size: 11px;
    color: #c0c4cc;
  }
}

.search-card,
.table-card {
  border-radius: 8px;
}

.search-card {
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.inline-upload {
  display: inline-block;
}

.upload-progress {
  margin-bottom: 16px;
}

.file-name-cell {
  display: flex;
  align-items: center;
  gap: 8px;

  .el-icon {
    font-size: 18px;
    flex-shrink: 0;
  }

  .file-name {
    color: #409eff;
    cursor: pointer;

    &:hover {
      text-decoration: underline;
    }
  }
}

/* 网格视图 */
.grid-view {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: 16px;
  min-height: 200px;
}

.grid-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 12px 12px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: #409eff;
    box-shadow: 0 2px 12px rgba(64, 158, 255, 0.15);
  }

  .grid-item-icon {
    margin-bottom: 12px;
  }

  .grid-item-name {
    font-size: 12px;
    color: #303133;
    text-align: center;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    width: 100%;
    margin-bottom: 4px;
  }

  .grid-item-meta {
    font-size: 11px;
    color: #c0c4cc;
    margin-bottom: 8px;
  }

  .grid-item-actions {
    display: flex;
    gap: 8px;
  }
}

/* 预览 */
.preview-content {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
}

.preview-image {
  max-width: 100%;
  max-height: 70vh;
  object-fit: contain;
  border-radius: 4px;
}

.preview-pdf {
  width: 100%;
  height: 70vh;
  border: none;
  border-radius: 4px;
}

.preview-unsupported {
  text-align: center;
  color: #909399;

  .el-icon {
    margin-bottom: 16px;
    color: #c0c4cc;
  }

  p {
    margin-bottom: 16px;
  }
}
</style>
