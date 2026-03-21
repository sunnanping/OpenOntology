<template>
  <div class="class-editor">
    <!-- 顶部导航栏 -->
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <div class="container-fluid">
        <a class="navbar-brand" href="#">OpenOntology</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav">
            <li class="nav-item">
              <router-link to="/" class="nav-link">Home</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/ontology" class="nav-link">Ontology</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/class-editor" class="nav-link active">Class Editor</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/property" class="nav-link">Property</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/instance" class="nav-link">Instance</router-link>
            </li>
          </ul>
          <div class="ms-auto d-flex align-items-center">
            <div class="me-3">
              <span class="text-muted">bjsun07</span>
            </div>
            <button class="btn btn-outline-primary">Logout</button>
          </div>
        </div>
      </div>
    </nav>

    <!-- 主内容区域 -->
    <div class="container-fluid">
      <div class="row">
        <!-- 左侧 Class Hierarchy 面板 -->
        <div class="col-md-3 border-end">
          <div class="p-3">
            <!-- 项目信息 -->
            <div class="project-info mb-3 p-2 bg-light rounded">
              <small class="text-muted">Project ID:</small>
              <span class="ms-1 font-weight-bold">{{ projectId }}</span>
            </div>
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h5>Class Hierarchy</h5>
              <div class="btn-group">
                <button class="btn btn-sm btn-outline-secondary" @click="showCreateClassModal = true">
                  <i class="bi bi-plus"></i> Create
                </button>
                <button class="btn btn-sm btn-outline-secondary" @click="deleteSelectedClass" :disabled="!selectedClass">
                  <i class="bi bi-trash"></i> Delete
                </button>
              </div>
            </div>
            
            <!-- 搜索框 -->
            <div class="mb-3">
              <div class="input-group">
                <input 
                  type="text" 
                  class="form-control form-control-sm" 
                  placeholder="Search classes..." 
                  v-model="searchQuery"
                  @keyup.enter="searchClasses"
                >
                <button class="btn btn-outline-secondary btn-sm" @click="searchClasses">
                  <i class="bi bi-search"></i>
                </button>
              </div>
            </div>
            
            <!-- 类层次结构树 -->
            <div class="class-hierarchy-tree">
              <el-tree
                :data="treeData"
                :props="treeProps"
                :default-expanded-keys="expandedKeys"
                @node-click="handleNodeClick"
                @node-expand="handleNodeExpand"
                @node-collapse="handleNodeCollapse"
                node-key="id"
                default-expand-all
              >
                <template #default="{ node, data }">
                  <span class="custom-tree-node">
                    <span :class="{ 'font-weight-bold': selectedClass?.id === data.id }">
                      {{ data.name }}
                    </span>
                  </span>
                </template>
              </el-tree>
            </div>
          </div>
        </div>

        <!-- 中间 Class Details 面板 -->
        <div class="col-md-6 border-end">
          <div v-if="selectedClass" class="p-3">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h5>Class {{ selectedClass.name }}</h5>
              <div class="btn-group">
                <button class="btn btn-sm btn-outline-secondary" @click="editClass(selectedClass)">
                  <i class="bi bi-pencil"></i> Edit
                </button>
              </div>
            </div>
            
            <!-- 标签页 -->
            <ul class="nav nav-tabs mb-3">
              <li class="nav-item">
                <a class="nav-link active" data-bs-toggle="tab" href="#details">Details</a>
              </li>
              <li class="nav-item">
                <a class="nav-link" data-bs-toggle="tab" href="#entity-graph">Entity Graph</a>
              </li>
              <li class="nav-item">
                <a class="nav-link" data-bs-toggle="tab" href="#changes">Changes</a>
              </li>
            </ul>
            
            <!-- 标签页内容 -->
            <div class="tab-content">
              <!-- Details 标签页 -->
              <div class="tab-pane fade show active" id="details">
                <div class="mb-4">
                  <h6>IRI</h6>
                  <p class="text-muted">{{ selectedClass.iri }}</p>
                </div>
                
                <div class="mb-4">
                  <h6>Annotations</h6>
                  <div class="border rounded p-3">
                    <div class="mb-2">
                      <span class="font-weight-bold">rdfs:label</span>
                      <span class="ms-2">{{ selectedClass.name }}</span>
                      <button class="btn btn-sm btn-outline-secondary ms-2">Edit</button>
                    </div>
                    <button class="btn btn-sm btn-outline-primary mt-2">
                      <i class="bi bi-plus"></i> Add Annotation
                    </button>
                  </div>
                </div>
                
                <div class="mb-4">
                  <h6>Parents</h6>
                  <div class="border rounded p-3">
                    <div class="mb-2">
                      <span>owl:Thing</span>
                      <button class="btn btn-sm btn-outline-secondary ms-2">Remove</button>
                    </div>
                    <button class="btn btn-sm btn-outline-primary mt-2">
                      <i class="bi bi-plus"></i> Add Parent
                    </button>
                  </div>
                </div>
                
                <div class="mb-4">
                  <h6>Relationships</h6>
                  <div class="border rounded p-3">
                    <div class="mb-2">
                      <input type="text" class="form-control form-control-sm mb-1" placeholder="Enter property">
                      <input type="text" class="form-control form-control-sm" placeholder="Enter value">
                    </div>
                    <button class="btn btn-sm btn-outline-primary mt-2">
                      <i class="bi bi-plus"></i> Add Relationship
                    </button>
                  </div>
                </div>
              </div>
              
              <!-- Entity Graph 标签页 -->
              <div class="tab-pane fade" id="entity-graph">
                <div v-if="entityGraph" class="border rounded p-3">
                  <h6>Entity Graph for {{ selectedClass.name }}</h6>
                  <div class="entity-graph-container" style="height: 400px; border: 1px solid #e9ecef;">
                    <div ref="graphContainer" style="width: 100%; height: 100%;"></div>
                  </div>
                </div>
                <div v-else class="text-center py-5">
                  <div class="spinner-border" role="status">
                    <span class="visually-hidden">Loading...</span>
                  </div>
                </div>
              </div>
              
              <!-- Changes 标签页 -->
              <div class="tab-pane fade" id="changes">
                <div v-if="classChanges && classChanges.length > 0">
                  <div v-for="change in classChanges" :key="change.id" class="border rounded p-3 mb-2">
                    <div class="d-flex justify-content-between">
                      <span class="font-weight-bold">{{ change.action }}</span>
                      <span class="text-muted">{{ formatDate(change.timestamp) }}</span>
                    </div>
                    <div class="mt-1">
                      <span class="text-muted">By: {{ change.author }}</span>
                    </div>
                    <div class="mt-2">
                      {{ change.details }}
                    </div>
                  </div>
                </div>
                <div v-else class="text-center py-5">
                  <div class="spinner-border" role="status">
                    <span class="visually-hidden">Loading...</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="p-3 text-center text-muted">
            <div class="py-5">
              <i class="bi bi-folder-open text-4xl mb-3"></i>
              <h5>Select a class to view details</h5>
              <p>Choose a class from the hierarchy on the left</p>
            </div>
          </div>
        </div>

        <!-- 右侧 Comments 和 Project Feed 面板 -->
        <div class="col-md-3">
          <!-- Comments 部分 -->
          <div class="p-3 border-bottom">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h5>Comments</h5>
              <button class="btn btn-sm btn-outline-secondary" @click="showCreateCommentModal = true">
                <i class="bi bi-plus"></i> Start new thread
              </button>
            </div>
            <div v-if="comments && comments.length > 0">
              <div v-for="comment in comments" :key="comment.id" class="border rounded p-3 mb-2">
                <div class="d-flex justify-content-between">
                  <span class="font-weight-bold">{{ comment.author }}</span>
                  <span class="text-muted">{{ formatDate(comment.createdAt) }}</span>
                </div>
                <div class="mt-1">
                  {{ comment.content }}
                </div>
              </div>
            </div>
            <div v-else class="text-center py-3 text-muted">
              No comments yet
            </div>
          </div>
          
          <!-- Project Feed 部分 -->
          <div class="p-3">
            <h5 class="mb-3">Project Feed</h5>
            <div v-if="projectActivities && projectActivities.length > 0">
              <div v-for="activity in projectActivities" :key="activity.id" class="border rounded p-3 mb-2">
                <div class="d-flex justify-content-between">
                  <span class="font-weight-bold">{{ activity.author }}</span>
                  <span class="text-muted">{{ formatDate(activity.timestamp) }}</span>
                </div>
                <div class="mt-1">
                  {{ activity.action }}
                </div>
                <div class="mt-1 text-muted">
                  {{ activity.entityType }}: {{ activity.entityName }}
                </div>
              </div>
            </div>
            <div v-else class="text-center py-3 text-muted">
              No activities yet
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Create Class Modal -->
    <div class="modal" :class="{ 'show': showCreateClassModal }" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Create Class</h5>
            <button type="button" class="btn-close" @click="showCreateClassModal = false"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="handleCreateClass">
              <div class="mb-3">
                <label for="className" class="form-label">Class Name</label>
                <input type="text" class="form-control" id="className" v-model="createClassForm.name" required>
              </div>
              <div class="mb-3">
                <label for="classIri" class="form-label">IRI</label>
                <input type="text" class="form-control" id="classIri" v-model="createClassForm.iri" required>
              </div>
              <div class="mb-3">
                <label for="parentClass" class="form-label">Parent Class</label>
                <select class="form-select" id="parentClass" v-model="createClassForm.parentId">
                  <option value="owl:Thing">owl:Thing</option>
                  <option v-for="node in classHierarchy" :key="node.id" :value="node.id">
                    {{ node.name }}
                  </option>
                </select>
              </div>
              <button type="submit" class="btn btn-primary">Create</button>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- Create Comment Modal -->
    <div class="modal" :class="{ 'show': showCreateCommentModal }" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Start New Thread</h5>
            <button type="button" class="btn-close" @click="showCreateCommentModal = false"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="handleCreateComment">
              <div class="mb-3">
                <label for="commentContent" class="form-label">Comment</label>
                <textarea class="form-control" id="commentContent" v-model="createCommentForm.content" rows="4" required></textarea>
              </div>
              <button type="submit" class="btn btn-primary">Post</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject, watch, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import * as echarts from 'echarts'

