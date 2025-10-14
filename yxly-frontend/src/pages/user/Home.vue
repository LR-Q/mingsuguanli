<template>
  <div class="user-home">
    <!-- 英雄区域 -->
    <section class="hero-section">
      <div class="hero-content">
        <div class="hero-text">
          <h1>欢迎来到悦鑫乐怡民宿</h1>
          <p>体验家的温暖，享受旅途的美好</p>
          <div class="hero-actions">
            <el-button type="primary" size="large" @click="scrollToRooms">
              立即预订
            </el-button>
            <el-button size="large" @click="scrollToAbout">
              了解更多
            </el-button>
          </div>
        </div>
        <div class="hero-image">
          <div class="image-placeholder">
            <el-icon size="80"><House /></el-icon>
            <p>精美民宿图片</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 快速预订区域 -->
    <section class="booking-section" ref="roomsSection">
      <div class="container">
        <h2>快速预订</h2>
        <div class="booking-form">
          <el-form :model="bookingForm" inline>
            <el-form-item label="入住日期">
              <el-date-picker
                v-model="bookingForm.checkIn"
                type="date"
                placeholder="选择入住日期"
                :disabled-date="disabledDate"
              />
            </el-form-item>
            <el-form-item label="退房日期">
              <el-date-picker
                v-model="bookingForm.checkOut"
                type="date"
                placeholder="选择退房日期"
                :disabled-date="disabledCheckOutDate"
              />
            </el-form-item>
            <el-form-item label="入住人数">
              <el-select v-model="bookingForm.guests" placeholder="选择人数">
                <el-option label="1人" :value="1" />
                <el-option label="2人" :value="2" />
                <el-option label="3人" :value="3" />
                <el-option label="4人" :value="4" />
                <el-option label="5人以上" :value="5" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" size="large" @click="searchRooms">
                搜索房间
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </section>

    <!-- 特色房型展示 -->
    <section class="rooms-showcase">
      <div class="container">
        <h2>精选房型</h2>
        <div class="rooms-grid">
          <div v-for="room in featuredRooms" :key="room.id" class="room-card">
            <div class="room-image">
              <div class="image-placeholder">
                <el-icon size="40"><Picture /></el-icon>
              </div>
              <div class="room-badge">{{ room.badge }}</div>
            </div>
            <div class="room-info">
              <h3>{{ room.name }}</h3>
              <p class="room-desc">{{ room.description }}</p>
              <div class="room-features">
                <span v-for="feature in room.features" :key="feature" class="feature-tag">
                  {{ feature }}
                </span>
              </div>
              <div class="room-footer">
                <div class="price">
                  <span class="price-label">￥</span>
                  <span class="price-value">{{ room.price }}</span>
                  <span class="price-unit">/晚</span>
                </div>
                <el-button type="primary" @click="bookRoom(room)">
                  立即预订
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 服务特色 -->
    <section class="features-section" ref="aboutSection">
      <div class="container">
        <h2>为什么选择我们</h2>
        <div class="features-grid">
          <div v-for="feature in features" :key="feature.id" class="feature-card">
            <div class="feature-icon">
              <el-icon size="48" :color="feature.color">
                <component :is="feature.icon" />
              </el-icon>
            </div>
            <h3>{{ feature.title }}</h3>
            <p>{{ feature.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 客户评价 -->
    <section class="reviews-section">
      <div class="container">
        <h2>客户评价</h2>
        <div class="reviews-grid">
          <div v-for="review in reviews" :key="review.id" class="review-card">
            <div class="review-header">
              <el-avatar :size="50">{{ review.name.charAt(0) }}</el-avatar>
              <div class="review-info">
                <h4>{{ review.name }}</h4>
                <el-rate v-model="review.rating" disabled show-score />
              </div>
            </div>
            <p class="review-content">{{ review.content }}</p>
            <div class="review-date">{{ review.date }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 联系我们 -->
    <section class="contact-section">
      <div class="container">
        <div class="contact-content">
          <div class="contact-info">
            <h2>联系我们</h2>
            <div class="contact-item">
              <el-icon><Phone /></el-icon>
              <span>400-123-4567</span>
            </div>
            <div class="contact-item">
              <el-icon><Message /></el-icon>
              <span>info@yxly.com</span>
            </div>
            <div class="contact-item">
              <el-icon><Location /></el-icon>
              <span>某某市某某区某某街道123号</span>
            </div>
          </div>
          <div class="contact-form">
            <h3>在线咨询</h3>
            <el-form :model="contactForm" label-width="80px">
              <el-form-item label="姓名">
                <el-input v-model="contactForm.name" placeholder="请输入您的姓名" />
              </el-form-item>
              <el-form-item label="电话">
                <el-input v-model="contactForm.phone" placeholder="请输入您的电话" />
              </el-form-item>
              <el-form-item label="留言">
                <el-input
                  v-model="contactForm.message"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入您的留言"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="submitContact">提交咨询</el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  House,
  Picture,
  Star,
  Shield,
  Service,
  Phone,
  Message,
  Location
} from '@element-plus/icons-vue'

const router = useRouter()

// 引用
const roomsSection = ref()
const aboutSection = ref()

// 表单数据
const bookingForm = reactive({
  checkIn: '',
  checkOut: '',
  guests: 2
})

const contactForm = reactive({
  name: '',
  phone: '',
  message: ''
})

// 精选房型数据
const featuredRooms = ref([
  {
    id: 1,
    name: '豪华海景套房',
    description: '180度海景视野，配备独立阳台和按摩浴缸',
    price: 588,
    badge: '热门',
    features: ['海景', '阳台', '按摩浴缸', '免费WiFi']
  },
  {
    id: 2,
    name: '温馨家庭房',
    description: '适合家庭入住，配备儿童设施和游戏区',
    price: 388,
    badge: '家庭首选',
    features: ['家庭房', '儿童设施', '游戏区', '免费早餐']
  },
  {
    id: 3,
    name: '商务标准间',
    description: '商务人士首选，配备办公桌和高速网络',
    price: 288,
    badge: '商务',
    features: ['办公桌', '高速网络', '商务中心', '24小时服务']
  }
])

// 服务特色数据
const features = ref([
  {
    id: 1,
    icon: 'Star',
    title: '五星服务',
    description: '专业的服务团队，24小时为您提供贴心服务',
    color: '#f39c12'
  },
  {
    id: 2,
    icon: 'Shield',
    title: '安全保障',
    description: '完善的安全设施，让您住得安心放心',
    color: '#27ae60'
  },
  {
    id: 3,
    icon: 'Service',
    title: '便民服务',
    description: '提供接送、导游、餐饮等一站式便民服务',
    color: '#3498db'
  }
])

// 客户评价数据
const reviews = ref([
  {
    id: 1,
    name: '张先生',
    rating: 5,
    content: '房间很干净，服务很好，下次还会选择这里！',
    date: '2024-01-15'
  },
  {
    id: 2,
    name: '李女士',
    rating: 5,
    content: '位置很好，交通便利，房间设施齐全，性价比很高。',
    date: '2024-01-10'
  },
  {
    id: 3,
    name: '王先生',
    rating: 4,
    content: '整体体验不错，前台服务态度很好，会推荐给朋友。',
    date: '2024-01-08'
  }
])

// 日期禁用逻辑
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

const disabledCheckOutDate = (time) => {
  if (!bookingForm.checkIn) return time.getTime() < Date.now() - 8.64e7
  return time.getTime() < bookingForm.checkIn.getTime() + 8.64e7
}

// 滚动到指定区域
const scrollToRooms = () => {
  roomsSection.value?.scrollIntoView({ behavior: 'smooth' })
}

const scrollToAbout = () => {
  aboutSection.value?.scrollIntoView({ behavior: 'smooth' })
}

// 搜索房间
const searchRooms = () => {
  if (!bookingForm.checkIn || !bookingForm.checkOut) {
    ElMessage.warning('请选择入住和退房日期')
    return
  }
  
  // 跳转到房间列表页面，携带搜索参数
  router.push({
    path: '/rooms',
    query: {
      checkIn: bookingForm.checkIn,
      checkOut: bookingForm.checkOut,
      guests: bookingForm.guests
    }
  })
}

// 预订房间
const bookRoom = (room) => {
  if (!bookingForm.checkIn || !bookingForm.checkOut) {
    ElMessage.warning('请先选择入住日期')
    scrollToRooms()
    return
  }
  
  router.push({
    path: `/rooms/${room.id}/book`,
    query: {
      checkIn: bookingForm.checkIn,
      checkOut: bookingForm.checkOut,
      guests: bookingForm.guests
    }
  })
}

// 提交咨询
const submitContact = () => {
  if (!contactForm.name || !contactForm.phone) {
    ElMessage.warning('请填写姓名和电话')
    return
  }
  
  // 这里可以调用API提交咨询
  ElMessage.success('咨询提交成功，我们会尽快联系您！')
  
  // 清空表单
  Object.assign(contactForm, {
    name: '',
    phone: '',
    message: ''
  })
}
</script>

<style lang="scss" scoped>
.user-home {
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }
}

// 英雄区域
.hero-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 80px 0;
  
  .hero-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 60px;
    align-items: center;
  }
  
  .hero-text {
    h1 {
      font-size: 48px;
      margin: 0 0 20px 0;
      font-weight: bold;
      line-height: 1.2;
    }
    
    p {
      font-size: 20px;
      margin: 0 0 40px 0;
      opacity: 0.9;
      line-height: 1.6;
    }
  }
  
  .hero-actions {
    display: flex;
    gap: 16px;
    
    .el-button {
      padding: 12px 32px;
      font-size: 16px;
    }
  }
  
  .hero-image {
    .image-placeholder {
      background: rgba(255, 255, 255, 0.1);
      border-radius: 12px;
      padding: 60px;
      text-align: center;
      backdrop-filter: blur(10px);
      
      .el-icon {
        margin-bottom: 16px;
        opacity: 0.8;
      }
      
      p {
        margin: 0;
        opacity: 0.7;
      }
    }
  }
}

