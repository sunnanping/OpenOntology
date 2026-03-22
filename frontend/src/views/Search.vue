<template>
  <div class="search">
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
      <div class="container-fluid">
        <a class="navbar-brand" href="#">WebProtégé</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav">
            <li class="nav-item">
              <router-link to="/" class="nav-link">Home</router-link>
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
            <li class="nav-item">
              <router-link to="/search" class="nav-link active">Search</router-link>
            </li>
          </ul>
        </div>
      </div>
    </nav>
    <div class="container mt-4">
      <h1>Search</h1>
      <div class="card mt-4">
        <div class="card-body">
          <form @submit.prevent="performSearch">
            <div class="row">
              <div class="col-md-6">
                <div class="input-group mb-3">
                  <input type="text" class="form-control" 
                         v-model="searchQuery" 
                         placeholder="Search entities..."
                         required>
                  <button class="btn btn-primary" type="submit">
                    <i class="bi bi-search"></i> Search
                  </button>
                </div>
              </div>
              <div class="col-md-3">
                <select class="form-control" v-model="selectedOntologyId">
                  <option value="">All Ontologies</option>
                  <option v-for="ontology in ontologies" :key="ontology.id" :value="ontology.id">
                    {{ ontology.name }}
                  </option>
                </select>
              </div>
              <div class="col-md-3">
                <select class="form-control" v-model="selectedEntityType">
                  <option value="">All Types</option>
                  <option value="class">Class</option>
                  <option value="property">Property</option>
                  <option value="instance">Instance</option>
                </select>
              </div>
            </div>
          </form>
        </div>
      </div>

      <div class="row mt-4">
        <div class="col-md-3">
          <div class="card">
            <div class="card-header">
              <h6>Filters</h6>
            </div>
            <div class="card-body">
              <div class="mb-3">
                <label class="form-label">Entity Type</label>
                <div class="form-check">
                  <input class="form-check-input" type="checkbox" 
                         id="filterClass" 
                         :checked="selectedEntityType === '' || selectedEntityType === 'class'"
                         @change="toggleEntityType('class')">
                  <label class="form-check-label" for="filterClass">Class</label>
                </div>
                <div class="form-check">
                  <input class="form-check-input" type="checkbox" 
                         id="filterProperty"
                         :checked="selectedEntityType === '' || selectedEntityType === 'property'"
                         @change="toggleEntityType('property')">
                  <label class="form-check-label" for="filterProperty">Property</label>
                </div>
                <div class="form-check">
                  <input class="form-check-input" type="checkbox" 
                         id="filterInstance"
                         :checked="selectedEntityType === '' || selectedEntityType === 'instance'"
                         @change="toggleEntityType('instance')">
                  <label class="form-check-label" for="filterInstance">Instance</label>
                </div>
              </div>
              <hr/>
              <h6>Quick Search</h6>
              <div class="d-grid gap-2">
                <button class="btn btn-outline-primary btn-sm" @click="searchByName">
                  Search by Name
                </button>
                <button class="btn btn-outline-primary btn-sm" @click="searchByIri">
                  Search by IRI
                </button>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-9">
          <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
              <h6>Search Results</h6>
              <span class="badge bg-secondary">{{ results.length }} results</span>
            </div>
            <div class="card-body">
              <div v-if="isSearching" class="text-center py-4">
                <div class="spinner-border text-primary" role="status">
                  <span class="visually-hidden">Loading...</span>
                </div>
              </div>
              <div v-else-if="results.length === 0" class="text-center text-muted py-4">
                <p v-if="!hasSearched">Enter a search query and click Search to find entities.</p>
                <p v-else>No results found for your search query.</p>
              </div>
              <div v-else>
                <div v-for="result in results" :key="result.entityId" class="card mb-2">
                  <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start">
                      <div>
                        <h6 class="mb-1">
                          <span :class="getTypeBadgeClass(result.entityType)" class="me-2">
                            {{ result.entityType }}
                          </span>
                          {{ result.name }}
                        </h6>
                        <p class="mb-1 text-muted small">{{ result.iri }}</p>
                        <p v-if="result.description" class="mb-0 small">
                          {{ truncateText(result.description, 100) }}
                        </p>
                      </div>
                      <div>
                        <button class="btn btn-outline-primary btn-sm" @click="viewEntity(result)">
                          View
                        </button>
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

    <div class="modal" :class="{ 'show': showDetailModal }" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Entity Details</h5>
            <button type="button" class="btn-close" @click="showDetailModal = false"></button>
          </div>
          <div class="modal-body" v-if="selectedResult">
            <div class="mb-3">
              <label class="form-label fw-bold">Name</label>
              <p>{{ selectedResult.name }}</p>
            </div>
            <div class="mb-3">
              <label class="form-label fw-bold">Type</label>
              <p>
                <span :class="getTypeBadgeClass(selectedResult.entityType)">
                  {{ selectedResult.entityType }}
                </span>
              </p>
            </div>
            <div class="mb-3">
              <label class="form-label fw-bold">IRI</label>
              <p><code>{{ selectedResult.iri }}</code></p>
            </div>
            <div class="mb-3" v-if="selectedResult.description">
              <label class="form-label fw-bold">Description</label>
              <p>{{ selectedResult.description }}</p>
            </div>
            <div class="mb-3">
              <label class="form-label fw-bold">Ontology ID</label>
              <p>{{ selectedResult.ontologyId }}</p>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showDetailModal = false">Close</button>
            <button type="button" class="btn btn-primary" @click="goToEntity">Go to Entity</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import http from '@/utils/http'

