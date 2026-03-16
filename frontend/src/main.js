import { createApp } from 'vue'
import { createI18n } from 'vue-i18n'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import request from './utils/request'

import en from './locales/en.json'
import zhCN from './locales/zh-CN.json'

const savedLocale = localStorage.getItem('locale') || 'en'

const i18n = createI18n({
  legacy: false,
  locale: savedLocale,
  fallbackLocale: 'en',
  messages: {
    en,
    'zh-CN': zhCN
  }
})

// 将request实例挂载到Vue全局
const app = createApp(App)
app.config.globalProperties.$http = request
// 提供$http实例，供组合式API使用
app.provide('$http', request)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(router)
app.use(i18n)
app.use(ElementPlus)
app.mount('#app')
