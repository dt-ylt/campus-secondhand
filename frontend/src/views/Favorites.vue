<template>
  <div>
    <h2>💖 我的收藏</h2>
    <div class="grid">
      <div v-for="p in products" :key="p.id" class="card">
        <router-link :to="'/product/' + p.id" class="img">
          <img v-if="p.images && p.images.length" :src="p.images[0]" alt="商品图" />
          <span v-else class="noimg">暂无图片</span>
        </router-link>
        <div class="info">
          <div class="title">{{ p.title }}</div>
          <div class="price">¥{{ p.price }}</div>
          <div class="meta">{{ p.categoryName }} · {{ p.sellerName }}</div>
          <button @click="unfavorite(p.id)">取消收藏</button>
        </div>
      </div>
      <div v-if="!products.length" class="empty">🌸 还没有收藏，去首页逛逛吧</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api'

const products = ref([])

onMounted(loadFavorites)

async function loadFavorites() {
  try {
    products.value = await api.get('/favorite/my')
  } catch (e) {}
}

async function unfavorite(id) {
  try {
    await api.delete('/favorite/' + id)
    alert('已取消收藏')
    loadFavorites()
  } catch (e) {}
}
</script>

<style scoped>
h2 { margin-bottom: 16px; color: var(--rose); }
.grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.card { background: var(--card); border-radius: 14px; overflow: hidden; box-shadow: var(--shadow); }
.img { display: block; height: 150px; background: linear-gradient(135deg, #fff0f5, #ffe4ec); align-items: center; justify-content: center; }
.img img { width: 100%; height: 100%; object-fit: cover; }
.noimg { color: var(--text-soft); }
.info { padding: 12px; }
.title { font-size: 14px; margin-bottom: 6px; color: var(--text); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.price { color: var(--rose); font-size: 17px; font-weight: bold; margin-bottom: 6px; }
.meta { color: var(--text-soft); font-size: 12px; margin-bottom: 8px; }
.info button {
  width: 100%; padding: 6px; background: linear-gradient(135deg, #ff8fb3, #ff6b9d);
  color: #fff; border: none; border-radius: 16px;
}
.empty { grid-column: 1/-1; text-align: center; color: var(--text-soft); padding: 50px; }
</style>
