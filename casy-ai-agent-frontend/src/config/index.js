/**
 * 全局配置文件
 * 根据环境变量自动切换开发/生产环境配置
 * 
 * 使用方法：
 * 1. 开发环境：npm run dev（使用 .env.development）
 * 2. 生产环境：npm run build（使用 .env.production）
 * 
 * 部署说明：
 * - 同源部署：VITE_API_BASE_URL 留空，API 请求会自动使用当前域名
 * - 分离部署：VITE_API_BASE_URL 设置为实际后端地址，如 http://api.example.com:8123
 */

// API 基础配置
const config = {
  // 开发环境
  development: {
    // API 基础地址
    // 开发环境通过 vite.config.js 的 proxy 代理到后端
    apiBaseUrl: import.meta.env.VITE_API_BASE_URL || '',
    // API 前缀
    apiPrefix: import.meta.env.VITE_API_PREFIX || '/api',
    // SSE 接口地址
    get sseUrl() {
      return `${this.apiBaseUrl}${this.apiPrefix}/ai/manus/chat`
    },
    // LoveApp SSE 接口
    get loveAppSseUrl() {
      return `${this.apiBaseUrl}${this.apiPrefix}/ai/love_app/chat/sse`
    },
    // 应用标题
    appTitle: import.meta.env.VITE_APP_TITLE || 'CASY AI Agent',
    // 是否开启调试
    debug: true,
  },
  
  // 生产环境
  production: {
    // API 基础地址
    // 同源部署：留空，使用相对路径
    // 分离部署：填写完整后端地址，如 http://api.example.com:8123
    apiBaseUrl: import.meta.env.VITE_API_BASE_URL || '',
    apiPrefix: import.meta.env.VITE_API_PREFIX || '/api',
    get sseUrl() {
      return `${this.apiBaseUrl}${this.apiPrefix}/ai/manus/chat`
    },
    get loveAppSseUrl() {
      return `${this.apiBaseUrl}${this.apiPrefix}/ai/love_app/chat/sse`
    },
    appTitle: import.meta.env.VITE_APP_TITLE || 'CASY AI Agent',
    debug: false,
  }
}

// 获取当前环境配置
const env = import.meta.env.MODE || 'development'
const currentConfig = config[env] || config.development

// 导出配置
export default currentConfig

// 导出常用配置项
export const { 
  apiBaseUrl, 
  apiPrefix, 
  sseUrl,
  loveAppSseUrl,
  appTitle, 
  debug 
} = currentConfig
