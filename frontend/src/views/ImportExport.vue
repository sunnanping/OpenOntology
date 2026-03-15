<template>
  <div class="import-export-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>{{ $t('ontology.importExport') }}</span>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane :label="$t('ontology.export')" name="export">
          <el-form :model="exportForm" label-width="120px">
            <el-form-item :label="$t('ontology.selectOntology')">
              <el-select v-model="exportForm.ontologyId" placeholder="Select Ontology" style="width: 100%">
                <el-option
                  v-for="ontology in ontologies"
                  :key="ontology.id"
                  :label="ontology.name"
                  :value="ontology.id"
                />
              </el-select>
            </el-form-item>

            <el-form-item :label="$t('ontology.exportFormat')">
              <el-select v-model="exportForm.format" placeholder="Select Format" style="width: 100%">
                <el-option
                  v-for="format in formats"
                  :key="format.code"
                  :label="format.name"
                  :value="format.code"
                />
              </el-select>
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="handleExport" :loading="exporting">
                {{ $t('ontology.export') }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane :label="$t('ontology.import')" name="import">
          <el-form :model="importForm" label-width="120px">
            <el-form-item :label="$t('ontology.selectFile')">
              <el-upload
                ref="uploadRef"
                :auto-upload="false"
                :on-change="handleFileChange"
                :limit="1"
                :file-list="fileList"
                accept=".owl,.rdf,.ttl,.nt,.jsonld,.man,.ofn,.xml,.csv,.json"
              >
                <el-button slot="trigger" type="primary">{{ $t('ontology.selectFile') }}</el-button>
                <template #tip>
                  <div class="el-upload__tip">
                    {{ $t('ontology.supportedFormats') }}: .owl, .rdf, .ttl, .nt, .jsonld, .man, .ofn, .xml, .csv, .json
                  </div>
                </template>
              </el-upload>
            </el-form-item>

            <el-form-item :label="$t('ontology.importFormat')">
              <el-select v-model="importForm.format" placeholder="Auto Detect" style="width: 100%">
                <el-option label="Auto Detect" value="" />
                <el-option
                  v-for="format in formats"
                  :key="format.code"
                  :label="format.name"
                  :value="format.code"
                />
              </el-select>
            </el-form-item>

            <el-form-item :label="$t('ontology.ontologyName')">
              <el-input v-model="importForm.ontologyName" :placeholder="$t('ontology.optional')" />
            </el-form-item>

            <el-form-item :label="$t('ontology.namespace')">
              <el-input v-model="importForm.namespace" :placeholder="$t('ontology.optional')" />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="handleImport" :loading="importing">
                {{ $t('ontology.import') }}
              </el-button>
              <el-button @click="resetImportForm">{{ $t('common.reset') }}</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-dialog
      v-model="showResult"
      :title="$t('ontology.importResult')"
      width="30%"
    >
      <el-result
        :icon="importResult.success ? 'success' : 'error'"
        :title="importResult.success ? $t('ontology.importSuccess') : $t('ontology.importFailed')"
        :sub-title="importResult.message"
      >
        <template #extra v-if="importResult.success">
          <el-button type="primary" @click="navigateToOntology(importResult.ontologyId)">
            {{ $t('ontology.viewOntology') }}
          </el-button>
        </template>
      </el-result>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const router = useRouter()
const activeTab = ref('export')
const exporting = ref(false)
const importing = ref(false)
const showResult = ref(false)
const uploadRef = ref(null)
const fileList = ref([])

const ontologies = ref([])
const formats = ref([])

const exportForm = ref({
  ontologyId: '',
  format: 'OWL'
})

const importForm = ref({
  format: '',
  ontologyName: '',
  namespace: ''
})

const importResult = ref({
  success: false,
  ontologyId: '',
  message: ''
})

const loadOntologies = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/ontology/findAll')
    ontologies.value = response.data
  } catch (error) {
    ElMessage.error('Failed to load ontologies')
    console.error(error)
  }
}

const loadFormats = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/ontology/import-export/formats')
    formats.value = response.data
  } catch (error) {
    ElMessage.error('Failed to load formats')
    console.error(error)
  }
}

const handleExport = async () => {
  if (!exportForm.value.ontologyId) {
    ElMessage.warning('Please select an ontology')
    return
  }

  exporting.value = true
  try {
    const response = await axios.get(
      `http://localhost:8080/api/ontology/import-export/export/${exportForm.value.ontologyId}`,
      {
        params: { format: exportForm.value.format },
        responseType: 'blob'
      }
    )

    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `${exportForm.value.ontologyId}.${getExportExtension(exportForm.value.format)}`)
    document.body.appendChild(link)
    link.click()
    link.remove()

    ElMessage.success('Export successful')
  } catch (error) {
    ElMessage.error('Export failed')
    console.error(error)
  } finally {
    exporting.value = false
  }
}

const handleFileChange = (file) => {
  fileList.value = [file]
  
  if (!importForm.value.format) {
    const extension = file.name.split('.').pop().toLowerCase()
    const formatMap = {
      'owl': 'OWL',
      'rdf': 'RDFXML',
      'ttl': 'TURTLE',
      'nt': 'NTRIPLES',
      'jsonld': 'JSONLD',
      'man': 'MANCHESTER',
      'ofn': 'FUNCTIONAL',
      'xml': 'XML',
      'csv': 'CSV',
      'json': 'JSON'
    }
    importForm.value.format = formatMap[extension] || ''
  }
}

const handleImport = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('Please select a file')
    return
  }

  importing.value = true
  try {
    const formData = new FormData()
    formData.append('file', fileList.value[0].raw)
    formData.append('format', importForm.value.format)
    if (importForm.value.ontologyName) {
      formData.append('ontologyName', importForm.value.ontologyName)
    }
    if (importForm.value.namespace) {
      formData.append('namespace', importForm.value.namespace)
    }

    const response = await axios.post(
      'http://localhost:8080/api/ontology/import-export/import',
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }
    )

    importResult.value = response.data
    showResult.value = true

    if (response.data.success) {
      ElMessage.success('Import successful')
      await loadOntologies()
    } else {
      ElMessage.error('Import failed')
    }
  } catch (error) {
    ElMessage.error('Import failed')
    console.error(error)
  } finally {
    importing.value = false
  }
}

const resetImportForm = () => {
  importForm.value = {
    format: '',
    ontologyName: '',
    namespace: ''
  }
  fileList.value = []
  if (uploadRef.value) {
    uploadRef.value.clearFiles()
  }
}

const navigateToOntology = (ontologyId) => {
  showResult.value = false
  router.push(`/ontology/${ontologyId}`)
}

const getExportExtension = (format) => {
  const formatObj = formats.value.find(f => f.code === format)
  return formatObj ? formatObj.extension.substring(1) : 'owl'
}

onMounted(() => {
  loadOntologies()
  loadFormats()
})
</script>

<style scoped>
.import-export-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.box-card {
  margin-bottom: 20px;
}

.el-form {
  max-width: 600px;
}
</style>
