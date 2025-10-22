<template>
  <div class="favorites-page">
    <!-- 收藏统计 -->
    <el-card class="stats-card" shadow="never">
      <div class="favorites-stats">
        <div class="stat-item">
          <div class="stat-icon">
            <el-icon><Star /></el-icon>
          </div>
          <div class="stat-content">
            <h3>收藏房间</h3>
            <p class="stat-number">{{ favoriteRooms.length }}</p>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 收藏列表 -->
    <el-card class="favorites-list-card" shadow="never">
      <template #header>
        <div class="card-header">
          <h3>我的收藏</h3>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索房间名称"
              style="width: 200px"
              clearable
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
        </div>
      </template>

      <div v-if="filteredRooms.length > 0" class="room-grid">
        <div 
          v-for="room in filteredRooms" 
          :key="room.id" 
          class="room-card"
        >
          <div class="room-image">
            <el-image 
              :src="room.image" 
              fit="cover"
              style="width: 100%; height: 200px"
            >
              <template #error>
                <div class="image-slot">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            
            <div class="room-actions">
              <el-button 
                type="danger" 
                :icon="Delete" 
                circle 
                size="small"
                @click="cancelFavorite(room)"
              />
            </div>
          </div>
          
          <div class="room-info">
            <div class="room-header">
              <h4>{{ room.name }}</h4>
              <el-tag :type="getStatusType(room.status)" size="small">
                {{ getStatusText(room.status) }}
              </el-tag>
            </div>
            <p class="room-type">{{ room.type }}</p>
            <p class="room-description">{{ room.description }}</p>
            
            <div class="room-features">
              <el-tag 
                v-for="feature in room.features" 
                :key="feature" 
                size="small"
                type="info"
              >
                {{ feature }}
              </el-tag>
            </div>
            
            <div class="room-footer">
              <div class="room-price">
                <span class="price">¥{{ room.price }}</span>
                <span class="unit">/晚</span>
              </div>
              
              <div class="room-buttons">
                <el-button size="small" @click="viewRoom(room)">
                  查看详情
                </el-button>
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="bookRoom(room)"
                  :disabled="!isRoomAvailable(room.status)"
                >
                  {{ isRoomAvailable(room.status) ? '立即预订' : '暂不可订' }}
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty 
        v-else 
        description="暂无收藏的房间"
        :image-size="200"
      >
        <el-button type="primary" @click="$router.push('/rooms')">
          去看看房间
        </el-button>
      </el-empty>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Star, Search, Picture, Delete } from '@element-plus/icons-vue'
import { getFavoriteList, removeFavorite } from '@/api/modules/favorite'

const router = useRouter()

// 响应式数据
const searchKeyword = ref('')
const loading = ref(false)
const favoriteRooms = ref([])

// 计算属性
const filteredRooms = computed(() => {
  if (!searchKeyword.value) {
    return favoriteRooms.value
  }
  
  const keyword = searchKeyword.value.toLowerCase()
  return favoriteRooms.value.filter(room => 
    room.name.toLowerCase().includes(keyword) ||
    room.type.toLowerCase().includes(keyword)
  )
})

// 加载收藏列表
const loadFavorites = async () => {
  try {
    loading.value = true
    const response = await getFavoriteList()
    
    if (response.data && Array.isArray(response.data)) {
      favoriteRooms.value = response.data.map(item => {
        // 处理图片
        let imageUrl = ''
        if (item.images) {
          try {
            const images = typeof item.images === 'string' ? JSON.parse(item.images) : item.images
            imageUrl = Array.isArray(images) && images.length > 0 ? images[0] : ''
          } catch (e) {
            console.error('解析图片失败:', e)
          }
        }
        
        // 处理设施
        let facilities = []
        if (item.facilities) {
          try {
            facilities = typeof item.facilities === 'string' ? JSON.parse(item.facilities) : item.facilities
          } catch (e) {
            console.error('解析设施失败:', e)
            facilities = []
          }
        }
        
        return {
          id: item.room_id || item.roomId || item.id,
          name: item.room_type_name || item.roomTypeName || item.room_number || item.roomNumber || '房间',
          type: item.room_type_name || item.roomTypeName || '标准房型',
          description: item.description || '舒适温馨的房间',
          image: imageUrl,
          price: item.current_price || item.currentPrice || item.base_price || item.basePrice || 0,
          features: facilities,
          status: item.status || 1,
          favoriteTime: item.create_time || item.createTime || item.favoriteTime
        }
      })
    }
  } catch (error) {
    console.error('加载收藏列表失败:', error)
    ElMessage.error('加载收藏列表失败')
  } finally {
    loading.value = false
  }
}

