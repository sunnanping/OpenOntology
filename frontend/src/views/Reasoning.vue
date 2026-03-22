<template>
  <div class="reasoning">
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
              <router-link to="/reasoning" class="nav-link active">Reasoning</router-link>
            </li>
          </ul>
        </div>
      </div>
    </nav>
    <div class="container mt-4">
      <h1>Reasoning Tasks</h1>
      <div class="row mt-4">
        <div class="col-md-4">
          <div class="card">
            <div class="card-header">
              <h5>Create New Task</h5>
            </div>
            <div class="card-body">
              <form @submit.prevent="createTask">
                <div class="mb-3">
                  <label class="form-label">Task Name</label>
                  <input type="text" class="form-control" v-model="newTask.name" required>
                </div>
                <div class="mb-3">
                  <label class="form-label">Ontology</label>
                  <select class="form-control" v-model="newTask.ontologyId" required>
                    <option value="">-- Select Ontology --</option>
                    <option v-for="ontology in ontologies" :key="ontology.id" :value="ontology.id">
                      {{ ontology.name }}
                    </option>
                  </select>
                </div>
                <div class="mb-3">
                  <label class="form-label">Reasoner Type</label>
                  <select class="form-control" v-model="newTask.reasonerType" required>
                    <option value="pellet">Pellet</option>
                    <option value="hermit">HermiT</option>
                    <option value="jfact">JFact</option>
                    <option value="elk">ELK</option>
                  </select>
                </div>
                <div class="mb-3">
                  <label class="form-label">Description</label>
                  <textarea class="form-control" v-model="newTask.description" rows="3"></textarea>
                </div>
                <div class="mb-3">
                  <label class="form-label">Rules (one per line)</label>
                  <textarea class="form-control" v-model="rulesText" rows="5" 
                    placeholder="Enter SWRL rules, one per line"></textarea>
                </div>
                <button type="submit" class="btn btn-primary w-100">Create Task</button>
              </form>
            </div>
          </div>
        </div>
        <div class="col-md-8">
          <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
              <h5>Reasoning Tasks</h5>
              <button class="btn btn-sm btn-outline-primary" @click="loadTasks">Refresh</button>
            </div>
            <div class="card-body">
              <div v-if="tasks.length === 0" class="text-center text-muted py-4">
                No reasoning tasks found. Create a new task to get started.
              </div>
              <div v-else>
                <div v-for="task in tasks" :key="task.id" class="card mb-3">
                  <div class="card-body">
                    <div class="d-flex justify-content-between align-items-start">
                      <div>
                        <h5 class="card-title">{{ task.name }}</h5>
                        <p class="card-text text-muted">{{ task.description }}</p>
                        <div class="mb-2">
                          <span class="badge bg-info me-2">{{ task.reasonerType }}</span>
                          <span :class="getStatusBadgeClass(task.status)">{{ task.status }}</span>
                        </div>
                        <small class="text-muted">Created: {{ formatDate(task.createdAt) }}</small>
                      </div>
                      <div>
                        <button v-if="task.status === 'PENDING'" 
                                class="btn btn-success btn-sm me-2"
                                @click="executeTask(task.id)">
                          Execute
                        </button>
                        <button v-if="task.status === 'COMPLETED'" 
                                class="btn btn-info btn-sm me-2"
                                @click="viewResults(task)">
                          View Results
                        </button>
                        <button class="btn btn-danger btn-sm"
                                @click="deleteTask(task.id)">
                          Delete
                        </button>
                      </div>
                    </div>
                    <div v-if="task.status === 'RUNNING'" class="progress mt-3">
                      <div class="progress-bar progress-bar-striped progress-bar-animated" 
                           style="width: 100%">Running...</div>
                    </div>
                    <div v-if="task.status === 'COMPLETED' && task.results" class="mt-3">
                      <h6>Results:</h6>
                      <ul class="list-group">
                        <li class="list-group-item" v-if="task.results.inferredClasses !== undefined">
                          Inferred Classes: {{ task.results.inferredClasses }}
                        </li>
                        <li class="list-group-item" v-if="task.results.inferredProperties !== undefined">
                          Inferred Properties: {{ task.results.inferredProperties }}
                        </li>
                        <li class="list-group-item" v-if="task.results.inferredInstances !== undefined">
                          Inferred Instances: {{ task.results.inferredInstances }}
                        </li>
                        <li class="list-group-item" v-if="task.results.inconsistencies !== undefined">
                          Inconsistencies: {{ task.results.inconsistencies }}
                        </li>
                      </ul>
                    </div>
                    <div v-if="task.status === 'FAILED'" class="alert alert-danger mt-3">
                      {{ task.errorMessage }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="modal" :class="{ 'show': showResultsModal }" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Reasoning Results</h5>
            <button type="button" class="btn-close" @click="showResultsModal = false"></button>
          </div>
          <div class="modal-body" v-if="selectedTask">
            <h6>Task: {{ selectedTask.name }}</h6>
            <hr/>
            <div v-if="selectedTask.results">
              <h6>Summary</h6>
              <table class="table table-striped">
                <tbody>
                  <tr>
                    <td>Inferred Classes</td>
                    <td>{{ selectedTask.results.inferredClasses || 0 }}</td>
                  </tr>
                  <tr>
                    <td>Inferred Properties</td>
                    <td>{{ selectedTask.results.inferredProperties || 0 }}</td>
                  </tr>
                  <tr>
                    <td>Inferred Instances</td>
                    <td>{{ selectedTask.results.inferredInstances || 0 }}</td>
                  </tr>
                  <tr>
                    <td>Inconsistencies Found</td>
                    <td>{{ selectedTask.results.inconsistencies || 0 }}</td>
                  </tr>
                  <tr>
                    <td>Reasoner Type</td>
                    <td>{{ selectedTask.results.reasonerType }}</td>
                  </tr>
                  <tr>
                    <td>Execution Time</td>
                    <td>{{ selectedTask.results.executionTime }} ms</td>
                  </tr>
                </tbody>
              </table>
              <h6 class="mt-3">Raw Results</h6>
              <pre>{{ JSON.stringify(selectedTask.results, null, 2) }}</pre>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showResultsModal = false">Close</button>
            <button type="button" class="btn btn-primary" @click="exportResults">Export Results</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import http from '@/utils/http'

const tasks = ref([])
const ontologies = ref([])
const newTask = ref({
  name: '',
  ontologyId: '',
  reasonerType: 'pellet',
  description: ''
})
const rulesText = ref('')
const showResultsModal = ref(false)
const selectedTask = ref(null)

onMounted(async () => {
  await Promise.all([
    loadTasks(),
    loadOntologies()
  ])
})

const loadTasks = async () => {
  try {
    const response = await http.get('/reasoning/findAll')
    tasks.value = response.data
  } catch (error) {
    console.error('Failed to load tasks:', error)
  }
}

const loadOntologies = async () => {
  try {
    const response = await http.get('/ontology/findAll')
    ontologies.value = response.data
  } catch (error) {
    console.error('Failed to load ontologies:', error)
  }
}

const createTask = async () => {
  try {
    const taskData = {
      ...newTask.value,
      rules: rulesText.value.split('\n').filter(r => r.trim())
    }
    const response = await http.post('/reasoning/create', taskData)
    tasks.value.push(response.data)
    newTask.value = {
      name: '',
      ontologyId: '',
      reasonerType: 'pellet',
      description: ''
    }
    rulesText.value = ''
  } catch (error) {
    console.error('Failed to create task:', error)
    alert('Failed to create reasoning task')
  }
}

const executeTask = async (taskId) => {
  try {
    const response = await http.post(`/reasoning/execute/${taskId}`)
    const index = tasks.value.findIndex(t => t.id === taskId)
    if (index !== -1) {
      tasks.value[index] = response.data
    }
  } catch (error) {
    console.error('Failed to execute task:', error)
    alert('Failed to execute reasoning task')
  }
}

const deleteTask = async (taskId) => {
  if (!confirm('Are you sure you want to delete this task?')) return
  
  try {
    await http.delete(`/reasoning/delete/${taskId}`)
    tasks.value = tasks.value.filter(t => t.id !== taskId)
  } catch (error) {
    console.error('Failed to delete task:', error)
    alert('Failed to delete reasoning task')
  }
}

const viewResults = (task) => {
  selectedTask.value = task
  showResultsModal.value = true
}

const exportResults = () => {
  if (!selectedTask.value || !selectedTask.value.results) return
  
  const data = JSON.stringify(selectedTask.value.results, null, 2)
  const blob = new Blob([data], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `reasoning-results-${selectedTask.value.id}.json`
  a.click()
  URL.revokeObjectURL(url)
}

const getStatusBadgeClass = (status) => {
  switch (status) {
    case 'PENDING': return 'badge bg-secondary'
    case 'RUNNING': return 'badge bg-primary'
    case 'COMPLETED': return 'badge bg-success'
    case 'FAILED': return 'badge bg-danger'
    default: return 'badge bg-secondary'
  }
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleString()
}
</script>

<style scoped>
.reasoning {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.modal.show {
  display: block;
  background-color: rgba(0, 0, 0, 0.5);
}

pre {
  background-color: #f8f9fa;
  padding: 10px;
  border-radius: 5px;
  overflow-x: auto;
}
</style>