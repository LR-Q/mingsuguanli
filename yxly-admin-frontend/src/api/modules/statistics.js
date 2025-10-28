import request from '@/utils/request'

// 统计数据相关API

/**
 * 获取仪表板统计数据
 * @returns {Promise}
 */
export function getDashboardStatistics() {
  return request({
    url: '/api/v1/admin/statistics/dashboard',
    method: 'GET'
  })
}

/**
 * 获取入住率趋势
 * @param {number} days 近N天，默认7
 */
export function getOccupancyTrend(days = 7) {
  return request({
    url: '/api/v1/admin/statistics/occupancy-trend',
    method: 'GET',
    params: { days }
  })
}

/**
 * 获取收入趋势
 * @param {number} days 近N天，默认7
 */
export function getRevenueTrend(days = 7) {
  return request({
    url: '/api/v1/admin/statistics/revenue-trend',
    method: 'GET',
    params: { days }
  })
}
