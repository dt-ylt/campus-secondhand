<template>
  <div>
    <!-- 梦幻 Hero 横幅 -->
    <div class="hero">
      <span class="hero-deco d1">🌸</span>
      <span class="hero-deco d2">💗</span>
      <span class="hero-deco d3">🎀</span>
      <span class="hero-deco d4">✨</span>
      <h1 class="hero-title">把闲置变成<span class="gradient-text">小惊喜</span></h1>
      <p class="hero-sub">校园二手，让每件旧物都有新故事 🌷</p>
      <div class="hero-search">
        <input v-model="query.keyword" placeholder="🔍 搜一搜你想要的宝贝" @keyup.enter="loadList(1)" />
        <button class="btn-pink" @click="loadList(1)">搜索</button>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <select v-model="query.categoryId" @change="loadList(1)">
        <option value="">全部分类</option>
        <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
      </select>
      <select v-model="query.orderBy" @change="loadList(1)">
        <option value="time">按最新</option>
        <option value="priceAsc">价格从低到高</option>
        <option value="priceDesc">价格从高到低</option>
      </select>
    </div>

    <!-- 商品卡片 -->
    <div class="grid">
      <router-link
        v-for="(p, i) in products"
        :key="p.id"
        :to="'/product/' + p.id"
        class="card"
        :style="{ animationDelay: i * 70 + 'ms' }"
      >
        <div class="img">
          <img v-if="p.images && p.images.length" :src="p.images[0]" alt="商品图" />
          <span v-else class="noimg">暂无图片</span>
          <span v-if="p.conditionLevel" class="badge">{{ conditionText(p.conditionLevel) }}</span>
        </div>
        <div class="info">
          <div class="title">{{ p.title }}</div>
          <div class="price-row">
            <span class="price">¥{{ p.price }}</span>
            <span v-if="p.originalPrice" class="orig">¥{{ p.originalPrice }}</span>
          </div>
          <div class="meta">
            <span>{{ p.categoryName }}</span>
            <span>· {{ p.sellerName }}</span>
            <span>· 👀 {{ p.viewCount }}</span>
          </div>
        </div>
      </router-link>
      <div v-if="!products.length" class="empty">🌸 这里还没有商品，去发布一个吧</div>
    </div>

    <!-- 分页 -->
    <div class="pager">
      <button :disabled="pageNum <= 1" @click="loadList(pageNum - 1)">上一页</button>
      <span>第 {{ pageNum }} / {{ pages }} 页</span>
      <button :disabled="pageNum >= pages" @click="loadList(pageNum + 1)">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import api from '../api'

const categories = ref([])
const products = ref([])
const pageNum = ref(1)
const pages = ref(1)
const query = ref({ categoryId: '', keyword: '', orderBy: 'time' })

const conditionText = (c) => ({ 1: '全新', 2: '几乎全新', 3: '轻微使用', 4: '明显使用' }[c] || '')

onMounted(async () => {
  try {
    categories.value = await api.get('/category/list')
  } catch (e) {}
  loadList(1)
})

async function loadList(page) {
  if (!page) page = 1
  pageNum.value = page
  const params = { pageNum: pageNum.value, pageSize: 8, orderBy: query.value.orderBy || 'time' }
  if (query.value.categoryId) params.categoryId = query.value.categoryId
  if (query.value.keyword) params.keyword = query.value.keyword

  try {
    const data = await api.get('/product/list', { params })
    products.value = data.records || []
    pages.value = data.pages || 1
  } catch (e) {}
}
</script>

<style scoped>
/* ===== Hero 横幅 ===== */
.hero {
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #ffd9e8, #ffb3cd 45%, #ff8fb3);
  border-radius: 22px;
  padding: 46px 24px 42px;
  margin-bottom: 20px;
  text-align: center;
  box-shadow: 0 10px 34px rgba(255, 107, 157, 0.3);
}
.hero-deco {
  position: absolute;
  font-size: 30px;
  opacity: 0.75;
  animation: float 4s ease-in-out infinite;
}
.d1 { top: 16px; left: 7%; }
.d2 { top: 14px; right: 9%; animation-delay: 1s; }
.d3 { bottom: 14px; left: 14%; animation-delay: 2s; }
.d4 { bottom: 18px; right: 16%; animation-delay: 0.5s; }
@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-12px); }
}
.hero-title { font-size: 36px; color: #fff; text-shadow: 0 2px 12px rgba(214, 51, 108, 0.3); }
.gradient-text {
  background: linear-gradient(90deg, #fff, #ffeaf2);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}
.hero-sub { color: rgba(255, 255, 255, 0.92); margin: 10px 0 20px; font-size: 15px; }
.hero-search { display: flex; justify-content: center; gap: 10px; }
.hero-search input {
  width: 360px; max-width: 80%; padding: 12px 20px; border-radius: 24px;
  border: none; font-size: 15px; box-shadow: 0 4px 14px rgba(214, 51, 108, 0.18);
  outline: none;
}
.hero-search .btn-pink { border-radius: 24px; padding: 12px 26px; }

/* ===== 筛选栏 ===== */
.filter-bar {
  display: flex; gap: 10px; align-items: center;
  background: var(--card); padding: 12px 14px; border-radius: 16px; margin-bottom: 18px;
  box-shadow: var(--shadow);
}
.filter-bar select {
  padding: 9px 12px; border: 1px solid var(--border); border-radius: 10px;
  color: var(--text); font-size: 14px; background: #fff;
}
.filter-bar select:focus { outline: none; border-color: var(--pink); }

/* ===== 商品卡片 ===== */
.grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.card {
  display: block; color: var(--text); background: var(--card); border-radius: 16px;
  overflow: hidden; box-shadow: var(--shadow);
  opacity: 0;
  animation: fadeUp 0.5s forwards;
  transition: transform 0.2s, box-shadow 0.25s;
}
@keyframes fadeUp {
  from { opacity: 0; transform: translateY(18px); }
  to { opacity: 1; transform: none; }
}
.card:hover { transform: translateY(-5px); box-shadow: 0 12px 30px rgba(255, 107, 157, 0.28); }
.img {
  position: relative; height: 160px;
  background: linear-gradient(135deg, #fff0f5, #ffe4ec);
  display: flex; align-items: center; justify-content: center;
  overflow: hidden;
}
.img img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.35s; }
.card:hover .img img { transform: scale(1.07); }
.noimg { color: var(--text-soft); }
.badge {
  position: absolute; top: 8px; left: 8px;
  background: rgba(255, 255, 255, 0.88); color: var(--rose);
  font-size: 12px; padding: 3px 11px; border-radius: 12px;
  box-shadow: 0 2px 6px rgba(214, 51, 108, 0.15);
}
.info { padding: 12px; }
.title {
  font-size: 15px; margin-bottom: 7px; color: var(--text);
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}
.price-row { display: flex; align-items: baseline; gap: 8px; margin-bottom: 6px; }
.price { color: var(--rose); font-size: 19px; font-weight: bold; }
.orig { color: #c9a8b4; font-size: 13px; text-decoration: line-through; }
.meta { color: var(--text-soft); font-size: 12px; }

.empty { grid-column: 1/-1; text-align: center; color: var(--text-soft); padding: 50px; }
.pager {
  display: flex; justify-content: center; align-items: center; gap: 16px; margin-top: 24px;
}
.pager button {
  padding: 7px 18px; border: 1px solid var(--border); background: var(--card);
  border-radius: 16px; color: var(--pink-deep);
}
.pager button:disabled { color: #d9b0bf; cursor: not-allowed; }
.pager span { color: var(--text-soft); }
</style>
