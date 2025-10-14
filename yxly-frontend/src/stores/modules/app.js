import { defineStore } from 'pinia'

export const useAppStore = defineStore('app', {
  state: () => ({
    // 侧边栏状态
    sidebarCollapsed: false,
    
    // 设备类型
    device: 'desktop',
    
    // 主题设置
    theme: 'light',
    
    // 语言设置
    language: 'zh-CN',
    
    // 页面加载状态
    loading: false,
    
    // 面包屑导航
    breadcrumbs: []
  }),

  getters: {
    isMobile: (state) => state.device === 'mobile',
    
    isTablet: (state) => state.device === 'tablet',
    
    isDesktop: (state) => state.device === 'desktop'
  },

  actions: {
    // 切换侧边栏状态
    toggleSidebar() {
      this.sidebarCollapsed = !this.sidebarCollapsed
    },
    
    // 设置侧边栏状态
    setSidebarCollapsed(collapsed) {
      this.sidebarCollapsed = collapsed
    },
    
    // 设置设备类型
    setDevice(device) {
      this.device = device
    },
    
    // 设置主题
    setTheme(theme) {
      this.theme = theme
    },
    
    // 设置语言
    setLanguage(language) {
      this.language = language
    },
    
    // 设置加载状态
    setLoading(loading) {
      this.loading = loading
    },
    
    // 设置面包屑导航
    setBreadcrumbs(breadcrumbs) {
      this.breadcrumbs = breadcrumbs
    }
  }
})
