<template>
  <div class="settings-page">
    <!-- 账户设置 -->
    <el-card class="settings-card" shadow="never">
      <template #header>
        <h3>账户设置</h3>
      </template>
      
      <div class="settings-section">
        <div class="setting-item">
          <div class="setting-info">
            <h4>账户状态</h4>
            <p>当前账户状态：{{ userInfo.status === 1 ? '正常' : '已禁用' }}</p>
          </div>
          <el-tag :type="userInfo.status === 1 ? 'success' : 'danger'">
            {{ userInfo.status === 1 ? '正常' : '已禁用' }}
          </el-tag>
        </div>
        
        <div class="setting-item">
          <div class="setting-info">
            <h4>账户注销</h4>
            <p>注销后将无法恢复，请谨慎操作</p>
          </div>
          <el-button type="danger" plain @click="handleDeleteAccount">
            注销账户
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 隐私设置 -->
    <el-card class="settings-card" shadow="never">
      <template #header>
        <h3>隐私设置</h3>
      </template>
      
      <div class="settings-section">
        <div class="setting-item">
          <div class="setting-info">
            <h4>个人信息公开</h4>
            <p>是否允许其他用户查看您的个人信息</p>
          </div>
          <el-switch 
            v-model="privacySettings.profilePublic" 
            @change="updatePrivacySetting('profilePublic')"
          />
        </div>
        
        <div class="setting-item">
          <div class="setting-info">
            <h4>显示真实姓名</h4>
            <p>在评价和互动中是否显示真实姓名</p>
          </div>
          <el-switch 
            v-model="privacySettings.showRealName" 
            @change="updatePrivacySetting('showRealName')"
          />
        </div>
        
        <div class="setting-item">
          <div class="setting-info">
            <h4>接收推荐</h4>
            <p>是否接收个性化房间推荐</p>
          </div>
          <el-switch 
            v-model="privacySettings.receiveRecommendations" 
            @change="updatePrivacySetting('receiveRecommendations')"
          />
        </div>
      </div>
    </el-card>

    <!-- 通知设置 -->
    <el-card class="settings-card" shadow="never">
      <template #header>
        <h3>通知设置</h3>
      </template>
      
      <div class="settings-section">
        <div class="setting-item">
          <div class="setting-info">
            <h4>邮件通知</h4>
            <p>接收订单状态、优惠活动等邮件通知</p>
          </div>
          <el-switch 
            v-model="notificationSettings.emailNotification" 
            @change="updateNotificationSetting('emailNotification')"
          />
        </div>
        
        <div class="setting-item">
          <div class="setting-info">
            <h4>短信通知</h4>
            <p>接收订单确认、入住提醒等短信通知</p>
          </div>
          <el-switch 
            v-model="notificationSettings.smsNotification" 
            @change="updateNotificationSetting('smsNotification')"
          />
        </div>
        
        <div class="setting-item">
          <div class="setting-info">
            <h4>推送通知</h4>
            <p>接收浏览器推送通知</p>
          </div>
          <el-switch 
            v-model="notificationSettings.pushNotification" 
            @change="updateNotificationSetting('pushNotification')"
          />
        </div>
        
        <div class="setting-item">
          <div class="setting-info">
            <h4>营销信息</h4>
            <p>接收优惠活动、新房源等营销信息</p>
          </div>
          <el-switch 
            v-model="notificationSettings.marketingNotification" 
            @change="updateNotificationSetting('marketingNotification')"
          />
        </div>
      </div>
    </el-card>

    <!-- 偏好设置 -->
    <el-card class="settings-card" shadow="never">
      <template #header>
        <h3>偏好设置</h3>
      </template>
      
      <div class="settings-section">
        <div class="setting-item">
          <div class="setting-info">
            <h4>语言设置</h4>
            <p>选择您的首选语言</p>
          </div>
          <el-select 
            v-model="preferenceSettings.language" 
            style="width: 120px"
            @change="updatePreferenceSetting('language')"
          >
            <el-option label="简体中文" value="zh-CN" />
            <el-option label="繁體中文" value="zh-TW" />
            <el-option label="English" value="en-US" />
          </el-select>
        </div>
        
        <div class="setting-item">
          <div class="setting-info">
            <h4>货币单位</h4>
            <p>选择您的首选货币</p>
          </div>
          <el-select 
            v-model="preferenceSettings.currency" 
            style="width: 120px"
            @change="updatePreferenceSetting('currency')"
          >
            <el-option label="人民币 (¥)" value="CNY" />
            <el-option label="美元 ($)" value="USD" />
            <el-option label="欧元 (€)" value="EUR" />
          </el-select>
        </div>
        
        <div class="setting-item">
          <div class="setting-info">
            <h4>主题模式</h4>
            <p>选择您喜欢的界面主题</p>
          </div>
          <el-radio-group 
            v-model="preferenceSettings.theme" 
            @change="updatePreferenceSetting('theme')"
          >
            <el-radio label="light">浅色</el-radio>
            <el-radio label="dark">深色</el-radio>
            <el-radio label="auto">跟随系统</el-radio>
          </el-radio-group>
        </div>
        
        <div class="setting-item">
          <div class="setting-info">
            <h4>房间偏好</h4>
            <p>设置您的房间类型偏好</p>
          </div>
          <el-select 
            v-model="preferenceSettings.roomPreferences" 
            multiple
            placeholder="选择偏好的房间类型"
            style="width: 200px"
            @change="updatePreferenceSetting('roomPreferences')"
          >
            <el-option label="海景房" value="sea-view" />
            <el-option label="山景房" value="mountain-view" />
            <el-option label="豪华套房" value="luxury-suite" />
            <el-option label="商务房" value="business-room" />
            <el-option label="经济房" value="economy-room" />
          </el-select>
        </div>
      </div>
    </el-card>

    <!-- 数据管理 -->
    <el-card class="settings-card" shadow="never">
      <template #header>
        <h3>数据管理</h3>
      </template>
      
      <div class="settings-section">
        <div class="setting-item">
          <div class="setting-info">
            <h4>导出数据</h4>
            <p>导出您的个人数据和订单记录</p>
          </div>
          <el-button @click="handleExportData">
            导出数据
          </el-button>
        </div>
        
        <div class="setting-item">
          <div class="setting-info">
            <h4>清除缓存</h4>
            <p>清除本地缓存数据，提升应用性能</p>
          </div>
          <el-button @click="handleClearCache">
            清除缓存
          </el-button>
        </div>
        
        <div class="setting-item">
          <div class="setting-info">
            <h4>删除浏览记录</h4>
            <p>删除您的房间浏览记录</p>
          </div>
          <el-button type="warning" plain @click="handleClearHistory">
            删除记录
          </el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/modules/auth'

