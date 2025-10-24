import request from '@/utils/request'

// 获取我的财务记录
export function getMyFinancialRecords(params) {
  return request({
    url: '/api/v1/user/financial-records/my',
    method: 'GET',
    params
  })
}
