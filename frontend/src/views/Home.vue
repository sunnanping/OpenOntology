<template>
  <div class="home">
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <div class="container-fluid">
        <a class="navbar-brand" href="#">{{ $t('app.title') }}</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav">
            <li class="nav-item">
              <router-link to="/" class="nav-link active">{{ $t('nav.home') }}</router-link>
            </li>
            <template v-if="isLoggedIn">
              <li class="nav-item">
                <router-link to="/ontology" class="nav-link">{{ $t('nav.ontology') }}</router-link>
              </li>
              <li class="nav-item">
                <router-link to="/class" class="nav-link">{{ $t('nav.class') }}</router-link>
              </li>
              <li class="nav-item">
                <router-link to="/property" class="nav-link">{{ $t('nav.property') }}</router-link>
              </li>
              <li class="nav-item">
                <router-link to="/instance" class="nav-link">{{ $t('nav.instance') }}</router-link>
              </li>
              <li class="nav-item">
                <router-link to="/import-export" class="nav-link">{{ $t('app.import') }}/{{ $t('app.export') }}</router-link>
              </li>
              <li class="nav-item">
                <router-link to="/admin/login" class="nav-link">管理员</router-link>
              </li>
            </template>
          </ul>
          <div class="ms-auto d-flex align-items-center">
            <LanguageSwitcher class="me-3" />
            <template v-if="!isLoggedIn">
              <router-link to="/login" class="btn btn-primary me-2">{{ $t('nav.login') }}</router-link>
              <router-link to="/register" class="btn btn-secondary">{{ $t('nav.register') }}</router-link>
            </template>
            <template v-else>
              <span class="me-3">{{ currentUser.username }}</span>
              <button class="btn btn-outline-secondary" @click="logout">{{ $t('nav.logout') }}</button>
            </template>
          </div>
        </div>
      </div>
    </nav>
    <div class="container mt-5">
      <h1>{{ $t('home.welcome') }}</h1>
      <p class="lead">{{ $t('home.description') }}</p>
      <div class="row mt-5">
        <div class="col-md-4">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title">{{ $t('home.ontologyCard.title') }}</h5>
              <p class="card-text">{{ $t('home.ontologyCard.description') }}</p>
              <router-link v-if="isLoggedIn" to="/ontology" class="btn btn-primary">{{ $t('home.getStarted') }}</router-link>
            </div>
          </div>
        </div>
        <div class="col-md-4">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title">{{ $t('home.classCard.title') }}</h5>
              <p class="card-text">{{ $t('home.classCard.description') }}</p>
              <router-link v-if="isLoggedIn" to="/class" class="btn btn-primary">{{ $t('home.getStarted') }}</router-link>
            </div>
          </div>
        </div>
        <div class="col-md-4">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title">{{ $t('home.propertyCard.title') }}</h5>
              <p class="card-text">{{ $t('home.propertyCard.description') }}</p>
              <router-link v-if="isLoggedIn" to="/property" class="btn btn-primary">{{ $t('home.getStarted') }}</router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import LanguageSwitcher from '../components/LanguageSwitcher.vue'

const router = useRouter()
const isLoggedIn = ref(false)
const currentUser = ref({ username: '' })

onMounted(() => {
  // 检查登录状态
  const token = localStorage.getItem('token')
  const username = localStorage.getItem('username')
  if (token && username) {
    isLoggedIn.value = true
    currentUser.value.username = username
  }
})

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  isLoggedIn.value = false
  currentUser.value.username = ''
  router.push('/login')
}
</script>

<style scoped>
.home {
  min-height: 100vh;
}
</style>