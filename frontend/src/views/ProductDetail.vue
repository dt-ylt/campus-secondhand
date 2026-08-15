<template>
  <div v-if="p" class="detail">
    <div class="top">
      <!-- 图片画廊 -->
      <div class="gallery">
        <img v-for="(img, i) in p.images" :key="i" :src="img" class="big" />
        <div v-if="!p.images || !p.images.length" class="noimg">暂无图片</div>
      </div>

      <!-- 信息侧栏 -->
      <div class="side">
        <h2>{{ p.title }}</h2>
        <div class="price">
          ¥{{ p.price }}
          <span v-if="p.originalPrice" class="orig">¥{{ p.originalPrice }}</span>
        </div>
        <div class="tags">
          <span class="tag">{{ p.categoryName }}</span>
          <span class="tag">{{ conditionText(p.conditionLevel) }}</span>
          <span v-if="p.location" class="tag">📍 {{ p.location }}</span>
        </div>
        <div class="meta">卖家：{{ p.sellerName }} · 浏览 {{ p.viewCount }}</div>
        <div class="actions">
          <button v-if="isLogin" @click="favorite">💗 收藏</button>
          <router-link v-else to="/login" class="login-link">登录后可收藏</router-link>
        </div>
      </div>
    </div>

    <div v-if="p.description" class="desc-block">
      <h3>💌 商品描述</h3>
      <p>{{ p.description }}</p>
    </div>
  </div>

  <div v-else class="empty">加载中…或商品不存在</div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import api from '../api'

const route = useRoute()
const p = ref(null)
const isLogin = !!localStorage.getItem('token')

const conditionText = (c) => ({ 1: '全新', 2: '几乎全新', 3: '轻微使用痕迹', 4: '明显使用痕迹' }[c] || '未知')

onMounted(async () => {
  try {
    p.value = await api.get('/product/' + route.params.id)
  } catch (e) {}
})

async function favorite() {
  try {
    await api.post('/favorite/' + p.value.id)
    alert('收藏成功，可在"我的收藏"查看')
  } catch (e) {}
}
</script>

<style scoped>
.detail {
  background: var(--card); border-radius: 20px; padding: 26px; max-width: 760px;
  margin: 0 auto; box-shadow: var(--shadow);
}
.top { display: flex; gap: 24px; flex-wrap: wrap; }
.gallery { flex: 1 1 300px; display: flex; gap: 10px; flex-wrap: wrap; align-content: flex-start; }
.big { width: 150px; height: 150px; object-fit: cover; border-radius: 14px; }
.noimg { color: var(--text-soft); padding: 40px; border: 1px dashed var(--border); border-radius: 12px; }
.side { flex: 1 1 260px; }
.side h2 { margin-bottom: 10px; color: var(--text); font-size: 22px; }
.price { color: var(--rose); font-size: 30px; font-weight: bold; margin-bottom: 12px; }
.orig { color: var(--text-soft); font-size: 15px; font-weight: normal; margin-left: 6px; text-decoration: line-through; }
.tags { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 10px; }
.tag {
  background: #fff0f5; color: var(--pink-deep); font-size: 13px;
  padding: 4px 12px; border-radius: 14px; border: 1px solid var(--border);
}
.meta { color: var(--text-soft); margin-bottom: 16px; }
.actions button {
  padding: 12px 30px; background: linear-gradient(135deg, #ff8fb3, #ff6b9d); color: #fff;
  border: none; border-radius: 22px; font-size: 15px;
  box-shadow: 0 5px 14px rgba(255, 107, 157, 0.32); transition: transform 0.15s, box-shadow 0.2s;
}
.actions button:hover { transform: translateY(-2px); box-shadow: 0 7px 20px rgba(255, 107, 157, 0.42); }
.login-link { color: var(--pink); }
.desc-block { margin-top: 22px; padding-top: 18px; border-top: 1px solid var(--border); }
.desc-block h3 { margin-bottom: 8px; color: var(--rose); }
.desc-block p { color: var(--text); line-height: 1.7; white-space: pre-wrap; }
.empty { text-align: center; color: var(--text-soft); padding: 60px; }
</style>
