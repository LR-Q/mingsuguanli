<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h2>用户注册</h2>
        <p>创建您的账号</p>
      </div>
      
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        class="register-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            size="large"
            prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item prop="email">
          <el-input
            v-model="registerForm.email"
            placeholder="请输入邮箱"
            size="large"
            prefix-icon="Message"
          />
        </el-form-item>
        
        <el-form-item prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            size="large"
            prefix-icon="Phone"
          />
        </el-form-item>
        
        <el-form-item prop="realName">
          <el-input
            v-model="registerForm.realName"
            placeholder="请输入真实姓名（可选）"
            size="large"
            prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码（至少6位，包含字母数字）"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            style="width: 100%"
            :loading="loading"
            @click="handleRegister"
          >
            注册
          </el-button>
        </el-form-item>
        
        <el-form-item>
          <div class="login-link">
            已有账号？
            <el-link type="primary" @click="$router.push('/login')">
              立即登录
            </el-link>
          </div>
        </el-form-item>
        
        <el-divider>或</el-divider>
        
        <el-form-item>
          <el-button
            type="success"
            size="large"
            style="width: 100%"
            plain
            @click="$router.push('/merchant-register')"
          >
            我是民宿主，注册管理员账号
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register, checkUsername, checkEmail, checkPhone } from '@/api/modules/auth'
import { useAuthStore } from '@/stores/modules/auth'

const router = useRouter()
const authStore = useAuthStore()

// 表单引用
const registerFormRef = ref()

// 加载状态
const loading = ref(false)
const codeCountdown = ref(0)

// 注册表单
const registerForm = reactive({
  username: '',
  email: '',
  phone: '',
  realName: '',
  password: '',
  confirmPassword: ''
})

// 自定义验证器
const validateUsername = async (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入用户名'))
    return
  }
  if (!/^[a-zA-Z0-9_]+$/.test(value)) {
    callback(new Error('用户名只能包含字母、数字和下划线'))
    return
  }
  try {
    const result = await checkUsername(value)
    if (!result.data) {
      callback(new Error('用户名已存在'))
    } else {
      callback()
    }
  } catch (error) {
    callback()
  }
}

const validateEmail = async (rule, value, callback) => {
  if (!value) {
    callback()
    return
  }
  try {
    const result = await checkEmail(value)
    if (!result.data) {
      callback(new Error('邮箱已被注册'))
    } else {
      callback()
    }
  } catch (error) {
    callback()
  }
}

const validatePhone = async (rule, value, callback) => {
  if (!value) {
    callback()
    return
  }
  try {
    const result = await checkPhone(value)
    if (!result.data) {
      callback(new Error('手机号已被注册'))
    } else {
      callback()
    }
  } catch (error) {
    callback()
  }
}

const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
    return
  }
  if (!/^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]{6,}$/.test(value)) {
    callback(new Error('密码至少包含一个字母和一个数字'))
    return
  }
  callback()
}

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请确认密码'))
    return
  }
  if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
    return
  }
  callback()
}

// 表单验证规则
const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度在3-50个字符', trigger: 'blur' },
    { validator: validateUsername, trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
    { validator: validateEmail, trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' },
    { validator: validatePhone, trigger: 'blur' }
  ],
  realName: [
    { max: 50, message: '姓名长度不能超过50个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符', trigger: 'blur' },
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    await registerFormRef.value.validate()
    
    loading.value = true
    
    const response = await register({
      username: registerForm.username,
      email: registerForm.email || undefined,
      phone: registerForm.phone, // 手机号现在是必填的
      realName: registerForm.realName || undefined,
      password: registerForm.password,
      confirmPassword: registerForm.confirmPassword
    })
    
    // 注册成功后自动登录
    if (response.data) {
      authStore.setToken(response.data.accessToken)
      authStore.setRefreshToken(response.data.refreshToken)
      authStore.setUserInfo(response.data.userInfo)
      
      ElMessage.success('注册成功，欢迎使用悦鑫乐怡民宿管理系统！')
      
      // 跳转到首页
      router.push('/')
    } else {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    }
  } catch (error) {
    ElMessage.error(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.register-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
  
  &::before {
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
  
  .register-box {
    width: 520px;
    max-width: 95%;
    padding: 48px 56px;
    background: white;
    border-radius: 14px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    position: relative;
    z-index: 1;
    
    .register-header {
      text-align: center;
      margin-bottom: 36px;
      
      h2 {
        margin: 0 0 8px 0;
        color: #303133;
        font-size: 28px;
        font-weight: 500;
      }
      
      p {
        margin: 0;
        color: #909399;
        font-size: 16px;
      }
    }
    
    .register-form {
      .verification-code {
        display: flex;
        align-items: center;
      }
      
      .login-link {
        text-align: center;
        color: #909399;
        font-size: 15px;
      }
    }
  }

  :deep(.el-form-item) {
    margin-bottom: 24px;
  }

  :deep(.el-input__wrapper) {
    border-radius: 10px;
    padding: 12px 14px;
  }

  :deep(.el-button) {
    height: 50px;
    font-size: 16px;
    border-radius: 10px;
  }
}
</style>
