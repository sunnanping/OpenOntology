<template>
  <div class="project-workspace">
    <!-- 顶部导航栏 -->
    <div class="workspace-header">
      <div class="header-left">
        <span class="project-name">{{ projectName }}</span>
        <el-button type="text" @click="goToHome">
          <el-icon><HomeFilled /></el-icon>
          Home
        </el-button>
      </div>
      
      <!-- 顶部标签栏 -->
      <div class="header-tabs">
        <el-tabs v-model="activeTab" type="border-card" @tab-change="handleTabChange">
          <el-tab-pane label="Classes" name="Classes">
            <template #label>
              <span><el-icon><Folder /></el-icon> Classes</span>
            </template>
          </el-tab-pane>
          <el-tab-pane label="Properties" name="Properties">
            <template #label>
              <span><el-icon><Connection /></el-icon> Properties</span>
            </template>
          </el-tab-pane>
          <el-tab-pane label="Individuals" name="Individuals">
            <template #label>
              <span><el-icon><User /></el-icon> Individuals</span>
            </template>
          </el-tab-pane>
          <el-tab-pane label="Comments" name="Comments">
            <template #label>
              <span><el-icon><ChatDotRound /></el-icon> Comments</span>
            </template>
          </el-tab-pane>
          <el-tab-pane label="Changes" name="Changes">
            <template #label>
              <span><el-icon><DocumentChecked /></el-icon> Changes by Entity</span>
            </template>
          </el-tab-pane>
          <el-tab-pane label="History" name="History">
            <template #label>
              <span><el-icon><Clock /></el-icon> History</span>
            </template>
          </el-tab-pane>
          <el-tab-pane label="OWL Properties" name="OWLProperties">
            <template #label>
              <span><el-icon><Link /></el-icon> OWL Properties</span>
            </template>
          </el-tab-pane>
          <el-tab-pane label="OWL Classes" name="OWLClasses">
            <template #label>
              <span><el-icon><FolderOpened /></el-icon> OWL Classes</span>
            </template>
          </el-tab-pane>
          <el-tab-pane label="Query" name="Query">
            <template #label>
              <span><el-icon><Search /></el-icon> Query</span>
            </template>
          </el-tab-pane>
        </el-tabs>
      </div>
      
      <!-- 右侧功能菜单 -->
      <div class="header-right">
        <el-dropdown trigger="click">
          <el-button type="text">Display <el-icon><ArrowDown /></el-icon></el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>Language</el-dropdown-item>
              <el-dropdown-item>Theme</el-dropdown-item>
              <el-dropdown-item>Layout</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        
        <el-dropdown trigger="click">
          <el-button type="text">Project <el-icon><ArrowDown /></el-icon></el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>Settings</el-dropdown-item>
              <el-dropdown-item>Import</el-dropdown-item>
              <el-dropdown-item>Export</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        
        <el-button type="text">Share</el-button>
        
        <el-dropdown trigger="click">
          <el-button type="text" class="user-menu">
            <el-avatar :size="24" :style="{ backgroundColor: getAvatarColor(currentUser) }">
              {{ getAvatarInitial(currentUser) }}
            </el-avatar>
            {{ currentUser }}
            <el-icon><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>Profile</el-dropdown-item>
              <el-dropdown-item>Settings</el-dropdown-item>
              <el-dropdown-item divided @click="logout">Sign out</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        
        <el-dropdown trigger="click">
          <el-button type="text">Help <el-icon><ArrowDown /></el-icon></el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>Documentation</el-dropdown-item>
              <el-dropdown-item>About</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        
        <el-button type="primary" size="small" @click="addNewTab">
          <el-icon><Plus /></el-icon> Add tab
        </el-button>
      </div>
    </div>
    
    <!-- 主编辑区域 -->
    <div class="workspace-body">
      <!-- 左侧：类层级树 -->
      <div class="left-panel" v-if="showLeftPanel">
        <div class="panel-header">
          <span class="panel-title">Class Hierarchy</span>
          <div class="panel-tools">
            <el-button type="text" size="small" @click="showCreateClassDialog" title="Add Class">
              <el-icon><Plus /></el-icon>
            </el-button>
            <el-button type="text" size="small" @click="deleteSelectedClass" title="Delete Class">
              <el-icon><Delete /></el-icon>
            </el-button>
            <el-button type="text" size="small" @click="showSearchDialog" title="Search">
              <el-icon><Search /></el-icon>
            </el-button>
            <el-button type="text" size="small" @click="showLeftPanel = false">
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
        </div>
        <div class="panel-content">
          <el-tree
            ref="classTree"
            :data="classHierarchy"
            :props="treeProps"
            node-key="id"
            default-expand-all
            highlight-current
            draggable
            @node-click="handleNodeClick"
            @node-contextmenu="handleContextMenu"
          >
            <template #default="{ node, data }">
              <span class="tree-node">
                <el-icon v-if="data.children && data.children.length"><Folder /></el-icon>
                <el-icon v-else><Document /></el-icon>
                <span class="node-label">{{ node.label }}</span>
              </span>
            </template>
          </el-tree>
        </div>
      </div>
      
      <!-- 中间：内容编辑区 -->
      <div class="center-panel">
        <div v-if="!selectedClass" class="empty-state">
          <el-empty description="Nothing selected" />
        </div>
        <div v-else class="class-editor">
          <!-- Class Editor Header -->
          <div class="editor-header">
            <h2>{{ selectedClass.name }}</h2>
            <el-radio-group v-model="activeView" size="small">
              <el-radio-button label="edit">
                <el-icon><Edit /></el-icon> Edit
              </el-radio-button>
              <el-radio-button label="graph">
                <el-icon><Share /></el-icon> Graph
              </el-radio-button>
              <el-radio-button label="history">
                <el-icon><Clock /></el-icon> History
              </el-radio-button>
            </el-radio-group>
          </div>
          
          <!-- Edit View -->
          <div v-if="activeView === 'edit'" class="edit-view">
            <el-form label-position="top">
              <el-form-item label="IRI">
                <el-input v-model="selectedClass.iri" readonly />
              </el-form-item>
              
              <el-form-item label="Annotations">
                <div v-for="(annotation, index) in classAnnotations" :key="index" class="annotation-row">
                  <el-input v-model="annotation.property" placeholder="Property" style="width: 150px" />
                  <el-input v-model="annotation.value" placeholder="Value" style="flex: 1" />
                  <el-input v-model="annotation.lang" placeholder="Lang" style="width: 80px" />
                  <el-button type="danger" size="small" @click="removeAnnotation(index)">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
                <el-button type="primary" size="small" @click="addAnnotation">
                  <el-icon><Plus /></el-icon> Add Annotation
                </el-button>
              </el-form-item>
              
              <el-form-item label="Parents">
                <el-tag
                  v-for="(parent, index) in classParents"
                  :key="index"
                  closable
                  @close="removeParent(index)"
                  class="parent-tag"
                >
                  {{ parent.name }}
                </el-tag>
                <el-button type="primary" size="small" @click="showAddParentDialog">
                  <el-icon><Plus /></el-icon> Add Parent
                </el-button>
              </el-form-item>
            </el-form>
            
            <div class="editor-actions">
              <el-button type="primary" @click="saveClass">Save</el-button>
              <el-button @click="cancelEdit">Cancel</el-button>
            </div>
          </div>
          
          <!-- Graph View -->
          <div v-else-if="activeView === 'graph'" class="graph-view">
            <div class="graph-toolbar">
              <el-button-group>
                <el-button size="small" @click="zoomIn"><el-icon><ZoomIn /></el-icon></el-button>
                <el-button size="small" @click="zoomOut"><el-icon><ZoomOut /></el-icon></el-button>
                <el-button size="small" @click="resetZoom"><el-icon><RefreshLeft /></el-icon></el-button>
              </el-button-group>
              <el-divider direction="vertical" />
              <el-button size="small" @click="downloadGraph('png')">PNG</el-button>
              <el-button size="small" @click="downloadGraph('svg')">SVG</el-button>
            </div>
            <div class="graph-container" ref="graphContainer">
              <!-- Graph visualization will be rendered here -->
              <div class="graph-placeholder">
                <el-icon :size="64"><Share /></el-icon>
                <p>Graph visualization of {{ selectedClass.name }}</p>
              </div>
            </div>
          </div>
          
          <!-- History View -->
          <div v-else-if="activeView === 'history'" class="history-view">
            <el-timeline>
              <el-timeline-item
                v-for="(item, index) in classHistory"
                :key="index"
                :timestamp="formatDate(item.date)"
                :type="item.type"
              >
                <h4>{{ item.action }}</h4>
                <p>Revision: {{ item.revision }} by {{ item.author }}</p>
                <div v-for="(change, cidx) in item.changes" :key="cidx" class="change-item">
                  <el-tag :type="change.type === 'add' ? 'success' : change.type === 'remove' ? 'danger' : 'warning'" size="small">
                    {{ change.type === 'add' ? '+' : change.type === 'remove' ? '-' : '±' }}
                  </el-tag>
                  <span v-html="change.description"></span>
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>
        </div>
      </div>
      
      <!-- 右侧：评论和动态 -->
      <div class="right-panel">
        <!-- Comments Panel -->
        <div class="comments-section" v-if="showCommentsPanel">
          <div class="panel-header">
            <span class="panel-title">
              Comments
              <span v-if="selectedClass">: {{ selectedClass.name }}</span>
            </span>
            <div class="panel-tools">
              <el-button type="text" size="small" @click="focusCommentInput">
                <el-icon><Plus /></el-icon>
              </el-button>
              <el-button type="text" size="small" @click="showCommentsPanel = false">
                <el-icon><Close /></el-icon>
              </el-button>
            </div>
          </div>
          <div class="panel-content">
            <div class="comments-list">
              <div v-for="comment in comments" :key="comment.id" class="comment-item">
                <div class="comment-header">
                  <el-avatar :size="24" :style="{ backgroundColor: getAvatarColor(comment.author) }">
                    {{ getAvatarInitial(comment.author) }}
                  </el-avatar>
                  <span class="comment-author">{{ comment.author }}</span>
                  <span class="comment-time">{{ formatTimeAgo(comment.createdAt) }}</span>
                </div>
                <div class="comment-body">{{ comment.content }}</div>
              </div>
            </div>
            <div class="comment-input" ref="commentInput">
              <el-input
                v-model="newComment"
                type="textarea"
                :rows="2"
                placeholder="Add a comment..."
              />
              <el-button type="primary" size="small" @click="addComment">Post</el-button>
            </div>
          </div>
        </div>
        
        <!-- Project Feed Panel -->
        <div class="feed-section" v-if="showFeedPanel">
          <div class="panel-header">
            <span class="panel-title">Project Feed</span>
            <el-button type="text" size="small" @click="showFeedPanel = false">
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
          <div class="panel-content">
            <div class="feed-list">
              <div v-for="activity in projectActivities" :key="activity.id" class="feed-item">
                <el-avatar :size="32" :style="{ backgroundColor: getAvatarColor(activity.author) }">
                  {{ getAvatarInitial(activity.author) }}
                </el-avatar>
                <div class="feed-content">
                  <div class="feed-action">{{ activity.action }}</div>
                  <div class="feed-time">{{ formatTimeAgo(activity.timestamp) }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Create Class Dialog -->
    <DraggableModal
      v-if="createClassDialogVisible"
      title="Create Classes"
      width="500px"
      @close="createClassDialogVisible = false"
    >
      <div class="form-group">
        <label>Class names (one per line)</label>
        <textarea
          v-model="newClassNames"
          rows="5"
          placeholder="Enter one name per line"
          class="form-control"
        ></textarea>
      </div>
      <div class="form-group">
        <label>Language Tag</label>
        <input type="text" v-model="languageTag" placeholder="Leave blank for no language tag" class="form-control" />
      </div>
      <template #footer>
        <button class="btn btn-secondary" @click="createClassDialogVisible = false">Cancel</button>
        <button class="btn btn-primary" @click="createClasses">Create</button>
      </template>
    </DraggableModal>
    
    <!-- Search Dialog -->
    <DraggableModal
      v-if="searchDialogVisible"
      title="Search for Class"
      width="500px"
      @close="searchDialogVisible = false"
    >
      <div class="form-group">
        <input
          type="text"
          v-model="searchQuery"
          placeholder="Enter text to search for"
          class="form-control"
          @input="performSearch"
        />
      </div>
      <div class="search-results" v-if="searchResults.length > 0">
        <div
          v-for="result in searchResults"
          :key="result.id"
          class="search-result-item"
          @click="selectSearchResult(result)"
        >
          <el-icon><Document /></el-icon>
          <span>{{ result.name }}</span>
        </div>
      </div>
    </DraggableModal>
    
    <!-- Context Menu -->
    <el-dropdown
      v-if="contextMenu.visible"
      :style="{ position: 'fixed', left: contextMenu.x + 'px', top: contextMenu.y + 'px', zIndex: 9999 }"
      trigger="click"
      @visible-change="(visible) => { if (!visible) contextMenu.visible = false }"
    >
      <span></span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="addSubclass">Add Subclass</el-dropdown-item>
          <el-dropdown-item @click="renameClass">Rename</el-dropdown-item>
          <el-dropdown-item divided @click="deleteClass">Delete</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '@/utils/http'
import DraggableModal from '@/components/DraggableModal.vue'
import {
  HomeFilled, Folder, FolderOpened, Connection, User, ChatDotRound,
  DocumentChecked, Clock, Link, Search, ArrowDown, Plus, Delete,
  Close, Edit, Share, ZoomIn, ZoomOut, RefreshLeft, Document
} from '@element-plus/icons-vue'

export default {
  name: 'ProjectWorkspace',
  components: {
    DraggableModal,
    HomeFilled, Folder, FolderOpened, Connection, User, ChatDotRound,
    DocumentChecked, Clock, Link, Search, ArrowDown, Plus, Delete,
    Close, Edit, Share, ZoomIn, ZoomOut, RefreshLeft, Document
  },
  props: {
    projectId: {
      type: String,
      default: ''
    },
    viewType: {
      type: String,
      default: 'Classes'
    }
  },
  setup(props) {
    const route = useRoute()
    const router = useRouter()
    
    // 从query参数获取项目ID
    const projectId = computed(() => props.projectId || route.query.p)
    const viewType = computed(() => props.viewType || route.query.v || 'Classes')
    
    const projectName = ref('testProject')
    const currentUser = ref('bjsun07')
    const activeTab = ref(viewType.value)
    const activeView = ref('edit')
    
    // 面板显示状态
    const showLeftPanel = ref(true)
    const showCommentsPanel = ref(true)
    const showFeedPanel = ref(true)
    
    // 类层级树
    const classHierarchy = ref([])
    const treeProps = {
      label: 'name',
      children: 'children'
    }
    const selectedClass = ref(null)
    const classTree = ref(null)
    
    // 类编辑数据
    const classAnnotations = ref([])
    const classParents = ref([])
    const classHistory = ref([])
    
    // 评论
    const comments = ref([])
    const newComment = ref('')
    const commentInput = ref(null)
    
    // 项目动态
    const projectActivities = ref([])
    
    // 对话框
    const createClassDialogVisible = ref(false)
    const newClassNames = ref('')
    const languageTag = ref('')
    
    const searchDialogVisible = ref(false)
    const searchQuery = ref('')
    const searchResults = ref([])
    
    // 右键菜单
    const contextMenu = ref({
      visible: false,
      x: 0,
      y: 0,
      node: null
    })
    
    // 获取项目详情
    const loadProjectDetails = async () => {
      try {
        const response = await http.get(`/projects/available`, {
          params: { username: currentUser.value }
        })
        const projects = response.data || []
        const project = projects.find(p => p.id === projectId.value)
        if (project) {
          projectName.value = project.name
        }
      } catch (error) {
        console.error('Failed to load project details:', error)
      }
    }
    
    // 获取类层级树
    const loadClassHierarchy = async () => {
      try {
        const response = await http.get(`/classes/hierarchy/${projectId.value}`)
        classHierarchy.value = response.data || []
      } catch (error) {
        console.error('Failed to load class hierarchy:', error)
        ElMessage.error('Failed to load class hierarchy')
      }
    }
    
    // 获取评论
    const loadComments = async (entityId) => {
      try {
        const response = await http.get(`/classes/comments/${entityId}`)
        comments.value = response.data || []
      } catch (error) {
        console.error('Failed to load comments:', error)
      }
    }
    
    // 获取项目动态
    const loadProjectActivities = async () => {
      try {
        const response = await http.get(`/classes/activities/${projectId.value}`)
        projectActivities.value = response.data || []
      } catch (error) {
        console.error('Failed to load project activities:', error)
      }
    }
    
    // 树节点点击
    const handleNodeClick = (data) => {
      selectedClass.value = data
      loadComments(data.id)
      loadClassHistory(data)
    }
    
    // 加载类历史
    const loadClassHistory = (cls) => {
      classHistory.value = [
        {
          date: new Date(),
          action: `Moved class "${cls.name}"`,
          revision: 142,
          author: currentUser.value,
          type: 'primary',
          changes: [
            { type: 'remove', description: `${cls.name} <strong>SubClassOf</strong> 应用架构` },
            { type: 'add', description: `${cls.name} <strong>SubClassOf</strong> 核心系统` }
          ]
        },
        {
          date: new Date(Date.now() - 86400000 * 45),
          action: `Created class "${cls.name}"`,
          revision: 91,
          author: currentUser.value,
          type: 'success',
          changes: [
            { type: 'add', description: `<strong>Class:</strong> ${cls.name}` },
            { type: 'add', description: `${cls.name} <strong>SubClassOf</strong> 应用架构` }
          ]
        }
      ]
    }
    
    // 右键菜单
    const handleContextMenu = (event, data) => {
      contextMenu.value = {
        visible: true,
        x: event.clientX,
        y: event.clientY,
        node: data
      }
    }
    
    // 工具函数
    const getAvatarColor = (username) => {
      const colors = ['#4CAF50', '#2196F3', '#FF9800', '#9C27B0', '#F44336', '#607D8B']
      let hash = 0
      for (let i = 0; i < (username || '').length; i++) {
        hash = username.charCodeAt(i) + ((hash << 5) - hash)
      }
      return colors[Math.abs(hash) % colors.length]
    }
    
    const getAvatarInitial = (username) => {
      return (username || '?').charAt(0).toUpperCase()
    }
    
    const formatTimeAgo = (date) => {
      if (!date) return ''
      const now = new Date()
      const diff = now - new Date(date)
      const hours = Math.floor(diff / (1000 * 60 * 60))
      if (hours < 1) return 'Just now'
      if (hours < 24) return `${hours} hours ago`
      const days = Math.floor(hours / 24)
      return `${days} days ago`
    }
    
    const formatDate = (date) => {
      if (!date) return ''
      return new Date(date).toLocaleDateString('en-US', {
        weekday: 'short',
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      })
    }
    
    // 导航
    const goToHome = () => {
      router.push('/projects/list')
    }
    
    const logout = () => {
      localStorage.removeItem('user')
      router.push('/login')
    }
    
    // 标签切换
    const handleTabChange = (tab) => {
      router.replace({
        query: { ...route.query, v: tab }
      })
    }
    
    const addNewTab = () => {
      ElMessage.info('Add tab functionality coming soon')
    }
    
    // 类操作
    const showCreateClassDialog = () => {
      createClassDialogVisible.value = true
    }
    
    const createClasses = async () => {
      try {
        const names = newClassNames.value.split('\n').filter(n => n.trim())
        for (const name of names) {
          await http.post('/classes/create', {
            name: name.trim(),
            iri: `http://example.org/${name.trim()}`,
            description: '',
            parentId: selectedClass.value?.id
          })
        }
        ElMessage.success('Classes created successfully')
        createClassDialogVisible.value = false
        newClassNames.value = ''
        loadClassHierarchy()
      } catch (error) {
        console.error('Failed to create classes:', error)
        ElMessage.error('Failed to create classes')
      }
    }
    
    const deleteSelectedClass = async () => {
      if (!selectedClass.value) {
        ElMessage.warning('Please select a class to delete')
        return
      }
      try {
        await ElMessageBox.confirm(
          `Are you sure you want to delete "${selectedClass.value.name}"?`,
          'Confirm Delete',
          { type: 'warning' }
        )
        await http.delete(`/classes/delete/${selectedClass.value.id}`)
        ElMessage.success('Class deleted successfully')
        selectedClass.value = null
        loadClassHierarchy()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('Failed to delete class:', error)
          ElMessage.error('Failed to delete class')
        }
      }
    }
    
    // 搜索
    const showSearchDialog = () => {
      searchDialogVisible.value = true
    }
    
    const performSearch = async () => {
      if (!searchQuery.value.trim()) {
        searchResults.value = []
        return
      }
      try {
        const response = await http.get('/classes/search', {
          params: { query: searchQuery.value, projectId: projectId.value }
        })
        searchResults.value = response.data || []
      } catch (error) {
        console.error('Search failed:', error)
      }
    }
    
    const selectSearchResult = (result) => {
      selectedClass.value = result
      searchDialogVisible.value = false
      loadComments(result.id)
    }
    
    // 注释操作
    const addAnnotation = () => {
      classAnnotations.value.push({ property: '', value: '', lang: '' })
    }
    
    const removeAnnotation = (index) => {
      classAnnotations.value.splice(index, 1)
    }
    
    // 父类操作
    const removeParent = (index) => {
      classParents.value.splice(index, 1)
    }
    
    const showAddParentDialog = () => {
      ElMessage.info('Add parent dialog coming soon')
    }
    
    // 评论
    const focusCommentInput = () => {
      commentInput.value?.querySelector('textarea')?.focus()
    }
    
    const addComment = async () => {
      if (!newComment.value.trim() || !selectedClass.value) return
      try {
        const response = await http.post('/classes/comments', {
          entityId: selectedClass.value.id,
          entityType: 'Class',
          author: currentUser.value,
          content: newComment.value
        })
        comments.value.push(response.data)
        newComment.value = ''
      } catch (error) {
        console.error('Failed to add comment:', error)
        ElMessage.error('Failed to add comment')
      }
    }
    
    // 保存/取消
    const saveClass = async () => {
      try {
        await http.put(`/classes/update/${selectedClass.value.id}`, selectedClass.value)
        ElMessage.success('Class saved successfully')
      } catch (error) {
        console.error('Failed to save class:', error)
        ElMessage.error('Failed to save class')
      }
    }
    
    const cancelEdit = () => {
      selectedClass.value = null
    }
    
    // Graph视图
    const zoomIn = () => {}
    const zoomOut = () => {}
    const resetZoom = () => {}
    const downloadGraph = (format) => {
      ElMessage.info(`Download ${format} coming soon`)
    }
    
    // 上下文菜单操作
    const addSubclass = () => {
      selectedClass.value = contextMenu.value.node
      showCreateClassDialog()
    }
    
    const renameClass = () => {
      ElMessageBox.prompt('Enter new name:', 'Rename', {
        inputValue: contextMenu.value.node.name
      }).then(({ value }) => {
        contextMenu.value.node.name = value
      }).catch(() => {})
    }
    
    const deleteClass = () => {
      selectedClass.value = contextMenu.value.node
      deleteSelectedClass()
    }
    
    onMounted(() => {
      if (!projectId.value) {
        ElMessage.error('Project ID is required')
        router.push('/projects/list')
        return
      }
      loadProjectDetails()
      loadClassHierarchy()
      loadProjectActivities()
    })
    
    onUnmounted(() => {
      // Cleanup
    })
    
    return {
      projectName,
      currentUser,
      activeTab,
      activeView,
      showLeftPanel,
      showCommentsPanel,
      showFeedPanel,
      classHierarchy,
      treeProps,
      selectedClass,
      classTree,
      classAnnotations,
      classParents,
      classHistory,
      comments,
      newComment,
      commentInput,
      projectActivities,
      createClassDialogVisible,
      newClassNames,
      languageTag,
      searchDialogVisible,
      searchQuery,
      searchResults,
      contextMenu,
      getAvatarColor,
      getAvatarInitial,
      formatTimeAgo,
      formatDate,
      goToHome,
      logout,
      handleTabChange,
      addNewTab,
      handleNodeClick,
      handleContextMenu,
      showCreateClassDialog,
      createClasses,
      deleteSelectedClass,
      showSearchDialog,
      performSearch,
      selectSearchResult,
      addAnnotation,
      removeAnnotation,
      removeParent,
      showAddParentDialog,
      focusCommentInput,
      addComment,
      saveClass,
      cancelEdit,
      zoomIn,
      zoomOut,
      resetZoom,
      downloadGraph,
      addSubclass,
      renameClass,
      deleteClass
    }
  }
}
</script>

