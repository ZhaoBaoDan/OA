import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { useUserStore } from '@/stores/user'
import Layout from '@/layout/index.vue'

NProgress.configure({ showSpinner: false })

/** 静态路由 - 不需要权限 */
export const constantRoutes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { hidden: true },
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/views/login/index.vue'), // 简化处理
    meta: { hidden: true },
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'HomeFilled', affix: true },
      },
    ],
  },
  {
    path: '/system',
    component: Layout,
    redirect: '/system/user',
    meta: { title: '系统管理', icon: 'Setting' },
    children: [
      {
        path: 'user',
        name: 'SystemUser',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', icon: 'User' },
      },
      {
        path: 'role',
        name: 'SystemRole',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理', icon: 'UserFilled' },
      },
      {
        path: 'menu',
        name: 'SystemMenu',
        component: () => import('@/views/system/menu/index.vue'),
        meta: { title: '菜单管理', icon: 'Menu' },
      },
      {
        path: 'dept',
        name: 'SystemDept',
        component: () => import('@/views/system/dept/index.vue'),
        meta: { title: '部门管理', icon: 'OfficeBuilding' },
      },
      {
        path: 'post',
        name: 'SystemPost',
        component: () => import('@/views/system/post/index.vue'),
        meta: { title: '岗位管理', icon: 'Briefcase' },
      },
      {
        path: 'dict',
        name: 'SystemDict',
        component: () => import('@/views/system/dict/index.vue'),
        meta: { title: '字典管理', icon: 'Collection' },
      },
      {
        path: 'log',
        name: 'SystemLog',
        component: () => import('@/views/system/log/index.vue'),
        meta: { title: '操作日志', icon: 'Document' },
      },
    ],
  },
  {
    path: '/workflow',
    component: Layout,
    redirect: '/workflow/design',
    meta: { title: '流程管理', icon: 'Connection' },
    children: [
      {
        path: 'design',
        name: 'WorkflowDesign',
        component: () => import('@/views/workflow/design/index.vue'),
        meta: { title: '流程设计', icon: 'Edit' },
      },
      {
        path: 'instance',
        name: 'WorkflowInstance',
        component: () => import('@/views/workflow/instance/index.vue'),
        meta: { title: '流程实例', icon: 'List' },
      },
      {
        path: 'task',
        name: 'WorkflowTask',
        component: () => import('@/views/workflow/task/index.vue'),
        meta: { title: '我的待办', icon: 'Tickets' },
      },
    ],
  },
  {
    path: '/attendance',
    component: Layout,
    redirect: '/attendance/checkin',
    meta: { title: '考勤管理', icon: 'Clock' },
    children: [
      {
        path: 'checkin',
        name: 'AttendanceCheckin',
        component: () => import('@/views/attendance/checkin/index.vue'),
        meta: { title: '考勤打卡', icon: 'AlarmClock' },
      },
      {
        path: 'record',
        name: 'AttendanceRecord',
        component: () => import('@/views/attendance/record/index.vue'),
        meta: { title: '考勤记录', icon: 'Tickets' },
      },
    ],
  },
  {
    path: '/document',
    component: Layout,
    redirect: '/document/list',
    meta: { title: '文档管理', icon: 'Folder' },
    children: [
      {
        path: 'list',
        name: 'DocumentList',
        component: () => import('@/views/document/list/index.vue'),
        meta: { title: '文档列表', icon: 'Files' },
      },
      {
        path: 'template',
        name: 'DocumentTemplate',
        component: () => import('@/views/document/template/index.vue'),
        meta: { title: '文档模板', icon: 'DocumentCopy' },
      },
    ],
  },
  {
    path: '/task',
    component: Layout,
    redirect: '/task/board',
    meta: { title: '任务管理', icon: 'Finished' },
    children: [
      {
        path: 'board',
        name: 'TaskBoard',
        component: () => import('@/views/task/board/index.vue'),
        meta: { title: '任务看板', icon: 'Grid' },
      },
      {
        path: 'gantt',
        name: 'TaskGantt',
        component: () => import('@/views/task/gantt/index.vue'),
        meta: { title: '甘特图', icon: 'DataLine' },
      },
    ],
  },
  {
    path: '/meeting',
    component: Layout,
    redirect: '/meeting/room',
    meta: { title: '会议管理', icon: 'VideoCamera' },
    children: [
      {
        path: 'room',
        name: 'MeetingRoom',
        component: () => import('@/views/meeting/room/index.vue'),
        meta: { title: '会议室预约', icon: 'OfficeBuilding' },
      },
      {
        path: 'record',
        name: 'MeetingRecord',
        component: () => import('@/views/meeting/record/index.vue'),
        meta: { title: '会议记录', icon: 'Notebook' },
      },
    ],
  },
  {
    path: '/asset',
    component: Layout,
    redirect: '/asset/list',
    meta: { title: '资产管理', icon: 'Box' },
    children: [
      {
        path: 'list',
        name: 'AssetList',
        component: () => import('@/views/asset/list/index.vue'),
        meta: { title: '资产列表', icon: 'Goods' },
      },
      {
        path: 'inventory',
        name: 'AssetInventory',
        component: () => import('@/views/asset/inventory/index.vue'),
        meta: { title: '资产盘点', icon: 'Checked' },
      },
    ],
  },
  {
    path: '/report',
    component: Layout,
    children: [
      {
        path: 'center',
        name: 'ReportCenter',
        component: () => import('@/views/report/center/index.vue'),
        meta: { title: '报表中心', icon: 'DataAnalysis' },
      },
    ],
  },
  {
    path: '/notification',
    component: Layout,
    children: [
      {
        path: 'list',
        name: 'NotificationList',
        component: () => import('@/views/notification/list/index.vue'),
        meta: { title: '通知中心', icon: 'Bell' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
  scrollBehavior: () => ({ top: 0 }),
})

// 白名单路由
const whiteList = ['/login']

// 路由守卫
router.beforeEach(async (to, _from, next) => {
  NProgress.start()
  const hasToken = getToken()

  if (hasToken) {
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else {
      const userStore = useUserStore()
      if (userStore.username) {
        next()
      } else {
        try {
          await userStore.fetchUserInfo()
          next({ ...to, replace: true })
        } catch {
          userStore.resetState()
          next(`/login?redirect=${to.path}`)
          NProgress.done()
        }
      }
    }
  } else {
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router
