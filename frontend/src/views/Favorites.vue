<template>
  <div>
    <h2>我的收藏</h2>
    <div class="grid">
      <div v-for="p in products" :key="p.id" class="card">
        <div class="img">
          <img v-if="p.images && p.images.length" :src="p.images[0]" alt="商品图" />
          <span v-else class="noimg">暂无图片</span>
        </div>
        <div class="info">
          <div class="title">{{ p.title }}</div>
          <div class="price">¥{{ p.price }}</div>
          <div class="meta">{{ p.categoryName }} · {{ p.sellerName }}</div>
          <button @click="unfavorite(p.id)">取消收藏</button>
        </div>
      </div>
      <div v-if="!products.length" class="empty">还没有收藏的商品</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api'

const products = ref([])

onMounted(loadFavorites)

// 加载我的收藏（接口返回商品信息列表）
async function loadFavorites() {
  try {
    products.value = await api.get('/favorite/my')
  } catch (e) {}
}

// 取消收藏：调 DELETE 接口后重新加载
async function unfavorite(id) {
  try {
    await api.delete('/favorite/' + id)
    alert('已取消收藏')
    loadFavorites()
  } catch (e) {}
}
</script>

<style scoped>
h2 { margin-bottom: 16px; }
.grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.card { background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 6px rgba(0,0,0,.06); }
.img { height: 150px; background: #f0f2f5; display: flex; align-items: center; justify-content: center; }
.img img { width: 100%; height: 100%; object-fit: cover; }
.noimg { color: #c0c4cc; }
.info { padding: 12px; }
.title { font-size: 14px; margin-bottom: 6px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.price { color: #f56c6c; font-size: 17px; font-weight: bold; margin-bottom: 6px; }
.meta { color: #909399; font-size: 12px; margin-bottom: 8px; }
.info button {
  width: 100%; padding: 6px; background: #f56c6c; color: #fff; border: none; border-radius: 4px;
}
.empty { grid-column: 1/-1; text-align: center; color: #909399; padding: 40px; }
</style>
