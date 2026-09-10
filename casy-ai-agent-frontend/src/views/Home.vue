<template>
  <div class="min-h-screen flex flex-col">
    <!-- 顶部导航 -->
    <header class="bg-kimi-sidebar border-b border-kimi-border">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
        <div class="flex items-center justify-between">
          <div class="flex items-center space-x-3">
            <div
                class="w-10 h-10 bg-gradient-to-br from-kimi-primary to-purple-600 rounded-xl flex items-center justify-center">
              <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
              </svg>
            </div>
            <div>
              <h1 class="text-xl font-bold text-white">CASY AI Agent</h1>
              <p class="text-xs text-kimi-text-secondary">基于 Spring AI 的企业级智能体平台</p>
            </div>
          </div>
          <a href="https://gitee.com/linlinyes/casy-ai-agent" target="_blank"
             class="text-kimi-text-secondary hover:text-white transition-colors flex items-center space-x-2">
            <img src="@/assets/giteeIcon.png" style="width: 20px; height: 20px;" />
            <span class="text-sm">Gitee</span>
          </a>
        </div>
      </div>
    </header>

    <!-- 主要内容 -->
    <main class="flex-1">
      <!-- Hero 区域 -->
      <section class="relative py-16 overflow-hidden">
        <!-- 背景装饰 -->
        <div class="absolute inset-0 overflow-hidden">
          <div class="absolute -top-40 -right-40 w-80 h-80 bg-kimi-primary/10 rounded-full blur-3xl"></div>
          <div class="absolute -bottom-40 -left-40 w-80 h-80 bg-purple-500/10 rounded-full blur-3xl"></div>
        </div>

        <div class="relative max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
          <!-- 欢迎语 -->
          <div class="text-center mb-12">
            <div class="inline-flex items-center px-4 py-2 bg-kimi-card border border-kimi-border rounded-full mb-6">
              <span class="w-2 h-2 bg-green-500 rounded-full mr-2 animate-pulse"></span>
              <span class="text-sm text-kimi-text-secondary">Spring AI · ReAct 架构 · 工具调用</span>
            </div>
            <h2 class="text-5xl font-bold text-white mb-6">
              选择一个 AI 应用
            </h2>
            <p class="text-kimi-text-secondary text-lg max-w-2xl mx-auto">
              探索 CASY AI 的强大功能，体验基于 ReAct 架构的智能对话与自主任务执行
            </p>
          </div>

          <!-- 应用卡片网格 -->
          <div class="grid md:grid-cols-3 gap-8 mb-16">
            <!-- AI 恋爱大师 -->
            <router-link
                to="/love-app"
                class="group relative bg-kimi-card border border-kimi-border rounded-2xl p-8 hover:border-pink-500/50 transition-all duration-300 hover:shadow-2xl hover:shadow-pink-500/10"
            >
              <div
                  class="absolute inset-0 bg-gradient-to-br from-pink-500/5 to-transparent rounded-2xl opacity-0 group-hover:opacity-100 transition-opacity"/>

              <div class="relative">
                <div class="flex items-start justify-between mb-6">
                  <div
                      class="w-16 h-16 bg-gradient-to-br from-pink-500 to-rose-600 rounded-2xl flex items-center justify-center group-hover:scale-110 transition-transform shadow-lg shadow-pink-500/20">
                    <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
                    </svg>
                  </div>
                  <div class="flex gap-2">
                    <span class="px-3 py-1 bg-pink-500/10 text-pink-400 text-xs rounded-full border border-pink-500/20">RAG</span>
                    <span class="px-3 py-1 bg-pink-500/10 text-pink-400 text-xs rounded-full border border-pink-500/20">记忆</span>
                  </div>
                </div>

                <h3 class="text-2xl font-semibold text-white mb-3">AI 恋爱大师</h3>
                <p class="text-kimi-text-secondary mb-6 leading-relaxed">
                  专业的恋爱心理顾问，基于 RAG 知识库技术，为你解答感情困惑，提供恋爱建议。支持多轮对话记忆，懂你更贴心。
                </p>

                <div class="flex items-center text-pink-400 text-sm font-medium">
                  <span>开始对话</span>
                  <svg class="w-4 h-4 ml-1 group-hover:translate-x-1 transition-transform" fill="none"
                       stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
                  </svg>
                </div>
              </div>
            </router-link>

            <!-- 企业智能客服 -->
            <router-link
                to="/customer-service"
                class="group relative bg-kimi-card border border-kimi-border rounded-2xl p-8 hover:border-cyan-500/50 transition-all duration-300 hover:shadow-2xl hover:shadow-cyan-500/10"
            >
              <div
                  class="absolute inset-0 bg-gradient-to-br from-cyan-500/5 to-transparent rounded-2xl opacity-0 group-hover:opacity-100 transition-opacity"/>

              <div class="relative">
                <div class="flex items-start justify-between mb-6">
                  <div
                      class="w-16 h-16 bg-gradient-to-br from-cyan-500 to-teal-600 rounded-2xl flex items-center justify-center group-hover:scale-110 transition-transform shadow-lg shadow-cyan-500/20">
                    <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"/>
                    </svg>
                  </div>
                  <div class="flex gap-2">
                    <span class="px-3 py-1 bg-cyan-500/10 text-cyan-400 text-xs rounded-full border border-cyan-500/20">RAG</span>
                    <span class="px-3 py-1 bg-cyan-500/10 text-cyan-400 text-xs rounded-full border border-cyan-500/20">知识库</span>
                  </div>
                </div>

                <h3 class="text-2xl font-semibold text-white mb-3">企业智能客服</h3>
                <p class="text-kimi-text-secondary mb-6 leading-relaxed">
                  星河云智能客服，基于 Markdown 知识库做 RAG 检索增强。咨询产品、账号、套餐、售后与合规政策，回答有据可依。
                </p>

                <div class="flex items-center text-cyan-400 text-sm font-medium">
                  <span>开始对话</span>
                  <svg class="w-4 h-4 ml-1 group-hover:translate-x-1 transition-transform" fill="none"
                       stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
                  </svg>
                </div>
              </div>
            </router-link>

            <!-- AI 超级智能体 -->
            <router-link
                to="/casy-manus"
                class="group relative bg-kimi-card border border-kimi-border rounded-2xl p-8 hover:border-kimi-primary/50 transition-all duration-300 hover:shadow-2xl hover:shadow-kimi-primary/10"
            >
              <div
                  class="absolute inset-0 bg-gradient-to-br from-kimi-primary/5 to-transparent rounded-2xl opacity-0 group-hover:opacity-100 transition-opacity"/>

              <!-- 推荐标签 -->
              <div class="absolute -top-3 right-6">
                <span
                    class="px-3 py-1 bg-gradient-to-r from-kimi-primary to-purple-600 text-white text-xs font-medium rounded-full shadow-lg">
                  强烈推荐
                </span>
              </div>

              <div class="relative">
                <div class="flex items-start justify-between mb-6">
                  <div
                      class="w-16 h-16 bg-gradient-to-br from-kimi-primary to-purple-600 rounded-2xl flex items-center justify-center group-hover:scale-110 transition-transform shadow-lg shadow-kimi-primary/20">
                    <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                            d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"/>
                    </svg>
                  </div>
                  <div class="flex gap-2 flex-wrap justify-end">
                    <span
                        class="px-3 py-1 bg-kimi-primary/10 text-kimi-primary text-xs rounded-full border border-kimi-primary/20">ReAct</span>
                    <span
                        class="px-3 py-1 bg-kimi-primary/10 text-kimi-primary text-xs rounded-full border border-kimi-primary/20">工具调用</span>
                    <span
                        class="px-3 py-1 bg-kimi-primary/10 text-kimi-primary text-xs rounded-full border border-kimi-primary/20">SSE</span>
                  </div>
                </div>

                <h3 class="text-2xl font-semibold text-white mb-3">AI 超级智能体</h3>
                <p class="text-kimi-text-secondary mb-6 leading-relaxed">
                  仿 OpenManus 架构的智能体，采用 ReAct 模式实现自主任务规划与执行。支持 8+ 种工具调用，自动规划步骤，实时流式响应。
                </p>

                <div class="flex items-center text-kimi-primary text-sm font-medium">
                  <span>开始对话</span>
                  <svg class="w-4 h-4 ml-1 group-hover:translate-x-1 transition-transform" fill="none"
                       stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"/>
                  </svg>
                </div>
              </div>
            </router-link>
          </div>
        </div>
      </section>

      <!-- 核心架构展示 -->
      <section class="py-16 bg-kimi-sidebar/50 border-y border-kimi-border">
        <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="text-center mb-12">
            <h3 class="text-2xl font-bold text-white mb-3">核心架构</h3>
            <p class="text-kimi-text-secondary">基于 Spring AI 的企业级智能体架构设计</p>
          </div>

          <!-- ReAct 流程图 -->
          <div class="relative">
            <!-- 连接线 -->
            <div
                class="hidden md:block absolute top-1/2 left-0 right-0 h-0.5 bg-gradient-to-r from-transparent via-kimi-primary/30 to-transparent -translate-y-1/2"></div>

            <div class="grid md:grid-cols-4 gap-6 relative">
              <!-- Think -->
              <div
                  class="bg-kimi-card border border-kimi-border rounded-xl p-6 text-center hover:border-kimi-primary/30 transition-colors group">
                <div
                    class="w-14 h-14 bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl flex items-center justify-center mx-auto mb-4 shadow-lg shadow-blue-500/20 group-hover:scale-110 transition-transform">
                  <svg class="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z"/>
                  </svg>
                </div>
                <h4 class="text-white font-semibold mb-2">Think 思考</h4>
                <p class="text-kimi-text-secondary text-sm">分析任务需求，制定执行计划，决策下一步行动</p>
              </div>

              <!-- Act -->
              <div
                  class="bg-kimi-card border border-kimi-border rounded-xl p-6 text-center hover:border-kimi-primary/30 transition-colors group">
                <div
                    class="w-14 h-14 bg-gradient-to-br from-kimi-primary to-purple-600 rounded-xl flex items-center justify-center mx-auto mb-4 shadow-lg shadow-kimi-primary/20 group-hover:scale-110 transition-transform">
                  <svg class="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M13 10V3L4 14h7v7l9-11h-7z"/>
                  </svg>
                </div>
                <h4 class="text-white font-semibold mb-2">Act 行动</h4>
                <p class="text-kimi-text-secondary text-sm">调用搜索、地图、天气等 8+ 种工具执行任务</p>
              </div>

              <!-- Observe -->
              <div
                  class="bg-kimi-card border border-kimi-border rounded-xl p-6 text-center hover:border-kimi-primary/30 transition-colors group">
                <div
                    class="w-14 h-14 bg-gradient-to-br from-green-500 to-emerald-600 rounded-xl flex items-center justify-center mx-auto mb-4 shadow-lg shadow-green-500/20 group-hover:scale-110 transition-transform">
                  <svg class="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"/>
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"/>
                  </svg>
                </div>
                <h4 class="text-white font-semibold mb-2">Observe 观察</h4>
                <p class="text-kimi-text-secondary text-sm">获取工具执行结果，分析当前状态</p>
              </div>

              <!-- Loop -->
              <div
                  class="bg-kimi-card border border-kimi-border rounded-xl p-6 text-center hover:border-kimi-primary/30 transition-colors group">
                <div
                    class="w-14 h-14 bg-gradient-to-br from-orange-500 to-red-600 rounded-xl flex items-center justify-center mx-auto mb-4 shadow-lg shadow-orange-500/20 group-hover:scale-110 transition-transform">
                  <svg class="w-7 h-7 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/>
                  </svg>
                </div>
                <h4 class="text-white font-semibold mb-2">Loop 循环</h4>
                <p class="text-kimi-text-secondary text-sm">循环迭代直到任务完成，支持最多 10 步深度推理</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 技术亮点 -->
      <section class="py-16">
        <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="text-center mb-12">
            <h3 class="text-2xl font-bold text-white mb-3">技术亮点</h3>
            <p class="text-kimi-text-secondary">企业级智能体平台的核心技术能力</p>
          </div>

          <div class="grid md:grid-cols-3 gap-6">
            <!-- 工具调用 -->
            <div
                class="bg-kimi-card border border-kimi-border rounded-xl p-6 hover:border-kimi-primary/30 transition-all group">
              <div class="flex items-center mb-4">
                <div class="w-10 h-10 bg-kimi-primary/10 rounded-lg flex items-center justify-center mr-3">
                  <svg class="w-5 h-5 text-kimi-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M19.428 15.428a2 2 0 00-1.022-.547l-2.384-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z"/>
                  </svg>
                </div>
                <h4 class="text-white font-semibold">8+ 种工具调用</h4>
              </div>
              <p class="text-kimi-text-secondary text-sm leading-relaxed">
                支持联网搜索、网页抓取、高德地图、天气查询、PDF 生成等多种工具，AI 自主决策调用。
              </p>
            </div>

            <!-- SSE 流式 -->
            <div
                class="bg-kimi-card border border-kimi-border rounded-xl p-6 hover:border-kimi-primary/30 transition-all group">
              <div class="flex items-center mb-4">
                <div class="w-10 h-10 bg-kimi-primary/10 rounded-lg flex items-center justify-center mr-3">
                  <svg class="w-5 h-5 text-kimi-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M13 10V3L4 14h7v7l9-11h-7z"/>
                  </svg>
                </div>
                <h4 class="text-white font-semibold">SSE 实时流式</h4>
              </div>
              <p class="text-kimi-text-secondary text-sm leading-relaxed">
                基于 Server-Sent Events 的实时流式响应，打字机效果展示，秒级响应体验。
              </p>
            </div>

            <!-- 对话记忆 -->
            <div
                class="bg-kimi-card border border-kimi-border rounded-xl p-6 hover:border-kimi-primary/30 transition-all group">
              <div class="flex items-center mb-4">
                <div class="w-10 h-10 bg-kimi-primary/10 rounded-lg flex items-center justify-center mr-3">
                  <svg class="w-5 h-5 text-kimi-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
                  </svg>
                </div>
                <h4 class="text-white font-semibold">对话记忆持久化</h4>
              </div>
              <p class="text-kimi-text-secondary text-sm leading-relaxed">
                支持 MySQL/PostgreSQL 持久化存储，多轮对话上下文连贯，记忆不丢失。
              </p>
            </div>

            <!-- RAG 知识库 -->
            <div
                class="bg-kimi-card border border-kimi-border rounded-xl p-6 hover:border-kimi-primary/30 transition-all group">
              <div class="flex items-center mb-4">
                <div class="w-10 h-10 bg-kimi-primary/10 rounded-lg flex items-center justify-center mr-3">
                  <svg class="w-5 h-5 text-kimi-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
                  </svg>
                </div>
                <h4 class="text-white font-semibold">RAG 知识检索</h4>
              </div>
              <p class="text-kimi-text-secondary text-sm leading-relaxed">
                基于向量数据库的 RAG 检索增强，支持 Markdown 知识库，专业领域问答更精准。
              </p>
            </div>

            <!-- 多模型支持 -->
            <div
                class="bg-kimi-card border border-kimi-border rounded-xl p-6 hover:border-kimi-primary/30 transition-all group">
              <div class="flex items-center mb-4">
                <div class="w-10 h-10 bg-kimi-primary/10 rounded-lg flex items-center justify-center mr-3">
                  <svg class="w-5 h-5 text-kimi-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M19.428 15.428a2 2 0 00-1.022-.547l-2.384-.477a6 6 0 00-3.86.517l-.318.158a6 6 0 01-3.86.517L6.05 15.21a2 2 0 00-1.806.547M8 4h8l-1 1v5.172a2 2 0 00.586 1.414l5 5c1.26 1.26.367 3.414-1.415 3.414H4.828c-1.782 0-2.674-2.154-1.414-3.414l5-5A2 2 0 009 10.172V5L8 4z"/>
                  </svg>
                </div>
                <h4 class="text-white font-semibold">多模型接入</h4>
              </div>
              <p class="text-kimi-text-secondary text-sm leading-relaxed">
                支持阿里云 DashScope、OpenAI、LangChain4j、Ollama 本地模型等多种接入方式。
              </p>
            </div>

            <!-- 自我纠错 -->
            <div
                class="bg-kimi-card border border-kimi-border rounded-xl p-6 hover:border-kimi-primary/30 transition-all group">
              <div class="flex items-center mb-4">
                <div class="w-10 h-10 bg-kimi-primary/10 rounded-lg flex items-center justify-center mr-3">
                  <svg class="w-5 h-5 text-kimi-primary" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                          d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                  </svg>
                </div>
                <h4 class="text-white font-semibold">三层兜底机制</h4>
              </div>
              <p class="text-kimi-text-secondary text-sm leading-relaxed">
                智能检测 AI "口嗨"行为，自动纠正，确保工具真正被执行，任务可靠完成。
              </p>
            </div>
          </div>
        </div>
      </section>

      <!-- 技术栈展示 -->
      <section class="py-16 bg-kimi-sidebar/50 border-y border-kimi-border">
        <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="text-center mb-10">
            <h3 class="text-2xl font-bold text-white mb-3">技术栈</h3>
            <p class="text-kimi-text-secondary">基于业界领先的技术构建</p>
          </div>

          <div class="flex flex-wrap justify-center gap-4">
            <div
                class="px-4 py-2 bg-kimi-card border border-kimi-border rounded-lg text-kimi-text-secondary text-sm hover:border-kimi-primary/30 transition-colors">
              Java 21
            </div>
            <div
                class="px-4 py-2 bg-kimi-card border border-kimi-border rounded-lg text-kimi-text-secondary text-sm hover:border-kimi-primary/30 transition-colors">
              Spring Boot 3.4
            </div>
            <div
                class="px-4 py-2 bg-kimi-card border border-kimi-border rounded-lg text-kimi-text-secondary text-sm hover:border-kimi-primary/30 transition-colors">
              Spring AI 1.1
            </div>
            <div
                class="px-4 py-2 bg-kimi-card border border-kimi-border rounded-lg text-kimi-text-secondary text-sm hover:border-kimi-primary/30 transition-colors">
              Vue 3
            </div>
            <div
                class="px-4 py-2 bg-kimi-card border border-kimi-border rounded-lg text-kimi-text-secondary text-sm hover:border-kimi-primary/30 transition-colors">
              Vite
            </div>
            <div
                class="px-4 py-2 bg-kimi-card border border-kimi-border rounded-lg text-kimi-text-secondary text-sm hover:border-kimi-primary/30 transition-colors">
              Tailwind CSS
            </div>
            <div
                class="px-4 py-2 bg-kimi-card border border-kimi-border rounded-lg text-kimi-text-secondary text-sm hover:border-kimi-primary/30 transition-colors">
              MySQL
            </div>
            <div
                class="px-4 py-2 bg-kimi-card border border-kimi-border rounded-lg text-kimi-text-secondary text-sm hover:border-kimi-primary/30 transition-colors">
              pgVector
            </div>
            <div
                class="px-4 py-2 bg-kimi-card border border-kimi-border rounded-lg text-kimi-text-secondary text-sm hover:border-kimi-primary/30 transition-colors">
              DashScope
            </div>
            <div
                class="px-4 py-2 bg-kimi-card border border-kimi-border rounded-lg text-kimi-text-secondary text-sm hover:border-kimi-primary/30 transition-colors">
              OpenAI
            </div>
          </div>
        </div>
      </section>
    </main>

    <!-- 页脚 -->
    <footer class="bg-kimi-sidebar border-t border-kimi-border py-6">
      <div class="max-w-7xl mx-auto px-4 text-center">
        <p class="text-kimi-text-muted text-sm mb-2">© 2026 CASY AI Agent. All rights reserved.</p>
        <p class="text-kimi-text-muted text-xs">基于 Spring AI 构建 · 仿 OpenManus 架构 · 开源智能体平台</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
</script>