// 快速预订区域
.booking-section {
  padding: 60px 0;
  background: #f8f9fa;
  
  h2 {
    text-align: center;
    margin: 0 0 40px 0;
    font-size: 32px;
    color: #333;
  }
  
  .booking-form {
    background: white;
    padding: 40px;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    
    .el-form {
      display: flex;
      flex-wrap: wrap;
      gap: 20px;
      justify-content: center;
      align-items: end;
    }
  }
}

// 房型展示
.rooms-showcase {
  padding: 80px 0;
  
  h2 {
    text-align: center;
    margin: 0 0 60px 0;
    font-size: 32px;
    color: #333;
  }
  
  .rooms-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
    gap: 30px;
  }
  
  .room-card {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s, box-shadow 0.3s;
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
    }
    
    .room-image {
      position: relative;
      height: 200px;
      
      .image-placeholder {
        width: 100%;
        height: 100%;
        background: #f5f5f5;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        color: #999;
      }
      
      .room-badge {
        position: absolute;
        top: 12px;
        right: 12px;
        background: #409eff;
        color: white;
        padding: 4px 12px;
        border-radius: 12px;
        font-size: 12px;
        font-weight: bold;
      }
    }
    
    .room-info {
      padding: 24px;
      
      h3 {
        margin: 0 0 12px 0;
        font-size: 20px;
        color: #333;
      }
      
      .room-desc {
        margin: 0 0 16px 0;
        color: #666;
        line-height: 1.6;
      }
      
      .room-features {
        margin-bottom: 20px;
        
        .feature-tag {
          display: inline-block;
          background: #f0f8ff;
          color: #409eff;
          padding: 4px 8px;
          border-radius: 4px;
          font-size: 12px;
          margin: 0 8px 8px 0;
        }
      }
      
      .room-footer {
        display: flex;
        align-items: center;
        justify-content: space-between;
        
        .price {
          .price-label {
            font-size: 16px;
            color: #e74c3c;
          }
          
          .price-value {
            font-size: 24px;
            font-weight: bold;
            color: #e74c3c;
          }
          
          .price-unit {
            font-size: 14px;
            color: #666;
          }
        }
      }
    }
  }
}

