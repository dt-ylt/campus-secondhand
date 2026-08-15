<template>
  <div class="app">
    <nav class="nav">
      <div class="nav-left">
        <router-link to="/" class="brand">🏫 校园二手</router-link>
        <router-link to="/">首页</router-link>
        <router-link to="/publish">发布闲置</router-link>
        <router-link to="/favorites">我的收藏</router-link>
      </div>
      <div class="nav-right">
        <template v-if="isLogin">
          <span class="nick">👤 {{ nickname }}</span>
          <a href="#" @click.prevent="logout">退出</a>
        </template>
        <template v-else>
          <router-link to="/login">登录</router-link>
          <router-link to="/register">注册</router-link>
        </template>
      </div>
    </nav>

    <main class="main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 是否登录、昵称，都从 localStorage 读（登录成功时存的）
const isLogin = ref(!!localStorage.getItem('token'))
const nickname = ref(localStorage.getItem('nickname') || '用户')

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('nickname')
  location.href = '/login'
}
</script>

<style scoped>
.nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  padding: 12px 24px;
  border-bottom: 1px solid #e4e7ed;
}
.nav-left a {
  margin-right: 18px;
}
.brand {
  font-weight: bold;
  color: #333;
}
.nick {
  margin-right: 12px;
  color: #606266;
}
.main {
  max-width: 1000px;
  margin: 24px auto;
  padding: 0 16px;
}
</style>
