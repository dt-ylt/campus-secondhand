import axios from 'axios'

// 创建 axios 实例：baseURL=/api，开发时由 Vite 代理转发到后端 8080
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 请求拦截器：每次请求自动带上 token（JWT 通行证）
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = 'Bearer ' + token
  }
  return config
})

// 响应拦截器：统一处理后端返回的 {code, message, data}
api.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res.data          // 成功：直接返回 data，页面里用起来更爽
    }
    // 业务失败：弹提示；401 未登录则跳登录页
    alert(res.message || '操作失败')
    if (res.code === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('nickname')
      window.location.href = '/login'
    }
    return Promise.reject(new Error(res.message))
  },
  (error) => {
    alert('网络错误：' + error.message)
    return Promise.reject(error)
  }
)

export default api
