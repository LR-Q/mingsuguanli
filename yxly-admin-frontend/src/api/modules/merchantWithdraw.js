import request from '@/utils/request'

/**
 * 查询自己的提现记录
 * @param {object} params - 查询参数
 * @param {number} params.current - 当前页
 * @param {number} params.size - 每页大小
 * @param {number} params.status - 状态筛选
 */
export const getMyWithdrawRecords = (params) => {
  return request({
    url: '/api/v1/merchant/withdraw/my-records',
    method: 'get',
    params
  })
}

/**
 * 申请提现
 * @param {object} data - 提现申请数据
 * @param {number} data.amount - 提现金额
 * @param {string} data.withdrawMethod - 提现方式
 * @param {string} data.accountInfo - 收款账户信息
 * @param {string} data.userRemark - 用户备注
 */
export const applyWithdraw = (data) => {
  return request({
    url: '/api/v1/merchant/withdraw/apply',
    method: 'post',
    data
  })
}

/**
 * 取消提现申请（仅限待审核状态）
 * @param {number} id - 提现记录ID
 */
export const cancelWithdraw = (id) => {
  return request({
    url: `/api/v1/merchant/withdraw/cancel/${id}`,
    method: 'post'
  })
}

/**
 * 获取提现记录详情
 * @param {number} id - 提现记录ID
 */
export const getWithdrawDetail = (id) => {
  return request({
    url: `/api/v1/merchant/withdraw/${id}`,
    method: 'get'
  })
}
