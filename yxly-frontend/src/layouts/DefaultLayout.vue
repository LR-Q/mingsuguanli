<template>
  <div class="default-layout">
    <!-- 侧边栏 -->
    <el-aside :width="sidebarWidth" class="sidebar">
      <div class="logo">
        <span v-if="!appStore.sidebarCollapsed">悦鑫乐怡民宿</span>
        <span v-else class="logo-collapsed">悦</span>
      </div>
      
      <el-menu
        :default-active="$route.path"
        :collapse="appStore.sidebarCollapsed"
        :unique-opened="true"
        router
        class="sidebar-menu"
      >
        <template v-for="route in menuRoutes" :key="route.path">
          <el-sub-menu 
            v-if="route.children && route.children.length > 1" 
            :index="route.path"
          >
            <template #title>
              <el-icon v-if="route.meta?.icon">
                <component :is="route.meta.icon" />
              </el-icon>
              <span>{{ route.meta?.title }}</span>
            </template>
            
            <el-menu-item
              v-for="child in route.children"
              :key="child.path"
              :index="child.path"
              v-show="!child.meta?.hideInMenu"
            >
              {{ child.meta?.title }}
            </el-menu-item>
          </el-sub-menu>
          
          <el-menu-item 
            v-else 
            :index="getMenuIndex(route)"
            v-show="!route.meta?.hideInMenu"
          >
            <el-icon v-if="route.meta?.icon">
              <component :is="route.meta.icon" />
            </el-icon>
            <template #title>{{ getMenuTitle(route) }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>
    
    <!-- 主要内容区域 -->
    <el-container class="main-container">
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <el-button
            type="text"
            @click="toggleSidebar"
            class="sidebar-toggle"
          >
            <el-icon>
              <Fold v-if="!appStore.sidebarCollapsed" />
              <Expand v-else />
            </el-icon>
          </el-button>
          
          <el-breadcrumb separator="/">
            <el-breadcrumb-item 
              v-for="item in breadcrumbs" 
              :key="item.path"
              :to="item.path"
            >
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userAvatar">
                {{ userName }}
              </el-avatar>
              <span class="username">{{ userName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="settings">系统设置</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <!-- 页面内容 -->
      <el-main class="content">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useAppStore } from '@/stores/modules/app'
import { useAuthStore } from '@/stores/modules/auth'

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()
const authStore = useAuthStore()

// 计算属性
const sidebarWidth = computed(() => {
  return appStore.sidebarCollapsed ? '64px' : '200px'
})

const userName = computed(() => {
  return authStore.userInfo?.realName || authStore.userInfo?.username || '用户'
})

const userAvatar = computed(() => {
  return authStore.userInfo?.avatar || ''
})

const menuRoutes = computed(() => {
  return router.getRoutes().filter(route => {
    return route.meta?.requiresAuth && !route.meta?.hideInMenu && route.children
  })
})

const breadcrumbs = computed(() => {
  const matched = route.matched.filter(item => item.meta?.title)
  return matched.map(item => ({
    title: item.meta.title,
    path: item.path
  }))
})

// 方法
const toggleSidebar = () => {
  appStore.toggleSidebar()
}

const getMenuIndex = (route) => {
  if (route.children && route.children.length === 1) {
    return route.children[0].path
  }
  return route.path
}

const getMenuTitle = (route) => {
  if (route.children && route.children.length === 1) {
    return route.children[0].meta?.title || route.meta?.title
  }
  return route.meta?.title
}

const handleCommand = async (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      router.push('/settings')
      break
    case 'logout':
      await handleLogout()
      break
  }
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await authStore.logout()
  } catch (error) {
    // 用户取消或退出失败
    console.log('退出登录取消或失败:', error)
  }
}

// 监听路由变化，更新面包屑
watch(
  () => route.path,
  () => {
    appStore.setBreadcrumbs(breadcrumbs.value)
  },
  { immediate: true }
)
</script>

<style lang="scss" scoped>
.default-layout {
  display: flex;
  height: 100vh;
  
  .sidebar {
    background: #304156;
    transition: width 0.3s;
    
    .logo {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 16px;
      color: white;
      font-size: 18px;
      font-weight: bold;
      
      .logo-collapsed {
        font-size: 24px;
        font-weight: bold;
        text-align: center;
        width: 100%;
      }
    }
    
    .sidebar-menu {
      border-right: none;
      background: #304156;
      
      :deep(.el-menu-item) {
        color: #bfcbd9;
        
        &:hover {
          background: #263445;
        }
        
        &.is-active {
          background: #409eff;
          color: white;
        }
      }
      
      :deep(.el-sub-menu) {
        .el-sub-menu__title {
          color: #bfcbd9;
          
          &:hover {
            background: #263445;
          }
        }
      }
    }
  }
  
  .main-container {
    flex: 1;
    
    .header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 16px;
      background: white;
      border-bottom: 1px solid #e6e6e6;
      
      .header-left {
        display: flex;
        align-items: center;
        
        .sidebar-toggle {
          margin-right: 16px;
          font-size: 18px;
        }
      }
      
      .header-right {
        .user-info {
          display: flex;
          align-items: center;
          cursor: pointer;
          
          .username {
            margin: 0 8px;
          }
        }
      }
    }
    
    .content {
      padding: 16px;
      background: #f5f5f5;
      overflow-y: auto;
    }
  }
}
</style>
