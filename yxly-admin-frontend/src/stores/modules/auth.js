import { defineStore } from 'pinia'
import { logout, refreshToken } from '@/api/modules/auth'
import { 
  getToken, setToken, removeToken, 
  getRefreshToken, setRefreshToken, removeRefreshToken,
  getUserInfo, setUserInfo, removeUserInfo 
} from '@/utils/auth'
import router from '@/router'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: getToken() || '',
    refreshToken: getRefreshToken() || '',
    userInfo: getUserInfo(),
    permissions: []
  }),

  getters: {
    isAuthenticated: (state) => !!state.token,
    
    hasRole: (state) => (roles) => {
      if (!state.userInfo) return false
      if (!Array.isArray(roles)) roles = [roles]
      return roles.some(role => state.userInfo.roles?.includes(role))
    },
    
    hasPermission: (state) => (permission) => {
      return state.permissions.includes(permission)
    }
  },

  actions: {
    // 设置Token
    setToken(token) {
      this.token = token
      setToken(token)
    },

    // 设置刷新Token
    setRefreshToken(refreshToken) {
      this.refreshToken = refreshToken
      setRefreshToken(refreshToken)
    },

    // 设置用户信息
    setUserInfo(userInfo) {
      this.userInfo = userInfo
      setUserInfo(userInfo) // 持久化保存
    },

    // 获取用户信息
    async getUserInfo() {
      try {
        // 如果已经有用户信息，直接返回
        if (this.userInfo) {
          return this.userInfo
        }
        
        // 如果没有token，抛出错误
        if (!this.token) {
          throw new Error('未登录')
        }
        
        // 这里可以调用获取用户信息的API
        // 暂时返回空，因为登录时已经设置了用户信息
        return this.userInfo
      } catch (error) {
        console.error('获取用户信息失败:', error)
        throw error
      }
    },

    // 刷新Token
    async refreshAccessToken() {
      try {
        if (!this.refreshToken) {
          throw new Error('没有刷新令牌')
        }
        
        const response = await refreshToken(this.refreshToken)
        const { accessToken, refreshToken: newRefreshToken, userInfo } = response.data
        
        this.setToken(accessToken)
        this.setRefreshToken(newRefreshToken)
        this.setUserInfo(userInfo)
        
        return response
      } catch (error) {
        // 刷新失败，清除所有信息并跳转登录
        this.clearUserInfo()
        throw error
      }
    },

    // 退出登录
    async logout() {
      try {
        await logout()
      } catch (error) {
        console.error('退出登录失败:', error)
      } finally {
        this.clearUserInfo()
        router.push('/login')
      }
    },

    // 更新用户信息
    updateUserInfo(userInfo) {
      this.userInfo = { ...this.userInfo, ...userInfo }
      setUserInfo(this.userInfo) // 持久化保存
    },

    // 初始化认证状态（页面刷新时调用）
    initAuthState() {
      // 从持久化存储中恢复状态
      this.token = getToken() || ''
      this.refreshToken = getRefreshToken() || ''
      this.userInfo = getUserInfo()
      
      // 如果有token但没有用户信息，清除无效状态
      if (this.token && !this.userInfo) {
        console.log('检测到无效的认证状态，清除token')
        this.clearUserInfo()
      }
      
      console.log('认证状态初始化完成:', {
        hasToken: !!this.token,
        hasRefreshToken: !!this.refreshToken,
        hasUserInfo: !!this.userInfo,
        username: this.userInfo?.username
      })
    },

    // 清除用户信息
    clearUserInfo() {
      this.token = ''
      this.refreshToken = ''
      this.userInfo = null
      this.permissions = []
      removeToken()
      removeRefreshToken()
      removeUserInfo() // 清除持久化的用户信息
    }
  }
})
