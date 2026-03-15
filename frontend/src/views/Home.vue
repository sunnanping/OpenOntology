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
          </ul>
          <div class="ms-auto d-flex align-items-center">
            <LanguageSwitcher class="me-3" />
            <router-link to="/login" class="btn btn-primary me-2">{{ $t('nav.login') }}</router-link>
            <router-link to="/register" class="btn btn-secondary">{{ $t('nav.register') }}</router-link>
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
              <router-link to="/ontology" class="btn btn-primary">{{ $t('home.getStarted') }}</router-link>
            </div>
          </div>
        </div>
        <div class="col-md-4">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title">{{ $t('home.classCard.title') }}</h5>
              <p class="card-text">{{ $t('home.classCard.description') }}</p>
              <router-link to="/class" class="btn btn-primary">{{ $t('home.getStarted') }}</router-link>
            </div>
          </div>
        </div>
        <div class="col-md-4">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title">{{ $t('home.propertyCard.title') }}</h5>
              <p class="card-text">{{ $t('home.propertyCard.description') }}</p>
              <router-link to="/property" class="btn btn-primary">{{ $t('home.getStarted') }}</router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import LanguageSwitcher from '../components/LanguageSwitcher.vue'
</script>

<style scoped>
.home {
  min-height: 100vh;
}
</style>