<template>
  <div class="collaboration">
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
              <router-link to="/collaboration" class="nav-link active">Collaboration</router-link>
            </li>
          </ul>
        </div>
      </div>
    </nav>
    <div class="container mt-4">
      <h1>Collaboration</h1>
      <div class="row mt-4">
        <div class="col-md-4">
          <div class="card">
            <div class="card-header">
              <h5>Create Session</h5>
            </div>
            <div class="card-body">
              <form @submit.prevent="createSession">
                <div class="mb-3">
                  <label class="form-label">Session Name</label>
                  <input type="text" class="form-control" v-model="newSession.name" required>
                </div>
                <div class="mb-3">
                  <label class="form-label">Ontology</label>
                  <select class="form-control" v-model="newSession.ontologyId" required>
                    <option value="">-- Select Ontology --</option>
                    <option v-for="ontology in ontologies" :key="ontology.id" :value="ontology.id">
                      {{ ontology.name }}
                    </option>
                  </select>
                </div>
                <button type="submit" class="btn btn-primary w-100">Create Session</button>
              </form>
            </div>
          </div>
          <div class="card mt-3">
            <div class="card-header">
              <h5>Active Sessions</h5>
            </div>
            <div class="card-body">
              <div v-if="sessions.length === 0" class="text-muted text-center py-3">
                No active sessions
              </div>
              <div v-else>
                <div v-for="session in sessions" :key="session.id" 
                     class="border rounded p-2 mb-2"
                     :class="{ 'bg-light': selectedSession && selectedSession.id === session.id }"
                     @click="selectSession(session)"
                     style="cursor: pointer;">
                  <div class="d-flex justify-content-between align-items-center">
                    <div>
                      <strong>{{ session.name }}</strong>
                      <br/>
                      <small class="text-muted">{{ session.participants?.length || 0 }} participants</small>
                    </div>
                    <span :class="getSessionStatusBadge(session.status)">{{ session.status }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-8">
          <div v-if="selectedSession">
            <div class="card">
              <div class="card-header d-flex justify-content-between align-items-center">
                <h5>{{ selectedSession.name }}</h5>
                <div>
                  <button v-if="selectedSession.status === 'ACTIVE'" 
                          class="btn btn-danger btn-sm"
                          @click="endSession(selectedSession.id)">
                    End Session
                  </button>
                  <button class="btn btn-outline-primary btn-sm ms-2"
                          @click="refreshSession">
                    Refresh
                  </button>
                </div>
              </div>
              <div class="card-body">
                <div class="row">
                  <div class="col-md-6">
                    <h6>Participants</h6>
                    <div class="list-group">
                      <div v-for="participant in selectedSession.participants" :key="participant"
                           class="list-group-item d-flex justify-content-between align-items-center">
                        <span>{{ participant }}</span>
                        <button class="btn btn-outline-danger btn-sm"
                                @click="removeParticipant(selectedSession.id, participant)">
                          Remove
                        </button>
                      </div>
                      <div v-if="!selectedSession.participants || selectedSession.participants.length === 0"
                           class="list-group-item text-muted">
                        No participants
                      </div>
                    </div>
                    <div class="mt-2">
                      <div class="input-group">
                        <input type="text" class="form-control" 
                               v-model="newParticipantId" 
                               placeholder="User ID">
                        <button class="btn btn-primary" 
                                @click="addParticipant(selectedSession.id, newParticipantId)">
                          Add
                        </button>
                      </div>
                    </div>
                  </div>
                  <div class="col-md-6">
                    <h6>Session Info</h6>
                    <table class="table table-sm">
                      <tbody>
                        <tr>
                          <td>Status</td>
                          <td>{{ selectedSession.status }}</td>
                        </tr>
                        <tr>
                          <td>Created By</td>
                          <td>{{ selectedSession.createdBy }}</td>
                        </tr>
                        <tr>
                          <td>Created At</td>
                          <td>{{ formatDate(selectedSession.createdAt) }}</td>
                        </tr>
                        <tr>
                          <td>Last Updated</td>
                          <td>{{ formatDate(selectedSession.updatedAt) }}</td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>
            </div>
            <div class="card mt-3">
              <div class="card-header">
                <h6>Comments</h6>
              </div>
              <div class="card-body">
                <div class="mb-3">
                  <form @submit.prevent="addComment">
                    <div class="mb-2">
                      <label class="form-label">Entity ID (optional)</label>
                      <input type="text" class="form-control" v-model="newComment.entityId">
                    </div>
                    <div class="mb-2">
                      <label class="form-label">Entity Type</label>
                      <select class="form-control" v-model="newComment.entityType">
                        <option value="class">Class</option>
                        <option value="property">Property</option>
                        <option value="instance">Instance</option>
                        <option value="ontology">Ontology</option>
                      </select>
                    </div>
                    <div class="mb-2">
                      <label class="form-label">Comment</label>
                      <textarea class="form-control" v-model="newComment.content" rows="3" required></textarea>
                    </div>
                    <button type="submit" class="btn btn-primary">Add Comment</button>
                  </form>
                </div>
                <hr/>
                <div v-if="comments.length === 0" class="text-muted text-center py-3">
                  No comments yet
                </div>
                <div v-else>
                  <div v-for="comment in comments" :key="comment.id" class="card mb-2">
                    <div class="card-body">
                      <div class="d-flex justify-content-between">
                        <div>
                          <strong>{{ comment.userName || comment.userId }}</strong>
                          <small class="text-muted ms-2">{{ formatDate(comment.createdAt) }}</small>
                        </div>
                        <button class="btn btn-outline-danger btn-sm"
                                @click="deleteComment(comment.id)">
                          Delete
                        </button>
                      </div>
                      <div v-if="comment.entityId" class="mt-1">
                        <small class="text-muted">On {{ comment.entityType }}: {{ comment.entityId }}</small>
                      </div>
                      <p class="mt-2 mb-0">{{ comment.content }}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="card">
            <div class="card-body text-center text-muted py-5">
              <h5>Select or create a collaboration session</h5>
              <p>Choose a session from the list or create a new one to start collaborating.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'

const sessions = ref([])
const ontologies = ref([])
const selectedSession = ref(null)
const comments = ref([])
const newSession = ref({
  name: '',
  ontologyId: ''
})
const newParticipantId = ref('')
const newComment = ref({
  entityId: '',
  entityType: 'ontology',
  content: ''
})

onMounted(async () => {
  await Promise.all([
    loadSessions(),
    loadOntologies()
  ])
})

const loadSessions = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/collaboration/session/findAll')
    sessions.value = response.data
  } catch (error) {
    console.error('Failed to load sessions:', error)
  }
}

