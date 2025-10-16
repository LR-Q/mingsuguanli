<template>
  <div class="user-rooms">
    <div class="container">
      <div class="page-header">
        <h1>房间预订</h1>
        <p>选择您心仪的房间，享受舒适的住宿体验</p>
      </div>

      <!-- 搜索筛选 -->
      <div class="search-section">
        <el-card>
          <el-form :model="searchForm" inline>
            <el-form-item label="入住日期">
              <el-date-picker
                v-model="searchForm.checkIn"
                type="date"
                placeholder="选择入住日期"
                style="width: 160px;"
                :disabled-date="disabledCheckInDate"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
            <el-form-item label="退房日期">
              <el-date-picker
                v-model="searchForm.checkOut"
                type="date"
                placeholder="选择退房日期"
                style="width: 160px;"
                :disabled-date="disabledCheckOutDate"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
            <el-form-item label="入住人数">
              <el-select 
                v-model="searchForm.guests" 
                placeholder="选择人数（可选）" 
                clearable
                style="width: 160px;"
              >
                <el-option label="1人" :value="1" />
                <el-option label="2人" :value="2" />
                <el-option label="3人" :value="3" />
                <el-option label="4人" :value="4" />
                <el-option label="5人以上" :value="5" />
              </el-select>
            </el-form-item>
            <el-form-item label="房型">
              <el-select 
                v-model="searchForm.roomTypeId" 
                placeholder="选择房型" 
                clearable
                style="width: 180px;"
              >
                <el-option
                  label="所有房型"
                  :value="null"
                />
                <el-option
                  v-for="type in roomTypes"
                  :key="type.id"
                  :label="type.typeName"
                  :value="type.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="searchRooms">搜索</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>

      <!-- 房间列表 -->
      <div class="rooms-section">
        <div v-if="loading && rooms.length === 0" class="loading-container">
          <el-skeleton :rows="3" animated />
        </div>
        
        <div v-else-if="rooms.length === 0" class="empty-container">
          <el-empty description="暂无可用房间">
            <el-button type="primary" @click="loadRooms">重新加载</el-button>
          </el-empty>
        </div>
        
        <div v-else class="rooms-grid">
          <div v-for="room in rooms" :key="room.id" class="room-card">
            <div class="room-image" @mouseenter="room.showControls = true" @mouseleave="room.showControls = false">
              <el-image
                v-if="room.currentImage"
                :src="room.currentImage"
                fit="cover"
                class="room-img"
              >
                <template #error>
                  <div class="image-placeholder">
                    <el-icon size="60"><Picture /></el-icon>
                    <p>房间图片</p>
                  </div>
                </template>
              </el-image>
              <div v-else class="image-placeholder">
                <el-icon size="60"><Picture /></el-icon>
                <p>房间图片</p>
              </div>
              
              <!-- 图片轮播控制 -->
              <div v-if="room.imageList && room.imageList.length > 1" class="image-controls">
                <!-- 左右切换按钮 -->
                <div 
                  v-show="room.showControls" 
                  class="image-nav prev-btn" 
                  @click.stop="prevImage(room)"
                >
                  <el-icon><ArrowLeft /></el-icon>
                </div>
                <div 
                  v-show="room.showControls" 
                  class="image-nav next-btn" 
                  @click.stop="nextImage(room)"
                >
                  <el-icon><ArrowRight /></el-icon>
                </div>
                
                <!-- 图片指示器 -->
                <div class="image-indicators">
                  <span 
                    v-for="(img, index) in room.imageList" 
                    :key="index"
                    class="indicator"
                    :class="{ active: index === room.currentImageIndex }"
                    @click.stop="setCurrentImage(room, index)"
                  ></span>
                </div>
              </div>
            </div>
            <div class="room-content">
              <div class="room-info">
                <h3>{{ room.name }}</h3>
                <p class="room-desc">{{ room.description || '舒适温馨的房间，为您提供优质的住宿体验' }}</p>
                <div class="room-features" v-if="room.facilities && room.facilities.length > 0">
                  <div class="feature-list">
                    <span 
                      v-for="feature in room.facilities.slice(0, 6)" 
                      :key="feature" 
                      class="feature-item"
                    >
                      <el-icon class="feature-icon"><Check /></el-icon>
                      {{ feature }}
                    </span>
                    <span v-if="room.facilities.length > 6" class="more-features">
                      +{{ room.facilities.length - 6 }}项设施
                    </span>
                  </div>
                </div>
                <div class="room-details">
                  <span><el-icon><User /></el-icon> 最多{{ room.maxGuests }}人</span>
                  <span><el-icon><Expand /></el-icon> {{ room.area }}㎡</span>
                  <span>{{ room.bedType || '标准床型' }}</span>
                </div>
              </div>
              <div class="room-actions">
                <div class="price-info">
                  <span class="price">￥{{ room.price }}</span>
                  <span class="unit">/晚</span>
                </div>
                <el-button type="primary" size="large" @click="bookRoom(room)">
                  立即预订
                </el-button>
              </div>
            </div>
          </div>
        </div>

        <!-- 加载更多 -->
        <div class="load-more" v-if="hasMore">
          <el-button @click="loadMore" :loading="loading">加载更多</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Picture, User, Expand, Check, ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import { getAvailableRooms, getUserRoomTypes, searchRooms as searchRoomsAPI } from '@/api/modules/userRoom'

