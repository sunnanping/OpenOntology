<template>
  <div class="entity-tabs-bar">
    <div class="tabs-container">
      <!-- 左侧：实体类型标签 - 使用ElementUI的滚动容器 -->
      <el-scrollbar class="tabs-scrollbar" wrap-class="tabs-wrap">
        <div class="tabs-left">
          <div 
            v-for="tab in tabs" 
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
</template>

<script setup>
const props = defineProps({
  tabs: {
    type: Array,
    default: () => [
      { id: 'classes', label: 'Classes', shortLabel: 'Cls', icon: 'bi bi-diagram-3', module: 'ClassEditor' },
      { id: 'properties', label: 'Properties', shortLabel: 'Prop', icon: 'bi bi-link-45deg', module: 'PropertyEditor' },
      { id: 'individuals', label: 'Individuals', shortLabel: 'Ind', icon: 'bi bi-people', module: 'IndividualEditor' },
      { id: 'comments', label: 'Comments', shortLabel: 'Cmt', icon: 'bi bi-chat-left-text', module: 'CommentManager' },
      { id: 'changes', label: 'Changes by Entity', shortLabel: 'Chg', icon: 'bi bi-clock-history', module: 'ChangesViewer' },
      { id: 'history', label: 'History', shortLabel: 'His', icon: 'bi bi-journal-text', module: 'HistoryViewer' }
    ]
  },
  activeTab: {
    type: String,
    default: 'classes'
  },
  projectDataRecord: {
    type: Object,
    default: () => null
  }
})

const emit = defineEmits(['tab-change', 'add-tab'])

const handleTabChange = (tab) => {
  emit('tab-change', tab)
}

const handleAddTab = () => {
  emit('add-tab')
}
</script>

<style scoped>
.entity-tabs-bar {
  height: 32px;
  background: linear-gradient(180deg, #f5f5f5 0%, #e8e8e8 100%);
  border-bottom: 1px solid #ccc;
  flex-shrink: 0;
}

.tabs-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 8px;
}

.tabs-scrollbar {
  flex: 1;
  height: 100%;
}

.tabs-left {
  display: flex;
  align-items: center;
  gap: 2px;
  overflow-x: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.tabs-left::-webkit-scrollbar {
  display: none;
}

.entity-tab {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  font-size: 12px;
  color: #555;
  cursor: pointer;
  border-radius: 3px 3px 0 0;
  white-space: nowrap;
  transition: all 0.2s;
}

.entity-tab:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.entity-tab.active {
  background-color: #4a90d9;
  color: white;
}

.entity-tab i {
  font-size: 11px;
}

.tab-label {
  display: inline;
}

.tab-label-short {
  font-size: 11px;
}

.btn-add-tab {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 3px 8px;
  font-size: 11px;
  color: #555;
  background: white;
  border: 1px solid #ccc;
  border-radius: 3px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
}

.btn-add-tab:hover {
  background-color: #f0f0f0;
}

.btn-add-tab i {
  font-size: 10px;
}

@media (max-width: 992px) {
  .entity-tab {
    padding: 4px 10px;
  }
  
  .tab-label-short {
    font-size: 11px;
  }
}

@media (max-width: 768px) {
  .entity-tabs-bar {
    height: 36px;
  }
  
  .entity-tab {
    padding: 4px 8px;
    font-size: 11px;
  }
  
  .entity-tab i {
    font-size: 14px;
  }
}

@media (max-width: 576px) {
  .entity-tabs-bar {
    height: 32px;
  }
  
  .tabs-container {
    padding: 0 4px;
  }
  
  .entity-tab {
    padding: 2px 6px;
    font-size: 10px;
  }
  
  .entity-tab i {
    font-size: 12px;
  }
  
  .btn-add-tab {
    padding: 2px 6px;
  }
}
</style>
