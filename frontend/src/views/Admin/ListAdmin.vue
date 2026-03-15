<template>
  <div class="admin-container">
    <h1>管理员列表</h1>
    <button class="btn btn-primary" @click="$router.push('/admin/create')">创建管理员</button>
    <table class="admin-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>用户名</th>
          <th>邮箱</th>
          <th>名字</th>
          <th>姓氏</th>
          <th>部门</th>
          <th>电话</th>
          <th>创建时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="admin in admins" :key="admin.id">
          <td>{{ admin.id }}</td>
          <td>{{ admin.username }}</td>
          <td>{{ admin.email }}</td>
          <td>{{ admin.firstName }}</td>
          <td>{{ admin.lastName }}</td>
          <td>{{ admin.department }}</td>
          <td>{{ admin.phone }}</td>
          <td>{{ formatDate(admin.createdDate) }}</td>
          <td>
            <button class="btn btn-sm btn-info" @click="editAdmin(admin)">编辑</button>
            <button class="btn btn-sm btn-danger" @click="deleteAdmin(admin.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-if="admins.length === 0" class="no-data">暂无管理员数据</div>
    <div v-if="message" :class="messageType">{{ message }}</div>
  </div>
</template>

<script>
export default {
  name: 'ListAdmin',
  data() {
    return {
      admins: [],
      message: '',
      messageType: ''
    }
  },
  mounted() {
    this.fetchAdmins()
  },
  methods: {
    async fetchAdmins() {
      try {
        const response = await this.$axios.get('/api/admin/findAll')
        this.admins = response.data
      } catch (error) {
        this.message = '获取管理员列表失败：' + (error.response?.data?.message || '未知错误')
        this.messageType = 'error'
      }
    },
    editAdmin(admin) {
      this.$router.push({ path: '/admin/update', query: { id: admin.id } })
    },
    async deleteAdmin(id) {
      if (confirm('确定要删除这个管理员吗？')) {
        try {
          await this.$axios.delete(`/api/admin/delete/${id}`)
          this.message = '删除成功！'
          this.messageType = 'success'
          this.fetchAdmins()
        } catch (error) {
          this.message = '删除失败：' + (error.response?.data?.message || '未知错误')
          this.messageType = 'error'
        }
      }
    },
    formatDate(dateString) {
      if (!dateString) return ''
      return new Date(dateString).toLocaleString()
    }
  }
}
</script>

<style scoped>
.admin-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.admin-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 20px;
}

.admin-table th,
.admin-table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

.admin-table th {
  background-color: #f2f2f2;
  font-weight: bold;
}

.btn {
  padding: 5px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 5px;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-info {
  background-color: #17a2b8;
  color: white;
}

.btn-danger {
  background-color: #dc3545;
  color: white;
}

.no-data {
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