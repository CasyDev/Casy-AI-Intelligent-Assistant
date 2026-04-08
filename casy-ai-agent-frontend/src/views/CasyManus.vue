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
        <div class="w-8 h-8 bg-gradient-to-br from-kimi-primary to-purple-600 rounded-lg flex items-center justify-center">
          <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"/>
          </svg>
        </div>
        <div>
          <h1 class="text-white font-semibold">AI 超级智能体</h1>
          <p class="text-xs text-kimi-text-muted">自动规划 · 工具调用 · 任务执行</p>
        </div>
      </div>
      <div class="flex items-center space-x-2">
        <div v-if="isExecuting" class="flex items-center space-x-2 px-3 py-1 bg-kimi-card rounded-full">
          <div class="w-2 h-2 bg-kimi-primary rounded-full animate-pulse"></div>
          <span class="text-xs text-kimi-text-secondary">执行中</span>
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
      </div>
    </header>

    <!-- 聊天区域 -->
    <div class="flex-1 overflow-y-auto" ref="chatContainer">
      <div class="max-w-4xl mx-auto py-6 px-4 space-y-4">
        <!-- 欢迎消息 -->
        <div v-if="messages.length === 0" class="text-center py-12">
          <div class="w-16 h-16 bg-gradient-to-br from-kimi-primary/20 to-purple-600/20 rounded-2xl flex items-center justify-center mx-auto mb-4">
            <svg class="w-8 h-8 text-kimi-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"/>
            </svg>
          </div>
          <h3 class="text-xl font-semibold text-white mb-2">我是你的 AI 超级智能体</h3>
          <p class="text-kimi-text-secondary max-w-md mx-auto mb-6">
            我可以帮你完成复杂任务，自动规划步骤、调用工具、执行操作。
          </p>
          <div class="flex flex-wrap justify-center gap-2">
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
        <template v-else>
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

            <!-- 执行计划卡片 -->
            <div v-else-if="message.type === 'plan'" class="mb-4">
              <div class="bg-kimi-card border border-kimi-border rounded-xl overflow-hidden">
                <button 
                  @click="message.expanded = !message.expanded"
                  class="w-full flex items-center justify-between px-4 py-3 hover:bg-kimi-border/50 transition-colors"
                >
                  <div class="flex items-center space-x-2">
                    <div class="w-6 h-6 bg-kimi-step/20 rounded-lg flex items-center justify-center">
                      <svg class="w-4 h-4 text-kimi-step" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01"/>
                      </svg>
                    </div>
                    <span class="text-white font-medium">执行计划</span>
                  </div>
                  <svg 
                    class="w-5 h-5 text-kimi-text-secondary transition-transform"
                    :class="{ 'rotate-180': message.expanded }"
                    fill="none" stroke="currentColor" viewBox="0 0 24 24"
                  >
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
                  </svg>
                </button>
                <div v-show="message.expanded" class="px-4 pb-4 border-t border-kimi-border">
                  <div class="pt-3 text-kimi-text text-sm markdown-body" v-html="renderMarkdown(message.content)"></div>
                </div>
              </div>
            </div>

            <!-- 步骤指示器 -->
            <div v-else-if="message.type === 'step'" class="mb-4">
              <div class="flex items-center space-x-3 px-4 py-2 bg-kimi-card/50 border border-kimi-border rounded-lg">
                <!-- 当前正在执行的步骤显示转圈 -->
                <div v-if="message.isCurrent" class="w-5 h-5 border-2 border-kimi-primary border-t-transparent rounded-full animate-spin"></div>
                <!-- 已完成的步骤显示成功图标 -->
                <div v-else class="w-5 h-5 bg-green-500/20 rounded-full flex items-center justify-center">
                  <svg class="w-3 h-3 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7"/>
                  </svg>
                </div>
                <span class="text-kimi-text-secondary text-sm">{{ message.content }}</span>
              </div>
            </div>

            <!-- 思考内容 -->
            <div v-else-if="message.type === 'thinking'" class="mb-4">
              <div class="bg-kimi-card border border-kimi-border rounded-xl overflow-hidden">
                <button 
                  @click="message.expanded = !message.expanded"
                  class="w-full flex items-center justify-between px-4 py-3 bg-kimi-think/10 border-b border-kimi-border hover:bg-kimi-think/20 transition-colors"
                >
                  <div class="flex items-center space-x-2">
                    <div class="w-5 h-5 bg-kimi-think/20 rounded flex items-center justify-center">
                      <svg class="w-3 h-3 text-kimi-think" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"/>
                      </svg>
                    </div>
                    <span class="text-kimi-think text-sm font-medium">思考过程</span>
                  </div>
                  <svg 
                    class="w-5 h-5 text-kimi-text-secondary transition-transform"
                    :class="{ 'rotate-180': message.expanded }"
                    fill="none" stroke="currentColor" viewBox="0 0 24 24"
                  >
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
                  </svg>
                </button>
                <div v-show="message.expanded" class="px-4 py-3">
                  <div class="text-kimi-text text-sm markdown-body" v-html="renderMarkdown(message.content)"></div>
                </div>
              </div>
            </div>

            <!-- 工具调用 -->
            <div v-else-if="message.type === 'tool'" class="mb-4">
              <div class="bg-kimi-card border border-kimi-tool/30 rounded-xl overflow-hidden">
                <button 
                  @click="message.expanded = !message.expanded"
                  class="w-full flex items-center justify-between px-4 py-3 bg-kimi-tool/10 border-b border-kimi-tool/20 hover:bg-kimi-tool/20 transition-colors"
                >
                  <div class="flex items-center space-x-2">
                    <div class="w-5 h-5 bg-kimi-tool/20 rounded flex items-center justify-center">
                      <svg class="w-3 h-3 text-kimi-tool" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"/>
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                      </svg>
                    </div>
                    <span class="text-kimi-tool text-sm font-medium">工具调用</span>
                  </div>
                  <div class="flex items-center space-x-2">
                    <span class="text-xs text-kimi-text-muted bg-kimi-bg px-2 py-1 rounded">{{ message.toolName }}</span>
                    <svg 
                      class="w-5 h-5 text-kimi-text-secondary transition-transform"
                      :class="{ 'rotate-180': message.expanded }"
                      fill="none" stroke="currentColor" viewBox="0 0 24 24"
                    >
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
                    </svg>
                  </div>
                </button>
                <div v-show="message.expanded" class="px-4 py-3">
                  <div v-if="message.toolParams" class="mb-2">
                    <div class="text-xs text-kimi-text-muted mb-1">参数</div>
                    <pre class="text-xs text-kimi-text bg-kimi-bg p-2 rounded overflow-x-auto">{{ JSON.stringify(message.toolParams, null, 2) }}</pre>
                  </div>
                  <div v-if="message.content" class="border-t border-kimi-border pt-2 mt-2">
                    <div class="text-xs text-kimi-text-muted mb-1">结果</div>
                    <div class="text-sm text-kimi-text markdown-body" v-html="renderMarkdown(message.content)"></div>
                  </div>
                </div>
              </div>
            </div>

            <!-- AI 最终结果 -->
            <div v-else-if="message.role === 'assistant'" class="flex justify-start mb-6">
              <div class="flex items-start space-x-3 max-w-[85%]">
                <div class="w-8 h-8 bg-gradient-to-br from-kimi-primary to-purple-600 rounded-full flex items-center justify-center flex-shrink-0">
                  <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"/>
                  </svg>
                </div>
                <div class="bg-kimi-ai-msg text-kimi-text px-4 py-3 rounded-2xl rounded-tl-sm border border-kimi-border">
                  <div class="markdown-body" v-html="renderMarkdown(message.content)"></div>
                </div>
              </div>
            </div>

            <!-- 系统消息 -->
            <div v-else-if="message.type === 'system'" class="mb-4">
              <div class="flex items-center justify-center">
                <span class="text-xs text-kimi-text-muted bg-kimi-card px-3 py-1 rounded-full">
                  {{ message.content }}
                </span>
              </div>
            </div>

            <!-- 用户输入请求 -->
            <div v-else-if="message.type === 'userInputRequest'" class="mb-6">
              <div class="bg-kimi-card border border-orange-500/30 rounded-xl overflow-hidden">
                <div class="px-4 py-3 bg-orange-500/10 border-b border-orange-500/20 flex items-center space-x-2">
                  <div class="w-5 h-5 bg-orange-500/20 rounded flex items-center justify-center">
                    <svg class="w-3 h-3 text-orange-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"/>
                    </svg>
                  </div>
                  <span class="text-orange-500 text-sm font-medium">需要您的补充</span>
                </div>
                <div class="px-4 py-3">
                  <p class="text-kimi-text mb-3">{{ message.prompt }}</p>
                  <form @submit.prevent="submitUserInput(message.index)" class="flex space-x-2">
                    <input
                      v-model="message.userInput"
                      type="text"
                      class="flex-1 bg-kimi-bg border border-kimi-border rounded-lg px-3 py-2 text-white placeholder-kimi-text-muted focus:outline-none focus:border-orange-500 text-sm"
                      placeholder="请输入补充信息..."
                      :disabled="message.submitted"
                    />
                    <button
                      type="submit"
                      :disabled="!message.userInput?.trim() || message.submitted"
                      class="px-4 py-2 bg-orange-500 hover:bg-orange-600 disabled:opacity-50 disabled:cursor-not-allowed rounded-lg text-white text-sm font-medium transition-colors"
                    >
                      {{ message.submitted ? '已发送' : '发送' }}
                    </button>
                  </form>
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="bg-kimi-sidebar border-t border-kimi-border p-4">
      <div class="max-w-4xl mx-auto">
        <form @submit.prevent="handleSubmit" class="relative">
          <textarea
            v-model="inputMessage"
            rows="1"
            class="w-full bg-kimi-card border border-kimi-border rounded-xl pl-4 pr-12 py-3 text-white placeholder-kimi-text-muted focus:outline-none focus:border-kimi-primary resize-none"
            :placeholder="waitingForInput ? '请在上方的输入框中补充信息...' : '输入任务描述，我将自动规划并执行...'"
            @keydown.enter.prevent="handleEnter"
            @input="autoResize"
            ref="textarea"
            :disabled="waitingForInput"
          ></textarea>
          <button
            type="submit"
            :disabled="!inputMessage.trim() || isLoading || waitingForInput"
            class="absolute right-2 bottom-2 p-2 bg-kimi-primary hover:bg-kimi-primary-hover disabled:opacity-50 disabled:cursor-not-allowed rounded-lg transition-colors"
          >
            <svg class="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"/>
            </svg>
          </button>
        </form>
        <p class="text-xs text-kimi-text-muted mt-2 text-center">
          {{ waitingForInput ? 'AI 正在等待您的补充信息' : 'AI 超级智能体可调用多种工具完成任务，执行过程可能需要一些时间' }}
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted } from 'vue'
import { marked } from 'marked'
import hljs from 'highlight.js'

