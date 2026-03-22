<template>
  <div class="ontology-editor">
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
              <router-link to="/editor" class="nav-link active">Editor</router-link>
            </li>
          </ul>
        </div>
      </div>
    </nav>
    <div class="container-fluid mt-3">
      <div class="row">
        <div class="col-md-3">
          <div class="card">
            <div class="card-header">
              <h5>Ontology Elements</h5>
            </div>
            <div class="card-body">
              <div class="mb-3">
                <label class="form-label">Select Ontology</label>
                <select class="form-control" v-model="selectedOntologyId" @change="loadOntologyData">
                  <option value="">-- Select Ontology --</option>
                  <option v-for="ontology in ontologies" :key="ontology.id" :value="ontology.id">
                    {{ ontology.name }}
                  </option>
                </select>
              </div>
              <div class="accordion" id="elementAccordion">
                <div class="accordion-item">
                  <h2 class="accordion-header">
                    <button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#classesCollapse">
                      Classes
                    </button>
                  </h2>
                  <div id="classesCollapse" class="accordion-collapse collapse show">
                    <div class="accordion-body">
                      <ul class="list-group">
                        <li v-for="cls in classes" :key="cls.id" 
                            class="list-group-item list-group-item-action"
                            :class="{ active: selectedElement && selectedElement.id === cls.id }"
                            @click="selectElement(cls, 'class')"
                            draggable="true"
                            @dragstart="dragStart($event, cls, 'class')">
                          {{ cls.name }}
                        </li>
                      </ul>
                    </div>
                  </div>
                </div>
                <div class="accordion-item">
                  <h2 class="accordion-header">
                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#propertiesCollapse">
                      Properties
                    </button>
                  </h2>
                  <div id="propertiesCollapse" class="accordion-collapse collapse">
                    <div class="accordion-body">
                      <ul class="list-group">
                        <li v-for="prop in properties" :key="prop.id" 
                            class="list-group-item list-group-item-action"
                            :class="{ active: selectedElement && selectedElement.id === prop.id }"
                            @click="selectElement(prop, 'property')"
                            draggable="true"
                            @dragstart="dragStart($event, prop, 'property')">
                          {{ prop.name }}
                        </li>
                      </ul>
                    </div>
                  </div>
                </div>
                <div class="accordion-item">
                  <h2 class="accordion-header">
                    <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#instancesCollapse">
                      Instances
                    </button>
                  </h2>
                  <div id="instancesCollapse" class="accordion-collapse collapse">
                    <div class="accordion-body">
                      <ul class="list-group">
                        <li v-for="inst in instances" :key="inst.id" 
                            class="list-group-item list-group-item-action"
                            :class="{ active: selectedElement && selectedElement.id === inst.id }"
                            @click="selectElement(inst, 'instance')"
                            draggable="true"
                            @dragstart="dragStart($event, inst, 'instance')">
                          {{ inst.name }}
                        </li>
                      </ul>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-6">
          <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
              <h5>Visual Editor</h5>
              <div>
                <button class="btn btn-sm btn-primary me-2" @click="zoomIn">Zoom In</button>
                <button class="btn btn-sm btn-primary me-2" @click="zoomOut">Zoom Out</button>
                <button class="btn btn-sm btn-secondary" @click="resetView">Reset</button>
              </div>
            </div>
            <div class="card-body">
              <div ref="canvasContainer" 
                   class="canvas-container"
                   @drop="drop"
                   @dragover.prevent
                   @click="canvasClick">
                <svg ref="canvas" :width="canvasWidth" :height="canvasHeight">
                  <g :transform="`translate(${pan.x}, ${pan.y}) scale(${zoom})`">
                    <g v-for="node in nodes" :key="node.id">
                      <rect 
                        :x="node.x" 
                        :y="node.y" 
                        :width="node.width" 
                        :height="node.height"
                        :fill="getNodeColor(node.type)"
                        stroke="#333"
                        stroke-width="2"
                        rx="5"
                        @mousedown="startDrag($event, node)"
                        @click.stop="selectNode(node)"
                        class="node-rect"/>
                      <text 
                        :x="node.x + node.width / 2" 
                        :y="node.y + 20"
                        text-anchor="middle"
                        fill="white"
                        font-size="14"
                        font-weight="bold">
                        {{ node.name }}
                      </text>
                      <text 
                        :x="node.x + node.width / 2" 
                        :y="node.y + 40"
                        text-anchor="middle"
                        fill="white"
                        font-size="10">
                        {{ node.type }}
                      </text>
                    </g>
                    <g v-for="edge in edges" :key="edge.id">
                      <line 
                        :x1="edge.x1" 
                        :y1="edge.y1" 
                        :x2="edge.x2" 
                        :y2="edge.y2"
                        stroke="#666"
                        stroke-width="2"
                        marker-end="url(#arrowhead)"/>
                      <text 
                        :x="(edge.x1 + edge.x2) / 2" 
                        :y="(edge.y1 + edge.y2) / 2 - 5"
                        text-anchor="middle"
                        fill="#333"
                        font-size="10">
                        {{ edge.label }}
                      </text>
                    </g>
                    <defs>
                      <marker id="arrowhead" markerWidth="10" markerHeight="7" refX="9" refY="3.5" orient="auto">
                        <polygon points="0 0, 10 3.5, 0 7" fill="#666" />
                      </marker>
                    </defs>
                  </g>
                </svg>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-3">
          <div class="card">
            <div class="card-header">
              <h5>Properties</h5>
            </div>
            <div class="card-body" v-if="selectedElement">
              <h6>{{ selectedElement.name }}</h6>
              <p class="text-muted">Type: {{ selectedElementType }}</p>
              <hr/>
              <div v-if="selectedElementType === 'class'">
                <div class="mb-3">
                  <label class="form-label">Name</label>
                  <input type="text" class="form-control" v-model="selectedElement.name" @change="updateElement">
                </div>
                <div class="mb-3">
                  <label class="form-label">IRI</label>
                  <input type="text" class="form-control" v-model="selectedElement.iri" readonly>
                </div>
                <div class="mb-3">
                  <label class="form-label">Description</label>
                  <textarea class="form-control" v-model="selectedElement.description" @change="updateElement"></textarea>
                </div>
              </div>
              <div v-else-if="selectedElementType === 'property'">
                <div class="mb-3">
                  <label class="form-label">Name</label>
                  <input type="text" class="form-control" v-model="selectedElement.name" @change="updateElement">
                </div>
                <div class="mb-3">
                  <label class="form-label">Property Type</label>
                  <input type="text" class="form-control" :value="selectedElement.propertyType" readonly>
                </div>
                <div class="mb-3">
                  <label class="form-label">Domain</label>
                  <input type="text" class="form-control" :value="selectedElement.domains ? selectedElement.domains.join(', ') : ''" readonly>
                </div>
                <div class="mb-3">
                  <label class="form-label">Range</label>
                  <input type="text" class="form-control" :value="selectedElement.ranges ? selectedElement.ranges.join(', ') : ''" readonly>
                </div>
              </div>
              <div v-else-if="selectedElementType === 'instance'">
                <div class="mb-3">
                  <label class="form-label">Name</label>
                  <input type="text" class="form-control" v-model="selectedElement.name" @change="updateElement">
                </div>
                <div class="mb-3">
                  <label class="form-label">Class</label>
                  <input type="text" class="form-control" :value="selectedElement.classId" readonly>
                </div>
                <div class="mb-3">
                  <label class="form-label">Properties</label>
                  <pre>{{ JSON.stringify(selectedElement.propertyValues, null, 2) }}</pre>
                </div>
              </div>
              <button class="btn btn-danger btn-sm" @click="deleteElement">Delete</button>
            </div>
            <div class="card-body" v-else>
              <p class="text-muted">Select an element to view its properties</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import http from '@/utils/http'

