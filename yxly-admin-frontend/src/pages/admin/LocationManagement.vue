<template>
  <div class="location-map-management">
    <!-- 顶部工具栏 -->
    <div class="map-toolbar">
      <div class="toolbar-left">
        <h2>位置管理</h2>
        <el-input
          v-model="searchKeyword"
          placeholder="搜索地址定位"
          style="width: 300px; margin-left: 20px;"
          clearable
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #append>
            <el-button @click="handleMapSearch" :icon="Search">搜索</el-button>
          </template>
        </el-input>
      </div>
      <div class="toolbar-right">
        <el-button type="primary" @click="handleAddLocation">
          <el-icon><Plus /></el-icon>
          添加位置
        </el-button>
        <el-button @click="handleRefresh" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
        <el-button @click="drawerVisible = true">
          <el-icon><List /></el-icon>
          位置列表
        </el-button>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 左侧地图 -->
      <div class="map-section">
        <div id="main-map" class="main-map"></div>
        
        <!-- 地图上的提示信息 -->
        <div class="map-tips">
          <el-alert
            title="点击地图选择位置，在右侧表单中编辑信息"
            type="info"
            :closable="false"
          />
        </div>
      </div>

      <!-- 右侧表单面板 -->
      <div class="form-panel">
        <div class="panel-header">
          <h3>{{ isReadOnly ? '查看位置' : (isEdit ? '编辑位置' : '添加位置') }}</h3>
          <el-button-group>
            <el-button size="small" @click="handleClearForm">
              <el-icon><RefreshLeft /></el-icon>
              清空
            </el-button>
            <el-button size="small" @click="drawerVisible = true">
              <el-icon><List /></el-icon>
              列表
            </el-button>
          </el-button-group>
        </div>

        <div class="panel-content">
          <el-form
            ref="formRef"
            :model="formData"
            :rules="formRules"
            label-width="90px"
            label-position="left"
            size="default"
          >
            <el-form-item label="位置名称" prop="name">
              <el-input
                v-model="formData.name"
                placeholder="请输入位置名称"
                :disabled="isReadOnly"
              />
            </el-form-item>
            
            <el-form-item label="详细地址" prop="address">
              <el-input
                v-model="formData.address"
                type="textarea"
                :rows="2"
                placeholder="点击地图自动获取地址"
                :disabled="isReadOnly"
              />
            </el-form-item>
            
            <el-form-item label="省份" prop="province">
              <el-input v-model="formData.province" placeholder="省份" :disabled="isReadOnly" />
            </el-form-item>
            
            <el-form-item label="城市" prop="city">
              <el-input v-model="formData.city" placeholder="城市" :disabled="isReadOnly" />
            </el-form-item>
            
            <el-form-item label="区县" prop="district">
              <el-input v-model="formData.district" placeholder="区县" :disabled="isReadOnly" />
            </el-form-item>
            
            <el-row :gutter="10">
              <el-col :span="12">
                <el-form-item label="经度" prop="longitude">
                  <el-input
                    v-model.number="formData.longitude"
                    placeholder="经度"
                    readonly
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="纬度" prop="latitude">
                  <el-input
                    v-model.number="formData.latitude"
                    placeholder="纬度"
                    readonly
                  />
                </el-form-item>
              </el-col>
            </el-row>
            
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input
                v-model="formData.contactPhone"
                placeholder="请输入联系电话"
                :disabled="isReadOnly"
              />
            </el-form-item>
            
            <el-form-item label="地图类型" prop="mapType">
              <el-radio-group v-model="formData.mapType" :disabled="isReadOnly">
                <el-radio label="baidu">百度</el-radio>
                <el-radio label="amap">高德</el-radio>
                <el-radio label="google">谷歌</el-radio>
              </el-radio-group>
            </el-form-item>
            
            <el-form-item label="位置描述" prop="description">
              <el-input
                v-model="formData.description"
                type="textarea"
                :rows="3"
                placeholder="请输入位置描述"
                :disabled="isReadOnly"
              />
            </el-form-item>
            
            <el-form-item label="是否启用" prop="isActive">
              <el-switch
                v-model="formData.isActive"
                :active-value="1"
                :inactive-value="0"
                active-text="启用"
                inactive-text="禁用"
                :disabled="isReadOnly"
              />
            </el-form-item>
            
            <el-form-item v-if="!isReadOnly">
              <el-button
                type="primary"
                @click="handleSubmit"
                :loading="submitLoading"
                style="width: 100%;"
              >
                {{ isEdit ? '更新位置' : '保存位置' }}
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>

    <!-- 右侧抽屉 - 位置列表 -->
    <el-drawer
      v-model="drawerVisible"
      title="位置列表"
      direction="rtl"
      size="400px"
    >
      <div class="location-list">
        <el-empty v-if="locationList.length === 0" description="暂无位置数据" />
        <div v-else class="location-cards">
          <el-card
            v-for="location in locationList"
            :key="location.id"
            class="location-card"
            :class="{ 'active': selectedLocation?.id === location.id }"
            @click="handleLocationClick(location)"
          >
            <template #header>
              <div class="card-header">
                <span class="location-name">{{ location.name }}</span>
                <el-tag
                  :type="location.isActive === 1 ? 'success' : 'info'"
                  size="small"
                >
                  {{ location.isActive === 1 ? '启用' : '禁用' }}
                </el-tag>
              </div>
            </template>
            <div class="card-content">
              <p><el-icon><Location /></el-icon> {{ location.address }}</p>
              <p><el-icon><Phone /></el-icon> {{ location.contactPhone || '-' }}</p>
              <p><el-icon><Position /></el-icon> {{ location.longitude }}, {{ location.latitude }}</p>
            </div>
            <template #footer>
              <div class="card-actions">
                <!-- 查看按钮（所有人都可以查看） -->
                <el-button size="small" @click.stop="handleEdit(location)" v-if="!canEdit(location)">
                  查看
                </el-button>
                
                <!-- 编辑按钮（仅创建者和超级管理员可见） -->
                <el-button size="small" type="primary" @click.stop="handleEdit(location)" v-if="canEdit(location)">
                  编辑
                </el-button>
                <el-button 
                  v-if="canEdit(location)"
                  size="small" 
                  :type="location.isActive === 1 ? 'warning' : 'success'"
                  @click.stop="handleToggleStatus(location)"
                >
                  {{ location.isActive === 1 ? '禁用' : '启用' }}
                </el-button>
                <el-button 
                  v-if="canEdit(location)"
                  size="small" 
                  type="danger" 
                  @click.stop="handleDelete(location)"
                >
                  删除
                </el-button>
              </div>
            </template>
          </el-card>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, nextTick, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Refresh, RefreshLeft, List, Location, Phone, Position } from '@element-plus/icons-vue'
