<template>
  <div class="modal-overlay" @click="closeModal">
    <div class="modal-content" ref="modalContent" @mousedown="startDrag" @click.stop>
      <div class="modal-header">
        <h2>Create New Project</h2>
        <button class="close-btn" @click="closeModal">&times;</button>
      </div>
      <div class="modal-body">
        <form @submit.prevent="createProject">
          <div class="form-group">
            <label for="projectName">Project name</label>
            <input type="text" id="projectName" v-model="form.name" required class="form-control">
          </div>
          
          <div class="form-group">
            <label for="language">Language</label>
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
            <label>Create from existing sources</label>
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
                选择文件
              </button>
              <span v-if="selectedFile">{{ selectedFile.name }}</span>
              <span v-else>未选择文件</span>
            </div>
          </div>
          
          <div class="form-actions">
            <button type="button" class="btn btn-secondary" @click="closeModal">Cancel</button>
            <button type="submit" class="btn btn-primary">Create New Project</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CreateProject',
  data() {
    return {
      form: {
        name: '',
        language: 'en',
        namespace: ''
      },
      selectedLanguage: 'en',
      showLanguageDropdown: false,
      selectedFile: null,
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
      ],
      isDragging: false,
      startX: 0,
      startY: 0,
      offsetX: 0,
      offsetY: 0
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
    closeModal() {
      this.$emit('close');
    },
    startDrag(e) {
      if (e.target.classList.contains('modal-header')) {
        this.isDragging = true;
        this.startX = e.clientX;
        this.startY = e.clientY;
        
        const rect = this.$refs.modalContent.getBoundingClientRect();
        this.offsetX = this.startX - rect.left;
        this.offsetY = this.startY - rect.top;
        
        document.addEventListener('mousemove', this.drag);
        document.addEventListener('mouseup', this.stopDrag);
      }
    },
    drag(e) {
      if (this.isDragging) {
        const x = e.clientX - this.offsetX;
        const y = e.clientY - this.offsetY;
        
        this.$refs.modalContent.style.left = `${x}px`;
        this.$refs.modalContent.style.top = `${y}px`;
      }
    },
    stopDrag() {
      this.isDragging = false;
      document.removeEventListener('mousemove', this.drag);
      document.removeEventListener('mouseup', this.stopDrag);
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
      try {
        if (this.selectedFile) {
          // 从现有资源创建项目（导入）
          const formData = new FormData();
          formData.append('file', this.selectedFile);
          formData.append('format', this.getFileFormat(this.selectedFile.name));
          formData.append('ontologyName', this.form.name);
          formData.append('namespace', this.form.namespace || `http://example.org/${this.form.name.toLowerCase().replace(/\s+/g, '-')}`);
          
          const response = await this.$axios.post('/api/ontology/import-export/import', formData, {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          });
          
          this.$emit('projectCreated', response.data);
          this.closeModal();
        } else {
          // 创建新项目
          const response = await this.$axios.post('/api/ontology/create', {
            name: this.form.name,
            namespace: this.form.namespace || `http://example.org/${this.form.name.toLowerCase().replace(/\s+/g, '-')}`,
            description: '',
            format: 'OWL'
          });
          
          this.$emit('projectCreated', response.data);
          this.closeModal();
        }
      } catch (error) {
        console.error('创建项目失败:', error);
        alert('创建项目失败: ' + (error.response?.data?.message || '未知错误'));
      }
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
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  width: 90%;
  max-width: 600px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  cursor: move;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #e0e0e0;
  background-color: #f5f5f5;
  border-radius: 8px 8px 0 0;
}

.modal-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: bold;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
}

.close-btn:hover {
  background-color: #e0e0e0;
}

.modal-body {
  padding: 20px;
  cursor: default;
}

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

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 30px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-primary {
  background-color: #9C27B0;
  color: white;
  font-weight: bold;
}

.btn:hover {
  opacity: 0.9;
}

@media (max-width: 768px) {
  .modal-content {
    width: 95%;
    margin: 10px;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .btn {
    width: 100%;
  }
}
</style>