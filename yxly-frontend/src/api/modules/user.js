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

/**
 * 上传用户头像
 * @param {FormData} formData 表单数据
 */
export function uploadAvatar(formData) {
  return request({
    url: '/api/v1/user/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 更新用户信息
 * @param {Object} data 用户信息
 */
export function updateUserProfile(data) {
  return request({
    url: '/api/v1/user/profile',
    method: 'put',
    data
  })
}
