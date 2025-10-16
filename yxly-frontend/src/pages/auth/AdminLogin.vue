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
      rememberMe: loginForm.rememberMe,
      userType: 'admin'  // 管理员登录页面固定为admin类型
    })
    
    if (response.data) {
      // 保存登录信息到store，确保用户信息包含用户类型
      authStore.setToken(response.data.accessToken)
      authStore.setRefreshToken(response.data.refreshToken)
      
      // 确保用户信息包含用户类型
      const userInfo = {
        ...response.data.userInfo,
        userType: 'admin'  // 明确标记为管理员
      }
      authStore.setUserInfo(userInfo)
      
      ElMessage.success('管理员登录成功')
      
      // 管理员登录后直接跳转到管理后台
      router.push('/admin')
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.admin-login-form {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.admin-header {
  text-align: center;
  margin-bottom: 30px;
}

.admin-header h2 {
  color: #333;
  margin: 0 0 10px 0;
  font-size: 24px;
  font-weight: 600;
}

.admin-subtitle {
  color: #666;
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

.user-login-link {
  text-align: center;
  width: 100%;
  color: #999;
  font-size: 14px;
}

.user-login-link .el-link {
  margin: 0 8px;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
}

:deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
}
</style>
