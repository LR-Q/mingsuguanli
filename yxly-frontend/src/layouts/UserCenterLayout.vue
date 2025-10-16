<template>
  <div class="user-center-layout">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-container">
        <div class="logo">
          <router-link to="/home">
            <h1>悦鑫乐怡民宿</h1>
          </router-link>
        </div>
        
        <nav class="nav-menu">
          <router-link to="/home" class="nav-item">首页</router-link>
          <router-link to="/rooms" class="nav-item">房间预订</router-link>
        </nav>
        
        <div class="user-actions">
          <el-dropdown @command="handleUserAction">
            <span class="user-info">
              <el-avatar :size="32" :src="userAvatar">
                {{ userName.charAt(0) }}
              </el-avatar>
              <span class="username">{{ userName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout" divided>
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <div class="main-container">
      <!-- 侧边栏导航 -->
      <aside class="sidebar">
        <div class="sidebar-header">
          <el-avatar :size="60" :src="userAvatar">
            {{ userName.charAt(0) }}
          </el-avatar>
          <div class="user-info">
            <h3>{{ userName }}</h3>
            <p class="user-email">{{ userEmail }}</p>
          </div>
        </div>
        
        <nav class="sidebar-nav">
          <router-link 
            v-for="item in menuItems" 
            :key="item.path"
            :to="item.path" 
            class="nav-item"
            :class="{ active: $route.path === item.path }"
          >
            <el-icon>
              <component :is="item.icon" />
            </el-icon>
            <span>{{ item.title }}</span>
          </router-link>
        </nav>
      </aside>

      <!-- 主要内容区域 -->
      <main class="main-content">
        <div class="content-header">
          <h2>{{ currentPageTitle }}</h2>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>个人中心</el-breadcrumb-item>
            <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="content-body">
          <router-view />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ArrowDown, 
  SwitchButton, 
  User, 
  Wallet, 
  Document, 
  Star, 
  Setting 
} from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/modules/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

// 菜单项配置
const menuItems = [
  {
    path: '/user-center/profile',
    title: '个人信息',
    icon: 'User'
  },
  {
    path: '/user-center/wallet',
    title: '我的钱包',
    icon: 'Wallet'
  },
  {
    path: '/user-center/orders',
    title: '预订信息',
    icon: 'Document'
  },
  {
    path: '/user-center/favorites',
    title: '我的收藏',
    icon: 'Star'
  },
  {
    path: '/user-center/settings',
    title: '账户设置',
    icon: 'Setting'
  }
]

// 计算属性
const userName = computed(() => {
  return authStore.userInfo?.realName || authStore.userInfo?.username || '用户'
})

const userEmail = computed(() => {
  return authStore.userInfo?.email || ''
})

const userAvatar = computed(() => {
  return authStore.userInfo?.avatar || ''
})

const currentPageTitle = computed(() => {
  const currentItem = menuItems.find(item => item.path === route.path)
  return currentItem?.title || '个人中心'
})

// 处理用户操作
const handleUserAction = async (command) => {
  switch (command) {
    case 'logout':
      try {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await authStore.logout()
        ElMessage.success('退出登录成功')
        router.push('/home')
      } catch (error) {
        // 用户取消操作
      }
      break
  }
}
</script>

<style lang="scss" scoped>
.user-center-layout {
  min-height: 100vh;
  background: #f5f7fa;
}

.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
  
  .header-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 60px;
  }
  
  .logo {
    a {
      text-decoration: none;
    }
    
    h1 {
      margin: 0;
      color: #409eff;
      font-size: 20px;
      font-weight: bold;
    }
  }
  
  .nav-menu {
    display: flex;
    gap: 24px;
    
    .nav-item {
      text-decoration: none;
      color: #333;
      font-weight: 500;
      padding: 8px 16px;
      border-radius: 4px;
      transition: all 0.3s;
      
      &:hover, &.router-link-active {
        color: #409eff;
        background: #f0f8ff;
      }
    }
  }
  
  .user-actions {
    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      padding: 8px 12px;
      border-radius: 6px;
      transition: background 0.3s;
      
      &:hover {
        background: #f5f5f5;
      }
      
      .username {
        font-weight: 500;
        color: #333;
      }
    }
  }
}

.main-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  display: flex;
  gap: 20px;
}

.sidebar {
  width: 280px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  height: fit-content;
  position: sticky;
  top: 100px;
  
  .sidebar-header {
    padding: 24px;
    text-align: center;
    border-bottom: 1px solid #ebeef5;
    
    .user-info {
      margin-top: 16px;
      
      h3 {
        margin: 0 0 8px 0;
        color: #303133;
        font-size: 18px;
      }
      
      .user-email {
        margin: 0;
        color: #909399;
        font-size: 14px;
      }
    }
  }
  
  .sidebar-nav {
    padding: 16px 0;
    
    .nav-item {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 12px 24px;
      color: #606266;
      text-decoration: none;
      transition: all 0.3s;
      
      .el-icon {
        font-size: 18px;
      }
      
      span {
        font-weight: 500;
      }
      
      &:hover {
        background: #f0f8ff;
        color: #409eff;
      }
      
      &.active {
        background: linear-gradient(90deg, #409eff 0%, rgba(64, 158, 255, 0.1) 100%);
        color: #409eff;
        border-right: 3px solid #409eff;
      }
    }
  }
}

.main-content {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  
  .content-header {
    padding: 24px 24px 16px;
    border-bottom: 1px solid #ebeef5;
    
    h2 {
      margin: 0 0 12px 0;
      color: #303133;
      font-size: 24px;
      font-weight: 600;
    }
  }
  
  .content-body {
    padding: 24px;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .main-container {
    flex-direction: column;
    padding: 16px;
  }
  
  .sidebar {
    width: 100%;
    position: static;
    
    .sidebar-nav {
      display: flex;
      overflow-x: auto;
      padding: 8px 0;
      
      .nav-item {
        flex-shrink: 0;
        padding: 8px 16px;
        white-space: nowrap;
      }
    }
  }
  
  .header {
    .nav-menu {
      display: none;
    }
  }
}

// 下拉菜单样式
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
