<template>
  <div class="home-container">
    <div class="header">
      <h1 class="title">悦鑫乐怡民宿</h1>
      <p class="subtitle">欢迎来到我们的民宿预订系统</p>
    </div>
    
    <!-- 地图和导航面板 -->
    <div class="map-navigation-wrapper">
      <!-- 地图区域 -->
      <div class="map-section">
        <div id="user-home-map" class="map-container"></div>
        
        <!-- 路况控制按钮 -->
        <div class="map-controls">
          <el-button 
            :type="showTraffic ? 'primary' : 'default'"
            size="small"
            @click="toggleTraffic"
          >
            {{ showTraffic ? '关闭路况' : '开启路况' }}
          </el-button>
        </div>
      </div>
      
      <!-- 导航面板 -->
      <div class="navigation-panel">
        <div class="panel-header">
          <h3>路线导航</h3>
        </div>
        
        <!-- 导航方式选择 -->
        <div class="nav-mode-tabs">
          <el-radio-group v-model="navMode" size="small" @change="handleNavModeChange">
            <el-radio-button label="driving">驾车</el-radio-button>
            <el-radio-button label="walking">步行</el-radio-button>
            <el-radio-button label="transit">公交</el-radio-button>
            <el-radio-button label="riding">骑行</el-radio-button>
          </el-radio-group>
        </div>
        
        <!-- 起点 -->
        <div class="route-point">
          <div class="point-label start-label">起点</div>
          <el-autocomplete
            v-model="startAddress"
            :fetch-suggestions="searchLocation"
            placeholder="输入起点地址"
            @select="handleStartSelect"
            style="width: 100%"
            clearable
          >
            <template #prefix>
              <el-icon><Location /></el-icon>
            </template>
            <template #suffix>
              <el-button 
                link 
                type="primary"
                size="small"
                @click="getMyLocation"
                :loading="gettingLocation"
              >
                {{ gettingLocation ? '定位中...' : '我的位置' }}
              </el-button>
            </template>
          </el-autocomplete>
        </div>
        
        <!-- 途经点列表 -->
        <div class="waypoints-section">
          <draggable 
            v-model="waypoints" 
            item-key="id"
            handle=".drag-handle"
            @end="handleWaypointReorder"
          >
            <template #item="{ element, index }">
              <div class="route-point waypoint-item">
                <div class="point-label waypoint-label">
                  <el-icon class="drag-handle" style="cursor: move;">
                    <Rank />
                  </el-icon>
                  途经 {{ index + 1 }}
                </div>
                <div class="waypoint-input-group">
                  <el-autocomplete
                    v-model="element.address"
                    :fetch-suggestions="searchLocation"
                    placeholder="输入途经点地址"
                    @select="(item) => handleWaypointSelect(index, item)"
                    style="flex: 1"
                    clearable
                  >
                    <template #prefix>
                      <el-icon><Guide /></el-icon>
                    </template>
                  </el-autocomplete>
                  <el-button 
                    type="danger" 
                    size="small"
                    :icon="Delete"
                    circle
                    @click="removeWaypoint(index)"
                  />
                </div>
              </div>
            </template>
          </draggable>
          
          <!-- 添加途经点按钮 -->
          <el-button 
            v-if="waypoints.length < 10"
            type="primary" 
            plain
            size="small"
            @click="addWaypoint"
            style="width: 100%; margin-top: 10px"
          >
            <el-icon><Plus /></el-icon>
            添加途经点 ({{ waypoints.length }}/10)
          </el-button>
        </div>
        
        <!-- 终点 -->
        <div class="route-point">
          <div class="point-label end-label">终点</div>
          <el-select
            v-model="selectedDestinationId"
            placeholder="选择民宿位置"
            @change="handleDestinationSelect"
            style="width: 100%"
            clearable
          >
            <el-option
              v-for="location in locationList"
              :key="location.id"
              :label="location.name"
              :value="location.id"
            >
              <div style="display: flex; justify-content: space-between;">
                <span>{{ location.name }}</span>
                <span style="color: #999; font-size: 12px;">{{ location.address }}</span>
              </div>
            </el-option>
          </el-select>
        </div>
        
        <!-- 开始导航按钮 -->
        <el-button 
          type="primary" 
          size="large"
          @click="startNavigation"
          :loading="calculating"
          :disabled="!canNavigate"
          style="width: 100%; margin-top: 20px"
        >
          {{ calculating ? '计算路线中...' : '开始导航' }}
        </el-button>
        
        <!-- 路线信息 -->
        <div v-if="routeInfo" class="route-info">
          <el-divider />
          <div class="info-item">
            <el-icon><Location /></el-icon>
            <span>总距离：{{ routeInfo.distance }}</span>
          </div>
          <div class="info-item">
            <el-icon><Clock /></el-icon>
            <span>预计时间：{{ routeInfo.duration }}</span>
          </div>
          <div v-if="routeInfo.taxiFare" class="info-item">
            <el-icon><Money /></el-icon>
            <span>打车费用：约{{ routeInfo.taxiFare }}元</span>
          </div>
          
          <!-- 清除路线按钮 -->
          <el-button 
            type="warning" 
            plain
            size="small"
            @click="clearRoute"
            style="width: 100%; margin-top: 10px"
          >
            清除路线
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Location, Clock, Money, Plus, Delete, Guide, Rank } from '@element-plus/icons-vue'
import draggable from 'vuedraggable'
import { getActiveLocationList } from '@/api/modules/location'

