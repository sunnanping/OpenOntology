<template>
  <div class="ontology">
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
              <router-link to="/ontology" class="nav-link active">Ontology</router-link>
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
            <button class="btn btn-primary me-2" @click="showCreateModal = true">Create Ontology</button>
          </div>
        </div>
      </div>
    </nav>
    <div class="container mt-5">
      <h1>Ontology Management</h1>
      <div class="mt-4">
        <table class="table table-striped">
          <thead>
            <tr>
              <th>Name</th>
              <th>Description</th>
              <th>Namespace</th>
              <th>Created Date</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="ontology in ontologies" :key="ontology.id">
              <td>{{ ontology.name }}</td>
              <td>{{ ontology.description }}</td>
              <td>{{ ontology.namespace }}</td>
              <td>{{ formatDate(ontology.createdDate) }}</td>
              <td>
                <button class="btn btn-sm btn-primary me-2" @click="editOntology(ontology)">Edit</button>
                <button class="btn btn-sm btn-danger" @click="deleteOntology(ontology.id)">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Create Ontology Modal -->
    <div class="modal" :class="{ 'show': showCreateModal }" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Create Ontology</h5>
            <button type="button" class="btn-close" @click="showCreateModal = false"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="handleCreate">
              <div class="mb-3">
                <label for="name" class="form-label">Name</label>
                <input type="text" class="form-control" id="name" v-model="form.name" required>
              </div>
              <div class="mb-3">
                <label for="description" class="form-label">Description</label>
                <textarea class="form-control" id="description" v-model="form.description"></textarea>
              </div>
              <div class="mb-3">
                <label for="namespace" class="form-label">Namespace</label>
                <input type="text" class="form-control" id="namespace" v-model="form.namespace" required>
              </div>
              <div class="mb-3">
                <label for="format" class="form-label">Format</label>
                <select class="form-control" id="format" v-model="form.format" required>
                  <option value="OWL">OWL</option>
                  <option value="RDF">RDF</option>
                  <option value="Turtle">Turtle</option>
                </select>
              </div>
              <button type="submit" class="btn btn-primary">Create</button>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- Edit Ontology Modal -->
    <div class="modal" :class="{ 'show': showEditModal }" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Edit Ontology</h5>
            <button type="button" class="btn-close" @click="showEditModal = false"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="handleUpdate">
              <div class="mb-3">
                <label for="editName" class="form-label">Name</label>
                <input type="text" class="form-control" id="editName" v-model="editForm.name" required>
              </div>
              <div class="mb-3">
                <label for="editDescription" class="form-label">Description</label>
                <textarea class="form-control" id="editDescription" v-model="editForm.description"></textarea>
              </div>
              <div class="mb-3">
                <label for="editNamespace" class="form-label">Namespace</label>
                <input type="text" class="form-control" id="editNamespace" v-model="editForm.namespace" required>
              </div>
              <div class="mb-3">
                <label for="editFormat" class="form-label">Format</label>
                <select class="form-control" id="editFormat" v-model="editForm.format" required>
                  <option value="OWL">OWL</option>
                  <option value="RDF">RDF</option>
                  <option value="Turtle">Turtle</option>
                </select>
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

const ontologies = ref([])
const showCreateModal = ref(false)
const showEditModal = ref(false)
const form = ref({
  name: '',
  description: '',
  namespace: '',
  format: 'OWL'
})
const editForm = ref({
  id: '',
  name: '',
  description: '',
  namespace: '',
  format: 'OWL'
})

// 加载本体列表
onMounted(async () => {
  await loadOntologies()
})

const loadOntologies = async () => {
  try {
    const response = await axios.get('/api/ontology/findAll')
    ontologies.value = response.data
  } catch (error) {
    console.error('Failed to load ontologies:', error)
  }
}

// 创建本体
const handleCreate = async () => {
  try {
    await axios.post('/api/ontology/create', form.value)
    showCreateModal.value = false
    // 重置表单
    form.value = {
      name: '',
      description: '',
      namespace: '',
      format: 'OWL'
    }
    // 重新加载本体列表
    await loadOntologies()
  } catch (error) {
    console.error('Failed to create ontology:', error)
  }
}

// 编辑本体
const editOntology = (ontology) => {
  editForm.value = {
    id: ontology.id,
    name: ontology.name,
    description: ontology.description,
    namespace: ontology.namespace,
    format: ontology.format
  }
  showEditModal.value = true
}

// 更新本体
const handleUpdate = async () => {
  try {
    await axios.put('/api/ontology/update', editForm.value)
    showEditModal.value = false
    // 重新加载本体列表
    await loadOntologies()
  } catch (error) {
    console.error('Failed to update ontology:', error)
  }
}

// 删除本体
const deleteOntology = async (id) => {
  if (confirm('Are you sure you want to delete this ontology?')) {
    try {
      await axios.delete(`/api/ontology/delete/${id}`)
      // 重新加载本体列表
      await loadOntologies()
    } catch (error) {
      console.error('Failed to delete ontology:', error)
    }
  }
}

// 格式化日期
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString()
}
</script>

<style scoped>
.ontology {
  min-height: 100vh;
}

.modal.show {
  display: block;
  background-color: rgba(0, 0, 0, 0.5);
}
</style>