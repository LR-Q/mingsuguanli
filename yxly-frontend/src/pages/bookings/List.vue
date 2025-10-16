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
              <el-option label="已取消" value="cancelled" />
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
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">
              查看
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
              v-if="row.status === 'pending'" 
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

// 模拟订单数据
const bookingList = ref([
  {
    id: 1,
    orderNumber: 'ORD202410150001',
    customerName: '张三',
    customerPhone: '13800138001',
    roomNumber: '101',
    checkInDate: '2024-10-20',
    checkOutDate: '2024-10-22',
    nights: 2,
    totalAmount: 576,
    status: 'confirmed',
    createTime: '2024-10-15 10:30:00'
  },
  {
    id: 2,
    orderNumber: 'ORD202410150002',
    customerName: '李四',
    customerPhone: '13800138002',
    roomNumber: '102',
    checkInDate: '2024-10-18',
    checkOutDate: '2024-10-20',
    nights: 2,
    totalAmount: 716,
    status: 'checkedin',
    createTime: '2024-10-14 15:20:00'
  },
  {
    id: 3,
    orderNumber: 'ORD202410150003',
    customerName: '王五',
    customerPhone: '13800138003',
    roomNumber: '201',
    checkInDate: '2024-10-25',
    checkOutDate: '2024-10-27',
    nights: 2,
    totalAmount: 1176,
    status: 'pending',
    createTime: '2024-10-15 09:15:00'
  },
  {
    id: 4,
    orderNumber: 'ORD202410150004',
    customerName: '赵六',
    customerPhone: '13800138004',
    roomNumber: '202',
    checkInDate: '2024-10-12',
    checkOutDate: '2024-10-14',
    nights: 2,
    totalAmount: 656,
    status: 'checkedout',
    createTime: '2024-10-10 14:45:00'
  }
])

// 方法
const getStatusName = (status) => {
  const names = {
    pending: '待确认',
    confirmed: '已确认',
    checkedin: '已入住',
    checkedout: '已退房',
    cancelled: '已取消'
  }
  return names[status] || status
}

const getStatusColor = (status) => {
  const colors = {
    pending: 'warning',
    confirmed: 'success',
    checkedin: 'primary',
    checkedout: 'info',
    cancelled: 'danger'
  }
  return colors[status] || ''
}

const handleAdd = () => {
  router.push('/admin/bookings/create')
}

const handleView = (row) => {
  router.push(`/admin/bookings/${row.id}`)
}

const handleCheckIn = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要为订单 ${row.orderNumber} 办理入住吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 这里应该调用办理入住API
    row.status = 'checkedin'
    ElMessage.success('办理入住成功')
  } catch (error) {
    // 用户取消操作
  }
}

const handleCheckOut = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要为订单 ${row.orderNumber} 办理退房吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 这里应该调用办理退房API
    row.status = 'checkedout'
    ElMessage.success('办理退房成功')
  } catch (error) {
    // 用户取消操作
  }
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要取消订单 ${row.orderNumber} 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 这里应该调用取消订单API
    row.status = 'cancelled'
    ElMessage.success('订单取消成功')
  } catch (error) {
    // 用户取消操作
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
    // 这里应该调用API获取订单列表
    await new Promise(resolve => setTimeout(resolve, 500))
    total.value = bookingList.value.length
  } catch (error) {
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
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


