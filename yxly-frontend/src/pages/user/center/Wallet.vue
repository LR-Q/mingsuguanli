<template>
  <div class="wallet-page">
    <!-- 账户余额 -->
    <el-card class="balance-card" shadow="never">
      <div class="balance-info">
        <div class="balance-icon">
          <el-icon><Wallet /></el-icon>
        </div>
        <div class="balance-content">
          <h3>账户余额</h3>
          <p class="balance-amount" v-if="!balanceLoading">¥{{ formatAmount(walletInfo.balance) }}</p>
          <p class="balance-amount" v-else>加载中...</p>
        </div>
        <div class="balance-actions">
          <el-button type="primary" @click="showRecharge = true">
            <el-icon><Plus /></el-icon>
            充值
          </el-button>
          <el-button @click="showWithdraw = true">
            <el-icon><Minus /></el-icon>
            提现
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 交易记录 -->
    <el-card class="details-card" shadow="never">
      <div class="transactions-section">
        <div class="section-header">
          <h3>交易记录</h3>
          <div class="filters">
            <el-select v-model="transactionFilter" placeholder="交易类型" style="width: 120px">
              <el-option label="全部" value="all" />
              <el-option label="充值" value="recharge" />
              <el-option label="消费" value="consume" />
              <el-option label="退款" value="refund" />
              <el-option label="提现" value="withdraw" />
            </el-select>
            <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              style="width: 240px; margin-left: 12px"
            />
          </div>
        </div>
        
        <el-table :data="filteredTransactions" style="width: 100%">
          <el-table-column prop="id" label="交易单号" width="180" />
          <el-table-column prop="type" label="交易类型" width="100">
            <template #default="{ row }">
              <el-tag :type="getTransactionTypeColor(row.type)">
                {{ getTransactionTypeName(row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额" width="120">
            <template #default="{ row }">
              <span :class="row.type === 'recharge' || row.type === 'refund' ? 'income' : 'expense'">
                {{ row.type === 'recharge' || row.type === 'refund' ? '+' : '-' }}¥{{ formatAmount(row.amount) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="交易说明" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusColor(row.status)">
                {{ getStatusName(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="交易时间" width="180" />
        </el-table>
        
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="totalTransactions"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </el-card>

    <!-- 充值申请对话框 -->
    <el-dialog v-model="showRecharge" title="申请充值" width="600px">
      <el-form :model="rechargeForm" :rules="rechargeRules" ref="rechargeFormRef" label-width="100px">
        <el-form-item label="充值金额" prop="amount">
          <el-input-number
            v-model="rechargeForm.amount"
            :min="1"
            :max="10000"
            :precision="2"
            style="width: 100%"
            placeholder="请输入充值金额"
          />
          <div class="form-tip">最低充值1元，最高10000元</div>
        </el-form-item>
        
        <el-form-item label="支付方式" prop="paymentMethod">
          <el-radio-group v-model="rechargeForm.paymentMethod">
            <el-radio label="支付宝">支付宝</el-radio>
            <el-radio label="微信支付">微信支付</el-radio>
            <el-radio label="银行转账">银行转账</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="支付凭证" prop="paymentProof">
          <el-upload
            class="proof-uploader"
            :auto-upload="true"
            :show-file-list="false"
            :http-request="customUpload"
            :before-upload="beforeProofUpload"
            accept="image/*"
          >
            <el-image
              v-if="rechargeForm.paymentProof"
              :src="rechargeForm.paymentProof"
              class="proof-image"
              fit="cover"
            />
            <el-icon v-else class="proof-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div class="form-tip">请上传支付截图或转账凭证，支持jpg、png格式，大小不超过10MB</div>
        </el-form-item>
        
        <el-form-item label="备注说明">
          <el-input
            v-model="rechargeForm.userRemark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注说明（可选）"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        
        <el-alert
          title="充值说明"
          type="info"
          :closable="false"
          show-icon
        >
          <p>1. 请先完成支付，然后上传支付凭证</p>
          <p>2. 管理员将在1-3个工作日内审核您的充值申请</p>
          <p>3. 审核通过后，金额将自动充值到您的账户</p>
        </el-alert>
      </el-form>
      
      <template #footer>
        <el-button @click="showRecharge = false">取消</el-button>
        <el-button 
          type="primary" 
          @click="handleRecharge"
          :loading="rechargeLoading"
        >
          提交申请
        </el-button>
      </template>
    </el-dialog>

    <!-- 提现对话框 -->
    <el-dialog v-model="showWithdraw" title="账户提现" width="500px">
      <el-form :model="withdrawForm" label-width="80px">
        <el-form-item label="提现金额">
          <el-input-number
            v-model="withdrawForm.amount"
            :min="1"
            :max="walletInfo.balance"
            :precision="2"
            style="width: 100%"
          />
          <div class="balance-tip">
            可提现余额：¥{{ formatAmount(walletInfo.balance) }}
          </div>
        </el-form-item>
        <el-form-item label="提现方式">
          <el-radio-group v-model="withdrawForm.method">
            <el-radio label="支付宝">支付宝</el-radio>
            <el-radio label="微信">微信</el-radio>
            <el-radio label="银行卡">银行卡</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showWithdraw = false">取消</el-button>
        <el-button type="primary" @click="handleWithdraw">确认提现</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Wallet, 
  Plus, 
  Minus
} from '@element-plus/icons-vue'
import { applyRecharge, uploadPaymentProof, getUserRechargeRecords, applyWithdraw, getUserWithdrawRecords } from '@/api/modules/recharge'
import { getUserBalance } from '@/api/modules/user'
import { getMyFinancialRecords } from '@/api/modules/financial'

// 响应式数据
const showRecharge = ref(false)
const showWithdraw = ref(false)
const rechargeLoading = ref(false)
const rechargeFormRef = ref()

const transactionFilter = ref('all')
const dateRange = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalTransactions = ref(0)

// 钱包信息
const walletInfo = reactive({
  balance: 0.00
})

// 加载状态
const balanceLoading = ref(false)

// 表单数据
const rechargeForm = reactive({
  amount: null,
  paymentMethod: '支付宝',
  paymentProof: '',
  userRemark: ''
})

// 表单验证规则
const rechargeRules = {
  amount: [
    { required: true, message: '请输入充值金额', trigger: 'blur' },
    { type: 'number', min: 1, max: 10000, message: '充值金额必须在1-10000元之间', trigger: 'blur' }
  ],
  paymentMethod: [
    { required: true, message: '请选择支付方式', trigger: 'change' }
  ],
  paymentProof: [
    { required: true, message: '请上传支付凭证', trigger: 'change' }
  ]
}

const withdrawForm = reactive({
  amount: null,
  method: '支付宝'
})


// 模拟其他交易记录数据（已清空，新用户无交易记录）
const mockTransactions = []

// 所有交易记录
const transactions = ref([])


// 计算属性
const filteredTransactions = computed(() => {
  let result = transactions.value
  
  if (transactionFilter.value !== 'all') {
    result = result.filter(t => t.type === transactionFilter.value)
  }
  
  // 这里可以添加日期范围过滤逻辑
  
  return result
})


// 方法
const formatAmount = (amount) => {
  return amount.toFixed(2)
}

const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleString('zh-CN')
}

const getTransactionTypeName = (type) => {
  const names = {
    recharge: '充值',
    consume: '消费',
    refund: '退款',
    withdraw: '提现'
  }
  return names[type] || type
}

const getTransactionTypeColor = (type) => {
  const colors = {
    recharge: 'success',
    consume: 'warning',
    refund: 'info',
    withdraw: 'danger'
  }
  return colors[type] || ''
}

const getStatusName = (status) => {
  const names = {
    success: '成功',
    pending: '处理中',
    failed: '失败'
  }
  return names[status] || status
}

const getStatusColor = (status) => {
  const colors = {
    success: 'success',
    pending: 'warning',
    failed: 'danger'
  }
  return colors[status] || ''
}

const handleSizeChange = (size) => {
  pageSize.value = size
  // 重新加载数据
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  // 重新加载数据
}

// 文件上传相关方法
const customUpload = async (options) => {
  try {
    const formData = new FormData()
    formData.append('file', options.file)
    
    const response = await uploadPaymentProof(formData)
    rechargeForm.paymentProof = response.data
    
    options.onSuccess(response, options.file)
    ElMessage.success('支付凭证上传成功')
  } catch (error) {
    console.error('支付凭证上传失败:', error)
    ElMessage.error('支付凭证上传失败: ' + (error.response?.data?.message || error.message))
    options.onError(error, options.file)
  }
}

const beforeProofUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过 10MB!')
    return false
  }
  return true
}

const handleRecharge = async () => {
  try {
    // 表单验证
    await rechargeFormRef.value.validate()
    
    rechargeLoading.value = true
    
    // 提交充值申请
    await applyRecharge({
      amount: rechargeForm.amount,
      paymentMethod: rechargeForm.paymentMethod,
      paymentProof: rechargeForm.paymentProof,
      userRemark: rechargeForm.userRemark
    })
    
    ElMessage.success('充值申请提交成功，请等待管理员审核')
    showRecharge.value = false
    
    // 重置表单
    Object.assign(rechargeForm, {
      amount: null,
      paymentMethod: '支付宝',
      paymentProof: '',
      userRemark: ''
    })
    
    // 刷新交易记录
    loadRechargeRecords()
  } catch (error) {
    console.error('充值申请失败:', error)
    if (error.response?.data?.message) {
      ElMessage.error('充值申请失败: ' + error.response.data.message)
    } else {
      ElMessage.error('充值申请失败，请重试')
    }
  } finally {
    rechargeLoading.value = false
  }
}

// 加载充值和提现记录
const loadRechargeRecords = async () => {
  try {
    // 并行加载充值记录、提现记录和财务记录
    const [rechargeResponse, withdrawResponse, financialResponse] = await Promise.all([
      getUserRechargeRecords({
        current: currentPage.value,
        size: pageSize.value
      }),
      getUserWithdrawRecords({
        current: currentPage.value,
        size: pageSize.value
      }),
      getMyFinancialRecords({
        current: currentPage.value,
        size: 1000 // 获取更多记录
      })
    ])
    
    // 将充值记录转换为交易记录格式
    const rechargeTransactions = rechargeResponse.data.records.map(record => ({
      id: `R${record.id}`,
      type: 'recharge',
      amount: record.amount,
      description: `充值申请 - ${record.statusName}`,
      status: record.status === 1 ? 'success' : record.status === 0 ? 'pending' : 'failed',
      createTime: record.applyTime
    }))
    
    // 将提现记录转换为交易记录格式
    const withdrawTransactions = withdrawResponse.data.records.map(record => ({
      id: `W${record.id}`,
      type: 'withdraw',
      amount: record.amount,
      description: `提现申请 - ${record.statusDesc}`,
      status: record.status === 1 ? 'success' : record.status === 0 ? 'pending' : 'failed',
      createTime: record.applyTime
    }))
    
    // 将财务记录转换为交易记录格式
    const financialTransactions = (financialResponse.data.records || []).map(record => {
      let type = 'consume'  // 默认消费
      let description = record.description || '交易'
      
      // 根据category判断类型
      if (record.category === 'CONSUMPTION') {
        type = 'consume'
        description = record.description || '订房消费'
      } else if (record.category === 'REFUND') {
        type = 'refund'
        description = record.description || '退款'
      }
      
      return {
        id: record.recordNo || `F${record.id}`,
        type: type,
        amount: record.amount,
        description: description,
        status: 'success',
        createTime: record.createTime || record.recordDate  // 使用创建时间或记录日期
      }
    })
    
    // 合并所有交易记录并按时间排序
    const allTransactions = [...rechargeTransactions, ...withdrawTransactions, ...financialTransactions, ...mockTransactions]
    allTransactions.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
    
    transactions.value = allTransactions
    totalTransactions.value = transactions.value.length
  } catch (error) {
    console.error('加载交易记录失败:', error)
  }
}

// 加载用户余额
const loadUserBalance = async () => {
  balanceLoading.value = true
  try {
    const { data } = await getUserBalance()
    walletInfo.balance = parseFloat(data) || 0.00
    console.log('从后端获取余额:', walletInfo.balance)
  } catch (error) {
    console.error('加载用户余额失败:', error)
    // 如果后端不可用，设置为0（新用户默认余额）
    walletInfo.balance = 0.00
    console.log('后端不可用，设置默认余额为0')
  } finally {
    balanceLoading.value = false
  }
}

const handleWithdraw = async () => {
  try {
    if (withdrawForm.amount > walletInfo.balance) {
      ElMessage.error('提现金额不能超过账户余额')
      return
    }
    
    if (walletInfo.balance === 0) {
      ElMessage.error('账户余额为0，无法提现')
      return
    }
    
    // 构建提现申请数据
    const withdrawData = {
      amount: withdrawForm.amount,
      withdrawMethod: withdrawForm.method,
      accountInfo: `${withdrawForm.method}账户`, // 简化的账户信息
      userRemark: ''
    }
    
    // 调用真正的提现API
    await applyWithdraw(withdrawData)
    
    // 提现申请成功后重新加载余额和交易记录
    await loadUserBalance()
    await loadRechargeRecords()
    
    ElMessage.success(`提现申请已提交，金额：¥${withdrawForm.amount}，请等待管理员审核`)
    showWithdraw.value = false
    
    // 重置提现表单
    withdrawForm.amount = null
    withdrawForm.method = '支付宝'
  } catch (error) {
    console.error('提现申请失败:', error)
    ElMessage.error('提现申请失败，请重试')
  }
}


onMounted(() => {
  // 初始化时加载用户余额和充值记录
  loadUserBalance()
  loadRechargeRecords()
})
</script>

<style lang="scss" scoped>
.wallet-page {
  .balance-card {
    margin-bottom: 20px;
    
    .balance-info {
      display: flex;
      align-items: center;
      gap: 16px;
      
      .balance-icon {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        background: linear-gradient(135deg, #409eff, #67c23a);
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-size: 24px;
      }
      
      .balance-content {
        flex: 1;
        
        h3 {
          margin: 0 0 8px 0;
          color: #606266;
          font-size: 14px;
          font-weight: 500;
        }
        
        .balance-amount {
          margin: 0;
          color: #303133;
          font-size: 24px;
          font-weight: 600;
        }
      }
      
      .balance-actions {
        display: flex;
        gap: 12px;
        
        .el-button {
          min-width: 80px;
        }
      }
    }
  }
  
  
  .details-card {
    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      
      h3 {
        margin: 0;
        color: #303133;
        font-size: 18px;
        font-weight: 600;
      }
      
      .filters {
        display: flex;
        align-items: center;
      }
    }
    
    .pagination-wrapper {
      margin-top: 20px;
      text-align: center;
    }
    
    .income {
      color: #67c23a;
      font-weight: 600;
    }
    
    .expense {
      color: #f56c6c;
      font-weight: 600;
    }
  }
  
}

.balance-tip {
  margin-top: 8px;
  color: #909399;
  font-size: 12px;
}

.form-tip {
  margin-top: 8px;
  color: #909399;
  font-size: 12px;
  line-height: 1.4;
}

.proof-uploader {
  .proof-image {
    width: 200px;
    height: 150px;
    border-radius: 6px;
    border: 1px solid #dcdfe6;
  }
  
  .proof-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 200px;
    height: 150px;
    text-align: center;
    line-height: 150px;
    border: 2px dashed #dcdfe6;
    border-radius: 6px;
    cursor: pointer;
    transition: border-color 0.3s;
    
    &:hover {
      border-color: #409eff;
      color: #409eff;
    }
  }
}

:deep(.el-card__header) {
  background: #fafafa;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-alert) {
  margin-top: 16px;
  
  p {
    margin: 4px 0;
    font-size: 13px;
  }
}

</style>
