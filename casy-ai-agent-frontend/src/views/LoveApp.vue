<template>
  <div class="h-screen flex flex-col bg-kimi-bg">
    <!-- 顶部导航 -->
    <header class="bg-kimi-sidebar border-b border-kimi-border flex items-center justify-between px-4 py-3">
      <div class="flex items-center space-x-3">
        <router-link to="/" class="p-2 hover:bg-kimi-card rounded-lg transition-colors">
          <svg class="w-5 h-5 text-kimi-text-secondary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 19l-7-7m0 0l7-7m-7 7h18"/>
          </svg>
        </router-link>
        <div class="w-8 h-8 bg-gradient-to-br from-pink-500 to-rose-600 rounded-lg flex items-center justify-center">
          <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
          </svg>
        </div>
        <div>
          <h1 class="text-white font-semibold">AI 恋爱大师</h1>
          <p class="text-xs text-kimi-text-muted">会话 ID: {{ chatId }}</p>
        </div>
      </div>
      <button 
        @click="clearChat"
        class="p-2 hover:bg-kimi-card rounded-lg transition-colors text-kimi-text-secondary hover:text-white"
        title="清空对话"
      >
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
        </svg>
      </button>
    </header>

    <!-- 聊天区域 -->
    <div class="flex-1 overflow-y-auto" ref="chatContainer">
      <div class="max-w-3xl mx-auto py-6 px-4 space-y-6">
        <!-- 欢迎消息 -->
        <div v-if="messages.length === 0" class="text-center py-12">
          <div class="w-16 h-16 bg-gradient-to-br from-pink-500/20 to-rose-600/20 rounded-2xl flex items-center justify-center mx-auto mb-4">
            <svg class="w-8 h-8 text-pink-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-white mb-2">我是你的 AI 恋爱大师</h3>
          <p class="text-kimi-text-secondary max-w-md mx-auto">
            无论是单身、恋爱中还是已婚，我都可以为你提供专业的恋爱心理建议和情感指导。
          </p>
          <div class="flex flex-wrap justify-center gap-2 mt-6">
            <button 
              v-for="prompt in quickPrompts" 
              :key="prompt"
              @click="sendMessage(prompt)"
              class="px-4 py-2 bg-kimi-card hover:bg-kimi-border border border-kimi-border rounded-full text-sm text-kimi-text-secondary hover:text-white transition-colors"
            >
              {{ prompt }}
            </button>
          </div>
        </div>

        <!-- 消息列表 -->
        <div v-else>
          <div 
            v-for="(message, index) in messages" 
            :key="index"
            class="animate-fade-in"
          >
            <!-- 用户消息 -->
            <div v-if="message.role === 'user'" class="flex justify-end mb-6">
              <div class="flex items-start space-x-3 max-w-[80%]">
                <div class="bg-kimi-user-msg text-white px-4 py-3 rounded-2xl rounded-tr-sm">
                  <p class="whitespace-pre-wrap">{{ message.content }}</p>
                </div>
                <div class="w-8 h-8 bg-gradient-to-br from-gray-600 to-gray-700 rounded-full flex items-center justify-center flex-shrink-0">
                  <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
                  </svg>
                </div>
              </div>
            </div>

            <!-- AI 消息 -->
            <div v-else-if="message.role === 'assistant'" class="flex justify-start mb-6">
              <div class="flex items-start space-x-3 max-w-[80%]">
                <div class="w-8 h-8 bg-gradient-to-br from-pink-500 to-rose-600 rounded-full flex items-center justify-center flex-shrink-0">
                  <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
                  </svg>
                </div>
                <div class="bg-kimi-ai-msg text-kimi-text px-4 py-3 rounded-2xl rounded-tl-sm border border-kimi-border">
                  <div class="markdown-body" v-html="renderMarkdown(message.content)"></div>
                </div>
              </div>
            </div>
          </div>

          <!-- 加载动画 -->
          <div v-if="isLoading" class="flex justify-start mb-6">
            <div class="flex items-start space-x-3">
              <div class="w-8 h-8 bg-gradient-to-br from-pink-500 to-rose-600 rounded-full flex items-center justify-center flex-shrink-0">
                <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
                </svg>
              </div>
              <div class="bg-kimi-ai-msg px-4 py-3 rounded-2xl rounded-tl-sm border border-kimi-border">
                <div class="flex space-x-1">
                  <div class="w-2 h-2 bg-kimi-text-muted rounded-full animate-bounce"></div>
                  <div class="w-2 h-2 bg-kimi-text-muted rounded-full animate-bounce" style="animation-delay: 0.1s"></div>
                  <div class="w-2 h-2 bg-kimi-text-muted rounded-full animate-bounce" style="animation-delay: 0.2s"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="bg-kimi-sidebar border-t border-kimi-border p-4">
      <div class="max-w-3xl mx-auto">
        <form @submit.prevent="handleSubmit" class="relative">
          <textarea
            v-model="inputMessage"
            rows="1"
            class="w-full bg-kimi-card border border-kimi-border rounded-xl pl-4 pr-12 py-3 text-white placeholder-kimi-text-muted focus:outline-none focus:border-kimi-primary resize-none"
            placeholder="输入你的情感问题..."
            @keydown.enter.prevent="handleEnter"
            @input="autoResize"
            ref="textarea"
          ></textarea>
          <button
            type="submit"
            :disabled="!inputMessage.trim() || isLoading"
            class="absolute right-2 bottom-2 p-2 bg-kimi-primary hover:bg-kimi-primary-hover disabled:opacity-50 disabled:cursor-not-allowed rounded-lg transition-colors"
          >
            <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"/>
            </svg>
          </button>
        </form>
        <p class="text-xs text-kimi-text-muted mt-2 text-center">
          AI 恋爱大师仅供参考，重要决策请结合实际情况
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted } from 'vue'
import { marked } from 'marked'
import hljs from 'highlight.js'
import config from '../config'

