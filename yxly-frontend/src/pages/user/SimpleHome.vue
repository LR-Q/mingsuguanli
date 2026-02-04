<template>
  <div class="home-container">
    <div class="header">
      <h1 class="title brand-title">悦鑫乐怡民宿</h1>
      <p class="subtitle">民宿推荐</p>
    </div>

    <section class="geo-section">
      <div class="geo-header">
        <h2>定位并推荐最近民宿</h2>
        <p class="desc">获取您的当前位置，与各民宿位置计算距离，推荐距离最近的民宿</p>
      </div>
      <div class="geo-actions">
        <el-button type="primary" :loading="locating" @click="locateUser">定位我的位置</el-button>
        <span class="status" v-if="userPos">已定位</span>
        <span class="status warn" v-else>尚未定位</span>
        <el-button type="text" v-if="userPos" @click="clearUserLocation">清除定位</el-button>
      </div>
      <div v-if="nearestLocation" class="nearest-card">
        <div class="nearest-info">
          <div class="location-row">
            <span class="label">民宿：</span>
            <el-select v-model="selectedLocationId" placeholder="选择民宿" filterable size="small" class="location-select">
              <el-option
                v-for="l in locationOptions"
                :key="l.id"
                :label="`${l.name}${l.distanceKm!=null ? `（${l.distanceKm.toFixed(2)} km）` : ''}`"
                :value="l.id"
              />
            </el-select>
          </div>
          <p class="distance" v-if="activeDistanceKm !== null">约 {{ activeDistanceKm.toFixed(2) }} km</p>
          <p class="distance warn" v-else>无法计算距离，需先定位</p>
          
          <div class="nearest-actions">
            <el-button type="primary" @click="openNearest">查看该民宿房源</el-button>
          </div>
        </div>
        <div class="nearest-rooms" v-if="nearestRooms.length">
          <div class="mini-card" v-for="r in nearestRooms" :key="r.id">
            <div class="mini-img">
              <el-image v-if="r.currentImage" :src="r.currentImage" fit="cover" />
              <div v-else class="image-placeholder">
                <el-icon size="28"><Picture /></el-icon>
              </div>
            </div>
            <div class="mini-content">
              <div class="title">{{ r.roomTypeName || '房型' }}</div>
              <div class="meta">￥{{ r.price }}/晚 · 最多{{ r.maxGuests || 1 }}人</div>
              <el-button type="primary" size="small" @click="book(r)">预订</el-button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="recommend-section">
      <div class="recommend-header">
        <h2>民宿推荐</h2>
        <p class="desc">展示由平台超级管理员配置的首页推荐房源</p>
      </div>
      <div v-if="loading && rooms.length === 0" class="loading-container">
        <el-skeleton :rows="4" animated />
      </div>
      <div v-else-if="rooms.length === 0" class="empty-container">
        <el-empty description="暂无首页推荐">
          <el-button type="primary" @click="reload">重新加载</el-button>
          <el-button @click="$router.push('/rooms')" style="margin-left:8px">去全部房间</el-button>
        </el-empty>
      </div>
      <div v-else class="rooms-grid">
        <div class="room-card" v-for="room in rooms" :key="room.id">
          <div class="room-image">
            <el-image v-if="room.currentImage" :src="room.currentImage" fit="cover" class="room-img">
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
              <h3>{{ room.roomTypeName || '单人房' }}</h3>
              <p class="room-location" v-if="room.locationName">
                <el-icon><Location /></el-icon>
                {{ room.locationName }}
              </p>
              <p class="room-desc">{{ room.description || '舒适单人间，适合差旅与独行旅行' }}</p>
              <div class="room-details">
                <span><el-icon><User /></el-icon> 最多{{ room.maxGuests || 1 }}人</span>
                <span><el-icon><Expand /></el-icon> {{ room.area || 18 }}㎡</span>
                <span>{{ room.bedType || '单人床' }}</span>
              </div>
            </div>
            <div class="room-actions">
              <div class="price-info">
                <span class="price">￥{{ room.price }}</span>
                <span class="unit">/晚</span>
              </div>
              <div class="action-buttons">
                <el-button type="primary" size="large" @click="book(room)">立即预订</el-button>
                <el-button size="large" @click="view(room)">查看详情</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Picture, User, Expand, Location } from '@element-plus/icons-vue'
