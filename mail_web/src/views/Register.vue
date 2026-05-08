<template>
  <div class="register-page">
    <el-card class="register-card enhanced-card">
      <div class="register-header">
        <h2>创建账号</h2>
        <p>注册成为宸购优选用户</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0">
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="手机号" maxlength="11" size="large">
            <template #prefix><el-icon><Iphone /></el-icon></template>
            <template #append>
              <el-button @click="handleSendCode" :disabled="codeBtnDisabled" size="small" style="width: 100px">
                {{ codeBtnText }}
              </el-button>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="code">
          <el-input v-model="form.code" placeholder="验证码" maxlength="6" size="large">
            <template #prefix><el-icon><ChatDotRound /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="密码" size="large">
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="昵称" size="large">
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister" :loading="loading" style="width: 100%" size="large">
            注册
          </el-button>
        </el-form-item>
        <div class="register-footer">
          <span>已有账号？</span>
          <router-link to="/login">立即登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register, sendCode } from '../api/user'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const countdown = ref(0)

const form = reactive({ phone: '', code: '', password: '', username: '' })
const rules = {
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  username: [{ required: true, message: '请输入昵称', trigger: 'blur' }]
}

const codeBtnDisabled = computed(() => countdown.value > 0 || !form.phone)
const codeBtnText = computed(() => countdown.value > 0 ? `${countdown.value}s` : '获取验证码')

function handleSendCode() {
  if (!form.phone) { ElMessage.warning('请先输入手机号'); return }
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) clearInterval(timer)
  }, 1000)
  sendCode(form.phone)
}

async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => {})
  if (!valid) return
  loading.value = true
  try {
    const res = await register({ ...form })
    if (res.code === 20000) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(res.message || '注册失败')
    }
  } catch (_) {
    ElMessage.error('网络异常，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.register-card {
  width: 450px;
  padding: 32px 24px;
}
.register-header {
  text-align: center;
  margin-bottom: 28px;
}
.register-header h2 {
  margin: 0 0 4px;
  font-size: 26px;
  background: linear-gradient(135deg, #409eff, #2d7fd3);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.register-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}
.register-footer {
  text-align: center;
  font-size: 14px;
  color: #909399;
}
.register-footer a {
  color: #409eff;
  text-decoration: none;
  margin-left: 4px;
}
.register-footer a:hover {
  text-decoration: underline;
}
</style>
