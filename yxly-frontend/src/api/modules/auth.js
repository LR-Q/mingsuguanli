import request from '@/utils/request'

// 用户登录
export function login(data) {
  return request({
    url: '/api/v1/auth/login',
    method: 'POST',
    data
  })
}

// 用户注册
export function register(data) {
  return request({
    url: '/api/v1/auth/register',
    method: 'POST',
    data
  })
}

// 退出登录
export function logout() {
  return request({
    url: '/api/v1/auth/logout',
    method: 'POST'
  })
}

// 刷新token
export function refreshToken(refreshToken) {
  return request({
    url: '/api/v1/auth/refresh',
    method: 'POST',
    params: { refreshToken }
  })
}

// 重置密码
export function resetPassword(data) {
  return request({
    url: '/api/v1/auth/reset-password',
    method: 'POST',
    data
  })
}

// 检查用户名是否存在
export function checkUsername(username) {
  return request({
    url: '/api/v1/auth/check-username',
    method: 'GET',
    params: { username }
  })
}

// 检查邮箱是否存在
export function checkEmail(email) {
  return request({
    url: '/api/v1/auth/check-email',
    method: 'GET',
    params: { email }
  })
}

// 检查手机号是否存在
export function checkPhone(phone) {
  return request({
    url: '/api/v1/auth/check-phone',
    method: 'GET',
    params: { phone }
  })
}
