import request from '@/utils/request'

/**
 * 管理员端充值管理API
 */

// 分页查询充值记录
export function getRechargeRecords(params) {
  return request({
    url: '/api/v1/admin/recharge',
    method: 'GET',
    params
  })
}

// 获取充值记录详情
export function getRechargeRecordById(id) {
  return request({
    url: `/api/v1/admin/recharge/${id}`,
    method: 'GET'
  })
}

// 审核充值申请
export function auditRechargeRecord(data) {
  return request({
    url: '/api/v1/admin/recharge/audit',
    method: 'POST',
    data
  })
}

/**
 * 用户端充值API
 */

// 申请充值
export function applyRecharge(data) {
  return request({
    url: '/api/v1/user/recharge/apply',
    method: 'POST',
    data
  })
}

// 查询用户充值记录
export function getUserRechargeRecords(params) {
  return request({
    url: '/api/v1/user/recharge/records',
    method: 'GET',
    params
  })
}

// 上传支付凭证
export function uploadPaymentProof(formData) {
  return request({
    url: '/api/v1/admin/files/upload/payment-proof',
    method: 'POST',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 申请提现
export function applyWithdraw(data) {
  return request({
    url: '/api/v1/user/recharge/withdraw/apply',
    method: 'POST',
    data
  })
}

// 查询用户提现记录
export function getUserWithdrawRecords(params) {
  return request({
    url: '/api/v1/user/recharge/withdraw/records',
    method: 'GET',
    params
  })
}
