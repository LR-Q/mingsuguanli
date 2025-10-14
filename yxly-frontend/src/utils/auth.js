import Cookies from 'js-cookie'

const TOKEN_KEY = 'yxly_token'
const REFRESH_TOKEN_KEY = 'yxly_refresh_token'

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
