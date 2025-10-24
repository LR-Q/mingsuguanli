import request from '@/utils/request'

/**
 * 民宿主管理员注册
 */
export const registerMerchant = (data) => {
  return request({
    url: '/api/v1/merchant/register',
    method: 'post',
    data
  })
}
