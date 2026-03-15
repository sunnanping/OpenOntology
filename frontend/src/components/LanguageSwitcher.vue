<template>
  <div class="language-switcher">
    <el-dropdown @command="handleLanguageChange" trigger="click">
      <span class="language-btn">
        <i class="bi bi-globe"></i>
        {{ currentLanguageLabel }}
        <i class="el-icon-arrow-down el-icon--right"></i>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item 
            v-for="lang in availableLanguages" 
            :key="lang.code"
            :command="lang.code"
            :disabled="currentLocale === lang.code"
          >
            {{ lang.name }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

const { locale } = useI18n()

const currentLocale = computed(() => locale.value)

const availableLanguages = [
  { code: 'en', name: 'English' },
  { code: 'zh-CN', name: '简体中文' },
  { code: 'zh-TW', name: '繁體中文' },
  { code: 'de', name: 'Deutsch' },
  { code: 'fr', name: 'Français' },
  { code: 'es', name: 'Español' },
  { code: 'ja', name: '日本語' },
  { code: 'ko', name: '한국어' }
]

const currentLanguageLabel = computed(() => {
  const lang = availableLanguages.find(l => l.code === currentLocale.value)
  return lang ? lang.name : currentLocale.value
})

const handleLanguageChange = (langCode) => {
  locale.value = langCode
  localStorage.setItem('locale', langCode)
  // 刷新页面以应用新语言
  window.location.reload()
}
</script>

<style scoped>
.language-switcher {
  display: inline-block;
}

.language-btn {
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.language-btn:hover {
  background-color: rgba(0, 0, 0, 0.05);
}
</style>