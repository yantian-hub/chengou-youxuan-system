<template>
  <header class="nav-header">
    <div class="nav-inner">
      <router-link to="/" class="logo">
        <span class="logo-icon">&#9733;</span>
        <span class="logo-text">宸购优选</span>
      </router-link>

      <nav class="nav-links">
        <router-link to="/" class="nav-link" active-class="nav-link--active">
          <el-icon><HomeFilled /></el-icon> 首页
        </router-link>
        <router-link to="/seckill" class="nav-link" active-class="nav-link--active">
          <el-icon><Timer /></el-icon> 限时秒杀
        </router-link>
        <router-link to="/cart" class="nav-link" active-class="nav-link--active">
          <el-badge :value="cartCount" :hidden="cartCount <= 0" class="cart-badge">
            <el-icon><ShoppingCart /></el-icon> 购物车
          </el-badge>
        </router-link>
      </nav>

      <div class="nav-user">
        <el-dropdown trigger="click" v-if="isLoggedIn">
          <span class="user-trigger">
            <el-avatar :size="32" class="user-avatar">{{ username.charAt(0) }}</el-avatar>
            <span class="username">{{ username }}</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="router.push('/order/list')">
                <el-icon><List /></el-icon>我的订单
              </el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">
                <el-icon><SwitchButton /></el-icon>退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <template v-else>
          <el-button size="small" @click="router.push('/login')">登录</el-button>
          <el-button size="small" type="primary" @click="router.push('/register')">注册</el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { getCartList } from '../api/cart'

const router = useRouter()
const username = ref(localStorage.getItem('username') || '用户')
const cartCount = ref(0)
const isLoggedIn = computed(() => !!localStorage.getItem('token'))

let pollTimer = null

onMounted(() => {
  if (isLoggedIn.value) fetchCartCount()
  pollTimer = setInterval(() => {
    if (isLoggedIn.value) fetchCartCount()
  }, 10000)
})

onUnmounted(() => {
  if (pollTimer) clearInterval(pollTimer)
})

async function fetchCartCount() {
  try {
    const res = await getCartList()
    if (res.code === 20000) cartCount.value = (res.data || []).length
  } catch (_) { /* ignore */ }
}

function handleLogout() {
  ElMessageBox.confirm('确定退出登录吗？', '提示').then(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    router.push('/login')
  }).catch(() => {})
}
</script>

<style scoped>
.nav-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid #e8eaed;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}
.nav-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  height: 60px;
  padding: 0 20px;
  gap: 40px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 6px;
  text-decoration: none;
  flex-shrink: 0;
}
.logo-icon {
  font-size: 24px;
  color: #e6a23c;
}
.logo-text {
  font-size: 20px;
  font-weight: 700;
  background: linear-gradient(135deg, #409eff, #2d7fd3);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.nav-links {
  display: flex;
  align-items: center;
  gap: 4px;
  flex: 1;
}
.nav-link {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 16px;
  border-radius: 8px;
  color: #606266;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.2s;
}
.nav-link:hover {
  color: #409eff;
  background: rgba(64, 158, 255, 0.06);
}
.nav-link--active {
  color: #409eff;
  background: rgba(64, 158, 255, 0.1);
  font-weight: 600;
}
.cart-badge :deep(.el-badge__content) {
  top: 6px;
  right: 2px;
}
.nav-user {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}
.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 8px;
  transition: background 0.2s;
}
.user-trigger:hover {
  background: rgba(0, 0, 0, 0.04);
}
.user-avatar {
  background: linear-gradient(135deg, #409eff, #2d7fd3);
  color: #fff;
  font-size: 13px;
}
.username {
  font-size: 14px;
  color: #303133;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
