<template>
  <nav class="top-menu-bar navbar navbar-expand navbar-dark">
    <div class="container-fluid menu-container">
      <!-- 最左侧：项目Logo和名称 + Home -->
      <div class="menu-left navbar-brand-wrapper">
        <a class="menu-brand navbar-brand" href="#">
          <i class="bi bi-grid-3x3-gap"></i>
          <span class="project-name d-none d-sm-inline">{{ projectName }}</span>
          <span class="project-name d-inline d-sm-none">{{ projectName ? projectName.substring(0, 10) : 'Project' }}</span>
        </a>
        <a class="menu-link nav-link home-link" href="#" @click.prevent="handleGoHome">
          <i class="bi bi-house-door"></i> 
          <span class="d-none d-md-inline">Home</span>
        </a>
      </div>
      
      <!-- 中间：空白区域，占据剩余空间 -->
      <div class="menu-spacer"></div>
      
      <!-- 右侧：系统菜单（Display、Project、Share，位于用户账号左侧）- 使用Bootstrap响应式类 -->
      <div class="menu-system navbar-nav-wrapper d-none d-lg-flex">
        <ul class="navbar-nav">
          <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
              <i class="bi bi-display d-inline d-xl-none"></i>
              <span class="d-none d-xl-inline">Display</span>
            </a>
            <ul class="dropdown-menu">
              <li><a class="dropdown-item" href="#" @click.prevent="handleTheme"><i class="bi bi-palette me-2"></i>Theme</a></li>
              <li><a class="dropdown-item" href="#" @click.prevent="handleLanguage"><i class="bi bi-globe me-2"></i>Language</a></li>
            </ul>
          </li>
          <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
              <i class="bi bi-folder d-inline d-xl-none"></i>
              <span class="d-none d-xl-inline">Project</span>
            </a>
            <ul class="dropdown-menu">
              <li><a class="dropdown-item" href="#" @click.prevent="handleSettings"><i class="bi bi-gear me-2"></i>Settings</a></li>
              <li><a class="dropdown-item" href="#" @click.prevent="handleShare"><i class="bi bi-share me-2"></i>Share</a></li>
            </ul>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="#" @click.prevent="handleShare">
              <i class="bi bi-share d-inline d-xl-none"></i>
              <span class="d-none d-xl-inline">Share</span>
            </a>
          </li>
        </ul>
      </div>
      
      <!-- 最右侧：用户信息和帮助 -->
      <div class="menu-right">
        <!-- 移动端菜单切换按钮 -->
        <button class="navbar-toggler d-lg-none mobile-menu-btn" type="button" @click="toggleMobileMenu">
          <i class="bi bi-list"></i>
        </button>
        
        <div class="menu-item dropdown d-none d-md-block">
          <a class="menu-link dropdown-toggle" href="#" data-bs-toggle="dropdown">
            <i class="bi bi-person-circle"></i> 
            <span class="d-none d-xl-inline">{{ currentUser?.username || 'User' }}</span>
          </a>
          <ul class="dropdown-menu dropdown-menu-end">
            <li><a class="dropdown-item" href="#" @click.prevent="handleProfile"><i class="bi bi-person me-2"></i>Profile</a></li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item" href="#" @click.prevent="handleLogout"><i class="bi bi-box-arrow-right me-2"></i>Logout</a></li>
          </ul>
        </div>
        <div class="menu-item dropdown d-none d-sm-block">
          <a class="menu-link" href="#" data-bs-toggle="dropdown">
            <i class="bi bi-question-circle"></i>
            <span class="d-none d-xl-inline">Help</span>
          </a>
          <ul class="dropdown-menu dropdown-menu-end">
            <li><a class="dropdown-item" href="#" @click.prevent="handleDocumentation"><i class="bi bi-book me-2"></i>Documentation</a></li>
            <li><a class="dropdown-item" href="#" @click.prevent="handleAbout"><i class="bi bi-info-circle me-2"></i>About</a></li>
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
        <a href="#" @click.prevent="handleGoHome" class="mobile-menu-item">
          <i class="bi bi-house-door"></i> Home
        </a>
        <a href="#" @click.prevent="handleTheme" class="mobile-menu-item">
          <i class="bi bi-display"></i> Display
        </a>
        <a href="#" @click.prevent="handleSettings" class="mobile-menu-item">
          <i class="bi bi-folder"></i> Project
        </a>
        <a href="#" @click.prevent class="mobile-menu-item">
          <i class="bi bi-share"></i> Share
        </a>
        <div class="mobile-menu-divider"></div>
        <a href="#" @click.prevent="handleProfile" class="mobile-menu-item">
          <i class="bi bi-person"></i> Profile
        </a>
        <a href="#" @click.prevent="handleLogout" class="mobile-menu-item">
          <i class="bi bi-box-arrow-right"></i> Logout
        </a>
        <div class="mobile-menu-divider"></div>
        <a href="#" @click.prevent="handleDocumentation" class="mobile-menu-item">
          <i class="bi bi-book"></i> Documentation
        </a>
        <a href="#" @click.prevent="handleAbout" class="mobile-menu-item">
          <i class="bi bi-info-circle"></i> About
        </a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  projectName: {
    type: String,
    required: true
  },
  currentUser: {
    type: Object,
    default: () => ({ username: 'User' })
  },
  projectInfo: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits([
  'go-home',
  'logout',
  'settings',
  'share',
  'theme',
  'language',
  'profile',
  'documentation',
  'about'
])

