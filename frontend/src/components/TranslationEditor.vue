<template>
  <div class="translation-editor">
    <el-card class="translation-card">
      <template #header>
        <div class="card-header">
          <span>{{ $t('ontology.translations') }}</span>
          <el-button type="primary" size="small" @click="showAddDialog = true">
            <i class="bi bi-plus"></i> {{ $t('ontology.addTranslation') }}
          </el-button>
        </div>
      </template>
      
      <el-table :data="translationList" style="width: 100%" v-if="translationList.length > 0">
        <el-table-column prop="language" :label="$t('ontology.language')" width="150">
          <template #default="scope">
            <el-tag>{{ getLanguageName(scope.row.language) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="value" :label="$t('ontology.value')">
          <template #default="scope">
            <span>{{ scope.row.value }}</span>
          </template>
        </el-table-column>
        <el-table-column :label="$t('app.actions')" width="150">
          <template #default="scope">
            <el-button type="primary" size="small" @click="editTranslation(scope.row)">
              <i class="bi bi-pencil"></i>
            </el-button>
            <el-button type="danger" size="small" @click="deleteTranslation(scope.row.language)">
              <i class="bi bi-trash"></i>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-else :description="$t('messages.noData')"></el-empty>
    </el-card>

    <!-- Add/Edit Dialog -->
    <el-dialog
      v-model="showAddDialog"
      :title="isEditing ? $t('ontology.editTranslation') : $t('ontology.addTranslation')"
      width="500px"
    >
      <el-form :model="form" label-width="100px">
        <el-form-item :label="$t('ontology.language')">
          <el-select v-model="form.language" :disabled="isEditing" style="width: 100%">
            <el-option
              v-for="lang in availableLanguages"
              :key="lang.code"
              :label="lang.name"
              :value="lang.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('ontology.value')">
          <el-input
            v-model="form.value"
            type="textarea"
            :rows="3"
            :placeholder="$t('ontology.enterValue')"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">{{ $t('app.cancel') }}</el-button>
        <el-button type="primary" @click="saveTranslation">{{ $t('app.save') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import axios from 'axios'

const props = defineProps({
  entityRef: {
    type: String,
    required: true
  },
  entityType: {
    type: String,
    required: true
  }
})

const { t } = useI18n()

const translations = ref({})
const showAddDialog = ref(false)
const isEditing = ref(false)
const form = ref({
  language: '',
  value: ''
})

const availableLanguages = [
  { code: 'en', name: 'English' },
  { code: 'zh-CN', name: '简体中文' },
  { code: 'zh-TW', name: '繁體中文' },
  { code: 'de', name: 'Deutsch' },
  { code: 'fr', name: 'Français' },
  { code: 'es', name: 'Español' },
  { code: 'it', name: 'Italiano' },
  { code: 'ja', name: '日本語' },
  { code: 'ko', name: '한국어' },
  { code: 'ru', name: 'Русский' },
  { code: 'pt', name: 'Português' },
  { code: 'ar', name: 'العربية' }
]

const translationList = computed(() => {
  return Object.entries(translations.value).map(([language, value]) => ({
    language,
    value
  }))
})

const getLanguageName = (code) => {
  const lang = availableLanguages.find(l => l.code === code)
  return lang ? lang.name : code
}

const loadTranslations = async () => {
  try {
    const response = await axios.get(`/api/i18n/translations/${props.entityType}/${props.entityRef}`)
    if (response.data && response.data.translations) {
      translations.value = response.data.translations
    }
  } catch (error) {
    console.error('Failed to load translations:', error)
  }
}

const editTranslation = (row) => {
  isEditing.value = true
  form.value = {
    language: row.language,
    value: row.value
  }
  showAddDialog.value = true
}

const saveTranslation = async () => {
  try {
    if (isEditing.value) {
      await axios.put(`/api/i18n/translations/${props.entityType}/${props.entityRef}/${form.value.language}`, {
        value: form.value.value
      })
    } else {
      await axios.post(`/api/i18n/translations/${props.entityType}/${props.entityRef}`, {
        [form.value.language]: form.value.value
      })
    }
    await loadTranslations()
    showAddDialog.value = false
    resetForm()
  } catch (error) {
    console.error('Failed to save translation:', error)
    alert(t('messages.saveError'))
  }
}

const deleteTranslation = async (language) => {
  if (!confirm(t('messages.confirmDelete'))) return
  
  try {
    await axios.delete(`/api/i18n/translations/${props.entityType}/${props.entityRef}/${language}`)
    await loadTranslations()
  } catch (error) {
    console.error('Failed to delete translation:', error)
    alert(t('messages.deleteError'))
  }
}

const resetForm = () => {
  form.value = {
    language: '',
    value: ''
  }
  isEditing.value = false
}

watch(() => props.entityRef, () => {
  if (props.entityRef) {
    loadTranslations()
  }
}, { immediate: true })
</script>

<style scoped>
.translation-editor {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.translation-card {
  margin-bottom: 20px;
}
</style>