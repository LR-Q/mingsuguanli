import request from '@/utils/request'

// 获取客户列表
export function getCustomerList(params) {
  return request({
    url: '/v1/customers',
    method: 'GET',
    params
  })
}

// 获取客户详情
export function getCustomerDetail(id) {
  return request({
    url: `/v1/customers/${id}`,
    method: 'GET'
  })
}

// 创建客户
export function createCustomer(data) {
  return request({
    url: '/v1/customers',
    method: 'POST',
    data
  })
}

// 更新客户
export function updateCustomer(id, data) {
  return request({
    url: `/v1/customers/${id}`,
    method: 'PUT',
    data
  })
}

// 删除客户
export function deleteCustomer(id) {
  return request({
    url: `/v1/customers/${id}`,
    method: 'DELETE'
  })
}

// 搜索客户
export function searchCustomer(keyword) {
  return request({
    url: '/v1/customers/search',
    method: 'GET',
    params: { keyword }
  })
}
