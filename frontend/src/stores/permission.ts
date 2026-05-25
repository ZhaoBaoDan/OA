import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { RouteRecordRaw } from 'vue-router'
import { constantRoutes } from '@/router'

// 动态导入布局和视图组件
const layoutModules = import.meta.glob('@/layout/**/*.vue')
const viewModules = import.meta.glob('@/views/**/*.vue')

/**
 * 根据后端返回的菜单数据动态生成路由
 */
function filterAsyncRoutes(menus: any[]): RouteRecordRaw[] {
  const routes: RouteRecordRaw[] = []

  menus.forEach((menu) => {
    if (menu.menuType === 'F') return // 按钮权限不生成路由

    const route: RouteRecordRaw = {
      path: menu.path,
      name: menu.path?.replace(/\//g, '-'),
      meta: {
        title: menu.menuName,
        icon: menu.icon,
        hidden: menu.visible === '1',
        noCache: false,
      },
      children: [],
      component: undefined,
    } as any

    // 顶级目录使用布局组件
    if (menu.parentId === 0) {
      ;(route as any).component = layoutModules['/src/layout/index.vue']
    } else if (menu.component) {
      const componentPath = `/src/views/${menu.component}.vue`
      ;(route as any).component = viewModules[componentPath]
    }

    if (menu.children && menu.children.length > 0) {
      route.children = filterAsyncRoutes(menu.children)
    }

    routes.push(route)
  })

  return routes
}

export const usePermissionStore = defineStore('permission', () => {
  const routes = ref<RouteRecordRaw[]>([])
  const addRoutes = ref<RouteRecordRaw[]>([])

  /** 根据权限生成可访问路由 */
  function generateRoutes(menus: any[]) {
    const asyncRoutes = filterAsyncRoutes(menus)
    addRoutes.value = asyncRoutes
    routes.value = constantRoutes.concat(asyncRoutes)
    return asyncRoutes
  }

  /** 重置路由 */
  function resetRoutes() {
    routes.value = []
    addRoutes.value = []
  }

  return {
    routes,
    addRoutes,
    generateRoutes,
    resetRoutes,
  }
})