import {
  getLocationPage,
  createLocation,
  updateLocation,
  deleteLocation,
  toggleLocationStatus
} from '@/api/modules/location'
import { useAuthStore } from '@/stores/modules/auth'

// 获取用户信息
const authStore = useAuthStore()
const currentUser = computed(() => authStore.userInfo)

// 权限判断：是否可以编辑某个位置
const canEdit = (location) => {
  if (!currentUser.value) return false
  
  // 超级管理员可以编辑所有位置
  if (currentUser.value.roleCode === 'SUPER_ADMIN') {
    return true
  }
  
  // 民宿主只能编辑自己创建的位置
  return location.merchantId === currentUser.value.merchantId
}

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const locationList = ref([])
const formRef = ref(null)
const isEdit = ref(false)
const editId = ref(null)
const isReadOnly = ref(false) // 是否只读模式（查看别人的位置）
const searchKeyword = ref('')
const drawerVisible = ref(false)
const selectedLocation = ref(null)
const currentMarker = ref(null) // 当前编辑的标记

// 地图相关
let mainMap = null
let mainMarkers = []
let tempMarker = null // 临时标记（用于显示当前选择的位置）
let geocoder = null

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 表单数据
const formData = reactive({
  name: '',
  address: '',
  province: '',
  city: '',
  district: '',
  longitude: null,
  latitude: null,
  contactPhone: '',
  description: '',
  mapType: 'baidu',
  isActive: 1
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入位置名称', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ],
  longitude: [
    { required: true, message: '请输入经度', trigger: 'blur' },
    { type: 'number', message: '经度必须为数字', trigger: 'blur' }
  ],
  latitude: [
    { required: true, message: '请输入纬度', trigger: 'blur' },
    { type: 'number', message: '纬度必须为数字', trigger: 'blur' }
  ]
}

// 获取位置列表
const fetchLocationList = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size
    }
    const res = await getLocationPage(params)
    // request.js拦截器返回的是 { data, message }，没有code字段
    if (res.data) {
      locationList.value = res.data.records || []
      pagination.total = res.data.total || 0
      
      // 重新加载地图标记
      setTimeout(() => {
        loadAllMarkers()
      }, 100)
    }
  } catch (error) {
    console.error('获取位置列表失败:', error)
    ElMessage.error('获取位置列表失败')
  } finally {
    loading.value = false
  }
}

