import request from '@/utils/request'

// 房间管理相关API

/**
 * 分页查询房间列表
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getRoomPage(params) {
  return request({
    url: '/api/v1/admin/rooms',
    method: 'GET',
    params
  })
}

/**
 * 根据ID查询房间详情
 * @param {number} id 房间ID
 * @returns {Promise}
 */
export function getRoomById(id) {
  return request({
    url: `/api/v1/admin/rooms/${id}`,
    method: 'GET'
  })
}

/**
 * 创建房间
 * @param {Object} data 房间数据
 * @returns {Promise}
 */
export function createRoom(data) {
  return request({
    url: '/api/v1/admin/rooms',
    method: 'POST',
    data
  })
}

/**
 * 更新房间
 * @param {number} id 房间ID
 * @param {Object} data 房间数据
 * @returns {Promise}
 */
export function updateRoom(id, data) {
  return request({
    url: `/api/v1/admin/rooms/${id}`,
    method: 'PUT',
    data
  })
}

/**
 * 删除房间
 * @param {number} id 房间ID
 * @returns {Promise}
 */
export function deleteRoom(id) {
  return request({
    url: `/api/v1/admin/rooms/${id}`,
    method: 'DELETE'
  })
}

/**
 * 获取房型列表
 * @returns {Promise}
 */
export function getRoomTypes() {
  return request({
    url: '/api/v1/admin/rooms/types',
    method: 'GET'
  })
}

/**
 * 更新房间状态
 * @param {number} id 房间ID
 * @param {number} status 新状态
 * @returns {Promise}
 */
export function updateRoomStatus(id, status) {
  return request({
    url: `/api/v1/admin/rooms/${id}/status`,
    method: 'PUT',
    params: { status }
  })
}

/**
 * 上传房间图片
 * @param {FormData} formData 图片文件
 * @param {number} roomId 房间ID
 * @returns {Promise}
 */
export function uploadRoomImage(formData, roomId) {
  // 添加房间ID到FormData
  formData.append('roomId', roomId)
  
  return request({
    url: '/api/v1/admin/files/upload/room-image',
    method: 'POST',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 删除文件
 * @param {string} fileUrl 文件URL
 * @returns {Promise}
 */
export function deleteFile(fileUrl) {
  return request({
    url: '/api/v1/admin/files/delete',
    method: 'DELETE',
    params: { fileUrl }
  })
}