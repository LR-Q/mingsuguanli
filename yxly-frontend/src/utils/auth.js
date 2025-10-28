import Cookies from 'js-cookie'

// 前台站点：使用独立的Cookie/LocalStorage键，避免与管理后台冲突
const TOKEN_KEY = 'yxly_user_token'
const REFRESH_TOKEN_KEY = 'yxly_user_refresh_token'
const USER_INFO_KEY = 'yxly_user_info'

// 访问令牌相关
export function getToken() {
  return Cookies.get(TOKEN_KEY)
}

export function setToken(token) {
  return Cookies.set(TOKEN_KEY, token, { expires: 1 }) // 1天过期
}

export function removeToken() {
  return Cookies.remove(TOKEN_KEY)
}

// 刷新令牌相关
export function getRefreshToken() {
  return Cookies.get(REFRESH_TOKEN_KEY)
}

export function setRefreshToken(refreshToken) {
  return Cookies.set(REFRESH_TOKEN_KEY, refreshToken, { expires: 7 }) // 7天过期
}

export function removeRefreshToken() {
  return Cookies.remove(REFRESH_TOKEN_KEY)
}

// 用户信息相关
export function getUserInfo() {
  try {
    const userInfoStr = localStorage.getItem(USER_INFO_KEY)
    return userInfoStr ? JSON.parse(userInfoStr) : null
  } catch (error) {
    console.error('获取用户信息失败:', error)
    return null
  }
}

export function setUserInfo(userInfo) {
  try {
    localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo))
    return true
  } catch (error) {
    console.error('保存用户信息失败:', error)
    return false
  }
}

export function removeUserInfo() {
  try {
    localStorage.removeItem(USER_INFO_KEY)
    return true
  } catch (error) {
    console.error('删除用户信息失败:', error)
    return false
  }
}
