/**
 * 后端健康检查 Composable
 * 用于检测后端是否重启，并提醒用户重新登录
 */

import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/modules/auth'
import { useRouter } from 'vue-router'

export function useBackendHealth() {
  const authStore = useAuthStore()
  const router = useRouter()
  const isHealthy = ref(true)
  const lastCheckTime = ref(Date.now())
  let healthCheckTimer = null

  // 检查后端健康状态
  const checkBackendHealth = async () => {
    try {
      // 发送一个简单的健康检查请求
      const response = await fetch('/api/v1/auth/health', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${authStore.token}`
        }
      })

      if (response.status === 401) {
        // Token失效，需要重新登录
        handleTokenInvalid()
      } else if (response.ok) {
        isHealthy.value = true
        lastCheckTime.value = Date.now()
      }
    } catch (error) {
      console.warn('健康检查失败:', error)
      // 网络错误，暂不处理
    }
  }

  // 处理Token失效
  const handleTokenInvalid = () => {
    // 停止定时检查
    if (healthCheckTimer) {
      clearInterval(healthCheckTimer)
    }

    ElMessageBox.alert(
      '检测到后端服务已重启，您的登录状态已失效。\n\n为确保数据安全，请重新登录。',
      '登录状态失效',
      {
        confirmButtonText: '重新登录',
        type: 'warning',
        showClose: false,
        closeOnClickModal: false,
        closeOnPressEscape: false
      }
    ).then(() => {
      // 清除认证信息
      authStore.clearUserInfo()
      // 跳转到登录页
      router.push('/login')
    })
  }

  // 监听window focus事件，页面激活时检查
  const handleWindowFocus = () => {
    const now = Date.now()
    // 如果距离上次检查超过30秒，立即检查一次
    if (now - lastCheckTime.value > 30000) {
      checkBackendHealth()
    }
  }

  // 启动健康检查
  const startHealthCheck = () => {
    // 立即检查一次
    checkBackendHealth()

    // 每30秒检查一次
    healthCheckTimer = setInterval(checkBackendHealth, 30000)

    // 监听窗口焦点
    window.addEventListener('focus', handleWindowFocus)
  }

  // 停止健康检查
  const stopHealthCheck = () => {
    if (healthCheckTimer) {
      clearInterval(healthCheckTimer)
      healthCheckTimer = null
    }
    window.removeEventListener('focus', handleWindowFocus)
  }

  // 组件挂载时启动
  onMounted(() => {
    if (authStore.isLoggedIn) {
      startHealthCheck()
    }
  })

  // 组件卸载时停止
  onUnmounted(() => {
    stopHealthCheck()
  })

  return {
    isHealthy,
    checkBackendHealth,
    startHealthCheck,
    stopHealthCheck
  }
}
