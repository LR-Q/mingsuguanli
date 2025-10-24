import request from '@/utils/request'

// 分页查询客户列表
export function getCustomerPage(params) {
  return request({
    url: '/api/v1/admin/customers',
    method: 'GET',
    params
  })
}

// 获取客户详情
export function getCustomerById(id) {
  return request({
    url: `/api/v1/admin/customers/${id}`,
    method: 'GET'
  })
}

// 更新客户状态
export function updateCustomerStatus(id, status) {
  return request({
    url: `/api/v1/admin/customers/${id}/status`,
    method: 'PUT',
    params: { status }
  })
}

// 删除客户
export function deleteCustomer(id) {
  return request({
    url: `/api/v1/admin/customers/${id}`,
    method: 'DELETE'
  })
}