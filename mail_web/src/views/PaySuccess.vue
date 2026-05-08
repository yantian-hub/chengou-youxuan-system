<template>
  <div class="pay-page page-container">
    <el-card class="enhanced-card pay-card">
      <div class="pay-success-icon">
        <el-icon :size="64" color="#67c23a"><SuccessFilled /></el-icon>
      </div>
      <h2 class="pay-title">支付成功！</h2>
      <p class="pay-sub">感谢您的购买，订单正在处理中</p>

      <div class="pay-details">
        <div class="pay-detail-row">
          <span class="pay-detail-label">订单号</span>
          <span class="pay-detail-value">{{ route.params.id }}</span>
        </div>
      </div>

      <div class="pay-actions">
        <el-button type="primary" size="large" @click="$router.push('/')">
          <el-icon><HomeFilled /></el-icon> 回到首页
        </el-button>
        <el-button size="large" @click="mockPay">
          <el-icon><Link /></el-icon> 模拟支付回调
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { payResult } from '../api/pay'

const route = useRoute()

async function mockPay() {
  const id = route.params.id
  try {
    const res = await payResult(id, 1)
    ElMessage.success(res === 'success' ? '支付回调成功' : '回调已处理')
  } catch (_) {
    ElMessage.error('回调失败')
  }
}
</script>

<style scoped>
.pay-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
}
.pay-card {
  width: 480px;
  text-align: center;
  padding: 40px 20px;
}
.pay-success-icon {
  margin-bottom: 16px;
  animation: scaleIn 0.4s ease;
}
@keyframes scaleIn {
  0% { transform: scale(0); opacity: 0; }
  60% { transform: scale(1.2); }
  100% { transform: scale(1); opacity: 1; }
}
.pay-title {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 8px;
  color: #303133;
}
.pay-sub {
  color: #909399;
  margin: 0 0 24px;
}
.pay-details {
  background: #f5f7fa;
  border-radius: 10px;
  padding: 16px 20px;
  margin-bottom: 24px;
  text-align: left;
}
.pay-detail-row {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  padding: 4px 0;
}
.pay-detail-label {
  color: #909399;
}
.pay-detail-value {
  color: #303133;
  font-family: monospace;
  font-size: 13px;
}
.pay-actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}
</style>
