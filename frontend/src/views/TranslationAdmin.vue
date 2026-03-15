<template>
  <div class="translation-admin">
    <el-card class="admin-card">
      <template #header>
        <div class="card-header">
          <span>{{ $t('translation.adminTitle') }}</span>
          <el-button type="primary" size="small" @click="exportHistory">
            <i class="bi bi-download"></i> {{ $t('translation.export') }}
          </el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane :label="$t('translation.pendingTranslations')" name="pending">
          <div class="filter-section">
            <el-form :inline="true" :model="filterForm" class="filter-form">
              <el-form-item :label="$t('translation.entityType')">
                <el-select v-model="filterForm.entityType" clearable placeholder="Select">
                  <el-option label="Class" value="class" />
                  <el-option label="Property" value="property" />
                  <el-option label="Instance" value="instance" />
                </el-select>
              </el-form-item>
              <el-form-item :label="$t('translation.language')">
                <el-select v-model="filterForm.language" clearable placeholder="Select">
                  <el-option
                    v-for="lang in availableLanguages"
                    :key="lang.code"
                    :label="lang.name"
                    :value="lang.code"
                  />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="loadPendingTranslations">
                  {{ $t('common.search') }}
                </el-button>
                <el-button @click="resetFilter">{{ $t('common.reset') }}</el-button>
              </el-form-item>
            </el-form>
          </div>

          <el-table :data="pendingTranslations" style="width: 100%" v-loading="loading">
            <el-table-column prop="entityRef" :label="$t('translation.entityRef')" width="200" />
            <el-table-column prop="entityType" :label="$t('translation.entityType')" width="120" />
            <el-table-column prop="language" :label="$t('translation.language')" width="120">
              <template #default="scope">
                <el-tag>{{ getLanguageName(scope.row.language) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="value" :label="$t('translation.value')" />
            <el-table-column prop="lastProposedBy" :label="$t('translation.lastProposedBy')" width="120" />
            <el-table-column prop="lastProposedAt" :label="$t('translation.lastProposedAt')" width="150">
              <template #default="scope">
                {{ formatDate(scope.row.lastProposedAt) }}
              </template>
            </el-table-column>
            <el-table-column prop="source" :label="$t('translation.source')" width="100" />
            <el-table-column :label="$t('app.actions')" width="200">
              <template #default="scope">
                <el-button type="success" size="small" @click="confirmTranslation(scope.row)">
                  <i class="bi bi-check"></i> {{ $t('translation.confirm') }}
                </el-button>
                <el-button type="danger" size="small" @click="rejectTranslation(scope.row)">
                  <i class="bi bi-x"></i> {{ $t('translation.reject') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-if="total > 0"
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadPendingTranslations"
            @current-change="loadPendingTranslations"
            style="margin-top: 20px; text-align: right"
          />
        </el-tab-pane>

        <el-tab-pane :label="$t('translation.translationHistory')" name="history">
          <div class="filter-section">
            <el-form :inline="true" :model="historyFilter" class="filter-form">
              <el-form-item :label="$t('translation.entityRef')">
                <el-input v-model="historyFilter.entityRef" clearable placeholder="Entity Ref" />
              </el-form-item>
              <el-form-item :label="$t('translation.entityType')">
                <el-select v-model="historyFilter.entityType" clearable placeholder="Select">
                  <el-option label="Class" value="class" />
                  <el-option label="Property" value="property" />
                  <el-option label="Instance" value="instance" />
                </el-select>
              </el-form-item>
              <el-form-item :label="$t('translation.language')">
                <el-select v-model="historyFilter.langTag" clearable placeholder="Select">
                  <el-option
                    v-for="lang in availableLanguages"
                    :key="lang.code"
                    :label="lang.name"
                    :value="lang.code"
                  />
                </el-select>
              </el-form-item>
              <el-form-item :label="$t('translation.status')">
                <el-select v-model="historyFilter.status" clearable placeholder="Select">
                  <el-option label="Pending" value="PENDING" />
                  <el-option label="Approved" value="APPROVED" />
                  <el-option label="Rejected" value="REJECTED" />
                </el-select>
              </el-form-item>
              <el-form-item :label="$t('translation.proposedBy')">
                <el-input v-model="historyFilter.username" clearable placeholder="Username" />
              </el-form-item>
              <el-form-item :label="$t('translation.startDate')">
                <el-date-picker
                  v-model="historyFilter.startDate"
                  type="datetime"
                  placeholder="Start Date"
                  format="YYYY-MM-DD HH:mm:ss"
                  value-format="YYYY-MM-DD HH:mm:ss"
                />
              </el-form-item>
              <el-form-item :label="$t('translation.endDate')">
                <el-date-picker
                  v-model="historyFilter.endDate"
                  type="datetime"
                  placeholder="End Date"
                  format="YYYY-MM-DD HH:mm:ss"
                  value-format="YYYY-MM-DD HH:mm:ss"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="loadHistory">
                  {{ $t('common.search') }}
                </el-button>
                <el-button @click="resetHistoryFilter">{{ $t('common.reset') }}</el-button>
              </el-form-item>
            </el-form>
          </div>

          <el-table :data="historyList" style="width: 100%" v-loading="historyLoading">
            <el-table-column prop="entityRef" :label="$t('translation.entityRef')" width="180" />
            <el-table-column prop="entityType" :label="$t('translation.entityType')" width="100" />
            <el-table-column prop="langTag" :label="$t('translation.language')" width="100">
              <template #default="scope">
                <el-tag>{{ getLanguageName(scope.row.langTag) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="oldValue" :label="$t('translation.oldValue')" width="150">
              <template #default="scope">
                <span>{{ scope.row.oldValue || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="newValue" :label="$t('translation.newValue')" width="150" />
            <el-table-column prop="action" :label="$t('translation.action')" width="100" />
            <el-table-column prop="proposedBy" :label="$t('translation.proposedBy')" width="100" />
            <el-table-column prop="proposedAt" :label="$t('translation.proposedAt')" width="150">
              <template #default="scope">
                {{ formatDate(scope.row.proposedAt) }}
              </template>
            </el-table-column>
            <el-table-column prop="approvalStatus" :label="$t('translation.status')" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.approvalStatus)">
                  {{ scope.row.approvalStatus }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="confirmedBy" :label="$t('translation.confirmedBy')" width="100" />
            <el-table-column prop="confirmedAt" :label="$t('translation.confirmedAt')" width="150">
              <template #default="scope">
                {{ formatDate(scope.row.confirmedAt) }}
              </template>
            </el-table-column>
            <el-table-column prop="comment" :label="$t('translation.comment')" width="150">
              <template #default="scope">
                <span>{{ scope.row.comment || '-' }}</span>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-if="historyTotal > 0"
            v-model:current-page="historyCurrentPage"
            v-model:page-size="historyPageSize"
            :total="historyTotal"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadHistory"
            @current-change="loadHistory"
            style="margin-top: 20px; text-align: right"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useStore } from 'vuex'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const { t } = useI18n()
const store = useStore()

const activeTab = ref('pending')
const loading = ref(false)
const historyLoading = ref(false)
const pendingTranslations = ref([])
const historyList = ref([])
const total = ref(0)
const historyTotal = ref(0)
const currentPage = ref(1)
const pageSize = ref(20)
const historyCurrentPage = ref(1)
const historyPageSize = ref(20)

const filterForm = ref({
  entityType: '',
  language: ''
})

const historyFilter = ref({
  entityRef: '',
  entityType: '',
  langTag: '',
  status: '',
  username: '',
  startDate: '',
  endDate: ''
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

const getLanguageName = (code) => {
  const lang = availableLanguages.find(l => l.code === code)
  return lang ? lang.name : code
}

const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleString()
}

const getStatusType = (status) => {
  switch (status) {
    case 'APPROVED':
      return 'success'
    case 'REJECTED':
      return 'danger'
    case 'PENDING':
      return 'warning'
    default:
      return 'info'
  }
}

const loadPendingTranslations = async () => {
  loading.value = true
  try {
    const response = await axios.get('http://localhost:8086/api/i18n/pending-translations', {
      params: {
        entityType: filterForm.value.entityType,
        language: filterForm.value.language,
        page: currentPage.value - 1,
        size: pageSize.value
      }
    })
    pendingTranslations.value = response.data.content || []
    total.value = response.data.totalElements || 0
  } catch (error) {
    console.error('Failed to load pending translations:', error)
    ElMessage.error(t('translation.loadError'))
  } finally {
    loading.value = false
  }
}

const loadHistory = async () => {
  historyLoading.value = true
  try {
    const response = await axios.get('http://localhost:8086/api/i18n/history', {
      params: {
        entityRef: historyFilter.value.entityRef,
        entityType: historyFilter.value.entityType,
        langTag: historyFilter.value.langTag,
        status: historyFilter.value.status,
        username: historyFilter.value.username,
        startDate: historyFilter.value.startDate,
        endDate: historyFilter.value.endDate,
        page: historyCurrentPage.value - 1,
        size: historyPageSize.value
      }
    })
    historyList.value = response.data.content || []
    historyTotal.value = response.data.totalElements || 0
  } catch (error) {
    console.error('Failed to load history:', error)
    ElMessage.error(t('translation.loadHistoryError'))
  } finally {
    historyLoading.value = false
  }
}

const confirmTranslation = async (row) => {
  try {
    const username = store.state.user?.username || 'admin'
    await axios.post(
      `http://localhost:8086/api/i18n/confirm-translation/${row.entityType}/${row.entityRef}/${row.language}`,
      {},
      {
        headers: {
          'X-User-Name': username
        }
      }
    )
    ElMessage.success(t('translation.confirmSuccess'))
    loadPendingTranslations()
  } catch (error) {
    console.error('Failed to confirm translation:', error)
    ElMessage.error(t('translation.confirmError'))
  }
}

const rejectTranslation = async (row) => {
  const comment = prompt(t('translation.rejectReason'))
  if (comment === null) return
  
  try {
    const username = store.state.user?.username || 'admin'
    await axios.post(
      `http://localhost:8086/api/i18n/reject-translation/${row.entityType}/${row.entityRef}/${row.language}`,
      { comment },
      {
        headers: {
          'X-User-Name': username
        }
      }
    )
    ElMessage.success(t('translation.rejectSuccess'))
    loadPendingTranslations()
  } catch (error) {
    console.error('Failed to reject translation:', error)
    ElMessage.error(t('translation.rejectError'))
  }
}

const exportHistory = async () => {
  try {
    const response = await axios.get('http://localhost:8086/api/i18n/history/export', {
      params: {
        entityRef: historyFilter.value.entityRef,
        entityType: historyFilter.value.entityType,
        startDate: historyFilter.value.startDate,
        endDate: historyFilter.value.endDate,
        status: historyFilter.value.status,
        username: historyFilter.value.username
      },
      responseType: 'blob'
    })
    
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `translation_history_${new Date().toISOString()}.csv`)
    document.body.appendChild(link)
    link.click()
    link.remove()
    
    ElMessage.success(t('translation.exportSuccess'))
  } catch (error) {
    console.error('Failed to export history:', error)
    ElMessage.error(t('translation.exportError'))
  }
}

const resetFilter = () => {
  filterForm.value = {
    entityType: '',
    language: ''
  }
  currentPage.value = 1
  loadPendingTranslations()
}

const resetHistoryFilter = () => {
  historyFilter.value = {
    entityRef: '',
    entityType: '',
    langTag: '',
    status: '',
    username: '',
    startDate: '',
    endDate: ''
  }
  historyCurrentPage.value = 1
  loadHistory()
}

onMounted(() => {
  loadPendingTranslations()
})
</script>

<style scoped>
.translation-admin {
  padding: 20px;
}

.admin-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-section {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f5f5;
  border-radius: 4px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
</style>
