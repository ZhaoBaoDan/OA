<template>
  <div class="dict-tag-wrapper">
    <template v-if="options.length">
      <template v-for="item in options" :key="item.dictValue">
        <el-tag
          v-if="String(item.dictValue) === String(value)"
          :type="getTagType(item)"
          :class="item.cssClass"
          size="small"
        >
          {{ item.dictLabel }}
        </el-tag>
      </template>
    </template>
    <span v-else>{{ value }}</span>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { getDictData } from '@/utils/dict'

interface DictItem {
  dictValue: string | number
  dictLabel: string
  cssClass?: string
  listClass?: string
}

interface Props {
  value: string | number
  options?: DictItem[]
  dictType?: string
}

const props = withDefaults(defineProps<Props>(), {
  options: () => [],
  dictType: '',
})

const options = ref<DictItem[]>(props.options)

watch(
  () => props.options,
  (val) => {
    if (val.length) options.value = val
  }
)

onMounted(async () => {
  if (props.dictType && !props.options.length) {
    const data = await getDictData(props.dictType)
    options.value = data || []
  }
})

function getTagType(item: DictItem): '' | 'success' | 'warning' | 'info' | 'danger' {
  const map: Record<string, '' | 'success' | 'warning' | 'info' | 'danger'> = {
    default: '',
    success: 'success',
    warning: 'warning',
    info: 'info',
    danger: 'danger',
  }
  return map[item.listClass || 'default'] || ''
}
</script>

<style lang="scss" scoped>
.dict-tag-wrapper {
  display: inline-block;
}
</style>
