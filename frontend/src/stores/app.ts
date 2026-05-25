import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  /** 侧边栏是否折叠 */
  const sidebarCollapsed = ref(false)
  /** 设备类型 */
  const device = ref<'desktop' | 'mobile'>('desktop')
  /** 主题色 */
  const themeColor = ref('#409eff')
  /** 暗黑模式 */
  const isDark = ref(false)

  /** 切换侧边栏 */
  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  /** 设置设备类型 */
  function setDevice(val: 'desktop' | 'mobile') {
    device.value = val
  }

  /** 切换暗黑模式 */
  function toggleDark() {
    isDark.value = !isDark.value
    document.documentElement.classList.toggle('dark', isDark.value)
  }

  /** 设置主题色 */
  function setThemeColor(color: string) {
    themeColor.value = color
    document.documentElement.style.setProperty('--el-color-primary', color)
  }

  return {
    sidebarCollapsed,
    device,
    themeColor,
    isDark,
    toggleSidebar,
    setDevice,
    toggleDark,
    setThemeColor,
  }
})
