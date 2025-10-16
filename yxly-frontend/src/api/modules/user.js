import request from '@/utils/request'

/**
 * 获取当前用户信息
 */
export function getCurrentUserInfo() {
  return request({
    url: '/api/v1/user/info',
    method: 'get'
  })
}

/**
 * 获取用户余额
 */
export function getUserBalance() {
  return request({
    url: '/api/v1/user/recharge/balance',
    method: 'get'
  })
}