const router = useRouter()
const route = useRoute()

// 响应式数据
const loading = ref(false)
const hasMore = ref(true)
const rooms = ref([])
const roomTypes = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(6)

const searchForm = reactive({
  checkIn: route.query.checkIn || '',
  checkOut: route.query.checkOut || '',
  guests: route.query.guests || null, // 默认不筛选入住人数
  roomTypeId: null
})

// 加载房间列表
const loadRooms = async (isLoadMore = false) => {
  try {
    loading.value = true
    
    const params = {
      current: isLoadMore ? currentPage.value + 1 : 1,
      size: pageSize.value,
      roomTypeId: searchForm.roomTypeId || null
      // 注意：初始加载时不过滤入住人数，让用户看到所有可用房间
    }
    
    const response = await getAvailableRooms(params)
    const data = response.data
    
    if (isLoadMore) {
      // 加载更多时追加数据
      rooms.value.push(...data.records)
      currentPage.value = data.current
    } else {
      // 首次加载或搜索时替换数据
      rooms.value = data.records
      currentPage.value = 1
    }
    
    total.value = data.total
    hasMore.value = data.current < data.pages
    
    // 处理房间数据
    rooms.value.forEach(room => {
      // 处理设施数据
      if (room.facilities && typeof room.facilities === 'string') {
        try {
          room.facilities = JSON.parse(room.facilities)
        } catch (e) {
          room.facilities = []
        }
      }
      
      // 处理图片数据
      if (room.images && typeof room.images === 'string') {
        try {
          const imageArray = JSON.parse(room.images)
          room.imageList = imageArray
          room.currentImageIndex = 0
          room.currentImage = imageArray.length > 0 ? imageArray[0] : null
          room.showControls = false
        } catch (e) {
          room.imageList = []
          room.currentImageIndex = 0
          room.currentImage = null
          room.showControls = false
        }
      } else {
        room.imageList = []
        room.currentImageIndex = 0
        room.currentImage = null
        room.showControls = false
      }
      
      // 添加显示名称
      room.name = room.roomTypeName || `${room.roomNumber}号房间`
      room.price = room.currentPrice || room.basePrice || 0
    })
    
  } catch (error) {
    console.error('加载房间列表失败:', error)
    ElMessage.error('加载房间列表失败')
  } finally {
    loading.value = false
  }
}

// 加载房型列表
const loadRoomTypes = async () => {
  try {
    const response = await getUserRoomTypes()
    roomTypes.value = response.data
  } catch (error) {
    console.error('加载房型列表失败:', error)
  }
}

// 搜索验证
const validateSearchForm = () => {
  // 如果选择了入住日期和退房日期，验证日期逻辑
  if (searchForm.checkIn && searchForm.checkOut) {
    const checkInTime = new Date(searchForm.checkIn).getTime()
    const checkOutTime = new Date(searchForm.checkOut).getTime()
    
    if (checkOutTime <= checkInTime) {
      ElMessage.error('退房日期必须晚于入住日期')
      return false
    }
  }
  
  // 如果只选择了退房日期而没有选择入住日期
  if (!searchForm.checkIn && searchForm.checkOut) {
    ElMessage.error('请先选择入住日期')
    return false
  }
  
  return true
}