// 服务特色
.features-section {
  padding: 80px 0;
  background: #f8f9fa;
  
  h2 {
    text-align: center;
    margin: 0 0 60px 0;
    font-size: 32px;
    color: #333;
  }
  
  .features-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 40px;
  }
  
  .feature-card {
    text-align: center;
    padding: 40px 20px;
    background: white;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    
    .feature-icon {
      margin-bottom: 24px;
    }
    
    h3 {
      margin: 0 0 16px 0;
      font-size: 20px;
      color: #333;
    }
    
    p {
      margin: 0;
      color: #666;
      line-height: 1.6;
    }
  }
}

// 客户评价
.reviews-section {
  padding: 80px 0;
  
  h2 {
    text-align: center;
    margin: 0 0 60px 0;
    font-size: 32px;
    color: #333;
  }
  
  .reviews-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
    gap: 30px;
  }
  
  .review-card {
    background: white;
    padding: 30px;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    
    .review-header {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-bottom: 20px;
      
      .review-info {
        h4 {
          margin: 0 0 8px 0;
          color: #333;
        }
      }
    }
    
    .review-content {
      margin: 0 0 16px 0;
      color: #666;
      line-height: 1.6;
      font-style: italic;
    }
    
    .review-date {
      color: #999;
      font-size: 14px;
    }
  }
}

// 联系我们
.contact-section {
  padding: 80px 0;
  background: #f8f9fa;
  
  .contact-content {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 60px;
    align-items: start;
  }
  
  .contact-info {
    h2 {
      margin: 0 0 40px 0;
      font-size: 32px;
      color: #333;
    }
    
    .contact-item {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 20px;
      font-size: 16px;
      color: #666;
      
      .el-icon {
        color: #409eff;
      }
    }
  }
  
  .contact-form {
    background: white;
    padding: 40px;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
    
    h3 {
      margin: 0 0 30px 0;
      font-size: 24px;
      color: #333;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .hero-section {
    padding: 60px 0;
    
    .hero-content {
      grid-template-columns: 1fr;
      gap: 40px;
      text-align: center;
    }
    
    .hero-text h1 {
      font-size: 36px;
    }
  }
  
  .booking-form .el-form {
    flex-direction: column;
    align-items: stretch;
  }
  
  .contact-content {
    grid-template-columns: 1fr;
    gap: 40px;
  }
}
</style>
