/**
 * 表单验证规则集合
 */

/** 必填项 */
export const required = (message: string = '此项为必填项') => ({
  required: true,
  message,
  trigger: 'blur',
})

/** 下拉框必填 */
export const requiredSelect = (message: string = '请选择') => ({
  required: true,
  message,
  trigger: 'change',
})

/** 手机号验证 */
export const phoneRule = {
  pattern: /^1[3-9]\d{9}$/,
  message: '请输入正确的手机号码',
  trigger: 'blur',
}

/** 邮箱验证 */
export const emailRule = {
  pattern: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
  message: '请输入正确的邮箱地址',
  trigger: 'blur',
}

/** 身份证验证 */
export const idCardRule = {
  pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/,
  message: '请输入正确的身份证号码',
  trigger: 'blur',
}

/** 用户名验证: 4-20位字母数字下划线 */
export const usernameRule = {
  pattern: /^[a-zA-Z0-9_]{4,20}$/,
  message: '用户名需为4-20位字母、数字或下划线',
  trigger: 'blur',
}

/** 密码验证: 至少6位，包含字母和数字 */
export const passwordRule = {
  pattern: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]{6,20}$/,
  message: '密码需为6-20位，且包含字母和数字',
  trigger: 'blur',
}

/** IP地址验证 */
export const ipRule = {
  pattern: /^((25[0-5]|2[0-4]\d|[01]?\d\d?)\.){3}(25[0-5]|2[0-4]\d|[01]?\d\d?)$/,
  message: '请输入正确的IP地址',
  trigger: 'blur',
}

/** URL验证 */
export const urlRule = {
  pattern: /^https?:\/\/.+/,
  message: '请输入正确的URL地址',
  trigger: 'blur',
}

/**
 * 创建长度范围验证
 */
export function lengthRange(min: number, max: number, message?: string) {
  return {
    min,
    max,
    message: message || `长度需在 ${min} 到 ${max} 个字符之间`,
    trigger: 'blur',
  }
}
