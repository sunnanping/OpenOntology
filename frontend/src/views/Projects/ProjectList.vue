<template>
  <div class="projects-container">
    <div class="projects-header">
      <h1>Projects</h1>
      <div class="user-info">
        <span class="username">{{ currentUser.username }}</span>
        <div class="user-avatar" :style="{ backgroundColor: getAvatarColor(currentUser.username) }">
          {{ getAvatarInitial(currentUser.username) }}
        </div>
      </div>
    </div>

    <div class="projects-content">
      <div class="projects-toolbar">
        <button v-if="isAdmin" class="btn btn-primary create-project-btn" @click="showCreateModal = true">
          Create New Project
        </button>
      </div>

      <div class="projects-filters">
        <div class="filter-group">
          <label>
            <input type="checkbox" v-model="filters.ownedByMe" @change="filterProjects">
            Owned by Me
          </label>
        </div>
        <div class="filter-group">
          <label>
            <input type="checkbox" v-model="filters.sharedWithMe" @change="filterProjects">
            Shared with Me
          </label>
        </div>
        <div class="filter-group">
          <label>
            <input type="checkbox" v-model="filters.trash" @change="filterProjects">
            Trash
          </label>
        </div>
        <div class="sort-group">
          <label>Sort by:</label>
          <select v-model="sortBy" @change="sortProjects">
            <option value="lastOpened">Last Opened</option>
            <option value="lastModified">Last Modified</option>
            <option value="name">Name</option>
            <option value="owner">Owner</option>
          </select>
        </div>
      </div>

      <div class="projects-table">
        <table>
          <thead>
            <tr>
              <th>Project name</th>
              <th>Owner</th>
              <th>Last opened</th>
              <th>Last modified</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="project in filteredProjects" :key="project.id">
              <td>{{ project.name }}</td>
              <td>
                <div class="owner-info">
                  <div class="owner-avatar" :style="{ backgroundColor: getAvatarColor(project.owner) }">
                    {{ getAvatarInitial(project.owner) }}
                  </div>
                  <span>{{ project.owner }}</span>
                </div>
              </td>
              <td>{{ formatTimeAgo(project.lastOpened) }}</td>
              <td>{{ formatDate(project.lastModified) }}</td>
              <td>
                <div class="action-menu">
                  <button class="action-btn" @click="toggleActionMenu(project.id)">
                    <span class="menu-icon">⋮</span>
                  </button>
                  <div v-if="activeMenu === project.id" class="action-dropdown">
                    <a href="#" @click="openProject(project.id)">Open</a>
                    <a href="#" @click="openInNewWindow(project.id)">Open in new window</a>
                    <a href="#" @click="downloadProject(project.id)">Download</a>
                    <a href="#" @click="moveToTrash(project.id)">Move to trash</a>
                  </div>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 创建项目模态框 -->
    <CreateProject 
      v-if="showCreateModal" 
      @close="showCreateModal = false"
      @projectCreated="handleProjectCreated"
    />
  </div>
</template>

<script>
import CreateProject from './CreateProject.vue'

