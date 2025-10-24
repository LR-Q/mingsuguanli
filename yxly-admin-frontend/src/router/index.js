import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/modules/auth'
import { ElMessage } from 'element-plus'
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
    name: 'AdminLogin',
    component: () => import('@/pages/auth/AdminLogin.vue'),
    meta: {
      title: '管理员登录',
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

// 超级管理员路由
const superAdminRoutes = [
  {
    path: '/super-admin',
    name: 'SuperAdminLayout',
    component: () => import('@/layouts/DefaultLayout.vue'),
    redirect: '/super-admin/merchants',
    meta: {
      requiresAuth: true,
      requiresSuperAdmin: true
    },
    children: [
      {
        path: 'merchants',
        name: 'MerchantAudit',
        component: () => import('@/pages/super-admin/MerchantAudit.vue'),
        meta: {
          title: '商户管理',
          icon: 'UserFilled',
          requiresAuth: true,
          requiresSuperAdmin: true
        }
      },
      {
        path: 'recharge',
        name: 'SuperAdminRecharge',
        component: () => import('@/pages/super-admin/RechargeManagement.vue'),
        meta: {
          title: '充值管理',
          icon: 'CreditCard',
          requiresAuth: true,
          requiresSuperAdmin: true
        }
      },
      {
        path: 'withdraw',
        name: 'SuperAdminWithdraw',
        component: () => import('@/pages/super-admin/WithdrawManagement.vue'),
        meta: {
          title: '提现管理',
          icon: 'Money',
          requiresAuth: true,
          requiresSuperAdmin: true
        }
      }
    ]
  }
]

// 民宿管理员路由
const adminRoutes = [
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('@/layouts/DefaultLayout.vue'),
    redirect: '/admin/dashboard',
    meta: {
      requiresAuth: true,
      requiresAdmin: true
    },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/pages/dashboard/Index.vue'),
        meta: {
          title: '管理仪表盘',
          icon: 'DataBoard',
          requiresAuth: true,
          requiresAdmin: true
        }
      },
      {
        path: 'customers',
        name: 'CustomerManagement',
        component: () => import('@/pages/admin/CustomerManagement.vue'),
        meta: {
          title: '客户管理',
          icon: 'User',
          requiresAuth: true,
          requiresAdmin: true
        }
      },
      {
        path: 'rooms',
        name: 'RoomManagement',
        component: () => import('@/pages/rooms/List.vue'),
        meta: {
          title: '房间管理',
          icon: 'House',
          requiresAuth: true,
          requiresAdmin: true
        }
      },
      {
        path: 'bookings',
        name: 'BookingManagement',
        component: () => import('@/pages/bookings/List.vue'),
        meta: {
          title: '订单管理',
          icon: 'Document',
          requiresAuth: true,
          requiresAdmin: true
        }
      },
      {
        path: 'withdraw-apply',
        name: 'WithdrawApply',
        component: () => import('@/pages/merchant/WithdrawApply.vue'),
        meta: {
          title: '提现申请',
          icon: 'Money',
          requiresAuth: true,
          requiresAdmin: true
        }
      },
      {
        path: 'location',
        name: 'LocationManagement',
        component: () => import('@/pages/admin/LocationManagement.vue'),
        meta: {
          title: '位置管理',
          icon: 'Location',
          requiresAuth: true,
          requiresAdmin: true
        }
      },
      // 隐藏的子页面路由
      {
        path: 'rooms/create',
        name: 'RoomCreate',
        component: () => import('@/pages/rooms/Form.vue'),
        meta: {
          title: '添加房间',
          requiresAuth: true,
          requiresAdmin: true,
          hideInMenu: true
        }
      },
      {
        path: 'rooms/:id/edit',
        name: 'RoomEdit',
        component: () => import('@/pages/rooms/Form.vue'),
        meta: {
          title: '编辑房间',
          requiresAuth: true,
          requiresAdmin: true,
          hideInMenu: true
        }
      },
      {
        path: 'rooms/:id',
        name: 'RoomDetail',
        component: () => import('@/pages/rooms/Detail.vue'),
        meta: {
          title: '房间详情',
          requiresAuth: true,
          requiresAdmin: true,
          hideInMenu: true
        }
      },
      {
        path: 'bookings/create',
        name: 'BookingCreate',
        component: () => import('@/pages/bookings/Form.vue'),
        meta: {
          title: '创建订单',
          requiresAuth: true,
          requiresAdmin: true,
          hideInMenu: true
        }
      },
      {
        path: 'bookings/:id',
        name: 'BookingDetail',
        component: () => import('@/pages/bookings/Detail.vue'),
        meta: {
          title: '订单详情',
          requiresAuth: true,
          requiresAdmin: true,
          hideInMenu: true
        }
      },
      {
        path: 'bookings/:id/checkin',
        name: 'BookingCheckIn',
        component: () => import('@/pages/bookings/CheckIn.vue'),
        meta: {
          title: '办理入住',
          requiresAuth: true,
          requiresAdmin: true,
          hideInMenu: true
        }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'Root',
      redirect: () => {
        const authStore = useAuthStore()
        const roleCode = authStore.userInfo?.roleCode
        
        // 根据角色重定向到不同页面
        if (roleCode === 'SUPER_ADMIN') {
          return '/super-admin/merchants'
        } else if (roleCode === 'HOMESTAY_ADMIN') {
          return '/admin/dashboard'
        }
        return '/login'
      }
    },
    ...constantRoutes, 
    ...superAdminRoutes, 
    ...adminRoutes
  ],
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
  document.title = to.meta.title ? `${to.meta.title} - 悦鑫乐怡民宿管理后台` : '悦鑫乐怡民宿管理后台'
  
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
  
  // 如果已登录访问登录页，重定向到相应首页
  if (to.path === '/login' && authStore.isAuthenticated) {
    const roleCode = authStore.userInfo?.roleCode
    
    if (roleCode === 'SUPER_ADMIN') {
      next('/super-admin/merchants')
    } else if (roleCode === 'HOMESTAY_ADMIN') {
      next('/admin/dashboard')
    } else {
      // 普通用户不能访问管理后台
      ElMessage.error('普通用户无权访问管理后台')
      authStore.logout()
      next(false)
    }
    return
  }
  
  // 检查超级管理员权限
  if (to.meta.requiresSuperAdmin) {
    if (!authStore.isAuthenticated) {
      next('/login')
      return
    }
    const roleCode = authStore.userInfo?.roleCode
    if (roleCode !== 'SUPER_ADMIN') {
      ElMessage.error('无权访问，需要超级管理员权限')
      next('/admin/dashboard')
      return
    }
  }
  
  // 检查管理员权限
  if (to.meta.requiresAdmin) {
    if (!authStore.isAuthenticated) {
      next('/login')
      return
    }
    const roleCode = authStore.userInfo?.roleCode
    // 只允许民宿管理员访问
    if (roleCode !== 'HOMESTAY_ADMIN') {
      ElMessage.error('无权访问，需要民宿管理员权限')
      next('/super-admin/merchants')
      return
    }
  }
  
  next()
})

// 全局后置守卫
router.afterEach(() => {
  NProgress.done()
})

export default router
