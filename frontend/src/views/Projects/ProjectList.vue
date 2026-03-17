<template>
  <div class="projects-container">
    <div class="projects-header">
      <div class="header-left">
        <h1>Projects</h1>
        <el-button type="primary" class="create-btn" @click="showCreateModal = true">
          <el-icon><Plus /></el-icon>
          Create New Project
        </el-button>
      </div>
      <div class="header-right">
        <el-dropdown trigger="click" @command="handleUserCommand">
          <div class="user-menu">
            <el-avatar :size="32" :style="{ backgroundColor: getAvatarColor(currentUser.username) }">
              {{ getAvatarInitial(currentUser.username) }}
            </el-avatar>
            <span class="username">{{ currentUser.username }}</span>
            <el-icon><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>
                Profile
              </el-dropdown-item>
              <el-dropdown-item command="changeEmail">
                <el-icon><Message /></el-icon>
                Change Email Address
              </el-dropdown-item>
              <el-dropdown-item command="changePassword">
                <el-icon><Lock /></el-icon>
                Change Password
              </el-dropdown-item>
              <el-dropdown-item divided command="signOut">
                <el-icon><SwitchButton /></el-icon>
                Sign Out
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <div class="projects-content">
      <div class="projects-filters">
        <el-checkbox v-model="filters.ownedByMe" @change="filterProjects">Owned by Me</el-checkbox>
        <el-checkbox v-model="filters.sharedWithMe" @change="filterProjects">Shared with Me</el-checkbox>
        <el-checkbox v-model="filters.trash" @change="filterProjects">Trash</el-checkbox>
        
        <div class="search-box">
          <el-input
            v-model="searchQuery"
            placeholder="Search projects..."
            prefix-icon="Search"
            clearable
            @input="filterProjects"
          />
        </div>

        <div class="sort-group">
          <span>Sort by:</span>
          <el-select v-model="sortBy" @change="sortProjects" style="width: 150px;">
            <el-option label="Last Opened" value="lastOpened" />
            <el-option label="Last Modified" value="lastModified" />
            <el-option label="Name" value="name" />
            <el-option label="Owner" value="owner" />
          </el-select>
        </div>
      </div>

      <el-table :data="filteredProjects" style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="Project name" min-width="200">
          <template #default="{ row }">
            <div class="project-name">
              <el-icon><Folder /></el-icon>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="owner" label="Owner" width="180">
          <template #default="{ row }">
            <div class="owner-info">
              <el-avatar :size="24" :style="{ backgroundColor: getAvatarColor(row.owner) }">
                {{ getAvatarInitial(row.owner) }}
              </el-avatar>
              <span>{{ row.owner }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="lastOpened" label="Last opened" width="150">
          <template #default="{ row }">
            {{ formatTimeAgo(row.lastOpened) }}
          </template>
        </el-table-column>
        <el-table-column prop="lastModified" label="Last modified" width="180">
          <template #default="{ row }">
            {{ formatDate(row.lastModified) }}
          </template>
        </el-table-column>
        <el-table-column label="Actions" width="100" fixed="right">
          <template #default="{ row }">
            <el-dropdown trigger="click" @command="(cmd) => handleCommand(cmd, row)">
              <el-button type="text">
                <el-icon><MoreFilled /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="open">
                    <el-icon><FolderOpened /></el-icon>
                    Open
                  </el-dropdown-item>
                  <el-dropdown-item command="openNew">
                    <el-icon><Link /></el-icon>
                    Open in new window
                  </el-dropdown-item>
                  <el-dropdown-item command="download" divided>
                    <el-icon><Download /></el-icon>
                    Download
                  </el-dropdown-item>
                  <el-dropdown-item command="trash" v-if="!filters.trash" divided>
                    <el-icon><Delete /></el-icon>
                    Move to trash
                  </el-dropdown-item>
                  <el-dropdown-item command="restore" v-if="filters.trash">
                    <el-icon><RefreshLeft /></el-icon>
                    Restore
                  </el-dropdown-item>
                  <el-dropdown-item command="delete" v-if="filters.trash" divided>
                    <el-icon><Delete /></el-icon>
                    Delete permanently
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <div class="projects-pagination" v-if="totalProjects > pageSize">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="totalProjects"
          layout="prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <CreateProject 
      v-if="showCreateModal" 
      @close="showCreateModal = false"
      @projectCreated="handleProjectCreated"
    />
    
    <!-- Change Email Dialog -->
    <DraggableModal
      v-if="showChangeEmailDialog"
      title="Change Email Address"
      width="400px"
      @close="showChangeEmailDialog = false"
    >
      <div class="form-group">
        <label>Current Email</label>
        <input type="text" v-model="emailForm.currentEmail" disabled class="form-control" />
      </div>
      <div class="form-group">
        <label>New Email</label>
        <input type="email" v-model="emailForm.newEmail" placeholder="Enter new email address" class="form-control" />
      </div>
      <div class="form-group">
        <label>Password (for verification)</label>
        <input type="password" v-model="emailForm.password" placeholder="Enter your password" class="form-control" />
      </div>
      <template #footer>
        <button class="btn btn-secondary" @click="showChangeEmailDialog = false">Cancel</button>
        <button class="btn btn-primary" style="background-color: #0d6efd; border-color: #0d6efd;" @click="changeEmail">Change Email</button>
      </template>
    </DraggableModal>
    
    <!-- Change Password Dialog -->
    <DraggableModal
      v-if="showChangePasswordDialog"
      title="Change Password"
      width="400px"
      @close="showChangePasswordDialog = false"
    >
      <div class="form-group">
        <label>Current Password</label>
        <input type="password" v-model="passwordForm.currentPassword" placeholder="Enter current password" class="form-control" />
      </div>
      <div class="form-group">
        <label>New Password</label>
        <input type="password" v-model="passwordForm.newPassword" placeholder="Enter new password" class="form-control" />
      </div>
      <div class="form-group">
        <label>Confirm New Password</label>
        <input type="password" v-model="passwordForm.confirmPassword" placeholder="Confirm new password" class="form-control" />
      </div>
      <template #footer>
        <button class="btn btn-secondary" @click="showChangePasswordDialog = false">Cancel</button>
        <button class="btn btn-primary" style="background-color: #0d6efd; border-color: #0d6efd;" @click="changePassword">Change Password</button>
      </template>
    </DraggableModal>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Folder, FolderOpened, Download, Delete, MoreFilled, Link, RefreshLeft, Search, ArrowDown, User, Message, Lock, SwitchButton } from '@element-plus/icons-vue'