export default {
  name: 'ProjectList',
  components: {
    CreateProject
  },
  data() {
    return {
      currentUser: {
        username: 'bjsun07',
        role: 'admin' // 这里应该从登录状态中获取
      },
      projects: [
        {
          id: 1,
          name: 'testProject',
          owner: 'bjsun07',
          lastOpened: new Date(Date.now() - 23 * 24 * 60 * 60 * 1000),
          lastModified: new Date('2026-01-29'),
          sharedWith: [],
          inTrash: false
        }
        // 可以添加更多测试数据
      ],
      filters: {
        ownedByMe: true,
        sharedWithMe: true,
        trash: false
      },
      sortBy: 'lastOpened',
      activeMenu: null,
      showCreateModal: false
    }
  },
  computed: {
    isAdmin() {
      return this.currentUser.role === 'admin';
    },
    filteredProjects() {
      let result = [...this.projects];
      
      // 应用筛选
      if (!this.filters.trash) {
        result = result.filter(project => !project.inTrash);
      }
      
      if (this.filters.ownedByMe && !this.filters.sharedWithMe) {
        result = result.filter(project => project.owner === this.currentUser.username);
      } else if (!this.filters.ownedByMe && this.filters.sharedWithMe) {
        result = result.filter(project => project.owner !== this.currentUser.username && project.sharedWith.includes(this.currentUser.username));
      } else if (this.filters.ownedByMe && this.filters.sharedWithMe) {
        result = result.filter(project => project.owner === this.currentUser.username || project.sharedWith.includes(this.currentUser.username));
      }
      
      // 应用排序
      result.sort((a, b) => {
        switch (this.sortBy) {
          case 'lastOpened':
            return new Date(b.lastOpened) - new Date(a.lastOpened);
          case 'lastModified':
            return new Date(b.lastModified) - new Date(a.lastModified);
          case 'name':
            return a.name.localeCompare(b.name);
          case 'owner':
            return a.owner.localeCompare(b.owner);
          default:
            return 0;
        }
      });
      
      return result;
    }
  },
  methods: {
    getAvatarColor(username) {
      const colors = ['#4CAF50', '#2196F3', '#FF9800', '#9C27B0', '#F44336', '#607D8B'];
      let hash = 0;
      for (let i = 0; i < username.length; i++) {
        hash = username.charCodeAt(i) + ((hash << 5) - hash);
      }
      return colors[Math.abs(hash) % colors.length];
    },
    getAvatarInitial(username) {
      return username.charAt(0).toUpperCase();
    },
    formatTimeAgo(date) {
      const now = new Date();
      const diff = now - new Date(date);
      const days = Math.floor(diff / (1000 * 60 * 60 * 24));
      if (days === 0) return 'Today';
      if (days === 1) return '1 day ago';
      return `${days} days ago`;
    },
    formatDate(date) {
      const d = new Date(date);
      return d.toLocaleDateString('en-GB', {
        day: 'numeric',
        month: 'long',
        year: 'numeric'
      });
    },
    filterProjects() {
      // 筛选逻辑已在computed中实现
    },
    sortProjects() {
      // 排序逻辑已在computed中实现
    },
    toggleActionMenu(projectId) {
      this.activeMenu = this.activeMenu === projectId ? null : projectId;
    },
    async openProject(projectId) {
      // 打开项目的逻辑
      console.log('Open project:', projectId);
      // 更新项目最近打开时间
      await this.updateLastOpened(projectId);
      // 这里可以跳转到项目编辑页面
      // this.$router.push(`/projects/edit/${projectId}`);
      this.activeMenu = null;
    },
    async openInNewWindow(projectId) {
      // 在新窗口打开项目的逻辑
      console.log('Open in new window:', projectId);
      // 更新项目最近打开时间
      await this.updateLastOpened(projectId);
      // 这里可以在新窗口打开项目编辑页面
      // window.open(`/projects/edit/${projectId}`, '_blank');
      this.activeMenu = null;
    },
    async updateLastOpened(projectId) {
      try {
        const response = await fetch(`/api/ontology/update-last-opened/${projectId}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          }
        });
        if (!response.ok) {
          throw new Error('Failed to update last opened time');
        }
        const updatedProject = await response.json();
        console.log('Last opened time updated:', updatedProject);
        // 更新前端项目列表中的lastOpened字段
        const project = this.projects.find(p => p.id === projectId);
        if (project) {
          project.lastOpened = new Date();
        }
      } catch (error) {
        console.error('Error updating last opened time:', error);
        // 可以在这里添加错误处理逻辑
      }
    },
    downloadProject(projectId) {
      // 下载项目的逻辑
      console.log('Download project:', projectId);
      // 调用后端导出接口
      const format = 'OWL'; // 默认格式
      window.location.href = `/api/ontology/import-export/export/${projectId}?format=${format}`;
      this.activeMenu = null;
    },
    moveToTrash(projectId) {
      // 移动到回收站的逻辑
      console.log('Move to trash:', projectId);
      const project = this.projects.find(p => p.id === projectId);
      if (project) {
        project.inTrash = true;
        // 调用后端接口来更新项目状态
        this.updateProjectStatus(projectId, 'TRASH');
      }
      this.activeMenu = null;
    },
    async updateProjectStatus(projectId, status) {
      try {
        const response = await fetch(`/api/ontology/update-status/${projectId}?status=${status}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          }
        });
        if (!response.ok) {
          throw new Error('Failed to update project status');
        }
        const updatedProject = await response.json();
        console.log('Project status updated:', updatedProject);
      } catch (error) {
        console.error('Error updating project status:', error);
        // 可以在这里添加错误处理逻辑
      }
    },
    handleProjectCreated(project) {
      // 处理项目创建成功后的逻辑
      console.log('Project created:', project);
      // 添加新项目到列表
      const newProject = {
        id: project.id,
        name: project.name,
        owner: this.currentUser.username,
        lastOpened: new Date(),
        lastModified: new Date(),
        sharedWith: [],
        inTrash: false
      };
      this.projects.unshift(newProject);
      // 刷新项目列表
      this.filterProjects();
    }
  }
}
</script>

<style scoped>
.projects-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.projects-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.projects-header h1 {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.username {
  font-weight: 500;
}

.user-avatar,
.owner-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 14px;
}

.projects-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  padding: 20px;
}

.projects-toolbar {
  margin-bottom: 20px;
}

.create-project-btn {
  background-color: #9C27B0;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
  font-size: 14px;
}

.create-project-btn:hover {
  background-color: #7B1FA2;
}

.projects-filters {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 5px;
}

.sort-group {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: auto;
}

.projects-table {
  width: 100%;
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th {
  text-align: left;
  padding: 12px;
  border-bottom: 1px solid #e0e0e0;
  font-weight: bold;
  background-color: #f5f5f5;
}

td {
  padding: 12px;
  border-bottom: 1px solid #e0e0e0;
}

.owner-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-menu {
  position: relative;
  display: inline-block;
}

.action-btn {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
}

.action-btn:hover {
  background-color: #f0f0f0;
}

.menu-icon {
  font-size: 16px;
}

.action-dropdown {
  position: absolute;
  right: 0;
  top: 100%;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
  min-width: 150px;
  z-index: 1000;
}

.action-dropdown a {
  display: block;
  padding: 8px 12px;
  text-decoration: none;
  color: #333;
  font-size: 14px;
}

.action-dropdown a:hover {
  background-color: #f5f5f5;
}

.action-dropdown a:nth-child(3) {
  color: #2196F3;
  font-weight: 500;
}

@media (max-width: 768px) {
  .projects-filters {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .sort-group {
    margin-left: 0;
    width: 100%;
  }
  
  .sort-group select {
    flex: 1;
  }
}
</style>