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
    </div>
    
    <!-- 图表区域 -->
    <div class="charts-grid">
      <div class="chart-card">
        <div class="card-header">
          <h3>入住率趋势</h3>
        </div>
        <div class="chart-content">
          <!-- 这里可以集成图表库如ECharts -->
          <div class="placeholder-chart">
            <p>入住率图表</p>
          </div>
        </div>
      </div>
      
      <div class="chart-card">
        <div class="card-header">
          <h3>收入统计</h3>
        </div>
        <div class="chart-content">
          <div class="placeholder-chart">
            <p>收入统计图表</p>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 快捷操作 -->
    <div class="quick-actions">
      <h3>快捷操作</h3>
      <div class="actions-grid">
        <el-button type="primary" @click="$router.push('/bookings/create')">
          <el-icon><Plus /></el-icon>
          新建订单
        </el-button>
        <el-button type="success" @click="$router.push('/rooms/create')">
          <el-icon><Plus /></el-icon>
          添加房间
        </el-button>
        <el-button type="warning" @click="$router.push('/customers')">
          <el-icon><Search /></el-icon>
          查找客户
        </el-button>
        <el-button type="info" @click="$router.push('/bookings')">
          <el-icon><Document /></el-icon>
          订单管理
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'

// 统计数据
const stats = reactive({
  totalRooms: 0,
  todayBookings: 0,
  totalCustomers: 0,
  todayRevenue: 0
})

// 获取统计数据
const getStats = async () => {
  try {
    // 这里调用API获取统计数据
    // const response = await getStatistics()
    
    // 模拟数据
    stats.totalRooms = 50
    stats.todayBookings = 12
    stats.totalCustomers = 368
    stats.todayRevenue = 8680
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

onMounted(() => {
  getStats()
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
        
        .placeholder-chart {
          height: 300px;
          display: flex;
          align-items: center;
          justify-content: center;
          background: #f5f7fa;
          border-radius: 4px;
          color: #909399;
        }
      }
    }
  }
  
  .quick-actions {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    padding: 24px;
    
    h3 {
      margin: 0 0 16px 0;
      color: #303133;
      font-size: 18px;
      font-weight: 500;
    }
    
    .actions-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
      gap: 16px;
      
      .el-button {
        height: 48px;
        
        .el-icon {
          margin-right: 8px;
        }
      }
    }
  }
}
</style>
