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
              <button class="panel-btn" title="Import classes" @click="showImportClassesModal = true">
                <svg class="wp-icon" viewBox="0 0 24 24" width="14" height="14">
                  <path d="M12 3v12M8 9l4-4 4 4M4 17h16" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>
              <button class="panel-btn" title="Filter" @click="toggleFilter">
                <svg class="wp-icon" viewBox="0 0 24 24" width="14" height="14">
                  <polygon points="3,5 21,5 14,12 14,19 10,19 10,12" fill="none" stroke="currentColor" stroke-width="1.5"/>
                </svg>
              </button>
            </div>
            <button class="panel-btn close-btn minimize-btn" :title="t('panel.minimize')" @click="showClassHierarchy = false">
              <span class="minimize-icon">_</span>
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
            :current-node-key="selectedClass?.id"
            show-line
            :expand-on-click-node="false"
          >
            <template #default="{ node, data }">
              <div class="custom-tree-node">
                <span class="custom-node-icon"></span>
                <span class="custom-node-label">{{ data.displayName || data.name }}</span>
              </div>
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
              <button class="panel-btn close-btn minimize-btn" :title="t('panel.minimize')" @click="showDescription = false">
                <span class="minimize-icon">_</span>
              </button>
            </div>
          </div>
          
          <div class="panel-body">
            <div v-if="isDescriptionLoading" class="loading-state">
              <div class="loading-spinner"></div>
              <span>Loading description...</span>
            </div>
            <div v-else class="description-section">
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
            <button class="panel-btn close-btn minimize-btn" :title="t('panel.minimize')" @click="showClassDetails = false">
              <span class="minimize-icon">_</span>
            </button>
          </div>
        </div>
        
        <div v-if="isClassLoading" class="panel-body">
          <div class="loading-state">
            <div class="loading-spinner"></div>
            <span>Loading class details...</span>
          </div>
        </div>
        <div v-else-if="selectedClass && activeClassTab === 'details'" class="panel-body">
          <div class="panel-content">
            <div class="detail-section">
              <h6 class="section-title">IRI</h6>
              <div class="iri-display">{{ selectedClass.iri || `http://www.w3.org/2002/07/owl#${selectedClass.name}` }}</div>
            </div>
          
          <div class="detail-section">
            <h6 class="section-title">Annotations</h6>
            <div class="annotation-list">
              <div v-for="(ann, index) in selectedClass.annotations || []" :key="index" class="annotation-item">
                <div class="annotation-content">
                  <div class="annotation-property-search">
                    <input 
                      type="text" 
                      class="annotation-property-input" 
                      v-model="ann.property" 
                      placeholder="Search property..."
                      @input="handleAnnotationPropertyInput(index, ann.property)"
                      @focus="handleAnnotationPropertyFocus(index)"
                      @blur="handleAnnotationPropertyBlur"
                    >
                    <div v-if="showAnnotationPropertyDropdown && currentAnnotationIndex === index" class="annotation-property-dropdown">
                      <div 
                        v-for="prop in filteredAnnotationProperties" 
                        :key="prop" 
                        class="annotation-property-option"
                        @mousedown="selectAnnotationProperty(index, prop)"
                      >
                        {{ prop }}
                      </div>
                    </div>
                  </div>
                  <input type="text" class="annotation-value-input" v-model="ann.value" placeholder="Enter value, for example: value-1@lang, value-2@lang">
                  <div class="annotation-lang-container">
                    <div class="annotation-lang-search">
                      <input 
                        type="text" 
                        class="annotation-lang-input" 
                        v-model="ann.language" 
                        placeholder="lang"
                        @input="handleAnnotationLangInput(index, ann.language)"
                        @focus="handleAnnotationLangFocus(index)"
                        @blur="handleAnnotationLangBlur(ann, index)"
                        @keyup.enter="handleAnnotationLangBlur(ann, index)"
                      >
                      <div v-if="showLanguageDropdown && currentAnnotationLangIndex === index" class="annotation-language-dropdown">
                        <div 
                          v-for="lang in filteredLanguages" 
                          :key="lang.code" 
                          class="annotation-language-option"
                          @mousedown="selectLanguage(index, lang)"
                        >
                          {{ lang.code }} ({{ lang.name }})
                        </div>
                      </div>
                    </div>
                    <div class="annotation-actions">
                      <button 
                        v-if="!ann.id" 
                        class="annotation-submit" 
                        @click="submitAnnotation(ann, index)"
                      >
                        <span class="submit-icon">✓</span>
                      </button>
                      <button 
                        v-if="ann.property || ann.value || ann.language" 
                        class="annotation-delete" 
                        @click="removeAnnotation(index)"
                      >
                        <span class="delete-icon">×</span>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
              <div class="annotation-input-row" v-if="showNewAnnotationInput">
                <div class="annotation-content">
                  <div class="annotation-property-search">
                    <input 
                      type="text" 
                      class="annotation-property-input" 
                      v-model="newAnnotation.property" 
                      placeholder="Search property..."
                      @input="handleAnnotationPropertyInput(-1, newAnnotation.property)"
                      @focus="handleNewAnnotationPropertyFocus"
                      @blur="handleAnnotationPropertyBlur"
                    >
                    <div v-if="showAnnotationPropertyDropdown && currentAnnotationIndex === -1" class="annotation-property-dropdown">
                      <div 
                        v-for="prop in filteredAnnotationProperties" 
                        :key="prop" 
                        class="annotation-property-option"
                        @mousedown="selectAnnotationProperty(-1, prop)"
                      >
                        {{ prop }}
                      </div>
                    </div>
                  </div>
                  <input type="text" class="annotation-value-input" v-model="newAnnotation.value" placeholder="Enter value, for example: value-1@lang, value-2@lang">
                  <div class="annotation-lang-container">
                    <div class="annotation-lang-search">
                      <input 
                        type="text" 
                        class="annotation-lang-input" 
                        v-model="newAnnotation.language" 
                        placeholder="lang"
                        @input="handleNewAnnotationLangInput(newAnnotation.language)"
                        @focus="handleNewAnnotationLangFocus"
                        @blur="handleNewAnnotationLangBlur"
                        @keyup.enter="handleNewAnnotationLangBlur"
                      >
                      <div v-if="showLanguageDropdown && currentAnnotationLangIndex === -1" class="annotation-language-dropdown">
                        <div 
                          v-for="lang in filteredLanguages" 
                          :key="lang.code" 
                          class="annotation-language-option"
                          @mousedown="selectNewAnnotationLanguage(lang)"
                        >
                          {{ lang.code }} ({{ lang.name }})
                        </div>
                      </div>
                    </div>
                    <button 
                      v-if="newAnnotation.property || newAnnotation.value || newAnnotation.language" 
                      class="annotation-delete" 
                      @click="cancelNewAnnotation"
                    >
                      <span class="delete-icon">×</span>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div class="detail-section">
            <h6 class="section-title">Parents</h6>
            <div class="parent-list">
              <div v-for="(parent, index) in selectedClass.superClasses || []" :key="index" class="parent-item">
                <div class="parent-content">
                  <span class="parent-checkbox"><i class="bi bi-circle"></i></span>
                  <span class="parent-name">{{ getClassNameById(parent) }}</span>
                </div>
                <button class="parent-delete" @click="removeParent(index)" :disabled="parent === 'owl:Thing'">
                  <span class="delete-icon">×</span>
                </button>
              </div>
              <div class="parent-input-row">
                <div class="parent-input-container">
                  <input 
                    type="text" 
                    class="parent-input" 
                    placeholder="Enter a class name" 
                    v-model="newParent.name"
                    @input="searchParents"
                    @focus="showParentSearchResults = true"
                    @blur="handleParentInputBlur"
                  >
                  <div class="parent-search-results" v-if="showParentSearchResults && parentSearchResults.length > 0">
                    <div 
                      v-for="cls in parentSearchResults" 
                      :key="cls.id" 
                      class="parent-search-result-item"
                      @click="selectParent(cls)"
                    >
                      {{ cls.name }}
                    </div>
                  </div>
                </div>
                <button class="parent-add" @click="addParent">
                  <span class="add-icon">+</span>
                </button>
              </div>
            </div>
          </div>
          
          <div class="detail-section">
            <h6 class="section-title">Relationships</h6>
            <div class="annotation-list">
              <div v-for="(rel, index) in selectedClass.relationships || []" :key="index" class="annotation-item">
                <div class="annotation-content">
                  <div class="annotation-property-search">
                    <input 
                      type="text" 
                      class="annotation-property-input" 
                      v-model="rel.property" 
                      placeholder="Enter property"
                      @focus="handleRelationshipPropertyFocus(index)"
                      @input="handleRelationshipPropertyInput(index)"
                      @blur="handleRelationshipPropertyBlur"
                    >
                    <div v-if="showRelationshipPropertyDropdown && currentRelationshipIndex === index" class="annotation-property-dropdown">
                      <div 
                        v-for="prop in filteredRelationshipProperties" 
                        :key="prop"
                        class="annotation-property-option"
                        @click="selectRelationshipProperty(prop, index)"
                      >
                        {{ prop }}
                      </div>
                    </div>
                  </div>
                  <div class="annotation-value-search">
                    <input 
                      type="text" 
                      class="annotation-value-input" 
                      v-model="rel.target" 
                      :placeholder="getRelationshipConfig(rel.property).placeholder"
                      @focus="handleRelationshipValueFocus(index)"
                      @input="handleRelationshipValueInput(index)"
                      @blur="handleRelationshipValueBlur(index)"
                    >
                    <!-- DataProperty类型：只显示DataTypes和New Datatype按钮 -->
                    <div v-if="showValueDropdown && currentValueIndex === index && getRelationshipConfig(rel.property).propertyType === 'datatype'" class="annotation-property-dropdown">
                      <div class="dropdown-section">
                        <div class="dropdown-section-title">Data Types</div>
                        <div 
                          v-for="datatype in filteredValues.dataTypes" 
                          :key="datatype"
                          class="annotation-property-option"
                          @click="selectRelationshipValue(datatype, index)"
                        >
                          {{ datatype }}
                        </div>
                      </div>
                      <div v-if="filteredValues.dataTypes.length === 0 && rel.target" class="dropdown-section">
                        <div class="dropdown-section-title">New</div>
                        <div class="annotation-property-option new-option" @click="createNewValue('datatype', index)">
                          New Datatype named {{ rel.target }}
                        </div>
                      </div>
                    </div>
                    
                    <!-- ObjectProperty类型：显示Classes/Individuals，有内容时显示New按钮 -->
                    <div v-if="showValueDropdown && currentValueIndex === index && getRelationshipConfig(rel.property).propertyType === 'object' && rel.target" class="annotation-property-dropdown">
                      <div class="dropdown-section">
                        <div class="dropdown-section-title">Classes</div>
                        <div 
                          v-for="cls in filteredValues.classes" 
                          :key="cls.id"
                          class="annotation-property-option"
                          @click="selectRelationshipValue(cls.name, index)"
                        >
                          {{ cls.name }}
                        </div>
                      </div>
                      <div class="dropdown-section">
                        <div class="dropdown-section-title">Individuals</div>
                        <div 
                          v-for="individual in filteredValues.individuals" 
                          :key="individual.id"
                          class="annotation-property-option"
                          @click="selectRelationshipValue(individual.name, index)"
                        >
                          {{ individual.name }}
                        </div>
                      </div>
                      <div v-if="filteredValues.classes.length === 0 && filteredValues.individuals.length === 0" class="dropdown-section">
                        <div class="dropdown-section-title">New</div>
                        <div class="annotation-property-option new-option" @click="createNewValue('individual', index)">
                          New Individual named {{ rel.target }}
                        </div>
                        <div class="annotation-property-option new-option" @click="createNewValue('class', index)">
                          New Class named {{ rel.target }}
                        </div>
                      </div>
                    </div>
                    
                    <!-- Default类型：显示所有选项 -->
                    <div v-if="showValueDropdown && currentValueIndex === index && getRelationshipConfig(rel.property).propertyType === 'default' && rel.target" class="annotation-property-dropdown">
                      <div class="dropdown-section">
                        <div class="dropdown-section-title">Classes</div>
                        <div 
                          v-for="cls in filteredValues.classes" 
                          :key="cls.id"
                          class="annotation-property-option"
                          @click="selectRelationshipValue(cls.name, index)"
                        >
                          {{ cls.name }}
                        </div>
                      </div>
                      <div class="dropdown-section">
                        <div class="dropdown-section-title">Individuals</div>
                        <div 
                          v-for="individual in filteredValues.individuals" 
                          :key="individual.id"
                          class="annotation-property-option"
                          @click="selectRelationshipValue(individual.name, index)"
                        >
                          {{ individual.name }}
                        </div>
                      </div>
                      <div class="dropdown-section">
                        <div class="dropdown-section-title">Data Types</div>
                        <div 
                          v-for="datatype in filteredValues.dataTypes" 
                          :key="datatype"
                          class="annotation-property-option"
                          @click="selectRelationshipValue(datatype, index)"
                        >
                          {{ datatype }}
                        </div>
                      </div>
                      <div class="dropdown-section">
                        <div class="dropdown-section-title">New</div>
                        <div class="annotation-property-option new-option" @click="createNewValue('individual', index)">
                          New Individual named {{ rel.target }}
                        </div>
                        <div class="annotation-property-option new-option" @click="createNewValue('class', index)">
                          New Class named {{ rel.target }}
                        </div>
                        <div class="annotation-property-option new-option" @click="createNewValue('datatype', index)">
                          New Datatype named {{ rel.target }}
                        </div>
                      </div>
                    </div>
                  </div>
                  <div v-if="getRelationshipConfig(rel.property).showLang" class="annotation-lang-container">
                    <div class="annotation-lang-search">
                      <input 
                      type="text" 
                      class="annotation-lang-input" 
                      v-model="rel.language" 
                      placeholder="lang"
                      @focus="handleRelationshipLangFocus(index)"
                      @input="handleRelationshipLangInput(index, rel.language)"
                      @blur="handleRelationshipLangBlur(rel, index)"
                    >
                    <div v-if="showLanguageDropdown && currentRelationshipLangIndex === index" class="annotation-language-dropdown">
                      <div 
                        v-for="lang in filteredLanguages" 
                        :key="lang.code" 
                        class="annotation-language-option"
                        @mousedown="selectRelationshipLanguage(index, lang)"
                      >
                        {{ lang.code }} ({{ lang.name }})
                      </div>
                    </div>
                    </div>
                    <div class="annotation-actions">
                      <button 
                        v-if="rel.property || rel.target || rel.language" 
                        class="annotation-delete" 
                        @click="removeRelationship(index)"
                      >
                        <span class="delete-icon">×</span>
                      </button>
                    </div>
                  </div>
                  <div v-else class="annotation-lang-container">
                    <div class="annotation-actions">
                      <button 
                        v-if="rel.property || rel.target" 
                        class="annotation-delete" 
                        @click="removeRelationship(index)"
                      >
                        <span class="delete-icon">×</span>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
              <div class="annotation-input-row" v-if="showNewRelationshipInput">
                <div class="annotation-content">
                  <div class="annotation-property-search">
                    <input 
                      type="text" 
                      class="annotation-property-input" 
                      v-model="newRelationship.property" 
                      placeholder="Enter property"
                      @focus="handleNewRelationshipPropertyFocus"
                      @input="handleNewRelationshipPropertyInput"
                      @blur="handleRelationshipPropertyBlur"
                    >
                    <div v-if="showRelationshipPropertyDropdown && currentRelationshipIndex === -1" class="annotation-property-dropdown">
                      <div 
                        v-for="prop in filteredRelationshipProperties" 
                        :key="prop"
                        class="annotation-property-option"
                        @click="selectNewRelationshipProperty(prop)"
                      >
                        {{ prop }}
                      </div>
                    </div>
                  </div>
                  <div class="annotation-value-search">
                    <input 
                      type="text" 
                      class="annotation-value-input" 
                      v-model="newRelationship.target" 
                      :placeholder="getRelationshipConfig(newRelationship.property).placeholder"
                      @focus="handleNewRelationshipValueFocus"
                      @input="handleNewRelationshipValueInput"
                      @blur="handleNewRelationshipValueBlur"
                    >
                    <!-- DataProperty类型：只显示DataTypes和New Datatype按钮 -->
                    <div v-if="showValueDropdown && currentValueIndex === -1 && getRelationshipConfig(newRelationship.property).propertyType === 'datatype'" class="annotation-property-dropdown">
                      <div class="dropdown-section">
                        <div class="dropdown-section-title">Data Types</div>
                        <div 
                          v-for="datatype in filteredValues.dataTypes" 
                          :key="datatype"
                          class="annotation-property-option"
                          @click="selectNewRelationshipValue(datatype)"
                        >
                          {{ datatype }}
                        </div>
                      </div>
                      <div v-if="filteredValues.dataTypes.length === 0 && newRelationship.target" class="dropdown-section">
                        <div class="dropdown-section-title">New</div>
                        <div class="annotation-property-option new-option" @click="createNewValue('datatype', -1)">
                          New Datatype named {{ newRelationship.target }}
                        </div>
                      </div>
                    </div>
                    
                    <!-- ObjectProperty类型：显示Classes/Individuals，有内容时显示New按钮 -->
                    <div v-if="showValueDropdown && currentValueIndex === -1 && getRelationshipConfig(newRelationship.property).propertyType === 'object' && newRelationship.target" class="annotation-property-dropdown">
                      <div class="dropdown-section">
                        <div class="dropdown-section-title">Classes</div>
                        <div 
                          v-for="cls in filteredValues.classes" 
                          :key="cls.id"
                          class="annotation-property-option"
                          @click="selectNewRelationshipValue(cls.name)"
                        >
                          {{ cls.name }}
                        </div>
                      </div>
                      <div class="dropdown-section">
                        <div class="dropdown-section-title">Individuals</div>
                        <div 
                          v-for="individual in filteredValues.individuals" 
                          :key="individual.id"
                          class="annotation-property-option"
                          @click="selectNewRelationshipValue(individual.name)"
                        >
                          {{ individual.name }}
                        </div>
                      </div>
                      <div v-if="filteredValues.classes.length === 0 && filteredValues.individuals.length === 0" class="dropdown-section">
                        <div class="dropdown-section-title">New</div>
                        <div class="annotation-property-option new-option" @click="createNewValue('individual', -1)">
                          New Individual named {{ newRelationship.target }}
                        </div>
                        <div class="annotation-property-option new-option" @click="createNewValue('class', -1)">
                          New Class named {{ newRelationship.target }}
                        </div>
                      </div>
                    </div>
                    
                    <!-- Default类型：显示所有选项 -->
                    <div v-if="showValueDropdown && currentValueIndex === -1 && getRelationshipConfig(newRelationship.property).propertyType === 'default' && newRelationship.target" class="annotation-property-dropdown">
                      <div class="dropdown-section">
                        <div class="dropdown-section-title">Classes</div>
                        <div 
                          v-for="cls in filteredValues.classes" 
                          :key="cls.id"
                          class="annotation-property-option"
                          @click="selectNewRelationshipValue(cls.name)"
                        >
                          {{ cls.name }}
                        </div>
                      </div>
                      <div class="dropdown-section">
                        <div class="dropdown-section-title">Individuals</div>
                        <div 
                          v-for="individual in filteredValues.individuals" 
                          :key="individual.id"
                          class="annotation-property-option"
                          @click="selectNewRelationshipValue(individual.name)"
                        >
                          {{ individual.name }}
                        </div>
                      </div>
                      <div class="dropdown-section">
                        <div class="dropdown-section-title">Data Types</div>
                        <div 
                          v-for="datatype in filteredValues.dataTypes" 
                          :key="datatype"
                          class="annotation-property-option"
                          @click="selectNewRelationshipValue(datatype)"
                        >
                          {{ datatype }}
                        </div>
                      </div>
                      <div class="dropdown-section">
                        <div class="dropdown-section-title">New</div>
                        <div class="annotation-property-option new-option" @click="createNewValue('individual', -1)">
                          New Individual named {{ newRelationship.target }}
                        </div>
                        <div class="annotation-property-option new-option" @click="createNewValue('class', -1)">
                          New Class named {{ newRelationship.target }}
                        </div>
                        <div class="annotation-property-option new-option" @click="createNewValue('datatype', -1)">
                          New Datatype named {{ newRelationship.target }}
                        </div>
                      </div>
                    </div>
                  </div>
                  <div v-if="getRelationshipConfig(newRelationship.property).showLang" class="annotation-lang-container">
                    <div class="annotation-lang-search">
                      <input 
                        type="text" 
                        class="annotation-lang-input" 
                        v-model="newRelationship.language" 
                        placeholder="lang"
                        @focus="handleNewRelationshipLangFocus"
                        @input="handleNewRelationshipLangInput(newRelationship.language)"
                        @blur="handleNewRelationshipLangBlur"
                      >
                      <div v-if="showLanguageDropdown && currentRelationshipLangIndex === -1" class="annotation-language-dropdown">
                        <div 
                          v-for="lang in filteredLanguages" 
                          :key="lang.code" 
                          class="annotation-language-option"
                          @mousedown="selectNewRelationshipLanguage(lang)"
                        >
                          {{ lang.code }} ({{ lang.name }})
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
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
              <button class="panel-btn close-btn minimize-btn" :title="t('panel.minimize')" @click="showComments = false">
                <span class="minimize-icon">_</span>
              </button>
            </div>
          </div>
          <div class="panel-body">
            <div v-if="isCommentsLoading" class="loading-state">
              <div class="loading-spinner"></div>
              <span>Loading comments...</span>
            </div>
            <div v-else-if="filteredComments && filteredComments.length > 0" class="comments-list">
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
              <button class="panel-btn close-btn minimize-btn" :title="t('panel.minimize')" @click="showProjectFeed = false">
                <span class="minimize-icon">_</span>
              </button>
            </div>
          </div>
          <div class="panel-body">
            <div v-if="isFeedLoading" class="loading-state">
              <div class="loading-spinner"></div>
              <span>Loading project feed...</span>
            </div>
            <div v-else-if="projectActivities && projectActivities.length > 0" class="activities-list">
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

    <!-- Import Classes Modal -->
    <div class="modal fade" :class="{ 'show': showImportClassesModal }" tabindex="-1" v-if="showImportClassesModal">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Import Classes</h5>
            <button type="button" class="btn-close" @click="showImportClassesModal = false"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="handleImportClasses">
              <div class="mb-3">
                <label for="importScript" class="form-label">Please enter turtle/rdfs script:</label>
                <textarea 
                  class="form-control" 
                  id="importScript" 
                  v-model="importScript" 
                  rows="12" 
                  required
                  placeholder="@prefix rdf: &lt;http://www.w3.org/1999/02/22-rdf-syntax-ns#&gt; .
