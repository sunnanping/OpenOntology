<template>
  <div class="home">
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <div class="container-fluid">
        <a class="navbar-brand" href="#">WebProtégé</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav">
            <li class="nav-item">
              <router-link to="/" class="nav-link active">Home</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/ontology" class="nav-link">Ontology</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/class" class="nav-link">Class</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/property" class="nav-link">Property</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/instance" class="nav-link">Instance</router-link>
            </li>
          </ul>
          <div class="ms-auto">
            <router-link to="/login" class="btn btn-primary me-2">Login</router-link>
            <router-link to="/register" class="btn btn-secondary">Register</router-link>
          </div>
        </div>
      </div>
    </nav>
    <div class="container mt-5">
      <h1>Welcome to WebProtégé</h1>
      <p class="lead">A collaborative ontology editor and knowledge base framework.</p>
      <div class="row mt-5">
        <div class="col-md-4">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title">Ontology Management</h5>
              <p class="card-text">Create, edit, and manage ontologies.</p>
              <router-link to="/ontology" class="btn btn-primary">Get Started</router-link>
            </div>
          </div>
        </div>
        <div class="col-md-4">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title">Class Management</h5>
              <p class="card-text">Define and organize classes in your ontology.</p>
              <router-link to="/class" class="btn btn-primary">Get Started</router-link>
            </div>
          </div>
        </div>
        <div class="col-md-4">
          <div class="card">
            <div class="card-body">
              <h5 class="card-title">Property Management</h5>
              <p class="card-text">Define properties and relationships between classes.</p>
              <router-link to="/property" class="btn btn-primary">Get Started</router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// Home component
</script>

<style scoped>
.home {
  min-height: 100vh;
}
</style>