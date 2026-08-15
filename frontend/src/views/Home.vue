<template>
  <div>
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <select v-model="query.categoryId" @change="loadList(1)">
        <option value="">全部分类</option>
        <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
      </select>
      <input v-model="query.keyword" placeholder="🔍 搜索商品标题" @keyup.enter="loadList(1)" />
      <button class="btn-pink" @click="loadList(1)">搜索</button>
      <select v-model="query.orderBy" @change="loadList(1)">
        <option value="time">按最新</option>
        <option value="priceAsc">价格从低到高</option>
        <option value="priceDesc">价格从高到低</option>
      </select>
    </div>

    <!-- 商品卡片列表 -->
    <div class="grid">
      <router-link v-for="p in products" :key="p.id" :to="'/product/' + p.id" class="card">
        <div class="img">
          <img v-if="p.images && p.images.length" :src="p.images[0]" alt="商品图" />
          <span v-else class="noimg">暂无图片</span>
        </div>
        <div class="info">
          <div class="title">{{ p.title }}</div>
          <div class="price">¥{{ p.price }}</div>
          <div class="meta">{{ p.categoryName }} · {{ p.sellerName }} · 浏览{{ p.viewCount }}</div>
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
.filter-bar {
  display: flex; gap: 8px; align-items: center;
  background: var(--card); padding: 12px; border-radius: 14px; margin-bottom: 18px;
  box-shadow: var(--shadow);
}
.filter-bar select, .filter-bar input {
  padding: 9px 12px; border: 1px solid var(--border); border-radius: 10px;
  color: var(--text); font-size: 14px; background: #fff;
}
.filter-bar select:focus, .filter-bar input:focus {
  outline: none; border-color: var(--pink); box-shadow: 0 0 0 3px rgba(255, 107, 157, 0.12);
}
.filter-bar input { flex: 1; }

.grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.card {
  display: block; color: var(--text); background: var(--card); border-radius: 14px;
  overflow: hidden; box-shadow: var(--shadow);
  transition: transform 0.2s, box-shadow 0.25s;
}
.card:hover { transform: translateY(-4px); box-shadow: 0 10px 26px rgba(255, 107, 157, 0.25); }
.img { height: 160px; background: linear-gradient(135deg, #fff0f5, #ffe4ec); display: flex; align-items: center; justify-content: center; }
.img img { width: 100%; height: 100%; object-fit: cover; }
.noimg { color: var(--text-soft); }
.info { padding: 12px; }
.title { font-size: 15px; margin-bottom: 6px; color: var(--text); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.price { color: var(--rose); font-size: 18px; font-weight: bold; margin-bottom: 6px; }
.meta { color: var(--text-soft); font-size: 12px; }
.empty { grid-column: 1/-1; text-align: center; color: var(--text-soft); padding: 50px; }
.pager {
  display: flex; justify-content: center; align-items: center; gap: 16px; margin-top: 22px;
}
.pager button {
  padding: 7px 18px; border: 1px solid var(--border); background: var(--card);
  border-radius: 16px; color: var(--pink-deep);
}
.pager button:disabled { color: #d9b0bf; cursor: not-allowed; }
.pager span { color: var(--text-soft); }
</style>
