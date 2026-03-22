<template>
  <div class="class-hierarchy">
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
              <router-link to="/hierarchy" class="nav-link active">Hierarchy</router-link>
            </li>
          </ul>
        </div>
      </div>
    </nav>
    <div class="container-fluid mt-3">
      <div class="row">
        <div class="col-md-4">
          <div class="card">
            <div class="card-header">
              <h5>Class Hierarchy</h5>
            </div>
            <div class="card-body">
              <div class="mb-3">
                <label class="form-label">Select Ontology</label>
                <select class="form-control" v-model="selectedOntologyId" @change="loadClassHierarchy">
                  <option value="">-- Select Ontology --</option>
                  <option v-for="ontology in ontologies" :key="ontology.id" :value="ontology.id">
                    {{ ontology.name }}
                  </option>
                </select>
              </div>
              <div class="tree-container">
                <ul class="tree">
                  <tree-node 
                    v-for="node in rootNodes" 
                    :key="node.id"
                    :node="node"
                    :selected-node="selectedNode"
                    @select="selectNode"
                    @expand="toggleExpand"/>
                </ul>
              </div>
            </div>
          </div>
        </div>
        <div class="col-md-8">
          <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
              <h5>Class Visualization</h5>
              <div>
                <button class="btn btn-sm btn-primary me-2" @click="expandAll">Expand All</button>
                <button class="btn btn-sm btn-secondary me-2" @click="collapseAll">Collapse All</button>
                <button class="btn btn-sm btn-info" @click="exportHierarchy">Export</button>
              </div>
            </div>
            <div class="card-body">
              <div ref="visualizationContainer" class="visualization-container">
                <svg ref="svgCanvas" :width="svgWidth" :height="svgHeight">
                  <g :transform="`translate(${translateX}, ${translateY}) scale(${scale})`">
                    <g v-for="node in visibleNodes" :key="node.id">
                      <rect 
                        :x="node.x" 
                        :y="node.y" 
                        :width="nodeWidth" 
                        :height="nodeHeight"
                        :fill="getNodeColor(node)"
                        :stroke="selectedNode && selectedNode.id === node.id ? '#ff0000' : '#333'"
                        :stroke-width="selectedNode && selectedNode.id === node.id ? 3 : 1"
                        rx="5"
                        @click="selectNode(node)"
                        class="hierarchy-node"/>
                      <text 
                        :x="node.x + nodeWidth / 2" 
                        :y="node.y + 25"
                        text-anchor="middle"
                        fill="white"
                        font-size="12"
                        font-weight="bold">
                        {{ truncateText(node.name, 15) }}
                      </text>
                    </g>
                    <g v-for="edge in visibleEdges" :key="edge.id">
                      <path 
                        :d="edge.path"
                        fill="none"
                        stroke="#666"
                        stroke-width="2"/>
                    </g>
                  </g>
                </svg>
              </div>
            </div>
          </div>
          <div class="card mt-3" v-if="selectedNode">
            <div class="card-header">
              <h5>Class Details</h5>
            </div>
            <div class="card-body">
              <div class="row">
                <div class="col-md-6">
                  <h6>{{ selectedNode.name }}</h6>
                  <p><strong>IRI:</strong> {{ selectedNode.iri }}</p>
                  <p><strong>Description:</strong> {{ selectedNode.description || 'N/A' }}</p>
                </div>
                <div class="col-md-6">
                  <h6>Statistics</h6>
                  <p><strong>Subclasses:</strong> {{ getSubclassCount(selectedNode.id) }}</p>
                  <p><strong>Depth:</strong> {{ getNodeDepth(selectedNode.id) }}</p>
                  <p><strong>Instances:</strong> {{ getInstanceCount(selectedNode.id) }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, h, defineComponent } from 'vue'
import http from '@/utils/http'

const ontologies = ref([])
const classes = ref([])
const selectedOntologyId = ref('')
const selectedNode = ref(null)
const expandedNodes = ref(new Set())
const svgWidth = ref(800)
const svgHeight = ref(600)
const translateX = ref(50)
const translateY = ref(50)
const scale = ref(1)
const nodeWidth = 120
const nodeHeight = 40
const horizontalSpacing = 150
const verticalSpacing = 60

const TreeNode = defineComponent({
  name: 'TreeNode',
  props: {
    node: Object,
    selectedNode: Object
  },
  emits: ['select', 'expand'],
  setup(props, { emit }) {
    const isExpanded = computed(() => expandedNodes.value.has(props.node.id))
    const hasChildren = computed(() => props.node.children && props.node.children.length > 0)
    const isSelected = computed(() => props.selectedNode && props.selectedNode.id === props.node.id)

    return () => h('li', { class: 'tree-node' }, [
      h('div', {
        class: ['tree-node-content', { selected: isSelected.value }],
        onClick: () => emit('select', props.node)
      }, [
        h('span', {
          class: ['expand-icon', { clickable: hasChildren.value }],
          onClick: (e) => {
            e.stopPropagation()
            if (hasChildren.value) emit('expand', props.node)
          }
        }, hasChildren.value ? (isExpanded.value ? '▼' : '▶') : '•'),
        h('span', { class: 'node-name' }, props.node.name)
      ]),
      hasChildren.value && isExpanded.value ? h('ul', { class: 'tree-children' },
        props.node.children.map(child =>
          h(TreeNode, {
            key: child.id,
            node: child,
            selectedNode: props.selectedNode,
            onSelect: (n) => emit('select', n),
            onExpand: (n) => emit('expand', n)
          })
        )
      ) : null
    ])
  }
})

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

