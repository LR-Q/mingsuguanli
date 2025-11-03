import request from '@/utils/request'

// 获取房间评论列表
export function getRoomReviews(roomId, params) {
  return request({
    url: `/api/user/reviews/room/${roomId}`,
    method: 'GET',
    params
  })
}

// 提交评论
export function submitReview(data) {
  return request({
    url: '/api/user/reviews',
    method: 'POST',
    data
  })
}


