/** 角色信息 */
export interface RoleInfo {
  roleId: number
  roleName: string
  roleKey: string
  roleSort: number
  status: string
  menuIds?: number[]
  remark?: string
  createTime?: string
}

/** 菜单信息 */
export interface MenuInfo {
  menuId: number
  menuName: string
  parentId: number
  orderNum: number
  path: string
  component: string
  menuType: string  // M=目录 C=菜单 F=按钮
  visible: string
  status: string
  perms?: string
  icon?: string
  children?: MenuInfo[]
  createTime?: string
}

/** 部门信息 */
export interface DeptInfo {
  deptId: number
  deptName: string
  parentId: number
  orderNum: number
  leader: string
  phone: string
  email: string
  status: string
  children?: DeptInfo[]
  createTime?: string
}

/** 岗位信息 */
export interface PostInfo {
  postId: number
  postCode: string
  postName: string
  postSort: number
  status: string
  remark?: string
  createTime?: string
}

/** 字典类型 */
export interface DictType {
  dictId: number
  dictName: string
  dictType: string
  status: string
  remark?: string
  createTime?: string
}

/** 字典数据 */
export interface DictData {
  dictCode: number
  dictSort: number
  dictLabel: string
  dictValue: string
  dictType: string
  cssClass?: string
  listClass?: string
  status: string
}

/** 操作日志 */
export interface OperLog {
  operId: number
  title: string
  businessType: number
  method: string
  requestMethod: string
  operName: string
  operUrl: string
  operIp: string
  operLocation: string
  operParam: string
  jsonResult: string
  status: number
  errorMsg: string
  operTime: string
}

/** 资产信息 */
export interface AssetInfo {
  assetId: number
  assetCode: string
  assetName: string
  assetType: string
  brand: string
  model: string
  purchaseDate: string
  purchasePrice: number
  status: string
  location: string
  userId?: number
  userName?: string
  remark?: string
}
