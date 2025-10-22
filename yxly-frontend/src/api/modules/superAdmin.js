import request from '@/utils/request'

/**
 * 获取待审核商户列表
 */
export const getPendingMerchants = () => {
  return request({
    url: '/api/v1/super-admin/merchants/pending',
    method: 'get'
  })
}

/**
 * 获取所有商户列表
 * @param {number} auditStatus - 审核状态(0:待审核 1:已认证 2:已拒绝)
 */
export const getAllMerchants = (auditStatus) => {
  return request({
    url: '/api/v1/super-admin/merchants/all',
    method: 'get',
    params: { auditStatus }
  })
}

/**
 * 审核商户
 * @param {object} data - 审核数据
 * @param {number} data.merchantId - 商户ID
 * @param {number} data.auditStatus - 审核状态(1:通过 2:拒绝)
 * @param {string} data.auditRemarks - 审核备注
 */
export const auditMerchant = (data) => {
  return request({
    url: '/api/v1/super-admin/merchants/audit',
    method: 'post',
    data
  })
}

/**
 * 获取商户详情
 * @param {number} merchantId - 商户ID
 */
export const getMerchantDetail = (merchantId) => {
  return request({
    url: `/api/v1/super-admin/merchants/${merchantId}`,
    method: 'get'
  })
}
