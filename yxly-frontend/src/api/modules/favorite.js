import request from '@/utils/request'

// 获取收藏列表
export function getFavoriteList(params) {
  return request({
    url: '/api/user/favorites',
    method: 'GET',
    params
  })
}

// 添加收藏
export function addFavorite(roomId) {
  return request({
    url: '/api/user/favorites',
    method: 'POST',
    data: { roomId }
  })
}

// 取消收藏
export function removeFavorite(roomId) {
  return request({
    url: `/api/user/favorites/${roomId}`,
    method: 'DELETE'
  })
}

// 检查房间是否已收藏
export function checkFavorite(roomId) {
  return request({
    url: `/api/user/favorites/check/${roomId}`,
    method: 'GET'
  })
}

// 批量检查房间收藏状态
export function checkMultipleFavorites(roomIds) {
  return request({
    url: '/api/user/favorites/check-batch',
    method: 'POST',
    data: { roomIds }
  })
}
