<template>
  <div class="room-list">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <h3>房间管理</h3>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加房间
          </el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :model="searchForm" inline>
          <el-form-item label="选择民宿">
            <el-select 
              v-model="searchForm.locationId" 
              placeholder="请选择民宿位置" 
              clearable
              style="width: 200px;"
              @change="handleSearch"
            >
              <el-option 
                v-for="location in locationList" 
                :key="location.id" 
                :label="location.name" 
                :value="location.id"
              >
                <span style="float: left">{{ location.name }}</span>
                <span style="float: right; color: #8492a6; font-size: 13px">
                  {{ location.address }}
                </span>
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="房间号">
            <el-input 
              v-model="searchForm.roomNumber" 
              placeholder="请输入房间号"
              clearable
              style="width: 140px;"
            />
          </el-form-item>
          <el-form-item label="房间类型">
            <el-select 
              v-model="searchForm.roomTypeId" 
              placeholder="请选择房间类型" 
              clearable
              style="width: 160px;"
            >
              <el-option 
                v-for="type in roomTypes" 
                :key="type.id" 
                :label="type.typeName" 
                :value="type.id" 
              />
            </el-select>
          </el-form-item>
          <el-form-item label="楼层">
            <el-input-number 
              v-model="searchForm.floorNumber" 
              placeholder="请输入楼层"
              :min="1"
              :max="50"
              clearable
              style="width: 120px;"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select 
              v-model="searchForm.status" 
              placeholder="请选择状态" 
              clearable
              style="width: 120px;"
            >
              <el-option label="可用" :value="1" />
              <el-option label="占用" :value="2" />
              <el-option label="维修" :value="3" />
              <el-option label="清洁" :value="4" />
              <el-option label="停用" :value="5" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 房间列表 -->
      <el-table :data="roomList" style="width: 100%" v-loading="loading">
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="locationName" label="所属民宿" width="140">
          <template #default="{ row }">
            <el-tag type="primary" effect="plain">
              {{ row.locationName || '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="roomTypeName" label="房间类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getRoomTypeColor(row.roomTypeId)">
              {{ row.roomTypeName || getRoomTypeName(row.roomTypeId) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="floorNumber" label="楼层" width="80" />
        <el-table-column prop="currentPrice" label="价格/晚" width="100">
          <template #default="{ row }">
            ¥{{ row.currentPrice }}
          </template>
        </el-table-column>
        <el-table-column prop="maxGuests" label="容纳人数" width="100" />
        <el-table-column prop="area" label="面积(㎡)" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusColor(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button type="warning" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button 
              type="danger" 
              size="small" 
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getRoomPage, deleteRoom, getRoomTypes, getAdminLocations } from '@/api/modules/room'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  locationId: null,
  roomNumber: '',
  roomTypeId: null,
  status: null,
  floorNumber: null
})

// 房间数据
const roomList = ref([])
const roomTypes = ref([])
const locationList = ref([])

// 方法
const getRoomTypeName = (roomTypeId) => {
  const roomType = roomTypes.value.find(type => type.id === roomTypeId)
  return roomType ? roomType.typeName : '未知'
}

const getRoomTypeColor = (roomTypeId) => {
  const colors = ['', 'success', 'warning', 'danger']
  return colors[roomTypeId % 4] || ''
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

const handleAdd = () => {
  router.push('/admin/rooms/create')
}

const handleView = (row) => {
  router.push(`/admin/rooms/${row.id}`)
}

const handleEdit = (row) => {
  router.push(`/admin/rooms/${row.id}/edit`)
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除房间 ${row.roomNumber} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteRoom(row.id)
    ElMessage.success('删除成功')
    loadRoomList()
  } catch (error) {
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else if (error.message !== 'cancel') {
      ElMessage.error('删除失败，请重试')
    }
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadRoomList()
}

const handleReset = () => {
  Object.assign(searchForm, {
    locationId: null,
    roomNumber: '',
    roomTypeId: null,
    status: null,
    floorNumber: null
  })
  currentPage.value = 1
  loadRoomList()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  loadRoomList()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadRoomList()
}

const loadRoomList = async () => {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      ...searchForm
    }
    
    const response = await getRoomPage(params)
    // 响应拦截器已经处理了成功响应，直接使用data
    roomList.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    console.error('获取房间列表失败:', error)
    ElMessage.error('获取房间列表失败')
  } finally {
    loading.value = false
  }
}

const loadRoomTypes = async () => {
  try {
    const response = await getRoomTypes()
    // 响应拦截器已经处理了成功响应，直接使用data
    roomTypes.value = response.data
  } catch (error) {
    console.error('获取房型列表失败:', error)
  }
}

const loadLocationList = async () => {
  try {
    const response = await getAdminLocations()
    locationList.value = response.data || []
    console.log('民宿位置列表:', locationList.value)
  } catch (error) {
    console.error('获取民宿位置列表失败:', error)
    ElMessage.warning('获取民宿位置列表失败')
  }
}

onMounted(() => {
  loadLocationList()
  loadRoomTypes()
  loadRoomList()
})
</script>

<style lang="scss" scoped>
.room-list {
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
  
  .search-area {
    margin-bottom: 20px;
    padding: 20px;
    background: #f8f9fa;
    border-radius: 4px;
  }
  
  .pagination-wrapper {
    margin-top: 20px;
    text-align: center;
  }
}
</style>