// 生成唯一会话 ID
const generateChatId = () => {
  return 'love_' + Date.now().toString(36) + Math.random().toString(36).substr(2, 5)
}

// 状态
const chatId = ref(generateChatId())
const messages = ref([])
const inputMessage = ref('')
const isLoading = ref(false)
const chatContainer = ref(null)
const textarea = ref(null)
const eventSource = ref(null)

// 快捷提示
const quickPrompts = [
  '如何表白成功率高？',
  '约会去哪里比较好？',
  '分手后如何走出阴影？',
  '如何维持长期关系？'
]

// 配置 marked
marked.setOptions({
  highlight: function(code, lang) {
    if (lang && hljs.getLanguage(lang)) {
      return hljs.highlight(code, { language: lang }).value
    }
    return hljs.highlightAuto(code).value
  },
  breaks: true
})

// 渲染 Markdown
const renderMarkdown = (content) => {
  return marked(content)
}

// 自动调整文本框高度
const autoResize = () => {
  const el = textarea.value
  el.style.height = 'auto'
  el.style.height = Math.min(el.scrollHeight, 200) + 'px'
}

// 处理回车键
const handleEnter = (e) => {
  if (!e.shiftKey) {
    handleSubmit()
  }
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
  })
}

// 发送消息
const sendMessage = async (text) => {
  if (!text.trim() || isLoading.value) return
  
  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: text
  })
  
  inputMessage.value = ''
  if (textarea.value) {
    textarea.value.style.height = 'auto'
  }
  
  isLoading.value = true
  scrollToBottom()
  
  // 添加 AI 消息占位
  const aiMessageIndex = messages.value.length
  messages.value.push({
    role: 'assistant',
    content: ''
  })
  
  // 建立 SSE 连接
  try {
    const encodedMessage = encodeURIComponent(text)
    const url = `${config.apiBaseUrl}${config.apiPrefix}/ai/love_app/chat/sse?message=${encodedMessage}&chatId=${chatId.value}`
    
    eventSource.value = new EventSource(url)
    
    eventSource.value.onmessage = (event) => {
      const data = event.data
      if (data) {
        messages.value[aiMessageIndex].content += data
        scrollToBottom()
      }
    }
    
    eventSource.value.onerror = (error) => {
      console.error('SSE error:', error)
      eventSource.value.close()
      isLoading.value = false
      
      if (!messages.value[aiMessageIndex].content) {
        messages.value[aiMessageIndex].content = '抱歉，连接出现错误，请稍后重试。'
      }
    }
    
    eventSource.value.onclose = () => {
      isLoading.value = false
    }
  } catch (error) {
    console.error('Error:', error)
    messages.value[aiMessageIndex].content = '抱歉，发送消息时出现错误。'
    isLoading.value = false
  }
}

// 提交表单
const handleSubmit = () => {
  sendMessage(inputMessage.value)
}

// 清空对话
const clearChat = () => {
  if (eventSource.value) {
    eventSource.value.close()
  }
  messages.value = []
  chatId.value = generateChatId()
  isLoading.value = false
}

// 组件卸载时关闭连接
onUnmounted(() => {
  if (eventSource.value) {
    eventSource.value.close()
  }
})

onMounted(() => {
  // 自动聚焦输入框
  textarea.value?.focus()
})
</script>

<style scoped>
.markdown-body {
  color: #e4e4f0;
  line-height: 1.6;
}

.markdown-body p {
  margin-bottom: 0.75rem;
}

.markdown-body p:last-child {
  margin-bottom: 0;
}

.markdown-body ul,
.markdown-body ol {
  margin-left: 1.5rem;
  margin-bottom: 0.75rem;
}

.markdown-body li {
  margin-bottom: 0.25rem;
}

.markdown-body code {
  background: #2d2b55;
  padding: 0.2rem 0.4rem;
  border-radius: 4px;
  font-family: 'JetBrains Mono', Consolas, Monaco, monospace;
  font-size: 0.9em;
}

.markdown-body pre {
  background: #2d2b55;
  padding: 1rem;
  border-radius: 8px;
  overflow-x: auto;
  margin-bottom: 0.75rem;
}

.markdown-body pre code {
  background: none;
  padding: 0;
}

.markdown-body blockquote {
  border-left: 4px solid #6c5ce7;
  padding-left: 1rem;
  margin-left: 0;
  margin-bottom: 0.75rem;
  color: #9ca3af;
}

.markdown-body h1,
.markdown-body h2,
.markdown-body h3,
.markdown-body h4 {
  margin-bottom: 0.75rem;
  font-weight: 600;
}

.markdown-body a {
  color: #6c5ce7;
  text-decoration: none;
}

.markdown-body a:hover {
  text-decoration: underline;
}
</style>