// 地图相关
let userMap = null
let trafficLayer = null
let currentRoute = null
const locationList = ref([])

// 导航状态
const navMode = ref('driving') // driving, walking, transit, riding
const showTraffic = ref(false)
const calculating = ref(false)
const gettingLocation = ref(false)

// 起点终点
const startAddress = ref('')
const startPoint = ref(null)
const selectedDestinationId = ref(null)
const destinationPoint = ref(null)

// 途经点
const waypoints = ref([])
let waypointIdCounter = 0

// 路线信息
const routeInfo = ref(null)

// 搜索相关
let localSearch = null
let geocoder = null

// 计算属性：是否可以开始导航
const canNavigate = computed(() => {
  return startPoint.value && destinationPoint.value
})

// 获取位置列表
const fetchLocationList = async () => {
  try {
    const res = await getActiveLocationList()
    if (res.data) {
      locationList.value = res.data || []
      
      // 加载地图标记
      setTimeout(() => {
        loadMapMarkers()
      }, 100)
    }
  } catch (error) {
    console.error('获取位置列表失败:', error)
  }
}

// 初始化地图
const initUserMap = async () => {
  // 确保百度地图API已加载
  if (!window.BMapGL) {
    try {
      await loadBaiduMapScript()
    } catch (error) {
      console.error('地图API加载失败:', error)
      return
    }
  }
  
  nextTick(() => {
    if (!window.BMapGL || userMap) {
      return
    }
    
    // 创建地图实例
    userMap = new window.BMapGL.Map('user-home-map')
    
    // 默认中心点
    const defaultPoint = new window.BMapGL.Point(116.404, 39.915)
    userMap.centerAndZoom(defaultPoint, 12)
    userMap.enableScrollWheelZoom(true)
    
    // 添加地图控件
    userMap.addControl(new window.BMapGL.NavigationControl())
    userMap.addControl(new window.BMapGL.ScaleControl())
    
    // 初始化路况图层
    trafficLayer = new window.BMapGL.TrafficLayer()
    
    // 初始化搜索和地理编码器
    localSearch = new window.BMapGL.LocalSearch(userMap)
    geocoder = new window.BMapGL.Geocoder()
    
    // 加载位置标记
    loadMapMarkers()
  })
}

// 加载地图标记
const loadMapMarkers = () => {
  if (!userMap || locationList.value.length === 0) return
  
  // 添加所有位置的标记
  locationList.value.forEach(location => {
    if (location.longitude && location.latitude && location.isActive === 1) {
      const point = new window.BMapGL.Point(location.longitude, location.latitude)
      const marker = new window.BMapGL.Marker(point)
      
      // 添加标记标签
      const label = new window.BMapGL.Label(location.name, {
        position: point,
        offset: new window.BMapGL.Size(10, -20)
      })
      label.setStyle({
        color: '#333',
        fontSize: '14px',
        border: '1px solid #409eff',
        padding: '4px 8px',
        borderRadius: '4px',
        backgroundColor: '#fff',
        fontWeight: '500'
      })
      marker.setLabel(label)
      
      // 添加点击事件显示信息窗口
      marker.addEventListener('click', () => {
        const infoWindow = new window.BMapGL.InfoWindow(
          `<div style="padding: 10px;">
            <h3 style="margin: 0 0 10px 0; color: #409eff; font-size: 16px;">${location.name}</h3>
            <p style="margin: 5px 0; color: #666; font-size: 14px;">📍 ${location.address}</p>
            ${location.contactPhone ? `<p style="margin: 5px 0; color: #666; font-size: 14px;">📞 ${location.contactPhone}</p>` : ''}
            ${location.description ? `<p style="margin: 5px 0; color: #666; font-size: 14px;">${location.description}</p>` : ''}
          </div>`,
          {
            width: 300,
            height: 0,
            title: ''
          }
        )
        userMap.openInfoWindow(infoWindow, point)
      })
      
      userMap.addOverlay(marker)
    }
  })
  
  // 如果有位置，自动调整视野到第一个位置
  if (locationList.value.length > 0) {
    const firstActive = locationList.value.find(loc => loc.isActive === 1 && loc.longitude && loc.latitude)
    if (firstActive) {
      const firstPoint = new window.BMapGL.Point(firstActive.longitude, firstActive.latitude)
      userMap.centerAndZoom(firstPoint, 14)
    }
  }
}

