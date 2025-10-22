<template>
  <div class="merchant-register-container">
    <div class="register-box">
      <div class="register-header">
        <h2>民宿主管理员注册</h2>
        <p>简单几步，开启您的民宿管理之旅</p>
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
        
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码（至少6位，包含字母和数字）"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请再次确认密码"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-form-item prop="realName">
          <el-input
            v-model="registerForm.realName"
            placeholder="请输入真实姓名"
            size="large"
            prefix-icon="User"
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
        
        <el-divider>身份证明</el-divider>
        
        <el-form-item prop="idCardFront">
          <el-upload
            class="id-card-uploader"
            :action="uploadAction"
            :show-file-list="false"
            :on-success="handleIdCardFrontSuccess"
            :before-upload="beforeUpload"
          >
            <img v-if="registerForm.idCardFront" :src="registerForm.idCardFront" class="id-card-image">
            <div v-else class="upload-placeholder">
              <el-icon class="upload-icon"><Plus /></el-icon>
              <div class="upload-text">上传身份证正面</div>
            </div>
          </el-upload>
        </el-form-item>
        
        <el-form-item prop="idCardBack">
          <el-upload
            class="id-card-uploader"
            :action="uploadAction"
            :show-file-list="false"
            :on-success="handleIdCardBackSuccess"
            :before-upload="beforeUpload"
          >
            <img v-if="registerForm.idCardBack" :src="registerForm.idCardBack" class="id-card-image">
            <div v-else class="upload-placeholder">
              <el-icon class="upload-icon"><Plus /></el-icon>
              <div class="upload-text">上传身份证反面</div>
            </div>
          </el-upload>
        </el-form-item>
        
        <el-alert
          title="温馨提示"
          type="info"
          :closable="false"
          show-icon
          style="margin-bottom: 20px"
        >
          提交注册申请后，需要等待平台审核通过才能使用。请确保信息真实有效。
        </el-alert>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            style="width: 100%"
            :loading="loading"
            @click="handleRegister"
          >
            提交注册申请
          </el-button>
        </el-form-item>
        
        <el-form-item>
          <div class="back-link">
            <el-link type="primary" @click="$router.push('/register')">
              返回普通用户注册
            </el-link>
            <span style="margin: 0 10px">|</span>
            <el-link type="primary" @click="$router.push('/login')">
              已有账号，去登录
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
import { Plus } from '@element-plus/icons-vue'
import { registerMerchant } from '@/api/modules/merchant'
import { checkUsername, checkPhone } from '@/api/modules/auth'

const router = useRouter()

// 表单引用
const registerFormRef = ref()

// 加载状态
const loading = ref(false)

// 上传地址（需要配置实际的上传接口）
const uploadAction = ref('/api/v1/upload/image')

// 注册表单
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  realName: '',
  phone: '',
  idCardFront: '',
  idCardBack: ''
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


const validatePhone = async (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入手机号'))
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

const validateIdCard = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请上传身份证照片'))
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
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符', trigger: 'blur' },
    { validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { max: 50, message: '姓名长度不能超过50个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' },
    { validator: validatePhone, trigger: 'blur' }
  ],
  idCardFront: [
    { required: true, message: '请上传身份证正面', trigger: 'change' },
    { validator: validateIdCard, trigger: 'change' }
  ],
  idCardBack: [
    { required: true, message: '请上传身份证反面', trigger: 'change' },
    { validator: validateIdCard, trigger: 'change' }
  ]
}

// 上传成功回调
const handleIdCardFrontSuccess = (response, file) => {
  if (response.code === 200) {
    registerForm.idCardFront = response.data.url
    ElMessage.success('身份证正面上传成功')
  } else {
    ElMessage.error('上传失败，请重试')
  }
}

const handleIdCardBackSuccess = (response, file) => {
  if (response.code === 200) {
    registerForm.idCardBack = response.data.url
    ElMessage.success('身份证反面上传成功')
  } else {
    ElMessage.error('上传失败，请重试')
  }
}

// 上传前校验
const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB！')
    return false
  }
  return true
}

// 处理注册
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    await registerFormRef.value.validate()
    
    loading.value = true
    
    const response = await registerMerchant(registerForm)
    
    ElMessage.success('注册申请提交成功！请等待平台审核，审核通过后即可登录使用')
    
    // 3秒后跳转到登录页
    setTimeout(() => {
      router.push('/login')
    }, 3000)
    
  } catch (error) {
    if (error.errors) {
      // 表单验证错误
      return
    }
    ElMessage.error(error.message || '注册失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.merchant-register-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 40px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  
  .register-box {
    width: 500px;
    max-width: 95%;
    padding: 40px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    
    .register-header {
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
    
    .register-form {
      .id-card-uploader {
        width: 100%;
        
        :deep(.el-upload) {
          width: 100%;
          border: 1px dashed #d9d9d9;
          border-radius: 6px;
          cursor: pointer;
          overflow: hidden;
          transition: border-color 0.3s;
          
          &:hover {
            border-color: #409eff;
          }
        }
        
        .upload-placeholder {
          width: 100%;
          height: 180px;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          background: #fafafa;
          
          .upload-icon {
            font-size: 48px;
            color: #8c939d;
            margin-bottom: 10px;
          }
          
          .upload-text {
            color: #606266;
            font-size: 14px;
          }
        }
        
        .id-card-image {
          width: 100%;
          height: 180px;
          object-fit: cover;
          display: block;
        }
      }
      
      .back-link {
        width: 100%;
        text-align: center;
        color: #909399;
        font-size: 14px;
      }
    }
  }
}
</style>
