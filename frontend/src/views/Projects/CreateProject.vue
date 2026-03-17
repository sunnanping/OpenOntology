<template>
  <DraggableModal
    :title="t('project.create.title')"
    width="600px"
    @close="closeModal"
  >
    <form @submit.prevent="createProject">
      <div class="form-group">
        <label for="projectName">{{ t('project.create.name') }}</label>
        <input type="text" id="projectName" v-model="form.name" required class="form-control">
      </div>
      
      <div class="form-group">
        <label for="language">{{ t('project.create.language') }}</label>
        <div class="language-selector">
          <input 
            type="text" 
            id="language" 
            v-model="selectedLanguage" 
            @focus="showLanguageDropdown = true"
            @blur="hideLanguageDropdown"
            class="form-control"
          >
          <div v-if="showLanguageDropdown" class="language-dropdown">
            <div 
              v-for="lang in filteredLanguages" 
              :key="lang.code"
              class="language-item"
              @mousedown="selectLanguage(lang)"
            >
              {{ lang.code }} ({{ lang.name }})
            </div>
          </div>
        </div>
      </div>
      
      <div class="form-group">
        <label for="description">{{ t('project.create.description') }}</label>
        <textarea 
          id="description" 
          v-model="form.description" 
          class="form-control" 
          rows="4" 
          :placeholder="t('project.create.descriptionPlaceholder')"
        ></textarea>
      </div>
      
      <div class="form-group">
        <label>{{ t('project.create.sources') }}</label>
        <div class="file-upload">
          <input 
            type="file" 
            id="fileInput" 
            ref="fileInput"
            @change="handleFileChange"
            style="display: none"
          >
          <button 
            type="button" 
            class="btn btn-secondary"
            @click="$refs.fileInput.click()"
          >
            {{ t('project.create.selectFile') }}
          </button>
          <span v-if="selectedFile">{{ selectedFile.name }}</span>
          <span v-else>{{ t('project.create.noFileSelected') }}</span>
        </div>
      </div>
    </form>
    
    <template #footer>
      <button type="button" class="btn btn-secondary" @click="closeModal">{{ t('project.create.cancel') }}</button>
      <button type="button" class="btn btn-primary" style="background-color: #9C27B0; border-color: #9C27B0;" @click="createProject">{{ t('project.create.submit') }}</button>
    </template>
  </DraggableModal>
  
  <!-- Alert Modal -->
  <AlertModal
    v-if="showAlert"
    :title="alertType === 'error' ? t('app.error') : alertType === 'success' ? t('app.success') : alertType === 'warning' ? t('app.warning') : t('app.info')"
    :message="alertMessage"
    :type="alertType"
    @close="closeAlert"
  />
</template>

<script>
import http from '@/utils/http'
import DraggableModal from '@/components/DraggableModal.vue'
import AlertModal from '@/components/AlertModal.vue'
import { useI18n } from 'vue-i18n'

