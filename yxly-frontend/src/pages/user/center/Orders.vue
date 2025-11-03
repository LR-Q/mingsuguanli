<template>
  <div class="orders-page">
    <!-- 订单统计 -->
    <el-row :gutter="20" class="order-stats">
      <el-col :span="6">
        <el-card class="stat-card" shadow="never">
          <div class="stat-info">
            <div class="stat-icon pending">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-content">
              <h3>待确认</h3>
              <p class="stat-number">{{ orderStats.pending }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card" shadow="never">
          <div class="stat-info">
            <div class="stat-icon confirmed">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stat-content">
              <h3>已确认</h3>
              <p class="stat-number">{{ orderStats.confirmed }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card" shadow="never">
          <div class="stat-info">
            <div class="stat-icon completed">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-content">
              <h3>已完成</h3>
              <p class="stat-number">{{ orderStats.completed }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card" shadow="never">
          <div class="stat-info">
            <div class="stat-icon cancelled">
              <el-icon><Close /></el-icon>
            </div>
            <div class="stat-content">
              <h3>已取消</h3>
              <p class="stat-number">{{ orderStats.cancelled }}</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 订单筛选 -->
    <el-card class="filter-card" shadow="never">
      <div class="order-filters">
        <div class="filter-tabs">
          <el-button 
            v-for="tab in statusTabs" 
            :key="tab.value"
            :type="activeStatus === tab.value ? 'primary' : ''"
            :plain="activeStatus !== tab.value"
            @click="handleStatusChange(tab.value)"
          >
            {{ tab.label }}
          </el-button>
        </div>
        
        <div class="filter-controls">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索订单号或房间名称"
            style="width: 200px"
            clearable
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 240px; margin-left: 12px"
          />
        </div>
      </div>
    </el-card>

    <!-- 订单列表 -->
    <div class="order-list">
      <el-card 
        v-for="order in filteredOrders" 
        :key="order.id" 
        class="order-item" 
        shadow="never"
      >
        <div class="order-header">
          <div class="order-info">
            <h3>{{ order.roomName }}</h3>
            <p class="order-number">订单号：{{ order.orderNumber }}</p>
          </div>
          <div class="order-status">
            <el-tag :type="getStatusColor(order.status)" size="large">
              {{ getStatusName(order.status) }}
            </el-tag>
          </div>
        </div>
        
        <div class="order-content">
          <div class="room-image">
            <el-image 
              :src="order.roomImage" 
              fit="cover"
              style="width: 120px; height: 90px; border-radius: 6px"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
          </div>
          
          <div class="order-details">
            <div class="detail-row">
              <span class="label">入住时间：</span>
              <span>{{ formatDate(order.checkInDate) }} - {{ formatDate(order.checkOutDate) }}</span>
            </div>
            <div class="detail-row">
              <span class="label">入住天数：</span>
              <span>{{ order.nights }}晚</span>
            </div>
            <div class="detail-row">
              <span class="label">入住人数：</span>
              <span>{{ order.guests }}人</span>
            </div>
            <div class="detail-row">
              <span class="label">联系电话：</span>
              <span>{{ order.contactPhone }}</span>
            </div>
            <div class="detail-row" v-if="order.specialRequests">
              <span class="label">特殊要求：</span>
              <span>{{ order.specialRequests }}</span>
            </div>
          </div>
          
          <div class="order-price">
            <div class="price-info">
              <p class="total-price">¥{{ formatAmount(order.totalAmount) }}</p>
              <p class="price-detail">{{ order.nights }}晚 × ¥{{ formatAmount(order.roomPrice) }}/晚</p>
            </div>
          </div>
        </div>
        
        <div class="order-footer">
          <div class="order-time">
            <span>下单时间：{{ formatDateTime(order.createTime) }}</span>
          </div>
          <div class="order-actions">
            <el-button 
              v-if="order.status === 1" 
              type="danger" 
              plain 
              size="small"
              @click="cancelOrder(order)"
            >
              取消订单
            </el-button>
            
            <el-button 
              v-if="order.status === 2" 
              type="primary" 
              plain 
              size="small"
              @click="viewOrder(order)"
            >
              查看详情
            </el-button>
            
            <el-button 
              v-if="order.status === 4" 
              type="success" 
              plain 
              size="small"
              @click="reviewOrder(order)"
            >
              评价订单
            </el-button>
            
            <el-button 
              v-if="order.status === 5 && order.refundStatus === 'pending'" 
              type="warning" 
              plain 
              size="small"
              @click="viewRefund(order)"
            >
              查看退款
            </el-button>
            
            <el-button 
              size="small" 
              @click="viewOrder(order)"
            >
              订单详情
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="filteredOrders.length > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[5, 10, 20]"
        :total="totalOrders"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 空状态 -->
    <el-empty 
      v-if="filteredOrders.length === 0" 
      description="暂无订单记录"
      :image-size="200"
    >
      <el-button type="primary" @click="$router.push('/rooms')">
        去预订房间
      </el-button>
    </el-empty>

    <!-- 订单详情对话框 -->
    <el-dialog v-model="showOrderDetail" title="订单详情" width="600px">
      <div v-if="selectedOrder" class="order-detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ selectedOrder.orderNumber }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusColor(selectedOrder.status)">
              {{ getStatusName(selectedOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="房间名称">{{ selectedOrder.roomName }}</el-descriptions-item>
          <el-descriptions-item label="房间类型">{{ selectedOrder.roomType }}</el-descriptions-item>
          <el-descriptions-item label="入住日期">{{ formatDate(selectedOrder.checkInDate) }}</el-descriptions-item>
          <el-descriptions-item label="退房日期">{{ formatDate(selectedOrder.checkOutDate) }}</el-descriptions-item>
          <el-descriptions-item label="入住天数">{{ selectedOrder.nights }}晚</el-descriptions-item>
          <el-descriptions-item label="入住人数">{{ selectedOrder.guests }}人</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ selectedOrder.contactName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ selectedOrder.contactPhone }}</el-descriptions-item>
          <el-descriptions-item label="房间单价">¥{{ formatAmount(selectedOrder.roomPrice) }}/晚</el-descriptions-item>
          <el-descriptions-item label="订单总额">¥{{ formatAmount(selectedOrder.totalAmount) }}</el-descriptions-item>
          <el-descriptions-item label="下单时间" :span="2">{{ formatDateTime(selectedOrder.createTime) }}</el-descriptions-item>
          <el-descriptions-item v-if="selectedOrder.specialRequests" label="特殊要求" :span="2">
            {{ selectedOrder.specialRequests }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 评价对话框 -->
    <el-dialog v-model="showReview" title="订单评价" width="500px">
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" :max="5" show-text />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input
            v-model="reviewForm.content"
            type="textarea"
            :rows="4"
            placeholder="请分享您的入住体验..."
          />
        </el-form-item>
        <el-form-item label="是否匿名">
          <el-switch v-model="reviewForm.anonymous" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showReview = false">取消</el-button>
        <el-button type="primary" @click="submitReview">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Clock, 
  Check, 
  CircleCheck, 
  Close, 
  Search, 
  Picture 
} from '@element-plus/icons-vue'
import { getMyBookingList, cancelBooking } from '@/api/modules/booking'
import { submitReview as submitReviewApi } from '@/api/modules/review'

// 响应式数据
const activeStatus = ref('all')
const searchKeyword = ref('')
const dateRange = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalOrders = ref(0)
const loading = ref(false)

const showOrderDetail = ref(false)
const showReview = ref(false)
const selectedOrder = ref(null)

// 订单数据
const orders = ref([])

// 订单统计
const orderStats = reactive({
  pending: 0,
  confirmed: 0,
  completed: 0,
  cancelled: 0
})

// 状态标签
const statusTabs = [
  { label: '全部订单', value: 'all' },
  { label: '待确认', value: 1 },
  { label: '已确认', value: 2 },
  { label: '已完成', value: 4 },
  { label: '已取消', value: 5 },
  { label: '被取消', value: 6 }
]

// 评价表单
const reviewForm = reactive({
  rating: 5,
  content: '',
  anonymous: false
})

// 计算属性
const filteredOrders = computed(() => {
  return orders.value
})

// 方法
const formatAmount = (amount) => {
  return amount.toFixed(2)
}

const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const formatDateTime = (dateStr) => {
  return new Date(dateStr).toLocaleString('zh-CN')
}

const getStatusName = (status) => {
  const names = {
    1: '待确认',
    2: '已确认',
    3: '已入住',
    4: '已完成',
    5: '已取消',
    6: '被取消'
  }
  return names[status] || '未知'
}

const getStatusColor = (status) => {
  const colors = {
    1: 'warning',
    2: 'primary',
    3: 'info',
    4: 'success',
    5: 'danger',
    6: 'danger'
  }
  return colors[status] || ''
}

const handleSizeChange = (size) => {
  pageSize.value = size
  loadOrders()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadOrders()
}

const cancelOrder = async (order) => {
  try {
    const { value: reason } = await ElMessageBox.prompt('请输入取消原因（可选）', '取消订单', {
      confirmButtonText: '确定取消',
      cancelButtonText: '暂不取消',
      inputPattern: /.*/,
      inputPlaceholder: '请输入取消原因'
    })

    await cancelBooking(order.id, reason || '用户主动取消')
    
    ElMessage.success('订单已取消')
    
    // 重新加载订单列表
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '取消订单失败')
    }
  }
}

const viewOrder = (order) => {
  selectedOrder.value = order
  showOrderDetail.value = true
}

const reviewOrder = (order) => {
  selectedOrder.value = order
  showReview.value = true
  
  // 重置评价表单
  Object.assign(reviewForm, {
    rating: 5,
    content: '',
    anonymous: false
  })
}

const viewRefund = (order) => {
  ElMessage.info('退款正在处理中，请耐心等待')
}

const submitReview = async () => {
  try {
    if (!reviewForm.content.trim()) {
      ElMessage.error('请填写评价内容')
      return
    }

    await submitReviewApi({
      roomId: selectedOrder.value?.roomId,
      orderId: selectedOrder.value?.id,
      rating: reviewForm.rating,
      content: reviewForm.content,
      anonymous: reviewForm.anonymous
    })

    ElMessage.success('评价提交成功')
    showReview.value = false
  } catch (error) {
    ElMessage.error('评价提交失败，请重试')
  }
}

// 加载订单列表
const loadOrders = async () => {
  try {
    loading.value = true
    
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    
    // 如果选择了具体状态，传递状态参数
    if (activeStatus.value !== 'all') {
      params.status = activeStatus.value
    }
    
    const response = await getMyBookingList(params)
    
    // 处理响应数据
    if (response.data && response.data.records) {
      orders.value = response.data.records.map(order => ({
        id: order.id,
        roomId: order.roomId,
        orderNumber: order.orderNo,
        roomName: order.roomName || '房间',
        roomType: order.roomType || '',
        roomImage: '/api/placeholder/300/200',
        roomPrice: order.roomPrice,
        checkInDate: order.checkInDate,
        checkOutDate: order.checkOutDate,
        nights: order.nights,
        guests: order.guestsCount,
        contactName: order.contactName,
        contactPhone: order.contactPhone,
        specialRequests: order.specialRequests,
        totalAmount: order.totalAmount,
        status: order.bookingStatus,
        createTime: order.createTime,
        cancelReason: order.cancelReason,
        cancelTime: order.cancelTime
      }))
      
      totalOrders.value = response.data.total
      
      // 更新统计数据
      updateStats()
    }
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败，请重试')
  } finally {
    loading.value = false
  }
}

// 更新统计数据
const updateStats = async () => {
  try {
    // 获取所有订单统计
    const allResponse = await getMyBookingList({ current: 1, size: 1000 })
    if (allResponse.data && allResponse.data.records) {
      const allOrders = allResponse.data.records
      orderStats.pending = allOrders.filter(o => o.bookingStatus === 1).length
      orderStats.confirmed = allOrders.filter(o => o.bookingStatus === 2).length
      orderStats.completed = allOrders.filter(o => o.bookingStatus === 4).length
      // 已取消和被取消都计入已取消统计
      orderStats.cancelled = allOrders.filter(o => o.bookingStatus === 5 || o.bookingStatus === 6).length
    }
  } catch (error) {
    console.error('更新统计失败:', error)
  }
}

// 监听状态变化
const handleStatusChange = (status) => {
  activeStatus.value = status
  currentPage.value = 1
  loadOrders()
}

onMounted(() => {
  loadOrders()
})
</script>

<style lang="scss" scoped>
.orders-page {
  .order-stats {
    margin-bottom: 20px;
    
    .stat-card {
      .stat-info {
        display: flex;
        align-items: center;
        gap: 16px;
        
        .stat-icon {
          width: 50px;
          height: 50px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          color: white;
          font-size: 20px;
          
          &.pending {
            background: linear-gradient(135deg, #e6a23c, #f56c6c);
          }
          
          &.confirmed {
            background: linear-gradient(135deg, #409eff, #67c23a);
          }
          
          &.completed {
            background: linear-gradient(135deg, #67c23a, #409eff);
          }
          
          &.cancelled {
            background: linear-gradient(135deg, #f56c6c, #e6a23c);
          }
        }
        
        .stat-content {
          flex: 1;
          
          h3 {
            margin: 0 0 4px 0;
            color: #606266;
            font-size: 14px;
            font-weight: 500;
          }
          
          .stat-number {
            margin: 0;
            color: #303133;
            font-size: 24px;
            font-weight: 600;
          }
        }
      }
    }
  }
  
  .filter-card {
    margin-bottom: 20px;
    
    .order-filters {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .filter-tabs {
        display: flex;
        gap: 8px;
      }
      
      .filter-controls {
        display: flex;
        align-items: center;
      }
    }
  }
  
  .order-list {
    .order-item {
      margin-bottom: 16px;
      
      .order-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 16px;
        
        .order-info {
          h3 {
            margin: 0 0 4px 0;
            color: #303133;
            font-size: 18px;
            font-weight: 600;
          }
          
          .order-number {
            margin: 0;
            color: #909399;
            font-size: 14px;
          }
        }
      }
      
      .order-content {
        display: flex;
        gap: 20px;
        margin-bottom: 16px;
        
        .room-image {
          flex-shrink: 0;
          
          .image-slot {
            width: 120px;
            height: 90px;
            display: flex;
            align-items: center;
            justify-content: center;
            background: #f5f7fa;
            border-radius: 6px;
            color: #c0c4cc;
            font-size: 24px;
          }
        }
        
        .order-details {
          flex: 1;
          
          .detail-row {
            margin-bottom: 8px;
            font-size: 14px;
            
            .label {
              color: #606266;
              font-weight: 500;
            }
          }
        }
        
        .order-price {
          text-align: right;
          
          .price-info {
            .total-price {
              margin: 0 0 4px 0;
              color: #f56c6c;
              font-size: 24px;
              font-weight: 600;
            }
            
            .price-detail {
              margin: 0;
              color: #909399;
              font-size: 12px;
            }
          }
        }
      }
      
      .order-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding-top: 16px;
        border-top: 1px solid #ebeef5;
        
        .order-time {
          color: #909399;
          font-size: 12px;
        }
        
        .order-actions {
          display: flex;
          gap: 8px;
        }
      }
    }
  }
  
  .pagination-wrapper {
    margin-top: 20px;
    text-align: center;
  }
}

.order-detail-content {
  :deep(.el-descriptions__label) {
    font-weight: 500;
  }
}

:deep(.el-card__header) {
  background: #fafafa;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-empty) {
  padding: 60px 0;
}

// 响应式设计
@media (max-width: 768px) {
  .order-stats {
    .el-col {
      margin-bottom: 16px;
    }
  }
  
  .order-filters {
    flex-direction: column;
    gap: 16px;
    align-items: stretch !important;
    
    .filter-tabs {
      flex-wrap: wrap;
    }
    
    .filter-controls {
      flex-direction: column;
      gap: 12px;
      align-items: stretch;
    }
  }
  
  .order-content {
    flex-direction: column !important;
    
    .room-image {
      align-self: center;
    }
    
    .order-price {
      text-align: left !important;
    }
  }
  
  .order-footer {
    flex-direction: column !important;
    gap: 12px;
    align-items: stretch !important;
    
    .order-actions {
      justify-content: center;
    }
  }
}
</style>
