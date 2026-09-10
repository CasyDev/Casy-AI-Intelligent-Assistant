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
        <div class="w-8 h-8 bg-gradient-to-br from-cyan-500 to-teal-600 rounded-lg flex items-center justify-center">
          <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M18 10c0-3.314-2.686-6-6-6S6 6.686 6 10c0 1.657.672 3.157 1.757 4.243L6 21l6-3 6 3-1.757-6.757A5.978 5.978 0 0018 10z"/>
          </svg>
        </div>
        <div>
          <h1 class="text-white font-semibold">企业智能客服</h1>
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
          <div class="w-16 h-16 bg-gradient-to-br from-cyan-500/20 to-teal-600/20 rounded-2xl flex items-center justify-center mx-auto mb-4">
            <svg class="w-8 h-8 text-cyan-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-white mb-2">我是星河云智能客服小河</h3>
          <p class="text-kimi-text-secondary max-w-md mx-auto">
            基于企业知识库为你解答产品、账号、套餐、售后和安全合规问题。答案来自 RAG 检索，不编造政策数字。
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
                <div class="w-8 h-8 bg-gradient-to-br from-cyan-500 to-teal-600 rounded-full flex items-center justify-center flex-shrink-0">
                  <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"/>
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
              <div class="w-8 h-8 bg-gradient-to-br from-cyan-500 to-teal-600 rounded-full flex items-center justify-center flex-shrink-0">
                <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"/>
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
            class="w-full bg-kimi-card border border-kimi-border rounded-xl pl-4 pr-12 py-3 text-white placeholder-kimi-text-muted focus:outline-none focus:border-cyan-500 resize-none"
            placeholder="输入产品、账号、套餐或售后问题..."
            @keydown.enter.prevent="handleEnter"
            @input="autoResize"
            ref="textarea"
          ></textarea>
          <button
            type="submit"
            :disabled="!inputMessage.trim() || isLoading"
            class="absolute right-2 bottom-2 p-2 bg-cyan-600 hover:bg-cyan-500 disabled:opacity-50 disabled:cursor-not-allowed rounded-lg transition-colors"
          >
            <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"/>
            </svg>
          </button>
        </form>
        <p class="text-xs text-kimi-text-muted mt-2 text-center">
          答案来自星河云知识库检索，政策类问题请以控制台公示为准
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

const generateChatId = () => {
  return 'cs_' + Date.now().toString(36) + Math.random().toString(36).substr(2, 5)
}

const chatId = ref(generateChatId())
const messages = ref([])
const inputMessage = ref('')
const isLoading = ref(false)
const chatContainer = ref(null)
const textarea = ref(null)
const eventSource = ref(null)

const quickPrompts = [
  '星河云有哪些产品？',
  '专业版一年多少钱？',
  '忘记密码怎么办？',
  '如何提交工单？'
]

marked.setOptions({
  highlight: function(code, lang) {
    if (lang && hljs.getLanguage(lang)) {
      return hljs.highlight(code, { language: lang }).value
    }
    return hljs.highlightAuto(code).value
  },
  breaks: true
})

const renderMarkdown = (content) => {
  return marked(content)
}

const autoResize = () => {
  const el = textarea.value
  el.style.height = 'auto'
  el.style.height = Math.min(el.scrollHeight, 200) + 'px'
}

const handleEnter = (e) => {
  if (!e.shiftKey) {
    handleSubmit()
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatContainer.value) {
      chatContainer.value.scrollTop = chatContainer.value.scrollHeight
    }
  })
}

const sendMessage = async (text) => {
  if (!text.trim() || isLoading.value) return

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

  const aiMessageIndex = messages.value.length
  messages.value.push({
    role: 'assistant',
    content: ''
  })

  try {
    const encodedMessage = encodeURIComponent(text)
    const url = `${config.apiBaseUrl}${config.apiPrefix}/ai/customer_service/chat/sse?message=${encodedMessage}&chatId=${chatId.value}`

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

const handleSubmit = () => {
  sendMessage(inputMessage.value)
}

const clearChat = () => {
  if (eventSource.value) {
    eventSource.value.close()
  }
  messages.value = []
  chatId.value = generateChatId()
  isLoading.value = false
}

onUnmounted(() => {
  if (eventSource.value) {
    eventSource.value.close()
  }
})

onMounted(() => {
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
  border-left: 4px solid #22d3ee;
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
  color: #22d3ee;
  text-decoration: none;
}

.markdown-body a:hover {
  text-decoration: underline;
}

.markdown-body table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 0.75rem;
  font-size: 0.9em;
}

.markdown-body th,
.markdown-body td {
  border: 1px solid #3f3d56;
  padding: 0.4rem 0.6rem;
}
</style>
