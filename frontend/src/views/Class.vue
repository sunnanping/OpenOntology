<template>
  <div class="class">
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
              <router-link to="/class" class="nav-link active">Class</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/property" class="nav-link">Property</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/instance" class="nav-link">Instance</router-link>
            </li>
          </ul>
          <div class="ms-auto">
            <button class="btn btn-primary me-2" @click="showCreateModal = true">Create Class</button>
          </div>
        </div>
      </div>
    </nav>
    <div class="container mt-5">
      <h1>Class Management</h1>
      <div class="mt-4">
        <table class="table table-striped">
          <thead>
            <tr>
              <th>Name</th>
              <th>Description</th>
              <th>Parent Class</th>
              <th>Created Date</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="cls in classes" :key="cls.id">
              <td>{{ cls.name }}</td>
              <td>{{ cls.description }}</td>
              <td>{{ cls.superClasses ? cls.superClasses.join(', ') : '' }}</td>
              <td>{{ formatDate(cls.createdDate) }}</td>
              <td>
                <button class="btn btn-sm btn-primary me-2" @click="editClass(cls)">Edit</button>
                <button class="btn btn-sm btn-danger" @click="deleteClass(cls.id)">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Create Class Modal -->
    <div class="modal" :class="{ 'show': showCreateModal }" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Create Class</h5>
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
                <label for="description" class="form-label">Description</label>
                <textarea class="form-control" id="description" v-model="form.description"></textarea>
              </div>
              <div class="mb-3">
                <label for="superClasses" class="form-label">Super Classes (comma separated)</label>
                <input type="text" class="form-control" id="superClasses" v-model="form.superClasses">
              </div>
              <button type="submit" class="btn btn-primary">Create</button>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- Edit Class Modal -->
    <div class="modal" :class="{ 'show': showEditModal }" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Edit Class</h5>
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
                <label for="editDescription" class="form-label">Description</label>
                <textarea class="form-control" id="editDescription" v-model="editForm.description"></textarea>
              </div>
              <div class="mb-3">
                <label for="editSuperClasses" class="form-label">Super Classes (comma separated)</label>
                <input type="text" class="form-control" id="editSuperClasses" v-model="editForm.superClasses">
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
import { ref, onMounted } from 'vue'
import axios from 'axios'

const classes = ref([])
const showCreateModal = ref(false)
const showEditModal = ref(false)
const form = ref({
  name: '',
  iri: '',
  ontologyId: '',
  description: '',
  superClasses: ''
})
const editForm = ref({
  id: '',
  name: '',
  iri: '',
  ontologyId: '',
  description: '',
  superClasses: ''
})

onMounted(async () => {
  await loadClasses()
})

const loadClasses = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/class/findAll')
    classes.value = response.data
  } catch (error) {
    console.error('Failed to load classes:', error)
  }
}

const handleCreate = async () => {
  try {
    const classData = {
      name: form.value.name,
      iri: form.value.iri,
      ontologyId: form.value.ontologyId,
      description: form.value.description,
      superClasses: form.value.superClasses ? form.value.superClasses.split(',').map(s => s.trim()) : []
    }
    const response = await axios.post('http://localhost:8080/api/class/create', classData)
    classes.value.push(response.data)
    showCreateModal.value = false
    form.value = {
      name: '',
      iri: '',
      ontologyId: '',
      description: '',
      superClasses: ''
    }
  } catch (error) {
    console.error('Failed to create class:', error)
  }
}

const editClass = (cls) => {
  editForm.value = {
    id: cls.id,
    name: cls.name,
    iri: cls.iri,
    ontologyId: cls.ontologyId,
    description: cls.description,
    superClasses: cls.superClasses ? cls.superClasses.join(', ') : ''
  }
  showEditModal.value = true
}

const handleUpdate = async () => {
  try {
    const classData = {
      name: editForm.value.name,
      iri: editForm.value.iri,
      ontologyId: editForm.value.ontologyId,
      description: editForm.value.description,
      superClasses: editForm.value.superClasses ? editForm.value.superClasses.split(',').map(s => s.trim()) : []
    }
    const response = await axios.put(`http://localhost:8080/api/class/update/${editForm.value.id}`, classData)
    const index = classes.value.findIndex(cls => cls.id === editForm.value.id)
    if (index !== -1) {
      classes.value[index] = response.data
    }
    showEditModal.value = false
  } catch (error) {
    console.error('Failed to update class:', error)
  }
}

const deleteClass = async (id) => {
  if (confirm('Are you sure you want to delete this class?')) {
    try {
      await axios.delete(`http://localhost:8080/api/class/delete/${id}`)
      classes.value = classes.value.filter(cls => cls.id !== id)
    } catch (error) {
      console.error('Failed to delete class:', error)
    }
  }
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString()
}
</script>

<style scoped>
.class {
  min-height: 100vh;
}

.modal.show {
  display: block;
  background-color: rgba(0, 0, 0, 0.5);
}
</style>