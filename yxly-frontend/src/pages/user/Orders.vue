<template>
  <div class="user-orders">
    <div class="container">
      <div class="orders-header">
        <h1>我的订单</h1>
        <p>查看和管理您的预订记录</p>
      </div>

      <!-- 订单筛选 -->
      <div class="orders-filter">
        <el-tabs v-model="activeStatus" @tab-change="handleStatusChange">
          <el-tab-pane label="全部订单" name="all" />
          <el-tab-pane label="待确认" name="pending" />
          <el-tab-pane label="已确认" name="confirmed" />
          <el-tab-pane label="已入住" name="checkedIn" />
          <el-tab-pane label="已完成" name="completed" />
          <el-tab-pane label="已取消" name="cancelled" />
        </el-tabs>
      </div>

      <!-- 订单列表 -->
      <div class="orders-list">
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="3" animated />
        </div>
        
        <div v-else-if="filteredOrders.length === 0" class="empty-container">
          <el-empty description="暂无订单记录">
            <el-button type="primary" @click="goToRooms">去预订房间</el-button>
          </el-empty>
        </div>
        
        <div v-else class="orders-grid">
          <el-card v-for="order in filteredOrders" :key="order.id" class="order-card">
            <div class="order-header">
              <div class="order-info">
                <h3>{{ order.roomName }}</h3>
                <p class="order-no">订单号：{{ order.orderNo }}</p>
              </div>
              <div class="order-status">
                <el-tag :type="getStatusType(order.status)">
                  {{ getStatusText(order.status) }}
                </el-tag>
              </div>
            </div>
            
            <div class="order-content">
              <div class="order-details">
                <div class="detail-item">
                  <span class="label">入住时间：</span>
                  <span>{{ order.checkInDate }} 至 {{ order.checkOutDate }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">入住天数：</span>
                  <span>{{ order.nights }}晚</span>
                </div>
                <div class="detail-item">
                  <span class="label">入住人数：</span>
                  <span>{{ order.guests }}人</span>
                </div>
                <div class="detail-item">
                  <span class="label">联系人：</span>
                  <span>{{ order.contactName }}</span>
                </div>
                <div class="detail-item">
                  <span class="label">联系电话：</span>
                  <span>{{ order.contactPhone }}</span>
                </div>
                <div v-if="order.specialRequests" class="detail-item">
                  <span class="label">特殊要求：</span>
                  <span>{{ order.specialRequests }}</span>
                </div>
              </div>
              
              <div class="order-price">
                <div class="price-info">
                  <span class="total-label">总价：</span>
                  <span class="total-amount">￥{{ order.totalAmount }}</span>
                </div>
                <div class="payment-status">
                  <el-tag :type="getPaymentStatusType(order.paymentStatus)" size="small">
                    {{ getPaymentStatusText(order.paymentStatus) }}
                  </el-tag>
                </div>
              </div>
            </div>
            
            <div class="order-actions">
              <div class="order-time">
                <span>预订时间：{{ formatDate(order.createTime) }}</span>
              </div>
              <div class="action-buttons">
                <el-button size="small" @click="viewOrder(order)">查看详情</el-button>
                <el-button 
                  v-if="order.status === 'pending'" 
                  type="danger" 
                  size="small" 
                  @click="cancelOrder(order)"
                >
                  取消订单
                </el-button>
                <el-button 
                  v-if="order.status === 'completed'" 
                  type="primary" 
                  size="small" 
                  @click="reviewOrder(order)"
                >
                  评价
                </el-button>
                <el-button 
                  v-if="['completed', 'cancelled'].includes(order.status)" 
                  size="small" 
                  @click="rebookOrder(order)"
                >
                  再次预订
                </el-button>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()

// 响应式数据
const loading = ref(true)
const activeStatus = ref('all')

// 模拟订单数据
const orders = ref([
  {
    id: 1,
    orderNo: 'YX202401150001',
    roomName: '豪华海景套房',
    checkInDate: '2024-02-01',
    checkOutDate: '2024-02-03',
    nights: 2,
    guests: 2,
    contactName: '张三',
    contactPhone: '13800138000',
    specialRequests: '希望安排高楼层房间',
    totalAmount: 1176,
    status: 'confirmed',
    paymentStatus: 'paid',
    createTime: '2024-01-15 14:30:00'
  },
  {
    id: 2,
    orderNo: 'YX202401120002',
    roomName: '温馨家庭房',
    checkInDate: '2024-01-25',
    checkOutDate: '2024-01-27',
    nights: 2,
    guests: 4,
    contactName: '李四',
    contactPhone: '13900139000',
    specialRequests: '',
    totalAmount: 776,
    status: 'completed',
    paymentStatus: 'paid',
    createTime: '2024-01-12 10:15:00'
  },
  {
    id: 3,
    orderNo: 'YX202401100003',
    roomName: '商务标准间',
    checkInDate: '2024-01-20',
    checkOutDate: '2024-01-22',
    nights: 2,
    guests: 1,
    contactName: '王五',
    contactPhone: '13700137000',
    specialRequests: '需要安静环境',
    totalAmount: 576,
    status: 'pending',
    paymentStatus: 'unpaid',
    createTime: '2024-01-10 16:45:00'
  }
])

// 计算属性
const filteredOrders = computed(() => {
  if (activeStatus.value === 'all') {
    return orders.value
  }
  return orders.value.filter(order => order.status === activeStatus.value)
})

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    pending: 'warning',
    confirmed: 'info',
    checkedIn: 'success',
    completed: 'success',
    cancelled: 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    pending: '待确认',
    confirmed: '已确认',
    checkedIn: '已入住',
    completed: '已完成',
    cancelled: '已取消'
  }
  return statusMap[status] || '未知'
}

