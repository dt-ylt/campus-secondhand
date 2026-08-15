<template>
  <div class="auth-card">
    <h2>登录</h2>
    <form @submit.prevent="handleLogin">
      <label>用户名</label>
      <input v-model="form.username" placeholder="请输入用户名" />
      <label>密码</label>
      <input v-model="form.password" type="password" placeholder="请输入密码" />
      <button type="submit">登 录</button>
    </form>
    <p>还没有账号？<router-link to="/register">去注册</router-link></p>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'

const router = useRouter()
const form = reactive({ username: '', password: '' })

async function handleLogin() {
  if (!form.username || !form.password) {
    alert('请填写用户名和密码')
    return
  }
  try {
    // 调后端 /user/login（拦截器会带上 token、统一处理错误）
    const data = await api.post('/user/login', form)
    // 成功：把 token 和昵称存进 localStorage（刷新页面不掉登录态）
    localStorage.setItem('token', data.token)
    localStorage.setItem('nickname', data.nickname || data.username)
    alert('登录成功')
    router.push('/')   // 跳首页
  } catch (e) {
    // 错误提示已由响应拦截器统一 alert，这里不用重复处理
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
  background: #409eff; color: #fff;
  border: none; border-radius: 4px; font-size: 16px;
}
p { text-align: center; margin-top: 16px; }
</style>
