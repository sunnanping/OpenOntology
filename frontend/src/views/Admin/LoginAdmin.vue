<template>
  <div class="admin-container">
    <h1>管理员登录</h1>
    <form @submit.prevent="login">
      <div class="form-group">
        <label for="username">用户名</label>
        <input type="text" id="username" v-model="loginForm.username" required>
      </div>
      <div class="form-group">
        <label for="password">密码</label>
        <input type="password" id="password" v-model="loginForm.password" required>
      </div>
      <button type="submit" class="btn btn-primary">登录</button>
    </form>
    <div v-if="message" :class="messageType">{{ message }}</div>
  </div>
</template>

<script>
export default {
  name: 'LoginAdmin',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      message: '',
      messageType: ''
    }
  },
  methods: {
    async login() {
      try {
        const response = await this.$axios.post(`/api/admin/login?username=${this.loginForm.username}&password=${this.loginForm.password}`)
        localStorage.setItem('adminToken', response.data.token || 'admin')
        localStorage.setItem('adminUser', JSON.stringify(response.data))
        this.message = '登录成功！'
        this.messageType = 'success'
        this.$router.push('/admin/list')
      } catch (error) {
        this.message = '登录失败：' + (error.response?.data?.message || '用户名或密码错误')
        this.messageType = 'error'
      }
    }
  }
}
</script>

<style scoped>
.admin-container {
  max-width: 400px;
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
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #0069d9;
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