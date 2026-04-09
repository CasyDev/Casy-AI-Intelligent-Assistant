# 🤖 CASY AI Agent - 智能 AI 对话应用系统

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-blue?logo=java" alt="Java 21">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.12-green?logo=spring" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Spring%20AI-1.1.2-orange?logo=spring" alt="Spring AI">
  <img src="https://img.shields.io/badge/Vue.js-3-4FC08D?logo=vue.js" alt="Vue 3">
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License">
</p>

<p align="center">
  <b>基于 Spring AI 的智能对话系统，集成 RAG 知识库、工具调用、多轮记忆等高级功能</b><br>
  <i>专注于恋爱心理领域的 AI 咨询专家，支持联网搜索、天气查询、文件操作等丰富工具</i>
</p>

---

## 📖 项目简介

CASY AI Agent 是一个基于 **Spring AI** 框架构建的企业级智能对话应用系统。项目采用 **Java 21 + Spring Boot 3.4 + Vue 3** 技术栈，集成了多种 AI 大模型接入方式，支持 **RAG（检索增强生成）**、**工具调用（Tool Calling）**、**对话记忆持久化** 等高级功能。

### 🌟 核心定位

系统扮演一位**深耕恋爱心理领域的专家**，针对用户的不同情感状态（单身、恋爱中、已婚）提供个性化的恋爱咨询服务，结合本地知识库和实时联网搜索，给出专业、贴心的建议。

---

## ✨ 项目亮点

### 1. 🤖 ReAct 模式 AI Agent（仿 OpenManus 架构）

CasyManus 采用 **ReAct（Reasoning + Acting）** 模式，仿照 OpenManus 架构设计，实现真正的自主任务规划与执行能力。

**核心机制**：
- **任务规划**：执行前先制定整体计划（Planning → Execution）
- **思考-行动循环**：Think → Act → Observe 循环迭代
- **自我纠错**：三层兜底机制，检测并纠正 AI "口嗨"行为
- **状态管理**：完整的状态机（IDLE → RUNNING → FINISHED/ERROR）

```
Think: 分析当前状态，决定下一步行动
  │
  ▼
Act:   调用工具或生成回复
  │
  ▼
Observe: 观察结果，进入下一轮思考
```

### 2. 🔧 智能工具调用系统（Tool Calling）

系统内置 **8+ 种实用工具**，AI 可根据用户需求自主判断并调用：

| 工具类别 | 工具名称 | 功能描述 |
|---------|---------|---------|
| 🌐 信息获取 | `WebSearchTool` | 联网搜索（SearchAPI），获取实时信息 |
| 🌐 信息获取 | `WebScrapingTool` | 网页内容抓取（Jsoup），解析详情页 |
| 🌐 信息获取 | `AmapTools` | 高德地图服务，地理位置、天气、POI 搜索 |
| 🌐 信息获取 | `ImageSearchTool` | 图片搜索服务 |
| 🌤️ 生活服务 | `WeatherTools` | 天气查询（UAPI），支持全国城市 |
| 📄 文档处理 | `PDFGenerationTool` | PDF 文档生成（iText），支持图文混排 |
| 📁 文件操作 | `FileOperationTool` | 本地文件读写操作 |
| 💻 系统管理 | `TerminalOperationTool` | 终端命令执行 |
| ⬇️ 资源下载 | `ResourceDownloadTool` | 网络资源下载 |

> 💡 **亮点**：所有工具采用原生 Java 实现，无需额外的 Node.js MCP 服务，部署更简单！

### 3. 🧠 RAG 检索增强生成

- **本地知识库**：Markdown 文档存储，支持恋爱领域的专业知识
- **智能分片**：基于 Token 的文本分割策略，优化检索精度
- **向量存储**：支持 PostgreSQL + pgVector 和内存存储双模式
- **查询增强**：百度翻译查询转换、查询重写等高级功能

### 4. 💬 多轮对话与记忆系统

- **对话记忆**：支持 MySQL/PostgreSQL 持久化存储，自动记录对话历史
- **上下文感知**：AI 能理解多轮对话的上下文关系
- **会话管理**：前端 `chatId` 机制，支持多会话并发
- **补充输入**：支持 `requestUserInput` 工具，AI 可主动询问用户补充信息