const authStore = useAuthStore()

// 用户信息
const userInfo = reactive({
  status: 1
})

// 隐私设置
const privacySettings = reactive({
  profilePublic: true,
  showRealName: false,
  receiveRecommendations: true
})

// 通知设置
const notificationSettings = reactive({
  emailNotification: true,
  smsNotification: true,
  pushNotification: false,
  marketingNotification: false
})

// 偏好设置
const preferenceSettings = reactive({
  language: 'zh-CN',
  currency: 'CNY',
  theme: 'light',
  roomPreferences: ['sea-view', 'luxury-suite']
})

// 更新隐私设置
const updatePrivacySetting = async (key) => {
  try {
    // 这里调用API更新设置
    await new Promise(resolve => setTimeout(resolve, 500))
    
    ElMessage.success('设置已更新')
  } catch (error) {
    console.error('更新设置失败:', error)
    ElMessage.error('设置更新失败')
    
    // 回滚设置
    privacySettings[key] = !privacySettings[key]
  }
}

// 更新通知设置
const updateNotificationSetting = async (key) => {
  try {
    // 这里调用API更新设置
    await new Promise(resolve => setTimeout(resolve, 500))
    
    ElMessage.success('设置已更新')
  } catch (error) {
    console.error('更新设置失败:', error)
    ElMessage.error('设置更新失败')
    
    // 回滚设置
    notificationSettings[key] = !notificationSettings[key]
  }
}

