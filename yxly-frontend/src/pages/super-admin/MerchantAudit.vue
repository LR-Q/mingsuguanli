<template>
  <div class="merchant-audit-container">
    <el-card class="header-card">
      <h2>商户审核管理</h2>
      <p>审核民宿主管理员的注册申请</p>
    </el-card>

    <!-- 筛选条件 -->
    <el-card class="filter-card">
      <el-form :inline="true">
        <el-form-item label="审核状态">
          <el-select v-model="filterStatus" placeholder="全部" size="large" @change="loadMerchants" style="width: 150px;">
            <el-option label="全部" :value="null" />
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" @click="loadMerchants">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 商户列表 -->
    <el-card class="table-card">
      <el-table :data="merchantList" v-loading="loading" border>
        <el-table-column prop="id" label="商户ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column label="身份证照片" width="180">
          <template #default="{ row }">
            <div class="id-card-preview">
              <el-image
                v-if="row.idCardFront"
                :src="row.idCardFront"
                :preview-src-list="[row.idCardFront]"
                style="width: 60px; height: 40px; margin-right: 5px; cursor: pointer;"
                fit="cover"
              >
                <template #error>
                  <div class="image-error">正面</div>
                </template>
              </el-image>
              <el-image
                v-if="row.idCardBack"
                :src="row.idCardBack"
                :preview-src-list="[row.idCardBack]"
                style="width: 60px; height: 40px; cursor: pointer;"
                fit="cover"
              >
                <template #error>
                  <div class="image-error">反面</div>
                </template>
              </el-image>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="审核状态" width="140" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.auditStatus === 0" type="warning" size="large" effect="plain">待审核</el-tag>
            <el-tag v-else-if="row.auditStatus === 1" type="success" size="large" effect="plain">已通过</el-tag>
            <el-tag v-else-if="row.auditStatus === 2" type="danger" size="large" effect="plain">已拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="auditRemarks" label="审核备注" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div style="display: flex; gap: 8px; justify-content: center;">
              <el-button
                size="small"
                @click="handleViewDetail(row)"
              >
                详情
              </el-button>
              <el-button
                v-if="row.auditStatus === 0"
                type="success"
                size="small"
                @click="handleAudit(row, 1)"
              >
                通过
              </el-button>
              <el-button
                v-if="row.auditStatus === 0"
                type="danger"
                size="small"
                @click="handleAudit(row, 2)"
              >
                拒绝
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 审核对话框 -->
    <el-dialog
      v-model="auditDialogVisible"
      :title="auditForm.auditStatus === 1 ? '审核通过' : '审核拒绝'"
      width="500px"
    >
      <el-form :model="auditForm" label-width="100px">
        <el-form-item label="商户ID">
          <span>{{ auditForm.merchantId }}</span>
        </el-form-item>
        <el-form-item label="真实姓名">
          <span>{{ currentMerchant?.realName }}</span>
        </el-form-item>
        <el-form-item label="审核备注">
          <el-input
            v-model="auditForm.auditRemarks"
            type="textarea"
            :rows="4"
            :placeholder="auditForm.auditStatus === 1 ? '请填写审核通过的备注（可选）' : '请填写拒绝原因'"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAudit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="商户详情"
      width="600px"
    >
      <el-descriptions :column="2" border v-if="currentMerchant">
        <el-descriptions-item label="商户ID">{{ currentMerchant.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentMerchant.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ currentMerchant.realName }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentMerchant.phone }}</el-descriptions-item>
        <el-descriptions-item label="申请时间" :span="2">
          {{ formatDateTime(currentMerchant.applyTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag v-if="currentMerchant.auditStatus === 0" type="warning" size="large" effect="plain">待审核</el-tag>
          <el-tag v-else-if="currentMerchant.auditStatus === 1" type="success" size="large" effect="plain">已通过</el-tag>
          <el-tag v-else-if="currentMerchant.auditStatus === 2" type="danger" size="large" effect="plain">已拒绝</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核时间">
          {{ currentMerchant.auditTime ? formatDateTime(currentMerchant.auditTime) : '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="审核备注" :span="2">
          {{ currentMerchant.auditRemarks || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="身份证正面" :span="2">
          <el-image
            v-if="currentMerchant.idCardFront"
            :src="currentMerchant.idCardFront"
            :preview-src-list="[currentMerchant.idCardFront]"
            style="width: 200px; cursor: pointer;"
            fit="contain"
          />
        </el-descriptions-item>
        <el-descriptions-item label="身份证反面" :span="2">
          <el-image
            v-if="currentMerchant.idCardBack"
            :src="currentMerchant.idCardBack"
            :preview-src-list="[currentMerchant.idCardBack]"
            style="width: 200px; cursor: pointer;"
            fit="contain"
          />
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllMerchants, auditMerchant } from '@/api/modules/superAdmin'
import dayjs from 'dayjs'

// 状态
const loading = ref(false)
const submitLoading = ref(false)
const merchantList = ref([])
const filterStatus = ref(null)
const auditDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentMerchant = ref(null)

// 审核表单
const auditForm = ref({
  merchantId: null,
  auditStatus: null,
  auditRemarks: ''
})

// 加载商户列表
const loadMerchants = async () => {
  loading.value = true
  try {
    const response = await getAllMerchants(filterStatus.value)
    merchantList.value = response.data || []
  } catch (error) {
    console.error('加载商户列表失败:', error)
    ElMessage.error(error.message || '加载失败')
  } finally {
    loading.value = false
  }
}

// 处理审核
const handleAudit = (row, status) => {
  currentMerchant.value = row
  auditForm.value = {
    merchantId: row.id,
    auditStatus: status,
    auditRemarks: ''
  }
  auditDialogVisible.value = true
}

// 确认审核
const confirmAudit = async () => {
  if (auditForm.value.auditStatus === 2 && !auditForm.value.auditRemarks) {
    ElMessage.warning('请填写拒绝原因')
    return
  }

  submitLoading.value = true
  try {
    await auditMerchant(auditForm.value)
    ElMessage.success('审核成功')
    auditDialogVisible.value = false
    loadMerchants()
  } catch (error) {
    console.error('审核失败:', error)
    ElMessage.error(error.message || '审核失败')
  } finally {
    submitLoading.value = false
  }
}

// 查看详情
const handleViewDetail = (row) => {
  currentMerchant.value = row
  detailDialogVisible.value = true
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return dayjs(dateTime).format('YYYY-MM-DD HH:mm:ss')
}

// 页面加载时获取数据
onMounted(() => {
  loadMerchants()
})
</script>

<style scoped lang="scss">
.merchant-audit-container {
  padding: 20px;
  
  .header-card {
    margin-bottom: 20px;
    
    h2 {
      margin: 0 0 10px 0;
      font-size: 24px;
      color: #303133;
    }
    
    p {
      margin: 0;
      color: #909399;
      font-size: 14px;
    }
  }
  
  .filter-card {
    margin-bottom: 20px;
  }
  
  .table-card {
    .id-card-preview {
      display: flex;
      align-items: center;
      
      .image-error {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 60px;
        height: 40px;
        background: #f5f7fa;
        color: #909399;
        font-size: 12px;
        border-radius: 4px;
      }
    }
    
    // 增大审核状态标签
    :deep(.el-tag--large) {
      padding: 8px 16px;
      font-size: 14px;
      font-weight: 500;
      border-radius: 6px;
      min-width: 80px;
      text-align: center;
    }
  }
}
</style>