### 5. ⚡ 实时流式响应（SSE）

- **Server-Sent Events**：AI 回复实时流式展示，无需等待完整响应
- **打字机效果**：逐字显示，提升用户体验
- **步骤可视化**：AI 思考过程、工具调用、执行结果实时展示

### 6. 🎨 现代化前端界面

- **Vue 3 + Vite**：现代化前端技术栈，开发体验优秀
- **实时流式显示**：支持 Markdown 渲染、代码高亮
- **步骤追踪**：AI 执行步骤可视化，带完成状态标识
- **输入优化**：补充信息自动添加提示词前缀，增强 AI 理解

### 7. 🔌 多模型支持

- **阿里云 DashScope**：通义千问系列（qwen-turbo/qwen-plus/qwen-max）
- **OpenAI**：GPT 系列模型支持
- **LangChain4j**：社区版 DashScope 支持
- **Ollama**：本地模型部署支持

---

## 🚀 快速开始

### 环境要求

| 环境 | 版本要求 |
|-----|---------|
| JDK | 21+ |
| Maven | 3.9+ |
| MySQL | 8.0+（可选，支持内存模式） |
| Node.js | 18+（前端开发） |

### 1. 克隆项目

```bash
git clone https://gitee.com/yourusername/casy-ai-agent.git
cd casy-ai-agent
```

### 2. 后端配置

创建 `src/main/resources/application-local.yml`：

```yaml
spring:
  ai:
    dashscope:
      chat:
        options:
          model: qwen-turbo
      api-key: your-dashscope-api-key

baidu:
  translate:
    appid: your-baidu-appid
    secretKey: your-baidu-secret

search_api:
  api_key: your-searchapi-key

u_api_pro:
  api_key: your-uapi-key
```

### 3. 启动后端

```bash
./mvnw clean compile
./mvnw spring-boot:run
```

后端服务将启动在 `http://localhost:8123/api`

### 4. 启动前端

```bash
cd casy-ai-agent-frontend
npm install
npm run dev
```

前端将启动在 `http://localhost:3000`

### 5. 访问应用

- **应用首页**：http://localhost:3000
- **API 文档**：http://localhost:8123/api/swagger-ui.html
- **健康检查**：http://localhost:8123/api/health

---

## 🏗️ 项目架构

### 技术架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                        前端层 (Vue 3)                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │  Chat View  │  │  SSE Stream │  │    Step Visualization   │  │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      控制层 (Controller)                         │
│              SseEmitter 流式响应 / REST API                      │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      业务层 (Service)                            │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │  CasyManus  │  │  LoveApp    │  │    Tool Registration    │  │
│  │  (AI Agent) │  │  (Chat)     │  │    (工具注册中心)        │  │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      AI 层 (Spring AI)                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │   ChatClient│  │  VectorStore│  │    ChatMemory           │  │
│  │   (对话)    │  │  (向量存储)  │  │    (记忆管理)           │  │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │   Advisor   │  │  Tool Calling│  │    RAG Retrieval       │  │
│  │   (拦截器)  │  │  (工具调用)  │  │    (知识检索)          │  │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      模型层 (AI Models)                          │
│     DashScope    │    OpenAI    │    LangChain4j   │   Ollama   │
│   (通义千问)      │   (GPT系列)   │   (链式调用)     │  (本地模型) │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      数据层 (Data Layer)                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │    MySQL    │  │  PostgreSQL │  │    Markdown Docs        │  │
│  │  (对话记忆)  │  │ (pgVector)  │  │    (知识库文档)         │  │
│  └─────────────┘  └─────────────┘  └─────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### 核心组件说明

| 组件 | 说明 | 技术亮点 |
|-----|------|---------|
| **CasyManus** | AI Agent 核心实现 | 仿 OpenManus 架构，ReAct 思考-行动循环 |
| **ReActAgent** | ReAct 模式抽象基类 | Think → Act → Observe 循环 |
| **ToolCallAgent** | 工具调用决策执行 | 三层兜底机制，防止 AI "口嗨" |
| **BaseAgent** | Agent 基础能力 | 状态机管理、循环检测、异常恢复 |
| **ToolRegistration** | 工具注册中心 | 集中管理 8+ 种 AI 工具 |
| **ChatMemory** | 对话记忆管理 | JDBC/MySQL 持久化，多轮对话 |
| **VectorStore** | 向量存储 | 内存/pgVector 双模式，RAG 知识检索 |
| **Advisor 链** | 拦截器链 | PromptLoggingAdvisor、ReReadingAdvisor |

