import { createI18n } from 'vue-i18n'
import zhCn from './zh-CN'
import enUs from './en-US'
import zhCnEl from 'element-plus/es/locale/lang/zh-cn'
import enUsEl from 'element-plus/es/locale/lang/en'

const i18n = createI18n({
  legacy: false, // 使用 Composition API 模式
  locale: localStorage.getItem('language') || 'zh-CN',
  fallbackLocale: 'zh-CN',
  messages: {
    'zh-CN': zhCn,
    'en-US': enUs,
  },
})

/** Element Plus 语言包映射 */
export const elementLocaleMap: Record<string, any> = {
  'zh-CN': zhCnEl,
  'en-US': enUsEl,
}

export default i18n
