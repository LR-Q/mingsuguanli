<template>
  <div class="admin-settings">
    <el-card shadow="never">
      <template #header>
        <h3>系统设置</h3>
      </template>
      
      <el-tabs v-model="activeTab" type="border-card">
        <el-tab-pane label="基本设置" name="basic">
          <el-form :model="basicSettings" label-width="120px">
            <el-form-item label="系统名称">
              <el-input v-model="basicSettings.systemName" />
            </el-form-item>
            <el-form-item label="系统描述">
              <el-input 
                v-model="basicSettings.systemDesc" 
                type="textarea" 
                :rows="3"
              />
            </el-form-item>
            <el-form-item label="联系电话">
              <el-input v-model="basicSettings.contactPhone" />
            </el-form-item>
            <el-form-item label="联系邮箱">
              <el-input v-model="basicSettings.contactEmail" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveBasicSettings">
                保存设置
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="业务设置" name="business">
          <el-form :model="businessSettings" label-width="120px">
            <el-form-item label="默认入住时间">
              <el-time-picker v-model="businessSettings.checkInTime" />
            </el-form-item>
            <el-form-item label="默认退房时间">
              <el-time-picker v-model="businessSettings.checkOutTime" />
            </el-form-item>
            <el-form-item label="提前预订天数">
              <el-input-number 
                v-model="businessSettings.advanceBookingDays" 
                :min="1" 
                :max="365"
              />
            </el-form-item>
            <el-form-item label="取消预订时限">
              <el-input-number 
                v-model="businessSettings.cancelHours" 
                :min="1" 
                :max="72"
              />
              <span style="margin-left: 8px;">小时</span>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveBusinessSettings">
                保存设置
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="安全设置" name="security">
          <el-form :model="securitySettings" label-width="120px">
            <el-form-item label="密码最小长度">
              <el-input-number 
                v-model="securitySettings.minPasswordLength" 
                :min="6" 
                :max="20"
              />
            </el-form-item>
            <el-form-item label="登录失败锁定">
              <el-switch v-model="securitySettings.loginLockEnabled" />
            </el-form-item>
            <el-form-item label="最大失败次数" v-if="securitySettings.loginLockEnabled">
              <el-input-number 
                v-model="securitySettings.maxLoginAttempts" 
                :min="3" 
                :max="10"
              />
            </el-form-item>
            <el-form-item label="锁定时长" v-if="securitySettings.loginLockEnabled">
              <el-input-number 
                v-model="securitySettings.lockDuration" 
                :min="5" 
                :max="60"
              />
              <span style="margin-left: 8px;">分钟</span>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveSecuritySettings">
                保存设置
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const activeTab = ref('basic')

// 基本设置
const basicSettings = reactive({
  systemName: '悦鑫乐怡民宿管理系统',
  systemDesc: '专业的民宿管理解决方案',
  contactPhone: '400-123-4567',
  contactEmail: 'contact@yxly.com'
})

// 业务设置
const businessSettings = reactive({
  checkInTime: new Date(2024, 0, 1, 14, 0), // 14:00
  checkOutTime: new Date(2024, 0, 1, 12, 0), // 12:00
  advanceBookingDays: 30,
  cancelHours: 24
})

// 安全设置
const securitySettings = reactive({
  minPasswordLength: 6,
  loginLockEnabled: true,
  maxLoginAttempts: 5,
  lockDuration: 15
})

// 保存设置的方法
const saveBasicSettings = () => {
  // 这里应该调用API保存设置
  ElMessage.success('基本设置保存成功')
}

const saveBusinessSettings = () => {
  // 这里应该调用API保存设置
  ElMessage.success('业务设置保存成功')
}

const saveSecuritySettings = () => {
  // 这里应该调用API保存设置
  ElMessage.success('安全设置保存成功')
}
</script>

<style lang="scss" scoped>
.admin-settings {
  .el-form {
    max-width: 600px;
  }
  
  .el-form-item {
    margin-bottom: 24px;
  }
}
</style>


