<template>
  <div class="property">
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
              <router-link to="/property" class="nav-link active">Property</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/instance" class="nav-link">Instance</router-link>
            </li>
          </ul>
          <div class="ms-auto">
            <button class="btn btn-primary me-2" @click="showCreateModal = true">Create Property</button>
          </div>
        </div>
      </div>
    </nav>
    <div class="container mt-5">
      <h1>Property Management</h1>
      <div class="mt-4">
        <table class="table table-striped">
          <thead>
            <tr>
              <th>Name</th>
              <th>Description</th>
              <th>Domain</th>
              <th>Range</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="property in properties" :key="property.id">
              <td>{{ property.name }}</td>
              <td>{{ property.description }}</td>
              <td>{{ property.domains ? property.domains.join(', ') : '' }}</td>
              <td>{{ property.ranges ? property.ranges.join(', ') : '' }}</td>
              <td>
                <button class="btn btn-sm btn-primary me-2" @click="editProperty(property)">Edit</button>
                <button class="btn btn-sm btn-danger" @click="deleteProperty(property.id)">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Create Property Modal -->
    <div class="modal" :class="{ 'show': showCreateModal }" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Create Property</h5>
            <button type="button" class="btn-close" @click="showCreateModal = false"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="handleCreate">
              <div class="mb-3">
                <label for="name" class="form-label">Name</label>
                <input type="text" class="form-control" id="name" v-model="form.name" required>
              </div>
              <div class="mb-3">
                <label for="iri" class="form-label">IRI</label>
                <input type="text" class="form-control" id="iri" v-model="form.iri" required>
              </div>
              <div class="mb-3">
                <label for="ontologyId" class="form-label">Ontology ID</label>
                <input type="text" class="form-control" id="ontologyId" v-model="form.ontologyId" required>
              </div>
              <div class="mb-3">
                <label for="propertyType" class="form-label">Property Type</label>
                <select class="form-control" id="propertyType" v-model="form.propertyType" required>
                  <option value="object">Object Property</option>
                  <option value="datatype">Datatype Property</option>
                  <option value="annotation">Annotation Property</option>
                </select>
              </div>
              <div class="mb-3">
                <label for="description" class="form-label">Description</label>
                <textarea class="form-control" id="description" v-model="form.description"></textarea>
              </div>
              <div class="mb-3">
                <label for="domains" class="form-label">Domains (comma separated)</label>
                <input type="text" class="form-control" id="domains" v-model="form.domains">
              </div>
              <div class="mb-3">
                <label for="ranges" class="form-label">Ranges (comma separated)</label>
                <input type="text" class="form-control" id="ranges" v-model="form.ranges">
              </div>
              <button type="submit" class="btn btn-primary">Create</button>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- Edit Property Modal -->
    <div class="modal" :class="{ 'show': showEditModal }" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Edit Property</h5>
            <button type="button" class="btn-close" @click="showEditModal = false"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="handleUpdate">
              <div class="mb-3">
                <label for="editName" class="form-label">Name</label>
                <input type="text" class="form-control" id="editName" v-model="editForm.name" required>
              </div>
              <div class="mb-3">
                <label for="editIri" class="form-label">IRI</label>
                <input type="text" class="form-control" id="editIri" v-model="editForm.iri" required>
              </div>
              <div class="mb-3">
                <label for="editOntologyId" class="form-label">Ontology ID</label>
                <input type="text" class="form-control" id="editOntologyId" v-model="editForm.ontologyId" required>
              </div>
              <div class="mb-3">
                <label for="editPropertyType" class="form-label">Property Type</label>
                <select class="form-control" id="editPropertyType" v-model="editForm.propertyType" required>
                  <option value="object">Object Property</option>
                  <option value="datatype">Datatype Property</option>
                  <option value="annotation">Annotation Property</option>
                </select>
              </div>
              <div class="mb-3">
                <label for="editDescription" class="form-label">Description</label>
                <textarea class="form-control" id="editDescription" v-model="editForm.description"></textarea>
              </div>
              <div class="mb-3">
                <label for="editDomains" class="form-label">Domains (comma separated)</label>
                <input type="text" class="form-control" id="editDomains" v-model="editForm.domains">
              </div>
              <div class="mb-3">
                <label for="editRanges" class="form-label">Ranges (comma separated)</label>
                <input type="text" class="form-control" id="editRanges" v-model="editForm.ranges">
              </div>
              <button type="submit" class="btn btn-primary">Update</button>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'

const properties = ref([])
const http = inject('$http') || window.$http
const showCreateModal = ref(false)
const showEditModal = ref(false)
const form = ref({
  name: '',
  iri: '',
  ontologyId: '',
  propertyType: 'object',
  description: '',
  domains: '',
  ranges: ''
})
const editForm = ref({
  id: '',
  name: '',
  iri: '',
  ontologyId: '',
  propertyType: 'object',
  description: '',
  domains: '',
  ranges: ''
})

onMounted(async () => {
  await loadProperties()
})

const loadProperties = async () => {
  try {
    const response = await http.get('/property/findAll')
    properties.value = response.data
  } catch (error) {
    console.error('Failed to load properties:', error)
  }
}

const handleCreate = async () => {
  try {
    const propertyData = {
      name: form.value.name,
      iri: form.value.iri,
      ontologyId: form.value.ontologyId,
      propertyType: form.value.propertyType,
      description: form.value.description,
      domains: form.value.domains ? form.value.domains.split(',').map(s => s.trim()) : [],
      ranges: form.value.ranges ? form.value.ranges.split(',').map(s => s.trim()) : []
    }
    const response = await http.post('/property/create', propertyData)
    properties.value.push(response.data)
    showCreateModal.value = false
    form.value = {
      name: '',
      iri: '',
      ontologyId: '',
      propertyType: 'object',
      description: '',
      domains: '',
      ranges: ''
    }
  } catch (error) {
    console.error('Failed to create property:', error)
  }
}

const editProperty = (property) => {
  editForm.value = {
    id: property.id,
    name: property.name,
    iri: property.iri,
    ontologyId: property.ontologyId,
    propertyType: property.propertyType,
    description: property.description,
    domains: property.domains ? property.domains.join(', ') : '',
    ranges: property.ranges ? property.ranges.join(', ') : ''
  }
  showEditModal.value = true
}

const handleUpdate = async () => {
  try {
    const propertyData = {
      name: editForm.value.name,
      iri: editForm.value.iri,
      ontologyId: editForm.value.ontologyId,
      propertyType: editForm.value.propertyType,
      description: editForm.value.description,
      domains: editForm.value.domains ? editForm.value.domains.split(',').map(s => s.trim()) : [],
      ranges: editForm.value.ranges ? editForm.value.ranges.split(',').map(s => s.trim()) : []
    }
    const response = await http.put(`/property/update/${editForm.value.id}`, propertyData)
    const index = properties.value.findIndex(property => property.id === editForm.value.id)
    if (index !== -1) {
      properties.value[index] = response.data
    }
    showEditModal.value = false
  } catch (error) {
    console.error('Failed to update property:', error)
  }
}

const deleteProperty = async (id) => {
  if (confirm('Are you sure you want to delete this property?')) {
    try {
      await http.delete(`/property/delete/${id}`)
      properties.value = properties.value.filter(property => property.id !== id)
    } catch (error) {
      console.error('Failed to delete property:', error)
    }
  }
}
</script>

<style scoped>
.property {
  min-height: 100vh;
}

.modal.show {
  display: block;
  background-color: rgba(0, 0, 0, 0.5);
}
</style>