const loadOntologies = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/ontology/findAll')
    ontologies.value = response.data
  } catch (error) {
    console.error('Failed to load ontologies:', error)
  }
}

const createSession = async () => {
  try {
    const response = await axios.post('http://localhost:8080/api/collaboration/session/create', {
      ...newSession.value,
      createdBy: 'current-user',
      participants: []
    })
    sessions.value.push(response.data)
    newSession.value = { name: '', ontologyId: '' }
  } catch (error) {
    console.error('Failed to create session:', error)
    alert('Failed to create session')
  }
}

const selectSession = async (session) => {
  selectedSession.value = session
  await loadComments(session.id)
}

const refreshSession = async () => {
  if (!selectedSession.value) return
  
  try {
    const response = await axios.get(`http://localhost:8080/api/collaboration/session/findById/${selectedSession.value.id}`)
    selectedSession.value = response.data
    await loadComments(selectedSession.value.id)
  } catch (error) {
    console.error('Failed to refresh session:', error)
  }
}

const endSession = async (sessionId) => {
  if (!confirm('Are you sure you want to end this session?')) return
  
  try {
    const response = await axios.post(`http://localhost:8080/api/collaboration/session/end/${sessionId}`)
    const index = sessions.value.findIndex(s => s.id === sessionId)
    if (index !== -1) {
      sessions.value[index] = response.data
    }
    selectedSession.value = response.data
  } catch (error) {
    console.error('Failed to end session:', error)
    alert('Failed to end session')
  }
}

const addParticipant = async (sessionId, userId) => {
  if (!userId) return
  
  try {
    const response = await axios.post(`http://localhost:8080/api/collaboration/session/${sessionId}/addParticipant/${userId}`)
    selectedSession.value = response.data
    newParticipantId.value = ''
  } catch (error) {
    console.error('Failed to add participant:', error)
    alert('Failed to add participant')
  }
}

const removeParticipant = async (sessionId, userId) => {
  try {
    const response = await axios.delete(`http://localhost:8080/api/collaboration/session/${sessionId}/removeParticipant/${userId}`)
    selectedSession.value = response.data
  } catch (error) {
    console.error('Failed to remove participant:', error)
    alert('Failed to remove participant')
  }
}

const loadComments = async (sessionId) => {
  try {
    const response = await axios.get(`http://localhost:8080/api/collaboration/comment/findBySessionId/${sessionId}`)
    comments.value = response.data
  } catch (error) {
    console.error('Failed to load comments:', error)
  }
}

const addComment = async () => {
  if (!selectedSession.value || !newComment.value.content) return
  
  try {
    const response = await axios.post('http://localhost:8080/api/collaboration/comment/create', {
      ...newComment.value,
      sessionId: selectedSession.value.id,
      ontologyId: selectedSession.value.ontologyId,
      userId: 'current-user',
      userName: 'Current User'
    })
    comments.value.push(response.data)
    newComment.value = {
      entityId: '',
      entityType: 'ontology',
      content: ''
    }
  } catch (error) {
    console.error('Failed to add comment:', error)
    alert('Failed to add comment')
  }
}

const deleteComment = async (commentId) => {
  if (!confirm('Are you sure you want to delete this comment?')) return
  
  try {
    await axios.delete(`http://localhost:8080/api/collaboration/comment/delete/${commentId}`)
    comments.value = comments.value.filter(c => c.id !== commentId)
  } catch (error) {
    console.error('Failed to delete comment:', error)
    alert('Failed to delete comment')
  }
}

const getSessionStatusBadge = (status) => {
  switch (status) {
    case 'ACTIVE': return 'badge bg-success'
    case 'ENDED': return 'badge bg-secondary'
    default: return 'badge bg-secondary'
  }
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleString()
}
</script>

<style scoped>
.collaboration {
  min-height: 100vh;
  background-color: #f5f5f5;
}
</style>