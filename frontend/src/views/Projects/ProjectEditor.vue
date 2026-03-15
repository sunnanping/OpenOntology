<template>
  <div class="project-editor">
    <!-- 顶部导航栏 -->
    <nav class="editor-navbar">
      <div class="project-title">
        <span class="project-name">{{ projectName }}</span>
        <router-link to="/projects/list" class="home-link">Home</router-link>
      </div>
      <div class="editor-tabs">
        <button 
          v-for="tab in tabs" 
          :key="tab.id"
          :class="['tab-btn', { active: activeTab === tab.id }]"
          @click="activeTab = tab.id"
        >
          {{ tab.name }}
        </button>
        <button class="tab-btn add-tab" @click="showAddTab = true">Add tab</button>
      </div>
      <div class="editor-actions">
        <span class="display-btn">Display ▼</span>
        <span class="project-btn">Project ▼</span>
        <span class="share-btn">Share</span>
        <span class="user-name">{{ currentUser }}</span>
        <span class="help-btn">Help ▼</span>
      </div>
    </nav>

    <!-- 主编辑区域 -->
    <div class="editor-main">
      <!-- 左侧：类层次结构 -->
      <div 
        class="left-panel draggable-panel" 
        v-if="showLeftPanel"
        :style="leftPanelStyle"
        ref="leftPanel"
      >
        <div class="panel-header" @mousedown="startDrag($event, 'left')">
          <span class="panel-title">Class Hierarchy</span>
          <div class="panel-tools">
            <button class="tool-btn" title="Add Class" @click="showAddClassModal = true">➕</button>
            <button class="tool-btn" title="Delete Class" @click="deleteSelectedClass">✕</button>
            <button class="tool-btn" title="Search" @click="showSearchModal = true">🔍</button>
            <button class="tool-btn" @click="showLeftPanel = false">×</button>
          </div>
        </div>
        <div class="panel-content">
          <div class="class-tree">
            <ul class="tree-list">
              <tree-node 
                v-for="node in classTreeData" 
                :key="node.id"
                :node="node"
                :expanded-nodes="expandedNodes"
                :selected-node="selectedTreeNode"
                :dragging-node="draggingNode"
                :drop-target-node="dropTargetNode"
                @toggle="toggleNode"
                @select="selectTreeNode"
                @contextmenu="showContextMenu"
                @dragstart="onNodeDragStart"
                @dragend="onNodeDragEnd"
                @drop="onNodeDrop"
              />
            </ul>
          </div>
        </div>
      </div>

      <!-- 中间：内容编辑区 -->
      <div class="center-panel">
        <div class="content-tabs">
          <div 
            v-for="(tab, index) in openTabs" 
            :key="tab.id"
            :class="['content-tab', { active: activeContentTab === tab.id }]"
          >
            <span @click="activeContentTab = tab.id">{{ tab.name }}</span>
            <button class="close-tab" @click="closeTab(index)">×</button>
          </div>
        </div>
        <div class="content-area">
          <div v-if="!selectedClass" class="empty-state">
            <div class="empty-box">
              <p>Nothing selected</p>
            </div>
          </div>
          <div v-else class="class-editor">
            <!-- Class Editor Header with 3 Action Buttons -->
            <div class="class-editor-header">
              <h2>Class: {{ selectedClass.name }}</h2>
              <div class="class-action-buttons">
                <button 
                  class="action-btn" 
                  :class="{ active: activeClassView === 'edit' }"
                  @click="activeClassView = 'edit'"
                  title="Edit Class"
                >
                  ✏️
                </button>
                <button 
                  class="action-btn" 
                  :class="{ active: activeClassView === 'graph' }"
                  @click="activeClassView = 'graph'"
                  title="Graph View"
                >
                  🌐
                </button>
                <button 
                  class="action-btn" 
                  :class="{ active: activeClassView === 'history' }"
                  @click="activeClassView = 'history'"
                  title="Change History"
                >
                  📜
                </button>
              </div>
            </div>

            <!-- Edit View -->
            <div v-if="activeClassView === 'edit'" class="class-edit-view">
              <div class="class-section">
                <label class="section-label">IRI</label>
                <div class="iri-display">{{ selectedClass.iri }}</div>
              </div>

              <!-- Annotations Section -->
              <div class="class-section">
                <label class="section-label">Annotations</label>
                <div class="annotations-list">
                  <div v-for="(annotation, index) in classAnnotations" :key="index" class="annotation-row">
                    <span class="annotation-property">{{ annotation.property }}</span>
                    <span class="annotation-lang" v-if="annotation.lang">{{ annotation.lang }}</span>
                    <input type="text" v-model="annotation.value" class="form-control annotation-value" />
                    <button class="btn-icon" @click="removeAnnotation(index)">✕</button>
                  </div>
                  <div class="annotation-row add-new">
                    <input type="text" v-model="newAnnotation.property" placeholder="Enter property" class="form-control" />
                    <input type="text" v-model="newAnnotation.value" placeholder="Enter value" class="form-control" />
                    <input type="text" v-model="newAnnotation.lang" placeholder="lang" class="form-control lang-input" />
                    <button class="btn btn-sm btn-primary" @click="addAnnotation">Add</button>
                  </div>
                </div>
              </div>

              <!-- Parents Section -->
              <div class="class-section">
                <label class="section-label">Parents</label>
                <div class="parents-list">
                  <div v-for="(parent, index) in classParents" :key="index" class="parent-row">
                    <span class="parent-radio">●</span>
                    <span class="parent-name">{{ parent.name }}</span>
                    <button class="btn-icon" @click="removeParent(index)">✕</button>
                  </div>
                  <div class="parent-row add-new">
                    <input 
                      type="text" 
                      v-model="newParentSearch" 
                      placeholder="Enter a class name" 
                      class="form-control"
                      @input="searchParentClasses"
                    />
                    <div v-if="parentSearchResults.length > 0" class="search-dropdown">
                      <div 
                        v-for="result in parentSearchResults" 
                        :key="result.id"
                        class="search-result"
                        @click="addParent(result)"
                      >
                        {{ result.name }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Relationships Section -->
              <div class="class-section">
                <label class="section-label">Relationships</label>
                <div class="relationships-list">
                  <div v-for="(rel, index) in classRelationships" :key="index" class="relationship-row">
                    <input type="text" v-model="rel.property" placeholder="Enter property" class="form-control" />
                    <input type="text" v-model="rel.value" placeholder="Enter value" class="form-control" />
                    <input type="text" v-model="rel.lang" placeholder="lang" class="form-control lang-input" />
                    <button class="btn-icon" @click="removeRelationship(index)">✕</button>
                  </div>
                  <div class="relationship-row add-new">
                    <input type="text" v-model="newRelationship.property" placeholder="Enter property" class="form-control" />
                    <input type="text" v-model="newRelationship.value" placeholder="Enter value" class="form-control" />
                    <input type="text" v-model="newRelationship.lang" placeholder="lang" class="form-control lang-input" />
                    <button class="btn btn-sm btn-primary" @click="addRelationship">Add</button>
                  </div>
                </div>
              </div>

              <div class="editor-actions-bar">
                <button class="btn btn-primary" @click="saveClass">Save</button>
                <button class="btn btn-secondary" @click="cancelEdit">Cancel</button>
              </div>
            </div>

            <!-- Graph View -->
            <div v-else-if="activeClassView === 'graph'" class="class-graph-view">
              <div class="graph-toolbar">
                <button class="toolbar-btn" @click="zoomIn" title="Zoom In">➕</button>
                <button class="toolbar-btn" @click="zoomOut" title="Zoom Out">➖</button>
                <button class="toolbar-btn" @click="resetZoom" title="Reset">⟲</button>
                <div class="toolbar-divider"></div>
                <button class="toolbar-btn" @click="downloadGraph('png')" title="Download PNG">🖼️ PNG</button>
                <button class="toolbar-btn" @click="downloadGraph('jpg')" title="Download JPG">🖼️ JPG</button>
                <button class="toolbar-btn" @click="downloadGraph('drawio')" title="Download DrawIO">📊 DrawIO</button>
                <button class="toolbar-btn" @click="downloadGraph('mermaid')" title="Download Mermaid">📄 Mermaid</button>
              </div>
              <div class="graph-container" ref="graphContainer">
                <div class="graph-canvas" :style="graphCanvasStyle">
                  <div class="graph-node root-node" :style="rootNodeStyle">
                    {{ selectedClass.name }}
                  </div>
                  <div class="graph-edges">
                    <div v-for="(parent, index) in graphParents" :key="'parent-'+index" class="graph-edge parent-edge">
                      <div class="edge-line" :style="getParentEdgeStyle(index)"></div>
                      <div class="edge-node parent-node" :style="getParentNodeStyle(index)">
                        {{ parent.name }}
                      </div>
                    </div>
                    <div v-for="(child, index) in graphChildren" :key="'child-'+index" class="graph-edge child-edge">
                      <div class="edge-line" :style="getChildEdgeStyle(index)"></div>
                      <div class="edge-node child-node" :style="getChildNodeStyle(index)">
                        {{ child.name }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- History View -->
            <div v-else-if="activeClassView === 'history'" class="class-history-view">
              <div class="history-list">
                <div v-for="change in paginatedHistory" :key="change.id" class="history-item">
                  <div class="history-header">
                    <span class="history-date">Changes on {{ formatDate(change.date) }}</span>
                    <span class="history-revision">R {{ change.revision }}</span>
                  </div>
                  <div class="history-action">{{ change.action }}</div>
                  <div class="history-author">
                    <span class="author-avatar">{{ change.author.charAt(0).toUpperCase() }}</span>
                    <span class="author-name">{{ change.author }}</span>
                    <span class="author-time">authored {{ change.changes.length }} changes {{ formatTime(change.timestamp) }}</span>
                  </div>
                  <div class="history-changes">
                    <div v-for="(c, idx) in change.changes" :key="idx" class="change-detail" :class="c.type">
                      <span class="change-icon">{{ c.type === 'add' ? '✚' : c.type === 'remove' ? '⊖' : '✎' }}</span>
                      <span class="change-text" v-html="c.description"></span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="pagination">
                <span class="page-info">Page {{ currentPage }} of {{ totalPages }}</span>
                <button class="btn btn-sm" :disabled="currentPage === 1" @click="currentPage--">Prev</button>
                <button class="btn btn-sm" :disabled="currentPage === totalPages" @click="currentPage++">Next</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：评论和动态 -->
      <div 
        class="right-panels draggable-panel"
        :style="rightPanelStyle"
        ref="rightPanel"
      >
        <!-- 评论面板 -->
        <div 
          class="right-panel comments-panel" 
          v-if="showCommentsPanel"
          :style="{ height: commentsPanelHeight + 'px' }"
        >
          <div class="panel-header" @mousedown="startDrag($event, 'right')">
            <span class="panel-title">Comments<span v-if="selectedClass">: {{ selectedClass.name }}</span></span>
            <div class="panel-tools">
              <button class="tool-btn" title="Add Comment" @click="focusCommentInput">➕</button>
              <button class="tool-btn" @click="showCommentsPanel = false">×</button>
            </div>
          </div>
          <div class="panel-content">
            <div class="comments-list">
              <div v-for="comment in comments" :key="comment.id" class="comment-item">
                <div class="comment-header">
                  <span class="comment-author">{{ comment.author }}</span>
                  <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
                </div>
                <div class="comment-body">{{ comment.content }}</div>
              </div>
            </div>
            <div class="comment-input" ref="commentInput">
              <textarea v-model="newComment" placeholder="Add a comment..."></textarea>
              <button class="btn btn-primary btn-sm" @click="addComment">Post</button>
            </div>
          </div>
        </div>

        <!-- 可拖拽分隔条 -->
        <div 
          v-if="showCommentsPanel && showFeedPanel"
          class="resize-handle"
          @mousedown="startResize"
        ></div>

        <!-- 项目动态面板 -->
        <div 
          class="right-panel feed-panel" 
          v-if="showFeedPanel"
          :style="{ flex: 1 }"
        >
          <div class="panel-header" @mousedown="startDrag($event, 'right')">
            <span class="panel-title">Project Feed</span>
            <button class="tool-btn" @click="showFeedPanel = false">×</button>
          </div>
          <div class="panel-content">
            <div class="feed-list">
              <div v-for="activity in projectFeed" :key="activity.id" class="feed-item">
                <div class="feed-avatar">{{ activity.author ? activity.author.charAt(0).toUpperCase() : 'U' }}</div>
                <div class="feed-content">
                  <div class="feed-action">{{ activity.action }}</div>
                  <div class="feed-time">{{ formatTime(activity.timestamp) }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部状态栏 -->
    <div class="editor-statusbar">
      <span class="status-item">Ready</span>
      <span class="status-item">{{ selectedClass ? selectedClass.name : 'No selection' }}</span>
    </div>

    <!-- Add Class Modal -->
    <div v-if="showAddClassModal" class="modal-overlay" @click="showAddClassModal = false">
      <div 
        class="modal-content draggable-modal" 
        :style="addClassModalStyle"
        @click.stop
        ref="addClassModal"
      >
        <div class="modal-header" @mousedown="startModalDrag($event, 'addClass')">
          <h3>Create Classes</h3>
          <button class="close-btn" @click="showAddClassModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>Class names</label>
            <textarea 
              v-model="newClassNames" 
              class="form-control"
              rows="5"
              placeholder="Enter one name per line (press CTRL+ENTER to accept and close panel)"
            ></textarea>
          </div>
          <div class="form-group">
            <label>Language Tag</label>
            <div class="input-group">
              <input type="text" v-model="languageTag" class="form-control" placeholder="Leave blank for no language tag" />
              <button class="btn btn-secondary" @click="languageTag = ''">Reset</button>
            </div>
            <small class="form-text">Leave blank for no language tag. Press 'Reset' to reset the language tag to the project default.</small>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="showAddClassModal = false">Cancel</button>
          <button class="btn btn-primary" @click="createClasses">Create</button>
        </div>
      </div>
    </div>

    <!-- Delete Class Confirmation Modal -->
    <div v-if="showDeleteModal" class="modal-overlay" @click="showDeleteModal = false">
      <div 
        class="modal-content draggable-modal delete-modal" 
        :style="deleteModalStyle"
        @click.stop
        ref="deleteModal"
      >
        <div class="modal-header" @mousedown="startModalDrag($event, 'delete')">
          <h3>Delete {{ classToDelete?.name }}</h3>
          <button class="close-btn" @click="showDeleteModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="delete-confirm">
            <div class="question-icon">?</div>
            <p>Are you sure you want to delete the class <strong>{{ classToDelete?.name }}</strong>?</p>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="showDeleteModal = false">Cancel</button>
          <button class="btn btn-danger" @click="confirmDeleteClass">Delete</button>
        </div>
      </div>
    </div>

    <!-- Search Class Modal -->
    <div v-if="showSearchModal" class="modal-overlay" @click="showSearchModal = false">
      <div 
        class="modal-content draggable-modal search-modal" 
        :style="searchModalStyle"
        @click.stop
        ref="searchModal"
      >
        <div class="modal-header" @mousedown="startModalDrag($event, 'search')">
          <h3>Search for Class</h3>
          <button class="close-btn" @click="showSearchModal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <input 
              type="text" 
              v-model="searchQuery" 
              class="form-control"
              placeholder="Enter text to search for (multi-word search supported)"
              @input="performSearch"
            />
            <small class="form-text">Separate search words by spaces to perform multi-word search. Search for tagged entities by entering the name of the tag.</small>
          </div>
          <div class="search-results" v-if="searchResults.length > 0">
            <div 
              v-for="result in searchResults" 
              :key="result.id"
              class="search-result-item"
              @click="selectSearchResult(result)"
            >
              <span class="result-icon">📄</span>
              <span class="result-name">{{ result.name }}</span>
              <span class="result-path">{{ result.path }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Context Menu -->
    <div 
      v-if="contextMenu.show" 
      class="context-menu"
      :style="{ top: contextMenu.y + 'px', left: contextMenu.x + 'px' }"
    >
      <div class="context-menu-item" @click="addChildClass">Add Subclass</div>
      <div class="context-menu-item" @click="deleteNode">Delete</div>
      <div class="context-menu-item" @click="renameNode">Rename</div>
    </div>
  </div>
</template>

<script>
// 递归树节点组件
const TreeNode = {
  name: 'TreeNode',
  props: ['node', 'expandedNodes', 'selectedNode', 'draggingNode', 'dropTargetNode'],
  emits: ['toggle', 'select', 'contextmenu', 'dragstart', 'dragend', 'drop'],
  template: `
    <li 
      class="tree-item" 
      :class="{ 
        'selected': selectedNode?.id === node.id,
        'dragging': draggingNode?.id === node.id,
        'drop-target': dropTargetNode?.id === node.id
      }"
      draggable="true"
      @dragstart="onDragStart"
      @dragend="onDragEnd"
      @dragover.prevent="onDragOver"
      @dragleave="onDragLeave"
      @drop.prevent="onDrop"
    >
      <span 
        class="tree-toggle" 
        @click="$emit('toggle', node)"
        v-if="node.children && node.children.length > 0"
      >
        {{ isExpanded ? '▼' : '▶' }}
      </span>
      <span class="tree-toggle-placeholder" v-else></span>
      <span class="tree-icon">{{ node.children && node.children.length > 0 ? '📁' : '📄' }}</span>
      <span 
        class="tree-label" 
        @click="$emit('select', node)"
        @contextmenu.prevent="$emit('contextmenu', $event, node)"
      >{{ node.name }}</span>
      <ul v-if="isExpanded && node.children" class="tree-children">
        <tree-node 
          v-for="child in node.children" 
          :key="child.id"
          :node="child"
          :expanded-nodes="expandedNodes"
          :selected-node="selectedNode"
          :dragging-node="draggingNode"
          :drop-target-node="dropTargetNode"
          @toggle="$emit('toggle', $event)"
          @select="$emit('select', $event)"
          @contextmenu="$emit('contextmenu', $event)"
          @dragstart="$emit('dragstart', $event)"
          @dragend="$emit('dragend', $event)"
          @drop="$emit('drop', $event)"
        />
      </ul>
    </li>
  `,
  computed: {
    isExpanded() {
      return this.expandedNodes.includes(this.node.id)
    }
  },
  methods: {
    onDragStart(e) {
      e.dataTransfer.effectAllowed = 'move'
      e.dataTransfer.setData('text/plain', this.node.id)
      this.$emit('dragstart', this.node)
    },
    onDragEnd() {
      this.$emit('dragend')
    },
    onDragOver(e) {
      e.preventDefault()
      e.dataTransfer.dropEffect = 'move'
      this.$emit('dragover', this.node)
    },
    onDragLeave() {
      this.$emit('dragleave', this.node)
    },
    onDrop(e) {
      const draggedId = e.dataTransfer.getData('text/plain')
      this.$emit('drop', { draggedId, targetNode: this.node })
    }
  }
}

export default {
  name: 'ProjectEditor',
  components: { TreeNode },
  data() {
    return {
      projectId: null,
      projectName: 'testProject',
      currentUser: 'bjsun07',
      activeTab: 'classes',
      tabs: [
        { id: 'classes', name: 'Classes' },
        { id: 'properties', name: 'Properties' },
        { id: 'individuals', name: 'Individuals' },
        { id: 'comments', name: 'Comments' },
        { id: 'changes', name: 'Changes by Entity' },
        { id: 'history', name: 'History' },
        { id: 'owl-properties', name: 'OWL Properties' },
        { id: 'owl-classes', name: 'OWL Classes' },
        { id: 'query', name: 'Query' },
        { id: 'test', name: 'TestMenu' }
      ],
      showAddTab: false,
      showLeftPanel: true,
      showCommentsPanel: true,
      showFeedPanel: true,
      
      // 面板高度调整
      commentsPanelHeight: 250,
      minPanelHeight: 100,
      isResizing: false,
      resizeStartY: 0,
      resizeStartHeight: 0,
      
      // 拖拽相关
      leftPanelPos: { x: 0, y: 0 },
      rightPanelPos: { x: 0, y: 0 },
      isDragging: false,
      dragTarget: null,
      dragOffset: { x: 0, y: 0 },
      
      // 弹窗相关
      showAddClassModal: false,
      showDeleteModal: false,
      showSearchModal: false,
      addClassModalPos: { x: 100, y: 100 },
      deleteModalPos: { x: 200, y: 150 },
      searchModalPos: { x: 150, y: 120 },
      isModalDragging: false,
      modalDragTarget: null,
      modalDragOffset: { x: 0, y: 0 },
      
      // 类操作
      newClassNames: '',
      languageTag: '',
      classToDelete: null,
      searchQuery: '',
      searchResults: [],
      selectedTreeNode: null,
      
      // 右键菜单
      contextMenu: {
        show: false,
        x: 0,
        y: 0,
        node: null
      },
      
      // 拖拽状态
      draggingNode: null,
      dropTargetNode: null,
      
      // 树形数据 - 支持任意层级
      expandedNodes: ['owl:Thing', '1', '2'],
      selectedClass: null,
      classTreeData: [
        {
          id: 'owl:Thing',
          name: 'owl:Thing',
          iri: 'http://www.w3.org/2002/07/owl#Thing',
          children: [
            {
              id: '1',
              name: 'ICT领域',
              iri: 'http://example.org/ICT',
              description: 'ICT领域',
              children: [
                {
                  id: '1-1',
                  name: 'B域',
                  iri: 'http://example.org/B',
                  children: []
                },
                {
                  id: '1-2',
                  name: 'M域',
                  iri: 'http://example.org/M',
                  children: []
                },
                {
                  id: '1-3',
                  name: 'O域',
                  iri: 'http://example.org/O',
                  children: []
                }
              ]
            },
            {
              id: '2',
              name: '应用架构',
              iri: 'http://example.org/AppArch',
              description: '应用架构',
              children: [
                {
                  id: '2-1',
                  name: '一般系统',
                  iri: 'http://example.org/General',
                  children: []
                },
                {
                  id: '2-2',
                  name: '核心系统',
                  iri: 'http://example.org/Core',
                  children: []
                },
                {
                  id: '2-3',
                  name: '重要系统',
                  iri: 'http://example.org/Important',
                  children: []
                }
              ]
            },
            {
              id: '3',
              name: '数据架构',
              iri: 'http://example.org/DataArch',
              description: '数据架构',
              children: []
            },
            {
              id: '4',
              name: '物理位置',
              iri: 'http://example.org/PhysicalLoc',
              description: '物理位置',
              children: [
                {
                  id: '4-1',
                  name: '办公室',
                  iri: 'http://example.org/Office',
                  children: [
                    {
                      id: '4-1-1',
                      name: '物理位置',
                      iri: 'http://example.org/Physical',
                      children: []
                    }
                  ]
                },
                {
                  id: '4-2',
                  name: '机房',
                  iri: 'http://example.org/Room',
                  children: []
                },
                {
                  id: '4-3',
                  name: '机柜',
                  iri: 'http://example.org/Cabinet',
                  children: []
                }
              ]
            },
            {
              id: '5',
              name: '路由器',
              iri: 'http://example.org/Router',
              description: '路由器',
              children: [
                {
                  id: '5-1',
                  name: 'cisco-G口路由器',
                  iri: 'http://example.org/CiscoRouter',
                  children: []
                }
              ]
            }
          ]
        }
      ],
      
      availableClasses: [],
      openTabs: [],
      activeContentTab: null,
      
      // Class Editor State
      activeClassView: 'edit',
      
      // Annotations
      classAnnotations: [
        { property: 'rdfs:label', value: '一般系统', lang: 'zh' }
      ],
      newAnnotation: { property: '', value: '', lang: '' },
      
      // Parents
      classParents: [
        { id: '2', name: '应用架构' }
      ],
      newParentSearch: '',
      parentSearchResults: [],
      
      // Relationships
      classRelationships: [],
      newRelationship: { property: '', value: '', lang: '' },
      
      // Graph View
      graphZoom: 1,
      graphParents: [],
      graphChildren: [],
      
      // History View
      classHistory: [],
      currentPage: 1,
      pageSize: 5,
      
      comments: [
        { id: 1, author: 'user1', content: 'This class needs more properties.', createdAt: new Date(Date.now() - 3600000) },
        { id: 2, author: 'user2', content: 'Agreed, we should add documentation.', createdAt: new Date(Date.now() - 1800000) }
      ],
      newComment: '',
      projectFeed: [
        { id: 1, action: 'Created class "ICT领域"', timestamp: new Date(Date.now() - 86400000) },
        { id: 2, action: 'Modified property "hasName"', timestamp: new Date(Date.now() - 43200000) },
        { id: 3, action: 'Added individual "Router01"', timestamp: new Date(Date.now() - 3600000) }
      ]
    }
  },
  computed: {
    leftPanelStyle() {
      return {
        transform: `translate(${this.leftPanelPos.x}px, ${this.leftPanelPos.y}px)`
      }
    },
    rightPanelStyle() {
      return {
        transform: `translate(${this.rightPanelPos.x}px, ${this.rightPanelPos.y}px)`
      }
    },
    addClassModalStyle() {
      return {
        left: this.addClassModalPos.x + 'px',
        top: this.addClassModalPos.y + 'px'
      }
    },
    deleteModalStyle() {
      return {
        left: this.deleteModalPos.x + 'px',
        top: this.deleteModalPos.y + 'px'
      }
    },
    searchModalStyle() {
      return {
        left: this.searchModalPos.x + 'px',
        top: this.searchModalPos.y + 'px'
      }
    },
    // Graph View Computed
    graphCanvasStyle() {
      return {
        transform: `scale(${this.graphZoom})`,
        transformOrigin: 'center center'
      }
    },
    rootNodeStyle() {
      return {
        position: 'absolute',
        left: '50%',
        top: '50%',
        transform: 'translate(-50%, -50%)'
      }
    },
    // History Pagination
    paginatedHistory() {
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.classHistory.slice(start, end)
    },
    totalPages() {
      return Math.ceil(this.classHistory.length / this.pageSize)
    }
  },
  mounted() {
    this.projectId = this.$route.params.id
    this.loadProjectData()
    this.flattenClasses()
    
    // 添加全局事件监听
    document.addEventListener('mousemove', this.onDrag)
    document.addEventListener('mouseup', this.stopDrag)
    document.addEventListener('click', this.hideContextMenu)
  },
  beforeUnmount() {
    document.removeEventListener('mousemove', this.onDrag)
    document.removeEventListener('mouseup', this.stopDrag)
    document.removeEventListener('click', this.hideContextMenu)
  },
  methods: {
    loadProjectData() {
      console.log('Loading project:', this.projectId)
    },
    
    // 拖拽功能
    startDrag(e, target) {
      this.isDragging = true
      this.dragTarget = target
      this.dragOffset = {
        x: e.clientX - (target === 'left' ? this.leftPanelPos.x : this.rightPanelPos.x),
        y: e.clientY - (target === 'left' ? this.leftPanelPos.y : this.rightPanelPos.y)
      }
    },
    onDrag(e) {
      if (!this.isDragging) return
      
      if (this.dragTarget === 'left') {
        this.leftPanelPos = {
          x: e.clientX - this.dragOffset.x,
          y: e.clientY - this.dragOffset.y
        }
      } else if (this.dragTarget === 'right') {
        this.rightPanelPos = {
          x: e.clientX - this.dragOffset.x,
          y: e.clientY - this.dragOffset.y
        }
      }
    },
    stopDrag() {
      this.isDragging = false
      this.dragTarget = null
    },
    
    // 调整面板大小
    startResize(e) {
      this.isResizing = true
      this.resizeStartY = e.clientY
      this.resizeStartHeight = this.commentsPanelHeight
      
      document.addEventListener('mousemove', this.onResize)
      document.addEventListener('mouseup', this.stopResize)
    },
    onResize(e) {
      if (!this.isResizing) return
      const delta = e.clientY - this.resizeStartY
      const newHeight = this.resizeStartHeight + delta
      const maxHeight = this.$refs.rightPanel?.clientHeight - this.minPanelHeight - 10 || 400
      this.commentsPanelHeight = Math.max(this.minPanelHeight, Math.min(newHeight, maxHeight))
    },
    stopResize() {
      this.isResizing = false
      document.removeEventListener('mousemove', this.onResize)
      document.removeEventListener('mouseup', this.stopResize)
    },
    focusCommentInput() {
      this.$nextTick(() => {
        const textarea = this.$refs.commentInput?.querySelector('textarea')
        if (textarea) textarea.focus()
      })
    },
    
    // 弹窗拖拽
    startModalDrag(e, target) {
      this.isModalDragging = true
      this.modalDragTarget = target
      const pos = target === 'addClass' ? this.addClassModalPos : 
                  target === 'delete' ? this.deleteModalPos : this.searchModalPos
      this.modalDragOffset = {
        x: e.clientX - pos.x,
        y: e.clientY - pos.y
      }
      
      const moveHandler = (ev) => {
        if (!this.isModalDragging) return
        const newPos = {
          x: ev.clientX - this.modalDragOffset.x,
          y: ev.clientY - this.modalDragOffset.y
        }
        if (target === 'addClass') this.addClassModalPos = newPos
        else if (target === 'delete') this.deleteModalPos = newPos
        else if (target === 'search') this.searchModalPos = newPos
      }
      
      const upHandler = () => {
        this.isModalDragging = false
        this.modalDragTarget = null
        document.removeEventListener('mousemove', moveHandler)
        document.removeEventListener('mouseup', upHandler)
      }
      
      document.addEventListener('mousemove', moveHandler)
      document.addEventListener('mouseup', upHandler)
    },
    
    // 树形操作
    toggleNode(node) {
      if (this.expandedNodes.includes(node.id)) {
        this.expandedNodes = this.expandedNodes.filter(id => id !== node.id)
      } else {
        this.expandedNodes.push(node.id)
      }
    },
    selectTreeNode(node) {
      this.selectedTreeNode = node
      this.selectClass(node)
    },
    
    // 节点拖拽操作
    onNodeDragStart(node) {
      this.draggingNode = node
    },
    onNodeDragEnd() {
      this.draggingNode = null
      this.dropTargetNode = null
    },
    onNodeDrop({ draggedId, targetNode }) {
      if (draggedId === targetNode.id) return
      
      // 检查是否将父节点拖入子节点（防止循环引用）
      const isDescendant = (parent, childId) => {
        if (parent.id === childId) return true
        if (parent.children) {
          for (const child of parent.children) {
            if (isDescendant(child, childId)) return true
          }
        }
        return false
      }
      
      if (isDescendant(this.findNodeById(draggedId), targetNode.id)) {
        alert('Cannot move a parent node into its own child')
        return
      }
      
      // 执行移动操作
      this.moveNode(draggedId, targetNode.id)
      this.draggingNode = null
      this.dropTargetNode = null
      
      // 记录活动
      this.addActivity(`Moved class "${this.findNodeById(draggedId)?.name}" to "${targetNode.name}"`)
    },
    findNodeById(id, nodes = this.classTreeData) {
      for (const node of nodes) {
        if (node.id === id) return node
        if (node.children) {
          const found = this.findNodeById(id, node.children)
          if (found) return found
        }
      }
      return null
    },
    findParentById(id, nodes = this.classTreeData, parent = null) {
      for (const node of nodes) {
        if (node.id === id) return parent
        if (node.children) {
          const found = this.findParentById(id, node.children, node)
          if (found) return found
        }
      }
      return null
    },
    moveNode(nodeId, newParentId) {
      // 从原位置移除节点
      const removeNode = (nodes, id) => {
        const index = nodes.findIndex(n => n.id === id)
        if (index > -1) {
          const node = nodes[index]
          nodes.splice(index, 1)
          return node
        }
        for (const node of nodes) {
          if (node.children) {
            const removed = removeNode(node.children, id)
            if (removed) return removed
          }
        }
        return null
      }
      
      // 获取节点
      const nodeToMove = removeNode(this.classTreeData, nodeId)
      if (!nodeToMove) return
      
      // 添加到新父节点
      const addToParent = (nodes, parentId, node) => {
        for (const n of nodes) {
          if (n.id === parentId) {
            if (!n.children) n.children = []
            n.children.push(node)
            return true
          }
          if (n.children && addToParent(n.children, parentId, node)) {
            return true
          }
        }
        return false
      }
      
      addToParent(this.classTreeData, newParentId, nodeToMove)
      
      // 展开新父节点
      if (!this.expandedNodes.includes(newParentId)) {
        this.expandedNodes.push(newParentId)
      }
      
      // 刷新可用类列表
      this.flattenClasses()
    },
    addActivity(action) {
      this.projectFeed.unshift({
        id: Date.now(),
        action: action,
        timestamp: new Date()
      })
    },
    
    // Annotations
    addAnnotation() {
      if (this.newAnnotation.property && this.newAnnotation.value) {
        this.classAnnotations.push({ ...this.newAnnotation })
        this.newAnnotation = { property: '', value: '', lang: '' }
      }
    },
    removeAnnotation(index) {
      this.classAnnotations.splice(index, 1)
    },
    
    // Parents
    searchParentClasses() {
      if (!this.newParentSearch.trim()) {
        this.parentSearchResults = []
        return
      }
      const query = this.newParentSearch.toLowerCase()
      this.parentSearchResults = this.availableClasses.filter(c => 
        c.name.toLowerCase().includes(query) && 
        !this.classParents.find(p => p.id === c.id) &&
        c.id !== this.selectedClass?.id
      ).slice(0, 5)
    },
    addParent(cls) {
      if (!this.classParents.find(p => p.id === cls.id)) {
        this.classParents.push({ id: cls.id, name: cls.name })
      }
      this.newParentSearch = ''
      this.parentSearchResults = []
    },
    removeParent(index) {
      this.classParents.splice(index, 1)
    },
    
    // Relationships
    addRelationship() {
      if (this.newRelationship.property && this.newRelationship.value) {
        this.classRelationships.push({ ...this.newRelationship })
        this.newRelationship = { property: '', value: '', lang: '' }
      }
    },
    removeRelationship(index) {
      this.classRelationships.splice(index, 1)
    },
    
    // Graph View
    zoomIn() {
      this.graphZoom = Math.min(this.graphZoom + 0.1, 2)
    },
    zoomOut() {
      this.graphZoom = Math.max(this.graphZoom - 0.1, 0.5)
    },
    resetZoom() {
      this.graphZoom = 1
    },
    getParentEdgeStyle(index) {
      const angle = -90 + (index - (this.graphParents.length - 1) / 2) * 30
      const length = 80
      return {
        width: '2px',
        height: length + 'px',
        backgroundColor: '#FFC107',
        position: 'absolute',
        left: '50%',
        bottom: '50%',
        transform: `translateX(-50%) rotate(${angle}deg)`,
        transformOrigin: 'bottom center'
      }
    },
    getParentNodeStyle(index) {
      const angle = -90 + (index - (this.graphParents.length - 1) / 2) * 30
      const distance = 100
      const rad = (angle * Math.PI) / 180
      const x = Math.cos(rad) * distance
      const y = Math.sin(rad) * distance
      return {
        position: 'absolute',
        left: `calc(50% + ${x}px)`,
        bottom: `calc(50% + ${Math.abs(y)}px)`,
        transform: 'translate(-50%, 50%)'
      }
    },
    getChildEdgeStyle(index) {
      const angle = 90 + (index - (this.graphChildren.length - 1) / 2) * 30
      const length = 80
      return {
        width: '2px',
        height: length + 'px',
        backgroundColor: '#FFC107',
        position: 'absolute',
        left: '50%',
        top: '50%',
        transform: `translateX(-50%) rotate(${angle}deg)`,
        transformOrigin: 'top center'
      }
    },
    getChildNodeStyle(index) {
      const angle = 90 + (index - (this.graphChildren.length - 1) / 2) * 30
      const distance = 100
      const rad = (angle * Math.PI) / 180
      const x = Math.cos(rad) * distance
      const y = Math.sin(rad) * distance
      return {
        position: 'absolute',
        left: `calc(50% + ${x}px)`,
        top: `calc(50% + ${y}px)`,
        transform: 'translate(-50%, -50%)'
      }
    },
    downloadGraph(format) {
      // Mock download functionality
      const content = this.generateGraphContent(format)
      const blob = new Blob([content], { type: 'text/plain' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `${this.selectedClass.name}_graph.${format === 'mermaid' ? 'mmd' : format === 'drawio' ? 'drawio' : format}`
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
      URL.revokeObjectURL(url)
    },
    generateGraphContent(format) {
      if (format === 'mermaid') {
        let mermaid = 'graph TD\n'
        this.graphParents.forEach(p => {
          mermaid += `  ${p.name}[${p.name}] --> ${this.selectedClass.name}[${this.selectedClass.name}]\n`
        })
        this.graphChildren.forEach(c => {
          mermaid += `  ${this.selectedClass.name}[${this.selectedClass.name}] --> ${c.name}[${c.name}]\n`
        })
        return mermaid
      } else if (format === 'drawio') {
        return `<mxfile><diagram>${this.selectedClass.name} hierarchy</diagram></mxfile>`
      }
      return ''
    },
    
    // 右键菜单
    showContextMenu(e, node) {
      this.contextMenu = {
        show: true,
        x: e.pageX,
        y: e.pageY,
        node: node
      }
    },
    hideContextMenu() {
      this.contextMenu.show = false
    },
    addChildClass() {
      this.selectedTreeNode = this.contextMenu.node
      this.showAddClassModal = true
      this.hideContextMenu()
    },
    deleteNode() {
      this.classToDelete = this.contextMenu.node
      this.showDeleteModal = true
      this.hideContextMenu()
    },
    renameNode() {
      const newName = prompt('Enter new name:', this.contextMenu.node.name)
      if (newName) {
        this.contextMenu.node.name = newName
      }
      this.hideContextMenu()
    },
    
    // 类操作
    flattenClasses() {
      const flatten = (nodes, result = []) => {
        nodes.forEach(node => {
          result.push(node)
          if (node.children) {
            flatten(node.children, result)
          }
        })
        return result
      }
      this.availableClasses = flatten(this.classTreeData)
    },
    selectClass(cls) {
      this.selectedClass = { ...cls }
      const existingTab = this.openTabs.find(tab => tab.id === cls.id)
      if (!existingTab) {
        this.openTabs.push({
          id: cls.id,
          name: cls.name
        })
      }
      this.activeContentTab = cls.id
      this.activeClassView = 'edit'
      this.loadClassData(cls)
    },
    loadClassData(cls) {
      // Load parents
      const parent = this.findParentById(cls.id)
      this.classParents = parent ? [{ id: parent.id, name: parent.name }] : []
      
      // Load annotations
      this.classAnnotations = [
        { property: 'rdfs:label', value: cls.name, lang: 'zh' }
      ]
      
      // Load graph data
      this.graphParents = parent ? [{ id: parent.id, name: parent.name }] : []
      this.graphChildren = cls.children ? cls.children.map(c => ({ id: c.id, name: c.name })) : []
      
      // Load history data (mock)
      this.loadClassHistory(cls)
    },
    loadClassHistory(cls) {
      // Mock history data
      this.classHistory = [
        {
          id: 1,
          date: new Date(),
          revision: 142,
          action: `Moved class "${cls.name}" from 应用架构 to 核心系统`,
          author: 'bjsun07',
          timestamp: new Date(Date.now() - 480000),
          changes: [
            { type: 'remove', description: `${cls.name} <strong>SubClassOf</strong> 应用架构` },
            { type: 'add', description: `${cls.name} <strong>SubClassOf</strong> 核心系统` }
          ]
        },
        {
          id: 2,
          date: new Date(Date.now() - 86400000 * 45),
          revision: 91,
          action: `Created class "${cls.name}" as a subclass of 应用系统`,
          author: 'bjsun07',
          timestamp: new Date(Date.now() - 86400000 * 45),
          changes: [
            { type: 'add', description: `<a href="#">${cls.iri}</a> <strong>rdfs:label</strong> "${cls.name}"` },
            { type: 'add', description: `<strong>Class:</strong> ${cls.name}` },
            { type: 'add', description: `${cls.name} <strong>SubClassOf</strong> 应用架构` }
          ]
        }
      ]
      this.currentPage = 1
    },
    formatDate(date) {
      const d = new Date(date)
      return d.toLocaleDateString('en-US', { weekday: 'short', day: 'numeric', month: 'short', year: 'numeric' })
    },
    closeTab(index) {
      this.openTabs.splice(index, 1)
      if (this.openTabs.length > 0) {
        this.activeContentTab = this.openTabs[0].id
      } else {
        this.activeContentTab = null
        this.selectedClass = null
      }
    },
    saveClass() {
      console.log('Saving class:', this.selectedClass)
      alert('Class saved successfully!')
    },
    cancelEdit() {
      this.selectedClass = null
      this.activeContentTab = null
    },
    
    // Add Class
    createClasses() {
      const names = this.newClassNames.split('\n').filter(n => n.trim())
      names.forEach(name => {
        const newClass = {
          id: Date.now() + Math.random().toString(),
          name: name.trim(),
          iri: `http://example.org/${name.trim()}`,
          children: []
        }
        if (this.selectedTreeNode) {
          if (!this.selectedTreeNode.children) this.selectedTreeNode.children = []
          this.selectedTreeNode.children.push(newClass)
        } else {
          this.classTreeData[0].children.push(newClass)
        }
      })
      this.newClassNames = ''
      this.showAddClassModal = false
      this.flattenClasses()
    },
    
    // Delete Class
    deleteSelectedClass() {
      if (!this.selectedTreeNode) {
        alert('Please select a class to delete')
        return
      }
      this.classToDelete = this.selectedTreeNode
      this.showDeleteModal = true
    },
    confirmDeleteClass() {
      const deleteFromTree = (nodes, id) => {
        const index = nodes.findIndex(n => n.id === id)
        if (index > -1) {
          nodes.splice(index, 1)
          return true
        }
        for (const node of nodes) {
          if (node.children && deleteFromTree(node.children, id)) {
            return true
          }
        }
        return false
      }
      deleteFromTree(this.classTreeData, this.classToDelete.id)
      this.showDeleteModal = false
      this.classToDelete = null
      this.selectedTreeNode = null
      this.selectedClass = null
      this.flattenClasses()
    },
    
    // Search
    performSearch() {
      if (!this.searchQuery.trim()) {
        this.searchResults = []
        return
      }
      const query = this.searchQuery.toLowerCase()
      const results = []
      const searchInTree = (nodes, path = '') => {
        nodes.forEach(node => {
          const currentPath = path ? `${path} > ${node.name}` : node.name
          if (node.name.toLowerCase().includes(query)) {
            results.push({ ...node, path: currentPath })
          }
          if (node.children) {
            searchInTree(node.children, currentPath)
          }
        })
      }
      searchInTree(this.classTreeData)
      this.searchResults = results
    },
    selectSearchResult(result) {
      this.selectTreeNode(result)
      this.showSearchModal = false
      this.searchQuery = ''
      this.searchResults = []
    },
    
    // Comments
    addComment() {
      if (!this.newComment.trim()) return
      const comment = {
        id: Date.now(),
        author: this.currentUser,
        content: this.newComment,
        createdAt: new Date()
      }
      this.comments.push(comment)
      this.newComment = ''
    },
    formatTime(date) {
      const now = new Date()
      const diff = now - new Date(date)
      const minutes = Math.floor(diff / 60000)
      const hours = Math.floor(diff / 3600000)
      const days = Math.floor(diff / 86400000)
      
      if (minutes < 1) return 'Just now'
      if (minutes < 60) return `${minutes} minutes ago`
      if (hours < 24) return `${hours} hours ago`
      return `${days} days ago`
    }
  }
}
</script>

<style scoped>
.project-editor {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f5f5f5;
}

/* 顶部导航栏 */
.editor-navbar {
  display: flex;
  align-items: center;
  background-color: #4a4a4a;
  color: white;
  padding: 0 10px;
  height: 40px;
  border-bottom: 1px solid #333;
}

.project-title {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-right: 20px;
}

.project-name {
  font-weight: bold;
  font-size: 14px;
}

.home-link {
  color: #ccc;
  text-decoration: none;
  font-size: 12px;
}

.home-link:hover {
  color: white;
}

.editor-tabs {
  display: flex;
  flex: 1;
  gap: 2px;
}

.tab-btn {
  padding: 8px 15px;
  background-color: #5a5a5a;
  border: none;
  color: #ccc;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s;
}

.tab-btn:hover {
  background-color: #6a6a6a;
  color: white;
}

.tab-btn.active {
  background-color: #f5f5f5;
  color: #333;
}

.tab-btn.add-tab {
  background-color: transparent;
  color: #999;
}

.editor-actions {
  display: flex;
  align-items: center;
  gap: 15px;
  font-size: 12px;
}

.editor-actions span {
  cursor: pointer;
  color: #ccc;
}

.editor-actions span:hover {
  color: white;
}

/* 主编辑区域 */
.editor-main {
  display: flex;
  flex: 1;
  overflow: hidden;
  position: relative;
}

/* 可拖拽面板 */
.draggable-panel {
  position: relative;
  transition: none;
}

/* 左侧面板 */
.left-panel {
  width: 280px;
  background-color: white;
  border-right: 1px solid #ddd;
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 5px rgba(0,0,0,0.1);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 10px;
  background-color: #888;
  color: white;
  font-size: 12px;
  cursor: move;
  user-select: none;
}

.panel-title {
  font-weight: bold;
}

.panel-tools {
  display: flex;
  gap: 5px;
}

.tool-btn {
  background: none;
  border: none;
  color: white;
  cursor: pointer;
  font-size: 14px;
  padding: 2px 5px;
  border-radius: 3px;
  transition: background-color 0.2s;
}

.tool-btn:hover {
  background-color: rgba(255,255,255,0.2);
}

.panel-content {
  flex: 1;
  overflow: auto;
  padding: 10px;
}

/* 树形结构 */
.class-tree {
  font-size: 13px;
}

.tree-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tree-item {
  display: flex;
  align-items: center;
  padding: 3px 0;
  cursor: pointer;
  flex-wrap: wrap;
}

.tree-item.selected > .tree-label {
  background-color: #007bff;
  color: white;
}

.tree-item.dragging {
  opacity: 0.5;
}

.tree-item.dragging > .tree-label {
  background-color: #e3f2fd;
  border: 2px dashed #2196F3;
}

.tree-item.drop-target > .tree-label {
  background-color: #c8e6c9;
  border: 2px dashed #4CAF50;
}

.tree-toggle {
  width: 18px;
  text-align: center;
  color: #666;
  cursor: pointer;
  font-size: 10px;
}

.tree-toggle-placeholder {
  width: 18px;
}

.tree-icon {
  margin: 0 5px;
  font-size: 12px;
}

.tree-label {
  flex: 1;
  padding: 2px 5px;
  border-radius: 3px;
}

.tree-label:hover {
  background-color: #e3f2fd;
}

.tree-children {
  list-style: none;
  padding-left: 20px;
  margin: 0;
  width: 100%;
}

/* 中间面板 */
.center-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: white;
  min-width: 0;
}

.content-tabs {
  display: flex;
  background-color: #f0f0f0;
  border-bottom: 1px solid #ddd;
  overflow-x: auto;
}

.content-tab {
  display: flex;
  align-items: center;
  padding: 8px 15px;
  background-color: #e0e0e0;
  border-right: 1px solid #ccc;
  cursor: pointer;
  font-size: 12px;
  white-space: nowrap;
}

.content-tab.active {
  background-color: white;
  border-bottom: 1px solid white;
  margin-bottom: -1px;
}

.content-tab span {
  margin-right: 8px;
}

.close-tab {
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  font-size: 16px;
  padding: 0;
  line-height: 1;
}

.close-tab:hover {
  color: #333;
}

.content-area {
  flex: 1;
  overflow: auto;
  padding: 20px;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.empty-box {
  width: 80%;
  height: 80%;
  border: 3px dashed #ddd;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 18px;
}

.class-editor {
  max-width: 800px;
  margin: 0 auto;
}

.class-editor h2 {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #4CAF50;
}

.property-group {
  margin-bottom: 20px;
}

.property-group label {
  display: block;
  font-weight: bold;
  margin-bottom: 5px;
  color: #333;
}

/* Class Editor Styles */
.class-editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #4CAF50;
}

.class-editor-header h2 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.class-action-buttons {
  display: flex;
  gap: 5px;
}

.action-btn {
  width: 32px;
  height: 32px;
  border: 1px solid #ddd;
  background-color: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.action-btn:hover {
  background-color: #f0f0f0;
}

.action-btn.active {
  background-color: #4CAF50;
  border-color: #4CAF50;
}

.class-section {
  margin-bottom: 25px;
}

.section-label {
  display: block;
  font-weight: bold;
  color: #666;
  margin-bottom: 10px;
  font-size: 14px;
}

.iri-display {
  padding: 8px 12px;
  background-color: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 4px;
  color: #666;
  font-family: monospace;
  font-size: 13px;
}

/* Annotations */
.annotations-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.annotation-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.annotation-row.add-new {
  background-color: transparent;
  padding: 0;
}

.annotation-property {
  color: #4CAF50;
  font-size: 13px;
  min-width: 100px;
}

.annotation-lang {
  color: #999;
  font-size: 12px;
  padding: 2px 6px;
  background-color: #e0e0e0;
  border-radius: 3px;
}

.annotation-value {
  flex: 1;
}

/* Parents */
.parents-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.parent-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  background-color: #f9f9f9;
  border-radius: 4px;
  position: relative;
}

.parent-row.add-new {
  background-color: transparent;
  padding: 0;
}

.parent-radio {
  color: #2196F3;
  font-size: 16px;
}

.parent-name {
  flex: 1;
  color: #333;
}

.search-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  z-index: 100;
  max-height: 200px;
  overflow-y: auto;
}

.search-result {
  padding: 8px 12px;
  cursor: pointer;
  font-size: 13px;
}

.search-result:hover {
  background-color: #f0f0f0;
}

/* Relationships */
.relationships-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.relationship-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.relationship-row.add-new {
  background-color: transparent;
  padding: 0;
}

.lang-input {
  width: 60px;
}

.btn-icon {
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  font-size: 14px;
  padding: 4px;
}

.btn-icon:hover {
  color: #e74c3c;
}

/* Graph View */
.class-graph-view {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 200px);
}

.graph-toolbar {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 10px;
  background-color: #f5f5f5;
  border-bottom: 1px solid #ddd;
}

.toolbar-btn {
  padding: 6px 12px;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s;
}

.toolbar-btn:hover {
  background-color: #f0f0f0;
}

.toolbar-divider {
  width: 1px;
  height: 20px;
  background-color: #ddd;
  margin: 0 5px;
}

.graph-container {
  flex: 1;
  overflow: auto;
  position: relative;
  background-color: #fafafa;
}

.graph-canvas {
  width: 100%;
  height: 100%;
  position: relative;
  min-height: 400px;
}

.graph-node {
  padding: 10px 20px;
  background-color: #2196F3;
  color: white;
  border-radius: 4px;
  font-weight: bold;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
  white-space: nowrap;
}

.graph-node.root-node {
  background-color: #2196F3;
}

.edge-node {
  padding: 8px 16px;
  background-color: white;
  border: 2px solid #FFC107;
  border-radius: 4px;
  font-size: 13px;
  white-space: nowrap;
}

.edge-node.parent-node {
  border-color: #FFC107;
}

.edge-node.child-node {
  border-color: #4CAF50;
}

/* History View */
.class-history-view {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 200px);
}

.history-list {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

.history-item {
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.history-date {
  color: #666;
  font-size: 13px;
}

.history-revision {
  background-color: #e0e0e0;
  padding: 2px 8px;
  border-radius: 3px;
  font-size: 11px;
  color: #666;
}

.history-action {
  color: #333;
  margin-bottom: 8px;
  font-size: 14px;
}

.history-author {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.author-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  font-weight: bold;
}

.author-name {
  color: #2196F3;
  font-size: 13px;
}

.author-time {
  color: #999;
  font-size: 12px;
}

.history-changes {
  display: flex;
  flex-direction: column;
  gap: 5px;
  padding-left: 32px;
}

.change-detail {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 5px 10px;
  border-radius: 4px;
  font-size: 13px;
}

.change-detail.add {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.change-detail.remove {
  background-color: #ffebee;
  color: #c62828;
}

.change-detail.edit {
  background-color: #e3f2fd;
  color: #1565c0;
}

.change-icon {
  font-weight: bold;
}

.change-text a {
  color: #2196F3;
  text-decoration: none;
}

.change-text a:hover {
  text-decoration: underline;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  padding: 10px 15px;
  border-top: 1px solid #ddd;
  background-color: #f9f9f9;
}

.page-info {
  color: #666;
  font-size: 13px;
}

/* 右侧面板 */
.form-control {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.form-control:focus {
  outline: none;
  border-color: #4CAF50;
}

textarea.form-control {
  min-height: 100px;
  resize: vertical;
}

select.form-control[multiple] {
  min-height: 120px;
}

.input-group {
  display: flex;
  gap: 5px;
}

.input-group .form-control {
  flex: 1;
}

.form-text {
  display: block;
  margin-top: 5px;
  color: #666;
  font-size: 12px;
}

.editor-actions-bar {
  display: flex;
  gap: 10px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

/* 右侧面板 */
.right-panels {
  width: 320px;
  display: flex;
  flex-direction: column;
  background-color: white;
  border-left: 1px solid #ddd;
  box-shadow: -2px 0 5px rgba(0,0,0,0.1);
}

.right-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  border-bottom: 1px solid #ddd;
}

.right-panel:last-child {
  border-bottom: none;
}

/* 可拖拽分隔条 */
.resize-handle {
  height: 6px;
  background-color: #ddd;
  cursor: row-resize;
  position: relative;
  flex-shrink: 0;
  transition: background-color 0.2s;
}

.resize-handle:hover {
  background-color: #4CAF50;
}

.resize-handle::before {
  content: '';
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 30px;
  height: 2px;
  background-color: #999;
  border-radius: 1px;
}

.comments-list {
  flex: 1;
  overflow: auto;
  padding: 10px;
}

.comment-item {
  padding: 10px;
  border-bottom: 1px solid #eee;
  font-size: 12px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.comment-author {
  font-weight: bold;
  color: #4CAF50;
}

.comment-time {
  color: #999;
  font-size: 11px;
}

.comment-body {
  color: #333;
  line-height: 1.4;
}

.comment-input {
  padding: 10px;
  border-top: 1px solid #ddd;
}

.comment-input textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 12px;
  resize: vertical;
  min-height: 60px;
  margin-bottom: 8px;
}

.feed-list {
  padding: 10px;
}

.feed-item {
  display: flex;
  align-items: flex-start;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
  font-size: 12px;
}

.feed-icon {
  margin-right: 10px;
  font-size: 14px;
}

.feed-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  margin-right: 10px;
  flex-shrink: 0;
}

.feed-content {
  flex: 1;
}

.feed-action {
  color: #333;
  margin-bottom: 3px;
}

.feed-time {
  color: #999;
  font-size: 11px;
}

/* 底部状态栏 */
.editor-statusbar {
  display: flex;
  gap: 20px;
  padding: 5px 10px;
  background-color: #4a4a4a;
  color: #ccc;
  font-size: 11px;
  border-top: 1px solid #333;
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  min-width: 450px;
  max-width: 600px;
  position: absolute;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
  cursor: move;
  user-select: none;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #999;
  cursor: pointer;
}

.close-btn:hover {
  color: #333;
}

.modal-body {
  padding: 20px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 15px 20px;
  border-top: 1px solid #eee;
}

/* Delete Modal */
.delete-modal {
  min-width: 400px;
}

.delete-confirm {
  display: flex;
  align-items: center;
  gap: 20px;
}

.question-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #00bcd4, #0097a7);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  color: white;
  font-weight: bold;
}

.delete-confirm p {
  flex: 1;
  margin: 0;
  font-size: 14px;
  color: #333;
}

/* Search Modal */
.search-modal {
  min-width: 500px;
}

.search-results {
  max-height: 300px;
  overflow-y: auto;
  margin-top: 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.search-result-item {
  display: flex;
  align-items: center;
  padding: 10px;
  cursor: pointer;
  border-bottom: 1px solid #eee;
}

.search-result-item:hover {
  background-color: #f5f5f5;
}

.search-result-item:last-child {
  border-bottom: none;
}

.result-icon {
  margin-right: 10px;
}

.result-name {
  font-weight: bold;
  margin-right: 10px;
}

.result-path {
  color: #999;
  font-size: 12px;
}

/* 右键菜单 */
.context-menu {
  position: fixed;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  z-index: 1001;
  min-width: 150px;
}

.context-menu-item {
  padding: 10px 15px;
  cursor: pointer;
  font-size: 13px;
}

.context-menu-item:hover {
  background-color: #f0f0f0;
}

/* 按钮样式 */
.btn {
  padding: 8px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.btn-primary {
  background-color: #6b2c91;
  color: white;
}

.btn-primary:hover {
  background-color: #5a257a;
}

.btn-secondary {
  background-color: #95a5a6;
  color: white;
}

.btn-secondary:hover {
  background-color: #7f8c8d;
}

.btn-danger {
  background-color: #e74c3c;
  color: white;
}

.btn-danger:hover {
  background-color: #c0392b;
}

.btn-sm {
  padding: 5px 15px;
  font-size: 12px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
  color: #333;
}

/* 响应式 */
@media (max-width: 1200px) {
  .left-panel {
    width: 240px;
  }
  .right-panels {
    width: 280px;
  }
}

@media (max-width: 992px) {
  .left-panel,
  .right-panels {
    position: absolute;
    z-index: 100;
    height: 100%;
  }
  
  .left-panel {
    left: 0;
  }
  
  .right-panels {
    right: 0;
  }
}
</style>