@prefix rdfs: &lt;http://www.w3.org/2000/01/rdf-schema#&gt; .
@prefix : &lt;http://example.org/device#&gt; .

:Office a rdfs:Class ;
  rdfs:subClassOf :PhysicalLocation ;
  rdfs:label &quot;Office&quot;@en ."
                ></textarea>
              </div>
              <div class="d-flex gap-2">
                <button type="submit" class="btn btn-primary btn-sm">Import</button>
                <button type="button" class="btn btn-secondary btn-sm" @click="showImportClassesModal = false">Cancel</button>
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
                <label for="annotationPredicate" class="form-label">Property</label>
                <input type="text" class="form-control form-control-sm" id="annotationPredicate" v-model="newAnnotation.property" placeholder="e.g., rdfs:comment">
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
import { useI18n } from 'vue-i18n'
import * as echarts from 'echarts'
import http from '@/utils/http'
import { ElMessage } from 'element-plus'

const { t } = useI18n()

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
const showImportClassesModal = ref(false)
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

// 导入脚本数据
const importScript = ref('')

// 新的输入字段数据
const newAnnotation = ref({
  property: '',
  value: '',
  language: ''
})

const showNewAnnotationInput = ref(false)

// 存储原始Annotations数据，用于比较是否有更改
const originalAnnotations = ref([])

