import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

import App from './App.vue'
import router from './router'
import './assets/styles/global.scss'
import { useAuthStore } from '@/stores/modules/auth'

const app = createApp(App)

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

const pinia = createPinia()
app.use(pinia)
app.use(router)
app.use(ElementPlus, {
  locale: zhCn
})

// 初始化认证状态
const authStore = useAuthStore()
authStore.initAuthState()

// 应用启动后，根据管理员状态进行路由跳转
app.mount('#app')

// 在应用挂载后检查认证状态并跳转
setTimeout(() => {
  if (authStore.isAuthenticated && authStore.userInfo) {
    const currentPath = router.currentRoute.value.path
    const roleCode = authStore.userInfo.roleCode
    
    // 如果当前在根路径或登录页面，根据角色跳转
    if (currentPath === '/' || currentPath === '/login') {
      if (roleCode === 'SUPER_ADMIN') {
        router.push('/super-admin/merchants')
      } else if (roleCode === 'HOMESTAY_ADMIN') {
        router.push('/admin/dashboard')
      } else {
        // 普通用户不能访问管理后台
        authStore.logout()
        router.push('/login')
      }
    }
  }
}, 100)
