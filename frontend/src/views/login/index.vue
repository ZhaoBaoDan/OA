<template>
  <div class="login-container">
    <div class="login-card">
      <!-- 左侧品牌展示 -->
      <div class="brand-section">
        <div class="brand-content">
          <div class="logo">
            <el-icon :size="48"><OfficeBuilding /></el-icon>
          </div>
          <h1 class="brand-title">SmartAuto OA</h1>
          <p class="brand-subtitle">企业智能办公自动化平台</p>
          <div class="brand-features">
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>流程自动化管理</span>
            </div>
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>考勤与任务管理</span>
            </div>
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>文档与知识库</span>
            </div>
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>数据可视化报表</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="form-section">
        <div class="form-header">
          <h2>欢迎登录</h2>
          <p>请输入您的账号和密码</p>
        </div>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          size="large"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              :prefix-icon="UserIcon"
              clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="LockIcon"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item prop="captchaCode">
            <div class="captcha-row">
              <el-input
                v-model="loginForm.captchaCode"
                placeholder="验证码"
                :prefix-icon="KeyIcon"
                clearable
                @keyup.enter="handleLogin"
              />
              <div class="captcha-image" @click="refreshCaptcha">
                <img v-if="captchaImg" :src="captchaImg" alt="验证码" />
                <span v-else>获取验证码</span>
              </div>
            </div>
          </el-form-item>

          <div class="form-options">
            <el-checkbox v-model="rememberPassword">记住密码</el-checkbox>
          </div>

          <el-form-item>
            <el-button
              type="primary"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 底部版权信息 -->
    <div class="login-footer">
      <span>Copyright © 2024 SmartAuto OA. All Rights Reserved.</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, markRaw, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { User, Lock, Key } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { getCaptcha } from '@/api/auth'

const UserIcon = markRaw(User)
const LockIcon = markRaw(Lock)
const KeyIcon = markRaw(Key)

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref<FormInstance>()
const loading = ref(false)
const captchaImg = ref('')
const captchaKey = ref('')
const rememberPassword = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  captchaCode: '',
})

const loginRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captchaCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
}

/** 获取验证码 */
async function refreshCaptcha() {
  try {
    const res = await getCaptcha()
    captchaImg.value = res.data.captchaImage
    captchaKey.value = res.data.captchaKey
  } catch {
    // 验证码获取失败时清空
    captchaImg.value = ''
    captchaKey.value = ''
  }
}

/** 登录 */
async function handleLogin() {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await userStore.login({
        username: loginForm.username,
        password: loginForm.password,
        captchaCode: loginForm.captchaCode,
        captchaKey: captchaKey.value,
      })

      if (rememberPassword.value) {
        localStorage.setItem('remembered_user', loginForm.username)
        localStorage.setItem('remembered_pass', loginForm.password)
      } else {
        localStorage.removeItem('remembered_user')
        localStorage.removeItem('remembered_pass')
      }

      ElMessage.success('登录成功')
      const redirect = (route.query.redirect as string) || '/'
      router.push(redirect)
    } catch (err: any) {
      ElMessage.error(err.message || '登录失败')
      refreshCaptcha()
    } finally {
      loading.value = false
    }
  })
}

onMounted(() => {
  // 恢复记住的密码
  const rememberedUser = localStorage.getItem('remembered_user')
  const rememberedPass = localStorage.getItem('remembered_pass')
  if (rememberedUser && rememberedPass) {
    loginForm.username = rememberedUser
    loginForm.password = rememberedPass
    rememberPassword.value = true
  }
  refreshCaptcha()
})
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
}

.login-card {
  display: flex;
  width: 900px;
  min-height: 500px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  background: #fff;
}

.brand-section {
  width: 380px;
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  flex-shrink: 0;
}

.brand-content {
  text-align: center;
  color: #fff;
}

.logo {
  margin-bottom: 20px;
  color: rgba(255, 255, 255, 0.9);
}

.brand-title {
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px;
}

.brand-subtitle {
  font-size: 14px;
  opacity: 0.85;
  margin: 0 0 32px;
}

.brand-features {
  text-align: left;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  opacity: 0.9;
}

.form-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px;
}

.form-header {
  text-align: center;
  margin-bottom: 32px;

  h2 {
    font-size: 24px;
    color: #303133;
    margin: 0 0 8px;
  }

  p {
    font-size: 14px;
    color: #909399;
    margin: 0;
  }
}

.login-form {
  width: 100%;
  max-width: 360px;
}

.captcha-row {
  display: flex;
  gap: 12px;
  width: 100%;

  .el-input {
    flex: 1;
  }
}

.captcha-image {
  width: 120px;
  height: 40px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background: #f5f7fa;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  span {
    font-size: 12px;
    color: #909399;
  }
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
}

.login-footer {
  position: absolute;
  bottom: 20px;
  text-align: center;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.6);
}

@media (max-width: 768px) {
  .login-card {
    width: 90%;
    flex-direction: column;
    min-height: auto;
  }

  .brand-section {
    width: 100%;
    padding: 24px;
  }

  .form-section {
    padding: 24px;
  }
}
</style>
