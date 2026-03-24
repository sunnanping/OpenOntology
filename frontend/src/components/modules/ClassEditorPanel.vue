<template>
  <div class="class-editor-panel">
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
    
    <div class="panels-wrapper" :class="{ 'mobile-view': isMobileView }">
      <!-- 左侧：Class Hierarchy 和 Description 面板 -->
      <div 
        class="left-panels"
        :style="isMobileView ? {} : { width: leftPanelDynamicWidth ? leftPanelDynamicWidth + 'px' : '0px' }"
      >
        <!-- Class Hierarchy 面板 -->
        <div 
          v-show="showClassHierarchy && (isMobileView ? activeMobilePanel === 'hierarchy' : true)" 
          class="panel class-hierarchy-panel"
          :class="{ 'mobile-active': isMobileView && activeMobilePanel === 'hierarchy' }"
          :style="isMobileView ? {} : { height: hierarchyPanelHeight }"
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
              <button class="panel-btn" title="Search" @click="toggleSearch">
                <svg class="wp-icon" viewBox="0 0 24 24" width="14" height="14">
                  <circle cx="10" cy="10" r="6" fill="none" stroke="currentColor" stroke-width="1.5"/>
                  <line x1="15" y1="15" x2="20" y2="20" stroke="currentColor" stroke-width="1.5"/>
                </svg>
              </button>
              <button class="panel-btn" title="Filter" @click="toggleFilter">
                <svg class="wp-icon" viewBox="0 0 24 24" width="14" height="14">
                  <polygon points="3,5 21,5 14,12 14,19 10,19 10,12" fill="none" stroke="currentColor" stroke-width="1.5"/>
                </svg>
              </button>
            </div>
            <button class="panel-btn close-btn" title="Close" @click="showClassHierarchy = false">
              <i class="bi bi-x-lg"></i>
            </button>
          </div>
        </div>
        
        <div v-if="showSearch" class="panel-search-box">
          <input 
            type="text" 
            class="search-input" 
            placeholder="Search classes..." 
            v-model="searchQuery"
            @keyup.enter="searchClasses"
          >
          <button class="search-btn" @click="searchClasses">
            <i class="bi bi-search"></i>
          </button>
        </div>
        
        <div class="panel-body">
          <el-tree
            :data="treeData"
            :props="treeProps"
            :default-expanded-keys="expandedKeys"
            @node-click="handleNodeClick"
            @node-expand="handleNodeExpand"
            @node-collapse="handleNodeCollapse"
            @node-contextmenu="handleNodeContextMenu"
            :draggable="true"
            :allow-drop="allowDrop"
            :allow-drag="allowDrag"
            @node-drop="handleNodeDrop"
            node-key="id"
            default-expand-all
            highlight-current
          >
            <template #default="{ node, data }">
              <span 
                class="tree-node" 
                :class="{ 'selected': selectedClass?.id === data.id }"
              >
                <i class="bi bi-circle-fill node-icon"></i>
                <span class="node-label">{{ data.name }}</span>
              </span>
            </template>
          </el-tree>
        </div>

        <!-- Context Menu -->
        <div 
          v-if="showContextMenu" 
          class="context-menu" 
          :style="{ left: contextMenuPosition.x + 'px', top: contextMenuPosition.y + 'px' }"
        >
          <div class="context-menu-item" @click="handleContextMenuAction('create')">
            <i class="bi bi-plus-circle"></i>
            <span>Create</span>
          </div>
          <div class="context-menu-item" @click="handleContextMenuAction('delete')">
            <i class="bi bi-trash"></i>
            <span>Delete</span>
          </div>
          <div class="context-menu-divider"></div>
          <div class="context-menu-item" @click="handleContextMenuAction('tags')">
            <i class="bi bi-tags"></i>
            <span>Tags...</span>
          </div>
          <div class="context-menu-item" @click="handleContextMenuAction('move')">
            <i class="bi bi-arrow-right-circle"></i>
            <span>Move...</span>
          </div>
          <div class="context-menu-item" @click="handleContextMenuAction('merge')">
            <i class="bi bi-sign-merge-right"></i>
            <span>Merge Into...</span>
          </div>
          <div class="context-menu-divider"></div>
          <div class="context-menu-item" @click="handleContextMenuAction('setAnnotation')">
            <i class="bi bi-pencil-square"></i>
            <span>Set annotation value...</span>
          </div>
          <div class="context-menu-item" @click="handleContextMenuAction('editAnnotations')">
            <i class="bi bi-journal-text"></i>
            <span>Edit annotations...</span>
          </div>
          <div class="context-menu-item" @click="handleContextMenuAction('watch')">
            <i class="bi bi-eye"></i>
            <span>Watch...</span>
          </div>
          <div class="context-menu-divider"></div>
          <div class="context-menu-item" @click="handleContextMenuAction('pruneBranch')">
            <i class="bi bi-scissors"></i>
            <span>Prune branch to root</span>
          </div>
          <div class="context-menu-item" @click="handleContextMenuAction('pruneAllBranches')">
            <i class="bi bi-tree"></i>
            <span>Prune all branches to root</span>
          </div>
          <div class="context-menu-item" @click="handleContextMenuAction('clearPruning')">
            <i class="bi bi-arrow-counterclockwise"></i>
            <span>Clear pruning</span>
          </div>
          <div class="context-menu-divider"></div>
          <div class="context-menu-item" @click="handleContextMenuAction('showIRI')">
            <i class="bi bi-link-45deg"></i>
            <span>Show IRI</span>
          </div>
          <div class="context-menu-item" @click="handleContextMenuAction('showDirectLink')">
            <i class="bi bi-box-arrow-up-right"></i>
            <span>Show Direct Link</span>
          </div>
          <div class="context-menu-item" @click="handleContextMenuAction('refreshTree')">
            <i class="bi bi-arrow-clockwise"></i>
            <span>Refresh Tree</span>
          </div>
        </div>
        
        <!-- Class Hierarchy 和 Description 之间的垂直分割线 -->
        <div class="resize-handle-v" @mousedown="startResizeLeftPanels($event)"></div>
        </div>
        
        <!-- Description 面板 -->
        <div 
          v-show="showDescription && (isMobileView ? activeMobilePanel === 'description' : true)" 
          class="panel description-panel"
          :class="{ 'mobile-active': isMobileView && activeMobilePanel === 'description' }"
          :style="isMobileView ? {} : { height: descriptionPanelHeight }"
        >
          <div class="panel-header">
            <div class="panel-header-left">
              <span class="panel-title">Description</span>
            </div>
            <div class="panel-header-right">
              <div class="panel-actions">
                <!-- 暂时隐藏这些按钮，以后可能通过按钮来管理对应的7个功能模块的数据 -->
                <button class="panel-btn" title="Add Equivalent To" @click="showAddEquivalentToModal = true" style="display: none;">
                  <i class="bi bi-plus"></i>
                </button>
                <button class="panel-btn" title="Add SubClass Of" @click="showAddSubClassOfModal = true" style="display: none;">
                  <i class="bi bi-plus"></i>
                </button>
                <button class="panel-btn" title="Add General class axioms" @click="showAddGeneralClassAxiomsModal = true" style="display: none;">
                  <i class="bi bi-plus"></i>
                </button>
              </div>
              <button class="panel-btn close-btn" title="Close" @click="showDescription = false">
                <i class="bi bi-x-lg"></i>
              </button>
            </div>
          </div>
          
          <div class="panel-body">
            <div class="description-section">
              <!-- Equivalent To -->
              <div class="description-item" 
                   :class="{ 'focused': focusedDescriptionItem === 'equivalentTo' }"
                   @click="focusedDescriptionItem = 'equivalentTo'">
                <span class="item-label">Equivalent To</span>
                <button class="item-action" title="Add Equivalent To" @click.stop="showAddEquivalentToModal = true">
                  <i class="bi bi-plus"></i>
                  <span class="add-text">Add</span>
                </button>
                <div class="item-value" v-if="selectedClass && selectedClass.equivalentTo">
                  {{ selectedClass.equivalentTo }}
                </div>
              </div>
              
              <!-- SubClass Of -->
              <div class="description-item" 
                   :class="{ 'focused': focusedDescriptionItem === 'subClassOf' }"
                   @click="focusedDescriptionItem = 'subClassOf'">
                <span class="item-label">SubClass Of</span>
                <button class="item-action" title="Add SubClass Of" @click.stop="showAddSubClassOfModal = true">
                  <i class="bi bi-plus"></i>
                  <span class="add-text">Add</span>
                </button>
                <div class="item-value" v-if="selectedClass && selectedClass.superClasses">
                  <div v-for="(superClass, index) in selectedClass.superClasses" :key="index" class="super-class-item">
                    {{ superClass }}
                    <button class="super-class-action" @click.stop="removeSuperClass(index)" :disabled="superClass === 'owl:Thing'">
                      <i class="bi bi-x"></i>
                    </button>
                  </div>
                </div>
              </div>
              
              <!-- General class axioms -->
              <div class="description-item" 
                   :class="{ 'focused': focusedDescriptionItem === 'generalAxioms' }"
                   @click="focusedDescriptionItem = 'generalAxioms'">
                <span class="item-label">General class axioms</span>
                <button class="item-action" title="Add General class axioms" @click.stop="showAddGeneralClassAxiomsModal = true">
                  <i class="bi bi-plus"></i>
                  <span class="add-text">Add</span>
                </button>
                <div class="item-value" v-if="selectedClass && selectedClass.generalAxioms">
                  <div v-for="(axiom, index) in selectedClass.generalAxioms" :key="index">
                    {{ axiom }}
                  </div>
                </div>
              </div>
              
              <!-- SubClass Of (Anonymous Assertions) -->
              <div class="description-item" 
                   :class="{ 'focused': focusedDescriptionItem === 'anonymousAssertions' }"
                   @click="focusedDescriptionItem = 'anonymousAssertions'">
                <span class="item-label">SubClass Of (Anonymous Assertions)</span>
                <div class="item-value" v-if="selectedClass && selectedClass.anonymousAssertions">
                  <div v-for="(assertion, index) in selectedClass.anonymousAssertions" :key="index">
                    {{ assertion }}
                  </div>
                </div>
              </div>
              
              <!-- Instances -->
              <div class="description-item" 
                   :class="{ 'focused': focusedDescriptionItem === 'instances' }"
                   @click="focusedDescriptionItem = 'instances'">
                <span class="item-label">Instances</span>
                <button class="item-action" title="Add Instance" @click.stop="showAddInstanceModal = true">
                  <i class="bi bi-plus"></i>
                  <span class="add-text">Add</span>
                </button>
                <div class="item-value" v-if="selectedClass && selectedClass.instances">
                  <div v-for="(instance, index) in selectedClass.instances" :key="index">
                    {{ instance }}
                  </div>
                </div>
              </div>
              
              <!-- Target for Key -->
              <div class="description-item" 
                   :class="{ 'focused': focusedDescriptionItem === 'keyTargets' }"
                   @click="focusedDescriptionItem = 'keyTargets'">
                <span class="item-label">Target for Key</span>
                <button class="item-action" title="Add Target for Key" @click.stop="showAddKeyTargetModal = true">
                  <i class="bi bi-plus"></i>
                  <span class="add-text">Add</span>
                </button>
                <div class="item-value" v-if="selectedClass && selectedClass.keyTargets">
                  <div v-for="(target, index) in selectedClass.keyTargets" :key="index">
                    {{ target }}
                  </div>
                </div>
              </div>
              
              <!-- Disjoint With -->
              <div class="description-item" 
                   :class="{ 'focused': focusedDescriptionItem === 'disjointWith' }"
                   @click="focusedDescriptionItem = 'disjointWith'">
                <span class="item-label">Disjoint With</span>
                <button class="item-action" title="Add Disjoint With" @click.stop="showAddDisjointWithModal = true">
                  <i class="bi bi-plus"></i>
                  <span class="add-text">Add</span>
                </button>
                <div class="item-value disjoint-with" v-if="selectedClass && selectedClass.disjointWith">
                  <span v-for="(disjoint, index) in selectedClass.disjointWith" :key="index" class="disjoint-item">
                    {{ disjoint }}
                    <button class="disjoint-action" @click.stop="removeDisjointWith(index)">
                      <i class="bi bi-x"></i>
                    </button>
                  </span>
                </div>
              </div>
              
              <!-- Disjoint Union Of -->
              <div class="description-item" 
                   :class="{ 'focused': focusedDescriptionItem === 'disjointUnionOf' }"
                   @click="focusedDescriptionItem = 'disjointUnionOf'">
                <span class="item-label">Disjoint Union Of</span>
                <button class="item-action" title="Add Disjoint Union Of" @click.stop="showAddDisjointUnionOfModal = true">
                  <i class="bi bi-plus"></i>
                  <span class="add-text">Add</span>
                </button>
                <div class="item-value" v-if="selectedClass && selectedClass.disjointUnionOf">
                  <div v-for="(union, index) in selectedClass.disjointUnionOf" :key="index">
                    {{ union }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 左侧面板区域的分割线 - 覆盖整个left-panels区域 -->
        <div class="resize-handle-h" @mousedown="startResizeLeft($event)"></div>
      </div>

      <!-- 中间：Class 详情面板 -->
      <div 
        v-show="showClassDetails && (isMobileView ? activeMobilePanel === 'details' : true)" 
        class="panel class-details-panel"
        :class="{ 'mobile-active': isMobileView && activeMobilePanel === 'details' }"
        :style="isMobileView ? {} : { width: middlePanelDynamicWidth }"
      >
        <div class="panel-header">
          <div class="panel-header-left">
            <span class="panel-title">{{ selectedClass ? `Class: ${selectedClass.name}` : 'Class' }}</span>
          </div>
          <div class="panel-header-right">
            <div class="panel-actions">
              <button 
                class="panel-btn" 
                :class="{ 'active': activeClassTab === 'details' }" 
                title="Details" 
                @click="activeClassTab = 'details'"
              >
                <svg class="wp-icon" viewBox="0 0 24 24" width="14" height="14">
                  <rect x="4" y="4" width="16" height="16" fill="none" stroke="currentColor" stroke-width="1.5"/>
                  <line x1="8" y1="9" x2="16" y2="9" stroke="currentColor" stroke-width="1"/>
                  <line x1="8" y1="13" x2="16" y2="13" stroke="currentColor" stroke-width="1"/>
                  <line x1="8" y1="17" x2="12" y2="17" stroke="currentColor" stroke-width="1"/>
                </svg>
              </button>
              <button 
                class="panel-btn" 
                :class="{ 'active': activeClassTab === 'graph' }" 
                title="Entity Graph" 
                @click="activeClassTab = 'graph'"
              >
                <svg class="wp-icon" viewBox="0 0 24 24" width="14" height="14">
                  <circle cx="6" cy="6" r="2.5" fill="currentColor"/>
                  <circle cx="18" cy="6" r="2.5" fill="currentColor"/>
                  <circle cx="6" cy="18" r="2.5" fill="currentColor"/>
                  <circle cx="18" cy="18" r="2.5" fill="currentColor"/>
                  <line x1="8.5" y1="6" x2="15.5" y2="6" stroke="currentColor" stroke-width="1"/>
                  <line x1="6" y1="8.5" x2="6" y2="15.5" stroke="currentColor" stroke-width="1"/>
                  <line x1="18" y1="8.5" x2="18" y2="15.5" stroke="currentColor" stroke-width="1"/>
                </svg>
              </button>
              <button 
                class="panel-btn" 
                :class="{ 'active': activeClassTab === 'changes' }" 
                title="Changes" 
                @click="activeClassTab = 'changes'"
              >
                <svg class="wp-icon" viewBox="0 0 24 24" width="14" height="14">
                  <polyline points="22,4 22,9 17,9" fill="none" stroke="currentColor" stroke-width="1.5"/>
                  <path d="M20.5 14a8.5 8.5 0 1 1-2-8.8L22 9" fill="none" stroke="currentColor" stroke-width="1.5"/>
                </svg>
              </button>
            </div>
            <button class="panel-btn close-btn" title="Close" @click="showClassDetails = false">
              <i class="bi bi-x-lg"></i>
            </button>
          </div>
        </div>
        
        <div class="panel-body" v-if="selectedClass && activeClassTab === 'details'">
          <div class="detail-section">
            <h6 class="section-title">IRI</h6>
            <div class="iri-display">{{ selectedClass.iri || `http://www.w3.org/2002/07/owl#${selectedClass.name}` }}</div>
          </div>
          
          <div class="detail-section">
            <h6 class="section-title">Annotations</h6>
            <div class="data-list">
              <div v-for="(ann, index) in selectedClass.annotations || []" :key="index" class="data-item">
                <span class="item-predicate">{{ ann.predicate }}</span>
                <span class="item-value">{{ ann.value }}</span>
                <button class="item-action" @click="editAnnotation(index)">
                  <i class="bi bi-pencil"></i>
                </button>
              </div>
              <div v-if="!selectedClass.annotations || selectedClass.annotations.length === 0" class="data-item">
                <span class="item-predicate">rdfs:label</span>
                <span class="item-value">{{ selectedClass.name }}</span>
                <button class="item-action" @click="editLabel">
                  <i class="bi bi-pencil"></i>
                </button>
              </div>
              <button class="btn-add" @click="showAddAnnotationModal = true">
                <i class="bi bi-plus"></i> Add Annotation
              </button>
            </div>
          </div>
          
          <div class="detail-section">
            <h6 class="section-title">Parents</h6>
            <div class="data-list">
              <div v-for="(parent, index) in selectedClass.superClasses || []" :key="index" class="data-item">
                <span class="item-value">{{ parent }}</span>
                <button class="item-action delete" @click="removeParent(index)" :disabled="parent === 'owl:Thing'">
                  <i class="bi bi-x"></i>
                </button>
              </div>
              <button v-if="!selectedClass.superClasses || selectedClass.superClasses.length === 0" class="btn-add" @click="showAddParentModal = true">
                <i class="bi bi-plus"></i> Add Parent
              </button>
            </div>
          </div>
          
          <div class="detail-section">
            <h6 class="section-title">Relationships</h6>
            <div class="data-list">
              <div v-for="(rel, index) in selectedClass.relationships || []" :key="index" class="data-item">
                <span class="item-predicate">{{ rel.property }}</span>
                <span class="item-value">{{ rel.target }}</span>
                <button class="item-action delete" @click="removeRelationship(index)">
                  <i class="bi bi-x"></i>
                </button>
              </div>
              <button class="btn-add" @click="showAddRelationshipModal = true">
                <i class="bi bi-plus"></i> Add Relationship
              </button>
            </div>
          </div>
        </div>
        
        <div class="panel-body" v-else-if="selectedClass && activeClassTab === 'graph'">
          <div class="graph-container">
            <div ref="graphChart" class="graph-chart"></div>
          </div>
        </div>
        
        <div class="panel-body" v-else-if="selectedClass && activeClassTab === 'changes'">
          <div class="changes-list">
            <div v-for="change in classChanges" :key="change.id" class="change-item">
              <div class="change-header">
                <span class="change-author">{{ change.author }}</span>
                <span class="change-time">{{ formatDate(change.timestamp) }}</span>
              </div>
              <div class="change-content">{{ change.description }}</div>
            </div>
            <div v-if="!classChanges || classChanges.length === 0" class="empty-message">
              No changes recorded for this class
            </div>
          </div>
        </div>
        
        <div class="panel-body empty-state" v-else>
          <div class="nothing-selected">Nothing selected</div>
        </div>
        
        <div class="resize-handle-h" @mousedown="startResizeMiddle($event)"></div>
      </div>

      <!-- 右侧：Comments 和 Project Feed 面板 -->
      <div 
        class="right-panels"
        v-show="!isMobileView || (activeMobilePanel === 'comments' || activeMobilePanel === 'feed')"
        :style="isMobileView ? {} : { width: rightPanelDynamicWidth ? rightPanelDynamicWidth + 'px' : '0px' }"
      >
        <div 
          v-show="showComments && (isMobileView ? activeMobilePanel === 'comments' : true)" 
          class="panel comments-panel"
          :class="{ 'mobile-active': isMobileView && activeMobilePanel === 'comments' }"
          :style="isMobileView ? {} : { height: commentsPanelHeight }"
        >
          <div class="panel-header">
            <div class="panel-header-left">
              <span class="panel-title">{{ selectedClass ? `Comments: ${selectedClass.name}` : 'Comments' }}</span>
            </div>
            <div class="panel-header-right">
              <div class="panel-actions">
                <button class="panel-btn" title="Start new thread" @click="showCreateCommentModal = true">
                  <svg class="wp-icon" viewBox="0 0 24 24" width="14" height="14">
                    <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" fill="none" stroke="currentColor" stroke-width="1.5"/>
                    <line x1="8" y1="10" x2="16" y2="10" stroke="currentColor" stroke-width="1.5"/>
                    <line x1="8" y1="14" x2="12" y2="14" stroke="currentColor" stroke-width="1.5"/>
                  </svg>
                </button>
                <button class="panel-btn" title="Filter" @click="toggleCommentsFilter">
                  <svg class="wp-icon" viewBox="0 0 24 24" width="14" height="14">
                    <polygon points="3,5 21,5 14,12 14,19 10,19 10,12" fill="none" stroke="currentColor" stroke-width="1.5"/>
                  </svg>
                </button>
              </div>
              <button class="panel-btn close-btn" title="Close" @click="showComments = false">
                <i class="bi bi-x-lg"></i>
              </button>
            </div>
          </div>
          <div class="panel-body">
            <div v-if="filteredComments && filteredComments.length > 0" class="comments-list">
              <div v-for="comment in filteredComments" :key="comment.id" class="comment-item">
                <div class="comment-header">
                  <span class="comment-author">{{ comment.author }}</span>
                  <span class="comment-time">{{ formatDate(comment.createdAt) }}</span>
                </div>
                <div class="comment-content">{{ comment.content }}</div>
                <div v-if="comment.replies && comment.replies.length > 0" class="comment-replies">
                  <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                    <span class="reply-author">{{ reply.author }}:</span>
                    <span class="reply-content">{{ reply.content }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="empty-message">
              <button class="btn-add" @click="showCreateCommentModal = true">
                <svg class="wp-icon" viewBox="0 0 24 24" width="12" height="12">
                  <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" fill="none" stroke="currentColor" stroke-width="1.5"/>
                  <line x1="8" y1="10" x2="16" y2="10" stroke="currentColor" stroke-width="1.5"/>
                  <line x1="8" y1="14" x2="12" y2="14" stroke="currentColor" stroke-width="1.5"/>
                </svg>
                Start new thread
              </button>
            </div>
          </div>
          <div class="resize-handle-v" @mousedown="startResize($event)"></div>
        </div>
        
        <div 
          v-show="showProjectFeed && (isMobileView ? activeMobilePanel === 'feed' : true)" 
          class="panel project-feed-panel"
          :class="{ 'mobile-active': isMobileView && activeMobilePanel === 'feed' }"
          :style="isMobileView ? {} : { height: projectFeedPanelHeight }"
        >
          <div class="panel-header">
            <div class="panel-header-left">
              <span class="panel-title">Project Feed</span>
            </div>
            <div class="panel-header-right">
              <button class="panel-btn close-btn" title="Close" @click="showProjectFeed = false">
                <i class="bi bi-x-lg"></i>
              </button>
            </div>
          </div>
          <div class="panel-body">
            <div v-if="projectActivities && projectActivities.length > 0" class="activities-list">
              <div v-for="activity in projectActivities" :key="activity.id" class="activity-item">
                <div class="activity-header">
                  <span class="activity-author">{{ activity.author }}</span>
                  <span class="activity-time">{{ formatDate(activity.timestamp) }}</span>
                </div>
                <div class="activity-content">{{ activity.action }}</div>
                <div class="activity-entity">{{ activity.entityType }}: {{ activity.entityName }}</div>
              </div>
            </div>
            <div v-else class="empty-message">
              No activities yet
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 恢复面板按钮 -->
    <div 
      class="restore-panels" 
      v-if="!showClassHierarchy || !showClassDetails || !showDescription || !showComments || !showProjectFeed"
      :style="restorePanelsStyle"
      @mousedown="startDragRestorePanels"
    >
      <div class="restore-panels-drag-handle" title="拖动移动">
        <i class="bi bi-grip-horizontal"></i>
      </div>
      <button v-if="!showClassHierarchy" class="btn-restore" @click="showClassHierarchy = true">
        <i class="bi bi-diagram-3"></i>
        <span>Class Hierarchy</span>
      </button>
      <button v-if="!showDescription" class="btn-restore" @click="showDescription = true">
        <i class="bi bi-file-text"></i>
        <span>Description</span>
      </button>
      <button v-if="!showClassDetails" class="btn-restore" @click="showClassDetails = true">
        <i class="bi bi-file-earmark-text"></i>
        <span>Class</span>
      </button>
      <button v-if="!showComments" class="btn-restore" @click="showComments = true">
        <i class="bi bi-chat-left-text"></i>
        <span>Comments</span>
      </button>
      <button v-if="!showProjectFeed" class="btn-restore" @click="showProjectFeed = true">
        <i class="bi bi-activity"></i>
        <span>Project Feed</span>
      </button>
    </div>

    <!-- Create Class Modal -->
    <div class="modal fade" :class="{ 'show': showCreateClassModal }" tabindex="-1" v-if="showCreateClassModal">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Create Class</h5>
            <button type="button" class="btn-close" @click="showCreateClassModal = false"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="handleCreateClass">
              <div class="mb-3">
                <label for="className" class="form-label">Class Name</label>
                <input type="text" class="form-control form-control-sm" id="className" v-model="createClassForm.name" required>
              </div>
              <div class="mb-3">
                <label for="languageTag" class="form-label">Language Tag</label>
                <div class="input-group input-group-sm">
                  <input 
                    type="text" 
                    class="form-control" 
                    id="languageTag" 
                    v-model="createClassForm.languageTag"
                    :placeholder="projectDefaultLanguage"
                  >
                  <button type="button" class="btn btn-outline-secondary" @click="resetLanguageTag">Reset</button>
                </div>
                <div class="form-text text-muted small">Leave blank for no language tag. Press 'Reset' to reset to project default.</div>
              </div>
              <div class="mb-3">
                <label for="classIri" class="form-label">IRI</label>
                <input type="text" class="form-control form-control-sm" id="classIri" v-model="createClassForm.iri" :placeholder="generateIriPlaceholder()">
              </div>
              <div class="mb-3">
                <label for="parentClass" class="form-label">Parent Class</label>
                <select class="form-select form-select-sm" id="parentClass" v-model="createClassForm.parentId">
                  <option value="owl:Thing">owl:Thing</option>
                  <option v-for="node in classHierarchy" :key="node.id" :value="node.id">
                    {{ node.name }}
                  </option>
                </select>
              </div>
              <div class="d-flex gap-2">
                <button type="submit" class="btn btn-primary btn-sm">Create</button>
                <button type="button" class="btn btn-secondary btn-sm" @click="resetCreateClassForm">Reset</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- Create Comment Modal -->
    <div class="modal fade" :class="{ 'show': showCreateCommentModal }" tabindex="-1" v-if="showCreateCommentModal">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Start New Thread</h5>
            <button type="button" class="btn-close" @click="showCreateCommentModal = false"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="handleCreateComment">
              <div class="mb-3">
                <label for="commentContent" class="form-label">Comment</label>
                <textarea class="form-control form-control-sm" id="commentContent" v-model="createCommentForm.content" rows="4" required></textarea>
              </div>
              <button type="submit" class="btn btn-primary btn-sm">Post</button>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- Add Annotation Modal -->
    <div class="modal fade" :class="{ 'show': showAddAnnotationModal }" tabindex="-1" v-if="showAddAnnotationModal">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Add Annotation</h5>
            <button type="button" class="btn-close" @click="showAddAnnotationModal = false"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="handleAddAnnotation">
              <div class="mb-3">
                <label for="annotationPredicate" class="form-label">Predicate</label>
                <input type="text" class="form-control form-control-sm" id="annotationPredicate" v-model="newAnnotation.predicate" placeholder="e.g., rdfs:comment">
              </div>
              <div class="mb-3">
                <label for="annotationValue" class="form-label">Value</label>
                <input type="text" class="form-control form-control-sm" id="annotationValue" v-model="newAnnotation.value" required>
              </div>
              <button type="submit" class="btn btn-primary btn-sm">Add</button>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- Add Parent Modal -->
    <div class="modal fade" :class="{ 'show': showAddParentModal }" tabindex="-1" v-if="showAddParentModal">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Add Parent Class</h5>
            <button type="button" class="btn-close" @click="showAddParentModal = false"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="handleAddParent">
              <div class="mb-3">
                <label for="parentClassSelect" class="form-label">Select Parent Class</label>
                <select class="form-select form-select-sm" id="parentClassSelect" v-model="newParent" required>
                  <option v-for="node in availableParents" :key="node.id" :value="node.name">
                    {{ node.name }}
                  </option>
                </select>
              </div>
              <button type="submit" class="btn btn-primary btn-sm">Add</button>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- Add Relationship Modal -->
    <div class="modal fade" :class="{ 'show': showAddRelationshipModal }" tabindex="-1" v-if="showAddRelationshipModal">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Add Relationship</h5>
            <button type="button" class="btn-close" @click="showAddRelationshipModal = false"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="handleAddRelationship">
              <div class="mb-3">
                <label for="relationshipProperty" class="form-label">Property</label>
                <select class="form-select form-select-sm" id="relationshipProperty" v-model="newRelationship.property" required>
                  <option v-for="prop in availableProperties" :key="prop.id" :value="prop.name">
                    {{ prop.name }}
                  </option>
                </select>
              </div>
              <div class="mb-3">
                <label for="relationshipTarget" class="form-label">Target</label>
                <select class="form-select form-select-sm" id="relationshipTarget" v-model="newRelationship.target" required>
                  <option v-for="cls in availableClasses" :key="cls.id" :value="cls.name">
                    {{ cls.name }}
                  </option>
                </select>
              </div>
              <button type="submit" class="btn btn-primary btn-sm">Add</button>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- Tags Modal -->
    <div class="modal fade" :class="{ 'show': showTagsModal }" tabindex="-1" v-if="showTagsModal">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Manage Tags - {{ contextMenuNode?.name }}</h5>
            <button type="button" class="btn-close" @click="showTagsModal = false"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">Current Tags</label>
              <div class="tag-list">
                <span v-for="(tag, index) in classTags" :key="index" class="tag-item">
                  {{ tag }}
                  <i class="bi bi-x" @click="removeTag(index)"></i>
                </span>
                <span v-if="classTags.length === 0" class="text-muted">No tags</span>
              </div>
            </div>
            <div class="mb-3">
              <label class="form-label">Add New Tag</label>
              <div class="input-group input-group-sm">
                <input type="text" class="form-control" v-model="newTag" placeholder="Enter tag name">
                <button class="btn btn-primary" type="button" @click="addTag">Add</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Move Modal -->
    <div class="modal fade" :class="{ 'show': showMoveModal }" tabindex="-1" v-if="showMoveModal">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Move</h5>
            <button type="button" class="btn-close" @click="showMoveModal = false"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="handleMoveClass">
              <div class="mb-3">
                <label class="form-label">New parent</label>
                <div class="input-group input-group-sm mb-2">
                  <button 
                    type="button" 
                    class="btn btn-outline-secondary"
                    title="Classes"
                    @click="showClassesList"
                  >
                    <i class="bi bi-diagram-3"></i>
                  </button>
                  <button 
                    type="button" 
                    class="btn btn-outline-secondary"
                    title="Move up"
                    @click="moveSelectionUp"
                  >
                    <i class="bi bi-arrow-up"></i>
                  </button>
                  <button 
                    type="button" 
                    class="btn btn-outline-secondary"
                    title="Move down"
                    @click="moveSelectionDown"
                  >
                    <i class="bi bi-arrow-down"></i>
                  </button>
                  <button 
                    type="button" 
                    class="btn btn-outline-secondary"
                    title="Refresh"
                    @click="refreshClassesList"
                  >
                    <i class="bi bi-arrow-clockwise"></i>
                  </button>
                </div>
                <div class="input-group input-group-sm">
                  <input 
                    type="text" 
                    class="form-control form-control-sm" 
                    v-model="moveTargetParent" 
                    placeholder="Type in a class name" 
                    required
                    @input="searchClassesForMove"
                  >
                  <button 
                    type="button" 
                    class="btn btn-outline-secondary"
                    title="Clear"
                    @click="clearMoveSearch"
                  >
                    <i class="bi bi-x"></i>
                  </button>
                  <button 
                    type="button" 
                    class="btn btn-outline-secondary"
                    title="Search"
                    @click="searchClassesForMove"
                  >
                    <i class="bi bi-search"></i>
                  </button>
                </div>
                <div class="form-text mt-1">
                  Specify the class that should be the new parent. The selected classes will be moved from their current parent to this new parent.
                </div>
                <!-- 搜索结果下拉 -->
                <div class="search-results" v-if="moveSearchResults.length > 0">
                  <div 
                    v-for="cls in moveSearchResults" 
                    :key="cls.id" 
                    class="search-result-item"
                    @click="selectMoveTarget(cls)"
                  >
                    {{ cls.name }}
                  </div>
                </div>
              </div>
              <div class="d-flex justify-content-end gap-2">
                <button type="button" class="btn btn-secondary btn-sm" @click="showMoveModal = false">Cancel</button>
                <button type="submit" class="btn btn-primary btn-sm">Move</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- Merge Modal -->
    <div class="modal fade" :class="{ 'show': showMergeModal }" tabindex="-1" v-if="showMergeModal">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Merge Entities</h5>
            <button type="button" class="btn-close" @click="showMergeModal = false"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="handleMergeClass">
              <div class="mb-4">
                <p class="text-sm">
                  This action will merges the selected entities into a target entity. This will replace usages of the selected entities with the target entity and will transfer annotations and relationships from the merged entities to the target entity. To continue, specify the entity that you want to merge into.
                </p>
              </div>
              <div class="mb-3">
                <label class="form-label">Target</label>
                <div class="input-group input-group-sm mb-2">
                  <button 
                    type="button" 
                    class="btn btn-outline-secondary"
                    title="Classes"
                    @click="toggleMergeClassesTree"
                  >
                    <i class="bi bi-diagram-3"></i>
                  </button>
                  <button 
                    type="button" 
                    class="btn btn-outline-secondary"
                    title="Move up"
                    @click="moveMergeSelectionUp"
                  >
                    <i class="bi bi-arrow-up"></i>
                  </button>
                  <button 
                    type="button" 
                    class="btn btn-outline-secondary"
                    title="Move down"
                    @click="moveMergeSelectionDown"
                  >
                    <i class="bi bi-arrow-down"></i>
                  </button>
                  <button 
                    type="button" 
                    class="btn btn-outline-secondary"
                    title="Refresh"
                    @click="refreshMergeClassesList"
                  >
                    <i class="bi bi-arrow-clockwise"></i>
                  </button>
                </div>
                <!-- Classes Tree -->
                <div class="classes-tree" v-if="showMergeClassesTreeFlag" style="border: 1px solid #ced4da; border-radius: 0.25rem; max-height: 200px; overflow-y: auto; margin-top: 8px;">
                  <el-tree
                    :data="classHierarchy"
                    node-key="id"
                    :props="treeProps"
                    @node-click="handleMergeTreeNodeClick"
                    default-expand-all
                  />
                </div>
                <div class="input-group input-group-sm">
                  <input 
                    type="text" 
                    class="form-control form-control-sm" 
                    v-model="mergeTargetParent" 
                    placeholder="Type in a class name" 
                    required
                    @input="searchClassesForMerge"
                  >
                  <button 
                    type="button" 
                    class="btn btn-outline-secondary"
                    title="Clear"
                    @click="clearMergeSearch"
                  >
                    <i class="bi bi-x"></i>
                  </button>
                  <button 
                    type="button" 
                    class="btn btn-outline-secondary"
                    title="Search"
                    @click="searchClassesForMerge"
                  >
                    <i class="bi bi-search"></i>
                  </button>
                </div>
                <!-- 搜索结果下拉 -->
                <div class="search-results" v-if="mergeSearchResults.length > 0">
                  <div 
                    v-for="cls in mergeSearchResults" 
                    :key="cls.id" 
                    class="search-result-item"
                    @click="selectMergeTarget(cls)"
                  >
                    {{ cls.name }}
                  </div>
                </div>
              </div>
              <div class="d-flex justify-content-end gap-2">
                <button type="button" class="btn btn-secondary" @click="showMergeModal = false">Cancel</button>
                <button type="submit" class="btn btn-primary" style="background-color: #7b1fa2; border-color: #7b1fa2;">Merge Entities</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- Set Annotation Modal -->
    <div class="modal fade" :class="{ 'show': showSetAnnotationModal }" tabindex="-1" v-if="showSetAnnotationModal">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Set Annotation - {{ contextMenuNode?.name }}</h5>
            <button type="button" class="btn-close" @click="showSetAnnotationModal = false"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="handleSetAnnotation">
              <div class="mb-3">
                <label class="form-label">Annotation Property</label>
                <select class="form-select form-select-sm" v-model="setAnnotationForm.property" required>
                  <option value="rdfs:label">rdfs:label</option>
                  <option value="rdfs:comment">rdfs:comment</option>
                  <option value="dc:description">dc:description</option>
                  <option value="skos:definition">skos:definition</option>
                </select>
              </div>
              <div class="mb-3">
                <label class="form-label">Language</label>
                <select class="form-select form-select-sm" v-model="setAnnotationForm.language">
                  <option value="">No language tag</option>
                  <option value="en">English (en)</option>
                  <option value="zh">Chinese (zh)</option>
                  <option value="de">German (de)</option>
                  <option value="fr">French (fr)</option>
                </select>
              </div>
              <div class="mb-3">
                <label class="form-label">Value</label>
                <textarea class="form-control form-control-sm" v-model="setAnnotationForm.value" rows="3" required></textarea>
              </div>
              <div class="d-flex justify-content-end gap-2">
                <button type="button" class="btn btn-secondary btn-sm" @click="showSetAnnotationModal = false">Cancel</button>
                <button type="submit" class="btn btn-primary btn-sm">Set</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- Edit Annotations Modal -->
    <div class="modal fade" :class="{ 'show': showEditAnnotationsModal }" tabindex="-1" v-if="showEditAnnotationsModal">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Edit Annotations - {{ contextMenuNode?.name }}</h5>
            <button type="button" class="btn-close" @click="showEditAnnotationsModal = false"></button>
          </div>
          <div class="modal-body">
            <div class="edit-annotations-container">
              <table class="table table-sm">
                <thead>
                  <tr>
                    <th>Property</th>
                    <th>Language</th>
                    <th>Value</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(ann, index) in editAnnotationsList" :key="index">
                    <td>
                      <select class="form-select form-select-sm" v-model="ann.property">
                        <option value="rdfs:label">rdfs:label</option>
                        <option value="rdfs:comment">rdfs:comment</option>
                        <option value="dc:description">dc:description</option>
                        <option value="skos:definition">skos:definition</option>
                      </select>
                    </td>
                    <td>
                      <select class="form-select form-select-sm" v-model="ann.language">
                        <option value="">-</option>
                        <option value="en">en</option>
                        <option value="zh">zh</option>
                        <option value="de">de</option>
                        <option value="fr">fr</option>
                      </select>
                    </td>
                    <td>
                      <input type="text" class="form-control form-control-sm" v-model="ann.value">
                    </td>
                    <td>
                      <button class="btn btn-sm btn-outline-danger" @click="removeEditAnnotation(index)">
                        <i class="bi bi-trash"></i>
                      </button>
                    </td>
                  </tr>
                </tbody>
              </table>
              <button class="btn btn-sm btn-outline-primary" @click="addEditAnnotation">
                <i class="bi bi-plus"></i> Add Annotation
              </button>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary btn-sm" @click="showEditAnnotationsModal = false">Cancel</button>
            <button type="button" class="btn btn-primary btn-sm" @click="saveEditAnnotations">Save</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Direct Link Modal -->
    <div class="modal fade" :class="{ 'show': showDirectLinkModal }" tabindex="-1" v-if="showDirectLinkModal">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Direct Link</h5>
            <button type="button" class="btn-close" @click="showDirectLinkModal = false"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label class="form-label">Link</label>
              <input type="text" class="form-control form-control-sm" v-model="directLink" readonly>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-primary btn-sm" @click="copyDirectLink">Copy</button>
            <button type="button" class="btn btn-secondary btn-sm" @click="showDirectLinkModal = false">OK</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Watch Modal -->
    <div class="modal fade" :class="{ 'show': showWatchModal }" tabindex="-1" v-if="showWatchModal">
      <div class="modal-dialog modal-sm">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Watches</h5>
            <button type="button" class="btn-close" @click="showWatchModal = false"></button>
          </div>
          <div class="modal-body">
            <div class="watch-options">
              <div class="watch-option">
                <input type="radio" id="watch-none" v-model="watchOption" value="none">
                <label for="watch-none">None</label>
              </div>
              <div class="watch-option">
                <input type="radio" id="watch-entity" v-model="watchOption" value="entity">
                <label for="watch-entity">Entity</label>
              </div>
              <div class="watch-option">
                <input type="radio" id="watch-branch" v-model="watchOption" value="branch">
                <label for="watch-branch">Branch</label>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary btn-sm" @click="showWatchModal = false">Cancel</button>
            <button type="button" class="btn btn-primary btn-sm" @click="handleWatchSubmit">OK</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch, nextTick } from 'vue'
import * as echarts from 'echarts'
import http from '@/utils/http'

const props = defineProps({
  projectId: {
    type: String,
    required: true
  },
  projectInfo: {
    type: Object,
    default: () => ({})
  },
  projectDataRecord: {
    type: Object,
    default: () => null
  },
  userInfo: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['class-selected', 'class-created', 'class-deleted'])

// 响应式布局相关
const windowWidth = ref(window.innerWidth)
const windowHeight = ref(window.innerHeight)
const activeMobilePanel = ref('hierarchy')

const availablePanels = [
  { id: 'hierarchy', label: 'Class Hierarchy', shortLabel: 'Hierarchy', icon: 'bi bi-diagram-3' },
  { id: 'description', label: 'Description', shortLabel: 'Description', icon: 'bi bi-file-text' },
  { id: 'details', label: 'Class Details', shortLabel: 'Class', icon: 'bi bi-file-earmark-text' },
  { id: 'comments', label: 'Comments', shortLabel: 'Comments', icon: 'bi bi-chat-left-text' },
  { id: 'feed', label: 'Project Feed', shortLabel: 'Feed', icon: 'bi bi-activity' }
]

const isMobileView = computed(() => windowWidth.value < 992)

// 面板显示状态
const showClassHierarchy = ref(true)
const showDescription = ref(true)
const showClassDetails = ref(true)
const showComments = ref(true)
const showProjectFeed = ref(true)

// Description面板相关模态框状态
const showAddEquivalentToModal = ref(false)
const showAddSubClassOfModal = ref(false)
const showAddGeneralClassAxiomsModal = ref(false)
const showAddInstanceModal = ref(false)
const showAddKeyTargetModal = ref(false)
const showAddDisjointWithModal = ref(false)
const showAddDisjointUnionOfModal = ref(false)

// Description面板聚焦状态
const focusedDescriptionItem = ref(null)

// 右键菜单状态
const showContextMenu = ref(false)
const contextMenuPosition = ref({ x: 0, y: 0 })
const contextMenuNode = ref(null)

// 面板宽度（默认占比：Class Hierarchy 1/4, Class 2/4, 右侧面板 1/4）
const leftPanelWidth = ref(0)
const middlePanelWidth = ref(0)
const rightPanelWidth = ref(0)
const commentsHeight = ref(0)
const hierarchyHeight = ref(0)

// 动态计算左侧面板高度
const hierarchyPanelHeight = computed(() => {
  if (!showClassHierarchy.value && !showDescription.value) {
    return 0
  }
  if (!showClassHierarchy.value) {
    return 0
  }
  if (!showDescription.value) {
    return '100%'
  }
  return hierarchyHeight.value + 'px'
})

const descriptionPanelHeight = computed(() => {
  if (!showClassHierarchy.value && !showDescription.value) {
    return 0
  }
  if (!showDescription.value) {
    return 0
  }
  if (!showClassHierarchy.value) {
    return '100%'
  }
  return `calc(100% - ${hierarchyHeight.value}px)`
})

// 动态计算右侧面板高度
const commentsPanelHeight = computed(() => {
  if (!showComments.value && !showProjectFeed.value) {
    return 0
  }
  if (!showComments.value) {
    return 0
  }
  if (!showProjectFeed.value) {
    return '100%'
  }
  return commentsHeight.value + 'px'
})

const projectFeedPanelHeight = computed(() => {
  if (!showComments.value && !showProjectFeed.value) {
    return 0
  }
  if (!showProjectFeed.value) {
    return 0
  }
  if (!showComments.value) {
    return '100%'
  }
  return `calc(100% - ${commentsHeight.value}px)`
})

// 动态计算三列布局宽度
// 规则：当某个区域的面板都被最小化时，按照从右到左的顺序，由剩余的区域来填满
const leftPanelDynamicWidth = computed(() => {
  // 如果左侧没有面板显示，宽度为0
  if (!showClassHierarchy.value && !showDescription.value) {
    return 0
  }
  return leftPanelWidth.value
})

const rightPanelDynamicWidth = computed(() => {
  // 如果右侧没有面板显示，宽度为0
  if (!showComments.value && !showProjectFeed.value) {
    return 0
  }
  return rightPanelWidth.value
})

const middlePanelDynamicWidth = computed(() => {
  // 如果左侧和右侧都隐藏了，中间面板填满剩余空间
  const leftVisible = showClassHierarchy.value || showDescription.value
  const rightVisible = showComments.value || showProjectFeed.value
  
  if (!leftVisible && !rightVisible) {
    return '100%'
  }
  if (!leftVisible) {
    // 左侧隐藏，中间扩展填满左侧空间
    return `calc(100% - ${rightPanelWidth.value}px)`
  }
  if (!rightVisible) {
    // 右侧隐藏，中间扩展填满右侧空间
    return `calc(100% - ${leftPanelWidth.value}px)`
  }
  // 都显示时保持原宽度
  return middlePanelWidth.value + 'px'
})

// 恢复面板按钮位置
const restorePanelsPosition = ref({ x: 0, y: 0 })
const isDraggingRestorePanels = ref(false)

// 恢复面板样式计算
const restorePanelsStyle = computed(() => {
  if (restorePanelsPosition.value.x === 0 && restorePanelsPosition.value.y === 0) {
    return {} // 使用默认样式
  }
  return {
    left: `${restorePanelsPosition.value.x}px`,
    top: `${restorePanelsPosition.value.y}px`,
    right: 'auto',
    bottom: 'auto',
    transform: 'none'
  }
})

// 拖动恢复面板
const startDragRestorePanels = (e) => {
  // 只有点击拖动把手时才启动拖动
  if (!e.target.closest('.restore-panels-drag-handle')) {
    return
  }
  
  isDraggingRestorePanels.value = true
  const startX = e.clientX
  const startY = e.clientY
  
  // 获取Classes页面容器的位置和尺寸
  const classEditorPanel = document.querySelector('.class-editor-panel')
  if (!classEditorPanel) return
  
  const panelRect = classEditorPanel.getBoundingClientRect()
  
  // 获取恢复面板当前位置
  const restorePanels = document.querySelector('.restore-panels')
  if (!restorePanels) return
  
  const restoreRect = restorePanels.getBoundingClientRect()
  
  // 如果是第一次拖动（使用默认居中样式），记录当前实际位置
  let currentX = restorePanelsPosition.value.x
  let currentY = restorePanelsPosition.value.y
  
  if (currentX === 0 && currentY === 0) {
    // 使用实际位置（相对于视窗）
    currentX = restoreRect.left
    currentY = restoreRect.top
  }
  
  // 禁用文本选择
  document.body.classList.add('no-select')
  
  const handleMouseMove = (e) => {
    const deltaX = e.clientX - startX
    const deltaY = e.clientY - startY
    
    // 计算新位置（相对于视窗）
    let newX = currentX + deltaX
    let newY = currentY + deltaY
    
    // 限制在Classes页面容器范围内
    const minX = panelRect.left
    const minY = panelRect.top
    const maxX = panelRect.right - restoreRect.width
    const maxY = panelRect.bottom - restoreRect.height
    
    newX = Math.max(minX, Math.min(maxX, newX))
    newY = Math.max(minY, Math.min(maxY, newY))
    
    restorePanelsPosition.value = { x: newX, y: newY }
  }
  
  const handleMouseUp = () => {
    isDraggingRestorePanels.value = false
    document.removeEventListener('mousemove', handleMouseMove)
    document.removeEventListener('mouseup', handleMouseUp)
    // 恢复文本选择
    document.body.classList.remove('no-select')
  }
  
  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
  e.preventDefault()
}

// Class详情面板当前Tab
const activeClassTab = ref('details')

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
const showSearch = ref(false)
const commentsFilter = ref('')

// 评论和变更
const comments = ref([])
const projectActivities = ref([])
const classChanges = ref([])

// 模态框状态
const showCreateClassModal = ref(false)
const showCreateCommentModal = ref(false)
const showAddAnnotationModal = ref(false)
const showAddParentModal = ref(false)
const showAddRelationshipModal = ref(false)
const showTagsModal = ref(false)
const showMoveModal = ref(false)
const showMergeModal = ref(false)
const showSetAnnotationModal = ref(false)
const showEditAnnotationsModal = ref(false)
const showDirectLinkModal = ref(false)
const showWatchModal = ref(false)

// Direct Link相关
const directLink = ref('')

// Watch相关
const watchOption = ref('none')

// 剪枝状态
const prunedNodes = ref([])

// 拖拽相关状态
const draggingNode = ref(null)

// 表单数据
const createClassForm = ref({
  name: '',
  iri: '',
  parentId: 'owl:Thing',
  languageTag: ''
})

// 项目默认语言
const projectDefaultLanguage = computed(() => {
  return props.projectInfo?.language || 'en'
})

// 重置语言标签
const resetLanguageTag = () => {
  createClassForm.value.languageTag = projectDefaultLanguage.value
}

// 重置创建类表单
const resetCreateClassForm = () => {
  createClassForm.value = {
    name: '',
    iri: '',
    parentId: 'owl:Thing',
    languageTag: ''
  }
}

const createCommentForm = ref({
  content: ''
})

const newAnnotation = ref({
  predicate: '',
  value: ''
})

const newParent = ref('')

const newRelationship = ref({
  property: '',
  target: ''
})

// Tags相关
const classTags = ref([])
const newTag = ref('')

// Move相关
const moveTargetParent = ref('')
const moveSearchResults = ref([])
const availableMoveTargets = computed(() => {
  if (!contextMenuNode.value) return classHierarchy.value
  return classHierarchy.value.filter(c => c.id !== contextMenuNode.value.id)
})



// Set Annotation相关
const setAnnotationForm = ref({
  property: 'rdfs:label',
  language: '',
  value: ''
})

// Edit Annotations相关
const editAnnotationsList = ref([])

// ECharts引用
const graphChart = ref(null)
let chartInstance = null

// 计算属性
const filteredComments = computed(() => {
  if (!commentsFilter.value) return comments.value
  return comments.value.filter(c => 
    c.content.toLowerCase().includes(commentsFilter.value.toLowerCase()) ||
    c.author.toLowerCase().includes(commentsFilter.value.toLowerCase())
  )
})

const availableParents = computed(() => {
  if (!selectedClass.value) return classHierarchy.value
  return classHierarchy.value.filter(c => c.id !== selectedClass.value.id)
})

const availableProperties = ref([
  { id: '1', name: 'rdfs:subClassOf' },
  { id: '2', name: 'owl:equivalentClass' },
  { id: '3', name: 'owl:disjointWith' }
])

const availableClasses = computed(() => classHierarchy.value)

// 监听窗口大小变化
const handleResize = () => {
  windowHeight.value = window.innerHeight
  
  // 获取父容器的实际宽度
  const panelsWrapper = document.querySelector('.panels-wrapper')
  if (panelsWrapper) {
    const availableWidth = panelsWrapper.clientWidth
    windowWidth.value = availableWidth
    
    // 计算面板宽度（占比：Class Hierarchy 1/4, Class 2/4, 右侧面板 1/4）
    leftPanelWidth.value = Math.floor(availableWidth * 0.25)
    middlePanelWidth.value = Math.floor(availableWidth * 0.5)
    rightPanelWidth.value = availableWidth - leftPanelWidth.value - middlePanelWidth.value
  }
  
  // Comments 高度默认占右侧面板的 1/2
  commentsHeight.value = Math.floor((windowHeight.value - 120) * 0.5)
  hierarchyHeight.value = Math.floor((windowHeight.value - 120) * 0.67)
}

// 初始化加载
onMounted(async () => {
  handleResize()
  window.addEventListener('resize', handleResize)
  
  await loadClassHierarchy()
  await loadComments()
  await loadProjectActivities()
  
  // 初始化默认选中
  if (treeData.value && treeData.value.length > 0) {
    handleNodeClick(treeData.value[0])
  }
})

// 加载类层次结构
const loadClassHierarchy = async () => {
  try {
    // 使用 projectDataRecord 中的 id 作为 ontologyId，如果没有则使用 projectId
    const ontologyId = props.projectDataRecord?.id || props.projectId
    const response = await http.get(`/class/findByOntologyId/${ontologyId}`)
    classHierarchy.value = response.data || []
    treeData.value = buildTreeData(classHierarchy.value)
  } catch (error) {
    console.error('Failed to load class hierarchy:', error)
    // 使用模拟数据
    const mockData = [
      {
        id: 'owl:Thing',
        name: 'owl:Thing',
        children: [
          { id: 'class1', name: 'Person', children: [] },
          { id: 'class2', name: 'Organization', children: [] }
        ]
      }
    ]
    classHierarchy.value = mockData
    treeData.value = mockData
  }
}

// 构建树形数据 - 将扁平化的类数据转换为嵌套的树形结构
const buildTreeData = (classes) => {
  // 始终创建owl:Thing作为虚拟根节点
  const owlThingNode = {
    id: 'owl:Thing',
    name: 'owl:Thing',
    children: []
  }
  
  if (!classes || classes.length === 0) {
    return [owlThingNode]
  }
  
  // 创建类ID到类对象的映射
  const classMap = new Map()
  classes.forEach(cls => {
    classMap.set(cls.id, {
      ...cls,
      children: []
    })
  })
  
  // 构建树形结构
  classes.forEach(cls => {
    const node = classMap.get(cls.id)
    
    // 检查是否有父类
    if (cls.superClasses && cls.superClasses.length > 0) {
      const parentId = cls.superClasses[0]
      
      // 如果父类是owl:Thing，挂载到虚拟根节点下
      if (parentId === 'owl:Thing') {
        owlThingNode.children.push(node)
      } else {
        // 否则挂载到实际的父节点下
        const parentNode = classMap.get(parentId)
        if (parentNode) {
          parentNode.children.push(node)
        } else {
          // 父类不存在，作为owl:Thing的子节点
          owlThingNode.children.push(node)
        }
      }
    } else {
      // 没有父类，作为owl:Thing的子节点
      owlThingNode.children.push(node)
    }
  })
  
  return [owlThingNode]
}

// 加载评论
const loadComments = async () => {
  try {
    const response = await http.get(`/comment/findByProjectId/${props.projectId}`)
    comments.value = response.data || []
  } catch (error) {
    console.error('Failed to load comments:', error)
    comments.value = []
  }
}

// 加载项目活动
const loadProjectActivities = async () => {
  try {
    const response = await http.get(`/activity/findByProjectId/${props.projectId}`)
    projectActivities.value = response.data || []
  } catch (error) {
    console.error('Failed to load project activities:', error)
    projectActivities.value = []
  }
}

// 加载类详情
const loadClassDetails = async (classId) => {
  try {
    const response = await http.get(`/class/findById/${classId}`)
    selectedClass.value = response.data
    emit('class-selected', selectedClass.value)
    
    // 加载类变更历史
    // 注意：暂时注释掉这个调用，因为我们还没有实现 change 相关的 API
    // const changesResponse = await http.get(`/change/findByEntityId/${classId}`)
    // classChanges.value = changesResponse.data || []
  } catch (error) {
    console.error('Failed to load class details:', error)
  }
}

// 节点点击处理
const handleNodeClick = (data) => {
  loadClassDetails(data.id)
}

// 节点右键菜单处理
const handleNodeContextMenu = (event, data, node, element) => {
  event.preventDefault()
  event.stopPropagation()
  contextMenuPosition.value = { x: event.clientX, y: event.clientY }
  contextMenuNode.value = data
  showContextMenu.value = true
  
  // 点击其他地方关闭右键菜单
  setTimeout(() => {
    document.addEventListener('click', closeContextMenu)
    document.addEventListener('contextmenu', closeContextMenu)
  }, 0)
}

// 关闭右键菜单
const closeContextMenu = () => {
  showContextMenu.value = false
  document.removeEventListener('click', closeContextMenu)
  document.removeEventListener('contextmenu', closeContextMenu)
}

// 处理右键菜单动作
const handleContextMenuAction = (action) => {
  showContextMenu.value = false
  
  switch (action) {
    case 'create':
      // 设置父类为当前右键点击的节点
      createClassForm.value.parentId = contextMenuNode.value?.id || 'owl:Thing'
      showCreateClassModal.value = true
      break
    case 'delete':
      if (contextMenuNode.value && contextMenuNode.value.id !== 'owl:Thing') {
        selectedClass.value = contextMenuNode.value
        deleteSelectedClass()
      }
      break
    case 'tags':
      showTagsModal.value = true
      break
    case 'move':
      showMoveModal.value = true
      break
    case 'merge':
      showMergeModal.value = true
      break
    case 'setAnnotation':
      showSetAnnotationModal.value = true
      break
    case 'editAnnotations':
      showEditAnnotationsModal.value = true
      break
    case 'watch':
      showWatchModal.value = true
      break
    case 'pruneBranch':
      handlePruneBranch()
      break
    case 'pruneAllBranches':
      handlePruneAllBranches()
      break
    case 'clearPruning':
      handleClearPruning()
      break
    case 'showIRI':
      alert(`IRI: ${contextMenuNode.value?.iri || `http://www.w3.org/2002/07/owl#${contextMenuNode.value?.name}`}`)
      break
    case 'showDirectLink':
      generateDirectLink()
      showDirectLinkModal.value = true
      break
    case 'refreshTree':
      loadClassHierarchy()
      break
  }
}

const handleNodeExpand = (data) => {
  expandedKeys.value.push(data.id)
}

const handleNodeCollapse = (data) => {
  const index = expandedKeys.value.indexOf(data.id)
  if (index > -1) {
    expandedKeys.value.splice(index, 1)
  }
}

// Watch功能
const handleWatchClass = () => {
  if (!contextMenuNode.value) return
  console.log('Watch class:', contextMenuNode.value.name)
  alert(`Started watching class: ${contextMenuNode.value.name}`)
}

// 剪枝功能
const handlePruneBranch = () => {
  if (!contextMenuNode.value) return
  const nodeId = contextMenuNode.value.id
  if (!prunedNodes.value.includes(nodeId)) {
    prunedNodes.value.push(nodeId)
  }
  console.log('Prune branch to root for:', nodeId)
}

const handlePruneAllBranches = () => {
  console.log('Prune all branches to root')
  prunedNodes.value = [...classHierarchy.value.map(c => c.id)]
}

const handleClearPruning = () => {
  console.log('Clear pruning')
  prunedNodes.value = []
}

// Tags功能
const addTag = () => {
  if (!newTag.value.trim()) return
  if (!classTags.value.includes(newTag.value.trim())) {
    classTags.value.push(newTag.value.trim())
  }
  newTag.value = ''
}

const removeTag = (index) => {
  classTags.value.splice(index, 1)
}

// Move功能
const handleMoveClass = async () => {
  if (!contextMenuNode.value || !moveTargetParent.value) return
  
  try {
    await http.post('/api/class/move', {
      classId: contextMenuNode.value.id,
      newParentId: moveTargetParent.value
    })
    showMoveModal.value = false
    moveTargetParent.value = ''
    await loadClassHierarchy()
    alert(`Class moved successfully`)
  } catch (error) {
    console.error('Failed to move class:', error)
    alert('Failed to move class')
  }
}

// Merge功能
const handleMergeClass = async () => {
  if (!contextMenuNode.value || !mergeTargetParent.value) return
  
  try {
    await http.post('/api/class/merge', {
      sourceId: contextMenuNode.value.id,
      targetId: mergeTargetParent.value
    })
    showMergeModal.value = false
    mergeTargetParent.value = ''
    mergeSearchResults.value = []
    await loadClassHierarchy()
    alert(`Classes merged successfully`)
  } catch (error) {
    console.error('Failed to merge classes:', error)
    alert('Failed to merge classes')
  }
}

// Set Annotation功能
const handleSetAnnotation = async () => {
  if (!contextMenuNode.value) return
  
  try {
    await http.post('/annotation/set', {
      entityId: contextMenuNode.value.id,
      entityType: 'CLASS',
      property: setAnnotationForm.value.property,
      language: setAnnotationForm.value.language,
      value: setAnnotationForm.value.value
    })
    showSetAnnotationModal.value = false
    setAnnotationForm.value = { property: 'rdfs:label', language: '', value: '' }
    await loadClassDetails(contextMenuNode.value.id)
    alert('Annotation set successfully')
  } catch (error) {
    console.error('Failed to set annotation:', error)
    alert('Failed to set annotation')
  }
}

// Edit Annotations功能
const addEditAnnotation = () => {
  editAnnotationsList.value.push({
    property: 'rdfs:label',
    language: '',
    value: ''
  })
}

const removeEditAnnotation = (index) => {
  editAnnotationsList.value.splice(index, 1)
}

const saveEditAnnotations = async () => {
  if (!contextMenuNode.value) return
  
  try {
    await http.post('/annotation/batch', {
      entityId: contextMenuNode.value.id,
      entityType: 'CLASS',
      annotations: editAnnotationsList.value
    })
    showEditAnnotationsModal.value = false
    await loadClassDetails(contextMenuNode.value.id)
    alert('Annotations saved successfully')
  } catch (error) {
    console.error('Failed to save annotations:', error)
    alert('Failed to save annotations')
  }
}

// 切换搜索框
const toggleSearch = () => {
  showSearch.value = !showSearch.value
}

// 切换Filter
const toggleFilter = () => {
  const filter = prompt('Filter classes by:')
  if (filter !== null) {
    console.log('Filter:', filter)
  }
}

// 切换Comments Filter
const toggleCommentsFilter = () => {
  const filter = prompt('Filter comments by:')
  if (filter !== null) {
    commentsFilter.value = filter
  }
}

// 搜索类
const searchClasses = async () => {
  if (!searchQuery.value) return
  
  try {
    const response = await http.get(`/class/search`, {
      params: { query: searchQuery.value, projectId: props.projectId }
    })
    // 高亮搜索结果
    console.log('Search results:', response.data)
  } catch (error) {
    console.error('Failed to search classes:', error)
  }
}

// Move搜索类
const searchClassesForMove = async () => {
  if (!moveTargetParent.value) {
    moveSearchResults.value = []
    return
  }
  
  try {
    const response = await http.get(`/api/class/search`, {
      params: { query: moveTargetParent.value, projectId: props.projectId }
    })
    moveSearchResults.value = response.data || []
  } catch (error) {
    console.error('Failed to search classes for move:', error)
    moveSearchResults.value = []
  }
}

// 清除Move搜索
const clearMoveSearch = () => {
  moveTargetParent.value = ''
  moveSearchResults.value = []
}

// 选择Move目标
const selectMoveTarget = (cls) => {
  moveTargetParent.value = cls.id
  moveSearchResults.value = []
}

// 显示类列表
const showClassesList = async () => {
  try {
    const response = await http.get(`/api/class/findByOntologyId/${props.projectId}`)
    moveSearchResults.value = response.data || []
  } catch (error) {
    console.error('Failed to load classes list:', error)
    moveSearchResults.value = []
  }
}

// 上移选择
const moveSelectionUp = () => {
  // 实现上移逻辑
  console.log('Move selection up')
}

// 下移选择
const moveSelectionDown = () => {
  // 实现下移逻辑
  console.log('Move selection down')
}

// 刷新类列表
const refreshClassesList = async () => {
  await showClassesList()
}

// Merge相关变量
const mergeTargetParent = ref('')
const mergeSearchResults = ref([])
const showMergeClassesTreeFlag = ref(false)

// 显示Merge类列表
const showMergeClassesList = async () => {
  try {
    const response = await http.get(`/api/class/findByOntologyId/${props.projectId}`)
    mergeSearchResults.value = response.data || []
  } catch (error) {
    console.error('Failed to load classes list:', error)
    mergeSearchResults.value = []
  }
}

// 上移Merge选择
const moveMergeSelectionUp = () => {
  // 实现上移逻辑
  console.log('Move merge selection up')
}

// 下移Merge选择
const moveMergeSelectionDown = () => {
  // 实现下移逻辑
  console.log('Move merge selection down')
}

// 刷新Merge类列表
const refreshMergeClassesList = async () => {
  await showMergeClassesList()
}

// 清除Merge搜索
const clearMergeSearch = () => {
  mergeTargetParent.value = ''
  mergeSearchResults.value = []
}

// 选择Merge目标
const selectMergeTarget = (cls) => {
  mergeTargetParent.value = cls.id
  mergeSearchResults.value = []
}

// 切换Merge类树显示
const toggleMergeClassesTree = () => {
  showMergeClassesTreeFlag.value = !showMergeClassesTreeFlag.value
}

// 处理Merge树节点点击
const handleMergeTreeNodeClick = (node) => {
  if (node.id !== contextMenuNode.value?.id) {
    mergeTargetParent.value = node.id
    showMergeClassesTreeFlag.value = false
  }
}

// 删除选中的类
const deleteSelectedClass = async () => {
  if (!selectedClass.value || selectedClass.value.id === 'owl:Thing') return
  
  if (!confirm(`Are you sure you want to delete class "${selectedClass.value.name}"?`)) return
  
  try {
    await http.delete(`/class/delete/${selectedClass.value.id}`)
    emit('class-deleted', selectedClass.value)
    await loadClassHierarchy()
    selectedClass.value = null
  } catch (error) {
    console.error('Failed to delete class:', error)
    alert('Failed to delete class')
  }
}

// 创建类
const handleCreateClass = async () => {
  try {
    // 构建请求数据
    const requestData = {
      name: createClassForm.value.name,
      iri: createClassForm.value.iri || generateIriPlaceholder(),
      parentId: createClassForm.value.parentId,
      projectId: props.projectId,
      languageTag: createClassForm.value.languageTag || projectDefaultLanguage.value,
      ontologyId: props.projectDataRecord?.id || props.projectId
    }
    
    const response = await http.post('/class/create', requestData)
    
    // 显示成功消息
    const successMessage = `Successfully created class: ${createClassForm.value.name}`
    showAlertMessage(successMessage, 'success')
    
    // 延迟0.5秒后关闭模态框
    setTimeout(() => {
      showCreateClassModal.value = false
      emit('class-created', response.data)
      loadClassHierarchy()
      // 重置表单
      createClassForm.value = { 
        name: '', 
        iri: '', 
        parentId: 'owl:Thing',
        languageTag: '' 
      }
    }, 500)
    
  } catch (error) {
    console.error('Failed to create class:', error)
    
    // 构建错误消息
    const source = error.response?.config?.url || 'Server'
    const errorMessage = error.response?.data?.message || error.message || 'Unknown error'
    const fullErrorMessage = `Source: ${source}\nFailed to create class: ${errorMessage}`
    
    // 显示错误消息
    showAlertMessage(fullErrorMessage, 'error')
  }
}

// 显示提示消息
const showAlertMessage = (message, type = 'info') => {
  // 创建提示元素
  const alertDiv = document.createElement('div')
  alertDiv.className = `alert alert-${type === 'error' ? 'danger' : type === 'success' ? 'success' : 'info'} alert-dismissible fade show position-fixed`
  alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 9999; max-width: 400px;'
  alertDiv.innerHTML = `
    <div style="white-space: pre-line;">${message}</div>
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
  `
  
  document.body.appendChild(alertDiv)
  
  // 3秒后自动关闭
  setTimeout(() => {
    if (alertDiv.parentNode) {
      alertDiv.remove()
    }
  }, 3000)
  
  // 点击关闭按钮移除
  alertDiv.querySelector('.btn-close').addEventListener('click', () => {
    alertDiv.remove()
  })
}

// 生成IRI占位符
const generateIriPlaceholder = () => {
  return `http://www.example.org/ontology#${createClassForm.value.name || 'Class'}`
}

// 创建评论
const handleCreateComment = async () => {
  try {
    const response = await http.post('/comment/create', {
      content: createCommentForm.value.content,
      ontologyId: props.projectId,
      entityId: selectedClass.value?.id,
      entityType: 'CLASS',
      author: props.userInfo?.username || 'Anonymous'
    })
    showCreateCommentModal.value = false
    await loadComments()
    createCommentForm.value = { content: '' }
  } catch (error) {
    console.error('Failed to create comment:', error)
    alert('Failed to create comment')
  }
}

// 编辑标签
const editLabel = () => {
  const newLabel = prompt('Enter new label:', selectedClass.value?.name)
  if (newLabel && selectedClass.value) {
    console.log('Update label to:', newLabel)
  }
}

// 编辑注解
const editAnnotation = (index) => {
  const ann = selectedClass.value.annotations[index]
  const newValue = prompt('Edit annotation value:', ann.value)
  if (newValue) {
    console.log('Update annotation:', index, newValue)
  }
}

// 添加注解
const handleAddAnnotation = async () => {
  if (!selectedClass.value) return
  
  try {
    await http.post('/annotation/create', {
      entityId: selectedClass.value.id,
      entityType: 'CLASS',
      predicate: newAnnotation.value.predicate || 'rdfs:comment',
      value: newAnnotation.value.value
    })
    showAddAnnotationModal.value = false
    newAnnotation.value = { predicate: '', value: '' }
    await loadClassDetails(selectedClass.value.id)
  } catch (error) {
    console.error('Failed to add annotation:', error)
    alert('Failed to add annotation')
  }
}

// 移除父类
const removeParent = async (index) => {
  if (!selectedClass.value) return
  console.log('Remove parent at index:', index)
}

// 生成Direct Link
const generateDirectLink = () => {
  if (!contextMenuNode.value) return
  const projectId = props.projectId
  const classId = contextMenuNode.value.id
  // 参考WebProtege的链接格式
  directLink.value = `${window.location.origin}/#projects/${projectId}/edit/Classes?selection=Class(${classId})`
}

// 复制Direct Link到剪贴板
const copyDirectLink = async () => {
  try {
    await navigator.clipboard.writeText(directLink.value)
    alert('Link copied to clipboard')
  } catch (error) {
    console.error('Failed to copy link:', error)
    alert('Failed to copy link')
  }
}

// 处理Watch提交
const handleWatchSubmit = async () => {
  if (!contextMenuNode.value) return
  
  try {
    await http.post('/watch/set', {
      entityId: contextMenuNode.value.id,
      entityType: 'CLASS',
      watchType: watchOption.value
    })
    showWatchModal.value = false
    watchOption.value = 'none'
    alert(`Watch set to ${watchOption.value} for class: ${contextMenuNode.value.name}`)
  } catch (error) {
    console.error('Failed to set watch:', error)
    alert('Failed to set watch')
  }
}

// 添加父类
const handleAddParent = async () => {
  if (!selectedClass.value || !newParent.value) return
  
  try {
    await http.post('/class/addSuperClass', {
      classId: selectedClass.value.id,
      superClassId: newParent.value
    })
    showAddParentModal.value = false
    newParent.value = ''
    await loadClassDetails(selectedClass.value.id)
  } catch (error) {
    console.error('Failed to add parent:', error)
    alert('Failed to add parent')
  }
}

// 移除关系
const removeRelationship = async (index) => {
  if (!selectedClass.value) return
  console.log('Remove relationship at index:', index)
}

// Description面板相关方法
const removeDisjointWith = (index) => {
  if (!selectedClass.value) return
  if (selectedClass.value.disjointWith) {
    selectedClass.value.disjointWith.splice(index, 1)
  }
}

const removeSuperClass = (index) => {
  if (!selectedClass.value) return
  if (selectedClass.value.superClasses) {
    selectedClass.value.superClasses.splice(index, 1)
  }
}

// 添加关系
const handleAddRelationship = async () => {
  if (!selectedClass.value || !newRelationship.value.property || !newRelationship.value.target) return
  
  try {
    await http.post('/relationship/create', {
      sourceId: selectedClass.value.id,
      property: newRelationship.value.property,
      target: newRelationship.value.target
    })
    showAddRelationshipModal.value = false
    newRelationship.value = { property: '', target: '' }
    await loadClassDetails(selectedClass.value.id)
  } catch (error) {
    console.error('Failed to add relationship:', error)
    alert('Failed to add relationship')
  }
}

// 调整大小功能
const startResizeLeft = (e) => {
  const startX = e.clientX
  const startLeftWidth = leftPanelWidth.value
  const startMiddleWidth = middlePanelWidth.value
  
  // 禁用文本选择
  document.body.classList.add('no-select')
  
  const handleMouseMove = (e) => {
    const deltaX = e.clientX - startX
    leftPanelWidth.value = startLeftWidth + deltaX
    middlePanelWidth.value = startMiddleWidth - deltaX
  }
  
  const handleMouseUp = () => {
    document.removeEventListener('mousemove', handleMouseMove)
    document.removeEventListener('mouseup', handleMouseUp)
    // 恢复文本选择
    document.body.classList.remove('no-select')
  }
  
  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
  e.preventDefault()
}

// 左侧面板上下拖动调整大小
const startResizeLeftPanels = (e) => {
  const startY = e.clientY
  const startHeight = hierarchyHeight.value
  
  // 禁用文本选择
  document.body.classList.add('no-select')
  
  const handleMouseMove = (e) => {
    const deltaY = e.clientY - startY
    hierarchyHeight.value = Math.max(100, startHeight + deltaY)
  }
  
  const handleMouseUp = () => {
    document.removeEventListener('mousemove', handleMouseMove)
    document.removeEventListener('mouseup', handleMouseUp)
    // 恢复文本选择
    document.body.classList.remove('no-select')
  }
  
  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
  e.preventDefault()
}

const startResizeMiddle = (e) => {
  const startX = e.clientX
  const startMiddleWidth = middlePanelWidth.value
  const startRightWidth = rightPanelWidth.value
  
  // 禁用文本选择
  document.body.classList.add('no-select')
  
  const handleMouseMove = (e) => {
    const deltaX = e.clientX - startX
    middlePanelWidth.value = startMiddleWidth + deltaX
    rightPanelWidth.value = startRightWidth - deltaX
  }
  
  const handleMouseUp = () => {
    document.removeEventListener('mousemove', handleMouseMove)
    document.removeEventListener('mouseup', handleMouseUp)
    // 恢复文本选择
    document.body.classList.remove('no-select')
  }
  
  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
  e.preventDefault()
}

const startResize = (e) => {
  const startY = e.clientY
  const startHeight = commentsHeight.value
  
  // 禁用文本选择
  document.body.classList.add('no-select')
  
  const handleMouseMove = (e) => {
    const deltaY = e.clientY - startY
    commentsHeight.value = startHeight + deltaY
  }
  
  const handleMouseUp = () => {
    document.removeEventListener('mousemove', handleMouseMove)
    document.removeEventListener('mouseup', handleMouseUp)
    // 恢复文本选择
    document.body.classList.remove('no-select')
  }
  
  document.addEventListener('mousemove', handleMouseMove)
  document.addEventListener('mouseup', handleMouseUp)
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

// 允许拖拽
const allowDrag = (draggingNode) => {
  // 不允许拖拽根节点 owl:Thing
  return draggingNode.data.id !== 'owl:Thing'
}

// 允许放置
const allowDrop = (draggingNode, dropNode, type) => {
  // 只能作为子节点放置，不允许作为前一个或后一个兄弟节点
  if (type !== 'inner') {
    return false
  }
  
  // 不能放置到自己身上
  if (draggingNode.data.id === dropNode.data.id) {
    return false
  }
  
  // 检查循环依赖
  if (checkCycleDependency(draggingNode.data, dropNode.data)) {
    return false
  }
  
  return true
}

// 检查循环依赖 - 从整个树中检查目标是否是拖拽节点的后代
const checkCycleDependency = (draggingNodeData, targetNodeData) => {
  // 递归查找节点
  const findNodeById = (nodes, id) => {
    for (const node of nodes) {
      if (node.id === id) {
        return node
      }
      if (node.children && node.children.length > 0) {
        const found = findNodeById(node.children, id)
        if (found) {
          return found
        }
      }
    }
    return null
  }
  
  // 检查是否是后代
  const isDescendant = (parent, targetId) => {
    if (!parent.children || parent.children.length === 0) {
      return false
    }
    for (const child of parent.children) {
      if (child.id === targetId) {
        return true
      }
      if (isDescendant(child, targetId)) {
        return true
      }
    }
    return false
  }
  
  // 在整个树中找到拖拽节点的完整结构
  const fullDraggingNode = findNodeById(treeData.value, draggingNodeData.id)
  
  if (!fullDraggingNode) {
    return false
  }
  
  // 检查目标节点是否是拖拽节点的后代
  return isDescendant(fullDraggingNode, targetNodeData.id)
}

// 处理节点放置
const handleNodeDrop = async (draggingNode, dropNode, dropType, event) => {
  if (dropType !== 'inner') {
    return
  }
  
  try {
    const ontologyId = props.projectDataRecord?.id || props.projectId
    await http.post('/class/move', {
      classId: draggingNode.data.id,
      newParentId: dropNode.data.id,
      ontologyId: ontologyId
    })
    
    // 重新加载类层次结构
    await loadClassHierarchy()
    
    // 选中被移动的节点
    handleNodeClick(draggingNode.data)
    
    alert(`Class "${draggingNode.data.name}" moved successfully`)
  } catch (error) {
    console.error('Failed to move class:', error)
    alert('Failed to move class')
    
    // 失败时重新加载以恢复原状
    await loadClassHierarchy()
  }
}

// 初始化ECharts
watch(activeClassTab, async (newVal) => {
  if (newVal === 'graph' && selectedClass.value) {
    await nextTick()
    initGraph()
  }
})

const initGraph = () => {
  if (!graphChart.value) return
  
  if (chartInstance) {
    chartInstance.dispose()
  }
  
  chartInstance = echarts.init(graphChart.value)
  
  const option = {
    title: {
      text: 'Entity Graph',
      left: 'center',
      top: 10,
      textStyle: { fontSize: 14 }
    },
    tooltip: {},
    series: [{
      type: 'graph',
      layout: 'force',
      data: [
        { name: selectedClass.value?.name || 'Class', symbolSize: 50, category: 0 },
        { name: 'Parent', symbolSize: 30, category: 1 },
        { name: 'Child 1', symbolSize: 25, category: 2 },
        { name: 'Child 2', symbolSize: 25, category: 2 }
      ],
      links: [
        { source: selectedClass.value?.name || 'Class', target: 'Parent' },
        { source: selectedClass.value?.name || 'Class', target: 'Child 1' },
        { source: selectedClass.value?.name || 'Class', target: 'Child 2' }
      ],
      categories: [
        { name: 'Current' },
        { name: 'Parent' },
        { name: 'Children' }
      ],
      roam: true,
      label: {
        show: true,
        position: 'bottom'
      },
      force: {
        repulsion: 100,
        edgeLength: 50
      }
    }]
  }
  
  chartInstance.setOption(option)
}
</script>

<style scoped>
.class-editor-panel {
  flex: 1;
  overflow: hidden;
  background-color: #f0f0f0;
  display: flex;
  flex-direction: column;
}

.mobile-panel-tabs {
  display: flex;
  background: linear-gradient(180deg, #f5f5f5 0%, #e8e8e8 100%);
  border-bottom: 1px solid #ccc;
  padding: 4px;
  gap: 4px;
  overflow-x: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.mobile-panel-tabs::-webkit-scrollbar {
  display: none;
}

.mobile-panel-tab {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  padding: 6px 12px;
  font-size: 10px;
  color: #555;
  background: white;
  border: 1px solid #ccc;
  border-radius: 4px;
  cursor: pointer;
  white-space: nowrap;
  min-width: 60px;
  transition: all 0.2s;
}

.mobile-panel-tab i {
  font-size: 16px;
}

.mobile-panel-tab.active {
  background-color: #4a90d9;
  color: white;
  border-color: #4a90d9;
}

.panels-wrapper {
  display: flex;
  height: 100%;
  width: 100%;
}

.panels-wrapper.mobile-view {
  height: calc(100% - 60px);
}

.panel {
  display: flex;
  flex-direction: column;
  background-color: white;
  border-right: 1px solid #ccc;
  position: relative;
  flex-shrink: 0;
}

.panel.mobile-active {
  display: flex;
  width: 100% !important;
  height: 100% !important;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 28px;
  padding: 0 8px;
  background: linear-gradient(180deg, #808080 0%, #696969 100%);
  color: white;
  flex-shrink: 0;
}

.panel-header-left {
  display: flex;
  align-items: center;
  overflow: hidden;
  min-width: 0;
}

.panel-header-right {
  display: flex;
  align-items: center;
  gap: 2px;
}

.panel-title {
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.panel-actions {
  display: flex;
  align-items: center;
  gap: 1px;
}

.panel-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  padding: 0;
  color: white;
  background: transparent;
  border: none;
  border-radius: 2px;
  cursor: pointer;
  opacity: 0.9;
  transition: all 0.15s;
}

.panel-btn:hover {
  opacity: 1;
  background-color: rgba(255, 255, 255, 0.15);
}

.panel-btn.active {
  background-color: rgba(255, 255, 255, 0.25);
}

.panel-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.close-btn {
  margin-left: 4px;
}

.wp-icon {
  display: block;
}

.panel-search-box {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 8px;
  background-color: #f5f5f5;
  border-bottom: 1px solid #ddd;
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  height: 24px;
  padding: 0 6px;
  font-size: 12px;
  border: 1px solid #ccc;
  border-radius: 2px;
}

.search-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  padding: 0;
  background: white;
  border: 1px solid #ccc;
  border-radius: 2px;
  cursor: pointer;
}

.panel-body {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.class-hierarchy-panel {
  flex-shrink: 0;
  position: relative;
}

/* Class Hierarchy面板内的垂直分割线 */
.class-hierarchy-panel > .resize-handle-v {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 4px;
  cursor: row-resize;
  background-color: #ddd;
  z-index: 100;
}

.class-hierarchy-panel > .resize-handle-v:hover {
  background-color: #4a90d9;
}

.tree-node {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  cursor: pointer;
  user-select: none;
  padding: 2px 4px;
  border-radius: 3px;
}

.tree-node:hover {
  background-color: #f0f0f0;
}

.tree-node.selected {
  background-color: #e3f2fd;
}

.node-icon {
  font-size: 6px;
  color: #4a90d9;
}

.node-label {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.class-details-panel {
  flex-shrink: 0;
}

.detail-section {
  margin-bottom: 16px;
}

.section-title {
  font-size: 11px;
  font-weight: 600;
  color: #666;
  text-transform: uppercase;
  margin-bottom: 6px;
}

.iri-display {
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 11px;
  color: #333;
  word-break: break-all;
  background-color: #f8f8f8;
  padding: 6px 8px;
  border-radius: 2px;
  border: 1px solid #e0e0e0;
}

.data-list {
  border: 1px solid #ddd;
  border-radius: 2px;
  padding: 6px;
}

.data-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 0;
  font-size: 12px;
}

.item-predicate {
  font-weight: 500;
  color: #4a90d9;
  min-width: 90px;
  flex-shrink: 0;
}

.item-value {
  flex: 1;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.item-action {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  padding: 0;
  color: #4a90d9;
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 11px;
}

.item-action.delete {
  color: #dc3545;
}

.btn-add {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 6px;
  padding: 4px 8px;
  font-size: 11px;
  color: #4a90d9;
  background: transparent;
  border: 1px dashed #4a90d9;
  border-radius: 2px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-add:hover {
  background-color: rgba(74, 144, 217, 0.1);
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
}

.nothing-selected {
  color: #999;
  font-size: 13px;
}

.graph-container {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.graph-chart {
  width: 100%;
  height: 100%;
  min-height: 200px;
}

.changes-list {
  padding: 4px;
}

.change-item {
  padding: 8px;
  border-bottom: 1px solid #eee;
  font-size: 12px;
}

.change-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.change-author {
  font-weight: 500;
  color: #4a90d9;
}

.change-time {
  color: #999;
  font-size: 10px;
}

.change-content {
  color: #333;
}

.empty-message {
  padding: 24px;
  text-align: center;
  color: #999;
  font-size: 12px;
}

.left-panels {
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  overflow: hidden;
  position: relative;
}

.description-panel {
  border-top: 1px solid #ccc;
  flex-shrink: 0;
}

.description-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.description-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  padding: 4px 8px;
  font-size: 12px;
  color: #999;
  cursor: pointer;
  transition: all 0.2s;
  border-radius: 3px;
}

.description-item:hover {
  color: #4a90d9;
  background-color: rgba(74, 144, 217, 0.1);
}

.description-item.focused {
  background-color: #4a90d9;
  color: white;
}

.description-item .item-label {
  font-weight: 500;
  min-width: 150px;
  flex-shrink: 0;
}

.description-item .item-value {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.description-item.focused .item-value {
  color: white;
}

.description-item .item-action {
  display: flex;
  align-items: center;
  gap: 3px;
  padding: 2px 6px;
  font-size: 11px;
  color: inherit;
  background: transparent;
  border: 1px solid transparent;
  border-radius: 3px;
  cursor: pointer;
  transition: all 0.2s;
}

.description-item:hover .item-action {
  border-color: #4a90d9;
  background-color: rgba(255, 255, 255, 0.1);
}

.description-item.focused .item-action {
  border-color: white;
  background-color: rgba(255, 255, 255, 0.2);
}

.description-item .add-text {
  font-size: 10px;
}

.description-item.focused .super-class-item {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
}

.description-item.focused .disjoint-item {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
}

.description-item .disjoint-with {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  flex: 1;
}

.description-item .disjoint-item {
  background-color: #f0f0f0;
  padding: 2px 6px;
  border-radius: 10px;
  font-size: 11px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.description-item .disjoint-action {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 14px;
  height: 14px;
  padding: 0;
  color: #666;
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 10px;
}

.description-item .super-class-item {
  background-color: #f0f0f0;
  padding: 2px 6px;
  border-radius: 10px;
  font-size: 11px;
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 4px;
}

.description-item .super-class-action {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 14px;
  height: 14px;
  padding: 0;
  color: #666;
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 10px;
}

.description-item .super-class-action:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.right-panels {
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  overflow: hidden;
}

.comments-panel {
  position: relative;
  border-bottom: 1px solid #ccc;
}

.comments-list,
.activities-list {
  padding: 4px;
}

.comment-item,
.activity-item {
  padding: 8px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 12px;
}

.comment-header,
.activity-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.comment-author,
.activity-author {
  font-weight: 500;
  color: #4a90d9;
}

.comment-time,
.activity-time {
  color: #999;
  font-size: 10px;
}

.comment-content,
.activity-content {
  color: #333;
  line-height: 1.4;
}

.comment-replies {
  margin-top: 6px;
  padding-left: 12px;
  border-left: 2px solid #e0e0e0;
}

.reply-item {
  padding: 3px 0;
  font-size: 11px;
}

.reply-author {
  font-weight: 500;
  color: #4a90d9;
}

.activity-entity {
  color: #666;
  font-size: 10px;
  margin-top: 4px;
}

.resize-handle-h {
  position: absolute;
  top: 28px;
  right: 0;
  width: 4px;
  bottom: 0;
  cursor: col-resize;
  background-color: transparent;
  z-index: 10;
}

.resize-handle-h:hover {
  background-color: #4a90d9;
}

/* 拖动时禁用文本选择 */
.no-select {
  user-select: none !important;
  -webkit-user-select: none !important;
  -moz-user-select: none !important;
  -ms-user-select: none !important;
}

.resize-handle-v {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 4px;
  cursor: row-resize;
  background-color: transparent;
  z-index: 10;
}

.resize-handle-v:hover {
  background-color: #4a90d9;
}

.restore-panels {
  position: fixed;
  bottom: 12px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
  z-index: 1000;
  background-color: rgba(255, 255, 255, 0.98);
  padding: 8px 12px;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  border: 1px solid #ddd;
  cursor: default;
}

.restore-panels-drag-handle {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2px 4px;
  cursor: grab;
  color: #999;
  font-size: 12px;
  border-right: 1px solid #eee;
  margin-right: 4px;
}

.restore-panels-drag-handle:hover {
  color: #666;
  cursor: grab;
}

.restore-panels-drag-handle:active {
  cursor: grabbing;
}

.btn-restore {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 5px 10px;
  font-size: 11px;
  color: #555;
  background: white;
  border: 1px solid #ccc;
  border-radius: 3px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
}

.btn-restore:hover {
  background-color: #f5f5f5;
  border-color: #999;
}

/* Context Menu Styles */
.context-menu {
  position: fixed;
  background: white;
  border: 1px solid #ccc;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  z-index: 10000;
  min-width: 180px;
  padding: 4px 0;
  font-size: 13px;
}

.context-menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  cursor: pointer;
  transition: background-color 0.2s;
  color: #333;
}

.context-menu-item:hover {
  background-color: #4a90d9;
  color: white;
}

.context-menu-item i {
  font-size: 14px;
  width: 16px;
  text-align: center;
}

.context-menu-divider {
  height: 1px;
  background-color: #e0e0e0;
  margin: 4px 0;
}

/* Tags Styles */
.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
}

.tag-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background-color: #e3f2fd;
  color: #1976d2;
  border-radius: 12px;
  font-size: 12px;
}

.tag-item i {
  cursor: pointer;
  font-size: 12px;
}

.tag-item i:hover {
  color: #d32f2f;
}

/* Edit Annotations Styles */
.edit-annotations-container {
  padding: 16px;
  max-height: 300px;
  overflow-y: auto;
}

.edit-annotations-container table {
  width: 100%;
}

.edit-annotations-container th,
.edit-annotations-container td {
  padding: 8px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.edit-annotations-container th {
  font-weight: 500;
  color: #666;
  font-size: 12px;
}

.edit-annotations-container td {
  font-size: 12px;
}

:deep(.el-tree) {
  background: transparent;
}

:deep(.el-tree-node__content) {
  height: 26px;
  padding-left: 4px !important;
}

:deep(.el-tree-node__content:hover) {
  background-color: #f0f0f0;
}

:deep(.el-tree-node.is-current > .el-tree-node__content) {
  background-color: #4a90d9;
  color: white;
}

:deep(.el-tree-node.is-current > .el-tree-node__content .node-icon) {
  color: white !important;
}

:deep(.el-tree-node.is-current > .el-tree-node__content .tree-node) {
  color: white;
  background-color: transparent;
}

:deep(.el-tree-node.is-current > .el-tree-node__content .tree-node:hover) {
  background-color: transparent;
}

.modal.show {
  display: block;
  background-color: rgba(0, 0, 0, 0.4);
}

.modal-dialog {
  margin-top: 10vh;
}

.modal-sm {
  max-width: 360px;
}

@media (max-width: 992px) {
  .panel {
    width: 100% !important;
    height: 100% !important;
    border-right: none;
    border-bottom: 1px solid #ccc;
    display: none;
  }
  
  .panel.mobile-active {
    display: flex;
  }
  
  .class-hierarchy-panel,
  .class-details-panel {
    max-width: none;
    min-width: auto;
  }
  
  .right-panels {
    min-width: auto;
  }
  
  .resize-handle-h {
    display: none;
  }
  
  .resize-handle-v {
    display: none;
  }
}

@media (max-width: 576px) {
  .mobile-panel-tabs {
    padding: 2px;
  }
  
  .mobile-panel-tab {
    padding: 4px 6px;
    min-width: 45px;
    font-size: 8px;
  }
  
  .panel-header {
    height: 32px;
    padding: 0 6px;
  }
  
  .panel-title {
    font-size: 11px;
  }
  
  .panel-btn {
    width: 24px;
    height: 24px;
  }
  
  .restore-panels {
    padding: 6px 8px;
    gap: 6px;
  }
  
  .btn-restore {
    padding: 4px 8px;
    font-size: 10px;
  }
}

/* Move Modal Search Results */
.search-results {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #ced4da;
  border-radius: 0 0 0.25rem 0.25rem;
  max-height: 200px;
  overflow-y: auto;
  z-index: 1000;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.search-result-item {
  padding: 8px 12px;
  cursor: pointer;
  border-bottom: 1px solid #f8f9fa;
}

.search-result-item:hover {
  background-color: #f8f9fa;
}

.search-result-item:last-child {
  border-bottom: none;
}

/* Tree拖拽样式 */
:deep(.el-tree--highlight-current .el-tree-node.is-current > .el-tree-node__content) {
  background-color: #4a90d9;
  color: white;
}

:deep(.el-tree-node__content.is-drop-inner) {
  background-color: rgba(74, 144, 217, 0.2) !important;
}

:deep(.el-tree-node__content.is-drop-prev),
:deep(.el-tree-node__content.is-drop-next) {
  background-color: rgba(74, 144, 217, 0.1) !important;
}

:deep(.el-tree-draggable .el-tree-node__content) {
  cursor: move;
}

:deep(.el-tree-draggable .el-tree-node__content:hover) {
  background-color: #f0f0f0;
}
</style>
