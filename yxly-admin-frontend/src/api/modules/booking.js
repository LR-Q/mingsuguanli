import request from '@/utils/request'

// 获取我的订单列表（用户端）
export function getMyBookingList(params) {
  return request({
    url: '/api/user/bookings/my',
    method: 'GET',
    params
  })
}

// 获取订单列表（管理端）
export function getBookingList(params) {
  return request({
    url: '/api/admin/bookings',
    method: 'GET',
    params
  })
}

// 获取订单详情（用户端）
export function getBookingDetail(id) {
  return request({
    url: `/api/user/bookings/${id}`,
    method: 'GET'
  })
}

// 获取订单详情（管理员端）
export function getAdminBookingDetail(id) {
  return request({
    url: `/api/admin/bookings/${id}`,
    method: 'GET'
  })
}

// 创建订单
export function createBooking(data) {
  return request({
    url: '/api/user/bookings',
    method: 'POST',
    data
  })
}

// 更新订单
export function updateBooking(id, data) {
  return request({
    url: `/api/admin/bookings/${id}`,
    method: 'PUT',
    data
  })
}

// 取消订单（用户端）
export function cancelBooking(id, reason) {
  return request({
    url: `/api/user/bookings/${id}/cancel`,
    method: 'POST',
    params: { reason }
  })
}

// 取消订单（管理员端）
export function adminCancelBooking(id, reason) {
  return request({
    url: `/api/admin/bookings/${id}/cancel`,
    method: 'POST',
    params: { reason }
  })
}

// 确认订单（管理员端）
export function adminConfirmBooking(id) {
  return request({
    url: `/api/admin/bookings/${id}/confirm`,
    method: 'POST'
  })
}

// 办理入住（管理员端）
export function checkInBooking(id) {
  return request({
    url: `/api/admin/bookings/${id}/check-in`,
    method: 'POST'
  })
}

// 办理退房（管理员端）
export function checkOutBooking(id) {
  return request({
    url: `/api/admin/bookings/${id}/check-out`,
    method: 'POST'
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