// 获取支付状态类型
const getPaymentStatusType = (status) => {
  const statusMap = {
    unpaid: 'danger',
    paid: 'success',
    partial: 'warning',
    refunded: 'info'
  }
  return statusMap[status] || 'info'
}

// 获取支付状态文本
const getPaymentStatusText = (status) => {
  const statusMap = {
    unpaid: '未支付',
    paid: '已支付',
    partial: '部分支付',
    refunded: '已退款'
  }
  return statusMap[status] || '未知'
}

// 格式化日期
const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleString('zh-CN')
}

// 切换状态筛选
const handleStatusChange = (status) => {
  activeStatus.value = status
}

// 查看订单详情
const viewOrder = (order) => {
  ElMessage.info(`查看订单 ${order.orderNo} 详情`)
  // 这里可以跳转到订单详情页面
}

// 取消订单
const cancelOrder = async (order) => {
  try {
    await ElMessageBox.confirm(
      `确定要取消订单 ${order.orderNo} 吗？`,
      '取消订单',
      {
        confirmButtonText: '确定取消',
        cancelButtonText: '不取消',
        type: 'warning'
      }
    )
    
    // 模拟取消订单API调用
    order.status = 'cancelled'
    ElMessage.success('订单取消成功')
    
  } catch (error) {
    // 用户取消操作
  }
}

// 评价订单
const reviewOrder = (order) => {
  ElMessage.info(`为订单 ${order.orderNo} 写评价`)
  // 这里可以打开评价弹窗或跳转到评价页面
}

// 再次预订
const rebookOrder = (order) => {
  router.push({
    path: '/rooms',
    query: {
      checkIn: order.checkInDate,
      checkOut: order.checkOutDate,
      guests: order.guests
    }
  })
}

// 去预订房间
const goToRooms = () => {
  router.push('/rooms')
}

onMounted(() => {
  // 模拟加载数据
  setTimeout(() => {
    loading.value = false
  }, 1000)
})
</script>

<style lang="scss" scoped>
.user-orders {
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
  }
}

.orders-header {
  text-align: center;
  margin-bottom: 40px;
  
  h1 {
    margin: 0 0 16px 0;
    font-size: 32px;
    color: #333;
  }
  
  p {
    margin: 0;
    color: #666;
    font-size: 16px;
  }
}

.orders-filter {
  margin-bottom: 30px;
  
  .el-tabs {
    :deep(.el-tabs__header) {
      margin: 0 0 20px 0;
    }
  }
}

.orders-list {
  .loading-container,
  .empty-container {
    padding: 60px 0;
  }
  
  .orders-grid {
    display: grid;
    gap: 20px;
  }
  
  .order-card {
    border-radius: 8px;
    
    .order-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 20px;
      
      .order-info {
        h3 {
          margin: 0 0 8px 0;
          font-size: 18px;
          color: #333;
        }
        
        .order-no {
          margin: 0;
          color: #666;
          font-size: 14px;
        }
      }
    }
    
    .order-content {
      display: grid;
      grid-template-columns: 1fr auto;
      gap: 30px;
      margin-bottom: 20px;
      
      .order-details {
        .detail-item {
          display: flex;
          margin-bottom: 8px;
          
          .label {
            min-width: 80px;
            color: #666;
            font-size: 14px;
          }
          
          span:last-child {
            color: #333;
            font-size: 14px;
          }
        }
      }
      
      .order-price {
        text-align: right;
        
        .price-info {
          margin-bottom: 8px;
          
          .total-label {
            color: #666;
            font-size: 14px;
          }
          
          .total-amount {
            font-size: 20px;
            font-weight: bold;
            color: #e74c3c;
            margin-left: 8px;
          }
        }
      }
    }
    
    .order-actions {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-top: 20px;
      border-top: 1px solid #f0f0f0;
      
      .order-time {
        color: #999;
        font-size: 12px;
      }
      
      .action-buttons {
        display: flex;
        gap: 8px;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .order-content {
    grid-template-columns: 1fr !important;
    gap: 20px !important;
    
    .order-price {
      text-align: left !important;
    }
  }
  
  .order-actions {
    flex-direction: column !important;
    align-items: flex-start !important;
    gap: 12px;
    
    .action-buttons {
      width: 100%;
      justify-content: flex-end;
    }
  }
}
</style>
