import request from '@/utils/request'

// 位置管理相关API

/**
 * 分页查询位置列表（管理员）
 * @param {Object} params 查询参数
 * @returns {Promise}
 */
export function getLocationPage(params) {
  return request({
    url: '/api/v1/admin/locations',
    method: 'GET',
    params
  })
}

/**
 * 根据ID查询位置详情（管理员）
 * @param {number} id 位置ID
 * @returns {Promise}
 */
export function getLocationById(id) {
  return request({
    url: `/api/v1/admin/locations/${id}`,
    method: 'GET'
  })
}

/**
 * 创建位置
 * @param {Object} data 位置数据
 * @returns {Promise}
 */
export function createLocation(data) {
  return request({
    url: '/api/v1/admin/locations',
    method: 'POST',
    data
  })
}

/**
 * 更新位置
 * @param {number} id 位置ID
 * @param {Object} data 位置数据
 * @returns {Promise}
 */
export function updateLocation(id, data) {
  return request({
    url: `/api/v1/admin/locations/${id}`,
    method: 'PUT',
    data
  })
}

/**
 * 删除位置
 * @param {number} id 位置ID
 * @returns {Promise}
 */
export function deleteLocation(id) {
  return request({
    url: `/api/v1/admin/locations/${id}`,
    method: 'DELETE'
  })
}

/**
 * 启用/禁用位置
 * @param {number} id 位置ID
 * @param {number} isActive 是否启用(0:禁用 1:启用)
 * @returns {Promise}
 */
export function toggleLocationStatus(id, isActive) {
  return request({
    url: `/api/v1/admin/locations/${id}/status`,
    method: 'PATCH',
    params: { isActive }
  })
}

/**
 * 获取启用的位置信息（用户端）
 * @returns {Promise}
 */
export function getActiveLocation() {
  return request({
    url: '/api/v1/location',
    method: 'GET'
  })
}

/**
 * 获取所有启用的位置列表（用户端）
 * @returns {Promise}
 */
export function getActiveLocationList() {
  return request({
    url: '/api/v1/location/list',
    method: 'GET'
  })
}
