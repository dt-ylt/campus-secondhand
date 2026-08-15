<template>
  <div class="auth-card">
    <h2>注册</h2>
    <form @submit.prevent="handleRegister">
      <label>用户名</label>
      <input v-model="form.username" placeholder="3-20位字符" />
      <label>密码</label>
      <input v-model="form.password" type="password" placeholder="6-20位字符" />
      <label>昵称（选填）</label>
      <input v-model="form.nickname" placeholder="不填默认用用户名" />
      <button type="submit">注 册</button>
    </form>
    <p>已有账号？<router-link to="/login">去登录</router-link></p>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'

const router = useRouter()
const form = reactive({ username: '', password: '', nickname: '' })

async function handleRegister() {
  if (!form.username || !form.password) {
    alert('请填写用户名和密码')
    return
  }
  try {
    await api.post('/user/register', form)
    alert('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    // 错误提示已由拦截器处理
  }
}
</script>

<style scoped>
.auth-card {
  max-width: 380px;
  margin: 60px auto;
  background: #fff;
  padding: 32px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}
.auth-card h2 { text-align: center; margin-bottom: 20px; }
label { display: block; margin-top: 8px; color: #606266; }
input {
  width: 100%; padding: 10px; margin: 6px 0;
  border: 1px solid #dcdfe6; border-radius: 4px;
}
button {
  width: 100%; padding: 10px; margin-top: 12px;
  background: #67c23a; color: #fff;
  border: none; border-radius: 4px; font-size: 16px;
}
p { text-align: center; margin-top: 16px; }
</style>
