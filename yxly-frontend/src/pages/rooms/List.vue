<template>
  <div class="room-list">
    <div class="page-header">
      <h1>房间管理</h1>
      <el-button type="primary" @click="$router.push('/rooms/create')">
        <el-icon><Plus /></el-icon>
        添加房间
      </el-button>
    </div>
    
    <div class="page-content">
      <p>房间列表页面 - 开发中</p>
    </div>
  </div>
</template>

<script setup>
// 房间列表页面
</script>

<style lang="scss" scoped>
.room-list {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    h1 {
      margin: 0;
      color: #303133;
      font-size: 24px;
      font-weight: 500;
    }
  }
  
  .page-content {
    background: white;
    border-radius: 8px;
    padding: 24px;
    min-height: 400px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }
}
</style>