// 动态加载百度地图API
const loadBaiduMapScript = () => {
  if (window.BMapGL) {
    return Promise.resolve()
  }
  
  return new Promise((resolve, reject) => {
    // 设置全局回调函数
    window.onBMapCallback = () => {
      resolve()
      console.log('百度地图API加载成功')
    }
    
    const script = document.createElement('script')
    script.type = 'text/javascript'
    script.src = 'https://api.map.baidu.com/api?v=1.0&type=webgl&ak=bLJae13fqsY9klnNNJxkEk0StqFRZKNK&callback=onBMapCallback'
    script.onerror = () => {
      reject(new Error('百度地图API加载失败'))
    }
    document.head.appendChild(script)
  })
}

// 获取我的位置
const getMyLocation = () => {
  gettingLocation.value = true
  
  if (!navigator.geolocation) {
    ElMessage.error('您的浏览器不支持定位功能')
    gettingLocation.value = false
    return
  }
  
  navigator.geolocation.getCurrentPosition(
    (position) => {
      const point = new window.BMapGL.Point(position.coords.longitude, position.coords.latitude)
      
      // 逆地理编码获取地址
      geocoder.getLocation(point, (result) => {
        if (result) {
          startAddress.value = result.address
          startPoint.value = point
          userMap.centerAndZoom(point, 15)
          
          // 添加起点标记
          const marker = new window.BMapGL.Marker(point)
          marker.setLabel(new window.BMapGL.Label('我的位置', { offset: new window.BMapGL.Size(10, -20) }))
          userMap.addOverlay(marker)
          
          ElMessage.success('定位成功')
        }
        gettingLocation.value = false
      })
    },
    (error) => {
      console.error('定位失败:', error)
      ElMessage.error('定位失败，请手动输入地址')
      gettingLocation.value = false
    }
  )
}

// 搜索地址建议
const searchLocation = (queryString, cb) => {
  if (!queryString) {
    cb([])
    return
  }
  
  const options = {
    onSearchComplete: (results) => {
      if (localSearch.getStatus() === window.BMAP_STATUS_SUCCESS) {
        const suggestions = []
        for (let i = 0; i < results.getCurrentNumPois(); i++) {
          const poi = results.getPoi(i)
          suggestions.push({
            value: poi.address + poi.title,
            title: poi.title,
            address: poi.address,
            point: poi.point
          })
        }
        cb(suggestions)
      } else {
        cb([])
      }
    }
  }
  
  localSearch = new window.BMapGL.LocalSearch(userMap, options)
  localSearch.search(queryString)
}

// 选择起点
const handleStartSelect = (item) => {
  startAddress.value = item.value
  startPoint.value = item.point
  userMap.centerAndZoom(item.point, 15)
}

// 选择终点
const handleDestinationSelect = (locationId) => {
  const location = locationList.value.find(loc => loc.id === locationId)
  if (location) {
    destinationPoint.value = new window.BMapGL.Point(location.longitude, location.latitude)
    userMap.centerAndZoom(destinationPoint.value, 14)
  }
}

// 添加途经点
const addWaypoint = () => {
  if (waypoints.value.length >= 10) {
    ElMessage.warning('最多支持10个途经点')
    return
  }
  
  waypoints.value.push({
    id: ++waypointIdCounter,
    address: '',
    point: null
  })
}

// 移除途经点
const removeWaypoint = (index) => {
  waypoints.value.splice(index, 1)
}