import { getAvailableRooms, getUserRoomTypes, getUserLocations } from '@/api/modules/userRoom'

const router = useRouter()
const loading = ref(false)
const rooms = ref([])
const recommendedIds = ref([])
const backendAvailable = ref(true)
const locations = ref([])
const locating = ref(false)
const userPos = ref(null)
const nearestLocation = ref(null)
const nearestRooms = ref([])
const nearestDistanceKm = ref(0)
const selectedLocationId = ref(null)
const activeLocation = computed(() => {
  if (selectedLocationId.value) return getLocationById(selectedLocationId.value)
  return nearestLocation.value
})
const activeDistanceKm = computed(() => {
  if (!activeLocation.value || !userPos.value || typeof activeLocation.value.latitude !== 'number' || typeof activeLocation.value.longitude !== 'number') {
    return null
  }
  return haversine(userPos.value.lat, userPos.value.lng, activeLocation.value.latitude, activeLocation.value.longitude)
})

const locationOptions = computed(() => {
  const list = (locations.value || []).map(l => {
    const distanceKm = userPos.value && typeof l.latitude === 'number' && typeof l.longitude === 'number'
      ? haversine(userPos.value.lat, userPos.value.lng, l.latitude, l.longitude)
      : null
    return { ...l, distanceKm }
  })
  if (userPos.value) {
    list.sort((a,b) => (a.distanceKm ?? Number.POSITIVE_INFINITY) - (b.distanceKm ?? Number.POSITIVE_INFINITY))
  }
  return list
})

const GEO_CACHE_KEY = 'yxly:userGeo'
const GEO_CACHE_TTL_MS = 24 * 60 * 60 * 1000

const reload = () => loadRecommendations()

const findSingleRoomTypeId = async () => {
  try {
    const res = await getUserRoomTypes()
    const list = res.data || []
    const single = list.find(t => (t.typeName || '').includes('单人'))
    return single ? single.id : null
  } catch (e) {
    return null
  }
}

const loadLocations = async () => {
  try {
    const res = await getUserLocations()
    locations.value = res.data || []
  } catch (e) {
    locations.value = []
  }
}

const getLocationById = (id) => locations.value.find(l => l.id === id)

const toRad = (v) => (v * Math.PI) / 180
const haversine = (lat1, lng1, lat2, lng2) => {
  const R = 6371
  const dLat = toRad(lat2 - lat1)
  const dLng = toRad(lng2 - lng1)
  const a = Math.sin(dLat / 2) ** 2 + Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) * Math.sin(dLng / 2) ** 2
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
  return R * c
}

const updateRoomDistances = () => {
  if (!userPos.value || rooms.value.length === 0 || locations.value.length === 0) return
  rooms.value.forEach(r => {
    const loc = getLocationById(r.locationId)
    if (loc && typeof loc.latitude === 'number' && typeof loc.longitude === 'number') {
      r.distanceKm = haversine(userPos.value.lat, userPos.value.lng, loc.latitude, loc.longitude)
    } else {
      r.distanceKm = null
    }
  })
}

const computeNearest = () => {
  if (!userPos.value || locations.value.length === 0) return
  let min = Infinity
  let nearest = null
  locations.value.forEach(l => {
    if (typeof l.latitude === 'number' && typeof l.longitude === 'number') {
      const d = haversine(userPos.value.lat, userPos.value.lng, l.latitude, l.longitude)
      if (d < min) { min = d; nearest = l }
    }
  })
  nearestLocation.value = nearest || null
  nearestDistanceKm.value = nearest ? min : 0
  if (nearest && !selectedLocationId.value) {
    selectedLocationId.value = nearest.id
  }
  updateActiveRooms()
}

const updateActiveRooms = () => {
  if (!activeLocation.value) { nearestRooms.value = []; return }
  nearestRooms.value = rooms.value.filter(r => r.locationId === activeLocation.value.id).slice(0, 3)
}

