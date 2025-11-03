<template>
  <div class="forgot-password-container">
    <div class="forgot-password-form">
      <div class="form-header">
        <h2>重置密码</h2>
        <p class="subtitle">通过验证身份信息重置您的登录密码</p>
      </div>
      
      <el-form
        ref="forgotFormRef"
        :model="forgotForm"
        :rules="forgotRules"
        size="large"
        @keyup.enter="handleResetPassword"
      >
        <!-- 身份验证区域 -->
        <div class="form-section">
          <h3 class="section-title">身份验证</h3>
          <el-form-item prop="username">
            <el-input
              v-model="forgotForm.username"
              placeholder="请输入注册用户名"
              prefix-icon="User"
            />
          </el-form-item>
          <el-form-item prop="phone">
            <el-input
              v-model="forgotForm.phone"
              placeholder="请输入注册手机号"
              prefix-icon="Phone"
            />
          </el-form-item>
          
          <el-form-item prop="email">
            <el-input
              v-model="forgotForm.email"
              placeholder="请输入注册邮箱"
              prefix-icon="Message"
            />
          </el-form-item>
        </div>
        
        <!-- 新密码设置区域 -->
        <div class="form-section">
          <h3 class="section-title">设置新密码</h3>
          <el-form-item prop="newPassword">
            <el-input
              v-model="forgotForm.newPassword"
              type="password"
              placeholder="请输入新密码（至少6位，包含字母和数字）"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>
          
          <el-form-item prop="confirmPassword">
            <el-input
              v-model="forgotForm.confirmPassword"
              type="password"
              placeholder="请再次确认新密码"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>
        </div>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            style="width: 100%"
            :loading="loading"
            @click="handleResetPassword"
          >
            重置密码
          </el-button>
        </el-form-item>
        
        <el-form-item>
          <div class="back-to-login">
            <el-link type="primary" @click="$router.push('/login')">
              返回登录
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
import { resetPassword } from '@/api/modules/auth'

const router = useRouter()

// 表单引用
const forgotFormRef = ref()

// 加载状态
const loading = ref(false)

// 忘记密码表单
const forgotForm = reactive({
  username: '',
  phone: '',
  email: '',
  newPassword: '',
  confirmPassword: ''
})
const validateUsername = (rule, value, callback) => {
  const usernameRegex = /^[A-Za-z0-9_.-]{3,20}$/
  if (!value) {
    callback(new Error('请输入用户名'))
  } else if (!usernameRegex.test(value)) {
    callback(new Error('用户名为3-20位，支持字母、数字、._-'))
  } else {
    callback()
  }
}


// 自定义验证规则
const validatePhone = (rule, value, callback) => {
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!phoneRegex.test(value)) {
    callback(new Error('请输入正确的手机号格式'))
  } else {
    callback()
  }
}

const validateEmail = (rule, value, callback) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!value) {
    callback(new Error('请输入邮箱'))
  } else if (!emailRegex.test(value)) {
    callback(new Error('请输入正确的邮箱格式'))
  } else {
    callback()
  }
}

const validatePassword = (rule, value, callback) => {
  const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*?&]{6,}$/
  if (!value) {
    callback(new Error('请输入新密码'))
  } else if (value.length < 6) {
    callback(new Error('密码长度不能少于6位'))
  } else if (!passwordRegex.test(value)) {
    callback(new Error('密码必须包含字母和数字'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== forgotForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

// 表单验证规则
const forgotRules = {
  username: [{ validator: validateUsername, trigger: 'blur' }],
  phone: [{ validator: validatePhone, trigger: 'blur' }],
  email: [{ validator: validateEmail, trigger: 'blur' }],
  newPassword: [{ validator: validatePassword, trigger: 'blur' }],
  confirmPassword: [{ validator: validateConfirmPassword, trigger: 'blur' }]
}

// 处理重置密码
const handleResetPassword = async () => {
  if (!forgotFormRef.value) return
  
  try {
    await forgotFormRef.value.validate()
    
    loading.value = true
    
    const response = await resetPassword({
      username: forgotForm.username,
      phone: forgotForm.phone,
      email: forgotForm.email,
      newPassword: forgotForm.newPassword,
      confirmPassword: forgotForm.confirmPassword
    })
    
    // 如果没有抛出异常，说明重置成功
    ElMessage.success('密码重置成功，前往登录')
    
    // 跳转到登录页面
    router.push('/login')
  } catch (error) {
    console.error('重置密码失败:', error)
    ElMessage.error(error.message || '重置密码失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.forgot-password-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.forgot-password-form {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
}

.form-header {
  text-align: center;
  margin-bottom: 30px;
}

.form-header h2 {
  color: #333;
  margin: 0 0 10px 0;
  font-size: 24px;
  font-weight: 600;
}

.subtitle {
  color: #666;
  margin: 0;
  font-size: 16px;
  font-weight: 500;
}

.back-to-login {
  text-align: center;
  width: 100%;
}

.form-section {
  margin-bottom: 30px;
  padding: 20px;
  background: #fafafa;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
}

.section-title {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  padding-bottom: 8px;
  border-bottom: 2px solid #409eff;
  display: inline-block;
}

:deep(.el-form-item) {
  margin-bottom: 15px;
}

:deep(.form-section .el-form-item:last-child) {
  margin-bottom: 0;
}

:deep(.el-input__wrapper) {
  border-radius: 8px;
}

:deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
}
</style>
