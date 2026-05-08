<template>
  <div class="order-page page-container">
    <h2 class="page-title">
      <el-icon color="#409eff"><EditPen /></el-icon>
      确认订单
    </h2>

    <el-row :gutter="24">
      <el-col :span="16">
        <!-- 收货地址 -->
        <el-card class="enhanced-card mb-4">
          <template #header>
            <div class="card-header">
              <el-icon color="#409eff"><Location /></el-icon>
              <span>收货地址</span>
            </div>
          </template>
          <div class="address-area" v-if="addresses.length > 0">
            <el-radio-group v-model="selectedAddress" class="address-group">
              <el-radio
                v-for="addr in addresses"
                :key="addr.id"
                :value="addr"
                class="address-item"
                border
              >
                <div class="address-detail">
                  <span class="address-contact">{{ addr.contact }} {{ addr.phone }}</span>
                  <span class="address-text">{{ addr.address }}</span>
                </div>
                <el-tag v-if="addr.isDefault === 1" size="small" type="danger" effect="dark">默认</el-tag>
              </el-radio>
            </el-radio-group>
            <el-button text type="primary" @click="showAddressDialog = true" class="mt-2">
              <el-icon><Plus /></el-icon> 新增地址
            </el-button>
          </div>
          <div v-else class="no-address">
            <p>暂无收货地址</p>
            <el-button type="primary" @click="showAddressDialog = true">新增地址</el-button>
          </div>
        </el-card>

        <!-- 商品列表 -->
        <el-card class="enhanced-card">
          <template #header>
            <div class="card-header">
              <el-icon color="#409eff"><ShoppingBag /></el-icon>
              <span>商品信息</span>
            </div>
          </template>
          <div v-for="item in items" :key="item.id" class="order-item">
            <div class="order-item-info">
              <span class="order-item-name">{{ item.name }}</span>
              <span class="order-item-price">¥{{ item.price }}</span>
            </div>
            <div class="order-item-actions">
              <span>× {{ item.num }}</span>
              <span class="order-item-subtotal">¥{{ (item.price || 0) * (item.num || 0) }}</span>
            </div>
          </div>
          <el-empty v-if="items.length === 0" description="暂无商品" :image-size="60" />
        </el-card>
      </el-col>

      <!-- 订单摘要 -->
      <el-col :span="8">
        <el-card class="enhanced-card">
          <template #header>
            <div class="card-header">
              <el-icon color="#409eff"><Document /></el-icon>
              <span>订单摘要</span>
            </div>
          </template>
          <div class="summary-row">
            <span>商品数量</span>
            <span>{{ items.length }} 件</span>
          </div>
          <div class="summary-row">
            <span>商品小计</span>
            <span class="summary-price">¥{{ total }}</span>
          </div>
          <el-divider />
          <div class="summary-row summary-total">
            <span>应付总额</span>
            <span class="summary-total-price">¥{{ total }}</span>
          </div>
          <el-button
            type="primary"
            size="large"
            @click="submitOrder"
            :loading="submitting"
            style="width: 100%; margin-top: 16px"
          >
            提交订单
          </el-button>
        </el-card>
      </el-col>
    </el-row>

    <!-- 新增地址对话框 -->
    <el-dialog v-model="showAddressDialog" title="新增收货地址" width="500px">
      <el-form :model="addressForm" label-width="80px">
        <el-form-item label="联系人">
          <el-input v-model="addressForm.contact" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="addressForm.phone" />
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input v-model="addressForm.address" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddressDialog = false">取消</el-button>
        <el-button type="primary" @click="saveAddress">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCartByIds } from '../api/cart'
import { createOrder } from '../api/order'

const route = useRoute()
const router = useRouter()
const items = ref([])
const submitting = ref(false)
const addresses = ref([])
const selectedAddress = ref(null)
const showAddressDialog = ref(false)
const addressForm = ref({ contact: '', phone: '', address: '' })

const total = computed(() => {
  return items.value.reduce((s, i) => s + (i.price || 0) * (i.num || 0), 0)
})

onMounted(async () => {
  const ids = (route.query.ids || '').split(',').filter(Boolean)
  if (ids.length === 0) {
    ElMessage.warning('请选择商品')
    router.push('/cart')
    return
  }
  try {
    const res = await getCartByIds(ids)
    if (res.code === 20000) items.value = res.data || []
  } catch (_) {
    ElMessage.error('获取商品信息失败')
  }
  loadAddresses()
})

function loadAddresses() {
  // Mock addresses
  addresses.value = [
    { id: 1, contact: '张三', phone: '13800138000', address: '北京市朝阳区xxx街道1号', isDefault: 0 },
    { id: 2, contact: '李四', phone: '13900139000', address: '上海市浦东新区xxx路2号', isDefault: 1 },
  ]
  selectedAddress.value = addresses.value.find(a => a.isDefault === 1) || addresses.value[0]
}

function saveAddress() {
  const { contact, phone, address } = addressForm.value
  if (!contact || !phone || !address) {
    ElMessage.warning('请填写完整信息')
    return
  }
  addresses.value.push({
    id: Date.now(),
    contact,
    phone,
    address,
    isDefault: 0
  })
  showAddressDialog.value = false
  addressForm.value = { contact: '', phone: '', address: '' }
  ElMessage.success('地址已保存')
}

async function submitOrder() {
  if (!selectedAddress.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  submitting.value = true
  try {
    const res = await createOrder({ id: Date.now().toString() })
    if (res.code === 20000) {
      ElMessage.success('订单创建成功')
      router.push(`/pay/success/${res.data}`)
    } else {
      ElMessage.error(res.message || '创建失败')
    }
  } catch (_) {
    ElMessage.error('网络异常')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.page-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 24px;
  font-size: 22px;
}
.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 15px;
}
.mb-4 {
  margin-bottom: 20px;
}
.mt-2 {
  margin-top: 12px;
}
.address-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
}
.address-item {
  width: 100%;
  height: auto;
  padding: 12px 16px;
  margin: 0;
}
.address-detail {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
}
.address-contact {
  font-weight: 600;
  font-size: 14px;
}
.address-text {
  color: #909399;
  font-size: 13px;
}
.no-address {
  text-align: center;
  padding: 20px 0;
  color: #909399;
}
.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}
.order-item:last-child {
  border-bottom: none;
}
.order-item-info {
  display: flex;
  gap: 16px;
  align-items: center;
}
.order-item-name {
  font-weight: 500;
}
.order-item-price {
  color: #909399;
  font-size: 14px;
}
.order-item-actions {
  display: flex;
  align-items: center;
  gap: 20px;
  color: #909399;
  font-size: 14px;
}
.order-item-subtotal {
  font-weight: 600;
  color: var(--price);
  min-width: 70px;
  text-align: right;
}
.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  color: #606266;
  font-size: 14px;
}
.summary-price {
  color: var(--price);
  font-weight: 600;
}
.summary-total {
  font-size: 16px;
  font-weight: 700;
}
.summary-total-price {
  font-size: 22px;
  color: var(--price);
}
</style>
