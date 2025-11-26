<template>
  <div class="error-page">
    <div class="error-content">
      <div class="error-image">
        <svg
          class="error-illustration"
          width="320"
          height="220"
          viewBox="0 0 400 260"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <rect width="400" height="260" rx="28" fill="#EEF4FF" />
          <circle cx="110" cy="120" r="70" fill="#C7D2FE" opacity="0.65" />
          <circle cx="290" cy="120" r="95" fill="#DBEAFE" opacity="0.85" />
          <path
            d="M135 80L135 160M85 120H185"
            stroke="#2563EB"
            stroke-width="18"
            stroke-linecap="round"
          />
          <path
            d="M275 80L275 160M225 120H325"
            stroke="#2563EB"
            stroke-width="18"
            stroke-linecap="round"
          />
          <text
            x="200"
            y="210"
            text-anchor="middle"
            font-family="'Noto Sans SC','Microsoft YaHei',sans-serif"
            font-size="20"
            fill="#1E293B"
          >
            抱歉，页面走丢了
          </text>
          <text
            x="200"
            y="185"
            text-anchor="middle"
            font-family="'Inter',sans-serif"
            font-size="56"
            fill="#2563EB"
            font-weight="700"
          >
            404
          </text>
        </svg>
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
      
      .error-illustration {
        width: 320px;
        height: auto;
        display: block;
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
      
      .error-illustration {
        width: 220px;
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