// 加载状态管理
const isClassLoading = ref(false)
const isCommentsLoading = ref(false)
const isFeedLoading = ref(false)
const isDescriptionLoading = ref(false)

// Annotation属性搜索相关
const annotationProperties = ref([])
const filteredAnnotationProperties = ref([])
const showAnnotationPropertyDropdown = ref(false)
const annotationPropertySearchKeyword = ref('')
const currentAnnotationIndex = ref(-1) // -1表示新annotation，>=0表示已有annotation的索引

// 语言列表相关
const languages = ref([
  { code: 'en', name: 'English' },
  { code: 'zh', name: 'Chinese' },
  { code: 'zh-CN', name: 'Chinese; PRC' },
  { code: 'zh-HK', name: 'Chinese; Hong Kong' },
  { code: 'zh-TW', name: 'Chinese; Taiwan' },
  { code: 'fr', name: 'French' },
  { code: 'de', name: 'German' },
  { code: 'es', name: 'Spanish' },
  { code: 'it', name: 'Italian' },
  { code: 'ja', name: 'Japanese' },
  { code: 'ko', name: 'Korean' },
  { code: 'ru', name: 'Russian' },
  { code: 'pt', name: 'Portuguese' },
  { code: 'ar', name: 'Arabic' },
  { code: 'zu', name: 'Zulu' },
  { code: 'za', name: 'Zhuang; Chuang' }
])
const filteredLanguages = ref([])
const showLanguageDropdown = ref(false)
const currentAnnotationLangIndex = ref(-1)

// Relationship属性相关
const relationshipProperties = ref([])
const filteredRelationshipProperties = ref([])
const showRelationshipPropertyDropdown = ref(false)
const relationshipPropertySearchKeyword = ref('')
const currentRelationshipIndex = ref(-1)
const currentRelationshipLangIndex = ref(-1)

// 默认的OWL属性
const defaultOWLProperties = ref([
  'owl:bottomDataProperty',
  'owl:bottomObjectProperty',
  'owl:topDataProperty',
  'owl:topObjectProperty'
])

// 可用的value数据源
const valueDataSources = ref({
  classes: [],
  individuals: [],
  dataTypes: [
    'xsd:anyURI',
    'xsd:base64Binary',
    'xsd:boolean',
    'xsd:byte',
    'xsd:dateTime',
    'xsd:dateTimeStamp',
    'xsd:decimal',
    'xsd:double',
    'xsd:float',
    'xsd:hexBinary',
    'xsd:int',
    'xsd:integer',
    'xsd:language',
    'xsd:long',
    'xsd:Name',
    'xsd:NCName',
    'xsd:negativeInteger',
    'xsd:NMTOKEN',
    'xsd:nonNegativeInteger',
    'xsd:nonPositiveInteger',
    'owl:rational',
    'owl:real',
    'rdf:langString',
    'rdf:PlainLiteral',
    'rdf:XMLLiteral',
    'rdfs:Literal'
  ]
})

// Value相关状态
const showValueDropdown = ref(false)
const currentValueIndex = ref(-1)
const filteredValues = ref({
  classes: [],
  individuals: [],
  dataTypes: []
})
const valueSearchKeyword = ref('')

const newParent = ref({
  name: ''
})

const parentSearchResults = ref([])
const showParentSearchResults = ref(false)

const newRelationship = ref({
  property: '',
  target: '',
  language: ''
})

