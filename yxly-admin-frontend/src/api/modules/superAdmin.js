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

/**
 * 重置商户密码
 * @param {object} data - 重置密码数据
 * @param {number} data.merchantId - 商户ID
 * @param {string} data.newPassword - 新密码
 */
export const resetMerchantPassword = (data) => {
  return request({
    url: '/api/v1/super-admin/merchants/reset-password',
    method: 'post',
    data
  })
}

/**
 * 启用/禁用商户
 * @param {number} merchantId - 商户ID
 * @param {number} status - 状态(0:禁用 1:启用)
 */
export const updateMerchantStatus = (merchantId, status) => {
  return request({
    url: '/api/v1/super-admin/merchants/status',
    method: 'put',
    data: { merchantId, status }
  })
}

/**
 * 获取充值记录（分页）
 * @param {object} params - 查询参数
 */
export const getRechargeRecords = (params) => {
  return request({
    url: '/api/v1/super-admin/recharge',
    method: 'get',
    params
  })
}

/**
 * 获取充值记录详情
 * @param {number} id - 充值记录ID
 */
export const getRechargeDetail = (id) => {
  return request({
    url: `/api/v1/super-admin/recharge/${id}`,
    method: 'get'
  })
}

/**
 * 审核充值申请
 * @param {object} data - 审核数据
 */
export const auditRecharge = (data) => {
  return request({
    url: '/api/v1/super-admin/recharge/audit',
    method: 'post',
    data
  })
}

/**
 * 获取提现记录（分页）
 * @param {object} params - 查询参数
 */
export const getWithdrawRecords = (params) => {
  return request({
    url: '/api/v1/super-admin/withdraw',
    method: 'get',
    params
  })
}

/**
 * 获取提现记录详情
 * @param {number} id - 提现记录ID
 */
export const getWithdrawDetail = (id) => {
  return request({
    url: `/api/v1/super-admin/withdraw/${id}`,
    method: 'get'
  })
}

/**
 * 审核提现申请
 * @param {object} data - 审核数据
 */
export const auditWithdraw = (data) => {
  return request({
    url: '/api/v1/super-admin/withdraw/audit',
    method: 'post',
    data
  })
}

// 首页推荐房源 - 获取
export const getHomeRecommendations = () => {
  return request({
    url: '/api/v1/super-admin/home/recommendations',
    method: 'get'
  })
}

// 首页推荐房源 - 保存
export const saveHomeRecommendations = (roomIds) => {
  return request({
    url: '/api/v1/super-admin/home/recommendations',
    method: 'put',
    data: { roomIds }
  })
}

// 设置单个房间推荐状态（推荐字段：0未推荐，1已推荐）
export const setRoomRecommended = (roomId, recommended) => {
  return request({
    url: `/api/v1/super-admin/rooms/${roomId}/recommend`,
    method: 'put',
    data: { recommended }
  })
}
