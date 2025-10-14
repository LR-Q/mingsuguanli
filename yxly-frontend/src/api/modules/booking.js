import request from '@/utils/request'

// 获取订单列表
export function getBookingList(params) {
  return request({
    url: '/v1/bookings',
    method: 'GET',
    params
  })
}

// 获取订单详情
export function getBookingDetail(id) {
  return request({
    url: `/v1/bookings/${id}`,
    method: 'GET'
  })
}

// 创建订单
export function createBooking(data) {
  return request({
    url: '/v1/bookings',
    method: 'POST',
    data
  })
}

// 更新订单
export function updateBooking(id, data) {
  return request({
    url: `/v1/bookings/${id}`,
    method: 'PUT',
    data
  })
}

// 取消订单
export function cancelBooking(id, data) {
  return request({
    url: `/v1/bookings/${id}/cancel`,
    method: 'POST',
    data
  })
}

// 确认订单
export function confirmBooking(id) {
  return request({
    url: `/v1/bookings/${id}/confirm`,
    method: 'POST'
  })
}

// 办理入住
export function checkInBooking(id, data) {
  return request({
    url: `/v1/bookings/${id}/checkin`,
    method: 'POST',
    data
  })
}

// 办理退房
export function checkOutBooking(id, data) {
  return request({
    url: `/v1/bookings/${id}/checkout`,
    method: 'POST',
    data
  })
}

// 支付订单
export function payBooking(id, data) {
  return request({
    url: `/v1/bookings/${id}/payment`,
    method: 'POST',
    data
  })
}