// 取消收藏
const cancelFavorite = async (room) => {
  try {
    await ElMessageBox.confirm(`确定要取消收藏"${room.name}"吗？`, '取消收藏', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await removeFavorite(room.id)

    const index = favoriteRooms.value.findIndex(r => r.id === room.id)
    if (index > -1) {
      favoriteRooms.value.splice(index, 1)
    }

    ElMessage.success('已取消收藏')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
      ElMessage.error('取消收藏失败')
    }
  }
}

const viewRoom = (room) => {
  // 跳转到房间详情页
  router.push(`/rooms/${room.id}`)
}

const bookRoom = (room) => {
  // 检查房间状态
  if (room.status !== 1) {
    const statusText = getStatusText(room.status)
    ElMessage.warning(`该房间当前${statusText}，无法预订`)
    return
  }
  
  // 跳转到预订页面
  router.push(`/rooms/${room.id}/book`)
}

// 获取房间状态文本
const getStatusText = (status) => {
  const statusMap = {
    1: '可用',
    2: '已被预订',
    3: '维修中',
    4: '清洁中',
    5: '已停用'
  }
  return statusMap[status] || '未知状态'
}

// 获取房间状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    1: 'success',
    2: 'warning',
    3: 'danger',
    4: 'info',
    5: 'info'
  }
  return typeMap[status] || 'info'
}

// 判断房间是否可预订
const isRoomAvailable = (status) => {
  return status === 1
}

// 页面挂载时加载数据
onMounted(() => {
  loadFavorites()
})
</script>

<style lang="scss" scoped>
.favorites-page {
  .stats-card {
    margin-bottom: 20px;
    
    .favorites-stats {
      .stat-item {
        display: flex;
        align-items: center;
        gap: 16px;
        
        .stat-icon {
          width: 60px;
          height: 60px;
          border-radius: 50%;
          background: linear-gradient(135deg, #f56c6c, #e6a23c);
          display: flex;
          align-items: center;
          justify-content: center;
          color: white;
          font-size: 24px;
        }
        
        .stat-content {
          h3 {
            margin: 0 0 8px 0;
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
  
  .favorites-list-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      h3 {
        margin: 0;
        color: #303133;
        font-size: 18px;
        font-weight: 600;
      }
    }
    
    .room-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
      gap: 20px;
      
      .room-card {
        border: 1px solid #ebeef5;
        border-radius: 8px;
        overflow: hidden;
        transition: all 0.3s;
        
        &:hover {
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          transform: translateY(-2px);
        }
        
        .room-image {
          position: relative;
          
          .image-slot {
            width: 100%;
            height: 200px;
            display: flex;
            align-items: center;
            justify-content: center;
            background: #f5f7fa;
            color: #c0c4cc;
            font-size: 48px;
          }
          
          .room-actions {
            position: absolute;
            top: 12px;
            right: 12px;
          }
        }
        
        .room-info {
          padding: 16px;
          
          .room-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;
            gap: 8px;
          }
          
          h4 {
            margin: 0;
            color: #303133;
            font-size: 16px;
            font-weight: 600;
            flex: 1;
          }
          
          .room-type {
            margin: 0 0 8px 0;
            color: #909399;
            font-size: 14px;
          }
          
          .room-description {
            margin: 0 0 12px 0;
            color: #606266;
            font-size: 14px;
            line-height: 1.5;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }
          
          .room-features {
            margin-bottom: 16px;
            display: flex;
            flex-wrap: wrap;
            gap: 6px;
          }
          
          .room-footer {
            display: flex;
            justify-content: space-between;
            align-items: center;
            
            .room-price {
              .price {
                color: #f56c6c;
                font-size: 20px;
                font-weight: 600;
              }
              
              .unit {
                color: #909399;
                font-size: 14px;
              }
            }
            
            .room-buttons {
              display: flex;
              gap: 8px;
            }
          }
        }
      }
    }
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
  .room-grid {
    grid-template-columns: 1fr !important;
  }
  
  .card-header {
    flex-direction: column !important;
    gap: 16px;
    align-items: stretch !important;
  }
  
  .room-footer {
    flex-direction: column !important;
    gap: 12px;
    align-items: stretch !important;
    
    .room-buttons {
      justify-content: center;
    }
  }
}
</style>