// 更新偏好设置
const updatePreferenceSetting = async (key) => {
  try {
    // 这里调用API更新设置
    await new Promise(resolve => setTimeout(resolve, 500))
    
    ElMessage.success('设置已更新')
    
    // 如果是主题设置，可以在这里应用主题
    if (key === 'theme') {
      applyTheme(preferenceSettings.theme)
    }
  } catch (error) {
    console.error('更新设置失败:', error)
    ElMessage.error('设置更新失败')
  }
}

// 应用主题
const applyTheme = (theme) => {
  // 这里实现主题切换逻辑
  console.log('应用主题:', theme)
}

// 注销账户
const handleDeleteAccount = async () => {
  try {
    await ElMessageBox.confirm(
      '注销账户后，您的所有数据将被永久删除且无法恢复。确定要继续吗？',
      '注销账户',
      {
        confirmButtonText: '确定注销',
        cancelButtonText: '取消',
        type: 'error',
        dangerouslyUseHTMLString: true
      }
    )

    // 二次确认
    await ElMessageBox.prompt(
      '请输入 "确认注销" 来确认此操作',
      '最终确认',
      {
        confirmButtonText: '注销账户',
        cancelButtonText: '取消',
        inputPattern: /^确认注销$/,
        inputErrorMessage: '请输入 "确认注销"'
      }
    )

    // 这里调用API注销账户
    await new Promise(resolve => setTimeout(resolve, 2000))

    ElMessage.success('账户已注销')
    
    // 清除登录状态并跳转到首页
    await authStore.logout()
    
  } catch (error) {
    // 用户取消操作
  }
}

// 导出数据
const handleExportData = async () => {
  try {
    ElMessage.info('正在准备导出数据...')
    
    // 这里调用API导出数据
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // 模拟下载文件
    const link = document.createElement('a')
    link.href = 'data:text/plain;charset=utf-8,' + encodeURIComponent('用户数据导出文件')
    link.download = `user_data_${new Date().getTime()}.txt`
    link.click()
    
    ElMessage.success('数据导出成功')
  } catch (error) {
    ElMessage.error('数据导出失败')
  }
}

// 清除缓存
const handleClearCache = async () => {
  try {
    await ElMessageBox.confirm('确定要清除本地缓存吗？', '清除缓存', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 清除localStorage和sessionStorage
    localStorage.clear()
    sessionStorage.clear()
    
    // 清除浏览器缓存（如果支持）
    if ('caches' in window) {
      const cacheNames = await caches.keys()
      await Promise.all(cacheNames.map(name => caches.delete(name)))
    }
    
    ElMessage.success('缓存已清除')
  } catch (error) {
    // 用户取消操作
  }
}

// 清除浏览记录
const handleClearHistory = async () => {
  try {
    await ElMessageBox.confirm('确定要删除浏览记录吗？', '删除记录', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 这里调用API清除浏览记录
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    ElMessage.success('浏览记录已删除')
  } catch (error) {
    // 用户取消操作
  }
}

// 初始化设置
const initSettings = () => {
  // 从用户信息中获取设置
  const user = authStore.userInfo
  if (user) {
    userInfo.status = user.status || 1
    
    // 这里可以从API获取用户的设置信息
    // 暂时使用默认值
  }
}

onMounted(() => {
  initSettings()
})
</script>

<style lang="scss" scoped>
.settings-page {
  .settings-card {
    margin-bottom: 20px;
    
    .settings-section {
      .setting-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 20px 0;
        border-bottom: 1px solid #ebeef5;
        
        &:last-child {
          border-bottom: none;
        }
        
        .setting-info {
          flex: 1;
          
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
            line-height: 1.5;
          }
        }
      }
    }
  }
}

:deep(.el-card__header) {
  background: #fafafa;
  border-bottom: 1px solid #ebeef5;
  
  h3 {
    margin: 0;
    color: #303133;
    font-size: 18px;
    font-weight: 600;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .setting-item {
    flex-direction: column !important;
    align-items: flex-start !important;
    gap: 12px;
    
    .setting-info {
      width: 100%;
    }
  }
}
</style>
