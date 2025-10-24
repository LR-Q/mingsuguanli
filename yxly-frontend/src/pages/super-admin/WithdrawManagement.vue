<template>
  <div class="withdraw-management">
    <div class="page-header">
      <h2>提现管理</h2>
      <p>管理所有商户的提现申请，审核提现请求</p>
    </div>

    <!-- 搜索筛选 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="searchForm" inline>
        <el-form-item label="用户名">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="选择状态"
            clearable
            style="width: 150px"
          >
            <el-option label="全部" :value="null" />
            <el-option label="待审核" :value="0" />
            <el-option label="审核通过" :value="1" />
            <el-option label="审核拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="提现方式">
          <el-select
            v-model="searchForm.withdrawMethod"
            placeholder="选择提现方式"
            clearable
            style="width: 150px"
          >
            <el-option label="全部" :value="null" />
            <el-option label="支付宝" value="支付宝" />
            <el-option label="微信" value="微信" />
            <el-option label="银行卡" value="银行卡" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 提现记录表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        :data="withdrawList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column label="提现金额" width="120">
          <template #default="{ row }">
            <span class="amount-text">¥{{ formatAmount(row.amount) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="withdrawMethod" label="提现方式" width="100" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag
              :type="getStatusType(row.status)"
              size="small"
            >
              {{ row.statusDesc }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="申请时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.applyTime) }}
          </template>
        </el-table-column>
        <el-table-column label="审核时间" width="180">
          <template #default="{ row }">
            {{ row.auditTime ? formatDateTime(row.auditTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="auditorName" label="审核人" width="100">
          <template #default="{ row }">
            {{ row.auditorName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 0"
              type="primary"
              size="small"
              @click="handleAudit(row)"
            >
              审核详情
            </el-button>
            <el-button
              v-else
              type="info"
              size="small"
              @click="handleViewDetail(row)"
            >
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
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
    </el-card>

    <!-- 审核对话框 -->
    <el-dialog
      v-model="showAuditDialog"
      title="提现审核"
      width="600px"
      @close="handleCloseAuditDialog"
    >
      <div v-if="currentRecord" class="audit-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户名">{{ currentRecord.username }}</el-descriptions-item>
          <el-descriptions-item label="提现金额">
            <span class="amount-text">¥{{ formatAmount(currentRecord.amount) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="提现方式">{{ currentRecord.withdrawMethod }}</el-descriptions-item>
          <el-descriptions-item label="账户信息">{{ currentRecord.accountInfo }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ formatDateTime(currentRecord.applyTime) }}</el-descriptions-item>
          <el-descriptions-item label="用户备注">
            {{ currentRecord.userRemark || '无' }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="audit-form">
          <el-form :model="auditForm" label-width="100px">
            <el-form-item label="审核结果" required>
              <el-radio-group v-model="auditForm.status">
                <el-radio :label="1">通过</el-radio>
                <el-radio :label="2">拒绝</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="审核备注">
              <el-input
                v-model="auditForm.auditRemark"
                type="textarea"
                :rows="3"
                placeholder="请输入审核备注（可选）"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>
          </el-form>
        </div>
      </div>

      <template #footer>
        <el-button @click="showAuditDialog = false">取消</el-button>
        <el-button
          type="primary"
          @click="handleSubmitAudit"
          :loading="auditLoading"
        >
          提交审核
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getWithdrawRecords, auditWithdraw } from '@/api/modules/superAdmin'

// 响应式数据
const loading = ref(false)
const withdrawList = ref([])
const showAuditDialog = ref(false)
const currentRecord = ref(null)
const auditLoading = ref(false)

// 搜索表单
const searchForm = reactive({
  username: '',
  status: null,
  withdrawMethod: null
})

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 审核表单
const auditForm = reactive({
  status: 1,
  auditRemark: ''
})

// 加载提现记录
const loadWithdrawRecords = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      username: searchForm.username || undefined,
      status: searchForm.status !== null ? searchForm.status : undefined,
      withdrawMethod: searchForm.withdrawMethod || undefined
    }
    
    const { data } = await getWithdrawRecords(params)
    withdrawList.value = data.records
    pagination.total = data.total
  } catch (error) {
    console.error('加载提现记录失败:', error)
    ElMessage.error('加载提现记录失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  loadWithdrawRecords()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    username: '',
    status: null,
    withdrawMethod: null
  })
  pagination.current = 1
  loadWithdrawRecords()
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.current = 1
  loadWithdrawRecords()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  loadWithdrawRecords()
}

// 审核处理
const handleAudit = (record) => {
  currentRecord.value = record
  auditForm.status = 1
  auditForm.auditRemark = ''
  showAuditDialog.value = true
}

// 查看详情
const handleViewDetail = (record) => {
  currentRecord.value = record
  showAuditDialog.value = true
}

// 关闭审核对话框
const handleCloseAuditDialog = () => {
  currentRecord.value = null
  auditForm.status = 1
  auditForm.auditRemark = ''
}

// 提交审核
const handleSubmitAudit = async () => {
  if (!currentRecord.value) return
  
  auditLoading.value = true
  try {
    await auditWithdraw({
      withdrawId: currentRecord.value.id,
      status: auditForm.status,
      auditRemark: auditForm.auditRemark
    })
    
    ElMessage.success('审核成功')
    showAuditDialog.value = false
    loadWithdrawRecords()
  } catch (error) {
    console.error('审核失败:', error)
    ElMessage.error('审核失败，请重试')
  } finally {
    auditLoading.value = false
  }
}

// 工具函数
const formatAmount = (amount) => {
  return parseFloat(amount).toFixed(2)
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

const getStatusType = (status) => {
  switch (status) {
    case 0: return 'warning'  // 待审核
    case 1: return 'success'  // 审核通过
    case 2: return 'danger'   // 审核拒绝
    default: return 'info'
  }
}

// 页面加载
onMounted(() => {
  loadWithdrawRecords()
})
</script>

<style lang="scss" scoped>
.withdraw-management {
  .page-header {
    margin-bottom: 20px;
    
    h2 {
      margin: 0 0 8px 0;
      color: #303133;
      font-size: 20px;
      font-weight: 500;
    }
    
    p {
      margin: 0;
      color: #606266;
      font-size: 14px;
    }
  }
  
  .search-card {
    margin-bottom: 20px;
  }
  
  .table-card {
    .amount-text {
      color: #f56c6c;
      font-weight: 500;
    }
    
    .pagination-wrapper {
      margin-top: 20px;
      text-align: right;
    }
  }
  
  .audit-content {
    .audit-form {
      margin-top: 20px;
      padding-top: 20px;
      border-top: 1px solid #ebeef5;
    }
  }
}
</style>
