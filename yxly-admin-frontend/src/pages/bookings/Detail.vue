<template>
  <div class="booking-detail">
    <div class="page-header">
      <div class="header-left">
        <el-button @click="goBack" icon="ArrowLeft" circle />
        <h1>订单详情</h1>
      </div>
      <div class="header-right">
        <el-tag :type="getStatusColor(detail.bookingStatus)" size="large">
          {{ detail.bookingStatusName }}
        </el-tag>
      </div>
    </div>
    
    <div v-loading="loading" class="page-content">
      <el-card shadow="never" class="info-card">
        <template #header>
          <div class="card-header">
            <span class="header-title">基本信息</span>
          </div>
        </template>
        
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">
            <span class="order-no">{{ detail.orderNo }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ detail.createTime }}
          </el-descriptions-item>
          <el-descriptions-item label="客户姓名">
            {{ detail.contactName || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="联系电话">
            {{ detail.contactPhone || '-' }}
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card shadow="never" class="info-card">
        <template #header>
          <div class="card-header">
            <span class="header-title">房间信息</span>
          </div>
        </template>
        
        <el-descriptions :column="2" border>
          <el-descriptions-item label="房间号">
            {{ detail.roomNumber || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="房间类型">
            {{ detail.roomType || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="入住日期">
            {{ detail.checkInDate }}
          </el-descriptions-item>
          <el-descriptions-item label="退房日期">
            {{ detail.checkOutDate }}
          </el-descriptions-item>
          <el-descriptions-item label="住宿天数">
            {{ detail.nights }}晚
          </el-descriptions-item>
          <el-descriptions-item label="入住人数">
            {{ detail.guestsCount }}人
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card shadow="never" class="info-card">
        <template #header>
          <div class="card-header">
            <span class="header-title">费用信息</span>
          </div>
        </template>
        
        <el-descriptions :column="2" border>
          <el-descriptions-item label="房间单价">
            <span class="price">¥{{ detail.roomPrice }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="订单总金额">
            <span class="price total">¥{{ detail.totalAmount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="已付金额">
            <span class="price paid">¥{{ detail.paidAmount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="支付状态">
            <el-tag :type="getPaymentStatusColor(detail.paymentStatus)">
              {{ detail.paymentStatusName }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <el-card v-if="detail.specialRequests" shadow="never" class="info-card">
        <template #header>
          <div class="card-header">
            <span class="header-title">特殊要求</span>
          </div>
        </template>
        
        <div class="special-requests">
          {{ detail.specialRequests }}
        </div>
      </el-card>

      <el-card v-if="detail.bookingStatus === 5 || detail.bookingStatus === 6" shadow="never" class="info-card">
        <template #header>
          <div class="card-header">
            <span class="header-title">取消信息</span>
          </div>
        </template>
        
        <el-descriptions :column="2" border>
          <el-descriptions-item label="取消时间">
            {{ detail.cancelTime || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="取消原因">
            {{ detail.cancelReason || '-' }}
          </el-descriptions-item>
        </el-descriptions>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAdminBookingDetail } from '@/api/modules/booking'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const detail = ref({})

// 返回上一页
const goBack = () => {
  router.back()
}

// 获取订单详情
const loadDetail = async () => {
  loading.value = true
  try {
    const id = route.params.id
    const response = await getAdminBookingDetail(id)
    detail.value = response.data || {}
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error(error.message || '获取订单详情失败')
  } finally {
    loading.value = false
  }
}

// 获取订单状态颜色
const getStatusColor = (status) => {
  const colors = {
    1: 'warning',   // 待确认
    2: 'success',   // 已确认
    3: 'primary',   // 已入住
    4: 'info',      // 已完成
    5: 'danger',    // 已取消（用户）
    6: 'danger'     // 被取消（管理员）
  }
  return colors[status] || ''
}

// 获取支付状态颜色
const getPaymentStatusColor = (status) => {
  const colors = {
    0: 'info',      // 未支付
    1: 'success',   // 已支付
    2: 'warning',   // 部分支付
    3: 'danger'     // 已退款
  }
  return colors[status] || ''
}

onMounted(() => {
  loadDetail()
})
</script>

<style lang="scss" scoped>
.booking-detail {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    .header-left {
      display: flex;
      align-items: center;
      gap: 16px;
      
      h1 {
        margin: 0;
        color: #303133;
        font-size: 24px;
        font-weight: 500;
      }
    }
  }
  
  .page-content {
    .info-card {
      margin-bottom: 20px;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      .card-header {
        .header-title {
          font-size: 16px;
          font-weight: 600;
          color: #303133;
        }
      }
    }
    
    .order-no {
      font-family: 'Courier New', monospace;
      font-weight: 600;
      color: #409EFF;
    }
    
    .price {
      font-size: 16px;
      font-weight: 600;
      color: #67C23A;
      
      &.total {
        font-size: 18px;
        color: #E6A23C;
      }
      
      &.paid {
        color: #409EFF;
      }
    }
    
    .special-requests {
      padding: 12px;
      background: #f5f7fa;
      border-radius: 4px;
      line-height: 1.6;
      color: #606266;
    }
  }
}

:deep(.el-descriptions__label) {
  font-weight: 600;
  background: #fafafa;
}
</style>
