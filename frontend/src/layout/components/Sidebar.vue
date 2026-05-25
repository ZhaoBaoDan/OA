<template>
  <div class="sidebar">
    <!-- Logo -->
    <div class="sidebar-logo">
      <img src="@/../public/vite.svg" alt="Logo" class="logo-img" />
      <span v-show="!appStore.sidebarCollapsed" class="logo-title">SmartAuto OA</span>
    </div>

    <!-- 菜单 -->
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <el-menu
        :default-active="activeMenu"
        :collapse="appStore.sidebarCollapsed"
        :collapse-transition="false"
        background-color="#001529"
        text-color="#ffffffa6"
        active-text-color="#ffffff"
        :unique-opened="true"
        router
      >
        <template v-for="route in menuRoutes" :key="route.path">
          <!-- 单个菜单项 (无子菜单或只有一个子菜单) -->
          <template v-if="!route.children || route.children.length === 0">
            <el-menu-item :index="route.path">
              <el-icon v-if="route.meta?.icon">
                <component :is="route.meta.icon" />
              </el-icon>
              <template #title>{{ route.meta?.title }}</template>
            </el-menu-item>
          </template>

          <template v-else-if="hasOneShowingChild(route)">
            <el-menu-item :index="getOnlyChildPath(route)">
              <el-icon v-if="onlyChild?.meta?.icon">
                <component :is="onlyChild.meta.icon" />
              </el-icon>
              <template #title>{{ onlyChild?.meta?.title }}</template>
            </el-menu-item>
          </template>

          <!-- 多个子菜单 -->
          <template v-else>
            <el-sub-menu :index="route.path">
              <template #title>
                <el-icon v-if="route.meta?.icon">
                  <component :is="route.meta.icon" />
                </el-icon>
                <span>{{ route.meta?.title }}</span>
              </template>

              <template v-for="child in route.children" :key="child.path">
                <el-menu-item v-if="!child.meta?.hidden" :index="resolvePath(route.path, child.path)">
                  <el-icon v-if="child.meta?.icon">
                    <component :is="child.meta.icon" />
                  </el-icon>
                  <template #title>{{ child.meta?.title }}</template>
                </el-menu-item>
              </template>
            </el-sub-menu>
          </template>
        </template>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { constantRoutes } from '@/router'
import type { RouteRecordRaw } from 'vue-router'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()

const onlyChild = ref<RouteRecordRaw | null>(null)

/** 当前激活菜单 */
const activeMenu = computed(() => {
  const { meta, path } = route
  if (meta?.activeMenu) {
    return meta.activeMenu as string
  }
  return path
})

/** 过滤隐藏菜单 */
const menuRoutes = computed(() => {
  return constantRoutes.filter((r) => !r.meta?.hidden)
})

/** 判断是否只有一个显示的子菜单 */
function hasOneShowingChild(parent: RouteRecordRaw): boolean {
  const children = (parent.children || []).filter((c) => !c.meta?.hidden)
  if (children.length === 1) {
    onlyChild.value = children[0]
    return true
  }
  if (children.length === 0) {
    onlyChild.value = { ...parent, path: '' } as RouteRecordRaw
    return true
  }
  return false
}

/** 获取唯一子菜单路径 */
function getOnlyChildPath(parent: RouteRecordRaw): string {
  if (onlyChild.value) {
    return resolvePath(parent.path, onlyChild.value.path)
  }
  return parent.path
}

/** 拼接路径 */
function resolvePath(parentPath: string, childPath: string): string {
  if (childPath.startsWith('/')) return childPath
  return `${parentPath}/${childPath}`.replace(/\/+/g, '/')
}
</script>

<style lang="scss" scoped>
.sidebar {
  height: 100%;
  background-color: #001529;
  overflow: hidden;
}

.sidebar-logo {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 16px;
  background-color: #002140;
  overflow: hidden;

  .logo-img {
    width: 32px;
    height: 32px;
    flex-shrink: 0;
  }

  .logo-title {
    margin-left: 10px;
    color: #fff;
    font-size: 16px;
    font-weight: 600;
    white-space: nowrap;
    overflow: hidden;
  }
}

:deep(.el-menu) {
  border-right: none;
}

:deep(.el-menu-item.is-active) {
  background-color: #409eff !important;
}

:deep(.el-menu-item:hover) {
  background-color: #ffffff1a !important;
}

:deep(.el-sub-menu__title:hover) {
  background-color: #ffffff1a !important;
}

:deep(.scrollbar-wrapper) {
  overflow-x: hidden !important;
}

:deep(.el-scrollbar__bar.is-horizontal) {
  display: none;
}
</style>
