<template>
  <div class="login">
    <div class="container mt-5">
      <div class="row justify-content-center">
        <div class="col-md-6">
          <div class="card">
            <div class="card-header">
              <h3 class="text-center">{{ $t('auth.login') }}</h3>
            </div>
            <div class="card-body">
              <form @submit.prevent="handleLogin">
                <div class="mb-3">
                  <label for="username" class="form-label">{{ $t('auth.username') }}</label>
                  <input type="text" class="form-control" id="username" v-model="form.username" required>
                </div>
                <div class="mb-3">
                  <label for="password" class="form-label">{{ $t('auth.password') }}</label>
                  <input type="password" class="form-control" id="password" v-model="form.password" required>
                </div>
                <button type="submit" class="btn btn-primary w-100">{{ $t('auth.login') }}</button>
              </form>
              <div class="mt-3 text-center">
                <p>{{ $t('auth.noAccount') }} <router-link to="/register">{{ $t('nav.register') }}</router-link></p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import axios from 'axios'

const router = useRouter()
const { t } = useI18n()

const form = ref({
  username: '',
  password: ''
})

const handleLogin = async () => {
  try {
    const response = await axios.post('/api/user/login', {
      username: form.value.username,
      password: form.value.password
    })
    localStorage.setItem('user', JSON.stringify(response.data))
    router.push('/')
  } catch (error) {
    console.error('Login failed:', error)
    alert(t('auth.loginFailed'))
  }
}
</script>

<style scoped>
.login {
  min-height: 100vh;
  display: flex;
  align-items: center;
  padding-top: 40px;
  padding-bottom: 40px;
  background-color: #f5f5f5;
}
</style>