const locateUser = () => {
  if (!navigator.geolocation) {
    ElMessage.error('当前浏览器不支持定位')
    return
  }
  locating.value = true
  navigator.geolocation.getCurrentPosition(
    (pos) => {
      userPos.value = { lat: pos.coords.latitude, lng: pos.coords.longitude }
      try { localStorage.setItem(GEO_CACHE_KEY, JSON.stringify({ ...userPos.value, ts: Date.now() })) } catch {}
      locating.value = false
      updateRoomDistances()
      computeNearest()
      updateActiveRooms()
      ElMessage.success('定位成功')
    },
    (err) => {
      locating.value = false
      ElMessage.error('定位失败：' + (err.message || '请检查权限设置'))
    },
    { enableHighAccuracy: true, timeout: 10000, maximumAge: 60000 }
  )
}

const tryRestoreUserLocation = () => {
  try {
    const raw = localStorage.getItem(GEO_CACHE_KEY)
    if (!raw) return
    const data = JSON.parse(raw)
    if (data && typeof data.lat === 'number' && typeof data.lng === 'number') {
      if (!data.ts || Date.now() - data.ts < GEO_CACHE_TTL_MS) {
        userPos.value = { lat: data.lat, lng: data.lng }
      } else {
        localStorage.removeItem(GEO_CACHE_KEY)
      }
    }
  } catch {}
}

const clearUserLocation = () => {
  userPos.value = null
  try { localStorage.removeItem(GEO_CACHE_KEY) } catch {}
  nearestLocation.value = null
  nearestRooms.value = []
  nearestDistanceKm.value = 0
  selectedLocationId.value = null
}

const normalizeRoom = (room) => {
  if (room.images && typeof room.images === 'string') {
    try {
      const imageArray = JSON.parse(room.images)
      room.imageList = imageArray
      room.currentImage = imageArray?.[0] || null
    } catch {
      room.imageList = []
      room.currentImage = null
    }
  } else {
    room.imageList = Array.isArray(room.images) ? room.images : []
    room.currentImage = room.images?.[0] || null
  }
  room.price = room.currentPrice || room.basePrice || 0
  return room
}

const safeGetHomeRecommendations = async () => {
  try {
    const resp = await fetch('/api/v1/super-admin/home/recommendations', { method: 'GET' })
    backendAvailable.value = resp.ok
    if (!resp.ok) {
      return []
    }
    const json = await resp.json()
    // 兼容返回结构 { code, data }
    if (Array.isArray(json)) return json
    if (Array.isArray(json?.data)) return json.data
    return []
  } catch {
    backendAvailable.value = false
    return []
  }
}

const loadRecommendations = async () => {
  try {
    loading.value = true
    await loadLocations()
    // 先读取后端配置的推荐ID，若未配置则不展示任何推荐
    recommendedIds.value = await safeGetHomeRecommendations()
    if (!recommendedIds.value || recommendedIds.value.length === 0) {
      rooms.value = []
      return
    }
    // 拉取全部房源并按推荐ID过滤
    let current = 1
    const size = 100
    let all = []
    while (true) {
      const res = await getAvailableRooms({ current, size })
      const data = res.data || { records: [], current, pages: current }
      const batch = (data.records || []).map(normalizeRoom)
      all.push(...batch)
      if (data.current >= data.pages) break
      current = data.current + 1
    }
    let filtered = all.filter(r => recommendedIds.value.includes(r.id))
    filtered.sort((a,b) => {
      const ta = new Date(a.recommendTime || a.updateTime || a.update_time || 0).getTime()
      const tb = new Date(b.recommendTime || b.updateTime || b.update_time || 0).getTime()
      return tb - ta
    })
    rooms.value = filtered
    updateRoomDistances()
    computeNearest()
  } catch (error) {
    ElMessage.error('加载推荐房源失败')
  } finally {
    loading.value = false
  }
}

const book = (room) => {
  router.push({ path: `/rooms/${room.id}/book` })
}
const view = (room) => {
  router.push({ path: `/rooms/${room.id}` })
}

const openNearest = () => {
  const loc = activeLocation.value || nearestLocation.value
  if (!loc) return
  router.push({ path: '/rooms', query: { locationId: loc.id } })
}

onMounted(() => {
  tryRestoreUserLocation()
  loadRecommendations()
})

watch(selectedLocationId, () => {
  updateActiveRooms()
})
</script>

<style lang="scss" scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  padding: 32px 20px;

  .header {
    display: none;
  }
}

