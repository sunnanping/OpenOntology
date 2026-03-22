<template>
  <div class="load-operate-page">
    <!-- 第一层：系统菜单 -->
    <SystemMenuBar
      :project-name="projectInfo?.name || ''"
      :current-user="currentUser"
      :project-info="projectInfo"
      @go-home="handleGoHome"
      @logout="handleLogout"
      @settings="handleSettings"
      @share="handleShare"
    />
    
    <!-- 第二层：实体类型标签栏 -->
    <EntityTabsBar
      :tabs="availableTabs"
      :active-tab="activeTab"
      @tab-change="handleTabChange"
      @add-tab="handleAddTab"
    />
    
    <!-- 第三层：动态加载的模块组件 -->
    <component
      :is="currentModuleComponent"
      :project-id="projectId"
      :project-info="projectInfo"
      :project-data-record="projectDataRecord"
      :user-info="currentUser"
      @class-selected="handleClassSelected"
      @class-created="handleClassCreated"
      @class-deleted="handleClassDeleted"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, defineAsyncComponent, shallowRef } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import http from '@/utils/http'
import SystemMenuBar from '@/components/layout/SystemMenuBar.vue'
import EntityTabsBar from '@/components/layout/EntityTabsBar.vue'

const route = useRoute()
const router = useRouter()

// Props from route
const projectId = ref(route.params.projectId || route.query.projectId || '1')

// 状态
const currentUser = ref({})
const projectInfo = ref({})
const projectDataRecord = ref(null)
const activeTab = ref('classes')
const currentModule = ref('ClassEditorPanel')
const isInitialized = ref(false)

// 可用的标签（目前静态加载，后期从后端获取）
const availableTabs = ref([
  { id: 'classes', label: 'Classes', shortLabel: 'Cls', icon: 'bi bi-diagram-3', module: 'ClassEditorPanel' },
  { id: 'properties', label: 'Properties', shortLabel: 'Prop', icon: 'bi bi-link-45deg', module: 'PropertyEditorPanel' },
  { id: 'individuals', label: 'Individuals', shortLabel: 'Ind', icon: 'bi bi-people', module: 'IndividualEditorPanel' },
  { id: 'comments', label: 'Comments', shortLabel: 'Cmt', icon: 'bi bi-chat-left-text', module: 'CommentManagerPanel' },
  { id: 'changes', label: 'Changes by Entity', shortLabel: 'Chg', icon: 'bi bi-clock-history', module: 'ChangesViewerPanel' },
  { id: 'history', label: 'History', shortLabel: 'His', icon: 'bi bi-journal-text', module: 'HistoryViewerPanel' }
])

// 模块组件映射（目前静态加载，后期可改为动态加载）
const moduleComponents = {
  'ClassEditorPanel': defineAsyncComponent(() => import('@/components/modules/ClassEditorPanel.vue')),
  'PropertyEditorPanel': defineAsyncComponent(() => import('@/components/modules/PropertyEditorPanel.vue')),
  'IndividualEditorPanel': defineAsyncComponent(() => import('@/components/modules/IndividualEditorPanel.vue')),
  'CommentManagerPanel': defineAsyncComponent(() => import('@/components/modules/CommentManagerPanel.vue')),
  'ChangesViewerPanel': defineAsyncComponent(() => import('@/components/modules/ChangesViewerPanel.vue')),
  'HistoryViewerPanel': defineAsyncComponent(() => import('@/components/modules/HistoryViewerPanel.vue'))
}

// 当前模块组件
const currentModuleComponent = shallowRef(null)

// 计算当前模块组件
const loadModule = (moduleName) => {
  if (moduleComponents[moduleName]) {
    currentModuleComponent.value = moduleComponents[moduleName]
  } else {
    // 默认加载 ClassEditorPanel
    currentModuleComponent.value = moduleComponents['ClassEditorPanel']
  }
}

