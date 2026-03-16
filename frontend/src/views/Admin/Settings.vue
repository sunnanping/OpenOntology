<template>
  <div class="settings-container">
    <h1>系统设置</h1>
    <div class="settings-form">
      <h2>Application URL</h2>
      <div class="form-section">
        <div class="form-group">
          <label for="scheme">Scheme</label>
          <select id="scheme" v-model="settings.scheme" class="form-control">
            <option value="http">http</option>
            <option value="https">https</option>
          </select>
        </div>
        <div class="form-group">
          <label for="host">Host</label>
          <input type="text" id="host" v-model="settings.host" class="form-control" placeholder="localhost">
        </div>
        <div class="form-group">
          <label for="path">Path</label>
          <input type="text" id="path" v-model="settings.path" class="form-control" placeholder="(Leave blank for no path)">
        </div>
        <div class="form-group">
          <label for="port">Port</label>
          <input type="text" id="port" v-model="settings.port" class="form-control" placeholder="e.g. 8443 (leave blank for default port)">
        </div>
      </div>

      <h2>Permissions</h2>
      <div class="form-section">
        <div class="checkbox-group">
          <label>
            <input type="checkbox" v-model="settings.accountCreationEnabled">
            Account creation enabled
          </label>
        </div>
        <h3>Project Creation and Upload</h3>
        <div class="checkbox-group">
          <label>
            <input type="checkbox" v-model="settings.projectCreationEnabled">
            Project creation enabled
          </label>
        </div>
        <div class="checkbox-group">
          <label>
            <input type="checkbox" v-model="settings.projectUploadEnabled">
            Project upload enabled
          </label>
        </div>
        <div class="form-group">
          <label for="maxUploadSize">Max upload size (MB)</label>
          <input type="number" id="maxUploadSize" v-model.number="settings.maxUploadSize" class="form-control" placeholder="0 (Leave blank for no limit)">
        </div>
      </div>

      <div class="form-actions">
        <button class="btn btn-secondary" @click="cancel">Cancel</button>
        <button class="btn btn-primary" @click="saveSettings">Apply</button>
      </div>
    </div>
    <div v-if="message" :class="messageType">{{ message }}</div>
  </div>
</template>

<script>
export default {
  name: 'Settings',
  data() {
    return {
      settings: {
        scheme: 'http',
        host: 'localhost',
        path: '',
        port: '8080',
        accountCreationEnabled: true,
        projectCreationEnabled: true,
        projectUploadEnabled: true,
        maxUploadSize: 50
      },
      message: '',
      messageType: ''
    }
  },
  mounted() {
    this.loadSettings()
  },
  methods: {
    async loadSettings() {
      try {
        const response = await this.$http.get('/admin/settings')
        this.settings = response.data
      } catch (error) {
        this.message = '加载设置失败：' + (error.response?.data?.message || '未知错误')
        this.messageType = 'error'
      }
    },
    async saveSettings() {
      try {
        const response = await this.$http.put('/admin/settings', this.settings)
        this.settings = response.data
        this.message = '设置保存成功！'
        this.messageType = 'success'
      } catch (error) {
        this.message = '保存设置失败：' + (error.response?.data?.message || '未知错误')
        this.messageType = 'error'
      }
    },
    cancel() {
      this.loadSettings()
    }
  }
}
</script>

<style scoped>
.settings-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.settings-form {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.form-section {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #dee2e6;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-control {
  width: 100%;
  padding: 8px;
  border: 1px solid #ced4da;
  border-radius: 4px;
  font-size: 14px;
}

.checkbox-group {
  margin-bottom: 10px;
}

.checkbox-group label {
  font-weight: normal;
  cursor: pointer;
}

.checkbox-group input[type="checkbox"] {
  margin-right: 8px;
}

.form-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.success {
  color: green;
  margin-top: 15px;
  padding: 10px;
  background: #d4edda;
  border: 1px solid #c3e6cb;
  border-radius: 4px;
}

.error {
  color: red;
  margin-top: 15px;
  padding: 10px;
  background: #f8d7da;
  border: 1px solid #f5c6cb;
  border-radius: 4px;
}
</style>