// 获取地图类型名称
const getMapTypeName = (type) => {
  const typeMap = {
    'baidu': '百度',
    'amap': '高德',
    'google': '谷歌'
  }
  return typeMap[type] || type
}

// 获取地图类型颜色
const getMapTypeColor = (type) => {
  const colorMap = {
    'baidu': 'primary',
    'amap': 'success',
    'google': 'warning'
  }
  return colorMap[type] || ''
}

// 重置表单
const resetForm = () => {
  formData.name = ''
  formData.address = ''
  formData.province = ''
  formData.city = ''
  formData.district = ''
  formData.longitude = null
  formData.latitude = null
  formData.contactPhone = ''
  formData.description = ''
  formData.mapType = 'baidu'
  formData.isActive = 1
  isReadOnly.value = false // 重置只读状态
}

// 编辑位置
const handleEdit = (row) => {
  resetForm()
  isEdit.value = true
  editId.value = row.id
  
  // 判断是否有编辑权限
  isReadOnly.value = !canEdit(row)
  
  // 填充表单数据
  Object.keys(formData).forEach(key => {
    if (row[key] !== undefined && row[key] !== null) {
      formData[key] = row[key]
    }
  })
  
  // 在地图上显示该位置
  if (mainMap && row.longitude && row.latitude) {
    const point = new window.BMapGL.Point(row.longitude, row.latitude)
    
    // 移除临时标记
    if (tempMarker) {
      mainMap.removeOverlay(tempMarker)
    }
    
    // 添加新的临时标记
    tempMarker = new window.BMapGL.Marker(point)
    mainMap.addOverlay(tempMarker)
    mainMap.centerAndZoom(point, 16)
  }
  
  // 关闭抽屉
  drawerVisible.value = false
  
  if (isReadOnly.value) {
    ElMessage.info('您只能查看此位置信息，无权编辑')
  } else {
    ElMessage.info('可以在右侧编辑位置信息')
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      if (isEdit.value) {
        await updateLocation(editId.value, formData)
        ElMessage.success('更新成功')
      } else {
        await createLocation(formData)
        ElMessage.success('添加成功')
      }
      
      // 清空表单和标记
      handleClearForm()
      
      // 刷新列表
      fetchLocationList()
    } catch (error) {
      ElMessage.error(error.message || '操作失败')
    } finally {
      submitLoading.value = false
    }
  })
}

// 切换启用状态
const handleToggleStatus = async (row) => {
  const newStatus = row.isActive === 1 ? 0 : 1
  const statusText = newStatus === 1 ? '启用' : '禁用'
  
  try {
    await ElMessageBox.confirm(
      `确定要${statusText}该位置吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await toggleLocationStatus(row.id, newStatus)
    ElMessage.success(`${statusText}成功`)
    fetchLocationList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

// 删除位置
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除位置 "${row.name}" 吗？此操作不可恢复！`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'error'
      }
    )
    
    await deleteLocation(row.id)
    ElMessage.success('删除成功')
    
    // 如果当前页没有数据且不是第一页，则返回上一页
    if (locationList.value.length === 1 && pagination.current > 1) {
      pagination.current--
    }
    
    fetchLocationList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 刷新列表
const handleRefresh = () => {
  fetchLocationList()
}

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  fetchLocationList()
}

// 当前页改变
const handleCurrentChange = (current) => {
  pagination.current = current
  fetchLocationList()
}