export default {
  name: 'CreateProject',
  components: {
    DraggableModal,
    AlertModal
  },
  data() {
    return {
      form: {
        name: '',
        language: 'en',
        namespace: '',
        description: ''
      },
      selectedLanguage: 'en',
      showLanguageDropdown: false,
      selectedFile: null,
      showAlert: false,
      alertMessage: '',
      alertType: 'error',
      languages: [
        { code: 'en', name: 'English' },
        { code: 'zh', name: 'Chinese' },
        { code: 'zh-CN', name: 'Chinese; PRC' },
        { code: 'zh-HK', name: 'Chinese; Hong Kong' },
        { code: 'zh-TW', name: 'Chinese; Taiwan' },
        { code: 'fr', name: 'French' },
        { code: 'de', name: 'German' },
        { code: 'es', name: 'Spanish' },
        { code: 'it', name: 'Italian' },
        { code: 'ja', name: 'Japanese' },
        { code: 'ko', name: 'Korean' },
        { code: 'ru', name: 'Russian' },
        { code: 'pt', name: 'Portuguese' },
        { code: 'ar', name: 'Arabic' },
        { code: 'zu', name: 'Zulu' },
        { code: 'za', name: 'Zhuang; Chuang' }
      ]
    }
  },
  computed: {
    filteredLanguages() {
      const query = this.selectedLanguage.toLowerCase();
      return this.languages.filter(lang => 
        lang.code.toLowerCase().includes(query) || 
        lang.name.toLowerCase().includes(query)
      );
    }
  },
  methods: {
    t(key, options) {
      return this.$t(key, options);
    },
    closeModal() {
      this.$emit('close');
    },
    selectLanguage(lang) {
      this.selectedLanguage = lang.code;
      this.form.language = lang.code;
      this.showLanguageDropdown = false;
    },
    hideLanguageDropdown() {
      // 延迟隐藏，以便点击下拉项时能触发选择
      setTimeout(() => {
        this.showLanguageDropdown = false;
      }, 200);
    },
    handleFileChange(e) {
      this.selectedFile = e.target.files[0];
    },
    async createProject() {
      // 表单校验
      if (!this.form.name.trim()) {
        this.alertMessage = this.t('project.create.errors.nameRequired');
        this.alertType = 'error';
        this.showAlert = true;
        return;
      }
      
      try {
        const userStr = localStorage.getItem('user')
        const user = userStr ? JSON.parse(userStr) : {}
        
        if (this.selectedFile) {
          const formData = new FormData();
          formData.append('file', this.selectedFile);
          formData.append('format', this.getFileFormat(this.selectedFile.name));
          formData.append('ontologyName', this.form.name);
          formData.append('namespace', this.form.namespace || `http://example.org/${this.form.name.toLowerCase().replace(/\s+/g, '-')}`);
          formData.append('description', this.form.description);
          formData.append('creatorId', user.username);
          
          const response = await http.post('/ontology/import-export/import', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          });
          
          this.$emit('projectCreated', response.data);
          this.closeModal();
        } else {
          const response = await http.post('/ontology/create', {
            name: this.form.name,
            namespace: this.form.namespace || `http://example.org/${this.form.name.toLowerCase().replace(/\s+/g, '-')}`,
            description: this.form.description,
            format: 'OWL',
            creatorId: user.username
          });
          
          this.$emit('projectCreated', response.data);
          this.closeModal();
        }
      } catch (error) {
        console.error('创建项目失败:', error);
        const source = error.response?.config?.url || '服务器';
        const errorMessage = error.response?.data?.message || '未知错误';
        this.alertMessage = `来源: ${source}\n创建项目失败: ${errorMessage}`;
        this.alertType = 'error';
        this.showAlert = true;
      }
    },
    closeAlert() {
      this.showAlert = false;
    },
    getFileFormat(filename) {
      const extension = filename.split('.').pop().toLowerCase();
      switch (extension) {
        case 'owl':
          return 'OWL';
        case 'rdf':
        case 'xml':
          return 'RDFXML';
        case 'ttl':
          return 'TURTLE';
        case 'nt':
          return 'NTRIPLES';
        case 'jsonld':
          return 'JSONLD';
        case 'csv':
          return 'CSV';
        default:
          return 'OWL';
      }
    }
  }
}
</script>

<style scoped>
.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
  font-size: 14px;
}

.form-control {
  width: 100%;
  padding: 10px;
  border: 1px solid #ced4da;
  border-radius: 4px;
  font-size: 14px;
}

.language-selector {
  position: relative;
}

.language-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #ced4da;
  border-top: none;
  border-radius: 0 0 4px 4px;
  max-height: 200px;
  overflow-y: auto;
  z-index: 1001;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.language-item {
  padding: 8px 12px;
  cursor: pointer;
  font-size: 14px;
}

.language-item:hover {
  background-color: #f5f5f5;
}

.file-upload {
  display: flex;
  align-items: center;
  gap: 10px;
}

@media (max-width: 768px) {
  .file-upload {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>