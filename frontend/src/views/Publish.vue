<template>
  <div class="form-card">
    <h2>🌸 发布闲置</h2>
    <form @submit.prevent="submit">
      <label>标题 *</label>
      <input v-model="form.title" placeholder="如：九成新机械键盘" />

      <label>描述</label>
      <textarea v-model="form.description" rows="4" placeholder="描述一下成色、使用情况等"></textarea>

      <label>价格（元）*</label>
      <input v-model="form.price" type="number" step="0.01" placeholder="如 129" />

      <label>原价（元）</label>
      <input v-model="form.originalPrice" type="number" step="0.01" placeholder="选填" />

      <label>分类 *</label>
      <select v-model="form.categoryId">
        <option value="" disabled>请选择分类</option>
        <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
      </select>

      <label>成色</label>
      <select v-model="form.conditionLevel">
        <option value="1">全新</option>
        <option value="2">几乎全新</option>
        <option value="3">轻微使用痕迹</option>
        <option value="4">明显使用痕迹</option>
      </select>

      <label>交易地点</label>
      <input v-model="form.location" placeholder="如：东校区" />

      <label>商品图片（可传多张，第一张是封面）</label>
      <input type="file" accept="image/*" @change="uploadImage" />
      <div class="imgs">
        <span v-for="(img, i) in form.images" :key="i" class="img-item">
          <img :src="img" />
          <a href="#" @click.prevent="form.images.splice(i, 1)">✕</a>
        </span>
      </div>

      <button type="submit" class="btn-pink" :disabled="uploading">{{ uploading ? '上传中...' : '发 布' }}</button>
    </form>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'

const router = useRouter()
const categories = ref([])
const uploading = ref(false)
const form = reactive({
  title: '', description: '', price: '', originalPrice: '',
  categoryId: '', conditionLevel: '1', location: '', images: [],
})

onMounted(async () => {
  try { categories.value = await api.get('/category/list') } catch (e) {}
})

async function uploadImage(e) {
  const file = e.target.files[0]
  if (!file) return
  uploading.value = true
  try {
    const fd = new FormData()
    fd.append('file', file)
    const url = await api.post('/file/upload', fd)
    form.images.push(url)
    alert('图片上传成功')
  } catch (err) {
  } finally {
    uploading.value = false
    e.target.value = ''
  }
}

async function submit() {
  if (!form.title || !form.price || !form.categoryId) {
    alert('标题、价格、分类必填')
    return
  }
  try {
    await api.post('/product/publish', {
      title: form.title,
      description: form.description,
      price: Number(form.price),
      originalPrice: form.originalPrice ? Number(form.originalPrice) : null,
      categoryId: Number(form.categoryId),
      conditionLevel: Number(form.conditionLevel),
      location: form.location,
      images: form.images,
    })
    alert('发布成功，等待管理员审核')
    router.push('/')
  } catch (e) {}
}
</script>

<style scoped>
.form-card {
  max-width: 540px; margin: 20px auto; background: var(--card);
  padding: 32px; border-radius: 18px; box-shadow: var(--shadow);
  border-top: 5px solid var(--pink);
}
.form-card h2 { text-align: center; color: var(--rose); margin-bottom: 14px; }
label { display: block; margin-top: 12px; color: var(--text); font-size: 14px; }
input, select, textarea {
  width: 100%; padding: 10px 12px; margin: 5px 0;
  border: 1px solid var(--border); border-radius: 10px; color: var(--text); font-size: 14px;
}
input:focus, select:focus, textarea:focus {
  outline: none; border-color: var(--pink); box-shadow: 0 0 0 3px rgba(255, 107, 157, 0.12);
}
button { width: 100%; margin-top: 18px; }
.imgs { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 8px; }
.img-item { position: relative; }
.img-item img { width: 76px; height: 76px; object-fit: cover; border-radius: 10px; }
.img-item a {
  position: absolute; top: -6px; right: -6px; background: var(--rose); color: #fff;
  width: 20px; height: 20px; border-radius: 50%; text-align: center; line-height: 20px; font-size: 12px;
}
</style>
