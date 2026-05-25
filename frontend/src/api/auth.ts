import request from './request'
import type { LoginParams, LoginResult, UserInfo } from '@/types/user'
import type { ApiResponse } from '@/types/api'

/** 登录 */
export function login(data: LoginParams): Promise<ApiResponse<LoginResult>> {
  return request.post('/auth/login', data)
}

/** 登出 */
export function logout(): Promise<ApiResponse<void>> {
  return request.post('/auth/logout')
}

/** 获取用户信息 */
export function getInfo(): Promise<ApiResponse<UserInfo>> {
  return request.get('/auth/info')
}

/** 刷新 Token */
export function refreshToken(token: string): Promise<ApiResponse<LoginResult>> {
  return request.post('/auth/refresh', { token })
}

/** 获取验证码 */
export function getCaptcha(): Promise<ApiResponse<{ captchaKey: string; captchaImage: string }>> {
  return request.get('/auth/captcha')
}
