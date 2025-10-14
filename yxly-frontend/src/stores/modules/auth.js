import { defineStore } from 'pinia'
import { logout, refreshToken } from '@/api/modules/auth'
import { getToken, setToken, removeToken, getRefreshToken, setRefreshToken, removeRefreshToken } from '@/utils/auth'
import router from '@/router'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: getToken() || '',
    refreshToken: getRefreshToken() || '',
    userInfo: null,
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

    // 清除用户信息
    clearUserInfo() {
      this.token = ''
      this.refreshToken = ''
      this.userInfo = null
      this.permissions = []
      removeToken()
      removeRefreshToken()
    }
  }
})
