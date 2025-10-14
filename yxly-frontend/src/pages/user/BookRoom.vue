<template>
  <div class="book-room">
    <div class="container">
      <div class="booking-header">
        <el-button @click="goBack" icon="ArrowLeft">返回房间列表</el-button>
        <h1>预订房间</h1>
      </div>

      <div class="booking-content">
        <!-- 房间信息 -->
        <div class="room-section">
          <el-card>
            <template #header>
              <h2>房间信息</h2>
            </template>
            <div class="room-info">
              <div class="room-image">
                <div class="image-placeholder">
                  <el-icon size="80"><Picture /></el-icon>
                  <p>房间图片</p>
                </div>
              </div>
              <div class="room-details">
                <h3>{{ roomInfo.name }}</h3>
                <p class="description">{{ roomInfo.description }}</p>
                <div class="features">
                  <el-tag v-for="feature in roomInfo.features" :key="feature" size="small">
                    {{ feature }}
                  </el-tag>
                </div>
                <div class="room-specs">
                  <span><el-icon><User /></el-icon> 最多{{ roomInfo.maxGuests }}人</span>
                  <span><el-icon><Expand /></el-icon> {{ roomInfo.area }}㎡</span>
                </div>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 预订表单 -->
        <div class="booking-section">
          <el-card>
            <template #header>
              <h2>预订信息</h2>
            </template>
            <el-form :model="bookingForm" :rules="bookingRules" ref="bookingFormRef" label-width="100px">
              <el-form-item label="入住日期" prop="checkIn">
                <el-date-picker
                  v-model="bookingForm.checkIn"
                  type="date"
                  placeholder="选择入住日期"
                  style="width: 100%"
                  :disabled-date="disabledDate"
                />
              </el-form-item>
              
              <el-form-item label="退房日期" prop="checkOut">
                <el-date-picker
                  v-model="bookingForm.checkOut"
                  type="date"
                  placeholder="选择退房日期"
                  style="width: 100%"
                  :disabled-date="disabledCheckOutDate"
                />
              </el-form-item>
              
              <el-form-item label="入住人数" prop="guests">
                <el-select v-model="bookingForm.guests" placeholder="选择人数" style="width: 100%">
                  <el-option 
                    v-for="i in roomInfo.maxGuests" 
                    :key="i" 
                    :label="`${i}人`" 
                    :value="i" 
                  />
                </el-select>
              </el-form-item>
              
              <el-form-item label="联系人" prop="contactName">
                <el-input v-model="bookingForm.contactName" placeholder="请输入联系人姓名" />
              </el-form-item>
              
              <el-form-item label="联系电话" prop="contactPhone">
                <el-input v-model="bookingForm.contactPhone" placeholder="请输入联系电话" />
              </el-form-item>
              
              <el-form-item label="特殊要求">
                <el-input
                  v-model="bookingForm.specialRequests"
                  type="textarea"
                  :rows="3"
                  placeholder="如有特殊要求请在此说明（可选）"
                />
              </el-form-item>
            </el-form>
          </el-card>
        </div>

        <!-- 价格明细 -->
        <div class="price-section">
          <el-card>
            <template #header>
              <h2>价格明细</h2>
            </template>
            <div class="price-details">
              <div class="price-item">
                <span>房间单价</span>
                <span>￥{{ roomInfo.price }}/晚</span>
              </div>
              <div class="price-item">
                <span>入住天数</span>
                <span>{{ nights }}晚</span>
              </div>
              <div class="price-item subtotal">
                <span>小计</span>
                <span>￥{{ subtotal }}</span>
              </div>
              <div class="price-item total">
                <span>总计</span>
                <span>￥{{ totalAmount }}</span>
              </div>
            </div>
            
            <div class="booking-actions">
              <el-button size="large" @click="goBack">取消</el-button>
              <el-button 
                type="primary" 
                size="large" 
                @click="submitBooking"
                :loading="submitting"
              >
                确认预订
              </el-button>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture, User, Expand, ArrowLeft } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

// 响应式数据
const bookingFormRef = ref()
const submitting = ref(false)

// 房间信息（模拟数据）
const roomInfo = ref({
  id: 1,
  name: '豪华海景套房',
  description: '180度海景视野，配备独立阳台和按摩浴缸，是您度假的完美选择',
  price: 588,
  maxGuests: 2,
  area: 45,
  features: ['海景', '阳台', '按摩浴缸', '免费WiFi', '免费早餐']
})

