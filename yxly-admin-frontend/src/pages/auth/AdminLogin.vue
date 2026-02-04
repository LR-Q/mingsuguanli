<template>
  <div class="admin-login-container">
    <div class="admin-login-form">
      <div class="admin-header">
        <h2>悦鑫乐怡民宿管理系统</h2>
        <p class="admin-subtitle">管理员登录</p>
      </div>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        size="large"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="account">
          <el-input
            v-model="loginForm.account"
            placeholder="请输入管理员账号"
            prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-form-item>
          <el-checkbox v-model="loginForm.rememberMe">
            记住我
          </el-checkbox>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            style="width: 100%"
            :loading="loading"
            @click="handleLogin"
          >
            管理员登录
          </el-button>
        </el-form-item>
        
        <el-form-item>
          <div class="user-login-link">
            <el-link type="primary" @click="$router.push('/login')">
              普通用户登录
            </el-link>
            |
            <el-link type="primary" @click="$router.push('/home')">
              返回首页
            </el-link>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/modules/auth'
import { login } from '@/api/modules/auth'

const router = useRouter()
const authStore = useAuthStore()

// 表单引用
const loginFormRef = ref()

// 加载状态
const loading = ref(false)

// 登录表单
const loginForm = reactive({
  account: '',
  password: '',
  rememberMe: false
})

// 表单验证规则
const loginRules = {
  account: [
    { required: true, message: '请输入管理员账号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

// 处理管理员登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    
    loading.value = true
    
    const response = await login({
      account: loginForm.account,
      password: loginForm.password,
      rememberMe: loginForm.rememberMe
    })
    
    if (response.data) {
      const { accessToken, refreshToken, userInfo } = response.data
      
      // 检查用户角色
      const roleCode = userInfo.roleCode
      if (roleCode !== 'SUPER_ADMIN' && roleCode !== 'HOMESTAY_ADMIN') {
        ElMessage.error('您不是管理员，无法访问管理后台')
        return
      }
      
      // 保存登录信息
      authStore.setToken(accessToken)
      authStore.setRefreshToken(refreshToken)
      authStore.setUserInfo(userInfo)
      
      ElMessage.success('登录成功')
      
      // 根据角色跳转
      if (roleCode === 'SUPER_ADMIN') {
        router.push('/super-admin/merchants')
      } else if (roleCode === 'HOMESTAY_ADMIN') {
        router.push('/admin/dashboard')
      }
    }
  } catch (error) {
    console.error('管理员登录失败:', error)
    ElMessage.error(error.message || '管理员登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.admin-login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 32px;
  position: relative;
  overflow: hidden;
}

.admin-login-container::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image: url('/images/auth-bg.jpg'), url('@/assets/images/homestay-bg.jpg');
  background-position: center;
  background-size: cover;
  background-repeat: no-repeat;
  background-attachment: fixed;
  filter: brightness(0.6);
  z-index: 0;
}

.admin-login-form {
  background: white;
  padding: 48px 56px;
  border-radius: 16px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 520px;
  position: relative;
  z-index: 1;
}

.admin-header {
  text-align: center;
  margin-bottom: 36px;
}

.admin-header h2 {
  color: #333;
  margin: 0 0 10px 0;
  font-size: 28px;
  font-weight: 600;
}

.admin-subtitle {
  color: #666;
  margin: 0;
  font-size: 17px;
  font-weight: 500;
}

.user-login-link {
  text-align: center;
  width: 100%;
  color: #999;
  font-size: 15px;
}

.user-login-link .el-link {
  margin: 0 8px;
}

:deep(.el-form-item) {
  margin-bottom: 24px;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
  padding: 12px 14px;
}

:deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
  height: 50px;
  font-size: 16px;
}
</style>
