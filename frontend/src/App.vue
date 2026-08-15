<template>
  <div class="app">
    <!-- 全局飘落花瓣氛围层 -->
    <div class="petals" aria-hidden="true">
      <span
        v-for="n in 9"
        :key="n"
        class="petal"
        :style="{
          left: n * 11 + '%',
          animationDelay: n * 0.7 + 's',
          animationDuration: 14 + n * 2 + 's'
        }"
      >🌸</span>
    </div>

    <nav class="nav">
      <div class="nav-left">
        <router-link to="/" class="brand"><span class="brand-mark">💗</span>校园二手</router-link>
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

    <footer class="footer">💕 校园二手 · 让闲置焕发新生</footer>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const isLogin = ref(false)
const nickname = ref('用户')

function readAuth() {
  isLogin.value = !!localStorage.getItem('token')
  nickname.value = localStorage.getItem('nickname') || '用户'
}
readAuth()

watch(() => route.path, readAuth)

const isAdmin = () => localStorage.getItem('role') === '1'

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('nickname')
  localStorage.removeItem('role')
  location.href = '/login'
}
</script>

<style scoped>
/* 飘落花瓣 */
.petals {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}
.petal {
  position: absolute;
  top: -40px;
  font-size: 18px;
  opacity: 0.45;
  animation: fall linear infinite;
}
@keyframes fall {
  to { transform: translateY(112vh) rotate(340deg); }
}

.nav {
  position: relative;
  z-index: 10;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 13px 26px;
  background: linear-gradient(90deg, #ff9cbd, #ff6b9d);
  box-shadow: 0 2px 16px rgba(255, 107, 157, 0.3);
  position: sticky;
  top: 0;
}
.nav-left { display: flex; align-items: center; gap: 20px; }
.nav-left a, .nav-right a { color: #fff; font-size: 15px; }
.nav-left a:hover, .nav-right a:hover { opacity: 0.85; }
.brand {
  font-weight: bold; font-size: 18px; letter-spacing: 1px;
  display: flex; align-items: center; gap: 6px;
}
.brand-mark {
  width: 26px; height: 26px; border-radius: 50%;
  background: #fff; display: inline-flex; align-items: center; justify-content: center;
  font-size: 14px; box-shadow: 0 2px 8px rgba(214, 51, 108, 0.3);
}
.register-link {
  background: rgba(255, 255, 255, 0.25);
  padding: 6px 18px;
  border-radius: 18px;
}
.nick { color: #fff; margin-right: 12px; }
.logout { opacity: 0.85; }

.main {
  position: relative;
  z-index: 1;
  max-width: 1060px;
  margin: 24px auto;
  padding: 0 16px 40px;
}

.footer {
  position: relative;
  z-index: 1;
  text-align: center;
  color: var(--text-soft);
  padding: 20px 0 30px;
  font-size: 14px;
}
</style>
