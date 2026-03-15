<template>
  <div class="version-control">
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
              <router-link to="/version" class="nav-link active">Version Control</router-link>
            </li>
          </ul>
        </div>
      </div>
    </nav>
    <div class="container mt-4">
      <h1>Version Control</h1>
      <div class="row mt-4">
        <div class="col-md-4">
          <div class="card">
            <div class="card-header">
              <h5>Create Version</h5>
            </div>
            <div class="card-body">
              <form @submit.prevent="createVersion">
                <div class="mb-3">
                  <label class="form-label">Ontology</label>
                  <select class="form-control" v-model="newVersion.ontologyId" required>
                    <option value="">-- Select Ontology --</option>
                    <option v-for="ontology in ontologies" :key="ontology.id" :value="ontology.id">
                      {{ ontology.name }}
                    </option>
                  </select>
                </div>
                <div class="mb-3">
                  <label class="form-label">Description</label>
                  <textarea class="form-control" v-model="newVersion.description" rows="3"></textarea>
                </div>
                <button type="submit" class="btn btn-primary w-100">Create Version</button>
              </form>
            </div>
          </div>
          <div class="card mt-3">
            <div class="card-header">
              <h5>Ontologies</h5>
            </div>
            <div class="card-body">
              <div v-if="ontologies.length === 0" class="text-muted text-center py-3">
                No ontologies found
              </div>
              <div v-else>
                <div v-for="ontology in ontologies" :key="ontology.id"
                     class="border rounded p-2 mb-2"
                     :class="{ 'bg-light': selectedOntologyId === ontology.id }"
                     @click="selectOntology(ontology.id)"
                     style="cursor: pointer;">
                  <strong>{{ ontology.name }}</strong>
                  <br/>
                  <small class="text-muted">{{ ontology.iri }}</small>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-8">
          <div v-if="selectedOntologyId">
            <div class="card">
              <div class="card-header d-flex justify-content-between align-items-center">
                <h5>Version History</h5>
                <div>
                  <button class="btn btn-outline-primary btn-sm me-2" @click="loadVersions">
                    Refresh
                  </button>
                  <button class="btn btn-outline-secondary btn-sm" @click="exportHistory">
                    Export History
                  </button>
                </div>
              </div>
              <div class="card-body">
                <div v-if="versions.length === 0" class="text-muted text-center py-4">
                  No versions found. Create a version to start tracking changes.
                </div>
                <div v-else>
                  <div class="timeline">
                    <div v-for="(version, index) in versions" :key="version.id" class="timeline-item">
                      <div class="timeline-marker" :class="index === 0 ? 'bg-primary' : 'bg-secondary'"></div>
                      <div class="timeline-content">
                        <div class="card">
                          <div class="card-body">
                            <div class="d-flex justify-content-between align-items-start">
                              <div>
                                <h6 class="mb-1">
                                  <span class="badge bg-primary me-2">v{{ version.versionNumber }}</span>
                                  {{ version.description || 'No description' }}
                                </h6>
                                <small class="text-muted">
                                  Created by {{ version.createdByName || version.createdBy }} 
                                  on {{ formatDate(version.createdAt) }}
                                </small>
                              </div>
                              <div>
                                <button v-if="index !== 0" 
                                        class="btn btn-outline-primary btn-sm me-2"
                                        @click="rollbackVersion(version.id)">
                                  Rollback
                                </button>
                                <button class="btn btn-outline-info btn-sm me-2"
                                        @click="viewSnapshot(version)">
                                  View
                                </button>
                                <button class="btn btn-outline-danger btn-sm"
                                        @click="deleteVersion(version.id)">
                                  Delete
                                </button>
                              </div>
                            </div>
                            <div v-if="version.parentVersionId" class="mt-2">
                              <small class="text-muted">Parent: v{{ getVersionNumber(version.parentVersionId) }}</small>
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
          <div v-else class="card">
            <div class="card-body text-center text-muted py-5">
              <h5>Select an ontology to view version history</h5>
              <p>Choose an ontology from the list to see its version history.</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="modal" :class="{ 'show': showSnapshotModal }" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Version Snapshot</h5>
            <button type="button" class="btn-close" @click="showSnapshotModal = false"></button>
          </div>
          <div class="modal-body" v-if="selectedVersion">
            <h6>Version v{{ selectedVersion.versionNumber }}</h6>
            <p class="text-muted">{{ selectedVersion.description }}</p>
            <hr/>
            <div v-if="selectedVersion.snapshot">
              <h6>Snapshot Data</h6>
              <pre>{{ JSON.stringify(selectedVersion.snapshot, null, 2) }}</pre>
            </div>
            <div v-else class="text-muted">
              No snapshot data available
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showSnapshotModal = false">Close</button>
            <button type="button" class="btn btn-primary" @click="exportSnapshot">Export Snapshot</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const ontologies = ref([])
