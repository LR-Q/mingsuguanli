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
              controls-position="right"
              style="width: 180px;"
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
      <el-table :data="roomList" style="width: 100%" v-loading="loading" table-layout="fixed">
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
        <el-table-column prop="status" label="状态" min-width="160">
          <template #default="{ row }">
            <el-select
              v-model="row.status"
              size="small"
              class="status-select"
              placeholder="请选择状态"
              :disabled="statusLoading[row.id]"
              :loading="statusLoading[row.id]"
              @change="value => handleStatusChange(row, value)"
            >
              <el-option
                v-for="option in statusOptions"
                :key="option.value"
                :label="option.label"
                :value="option.value"
              >
                <div class="status-option">
                  <el-tag :type="option.color" size="small" effect="plain">
                    {{ option.label }}
                  </el-tag>
                </div>
              </el-option>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column v-if="roleCode === 'SUPER_ADMIN'" label="推荐" width="120">
          <template #default="{ row }">
            <el-switch
              v-model="row.isRecommendedSwitch"
              :active-value="true"
              :inactive-value="false"
              :disabled="recommendLoading[row.id]"
              @change="val => handleRecommendChange(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="260">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button type="warning" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button 
              type="success" 
              size="small" 
              @click="handleCopy(row)"
            >
              复制
            </el-button>
            <el-button 
              type="info" 
              size="small" 
              @click="openReviews(row)"
            >
              评论
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
      
      <el-dialog v-model="reviewDialogVisible" title="房间评论" width="720px">
        <div v-if="currentReviewRoom" class="review-header">
          <el-tag type="primary" effect="plain">房间号：{{ currentReviewRoom.roomNumber }}</el-tag>
          <el-tag type="info" effect="plain" style="margin-left:8px;">房型：{{ currentReviewRoom.roomTypeName }}</el-tag>
          <el-tag type="success" effect="plain" style="margin-left:8px;">民宿：{{ currentReviewRoom.locationName }}</el-tag>
        </div>
        <el-table :data="reviewList" v-loading="reviewLoading" style="width:100%;margin-top:8px;">
          <el-table-column prop="userDisplayName" label="用户" width="160" />
          <el-table-column label="评分" width="120">
            <template #default="{ row }">
              <el-rate v-model="rateTmp[row.id]" :max="5" disabled />
            </template>
          </el-table-column>
          <el-table-column prop="content" label="内容" min-width="260" />
          <el-table-column prop="createTime" label="时间" width="180" />
        </el-table>
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="reviewPage"
            v-model:page-size="reviewPageSize"
            :total="reviewTotal"
            layout="total, prev, pager, next"
            @current-change="loadReviews"
          />
        </div>
        <template #footer>
          <el-button @click="reviewDialogVisible=false">关闭</el-button>
        </template>
      </el-dialog>
      
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
import { getRoomPage, deleteRoom, getRoomTypes, getAdminLocations, updateRoomStatus, updateRoom, getRoomReviews } from '@/api/modules/room'
import { setRoomRecommended } from '@/api/modules/superAdmin'
import { useAuthStore } from '@/stores/modules/auth'

const router = useRouter()
const authStore = useAuthStore()
const roleCode = authStore.userInfo?.roleCode

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
const statusOptions = [
  { value: 1, label: '可用', color: 'success' },
  { value: 2, label: '占用', color: 'warning' },
  { value: 3, label: '维修', color: 'info' },
  { value: 4, label: '清洁', color: 'primary' },
  { value: 5, label: '停用', color: 'danger' }
]
const statusLoading = reactive({})
const statusSnapshot = reactive({})
const recommendLoading = reactive({})
// 评论对话框
const reviewDialogVisible = ref(false)
const currentReviewRoom = ref(null)
const reviewList = ref([])
const reviewLoading = ref(false)
const reviewPage = ref(1)
const reviewPageSize = ref(10)
const reviewTotal = ref(0)
const rateTmp = reactive({})

