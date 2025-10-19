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
    path: '/admin-login',
    name: 'AdminLogin',
    component: () => import('@/pages/auth/AdminLogin.vue'),
    meta: {
      title: '管理员登录',
      requiresAuth: false,
      hideInMenu: true
    }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/pages/auth/ForgotPassword.vue'),
    meta: {
      title: '忘记密码',
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

// 用户路由（面向客户）
const userRoutes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/layouts/SimpleUserLayout.vue'),
    children: [
      {
        path: '',
        name: 'HomePage',
        component: () => import('@/pages/user/SimpleHome.vue'),
        meta: {
          title: '首页',
          requiresAuth: false
        }
      }
    ]
  },
  {
    path: '/rooms',
    name: 'UserRooms',
    component: () => import('@/layouts/SimpleUserLayout.vue'),
    children: [
      {
        path: '',
        name: 'RoomsPage',
        component: () => import('@/pages/user/Rooms.vue'),
        meta: {
          title: '房间预订',
          requiresAuth: false
        }
      },
    ]
  },
  {
    path: '/rooms/:id/book',
    name: 'BookRoom',
    component: () => import('@/layouts/UserLayout.vue'),
    children: [
      {
        path: '',
        name: 'BookRoomPage',
        component: () => import('@/pages/user/BookRoom.vue'),
        meta: {
          title: '预订房间',
          requiresAuth: true
        }
      }
    ]
  },
  {
    path: '/user-center',
    name: 'UserCenter',
    component: () => import('@/layouts/UserCenterLayout.vue'),
    redirect: '/user-center/profile',
    meta: {
      title: '个人中心',
      requiresAuth: true
    },
    children: [
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('@/pages/user/center/Profile.vue'),
        meta: {
          title: '个人信息',
          requiresAuth: true
        }
      },
      {
        path: 'wallet',
        name: 'UserWallet',
        component: () => import('@/pages/user/center/Wallet.vue'),
        meta: {
          title: '我的钱包',
          requiresAuth: true
        }
      },
      {
        path: 'orders',
        name: 'UserOrders',
        component: () => import('@/pages/user/center/Orders.vue'),
        meta: {
          title: '预订信息',
          requiresAuth: true
        }
      },
      {
        path: 'favorites',
        name: 'UserFavorites',
        component: () => import('@/pages/user/center/Favorites.vue'),
        meta: {
          title: '我的收藏',
          requiresAuth: true
        }
      },
      {
        path: 'settings',
        name: 'UserSettings',
        component: () => import('@/pages/user/center/Settings.vue'),
        meta: {
          title: '账户设置',
          requiresAuth: true
        }
      }
    ]
  },
  // 兼容旧路由
  {
    path: '/profile',
    redirect: '/user-center/profile'
  },
  {
    path: '/orders',
    redirect: '/user-center/orders'
  }
]

// 管理员路由（面向房东/管理员）
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
        path: 'recharge',
        name: 'RechargeManagement',
        component: () => import('@/pages/admin/RechargeManagement.vue'),
        meta: {
          title: '充值管理',
          icon: 'Wallet',
          requiresAuth: true,
          requiresAdmin: true
        }
      },
      {
        path: 'withdraw',
        name: 'WithdrawManagement',
        component: () => import('@/pages/admin/WithdrawManagement.vue'),
        meta: {
          title: '提现管理',
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
  routes: [...constantRoutes, ...userRoutes, ...adminRoutes],
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
  
  // 如果已登录访问登录页，重定向到相应首页
  if ((to.path === '/login' || to.path === '/admin-login') && authStore.isAuthenticated) {
    // 根据用户角色判断跳转到哪里
    if (authStore.userInfo?.userType === 'admin' || authStore.userInfo?.roles?.includes('admin')) {
      next('/admin')
    } else {
      next('/home')
    }
    return
  }
  
  // 检查管理员权限
  if (to.meta.requiresAdmin) {
    if (!authStore.isAuthenticated) {
      next('/admin-login')
      return
    }
    // 这里可以添加更严格的管理员权限检查
    // 暂时只要登录就可以访问管理员页面
  }
  
  next()
})

// 全局后置守卫
router.afterEach(() => {
  NProgress.done()
})

export default router
