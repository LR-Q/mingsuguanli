<template>
  <div class="error-page">
    <div class="error-content">
      <div class="error-image">
        <img src="/404.svg" alt="404" />
      </div>
      <div class="error-text">
        <h1>404</h1>
        <h3>抱歉，页面不存在</h3>
        <p>您访问的页面可能已被删除、重命名或暂时不可用</p>
        <div class="error-actions">
          <el-button type="primary" @click="goHome">
            返回首页
          </el-button>
          <el-button @click="goBack">
            返回上一页
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.go(-1)
}
</script>

<style lang="scss" scoped>
.error-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: #f5f5f5;
  
  .error-content {
    display: flex;
    align-items: center;
    text-align: center;
    
    .error-image {
      margin-right: 48px;
      
      img {
        width: 300px;
        height: auto;
      }
    }
    
    .error-text {
      h1 {
        font-size: 72px;
        font-weight: bold;
        color: #409eff;
        margin: 0;
      }
      
      h3 {
        font-size: 24px;
        color: #303133;
        margin: 16px 0;
      }
      
      p {
        font-size: 16px;
        color: #606266;
        margin-bottom: 32px;
      }
      
      .error-actions {
        .el-button + .el-button {
          margin-left: 16px;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .error-content {
    flex-direction: column;
    
    .error-image {
      margin-right: 0;
      margin-bottom: 32px;
      
      img {
        width: 200px;
      }
    }
    
    .error-text {
      h1 {
        font-size: 48px;
      }
      
      h3 {
        font-size: 20px;
      }
    }
  }
}
</style>
