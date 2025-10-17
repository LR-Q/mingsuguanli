<template>
  <div class="booking-list">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <h3>订单管理</h3>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            创建订单
          </el-button>
        </div>
      </template>
      
      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :model="searchForm" inline>
          <el-form-item label="订单号">
            <el-input 
              v-model="searchForm.orderNumber" 
              placeholder="请输入订单号"
              clearable
            />
          </el-form-item>
          <el-form-item label="客户姓名">
            <el-input 
              v-model="searchForm.customerName" 
              placeholder="请输入客户姓名"
              clearable
            />
          </el-form-item>
          <el-form-item label="房间号">
            <el-input 
              v-model="searchForm.roomNumber" 
              placeholder="请输入房间号"
              clearable
            />
          </el-form-item>
          <el-form-item label="订单状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="待确认" value="pending" />
              <el-option label="已确认" value="confirmed" />
              <el-option label="已入住" value="checkedin" />
              <el-option label="已退房" value="checkedout" />
              <el-option label="被取消（用户取消）" value="cancelled" />
              <el-option label="已取消（我取消的）" value="cancelledByAdmin" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 订单列表 -->
      <el-table :data="bookingList" style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNumber" label="订单号" width="180" />
        <el-table-column prop="customerName" label="客户姓名" width="120" />
        <el-table-column prop="customerPhone" label="联系电话" width="130" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="checkInDate" label="入住日期" width="120" />
        <el-table-column prop="checkOutDate" label="退房日期" width="120" />
        <el-table-column prop="nights" label="住宿天数" width="100">
          <template #default="{ row }">
            {{ row.nights }}晚
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总金额" width="120">
          <template #default="{ row }">
            ¥{{ row.totalAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusColor(row.status)">
              {{ getStatusName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button 
              v-if="row.status === 'pending'" 
              type="success" 
              size="small" 
              @click="handleConfirm(row)"
            >
              确认订单
            </el-button>
            <el-button 
              v-if="row.status === 'confirmed'" 
              type="success" 
              size="small" 
              @click="handleCheckIn(row)"
            >
              办理入住
            </el-button>
            <el-button 
              v-if="row.status === 'checkedin'" 
              type="warning" 
              size="small" 
              @click="handleCheckOut(row)"
            >
              办理退房
            </el-button>
            <el-button 
              v-if="row.status === 'pending' || row.status === 'confirmed'" 
              type="danger" 
              size="small" 
              @click="handleCancel(row)"
            >
              取消订单
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
import { getBookingList, adminCancelBooking, adminConfirmBooking, checkInBooking, checkOutBooking } from '@/api/modules/booking'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  orderNumber: '',
  customerName: '',
  roomNumber: '',
  status: ''
})

// 订单列表数据
const bookingList = ref([])

// 方法 - 管理员视角
const getStatusName = (status) => {
  const names = {
    pending: '待确认',
    confirmed: '已确认',
    checkedin: '已入住',
    checkedout: '已退房',
    cancelled: '被取消',        // 5=用户取消，管理员看是"被取消"
    cancelledByAdmin: '已取消'   // 6=管理员取消，管理员看是"已取消"
  }
  return names[status] || status
}

const getStatusColor = (status) => {
  const colors = {
    pending: 'warning',
    confirmed: 'success',
    checkedin: 'primary',
    checkedout: 'info',
    cancelled: 'danger',
    cancelledByAdmin: 'danger'
  }
  return colors[status] || ''
}

const handleAdd = () => {
  router.push('/admin/bookings/create')
}

const handleView = (row) => {
  router.push(`/admin/bookings/${row.id}`)
}

const handleConfirm = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要确认订单 ${row.orderNumber} 吗？\n确认后用户即可入住`, 
      '确认订单', 
      {
        confirmButtonText: '确定确认',
        cancelButtonText: '取消',
        type: 'success'
      }
    )
    
    // 调用管理员确认订单API
    await adminConfirmBooking(row.id)
    
    ElMessage.success('订单已确认')
    
    // 重新加载订单列表
    await loadBookingList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认订单失败:', error)
      ElMessage.error(error.message || '确认订单失败')
    }
  }
}

const handleCheckIn = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要为订单 ${row.orderNumber} 办理入住吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 调用办理入住API
    await checkInBooking(row.id)
    ElMessage.success('办理入住成功')
    
    // 刷新列表
    loadBookingList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '办理入住失败')
    }
  }
}

const handleCheckOut = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要为订单 ${row.orderNumber} 办理退房吗？\n退房后订单状态将变为已完成`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 调用办理退房API
    await checkOutBooking(row.id)
    ElMessage.success('办理退房成功，订单已完成')
    
    // 刷新列表
    loadBookingList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '办理退房失败')
    }
  }
}