const loadClassHierarchy = async () => {
  if (!selectedOntologyId.value) return
  
  try {
    const response = await http.get('/class/findAll')
    classes.value = response.data.filter(c => c.ontologyId === selectedOntologyId.value)
  } catch (error) {
    console.error('Failed to load classes:', error)
  }
}

const hierarchyData = computed(() => {
  const nodeMap = new Map()
  classes.value.forEach(cls => {
    nodeMap.set(cls.id, {
      id: cls.id,
      name: cls.name,
      iri: cls.iri,
      description: cls.description,
      superClassIds: cls.superClasses || [],
      children: []
    })
  })
  
  nodeMap.forEach(node => {
    node.superClassIds.forEach(superId => {
      const superNode = nodeMap.get(superId)
      if (superNode) {
        superNode.children.push(node)
      }
    })
  })
  
  return nodeMap
})

const rootNodes = computed(() => {
  const roots = []
  hierarchyData.value.forEach(node => {
    if (node.superClassIds.length === 0 || node.superClassIds.every(id => !hierarchyData.value.has(id))) {
      roots.push(node)
    }
  })
  return roots
})

const visibleNodes = computed(() => {
  const nodes = []
  const positionNode = (node, x, y, depth) => {
    nodes.push({
      ...node,
      x,
      y,
      depth
    })
    
    if (expandedNodes.value.has(node.id) && node.children.length > 0) {
      let childX = x - (node.children.length - 1) * horizontalSpacing / 2
      node.children.forEach(child => {
        positionNode(child, childX, y + verticalSpacing + nodeHeight, depth + 1)
        childX += horizontalSpacing
      })
    }
  }
  
  let startX = svgWidth.value / 2 - nodeWidth / 2
  rootNodes.value.forEach((root, index) => {
    positionNode(root, startX + index * horizontalSpacing * 2, 0, 0)
  })
  
  return nodes
})

const visibleEdges = computed(() => {
  const edges = []
  
  visibleNodes.value.forEach(node => {
    if (node.children && expandedNodes.value.has(node.id)) {
      node.children.forEach(child => {
        const childNode = visibleNodes.value.find(n => n.id === child.id)
        if (childNode) {
          edges.push({
            id: `${node.id}-${child.id}`,
            path: `M ${node.x + nodeWidth / 2} ${node.y + nodeHeight} 
                   C ${node.x + nodeWidth / 2} ${node.y + nodeHeight + 20},
                     ${childNode.x + nodeWidth / 2} ${childNode.y - 20},
                     ${childNode.x + nodeWidth / 2} ${childNode.y}`
          })
        }
      })
    }
  })
  
  return edges
})

const selectNode = (node) => {
  selectedNode.value = node
  if (!expandedNodes.value.has(node.id)) {
    expandedNodes.value.add(node.id)
  }
}

const toggleExpand = (node) => {
  if (expandedNodes.value.has(node.id)) {
    expandedNodes.value.delete(node.id)
  } else {
    expandedNodes.value.add(node.id)
  }
}

const expandAll = () => {
  hierarchyData.value.forEach(node => {
    expandedNodes.value.add(node.id)
  })
}

const collapseAll = () => {
  expandedNodes.value.clear()
}

const exportHierarchy = () => {
  const data = JSON.stringify(hierarchyData.value, null, 2)
  const blob = new Blob([data], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'class-hierarchy.json'
  a.click()
  URL.revokeObjectURL(url)
}

const getNodeColor = (node) => {
  if (node.children && node.children.length > 0) {
    return '#4CAF50'
  }
  return '#8BC34A'
}

const truncateText = (text, maxLength) => {
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}

const getSubclassCount = (nodeId) => {
  let count = 0
  const countChildren = (node) => {
    if (node.children) {
      count += node.children.length
      node.children.forEach(countChildren)
    }
  }
  const node = hierarchyData.value.get(nodeId)
  if (node) countChildren(node)
  return count
}

const getNodeDepth = (nodeId) => {
  let maxDepth = 0
  const findDepth = (node, depth) => {
    if (depth > maxDepth) maxDepth = depth
    if (node.children) {
      node.children.forEach(child => findDepth(child, depth + 1))
    }
  }
  const node = hierarchyData.value.get(nodeId)
  if (node) findDepth(node, 0)
  return maxDepth
}

const getInstanceCount = async (nodeId) => {
  try {
    const response = await http.get('/instance/findAll')
    return response.data.filter(i => i.classId === nodeId).length
  } catch (error) {
    return 0
  }
}
</script>

<style scoped>
.class-hierarchy {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.tree-container {
  max-height: 500px;
  overflow-y: auto;
}

.tree {
  list-style: none;
  padding-left: 0;
}

.tree-node {
  margin: 2px 0;
}

.tree-node-content {
  padding: 5px 10px;
  border-radius: 3px;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.tree-node-content:hover {
  background-color: #e9ecef;
}

.tree-node-content.selected {
  background-color: #007bff;
  color: white;
}

.expand-icon {
  width: 20px;
  text-align: center;
  margin-right: 5px;
}

.expand-icon.clickable {
  cursor: pointer;
}

.node-name {
  font-weight: 500;
}

.tree-children {
  list-style: none;
  padding-left: 20px;
}

.visualization-container {
  border: 1px solid #ddd;
  background-color: #fff;
  overflow: auto;
  height: 500px;
}

.hierarchy-node {
  cursor: pointer;
  transition: all 0.2s ease;
}

.hierarchy-node:hover {
  filter: brightness(1.1);
}
</style>