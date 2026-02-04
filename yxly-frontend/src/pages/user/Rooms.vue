<template>
  <div class="user-rooms">
    <div class="container">
      <!-- 顶部大标题移除，仅保留下方蓝色搜索框 -->

      <!-- 搜索筛选（美化版） -->
      <div class="search-section">
        <div class="hero">
          <div class="hero-inner">
            <div class="hero-title">
              <div class="title-left">
                <h2>房间预订</h2>
                <p>选择您的目的地与日期，开始一次舒适的入住</p>
              </div>
              <div class="title-action">
                <el-button class="hero-search-btn" type="primary" @click="searchRooms">
                  <el-icon style="margin-right:8px"><Search /></el-icon>
                  搜索
                </el-button>
              </div>
            </div>
            <div class="search-bar">
              <!-- 目的地/民宿 -->
              <div class="field">
                <div class="label">民宿位置</div>
                <el-select 
                  v-model="searchForm.locationId" 
                  placeholder="全部民宿" 
                  clearable
                  filterable
                  class="w-240"
                >
                  <el-option
                    v-for="location in locations"
                    :key="location.id"
                    :label="location.name"
                    :value="location.id"
                  />
                </el-select>
              </div>

              <!-- 入住/退房（日期范围选择） -->
              <div class="field">
                <div class="label">入住/退房</div>
                <div class="control-row">
                  <el-date-picker
                    v-model="dateRange"
                    type="daterange"
                    range-separator="-"
                    start-placeholder="选择入住日期"
                    end-placeholder="选择退房日期"
                    value-format="YYYY-MM-DD"
                    :disabled-date="disableDateRange"
                    class="w-full"
                  />
                  <span class="nights-inline" v-if="nights > 0">{{ nights }} 晚</span>
                </div>
              </div>

              <!-- 入住人数 -->
              <div class="field">
                <div class="label">入住人数</div>
                <el-select 
                  v-model="searchForm.guests" 
                  placeholder="选择人数（可选）" 
                  clearable
                  class="w-160"
                >
                  <el-option label="1人" :value="1" />
                  <el-option label="2人" :value="2" />
                  <el-option label="3人" :value="3" />
                  <el-option label="4人" :value="4" />
                  <el-option label="5人以上" :value="5" />
                </el-select>
              </div>

              <!-- 房型 -->
              <div class="field">
                <div class="label">房型</div>
                <el-select 
                  v-model="searchForm.roomTypeId" 
                  placeholder="选择房型" 
                  clearable
                  class="w-200"
                >
                  <el-option label="所有房型" :value="null" />
                  <el-option
                    v-for="type in roomTypes"
                    :key="type.id"
                    :label="type.typeName"
                    :value="type.id"
                  />
                </el-select>
              </div>

            </div>
          </div>
        </div>
      </div>
      
      <div class="location-filter-tip" v-if="searchForm.locationId">
        <el-alert type="info" :closable="false" show-icon title="已按民宿过滤">
          <template #description>
            当前显示该民宿的全部房源
            <el-button type="text" @click="clearLocationFilter" style="margin-left:8px">清除筛选</el-button>
          </template>
        </el-alert>
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
          <div v-for="group in groupedRooms" :key="group.key" class="room-card">
            <div class="room-image" @mouseenter="room.showControls = true" @mouseleave="room.showControls = false">
              <el-image
                v-if="group.coverImage"
                :src="group.coverImage"
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
              
              
            </div>
            <div class="room-content">
              <div class="room-info">
                <h3>{{ group.roomTypeName }}</h3>
                <p class="room-location" v-if="group.locationName">
                  <el-icon><Location /></el-icon>
                  {{ group.locationName }}
                </p>
                <p class="room-desc">{{ group.description || '舒适温馨的房间，为您提供优质的住宿体验' }}</p>
                <div class="room-features" v-if="group.facilities && group.facilities.length > 0">
                  <div class="feature-list">
                    <span 
                      v-for="feature in group.facilities.slice(0, 6)" 
                      :key="feature" 
                      class="feature-item"
                    >
                      <el-icon class="feature-icon"><Check /></el-icon>
                      {{ feature }}
                    </span>
                    <span v-if="group.facilities.length > 6" class="more-features">
                      +{{ group.facilities.length - 6 }}项设施
                    </span>
                  </div>
                </div>
                <div class="room-details">
                  <span><el-icon><User /></el-icon> 最多{{ group.maxGuests }}人</span>
                  <span><el-icon><Expand /></el-icon> {{ group.area }}㎡</span>
                  <span>{{ group.bedType || '标准床型' }}</span>
                  <span class="group-count">剩余{{ group.rooms.length }}间</span>
                </div>
              </div>
              <div class="room-actions">
                <div class="price-info">
                  <span class="price">￥{{ group.minPrice }}</span>
                  <span class="unit">/晚</span>
                </div>
                <div class="action-buttons">
                  <el-button type="primary" size="large" @click="bookGroup(group)">立即预订</el-button>
                  <el-button size="large" @click="openSelectRooms(group)">选择具体房间</el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 加载更多 -->
        <div class="load-more" v-if="hasMore">
          <el-button @click="loadMore" :loading="loading">加载更多</el-button>
        </div>
      </div>
      <!-- 选择具体房间弹窗 -->
      <el-dialog v-model="roomSelectVisible" title="选择具体房间" width="600px">
        <div v-if="selectableRooms.length === 0">暂无可选房间</div>
        <div v-else class="select-rooms">
          <div class="select-room-item" v-for="r in selectableRooms" :key="r.id">
            <div class="left">
              <div class="title">房间 {{ r.roomNumber }}</div>
              <div class="meta">最多{{ r.maxGuests }}人 · {{ r.area }}㎡ · {{ r.bedType || '床型' }}</div>
            </div>
            <div class="right">
              <span class="price">￥{{ r.price }}</span>
              <el-button type="primary" size="small" @click="bookSpecificRoom(r)" style="margin-left:8px;">预订</el-button>
            </div>
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Picture, User, Expand, Check, ArrowLeft, ArrowRight, Star, StarFilled, Location, Search } from '@element-plus/icons-vue'
import { getAvailableRooms, getUserRoomTypes, searchRooms as searchRoomsAPI, getUserLocations } from '@/api/modules/userRoom'
import { addFavorite, removeFavorite, checkMultipleFavorites } from '@/api/modules/favorite'

