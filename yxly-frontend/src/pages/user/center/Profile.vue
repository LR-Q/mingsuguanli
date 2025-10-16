<template>
  <div class="profile-page">
    <!-- 个人信息卡片 -->
    <el-card class="profile-card" shadow="never">
      <template #header>
        <div class="card-header">
          <h3>个人信息</h3>
          <el-button type="primary" @click="editMode = !editMode">
            {{ editMode ? '取消编辑' : '编辑信息' }}
          </el-button>
        </div>
      </template>
      
      <el-form 
        ref="profileFormRef"
        :model="profileForm" 
        :rules="profileRules"
        label-width="100px"
        :disabled="!editMode"
      >
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="profileForm.username" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="profileForm.realName" placeholder="请输入真实姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="profileForm.email" placeholder="请输入邮箱地址" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="profileForm.phone" placeholder="请输入手机号码" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="profileForm.gender">
                <el-radio :label="1">男</el-radio>
                <el-radio :label="2">女</el-radio>
                <el-radio :label="0">保密</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生日" prop="birthday">
              <el-date-picker
                v-model="profileForm.birthday"
                type="date"
                placeholder="选择生日"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="个人简介" prop="bio">
          <el-input
            v-model="profileForm.bio"
            type="textarea"
            :rows="4"
            placeholder="介绍一下自己吧..."
          />
        </el-form-item>
        
        <el-form-item v-if="editMode">
          <el-button type="primary" @click="handleSave" :loading="saving">
            保存修改
          </el-button>
          <el-button @click="handleCancel">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 头像设置 -->
    <el-card class="avatar-card" shadow="never">
      <template #header>
        <h3>头像设置</h3>
      </template>
      
      <div class="avatar-section">
        <div class="avatar-preview">
          <el-avatar :size="120" :src="profileForm.avatar">
            {{ profileForm.realName?.charAt(0) || profileForm.username?.charAt(0) }}
          </el-avatar>
        </div>
        <div class="avatar-actions">
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :before-upload="beforeAvatarUpload"
            :http-request="handleAvatarUpload"
          >
            <el-button type="primary">上传头像</el-button>
          </el-upload>
          <p class="upload-tip">支持 JPG、PNG 格式，文件大小不超过 2MB</p>
        </div>
      </div>
    </el-card>

    <!-- 安全设置 -->
    <el-card class="security-card" shadow="never">
      <template #header>
        <h3>安全设置</h3>
      </template>
      
      <div class="security-items">
        <div class="security-item">
          <div class="item-info">
            <h4>登录密码</h4>
            <p>定期更换密码，保护账户安全</p>
          </div>
          <el-button type="primary" plain @click="showChangePassword = true">
            修改密码
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="showChangePassword" title="修改密码" width="400px">
      <el-form 
        ref="passwordFormRef"
        :model="passwordForm" 
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input 
            v-model="passwordForm.oldPassword" 
            type="password" 
            show-password
            placeholder="请输入当前密码"
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input 
            v-model="passwordForm.newPassword" 
            type="password" 
            show-password
            placeholder="请输入新密码"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="passwordForm.confirmPassword" 
            type="password" 
            show-password
            placeholder="请再次输入新密码"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showChangePassword = false">取消</el-button>
        <el-button type="primary" @click="handleChangePassword" :loading="changingPassword">
          确认修改
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/modules/auth'
import { changePassword } from '@/api/modules/auth'

const authStore = useAuthStore()

// 响应式数据
const editMode = ref(false)
const saving = ref(false)
const showChangePassword = ref(false)
const changingPassword = ref(false)

const profileFormRef = ref()
const passwordFormRef = ref()

// 个人信息表单
const profileForm = reactive({
  username: '',
  realName: '',
  email: '',
  phone: '',
  gender: 0,
  birthday: null,
  bio: '',
  avatar: '',
  emailVerified: false,
  phoneVerified: false
})

// 原始数据备份
const originalData = reactive({})

// 密码修改表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 表单验证规则
const profileRules = {
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' },
    { 
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{6,}$/, 
      message: '密码必须包含大小写字母和数字', 
      trigger: 'blur' 
    }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 初始化数据
const initData = () => {
  const userInfo = authStore.userInfo
  if (userInfo) {
    Object.assign(profileForm, {
      username: userInfo.username || '',
      realName: userInfo.realName || '',
      email: userInfo.email || '',
      phone: userInfo.phone || '',
      gender: userInfo.gender || 0,
      birthday: userInfo.birthday ? new Date(userInfo.birthday) : null,
      bio: userInfo.bio || '',
      avatar: userInfo.avatar || '',
      emailVerified: userInfo.emailVerified || false,
      phoneVerified: userInfo.phoneVerified || false
    })
    
    // 备份原始数据
    Object.assign(originalData, profileForm)
  }
}

// 保存修改
const handleSave = async () => {
  try {
    await profileFormRef.value.validate()
    
    saving.value = true
    
    // 这里调用API保存用户信息
    // await updateUserProfile(profileForm)
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('个人信息更新成功')
    editMode.value = false
    
    // 更新store中的用户信息
    authStore.updateUserInfo(profileForm)
    
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

// 取消编辑
const handleCancel = () => {
  Object.assign(profileForm, originalData)
  editMode.value = false
}

// 头像上传前验证
const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('头像只能是 JPG/PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
    return false
  }
  return true
}

// 头像上传处理
const handleAvatarUpload = async (options) => {
  try {
    // 这里实现头像上传逻辑
    // const formData = new FormData()
    // formData.append('avatar', options.file)
    // const response = await uploadAvatar(formData)
    
    // 模拟上传成功
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 更新头像URL
    profileForm.avatar = URL.createObjectURL(options.file)
    
    ElMessage.success('头像上传成功')
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败，请重试')
  }
}

// 修改密码
const handleChangePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    
    changingPassword.value = true
    
    // 调用API修改密码
    await changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    
    ElMessage.success('密码修改成功，请重新登录')
    showChangePassword.value = false
    
    // 重置表单
    Object.assign(passwordForm, {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    })
    
    // 密码修改成功后，清除登录状态并跳转到登录页
    setTimeout(async () => {
      await authStore.logout()
    }, 1500)
    
  } catch (error) {
    console.error('密码修改失败:', error)
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('密码修改失败，请检查当前密码是否正确')
    }
  } finally {
    changingPassword.value = false
  }
}


// 组件挂载时初始化数据
onMounted(() => {
  initData()
})
</script>

<style lang="scss" scoped>
.profile-page {
  .profile-card,
  .avatar-card,
  .security-card {
    margin-bottom: 20px;
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      h3 {
        margin: 0;
        color: #303133;
        font-size: 18px;
        font-weight: 600;
      }
    }
  }
  
  .avatar-section {
    display: flex;
    align-items: center;
    gap: 32px;
    
    .avatar-preview {
      text-align: center;
    }
    
    .avatar-actions {
      flex: 1;
      
      .upload-tip {
        margin: 8px 0 0 0;
        color: #909399;
        font-size: 12px;
      }
    }
  }
  
  .security-items {
    .security-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20px 0;
      border-bottom: 1px solid #ebeef5;
      
      &:last-child {
        border-bottom: none;
      }
      
      .item-info {
        h4 {
          margin: 0 0 4px 0;
          color: #303133;
          font-size: 16px;
          font-weight: 500;
        }
        
        p {
          margin: 0;
          color: #909399;
          font-size: 14px;
        }
      }
    }
  }
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-card__header) {
  background: #fafafa;
  border-bottom: 1px solid #ebeef5;
}
</style>