---

## 📁 项目结构

```
casy-ai-agent/
├── 📂 src/main/java/com/casy/casyaiagent/
│   ├── 🚀 CasyAiAgentApplication.java          # 应用入口
│   ├── 🤖 agent/                               # AI Agent 核心
│   │   ├── BaseAgent.java                      # Agent 基类
│   │   └── CasyManus.java                      # 主 Agent 实现
│   ├── 💬 ai/                                  # AI 业务
│   │   └── LoveApp.java                        # 恋爱咨询业务类
│   ├── 🔧 advisor/                             # AI 拦截器
│   │   ├── PromptLoggingAdvisor.java           # 提示词日志
│   │   ├── ReReadingAdvisor.java               # 重读优化
│   │   └── SimpleLoggerAdvisor.java            # 简单日志
│   ├── ⚙️ config/                              # 配置类
│   │   ├── ChatMemoryConfig.java               # 对话记忆配置
│   │   └── ToolRegistration.java               # 工具注册中心
│   ├── 🎮 controller/                          # REST API
│   │   └── AiController.java                   # AI 对话接口
│   ├── 📚 rag/                                 # RAG 组件
│   │   ├── component/                          # RAG 组件
│   │   │   ├── LoveAppDocumentLoader.java      # 文档加载器
│   │   │   ├── MyTokenTextSplitter.java        # 文本分片
│   │   │   └── QueryRewriter.java              # 查询重写
│   │   └── config/                             # RAG 配置
│   ├── 🔨 tool/                                # AI 工具
│   │   ├── AmapTools.java                      # 高德地图
│   │   ├── WebSearchTool.java                  # 联网搜索
│   │   ├── WebScrapingTool.java                # 网页抓取
│   │   ├── WeatherTools.java                   # 天气查询
│   │   ├── ImageSearchTool.java                # 图片搜索
│   │   ├── PDFGenerationTool.java              # PDF 生成
│   │   ├── FileOperationTool.java              # 文件操作
│   │   └── TerminalOperationTool.java          # 终端操作
│   └── 🛠️ util/                                # 工具类
├── 📂 src/main/resources/
│   ├── ⚙️ application.yml                      # 主配置
│   ├── ⚙️ application-local.yml                # 本地配置（gitignore）
│   ├── 📄 document/                            # RAG 知识库
│   │   ├── 恋爱常见问题和回答 - 单身篇.md
│   │   ├── 恋爱常见问题和回答 - 恋爱篇.md
│   │   ├── 恋爱常见问题和回答 - 已婚篇.md
│   │   └── 恋爱对象分类推荐清单.md
│   └── 📋 sql/                                 # 数据库脚本
├── 📂 casy-ai-agent-frontend/                  # 前端项目
│   ├── src/views/CasyManus.vue                 # 主聊天界面
│   └── ...
├── 📂 lib/                                     # 本地 JAR 依赖
│   └── uapi-sdk-java-0.1.10.jar               # UAPI 天气 SDK
├── 📂 sql/                                     # 数据库脚本
│   └── create.sql                              # MySQL 表结构
└── 📋 pom.xml                                  # Maven 配置
```

---

## 🔍 核心功能详解

### 1. CasyManus Agent 架构（仿 OpenManus 设计）

CasyManus 是一个仿照 **OpenManus** 架构设计的 AI 超级智能体，采用 **ReAct（Reasoning + Acting）** 模式，实现了真正的自主任务规划与执行能力。

