import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { UserInfo, LoginParams } from '@/types/user'
import { login as loginApi, logout as logoutApi, getInfo } from '@/api/auth'
import { getToken, setToken, removeToken, setRefreshToken, clearAuth } from '@/utils/auth'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(getToken() || '')
  const userInfo = ref<UserInfo | null>(null)

  const username = computed(() => userInfo.value?.username || '')
  const nickname = computed(() => userInfo.value?.nickname || '')
  const avatar = computed(() => userInfo.value?.avatar || '')
  const roles = computed(() => userInfo.value?.roles || [])
  const permissions = computed(() => userInfo.value?.permissions || [])

  /** 登录 */
  async function login(loginParams: LoginParams) {
    const res = await loginApi(loginParams)
    const { token: newToken, refreshToken } = res.data
    token.value = newToken
    setToken(newToken)
    if (refreshToken) {
      setRefreshToken(refreshToken)
    }
  }

  /** 获取用户信息 */
  async function fetchUserInfo() {
    const res = await getInfo()
    userInfo.value = res.data
    return res.data
  }

  /** 登出 */
  async function logout() {
    try {
      await logoutApi()
    } catch {
      // 即使接口调用失败也继续清理
    }
    resetState()
    router.push('/login')
  }

  /** 重置状态 */
  function resetState() {
    token.value = ''
    userInfo.value = null
    clearAuth()
  }

  return {
    token,
    userInfo,
    username,
    nickname,
    avatar,
    roles,
    permissions,
    login,
    fetchUserInfo,
    logout,
    resetState,
  }
})
