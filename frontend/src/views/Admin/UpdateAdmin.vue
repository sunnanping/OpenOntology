<template>
  <div class="admin-container">
    <h1>更新管理员</h1>
    <form v-if="admin" @submit.prevent="updateAdmin">
      <div class="form-group">
        <label for="username">用户名</label>
        <input type="text" id="username" v-model="admin.username" required>
      </div>
      <div class="form-group">
        <label for="email">邮箱</label>
        <input type="email" id="email" v-model="admin.email" required>
      </div>
      <div class="form-group">
        <label for="firstName">名字</label>
        <input type="text" id="firstName" v-model="admin.firstName">
      </div>
      <div class="form-group">
        <label for="lastName">姓氏</label>
        <input type="text" id="lastName" v-model="admin.lastName">
      </div>
      <div class="form-group">
        <label for="department">部门</label>
        <input type="text" id="department" v-model="admin.department">
      </div>
      <div class="form-group">
        <label for="phone">电话</label>
        <input type="text" id="phone" v-model="admin.phone">
      </div>
      <div class="form-group">
        <label for="password">密码（留空不修改）</label>
        <input type="password" id="password" v-model="password">
      </div>
      <div class="form-group">
        <label>
          <input type="checkbox" v-model="admin.enabled"> 启用
        </label>
      </div>
      <button type="submit" class="btn btn-primary">更新</button>
      <button type="button" class="btn btn-secondary" @click="$router.push('/admin/list')">取消</button>
    </form>
    <div v-else class="loading">加载中...</div>
    <div v-if="message" :class="messageType">{{ message }}</div>
  </div>
</template>

<script>
export default {
  name: 'UpdateAdmin',
  data() {
    return {
      admin: null,
      password: '',
      message: '',
      messageType: ''
    }
  },
  mounted() {
    this.fetchAdmin()
  },
  methods: {
    async fetchAdmin() {
      const adminId = this.$route.query.id
      if (!adminId) {
        this.message = '缺少管理员ID'
        this.messageType = 'error'
        return
      }
      try {
        const response = await this.$axios.get(`/api/admin/findById/${adminId}`)
        this.admin = response.data
      } catch (error) {
        this.message = '获取管理员信息失败：' + (error.response?.data?.message || '未知错误')
        this.messageType = 'error'
      }
    },
    async updateAdmin() {
      try {
        const updateData = { ...this.admin }
        if (this.password) {
          updateData.password = this.password
        }
        const response = await this.$axios.put('/api/admin/update', updateData)
        this.message = '更新成功！'
        this.messageType = 'success'
        this.$router.push('/admin/list')
      } catch (error) {
        this.message = '更新失败：' + (error.response?.data?.message || '未知错误')
        this.messageType = 'error'
      }
    }
  }
}
</script>

<style scoped>
.admin-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

button {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 10px;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.loading {
  text-align: center;
  padding: 20px;
  color: #666;
}

.success {
  color: green;
  margin-top: 10px;
}

.error {
  color: red;
  margin-top: 10px;
}
</style>