#### 🧠 ReAct 模式：思考-行动循环

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        ReAct 循环架构                                    │
│                                                                         │
│   ┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐          │
│   │  Think  │────▶│  Act    │────▶│Observe  │────▶│ Think   │          │
│   │  思考   │     │  行动   │     │ 观察    │     │ 再思考  │          │
│   └─────────┘     └─────────┘     └─────────┘     └─────────┘          │
│        ▲                                              │                 │
│        └──────────────────────────────────────────────┘                 │
│                          循环直到任务完成                               │
└─────────────────────────────────────────────────────────────────────────┘
```

**核心组件职责：**

| 组件 | 职责 | 实现类 |
|-----|------|--------|
| **BaseAgent** | 状态管理、执行循环、异常处理 | `BaseAgent` |
| **ReActAgent** | 定义 Think-Act 抽象接口 | `ReActAgent` |
| **ToolCallAgent** | 工具调用决策与执行 | `ToolCallAgent` |
| **CasyManus** | 业务配置与系统集成 | `CasyManus` |

#### 🔄 完整执行流程

```
用户输入
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│  Step 1: 任务规划 (Planning)                                 │
│  ├─ 调用 generateInitialPlan()                               │
│  ├─ AI 分析任务需求，输出执行计划                             │
│  └─ 计划内容：目标、步骤、所需工具、预期成果                   │
└─────────────────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│  Step 2: 执行循环 (ReAct Loop)                               │
│  for step in maxSteps:                                       │
│    ├─ think(): 分析当前状态，决定下一步行动                   │
│    │   ├─ 检查是否陷入循环（重复检测）                        │
│    │   ├─ 检查是否需要用户输入                                │
│    │   ├─ 调用 AI 生成思考结果                                │
│    │   └─ 决策：调用工具 / 完成任务 / 请求输入                 │
│    │                                                          │
│    ├─ act(): 执行决策的行动                                   │
│    │   ├─ 调用相应工具（搜索/抓取/地图等）                    │
│    │   ├─ 获取工具执行结果                                    │
│    │   └─ 更新对话历史                                        │
│    │                                                          │
│    └─ observe: 观察结果，进入下一轮思考                       │
└─────────────────────────────────────────────────────────────┘
    │
    ▼
┌─────────────────────────────────────────────────────────────┐
│  Step 3: 任务结束                                            │
│  ├─ 生成最终总结（如调用过 terminate 工具）                   │
│  ├─ 保存对话历史到 ChatMemory                                │
│  └─ 返回结果给用户                                           │
└─────────────────────────────────────────────────────────────┘
```

#### 🎯 核心特性详解

##### ① 任务规划与执行分离

不同于简单的单轮对话，CasyManus 在接收到任务后**首先进行规划**：

```java
// 第一步：生成整体执行计划
String initialPlan = generateInitialPlan(userPrompt);
// 输出示例：
// 【执行计划】
// 1. 任务目标：查询北京明天天气
// 2. 执行步骤：
//    - 步骤1：调用天气工具获取北京天气数据
//    - 步骤2：整理天气信息，生成友好回复
// 3. 所需工具：WeatherTools
// 4. 预期成果：提供北京明天的温度、天气状况、风力信息
```

**优点**：
- AI 先思考"怎么做"，再执行"做什么"
- 复杂任务可拆解为多个步骤，避免遗漏
- 用户可预览 AI 的执行思路，增强透明度

##### ② 循环思考与自我纠错

```
步骤1: Think → 需要搜索天气 → Act(调用WeatherTool) → 获取结果
步骤2: Think → 需要获取详情 → Act(调用WebScrapingTool) → 获取结果
步骤3: Think → 信息足够 → 生成回复 → 调用terminate
步骤4: Think → 生成最终总结 → 任务完成
```

**循环控制机制**：

| 机制 | 说明 | 阈值 |
|-----|------|------|
| **最大步数限制** | 防止无限循环 | maxSteps = 10 |
| **重复检测** | 检测 AI 是否陷入重复回复 | duplicateThreshold = 2 |
| **连续异常控制** | 防止工具调用异常导致死循环 | maxConsecutiveErrors = 3 |
| **步骤进度检查** | 定期提醒 AI 剩余步骤数 | 每 5 步检查一次 |

##### ③ 三层兜底机制（Self-Correction）

当 AI "口嗨"（只说但不执行）时，系统自动检测并纠正：

```
┌─────────────────────────────────────────────────────────────┐
│  兜底机制1：检测"口头上说调用工具"                           │
│  示例：AI说"调用工具：maps_around_search"但没有真正调用      │
│  处理：添加系统消息提醒 AI 必须真正调用工具                   │
├─────────────────────────────────────────────────────────────┤
│  兜底机制2：检测"口头上请求用户输入"                         │
│  示例：AI说"请告诉我您的目的地..."但没调用requestUserInput   │
│  处理：强制要求调用requestUserInput工具，否则任务无法暂停     │
├─────────────────────────────────────────────────────────────┤
│  兜底机制3：检测"口头上结束任务"                             │
│  示例：AI说"任务完成，结束调用"但没调用terminate             │
│  处理：设置needFinalSummary标记，下一步生成总结后结束         │
└─────────────────────────────────────────────────────────────┘
```

**实现代码示例**：
```java
// 检测 AI 只是口头上说要调用工具
if (isToolCallMentionedButNotExecuted(result)) {
    // 添加系统消息纠正 AI
    getMessageList().add(new SystemMessage("""
        系统提示：你刚才说要调用工具，但实际上工具并未被执行。
        请注意：口头描述"调用工具：xxx"不会让工具执行，必须真正调用工具。
        """));
    return false;  // 让 AI 重新思考并真正调用工具
}
```

##### ④ 智能状态管理

```
┌──────────┐    run()     ┌──────────┐    finish()    ┌──────────┐
│   IDLE   │─────────────▶│ RUNNING  │───────────────▶│ FINISHED │
│  空闲    │              │ 执行中   │                │ 已完成   │
└──────────┘              └──────────┘                └──────────┘
                               │
                               │ error()
                               ▼
                         ┌──────────┐
                         │  ERROR   │
                         │ 错误状态 │
                         └──────────┘
