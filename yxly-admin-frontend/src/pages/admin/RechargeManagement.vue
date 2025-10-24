<template>
  <div class="recharge-management">
    <div class="page-header">
      <h1>充值管理</h1>
      <p>管理用户充值申请，审核充值记录</p>
    </div>

    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="用户名">
          <el-input 
            v-model="searchForm.username" 
            placeholder="请输入用户名" 
            clearable
            style="width: 200px;"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select 
            v-model="searchForm.status" 
            placeholder="请选择状态" 
            clearable
            style="width: 150px;"
          >
            <el-option label="待审核" :value="0" />
            <el-option label="审核通过" :value="1" />
            <el-option label="审核拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="支付方式">
          <el-select 
            v-model="searchForm.paymentMethod" 
            placeholder="请选择支付方式" 
            clearable
            style="width: 150px;"
          >
            <el-option label="支付宝" value="支付宝" />
            <el-option label="微信支付" value="微信支付" />
            <el-option label="银行转账" value="银行转账" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchRechargeRecords">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 充值记录表格 -->
    <el-card class="table-card">
      <el-table 
        :data="rechargeRecords" 
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="amount" label="充值金额" width="120">
          <template #default="{ row }">
            <span class="amount">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag 
              :type="getStatusType(row.status)"
              size="small"
            >
              {{ row.statusName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.applyTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="auditTime" label="审核时间" width="180">
          <template #default="{ row }">
            {{ row.auditTime ? formatDateTime(row.auditTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="auditorName" label="审核人" width="120">
          <template #default="{ row }">
            {{ row.auditorName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button 
              type="primary" 
              size="small" 
              @click="viewDetail(row)"
            >
              查看详情
            </el-button>
            <el-button 
              v-if="row.status === 0"
              type="success" 
              size="small" 
              @click="auditRecharge(row, 1)"
            >
              通过
            </el-button>
            <el-button 
              v-if="row.status === 0"
              type="danger" 
              size="small" 
              @click="auditRecharge(row, 2)"
            >
              拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
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

    <!-- 详情对话框 -->
    <el-dialog 
      v-model="detailDialogVisible" 
      title="充值记录详情" 
      width="600px"
    >
      <div v-if="currentRecord" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="记录ID">{{ currentRecord.id }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ currentRecord.username }}</el-descriptions-item>
          <el-descriptions-item label="充值金额">
            <span class="amount">¥{{ currentRecord.amount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="支付方式">{{ currentRecord.paymentMethod }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentRecord.status)">
              {{ currentRecord.statusName }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ formatDateTime(currentRecord.applyTime) }}</el-descriptions-item>
          <el-descriptions-item label="审核时间">
            {{ currentRecord.auditTime ? formatDateTime(currentRecord.auditTime) : '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="审核人">{{ currentRecord.auditorName || '-' }}</el-descriptions-item>
        </el-descriptions>

        <div class="detail-section">
          <h4>支付凭证</h4>
          <el-image 
            v-if="currentRecord.paymentProof"
            :src="currentRecord.paymentProof"
            style="width: 300px; height: 200px;"
            fit="contain"
            :preview-src-list="[currentRecord.paymentProof]"
          />
          <span v-else>无</span>
        </div>

        <div class="detail-section" v-if="currentRecord.userRemark">
          <h4>用户备注</h4>
          <p>{{ currentRecord.userRemark }}</p>
        </div>

        <div class="detail-section" v-if="currentRecord.auditRemark">
          <h4>审核备注</h4>
          <p>{{ currentRecord.auditRemark }}</p>
        </div>
      </div>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog 
      v-model="auditDialogVisible" 
      :title="auditForm.status === 1 ? '审核通过' : '审核拒绝'" 
      width="500px"
    >
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="充值金额">
          <span class="amount">¥{{ auditForm.amount }}</span>
        </el-form-item>
        <el-form-item label="用户名">
          <span>{{ auditForm.username }}</span>
        </el-form-item>
        <el-form-item label="审核备注">
          <el-input 
            v-model="auditForm.auditRemark"
            type="textarea"
            :rows="3"
            placeholder="请输入审核备注（可选）"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="auditDialogVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="confirmAudit"
            :loading="auditLoading"
          >
            确认{{ auditForm.status === 1 ? '通过' : '拒绝' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRechargeRecords, auditRechargeRecord } from '@/api/modules/recharge'

// 响应式数据
const loading = ref(false)
const rechargeRecords = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const detailDialogVisible = ref(false)
const auditDialogVisible = ref(false)
const auditLoading = ref(false)
const currentRecord = ref(null)

// 搜索表单
const searchForm = reactive({
  username: '',
  status: null,
  paymentMethod: ''
})

// 审核表单
const auditForm = reactive({
  id: null,
  status: null,
  auditRemark: '',
  username: '',
  amount: 0
})

// 方法
const loadRechargeRecords = async () => {
  try {
    loading.value = true
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      ...searchForm
    }
    
    const response = await getRechargeRecords(params)
    const data = response.data
    
    rechargeRecords.value = data.records
    total.value = data.total
    
    ElMessage.success('查询成功')
  } catch (error) {
    console.error('查询充值记录失败:', error)
    ElMessage.error('查询充值记录失败')
  } finally {
    loading.value = false
  }
}

const searchRechargeRecords = () => {
  currentPage.value = 1
  loadRechargeRecords()
}

const resetSearch = () => {
  Object.assign(searchForm, {
    username: '',
    status: null,
    paymentMethod: ''
  })
  searchRechargeRecords()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadRechargeRecords()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadRechargeRecords()
}

const viewDetail = (record) => {
  currentRecord.value = record
  detailDialogVisible.value = true
}

const auditRecharge = (record, status) => {
  auditForm.id = record.id
  auditForm.status = status
  auditForm.auditRemark = ''
  auditForm.username = record.username
  auditForm.amount = record.amount
  auditDialogVisible.value = true
}

const confirmAudit = async () => {
  try {
    auditLoading.value = true
    
    await auditRechargeRecord({
      id: auditForm.id,
      status: auditForm.status,
      auditRemark: auditForm.auditRemark
    })
    
    ElMessage.success('审核成功')
    auditDialogVisible.value = false
    loadRechargeRecords()
  } catch (error) {
    console.error('审核失败:', error)
    ElMessage.error('审核失败: ' + (error.response?.data?.message || error.message))
  } finally {
    auditLoading.value = false
  }
}

const getStatusType = (status) => {
  const statusTypes = {
    0: 'warning',  // 待审核
    1: 'success',  // 审核通过
    2: 'danger'    // 审核拒绝
  }
  return statusTypes[status] || 'info'
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadRechargeRecords()
})
</script>

<style lang="scss" scoped>
.recharge-management {
  padding: 20px;
  
  .page-header {
    margin-bottom: 20px;
    
    h1 {
      margin: 0 0 8px 0;
      font-size: 24px;
      color: #303133;
    }
    
    p {
      margin: 0;
      color: #909399;
      font-size: 14px;
    }
  }
  
  .search-card {
    margin-bottom: 20px;
  }
  
  .table-card {
    .amount {
      font-weight: bold;
      color: #f56c6c;
    }
    
    .pagination-container {
      margin-top: 20px;
      text-align: right;
    }
  }
  
  .detail-content {
    .detail-section {
      margin-top: 20px;
      
      h4 {
        margin: 0 0 10px 0;
        font-size: 14px;
        color: #303133;
      }
      
      p {
        margin: 0;
        color: #606266;
        line-height: 1.5;
      }
    }
    
    .amount {
      font-weight: bold;
      color: #f56c6c;
      font-size: 16px;
    }
  }
}
</style>
