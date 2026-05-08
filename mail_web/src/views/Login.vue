<template>
  <div class="login-page">
    <el-card class="login-card enhanced-card">
      <div class="login-header">
        <h2>宸购优选</h2>
        <p>欢迎回来</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0">
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="手机号" maxlength="11" size="large">
            <template #prefix><el-icon><Iphone /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="密码" size="large">
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%" size="large">
            登录
          </el-button>
        </el-form-item>
        <div class="login-footer">
          <span>还没有账号？</span>
          <router-link to="/register">立即注册</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api/user'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({ phone: '', password: '' })
const rules = {
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => {})
  if (!valid) return
  loading.value = true
  try {
    const res = await login({ phone: form.phone, password: form.password })
    if (res.code === 20000) {
      localStorage.setItem('token', res.data)
      localStorage.setItem('phone', form.phone)
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error(res.message || '登录失败')
    }
  } catch (_) {
    ElMessage.error('网络异常，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-card {
  width: 400px;
  padding: 32px 24px;
}
.login-header {
  text-align: center;
  margin-bottom: 28px;
}
.login-header h2 {
  margin: 0 0 4px;
  font-size: 26px;
  background: linear-gradient(135deg, #409eff, #2d7fd3);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.login-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}
.login-footer {
  text-align: center;
  font-size: 14px;
  color: #909399;
}
.login-footer a {
  color: #409eff;
  text-decoration: none;
  margin-left: 4px;
}
.login-footer a:hover {
  text-decoration: underline;
}
</style>