// 初始化主地图
const initMainMap = async () => {
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
    if (!window.BMapGL || mainMap) {
      return
    }
    
    // 创建主地图实例
    mainMap = new window.BMapGL.Map('main-map')
    
    // 默认中心点（中国中心或第一个位置）
    const defaultPoint = new window.BMapGL.Point(116.404, 39.915)
    mainMap.centerAndZoom(defaultPoint, 12)
    mainMap.enableScrollWheelZoom(true)
    
    // 添加地图控件
    mainMap.addControl(new window.BMapGL.NavigationControl())
    mainMap.addControl(new window.BMapGL.ScaleControl())
    
    // 创建地理编码器实例
    if (!geocoder) {
      geocoder = new window.BMapGL.Geocoder()
    }
    
    // 监听主地图点击事件 - 直接添加新位置
    mainMap.addEventListener('click', (e) => {
      const clickPoint = e.latlng
      handleMapClick(clickPoint)
    })
    
    // 加载所有位置标记
    loadAllMarkers()
  })
}

// 加载所有位置标记到主地图
const loadAllMarkers = () => {
  if (!mainMap) return
  
  // 清除现有标记
  mainMarkers.forEach(marker => mainMap.removeOverlay(marker))
  mainMarkers = []
  
  // 添加所有位置的标记
  locationList.value.forEach(location => {
    if (location.longitude && location.latitude) {
      const point = new window.BMapGL.Point(location.longitude, location.latitude)
      const marker = new window.BMapGL.Marker(point)
      
      // 添加标记标签
      const label = new window.BMapGL.Label(location.name, {
        position: point,
        offset: new window.BMapGL.Size(10, -20)
      })
      label.setStyle({
        color: '#333',
        fontSize: '12px',
        border: '1px solid #409eff',
        padding: '2px 5px',
        borderRadius: '3px',
        backgroundColor: '#fff'
      })
      marker.setLabel(label)
      
      // 添加点击事件
      marker.addEventListener('click', () => {
        selectedLocation.value = location
        handleLocationClick(location)
      })
      
      mainMap.addOverlay(marker)
      mainMarkers.push(marker)
    }
  })
  
  // 如果有位置，自动调整视野
  if (locationList.value.length > 0 && locationList.value[0].longitude) {
    const firstPoint = new window.BMapGL.Point(
      locationList.value[0].longitude,
      locationList.value[0].latitude
    )
    mainMap.centerAndZoom(firstPoint, 14)
  }
}

// 处理主地图点击
const handleMapClick = (clickPoint) => {
  // 添加或更新临时标记
  if (tempMarker) {
    mainMap.removeOverlay(tempMarker)
  }
  
  tempMarker = new window.BMapGL.Marker(clickPoint)
  tempMarker.setAnimation(window.BMAP_ANIMATION_BOUNCE) // 添加动画
  setTimeout(() => {
    tempMarker.setAnimation(null)
  }, 1000)
  mainMap.addOverlay(tempMarker)
  mainMap.panTo(clickPoint)
  
  // 获取地址信息并填充表单
  geocoder.getLocation(clickPoint, (result) => {
    if (result) {
      // 清空编辑状态，切换为添加模式
      isEdit.value = false
      editId.value = null
      
      // 填充表单数据
      formData.longitude = clickPoint.lng
      formData.latitude = clickPoint.lat
      formData.address = result.address
      formData.province = result.addressComponents.province
      formData.city = result.addressComponents.city
      formData.district = result.addressComponents.district
      
      ElMessage.success('位置已选择，请在右侧填写信息')
    }
  })
}

// 清空表单
const handleClearForm = () => {
  resetForm()
  isEdit.value = false
  editId.value = null
  
  // 移除临时标记
  if (tempMarker) {
    mainMap.removeOverlay(tempMarker)
    tempMarker = null
  }
  
  ElMessage.info('表单已清空')
}

// 处理位置卡片点击
const handleLocationClick = (location) => {
  selectedLocation.value = location
  
  if (mainMap && location.longitude && location.latitude) {
    const point = new window.BMapGL.Point(location.longitude, location.latitude)
    mainMap.centerAndZoom(point, 16)
    mainMap.panTo(point)
  }
}

