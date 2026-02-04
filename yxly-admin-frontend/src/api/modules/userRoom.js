import request from '@/utils/request'

// 用户端房间相关API

/**
 * 获取可用房间列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getAvailableRooms(params) {
  return request({
    url: '/api/v1/rooms',
    method: 'GET',
    params
  })
}

/**
 * 根据ID查询房间详情
 * @param {number} id 房间ID
 * @returns {Promise}
 */
export function getUserRoomById(id) {
  return request({
    url: `/api/v1/rooms/${id}`,
    method: 'GET'
  })
}

/**
 * 获取房型列表
 * @returns {Promise}
 */
export function getUserRoomTypes() {
  return request({
    url: '/api/v1/rooms/types',
    method: 'GET'
  })
}

/**
 * 搜索房间
 * @param {Object} params 搜索参数
 * @returns {Promise}
 */
export function searchRooms(params) {
  return request({
    url: '/api/v1/rooms/search',
    method: 'GET',
    params
  })
}

// 获取所有民宿位置列表（用户端）
export function getUserLocations() {
  return request({
    url: '/api/v1/location/list',
    method: 'GET'
  })
}