```

**状态流转特点**：
- **会话复用**：通过 `resetState()` 重置状态，实例可重复使用
- **前端感知**：SSE 实时推送状态变化，前端展示执行进度
- **异常恢复**：连续异常检测，防止卡死

##### ⑤ 用户交互暂停机制

当 AI 缺少必要信息时，主动暂停任务请求用户输入：

```
用户：帮我规划旅行
    │
    ▼
AI Think → 缺少目的地、时间、预算信息
    │
    ▼
Act → 调用 requestUserInput(prompt="请提供目的地、时间、预算")
    │
    ▼
状态设置为 FINISHED，SSE 返回等待标记
    │
    ▼
前端显示输入框，用户填写信息
    │
    ▼
用户提交 → 自动添加【用户补充信息】前缀 → 继续任务
```

**交互体验优化**：
- 输入框带上下文提示（显示 AI 的问题）
- 补充信息自动添加前缀，AI 理解更准确
- 任务继续时保留之前所有上下文

### 2. ReAct 循环执行示例

**场景**：用户询问"帮我规划一次北京三日游"

```
┌─────────────────────────────────────────────────────────────────┐
│ Step 1: Planning（任务规划）                                     │
│ AI 分析：用户需要北京三日游规划，需要查询景点、餐厅、天气等信息     │
│ 输出计划：                                                        │
│   1. 搜索北京热门景点                                             │
│   2. 搜索北京特色美食                                             │
│   3. 查询北京天气预报                                             │
│   4. 整合信息生成行程                                             │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Step 2: Think → Act → Observe（第一轮）                          │
│ Think: 需要获取北京热门景点信息                                  │
│   │                                                             │
│   ▼                                                             │
│ Act: 调用 WebSearchTool("北京热门景点推荐")                      │
│   │                                                             │
│   ▼                                                             │
│ Observe: 获取到故宫、天安门、颐和园等景点信息                     │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Step 3: Think → Act → Observe（第二轮）                          │
│ Think: 需要获取北京特色美食信息                                  │
│   │                                                             │
│   ▼                                                             │
│ Act: 调用 WebSearchTool("北京特色美食推荐")                      │
│   │                                                             │
│   ▼                                                             │
│ Observe: 获取到北京烤鸭、炸酱面、豆汁等美食信息                   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Step 4: Think → Act → Observe（第三轮）                          │
│ Think: 需要查询天气以确定穿衣和出行建议                          │
│   │                                                             │
│   ▼                                                             │
│ Act: 调用 WeatherTools.queryWeather("北京")                      │
│   │                                                             │
│   ▼                                                             │
│ Observe: 获取到未来三天天气预报                                   │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│ Step 5: Think → Final Answer（总结）                             │
│ Think: 已收集足够信息，可以生成完整行程规划                        │
│   │                                                             │
│   ▼                                                             │
│ Act: 调用 terminate 工具结束任务                                 │
│   │                                                             │
│   ▼                                                             │
│ Generate: 整合所有信息，生成详细的三日游攻略                      │
└─────────────────────────────────────────────────────────────────┘
```

### 3. RAG 知识检索流程

```
用户问题
    │
    ▼
