<template>
  <div class="user-profile">
    <div class="container">
      <div class="profile-header">
        <h1>个人中心</h1>
        <p>管理您的个人信息和账户设置</p>
      </div>

      <div class="profile-content">
        <div class="profile-sidebar">
          <el-menu :default-active="activeTab" @select="handleTabSelect">
            <el-menu-item index="info">
              <el-icon><User /></el-icon>
              <span>基本信息</span>
            </el-menu-item>
            <el-menu-item index="security">
              <el-icon><Lock /></el-icon>
              <span>安全设置</span>
            </el-menu-item>
            <el-menu-item index="preferences">
              <el-icon><Setting /></el-icon>
              <span>偏好设置</span>
            </el-menu-item>
          </el-menu>
        </div>

        <div class="profile-main">
          <!-- 基本信息 -->
          <div v-show="activeTab === 'info'" class="tab-content">
            <el-card>
              <template #header>
                <h2>基本信息</h2>
              </template>
              <el-form :model="userForm" :rules="userRules" ref="userFormRef" label-width="100px">
                <el-form-item label="头像">
                  <div class="avatar-section">
                    <el-avatar :size="80" :src="normalizeUrl(userForm.avatar)">
                      {{ userForm.realName?.charAt(0) || userForm.username?.charAt(0) }}
                    </el-avatar>
                    <el-button size="small" @click="uploadAvatar">更换头像</el-button>
                  </div>
                </el-form-item>
                
                <el-form-item label="用户名" prop="username">
                  <el-input v-model="userForm.username" disabled />
                </el-form-item>
                
                <el-form-item label="真实姓名" prop="realName">
                  <el-input v-model="userForm.realName" placeholder="请输入真实姓名" />
                </el-form-item>
                
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="userForm.email" placeholder="请输入邮箱" />
                </el-form-item>
                
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="userForm.phone" placeholder="请输入手机号" />
                </el-form-item>
                
                <el-form-item>
                  <el-button type="primary" @click="updateProfile" :loading="updating">
                    保存修改
                  </el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </div>

          <!-- 安全设置 -->
          <div v-show="activeTab === 'security'" class="tab-content">
            <el-card>
              <template #header>
                <h2>安全设置</h2>
              </template>
              <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
                <el-form-item label="当前密码" prop="currentPassword">
                  <el-input
                    v-model="passwordForm.currentPassword"
                    type="password"
                    placeholder="请输入当前密码"
                    show-password
                  />
                </el-form-item>
                
                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    placeholder="请输入新密码"
                    show-password
                  />
                </el-form-item>
                
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    placeholder="请再次输入新密码"
                    show-password
                  />
                </el-form-item>
                
                <el-form-item>
                  <el-button type="primary" @click="changePassword" :loading="changingPassword">
                    修改密码
                  </el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </div>

          <!-- 偏好设置 -->
          <div v-show="activeTab === 'preferences'" class="tab-content">
            <el-card>
              <template #header>
                <h2>偏好设置</h2>
              </template>
              <el-form :model="preferencesForm" label-width="120px">
                <el-form-item label="通知设置">
                  <el-switch
                    v-model="preferencesForm.emailNotification"
                    active-text="邮件通知"
                  />
                </el-form-item>
                
                <el-form-item label="">
                  <el-switch
                    v-model="preferencesForm.smsNotification"
                    active-text="短信通知"
                  />
                </el-form-item>
                
                <el-form-item label="房间偏好">
                  <el-checkbox-group v-model="preferencesForm.roomPreferences">
                    <el-checkbox label="海景房">海景房</el-checkbox>
                    <el-checkbox label="安静环境">安静环境</el-checkbox>
                    <el-checkbox label="高楼层">高楼层</el-checkbox>
                    <el-checkbox label="靠近电梯">靠近电梯</el-checkbox>
                  </el-checkbox-group>
                </el-form-item>
                
                <el-form-item>
                  <el-button type="primary" @click="savePreferences" :loading="savingPreferences">
                    保存设置
                  </el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Lock, Setting } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/modules/auth'

const authStore = useAuthStore()

// 响应式数据
const activeTab = ref('info')
const userFormRef = ref()
const passwordFormRef = ref()
const updating = ref(false)
const changingPassword = ref(false)
const savingPreferences = ref(false)

// 用户信息表单
const userForm = reactive({
  username: '',
  realName: '',
  email: '',
  phone: '',
  avatar: ''
})

const normalizeUrl = (url) => {
  if (!url) return ''
  return url.replace(/^http:\/\/localhost:9000\/txt\//, '/txt/')
}

// 密码修改表单
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 偏好设置表单
const preferencesForm = reactive({
  emailNotification: true,
  smsNotification: false,
  roomPreferences: []
})

// 表单验证规则
const userRules = {
  realName: [
    { max: 50, message: '姓名长度不能超过50个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

const passwordRules = {
  currentPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符', trigger: 'blur' },
    { pattern: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d@$!%*#?&]{6,}$/, message: '密码至少包含一个字母和一个数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 切换标签
const handleTabSelect = (key) => {
  activeTab.value = key
}

// 上传头像
const uploadAvatar = () => {
  ElMessage.info('头像上传功能开发中...')
}

// 更新个人信息
const updateProfile = async () => {
  if (!userFormRef.value) return
  
  try {
    await userFormRef.value.validate()
    
    updating.value = true
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('个人信息更新成功')
    
    // 更新store中的用户信息
    authStore.setUserInfo({
      ...authStore.userInfo,
      ...userForm
    })
    
  } catch (error) {
    console.error('更新失败:', error)
    ElMessage.error('更新失败，请重试')
  } finally {
    updating.value = false
  }
}

// 修改密码
const changePassword = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
    
    changingPassword.value = true
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('密码修改成功')
    
    // 清空表单
    Object.assign(passwordForm, {
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    })
    
  } catch (error) {
    console.error('密码修改失败:', error)
    ElMessage.error('密码修改失败，请重试')
  } finally {
    changingPassword.value = false
  }
}

// 保存偏好设置
const savePreferences = async () => {
  try {
    savingPreferences.value = true
    
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('偏好设置保存成功')
    
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败，请重试')
  } finally {
    savingPreferences.value = false
  }
}

onMounted(() => {
  // 初始化用户信息
  if (authStore.userInfo) {
    Object.assign(userForm, {
      username: authStore.userInfo.username,
      realName: authStore.userInfo.realName || '',
      email: authStore.userInfo.email || '',
      phone: authStore.userInfo.phone || '',
      avatar: authStore.userInfo.avatar || ''
    })
  }
})
</script>

<style lang="scss" scoped>
.user-profile {
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
  }
}

.profile-header {
  text-align: center;
  margin-bottom: 40px;
  
  h1 {
    margin: 0 0 16px 0;
    font-size: 32px;
    color: #333;
  }
  
  p {
    margin: 0;
    color: #666;
    font-size: 16px;
  }
}

.profile-content {
  display: grid;
  grid-template-columns: 250px 1fr;
  gap: 30px;
  align-items: start;
}

.profile-sidebar {
  .el-menu {
    border-right: none;
    background: #f8f9fa;
    border-radius: 8px;
  }
}

.profile-main {
  .tab-content {
    .el-card {
      border-radius: 8px;
    }
  }
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

// 响应式设计
@media (max-width: 768px) {
  .profile-content {
    grid-template-columns: 1fr;
  }
  
  .profile-sidebar {
    .el-menu {
      display: flex;
      overflow-x: auto;
    }
  }
}
</style>
