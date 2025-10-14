import request from '@/utils/request'

// 获取房间列表
export function getRoomList(params) {
  return request({
    url: '/v1/rooms',
    method: 'GET',
    params
  })
}

// 获取房间详情
export function getRoomDetail(id) {
  return request({
    url: `/v1/rooms/${id}`,
    method: 'GET'
  })
}

// 创建房间
export function createRoom(data) {
  return request({
    url: '/v1/rooms',
    method: 'POST',
    data
  })
}

// 更新房间
export function updateRoom(id, data) {
  return request({
    url: `/v1/rooms/${id}`,
    method: 'PUT',
    data
  })
}

// 删除房间
export function deleteRoom(id) {
  return request({
    url: `/v1/rooms/${id}`,
    method: 'DELETE'
  })
}

// 获取房间类型列表
export function getRoomTypeList() {
  return request({
    url: '/v1/room-types',
    method: 'GET'
  })
}

// 检查房间可用性
export function checkRoomAvailability(params) {
  return request({
    url: '/v1/rooms/availability',
    method: 'GET',
    params
  })
}
