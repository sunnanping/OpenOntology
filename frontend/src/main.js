import { createApp } from 'vue'
import { createI18n } from 'vue-i18n'
import App from './App.vue'
import router from './router'
import axios from 'axios'

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

axios.defaults.baseURL = 'http://localhost:9000'
axios.defaults.headers.common['Content-Type'] = 'application/json'

const app = createApp(App)
app.use(router)
app.use(i18n)
app.mount('#app')