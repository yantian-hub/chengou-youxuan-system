<template>
  <div class="cart-page page-container">
    <div class="page-header">
      <h2><el-icon color="#409eff"><ShoppingCart /></el-icon> 我的购物车</h2>
      <el-button v-if="cartList.length > 0" text type="danger" @click="handleClear">
        <el-icon><Delete /></el-icon> 清空购物车
      </el-button>
    </div>

    <el-card class="enhanced-card" v-loading="loading">
      <template v-if="cartList.length > 0">
        <div
          v-for="item in cartList"
          :key="item.id"
          class="cart-item"
        >
          <div class="cart-item-info">
            <div class="cart-item-name">{{ item.name }}</div>
            <div class="cart-item-price">¥{{ item.price }}</div>
          </div>

          <div class="cart-item-actions">
            <el-input-number
              v-model="item.num"
              :min="1"
              :max="99"
              size="small"
              @change="(val) => updateNum(item, val)"
            />
            <span class="cart-item-subtotal">¥{{ (item.price || 0) * (item.num || 0) }}</span>
            <el-button circle size="small" type="danger" plain @click="handleDelete(item)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>

        <el-divider />

        <div class="cart-footer">
          <span class="cart-total">
            合计：
            <span class="cart-total-price">¥{{ total }}</span>
          </span>
          <el-button type="primary" size="large" @click="checkout" :disabled="cartList.length === 0">
            去结算
          </el-button>
        </div>
      </template>

      <el-empty v-else description="购物车是空的，去首页逛逛吧" :image-size="100">
        <template #extra>
          <el-button type="primary" @click="$router.push('/')">去首页</el-button>
        </template>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCartList, deleteCart, updateCartNum, clearCart } from '../api/cart'

const router = useRouter()
const loading = ref(false)
const cartList = ref([])

const total = computed(() => {
  return cartList.value.reduce((s, i) => s + (i.price || 0) * (i.num || 0), 0)
})

onMounted(() => loadCart())

async function loadCart() {
  loading.value = true
  try {
    const res = await getCartList()
    if (res.code === 20000) cartList.value = res.data || []
  } catch (_) {
    ElMessage.error('获取购物车失败')
  } finally {
    loading.value = false
  }
}

async function updateNum(item, val) {
  try {
    await updateCartNum(item.skuId, val)
  } catch (_) {
    ElMessage.error('修改数量失败')
  }
}

async function handleDelete(item) {
  try {
    const res = await deleteCart(item.skuId)
    if (res.code === 20000) {
      ElMessage.success('已删除')
      await loadCart()
    }
  } catch (_) {
    ElMessage.error('删除失败')
  }
}

function handleClear() {
  ElMessageBox.confirm('确定清空购物车吗？', '提示', { type: 'warning' }).then(async () => {
    try {
      await clearCart()
      ElMessage.success('已清空')
      cartList.value = []
    } catch (_) {
      ElMessage.error('清空失败')
    }
  }).catch(() => {})
}

function checkout() {
  const ids = cartList.value.map(c => c.id)
  router.push({ name: 'OrderConfirm', query: { ids: ids.join(',') } })
}
</script>

<style scoped>
.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}
.page-header h2 {
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 22px;
}
.cart-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}
.cart-item:last-child {
  border-bottom: none;
}
.cart-item-info {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
}
.cart-item-name {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}
.cart-item-price {
  color: #909399;
  font-size: 14px;
}
.cart-item-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}
.cart-item-subtotal {
  font-weight: 700;
  color: var(--price);
  font-size: 16px;
  min-width: 80px;
  text-align: right;
}
.cart-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.cart-total {
  font-size: 16px;
  color: #303133;
}
.cart-total-price {
  font-size: 24px;
  font-weight: 700;
  color: var(--price);
}
</style>
