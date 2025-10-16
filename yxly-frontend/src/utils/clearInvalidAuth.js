/**
 * 清除无效的认证状态
 * 当检测到有token但没有用户信息时，清除所有认证相关数据
 */
import { removeToken, removeRefreshToken, removeUserInfo } from './auth'

export function clearInvalidAuth() {
  try {
    // 使用统一的清除方法
    removeToken()
    removeRefreshToken()
    removeUserInfo()
    
    // 清除localStorage中其他可能的认证相关数据
    const keysToRemove = []
    for (let i = 0; i < localStorage.length; i++) {
      const key = localStorage.key(i)
      if (key && (key.includes('token') || key.includes('auth') || key.includes('pinia'))) {
        keysToRemove.push(key)
      }
    }
    keysToRemove.forEach(key => localStorage.removeItem(key))
    
    // 清除sessionStorage中可能存在的数据
    const sessionKeysToRemove = []
    for (let i = 0; i < sessionStorage.length; i++) {
      const key = sessionStorage.key(i)
      if (key && (key.includes('token') || key.includes('auth') || key.includes('user'))) {
        sessionKeysToRemove.push(key)
      }
    }
    sessionKeysToRemove.forEach(key => sessionStorage.removeItem(key))
    
    console.log('已清除所有无效的认证数据')
    return true
  } catch (error) {
    console.error('清除认证数据时出错:', error)
    return false
  }
}

/**
 * 检查并清除无效的认证状态
 */
export function checkAndClearInvalidAuth() {
  // 检查是否存在孤立的token（有token但没有对应的用户信息）
  const hasToken = document.cookie.includes('yxly_token')
  const hasUserInfo = localStorage.getItem('userInfo') || sessionStorage.getItem('userInfo')
  
  if (hasToken && !hasUserInfo) {
    console.log('检测到无效的认证状态，正在清除...')
    clearInvalidAuth()
    
    // 刷新页面以确保状态更新
    setTimeout(() => {
      window.location.reload()
    }, 100)
  }
}
