<template>
  <div class="min-h-screen flex flex-col">
    <!-- 顶部导航 -->
    <header class="bg-kimi-sidebar border-b border-kimi-border">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center space-x-3">
            <div class="w-10 h-10 bg-gradient-to-br from-kimi-primary to-purple-600 rounded-xl flex items-center justify-center">
              <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
              </svg>
            </div>
            <h1 class="text-xl font-bold text-white">CASY AI Agent</h1>
          </div>
          <a href="https://github.com" target="_blank" class="text-kimi-text-secondary hover:text-white transition-colors">
            <svg class="w-6 h-6" fill="currentColor" viewBox="0 0 24 24">
              <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
            </svg>
          </a>
        </div>
      </div>
    </header>

    <!-- 主要内容 -->
    <main class="flex-1 flex items-center justify-center p-4">
      <div class="max-w-4xl w-full">
        <!-- 欢迎语 -->
        <div class="text-center mb-12">
          <h2 class="text-4xl font-bold text-white mb-4">
            选择一个 AI 应用
          </h2>
          <p class="text-kimi-text-secondary text-lg">
            探索 CASY AI 的强大功能，体验智能对话的魅力
          </p>
        </div>

        <!-- 应用卡片网格 -->
        <div class="grid md:grid-cols-2 gap-6">
          <!-- AI 恋爱大师 -->
          <router-link 
            to="/love-app"
            class="group relative bg-kimi-card border border-kimi-border rounded-2xl p-6 hover:border-kimi-primary transition-all duration-300 hover:shadow-xl hover:shadow-kimi-primary/10"
          >
            <div class="absolute inset-0 bg-gradient-to-br from-kimi-primary/5 to-transparent rounded-2xl opacity-0 group-hover:opacity-100 transition-opacity"/>
            
            <div class="relative">
              <div class="w-14 h-14 bg-gradient-to-br from-pink-500 to-rose-600 rounded-xl flex items-center justify-center mb-4 group-hover:scale-110 transition-transform">
                <svg class="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
                </svg>
              </div>
              
              <h3 class="text-xl font-semibold text-white mb-2">AI 恋爱大师</h3>
              <p class="text-kimi-text-secondary mb-4">
                专业的恋爱心理顾问，为你解答感情困惑，提供恋爱建议，助你收获美好爱情。
              </p>
              
              <div class="flex items-center text-kimi-primary text-sm font-medium">
                <span>开始对话</span>
                <svg class="w-4 h-4 ml-1 group-hover:translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
                </svg>
              </div>
            </div>
          </router-link>

          <!-- AI 超级智能体 -->
          <router-link 
            to="/casy-manus"
            class="group relative bg-kimi-card border border-kimi-border rounded-2xl p-6 hover:border-kimi-primary transition-all duration-300 hover:shadow-xl hover:shadow-kimi-primary/10"
          >
            <div class="absolute inset-0 bg-gradient-to-br from-kimi-primary/5 to-transparent rounded-2xl opacity-0 group-hover:opacity-100 transition-opacity"/>
            
            <div class="relative">
              <div class="w-14 h-14 bg-gradient-to-br from-kimi-primary to-purple-600 rounded-xl flex items-center justify-center mb-4 group-hover:scale-110 transition-transform">
                <svg class="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"/>
                </svg>
              </div>
              
              <h3 class="text-xl font-semibold text-white mb-2">AI 超级智能体</h3>
              <p class="text-kimi-text-secondary mb-4">
                强大的任务执行智能体，可调用多种工具，自动规划并执行复杂任务。
              </p>
              
              <div class="flex items-center text-kimi-primary text-sm font-medium">
                <span>开始对话</span>
                <svg class="w-4 h-4 ml-1 group-hover:translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
                </svg>
              </div>
            </div>
          </router-link>
        </div>

        <!-- 特性说明 -->
        <div class="mt-12 grid grid-cols-3 gap-6 text-center">
          <div class="p-4">
            <div class="w-10 h-10 bg-kimi-card rounded-lg flex items-center justify-center mx-auto mb-3">
              <svg class="w-5 h-5 text-kimi-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
              </svg>
            </div>
            <h4 class="text-white font-medium mb-1">实时响应</h4>
            <p class="text-kimi-text-secondary text-sm">SSE 流式传输</p>
          </div>
          <div class="p-4">
            <div class="w-10 h-10 bg-kimi-card rounded-lg flex items-center justify-center mx-auto mb-3">
              <svg class="w-5 h-5 text-kimi-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
              </svg>
            </div>
            <h4 class="text-white font-medium mb-1">安全私密</h4>
            <p class="text-kimi-text-secondary text-sm">会话隔离保护</p>
          </div>
          <div class="p-4">
            <div class="w-10 h-10 bg-kimi-card rounded-lg flex items-center justify-center mx-auto mb-3">
              <svg class="w-5 h-5 text-kimi-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19.428 15.428a2 2 0 00-1.022-.547l-2.384-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z"/>
              </svg>
            </div>
            <h4 class="text-white font-medium mb-1">智能工具</h4>
            <p class="text-kimi-text-secondary text-sm">多种工具调用</p>
          </div>
        </div>
      </div>
    </main>

    <!-- 页脚 -->
    <footer class="bg-kimi-sidebar border-t border-kimi-border py-4">
      <div class="max-w-7xl mx-auto px-4 text-center text-kimi-text-muted text-sm">
        <p>© 2026 CASY AI Agent. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
</script>