// 主地图搜索
const handleMapSearch = () => {
  if (!searchKeyword.value) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  
  if (!mainMap) {
    ElMessage.error('地图未初始化')
    return
  }
  
  const localSearch = new window.BMapGL.LocalSearch(mainMap, {
    onSearchComplete: (results) => {
      if (localSearch.getStatus() === window.BMAP_STATUS_SUCCESS) {
        const firstResult = results.getPoi(0)
        if (firstResult) {
          const point = firstResult.point
          mainMap.centerAndZoom(point, 16)
          ElMessage.success('定位成功')
        } else {
          ElMessage.warning('未找到相关地址')
        }
      } else {
        ElMessage.error('搜索失败，请重试')
      }
    }
  })
  
  localSearch.search(searchKeyword.value)
}

// 添加位置按钮点击
const handleAddLocation = () => {
  resetForm()
  isEdit.value = false
  editId.value = null
  
  // 移除临时标记
  if (tempMarker) {
    mainMap.removeOverlay(tempMarker)
    tempMarker = null
  }
  
  ElMessage.info('请在地图上点击选择位置')
}

// 页面加载时获取列表并初始化主地图
onMounted(async () => {
  await loadBaiduMapScript()
  await fetchLocationList()
  setTimeout(() => {
    initMainMap()
  }, 500)
})

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
      ElMessage.error('百度地图API加载失败，请检查：\n1. 网络连接\n2. AK配置\n3. Referer白名单')
    }
    document.head.appendChild(script)
  })
}
</script>

<style lang="scss" scoped>
.location-map-management {
  height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
  
  // 顶部工具栏
  .map-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    background: white;
    border-radius: 4px;
    margin-bottom: 16px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    
    .toolbar-left {
      display: flex;
      align-items: center;
      
      h2 {
        margin: 0;
        font-size: 20px;
        font-weight: 600;
        color: #303133;
      }
    }
    
    .toolbar-right {
      display: flex;
      gap: 10px;
    }
  }
  
  // 主内容区域（地图 + 表单）
  .main-content {
    flex: 1;
    display: flex;
    gap: 16px;
    overflow: hidden;
  }
  
  // 左侧地图区域
  .map-section {
    flex: 1;
    position: relative;
    background: white;
    border-radius: 4px;
    overflow: hidden;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    
    .main-map {
      width: 100%;
      height: 100%;
    }
    
    // 地图提示信息
    .map-tips {
      position: absolute;
      top: 20px;
      left: 50%;
      transform: translateX(-50%);
      z-index: 1000;
      max-width: 500px;
    }
  }
  
  // 右侧表单面板
  .form-panel {
    width: 380px;
    background: white;
    border-radius: 4px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
    
    .panel-header {
      padding: 16px;
      border-bottom: 1px solid #ebeef5;
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      h3 {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
        color: #303133;
      }
    }
    
    .panel-content {
      flex: 1;
      padding: 20px;
      overflow-y: auto;
      
      :deep(.el-form-item) {
        margin-bottom: 18px;
      }
      
      :deep(.el-form-item__label) {
        font-size: 13px;
        color: #606266;
      }
    }
  }
  
  // 位置列表卡片
  .location-list {
    .location-cards {
      display: flex;
      flex-direction: column;
      gap: 12px;
    }
    
    .location-card {
      cursor: pointer;
      transition: all 0.3s;
      
      &:hover {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        transform: translateY(-2px);
      }
      
      &.active {
        border-color: #409eff;
        box-shadow: 0 0 10px rgba(64, 158, 255, 0.3);
      }
      
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .location-name {
          font-weight: 600;
          font-size: 16px;
          color: #303133;
        }
      }
      
      .card-content {
        p {
          margin: 8px 0;
          display: flex;
          align-items: center;
          gap: 8px;
          color: #606266;
          font-size: 14px;
          
          .el-icon {
            color: #409eff;
          }
        }
      }
      
      .card-actions {
        display: flex;
        gap: 8px;
        justify-content: flex-end;
      }
    }
  }
  
  :deep(.el-drawer__body) {
    padding: 20px;
  }
}
</style>