// 选择途经点
const handleWaypointSelect = (index, item) => {
  waypoints.value[index].address = item.value
  waypoints.value[index].point = item.point
}

// 途经点重新排序
const handleWaypointReorder = () => {
  // 拖拽完成后，如果已有路线则重新计算
  if (routeInfo.value) {
    ElMessage.info('途经点已调整，请重新计算路线')
  }
}

// 切换路况显示
const toggleTraffic = () => {
  if (!trafficLayer) return
  
  if (showTraffic.value) {
    userMap.removeOverlay(trafficLayer)
    ElMessage.info('已关闭路况显示')
  } else {
    userMap.addOverlay(trafficLayer)
    ElMessage.success('已开启路况显示')
  }
  
  showTraffic.value = !showTraffic.value
}

// 切换导航方式
const handleNavModeChange = () => {
  if (routeInfo.value) {
    ElMessage.info('导航方式已切换，请重新计算路线')
    clearRoute()
  }
}

// 开始导航
const startNavigation = async () => {
  if (!startPoint.value || !destinationPoint.value) {
    ElMessage.warning('请选择起点和终点')
    return
  }
  
  console.log('开始导航:')
  console.log('- 起点:', startPoint.value)
  console.log('- 终点:', destinationPoint.value)
  console.log('- 导航方式:', navMode.value)
  console.log('- 途经点数量:', waypoints.value.length)
  
  calculating.value = true
  
  try {
    // 清除之前的路线
    if (currentRoute) {
      userMap.removeOverlay(currentRoute)
    }
    
    // 准备途经点数组
    const waypointArray = waypoints.value
      .filter(wp => wp.point)
      .map(wp => wp.point)
    
    console.log('有效途经点数量:', waypointArray.length)
    
    // 根据导航方式选择不同的路线规划
    switch (navMode.value) {
      case 'driving':
        await calculateDrivingRoute(waypointArray)
        break
      case 'walking':
        await calculateWalkingRoute(waypointArray)
        break
      case 'transit':
        await calculateTransitRoute(waypointArray)
        break
      case 'riding':
        await calculateRidingRoute(waypointArray)
        break
    }
  } catch (error) {
    console.error('路线计算失败:', error)
    ElMessage.error('路线计算失败: ' + (error.message || '请重试'))
  } finally {
    calculating.value = false
  }
}

// 驾车路线规划
const calculateDrivingRoute = (waypointArray) => {
  return new Promise((resolve, reject) => {
    const driving = new window.BMapGL.DrivingRoute(userMap, {
      renderOptions: {
        map: userMap,
        autoViewport: true
      },
      onSearchComplete: (results) => {
        try {
          const status = driving.getStatus()
          console.log('驾车路线规划状态:', status)
          
          if (status === window.BMAP_STATUS_SUCCESS) {
            const plan = results.getPlan(0)
            
            if (!plan) {
              console.error('无法获取驾车路线方案')
              ElMessage.error('无法获取驾车路线方案')
              reject(new Error('无法获取驾车路线方案'))
              return
            }
            
            // 安全获取打车费用
            let taxiFare = null
            try {
              if (typeof plan.getTaxiFare === 'function') {
                const fare = plan.getTaxiFare()
                if (fare && !isNaN(fare)) {
                  taxiFare = fare.toFixed(0)
                }
              }
            } catch (e) {
              console.log('无法获取打车费用', e)
            }
            
            routeInfo.value = {
              distance: (plan.getDistance(false) / 1000).toFixed(2) + ' 公里',
              duration: formatDuration(plan.getDuration(false)),
              taxiFare: taxiFare
            }
            
            ElMessage.success('路线规划成功')
            resolve()
          } else {
            const errorMsg = getSearchStatusMessage(status)
            console.error('驾车路线规划失败:', errorMsg)
            ElMessage.error('驾车路线规划失败: ' + errorMsg)
            reject(new Error(errorMsg))
          }
        } catch (error) {
          console.error('驾车路线规划出错:', error)
          ElMessage.error('驾车路线规划出错：' + error.message)
          reject(error)
        }
      }
    })
    
    if (waypointArray.length > 0) {
      driving.search(startPoint.value, destinationPoint.value, {
        waypoints: waypointArray
      })
    } else {
      driving.search(startPoint.value, destinationPoint.value)
    }
  })
}