<style scoped>
.project-workspace {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f5;
}

.workspace-header {
  display: flex;
  align-items: center;
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  padding: 0 16px;
  height: 50px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 200px;
}

.project-name {
  font-weight: bold;
  font-size: 16px;
}

.header-tabs {
  flex: 1;
  margin: 0 16px;
}

.header-tabs :deep(.el-tabs__header) {
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 4px;
}

.workspace-body {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.left-panel {
  width: 280px;
  background-color: #fff;
  border-right: 1px solid #dcdfe6;
  display: flex;
  flex-direction: column;
}

.center-panel {
  flex: 1;
  background-color: #fff;
  margin: 0 1px;
  display: flex;
  flex-direction: column;
  overflow: auto;
}

.right-panel {
  width: 300px;
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background-color: #f5f5f5;
  border-bottom: 1px solid #dcdfe6;
}

.panel-title {
  font-weight: bold;
  font-size: 14px;
}

.panel-tools {
  display: flex;
  gap: 4px;
}

.panel-content {
  flex: 1;
  overflow: auto;
  padding: 8px;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 4px;
}

.node-label {
  margin-left: 4px;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.class-editor {
  padding: 16px;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.editor-header h2 {
  margin: 0;
}

.annotation-row {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
  align-items: center;
}

.parent-tag {
  margin-right: 8px;
  margin-bottom: 8px;
}

.editor-actions {
  margin-top: 24px;
  display: flex;
  gap: 8px;
}

.graph-view {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.graph-toolbar {
  padding: 8px;
  border-bottom: 1px solid #dcdfe6;
}

.graph-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.graph-placeholder {
  text-align: center;
  color: #909399;
}

.history-view {
  padding: 16px;
}

.change-item {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 4px 0;
}

.comments-section,
.feed-section {
  background-color: #fff;
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.comments-list {
  flex: 1;
  overflow: auto;
}

.comment-item {
  padding: 12px;
  border-bottom: 1px solid #ebeef5;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.comment-author {
  font-weight: bold;
}

.comment-time {
  color: #909399;
  font-size: 12px;
}

.comment-body {
  margin-left: 32px;
}

.comment-input {
  padding: 12px;
  border-top: 1px solid #ebeef5;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.feed-list {
  flex: 1;
  overflow: auto;
}

.feed-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-bottom: 1px solid #ebeef5;
}

.feed-content {
  flex: 1;
}

.feed-action {
  font-size: 14px;
}

.feed-time {
  color: #909399;
  font-size: 12px;
}

.search-results {
  margin-top: 16px;
  max-height: 300px;
  overflow: auto;
}

.search-result-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  cursor: pointer;
  border-radius: 4px;
}

.search-result-item:hover {
  background-color: #f5f7fa;
}

/* Form styles for DraggableModal */
.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.form-control {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
  transition: border-color 0.2s;
}

.form-control:focus {
  outline: none;
  border-color: #409eff;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-primary {
  background-color: #409eff;
  color: white;
}

.btn-primary:hover {
  background-color: #66b1ff;
}

.btn-secondary {
  background-color: #909399;
  color: white;
}

.btn-secondary:hover {
  background-color: #a6a9ad;
}
</style>
