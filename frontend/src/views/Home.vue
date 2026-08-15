<template>
  <div>
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <select v-model="query.categoryId" @change="loadList(1)">
        <option value="">全部分类</option>
        <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
      </select>
      <input v-model="query.keyword" placeholder="搜索商品标题" @keyup.enter="loadList(1)" />
      <button @click="loadList(1)">搜索</button>
      <select v-model="query.orderBy" @change="loadList(1)">
        <option value="time">按最新</option>
        <option value="priceAsc">价格从低到高</option>
        <option value="priceDesc">价格从高到低</option>
      </select>
    </div>

    <!-- 商品卡片列表 -->
    <div class="grid">
      <div v-for="p in products" :key="p.id" class="card">
        <div class="img">
          <img v-if="p.images && p.images.length" :src="p.images[0]" alt="商品图" />
          <span v-else class="noimg">暂无图片</span>
        </div>
        <div class="info">
          <div class="title">{{ p.title }}</div>
          <div class="price">¥{{ p.price }}</div>
          <div class="meta">{{ p.categoryName }} · {{ p.sellerName }} · 浏览{{ p.viewCount }}</div>
        </div>
      </div>
      <div v-if="!products.length" class="empty">暂无商品</div>
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

const categories = ref([])   // 分类下拉
const products = ref([])      // 商品列表
const pageNum = ref(1)
const pages = ref(1)
const query = ref({ categoryId: '', keyword: '', orderBy: 'time' })

onMounted(async () => {
  // 进入页面先加载分类（给筛选下拉用），再加载商品
  try {
    categories.value = await api.get('/category/list')
  } catch (e) {}
  loadList(1)
})

// 加载商品列表（page 是页码）
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
  background: #fff; padding: 12px; border-radius: 8px; margin-bottom: 16px;
}
.filter-bar select, .filter-bar input { padding: 8px; border: 1px solid #dcdfe6; border-radius: 4px; }
.filter-bar input { flex: 1; }
.filter-bar button { padding: 8px 20px; background: #409eff; color: #fff; border: none; border-radius: 4px; }

.grid {
  display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px;
}
.card {
  background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 6px rgba(0,0,0,.06);
}
.img { height: 160px; background: #f0f2f5; display: flex; align-items: center; justify-content: center; }
.img img { width: 100%; height: 100%; object-fit: cover; }
.noimg { color: #c0c4cc; }
.info { padding: 12px; }
.title { font-size: 15px; margin-bottom: 6px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.price { color: #f56c6c; font-size: 18px; font-weight: bold; margin-bottom: 6px; }
.meta { color: #909399; font-size: 12px; }
.empty { grid-column: 1/-1; text-align: center; color: #909399; padding: 40px; }
.pager {
  display: flex; justify-content: center; align-items: center; gap: 16px; margin-top: 20px;
}
.pager button { padding: 6px 16px; border: 1px solid #dcdfe6; background: #fff; border-radius: 4px; }
.pager button:disabled { color: #c0c4cc; cursor: not-allowed; }
</style>
