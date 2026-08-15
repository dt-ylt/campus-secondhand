import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Publish from '../views/Publish.vue'
import Favorites from '../views/Favorites.vue'

// 路由表：路径 -> 页面组件
const routes = [
  { path: '/', component: Home },          // 首页：商品列表
  { path: '/login', component: Login },    // 登录
  { path: '/register', component: Register }, // 注册
  { path: '/publish', component: Publish },   // 发布闲置
  { path: '/favorites', component: Favorites }, // 我的收藏
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router
