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
            @click="activeStatus = tab.value"
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
              v-if="order.status === 'pending'" 
              type="danger" 
              plain 
              size="small"
              @click="cancelOrder(order)"
            >
              取消订单
            </el-button>
            
            <el-button 
              v-if="order.status === 'confirmed'" 
              type="primary" 
              plain 
              size="small"
              @click="viewOrder(order)"
            >
              查看详情
            </el-button>
            
            <el-button 
              v-if="order.status === 'completed'" 
              type="success" 
              plain 
              size="small"
              @click="reviewOrder(order)"
            >
              评价订单
            </el-button>
            
            <el-button 
              v-if="order.status === 'cancelled' && order.refundStatus === 'pending'" 
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

// 响应式数据
const activeStatus = ref('all')
const searchKeyword = ref('')
const dateRange = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalOrders = ref(0)

const showOrderDetail = ref(false)
const showReview = ref(false)
const selectedOrder = ref(null)

// 订单统计
const orderStats = reactive({
  pending: 2,
  confirmed: 3,
  completed: 8,
  cancelled: 1
})

// 状态标签
const statusTabs = [
  { label: '全部订单', value: 'all' },
  { label: '待确认', value: 'pending' },
  { label: '已确认', value: 'confirmed' },
  { label: '已完成', value: 'completed' },
  { label: '已取消', value: 'cancelled' }
]

// 评价表单
const reviewForm = reactive({
  rating: 5,
  content: '',
  anonymous: false
})

// 模拟订单数据
const orders = ref([
  {
    id: 1,
    orderNumber: 'YX202410150001',
    roomName: '海景豪华大床房',
    roomType: '豪华大床房',
    roomImage: '/api/placeholder/300/200',
    roomPrice: 388.00,
    checkInDate: '2024-10-20',
    checkOutDate: '2024-10-22',
    nights: 2,
    guests: 2,
    contactName: '张三',
    contactPhone: '13800138000',
    specialRequests: '需要婴儿床',
    totalAmount: 776.00,
    status: 'confirmed',
    createTime: '2024-10-15 14:30:00'
  },
  {
    id: 2,
    orderNumber: 'YX202410140001',
    roomName: '温馨标准双人间',
    roomType: '标准双人间',
    roomImage: '/api/placeholder/300/200',
    roomPrice: 288.00,
    checkInDate: '2024-10-18',
    checkOutDate: '2024-10-19',
    nights: 1,
    guests: 2,
    contactName: '李四',
    contactPhone: '13800138001',
    specialRequests: '',
    totalAmount: 288.00,
    status: 'pending',
    createTime: '2024-10-14 09:15:00'
  },
  {
    id: 3,
    orderNumber: 'YX202410130001',
    roomName: '商务套房',
    roomType: '商务套房',
    roomImage: '/api/placeholder/300/200',
    roomPrice: 588.00,
    checkInDate: '2024-10-10',
    checkOutDate: '2024-10-12',
    nights: 2,
    guests: 1,
    contactName: '王五',
    contactPhone: '13800138002',
    specialRequests: '需要安静房间',
    totalAmount: 1176.00,
    status: 'completed',
    createTime: '2024-10-13 16:45:00'
  },
  {
    id: 4,
    orderNumber: 'YX202410120001',
    roomName: '经济型单人间',
    roomType: '经济单人间',
    roomImage: '/api/placeholder/300/200',
    roomPrice: 188.00,
    checkInDate: '2024-10-15',
    checkOutDate: '2024-10-16',
    nights: 1,
    guests: 1,
    contactName: '赵六',
    contactPhone: '13800138003',
    specialRequests: '',
    totalAmount: 188.00,
    status: 'cancelled',
    refundStatus: 'completed',
    createTime: '2024-10-12 11:20:00'
  }
])

// 计算属性
const filteredOrders = computed(() => {
  let result = orders.value

  // 按状态筛选
  if (activeStatus.value !== 'all') {
    result = result.filter(order => order.status === activeStatus.value)
  }

  // 按关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(order => 
      order.orderNumber.toLowerCase().includes(keyword) ||
      order.roomName.toLowerCase().includes(keyword)
    )
  }

  // 按日期范围筛选
  if (dateRange.value && dateRange.value.length === 2) {
    const [startDate, endDate] = dateRange.value
    result = result.filter(order => {
      const orderDate = new Date(order.createTime)
      return orderDate >= startDate && orderDate <= endDate
    })
  }

  return result
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
    pending: '待确认',
    confirmed: '已确认',
    completed: '已完成',
    cancelled: '已取消'
  }
  return names[status] || status
}

const getStatusColor = (status) => {
  const colors = {
    pending: 'warning',
    confirmed: 'primary',
    completed: 'success',
    cancelled: 'danger'
  }
  return colors[status] || ''
}

const handleSizeChange = (size) => {
  pageSize.value = size
  // 重新加载数据
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  // 重新加载数据
}

const cancelOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确定要取消这个订单吗？', '取消订单', {
      confirmButtonText: '确定取消',
      cancelButtonText: '暂不取消',
      type: 'warning'
    })

    // 这里调用取消订单API
    await new Promise(resolve => setTimeout(resolve, 1000))

    order.status = 'cancelled'
    orderStats.pending--
    orderStats.cancelled++

    ElMessage.success('订单已取消')
  } catch (error) {
    // 用户取消操作
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

    // 这里调用提交评价API
    await new Promise(resolve => setTimeout(resolve, 1000))

    ElMessage.success('评价提交成功')
    showReview.value = false
  } catch (error) {
    ElMessage.error('评价提交失败，请重试')
  }
}

onMounted(() => {
  totalOrders.value = orders.value.length
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
