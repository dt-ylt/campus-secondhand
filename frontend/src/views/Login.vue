<template>
  <div class="auth-card">
    <h2>💗 欢迎回来</h2>
    <p class="sub">登录校园二手，淘点好物</p>
    <form @submit.prevent="handleLogin">
      <label>用户名</label>
      <input v-model="form.username" placeholder="请输入用户名" />
      <label>密码</label>
      <input v-model="form.password" type="password" placeholder="请输入密码" />
      <button type="submit" class="btn-pink">登 录</button>
    </form>
    <p class="foot">还没有账号？<router-link to="/register">去注册</router-link></p>
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
    const data = await api.post('/user/login', form)
    localStorage.setItem('token', data.token)
    localStorage.setItem('nickname', data.nickname || data.username)
    localStorage.setItem('role', String(data.role || 0))
    alert('登录成功')
    router.push('/')
  } catch (e) {}
}
</script>

<style scoped>
.auth-card {
  max-width: 400px;
  margin: 50px auto;
  background: var(--card);
  padding: 36px 32px;
  border-radius: 18px;
  border-top: 5px solid var(--pink);
  box-shadow: var(--shadow);
}
.auth-card h2 { text-align: center; color: var(--rose); font-size: 22px; }
.sub { text-align: center; color: var(--text-soft); margin: 6px 0 18px; }
label { display: block; margin-top: 10px; color: var(--text); font-size: 14px; }
input {
  width: 100%; padding: 11px 14px; margin: 6px 0;
  border: 1px solid var(--border); border-radius: 10px;
  font-size: 15px; color: var(--text);
}
input:focus { outline: none; border-color: var(--pink); box-shadow: 0 0 0 3px rgba(255, 107, 157, 0.15); }
button { width: 100%; margin-top: 16px; }
.foot { text-align: center; margin-top: 16px; color: var(--text-soft); font-size: 14px; }
</style>