// 搜索房间
const searchRooms = async () => {
  // 验证搜索表单
  if (!validateSearchForm()) {
    return
  }
  
  try {
    loading.value = true
    
    const params = {
      current: 1,
      size: pageSize.value,
      checkIn: searchForm.checkIn || null,
      checkOut: searchForm.checkOut || null,
      guests: searchForm.guests || null,
      roomTypeId: searchForm.roomTypeId || null
    }
    
    const response = await searchRoomsAPI(params)
    const data = response.data
    
    rooms.value = data.records
    total.value = data.total
    currentPage.value = 1
    hasMore.value = data.current < data.pages
    
    // 处理房间数据
    rooms.value.forEach(room => {
      // 处理设施数据
      if (room.facilities && typeof room.facilities === 'string') {
        try {
          room.facilities = JSON.parse(room.facilities)
        } catch (e) {
          room.facilities = []
        }
      }
      
      // 处理图片数据
      if (room.images && typeof room.images === 'string') {
        try {
          const imageArray = JSON.parse(room.images)
          room.imageList = imageArray
          room.currentImageIndex = 0
          room.currentImage = imageArray.length > 0 ? imageArray[0] : null
          room.showControls = false
        } catch (e) {
          room.imageList = []
          room.currentImageIndex = 0
          room.currentImage = null
          room.showControls = false
        }
      } else {
        room.imageList = []
        room.currentImageIndex = 0
        room.currentImage = null
        room.showControls = false
      }
      
      room.name = room.roomTypeName || `${room.roomNumber}号房间`
      room.price = room.currentPrice || room.basePrice || 0
    })
    
    if (total.value === 0) {
      ElMessage.info('未找到符合条件的房间')
    } else {
      ElMessage.success(`找到 ${total.value} 间符合条件的房间`)
    }
    
  } catch (error) {
    console.error('搜索房间失败:', error)
    ElMessage.error('搜索房间失败')
  } finally {
    loading.value = false
  }
}

// 预订房间
const bookRoom = (room) => {
  router.push({
    path: `/rooms/${room.id}/book`,
    query: {
      checkIn: searchForm.checkIn,
      checkOut: searchForm.checkOut,
      guests: searchForm.guests
    }
  })
}

// 图片轮播控制函数
const prevImage = (room) => {
  if (room.imageList && room.imageList.length > 1) {
    room.currentImageIndex = room.currentImageIndex > 0 
      ? room.currentImageIndex - 1 
      : room.imageList.length - 1
    room.currentImage = room.imageList[room.currentImageIndex]
  }
}

const nextImage = (room) => {
  if (room.imageList && room.imageList.length > 1) {
    room.currentImageIndex = room.currentImageIndex < room.imageList.length - 1 
      ? room.currentImageIndex + 1 
      : 0
    room.currentImage = room.imageList[room.currentImageIndex]
  }
}

const setCurrentImage = (room, index) => {
  if (room.imageList && room.imageList.length > index) {
    room.currentImageIndex = index
    room.currentImage = room.imageList[index]
  }
}

// 日期禁用逻辑
const disabledCheckInDate = (time) => {
  // 入住日期不能早于今天
  return time.getTime() < Date.now() - 8.64e7
}

const disabledCheckOutDate = (time) => {
  // 如果没有选择入住日期，退房日期不能早于明天
  if (!searchForm.checkIn) {
    return time.getTime() < Date.now()
  }
  // 退房日期必须晚于入住日期
  return time.getTime() <= new Date(searchForm.checkIn).getTime()
}

// 加载更多
const loadMore = () => {
  loadRooms(true)
}

// 监听入住日期变化，自动调整退房日期
watch(() => searchForm.checkIn, (newCheckIn) => {
  if (newCheckIn && searchForm.checkOut) {
    const checkInTime = new Date(newCheckIn).getTime()
    const checkOutTime = new Date(searchForm.checkOut).getTime()
    
    // 如果退房日期不晚于入住日期，清空退房日期
    if (checkOutTime <= checkInTime) {
      searchForm.checkOut = ''
      ElMessage.warning('退房日期必须晚于入住日期，已自动清空退房日期')
    }
  }
})

onMounted(() => {
  // 初始化加载
  loadRoomTypes()
  loadRooms()
})
</script>

