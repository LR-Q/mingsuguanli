import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/modules/auth'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

// 配置NProgress
NProgress.configure({ 
  showSpinner: false,
  minimum: 0.2,
  speed: 500
})

// 静态路由
const constantRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/pages/auth/Login.vue'),
    meta: {
      title: '登录',
      requiresAuth: false,
      hideInMenu: true
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/pages/auth/Register.vue'),
    meta: {
      title: '注册',
      requiresAuth: false,
      hideInMenu: true
    }
  },
  {
    path: '/404',
    name: 'NotFound',
    component: () => import('@/pages/error/404.vue'),
    meta: {
      title: '页面不存在',
      hideInMenu: true
    }
  }
]

// 动态路由
const asyncRoutes = [
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/layouts/DefaultLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/pages/dashboard/Index.vue'),
        meta: {
          title: '仪表盘',
          icon: 'Dashboard',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/rooms',
    name: 'RoomManagement',
    component: () => import('@/layouts/DefaultLayout.vue'),
    meta: {
      title: '房间管理',
      icon: 'House',
      requiresAuth: true
    },
    children: [
      {
        path: '',
        name: 'RoomList',
        component: () => import('@/pages/rooms/List.vue'),
        meta: {
          title: '房间列表',
          requiresAuth: true
        }
      },
      {
        path: 'create',
        name: 'RoomCreate',
        component: () => import('@/pages/rooms/Form.vue'),
        meta: {
          title: '添加房间',
          requiresAuth: true,
          hideInMenu: true
        }
      },
      {
        path: ':id/edit',
        name: 'RoomEdit',
        component: () => import('@/pages/rooms/Form.vue'),
        meta: {
          title: '编辑房间',
          requiresAuth: true,
          hideInMenu: true
        }
      },
      {
        path: ':id',
        name: 'RoomDetail',
        component: () => import('@/pages/rooms/Detail.vue'),
        meta: {
          title: '房间详情',
          requiresAuth: true,
          hideInMenu: true
        }
      }
    ]
  },
  {
    path: '/bookings',
    name: 'BookingManagement',
    component: () => import('@/layouts/DefaultLayout.vue'),
    meta: {
      title: '订单管理',
      icon: 'Document',
      requiresAuth: true
    },
    children: [
      {
        path: '',
        name: 'BookingList',
        component: () => import('@/pages/bookings/List.vue'),
        meta: {
          title: '订单列表',
          requiresAuth: true
        }
      },
      {
        path: 'create',
        name: 'BookingCreate',
        component: () => import('@/pages/bookings/Form.vue'),
        meta: {
          title: '创建订单',
          requiresAuth: true,
          hideInMenu: true
        }
      },
      {
        path: ':id',
        name: 'BookingDetail',
        component: () => import('@/pages/bookings/Detail.vue'),
        meta: {
          title: '订单详情',
          requiresAuth: true,
          hideInMenu: true
        }
      },
      {
        path: ':id/checkin',
        name: 'BookingCheckIn',
        component: () => import('@/pages/bookings/CheckIn.vue'),
        meta: {
          title: '办理入住',
          requiresAuth: true,
          hideInMenu: true
        }
      }
    ]
  },
  {
    path: '/customers',
    name: 'CustomerManagement',
    component: () => import('@/layouts/DefaultLayout.vue'),
    meta: {
      title: '客户管理',
      icon: 'User',
      requiresAuth: true
    },
    children: [
      {
        path: '',
        name: 'CustomerList',
        component: () => import('@/pages/customers/List.vue'),
        meta: {
          title: '客户列表',
          requiresAuth: true
        }
      },
      {
        path: ':id',
        name: 'CustomerDetail',
        component: () => import('@/pages/customers/Detail.vue'),
        meta: {
          title: '客户详情',
          requiresAuth: true,
          hideInMenu: true
        }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: [...constantRoutes, ...asyncRoutes],
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// 全局前置守卫
router.beforeEach(async (to, from, next) => {
  NProgress.start()
  
  const authStore = useAuthStore()
  
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 悦鑫乐怡民宿管理系统` : '悦鑫乐怡民宿管理系统'
  
  // 检查认证状态
  if (to.meta.requiresAuth) {
    if (!authStore.isAuthenticated) {
      next('/login')
      return
    }
    
    // 如果有token但没有用户信息，尝试获取用户信息
    if (!authStore.userInfo) {
      try {
        await authStore.getUserInfo()
      } catch (error) {
        console.error('获取用户信息失败:', error)
        authStore.logout()
        next('/login')
        return
      }
    }
  }
  
  // 如果已登录访问登录页，重定向到首页
  if (to.path === '/login' && authStore.isAuthenticated) {
    next('/')
    return
  }
  
  next()
})

// 全局后置守卫
router.afterEach(() => {
  NProgress.done()
})

export default router
