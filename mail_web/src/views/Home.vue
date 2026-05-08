<template>
  <div class="home page-container">
    <!-- Hero 搜索区 -->
    <div class="hero">
      <h1 class="hero-title">宸购优选</h1>
      <p class="hero-sub">品质好物，尽在宸购</p>
      <div class="hero-search">
        <el-input
          v-model="keyword"
          placeholder="搜索商品名称或品牌..."
          clearable
          size="large"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #append>
            <el-button @click="handleSearch" type="primary">搜索</el-button>
          </template>
        </el-input>
      </div>
    </div>

    <el-row :gutter="24">
      <!-- 品牌侧边栏 -->
      <el-col :xs="24" :sm="6">
        <el-card class="enhanced-card sidebar-card">
          <template #header>
            <div class="card-header">
              <el-icon color="#409eff"><Goods /></el-icon>
              <span>品牌列表</span>
            </div>
          </template>
          <div class="brand-list">
            <div
              v-for="brand in brands"
              :key="brand.id"
              class="brand-item"
              :class="{ 'brand-item--active': keyword === brand.name }"
              @click="searchByBrand(brand.name)"
            >
              <span class="brand-dot" :style="{ background: brandColors[brand.id % brandColors.length] }"></span>
              {{ brand.name }}
            </div>
            <el-empty v-if="brands.length === 0" description="暂无品牌" :image-size="60" />
          </div>
        </el-card>
      </el-col>

      <!-- 商品列表 -->
      <el-col :xs="24" :sm="18">
        <el-card class="enhanced-card">
          <template #header>
            <div class="card-header">
              <el-icon color="#409eff"><ShoppingBag /></el-icon>
              <span>{{ keyword ? `"${keyword}" 的搜索结果` : '推荐商品' }}</span>
            </div>
          </template>

          <el-row :gutter="16">
            <el-col
              v-for="spu in spuList"
              :key="spu.id"
              :xs="24" :sm="12" :md="8"
              style="margin-bottom: 16px"
            >
              <el-card shadow="hover" class="enhanced-card goods-card" @click="addToCart(spu)">
                <div class="goods-image">
                  <el-image
                    :src="spu.images ? spu.images.split(',')[0] : ''"
                    fit="cover"
                    style="width: 100%; height: 160px"
                  >
                    <template #error>
                      <div class="image-placeholder">
                        <el-icon :size="40"><Goods /></el-icon>
                      </div>
                    </template>
                  </el-image>
                </div>
                <div class="goods-info">
                  <div class="goods-name">{{ spu.name }}</div>
                  <div class="goods-tags">
                    <el-tag size="small" type="warning" effect="plain" v-if="spu.brandName">
                      {{ spu.brandName }}
                    </el-tag>
                  </div>
                  <div class="goods-footer">
                    <span class="goods-price">联系客服询价</span>
                    <el-button type="primary" size="small" circle @click.stop="addToCart(spu)">
                      <el-icon><Plus /></el-icon>
                    </el-button>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <el-empty v-if="spuList.length === 0" description="暂无商品" :image-size="80" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { queryBrand, querySpu } from '../api/goods'
import { addCart } from '../api/cart'

const brandColors = ['#409eff', '#67c23a', '#e6a23c', '#e74c3c', '#9b59b6', '#1abc9c']
const keyword = ref('')
const brands = ref([])
const spuList = ref([])

onMounted(() => {
  loadBrands()
  loadSpus()
})

async function loadBrands() {
  try {
    const res = await queryBrand('')
    if (res.code === 20000) brands.value = res.data || []
  } catch (_) { /* ignore */ }
}

async function loadSpus(name) {
  try {
    const res = await querySpu(name || '')
    if (res.code === 20000) spuList.value = res.data || []
  } catch (_) { /* ignore */ }
}

function handleSearch() {
  loadSpus(keyword.value)
}

function searchByBrand(name) {
  keyword.value = name
  loadSpus(name)
}

async function addToCart(spu) {
  try {
    const res = await addCart(spu.id, 1)
    if (res.code === 20000) {
      ElMessage.success('已加入购物车')
    } else {
      ElMessage.error(res.message || '添加失败')
    }
  } catch (_) {
    ElMessage.error('网络异常')
  }
}
</script>

<style scoped>
.hero {
  text-align: center;
  padding: 48px 20px 40px;
  margin-bottom: 24px;
  background: linear-gradient(135deg, #f0f6ff 0%, #e8f4f8 100%);
  border-radius: 16px;
}
.hero-title {
  font-size: 36px;
  font-weight: 800;
  margin: 0 0 8px;
  background: linear-gradient(135deg, #409eff, #2d7fd3);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.hero-sub {
  color: #909399;
  margin: 0 0 24px;
  font-size: 15px;
}
.hero-search {
  max-width: 560px;
  margin: 0 auto;
}
.sidebar-card {
  position: sticky;
  top: 80px;
}
.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 15px;
}
.brand-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.brand-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  color: #303133;
  font-size: 14px;
  transition: all 0.2s;
}
.brand-item:hover {
  background: rgba(64, 158, 255, 0.08);
  color: #409eff;
}
.brand-item--active {
  background: rgba(64, 158, 255, 0.12);
  color: #409eff;
  font-weight: 600;
}
.brand-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}
.goods-card {
  cursor: pointer;
  overflow: hidden;
  transition: transform 0.25s, box-shadow 0.25s;
}
.goods-card:hover {
  transform: translateY(-4px);
}
.image-placeholder {
  height: 160px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
  color: #c0c4cc;
}
.goods-info {
  padding: 12px 0 0;
}
.goods-name {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.goods-tags {
  margin-bottom: 8px;
}
.goods-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.goods-price {
  color: var(--price);
  font-weight: 700;
  font-size: 16px;
}
</style>