// 计算两点之间的距离（公里）
const calculateDistance = (point1, point2) => {
  if (!point1 || !point2) return 0
  
  const distance = userMap.getDistance(point1, point2)
  return (distance / 1000).toFixed(2) // 转换为公里
}

// 步行路线规划
const calculateWalkingRoute = (waypointArray) => {
  // 检查距离是否超过20公里
  const distance = calculateDistance(startPoint.value, destinationPoint.value)
  console.log('起点到终点的直线距离:', distance, 'km')
  
  if (parseFloat(distance) > 20) {
    ElMessage.warning({
      message: `路途遥远（${distance}公里），不建议步行，请选择其他出行方式`,
      duration: 5000
    })
    return Promise.reject(new Error('距离超过20公里，不适合步行'))
  }
  
  return new Promise((resolve, reject) => {
    const walking = new window.BMapGL.WalkingRoute(userMap, {
      renderOptions: {
        map: userMap,
        autoViewport: true
      },
      onSearchComplete: (results) => {
        try {
          const status = walking.getStatus()
          console.log('步行路线规划状态:', status)
          
          if (status === window.BMAP_STATUS_SUCCESS) {
            const plan = results.getPlan(0)
            
            if (!plan) {
              console.error('无法获取步行路线方案')
              ElMessage.error('无法获取步行路线方案')
              reject(new Error('无法获取步行路线方案'))
              return
            }
            
            routeInfo.value = {
              distance: (plan.getDistance(false) / 1000).toFixed(2) + ' 公里',
              duration: formatDuration(plan.getDuration(false))
            }
            
            ElMessage.success('步行路线规划成功')
            resolve()
          } else {
            const errorMsg = getSearchStatusMessage(status)
            console.error('步行路线规划失败:', errorMsg)
            ElMessage.error('步行路线规划失败: ' + errorMsg)
            reject(new Error(errorMsg))
          }
        } catch (error) {
          console.error('步行路线规划出错:', error)
          ElMessage.error('步行路线规划出错：' + error.message)
          reject(error)
        }
      }
    })
    
    walking.search(startPoint.value, destinationPoint.value)
  })
}

// 公交路线规划
const calculateTransitRoute = (waypointArray) => {
  return new Promise((resolve, reject) => {
    const transit = new window.BMapGL.TransitRoute(userMap, {
      renderOptions: {
        map: userMap,
        autoViewport: true
      },
      onSearchComplete: (results) => {
        try {
          if (transit.getStatus() === window.BMAP_STATUS_SUCCESS) {
            const plan = results.getPlan(0)
            
            if (!plan) {
              console.error('无法获取公交路线方案')
              ElMessage.error('无法获取公交路线方案')
              reject(new Error('无法获取公交路线方案'))
              return
            }
            
            routeInfo.value = {
              distance: (plan.getDistance(false) / 1000).toFixed(2) + ' 公里',
              duration: formatDuration(plan.getDuration(false))
            }
            
            ElMessage.success('公交路线规划成功')
            resolve()
          } else {
            const errorMsg = getSearchStatusMessage(transit.getStatus())
            console.error('公交路线规划失败:', errorMsg)
            ElMessage.error('公交路线规划失败: ' + errorMsg)
            reject(new Error(errorMsg))
          }
        } catch (error) {
          console.error('公交路线规划出错:', error)
          ElMessage.error('公交路线规划出错：' + error.message)
          reject(error)
        }
      }
    })
    
    transit.search(startPoint.value, destinationPoint.value)
  })
}

// 骑行路线规划
const calculateRidingRoute = (waypointArray) => {
  // 检查距离是否超过50公里
  const distance = calculateDistance(startPoint.value, destinationPoint.value)
  console.log('起点到终点的直线距离:', distance, 'km')
  
  if (parseFloat(distance) > 50) {
    ElMessage.warning({
      message: `路途遥远（${distance}公里），不建议骑行，请选择其他出行方式`,
      duration: 5000
    })
    return Promise.reject(new Error('距离超过50公里，不适合骑行'))
  }
  
  return new Promise((resolve, reject) => {
    const riding = new window.BMapGL.RidingRoute(userMap, {
      renderOptions: {
        map: userMap,
        autoViewport: true
      },
      onSearchComplete: (results) => {
        try {
          if (riding.getStatus() === window.BMAP_STATUS_SUCCESS) {
            const plan = results.getPlan(0)
            
            routeInfo.value = {
              distance: (plan.getDistance(false) / 1000).toFixed(2) + ' 公里',
              duration: formatDuration(plan.getDuration(false))
            }
            
            ElMessage.success('骑行路线规划成功')
            resolve()
          } else {
            ElMessage.error('骑行路线规划失败')
            reject(new Error('骑行路线规划失败'))
          }
        } catch (error) {
          console.error('骑行路线规划出错:', error)
          ElMessage.error('骑行路线规划出错：' + error.message)
          reject(error)
        }
      }
    })
    
    riding.search(startPoint.value, destinationPoint.value)
  })
}