// 初始化函数
const initializePage = async () => {
  if (isInitialized.value) {
    console.log('Page already initialized, skipping...')
    return
  }
  
  console.log('Initializing page, projectId:', projectId.value)
  
  // 获取用户信息
  const userStr = localStorage.getItem('user')
  if (userStr) {
    currentUser.value = JSON.parse(userStr)
  }
  
  // 获取项目信息（优先从路由 state，其次 sessionStorage，最后 API）
  await loadProjectInfo()
  
  // 获取可用模块（目前静态，后期从后端获取）
  // await loadAvailableModules()
  
  // 加载默认模块
  const defaultTab = route.query.module || 'classes'
  const defaultTabInfo = availableTabs.value.find(t => t.id === defaultTab)
  if (defaultTabInfo) {
    activeTab.value = defaultTab
    currentModule.value = defaultTabInfo.module
  }
  
  loadModule(currentModule.value)
  
  isInitialized.value = true
}

// 初始化
onMounted(async () => {
  console.log('=== onMounted called ===')
  console.log('Current projectId:', projectId.value)
  console.log('Route params:', route.params)
  console.log('window.history.state:', window.history.state)
  await initializePage()
})

// 监听路由变化（处理 projectId 变化的情况）
watch(() => route.params.projectId, (newProjectId, oldProjectId) => {
  console.log('=== watch triggered ===')
  console.log('Old projectId:', oldProjectId)
  console.log('New projectId:', newProjectId)
  console.log('Current projectId.value:', projectId.value)
  if (newProjectId && newProjectId !== projectId.value) {
    console.log('ProjectId changed, reinitializing...')
    projectId.value = newProjectId
    isInitialized.value = false
    initializePage()
  }
}, { immediate: false })
/**
 * 实现数据获取策略：
- 优先从 sessionStorage 获取（正常跳转和新窗口打开场景）
- 最后从 API 查询（后备方案，处理页面刷新等场景）
 */
// 加载项目信息
const loadProjectInfo = async () => {
  // 方案 1: 从 sessionStorage 获取
  const cachedProject = sessionStorage.getItem(`project_${projectId.value}`)
  const cachedUser = sessionStorage.getItem('currentUser')
  if (cachedProject) {
    const projectData = JSON.parse(cachedProject)
    projectDataRecord.value = projectData
    projectInfo.value = projectData
    if (cachedUser) {
      currentUser.value = JSON.parse(cachedUser)
    }
    console.log('Loaded project info from sessionStorage:', projectInfo.value)
    // 清理 sessionStorage
    sessionStorage.removeItem(`project_${projectId.value}`)
    return
  }
  
  // 方案 2: 从 API 查询（后备方案，处理页面刷新等场景）
  try {
    const response = await http.get(`/ontology/findById/${projectId.value}`)
    const projectData = response.data || {}
    projectDataRecord.value = projectData
    projectInfo.value = projectData
    console.log('Loaded project info from API:', projectInfo.value)
  } catch (error) {
    console.error('Failed to load project info:', error)
    projectDataRecord.value = { name: 'error' }
    projectInfo.value = { name: 'error' }
  }
}

// 标签切换处理
const handleTabChange = (tab) => {
  activeTab.value = tab.id
  currentModule.value = tab.module
  loadModule(tab.module)
  
  // 更新URL参数（不刷新页面）
  router.replace({
    query: { ...route.query, module: tab.id }
  })
}

// 添加标签处理
const handleAddTab = () => {
  console.log('Add tab clicked')
  // TODO: 实现添加标签功能
}

// 返回首页
const handleGoHome = () => {
  router.push('/projects/list')
}

// 登出
const handleLogout = () => {
  localStorage.removeItem('user')
  localStorage.removeItem('token')
  router.push('/login')
}

// 设置
const handleSettings = () => {
  console.log('Settings clicked')
  // TODO: 打开设置面板
}

// 分享
const handleShare = () => {
  console.log('Share clicked')
  // TODO: 打开分享面板
}

// 类选中事件
const handleClassSelected = (classInfo) => {
  console.log('Class selected:', classInfo)
}

// 类创建事件
const handleClassCreated = (classInfo) => {
  console.log('Class created:', classInfo)
}

// 类删除事件
const handleClassDeleted = (classInfo) => {
  console.log('Class deleted:', classInfo)
}
</script>

<style scoped>
.load-operate-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
</style>