const handleCancel = async (row) => {
  try {
    const { value: reason } = await ElMessageBox.prompt(
      `确定要取消订单 ${row.orderNumber} 吗？\n取消后将自动退款至用户账户`, 
      '取消订单', 
      {
        confirmButtonText: '确定取消',
        cancelButtonText: '暂不取消',
        inputPlaceholder: '请输入取消原因（可选）',
        inputPattern: /.*/
      }
    )
    
    // 调用管理员取消订单API
    await adminCancelBooking(row.id, reason || '管理员取消')
    
    ElMessage.success('订单已取消，已自动退款')
    
    // 重新加载订单列表
    await loadBookingList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error(error.message || '取消订单失败')
    }
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadBookingList()
}

const handleReset = () => {
  Object.assign(searchForm, {
    orderNumber: '',
    customerName: '',
    roomNumber: '',
    status: ''
  })
  currentPage.value = 1
  loadBookingList()
}

const handleSizeChange = (size) => {
  pageSize.value = size
  loadBookingList()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  loadBookingList()
}

const loadBookingList = async () => {
  loading.value = true
  try {
    // 调用API获取订单列表
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      orderNo: searchForm.orderNumber,
      customerName: searchForm.customerName,
      roomNumber: searchForm.roomNumber,
      status: mapFrontendStatusToBackend(searchForm.status)
    }
    
    console.log('请求参数:', params)
    
    const response = await getBookingList(params)
    console.log('API响应:', response)
    
    // response.data 是后端IPage对象
    const pageData = response.data
    
    if (!pageData) {
      console.warn('响应数据为空')
      bookingList.value = []
      total.value = 0
      return
    }
    
    // 映射后端数据到前端表格字段
    const records = pageData.records || []
    console.log('订单记录数:', records.length)
    
    bookingList.value = records.map(item => ({
      id: item.id,
      orderNumber: item.orderNo,
      customerName: item.contactName || item.customerName || '-',
      customerPhone: item.contactPhone || '-',
      roomNumber: item.roomNumber || '-',
      checkInDate: item.checkInDate,
      checkOutDate: item.checkOutDate,
      nights: item.nights || 0,
      totalAmount: item.totalAmount || 0,
      status: mapBackendStatus(item.bookingStatus),
      createTime: item.createTime
    }))
    
    total.value = pageData.total || 0
    console.log('加载完成，总记录数:', total.value)
  } catch (error) {
    console.error('获取订单列表失败:', error)
    console.error('错误详情:', error.response?.data)
    const errorMsg = error.response?.data?.message || error.message || '获取订单列表失败'
    ElMessage.error(errorMsg)
    bookingList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 映射后端状态到前端状态
const mapBackendStatus = (backendStatus) => {
  const statusMap = {
    1: 'pending',           // 待确认
    2: 'confirmed',         // 已确认
    3: 'checkedin',         // 已入住
    4: 'checkedout',        // 已退房
    5: 'cancelled',         // 已取消（用户）
    6: 'cancelledByAdmin'   // 被取消（管理员）
  }
  return statusMap[backendStatus] || 'pending'
}

// 映射前端状态到后端状态
const mapFrontendStatusToBackend = (frontendStatus) => {
  if (!frontendStatus) return ''
  
  const statusMap = {
    'pending': 1,              // 待确认
    'confirmed': 2,            // 已确认
    'checkedin': 3,            // 已入住
    'checkedout': 4,           // 已退房
    'cancelled': 5,            // 已取消（用户）
    'cancelledByAdmin': 6      // 被取消（管理员）
  }
  return statusMap[frontendStatus] || ''
}

onMounted(() => {
  loadBookingList()
})
</script>

<style lang="scss" scoped>
.booking-list {
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