// 状态
const messages = ref([])
const inputMessage = ref('')
const isLoading = ref(false)
const isExecuting = ref(false)
const waitingForInput = ref(false)
const isTaskCompleted = ref(false)  // 任务是否已成功完成
const chatContainer = ref(null)
const textarea = ref(null)
const eventSource = ref(null)
const currentChatId = ref(null)  // 当前对话ID，用于保持对话记忆

// 当前步骤信息
const currentStep = ref(0)
const totalSteps = ref(10)

// 快捷提示
const quickPrompts = [
  '查询北京今天的天气',
  '搜索附近的餐厅推荐',
  '帮我规划一次旅行',
  '查询上海到北京的路线'
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

// 解析 SSE 消息
const parseMessage = (data) => {
  // 执行计划
  if (data.startsWith('【执行计划】')) {
    return {
      type: 'plan',
      content: data.replace('【执行计划】\n', ''),
      expanded: true
    }
  }
  
  // 步骤信息
  if (data.startsWith('执行步骤')) {
    const match = data.match(/执行步骤 (\d+)\/(\d+)/)
    if (match) {
      const stepNum = parseInt(match[1])
      currentStep.value = stepNum
      totalSteps.value = parseInt(match[2])
      
      // 将之前的步骤标记为非当前状态
      messages.value.forEach(msg => {
        if (msg.type === 'step') {
          msg.isCurrent = false
        }
      })
      
      return {
        type: 'step',
        content: data,
        isCurrent: true
      }
    }
    return {
      type: 'step',
      content: data,
      isCurrent: true
    }
  }
  
  // 步骤结果
  if (data.startsWith('Step')) {
    // 解析步骤结果
    const match = data.match(/Step \d+:\s*(.+)/)
    if (match) {
      const content = match[1]
      
      // 检测是否是用户输入请求（优先级最高）
      if (content.includes('[USER_INPUT_REQUEST]')) {
        const userInputMatch = content.match(/\[USER_INPUT_REQUEST\](.*?)\[END_USER_INPUT_REQUEST\]/s)
        if (userInputMatch) {
          waitingForInput.value = true
          isExecuting.value = false
          return {
            type: 'userInputRequest',
            prompt: userInputMatch[1].trim(),
            userInput: '',
            submitted: false,
            index: messages.value.length
          }
        }
      }
      
      // 检测是否是工具调用结果
      if (content.includes('工具') && content.includes('执行完成')) {
        const toolMatch = content.match(/工具 (\w+) 执行完成，结果:\s*(.+)/s)
        if (toolMatch) {
          return {
            type: 'tool',
            toolName: toolMatch[1],
            content: toolMatch[2],
            toolParams: {},
            expanded: false  // 默认折叠
          }
        }
      }
      
      // 检测是否是思考完成无需行动
      if (content.includes('思考完成')) {
        return {
          type: 'thinking',
          content: content,
          expanded: false  // 默认折叠
        }
      }
      
      // 默认作为思考内容
      return {
        type: 'thinking',
        content: content,
        expanded: false  // 默认折叠
      }
    }
    
    return {
      type: 'thinking',
      content: data
    }
  }
  
  // 执行结束
  if (data.startsWith('执行结束')) {
    isExecuting.value = false
    // 标记任务已完成
    if (data.includes('任务已完成')) {
      isTaskCompleted.value = true
      // 将所有步骤标记为已完成
      messages.value.forEach(msg => {
        if (msg.type === 'step') {
          msg.isCurrent = false
        }
      })
      return null  // 不显示这个系统消息
    }
    return {
      type: 'system',
      content: data
    }
  }
  
  // 错误信息
  if (data.startsWith('错误：') || data.startsWith('执行错误')) {
    isExecuting.value = false
    return {
      type: 'system',
      content: data
    }
  }
  
  // 检测用户输入请求（工具调用返回的特殊格式）
  if (data.includes('[USER_INPUT_REQUEST]')) {
    const match = data.match(/\[USER_INPUT_REQUEST\](.*?)\[END_USER_INPUT_REQUEST\]/s)
    if (match) {
      waitingForInput.value = true
      isExecuting.value = false
      return {
        type: 'userInputRequest',
        prompt: match[1].trim(),
        userInput: '',
        submitted: false,
        index: messages.value.length
      }
    }
  }
  
  // 默认作为 AI 回复
  return {
    role: 'assistant',
    content: data
  }
}

// 发送消息
// isUserInput: 是否是用户补充输入（从输入框提交），如果是则保留chatId
const sendMessage = async (text, isUserInput = false) => {
  if (!text.trim() || isLoading.value) return
  
  // 重置状态
  currentStep.value = 0
  isTaskCompleted.value = false  // 重置任务完成状态
  
  // 清空chatId的逻辑：
  // 1. 如果是用户补充输入（isUserInput=true），不清空
  // 2. 如果是新对话（messages.length <= 1），清空
  // 3. 如果已经有chatId且不是用户补充输入，保留（继续对话）
  if (!isUserInput && messages.value.length <= 1) {
    currentChatId.value = null
  }
  
  waitingForInput.value = false
  
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
  isExecuting.value = true
  scrollToBottom()
  
  // 建立 SSE 连接
  try {
    const encodedMessage = encodeURIComponent(text)
    // 如果有chatId，则传递以保持对话记忆
    let url = `http://localhost:8123/api/ai/manus/chat?message=${encodedMessage}`
    if (currentChatId.value) {
      url += `&chatId=${encodeURIComponent(currentChatId.value)}`
    }
    
    eventSource.value = new EventSource(url)
    
    eventSource.value.onmessage = (event) => {
      const data = event.data
      if (!data) return
      
      const parsed = parseMessage(data)
      
      // 如果解析返回 null，跳过
      if (!parsed) {
        scrollToBottom()
        return
      }
      
      // 合并连续的思考消息
      if (parsed.type === 'thinking' && messages.value.length > 0) {
        const lastMsg = messages.value[messages.value.length - 1]
        if (lastMsg.type === 'thinking') {
          lastMsg.content += '\n\n' + parsed.content
          scrollToBottom()
          return
        }
      }
      
      // 合并连续的工具消息
      if (parsed.type === 'tool' && messages.value.length > 0) {
        const lastMsg = messages.value[messages.value.length - 1]
        if (lastMsg.type === 'tool' && lastMsg.toolName === parsed.toolName) {
          lastMsg.content = parsed.content
          scrollToBottom()
          return
        }
      }
      
      messages.value.push(parsed)
      scrollToBottom()
    }
    
    eventSource.value.onerror = (error) => {
      console.error('SSE error:', error)
      eventSource.value.close()
      isLoading.value = false
      isExecuting.value = false
      
      // 将所有步骤标记为已完成
      messages.value.forEach(msg => {
        if (msg.type === 'step') {
          msg.isCurrent = false
        }
      })
      
      // 只有在不是等待用户输入且任务未完成的状态下才显示错误
      // 如果是等待用户输入或任务已完成，说明是正常结束，只是连接关闭
      if (!waitingForInput.value && !isTaskCompleted.value) {
        messages.value.push({
          type: 'system',
          content: '连接出现错误，请稍后重试。'
        })
        scrollToBottom()
      }
    }
    
    eventSource.value.onclose = () => {
      isLoading.value = false
      isExecuting.value = false
      // 连接关闭时，将所有步骤标记为已完成
      messages.value.forEach(msg => {
        if (msg.type === 'step') {
          msg.isCurrent = false
        }
      })
    }
  } catch (error) {
    console.error('Error:', error)
    messages.value.push({
      type: 'system',
      content: '发送消息时出现错误。'
    })
    isLoading.value = false
    isExecuting.value = false
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
  isLoading.value = false
  isExecuting.value = false
  waitingForInput.value = false
  isTaskCompleted.value = false  // 重置任务完成状态
  currentStep.value = 0
  currentChatId.value = null  // 清空对话ID
}

// 生成唯一的对话ID
const generateChatId = () => {
  return 'chat_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}

// 提交用户输入的补充信息
const submitUserInput = (index) => {
  const message = messages.value[index]
  if (!message || !message.userInput?.trim()) return
  
  message.submitted = true
  waitingForInput.value = false
  
  // 获取用户输入
  const userResponse = message.userInput.trim()
  
  // 如果是第一次用户输入补充，生成chatId以保持对话记忆
  if (!currentChatId.value) {
    currentChatId.value = generateChatId()
  }
  
  // 继续对话，发送用户输入（isUserInput=true 表示这是补充输入，保留chatId）
  setTimeout(() => {
    sendMessage(userResponse, true)
  }, 100)
}

// 组件卸载时关闭连接
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
