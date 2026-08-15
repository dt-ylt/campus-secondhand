<template>
  <div class="app">
    <nav class="nav">
      <div class="nav-left">
        <router-link to="/" class="brand">💗 校园二手</router-link>
        <router-link to="/">首页</router-link>
        <router-link to="/publish">发布闲置</router-link>
        <router-link to="/favorites">我的收藏</router-link>
        <router-link v-if="isAdmin()" to="/admin">管理后台</router-link>
      </div>
      <div class="nav-right">
        <template v-if="isLogin">
          <span class="nick">🌸 {{ nickname }}</span>
          <a href="#" @click.prevent="logout" class="logout">退出</a>
        </template>
        <template v-else>
          <router-link to="/login">登录</router-link>
          <router-link to="/register" class="register-link">注册</router-link>
        </template>
      </div>
    </nav>

    <main class="main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const isLogin = ref(false)
const nickname = ref('用户')

// 从 localStorage 读取登录状态（登录/退出时存的值）
function readAuth() {
  isLogin.value = !!localStorage.getItem('token')
  nickname.value = localStorage.getItem('nickname') || '用户'
}
readAuth()

// 每次切换路由时重新读一次，登录/退出后导航栏立刻更新（不用手动刷新）
watch(() => route.path, readAuth)

// 当前用户是否为管理员（登录时存了 role，1=管理员）
const isAdmin = () => localStorage.getItem('role') === '1'

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('nickname')
  localStorage.removeItem('role')
  location.href = '/login'
}
</script>

<style scoped>
.nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 26px;
  background: linear-gradient(90deg, #ff8fb3, #ff6b9d);
  box-shadow: 0 2px 14px rgba(255, 107, 157, 0.28);
  position: sticky;
  top: 0;
  z-index: 10;
}
.nav-left { display: flex; align-items: center; gap: 20px; }
.nav-left a, .nav-right a { color: #fff; font-size: 15px; }
.nav-left a:hover, .nav-right a:hover { opacity: 0.85; }
.brand { font-weight: bold; font-size: 18px; letter-spacing: 1px; }
.register-link {
  background: rgba(255, 255, 255, 0.22);
  padding: 6px 16px;
  border-radius: 16px;
}
.nick { color: #fff; margin-right: 12px; }
.logout { opacity: 0.85; }
.main { max-width: 1040px; margin: 24px auto; padding: 0 16px; }
</style>
