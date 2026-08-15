<template>
  <div>
    <h2>🌸 管理后台</h2>

    <div v-if="!isAdmin()" class="empty">你不是管理员，无法访问管理后台</div>

    <div v-else>
      <div class="tabs">
        <button :class="{ active: current === 0 }" @click="load(0)">待审核</button>
        <button :class="{ active: current === 1 }" @click="load(1)">已上架</button>
        <button :class="{ active: current === 2 }" @click="load(2)">已下架</button>
        <button :class="{ active: current === '' }" @click="load('')">全部</button>
      </div>

      <table class="tbl">
        <thead>
          <tr><th>ID</th><th>图片</th><th>标题</th><th>价格</th><th>分类</th><th>卖家</th><th>状态</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-for="p in products" :key="p.id">
            <td>{{ p.id }}</td>
            <td class="img-cell">
              <img v-if="p.images && p.images.length" :src="p.images[0]" class="thumb" />
              <span v-else class="gray">无图</span>
            </td>
            <td>
              {{ p.title }}
              <div v-if="p.description" class="desc">{{ p.description }}</div>
            </td>
            <td>¥{{ p.price }}</td>
            <td>{{ p.categoryName || '-' }}</td>
            <td>{{ p.sellerName || '-' }}</td>
            <td>{{ statusText(p.status) }}</td>
            <td>
              <router-link :to="'/product/' + p.id" class="view">查看</router-link>
              <button v-if="p.status !== 1" class="ok" @click="audit(p.id, 1)">上架</button>
              <button v-if="p.status !== 2" class="warn" @click="audit(p.id, 2)">下架</button>
            </td>
          </tr>
          <tr v-if="!products.length"><td colspan="8" class="empty">暂无数据</td></tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import api from '../api'

const products = ref([])
const current = ref(0)

const isAdmin = () => localStorage.getItem('role') === '1'

const statusText = (s) => ({ 0: '待审核', 1: '已上架', 2: '已下架', 3: '已售出' }[s] || s)

async function load(status) {
  current.value = status
  const params = { pageNum: 1, pageSize: 20 }
  if (status !== '') params.status = status
  try {
    const data = await api.get('/admin/product/list', { params })
    products.value = data.records || []
  } catch (e) {}
}

async function audit(id, status) {
  try {
    await api.put('/admin/product/audit', null, { params: { productId: id, status } })
    alert(status === 1 ? '已上架' : '已下架')
    load(current.value)
  } catch (e) {}
}

load(0)
</script>

<style scoped>
h2 { margin-bottom: 16px; color: var(--rose); }
.tabs { display: flex; gap: 8px; margin-bottom: 12px; }
.tabs button {
  padding: 7px 16px; border: 1px solid var(--border); background: var(--card);
  border-radius: 16px; color: var(--pink-deep);
}
.tabs button.active { background: linear-gradient(135deg, #ff8fb3, #ff6b9d); color: #fff; border-color: transparent; }
.tbl { width: 100%; border-collapse: collapse; background: var(--card); border-radius: 12px; overflow: hidden; box-shadow: var(--shadow); }
.tbl th, .tbl td { border: 1px solid #ffe4ec; padding: 10px 12px; text-align: left; vertical-align: top; }
.tbl th { background: linear-gradient(90deg, #fff0f5, #ffe4ec); color: var(--rose); }
.img-cell { width: 80px; }
.thumb { width: 60px; height: 60px; object-fit: cover; border-radius: 8px; }
.desc { color: var(--text-soft); font-size: 12px; margin-top: 4px; max-width: 220px; }
.gray { color: #d9b0bf; font-size: 12px; }
.view { margin-right: 8px; }
.ok {
  padding: 4px 12px; background: linear-gradient(135deg, #ff8fb3, #ff6b9d); color: #fff;
  border: none; border-radius: 12px; margin-right: 6px;
}
.warn { padding: 4px 12px; background: #ffb37a; color: #fff; border: none; border-radius: 12px; }
.empty { text-align: center; color: var(--text-soft); padding: 20px; }
</style>