<style lang="scss" scoped>
.user-rooms {
  min-height: 100vh;
  background-color: #f5f7fa;
  
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;
  }
  
  .page-header {
    text-align: center;
    margin-bottom: 40px;
    
    h1 {
      font-size: 32px;
      color: #303133;
      margin-bottom: 10px;
    }
    
    p {
      font-size: 16px;
      color: #606266;
    }
  }
  
  .search-section {
    margin-bottom: 30px;
    
    .el-form {
      display: flex;
      flex-wrap: wrap;
      gap: 16px;
      align-items: center;
      
      .el-form-item {
        margin-bottom: 0;
      }
    }
  }
  
  .rooms-section {
    .loading-container {
      padding: 40px;
    }
    
    .empty-container {
      padding: 60px 20px;
      text-align: center;
    }
    
    .rooms-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
      gap: 24px;
      margin-bottom: 40px;
    }
    
    .room-card {
      background: white;
      border-radius: 12px;
      overflow: hidden;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      transition: transform 0.3s ease, box-shadow 0.3s ease;
      
      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
      }
      
      .room-image {
        height: 200px;
        position: relative;
        overflow: hidden;
        
        .room-img {
          width: 100%;
          height: 100%;
        }
        
        .image-placeholder {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          height: 100%;
          background-color: #f5f7fa;
          color: #909399;
          
          p {
            margin-top: 8px;
            font-size: 14px;
          }
        }
        
        .image-controls {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          pointer-events: none;
          
          .image-nav {
            position: absolute;
            top: 50%;
            transform: translateY(-50%);
            width: 32px;
            height: 32px;
            background: rgba(0, 0, 0, 0.6);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            cursor: pointer;
            pointer-events: auto;
            transition: all 0.3s ease;
            z-index: 2;
            
            &:hover {
              background: rgba(0, 0, 0, 0.8);
              transform: translateY(-50%) scale(1.1);
            }
            
            &.prev-btn {
              left: 8px;
            }
            
            &.next-btn {
              right: 8px;
            }
          }
          
          .image-indicators {
            position: absolute;
            bottom: 8px;
            left: 50%;
            transform: translateX(-50%);
            display: flex;
            gap: 6px;
            pointer-events: auto;
            
            .indicator {
              width: 8px;
              height: 8px;
              border-radius: 50%;
              background: rgba(255, 255, 255, 0.5);
              cursor: pointer;
              transition: all 0.3s ease;
              
              &.active {
                background: white;
                transform: scale(1.2);
              }
              
              &:hover {
                background: rgba(255, 255, 255, 0.8);
              }
            }
          }
        }
      }
      
      .room-content {
        padding: 20px;
        display: flex;
        flex-direction: column;
        height: 280px;
        
        .room-info {
          flex: 1;
          
          h3 {
            font-size: 18px;
            font-weight: 600;
            color: #303133;
            margin-bottom: 8px;
          }
          
          .room-desc {
            font-size: 14px;
            color: #606266;
            line-height: 1.5;
            margin-bottom: 12px;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }
          
          .room-features {
            margin-bottom: 12px;
            
            .feature-list {
              display: flex;
              flex-wrap: wrap;
              gap: 6px;
              
              .feature-item {
                display: flex;
                align-items: center;
                gap: 4px;
                padding: 3px 6px;
                background: #f0f9ff;
                border: 1px solid #e0f2fe;
                border-radius: 4px;
                font-size: 12px;
                color: #333;
                
                .feature-icon {
                  font-size: 12px;
                  color: #10b981;
                }
              }
              
              .more-features {
                padding: 3px 6px;
                background: #f3f4f6;
                border: 1px solid #e5e7eb;
                border-radius: 4px;
                font-size: 12px;
                color: #6b7280;
                font-style: italic;
              }
            }
            
            .el-tag {
              margin-right: 6px;
              margin-bottom: 4px;
            }
          }
          
          .room-details {
            display: flex;
            gap: 16px;
            font-size: 14px;
            color: #909399;
            
            span {
              display: flex;
              align-items: center;
              gap: 4px;
            }
          }
        }
        
        .room-actions {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-top: 16px;
          
          .price-info {
            .price {
              font-size: 24px;
              font-weight: 600;
              color: #e6a23c;
            }
            
            .unit {
              font-size: 14px;
              color: #909399;
              margin-left: 4px;
            }
          }
        }
      }
    }
    
    .load-more {
      text-align: center;
      padding: 20px;
    }
  }
  
  // 响应式设计
  @media (max-width: 768px) {
    .container {
      padding: 16px;
    }
    
    .rooms-grid {
      grid-template-columns: 1fr;
      gap: 16px;
    }
    
    .search-section .el-form {
      flex-direction: column;
      align-items: stretch;
      
      .el-form-item {
        width: 100%;
        
        .el-date-picker,
        .el-select {
          width: 100% !important;
        }
      }
    }
  }
}
</style>