const ontologies = ref([])
const classes = ref([])
const properties = ref([])
const instances = ref([])
const selectedOntologyId = ref('')
const selectedElement = ref(null)
const selectedElementType = ref('')
const nodes = ref([])
const edges = ref([])
const canvasWidth = ref(800)
const canvasHeight = ref(600)
const zoom = ref(1)
const pan = reactive({ x: 0, y: 0 })
const draggingNode = ref(null)
const dragOffset = reactive({ x: 0, y: 0 })

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

const loadOntologyData = async () => {
  if (!selectedOntologyId.value) return
  
  try {
    const [classesRes, propertiesRes, instancesRes] = await Promise.all([
      http.get('/class/findAll'),
      http.get('/property/findAll'),
      http.get('/instance/findAll')
    ])
    
    classes.value = classesRes.data.filter(c => c.ontologyId === selectedOntologyId.value)
    properties.value = propertiesRes.data.filter(p => p.ontologyId === selectedOntologyId.value)
    instances.value = instancesRes.data.filter(i => i.ontologyId === selectedOntologyId.value)
    
    initializeCanvas()
  } catch (error) {
    console.error('Failed to load ontology data:', error)
  }
}

const initializeCanvas = () => {
  nodes.value = []
  edges.value = []
  
  let x = 50
  let y = 50
  
  classes.value.forEach(cls => {
    nodes.value.push({
      id: cls.id,
      name: cls.name,
      type: 'class',
      x: x,
      y: y,
      width: 120,
      height: 60,
      data: cls
    })
    x += 150
    if (x > canvasWidth.value - 150) {
      x = 50
      y += 100
    }
  })
  
  x = 50
  y += 150
  
  properties.value.forEach(prop => {
    nodes.value.push({
      id: prop.id,
      name: prop.name,
      type: 'property',
      x: x,
      y: y,
      width: 120,
      height: 60,
      data: prop
    })
    x += 150
    if (x > canvasWidth.value - 150) {
      x = 50
      y += 100
    }
  })
  
  x = 50
  y += 150
  
  instances.value.forEach(inst => {
    nodes.value.push({
      id: inst.id,
      name: inst.name,
      type: 'instance',
      x: x,
      y: y,
      width: 120,
      height: 60,
      data: inst
    })
    x += 150
    if (x > canvasWidth.value - 150) {
      x = 50
      y += 100
    }
  })
  
  classes.value.forEach(cls => {
    if (cls.superClasses && cls.superClasses.length > 0) {
      cls.superClasses.forEach(superClassId => {
        const superNode = nodes.value.find(n => n.id === superClassId)
        const childNode = nodes.value.find(n => n.id === cls.id)
        if (superNode && childNode) {
          edges.value.push({
            id: `${superNode.id}-${childNode.id}`,
            x1: superNode.x + superNode.width / 2,
            y1: superNode.y + superNode.height,
            x2: childNode.x + childNode.width / 2,
            y2: childNode.y,
            label: 'subclassOf'
          })
        }
      })
    }
  })
}