// 预订表单
const bookingForm = reactive({
  checkIn: route.query.checkIn || '',
  checkOut: route.query.checkOut || '',
  guests: route.query.guests || 1,
  contactName: '',
  contactPhone: '',
  specialRequests: ''
})

// 表单验证规则
const bookingRules = {
  checkIn: [
    { required: true, message: '请选择入住日期', trigger: 'change' }
  ],
  checkOut: [
    { required: true, message: '请选择退房日期', trigger: 'change' }
  ],
  guests: [
    { required: true, message: '请选择入住人数', trigger: 'change' }
  ],
  contactName: [
    { required: true, message: '请输入联系人姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度在2-20个字符', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 计算属性
const nights = computed(() => {
  if (!bookingForm.checkIn || !bookingForm.checkOut) return 0
  const checkIn = new Date(bookingForm.checkIn)
  const checkOut = new Date(bookingForm.checkOut)
  const diffTime = checkOut - checkIn
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24))
})

const subtotal = computed(() => {
  return roomInfo.value.price * nights.value
})

const totalAmount = computed(() => {
  // 这里可以加上其他费用，如服务费、税费等
  return subtotal.value
})

// 日期禁用逻辑
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

const disabledCheckOutDate = (time) => {
  if (!bookingForm.checkIn) return time.getTime() < Date.now() - 8.64e7
  return time.getTime() < new Date(bookingForm.checkIn).getTime() + 8.64e7
}

// 返回上一页
const goBack = () => {
  router.go(-1)
}

// 提交预订
const submitBooking = async () => {
  if (!bookingFormRef.value) return
  
  try {
    await bookingFormRef.value.validate()
    
    if (nights.value <= 0) {
      ElMessage.error('退房日期必须晚于入住日期')
      return
    }
    
    await ElMessageBox.confirm(
      `确认预订 ${roomInfo.value.name}？\n入住时间：${bookingForm.checkIn} 至 ${bookingForm.checkOut}\n总费用：￥${totalAmount.value}`,
      '确认预订',
      {
        confirmButtonText: '确认预订',
        cancelButtonText: '取消',
        type: 'info'
      }
    )
    
    submitting.value = true
    
    // 模拟提交预订
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    ElMessage.success('预订成功！我们会尽快与您联系确认')
    
    // 跳转到订单页面或首页
    router.push('/orders')
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('预订失败:', error)
      ElMessage.error('预订失败，请重试')
    }
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  // 根据路由参数获取房间信息
  const roomId = route.params.id
  
  // 这里可以调用API获取房间详情
  // 暂时使用模拟数据
  console.log('房间ID:', roomId)
})
</script>

<style lang="scss" scoped>
.book-room {
  .container {
    max-width: 1000px;
    margin: 0 auto;
    padding: 20px;
  }
}

.booking-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
  
  h1 {
    margin: 0;
    font-size: 28px;
    color: #333;
  }
}

.booking-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
  align-items: start;
}

.room-section {
  .room-info {
    display: flex;
    gap: 20px;
    
    .room-image {
      flex-shrink: 0;
      width: 200px;
      height: 150px;
      
      .image-placeholder {
        width: 100%;
        height: 100%;
        background: #f5f5f5;
        border-radius: 8px;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        color: #999;
        
        p {
          margin: 8px 0 0 0;
          font-size: 14px;
        }
      }
    }
    
    .room-details {
      flex: 1;
      
      h3 {
        margin: 0 0 12px 0;
        font-size: 20px;
        color: #333;
      }
      
      .description {
        margin: 0 0 16px 0;
        color: #666;
        line-height: 1.6;
      }
      
      .features {
        margin-bottom: 16px;
        
        .el-tag {
          margin: 0 8px 8px 0;
        }
      }
      
      .room-specs {
        display: flex;
        gap: 20px;
        color: #666;
        font-size: 14px;
        
        span {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }
  }
}

.booking-section {
  margin-bottom: 30px;
}

.price-section {
  .price-details {
    .price-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid #f0f0f0;
      
      &.subtotal {
        font-weight: 500;
        border-bottom: 2px solid #e0e0e0;
      }
      
      &.total {
        font-size: 18px;
        font-weight: bold;
        color: #e74c3c;
        border-bottom: none;
        padding-top: 16px;
      }
    }
  }
  
  .booking-actions {
    margin-top: 30px;
    display: flex;
    gap: 16px;
    
    .el-button {
      flex: 1;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .booking-content {
    grid-template-columns: 1fr;
  }
  
  .room-info {
    flex-direction: column !important;
    
    .room-image {
      width: 100% !important;
      height: 200px !important;
    }
  }
  
  .booking-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>
