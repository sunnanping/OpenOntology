<template>
  <div class="project-edit">
    <!-- 顶部导航栏 -->
    <header class="top-nav">
      <div class="project-title">
        <h1>{{ projectId }}</h1>
      </div>
      <div class="nav-tabs">
        <button class="tab-btn active">Classes</button>
        <button class="tab-btn">Properties</button>
        <button class="tab-btn">Individuals</button>
        <button class="tab-btn">Relationships</button>
        <button class="tab-btn">Comments</button>
        <button class="tab-btn">Changes</button>
        <button class="tab-btn">History</button>
        <button class="tab-btn">OWL Properties</button>
        <button class="tab-btn">OWL Classes</button>
        <button class="tab-btn">Query</button>
        <button class="tab-btn">Test Menu</button>
      </div>
      <div class="right-actions">
        <button class="action-btn">Display</button>
        <button class="action-btn">Project</button>
        <button class="action-btn">Share</button>
        <button class="action-btn">Branding</button>
        <button class="action-btn">Add IRI</button>
      </div>
    </header>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 左侧类层次结构 -->
      <div class="left-panel">
        <div class="panel-header">
          <h3>Classes</h3>
          <div class="search-box">
            <input type="text" placeholder="Search...">
          </div>
        </div>
        <div class="class-hierarchy">
          <ul>
            <li>
              <span class="class-name">Thing</span>
              <ul>
                <li>
                  <span class="class-name">ICT</span>
                  <ul>
                    <li>
                      <span class="class-name">ICT设备</span>
                    </li>
                    <li>
                      <span class="class-name">ICT服务</span>
                    </li>
                    <li>
                      <span class="class-name">ICT应用</span>
                    </li>
                  </ul>
                </li>
                <li>
                  <span class="class-name">电力</span>
                  <ul>
                    <li>
                      <span class="class-name">电力设备</span>
                    </li>
                    <li>
                      <span class="class-name">电力服务</span>
                    </li>
                    <li>
                      <span class="class-name">电力应用</span>
                    </li>
                    <li>
                      <span class="class-name">电力管理中心</span>
                    </li>
                  </ul>
                </li>
              </ul>
            </li>
          </ul>
        </div>
      </div>

      <!-- 中间编辑区域 -->
      <div class="center-panel">
        <div class="panel-header">
          <div class="panel-title">
            <h3>Class: 电力管理中心</h3>
          </div>
          <div class="panel-actions">
            <button class="action-btn">Add subclass</button>
            <button class="action-btn">Add sibling class</button>
            <button class="action-btn">Delete</button>
          </div>
        </div>
        <div class="class-diagram">
          <!-- 类图可视化 -->
          <svg width="100%" height="400" viewBox="0 0 800 400">
            <!-- 类节点 -->
            <rect x="350" y="50" width="100" height="50" rx="5" ry="5" fill="#f0f0f0" stroke="#000" stroke-width="1"/>
            <text x="400" y="80" text-anchor="middle" fill="#000">ICT设施</text>
            
            <rect x="300" y="150" width="100" height="50" rx="5" ry="5" fill="#f0f0f0" stroke="#000" stroke-width="1"/>
            <text x="350" y="180" text-anchor="middle" fill="#000">电力</text>
            
            <rect x="400" y="150" width="100" height="50" rx="5" ry="5" fill="#f0f0f0" stroke="#000" stroke-width="1"/>
            <text x="450" y="180" text-anchor="middle" fill="#000">设备</text>
            
            <rect x="350" y="250" width="100" height="50" rx="5" ry="5" fill="#f0f0f0" stroke="#000" stroke-width="1"/>
            <text x="400" y="280" text-anchor="middle" fill="#000">电力管理中心</text>
            
            <!-- 连接线 -->
            <line x1="400" y1="100" x2="350" y2="150" stroke="#000" stroke-width="1"/>
            <line x1="400" y1="100" x2="450" y2="150" stroke="#000" stroke-width="1"/>
            <line x1="350" y1="200" x2="400" y2="250" stroke="#000" stroke-width="1"/>
            <line x1="450" y1="200" x2="400" y2="250" stroke="#000" stroke-width="1"/>
          </svg>
        </div>
      </div>

      <!-- 右侧命令面板 -->
      <div class="right-panel">
        <div class="panel-header">
          <h3>Commands / 能力管理</h3>
        </div>
        <div class="command-list">
          <ul>
            <li class="command-item">
              <span class="command-name">Add subclass</span>
            </li>
            <li class="command-item">
              <span class="command-name">Add sibling class</span>
            </li>
            <li class="command-item">
              <span class="command-name">Delete</span>
            </li>
            <li class="command-item">
              <span class="command-name">Rename</span>
            </li>
            <li class="command-item">
              <span class="command-name">Move</span>
            </li>
          </ul>
        </div>
        <div class="project-feed">
          <div class="feed-header">
            <h4>Project Feed</h4>
          </div>
          <div class="feed-content">
            <!-- 项目动态内容 -->
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const projectId = ref('')
const selection = ref('')

