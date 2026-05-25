/** 通用 API 响应类型 */
export interface ApiResponse<T = any> {
  code: number
  data: T
  message: string
}

/** 分页请求参数 */
export interface PageQuery {
  pageNum: number
  pageSize: number
  [key: string]: any
}

/** 分页响应数据 */
export interface PageResult<T = any> {
  total: number
  rows: T[]
  pageNum: number
  pageSize: number
}

/** 通用树形结构 */
export interface TreeNode {
  id: number
  label: string
  children?: TreeNode[]
  [key: string]: any
}

/** 选项类型 */
export interface OptionType {
  label: string
  value: string | number
  disabled?: boolean
  children?: OptionType[]
}
