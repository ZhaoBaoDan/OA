<template>
  <div class="workflow-design-container">
    <!-- 顶部工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-button :icon="Plus" @click="handleNew">新建</el-button>
        <el-button :icon="FolderOpened" @click="drawerVisible = true">打开</el-button>
        <el-button type="primary" :icon="Check" :loading="saving" @click="handleSave">保存</el-button>
        <el-button type="success" :icon="Upload" :loading="deploying" @click="handleDeploy">部署</el-button>
        <el-divider direction="vertical" />
        <el-tooltip content="导出SVG">
          <el-button :icon="Picture" circle @click="handleExportSVG" />
        </el-tooltip>
        <el-tooltip content="XML编辑器">
          <el-button :icon="EditPen" circle @click="toggleXmlEditor" />
        </el-tooltip>
      </div>
      <div class="toolbar-right">
        <el-tooltip content="放大">
          <el-button :icon="ZoomIn" circle @click="zoomIn" />
        </el-tooltip>
        <el-tooltip content="缩小">
          <el-button :icon="ZoomOut" circle @click="zoomOut" />
        </el-tooltip>
        <el-tooltip content="还原">
          <el-button :icon="RefreshRight" circle @click="zoomReset" />
        </el-tooltip>
      </div>
    </div>

    <div class="main-area">
      <!-- BPMN 画布 -->
      <div v-show="!xmlEditorVisible" ref="canvasRef" class="canvas-area" />

      <!-- XML 编辑器 -->
      <div v-show="xmlEditorVisible" class="xml-editor-area">
        <div class="xml-editor-header">
          <span>BPMN XML</span>
          <el-button size="small" type="primary" @click="applyXml">应用更改</el-button>
        </div>
        <el-input v-model="xmlContent" type="textarea" :autosize="false" class="xml-textarea" placeholder="BPMN XML 内容" />
      </div>

      <!-- 右侧属性面板 -->
      <div v-show="showProperties && !xmlEditorVisible" class="properties-panel">
        <div class="panel-header">
          <span>属性面板</span>
          <el-button :icon="Close" link @click="showProperties = false" />
        </div>
        <el-scrollbar height="calc(100% - 48px)">
          <el-form label-width="80px" size="small" class="prop-form">
            <el-form-item label="ID">
              <el-input v-model="selectedElement.id" disabled />
            </el-form-item>
            <el-form-item label="名称">
              <el-input v-model="selectedElement.name" placeholder="请输入名称" @change="updateElementName" />
            </el-form-item>
            <el-form-item label="类型">
              <el-input :model-value="selectedElement.type" disabled />
            </el-form-item>
            <template v-if="selectedElement.type === 'bpmn:UserTask'">
              <el-form-item label="办理人">
                <el-input v-model="selectedElement.assignee" placeholder="指定办理人" />
              </el-form-item>
              <el-form-item label="候选人">
                <el-input v-model="selectedElement.candidateUsers" placeholder="多个用逗号分隔" />
              </el-form-item>
              <el-form-item label="到期时间">
                <el-input v-model="selectedElement.dueDate" placeholder="如: P3D" />
              </el-form-item>
            </template>
            <template v-if="selectedElement.type === 'bpmn:ServiceTask'">
              <el-form-item label="调用类型">
                <el-select v-model="selectedElement.callType" placeholder="请选择">
                  <el-option label="Java类" value="class" />
                  <el-option label="表达式" value="expression" />
                  <el-option label="委托表达式" value="delegateExpression" />
                </el-select>
              </el-form-item>
              <el-form-item label="调用值">
                <el-input v-model="selectedElement.callValue" placeholder="请输入调用值" />
              </el-form-item>
            </template>
            <el-form-item label="描述">
              <el-input v-model="selectedElement.documentation" type="textarea" :rows="3" placeholder="请输入描述" />
            </el-form-item>
          </el-form>
        </el-scrollbar>
      </div>
    </div>

    <!-- 流程列表抽屉 -->
    <el-drawer v-model="drawerVisible" title="已有流程" size="450px">
      <el-input v-model="searchKey" placeholder="搜索流程" clearable :prefix-icon="Search" class="mb-12" />
      <el-table :data="filteredProcessList" stripe size="small" v-loading="listLoading">
        <el-table-column prop="name" label="流程名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="key" label="标识" width="100" show-overflow-tooltip />
        <el-table-column prop="version" label="版本" width="60" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'" size="small">
              {{ row.status === 1 ? '正常' : '挂起' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleOpenProcess(row)">打开</el-button>
            <el-button :type="row.status === 1 ? 'warning' : 'success'" link size="small" @click="handleToggleStatus(row)">
              {{ row.status === 1 ? '挂起' : '激活' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, FolderOpened, Check, Upload, ZoomIn, ZoomOut, RefreshRight,
  Close, Search, Picture, EditPen,
} from '@element-plus/icons-vue'

interface ProcessItem {
  id: string
  name: string
  key: string
  version: number
  status: number
  bpmnXml: string
}

const canvasRef = ref<HTMLElement>()
const saving = ref(false)
const deploying = ref(false)
const drawerVisible = ref(false)
const showProperties = ref(true)
const searchKey = ref('')
const listLoading = ref(false)
const xmlEditorVisible = ref(false)
const xmlContent = ref('')

let modeler: any = null

const selectedElement = reactive({
  id: '',
  name: '',
  type: '',
  assignee: '',
  candidateUsers: '',
  dueDate: '',
  callType: '',
  callValue: '',
  documentation: '',
})

const processList = ref<ProcessItem[]>([
  { id: '1', name: '请假审批流程', key: 'leave_approval', version: 2, status: 1, bpmnXml: '' },
  { id: '2', name: '报销审批流程', key: 'expense_approval', version: 1, status: 1, bpmnXml: '' },
  { id: '3', name: '采购申请流程', key: 'purchase_request', version: 3, status: 0, bpmnXml: '' },
])

const filteredProcessList = computed(() => {
  if (!searchKey.value) return processList.value
  return processList.value.filter(p => p.name.includes(searchKey.value) || p.key.includes(searchKey.value))
})

const defaultBpmnXml = `<?xml version="1.0" encoding="UTF-8"?>
<definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"
  xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"
  xmlns:omgdc="http://www.omg.org/spec/DD/20100524/DC"
  xmlns:omgdi="http://www.omg.org/spec/DD/20100524/DI"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  targetNamespace="http://bpmn.io/schema/bpmn">
  <process id="Process_1" isExecutable="true">
    <startEvent id="StartEvent_1" name="开始" />
  </process>
  <bpmndi:BPMNDiagram id="BPMNDiagram_1">
    <bpmndi:BPMNPlane id="BPMNPlane_1" bpmnElement="Process_1">
      <bpmndi:BPMNShape id="StartEvent_1_di" bpmnElement="StartEvent_1">
        <omgdc:Bounds x="180" y="200" width="36" height="36" />
      </bpmndi:BPMNShape>
    </bpmndi:BPMNPlane>
  </bpmndi:BPMNDiagram>
</definitions>`

async function initModeler() {
  try {
    const BpmnModeler = (await import('bpmn-js/lib/Modeler')).default
    modeler = new BpmnModeler({
      container: canvasRef.value,
      additionalModules: [],
      moddleExtensions: {},
    })

    await modeler.importXML(defaultBpmnXml)
    const canvas = modeler.get('canvas')
    canvas.zoom('fit-viewport')

    // 监听元素选中事件
    const eventBus = modeler.get('eventBus')
    eventBus.on('selection.changed', (e: any) => {
      const element = e.newSelection[0]
      if (element) {
        selectedElement.id = element.id
        selectedElement.name = element.businessObject?.name || ''
        selectedElement.type = element.type || ''
        selectedElement.assignee = element.businessObject?.assignee || ''
        selectedElement.candidateUsers = element.businessObject?.candidateUsers || ''
        selectedElement.dueDate = element.businessObject?.dueDate || ''
        selectedElement.documentation = element.businessObject?.documentation?.[0]?.text || ''
        showProperties.value = true
      } else {
        Object.assign(selectedElement, { id: '', name: '', type: '', assignee: '', candidateUsers: '', dueDate: '', callType: '', callValue: '', documentation: '' })
      }
    })

    // 初始化XML内容
    const { xml } = await modeler.saveXML({ format: true })
    xmlContent.value = xml || ''
  } catch (err) {
    console.warn('bpmn-js 加载失败，使用模拟模式:', err)
  }
}

function updateElementName() {
  if (!modeler || !selectedElement.id) return
  const elementRegistry = modeler.get('elementRegistry')
  const modeling = modeler.get('modeling')
  const element = elementRegistry.get(selectedElement.id)
  if (element) {
    modeling.updateProperties(element, { name: selectedElement.name })
  }
}

function zoomIn() {
  if (!modeler) return
  const canvas = modeler.get('canvas')
  canvas.zoom(Math.min((canvas.zoom() || 1) + 0.1, 3))
}

function zoomOut() {
  if (!modeler) return
  const canvas = modeler.get('canvas')
  canvas.zoom(Math.max((canvas.zoom() || 1) - 0.1, 0.2))
}

function zoomReset() {
  modeler?.get('canvas').zoom('fit-viewport')
}

async function handleNew() {
  await ElMessageBox.confirm('当前内容将被替换，是否继续？', '提示', { type: 'warning' })
  if (modeler) {
    await modeler.importXML(defaultBpmnXml)
    modeler.get('canvas').zoom('fit-viewport')
    const { xml } = await modeler.saveXML({ format: true })
    xmlContent.value = xml || ''
  }
  ElMessage.success('已创建新流程')
}

function handleOpenProcess(item: ProcessItem) {
  ElMessage.success(`已打开流程：${item.name}`)
  drawerVisible.value = false
  // 实际应加载对应的BPMN XML
}

async function handleToggleStatus(item: ProcessItem) {
  const action = item.status === 1 ? '挂起' : '激活'
  await ElMessageBox.confirm(`确定要${action}流程「${item.name}」吗？`, '提示', { type: 'warning' })
  item.status = item.status === 1 ? 0 : 1
  ElMessage.success(`已${action}`)
}

async function handleSave() {
  saving.value = true
  try {
    if (modeler) {
      const { xml } = await modeler.saveXML({ format: true })
      xmlContent.value = xml || ''
      console.log('保存的BPMN XML:', xml)
    }
    await new Promise(r => setTimeout(r, 500))
    ElMessage.success('流程保存成功')
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

async function handleDeploy() {
  await ElMessageBox.confirm('确定要部署当前流程吗？', '部署确认', { type: 'info' })
  deploying.value = true
  try {
    if (modeler) {
      const { xml } = await modeler.saveXML({ format: true })
      console.log('部署的BPMN XML:', xml)
      // 调用 API: deployDefinition({ xml })
    }
    await new Promise(r => setTimeout(r, 800))
    ElMessage.success('流程部署成功')
  } catch {
    ElMessage.error('部署失败')
  } finally {
    deploying.value = false
  }
}

async function handleExportSVG() {
  if (!modeler) return
  try {
    const { svg } = await modeler.saveSVG()
    const blob = new Blob([svg], { type: 'image/svg+xml' })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'process.svg'
    a.click()
    URL.revokeObjectURL(url)
    ElMessage.success('SVG导出成功')
  } catch {
    ElMessage.error('导出失败')
  }
}

function toggleXmlEditor() {
  xmlEditorVisible.value = !xmlEditorVisible.value
  if (xmlEditorVisible.value && modeler) {
    modeler.saveXML({ format: true }).then(({ xml }: { xml: string }) => {
      xmlContent.value = xml || ''
    })
  }
}

async function applyXml() {
  if (!modeler || !xmlContent.value) return
  try {
    await modeler.importXML(xmlContent.value)
    modeler.get('canvas').zoom('fit-viewport')
    xmlEditorVisible.value = false
    ElMessage.success('XML已应用')
  } catch (err: any) {
    ElMessage.error('XML解析失败: ' + (err.message || '未知错误'))
  }
}

onMounted(() => {
  nextTick(() => {
    initModeler()
  })
})

onBeforeUnmount(() => {
  modeler?.destroy()
})
</script>

<style lang="scss" scoped>
.workflow-design-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 110px);
  background: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 16px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;

  .toolbar-left,
  .toolbar-right {
    display: flex;
    align-items: center;
    gap: 8px;
  }
}

.main-area {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.canvas-area {
  flex: 1;
  background: #fff;
  position: relative;

  :deep(.bjs-powered-by) {
    display: none;
  }
}

.xml-editor-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fff;

  .xml-editor-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 16px;
    border-bottom: 1px solid #e4e7ed;
    font-weight: 600;
    font-size: 14px;
  }

  .xml-textarea {
    flex: 1;

    :deep(.el-textarea__inner) {
      height: 100% !important;
      border: none;
      border-radius: 0;
      font-family: 'Consolas', 'Monaco', monospace;
      font-size: 13px;
      line-height: 1.5;
      resize: none;
    }
  }
}

.properties-panel {
  width: 300px;
  background: #fff;
  border-left: 1px solid #e4e7ed;
  flex-shrink: 0;

  .panel-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    border-bottom: 1px solid #e4e7ed;
    font-weight: 600;
    font-size: 14px;
  }

  .prop-form {
    padding: 16px;
  }
}

.mb-12 {
  margin-bottom: 12px;
}
</style>