const showNewRelationshipInput = ref(true)

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
  
  // 加载Relationship属性数据
  await loadRelationshipProperties()
  
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
    displayName: 'owl:Thing',
    children: []
  }
  
  if (!classes || classes.length === 0) {
    return [owlThingNode]
  }
  
  // 创建类ID到类对象的映射
  const classMap = new Map()
  classes.forEach(cls => {
    // 计算节点的显示名称
    const displayName = getNodeDisplayName(cls)
    
    classMap.set(cls.id, {
      ...cls,
      displayName,
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

// 根据优先级规则获取节点的显示名称
const getNodeDisplayName = (cls) => {
  // 项目默认语言
  const defaultLanguage = projectDefaultLanguage.value
  
  // 首先查找匹配项目语言的rdfs:label
  if (cls.annotations && Array.isArray(cls.annotations)) {
    // 查找rdfs:label且语言匹配项目语言的annotation
    const labelWithLanguage = cls.annotations.find(ann => 
      ann.property === 'rdfs:label' && ann.language === defaultLanguage
    )
    if (labelWithLanguage && labelWithLanguage.value) {
      return labelWithLanguage.value
    }
    
    // 其次查找rdfs:label且语言为空的annotation
    const labelWithoutLanguage = cls.annotations.find(ann => 
      ann.property === 'rdfs:label' && (!ann.language || ann.language === '')
    )
    if (labelWithoutLanguage && labelWithoutLanguage.value) {
      return labelWithoutLanguage.value
    }
  }
  
  // 最后使用类名
  return cls.name
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

// 根据ID获取类名称
const getClassNameById = (classId) => {
  if (classId === 'owl:Thing') {
    return 'owl:Thing'
  }
  const foundClass = classHierarchy.value.find(c => c.id === classId)
  return foundClass ? foundClass.name : classId
}

// 加载类详情
const loadClassDetails = async (classId) => {
  try {
    const response = await http.get(`/class/findById/${classId}`)
    selectedClass.value = response.data
    emit('class-selected', selectedClass.value)
    
    // 加载完整的Annotation数据（包含id）
    const annotationsResponse = await http.get(`/annotation/findByEntityIdAndEntityType/${classId}/CLASS`)
    selectedClass.value.annotations = annotationsResponse.data || []
    
    // 加载Relationships数据
    console.log('Loading relationships for class:', classId)
    const relationshipsResponse = await http.get(`/relationship/findByEntityIdAndEntityType/${classId}/CLASS`)
    console.log('Relationships response:', relationshipsResponse.data)
    selectedClass.value.relationships = relationshipsResponse.data || []
    // 将后端返回的value属性转换为target属性
    selectedClass.value.relationships.forEach(rel => {
      if (rel.value) {
        rel.target = rel.value
      }
    })
    console.log('Relationships loaded:', selectedClass.value.relationships)
    
    // 如果annotations为空，添加默认的rdfs:label annotation
    if (!selectedClass.value.annotations || selectedClass.value.annotations.length === 0) {
      selectedClass.value.annotations = [{
        property: 'rdfs:label',
        value: selectedClass.value.name,
        language: ''
      }]
    }
    
    // 保存原始Annotations数据，用于比较是否有更改
    originalAnnotations.value = JSON.parse(JSON.stringify(selectedClass.value.annotations || []))
    

    
    // 加载Relationship属性数据
    await loadRelationshipProperties()
    
    // 加载value数据源
    await loadValueDataSources()
    
    // 加载类变更历史
    // 注意：暂时注释掉这个调用，因为我们还没有实现 change 相关的 API
    // const changesResponse = await http.get(`/change/findByEntityId/${classId}`)
    // classChanges.value = changesResponse.data || []
  } catch (error) {
    console.error('Failed to load class details:', error)
  }
}

// 清除面板数据
const clearPanelData = () => {
  // 清除class数据
  selectedClass.value = null
  isClassLoading.value = true
  
  // 清除comments数据
  comments.value = []
  isCommentsLoading.value = true
  
  // 清除project feed数据
  projectActivities.value = []
  isFeedLoading.value = true
  
  // 清除description数据
  isDescriptionLoading.value = true
}

// 带加载状态的class详情加载
const loadClassDetailsWithLoading = async (classId) => {
  try {
    isClassLoading.value = true
    await loadClassDetails(classId)
  } finally {
    isClassLoading.value = false
  }
}

// 带加载状态的评论加载
const loadCommentsWithLoading = async (classId) => {
  try {
    isCommentsLoading.value = true
    // 尝试根据classId加载评论
    try {
      const response = await http.get(`/collaboration/comment/findByEntityId/${classId}`)
      comments.value = response.data || []
    } catch (error) {
      // 如果失败，回退到加载所有评论
      await loadComments()
    }
  } finally {
    isCommentsLoading.value = false
  }
}

// 带加载状态的项目活动加载
const loadProjectActivitiesWithLoading = async () => {
  try {
    isFeedLoading.value = true
    await loadProjectActivities()
  } finally {
    isFeedLoading.value = false
  }
}

// 带加载状态的描述加载
const loadDescriptionWithLoading = async (classId) => {
  try {
    isDescriptionLoading.value = true
    // 描述数据包含在class详情中
    await loadClassDetails(classId)
  } finally {
    isDescriptionLoading.value = false
  }
}

// 节点点击处理
const handleNodeClick = async (data) => {
  // 清除现有数据，显示加载状态
  clearPanelData()
  
  try {
    // 并行加载所有数据
    await Promise.all([
      loadClassDetailsWithLoading(data.id),
      loadCommentsWithLoading(data.id),
      loadProjectActivitiesWithLoading(),
      loadDescriptionWithLoading(data.id)
    ])
  } catch (error) {
    console.error('Failed to load panel data:', error)
  }
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
    await http.post('/class/move', {
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
    await http.post('/class/merge', {
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
    const response = await http.get(`/class/search`, {
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
    const response = await http.get(`/class/findByOntologyId/${props.projectId}`)
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
    const response = await http.get(`/class/findByOntologyId/${props.projectId}`)
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

// Annotations相关方法
// 搜索Annotation属性
const searchAnnotationProperties = async (keyword = '') => {
  try {
    console.log('Searching annotation properties with keyword:', keyword)
    const response = await http.get('/annotation/properties', {
      params: { keyword }
    })
    console.log('Received annotation properties:', response.data)
    annotationProperties.value = response.data
    
    // 根据关键字过滤
    if (keyword) {
      filteredAnnotationProperties.value = response.data.filter(prop =>
        prop.toLowerCase().includes(keyword.toLowerCase())
      )
    } else {
      filteredAnnotationProperties.value = response.data
    }
    console.log('Filtered annotation properties:', filteredAnnotationProperties.value)
  } catch (error) {
    console.error('Failed to search annotation properties:', error)
  }
}

// 处理Annotation属性输入
const handleAnnotationPropertyInput = (index, keyword) => {
  console.log('handleAnnotationPropertyInput called with index:', index, 'keyword:', keyword)
  currentAnnotationIndex.value = index
  annotationPropertySearchKeyword.value = keyword
  
  // 直接从后端获取数据，不管annotationProperties是否为空
  searchAnnotationProperties(keyword).then(() => {
    showAnnotationPropertyDropdown.value = true
  })
  
  console.log('filteredAnnotationProperties:', filteredAnnotationProperties.value)
  console.log('showAnnotationPropertyDropdown:', showAnnotationPropertyDropdown.value)
}

// 处理Annotation语言输入
const handleAnnotationLangInput = (index, keyword) => {
  console.log('handleAnnotationLangInput called with index:', index, 'keyword:', keyword)
  currentAnnotationLangIndex.value = index
  
  // 根据关键字过滤语言列表
  if (keyword) {
    const query = keyword.toLowerCase()
    filteredLanguages.value = languages.value.filter(lang => 
      lang.code.toLowerCase().includes(query) || 
      lang.name.toLowerCase().includes(query)
    )
  } else {
    filteredLanguages.value = languages.value
  }
  showLanguageDropdown.value = true
  
  console.log('filteredLanguages:', filteredLanguages.value)
  console.log('showLanguageDropdown:', showLanguageDropdown.value)
}

// 处理新Annotation语言输入
const handleNewAnnotationLangInput = (keyword) => {
  console.log('handleNewAnnotationLangInput called with keyword:', keyword)
  currentAnnotationLangIndex.value = -1
  
  // 根据关键字过滤语言列表
  if (keyword) {
    const query = keyword.toLowerCase()
    filteredLanguages.value = languages.value.filter(lang => 
      lang.code.toLowerCase().includes(query) || 
      lang.name.toLowerCase().includes(query)
    )
  } else {
    filteredLanguages.value = languages.value
  }
  showLanguageDropdown.value = true
  
  console.log('filteredLanguages:', filteredLanguages.value)
  console.log('showLanguageDropdown:', showLanguageDropdown.value)
}

// 选择Annotation属性
const selectAnnotationProperty = (index, property) => {
  console.log('selectAnnotationProperty called with index:', index, 'property:', property)
  if (index === -1) {
    // 新annotation
    newAnnotation.value.property = property
  } else {
    // 已有annotation
    selectedClass.value.annotations[index].property = property
  }
  showAnnotationPropertyDropdown.value = false
  annotationPropertySearchKeyword.value = ''
  console.log('showAnnotationPropertyDropdown set to false')
}

// 选择语言
const selectLanguage = (index, language) => {
  console.log('selectLanguage called with index:', index, 'language:', language)
  selectedClass.value.annotations[index].language = language.code
  showLanguageDropdown.value = false
}

// 选择新Annotation语言
const selectNewAnnotationLanguage = (language) => {
  console.log('selectNewAnnotationLanguage called with language:', language)
  newAnnotation.value.language = language.code
  showLanguageDropdown.value = false
}

// 关闭Annotation属性下拉框
const closeAnnotationPropertyDropdown = () => {
  console.log('closeAnnotationPropertyDropdown called')
  setTimeout(() => {
    showAnnotationPropertyDropdown.value = false
    console.log('showAnnotationPropertyDropdown set to false')
  }, 300)
}

// 处理Annotation属性输入框的blur事件
const handleAnnotationPropertyBlur = () => {
  console.log('handleAnnotationPropertyBlur called')
  // 延迟隐藏下拉框，以便点击下拉项时能触发选择事件
  setTimeout(() => {
    showAnnotationPropertyDropdown.value = false
    console.log('showAnnotationPropertyDropdown set to false on blur')
  }, 200)
}

// 处理Annotation属性输入框的focus事件
const handleAnnotationPropertyFocus = (index) => {
  console.log('handleAnnotationPropertyFocus called with index:', index)
  currentAnnotationIndex.value = index
  const keyword = selectedClass.value.annotations[index]?.property || ''
  console.log('keyword for focus:', keyword)
  searchAnnotationProperties(keyword).then(() => {
    showAnnotationPropertyDropdown.value = true
    console.log('showAnnotationPropertyDropdown set to true')
  })
}

// 处理新Annotation属性输入框的focus事件
const handleNewAnnotationPropertyFocus = () => {
  console.log('handleNewAnnotationPropertyFocus called')
  currentAnnotationIndex.value = -1
  const keyword = newAnnotation.value.property || ''
  console.log('keyword for new annotation focus:', keyword)
  searchAnnotationProperties(keyword).then(() => {
    showAnnotationPropertyDropdown.value = true
    console.log('showAnnotationPropertyDropdown set to true')
  })
}

// 处理Annotation语言焦点
const handleAnnotationLangFocus = (index) => {
  console.log('handleAnnotationLangFocus called with index:', index)
  currentAnnotationLangIndex.value = index
  showLanguageDropdown.value = true
  filteredLanguages.value = languages.value
  
  // 当标签和value都有数据时，点击lang输入框会在下边新增加1行数据
  const ann = selectedClass.value.annotations[index]
  if (ann && ann.property && ann.value) {
    // 检查是否已经有新的输入行
    if (!showNewAnnotationInput.value) {
      showNewAnnotationInput.value = true
    }
  }
}

// 处理Annotation语言失焦
const handleAnnotationLangBlur = async (ann, index) => {
  console.log('handleAnnotationLangBlur called with annotation:', ann, 'index:', index)
  showLanguageDropdown.value = false
  
  // 当焦点离开lang输入框时，向后台更新数据
  if (ann && (ann.property || ann.value || ann.language)) {
    // 检查数据是否有变化
    if (!isAnnotationChanged(ann, index)) {
      console.log('Annotation data unchanged, skipping submit')
      return
    }
    
    try {
      // 检查value是否包含多个annotation
      if (ann.value && ann.value.includes(',')) {
        // 使用新的process端点处理多个annotation
        await http.post('/annotation/process', {
          entityId: selectedClass.value.id,
          entityType: 'CLASS',
          property: ann.property,
          value: ann.value
        })
      } else {
        // 检查是否有重复的Annotation
        const hasDuplicate = checkAnnotationDuplicate(ann, index)
        if (hasDuplicate) {
          ElMessage.warning('Duplicate annotation: same property and language combination')
          return
        }
        
        if (ann.id) {
          // 更新操作
          await http.put(`/annotation/update/${ann.id}`, {
            entityId: selectedClass.value.id,
            entityType: 'CLASS',
            property: ann.property,
            language: ann.language,
            value: ann.value
          })
        } else {
          // 新增操作
          await http.post('/annotation/create', {
            entityId: selectedClass.value.id,
            entityType: 'CLASS',
            property: ann.property,
            language: ann.language,
            value: ann.value
          })
        }
      }
      await loadClassDetails(selectedClass.value.id)
      
      // 更新class名称
      updateClassName()
      
      // 如果提交的是rdfs:label annotation，重新加载类层次结构以更新节点显示名称
      if (ann.property === 'rdfs:label' && (ann.language === projectDefaultLanguage.value || !ann.language)) {
        await loadClassHierarchy()
      }
    } catch (error) {
      console.error('Failed to update annotation:', error)
      ElMessage.error('Failed to update annotation')
    }
  }
}

// 处理新Annotation语言焦点
const handleNewAnnotationLangFocus = () => {
  console.log('handleNewAnnotationLangFocus called')
  currentAnnotationLangIndex.value = -1
  showLanguageDropdown.value = true
  filteredLanguages.value = languages.value
  
  // 当新annotation的lang输入框获得焦点时，确保显示新输入行
  showNewAnnotationInput.value = true
}

// 处理新Annotation语言失焦
const handleNewAnnotationLangBlur = async () => {
  console.log('handleNewAnnotationLangBlur called with newAnnotation:', newAnnotation.value)
  showLanguageDropdown.value = false
  
  // 当新annotation的lang输入框失去焦点时，添加新数据
  if (newAnnotation.value.property && newAnnotation.value.value) {
    try {
      // 检查value是否包含多个annotation
      if (newAnnotation.value.value.includes(',')) {
        // 使用新的process端点处理多个annotation
        await http.post('/annotation/process', {
          entityId: selectedClass.value.id,
          entityType: 'CLASS',
          property: newAnnotation.value.property,
          value: newAnnotation.value.value
        })
      } else {
        // 检查是否有重复的Annotation
        const hasDuplicate = checkAnnotationDuplicate(newAnnotation.value, -1)
        if (hasDuplicate) {
          ElMessage.warning('Duplicate annotation: same property and language combination')
          return
        }
        
        await http.post('/annotation/create', {
          entityId: selectedClass.value.id,
          entityType: 'CLASS',
          property: newAnnotation.value.property,
          language: newAnnotation.value.language,
          value: newAnnotation.value.value
        })
      }
      // 保存提交的annotation属性，用于后续检查
      const submittedProperty = newAnnotation.value.property
      const submittedLanguage = newAnnotation.value.language
      
      await loadClassDetails(selectedClass.value.id)
      
      // 重置新Annotation输入
      newAnnotation.value = {
        property: '',
        value: '',
        language: ''
      }
      
      // 如果提交的是rdfs:label annotation，重新加载类层次结构以更新节点显示名称
      if (submittedProperty === 'rdfs:label' && (submittedLanguage === projectDefaultLanguage.value || !submittedLanguage)) {
        await loadClassHierarchy()
      }
      
      // 更新class名称
      updateClassName()
    } catch (error) {
      console.error('Failed to add annotation:', error)
      ElMessage.error('Failed to add annotation')
    }
  }
}

// 提交Annotation
const submitAnnotation = async (ann, index) => {
  // 检查property和value是否为空
  if (!ann.property) {
    ElMessage.warning('Property is required')
    return
  }
  if (!ann.value) {
    ElMessage.warning('Value is required')
    return
  }
  
  try {
    // 检查value是否包含多个annotation
    if (ann.value.includes(',')) {
      // 使用新的process端点处理多个annotation
      await http.post('/annotation/process', {
        entityId: selectedClass.value.id,
        entityType: 'CLASS',
        property: ann.property,
        value: ann.value
      })
    } else {
      await http.post('/annotation/create', {
        entityId: selectedClass.value.id,
        entityType: 'CLASS',
        property: ann.property,
        language: ann.language,
        value: ann.value
      })
    }
    await loadClassDetails(selectedClass.value.id)
    
    // 更新class名称
    updateClassName()
    
    // 如果提交的是rdfs:label annotation，重新加载类层次结构以更新节点显示名称
    if (ann.property === 'rdfs:label' && (ann.language === projectDefaultLanguage.value || !ann.language)) {
      await loadClassHierarchy()
    }
  } catch (error) {
    console.error('Failed to submit annotation:', error)
    ElMessage.error('Failed to submit annotation')
  }
}

// 检查Annotation重复
const checkAnnotationDuplicate = (annotation, currentIndex) => {
  console.log('checkAnnotationDuplicate called with annotation:', annotation, 'currentIndex:', currentIndex)
  const annotations = selectedClass.value.annotations || []
  return annotations.some((ann, index) => {
    if (index === currentIndex) return false
    return ann.property === annotation.property && ann.language === annotation.language
  })
}

// 比较Annotation数据是否与原始数据一致
const isAnnotationChanged = (annotation, index) => {
  console.log('isAnnotationChanged called with annotation:', annotation, 'index:', index)
  if (!originalAnnotations.value || index < 0 || index >= originalAnnotations.value.length) {
    return true // 新添加的Annotation，视为有变化
  }
  
  const originalAnn = originalAnnotations.value[index]
  if (!originalAnn) {
    return true // 原始数据中没有该Annotation，视为有变化
  }
  
  // 比较property、value、language是否一致
  return originalAnn.property !== annotation.property || 
         originalAnn.value !== annotation.value || 
         originalAnn.language !== annotation.language
}

// 更新class名称
const updateClassName = () => {
  console.log('updateClassName called')
  const annotations = selectedClass.value.annotations || []
  // 查找rdfs:label的Annotation
  const labelAnnotation = annotations.find(ann => 
    ann.property === 'rdfs:label' && (ann.language === projectDefaultLanguage.value || !ann.language)
  )
  
  if (labelAnnotation && labelAnnotation.value) {
    selectedClass.value.name = labelAnnotation.value
  } else {
    // 使用uid作为名称
    selectedClass.value.name = selectedClass.value.id
  }
}

const removeAnnotation = async (index) => {
  if (!selectedClass.value) return
  
  try {
    const ann = selectedClass.value.annotations[index]
    if (ann) {
      // 保存要删除的annotation属性，用于后续检查
      const deletedProperty = ann.property
      const deletedLanguage = ann.language
      
      if (ann.id) {
        // 使用id删除
        await http.delete(`/annotation/delete/${ann.id}`)
      } else {
        // 使用property和language删除
        await http.delete('/annotation/delete', {
          params: {
            entityId: selectedClass.value.id,
            entityType: 'CLASS',
            property: ann.property,
            language: ann.language
          }
        })
      }
      await loadClassDetails(selectedClass.value.id)
      
      // 更新class名称
      updateClassName()
      
      // 如果删除的是rdfs:label annotation，重新加载类层次结构以更新节点显示名称
      if (deletedProperty === 'rdfs:label' && (deletedLanguage === projectDefaultLanguage.value || !deletedLanguage)) {
        await loadClassHierarchy()
      }
    }
  } catch (error) {
    console.error('Failed to remove annotation:', error)
    ElMessage.error('Failed to remove annotation')
  }
}

const addAnnotation = async () => {
  if (!selectedClass.value || !newAnnotation.value.property || !newAnnotation.value.value) return
  
  try {
    await http.post('/annotation/create', {
      entityId: selectedClass.value.id,
      entityType: 'CLASS',
      property: newAnnotation.value.property,
      language: newAnnotation.value.language,
      value: newAnnotation.value.value
    })
    await loadClassDetails(selectedClass.value.id)
    // 重置新annotation表单
    newAnnotation.value = {
      property: '',
      value: '',
      language: ''
    }
  } catch (error) {
    console.error('Failed to add annotation:', error)
  }
}

// Parents相关方法
const searchParents = async () => {
  if (!newParent.value.name) {
    parentSearchResults.value = []
    return
  }
  
  try {
    const response = await http.get(`/class/search`, {
      params: { query: newParent.value.name, projectId: props.projectId }
    })
    // 过滤掉当前选中的类
    if (selectedClass.value) {
      parentSearchResults.value = (response.data || []).filter(cls => cls.id !== selectedClass.value.id)
    } else {
      parentSearchResults.value = response.data || []
    }
  } catch (error) {
    console.error('Failed to search parents:', error)
    parentSearchResults.value = []
  }
}

const handleParentInputBlur = () => {
  // 延迟关闭搜索结果，以便点击结果时能触发选择事件
  setTimeout(() => {
    showParentSearchResults.value = false
  }, 200)
}

const selectParent = (cls) => {
  newParent.value.name = cls.name
  showParentSearchResults.value = false
}

const addParent = async () => {
  if (!selectedClass.value || !newParent.value.name) return
  
  try {
    // 查找选中的类
    const cls = classHierarchy.value.find(c => c.name === newParent.value.name)
    if (!cls) return
    
    // 检查是否已经是父类
    if (selectedClass.value.superClasses && selectedClass.value.superClasses.includes(cls.id)) {
      return
    }
    
    await http.post(`/class/addSuperClass/${selectedClass.value.id}/${cls.id}`)
    await loadClassDetails(selectedClass.value.id)
    // 重置新parent表单
    newParent.value.name = ''
    ElMessage.success('Parent added successfully')
  } catch (error) {
    console.error('Failed to add parent:', error)
  }
}

const removeParent = async (index) => {
  if (!selectedClass.value) return
  
  try {
    const parentId = selectedClass.value.superClasses[index]
    if (parentId === 'owl:Thing') return
    
    await http.post('/class/removeSuperClass', {
      classId: selectedClass.value.id,
      superClassId: parentId
    })
    await loadClassDetails(selectedClass.value.id)
  } catch (error) {
    console.error('Failed to remove parent:', error)
  }
}

// 加载Relationship属性数据
const loadRelationshipProperties = async () => {
  try {
    // 从缓存中获取数据
    const cachedProperties = localStorage.getItem('relationshipProperties')
    if (cachedProperties) {
      try {
        relationshipProperties.value = JSON.parse(cachedProperties)
        console.log('Using cached relationship properties:', relationshipProperties.value)
      } catch (error) {
        console.error('Failed to parse cached relationship properties:', error)
      }
    }
    
    // 从后端API获取标准OWL属性
    let standardOWLProperties = []
    try {
      const owlPropertiesResponse = await http.get('/metadata/owl/properties')
      standardOWLProperties = owlPropertiesResponse.data || []
    } catch (error) {
      console.error('Failed to load standard OWL properties:', error)
      // 如果API调用失败，使用前端默认值
      standardOWLProperties = [...defaultOWLProperties.value]
    }
    
    // 合并标准OWL属性和ObjectProperty
    const properties = [...standardOWLProperties]
    
    // 尝试加载ObjectProperty数据
    try {
      const objectPropertiesResponse = await http.get('/property/findByPropertyType/OBJECT')
      const objectProperties = objectPropertiesResponse.data || []
      objectProperties.forEach(prop => {
        if (prop.name) {
          properties.push(prop.name)
        }
      })
    } catch (error) {
      console.error('Failed to load object properties:', error)
      // 即使加载ObjectProperty失败，也要确保标准的OWL属性被添加
    }
    
    // 去重并排序
    const sortedProperties = [...new Set(properties)].sort()
    relationshipProperties.value = sortedProperties
    
    // 缓存数据到localStorage
    try {
      localStorage.setItem('relationshipProperties', JSON.stringify(sortedProperties))
      console.log('Relationship properties cached:', sortedProperties)
    } catch (error) {
      console.error('Failed to cache relationship properties:', error)
    }
    
    console.log('Relationship properties loaded:', relationshipProperties.value)
  } catch (error) {
    console.error('Failed to load relationship properties:', error)
    // 即使发生错误，也要确保默认的OWL属性被添加
    const defaultProperties = [...new Set(defaultOWLProperties.value)].sort()
    relationshipProperties.value = defaultProperties
    
    // 缓存默认数据
    try {
      localStorage.setItem('relationshipProperties', JSON.stringify(defaultProperties))
      console.log('Default OWL properties cached:', defaultProperties)
    } catch (error) {
      console.error('Failed to cache default OWL properties:', error)
    }
    
    console.log('Using default OWL properties:', relationshipProperties.value)
  }
}

// 加载value数据源
const loadValueDataSources = async () => {
  try {
    // 从缓存中获取数据
    const cachedValueData = localStorage.getItem('valueDataSources')
    if (cachedValueData) {
      try {
        const cachedData = JSON.parse(cachedValueData)
        valueDataSources.value = { ...valueDataSources.value, ...cachedData }
        console.log('Using cached value data sources:', valueDataSources.value)
      } catch (error) {
        console.error('Failed to parse cached value data sources:', error)
      }
    }
    
    // 加载Class数据
    try {
      console.log('Loading classes from API...')
      const classesResponse = await http.get('/class/findAll')
      console.log('Classes response:', classesResponse)
      valueDataSources.value.classes = classesResponse.data || []
      console.log('Loaded classes:', valueDataSources.value.classes)
    } catch (error) {
      console.error('Failed to load class data:', error)
      // 使用缓存数据或空数组
      if (!valueDataSources.value.classes) {
        valueDataSources.value.classes = []
      }
      console.log('Using classes:', valueDataSources.value.classes)
    }
    
    // 加载Individual数据
    try {
      const individualsResponse = await http.get('/individual/findAll')
      valueDataSources.value.individuals = individualsResponse.data || []
    } catch (error) {
      console.error('Failed to load individual data:', error)
      // 使用缓存数据或空数组
      if (!valueDataSources.value.individuals) {
        valueDataSources.value.individuals = []
      }
    }
    
    // 加载数据类型 - 使用本地定义的dataTypes清单，不从API获取
    valueDataSources.value.dataTypes = [
      'xsd:anyURI',
      'xsd:base64Binary',
      'xsd:boolean',
      'xsd:byte',
      'xsd:dateTime',
      'xsd:dateTimeStamp',
      'xsd:decimal',
      'xsd:double',
      'xsd:float',
      'xsd:hexBinary',
      'xsd:int',
      'xsd:integer',
      'xsd:language',
      'xsd:long',
      'xsd:Name',
      'xsd:NCName',
      'xsd:negativeInteger',
      'xsd:nonNegativeInteger',
      'xsd:nonPositiveInteger',
      'xsd:positiveInteger',
      'xsd:short',
      'xsd:string',
      'xsd:token',
      'xsd:unsignedByte',
      'xsd:unsignedInt',
      'xsd:unsignedLong',
      'xsd:unsignedShort'
    ]
    console.log('Using local dataTypes:', valueDataSources.value.dataTypes)
    
    // 缓存数据到localStorage
    try {
      localStorage.setItem('valueDataSources', JSON.stringify(valueDataSources.value))
      console.log('Value data sources cached:', valueDataSources.value)
    } catch (error) {
      console.error('Failed to cache value data sources:', error)
    }
  } catch (error) {
    console.error('Failed to load value data sources:', error)
    
    // 确保所有数据源都有默认值
    if (!valueDataSources.value.classes) {
      valueDataSources.value.classes = []
    }
    if (!valueDataSources.value.individuals) {
      valueDataSources.value.individuals = []
    }
    if (!valueDataSources.value.dataTypes) {
      valueDataSources.value.dataTypes = [
        'xsd:anyURI',
        'xsd:base64Binary',
        'xsd:boolean',
        'xsd:byte',
        'xsd:dateTime',
        'xsd:dateTimeStamp',
        'xsd:decimal',
        'xsd:double',
        'xsd:float',
        'xsd:hexBinary',
        'xsd:int',
        'xsd:integer',
        'xsd:language',
        'xsd:long',
        'xsd:Name',
        'xsd:NCName',
        'xsd:negativeInteger',
        'xsd:nonNegativeInteger',
        'xsd:nonPositiveInteger',
        'xsd:positiveInteger',
        'xsd:short',
        'xsd:string',
        'xsd:token',
        'xsd:unsignedByte',
        'xsd:unsignedInt',
        'xsd:unsignedLong',
        'xsd:unsignedShort'
      ]
    }
    
    // 缓存默认数据
    try {
      localStorage.setItem('valueDataSources', JSON.stringify(valueDataSources.value))
      console.log('Default value data sources cached:', valueDataSources.value)
    } catch (error) {
      console.error('Failed to cache default value data sources:', error)
    }
  }
}

// Relationships相关方法
const handleRelationshipPropertyFocus = (index) => {
  // 关闭其他所有下拉框
  showValueDropdown.value = false
  showLanguageDropdown.value = false
  showAnnotationPropertyDropdown.value = false
  
  currentRelationshipIndex.value = index
  showRelationshipPropertyDropdown.value = true
  handleRelationshipPropertyInput(index)
}

const handleRelationshipPropertyInput = (index) => {
  const rel = selectedClass.value.relationships[index]
  if (rel) {
    const keyword = rel.property.toLowerCase()
    if (keyword) {
      filteredRelationshipProperties.value = relationshipProperties.value.filter(prop => 
        prop.toLowerCase().includes(keyword)
      )
    } else {
      // 当输入框为空时，显示所有属性
      filteredRelationshipProperties.value = relationshipProperties.value
    }
  }
}

const selectRelationshipProperty = (property, index) => {
  if (selectedClass.value.relationships[index]) {
    selectedClass.value.relationships[index].property = property
  }
  showRelationshipPropertyDropdown.value = false
}

const handleNewRelationshipPropertyFocus = () => {
  // 关闭其他所有下拉框
  showValueDropdown.value = false
  showLanguageDropdown.value = false
  showAnnotationPropertyDropdown.value = false
  
  currentRelationshipIndex.value = -1
  showRelationshipPropertyDropdown.value = true
  handleNewRelationshipPropertyInput()
}

const handleNewRelationshipPropertyInput = () => {
  const keyword = newRelationship.value.property.toLowerCase()
  if (keyword) {
    filteredRelationshipProperties.value = relationshipProperties.value.filter(prop => 
      prop.toLowerCase().includes(keyword)
    )
  } else {
    // 当输入框为空时，显示所有属性
    filteredRelationshipProperties.value = relationshipProperties.value
  }
}

const selectNewRelationshipProperty = (property) => {
  newRelationship.value.property = property
  showRelationshipPropertyDropdown.value = false
}

const handleRelationshipValueFocus = (index) => {
  // 关闭其他所有下拉框
  showRelationshipPropertyDropdown.value = false
  showLanguageDropdown.value = false
  showAnnotationPropertyDropdown.value = false
  
  currentValueIndex.value = index
  showValueDropdown.value = true
  handleRelationshipValueInput(index)
}

const handleRelationshipValueInput = (index) => {
  const rel = selectedClass.value.relationships[index]
  if (rel) {
    console.log('handleRelationshipValueInput called:', index, rel.target)
    console.log('Current classes in valueDataSources:', valueDataSources.value.classes)
    
    const keyword = rel.target.toLowerCase()
    
    // 过滤classes
    filteredValues.value.classes = valueDataSources.value.classes.filter(cls => {
      const matches = cls.name.toLowerCase().includes(keyword)
      console.log('Checking class:', cls.name, 'matches:', matches)
      return matches
    })
    console.log('Filtered classes:', filteredValues.value.classes)
    
    // 过滤individuals
    filteredValues.value.individuals = valueDataSources.value.individuals.filter(individual => 
      individual.name.toLowerCase().includes(keyword)
    )
    console.log('Filtered individuals:', filteredValues.value.individuals)
    
    // 过滤dataTypes
    filteredValues.value.dataTypes = valueDataSources.value.dataTypes.filter(datatype => 
      datatype.toLowerCase().includes(keyword)
    )
    console.log('Filtered dataTypes:', filteredValues.value.dataTypes)
  }
}

// 处理Relationship的Value输入框失去焦点
const handleRelationshipValueBlur = async (index) => {
  // 延迟关闭下拉框，以便点击选项时能触发选择事件
  setTimeout(() => {
    showValueDropdown.value = false
    currentValueIndex.value = -1
  }, 200)
  
  // 保存数据
  const rel = selectedClass.value.relationships[index]
  if (rel && rel.property && (rel.target || rel.value)) {
    try {
      await saveRelationship(rel)
      
      // 检查是否需要新增空白行
      checkAndAddNewRelationshipRow()
    } catch (error) {
      console.error('Failed to save relationship on value blur:', error)
    }
  }
}

// 处理新Relationship的Value输入框失去焦点
const handleNewRelationshipValueBlur = async () => {
  // 延迟关闭下拉框，以便点击选项时能触发选择事件
  setTimeout(() => {
    showValueDropdown.value = false
    currentValueIndex.value = -1
  }, 200)
  
  // 保存新Relationship数据
  if (newRelationship.value.property && (newRelationship.value.target || newRelationship.value.value)) {
    try {
      await addRelationship()
      
      // 检查是否需要新增空白行
      checkAndAddNewRelationshipRow()
    } catch (error) {
      console.error('Failed to save new relationship on value blur:', error)
    }
  }
}

// 检查并添加新的空白Relationship行
const checkAndAddNewRelationshipRow = () => {
  // 检查是否已存在空白待填写的行
  const hasEmptyRow = selectedClass.value.relationships.some(rel => 
    !rel.property && !rel.value
  )
  
  // 如果不存在空白行，则添加一行
  if (!hasEmptyRow) {
    selectedClass.value.relationships.push({
      property: '',
      value: '',
      language: ''
    })
  }
}

// 保存Relationship数据到后端
const saveRelationship = async (rel) => {
  try {
    // 根据property类型决定是否包含language字段
    const config = getRelationshipConfig(rel.property)
    const relationshipData = {
      property: rel.property,
      target: rel.target
    }
    
    // 只有DataProperty类型才包含language字段
    if (config.showLang && rel.language) {
      relationshipData.language = rel.language
    }
    
    // 调用后端API保存数据
    await http.post('/relationship/save', relationshipData)
    console.log('Relationship saved successfully:', relationshipData)
    ElMessage.success('Relationship saved successfully')
  } catch (error) {
    console.error('Failed to save relationship:', error)
    ElMessage.error('Failed to save relationship. Please try again.')
    throw error
  }
}

const selectRelationshipValue = (value, index) => {
  if (selectedClass.value.relationships[index]) {
    selectedClass.value.relationships[index].target = value
  }
  showValueDropdown.value = false
}

const handleNewRelationshipValueFocus = () => {
  // 检查property是否为空
  if (!newRelationship.value.property) {
    ElMessage.warning('First, please enter property')
    // 将焦点移动到property输入框
    nextTick(() => {
      const propertyInput = document.querySelector('.annotation-input-row .annotation-property-input')
      if (propertyInput) {
        propertyInput.focus()
      }
    })
    return
  }
  
  // 关闭其他所有下拉框
  showRelationshipPropertyDropdown.value = false
  showLanguageDropdown.value = false
  showAnnotationPropertyDropdown.value = false
  
  currentValueIndex.value = -1
  showValueDropdown.value = true
  handleNewRelationshipValueInput()
}

const handleNewRelationshipValueInput = () => {
  const keyword = newRelationship.value.target.toLowerCase()
  
  // 过滤classes
  filteredValues.value.classes = valueDataSources.value.classes.filter(cls => 
    cls.name.toLowerCase().includes(keyword)
  )
  
  // 过滤individuals
  filteredValues.value.individuals = valueDataSources.value.individuals.filter(individual => 
    individual.name.toLowerCase().includes(keyword)
  )
  
  // 过滤dataTypes
  filteredValues.value.dataTypes = valueDataSources.value.dataTypes.filter(datatype => 
    datatype.toLowerCase().includes(keyword)
  )
}

// 处理Relationship语言输入框获取焦点
const handleRelationshipLangFocus = (index) => {
  // 关闭其他所有下拉框
  showRelationshipPropertyDropdown.value = false
  showValueDropdown.value = false
  showAnnotationPropertyDropdown.value = false
  
  currentRelationshipLangIndex.value = index
  filteredLanguages.value = languages.value
  showLanguageDropdown.value = true
}

// 处理Relationship语言输入
const handleRelationshipLangInput = (index, keyword) => {
  // 关闭其他所有下拉框
  showRelationshipPropertyDropdown.value = false
  showValueDropdown.value = false
  showAnnotationPropertyDropdown.value = false
  
  currentRelationshipLangIndex.value = index
  
  // 根据关键字过滤语言列表
  if (keyword) {
    const query = keyword.toLowerCase()
    filteredLanguages.value = languages.value.filter(lang => 
      lang.code.toLowerCase().includes(query) || 
      lang.name.toLowerCase().includes(query)
    )
  } else {
    filteredLanguages.value = languages.value
  }
  showLanguageDropdown.value = true
}



// 选择Relationship语言
const selectRelationshipLanguage = (index, lang) => {
  if (selectedClass.value && selectedClass.value.relationships && selectedClass.value.relationships[index]) {
    selectedClass.value.relationships[index].language = lang.code
  }
  showLanguageDropdown.value = false
  currentRelationshipLangIndex.value = -1
  
  // 验证(property+lang)唯一性
  validateRelationshipUniqueness()
}

// 获取Relationship的配置信息（根据property类型）
const getRelationshipConfig = (property) => {
  if (!property) {
    return {
      placeholder: 'Enter value',
      showLang: true,
      propertyType: 'default'  // default类型：空输入时无下拉列表，有输入时显示所有
    }
  }
  
  // 检查是否是DataProperty类型
  if (property.includes('DataProperty')) {
    // DataProperty类型：只显示DataTypes，可以输入任意字符串/数值
    return {
      placeholder: 'Enter datatype,string,number etc.',
      showLang: true,
      propertyType: 'datatype'
    }
  } else if (property.includes('ObjectProperty')) {
    // ObjectProperty类型：显示Classes/Individuals，可以输入任意字符串
    return {
      placeholder: 'Enter class or individual name',
      showLang: false,
      propertyType: 'object'
    }
  }
  
  // ObjectProperty中定义的数据：显示Classes/Individuals
  return {
    placeholder: 'Enter class or individual name',
    showLang: false,
    propertyType: 'object'
  }
}



const selectNewRelationshipValue = (value) => {
  newRelationship.value.target = value
  showValueDropdown.value = false
}

const createNewValue = async (type, index) => {
  let name = ''
  if (index === -1) {
    name = newRelationship.value.target
  } else if (selectedClass.value.relationships[index]) {
    name = selectedClass.value.relationships[index].target
  }
  
  if (!name) {
    ElMessage.warning('Please enter a name for the new value')
    return
  }
  
  let result = null
  try {
    switch (type) {
      case 'individual':
        result = await createNewIndividual(name)
        break
      case 'class':
        result = await createNewClass(name)
        break
      case 'datatype':
        result = await createNewDatatype(name)
        break
    }
    
    if (result) {
      // 先更新本地数据
      if (index === -1) {
        newRelationship.value.target = name
      } else if (selectedClass.value.relationships[index]) {
        selectedClass.value.relationships[index].target = name
      }
      
      // 延迟一段时间，确保实体创建成功后再保存Relationship
      await new Promise(resolve => setTimeout(resolve, 500))
      
      // 然后保存Relationship到后端
      if (index === -1) {
        // 新关系
        if (newRelationship.value.property) {
          console.log('Calling addRelationship with:', {
            selectedClass: selectedClass.value,
            newRelationship: newRelationship.value
          })
          await addRelationship()
        } else {
          console.log('Skipping addRelationship because property is empty:', newRelationship.value)
        }
      } else if (selectedClass.value.relationships[index]) {
        // 现有关系
        console.log('Updating existing relationship with:', {
          entityId: selectedClass.value.id,
          property: selectedClass.value.relationships[index].property,
          value: name,
          language: selectedClass.value.relationships[index].language
        })
        await http.post('/relationship/set', {
          entityId: selectedClass.value.id,
          entityType: 'CLASS',
          property: selectedClass.value.relationships[index].property,
          value: name,
          language: selectedClass.value.relationships[index].language
        })
      }
      
      // 重新加载类详情，确保界面显示最新数据
      await loadClassDetails(selectedClass.value.id)
      
      // 重新加载value数据源，确保包含新创建的实体
      await loadValueDataSources()
      
      showValueDropdown.value = false
      ElMessage.success(`New ${type} created successfully`)
    }
  } catch (error) {
    console.error(`Failed to create new ${type}:`, error)
    ElMessage.error(`Failed to create new ${type}. Please try again.`)
  }
}



const handleRelationshipLangBlur = async (rel, index) => {
  // 当焦点离开lang输入框时，向后台更新数据
  if (rel && (rel.property && rel.target)) {
    try {
      // 检查(property+lang)组合的唯一性
      const isUnique = checkRelationshipUniqueness(rel, index)
      if (!isUnique) {
        ElMessage.warning('Relationship with this property and language already exists')
        return
      }
      
      // 根据property类型决定是否包含language字段
      const config = getRelationshipConfig(rel.property)
      const requestData = {
        entityId: selectedClass.value.id,
        entityType: 'CLASS',
        property: rel.property,
        value: rel.target
      }
      
      // 只有DataProperty类型才包含language字段
      if (config.showLang && rel.language) {
        requestData.language = rel.language
      }
      
      await http.post('/relationship/set', requestData)
      await loadClassDetails(selectedClass.value.id)
      ElMessage.success('Relationship updated successfully')
      
      // 检查是否需要新增空白行
      checkAndAddNewRelationshipRow()
    } catch (error) {
      console.error('Failed to update relationship:', error)
      ElMessage.error('Failed to update relationship')
    }
  }
}

const handleNewRelationshipLangFocus = () => {
  currentRelationshipLangIndex.value = -1
  filteredLanguages.value = languages.value
  showLanguageDropdown.value = true
  
  // 当新行的lang输入框获得焦点时，检查property和value是否有值
  if (newRelationship.value.property && newRelationship.value.target) {
    // 可以在这里添加逻辑
  }
}

// 处理新Relationship语言输入
const handleNewRelationshipLangInput = (keyword) => {
  currentRelationshipLangIndex.value = -1
  
  // 根据关键字过滤语言列表
  if (keyword) {
    const query = keyword.toLowerCase()
    filteredLanguages.value = languages.value.filter(lang => 
      lang.code.toLowerCase().includes(query) || 
      lang.name.toLowerCase().includes(query)
    )
  } else {
    filteredLanguages.value = languages.value
  }
  showLanguageDropdown.value = true
}

// 选择新Relationship语言
const selectNewRelationshipLanguage = (lang) => {
  newRelationship.value.language = lang.code
  showLanguageDropdown.value = false
  currentRelationshipLangIndex.value = -1
}

const handleNewRelationshipLangBlur = async () => {
  // 当新行的lang输入框失去焦点时，向后台添加数据
  if (newRelationship.value.property && newRelationship.value.value) {
    try {
      // 检查(property+lang)组合的唯一性
      const isUnique = checkNewRelationshipUniqueness()
      if (!isUnique) {
        ElMessage.warning('Relationship with this property and language already exists')
        return
      }
      
      // 根据property类型决定是否包含language字段
      const config = getRelationshipConfig(newRelationship.value.property)
      const requestData = {
        entityId: selectedClass.value.id,
        entityType: 'CLASS',
        property: newRelationship.value.property,
        value: newRelationship.value.value
      }
      
      // 只有DataProperty类型才包含language字段
      if (config.showLang && newRelationship.value.language) {
        requestData.language = newRelationship.value.language
      }
      
      await http.post('/relationship/set', requestData)
      await loadClassDetails(selectedClass.value.id)
      // 重置新行数据
      newRelationship.value = {
        property: '',
        value: '',
        language: ''
      }
      
      // 检查是否需要新增空白行
      checkAndAddNewRelationshipRow()
    } catch (error) {
      console.error('Failed to add relationship:', error)
      ElMessage.error('Failed to add relationship')
    }
  }
}

const checkRelationshipUniqueness = (currentRel, currentIndex) => {
  if (!selectedClass.value.relationships) return true
  
  for (let i = 0; i < selectedClass.value.relationships.length; i++) {
    if (i === currentIndex) continue
    const rel = selectedClass.value.relationships[i]
    if (rel.property === currentRel.property && 
        rel.language === currentRel.language) {
      return false
    }
  }
  return true
}

const checkNewRelationshipUniqueness = () => {
  if (!selectedClass.value.relationships) return true
  
  for (const rel of selectedClass.value.relationships) {
    if (rel.property === newRelationship.value.property && 
        rel.language === newRelationship.value.language) {
      return false
    }
  }
  return true
}

const removeRelationship = async (index) => {
  if (!selectedClass.value || !selectedClass.value.relationships) return
  
  try {
    const rel = selectedClass.value.relationships[index]
    if (rel) {
      console.log('Removing relationship:', rel)
      await http.post('/relationship/delete', {
        entityId: selectedClass.value.id,
        entityType: 'CLASS',
        property: rel.property,
        value: rel.target || rel.value
      })
      await loadClassDetails(selectedClass.value.id)
      ElMessage.success('Relationship deleted successfully')
    }
  } catch (error) {
    console.error('Failed to remove relationship:', error)
    ElMessage.error('Failed to remove relationship')
  }
}

// 3类New操作
const createNewIndividual = async (name) => {
  try {
    const response = await http.post('/individual/create', {
      name: name,
      classId: selectedClass.value.id
    })
    await loadValueDataSources()
    return response.data
  } catch (error) {
    console.error('Failed to create new individual:', error)
    ElMessage.error('Failed to create new individual')
    return null
  }
}

const createNewClass = async (name) => {
  try {
    const response = await http.post('/class/create', {
      name: name,
      parentId: 'owl:Thing',
      ontologyId: props.projectId,
      languageTag: projectDefaultLanguage.value
    })
    await loadValueDataSources()
    await loadClassHierarchy() // 成功创建Class后更新Class Hierarchy树
    return response.data
  } catch (error) {
    console.error('Failed to create new class:', error)
    ElMessage.error('Failed to create new class')
    return null
  }
}

const createNewDatatype = async (name) => {
  try {
    const response = await http.post('/datatype/create', {
      name: name
    })
    await loadValueDataSources()
    return response.data
  } catch (error) {
    console.error('Failed to create new datatype:', error)
    ElMessage.error('Failed to create new datatype')
    return null
  }
}

const addRelationship = async () => {
  if (!selectedClass.value || !newRelationship.value.property || !newRelationship.value.target) return
  
  try {
    // 检查(property+lang)组合的唯一性
    const isUnique = checkNewRelationshipUniqueness()
    if (!isUnique) {
      ElMessage.warning('Relationship with this property and language already exists')
      return
    }
    
    await http.post('/relationship/set', {
      entityId: selectedClass.value.id,
      entityType: 'CLASS',
      property: newRelationship.value.property,
      value: newRelationship.value.target,
      language: newRelationship.value.language
    })
    await loadClassDetails(selectedClass.value.id)
    // 重置新relationship表单
    newRelationship.value = {
      property: '',
      target: '',
      language: ''
    }
    ElMessage.success('Relationship added successfully')
  } catch (error) {
    console.error('Failed to add relationship:', error)
    ElMessage.error('Failed to add relationship. Please try again.')
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

// 导入类
const handleImportClasses = async () => {
  try {
    // 构建请求数据
    const requestData = {
      script: importScript.value,
      projectId: props.projectId,
      ontologyId: props.projectDataRecord?.id || props.projectId
    }
    
    const response = await http.post('/class/importRDFS', requestData)
    
    // 显示成功消息，1秒后自动关闭
    const importedCount = response.data?.importedCount || 0
    const successMessage = `Successfully imported ${importedCount} classes`
    showAlertMessage(successMessage, 'success', 1000)
    
    // 延迟1秒后关闭模态框
    setTimeout(() => {
      showImportClassesModal.value = false
      emit('classes-imported', response.data)
      loadClassHierarchy()
      // 重置表单
      importScript.value = ''
    }, 1000)
    
  } catch (error) {
    console.error('Failed to import classes:', error)
    
    // 构建错误消息
    const source = error.response?.config?.url || 'Server'
    const errorMessage = error.response?.data?.message || error.message || 'Unknown error'
    const fullErrorMessage = `Source: ${source}\nFailed to import classes: ${errorMessage}`
    
    // 显示错误消息，需要用户点击OK确认
    showConfirmMessage(fullErrorMessage, 'error')
  }
}

// 显示提示消息
const showAlertMessage = (message, type = 'info', duration = 3000) => {
  // 创建提示元素
  const alertDiv = document.createElement('div')
  alertDiv.className = `alert alert-${type === 'error' ? 'danger' : type === 'success' ? 'success' : 'info'} alert-dismissible fade show position-fixed`
  alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 9999; max-width: 400px;'
  alertDiv.innerHTML = `
    <div style="white-space: pre-line;">${message}</div>
    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
  `
  
  document.body.appendChild(alertDiv)
  
  // 指定时间后自动关闭
  setTimeout(() => {
    if (alertDiv.parentNode) {
      alertDiv.remove()
    }
  }, duration)
  
  // 点击关闭按钮移除
  alertDiv.querySelector('.btn-close').addEventListener('click', () => {
    alertDiv.remove()
  })
}

// 显示确认消息（需要用户点击OK确认）
const showConfirmMessage = (message, type = 'error') => {
  // 创建确认框元素
  const confirmDiv = document.createElement('div')
  confirmDiv.className = `alert alert-${type === 'error' ? 'danger' : type === 'success' ? 'success' : 'info'} position-fixed`
  confirmDiv.style.cssText = 'top: 20px; right: 20px; z-index: 9999; max-width: 400px;'
  confirmDiv.innerHTML = `
    <div style="white-space: pre-line; margin-bottom: 10px;">${message}</div>
    <button type="button" class="btn btn-primary btn-sm" id="confirmOkBtn">OK</button>
  `
  
  document.body.appendChild(confirmDiv)
  
  // 点击OK按钮移除
  confirmDiv.querySelector('#confirmOkBtn').addEventListener('click', () => {
    confirmDiv.remove()
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
      property: newAnnotation.value.property || 'rdfs:comment',
      value: newAnnotation.value.value
    })
    showAddAnnotationModal.value = false
    newAnnotation.value = { property: '', value: '' }
    await loadClassDetails(selectedClass.value.id)
  } catch (error) {
    console.error('Failed to add annotation:', error)
    alert('Failed to add annotation')
  }
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

/* 加载状态样式 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  width: 100%;
}

.loading-spinner {
  border: 3px solid #f3f3f3;
  border-top: 3px solid #4a90d9;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  animation: spin 1s linear infinite;
  margin-bottom: 10px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-state span {
  color: #666;
  font-size: 14px;
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

.minimize-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  padding: 0;
  font-weight: bold;
}

.minimize-icon {
  font-size: 18px;
  font-weight: bold;
  line-height: 1;
  color: inherit;
  transform: translateY(-2px);
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

.custom-tree-node {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  cursor: pointer;
  user-select: none;
  padding: 2px 4px;
  border-radius: 3px;
  transition: background-color 0.2s ease;
  width: 100%;
}

/* Ensure hover effect works for all nodes */
:deep(.el-tree-node) {
  transition: background-color 0.2s ease;
}

:deep(.el-tree-node__content) {
  height: 26px;
  padding-left: 4px !important;
  transition: background-color 0.2s ease;
}

:deep(.el-tree-node__content:hover) {
  background-color: #e3f2fd !important;
}

:deep(.el-tree-node__content:hover .custom-tree-node) {
  background-color: transparent !important;
}

/* Ensure hover effect works for all nodes including children */
:deep(.el-tree-node__children .el-tree-node__content) {
  transition: background-color 0.2s ease;
}

:deep(.el-tree-node__children .el-tree-node__content:hover) {
  background-color: #e3f2fd !important;
}

.custom-node-icon {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: #ffc107;
  flex-shrink: 0;
}

.custom-node-label {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Root node styling - only when selected */
:deep(.el-tree-node:first-child > .el-tree-node__content.is-current) {
  background-color: #4a90d9 !important;
  color: white !important;
}

:deep(.el-tree-node:first-child > .el-tree-node__content.is-current:hover) {
  background-color: #4a90d9 !important;
  color: white !important;
}

:deep(.el-tree-node:first-child > .el-tree-node__content.is-current .custom-node-icon) {
  background-color: white !important;
}

:deep(.el-tree-node:first-child > .el-tree-node__content.is-current .custom-node-label) {
  color: white !important;
}

/* Root node default styling */
:deep(.el-tree-node:first-child > .el-tree-node__content) {
  background-color: transparent !important;
  color: inherit !important;
  transition: background-color 0.2s ease;
}

:deep(.el-tree-node:first-child > .el-tree-node__content:hover) {
  background-color: #e3f2fd !important;
}

:deep(.el-tree-node:first-child > .el-tree-node__content .custom-node-icon) {
  background-color: #ffc107 !important;
}

:deep(.el-tree-node:first-child > .el-tree-node__content .custom-node-label) {
  color: inherit !important;
}

/* Custom line style */
:deep(.el-tree-node__line) {
  border-right: 1px dashed #999 !important;
}

:deep(.el-tree-node__line::before) {
  border-bottom: 1px dashed #999 !important;
}

:deep(.el-tree-node__line::after) {
  border-bottom: 1px dashed #999 !important;
}

/* Ensure tree lines are visible */
:deep(.el-tree) {
  --el-tree-line-color: #999 !important;
}

/* Increase indent for better hierarchy visualization */
:deep(.el-tree-node__children) {
  padding-left: 16px !important;
}

/* Ensure expand/collapse icons are visible */
:deep(.el-tree-node__expand-icon) {
  color: #666 !important;
}

:deep(.el-tree-node__expand-icon:hover) {
  color: #4a90d9 !important;
}

/* Selected node styling */
:deep(.el-tree-node.is-current > .el-tree-node__content) {
  background-color: #4a90d9 !important;
  color: white !important;
}

:deep(.el-tree-node.is-current > .el-tree-node__content:hover) {
  background-color: #4a90d9 !important;
  color: white !important;
}

:deep(.el-tree-node.is-current > .el-tree-node__content .custom-node-icon) {
  background-color: white !important;
}

:deep(.el-tree-node.is-current > .el-tree-node__content .custom-node-label) {
  color: white !important;
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

/* 新的样式 */
.annotation-list,
.parent-list,
.relationship-list {
  border: 1px solid #ddd;
  border-radius: 2px;
  padding: 6px;
}

.annotation-item,
.parent-item,
.relationship-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 4px 0;
  font-size: 12px;
}

.annotation-content,
.parent-content,
.relationship-content {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.annotation-predicate,
.relationship-property {
  font-weight: 500;
  color: #4a90d9;
  min-width: 90px;
  flex-shrink: 0;
}

.annotation-value,
.relationship-value {
  flex: 1;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.annotation-lang,
.relationship-lang {
  font-size: 11px;
  color: #666;
  min-width: 30px;
  text-align: right;
}

.parent-checkbox {
  color: #4a90d9;
  margin-right: 4px;
}

.parent-name {
  flex: 1;
  color: #333;
}

.annotation-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}

.annotation-submit {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  padding: 0;
  color: #666;
  background: #f8f9fa;
  border: 1px solid #ccc;
  border-radius: 50%;
  cursor: pointer;
  font-size: 12px;
  font-weight: bold;
  transition: all 0.2s ease;
  position: relative;
}

.annotation-submit:hover {
  background: #dc3545;
  border-color: #dc3545;
  color: white;
}

.annotation-submit::after {
  content: 'Add default annotation';
  position: absolute;
  top: -30px;
  left: 50%;
  transform: translateX(-50%);
  background: #333;
  color: #fff;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: normal;
  white-space: nowrap;
  opacity: 0;
  visibility: hidden;
  transition: all 0.2s ease;
  z-index: 1000;
}

.annotation-submit:hover::after {
  opacity: 1;
  visibility: visible;
}

.annotation-delete,
.parent-delete,
.relationship-delete {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  padding: 0;
  color: #fff;
  background: #ccc;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  font-size: 14px;
  font-weight: bold;
  transition: all 0.2s ease;
  position: relative;
}

.annotation-delete:hover,
.parent-delete:hover,
.relationship-delete:hover {
  background: #e74c3c;
}

.annotation-delete:focus,
.parent-delete:focus,
.relationship-delete:focus {
  background: #e74c3c;
  outline: none;
}

/* 删除按钮提示 */
.annotation-delete::after,
.parent-delete::after,
.relationship-delete::after {
  content: 'Delete';
  position: absolute;
  top: -30px;
  left: 50%;
  transform: translateX(-50%);
  background: #333;
  color: #fff;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: normal;
  white-space: nowrap;
  opacity: 0;
  visibility: hidden;
  transition: all 0.2s ease;
  z-index: 1000;
}

.annotation-delete:hover::after,
.parent-delete:hover::after,
.relationship-delete:hover::after,
.annotation-delete:focus::after,
.parent-delete:focus::after,
.relationship-delete:focus::after {
  opacity: 1;
  visibility: visible;
}

/* 删除按钮提示箭头 */
.annotation-delete::before,
.parent-delete::before,
.relationship-delete::before {
  content: '';
  position: absolute;
  top: -8px;
  left: 50%;
  transform: translateX(-50%);
  border-left: 5px solid transparent;
  border-right: 5px solid transparent;
  border-top: 5px solid #333;
  opacity: 0;
  visibility: hidden;
  transition: all 0.2s ease;
  z-index: 1000;
}

.annotation-delete:hover::before,
.parent-delete:hover::before,
.relationship-delete:hover::before,
.annotation-delete:focus::before,
.parent-delete:focus::before,
.relationship-delete:focus::before {
  opacity: 1;
  visibility: visible;
}

.annotation-input-row,
.parent-input-row,
.relationship-input-row {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 6px;
  padding-top: 6px;
  border-top: 1px solid #eee;
}

.annotation-property,
.annotation-lang-input,
.parent-input,
.relationship-property,
.relationship-value-input,
.relationship-lang-input {
  font-size: 12px;
  padding: 3px 6px;
  border: 1px solid #ddd;
  border-radius: 2px;
  flex: 1;
}

/* value占5/8宽度 */
.annotation-value-input {
  font-size: 12px;
  padding: 3px 6px;
  border: 1px solid #ddd;
  border-radius: 2px;
  flex: 0 0 62.5%;
}

.annotation-content {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
  /* 确保总宽度为100% */
  box-sizing: border-box;
  padding: 0;
  /* 调整宽度比例，考虑gap的影响 */
}

/* property占25%宽度 */
.annotation-property-search {
  position: relative;
  flex: 0 0 calc(25% - 2px);
  min-width: 100px;
}

/* value占62.5%宽度 */
.annotation-value-search {
  position: relative;
  flex: 0 0 calc(62.5% - 2px);
  min-width: 200px;
}

/* value占62.5%宽度 */
.annotation-value-input {
  font-size: 12px;
  padding: 3px 6px;
  border: 1px solid #ddd;
  border-radius: 2px;
  width: 100%;
  box-sizing: border-box;
}

/* lang容器占12.5%宽度 */
.annotation-lang-container {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 0 0 12.5%;
  min-width: 60px;
  /* 确保即使没有删除按钮，宽度也能正确应用 */
  box-sizing: border-box;
}

.annotation-lang-search {
  position: relative;
  flex: 1;
}

.annotation-language-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  max-height: 200px;
  overflow-y: auto;
  background: white;
  border: 1px solid #ddd;
  border-top: none;
  border-radius: 0 0 2px 2px;
  z-index: 1000;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.annotation-language-option {
  padding: 6px 8px;
  font-size: 12px;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
}

.annotation-language-option:hover {
  background-color: #f5f5f5;
}

.annotation-language-option:last-child {
  border-bottom: none;
}

.annotation-property-input {
  width: 100%;
  font-size: 12px;
  padding: 3px 6px;
  border: 1px solid #ddd;
  border-radius: 2px;
  box-sizing: border-box;
}

.annotation-property-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  max-height: 200px;
  overflow-y: auto;
  background: white;
  border: 1px solid #ddd;
  border-top: none;
  border-radius: 0 0 2px 2px;
  z-index: 1000;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.annotation-property-option {
  padding: 6px 8px;
  font-size: 12px;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
}

.annotation-property-option:hover {
  background-color: #f5f5f5;
}

.annotation-property-option:last-child {
  border-bottom: none;
}

.annotation-lang-input {
  flex: 1;
  width: 100%;
}

.relationship-lang-input {
  flex: 0 0 60px;
}

.annotation-add,
.parent-add {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  padding: 0;
  color: #4a90d9;
  background: transparent;
  border: 1px solid #4a90d9;
  border-radius: 2px;
  cursor: pointer;
  font-size: 12px;
}

.annotation-add:hover,
.parent-add:hover {
  background-color: #4a90d9;
  color: white;
}

.relationship-separator {
  color: #999;
  font-weight: bold;
  margin: 0 4px;
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
