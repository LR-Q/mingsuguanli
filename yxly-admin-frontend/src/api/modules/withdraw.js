import request from '@/utils/request'

/**
 * 管理员端提现管理API
 */

// 分页查询提现记录
export function getWithdrawRecords(params) {
  return request({
    url: '/api/v1/admin/withdraw',
    method: 'GET',
    params
  })
}

// 获取提现记录详情
export function getWithdrawRecordById(id) {
  return request({
    url: `/api/v1/admin/withdraw/${id}`,
    method: 'GET'
  })
}

// 审核提现申请
export function auditWithdrawRecord(data) {
  return request({
    url: '/api/v1/admin/withdraw/audit',
    method: 'POST',
    data
  })
}
