<template>
  <div class="simple-user-layout">
    <!-- 简单的顶部导航 -->
    <header style="background: #409eff; color: white; padding: 15px 0; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
      <div style="max-width: 1200px; margin: 0 auto; padding: 0 20px; display: flex; justify-content: space-between; align-items: center;">
        <h1 style="margin: 0; font-size: 24px;" class="brand-title">悦鑫乐怡民宿</h1>
        <nav style="display: flex; gap: 30px; align-items: center;">
          <router-link to="/home" style="color: white; text-decoration: none; font-weight: 500;">首页</router-link>
          <router-link to="/rooms" style="color: white; text-decoration: none; font-weight: 500;">房间预订</router-link>
          
          <!-- 根据登录状态显示不同内容 -->
          <template v-if="isLoggedIn">
            <el-dropdown @command="handleUserAction" trigger="click">
              <span style="color: white; font-weight: 500; cursor: pointer; padding: 8px 12px; border-radius: 4px; transition: background 0.3s; display: flex; align-items: center; gap: 8px;" 
                    class="user-dropdown-trigger">
                <el-avatar :size="28" :src="userAvatar" style="background: rgba(255,255,255,0.2);">
                  {{ userName.charAt(0) }}
                </el-avatar>
                <span>欢迎，{{ userName }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="wallet">
                    <el-icon><Wallet /></el-icon>
                    我的钱包
                  </el-dropdown-item>
                  <el-dropdown-item command="orders">
                    <el-icon><Document /></el-icon>
                    预订信息
                  </el-dropdown-item>
                  <el-dropdown-item command="favorites">
                    <el-icon><Star /></el-icon>
                    我的收藏
                  </el-dropdown-item>
                  <el-dropdown-item command="settings" divided>
                    <el-icon><Setting /></el-icon>
                    账户设置
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <router-link 
              to="/login" 
              style="color: white; text-decoration: none; font-weight: 500; padding: 8px 16px; border: 1px solid rgba(255,255,255,0.3); border-radius: 4px; transition: all 0.3s;"
              class="login-link"
            >
              登录
            </router-link>
            <router-link 
              to="/register" 
              style="color: white; text-decoration: none; font-weight: 500; padding: 8px 16px; background: rgba(255,255,255,0.2); border-radius: 4px; transition: all 0.3s;"
              class="register-link"
            >
              注册
            </router-link>
          </template>
          
          <!-- 调试按钮（仅在检测到无效状态时显示） -->
          <button 
            v-if="authStore.token && !authStore.userInfo"
            @click="forceLogout"
            style="background: #f56c6c; color: white; border: none; padding: 6px 12px; border-radius: 4px; font-size: 12px; cursor: pointer; margin-left: 16px;"
            title="检测到无效登录状态，点击清除"
          >
            清除无效状态
          </button>
        </nav>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <main style="min-height: calc(100vh - 140px);">
      <router-view />
    </main>

    <!-- 简单的底部 -->
    <footer style="background: #2c3e50; color: white; padding: 20px 0; text-align: center;">
      <p style="margin: 0;">&copy; 2025 悦鑫乐怡民宿管理系统. 由LuoRui开发.</p>
    </footer>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ArrowDown, 
  User, 
  Wallet, 
  Document, 
  Star, 
  Setting, 
  SwitchButton 
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/modules/auth'
import { clearInvalidAuth } from '@/utils/clearInvalidAuth'

const router = useRouter()
const authStore = useAuthStore()

// 计算属性
const isLoggedIn = computed(() => {
  // 检查是否有token且有用户信息
  return authStore.isAuthenticated && authStore.userInfo
})

const userName = computed(() => {
  return authStore.userInfo?.realName || authStore.userInfo?.username || '用户'
})

const userAvatar = computed(() => {
  return authStore.userInfo?.avatar || ''
})

// 处理用户操作
const handleUserAction = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/user-center/profile')
      break
    case 'wallet':
      router.push('/user-center/wallet')
      break
    case 'orders':
      router.push('/user-center/orders')
      break
    case 'favorites':
      router.push('/user-center/favorites')
      break
    case 'settings':
      router.push('/user-center/settings')
      break
    case 'logout':
      await handleLogout()
      break
  }
}

// 处理退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await authStore.logout()
    ElMessage.success('退出登录成功')
    // 退出后跳转到首页
    router.push('/home')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退出登录失败:', error)
      // 即使退出失败，也清除本地状态
      authStore.clearUserInfo()
      router.push('/home')
    }
  }
}

// 强制清除无效登录状态
const forceLogout = () => {
  console.log('手动清除无效登录状态')
  authStore.clearUserInfo()
  clearInvalidAuth()
  ElMessage.success('已清除无效登录状态')
  
  // 刷新页面
  setTimeout(() => {
    window.location.reload()
  }, 500)
}

// 组件挂载时检查登录状态
onMounted(() => {
  // 如果有token但没有用户信息，清除无效token
  if (authStore.token && !authStore.userInfo) {
    console.log('检测到无效token，清除登录状态')
    authStore.clearUserInfo()
    clearInvalidAuth()
    
    // 强制刷新页面以确保状态更新
    setTimeout(() => {
      window.location.reload()
    }, 100)
  }
})
</script>

<style scoped>
.simple-user-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

main {
  flex: 1;
}

/* 路由链接激活状态 */
a.router-link-active {
  background: rgba(255, 255, 255, 0.2);
  padding: 8px 16px;
  border-radius: 4px;
}

/* 用户下拉菜单触发器悬停效果 */
.user-dropdown-trigger:hover {
  background: rgba(255, 255, 255, 0.1) !important;
}

/* 登录相关按钮样式 */
.login-link:hover {
  background: rgba(255, 255, 255, 0.1) !important;
  border-color: rgba(255, 255, 255, 0.5) !important;
}

.register-link:hover {
  background: rgba(255, 255, 255, 0.3) !important;
}

/* 下拉菜单样式 */
:deep(.el-dropdown-menu) {
  .el-dropdown-menu__item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 16px;
    
    .el-icon {
      font-size: 16px;
      color: #409eff;
    }
    
    &:hover {
      background: #f0f8ff;
      color: #409eff;
    }
  }
}
</style>
