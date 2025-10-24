<template>
  <div class="withdraw-apply">
    <div class="page-header">
      <h2>提现申请</h2>
      <p>申请提现并查看提现记录</p>
    </div>

    <!-- 申请提现卡片 -->
    <el-card class="apply-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>申请提现</span>
        </div>
      </template>
      
      <el-form 
        :model="applyForm" 
        :rules="applyRules"
        ref="applyFormRef"
        label-width="100px"
      >
        <el-form-item label="提现金额" prop="amount">
          <el-input-number
            v-model="applyForm.amount"
            :min="0.01"
            :max="10000"
            :precision="2"
            :step="100"
            placeholder="请输入提现金额"
          />
          <span class="hint-text">（最小0.01元，最大10000元）</span>
        </el-form-item>
        
        <el-form-item label="提现方式" prop="withdrawMethod">
          <el-select 
            v-model="applyForm.withdrawMethod" 
            placeholder="请选择提现方式"
            style="width: 200px"
          >
            <el-option label="支付宝" value="支付宝" />
            <el-option label="微信" value="微信" />
            <el-option label="银行卡" value="银行卡" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="收款账户" prop="accountInfo">
          <el-input
            v-model="applyForm.accountInfo"
            placeholder="请输入收款账户信息"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="备注说明">
          <el-input
            v-model="applyForm.userRemark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注说明（可选）"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            @click="handleSubmitApply"
            :loading="submitLoading"
          >
            提交申请
          </el-button>
          <el-button @click="handleResetApplyForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 提现记录 -->
    <el-card class="record-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>我的提现记录</span>
          <el-select 
            v-model="statusFilter" 
            placeholder="筛选状态"
            clearable
            style="width: 150px"
            @change="loadWithdrawRecords"
          >
            <el-option label="全部" :value="null" />
            <el-option label="待审核" :value="0" />
            <el-option label="审核通过" :value="1" />
            <el-option label="审核拒绝" :value="2" />
          </el-select>
        </div>
      </template>

      <el-table
        :data="withdrawList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="提现金额" width="120">
          <template #default="{ row }">
            <span class="amount-text">¥{{ formatAmount(row.amount) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="withdrawMethod" label="提现方式" width="100" />
        <el-table-column prop="accountInfo" label="收款账户" min-width="150" show-overflow-tooltip />
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
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              type="info"
              size="small"
              @click="handleViewDetail(row)"
            >
              查看详情
            </el-button>
            <el-button
              v-if="row.status === 0"
              type="danger"
              size="small"
              @click="handleCancelWithdraw(row)"
            >
              取消申请
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      title="提现记录详情"
      width="600px"
    >
      <div v-if="currentRecord" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="提现金额">
            <span class="amount-text">¥{{ formatAmount(currentRecord.amount) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="提现方式">{{ currentRecord.withdrawMethod }}</el-descriptions-item>
          <el-descriptions-item label="收款账户" :span="2">{{ currentRecord.accountInfo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentRecord.status)">
              {{ currentRecord.statusDesc }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ formatDateTime(currentRecord.applyTime) }}</el-descriptions-item>
          <el-descriptions-item label="审核时间" :span="2">
            {{ currentRecord.auditTime ? formatDateTime(currentRecord.auditTime) : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="用户备注" :span="2">
            {{ currentRecord.userRemark || '无' }}
          </el-descriptions-item>
          <el-descriptions-item label="审核备注" :span="2">
            {{ currentRecord.auditRemark || '无' }}
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getMyWithdrawRecords, 
  applyWithdraw, 
  cancelWithdraw 
} from '@/api/modules/merchantWithdraw'

// 响应式数据
const loading = ref(false)
const submitLoading = ref(false)
const withdrawList = ref([])
const showDetailDialog = ref(false)
const currentRecord = ref(null)
const applyFormRef = ref(null)
const statusFilter = ref(null)

// 申请表单
const applyForm = reactive({
  amount: null,
  withdrawMethod: '',
  accountInfo: '',
  userRemark: ''
})

// 表单验证规则
const applyRules = {
  amount: [
    { required: true, message: '请输入提现金额', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value <= 0) {
          callback(new Error('提现金额必须大于0'))
        } else if (value > 10000) {
          callback(new Error('单次提现金额不能超过10000元'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ],
  withdrawMethod: [
    { required: true, message: '请选择提现方式', trigger: 'change' }
  ],
  accountInfo: [
    { required: true, message: '请输入收款账户信息', trigger: 'blur' }
  ]
}

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 加载提现记录
const loadWithdrawRecords = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      status: statusFilter.value !== null ? statusFilter.value : undefined
    }
    
    const { data } = await getMyWithdrawRecords(params)
    withdrawList.value = data.records
    pagination.total = data.total
  } catch (error) {
    console.error('加载提现记录失败:', error)
    ElMessage.error('加载提现记录失败')
  } finally {
    loading.value = false
  }
}

// 提交提现申请
const handleSubmitApply = async () => {
  if (!applyFormRef.value) return
  
  await applyFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      await applyWithdraw(applyForm)
      ElMessage.success('提现申请提交成功，请等待审核')
      handleResetApplyForm()
      loadWithdrawRecords()
    } catch (error) {
      console.error('提现申请失败:', error)
      ElMessage.error(error.response?.data?.message || '提现申请失败')
    } finally {
      submitLoading.value = false
    }
  })
}

// 重置申请表单
const handleResetApplyForm = () => {
  if (applyFormRef.value) {
    applyFormRef.value.resetFields()
  }
  Object.assign(applyForm, {
    amount: null,
    withdrawMethod: '',
    accountInfo: '',
    userRemark: ''
  })
}

// 查看详情
const handleViewDetail = (record) => {
  currentRecord.value = record
  showDetailDialog.value = true
}

// 取消提现申请
const handleCancelWithdraw = (record) => {
  ElMessageBox.confirm(
    '确定要取消该提现申请吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await cancelWithdraw(record.id)
      ElMessage.success('提现申请已取消')
      loadWithdrawRecords()
    } catch (error) {
      console.error('取消提现失败:', error)
      ElMessage.error('取消提现失败')
    }
  }).catch(() => {
    // 用户取消操作
  })
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
.withdraw-apply {
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
  
  .apply-card {
    margin-bottom: 20px;
    
    .hint-text {
      margin-left: 10px;
      color: #909399;
      font-size: 12px;
    }
  }
  
  .record-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    
    .amount-text {
      color: #f56c6c;
      font-weight: 500;
    }
    
    .pagination-wrapper {
      margin-top: 20px;
      text-align: right;
    }
  }
  
  .detail-content {
    .amount-text {
      color: #f56c6c;
      font-weight: 500;
      font-size: 16px;
    }
  }
}
</style>
