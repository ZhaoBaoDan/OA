/** 用户信息 */
export interface UserInfo {
  userId: number
  username: string
  nickname: string
  email: string
  phone: string
  avatar: string
  sex: string
  status: string
  deptId: number
  deptName: string
  roles: string[]
  permissions: string[]
}

/** 登录参数 */
export interface LoginParams {
  username: string
  password: string
  captchaCode?: string
  captchaKey?: string
}

/** 登录响应 */
export interface LoginResult {
  token: string
  refreshToken?: string
}

/** 用户查询参数 */
export interface UserQuery {
  username?: string
  nickname?: string
  phone?: string
  status?: string
  deptId?: number
  pageNum: number
  pageSize: number
}

/** 用户表单 */
export interface UserForm {
  userId?: number
  username: string
  nickname: string
  password?: string
  email?: string
  phone?: string
  sex?: string
  status?: string
  deptId?: number
  roleIds?: number[]
  postIds?: number[]
  remark?: string
}
