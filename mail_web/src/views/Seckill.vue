<template>
  <div class="seckill-page page-container">
    <!-- 秒杀标题区 -->
    <div class="seckill-hero">
      <div class="seckill-hero-left">
        <h2 class="seckill-title">
          <el-icon style="font-size: 28px; color: #fff"><Timer /></el-icon>
          限时秒杀
        </h2>
        <p class="seckill-desc">限时特惠，手慢无！</p>
      </div>
      <div class="seckill-countdown" v-if="countdown > 0">
        <span class="countdown-label">距结束</span>
        <span class="countdown-time">{{ formatCountdown }}</span>
      </div>
    </div>

    <!-- 秒杀商品 -->
    <el-row :gutter="20">
      <el-col
        v-for="item in seckillGoods"
        :key="item.id"
        :xs="24" :sm="12" :md="8"
        style="margin-bottom: 24px"
      >
        <el-card shadow="hover" class="enhanced-card seckill-card">
          <div class="seckill-badge">限时秒杀</div>
          <div class="seckill-image">
            <el-image
              :src="item.images || ''"
              fit="cover"
              style="width: 100%; height: 180px"
            >
              <template #error>
                <div class="seckill-image-placeholder">
                  <el-icon :size="48"><Goods /></el-icon>
                  <span>{{ item.name || `秒杀商品 ${item.id}` }}</span>
                </div>
              </template>
            </el-image>
          </div>
          <div class="seckill-info">
            <h3 class="seckill-name">{{ item.name || `秒杀商品 ${item.id}` }}</h3>
            <div class="seckill-price-row">
              <span class="seckill-price">¥{{ item.seckillPrice || item.price || '?' }}</span>
              <span class="seckill-original">
                <del>¥{{ item.originalPrice || item.price || '?' }}</del>
              </span>
            </div>
            <div class="seckill-progress">
              <el-progress
                :percentage="item.percentage || 80"
                :stroke-width="8"
                color="#e74c3c"
                :show-text="false"
              />
              <span class="seckill-stock">已抢 {{ item.sold || Math.floor(Math.random() * 50 + 20) }}%</span>
            </div>
            <el-button
              type="danger"
              size="large"
              @click="handleSeckill(item.id)"
              :disabled="seckilling[item.id]"
              :loading="seckilling[item.id]"
              class="seckill-btn"
            >
              {{ seckilling[item.id] ? '抢购中...' : '立即抢购' }}
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-divider />

    <!-- 我的秒杀记录 -->
    <div class="orders-section">
      <h3 class="section-title">
        <el-icon color="#e74c3c"><Document /></el-icon>
        我的秒杀记录
      </h3>
      <el-card class="enhanced-card">
        <el-table :data="myOrders" stripe style="width: 100%" v-if="myOrders.length > 0">
          <el-table-column prop="orderId" label="订单号" min-width="200" />
          <el-table-column prop="createTime" label="下单时间" min-width="160" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag
                :type="row.status === 1 ? 'success' : row.status === 2 ? 'danger' : 'warning'"
                effect="dark"
                size="small"
              >
                {{ row.status === 0 ? '未支付' : row.status === 1 ? '已支付' : '已取消' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button size="small" type="primary" v-if="row.status === 0" @click="handlePay(row)">
                去支付
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-empty v-else description="暂无秒杀记录" :image-size="80" />
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { doSeckill, querySeckillOrder } from '../api/seckill'

const seckillGoods = ref([
  { id: '1', name: '旗舰手机', originalPrice: 5999, price: 2999, seckillPrice: 2999 },
  { id: '2', name: '无线耳机', originalPrice: 1299, price: 599, seckillPrice: 599 },
  { id: '3', name: '智能手表', originalPrice: 2499, price: 1299, seckillPrice: 1299 }
])

const seckilling = ref({})
const myOrders = ref([])
const countdown = ref(2 * 60 * 60 + 30 * 60) // 2h30m mock

let timer = null

const formatCountdown = computed(() => {
  const h = Math.floor(countdown.value / 3600)
  const m = Math.floor((countdown.value % 3600) / 60)
  const s = countdown.value % 60
  return `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
})

onMounted(() => {
  timer = setInterval(() => {
    if (countdown.value > 0) countdown.value--
  }, 1000)
})
onUnmounted(() => {
  if (timer) clearInterval(timer)
})

async function handleSeckill(goodsId) {
  seckilling.value[goodsId] = true
  try {
    const res = await doSeckill(goodsId)
    if (res.code === 20000) {
      ElMessage.success('抢购成功！请尽快支付')
      const order = await querySeckillOrder(res.data)
      if (order.data) myOrders.value.unshift(order.data)
    } else {
      ElMessage.error(res.message || '秒杀失败')
    }
  } catch (_) {
    ElMessage.error('网络异常')
  } finally {
    seckilling.value[goodsId] = false
  }
}

function handlePay(row) {
  window.location.href = `/#/pay/success/${row.orderId}`
}
</script>

<style scoped>
.seckill-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32px 40px;
  margin-bottom: 28px;
  background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
  border-radius: 16px;
  color: #fff;
}
.seckill-title {
  margin: 0 0 4px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 28px;
  color: #fff;
}
.seckill-desc {
  margin: 0;
  opacity: 0.85;
  font-size: 14px;
}
.seckill-countdown {
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(0, 0, 0, 0.2);
  padding: 12px 20px;
  border-radius: 10px;
}
.countdown-label {
  font-size: 14px;
  opacity: 0.9;
}
.countdown-time {
  font-size: 28px;
  font-weight: 700;
  font-family: 'Courier New', monospace;
  letter-spacing: 2px;
}
.seckill-card {
  position: relative;
  overflow: hidden;
  transition: transform 0.25s, box-shadow 0.25s;
  text-align: center;
}
.seckill-card:hover {
  transform: translateY(-4px);
}
.seckill-badge {
  position: absolute;
  top: 12px;
  left: -28px;
  width: 100px;
  text-align: center;
  background: #e74c3c;
  color: #fff;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 0;
  transform: rotate(-45deg);
  z-index: 2;
}
.seckill-image-placeholder {
  height: 180px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: #fdf0ef;
  color: #e74c3c;
  font-size: 14px;
}
.seckill-info {
  padding: 16px 0 0;
}
.seckill-name {
  margin: 0 0 12px;
  font-size: 16px;
  font-weight: 600;
}
.seckill-price-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 12px;
}
.seckill-price {
  font-size: 28px;
  font-weight: 800;
  color: #e74c3c;
}
.seckill-original {
  font-size: 14px;
  color: #c0c4cc;
}
.seckill-progress {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}
.seckill-progress :deep(.el-progress) {
  flex: 1;
}
.seckill-stock {
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}
.seckill-btn {
  width: 100%;
}
.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 16px;
  font-size: 18px;
}
.orders-section {
  margin-top: 20px;
}
</style>
