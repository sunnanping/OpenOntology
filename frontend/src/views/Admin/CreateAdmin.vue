<template>
  <div class="admin-container">
    <h1>创建管理员</h1>
    <form @submit.prevent="createAdmin" class="admin-form">
      <div class="form-group">
        <label for="username">用户名：</label>
        <input type="text" id="username" v-model="admin.username" required>
      </div>
      <div class="form-group">
        <label for="password">密码：</label>
        <input type="password" id="password" v-model="admin.password" required>
      </div>
      <div class="form-group">
        <label for="email">邮箱：</label>
        <input type="email" id="email" v-model="admin.email" required>
      </div>
      <div class="form-group">
        <label for="firstName">名字：</label>
        <input type="text" id="firstName" v-model="admin.firstName">
      </div>
      <div class="form-group">
        <label for="lastName">姓氏：</label>
        <input type="text" id="lastName" v-model="admin.lastName">
      </div>
      <div class="form-group">
        <label for="department">部门：</label>
        <input type="text" id="department" v-model="admin.department">
      </div>
      <div class="form-group">
        <label for="phone">电话：</label>
        <input type="tel" id="phone" v-model="admin.phone">
      </div>
      <div class="form-actions">
        <button type="submit" class="btn btn-primary">创建</button>
        <button type="button" class="btn btn-secondary" @click="$router.push('/admin/list')">取消</button>
      </div>
    </form>
    <div v-if="message" :class="messageType">{{ message }}</div>
  </div>
</template>

<script>
export default {
  name: 'CreateAdmin',
  data() {
    return {
      admin: {
        username: '',
        password: '',
        email: '',
        firstName: '',
        lastName: '',
        department: '',
        phone: ''
      },
      message: '',
      messageType: ''
    }
  },
  methods: {
    async createAdmin() {
      try {
        await this.$axios.post('/api/admin/create', this.admin)
        this.message = '创建成功！'
        this.messageType = 'success'
        setTimeout(() => {
          this.$router.push('/admin/list')
        }, 1500)
      } catch (error) {
        this.message = '创建失败：' + (error.response?.data?.message || '未知错误')
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

.admin-form {
  margin-top: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.form-group input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.form-actions {
  margin-top: 20px;
}

.btn {
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

.success {
  color: green;
  margin-top: 10px;
}

.error {
  color: red;
  margin-top: 10px;
}
</style>