const selectElement = (element, type) => {
  selectedElement.value = element
  selectedElementType.value = type
}

const selectNode = (node) => {
  selectedElement.value = node.data
  selectedElementType.value = node.type
}

const getNodeColor = (type) => {
  switch (type) {
    case 'class': return '#4CAF50'
    case 'property': return '#2196F3'
    case 'instance': return '#FF9800'
    default: return '#9E9E9E'
  }
}

const dragStart = (event, element, type) => {
  event.dataTransfer.setData('element', JSON.stringify({ element, type }))
}

const drop = (event) => {
  const data = event.dataTransfer.getData('element')
  if (data) {
    const { element, type } = JSON.parse(data)
    const rect = event.target.getBoundingClientRect()
    const x = event.clientX - rect.left - 60
    const y = event.clientY - rect.top - 30
    
    const existingNode = nodes.value.find(n => n.id === element.id)
    if (existingNode) {
      existingNode.x = x
      existingNode.y = y
    } else {
      nodes.value.push({
        id: element.id,
        name: element.name,
        type: type,
        x: x,
        y: y,
        width: 120,
        height: 60,
        data: element
      })
    }
  }
}

const startDrag = (event, node) => {
  draggingNode.value = node
  dragOffset.x = event.clientX - node.x
  dragOffset.y = event.clientY - node.y
  
  document.addEventListener('mousemove', onMouseMove)
  document.addEventListener('mouseup', stopDrag)
}

const onMouseMove = (event) => {
  if (draggingNode.value) {
    draggingNode.value.x = event.clientX - dragOffset.x
    draggingNode.value.y = event.clientY - dragOffset.y
  }
}

const stopDrag = () => {
  draggingNode.value = null
  document.removeEventListener('mousemove', onMouseMove)
  document.removeEventListener('mouseup', stopDrag)
}

const canvasClick = () => {
  selectedElement.value = null
  selectedElementType.value = ''
}

const zoomIn = () => {
  zoom.value = Math.min(zoom.value * 1.2, 3)
}

const zoomOut = () => {
  zoom.value = Math.max(zoom.value / 1.2, 0.3)
}

const resetView = () => {
  zoom.value = 1
  pan.x = 0
  pan.y = 0
}

const updateElement = async () => {
  if (!selectedElement.value) return
  
  try {
    let url = ''
    switch (selectedElementType.value) {
      case 'class':
        url = `/class/update/${selectedElement.value.id}`
        break
      case 'property':
        url = `/property/update/${selectedElement.value.id}`
        break
      case 'instance':
        url = `/instance/update/${selectedElement.value.id}`
        break
    }
    
    if (url) {
      await http.put(url, selectedElement.value)
    }
  } catch (error) {
    console.error('Failed to update element:', error)
  }
}

const deleteElement = async () => {
  if (!selectedElement.value || !confirm('Are you sure you want to delete this element?')) return
  
  try {
    let url = ''
    switch (selectedElementType.value) {
      case 'class':
        url = `/class/delete/${selectedElement.value.id}`
        break
      case 'property':
        url = `/property/delete/${selectedElement.value.id}`
        break
      case 'instance':
        url = `/instance/delete/${selectedElement.value.id}`
        break
    }
    
    if (url) {
      await http.delete(url)
      nodes.value = nodes.value.filter(n => n.id !== selectedElement.value.id)
      selectedElement.value = null
      selectedElementType.value = ''
      await loadOntologyData()
    }
  } catch (error) {
    console.error('Failed to delete element:', error)
  }
}
</script>

<style scoped>
.ontology-editor {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.canvas-container {
  border: 1px solid #ddd;
  background-color: #fff;
  overflow: auto;
  height: 500px;
}

.node-rect {
  cursor: move;
}

.node-rect:hover {
  filter: brightness(1.1);
}

.list-group-item {
  cursor: pointer;
}

.list-group-item:hover {
  background-color: #e9ecef;
}
</style>