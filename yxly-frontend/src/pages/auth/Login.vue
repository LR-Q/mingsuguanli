<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>悦鑫乐怡民宿管理系统</h2>
        <p>欢迎登录</p>
      </div>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="account">
          <el-input
            v-model="loginForm.account"
            placeholder="请输入用户名/邮箱/手机号"
            size="large"
            prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-form-item>
          <div style="display: flex; justify-content: space-between; align-items: center; width: 100%;">
            <el-checkbox v-model="loginForm.rememberMe">
              记住我
            </el-checkbox>
            <el-link type="primary" @click="$router.push('/forgot-password')">
              忘记密码？
            </el-link>
          </div>
        </el-form-item>
        
        <el-form-item>
          <div style="text-align: center; width: 100%;">
            <el-radio-group v-model="loginForm.loginType">
              <el-radio label="user">普通用户</el-radio>
              <el-radio label="admin">管理员</el-radio>
            </el-radio-group>
          </div>
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            style="width: 100%"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
        
        <el-form-item>
          <div class="register-link">
            还没有账号？
            <el-link type="primary" @click="$router.push('/register')">
              立即注册
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
  rememberMe: false,
  loginType: 'user' // 默认为普通用户登录
})

// 表单验证规则
const loginRules = {
  account: [
    { required: true, message: '请输入用户名/邮箱/手机号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

// 处理登录
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    await loginFormRef.value.validate()
    
    loading.value = true
    
    const response = await login({
      account: loginForm.account,
      password: loginForm.password,
      rememberMe: loginForm.rememberMe,
      userType: loginForm.loginType
    })
    
    if (response.data) {
      // 保存登录信息到store，确保用户信息包含用户类型
      authStore.setToken(response.data.accessToken)
      authStore.setRefreshToken(response.data.refreshToken)
      
      // 确保用户信息包含用户类型
      const userInfo = {
        ...response.data.userInfo,
        userType: loginForm.loginType  // 保存登录时选择的用户类型
      }
      authStore.setUserInfo(userInfo)
      
      ElMessage.success('登录成功')
      
      // 根据登录类型跳转到不同页面
      if (loginForm.loginType === 'admin') {
        // 管理员登录，跳转到管理后台
        router.push('/admin')
      } else {
        // 普通用户登录，跳转到用户首页
        router.push('/home')
      }
    }
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  
  .login-box {
    width: 400px;
    padding: 40px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    
    .login-header {
      text-align: center;
      margin-bottom: 32px;
      
      h2 {
        margin: 0 0 8px 0;
        color: #303133;
        font-size: 24px;
        font-weight: 500;
      }
      
      p {
        margin: 0;
        color: #909399;
        font-size: 14px;
      }
    }
    
    .login-form {
      .register-link {
        text-align: center;
        color: #909399;
        font-size: 14px;
      }
    }
  }
}
</style>