const versions = ref([])
const selectedOntologyId = ref('')
const selectedVersion = ref(null)
const showSnapshotModal = ref(false)
const newVersion = ref({
  ontologyId: '',
  description: ''
})

onMounted(async () => {
  await loadOntologies()
})

const loadOntologies = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/ontology/findAll')
    ontologies.value = response.data
  } catch (error) {
    console.error('Failed to load ontologies:', error)
  }
}

const selectOntology = async (ontologyId) => {
  selectedOntologyId.value = ontologyId
  await loadVersions()
}

const loadVersions = async () => {
  if (!selectedOntologyId.value) return
  
  try {
    const response = await axios.get(`http://localhost:8080/api/version/findByOntologyId/${selectedOntologyId.value}`)
    versions.value = response.data
  } catch (error) {
    console.error('Failed to load versions:', error)
  }
}

const createVersion = async () => {
  if (!newVersion.value.ontologyId) {
    alert('Please select an ontology')
    return
  }
  
  try {
    const response = await axios.post('http://localhost:8080/api/version/create', {
      ...newVersion.value,
      createdBy: 'current-user',
      createdByName: 'Current User',
      snapshot: {}
    })
    
    if (selectedOntologyId.value === newVersion.value.ontologyId) {
      versions.value.unshift(response.data)
    }
    
    newVersion.value = { ontologyId: '', description: '' }
    alert('Version created successfully')
  } catch (error) {
    console.error('Failed to create version:', error)
    alert('Failed to create version')
  }
}

const rollbackVersion = async (versionId) => {
  if (!confirm('Are you sure you want to rollback to this version? This will create a new version with the snapshot data.')) return
  
  try {
    const response = await axios.post(`http://localhost:8080/api/version/rollback/${selectedOntologyId.value}/${versionId}`)
    versions.value.unshift(response.data)
    alert('Rollback successful')
  } catch (error) {
    console.error('Failed to rollback:', error)
    alert('Failed to rollback to this version')
  }
}

const deleteVersion = async (versionId) => {
  if (!confirm('Are you sure you want to delete this version?')) return
  
  try {
    await axios.delete(`http://localhost:8080/api/version/delete/${versionId}`)
    versions.value = versions.value.filter(v => v.id !== versionId)
  } catch (error) {
    console.error('Failed to delete version:', error)
    alert('Failed to delete version')
  }
}

const viewSnapshot = (version) => {
  selectedVersion.value = version
  showSnapshotModal.value = true
}

const exportHistory = () => {
  const data = JSON.stringify(versions.value, null, 2)
  const blob = new Blob([data], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `version-history-${selectedOntologyId.value}.json`
  a.click()
  URL.revokeObjectURL(url)
}

const exportSnapshot = () => {
  if (!selectedVersion.value) return
  
  const data = JSON.stringify(selectedVersion.value.snapshot, null, 2)
  const blob = new Blob([data], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `snapshot-v${selectedVersion.value.versionNumber}.json`
  a.click()
  URL.revokeObjectURL(url)
}

const getVersionNumber = (versionId) => {
  const version = versions.value.find(v => v.id === versionId)
  return version ? version.versionNumber : 'N/A'
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleString()
}
</script>

<style scoped>
.version-control {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.modal.show {
  display: block;
  background-color: rgba(0, 0, 0, 0.5);
}

.timeline {
  position: relative;
  padding-left: 30px;
}

.timeline::before {
  content: '';
  position: absolute;
  left: 10px;
  top: 0;
  bottom: 0;
  width: 2px;
  background-color: #dee2e6;
}

.timeline-item {
  position: relative;
  margin-bottom: 20px;
}

.timeline-marker {
  position: absolute;
  left: -25px;
  top: 5px;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  border: 2px solid white;
}

.timeline-content {
  margin-left: 10px;
}

pre {
  background-color: #f8f9fa;
  padding: 10px;
  border-radius: 5px;
  overflow-x: auto;
  max-height: 400px;
}
</style>