const router = useRouter()
const showMobileMenu = ref(false)

const toggleMobileMenu = () => {
  showMobileMenu.value = !showMobileMenu.value
}

const handleGoHome = () => {
  emit('go-home')
  router.push('/projects/list')
}

const handleLogout = () => {
  emit('logout')
  localStorage.removeItem('user')
  localStorage.removeItem('token')
  router.push('/login')
}

const handleSettings = () => {
  emit('settings')
}

const handleShare = () => {
  emit('share')
}

const handleTheme = () => {
  emit('theme')
}

const handleLanguage = () => {
  emit('language')
}

const handleProfile = () => {
  emit('profile')
}

const handleDocumentation = () => {
  emit('documentation')
}

const handleAbout = () => {
  emit('about')
}
</script>

<style scoped>
.top-menu-bar {
  background: linear-gradient(180deg, #4a90d9 0%, #357abd 100%);
  height: 36px;
  padding: 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 1030;
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
.menu-system,
.menu-right {
  display: flex;
  align-items: center;
  gap: 4px;
}

.menu-spacer {
  flex: 1;
}

.menu-brand {
  display: flex;
  align-items: center;
  gap: 6px;
  color: white;
  text-decoration: none;
  font-size: 13px;
  font-weight: 500;
  padding: 4px 8px;
  border-radius: 3px;
  transition: background-color 0.2s;
}

.menu-brand:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.menu-brand i {
  font-size: 14px;
}

.menu-link {
  display: flex;
  align-items: center;
  gap: 4px;
  color: white;
  text-decoration: none;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 3px;
  transition: background-color 0.2s;
  cursor: pointer;
}

.menu-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.menu-link i {
  font-size: 12px;
}

.home-link {
  margin-left: 8px;
}

.menu-system {
  display: flex;
  align-items: center;
}

.menu-system .navbar-nav {
  display: flex;
  flex-direction: row;
  gap: 2px;
}

.menu-system .nav-link {
  color: white;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 3px;
  transition: background-color 0.2s;
}

.menu-system .nav-link:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.menu-system .dropdown-menu {
  font-size: 12px;
  border: 1px solid #ccc;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.menu-system .dropdown-item {
  padding: 6px 12px;
}

.menu-system .dropdown-item:hover {
  background-color: #f5f5f5;
}

.menu-item {
  position: relative;
}

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
  background-color: white;
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
}

.mobile-menu-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-bottom: 1px solid #e0e0e0;
  background: linear-gradient(180deg, #4a90d9 0%, #357abd 100%);
}

.mobile-menu-title {
  font-size: 16px;
  font-weight: 600;
  color: white;
}

.mobile-menu-close {
  background: none;
  border: none;
  color: white;
  font-size: 20px;
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
  font-size: 14px;
  transition: background-color 0.2s;
}

.mobile-menu-item:hover {
  background-color: #f5f5f5;
}

.mobile-menu-item i {
  font-size: 18px;
  color: #666;
  width: 24px;
  text-align: center;
}

.mobile-menu-divider {
  height: 1px;
  background-color: #e0e0e0;
  margin: 8px 16px;
}

.mobile-menu-btn {
  color: white;
  border: none;
  padding: 4px 8px;
  font-size: 16px;
  background: transparent;
}

.mobile-menu-btn:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

/* Bootstrap overrides */
.navbar-nav .nav-link {
  color: white;
}

.navbar-nav .dropdown-toggle::after {
  margin-left: 4px;
  vertical-align: middle;
  border-top-color: white;
}

.dropdown-menu-end {
  right: 0;
  left: auto;
}

/* Responsive adjustments */
@media (max-width: 1199px) {
  .menu-system {
    display: none !important;
  }
}

@media (max-width: 767px) {
  .menu-right .menu-item.d-none.d-md-block {
    display: none !important;
  }
}

@media (max-width: 575px) {
  .menu-right .menu-item.d-none.d-sm-block {
    display: none !important;
  }
}
</style>
