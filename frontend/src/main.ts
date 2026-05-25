import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'virtual:uno.css'
import App from './App.vue'
import router from './router'
import i18n, { elementLocaleMap } from './i18n'
import './assets/styles/index.scss'

const app = createApp(App)
const pinia = createPinia()

// 注册所有 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(pinia)
app.use(router)
app.use(i18n)

// Element Plus 使用当前语言
const currentLang = i18n.global.locale.value
app.use(ElementPlus, { locale: elementLocaleMap[currentLang] || elementLocaleMap['zh-CN'] })

app.mount('#app')
