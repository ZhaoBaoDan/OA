<template>
  <div class="image-upload-wrapper">
    <el-upload
      ref="uploadRef"
      :action="uploadUrl"
      :headers="uploadHeaders"
      :show-file-list="false"
      :before-upload="beforeUpload"
      :on-success="handleSuccess"
      :on-error="handleError"
      accept="image/*"
      drag
    >
      <div v-if="imageUrl" class="preview-wrapper">
        <img :src="imageUrl" class="preview-image" />
        <div class="preview-actions">
          <el-icon class="action-icon" @click.stop="handlePreview"><ZoomIn /></el-icon>
          <el-icon class="action-icon" @click.stop="handleRemove"><Delete /></el-icon>
        </div>
      </div>
      <div v-else class="upload-placeholder">
        <el-icon class="upload-icon"><Plus /></el-icon>
        <span class="upload-text">拖拽或点击上传图片</span>
      </div>
    </el-upload>

    <!-- 图片预览弹窗 -->
    <el-dialog v-model="previewVisible" title="图片预览" width="600px" append-to-body>
      <img :src="imageUrl" style="width: 100%; object-fit: contain" />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { UploadProps, UploadInstance } from 'element-plus'
import { getToken } from '@/utils/auth'

interface Props {
  modelValue?: string
  uploadUrl?: string
  maxSize?: number
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  uploadUrl: '/api/upload/image',
  maxSize: 5,
})

const emit = defineEmits<{
  (e: 'update:modelValue', val: string): void
}>()

const uploadRef = ref<UploadInstance>()
const previewVisible = ref(false)

const imageUrl = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
})

const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${getToken()}`,
}))

function beforeUpload(file: File) {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  const isLt = file.size / 1024 / 1024 < props.maxSize
  if (!isLt) {
    ElMessage.error(`图片大小不能超过 ${props.maxSize}MB!`)
    return false
  }
  return true
}

const handleSuccess: UploadProps['onSuccess'] = (res) => {
  if (res.code === 200) {
    imageUrl.value = res.data.url
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

const handleError = () => {
  ElMessage.error('上传失败')
}

function handlePreview(e: Event) {
  e.stopPropagation()
  previewVisible.value = true
}

function handleRemove(e: Event) {
  e.stopPropagation()
  imageUrl.value = ''
}
</script>

<style lang="scss" scoped>
.image-upload-wrapper {
  :deep(.el-upload-dragger) {
    width: 148px;
    height: 148px;
    padding: 0;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.preview-wrapper {
  width: 100%;
  height: 100%;
  position: relative;

  &:hover .preview-actions {
    opacity: 1;
  }
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.preview-actions {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  opacity: 0;
  transition: opacity 0.3s;
}

.action-icon {
  color: #fff;
  font-size: 20px;
  cursor: pointer;

  &:hover {
    color: #409eff;
  }
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.upload-icon {
  font-size: 28px;
  color: #8c939d;
}

.upload-text {
  font-size: 12px;
  color: #8c939d;
}
</style>
