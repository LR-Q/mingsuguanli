<template>
  <div class="room-form">
    <div class="page-header">
      <h1>{{ isEdit ? '编辑房间' : '添加房间' }}</h1>
    </div>
    
    <div class="page-content">
      <p>房间表单页面 - 开发中</p>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const isEdit = computed(() => !!route.params.id)
</script>

<style lang="scss" scoped>
.room-form {
  .page-header {
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