const router = useRouter()
const route = useRoute()

// 响应式数据
const loading = ref(false)
const hasMore = ref(true)
const rooms = ref([])
// 分组后的数据
const groupedRooms = computed(() => {
  const map = new Map()
  rooms.value.forEach(r => {
    const key = `${r.locationId || ''}-${r.roomTypeId || ''}`
    if (!map.has(key)) {
      map.set(key, {
        key,
        locationId: r.locationId,
        locationName: r.locationName,
        roomTypeId: r.roomTypeId,
        roomTypeName: r.roomTypeName || r.name || '房型',
        description: r.description,
        facilities: Array.isArray(r.facilities) ? [...r.facilities] : [],
        area: r.area,
        bedType: r.bedType,
        maxGuests: r.maxGuests,
        minPrice: r.price,
        coverImage: r.currentImage,
        rooms: [r]
      })
    } else {
      const g = map.get(key)
      g.minPrice = Math.min(g.minPrice ?? r.price, r.price)
      // 合并设施（去重）
      const set = new Set([...(g.facilities || []), ...(Array.isArray(r.facilities) ? r.facilities : [])])
      g.facilities = Array.from(set)
      // 覆盖代表图（若无）
      if (!g.coverImage && r.currentImage) g.coverImage = r.currentImage
      g.rooms.push(r)
    }
  })
  return Array.from(map.values())
})
const roomTypes = ref([])
const locations = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(6)

const searchForm = reactive({
  locationId: route.query.locationId ? Number(route.query.locationId) : null,
  checkIn: route.query.checkIn || '',
  checkOut: route.query.checkOut || '',
  guests: route.query.guests || null, // 默认不筛选入住人数
  roomTypeId: null
})

// 日期范围（美化用）
const dateRange = ref(searchForm.checkIn && searchForm.checkOut ? [searchForm.checkIn, searchForm.checkOut] : null)
const nights = ref(0)

// 加载房间列表
const loadRooms = async (isLoadMore = false) => {
  try {
    loading.value = true
    
    const params = {
      current: isLoadMore ? currentPage.value + 1 : 1,
      size: pageSize.value,
      locationId: searchForm.locationId || null,
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
      
      // 初始化收藏状态
      room.isFavorite = false
      room.favoriteLoading = false
    })
    
    // 批量检查收藏状态
    await checkRoomsFavoriteStatus()
    
  } catch (error) {
    console.error('加载房间列表失败:', error)
    ElMessage.error('加载房间列表失败')
  } finally {
    loading.value = false
  }
}