.geo-section {
  max-width: 1400px;
  margin: 0 auto 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 32px;
  
  .geo-header { h2 { margin:0 0 8px; font-size:28px; color:#303133; font-weight: 600; } .desc { margin:0; color:#909399; font-size: 16px; } }
  .geo-actions { display:flex; align-items:center; gap:16px; margin-top:16px; .status { color:#606266; font-size: 15px; } .warn { color:#909399; } }
  .nearest-card { margin-top:16px; display:flex; gap:16px; align-items:flex-start; }
  .nearest-info { flex: 1; h3{ margin:0 0 6px; } .distance{ color:#409eff; margin:0 0 8px; } }
  .nearest-info .distance.warn{ color:#999; }
  .nearest-info .location-row{ display:flex; align-items:center; gap:8px; margin-bottom:6px; }
  .nearest-info .location-row .label{ color:#606266; }
  .nearest-info .location-select{ width:240px; flex:0 0 240px; }
  .nearest-info .coords{ color:#909399; font-size:12px; margin-bottom:8px; }
  .nearest-rooms { display:grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap:16px; flex: 2; }
  .mini-card { display:flex; gap:12px; background:#f9fafb; border:1px solid #eef2f7; border-radius:10px; padding:14px; transition: all 0.3s ease; }
  .mini-card:hover { box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1); transform: translateY(-2px); }
  .mini-img { width:120px; height:100px; overflow:hidden; border-radius:8px; background:#f5f7fa; flex-shrink: 0; }
  .mini-img .el-image { width:100%; height:100%; }
  .mini-content { flex:1; .title{ font-weight:600; font-size: 16px; margin-bottom: 6px; } .meta{ color:#909399; font-size:13px; margin:4px 0 10px; } }
}

.recommend-section {
  max-width: 1400px;
  margin: 0 auto;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  padding: 32px;

  .recommend-header {
    h2 { margin: 0 0 10px; font-size: 28px; color: #303133; font-weight: 600; }
    .desc { margin: 0 0 24px; color: #909399; font-size: 16px; }
  }

  .loading-container { padding: 20px; }
  .empty-container { padding: 40px 0; text-align: center; }

  .rooms-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
    gap: 24px;
  }

  .room-card {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;
    
    &:hover {
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
      transform: translateY(-4px);
    }

    .room-image { height: 240px; background:#f5f7fa; }
    .room-img { width:100%; height:100%; }
    .image-placeholder { height:100%; display:flex; align-items:center; justify-content:center; color:#909399; flex-direction:column; }

    .room-content { padding: 20px; display:flex; flex-direction:column; gap:14px; }
    .room-info {
      h3 { margin:0 0 8px; font-size:20px; color:#303133; font-weight: 600; }
      .room-location { display:flex; align-items:center; gap:4px; font-size:14px; color:#409eff; margin-bottom:8px; }
      .room-desc { font-size:15px; color:#606266; line-height: 1.6; margin-bottom: 8px; }
      .room-details { display:flex; gap:16px; font-size:14px; color:#909399; }
    }
    .room-actions { display:flex; justify-content:space-between; align-items:center; }
    .price-info { .price{ color:#e6a23c; font-weight:700; font-size:28px; } .unit{ color:#909399; margin-left:4px; font-size: 16px; } }
    .action-buttons { display:flex; gap:8px; }
  }
}

@media (max-width: 768px) {
  .home-container { padding: 12px; }
  .geo-section { padding: 16px; }
  .geo-header h2 { font-size: 22px; }
  .recommend-section { padding: 16px; }
  .recommend-header h2 { font-size: 24px; }
  .rooms-grid { grid-template-columns: 1fr; gap: 16px; }
  .nearest-rooms { grid-template-columns: 1fr; }
}

@media (min-width: 1600px) {
  .geo-section {
    max-width: 1600px;
  }
  
  .recommend-section {
    max-width: 1600px;
  }
  
  .rooms-grid {
    grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
    gap: 28px;
  }
  
  .room-card {
    .room-image { height: 260px; }
    .room-content { padding: 24px; }
    .room-info h3 { font-size: 22px; }
    .price-info .price { font-size: 30px; }
  }
}
</style>