import CreateProject from './CreateProject.vue'
import DraggableModal from '@/components/DraggableModal.vue'
import http from '@/utils/http'

export default {
  name: 'ProjectList',
  components: {
    CreateProject,
    DraggableModal,
    Plus,
    Folder,
    FolderOpened,
    Download,
    Delete,
    MoreFilled,
    Link,
    RefreshLeft,
    Search,
    ArrowDown,
    User,
    Message,
    Lock,
    SwitchButton
  },
  setup() {
    const router = useRouter()
    const currentUser = ref({ username: '', role: '' })
    const projects = ref([])
    const loading = ref(false)
    const filters = ref({
      ownedByMe: true,
      sharedWithMe: true,
      trash: false
    })
    const sortBy = ref('lastOpened')
    const searchQuery = ref('')
    const showCreateModal = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(20)
    const totalProjects = ref(0)
    
    // 用户菜单对话框
    const showChangeEmailDialog = ref(false)
    const showChangePasswordDialog = ref(false)
    const emailForm = ref({
      currentEmail: '',
      newEmail: '',
      password: ''
    })
    const passwordForm = ref({
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    })

    const isAdmin = computed(() => currentUser.value.role === 'admin')

    const filteredProjects = computed(() => {
      let result = [...projects.value]
      
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        result = result.filter(p => 
          p.name.toLowerCase().includes(query) ||
          p.owner.toLowerCase().includes(query)
        )
      }
      
      if (!filters.value.trash) {
        result = result.filter(project => project.status !== 'TRASH')
      } else {
        result = result.filter(project => project.status === 'TRASH')
      }
      
      if (filters.value.ownedByMe && !filters.value.sharedWithMe) {
        result = result.filter(project => project.owner === currentUser.value.username)
      } else if (!filters.value.ownedByMe && filters.value.sharedWithMe) {
        result = result.filter(project => 
          project.owner !== currentUser.value.username && 
          project.sharedWith && project.sharedWith.includes(currentUser.value.username)
        )
      } else if (filters.value.ownedByMe && filters.value.sharedWithMe) {
        result = result.filter(project => 
          project.owner === currentUser.value.username || 
          (project.sharedWith && project.sharedWith.includes(currentUser.value.username))
        )
      }
      
      result.sort((a, b) => {
        switch (sortBy.value) {
          case 'lastOpened':
            return new Date(b.lastOpened || 0) - new Date(a.lastOpened || 0)
          case 'lastModified':
            return new Date(b.lastModified || 0) - new Date(a.lastModified || 0)
          case 'name':
            return (a.name || '').localeCompare(b.name || '')
          case 'owner':
            return (a.owner || '').localeCompare(b.owner || '')
          default:
            return 0
        }
      })
      
      totalProjects.value = result.length
      const start = (currentPage.value - 1) * pageSize.value
      return result.slice(start, start + pageSize.value)
    })

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
      if (!date) return 'Never'
      const now = new Date()
      const diff = now - new Date(date)
      const days = Math.floor(diff / (1000 * 60 * 60 * 24))
      if (days === 0) return 'Today'
      if (days === 1) return '1 day ago'
      return `${days} days ago`
    }

    const formatDate = (date) => {
      if (!date) return '-'
      const d = new Date(date)
      return d.toLocaleDateString('en-GB', {
        day: 'numeric',
        month: 'long',
        year: 'numeric'
      })
    }

    const loadProjects = async () => {
      loading.value = true
      try {
        const userStr = localStorage.getItem('user')
        if (userStr) {
          const user = JSON.parse(userStr)
          currentUser.value.username = user.username
          currentUser.value.role = user.role || 'user'
        }

        const response = await http.get('/projects/available', {
          params: { username: currentUser.value.username }
        })
        projects.value = response.data || []
      } catch (error) {
        console.error('Failed to load projects:', error)
        ElMessage.error('Failed to load projects')
      } finally {
        loading.value = false
      }
    }

    const filterProjects = () => {
      currentPage.value = 1
    }

    const sortProjects = () => {
      currentPage.value = 1
    }

    const handlePageChange = (page) => {
      currentPage.value = page
    }

    const handleCommand = async (command, project) => {
      switch (command) {
        case 'open':
          await openProject(project)
          break
        case 'openNew':
          await openInNewWindow(project)
          break
        case 'download':
          downloadProject(project)
          break
        case 'trash':
          await moveToTrash(project)
          break
        case 'restore':
          await restoreProject(project)
          break
        case 'delete':
          await deleteProject(project)
          break
      }
    }

    // 用户菜单命令处理
    const handleUserCommand = (command) => {
      switch (command) {
        case 'profile':
          router.push('/profile')
          break
        case 'changeEmail':
          showChangeEmailDialog.value = true
          break
        case 'changePassword':
          showChangePasswordDialog.value = true
          break
        case 'signOut':
          handleSignOut()
          break
      }
    }

    const handleSignOut = () => {
      localStorage.removeItem('user')
      ElMessage.success('Signed out successfully')
      router.push('/login')
    }

    const changeEmail = async () => {
      try {
        await http.post('/user/change-email', {
          username: currentUser.value.username,
          newEmail: emailForm.value.newEmail,
          password: emailForm.value.password
        })
        ElMessage.success('Email changed successfully')
        showChangeEmailDialog.value = false
        emailForm.value = { currentEmail: '', newEmail: '', password: '' }
      } catch (error) {
        console.error('Failed to change email:', error)
        ElMessage.error('Failed to change email')
      }
    }

    const changePassword = async () => {
      if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
        ElMessage.error('New passwords do not match')
        return
      }
      try {
        await http.post('/user/change-password', {
          username: currentUser.value.username,
          currentPassword: passwordForm.value.currentPassword,
          newPassword: passwordForm.value.newPassword
        })
        ElMessage.success('Password changed successfully')
        showChangePasswordDialog.value = false
        passwordForm.value = { currentPassword: '', newPassword: '', confirmPassword: '' }
      } catch (error) {
        console.error('Failed to change password:', error)
        ElMessage.error('Failed to change password')
      }
    }

    const openProject = async (project) => {
      try {
        await http.put(`/projects/update-last-opened/${project.id}`)
        router.push({
          path: '/editor',
          query: { p: project.id, v: 'Classes' }
        })
      } catch (error) {
        console.error('Failed to open project:', error)
        ElMessage.error('Failed to open project')
      }
    }

    const openInNewWindow = async (project) => {
      try {
        await http.put(`/projects/update-last-opened/${project.id}`)
        window.open(`/editor?p=${project.id}&v=Classes`, '_blank')
      } catch (error) {
        console.error('Failed to open project:', error)
        ElMessage.error('Failed to open project')
      }
    }

    const downloadProject = (project) => {
      window.location.href = `/api/projects/download/${project.id}?format=OWL`
    }

    const moveToTrash = async (project) => {
      try {
        await ElMessageBox.confirm(
          `Are you sure you want to move "${project.name}" to trash?`,
          'Confirm',
          {
            confirmButtonText: 'Yes',
            cancelButtonText: 'Cancel',
            type: 'warning'
          }
        )
        
        await http.put(`/projects/move-to-trash/${project.id}`)
        ElMessage.success('Project moved to trash')
        await loadProjects()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('Failed to move to trash:', error)
          ElMessage.error('Failed to move project to trash')
        }
      }
    }

    const restoreProject = async (project) => {
      try {
        await http.put(`/projects/restore/${project.id}`)
        ElMessage.success('Project restored')
        await loadProjects()
      } catch (error) {
        console.error('Failed to restore project:', error)
        ElMessage.error('Failed to restore project')
      }
    }

    const deleteProject = async (project) => {
      try {
        await ElMessageBox.confirm(
          `Are you sure you want to permanently delete "${project.name}"? This action cannot be undone.`,
          'Delete Permanently',
          {
            confirmButtonText: 'Delete',
            cancelButtonText: 'Cancel',
            type: 'error'
          }
        )
        
        await http.delete(`/projects/delete/${project.id}`)
        ElMessage.success('Project deleted permanently')
        await loadProjects()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('Failed to delete project:', error)
          ElMessage.error('Failed to delete project')
        }
      }
    }

    const handleProjectCreated = (project) => {
      showCreateModal.value = false
      loadProjects()
    }

    onMounted(() => {
      loadProjects()
    })

    return {
      currentUser,
      projects,
      loading,
      filters,
      sortBy,
      searchQuery,
      showCreateModal,
      currentPage,
      pageSize,
      totalProjects,
      isAdmin,
      filteredProjects,
      showChangeEmailDialog,
      showChangePasswordDialog,
      emailForm,
      passwordForm,
      getAvatarColor,
      getAvatarInitial,
      formatTimeAgo,
      formatDate,
      filterProjects,
      sortProjects,
      handlePageChange,
      handleCommand,
      handleUserCommand,
      handleSignOut,
      changeEmail,
      changePassword,
      handleProjectCreated
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

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-left h1 {
  font-size: 24px;
  font-weight: bold;
  margin: 0;
}

.create-btn {
  background-color: #9c27b0;
  border-color: #9c27b0;
}

.create-btn:hover {
  background-color: #7b1fa2;
  border-color: #7b1fa2;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.user-menu:hover {
  background-color: #f5f5f5;
}

.username {
  font-weight: 500;
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

.projects-filters {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  flex-wrap: wrap;
  align-items: center;
}

.search-box {
  flex: 1;
  max-width: 300px;
}

.sort-group {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: auto;
}

.project-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.owner-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.projects-pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
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

.form-control:disabled {
  background-color: #f5f7fa;
  color: #c0c4cc;
  cursor: not-allowed;
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
