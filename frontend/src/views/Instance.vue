<template>
  <div class="instance">
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
              <router-link to="/instance" class="nav-link active">Instance</router-link>
            </li>
          </ul>
          <div class="ms-auto">
            <button class="btn btn-primary me-2" @click="showCreateModal = true">Create Instance</button>
          </div>
        </div>
      </div>
    </nav>
    <div class="container mt-5">
      <h1>Instance Management</h1>
      <div class="mt-4">
        <table class="table table-striped">
          <thead>
            <tr>
              <th>Name</th>
              <th>Class</th>
              <th>Properties</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="instance in instances" :key="instance.id">
              <td>{{ instance.name }}</td>
              <td>{{ instance.classId }}</td>
              <td>{{ formatProperties(instance.propertyValues) }}</td>
              <td>
                <button class="btn btn-sm btn-primary me-2" @click="editInstance(instance)">Edit</button>
                <button class="btn btn-sm btn-danger" @click="deleteInstance(instance.id)">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Create Instance Modal -->
    <div class="modal" :class="{ 'show': showCreateModal }" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Create Instance</h5>
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
                <label for="classId" class="form-label">Class ID</label>
                <input type="text" class="form-control" id="classId" v-model="form.classId" required>
              </div>
              <div class="mb-3">
                <label for="properties" class="form-label">Properties (JSON)</label>
                <textarea class="form-control" id="properties" v-model="form.properties"></textarea>
                <small class="form-text text-muted">Enter properties in JSON format, e.g., {"hasName": "John Doe", "hasAge": 30}</small>
              </div>
              <button type="submit" class="btn btn-primary">Create</button>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- Edit Instance Modal -->
    <div class="modal" :class="{ 'show': showEditModal }" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Edit Instance</h5>
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
                <label for="editClassId" class="form-label">Class ID</label>
                <input type="text" class="form-control" id="editClassId" v-model="editForm.classId" required>
              </div>
              <div class="mb-3">
                <label for="editProperties" class="form-label">Properties (JSON)</label>
                <textarea class="form-control" id="editProperties" v-model="editForm.properties"></textarea>
                <small class="form-text text-muted">Enter properties in JSON format, e.g., {"hasName": "John Doe", "hasAge": 30}</small>
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

const instances = ref([])
const http = inject('$http') || window.$http
const showCreateModal = ref(false)
const showEditModal = ref(false)
const form = ref({
  name: '',
  iri: '',
  ontologyId: '',
  classId: '',
  properties: ''
})
const editForm = ref({
  id: '',
  name: '',
  iri: '',
  ontologyId: '',
  classId: '',
  properties: ''
})

onMounted(async () => {
  await loadInstances()
})

const loadInstances = async () => {
  try {
    const response = await http.get('/instance/findAll')
    instances.value = response.data
  } catch (error) {
    console.error('Failed to load instances:', error)
  }
}

const handleCreate = async () => {
  try {
    const instanceData = {
      name: form.value.name,
      iri: form.value.iri,
      ontologyId: form.value.ontologyId,
      classId: form.value.classId,
      propertyValues: JSON.parse(form.value.properties || '{}')
    }
    const response = await http.post('/instance/create', instanceData)
    instances.value.push(response.data)
    showCreateModal.value = false
    form.value = {
      name: '',
      iri: '',
      ontologyId: '',
      classId: '',
      properties: ''
    }
  } catch (error) {
    console.error('Failed to create instance:', error)
    alert('Failed to create instance. Please check your input and try again.')
  }
}

const editInstance = (instance) => {
  editForm.value = {
    id: instance.id,
    name: instance.name,
    iri: instance.iri,
    ontologyId: instance.ontologyId,
    classId: instance.classId,
    properties: JSON.stringify(instance.propertyValues || {}, null, 2)
  }
  showEditModal.value = true
}

const handleUpdate = async () => {
  try {
    const instanceData = {
      name: editForm.value.name,
      iri: editForm.value.iri,
      ontologyId: editForm.value.ontologyId,
      classId: editForm.value.classId,
      propertyValues: JSON.parse(editForm.value.properties || '{}')
    }
    const response = await http.put(`/instance/update/${editForm.value.id}`, instanceData)
    const index = instances.value.findIndex(instance => instance.id === editForm.value.id)
    if (index !== -1) {
      instances.value[index] = response.data
    }
    showEditModal.value = false
  } catch (error) {
    console.error('Failed to update instance:', error)
    alert('Failed to update instance. Please check your input and try again.')
  }
}

const deleteInstance = async (id) => {
  if (confirm('Are you sure you want to delete this instance?')) {
    try {
      await http.delete(`/instance/delete/${id}`)
      instances.value = instances.value.filter(instance => instance.id !== id)
    } catch (error) {
      console.error('Failed to delete instance:', error)
      alert('Failed to delete instance. Please try again.')
    }
  }
}

const formatProperties = (properties) => {
  if (!properties) return ''
  return JSON.stringify(properties, null, 2)
}
</script>

<style scoped>
.instance {
  min-height: 100vh;
}

.modal.show {
  display: block;
  background-color: rgba(0, 0, 0, 0.5);
}
</style>