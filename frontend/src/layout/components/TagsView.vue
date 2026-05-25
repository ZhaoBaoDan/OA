<template>
  <div class="tags-view-container">
    <div class="tags-view-wrapper">
      <router-link
        v-for="tag in visitedViews"
        :key="tag.path"
        :to="{ path: tag.path, query: tag.query }"
        :class="['tags-view-item', { active: isActive(tag) }]"
        @click.middle="closeTag(tag)"
      >
        {{ tag.title }}
        <el-icon v-if="!isAffix(tag)" class="tag-close" @click.prevent.stop="closeTag(tag)"><Close /></el-icon>
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Close } from '@element-plus/icons-vue'

interface TagView {
  path: string
  title: string
  query?: any
  affix?: boolean
}

const route = useRoute()
const router = useRouter()
const visitedViews = ref<TagView[]>([])

const affixTags: TagView[] = [
  { path: '/', title: '首页', affix: true }
]

const isActive = (tag: TagView) => tag.path === route.path
const isAffix = (tag: TagView) => tag.affix

const addView = () => {
  const { path, meta, query } = route
  if (path && !visitedViews.value.find(v => v.path === path)) {
    visitedViews.value.push({
      path,
      title: (meta?.title as string) || '未命名',
      query
    })
  }
}

const closeTag = (tag: TagView) => {
  if (tag.affix) return
  const idx = visitedViews.value.findIndex(v => v.path === tag.path)
  if (idx > -1) visitedViews.value.splice(idx, 1)
  if (isActive(tag)) {
    const last = visitedViews.value[visitedViews.value.length - 1]
    router.push(last ? last.path : '/')
  }
}

watch(() => route.path, () => addView(), { immediate: true })
</script>

<style scoped>
.tags-view-container {
  height: 34px;
  background: #fff;
  border-bottom: 1px solid #d8dce5;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.12);
  padding: 0 8px;
  display: flex;
  align-items: center;
}
.tags-view-wrapper {
  display: flex;
  gap: 4px;
  overflow-x: auto;
  white-space: nowrap;
}
.tags-view-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  height: 26px;
  padding: 0 10px;
  font-size: 12px;
  color: #495060;
  background: #fff;
  border: 1px solid #d8dce5;
  border-radius: 3px;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.2s;
}
.tags-view-item:hover {
  color: #409eff;
}
.tags-view-item.active {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
}
.tag-close {
  font-size: 12px;
  border-radius: 50%;
  transition: all 0.2s;
}
.tag-close:hover {
  background: rgba(0, 0, 0, 0.15);
}
</style>
