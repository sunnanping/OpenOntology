<template>
  <div class="class-editor">
    <!-- 第一层：顶层系统菜单 - 参考WebProtege设计 -->
    <nav class="top-menu-bar navbar navbar-expand navbar-dark">
      <div class="container-fluid menu-container">
        <!-- 左侧：项目Logo和名称 -->
        <div class="menu-left navbar-brand-wrapper">
          <a class="menu-brand navbar-brand" href="#">
            <i class="bi bi-grid-3x3-gap"></i>
            <span class="project-name d-none d-sm-inline">{{ projectName || 'error' }}</span>
            <span class="project-name d-inline d-sm-none">{{ projectName ? projectName.substring(0, 10) : 'error' }}</span>
          </a>
          <a class="menu-link nav-link" href="#" @click.prevent="goHome">
            <i class="bi bi-house-door"></i> 
            <span class="d-none d-md-inline">Home</span>
          </a>
        </div>
        
        <!-- 中间：系统菜单 - 使用Bootstrap响应式类 -->
        <div class="menu-center navbar-nav-wrapper d-none d-lg-flex">
          <ul class="navbar-nav">
            <li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                <i class="bi bi-display d-inline d-xl-none"></i>
                <span class="d-none d-xl-inline">Display</span>
              </a>
              <ul class="dropdown-menu">
                <li><a class="dropdown-item" href="#" @click.prevent><i class="bi bi-palette me-2"></i>Theme</a></li>
                <li><a class="dropdown-item" href="#" @click.prevent><i class="bi bi-globe me-2"></i>Language</a></li>
              </ul>
            </li>
            <li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                <i class="bi bi-folder d-inline d-xl-none"></i>
                <span class="d-none d-xl-inline">Project</span>
              </a>
              <ul class="dropdown-menu">
                <li><a class="dropdown-item" href="#" @click.prevent><i class="bi bi-gear me-2"></i>Settings</a></li>
                <li><a class="dropdown-item" href="#" @click.prevent><i class="bi bi-share me-2"></i>Share</a></li>
              </ul>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="#" @click.prevent>
                <i class="bi bi-share d-inline d-xl-none"></i>
                <span class="d-none d-xl-inline">Share</span>
              </a>
            </li>
          </ul>
        </div>
        
        <!-- 右侧：用户信息和帮助 -->
        <div class="menu-right">
          <!-- 移动端菜单切换按钮 -->
          <button class="navbar-toggler d-lg-none mobile-menu-btn" type="button" @click="toggleMobileMenu">
            <i class="bi bi-list"></i>
          </button>
          
          <div class="menu-item dropdown d-none d-md-block">
            <a class="menu-link dropdown-toggle" href="#" data-bs-toggle="dropdown">
              <i class="bi bi-person-circle"></i> 
              <span class="d-none d-xl-inline">{{ currentUser }}</span>
            </a>
            <ul class="dropdown-menu dropdown-menu-end">
              <li><a class="dropdown-item" href="#" @click.prevent><i class="bi bi-person me-2"></i>Profile</a></li>
              <li><hr class="dropdown-divider"></li>
              <li><a class="dropdown-item" href="#" @click.prevent="logout"><i class="bi bi-box-arrow-right me-2"></i>Logout</a></li>
            </ul>
          </div>
          <div class="menu-item dropdown d-none d-sm-block">
            <a class="menu-link" href="#" data-bs-toggle="dropdown">
              <i class="bi bi-question-circle"></i>
              <span class="d-none d-xl-inline">Help</span>
            </a>
            <ul class="dropdown-menu dropdown-menu-end">
              <li><a class="dropdown-item" href="#" @click.prevent><i class="bi bi-book me-2"></i>Documentation</a></li>
              <li><a class="dropdown-item" href="#" @click.prevent><i class="bi bi-info-circle me-2"></i>About</a></li>
            </ul>
          </div>
        </div>
      </div>
    </nav>
    
    <!-- 移动端菜单遮罩 -->
    <div v-if="showMobileMenu" class="mobile-menu-overlay" @click="toggleMobileMenu">
      <div class="mobile-menu-content" @click.stop>
        <div class="mobile-menu-header">
          <span class="mobile-menu-title">Menu</span>
          <button class="mobile-menu-close" @click="toggleMobileMenu">
            <i class="bi bi-x-lg"></i>
          </button>
        </div>
        <div class="mobile-menu-items">
          <a href="#" @click.prevent="goHome" class="mobile-menu-item">
            <i class="bi bi-house-door"></i> Home
          </a>
          <a href="#" @click.prevent class="mobile-menu-item">
            <i class="bi bi-display"></i> Display
          </a>
          <a href="#" @click.prevent class="mobile-menu-item">
            <i class="bi bi-folder"></i> Project
          </a>
          <a href="#" @click.prevent class="mobile-menu-item">
            <i class="bi bi-person-circle"></i> {{ currentUser }}
          </a>
          <a href="#" @click.prevent="logout" class="mobile-menu-item">
            <i class="bi bi-box-arrow-right"></i> Logout
          </a>
        </div>
      </div>
    </div>

    <!-- 第二层：实体类型标签栏 -->
    <div class="entity-tabs-bar">
      <div class="tabs-container">
        <!-- 左侧：实体类型标签 - 使用ElementUI的滚动容器 -->
        <el-scrollbar class="tabs-scrollbar" wrap-class="tabs-wrap">
          <div class="tabs-left">
            <div 
              v-for="tab in entityTabs" 
              :key="tab.id"
              class="entity-tab"
              :class="{ 'active': activeTab === tab.id }"
              @click="handleTabChange(tab)"
            >
              <i :class="tab.icon"></i>
              <span class="tab-label d-none d-md-inline">{{ tab.label }}</span>
              <span class="tab-label-short d-inline d-md-none">{{ tab.shortLabel || tab.label.substring(0, 3) }}</span>
            </div>
          </div>
        </el-scrollbar>
        
        <!-- 右侧：Add Tab按钮 -->
        <button class="btn-add-tab" @click="handleAddTab">
          <i class="bi bi-plus-lg"></i>
          <span class="btn-label d-none d-sm-inline">Add tab</span>
        </button>
      </div>
    </div>

    <!-- 移动端面板切换按钮 -->
    <div class="mobile-panel-tabs d-lg-none">
      <button 
        v-for="panel in availablePanels" 
        :key="panel.id"
        class="mobile-panel-tab"
        :class="{ 'active': activeMobilePanel === panel.id }"
        @click="activeMobilePanel = panel.id"
      >
        <i :class="panel.icon"></i>
        <span>{{ panel.shortLabel }}</span>
      </button>
    </div>

    <!-- 第三层：内容区域 -->
    <div class="content-area">
      <div class="panels-wrapper" :class="{ 'mobile-view': isMobileView }">
        <!-- 左侧：Class Hierarchy 面板 -->
        <div 
          v-show="showClassHierarchy && (isMobileView ? activeMobilePanel === 'hierarchy' : true)" 
          class="panel class-hierarchy-panel"
          :class="{ 'mobile-active': isMobileView && activeMobilePanel === 'hierarchy' }"
          :style="isMobileView ? {} : { width: leftPanelWidth + 'px' }"
        >
          <div class="panel-header">
            <div class="panel-header-left">
              <span class="panel-title">Class Hierarchy</span>
            </div>
            <div class="panel-header-right">
              <div class="panel-actions">
                <button class="panel-btn" title="Create class" @click="showCreateClassModal = true">
                  <svg class="wp-icon" viewBox="0 0 24 24" width="14" height="14">
                    <circle cx="12" cy="12" r="9" fill="none" stroke="currentColor" stroke-width="1.5"/>
                    <line x1="12" y1="8" x2="12" y2="16" stroke="currentColor" stroke-width="1.5"/>
                    <line x1="8" y1="12" x2="16" y2="12" stroke="currentColor" stroke-width="1.5"/>
                  </svg>
                </button>
                <button 
                  class="panel-btn" 
                  title="Delete selected class" 
                  @click="deleteSelectedClass"
                  :disabled="!selectedClass || selectedClass.id === 'owl:Thing'"
                >
                  <svg class="wp-icon" viewBox="0 0 24 24" width="14" height="14">
                    <circle cx="12" cy="12" r="9" fill="none" stroke="currentColor" stroke-width="1.5"/>
                    <line x1="8" y1="12" x2="16" y2="12" stroke="currentColor" stroke-width="1.5"/>
                  </svg>
                </button>
                <button class="panel-btn" title="Search class" @click="showSearchModal = true">
                  <svg class="wp-icon" viewBox="0 0 24 24" width="14" height="14">
                    <circle cx="11" cy="11" r="7" fill="none" stroke="currentColor" stroke-width="1.5"/>
                    <line x1="16" y1="16" x2="20" y2="20" stroke="currentColor" stroke-width="1.5"/>
                  </svg>
                </button>
              </div>
              <button class="panel-close" @click="showClassHierarchy = false" title="Close panel">
                <i class="bi bi-x"></i>
              </button>
            </div>
          </div>
          <div class="panel-body">
            <div class="search-box">
              <input 
                v-model="searchQuery" 
                type="text" 
                class="form-control form-control-sm" 
                placeholder="Search classes..."
                @input="filterClasses"
              >
              <button class="btn btn-sm btn-outline-secondary" @click="showFilterModal = true">
                <i class="bi bi-funnel"></i>
              </button>
            </div>
            <el-tree
              ref="classTree"
              :data="filteredClassHierarchy"
              :props="treeProps"
              node-key="id"
              :default-expanded-keys="['owl:Thing']"
              highlight-current
              @node-click="handleClassSelect"
              class="class-tree"
            >
              <template #default="{ node, data }">
                <span class="tree-node" :class="{ 'selected': selectedClass?.id === data.id }">
                  <i :class="data.icon || 'bi bi-circle'" class="node-icon"></i>
                  <span class="node-label">{{ node.label }}</span>
                  <span v-if="data.children && data.children.length" class="child-count">
                    ({{ data.children.length }})
                  </span>
                </span>
              </template>
            </el-tree>
          </div>
        </div>

        <!-- 水平调整条 -->
        <div 
          v-if="!isMobileView && showClassHierarchy && showClassDetails" 
          class="resize-handle-h"
          @mousedown="startLeftResize"
        ></div>

        <!-- 中间：Class 详情面板 -->
        <div 
          v-show="showClassDetails && (isMobileView ? activeMobilePanel === 'details' : true)" 
          class="panel class-details-panel"
          :class="{ 'mobile-active': isMobileView && activeMobilePanel === 'details' }"
          :style="isMobileView ? {} : { width: middlePanelWidth + 'px' }"
        >
          <div class="panel-header">
            <div class="panel-header-left">
              <span class="panel-title">{{ selectedClass?.label || 'Class' }}</span>
            </div>
            <div class="panel-header-right">
              <div class="panel-actions">
                <button 
                  class="panel-btn" 
                  :class="{ 'active': activeDetailTab === 'details' }"
                  title="Details"
                  @click="activeDetailTab = 'details'"
                >
                  <i class="bi bi-file-earmark-text"></i>
                </button>
                <button 
                  class="panel-btn" 
                  :class="{ 'active': activeDetailTab === 'graph' }"
                  title="Entity Graph"
                  @click="activeDetailTab = 'graph'"
                >
                  <i class="bi bi-diagram-3"></i>
                </button>
                <button 
                  class="panel-btn" 
                  :class="{ 'active': activeDetailTab === 'changes' }"
                  title="Changes"
                  @click="activeDetailTab = 'changes'"
                >
                  <i class="bi bi-clock-history"></i>
                </button>
              </div>
              <button class="panel-close" @click="showClassDetails = false" title="Close panel">
                <i class="bi bi-x"></i>
              </button>
            </div>
          </div>
          <div class="panel-body">
            <!-- Details Tab -->
            <div v-if="activeDetailTab === 'details'" class="tab-content">
              <div class="class-info" v-if="selectedClass">
                <div class="info-section">
                  <h6 class="section-title">Basic Information</h6>
                  <div class="info-item">
                    <label>ID:</label>
                    <span>{{ selectedClass.id }}</span>
                  </div>
                  <div class="info-item">
                    <label>Label:</label>
                    <span>{{ selectedClass.label }}</span>
                  </div>
                  <div class="info-item" v-if="selectedClass.comment">
                    <label>Comment:</label>
                    <p class="comment-text">{{ selectedClass.comment }}</p>
                  </div>
                </div>
                
                <div class="info-section" v-if="selectedClass.parents?.length">
                  <h6 class="section-title">Super Classes</h6>
                  <ul class="parent-list">
                    <li v-for="parent in selectedClass.parents" :key="parent.id">
                      <i class="bi bi-arrow-up-circle"></i>
                      {{ parent.label }}
                    </li>
                  </ul>
                </div>
                
                <div class="info-section" v-if="selectedClass.children?.length">
                  <h6 class="section-title">Sub Classes</h6>
                  <ul class="children-list">
                    <li v-for="child in selectedClass.children" :key="child.id">
                      <i class="bi bi-arrow-down-circle"></i>
                      {{ child.label }}
                    </li>
                  </ul>
                </div>
                
                <div class="info-section">
                  <h6 class="section-title">Properties</h6>
                  <div class="properties-list">
                    <div v-for="prop in classProperties" :key="prop.id" class="property-item">
                      <div class="property-header">
                        <i :class="prop.type === 'data' ? 'bi bi-hash' : 'bi bi-link'"></i>
                        <span class="property-name">{{ prop.label }}</span>
                        <span class="property-type">({{ prop.type }})</span>
                      </div>
                      <div class="property-values" v-if="prop.values?.length">
                        <span v-for="(value, idx) in prop.values" :key="idx" class="value-tag">
                          {{ value }}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div v-else class="empty-state">
                <i class="bi bi-mouse"></i>
                <p>Select a class to view details</p>
              </div>
            </div>

            <!-- Graph Tab -->
            <div v-else-if="activeDetailTab === 'graph'" class="tab-content">
              <div ref="graphChart" class="graph-chart"></div>
            </div>

            <!-- Changes Tab -->
            <div v-else-if="activeDetailTab === 'changes'" class="tab-content">
              <div class="changes-list">
                <div v-for="change in classChanges" :key="change.id" class="change-item">
                  <div class="change-header">
                    <span class="change-type" :class="change.type">{{ change.type }}</span>
                    <span class="change-date">{{ formatDate(change.date) }}</span>
                  </div>
                  <div class="change-content">{{ change.description }}</div>
                  <div class="change-author">by {{ change.author }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 水平调整条 -->
        <div 
          v-if="!isMobileView && showClassDetails && (showComments || showProjectFeed)" 
          class="resize-handle-h"
          @mousedown="startMiddleResize"
        ></div>

        <!-- 右侧：Comments 和 Project Feed 面板 -->
        <div 
          class="right-panels"
          v-show="!isMobileView || (activeMobilePanel === 'comments' || activeMobilePanel === 'feed')"
        >
          <!-- Comments 面板 -->
          <div 
            v-show="showComments && (isMobileView ? activeMobilePanel === 'comments' : true)" 
            class="panel comments-panel"
            :class="{ 'mobile-active': isMobileView && activeMobilePanel === 'comments' }"
            :style="isMobileView ? {} : { height: commentsHeight + 'px' }"
          >
            <div class="panel-header">
              <div class="panel-header-left">
                <span class="panel-title">Comments</span>
              </div>
              <div class="panel-header-right">
                <button class="panel-btn" title="Start new thread" @click="showNewCommentModal = true">
                  <i class="bi bi-plus-circle"></i>
                </button>
                <button class="panel-close" @click="showComments = false" title="Close panel">
                  <i class="bi bi-x"></i>
                </button>
              </div>
            </div>
            <div class="panel-body">
              <div class="comments-list">
                <div v-for="comment in comments" :key="comment.id" class="comment-item">
                  <div class="comment-header">
                    <span class="comment-author">{{ comment.author }}</span>
                    <span class="comment-date">{{ formatDate(comment.date) }}</span>
                  </div>
                  <div class="comment-content">{{ comment.content }}</div>
                  <div class="comment-actions">
                    <button class="btn btn-sm btn-link" @click="replyToComment(comment)">
                      <i class="bi bi-reply"></i> Reply
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 垂直调整条 -->
          <div 
            v-if="!isMobileView && showComments && showProjectFeed" 
            class="resize-handle-v"
            @mousedown="startVerticalResize"
          ></div>

          <!-- Project Feed 面板 -->
          <div 
            v-show="showProjectFeed && (isMobileView ? activeMobilePanel === 'feed' : true)" 
            class="panel project-feed-panel"
            :class="{ 'mobile-active': isMobileView && activeMobilePanel === 'feed' }"
            :style="isMobileView ? {} : { flex: 1 }"
          >
            <div class="panel-header">
              <div class="panel-header-left">
                <span class="panel-title">Project Feed</span>
              </div>
              <div class="panel-header-right">
                <button class="panel-btn" title="Filter" @click="showFeedFilter = true">
                  <i class="bi bi-funnel"></i>
                </button>
                <button class="panel-close" @click="showProjectFeed = false" title="Close panel">
                  <i class="bi bi-x"></i>
                </button>
              </div>
            </div>
            <div class="panel-body">
              <div class="feed-list">
                <div v-for="activity in projectActivities" :key="activity.id" class="feed-item">
                  <div class="feed-icon" :class="activity.type">
                    <i :class="activity.icon"></i>
                  </div>
                  <div class="feed-content">
                    <div class="feed-text">{{ activity.description }}</div>
                    <div class="feed-meta">
                      <span class="feed-author">{{ activity.author }}</span>
                      <span class="feed-date">{{ formatDate(activity.date) }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 恢复面板按钮 -->
      <div v-if="!showClassHierarchy || !showClassDetails || !showComments || !showProjectFeed" class="restore-panels">
        <button v-if="!showClassHierarchy" class="btn-restore" @click="showClassHierarchy = true">
          <i class="bi bi-diagram-3"></i> Show Hierarchy
        </button>
        <button v-if="!showClassDetails" class="btn-restore" @click="showClassDetails = true">
          <i class="bi bi-file-earmark-text"></i> Show Details
        </button>
        <button v-if="!showComments" class="btn-restore" @click="showComments = true">
          <i class="bi bi-chat-left-text"></i> Show Comments
        </button>
        <button v-if="!showProjectFeed" class="btn-restore" @click="showProjectFeed = true">
          <i class="bi bi-activity"></i> Show Feed
        </button>
      </div>
    </div>

    <!-- Create Class Modal -->
    <div v-if="showCreateClassModal" class="modal-overlay" @click.self="showCreateClassModal = false">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Create New Class</h5>
            <button type="button" class="btn-close" @click="showCreateClassModal = false"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">Class Name</label>
              <input v-model="newClassName" type="text" class="form-control" placeholder="Enter class name">
            </div>
            <div class="mb-3">
              <label class="form-label">Parent Class</label>
              <select v-model="newClassParent" class="form-select">
                <option value="">owl:Thing (Root)</option>
                <option v-for="cls in allClasses" :key="cls.id" :value="cls.id">
                  {{ cls.label }}
                </option>
              </select>
            </div>
            <div class="mb-3">
              <label class="form-label">Comment</label>
              <textarea v-model="newClassComment" class="form-control" rows="3" placeholder="Enter class description"></textarea>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showCreateClassModal = false">Cancel</button>
            <button type="button" class="btn btn-primary" @click="createClass">Create</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Search Modal -->
    <div v-if="showSearchModal" class="modal-overlay" @click.self="showSearchModal = false">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Search Classes</h5>
            <button type="button" class="btn-close" @click="showSearchModal = false"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <input v-model="searchQuery" type="text" class="form-control" placeholder="Search classes...">
            </div>
            <div class="search-results">
              <div 
                v-for="result in searchResults" 
                :key="result.id" 
                class="search-result-item"
                @click="selectSearchResult(result)"
              >
                <i class="bi bi-circle"></i>
                <span>{{ result.label }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Filter Modal -->
    <div v-if="showFilterModal" class="modal-overlay" @click.self="showFilterModal = false">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Filter Options</h5>
            <button type="button" class="btn-close" @click="showFilterModal = false"></button>
          </div>
          <div class="modal-body">
            <div class="form-check">
              <input v-model="filterOptions.showDeprecated" class="form-check-input" type="checkbox" id="showDeprecated">
              <label class="form-check-label" for="showDeprecated">Show deprecated classes</label>
            </div>
            <div class="form-check">
              <input v-model="filterOptions.showExternal" class="form-check-input" type="checkbox" id="showExternal">
              <label class="form-check-label" for="showExternal">Show external classes</label>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showFilterModal = false">Cancel</button>
            <button type="button" class="btn btn-primary" @click="applyFilter">Apply</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, inject, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import * as echarts from 'echarts'

const http = inject('$http') || window.$http
const route = useRoute()
const router = useRouter()

// 响应式布局相关
const windowWidth = ref(window.innerWidth)
const windowHeight = ref(window.innerHeight)
const isMobileView = computed(() => windowWidth.value < 992)
const activeMobilePanel = ref('hierarchy')

// 项目信息
const projectId = ref(route.query.projectId || '1')
const projectName = ref('')
const currentUser = ref('bjsun07')

// 面板显示状态
const showClassHierarchy = ref(true)
const showClassDetails = ref(true)
const showComments = ref(true)
const showProjectFeed = ref(true)

// 面板宽度
const leftPanelWidth = ref(260)
const middlePanelWidth = ref(380)
const commentsHeight = ref(280)

// 实体标签
const entityTabs = [
  { id: 'classes', label: 'Classes', shortLabel: 'Cls', icon: 'bi bi-diagram-3', module: 'ClassEditor' },
  { id: 'properties', label: 'Properties', shortLabel: 'Prop', icon: 'bi bi-link-45deg', module: 'PropertyEditor' },
  { id: 'individuals', label: 'Individuals', shortLabel: 'Ind', icon: 'bi bi-people', module: 'IndividualEditor' },
  { id: 'comments', label: 'Comments', shortLabel: 'Cmt', icon: 'bi bi-chat-left-text', module: 'CommentManager' },
  { id: 'changes', label: 'Changes by Entity', shortLabel: 'Chg', icon: 'bi bi-clock-history', module: 'ChangesViewer' },
  { id: 'history', label: 'History', shortLabel: 'His', icon: 'bi bi-journal-text', module: 'HistoryViewer' }
]
const activeTab = ref('classes')

// 移动端面板选项
const availablePanels = [
  { id: 'hierarchy', label: 'Class Hierarchy', shortLabel: 'Hierarchy', icon: 'bi bi-diagram-3' },
  { id: 'details', label: 'Class Details', shortLabel: 'Class', icon: 'bi bi-file-earmark-text' },
  { id: 'comments', label: 'Comments', shortLabel: 'Comments', icon: 'bi bi-chat-left-text' },
  { id: 'feed', label: 'Project Feed', shortLabel: 'Feed', icon: 'bi bi-activity' }
]

// 类层次结构
const classHierarchy = ref([])
const filteredClassHierarchy = ref([])
const selectedClass = ref(null)
const searchQuery = ref('')

// 树形配置
const treeProps = {
  label: 'label',
  children: 'children'
}

// 详情面板标签
const activeDetailTab = ref('details')

// 类属性
const classProperties = ref([])

// 类变更历史
const classChanges = ref([])

// 评论
const comments = ref([])

// 项目活动
const projectActivities = ref([])

// 模态框状态
const showCreateClassModal = ref(false)
const showSearchModal = ref(false)
const showFilterModal = ref(false)
const showNewCommentModal = ref(false)
const showFeedFilter = ref(false)

// 新类表单
const newClassName = ref('')
const newClassParent = ref('')
const newClassComment = ref('')

// 过滤选项
const filterOptions = ref({
  showDeprecated: false,
  showExternal: false
})

// 所有类（用于下拉选择）
const allClasses = ref([])

// 搜索结果
const searchResults = computed(() => {
  if (!searchQuery.value) return []
  const query = searchQuery.value.toLowerCase()
  return allClasses.value.filter(cls => 
    cls.label.toLowerCase().includes(query) || 
    cls.id.toLowerCase().includes(query)
  )
})

// 图表引用
const graphChart = ref(null)
let chartInstance = null

// 窗口大小监听
const handleResize = () => {
  windowWidth.value = window.innerWidth
  windowHeight.value = window.innerHeight
  if (chartInstance) {
    chartInstance.resize()
  }
}

// 移动端菜单
const showMobileMenu = ref(false)
const toggleMobileMenu = () => {
  showMobileMenu.value = !showMobileMenu.value
}

onMounted(async () => {
  window.addEventListener('resize', handleResize)
  await loadProjectInfo()
  await loadClassHierarchy()
  await loadComments()
  await loadProjectActivities()
  
  // 初始化图表
  if (graphChart.value) {
    initGraphChart()
  }
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (chartInstance) {
    chartInstance.dispose()
  }
})

// 监听图表容器变化
watch(graphChart, (newVal) => {
  if (newVal && activeDetailTab.value === 'graph') {
    nextTick(() => {
      initGraphChart()
    })
  }
})

// 加载项目信息
const loadProjectInfo = async () => {
  try {
    const response = await http.get(`/ontology/findById/${projectId.value}`)
    if (response.data) {
      projectName.value = response.data.name
    }
  } catch (error) {
    console.error('Failed to load project info:', error)
    projectName.value = 'error'
  }
}

// 加载类层次结构
const loadClassHierarchy = async () => {
  try {
    const response = await http.get(`/class/hierarchy/${projectId.value}`)
    classHierarchy.value = response.data || []
    filteredClassHierarchy.value = classHierarchy.value
    
    // 提取所有类
    allClasses.value = extractAllClasses(classHierarchy.value)
    
    // 默认选中 owl:Thing
    const rootClass = findClassById(classHierarchy.value, 'owl:Thing')
    if (rootClass) {
      selectedClass.value = rootClass
      await loadClassDetails(rootClass.id)
    }
  } catch (error) {
    console.error('Failed to load class hierarchy:', error)
    // 使用模拟数据
    classHierarchy.value = getMockClassHierarchy()
    filteredClassHierarchy.value = classHierarchy.value
    allClasses.value = extractAllClasses(classHierarchy.value)
    selectedClass.value = classHierarchy.value[0]
  }
}

// 提取所有类
const extractAllClasses = (hierarchy) => {
  const classes = []
  const traverse = (nodes) => {
    nodes.forEach(node => {
      classes.push({ id: node.id, label: node.label })
      if (node.children) {
        traverse(node.children)
      }
    })
  }
  traverse(hierarchy)
  return classes
}

// 根据ID查找类
const findClassById = (hierarchy, id) => {
  for (const node of hierarchy) {
    if (node.id === id) return node
    if (node.children) {
      const found = findClassById(node.children, id)
      if (found) return found
    }
  }
  return null
}

// 加载类详情
const loadClassDetails = async (classId) => {
  try {
    const response = await http.get(`/class/details/${projectId.value}/${classId}`)
    const details = response.data || {}
    
    // 更新选中类的详细信息
    if (selectedClass.value) {
      selectedClass.value = { ...selectedClass.value, ...details }
    }
    
    // 加载属性
    await loadClassProperties(classId)
    
    // 加载变更历史
    await loadClassChanges(classId)
    
    // 更新图表
    if (chartInstance && activeDetailTab.value === 'graph') {
      updateGraphChart()
    }
  } catch (error) {
    console.error('Failed to load class details:', error)
    // 使用模拟数据
    classProperties.value = getMockProperties()
    classChanges.value = getMockChanges()
  }
}

// 加载类属性
const loadClassProperties = async (classId) => {
  try {
    const response = await http.get(`/class/properties/${projectId.value}/${classId}`)
    classProperties.value = response.data || []
  } catch (error) {
    console.error('Failed to load class properties:', error)
    classProperties.value = getMockProperties()
  }
}

// 加载类变更历史
const loadClassChanges = async (classId) => {
  try {
    const response = await http.get(`/class/changes/${projectId.value}/${classId}`)
    classChanges.value = response.data || []
  } catch (error) {
    console.error('Failed to load class changes:', error)
    classChanges.value = getMockChanges()
  }
}

// 加载评论
const loadComments = async () => {
  try {
    const response = await http.get(`/comments/${projectId.value}`)
    comments.value = response.data || []
  } catch (error) {
    console.error('Failed to load comments:', error)
    comments.value = getMockComments()
  }
}

// 加载项目活动
const loadProjectActivities = async () => {
  try {
    const response = await http.get(`/activities/${projectId.value}`)
    projectActivities.value = response.data || []
  } catch (error) {
    console.error('Failed to load project activities:', error)
    projectActivities.value = getMockActivities()
  }
}

// 处理类选择
const handleClassSelect = async (data) => {
  selectedClass.value = data
  await loadClassDetails(data.id)
}

// 过滤类
const filterClasses = () => {
  if (!searchQuery.value) {
    filteredClassHierarchy.value = classHierarchy.value
    return
  }
  
  const query = searchQuery.value.toLowerCase()
  const filterNode = (node) => {
    const matches = node.label.toLowerCase().includes(query) || 
                   node.id.toLowerCase().includes(query)
    
    if (node.children) {
      node.children = node.children.filter(filterNode)
      return matches || node.children.length > 0
    }
    
    return matches
  }
  
  filteredClassHierarchy.value = classHierarchy.value.filter(filterNode)
}

// 创建类
const createClass = async () => {
  try {
    const newClass = {
      id: `class_${Date.now()}`,
      label: newClassName.value,
      parentId: newClassParent.value || 'owl:Thing',
      comment: newClassComment.value,
      projectId: projectId.value
    }
    
    await http.post('/class/create', newClass)
    
    // 重新加载类层次结构
    await loadClassHierarchy()
    
    // 关闭模态框
    showCreateClassModal.value = false
    newClassName.value = ''
    newClassParent.value = ''
    newClassComment.value = ''
  } catch (error) {
    console.error('Failed to create class:', error)
    alert('Failed to create class')
  }
}

// 删除选中的类
const deleteSelectedClass = async () => {
  if (!selectedClass.value || selectedClass.value.id === 'owl:Thing') return
  
  if (!confirm(`Are you sure you want to delete "${selectedClass.value.label}"?`)) return
  
  try {
    await http.delete(`/class/delete/${projectId.value}/${selectedClass.value.id}`)
    await loadClassHierarchy()
    selectedClass.value = null
  } catch (error) {
    console.error('Failed to delete class:', error)
    alert('Failed to delete class')
  }
}

// 选择搜索结果
const selectSearchResult = (result) => {
  const classNode = findClassById(classHierarchy.value, result.id)
  if (classNode) {
    handleClassSelect(classNode)
  }
  showSearchModal.value = false
}

// 应用过滤
const applyFilter = () => {
  // 实现过滤逻辑
  showFilterModal.value = false
}

// 回复评论
const replyToComment = (comment) => {
  console.log('Reply to:', comment)
  // 实现回复逻辑
}

// 初始化图表
const initGraphChart = () => {
  if (!graphChart.value) return
  
  chartInstance = echarts.init(graphChart.value)
  updateGraphChart()
}

// 更新图表
const updateGraphChart = () => {
  if (!chartInstance || !selectedClass.value) return
  
  const nodes = []
  const links = []
  
  // 添加当前类
  nodes.push({
    id: selectedClass.value.id,
    name: selectedClass.value.label,
    symbolSize: 60,
    itemStyle: { color: '#4a90d9' }
  })
  
  // 添加父类
  if (selectedClass.value.parents) {
    selectedClass.value.parents.forEach(parent => {
      nodes.push({
        id: parent.id,
        name: parent.label,
        symbolSize: 40,
        itemStyle: { color: '#6c757d' }
      })
      links.push({
        source: parent.id,
        target: selectedClass.value.id,
        lineStyle: { color: '#28a745' }
      })
    })
  }
  
  // 添加子类
  if (selectedClass.value.children) {
    selectedClass.value.children.forEach(child => {
      nodes.push({
        id: child.id,
        name: child.label,
        symbolSize: 40,
        itemStyle: { color: '#6c757d' }
      })
      links.push({
        source: selectedClass.value.id,
        target: child.id,
        lineStyle: { color: '#dc3545' }
      })
    })
  }
  
  const option = {
    tooltip: {},
    series: [{
      type: 'graph',
      layout: 'force',
      data: nodes,
      links: links,
      roam: true,
      label: {
        show: true,
        position: 'bottom'
      },
      force: {
        repulsion: 300,
        edgeLength: 150
      }
    }]
  }
  
  chartInstance.setOption(option)
}

// 处理标签切换
const handleTabChange = (tab) => {
  activeTab.value = tab.id
  // TODO: 根据标签切换不同的编辑器
}

// 添加标签
const handleAddTab = () => {
  console.log('Add tab clicked')
  // TODO: 实现添加标签功能
}

// 返回首页
const goHome = () => {
  router.push('/projects/list')
}

// 登出
const logout = () => {
  localStorage.removeItem('user')
  localStorage.removeItem('token')
  router.push('/login')
}

// 开始左侧调整
const startLeftResize = (e) => {
  const startX = e.clientX
  const startWidth = leftPanelWidth.value
  
  const handleMouseMove = (e) => {
    const delta = e.clientX - startX
    leftPanelWidth.value = Math.max(200, Math.min(400, startWidth + delta))
  }
  
  const handleMouseUp = () => {
    document.removeEventListener('mousemove', handleMouseMove)
    document.removeEventListener('mouseup', handleMouseUp)
  }
  
  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
}

// 开始中间调整
const startMiddleResize = (e) => {
  const startX = e.clientX
  const startWidth = middlePanelWidth.value
  
  const handleMouseMove = (e) => {
    const delta = e.clientX - startX
    middlePanelWidth.value = Math.max(300, Math.min(600, startWidth + delta))
  }
  
  const handleMouseUp = () => {
    document.removeEventListener('mousemove', handleMouseMove)
    document.removeEventListener('mouseup', handleMouseUp)
  }
  
  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
}

// 开始垂直调整
const startVerticalResize = (e) => {
  const startY = e.clientY
  const startHeight = commentsHeight.value
  
  const handleMouseMove = (e) => {
    const delta = e.clientY - startY
    commentsHeight.value = Math.max(150, Math.min(500, startHeight + delta))
  }
  
  const handleMouseUp = () => {
    document.removeEventListener('mousemove', handleMouseMove)
    document.removeEventListener('mouseup', handleMouseUp)
  }
  
  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
}

// 模拟数据
const getMockClassHierarchy = () => [
  {
    id: 'owl:Thing',
    label: 'owl:Thing',
    icon: 'bi bi-circle-fill',
    comment: 'The root class of all classes',
    children: [
      {
        id: 'Person',
        label: 'Person',
        icon: 'bi bi-person',
        comment: 'A human being',
        children: [
          { id: 'Student', label: 'Student', icon: 'bi bi-person', comment: 'A person who is studying' },
          { id: 'Teacher', label: 'Teacher', icon: 'bi bi-person', comment: 'A person who teaches' }
        ]
      },
      {
        id: 'Organization',
        label: 'Organization',
        icon: 'bi bi-building',
        comment: 'An organized group of people',
        children: [
          { id: 'University', label: 'University', icon: 'bi bi-building', comment: 'An institution of higher education' },
          { id: 'Company', label: 'Company', icon: 'bi bi-building', comment: 'A business organization' }
        ]
      }
    ]
  }
]

const getMockProperties = () => [
  { id: 'name', label: 'name', type: 'data', values: ['John Doe'] },
  { id: 'age', label: 'age', type: 'data', values: ['25'] },
  { id: 'worksFor', label: 'works for', type: 'object', values: ['Company A'] }
]

const getMockChanges = () => [
  { id: 1, type: 'created', description: 'Class created', author: 'admin', date: new Date() },
  { id: 2, type: 'modified', description: 'Added property "name"', author: 'admin', date: new Date(Date.now() - 86400000) }
]

const getMockComments = () => [
  { id: 1, author: 'admin', content: 'This class needs more properties', date: new Date() },
  { id: 2, author: 'user1', content: 'Agreed, we should add email and phone', date: new Date(Date.now() - 3600000) }
]

const getMockActivities = () => [
  { id: 1, type: 'create', icon: 'bi bi-plus-circle', description: 'Created class "Student"', author: 'admin', date: new Date() },
  { id: 2, type: 'edit', icon: 'bi bi-pencil', description: 'Modified "Person" properties', author: 'user1', date: new Date(Date.now() - 1800000) },
  { id: 3, type: 'comment', icon: 'bi bi-chat', description: 'Added comment on "Organization"', author: 'admin', date: new Date(Date.now() - 3600000) }
]

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}
</script>

<style scoped>
/* ==================== 基础布局 ==================== */
.class-editor {
  height: 100vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* ==================== 第一层：顶层系统菜单 ==================== */
.top-menu-bar {
  height: 36px;
  background: linear-gradient(180deg, #4a90d9 0%, #357abd 100%);
  border-bottom: 1px solid #2a6299;
  flex-shrink: 0;
}

.menu-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 12px;
  max-width: 100%;
}

.menu-left,
.menu-center,
.menu-right {
  display: flex;
  align-items: center;
  gap: 4px;
}

.menu-brand {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #fff !important;
  font-size: 14px;
  font-weight: 500;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.menu-brand:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.menu-link {
  color: rgba(255, 255, 255, 0.9) !important;
  font-size: 13px;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 4px;
}

.menu-link:hover {
  color: #fff !important;
  background-color: rgba(255, 255, 255, 0.1);
}

.menu-center .nav-link {
  color: rgba(255, 255, 255, 0.9) !important;
  font-size: 13px;
  padding: 4px 10px;
}

.menu-center .nav-link:hover,
.menu-center .nav-link:focus {
  color: #fff !important;
  background-color: rgba(255, 255, 255, 0.1);
}

.menu-center .dropdown-menu {
  font-size: 13px;
  border: none;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.menu-center .dropdown-item {
  padding: 6px 12px;
}

.menu-center .dropdown-item:hover {
  background-color: #f8f9fa;
}

/* 移动端菜单按钮 */
.mobile-menu-btn {
  padding: 4px 8px;
  color: #fff;
  border: none;
  background: transparent;
}

.mobile-menu-btn:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

/* 移动端菜单遮罩 */
.mobile-menu-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1050;
  display: flex;
  justify-content: flex-end;
}

.mobile-menu-content {
  width: 280px;
  height: 100%;
  background-color: #fff;
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.mobile-menu-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-bottom: 1px solid #e9ecef;
}

.mobile-menu-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.mobile-menu-close {
  background: none;
  border: none;
  font-size: 20px;
  color: #666;
  cursor: pointer;
  padding: 4px;
}

.mobile-menu-items {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

.mobile-menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  color: #333;
  text-decoration: none;
  transition: background-color 0.2s;
}

.mobile-menu-item:hover {
  background-color: #f8f9fa;
}

.mobile-menu-item i {
  font-size: 18px;
  color: #666;
}

/* ==================== 第二层：实体类型标签栏 ==================== */
.entity-tabs-bar {
  height: 32px;
  background: linear-gradient(180deg, #f8f9fa 0%, #e9ecef 100%);
  border-bottom: 1px solid #dee2e6;
  flex-shrink: 0;
  padding: 0 8px;
}

.tabs-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
}

.tabs-left {
  display: flex;
  align-items: center;
  gap: 2px;
}

.entity-tab {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  font-size: 12px;
  color: #495057;
  cursor: pointer;
  border-radius: 3px 3px 0 0;
  transition: all 0.2s;
  white-space: nowrap;
}

.entity-tab:hover {
  background-color: rgba(74, 144, 217, 0.1);
  color: #4a90d9;
}

.entity-tab.active {
  background-color: #fff;
  color: #4a90d9;
  font-weight: 500;
  border-bottom: 2px solid #4a90d9;
}

.entity-tab i {
  font-size: 12px;
}

.btn-add-tab {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  font-size: 12px;
  color: #495057;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.btn-add-tab:hover {
  color: #4a90d9;
  background-color: rgba(74, 144, 217, 0.1);
}

/* ==================== 第三层：内容区域 ==================== */
.content-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: #f5f5f5;
}

.panels-wrapper {
  flex: 1;
  display: flex;
  overflow: hidden;
  padding: 8px;
  gap: 8px;
}

.panels-wrapper.mobile-view {
  flex-direction: column;
  padding: 4px;
}

/* 面板通用样式 */
.panel {
  background-color: #fff;
  border: 1px solid #dee2e6;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.panel-header {
  height: 36px;
  background: linear-gradient(180deg, #f8f9fa 0%, #e9ecef 100%);
  border-bottom: 1px solid #dee2e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 10px;
  flex-shrink: 0;
}

.panel-header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.panel-header-right {
  display: flex;
  align-items: center;
  gap: 6px;
}

.panel-title {
  font-size: 13px;
  font-weight: 600;
  color: #495057;
}

.panel-actions {
  display: flex;
  align-items: center;
  gap: 2px;
}

.panel-btn {
  width: 26px;
  height: 26px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: 1px solid transparent;
  border-radius: 3px;
  color: #6c757d;
  cursor: pointer;
  transition: all 0.2s;
  padding: 0;
}

.panel-btn:hover:not(:disabled) {
  background-color: #fff;
  border-color: #dee2e6;
  color: #495057;
}

.panel-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.panel-btn.active {
  background-color: #4a90d9;
  color: #fff;
  border-color: #4a90d9;
}

.panel-close {
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: none;
  color: #adb5bd;
  cursor: pointer;
  font-size: 16px;
  transition: color 0.2s;
  padding: 0;
}

.panel-close:hover {
  color: #dc3545;
}

.panel-body {
  flex: 1;
  overflow: auto;
  padding: 10px;
}

/* 特定面板样式 */
.class-hierarchy-panel {
  min-width: 200px;
  max-width: 400px;
}

.class-details-panel {
  min-width: 300px;
  max-width: 600px;
}

.right-panels {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 200px;
}

.comments-panel {
  min-height: 150px;
  max-height: 500px;
}

.project-feed-panel {
  flex: 1;
  min-height: 150px;
}

/* 调整条 */
.resize-handle-h {
  width: 6px;
  cursor: col-resize;
  background-color: transparent;
  transition: background-color 0.2s;
  flex-shrink: 0;
}

.resize-handle-h:hover {
  background-color: #4a90d9;
}

.resize-handle-v {
  height: 6px;
  cursor: row-resize;
  background-color: transparent;
  transition: background-color 0.2s;
  flex-shrink: 0;
}

.resize-handle-v:hover {
  background-color: #4a90d9;
}

/* 树形控件样式 */
.class-tree {
  font-size: 13px;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 2px 0;
}

.tree-node.selected {
  font-weight: 600;
  color: #4a90d9;
}

.node-icon {
  font-size: 10px;
  color: #6c757d;
}

.node-label {
  flex: 1;
}

.child-count {
  font-size: 11px;
  color: #adb5bd;
}

/* 搜索框 */
.search-box {
  display: flex;
  gap: 6px;
  margin-bottom: 10px;
}

.search-box input {
  flex: 1;
}

/* 信息展示 */
.info-section {
  margin-bottom: 16px;
}

.section-title {
  font-size: 12px;
  font-weight: 600;
  color: #495057;
  margin-bottom: 8px;
  padding-bottom: 4px;
  border-bottom: 1px solid #e9ecef;
}

.info-item {
  margin-bottom: 8px;
  font-size: 13px;
}

.info-item label {
  font-weight: 500;
  color: #6c757d;
  margin-right: 8px;
}

.comment-text {
  margin-top: 4px;
  padding: 8px;
  background-color: #f8f9fa;
  border-radius: 4px;
  font-size: 13px;
  color: #495057;
}

/* 列表样式 */
.parent-list,
.children-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.parent-list li,
.children-list li {
  padding: 4px 0;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.property-item {
  padding: 8px;
  background-color: #f8f9fa;
  border-radius: 4px;
  margin-bottom: 8px;
}

.property-header {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 500;
  margin-bottom: 4px;
}

.property-type {
  font-size: 11px;
  color: #6c757d;
}

.property-values {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.value-tag {
  padding: 2px 6px;
  background-color: #e9ecef;
  border-radius: 3px;
  font-size: 12px;
  color: #495057;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #adb5bd;
}

.empty-state i {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 14px;
}

/* 图表容器 */
.graph-chart {
  width: 100%;
  height: 400px;
}

/* 变更列表 */
.changes-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.change-item {
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
  border-left: 3px solid #4a90d9;
}

.change-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.change-type {
  font-size: 11px;
  font-weight: 600;
  padding: 2px 6px;
  border-radius: 3px;
  text-transform: uppercase;
}

.change-type.created {
  background-color: #d4edda;
  color: #155724;
}

.change-type.modified {
  background-color: #fff3cd;
  color: #856404;
}

.change-type.deleted {
  background-color: #f8d7da;
  color: #721c24;
}

.change-date {
  font-size: 11px;
  color: #6c757d;
}

.change-content {
  font-size: 13px;
  color: #495057;
  margin-bottom: 4px;
}

.change-author {
  font-size: 11px;
  color: #6c757d;
}

/* 评论列表 */
.comments-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.comment-item {
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.comment-author {
  font-size: 12px;
  font-weight: 600;
  color: #4a90d9;
}

.comment-date {
  font-size: 11px;
  color: #6c757d;
}

.comment-content {
  font-size: 13px;
  color: #495057;
  line-height: 1.5;
}

.comment-actions {
  margin-top: 8px;
}

/* 活动列表 */
.feed-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.feed-item {
  display: flex;
  gap: 10px;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.feed-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.feed-icon.create {
  background-color: #d4edda;
  color: #155724;
}

.feed-icon.edit {
  background-color: #fff3cd;
  color: #856404;
}

.feed-icon.comment {
  background-color: #cce5ff;
  color: #004085;
}

.feed-content {
  flex: 1;
}

.feed-text {
  font-size: 13px;
  color: #495057;
  margin-bottom: 4px;
}

.feed-meta {
  display: flex;
  gap: 8px;
  font-size: 11px;
  color: #6c757d;
}

/* 恢复面板按钮 */
.restore-panels {
  display: flex;
  gap: 8px;
  padding: 8px;
  background-color: #fff;
  border-top: 1px solid #dee2e6;
}

.btn-restore {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  font-size: 12px;
  color: #495057;
  background-color: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-restore:hover {
  background-color: #e9ecef;
  border-color: #ced4da;
}

/* 搜索结果 */
.search-results {
  max-height: 300px;
  overflow-y: auto;
}

.search-result-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.search-result-item:hover {
  background-color: #f8f9fa;
}

/* 移动端面板切换 */
.mobile-panel-tabs {
  display: flex;
  gap: 4px;
  padding: 4px 8px;
  background-color: #f8f9fa;
  border-bottom: 1px solid #dee2e6;
  overflow-x: auto;
}

.mobile-panel-tab {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 6px 10px;
  font-size: 10px;
  color: #6c757d;
  background: transparent;
  border: 1px solid transparent;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 60px;
}

.mobile-panel-tab i {
  font-size: 16px;
}

.mobile-panel-tab.active {
  color: #4a90d9;
  background-color: rgba(74, 144, 217, 0.1);
  border-color: #4a90d9;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 1060;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.modal-dialog {
  background-color: #fff;
  border-radius: 6px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  max-width: 500px;
  width: 100%;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-dialog.modal-sm {
  max-width: 350px;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #dee2e6;
}

.modal-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
}

.modal-body {
  padding: 16px;
  overflow-y: auto;
  flex: 1;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 12px 16px;
  border-top: 1px solid #dee2e6;
}

/* WebProtege 风格图标 */
.wp-icon {
  display: block;
}

/* 响应式布局 */
@media (max-width: 1200px) {
  .class-hierarchy-panel {
    min-width: 180px;
  }
  
  .class-details-panel {
    min-width: 250px;
  }
}

@media (max-width: 992px) {
  .panels-wrapper {
    flex-direction: column;
  }
  
  .class-hierarchy-panel,
  .class-details-panel {
    width: 100% !important;
    max-width: none;
    min-width: auto;
  }
  
  .right-panels {
    min-width: auto;
  }
  
  .resize-handle-h,
  .resize-handle-v {
    display: none;
  }
  
  .panel.mobile-active {
    flex: 1;
    display: flex !important;
  }
}

@media (max-width: 768px) {
  .top-menu-bar {
    height: 40px;
  }
  
  .menu-brand {
    font-size: 13px;
  }
  
  .entity-tabs-bar {
    height: 36px;
  }
  
  .entity-tab {
    padding: 4px 8px;
    font-size: 11px;
  }
  
  .panel-header {
    height: 40px;
  }
  
  .mobile-panel-tabs {
    padding: 6px;
  }
  
  .mobile-panel-tab {
    min-width: 70px;
    padding: 8px 12px;
  }
}

@media (max-width: 576px) {
  .mobile-panel-tabs {
    padding: 4px;
  }
  
  .mobile-panel-tab {
    padding: 4px 8px;
    min-width: 50px;
    font-size: 9px;
  }
  
  .mobile-panel-tab i {
    font-size: 14px;
  }
  
  .panel-header {
    height: 36px;
    padding: 0 8px;
  }
  
  .panel-title {
    font-size: 12px;
  }
  
  .panel-btn {
    width: 28px;
    height: 28px;
  }
  
  .modal-dialog {
    margin: 10px;
    max-height: calc(100vh - 20px);
  }
}

@media (max-width: 375px) {
  .mobile-panel-tab {
    min-width: 45px;
    padding: 3px 6px;
  }
  
  .mobile-panel-tab span {
    font-size: 8px;
  }
}

/* 打印样式 */
@media print {
  .top-menu-bar,
  .entity-tabs-bar,
  .mobile-panel-tabs,
  .panel-header,
  .resize-handle-h,
  .resize-handle-v,
  .restore-panels {
    display: none !important;
  }
  
  .class-editor {
    height: auto;
  }
  
  .content-area {
    overflow: visible;
  }
  
  .panels-wrapper {
    display: block;
  }
  
  .panel {
    display: block !important;
    width: 100% !important;
    height: auto !important;
    border: 1px solid #ccc;
    margin-bottom: 20px;
    page-break-inside: avoid;
  }
  
  .panel-body {
    overflow: visible;
  }
}
</style>