onMounted(() => {
  // 从路由参数中获取项目ID和选择的类
  projectId.value = route.params.id || '9b7fe218-e70e-4514-848e-9a11f394df07'
  selection.value = route.query.selection || ''
})
</script>

<style scoped>
.project-edit {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f5f5;
}

/* 顶部导航栏 */
.top-nav {
  background-color: #ffffff;
  border-bottom: 1px solid #e0e0e0;
  padding: 10px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.project-title h1 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: #333333;
}

.nav-tabs {
  display: flex;
  gap: 10px;
}

.tab-btn {
  background: none;
  border: none;
  padding: 8px 12px;
  font-size: 14px;
  color: #666666;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.tab-btn:hover {
  background-color: #f0f0f0;
  color: #333333;
}

.tab-btn.active {
  background-color: #e3f2fd;
  color: #1976d2;
  font-weight: 600;
}

.right-actions {
  display: flex;
  gap: 10px;
}

.action-btn {
  background: none;
  border: 1px solid #e0e0e0;
  padding: 6px 12px;
  font-size: 14px;
  color: #666666;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.action-btn:hover {
  background-color: #f0f0f0;
  color: #333333;
}

/* 主内容区域 */
.main-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

/* 左侧面板 */
.left-panel {
  width: 300px;
  background-color: #ffffff;
  border-right: 1px solid #e0e0e0;
  overflow-y: auto;
}

.panel-header {
  padding: 15px;
  border-bottom: 1px solid #e0e0e0;
  background-color: #f9f9f9;
}

.panel-header h3 {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 10px 0;
  color: #333333;
}

.search-box input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  font-size: 14px;
}

.class-hierarchy {
  padding: 10px;
}

.class-hierarchy ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.class-hierarchy li {
  margin: 5px 0;
}

.class-name {
  display: block;
  padding: 6px 10px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.class-name:hover {
  background-color: #f0f0f0;
}

.class-name.active {
  background-color: #e3f2fd;
  color: #1976d2;
  font-weight: 600;
}

/* 中间面板 */
.center-panel {
  flex: 1;
  background-color: #ffffff;
  border-right: 1px solid #e0e0e0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.panel-title h3 {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
  color: #333333;
}

.panel-actions {
  display: flex;
  gap: 10px;
}

.class-diagram {
  flex: 1;
  padding: 20px;
  overflow: auto;
}

/* 右侧面板 */
.right-panel {
  width: 300px;
  background-color: #ffffff;
  overflow-y: auto;
}

.command-list {
  padding: 15px;
}

.command-item {
  margin: 10px 0;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.command-item:hover {
  background-color: #f0f0f0;
}

.command-name {
  font-size: 14px;
  color: #333333;
}

.project-feed {
  margin-top: 20px;
  border-top: 1px solid #e0e0e0;
  padding: 15px;
}

.feed-header h4 {
  font-size: 14px;
  font-weight: 600;
  margin: 0 0 10px 0;
  color: #333333;
}

.feed-content {
  font-size: 13px;
  color: #666666;
  line-height: 1.5;
}
</style>