<template>
  <div v-if="p" class="detail">
    <div class="imgs">
      <img v-for="(img, i) in p.images" :key="i" :src="img" class="big" />
      <div v-if="!p.images || !p.images.length" class="noimg">暂无图片</div>
    </div>

    <h2>{{ p.title }}</h2>
    <div class="price">
      ¥{{ p.price }}
      <span v-if="p.originalPrice" class="orig">原价 ¥{{ p.originalPrice }}</span>
    </div>
    <div class="meta">分类：{{ p.categoryName }} · 卖家：{{ p.sellerName }} · 浏览{{ p.viewCount }}</div>
    <div class="meta">成色：{{ conditionText(p.conditionLevel) }}<span v-if="p.location"> · 地点：{{ p.location }}</span></div>

    <div v-if="p.description" class="desc-block">
      <h3>商品描述</h3>
      <p>{{ p.description }}</p>
    </div>

    <div class="actions">
      <button v-if="isLogin" @click="favorite">💗 收藏</button>
      <router-link v-else to="/login" class="login-link">登录后可收藏</router-link>
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
.detail { background: var(--card); border-radius: 18px; padding: 26px; max-width: 680px; margin: 0 auto; box-shadow: var(--shadow); }
.imgs { display: flex; gap: 12px; flex-wrap: wrap; margin-bottom: 16px; }
.big { width: 190px; height: 190px; object-fit: cover; border-radius: 14px; }
.noimg { color: var(--text-soft); padding: 40px; border: 1px dashed var(--border); border-radius: 12px; }
h2 { margin-bottom: 8px; color: var(--text); }
.price { color: var(--rose); font-size: 26px; font-weight: bold; margin-bottom: 8px; }
.orig { color: var(--text-soft); font-size: 14px; font-weight: normal; margin-left: 8px; text-decoration: line-through; }
.meta { color: var(--text-soft); margin: 4px 0; }
.desc-block { margin-top: 16px; padding-top: 16px; border-top: 1px solid var(--border); }
.desc-block h3 { margin-bottom: 8px; color: var(--rose); }
.desc-block p { color: var(--text); line-height: 1.6; white-space: pre-wrap; }
.actions { margin-top: 20px; }
.actions button {
  padding: 11px 28px; background: linear-gradient(135deg, #ff8fb3, #ff6b9d); color: #fff;
  border: none; border-radius: 20px; font-size: 15px; box-shadow: 0 4px 12px rgba(255, 107, 157, 0.3);
}
.login-link { color: var(--pink); }
.empty { text-align: center; color: var(--text-soft); padding: 60px; }
</style>