const http = inject('$http') || window.$http
const route = useRoute()

// 获取projectId参数
const projectId = ref(route.query.projectId || '1')

// 状态管理
const classHierarchy = ref([])
const treeData = ref([])
const treeProps = {
  children: 'children',
  label: 'name'
}
const expandedKeys = ref([])
const selectedClass = ref(null)
const searchQuery = ref('')
const entityGraph = ref(null)
const classChanges = ref(null)
const comments = ref([])
const projectActivities = ref([])
const graphContainer = ref(null)
let graphChart = null

// 模态框状态
const showCreateClassModal = ref(false)
const showCreateCommentModal = ref(false)

// 表单数据
const createClassForm = ref({
  name: '',
  iri: '',
  parentId: 'owl:Thing'
})

const createCommentForm = ref({
  content: ''
})

// 初始化加载
onMounted(async () => {
  // 如果有projectId参数，使用它加载数据
  if (route.query.projectId) {
    projectId.value = route.query.projectId
  }
  await loadClassHierarchy()
  await loadProjectActivities()
})

// 监听entityGraph变化，更新图表
watch(entityGraph, (newGraph) => {
  if (newGraph && graphContainer.value) {
    nextTick(() => {
      renderEntityGraph(newGraph)
    })
  }
}, { deep: true })

