<template>
  <div class="customer-management">
    <div class="page-header">
      <h2>客户管理</h2>
      <p>管理所有注册用户信息</p>
    </div>
    
    <!-- 搜索区域 -->
    <div class="search-section">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="搜索">
          <el-input
            v-model="searchForm.keyword"
            placeholder="输入用户名、姓名、手机号或邮箱"
            style="width: 300px"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch" :loading="loading">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <!-- 客户列表 -->
    <div class="table-section">
      <el-table
        :data="customerList"
        :loading="loading"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column prop="id" label="ID" width="80" />
        
        <el-table-column prop="username" label="用户名" min-width="120">
          <template #default="{ row }">
            <el-tag type="info">{{ row.username }}</el-tag>
          </template>
        </el-table-column>
        
        
        <el-table-column prop="phone" label="手机号" min-width="130">
          <template #default="{ row }">
            <span>{{ row.phone || '-' }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="email" label="邮箱" min-width="180">
          <template #default="{ row }">
            <span>{{ row.email || '-' }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        
        
        <el-table-column prop="lastLoginTime" label="最后登录" min-width="160">
          <template #default="{ row }">
            <span>{{ row.lastLoginTime || '-' }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="createTime" label="注册时间" min-width="160" />
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleViewDetail(row)"
            >
              详情
            </el-button>
            
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            
            <el-popconfirm
              title="确定要删除这个客户吗？"
              @confirm="handleDelete(row)"
            >
              <template #reference>
                <el-button type="danger" size="small">
                  删除
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
    
    <!-- 客户详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="客户详情"
      width="600px"
    >
      <div v-if="currentCustomer" class="customer-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户ID">
            {{ currentCustomer.id }}
          </el-descriptions-item>
          <el-descriptions-item label="用户名">
            {{ currentCustomer.username }}
          </el-descriptions-item>
          <el-descriptions-item label="手机号">
            {{ currentCustomer.phone || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="邮箱">
            {{ currentCustomer.email || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentCustomer.status === 1 ? 'success' : 'danger'">
              {{ currentCustomer.statusText }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="最后登录时间">
            {{ currentCustomer.lastLoginTime || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="最后登录IP">
            {{ currentCustomer.lastLoginIp || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="注册时间">
            {{ currentCustomer.createTime }}
          </el-descriptions-item>
          <el-descriptions-item label="更新时间">
            {{ currentCustomer.updateTime }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
      
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getCustomerPage, updateCustomerStatus, deleteCustomer } from '@/api/modules/customer'

// 响应式数据
const loading = ref(false)
const customerList = ref([])
const selectedCustomers = ref([])
const detailDialogVisible = ref(false)
const currentCustomer = ref(null)

// 搜索表单
const searchForm = reactive({
  keyword: ''
})

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 获取客户列表
const getCustomerList = async () => {
  try {
    loading.value = true
    
    const params = {
      current: pagination.current,
      size: pagination.size,
      keyword: searchForm.keyword || undefined
    }
    
    const response = await getCustomerPage(params)
    
    if (response.data) {
      customerList.value = response.data.records
      pagination.total = response.data.total
      pagination.current = response.data.current
      pagination.size = response.data.size
    }
  } catch (error) {
    console.error('获取客户列表失败:', error)
    ElMessage.error('获取客户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  getCustomerList()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  pagination.current = 1
  getCustomerList()
}

// 分页大小改变
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  getCustomerList()
}

// 当前页改变
const handleCurrentChange = (current) => {
  pagination.current = current
  getCustomerList()
}

// 选择改变
const handleSelectionChange = (selection) => {
  selectedCustomers.value = selection
}

// 查看详情
const handleViewDetail = (row) => {
  currentCustomer.value = row
  detailDialogVisible.value = true
}

// 切换状态
const handleToggleStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '启用' : '禁用'
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}客户 "${row.username}" 吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await updateCustomerStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    
    // 更新列表中的状态
    row.status = newStatus
    row.statusText = newStatus === 1 ? '正常' : '禁用'
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('状态更新失败:', error)
      ElMessage.error('状态更新失败')
    }
  }
}

// 删除客户
const handleDelete = async (row) => {
  try {
    await deleteCustomer(row.id)
    ElMessage.success('删除成功')
    
    // 重新加载列表
    getCustomerList()
    
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

// 组件挂载时获取数据
onMounted(() => {
  getCustomerList()
})
</script>

<style scoped>
.customer-management {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.page-header p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.search-section {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.search-form {
  margin: 0;
}

.table-section {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.pagination-section {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.customer-detail {
  padding: 10px 0;
}

:deep(.el-table) {
  font-size: 14px;
}

:deep(.el-table th) {
  background-color: #fafafa;
  color: #606266;
  font-weight: 600;
}

:deep(.el-pagination) {
  justify-content: flex-end;
}
</style>
