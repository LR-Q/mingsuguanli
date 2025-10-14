<template>
  <div style="padding: 40px; text-align: center; background: #f5f5f5; min-height: 100vh;">
    <h1 style="color: #409eff; margin-bottom: 20px;">悦鑫乐怡民宿</h1>
    <p style="font-size: 18px; color: #666; margin-bottom: 30px;">欢迎来到我们的民宿预订系统</p>
    
    <div style="background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 12px rgba(0,0,0,0.1); max-width: 600px; margin: 0 auto;">
      <h2 style="color: #333; margin-bottom: 20px;">快速预订</h2>
      
      <div style="display: flex; gap: 15px; justify-content: center; flex-wrap: wrap; margin-bottom: 20px;">
        <input type="date" style="padding: 8px; border: 1px solid #ddd; border-radius: 4px;" placeholder="入住日期">
        <input type="date" style="padding: 8px; border: 1px solid #ddd; border-radius: 4px;" placeholder="退房日期">
        <select style="padding: 8px; border: 1px solid #ddd; border-radius: 4px;">
          <option>1人</option>
          <option>2人</option>
          <option>3人</option>
          <option>4人</option>
        </select>
      </div>
      
      <button 
        style="background: #409eff; color: white; padding: 10px 30px; border: none; border-radius: 4px; cursor: pointer; font-size: 16px;"
        @click="searchRooms"
      >
        搜索房间
      </button>
    </div>
    
    <div style="margin-top: 40px;">
      <h3 style="color: #333; margin-bottom: 20px;">精选房型</h3>
      <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; max-width: 800px; margin: 0 auto;">
        <div v-for="room in rooms" :key="room.id" style="background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
          <h4 style="color: #333; margin: 0 0 10px 0;">{{ room.name }}</h4>
          <p style="color: #666; margin: 0 0 15px 0; font-size: 14px;">{{ room.desc }}</p>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <span style="color: #e74c3c; font-size: 18px; font-weight: bold;">￥{{ room.price }}/晚</span>
            <button 
              style="background: #67c23a; color: white; padding: 6px 15px; border: none; border-radius: 4px; cursor: pointer;"
              @click="bookRoom(room)"
            >
              预订
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <div style="margin-top: 40px; padding: 20px; background: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); max-width: 600px; margin: 40px auto 0;">
      <h3 style="color: #333; margin-bottom: 15px;">联系我们</h3>
      <p style="color: #666; margin: 5px 0;">📞 电话：400-123-4567</p>
      <p style="color: #666; margin: 5px 0;">📧 邮箱：info@yxly.com</p>
      <p style="color: #666; margin: 5px 0;">📍 地址：某某市某某区某某街道123号</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 简单的房间数据
const rooms = ref([
  { id: 1, name: '豪华海景套房', desc: '180度海景视野，配备独立阳台', price: 588 },
  { id: 2, name: '温馨家庭房', desc: '适合家庭入住，配备儿童设施', price: 388 },
  { id: 3, name: '商务标准间', desc: '商务人士首选，配备办公设施', price: 288 }
])

const searchRooms = () => {
  alert('搜索房间功能')
}

const bookRoom = (room) => {
  alert(`预订 ${room.name}`)
}
</script>