const loadRoomsForLocationAll = async () => {
  try {
    if (!searchForm.locationId) { await loadRooms(); return }
    loading.value = true
    const size = 50
    let current = 1
    let all = []
    while (true) {
      const response = await getAvailableRooms({ current, size, locationId: searchForm.locationId })
      const data = response.data
      all.push(...(data.records || []))
      if (data.current >= data.pages) break
      current = data.current + 1
    }
    rooms.value = all
    total.value = all.length
    currentPage.value = 1
    hasMore.value = false
    rooms.value.forEach(room => {
      if (room.facilities && typeof room.facilities === 'string') {
        try { room.facilities = JSON.parse(room.facilities) } catch { room.facilities = [] }
      }
      if (room.images && typeof room.images === 'string') {
        try {
          const imageArray = JSON.parse(room.images)
          room.imageList = imageArray
          room.currentImageIndex = 0
          room.currentImage = imageArray.length > 0 ? imageArray[0] : null
          room.showControls = false
        } catch {
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
      room.isFavorite = false
      room.favoriteLoading = false
    })
    await checkRoomsFavoriteStatus()
  } catch (error) {
    console.error('加载民宿全部房源失败:', error)
    ElMessage.error('加载民宿房源失败')
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

// 加载民宿位置列表
const loadLocations = async () => {
  try {
    const response = await getUserLocations()
    locations.value = response.data || []
  } catch (error) {
    console.error('加载民宿位置列表失败:', error)
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
  syncDateFromRange()
  if (!validateSearchForm()) {
    return
  }
  
  try {
    loading.value = true
    
    const params = {
      current: 1,
      size: pageSize.value,
      locationId: searchForm.locationId || null,
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
      
      // 初始化收藏状态
      room.isFavorite = false
      room.favoriteLoading = false
    })
    
    // 批量检查收藏状态
    await checkRoomsFavoriteStatus()
    
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

// 预订分组（默认选最低价房间）
const bookGroup = (group) => {
  if (!group.rooms || group.rooms.length === 0) return
  const target = [...group.rooms].sort((a,b) => (a.price||0)-(b.price||0))[0]
  router.push({
    path: `/rooms/${target.id}/book`,
    query: { checkIn: searchForm.checkIn, checkOut: searchForm.checkOut, guests: searchForm.guests }
  })
}

// 选择具体房间弹窗
const roomSelectVisible = ref(false)
const selectableRooms = ref([])
const openSelectRooms = (group) => {
  selectableRooms.value = group.rooms || []
  roomSelectVisible.value = true
}
const bookSpecificRoom = (room) => {
  roomSelectVisible.value = false
  bookGroup({ rooms: [room] })
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

// 范围选择禁用
const disableDateRange = (time) => {
  // 不能选择今天之前
  const min = new Date()
  min.setHours(0,0,0,0)
  return time.getTime() < min.getTime()
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

// 批量检查房间收藏状态
const checkRoomsFavoriteStatus = async () => {
  if (rooms.value.length === 0) return
  
  try {
    const roomIds = rooms.value.map(room => room.id)
    const response = await checkMultipleFavorites(roomIds)
    
    if (response.data && typeof response.data === 'object') {
      // 假设返回的是 { roomId: true/false } 的对象
      rooms.value.forEach(room => {
        room.isFavorite = response.data[room.id] || false
      })
    }
  } catch (error) {
    console.error('检查收藏状态失败:', error)
    // 失败时不影响房间列表显示
  }
}

// 切换收藏状态
const toggleFavorite = async (room) => {
  if (room.favoriteLoading) return
  
  room.favoriteLoading = true
  
  try {
    if (room.isFavorite) {
      // 取消收藏
      await removeFavorite(room.id)
      room.isFavorite = false
      ElMessage.success('已取消收藏')
    } else {
      // 添加收藏
      await addFavorite(room.id)
      room.isFavorite = true
      ElMessage.success('已添加到收藏')
    }
  } catch (error) {
    console.error('操作收藏失败:', error)
    ElMessage.error(error.message || '操作失败，请重试')
  } finally {
    room.favoriteLoading = false
  }
}

onMounted(() => {
  // 初始化加载
  loadLocations()
  loadRoomTypes()
  if (searchForm.locationId) {
    loadRoomsForLocationAll()
  } else {
    loadRooms()
  }
})

// 同步和计算晚数
const syncDateFromRange = () => {
  if (dateRange.value && dateRange.value.length === 2) {
    searchForm.checkIn = dateRange.value[0]
    searchForm.checkOut = dateRange.value[1]
  } else {
    searchForm.checkIn = ''
    searchForm.checkOut = ''
  }
}

watch(dateRange, (val) => {
  syncDateFromRange()
  if (searchForm.checkIn && searchForm.checkOut) {
    const checkInTime = new Date(searchForm.checkIn).getTime()
    const checkOutTime = new Date(searchForm.checkOut).getTime()
    const diff = (checkOutTime - checkInTime) / (1000 * 60 * 60 * 24)
    nights.value = diff > 0 ? diff : 0
  } else {
    nights.value = 0
  }
})

const clearLocationFilter = () => {
  searchForm.locationId = null
  loadRooms()
}
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
  
  .location-filter-tip { margin-bottom: 16px; }
  
  // 顶部大标题样式移除
  
  .search-section {
    margin-bottom: 30px;
    
    .hero {
      background: #ffffff;
      border-radius: 16px;
      padding: 0;
      box-shadow: none;
      color: #333;
    }
    
    .hero-inner {
      background: #ffffff;
      border-radius: 16px;
      padding: 28px 24px 18px;
      box-shadow: none;
    }
    
    .hero-title {
      position: relative;
      display: flex;
      justify-content: center; // 居中标题
      align-items: center;
      margin-bottom: 16px;
      
      .title-left {
        width: 100%;
        text-align: center;
        h2 {
          margin: 0 0 6px 0;
          color: #303133;
          font-size: 30px;
          font-weight: 700;
        }
        p {
          margin: 0;
          color: #7c8fa6;
          font-size: 14px;
        }
      }
      
      .title-action {
        position: absolute;
        right: 0;
        top: 50%;
        transform: translateY(-50%);
        display: flex;
        align-items: center;
      }
      
      .hero-search-btn {
        height: 48px;
        padding: 0 26px;
        border-radius: 10px;
        font-size: 18px;
        line-height: 48px;
        display: inline-flex;
        align-items: center;
        background: linear-gradient(135deg, #1ea0ff 0%, #006aff 100%);
        box-shadow: 0 8px 18px rgba(0, 106, 255, 0.35);
        transition: transform .15s ease, box-shadow .15s ease, background .2s ease;
      }
      .hero-search-btn:hover { transform: translateY(-1px); box-shadow: 0 10px 22px rgba(0,106,255,.45); }
      .hero-search-btn:active { transform: translateY(0); box-shadow: 0 6px 14px rgba(0,106,255,.35); }
    }
    
    .search-bar {
      display: grid;
      grid-template-columns: 220px minmax(420px, 1fr) 160px 180px;
      gap: 16px;
      align-items: center;
      padding-top: 8px;
    }
    
    .field {
      position: relative;
      
      .label {
        font-size: 12px;
        color: #6f8aa5;
        margin-bottom: 6px;
      }
      
      .w-240 { width: 220px; }
      .w-320 { width: 300px; }
      .w-200 { width: 180px; }
      .w-160 { width: 160px; }
      .w-full { width: 100%; }
      
      .control-row { display: flex; align-items: center; gap: 8px; }
      .nights-inline { color: #409eff; white-space: nowrap; }
    }
    
    .actions { display: none; }
    
    /* 统一输入控件视觉风格 */
    :deep(.el-input__wrapper),
    :deep(.el-select__wrapper),
    :deep(.el-date-editor) {
      border-radius: 10px !important;
      box-shadow: 0 2px 8px rgba(0,0,0,0.04);
      border: none !important;
      transition: box-shadow .2s ease, border-color .2s ease, background-color .2s ease;
    }
    :deep(.el-input__wrapper:hover),
    :deep(.el-select__wrapper:hover),
    :deep(.el-date-editor:hover) {
      box-shadow: 0 4px 12px rgba(0,0,0,0.06);
    }
    :deep(.el-input.is-focus .el-input__wrapper),
    :deep(.el-select.is-focus .el-select__wrapper),
    :deep(.is-active.el-date-editor) {
      box-shadow: 0 0 0 3px rgba(30, 136, 255, 0.25);
    }
    /* placeholder 优化 */
    :deep(.el-input__inner::placeholder),
    :deep(.el-select__placeholder) {
      color: #a6b7c8 !important;
      font-weight: 400;
    }
  }
  
  .rooms-section {
    
    .location-filter-tip { margin-bottom: 12px; }
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
          
          .room-location {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 13px;
            color: #409eff;
            margin-bottom: 8px;
            font-weight: 500;
            
            .el-icon {
              font-size: 14px;
            }
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
          .group-count { color: #409eff; }
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
          
          .action-buttons {
            display: flex;
            gap: 8px;
            align-items: center;
          }
        }
      }
    }
    
    .load-more {
      text-align: center;
      padding: 20px;
    }
  }
  
  .select-rooms {
    display: flex; flex-direction: column; gap: 12px;
    .select-room-item {
      display: flex; justify-content: space-between; align-items: center;
      padding: 10px 12px; border: 1px solid #f0f0f0; border-radius: 8px;
      .left { .title { font-weight: 600; } .meta { color:#909399; font-size:12px; margin-top:4px; } }
      .right { .price { color:#e6a23c; font-weight: 600; } }
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
    .search-section .search-bar { grid-template-columns: 1fr; }
  }
}
</style>
