<template>
  <div class="book-room">
    <div class="container">
      <div class="booking-header">
        <el-button @click="goBack" icon="ArrowLeft">返回房间列表</el-button>
        <h1>预订房间</h1>
      </div>

      <div class="booking-content">
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="8" animated />
        </div>
        
        <el-card v-else class="booking-card">
          <!-- 房间图片区域 -->
          <div class="room-image-section" @mouseenter="showImageControls = true" @mouseleave="showImageControls = false">
            <el-image 
              v-if="roomInfo.currentImage" 
              :src="roomInfo.currentImage" 
              fit="cover"
              class="room-main-image"
            >
              <template #error>
                <div class="image-placeholder">
                  <el-icon size="120"><Picture /></el-icon>
                  <p>房间图片</p>
                </div>
              </template>
            </el-image>
            <div v-else class="image-placeholder">
              <el-icon size="120"><Picture /></el-icon>
              <p>房间图片</p>
            </div>
            
            <!-- 图片轮播控制 -->
            <div v-if="roomInfo.imageList && roomInfo.imageList.length > 1" class="image-controls">
              <!-- 左右切换按钮 -->
              <div 
                v-show="showImageControls" 
                class="image-nav prev-btn" 
                @click="prevBookingImage"
              >
                <el-icon><ArrowLeft /></el-icon>
              </div>
              <div 
                v-show="showImageControls" 
                class="image-nav next-btn" 
                @click="nextBookingImage"
              >
                <el-icon><ArrowRight /></el-icon>
              </div>
              
              <!-- 图片指示器 -->
              <div class="image-indicators">
                <span 
                  v-for="(img, index) in roomInfo.imageList" 
                  :key="index"
                  class="indicator"
                  :class="{ active: index === roomInfo.currentImageIndex }"
                  @click="setBookingImage(index)"
                ></span>
              </div>
            </div>
          </div>

          <!-- 主要内容区域 -->
          <div class="main-content">
            <!-- 左侧：房间信息 + 预订表单 + 房间设施 -->
            <div class="left-section">
              <!-- 房间基本信息 -->
              <div class="room-info-section">
                <h2>{{ roomInfo.name }}</h2>
                <p class="room-description">{{ roomInfo.description }}</p>
                
                <div class="room-specs">
                  <div class="spec-item">
                    <el-icon><User /></el-icon>
                    <span>最多{{ roomInfo.maxGuests }}人</span>
                  </div>
                  <div class="spec-item">
                    <el-icon><Expand /></el-icon>
                    <span>{{ roomInfo.area }}㎡</span>
                  </div>
                  <div class="spec-item" v-if="roomInfo.bedType">
                    <span>{{ roomInfo.bedType }}</span>
                  </div>
                </div>
              </div>

              <!-- 预订表单 -->
              <div class="booking-form-section">
                <h3>预订信息</h3>
                <el-form :model="bookingForm" :rules="bookingRules" ref="bookingFormRef" label-width="80px">
                  <el-row :gutter="16">
                    <el-col :span="12">
                      <el-form-item label="入住日期" prop="checkIn">
                        <el-date-picker
                          v-model="bookingForm.checkIn"
                          type="date"
                          placeholder="选择入住日期"
                          style="width: 100%"
                          :disabled-date="disabledDate"
                        />
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item label="退房日期" prop="checkOut">
                        <el-date-picker
                          v-model="bookingForm.checkOut"
                          type="date"
                          placeholder="选择退房日期"
                          style="width: 100%"
                          :disabled-date="disabledCheckOutDate"
                        />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  
                  <el-row :gutter="16">
                    <el-col :span="12">
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
                    </el-col>
                  </el-row>
                  
                  <el-row :gutter="16">
                    <el-col :span="12">
                      <el-form-item label="联系人" prop="contactName">
                        <el-input v-model="bookingForm.contactName" placeholder="请输入联系人姓名" />
                      </el-form-item>
                    </el-col>
                    <el-col :span="12">
                      <el-form-item label="联系电话" prop="contactPhone">
                        <el-input v-model="bookingForm.contactPhone" placeholder="请输入联系电话" />
                      </el-form-item>
                    </el-col>
                  </el-row>
                  
                  <el-form-item label="特殊要求">
                    <el-input
                      v-model="bookingForm.specialRequests"
                      type="textarea"
                      :rows="3"
                      placeholder="如有特殊要求请在此说明（可选）"
                    />
                  </el-form-item>
                </el-form>
              </div>

              <!-- 房间设施 -->
              <div class="room-facilities-section" v-if="roomInfo.features && roomInfo.features.length > 0">
                <h4>房间设施</h4>
                <div class="facilities-grid">
                  <div class="facility-category" v-for="category in facilitiesCategories" :key="category.name">
                    <h5 class="category-title">{{ category.name }}</h5>
                    <div class="facility-items">
                      <div 
                        v-for="facility in category.items" 
                        :key="facility.name"
                        class="facility-item"
                        :class="{ 'available': facility.available, 'unavailable': !facility.available }"
                      >
                        <el-icon class="facility-icon">
                          <Check v-if="facility.available" />
                          <Close v-else />
                        </el-icon>
                        <span class="facility-name">{{ facility.name }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- 右侧：价格明细 -->
            <div class="right-section">
              <div class="price-card">
                <h3>价格明细</h3>
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
              </div>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture, User, Expand, ArrowLeft, Check, Close } from '@element-plus/icons-vue'