// 加载类层次结构
const loadClassHierarchy = async () => {
  try {
    const response = await http.get(`/api/classes/hierarchy/${projectId.value}`)
    classHierarchy.value = response.data
    // 转换为ElementUI Tree需要的数据格式
    treeData.value = response.data
    // 设置默认展开节点
    expandedKeys.value = response.data.flatMap(node => {
      const keys = [node.id]
      if (node.children) {
        node.children.forEach(child => {
          keys.push(child.id)
          if (child.children) {
            child.children.forEach(grandchild => {
              keys.push(grandchild.id)
            })
          }
        })
      }
      return keys
    })
  } catch (error) {
    console.error('Failed to load class hierarchy:', error)
  }
}

// 加载项目活动
const loadProjectActivities = async () => {
  try {
    const response = await http.get(`/api/classes/activities/${projectId.value}`)
    projectActivities.value = response.data
  } catch (error) {
    console.error('Failed to load project activities:', error)
  }
}

// 处理节点点击
const handleNodeClick = async (data) => {
  selectedClass.value = data
  await loadClassDetails(data.id)
}

// 处理节点展开
const handleNodeExpand = (node) => {
  console.log('Node expanded:', node)
}

// 处理节点折叠
const handleNodeCollapse = (node) => {
  console.log('Node collapsed:', node)
}

// 选择类
const selectClass = async (cls) => {
  selectedClass.value = cls
  await loadClassDetails(cls.id)
}

