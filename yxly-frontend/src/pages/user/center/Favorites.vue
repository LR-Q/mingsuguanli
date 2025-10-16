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
                @click="removeFavorite(room)"
              />
            </div>
          </div>
          
          <div class="room-info">
            <h4>{{ room.name }}</h4>
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
                <el-button type="primary" size="small" @click="bookRoom(room)">
                  立即预订
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
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Star, Search, Picture, Delete } from '@element-plus/icons-vue'

const router = useRouter()

// 响应式数据
const searchKeyword = ref('')

// 模拟收藏数据
const favoriteRooms = ref([
  {
    id: 1,
    name: '海景豪华大床房',
    type: '豪华大床房',
    description: '面朝大海，春暖花开。宽敞舒适的海景房，让您尽享海滨度假时光。',
    image: '/api/placeholder/400/300',
    price: 388,
    features: ['海景', '大床', '阳台', 'WiFi', '空调'],
    favoriteTime: '2024-10-15 14:30:00'
  },
  {
    id: 2,
    name: '温馨标准双人间',
    type: '标准双人间',
    description: '温馨舒适的标准双人间，设施齐全，性价比超高。',
    image: '/api/placeholder/400/300',
    price: 288,
    features: ['双床', 'WiFi', '空调', '24小时热水'],
    favoriteTime: '2024-10-14 09:15:00'
  },
  {
    id: 3,
    name: '商务套房',
    type: '商务套房',
    description: '专为商务人士设计的套房，配备办公区域和会客厅。',
    image: '/api/placeholder/400/300',
    price: 588,
    features: ['套房', '办公桌', '会客厅', '商务中心', 'WiFi'],
    favoriteTime: '2024-10-13 16:45:00'
  }
])

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

// 方法
const removeFavorite = async (room) => {
  try {
    await ElMessageBox.confirm(`确定要取消收藏"${room.name}"吗？`, '取消收藏', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    // 这里调用API取消收藏
    await new Promise(resolve => setTimeout(resolve, 500))

    const index = favoriteRooms.value.findIndex(r => r.id === room.id)
    if (index > -1) {
      favoriteRooms.value.splice(index, 1)
    }

    ElMessage.success('已取消收藏')
  } catch (error) {
    // 用户取消操作
  }
}

const viewRoom = (room) => {
  // 跳转到房间详情页
  router.push(`/rooms/${room.id}`)
}

const bookRoom = (room) => {
  // 跳转到预订页面
  router.push(`/rooms/${room.id}/book`)
}
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
          
          h4 {
            margin: 0 0 8px 0;
            color: #303133;
            font-size: 16px;
            font-weight: 600;
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
