<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1>仪表盘</h1>
      <p>欢迎使用悦鑫乐怡民宿管理系统</p>
    </div>
    
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><House /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.totalRooms }}</div>
          <div class="stat-label">总房间数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon success">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.todayBookings }}</div>
          <div class="stat-label">今日订单</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon warning">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">{{ stats.totalCustomers }}</div>
          <div class="stat-label">客户总数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon danger">
          <el-icon><Money /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">¥{{ stats.todayRevenue }}</div>
          <div class="stat-label">今日收入</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon success">
          <el-icon><Money /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">¥{{ stats.totalRevenue }}</div>
          <div class="stat-label">总收入</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon info">
          <el-icon><Money /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-value">¥{{ stats.accountBalance }}</div>
          <div class="stat-label">账户余额</div>
        </div>
      </div>
    </div>
    
    <!-- 图表区域 -->
    <div class="charts-grid">
      <div class="chart-card">
        <div class="card-header">
          <h3>入住率趋势</h3>
        </div>
        <div class="chart-content">
          <div ref="occupancyChartRef" class="chart-box"></div>
        </div>
      </div>
      
      <div class="chart-card">
        <div class="card-header">
          <h3>收入统计</h3>
        </div>
        <div class="chart-content">
          <div ref="revenueChartRef" class="chart-box"></div>
        </div>
      </div>
    </div>
    
  </div>
</template>

<script setup>
import { reactive, onMounted, ref } from 'vue'
import { getDashboardStatistics, getOccupancyTrend, getRevenueTrend } from '@/api/modules/statistics'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

// 统计数据
const stats = reactive({
  totalRooms: 0,
  todayBookings: 0,
  totalCustomers: 0,
  todayRevenue: 0,
  totalRevenue: 0,
  accountBalance: 0
})

// 获取统计数据
const getStats = async () => {
  try {
    const response = await getDashboardStatistics()
    // 响应拦截器已经处理了成功响应，直接使用data
    const data = response.data
    if (data) {
      stats.totalRooms = data.totalRooms || 0
      stats.todayBookings = data.todayBookings || 0
      stats.totalCustomers = data.totalCustomers || 0
      stats.todayRevenue = data.todayRevenue || 0
      stats.totalRevenue = data.totalRevenue || 0
      stats.accountBalance = data.accountBalance || 0
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('获取统计数据失败: ' + (error.message || '未知错误'))
  }
}

// 入住率图表
const occupancyChartRef = ref(null)
let occupancyChartInstance = null
const revenueChartRef = ref(null)
let revenueChartInstance = null

const renderOccupancyChart = (dates, rates) => {
  if (!occupancyChartInstance) {
    occupancyChartInstance = echarts.init(occupancyChartRef.value)
    window.addEventListener('resize', () => occupancyChartInstance && occupancyChartInstance.resize())
  }
  occupancyChartInstance.setOption({
    tooltip: { trigger: 'axis', valueFormatter: (v) => `${v}%` },
    grid: { left: 40, right: 20, top: 30, bottom: 30 },
    xAxis: { type: 'category', data: dates },
    yAxis: { type: 'value', axisLabel: { formatter: '{value}%' }, max: 100 },
    series: [
      {
        name: '入住率',
        type: 'line',
        data: rates,
        smooth: true,
        areaStyle: { opacity: 0.2 },
        showSymbol: false,
        lineStyle: { width: 2, color: '#409eff' }
      }
    ]
  })
}

const loadOccupancyTrend = async () => {
  try {
    const { data } = await getOccupancyTrend(7)
    renderOccupancyChart(data?.dates || [], (data?.occupancyRates || []).map(v => Number(v)))
  } catch (e) {
    console.error('获取入住率趋势失败:', e)
  }
}

const renderRevenueChart = (dates, revenues) => {
  if (!revenueChartInstance) {
    revenueChartInstance = echarts.init(revenueChartRef.value)
    window.addEventListener('resize', () => revenueChartInstance && revenueChartInstance.resize())
  }
  revenueChartInstance.setOption({
    tooltip: { trigger: 'axis', valueFormatter: (v) => `¥${v}` },
    grid: { left: 40, right: 20, top: 30, bottom: 30 },
    xAxis: { type: 'category', data: dates },
    yAxis: { type: 'value', axisLabel: { formatter: '¥{value}' } },
    series: [
      {
        name: '收入',
        type: 'bar',
        data: revenues,
        itemStyle: { color: '#67C23A' }
      }
    ]
  })
}

const loadRevenueTrend = async () => {
  try {
    const { data } = await getRevenueTrend(7)
    renderRevenueChart(data?.dates || [], (data?.revenues || []).map(v => Number(v)))
  } catch (e) {
    console.error('获取收入趋势失败:', e)
  }
}

onMounted(() => {
  getStats()
  loadOccupancyTrend()
  loadRevenueTrend()
})
</script>

<style lang="scss" scoped>
.dashboard {
  .dashboard-header {
    margin-bottom: 24px;
    
    h1 {
      margin: 0 0 8px 0;
      color: #303133;
      font-size: 28px;
      font-weight: 500;
    }
    
    p {
      margin: 0;
      color: #909399;
      font-size: 16px;
    }
  }
  
  .stats-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 24px;
    margin-bottom: 32px;
    
    .stat-card {
      display: flex;
      align-items: center;
      padding: 24px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
      
      .stat-icon {
        width: 60px;
        height: 60px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 50%;
        background: #409eff;
        color: white;
        font-size: 24px;
        margin-right: 16px;
        
        &.success {
          background: #67c23a;
        }
        
        &.warning {
          background: #e6a23c;
        }
        
        &.danger {
          background: #f56c6c;
        }
      }
      
      .stat-content {
        .stat-value {
          font-size: 24px;
          font-weight: 600;
          color: #303133;
          margin-bottom: 4px;
        }
        
        .stat-label {
          font-size: 14px;
          color: #909399;
        }
      }
    }
  }
  
  .charts-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
    gap: 24px;
    margin-bottom: 32px;
    
    .chart-card {
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
      
      .card-header {
        padding: 20px 24px 0;
        
        h3 {
          margin: 0;
          color: #303133;
          font-size: 18px;
          font-weight: 500;
        }
      }
      
      .chart-content {
        padding: 24px;
        
        .chart-box { height: 300px; }
      }
    }
  }
  
}
</style>
