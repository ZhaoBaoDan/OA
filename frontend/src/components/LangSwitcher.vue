<template>
  <el-dropdown @command="handleSwitch" trigger="click">
    <el-button link class="lang-switcher">
      <el-icon><Connection /></el-icon>
      <span>{{ currentLabel }}</span>
    </el-button>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="zh-CN" :class="{ active: locale === 'zh-CN' }">
          🇨🇳 中文
        </el-dropdown-item>
        <el-dropdown-item command="en-US" :class="{ active: locale === 'en-US' }">
          🇺🇸 English
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { Connection } from '@element-plus/icons-vue'

const { locale } = useI18n()

const currentLabel = computed(() => locale.value === 'zh-CN' ? '中文' : 'English')

function handleSwitch(lang: string) {
  locale.value = lang
  localStorage.setItem('language', lang)
  // 刷新页面以更新 Element Plus 语言
  window.location.reload()
}
</script>

<style scoped>
.lang-switcher {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
}
.el-dropdown-menu__item.active {
  color: #409eff;
  font-weight: 600;
}
</style>