// 方法
const getRoomTypeName = (roomTypeId) => {
  const roomType = roomTypes.value.find(type => type.id === roomTypeId)
  return roomType ? roomType.typeName : '未知'
}

const getRoomTypeColor = (roomTypeId) => {
  const colors = ['', 'success', 'warning', 'danger']
  return colors[roomTypeId % 4] || ''
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

const handleCopy = (row) => {
  router.push({ path: '/admin/rooms/create', query: { copyFrom: row.id } })
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

const handleStatusChange = async (row, newStatus) => {
  const previousStatus = statusSnapshot[row.id]
  if (newStatus === previousStatus) {
    return
  }
  
  statusLoading[row.id] = true
  try {
    await updateRoomStatus(row.id, newStatus)
    statusSnapshot[row.id] = newStatus
    ElMessage.success('房间状态更新成功')
  } catch (error) {
    row.status = previousStatus
    statusSnapshot[row.id] = previousStatus
    if (error.response?.data?.message) {
      ElMessage.error(error.response.data.message)
    } else {
      ElMessage.error('更新房间状态失败，请重试')
    }
  } finally {
    statusLoading[row.id] = false
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
    const records = response.data?.records ?? []
    roomList.value = records.map(r => ({
      ...r,
      isRecommendedSwitch: r.isRecommended === 1 || r.recommended === 1 || r.is_recommended === 1
    }))
    total.value = response.data?.total ?? 0
    
    // 更新状态快照和加载状态
    Object.keys(statusSnapshot).forEach(key => delete statusSnapshot[key])
    Object.keys(statusLoading).forEach(key => delete statusLoading[key])
    records.forEach(room => {
      statusSnapshot[room.id] = room.status
      statusLoading[room.id] = false
    })
  } catch (error) {
    console.error('获取房间列表失败:', error)
    ElMessage.error('获取房间列表失败')
  } finally {
    loading.value = false
  }
}

const openReviews = (row) => {
  currentReviewRoom.value = row
  reviewDialogVisible.value = true
  reviewPage.value = 1
  loadReviews()
}

const loadReviews = async () => {
  if (!currentReviewRoom.value) return
  reviewLoading.value = true
  try {
    const res = await getRoomReviews(currentReviewRoom.value.id, { current: reviewPage.value, size: reviewPageSize.value })
    const page = res.data || { records: [], total: 0 }
    reviewList.value = (page.records || []).map(r => {
      rateTmp[r.id] = r.rating
      return r
    })
    reviewTotal.value = page.total || 0
  } catch (e) {
    ElMessage.error('加载评论失败')
  } finally {
    reviewLoading.value = false
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
  
  .status-select {
    width: 140px;
  }
  
  .status-option {
    display: flex;
    align-items: center;
    gap: 6px;
  }
  
  .pagination-wrapper {
    margin-top: 20px;
    text-align: center;
  }

  @media (min-width: 1440px) {
    .search-area .search-form {
      gap: 14px 28px;
    }
    :deep(.el-input-number) {
      width: 200px;
    }
  }
}
</style>
const handleRecommendChange = async (row, val) => {
  const prev = !val
  try {
    recommendLoading[row.id] = true
    // 优先尝试管理员更新接口（很多后端已支持），同时兼容命名
    await updateRoom(row.id, { isRecommended: val ? 1 : 0, recommended: val ? 1 : 0 })
    ElMessage.success(val ? '已设为首页推荐' : '已取消首页推荐')
  } catch (e) {
    try {
      await setRoomRecommended(row.id, val ? 1 : 0)
      ElMessage.success(val ? '已设为首页推荐' : '已取消首页推荐')
    } catch (err) {
      row.isRecommendedSwitch = prev
      if (err.response?.data?.message) {
        ElMessage.error(err.response.data.message)
      } else {
        ElMessage.error('设置推荐失败，请检查后端接口')
      }
    }
  } finally {
    recommendLoading[row.id] = false
  }
}
