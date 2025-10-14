<template>
  <div class="simple-user-layout">
    <!-- 简单的顶部导航 -->
    <header style="background: #409eff; color: white; padding: 15px 0; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
      <div style="max-width: 1200px; margin: 0 auto; padding: 0 20px; display: flex; justify-content: space-between; align-items: center;">
        <h1 style="margin: 0; font-size: 24px;">悦鑫乐怡民宿</h1>
        <nav style="display: flex; gap: 30px; align-items: center;">
          <router-link to="/home" style="color: white; text-decoration: none; font-weight: 500;">首页</router-link>
          <router-link to="/rooms" style="color: white; text-decoration: none; font-weight: 500;">房间预订</router-link>
          
          <!-- 根据登录状态显示不同内容 -->
          <template v-if="isLoggedIn">
            <span style="color: white; font-weight: 500;">欢迎，{{ userName }}</span>
            <button 
              @click="handleLogout" 
              style="background: rgba(255,255,255,0.2); color: white; border: 1px solid rgba(255,255,255,0.3); padding: 6px 12px; border-radius: 4px; cursor: pointer; font-weight: 500;"
            >
              退出登录
            </button>
          </template>
          <template v-else>
            <router-link to="/login" style="color: white; text-decoration: none; font-weight: 500;">登录</router-link>
            <router-link to="/register" style="color: white; text-decoration: none; font-weight: 500;">注册</router-link>
            <router-link to="/admin-login" style="color: rgba(255,255,255,0.8); text-decoration: none; font-weight: 400; font-size: 14px;">管理员入口</router-link>
          </template>
        </nav>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <main style="min-height: calc(100vh - 140px);">
      <router-view />
    </main>

    <!-- 简单的底部 -->
    <footer style="background: #2c3e50; color: white; padding: 20px 0; text-align: center;">
      <p style="margin: 0;">&copy; 2024 悦鑫乐怡民宿管理系统. All rights reserved.</p>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/modules/auth'

const router = useRouter()
const authStore = useAuthStore()

// 计算属性
const isLoggedIn = computed(() => {
  return authStore.isAuthenticated
})

const userName = computed(() => {
  return authStore.userInfo?.realName || authStore.userInfo?.username || '用户'
})

// 处理退出登录
const handleLogout = async () => {
  try {
    await authStore.logout()
    // 退出后跳转到首页
    router.push('/home')
  } catch (error) {
    console.error('退出登录失败:', error)
    // 即使退出失败，也清除本地状态
    authStore.clearUserInfo()
    router.push('/home')
  }
}
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
</style>