// 获取搜索状态消息
const getSearchStatusMessage = (status) => {
  const statusMap = {
    [window.BMAP_STATUS_SUCCESS]: '成功',
    [window.BMAP_STATUS_CITY_LIST]: '城市列表',
    [window.BMAP_STATUS_UNKNOWN_LOCATION]: '位置不明确',
    [window.BMAP_STATUS_UNKNOWN_ROUTE]: '找不到路线',
    [window.BMAP_STATUS_INVALID_KEY]: 'API密钥无效',
    [window.BMAP_STATUS_INVALID_REQUEST]: '请求无效',
    [window.BMAP_STATUS_PERMISSION_DENIED]: '权限被拒绝',
    [window.BMAP_STATUS_SERVICE_UNAVAILABLE]: '服务不可用',
    [window.BMAP_STATUS_TIMEOUT]: '请求超时'
  }
  return statusMap[status] || `未知错误(${status})`
}

// 格式化时间
const formatDuration = (seconds) => {
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  
  if (hours > 0) {
    return `${hours}小时${minutes}分钟`
  }
  return `${minutes}分钟`
}

// 清除路线
const clearRoute = () => {
  routeInfo.value = null
  
  if (currentRoute) {
    userMap.removeOverlay(currentRoute)
    currentRoute = null
  }
  
  // 清除地图上的路线渲染
  userMap.clearOverlays()
  
  // 重新加载位置标记
  loadMapMarkers()
  
  ElMessage.info('路线已清除')
}

// 页面加载时初始化
onMounted(async () => {
  await loadBaiduMapScript()
  await fetchLocationList()
  setTimeout(() => {
    initUserMap()
  }, 500)
})
</script>

<style lang="scss" scoped>
.home-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 20px;
  
  .header {
    text-align: center;
    margin-bottom: 30px;
    
    .title {
      color: #409eff;
      font-size: 32px;
      margin: 0 0 10px 0;
    }
    
    .subtitle {
      color: #666;
      font-size: 16px;
      margin: 0;
    }
  }
  
  .map-navigation-wrapper {
    max-width: 1400px;
    margin: 0 auto;
    display: flex;
    gap: 20px;
    
    .map-section {
      flex: 1;
      position: relative;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
      overflow: hidden;
      height: 700px;
      
      .map-container {
        width: 100%;
        height: 100%;
      }
      
      .map-controls {
        position: absolute;
        top: 20px;
        right: 20px;
        z-index: 1000;
      }
    }
    
    .navigation-panel {
      width: 400px;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
      padding: 20px;
      height: 700px;
      overflow-y: auto;
      
      .panel-header {
        margin-bottom: 20px;
        
        h3 {
          margin: 0;
          font-size: 18px;
          color: #303133;
        }
      }
      
      .nav-mode-tabs {
        margin-bottom: 20px;
        
        :deep(.el-radio-group) {
          display: flex;
          width: 100%;
          
          .el-radio-button {
            flex: 1;
            
            .el-radio-button__inner {
              width: 100%;
            }
          }
        }
      }
      
      .route-point {
        margin-bottom: 15px;
        
        .point-label {
          font-size: 14px;
          font-weight: 500;
          margin-bottom: 8px;
          display: flex;
          align-items: center;
          gap: 5px;
          
          &.start-label {
            color: #67c23a;
          }
          
          &.end-label {
            color: #f56c6c;
          }
          
          &.waypoint-label {
            color: #409eff;
          }
        }
      }
      
      .waypoints-section {
        margin: 15px 0;
        
        .waypoint-item {
          margin-bottom: 10px;
          
          .waypoint-input-group {
            display: flex;
            gap: 8px;
            align-items: center;
          }
        }
      }
      
      .route-info {
        margin-top: 20px;
        
        .info-item {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 10px;
          font-size: 14px;
          color: #606266;
          
          .el-icon {
            color: #409eff;
          }
        }
      }
    }
  }
}
</style>
