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
              />
            </el-form-item>
            <el-form-item label="退房日期">
              <el-date-picker
                v-model="searchForm.checkOut"
                type="date"
                placeholder="选择退房日期"
              />
            </el-form-item>
            <el-form-item label="入住人数">
              <el-select v-model="searchForm.guests" placeholder="选择人数">
                <el-option label="1人" :value="1" />
                <el-option label="2人" :value="2" />
                <el-option label="3人" :value="3" />
                <el-option label="4人" :value="4" />
                <el-option label="5人以上" :value="5" />
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
        <div class="rooms-grid">
          <div v-for="room in rooms" :key="room.id" class="room-card">
            <div class="room-image">
              <div class="image-placeholder">
                <el-icon size="60"><Picture /></el-icon>
                <p>房间图片</p>
              </div>
            </div>
            <div class="room-content">
              <div class="room-info">
                <h3>{{ room.name }}</h3>
                <p class="room-desc">{{ room.description }}</p>
                <div class="room-features">
                  <el-tag v-for="feature in room.features" :key="feature" size="small">
                    {{ feature }}
                  </el-tag>
                </div>
                <div class="room-details">
                  <span><el-icon><User /></el-icon> 最多{{ room.maxGuests }}人</span>
                  <span><el-icon><Expand /></el-icon> {{ room.area }}㎡</span>
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Picture, User, Expand } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

// 响应式数据
const loading = ref(false)
const hasMore = ref(true)

const searchForm = reactive({
  checkIn: route.query.checkIn || '',
  checkOut: route.query.checkOut || '',
  guests: route.query.guests || 2
})

// 模拟房间数据
const rooms = ref([
  {
    id: 1,
    name: '豪华海景套房',
    description: '180度海景视野，配备独立阳台和按摩浴缸，是您度假的完美选择',
    price: 588,
    maxGuests: 2,
    area: 45,
    features: ['海景', '阳台', '按摩浴缸', '免费WiFi', '免费早餐']
  },
  {
    id: 2,
    name: '温馨家庭房',
    description: '适合家庭入住，配备儿童设施和游戏区，让全家人都能享受愉快时光',
    price: 388,
    maxGuests: 4,
    area: 35,
    features: ['家庭房', '儿童设施', '游戏区', '免费早餐', '停车位']
  },
  {
    id: 3,
    name: '商务标准间',
    description: '商务人士首选，配备办公桌和高速网络，工作休息两不误',
    price: 288,
    maxGuests: 2,
    area: 28,
    features: ['办公桌', '高速网络', '商务中心', '24小时服务']
  },
  {
    id: 4,
    name: '浪漫情侣套房',
    description: '为情侣精心设计，温馨浪漫的装饰，配备圆床和浪漫灯光',
    price: 468,
    maxGuests: 2,
    area: 32,
    features: ['圆床', '浪漫灯光', '香薰', '红酒', '玫瑰花瓣']
  },
  {
    id: 5,
    name: '经济实惠间',
    description: '性价比极高的选择，虽然简约但设施齐全，干净舒适',
    price: 188,
    maxGuests: 2,
    area: 20,
    features: ['免费WiFi', '24小时热水', '空调', '独立卫浴']
  },
  {
    id: 6,
    name: '豪华总统套房',
    description: '顶级奢华体验，配备私人管家服务和专属设施',
    price: 1288,
    maxGuests: 6,
    area: 80,
    features: ['私人管家', '专属设施', '豪华装修', '全景阳台', '私人泳池']
  }
])

// 搜索房间
const searchRooms = () => {
  if (!searchForm.checkIn || !searchForm.checkOut) {
    ElMessage.warning('请选择入住和退房日期')
    return
  }
  
  // 这里可以调用API搜索房间
  ElMessage.success('搜索完成')
}

// 预订房间
const bookRoom = (room) => {
  if (!searchForm.checkIn || !searchForm.checkOut) {
    ElMessage.warning('请先选择入住日期')
    return
  }
  
  router.push({
    path: `/rooms/${room.id}/book`,
    query: {
      checkIn: searchForm.checkIn,
      checkOut: searchForm.checkOut,
      guests: searchForm.guests
    }
  })
}

// 加载更多
const loadMore = () => {
  loading.value = true
  
  // 模拟加载
  setTimeout(() => {
    loading.value = false
    hasMore.value = false
    ElMessage.info('没有更多房间了')
  }, 1000)
}

onMounted(() => {
  // 初始化时如果有查询参数，自动搜索
  if (route.query.checkIn && route.query.checkOut) {
    searchRooms()
  }
})
</script>

<style lang="scss" scoped>
.user-rooms {
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 40px 20px;
  }
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
  
  h1 {
    margin: 0 0 16px 0;
    font-size: 36px;
    color: #333;
  }
  
  p {
    margin: 0;
    font-size: 16px;
    color: #666;
  }
}

.search-section {
  margin-bottom: 40px;
  
  .el-form {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    justify-content: center;
    align-items: end;
  }
}

.rooms-section {
  .rooms-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
    gap: 30px;
    margin-bottom: 40px;
  }
  
  .room-card {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s, box-shadow 0.3s;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
    }
    
    .room-image {
      height: 200px;
      
      .image-placeholder {
        width: 100%;
        height: 100%;
        background: #f5f5f5;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        color: #999;
        
        p {
          margin: 8px 0 0 0;
          font-size: 14px;
        }
      }
    }
    
    .room-content {
      padding: 24px;
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      gap: 20px;
    }
    
    .room-info {
      flex: 1;
      
      h3 {
        margin: 0 0 12px 0;
        font-size: 20px;
        color: #333;
      }
      
      .room-desc {
        margin: 0 0 16px 0;
        color: #666;
        line-height: 1.6;
        font-size: 14px;
      }
      
      .room-features {
        margin-bottom: 16px;
        
        .el-tag {
          margin: 0 8px 8px 0;
        }
      }
      
      .room-details {
        display: flex;
        gap: 16px;
        color: #666;
        font-size: 14px;
        
        span {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }
    
    .room-actions {
      display: flex;
      flex-direction: column;
      align-items: flex-end;
      gap: 16px;
      
      .price-info {
        text-align: right;
        
        .price {
          font-size: 24px;
          font-weight: bold;
          color: #e74c3c;
        }
        
        .unit {
          font-size: 14px;
          color: #666;
        }
      }
    }
  }
}

.load-more {
  text-align: center;
}

// 响应式设计
@media (max-width: 768px) {
  .rooms-grid {
    grid-template-columns: 1fr !important;
  }
  
  .room-content {
    flex-direction: column !important;
    align-items: stretch !important;
    
    .room-actions {
      flex-direction: row !important;
      justify-content: space-between !important;
      align-items: center !important;
    }
  }
  
  .search-section .el-form {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
