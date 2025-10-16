<template>
  <div class="room-detail">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <el-button 
          type="primary" 
          :icon="ArrowLeft" 
          @click="handleBack"
          style="margin-right: 16px;"
        >
          返回
        </el-button>
        <h1>房间详情</h1>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="8" animated />
    </div>
    
    <!-- 房间详情内容 -->
    <div v-else-if="roomData" class="page-content">
      <!-- 基本信息卡片 -->
      <el-card class="info-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <span class="card-title">基本信息</span>
            <el-tag :type="getStatusColor(roomData.status)" size="large">
              {{ getStatusName(roomData.status) }}
            </el-tag>
          </div>
        </template>
        
        <el-row :gutter="24">
          <el-col :span="8">
            <div class="info-item">
              <span class="label">房间号：</span>
              <span class="value">{{ roomData.roomNumber }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">房型：</span>
              <span class="value">
                <el-tag :type="getRoomTypeColor(roomData.roomTypeId)">
                  {{ getRoomTypeName(roomData.roomTypeId) }}
                </el-tag>
              </span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">楼层：</span>
              <span class="value">{{ roomData.floorNumber }}楼</span>
            </div>
          </el-col>
        </el-row>
        
        <el-row :gutter="24" style="margin-top: 20px;">
          <el-col :span="8">
            <div class="info-item">
              <span class="label">床型：</span>
              <span class="value">{{ roomData.bedType || '未设置' }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">最大入住人数：</span>
              <span class="value">{{ roomData.maxGuests }}人</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">面积：</span>
              <span class="value">{{ roomData.area }}m²</span>
            </div>
          </el-col>
        </el-row>
        
        <el-row :gutter="24" style="margin-top: 20px;">
          <el-col :span="8">
            <div class="info-item">
              <span class="label">价格：</span>
              <span class="value price">¥{{ roomData.price }}/晚</span>
            </div>
          </el-col>
          <el-col :span="16">
            <div class="info-item">
              <span class="label">WiFi密码：</span>
              <span class="value">{{ roomData.wifiPassword || '未设置' }}</span>
            </div>
          </el-col>
        </el-row>
      </el-card>
      
      <!-- 房间图片 -->
      <el-card v-if="roomData.imageUrl" class="image-card" shadow="hover">
        <template #header>
          <span class="card-title">房间图片</span>
        </template>
        <div class="image-container">
          <el-image 
            :src="roomData.imageUrl" 
            :preview-src-list="[roomData.imageUrl]"
            fit="cover"
            class="room-image"
          >
            <template #error>
              <div class="image-slot">
                <el-icon><Picture /></el-icon>
                <span>图片加载失败</span>
              </div>
            </template>
          </el-image>
        </div>
      </el-card>
      
      <!-- 房间描述 -->
      <el-card v-if="roomData.description" class="description-card" shadow="hover">
        <template #header>
          <span class="card-title">房间描述</span>
        </template>
        <div class="description-content">
          {{ roomData.description }}
        </div>
      </el-card>
      
      <!-- 设施信息 -->
      <el-card v-if="roomData.facilities && roomData.facilities.length > 0" class="facilities-card" shadow="hover">
        <template #header>
          <span class="card-title">房间设施</span>
        </template>
        <div class="facilities-content">
          <el-tag 
            v-for="facility in roomData.facilities" 
            :key="facility"
            type="info"
            size="large"
            style="margin: 4px 8px 4px 0;"
          >
            {{ facility }}
          </el-tag>
        </div>
      </el-card>
      
      <!-- 时间信息 -->
      <el-card class="time-card" shadow="hover">
        <template #header>
          <span class="card-title">时间信息</span>
        </template>
        <el-row :gutter="24">
          <el-col :span="12">
            <div class="info-item">
              <span class="label">创建时间：</span>
              <span class="value">{{ formatTime(roomData.createTime) }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="label">更新时间：</span>
              <span class="value">{{ formatTime(roomData.updateTime) }}</span>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>
    
    <!-- 数据不存在 -->
    <div v-else class="no-data">
      <el-empty description="房间不存在或已被删除">
        <el-button type="primary" @click="handleBack">返回列表</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Picture } from '@element-plus/icons-vue'
import { getRoomById, getRoomTypes } from '@/api/modules/room'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(true)
const roomData = ref(null)
const roomTypes = ref([])

// 获取房间详情
const loadRoomDetail = async () => {
  try {
    loading.value = true
    const roomId = route.params.id
    
    if (!roomId) {
      ElMessage.error('房间ID不存在')
      handleBack()
      return
    }
    
    const response = await getRoomById(roomId)
    roomData.value = response.data
    
    // 处理设施数据（如果是JSON字符串需要解析）
    if (roomData.value.facilities && typeof roomData.value.facilities === 'string') {
      try {
        roomData.value.facilities = JSON.parse(roomData.value.facilities)
      } catch (e) {
        console.warn('设施数据解析失败:', e)
        roomData.value.facilities = []
      }
    }
    
  } catch (error) {
    console.error('获取房间详情失败:', error)
    ElMessage.error('获取房间详情失败')
    roomData.value = null
  } finally {
    loading.value = false
  }
}

// 获取房型列表
const loadRoomTypes = async () => {
  try {
    const response = await getRoomTypes()
    roomTypes.value = response.data
  } catch (error) {
    console.error('获取房型列表失败:', error)
  }
}

// 工具方法
const getRoomTypeName = (roomTypeId) => {
  const roomType = roomTypes.value.find(type => type.id === roomTypeId)
  return roomType ? roomType.typeName : '未知房型'
}

const getRoomTypeColor = (roomTypeId) => {
  const colors = ['', 'success', 'warning', 'danger', 'info']
  return colors[roomTypeId % 5] || ''
}

const getStatusName = (status) => {
  const names = {
    1: '可用',
    2: '占用', 
    3: '维修',
    4: '清洁',
    5: '停用'
  }
  return names[status] || '未知'
}

const getStatusColor = (status) => {
  const colors = {
    1: 'success',
    2: 'warning',
    3: 'info', 
    4: 'primary',
    5: 'danger'
  }
  return colors[status] || ''
}

const formatTime = (time) => {
  if (!time) return '未知'
  return new Date(time).toLocaleString('zh-CN')
}

// 事件处理
const handleBack = () => {
  router.push('/admin/rooms')
}

// 生命周期
onMounted(() => {
  loadRoomTypes()
  loadRoomDetail()
})
</script>

<style lang="scss" scoped>
.room-detail {
  .page-header {
    margin-bottom: 24px;
    padding: 20px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    
    .header-left {
      display: flex;
      align-items: center;
      
      h1 {
        margin: 0;
        color: #303133;
        font-size: 24px;
        font-weight: 500;
      }
    }
  }
  
  .loading-container {
    background: white;
    border-radius: 8px;
    padding: 24px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }
  
  .page-content {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
  
  .no-data {
    background: white;
    border-radius: 8px;
    padding: 60px 24px;
    text-align: center;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }
  
  // 卡片样式
  .el-card {
    border-radius: 8px;
    
    :deep(.el-card__header) {
      padding: 20px 24px;
      border-bottom: 1px solid #f0f0f0;
      
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }
      
      .card-title {
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
    }
    
    :deep(.el-card__body) {
      padding: 24px;
    }
  }
  
  // 信息项样式
  .info-item {
    display: flex;
    align-items: center;
    margin-bottom: 16px;
    
    .label {
      font-weight: 500;
      color: #606266;
      min-width: 100px;
      margin-right: 12px;
    }
    
    .value {
      color: #303133;
      font-weight: 400;
      
      &.price {
        font-size: 18px;
        font-weight: 600;
        color: #e6a23c;
      }
    }
  }
  
  // 图片样式
  .image-container {
    display: flex;
    justify-content: center;
    
    .room-image {
      width: 400px;
      height: 300px;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
    
    .image-slot {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 300px;
      color: #909399;
      background-color: #f5f7fa;
      
      .el-icon {
        font-size: 48px;
        margin-bottom: 12px;
      }
    }
  }
  
  // 描述样式
  .description-content {
    line-height: 1.6;
    color: #606266;
    font-size: 14px;
    background-color: #f8f9fa;
    padding: 16px;
    border-radius: 6px;
    border-left: 4px solid #409eff;
  }
  
  // 设施样式
  .facilities-content {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
  
  // 响应式设计
  @media (max-width: 768px) {
    .page-header {
      .header-left {
        justify-content: center;
      }
    }
    
    .info-item {
      flex-direction: column;
      align-items: flex-start;
      
      .label {
        min-width: auto;
        margin-right: 0;
        margin-bottom: 4px;
      }
    }
    
    .image-container .room-image {
      width: 100%;
      max-width: 400px;
    }
  }
}
</style>