// 加载类详情
const loadClassDetails = async (classId) => {
  try {
    // 加载实体图
    const graphResponse = await http.get(`/api/classes/entity-graph/${classId}`)
    entityGraph.value = graphResponse.data
    
    // 加载变更历史
    const changesResponse = await http.get(`/api/classes/changes/${classId}`)
    classChanges.value = changesResponse.data
    
    // 加载评论
    const commentsResponse = await http.get(`/api/classes/comments/${classId}`)
    comments.value = commentsResponse.data
  } catch (error) {
    console.error('Failed to load class details:', error)
  }
}

// 渲染实体图
const renderEntityGraph = (graphData) => {
  if (!graphContainer.value) return
  
  // 初始化ECharts实例
  if (!graphChart) {
    graphChart = echarts.init(graphContainer.value)
  }
  
  // 准备数据
  const nodes = graphData.nodes || []
  const edges = graphData.edges || []
  
  // 转换为ECharts需要的数据格式
  const echartsNodes = nodes.map(node => ({
    id: node.id,
    name: node.label,
    symbolSize: 50,
    itemStyle: {
      color: getNodeColor(node.type)
    }
  }))
  
  const echartsEdges = edges.map(edge => ({
    source: edge.source,
    target: edge.target,
    label: {
      show: true,
      formatter: edge.label
    }
  }))
  
  // 配置项
  const option = {
    tooltip: {},
    animationDurationUpdate: 1500,
    animationEasingUpdate: 'quinticInOut',
    series: [
      {
        type: 'graph',
        layout: 'force',
        data: echartsNodes,
        links: echartsEdges,
        roam: true,
        label: {
          show: true
        },
        force: {
          repulsion: 1000,
          edgeLength: [80, 120]
        }
      }
    ]
  }
  
  // 设置配置项
  graphChart.setOption(option)
  
  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    graphChart.resize()
  })
}

// 根据节点类型获取颜色
const getNodeColor = (type) => {
  switch (type) {
    case 'class':
      return '#409EFF'
    case 'property':
      return '#67C23A'
    case 'individual':
      return '#E6A23C'
    default:
      return '#909399'
  }
}

// 搜索类
const searchClasses = async () => {
  if (!searchQuery.value) return
  
  try {
    const response = await http.get(`/api/classes/search?query=${encodeURIComponent(searchQuery.value)}&projectId=${projectId.value}`)
    // 这里可以处理搜索结果
    console.log('Search results:', response.data)
  } catch (error) {
    console.error('Failed to search classes:', error)
  }
}

// 创建类
const handleCreateClass = async () => {
  try {
    const response = await http.post('/api/classes/create', {
      name: createClassForm.value.name,
      iri: createClassForm.value.iri
    })
    await loadClassHierarchy()
    showCreateClassModal.value = false
    createClassForm.value = {
      name: '',
      iri: '',
      parentId: 'owl:Thing'
    }
  } catch (error) {
    console.error('Failed to create class:', error)
  }
}

// 删除类
const deleteSelectedClass = async () => {
  if (!selectedClass.value) return
  
  if (confirm('Are you sure you want to delete this class?')) {
    try {
      await http.delete(`/api/classes/delete/${selectedClass.value.id}`)
      await loadClassHierarchy()
      selectedClass.value = null
    } catch (error) {
      console.error('Failed to delete class:', error)
    }
  }
}

// 编辑类
const editClass = (cls) => {
  // 这里可以实现编辑功能
  console.log('Edit class:', cls)
}

// 创建评论
const handleCreateComment = async () => {
  if (!selectedClass.value) return
  
  try {
    const response = await http.post('/api/classes/comments', {
      entityId: selectedClass.value.id,
      entityType: 'Class',
      author: 'bjsun07',
      content: createCommentForm.value.content
    })
    comments.value.push(response.data)
    showCreateCommentModal.value = false
    createCommentForm.value = {
      content: ''
    }
  } catch (error) {
    console.error('Failed to create comment:', error)
  }
}

// 格式化日期
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleString()
}
</script>

<style scoped>
.class-editor {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.class-node {
  margin-bottom: 4px;
}

.class-node-header {
  padding: 4px 8px;
  border-radius: 4px;
  cursor: pointer;
}

.class-node-header:hover {
  background-color: #e9ecef;
}

.modal.show {
  display: block;
  background-color: rgba(0, 0, 0, 0.5);
}

.entity-graph-container {
  background-color: #f8f9fa;
  border-radius: 4px;
}

.project-info {
  border: 1px solid #dee2e6;
}

.project-info span {
  color: #0d6efd;
}
</style>