Query Rewriter（查询重写）
    │
    ▼
VectorStore 相似度检索
    │
    ▼
Document Loader 加载相关文档
    │
    ▼
Prompt Augmentation（提示增强）
    │
    ▼
增强后的 Prompt 发送给 AI
```

### 4. 补充输入机制

当 AI 需要更多信息时，会调用 `requestUserInput` 工具：

```
AI：请告诉我您的旅行目的地、出行日期和预算范围
    │
    ▼
前端显示输入框，用户输入："上海，6月18日，7000元"
    │
    ▼
自动添加前缀："【用户补充信息】请告诉我您的旅行目的地...
              用户回答：上海，6月18日，7000元"
    │
    ▼
发送给 AI 继续处理
```

---

## 🛠️ 部署说明

### 生产环境配置

#### 1. 数据库配置

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/casy-ai?allowMultiQueries=true
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  ai:
    chat:
      memory:
        repository:
          jdbc:
            initialize-schema: never  # 生产环境手动初始化
```

#### 2. 向量存储切换

```java
// 生产环境使用 pgVector
@Bean
public VectorStore pgVectorVectorStore(...) {
    return PgVectorStore.builder(...)
        .initializeSchema(true)
        .build();
}
```

#### 3. 前端打包

```bash
cd casy-ai-agent-frontend
npm run build
# 产物在 dist/ 目录
```

#### 4. Nginx 配置

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    location / {
        root /path/to/casy-ai-agent-frontend/dist;
        try_files $uri $uri/ /index.html;
    }
    
    location /api/ {
        proxy_pass http://localhost:8123/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

## 📸 界面预览

### 主聊天界面

> 这里可以放置项目截图

### 步骤追踪

> AI 执行步骤实时展示

### 工具调用展示

> 工具执行过程和结果可视化

---

## 🛡️ 安全与优化

### 已实施的安全措施

| 措施 | 说明 |
|-----|------|
| API Key 隔离 | 敏感配置放在 `application-local.yml`，已 gitignore |
| SQL 注入防护 | 使用 Spring Data JPA，参数化查询 |
| XSS 防护 | 前端 Vue 自动转义，后端输入校验 |
| 会话管理 | `chatId` 机制，支持会话清理 |

### 性能优化

- **连接池**：HikariCP 数据库连接池
- **缓存**：Caffeine 本地缓存（可选）
- **异步处理**：SSE 流式响应，减少等待时间
- **会话复用**：`ConcurrentHashMap` 缓存 Agent 实例

---

## 📝 API 文档

### 主要接口

#### 1. AI 对话（SSE 流式）

```http
GET /api/manus/chat?message={message}&chatId={chatId}
```

**参数**：
- `message`：用户消息
- `chatId`：会话 ID（前端生成）

**响应**：`text/event-stream`

#### 2. 删除会话

```http
GET /api/manus/delete/chat?chatId={chatId}
```

#### 3. 健康检查

```http
GET /api/health
```

完整 API 文档请访问：`http://localhost:8123/api/swagger-ui.html`

---

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

---

## 📄 开源协议

本项目基于 [MIT](LICENSE) 协议开源。

---

## 📧 联系方式

- **作者**：casy
- **邮箱**：3468389387@example.com
- **Gitee**：[https://gitee.com/yourusername/casy-ai-agent](https://gitee.com/yourusername/casy-ai-agent)

---

<p align="center">
  <b>如果这个项目对你有帮助，请给个 ⭐ Star 支持一下！</b>
</p>

<p align="center">
  Made with ❤️ by CASY Team
</p>