const router = useRouter()
const searchQuery = ref('')
const selectedOntologyId = ref('')
const selectedEntityType = ref('')
const results = ref([])
const ontologies = ref([])
const isSearching = ref(false)
const hasSearched = ref(false)
const showDetailModal = ref(false)
const selectedResult = ref(null)

onMounted(async () => {
  await loadOntologies()
})

const loadOntologies = async () => {
  try {
    const response = await http.get('/ontology/findAll')
    ontologies.value = response.data
  } catch (error) {
    console.error('Failed to load ontologies:', error)
  }
}

const performSearch = async () => {
  if (!searchQuery.value.trim()) return
  
  isSearching.value = true
  hasSearched.value = true
  
  try {
    const params = new URLSearchParams()
    params.append('query', searchQuery.value)
    if (selectedOntologyId.value) {
      params.append('ontologyId', selectedOntologyId.value)
    }
    if (selectedEntityType.value) {
      params.append('entityType', selectedEntityType.value)
    }
    
    const response = await http.get(`/search/query?${params.toString()}`)
    results.value = response.data
  } catch (error) {
    console.error('Search failed:', error)
    alert('Search failed. Please try again.')
  } finally {
    isSearching.value = false
  }
}

const searchByName = async () => {
  if (!searchQuery.value.trim()) {
    alert('Please enter a search query')
    return
  }
  
  isSearching.value = true
  hasSearched.value = true
  
  try {
    const response = await http.get(`/search/byname?name=${encodeURIComponent(searchQuery.value)}`)
    results.value = response.data
  } catch (error) {
    console.error('Search by name failed:', error)
    alert('Search failed. Please try again.')
  } finally {
    isSearching.value = false
  }
}

const searchByIri = async () => {
  if (!searchQuery.value.trim()) {
    alert('Please enter a search query')
    return
  }
  
  isSearching.value = true
  hasSearched.value = true
  
  try {
    const response = await http.get(`/search/byiri?iri=${encodeURIComponent(searchQuery.value)}`)
    results.value = response.data
  } catch (error) {
    console.error('Search by IRI failed:', error)
    alert('Search failed. Please try again.')
  } finally {
    isSearching.value = false
  }
}

const toggleEntityType = (type) => {
  if (selectedEntityType.value === type) {
    selectedEntityType.value = ''
  } else {
    selectedEntityType.value = type
  }
}

const viewEntity = (result) => {
  selectedResult.value = result
  showDetailModal.value = true
}

const goToEntity = () => {
  if (!selectedResult.value) return
  
  const type = selectedResult.value.entityType
  const id = selectedResult.value.entityId
  
  switch (type) {
    case 'class':
      router.push(`/class`)
      break
    case 'property':
      router.push(`/property`)
      break
    case 'instance':
      router.push(`/instance`)
      break
  }
  
  showDetailModal.value = false
}

const getTypeBadgeClass = (type) => {
  switch (type) {
    case 'class': return 'badge bg-success'
    case 'property': return 'badge bg-primary'
    case 'instance': return 'badge bg-warning'
    default: return 'badge bg-secondary'
  }
}

const truncateText = (text, maxLength) => {
  if (!text || text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}
</script>

<style scoped>
.search {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.modal.show {
  display: block;
  background-color: rgba(0, 0, 0, 0.5);
}

code {
  background-color: #f8f9fa;
  padding: 2px 6px;
  border-radius: 3px;
}
</style>