import { getUserRoomById } from '@/api/modules/userRoom'

const router = useRouter()
const route = useRoute()

// 响应式数据
const bookingFormRef = ref()
const submitting = ref(false)
const loading = ref(true)
const showImageControls = ref(false)

// 房间信息（从API获取）
const roomInfo = ref({
  id: null,
  name: '',
  description: '',
  price: 0,
  maxGuests: 1,
  area: 0,
  features: []
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

// 设施分类
const facilitiesCategories = computed(() => {
  const allFacilities = {
    '基础': [
      '无线网络', '电梯', '落地窗', '卧室-冷暖空调', '客厅-冷暖空调', '暖气',
      '晾衣架', '电热水壶', '沙发', '电视', '冰箱', '洗衣机',
      '空气净化器', '加湿器', '净水机'
    ],
    '卫浴': [
      '热水', '独立卫浴', '电吹风', '洗浴用品', '牙具', '浴巾',
      '毛巾', '浴缸', '智能马桶', '干湿分离'
    ],
    '厨房': [
      '微波炉', '餐具', '刀具菜板', '烹饪锅具', '电磁炉', '燃气灶',
      '洗涤用品', '电饭煲', '饮水机', '餐桌'
    ]
  }
  
  const userFacilities = roomInfo.value.features || []
  
  return Object.entries(allFacilities).map(([categoryName, facilities]) => ({
    name: categoryName,
    items: facilities.map(facility => ({
      name: facility,
      available: userFacilities.includes(facility)
    }))
  }))
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

// 预订页面图片轮播控制函数
const prevBookingImage = () => {
  if (roomInfo.value.imageList && roomInfo.value.imageList.length > 1) {
    roomInfo.value.currentImageIndex = roomInfo.value.currentImageIndex > 0 
      ? roomInfo.value.currentImageIndex - 1 
      : roomInfo.value.imageList.length - 1
    roomInfo.value.currentImage = roomInfo.value.imageList[roomInfo.value.currentImageIndex]
  }
}

const nextBookingImage = () => {
  if (roomInfo.value.imageList && roomInfo.value.imageList.length > 1) {
    roomInfo.value.currentImageIndex = roomInfo.value.currentImageIndex < roomInfo.value.imageList.length - 1 
      ? roomInfo.value.currentImageIndex + 1 
      : 0
    roomInfo.value.currentImage = roomInfo.value.imageList[roomInfo.value.currentImageIndex]
  }
}

const setBookingImage = (index) => {
  if (roomInfo.value.imageList && roomInfo.value.imageList.length > index) {
    roomInfo.value.currentImageIndex = index
    roomInfo.value.currentImage = roomInfo.value.imageList[index]
  }
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

onMounted(async () => {
  // 根据路由参数获取房间信息
  const roomId = route.params.id
  
  if (!roomId) {
    ElMessage.error('房间ID不存在')
    router.push('/rooms')
    return
  }
  
  try {
    loading.value = true
    console.log('正在获取房间详情，房间ID:', roomId)
    
    const response = await getUserRoomById(roomId)
    const roomData = response.data
    
    console.log('房间数据:', roomData)
    
    // 处理设施数据
    let facilitiesArray = []
    if (roomData.facilities && typeof roomData.facilities === 'string') {
      try {
        facilitiesArray = JSON.parse(roomData.facilities)
      } catch (e) {
        facilitiesArray = []
      }
    } else if (Array.isArray(roomData.facilities)) {
      facilitiesArray = roomData.facilities
    }
    
    // 处理图片数据
    let imageList = []
    let currentImage = null
    if (roomData.images && typeof roomData.images === 'string') {
      try {
        imageList = JSON.parse(roomData.images)
        currentImage = imageList.length > 0 ? imageList[0] : null
      } catch (e) {
        imageList = []
        currentImage = null
      }
    }
    
    // 更新房间信息
    roomInfo.value = {
      id: roomData.id,
      name: roomData.roomNumber + '号房 - ' + (roomData.roomTypeName || '标准房'),
      description: roomData.description || '舒适温馨的房间，为您提供优质的住宿体验',
      price: roomData.currentPrice || roomData.price || 0,
      maxGuests: roomData.maxGuests || 2,
      area: roomData.area || 0,
      features: facilitiesArray,
      imageList: imageList,
      currentImage: currentImage,
      currentImageIndex: 0,
      bedType: roomData.bedType
    }
    
  } catch (error) {
    console.error('获取房间详情失败:', error)
    ElMessage.error('获取房间信息失败')
    router.push('/rooms')
  } finally {
    loading.value = false
  }
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
  .loading-container {
    padding: 60px 20px;
    text-align: center;
  }
  
  .booking-card {
    max-width: 1200px;
    margin: 0 auto;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
  }
}

// 房间图片区域
.room-image-section {
  width: 100%;
  height: 300px;
  position: relative;
  overflow: hidden;
  
  .room-main-image {
    width: 100%;
    height: 100%;
  }
  
  .image-placeholder {
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #666;
    
    p {
      margin: 12px 0 0 0;
      font-size: 16px;
    }
  }
  
  .image-controls {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    pointer-events: none;
    
    .image-nav {
      position: absolute;
      top: 50%;
      transform: translateY(-50%);
      width: 40px;
      height: 40px;
      background: rgba(0, 0, 0, 0.6);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      cursor: pointer;
      pointer-events: auto;
      transition: all 0.3s ease;
      z-index: 2;
      
      &:hover {
        background: rgba(0, 0, 0, 0.8);
        transform: translateY(-50%) scale(1.1);
      }
      
      &.prev-btn {
        left: 16px;
      }
      
      &.next-btn {
        right: 16px;
      }
    }
    
    .image-indicators {
      position: absolute;
      bottom: 16px;
      left: 50%;
      transform: translateX(-50%);
      display: flex;
      gap: 8px;
      pointer-events: auto;
      
      .indicator {
        width: 12px;
        height: 12px;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.5);
        cursor: pointer;
        transition: all 0.3s ease;
        
        &.active {
          background: white;
          transform: scale(1.2);
        }
        
        &:hover {
          background: rgba(255, 255, 255, 0.8);
        }
      }
    }
  }
}

// 主要内容区域
.main-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 40px;
  padding: 30px;
}

// 左侧区域
.left-section {
  .room-info-section {
    margin-bottom: 30px;
    
    h2 {
      margin: 0 0 16px 0;
      font-size: 28px;
      color: #333;
      font-weight: 600;
    }
    
    .room-description {
      margin: 0 0 20px 0;
      color: #666;
      line-height: 1.6;
      font-size: 16px;
    }
    
    .room-specs {
      display: flex;
      gap: 24px;
      margin-bottom: 0;
      
      .spec-item {
        display: flex;
        align-items: center;
        gap: 6px;
        color: #666;
        font-size: 15px;
        
        .el-icon {
          color: #409eff;
        }
      }
    }
  }
  
  .booking-form-section {
    background: #f8fffe;
    border: 2px solid #10b981;
    border-radius: 12px;
    padding: 24px;
    margin: 30px 0;
    
    h3 {
      margin: 0 0 20px 0;
      font-size: 22px;
      color: #10b981;
      font-weight: 600;
      display: flex;
      align-items: center;
      gap: 8px;
      
      &::before {
        content: "📅";
        font-size: 20px;
      }
    }
    
    .el-form {
      .el-form-item {
        margin-bottom: 18px;
      }
      
      .el-form-item__label {
        font-weight: 500;
        color: #333;
      }
    }
  }
  
  .room-facilities-section {
    margin-top: 30px;
    
    h4 {
      margin: 0 0 20px 0;
      font-size: 18px;
      color: #333;
      font-weight: 600;
    }
    
    .facilities-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
      gap: 24px;
    }
    
    .facility-category {
      .category-title {
        margin: 0 0 16px 0;
        font-size: 16px;
        color: #333;
        font-weight: 600;
        padding-bottom: 8px;
        border-bottom: 2px solid #f0f0f0;
      }
      
      .facility-items {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(140px, 1fr));
        gap: 12px;
      }
      
      .facility-item {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 8px 12px;
        border-radius: 8px;
        transition: all 0.2s ease;
        
        &.available {
          background: #f0f9ff;
          border: 1px solid #e0f2fe;
          
          .facility-icon {
            color: #10b981;
          }
          
          .facility-name {
            color: #333;
          }
          
          &:hover {
            background: #e0f2fe;
          }
        }
        
        &.unavailable {
          background: #fafafa;
          border: 1px solid #f0f0f0;
          
          .facility-icon {
            color: #d1d5db;
          }
          
          .facility-name {
            color: #9ca3af;
          }
        }
        
        .facility-icon {
          font-size: 16px;
          flex-shrink: 0;
        }
        
        .facility-name {
          font-size: 14px;
          line-height: 1.4;
        }
      }
    }
  }
}

// 右侧区域
.right-section {
  .price-card {
    background: #f8f9fa;
    border-radius: 12px;
    padding: 24px;
    position: sticky;
    top: 20px;
    
    h3 {
      margin: 0 0 20px 0;
      font-size: 18px;
      color: #333;
      font-weight: 600;
    }
    
    .price-details {
      .price-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid #e9ecef;
        
        &.subtotal {
          font-weight: 500;
          border-bottom: 2px solid #dee2e6;
          margin-top: 8px;
        }
        
        &.total {
          font-size: 20px;
          font-weight: bold;
          color: #e74c3c;
          border-bottom: none;
          padding-top: 16px;
          margin-top: 8px;
        }
      }
    }
    
    .booking-actions {
      margin-top: 30px;
      display: flex;
      flex-direction: column;
      gap: 12px;
      
      .el-button {
        width: 100%;
        height: 44px;
        font-size: 16px;
        font-weight: 500;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .container {
    padding: 16px;
  }
  
  .booking-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
    
    h1 {
      font-size: 24px;
    }
  }
  
  .room-image-section {
    height: 250px;
  }
  
  .main-content {
    grid-template-columns: 1fr;
    gap: 20px;
    padding: 20px;
  }
  
  .left-section {
    .room-info-section {
      margin-bottom: 30px;
      
      h2 {
        font-size: 24px;
      }
      
      .room-specs {
        flex-wrap: wrap;
        gap: 16px;
      }
      
      .room-facilities {
        .facilities-grid {
          grid-template-columns: 1fr;
          gap: 20px;
        }
        
        .facility-items {
          grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
          gap: 8px;
        }
      }
    }
  }
  
  .right-section {
    .price-card {
      position: static;
      margin-top: 20px;
    }
  }
}

@media (max-width: 480px) {
  .booking-card {
    margin: 0 -16px;
    border-radius: 0;
  }
  
  .main-content {
    padding: 16px;
  }
  
  .room-specs {
    flex-direction: column !important;
    gap: 12px !important;
  }
}
</style>
