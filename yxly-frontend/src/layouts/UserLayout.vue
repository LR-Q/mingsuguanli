<template>
  <div class="user-layout">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="header-container">
        <div class="logo">
          <h1>悦鑫乐怡民宿</h1>
        </div>
        
        <nav class="nav-menu">
          <router-link to="/home" class="nav-item">首页</router-link>
          <router-link to="/rooms" class="nav-item">房间预订</router-link>
          <router-link to="/about" class="nav-item">关于我们</router-link>
          <router-link to="/contact" class="nav-item">联系我们</router-link>
        </nav>
        
        <div class="user-actions">
          <template v-if="authStore.isAuthenticated">
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
            <router-link to="/login" class="login-btn">登录</router-link>
            <router-link to="/register" class="register-btn">注册</router-link>
          </template>
        </div>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- 底部 -->
    <footer class="footer">
      <div class="footer-container">
        <div class="footer-content">
          <div class="footer-section">
            <h3>悦鑫乐怡民宿</h3>
            <p>为您提供温馨舒适的住宿体验</p>
          </div>
          <div class="footer-section">
            <h4>快速链接</h4>
            <ul>
              <li><router-link to="/">首页</router-link></li>
              <li><router-link to="/rooms">房间预订</router-link></li>
              <li><router-link to="/about">关于我们</router-link></li>
            </ul>
          </div>
          <div class="footer-section">
            <h4>联系我们</h4>
            <p>电话：400-123-4567</p>
            <p>邮箱：info@yxly.com</p>
            <p>地址：某某市某某区某某街道123号</p>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2024 悦鑫乐怡民宿管理系统. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown, User, Wallet, Document, Star, Setting, SwitchButton } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/modules/auth'

const router = useRouter()
const authStore = useAuthStore()

// 计算属性
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
.user-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
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
    height: 70px;
  }
  
  .logo {
    h1 {
      margin: 0;
      color: #409eff;
      font-size: 24px;
      font-weight: bold;
    }
  }
  
  .nav-menu {
    display: flex;
    gap: 32px;
    
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
    display: flex;
    align-items: center;
    gap: 16px;
    
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
    
    .login-btn, .register-btn {
      text-decoration: none;
      padding: 8px 16px;
      border-radius: 6px;
      font-weight: 500;
      transition: all 0.3s;
    }
    
    .login-btn {
      color: #409eff;
      border: 1px solid #409eff;
      
      &:hover {
        background: #409eff;
        color: white;
      }
    }
    
    .register-btn {
      background: #409eff;
      color: white;
      
      &:hover {
        background: #337ecc;
      }
    }
  }
}

.main-content {
  flex: 1;
}

.footer {
  background: #2c3e50;
  color: white;
  margin-top: auto;
  
  .footer-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 40px 20px 20px;
  }
  
  .footer-content {
    display: grid;
    grid-template-columns: 2fr 1fr 1fr;
    gap: 40px;
    margin-bottom: 30px;
    
    .footer-section {
      h3, h4 {
        margin: 0 0 16px 0;
        color: #409eff;
      }
      
      p {
        margin: 8px 0;
        color: #bdc3c7;
        line-height: 1.6;
      }
      
      ul {
        list-style: none;
        padding: 0;
        margin: 0;
        
        li {
          margin: 8px 0;
          
          a {
            color: #bdc3c7;
            text-decoration: none;
            transition: color 0.3s;
            
            &:hover {
              color: #409eff;
            }
          }
        }
      }
    }
  }
  
  .footer-bottom {
    border-top: 1px solid #34495e;
    padding-top: 20px;
    text-align: center;
    
    p {
      margin: 0;
      color: #95a5a6;
      font-size: 14px;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .header {
    .header-container {
      padding: 0 16px;
      height: 60px;
    }
    
    .nav-menu {
      display: none;
    }
    
    .logo h1 {
      font-size: 20px;
    }
  }
  
  .footer {
    .footer-content {
      grid-template-columns: 1fr;
      gap: 24px;
      text-align: center;
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
