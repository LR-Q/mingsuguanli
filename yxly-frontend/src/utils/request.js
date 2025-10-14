import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/modules/auth'
import { getToken } from '@/utils/auth'
import router from '@/router'

// 是否正在刷新token
let isRefreshing = false
// 重试队列
let requests = []

// 创建axios实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 添加认证token
    const token = getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    // 添加请求ID
    config.headers['X-Request-ID'] = generateRequestId()
    
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const { code, message, data } = response.data
    
    // 成功响应
    if (code === 200) {
      return { data, message }
    } else {
      // 业务错误
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message || '请求失败'))
    }
  },
  (error) => {
    console.error('响应错误:', error)
    
    const { response } = error
    
    if (response) {
      const { status, data } = response
      
      switch (status) {
        case 401:
          return handleUnauthorized(error.config)
        case 403:
          ElMessage.error('权限不足')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 422:
          ElMessage.error(data?.message || '参数验证失败')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(data?.message || `请求失败 (${status})`)
      }
    } else {
      ElMessage.error('网络连接异常，请检查网络设置')
    }
    
    return Promise.reject(error)
  }
)

// 处理未认证错误
async function handleUnauthorized(originalRequest) {
  const authStore = useAuthStore()
  
  // 如果没有刷新令牌，直接跳转登录
  if (!authStore.refreshToken) {
    authStore.clearUserInfo()
    router.push('/login')
    return Promise.reject(new Error('未登录'))
  }
  
  // 如果正在刷新token，将请求加入队列
  if (isRefreshing) {
    return new Promise((resolve) => {
      requests.push(() => {
        originalRequest.headers.Authorization = `Bearer ${getToken()}`
        resolve(request(originalRequest))
      })
    })
  }
  
  // 开始刷新token
  isRefreshing = true
  
  try {
    await authStore.refreshAccessToken()
    
    // 刷新成功，重试所有队列中的请求
    requests.forEach(cb => cb())
    requests = []
    
    // 重试原始请求
    originalRequest.headers.Authorization = `Bearer ${getToken()}`
    return request(originalRequest)
  } catch (error) {
    // 刷新失败，清除所有信息并跳转登录
    requests = []
    authStore.clearUserInfo()
    
    ElMessageBox.confirm(
      '登录状态已过期，请重新登录',
      '系统提示',
      {
        confirmButtonText: '重新登录',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      router.push('/login')
    }).catch(() => {
      router.push('/login')
    })
    
    return Promise.reject(error)
  } finally {
    isRefreshing = false
  }
}

// 生成请求ID
function generateRequestId() {
  return 'req_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}

export default request
