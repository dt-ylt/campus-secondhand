<template>
  <div>
    <!-- 迷你 Hero -->
    <div class="mini-hero">
      <h2>💖 我的收藏</h2>
      <p>攒下的心动，都在这里</p>
    </div>

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
.mini-hero {
  text-align: center;
  background: linear-gradient(135deg, #ffd9e8, #ffb3cd);
  border-radius: 18px;
  padding: 30px 20px;
  margin-bottom: 20px;
  box-shadow: 0 8px 26px rgba(255, 107, 157, 0.22);
}
.mini-hero h2 { color: #fff; font-size: 26px; text-shadow: 0 2px 10px rgba(214, 51, 108, 0.3); }
.mini-hero p { color: rgba(255, 255, 255, 0.9); margin-top: 6px; font-size: 14px; }

.grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.card { background: var(--card); border-radius: 16px; overflow: hidden; box-shadow: var(--shadow); transition: transform 0.2s, box-shadow 0.25s; }
.card:hover { transform: translateY(-4px); box-shadow: 0 10px 26px rgba(255, 107, 157, 0.25); }
.img {
  display: flex; align-items: center; justify-content: center; height: 150px;
  background: linear-gradient(135deg, #fff0f5, #ffe4ec); overflow: hidden;
}
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
