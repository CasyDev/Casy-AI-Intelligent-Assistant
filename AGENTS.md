# CASY AI Agent 项目指南

## 项目概述

CASY AI Agent 是一个基于 Spring AI 框架构建的智能 AI 对话应用系统，专注于恋爱心理领域的问答服务。项目集成了多种 AI 模型接入方式（DashScope、OpenAI、LangChain4j 等），支持 RAG（检索增强生成）、工具调用、对话记忆持久化等高级功能。

### 核心业务场景

- **恋爱咨询专家**：扮演深耕恋爱心理领域的专家，针对单身、恋爱、已婚三种状态提供专属解决方案
- 支持本地知识库问答（Markdown 文档）
- 支持联网搜索、天气查询、网页抓取、文件操作等工具调用

---

## 技术栈

### 核心框架

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 21 | 编程语言 |
| Spring Boot | 3.4.12 | 基础框架 |
| Spring AI | 1.1.2 | AI 应用开发框架 |
| Spring AI Alibaba | 1.1.0.0-RC2 | 阿里云 AI 接入 |

### AI 相关依赖

- **DashScope SDK** (2.21.8)：阿里云灵积模型服务
- **OpenAI Java SDK** (0.32.0)：OpenAI 模型接入
- **LangChain4j** (1.0.0-beta2)：LangChain4j 社区版 DashScope 支持

### 数据存储

- **MySQL**：对话记忆持久化（主数据库）
- **PostgreSQL + pgVector**：向量数据库存储（可选）
- **SimpleVectorStore**：内存向量存储（本地开发）

### 工具库

- **Lombok** (1.18.36)：代码简化
- **Hutool** (5.8.42)：Java 工具集
- **Knife4j** (4.5.0)：API 文档（OpenAPI 3）
- **Jsoup** (1.19.1)：网页抓取
- **iTextPDF** (9.1.0)：PDF 生成
- **Mockito** (5.14.2)：单元测试

---

## 项目结构

```
casy-ai-agent/
├── src/main/java/com/casy/casyaiagent/
│   ├── CasyAiAgentApplication.java    # 应用入口
│   ├── ai/
│   │   └── LoveApp.java               # 核心业务类：AI 对话服务
│   ├── advisor/                       # AI Advisor 组件
│   │   ├── PromptLoggingAdvisor.java  # 提示词日志记录
│   │   ├── ReReadingAdvisor.java      # 重读优化 Advisor
│   │   └── SimpleLoggerAdvisor.java   # 简单日志 Advisor
│   ├── config/                        # 配置类
│   │   ├── ChatMemoryAdvisorConfig.java
│   │   ├── ChatMemoryConfig.java      # 对话记忆配置
│   │   ├── GlobalBeanProcessor.java
│   │   └── ToolRegistration.java      # 工具注册中心
│   ├── constant/                      # 常量定义
│   │   ├── FileConstant.java
│   │   └── Global.java                # 全局变量和 Spring 上下文
│   ├── controller/                    # REST API 控制器
│   │   └── HealthController.java      # 健康检查接口
│   ├── demo/invoke/                   # AI 接入示例代码
│   │   ├── DashScopeSdkInvoke.java    # DashScope SDK 示例
│   │   ├── HttpAiInvoke.java          # HTTP 调用示例
│   │   ├── LangChainAiInvoke.java     # LangChain4j 示例
│   │   ├── OllamaAiInvoke.java        # Ollama 本地模型示例
│   │   ├── OpenAISDKInvoke.java       # OpenAI SDK 示例
│   │   └── SpringAiAiInvoke.java      # Spring AI 示例
│   ├── rag/                           # RAG 相关组件
│   │   ├── component/                 # RAG 组件
│   │   │   ├── BaiduTranslationQueryTransformer.java  # 百度翻译查询转换
│   │   │   ├── LoveAppDocumentLoader.java             # 文档加载器
│   │   │   ├── LoveAppPromptTemplate.java             # 提示词模板
│   │   │   ├── MyDocumentEnricher.java
│   │   │   ├── MyTokenTextSplitter.java               # 文本分片
│   │   │   └── QueryRewriter.java                     # 查询重写器
│   │   ├── config/                    # RAG 配置
│   │   │   ├── LoveAppLocalVectorStoreConfig.java     # 本地向量存储
│   │   │   ├── LoveAppRagCloudAdvisorConfig.java
│   │   │   └── PgVectorVectorStoreConfig.java         # pgVector 配置
│   │   └── factory/                   # 工厂类
│   │       ├── LoveAppContextualQueryAugmenterFactory.java
│   │       └── LoveAppRagCustomAdvisorFactory.java
│   ├── service/                       # 业务服务
│   │   └── BaiduTranslationService.java
│   ├── tool/                          # AI 工具（Tool Calling）
│   │   ├── FileOperationTool.java     # 文件操作
│   │   ├── PDFGenerationTool.java     # PDF 生成
│   │   ├── ResourceDownloadTool.java  # 资源下载
│   │   ├── TerminalOperationTool.java # 终端操作
│   │   ├── WeatherTools.java          # 天气查询
│   │   ├── WebScrapingTool.java       # 网页抓取
│   │   └── WebSearchTool.java         # 联网搜索
│   └── util/                          # 工具类
│       └── GlobalSpringContextInitializer.java
├── src/main/resources/
│   ├── application.yml                # 主配置文件
│   ├── application-local.yml          # 本地开发配置（敏感信息）
│   ├── document/                      # RAG 知识库文档（Markdown）
│   │   ├── 恋爱对象分类推荐清单.md
│   │   ├── 恋爱常见问题和回答 - 单身篇.md
│   │   ├── 恋爱常见问题和回答 - 已婚篇.md
│   │   └── 恋爱常见问题和回答 - 恋爱篇.md
│   ├── META-INF/
│   │   └── spring/
│   │       └── org.springframework.context.ApplicationContextInitializer.imports
│   └── sql/
│       └── schema-postgresql.sql      # PostgreSQL 表结构
├── src/test/                          # 测试代码
├── lib/                               # 本地 JAR 包
│   └── uapi-sdk-java-0.1.10.jar      # UAPI SDK（天气接口）
├── sql/                               # 数据库脚本
│   └── create.sql                     # MySQL 表结构
└── pom.xml                            # Maven 配置
```

---

## 构建与运行

### 环境要求

- JDK 21+
- Maven 3.9+
- MySQL 8.0+
- （可选）PostgreSQL 14+ with pgVector 扩展

### 构建命令

```bash
# 清理并编译
./mvnw clean compile

# 运行测试
./mvnw test

# 打包（包含本地 lib 依赖）
./mvnw clean package

# 跳过测试打包
./mvnw clean package -DskipTests
```

### 运行应用

```bash
# 使用 Maven 运行
./mvnw spring-boot:run

# 或使用 Java 直接运行
java -jar target/casy-ai-agent-0.0.1-SNAPSHOT.jar
```

### 访问接口

- **API 文档**：http://localhost:8123/api/swagger-ui.html
- **健康检查**：http://localhost:8123/api/health
- **OpenAPI Docs**：http://localhost:8123/api/v3/api-docs

---

## 配置说明

### 核心配置（application.yml）

```yaml
spring:
  profiles:
    active: local                    # 激活本地配置
  application:
    name: casy-ai-agent
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/casy-ai?allowMultiQueries=true
    username: root
    password: root
  ai:
    chat:
      memory:
        repository:
          jdbc:
            initialize-schema: always    # 自动初始化表结构

server:
  port: 8123
  servlet:
    context-path: /api
```

### 本地敏感配置（application-local.yml）

⚠️ **该文件包含敏感信息，已被 .gitignore 排除**

```yaml
spring:
  ai:
    dashscope:
      chat:
        options:
          model: qwen-turbo          # 通义千问模型
      api-key: sk-xxx                # DashScope API Key

baidu:
  translate:
    appid: xxx                       # 百度翻译 AppID
    secretKey: xxx                   # 百度翻译密钥

search_api:
  api_key: xxx                       # SearchAPI 搜索 Key

u_api_pro:
  api_key: xxx                       # UAPI 天气接口 Key
```

---

## 数据库表结构

### 对话记忆表（MySQL）

```sql
CREATE TABLE IF NOT EXISTS SPRING_AI_CHAT_MEMORY
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id VARCHAR(255) NOT NULL,
    content         TEXT         NOT NULL,
    type            VARCHAR(50)  NOT NULL,
    `timestamp`     DATETIME     NOT NULL,
    INDEX idx_spring_ai_chat_memory_cid (conversation_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
```

---

## 代码规范

### 命名规范

- **包名**：全小写，如 `com.casy.casyaiagent`
- **类名**：大驼峰，如 `LoveApp`, `WebSearchTool`
- **方法名**：小驼峰，如 `doChat()`, `searchWeb()`
- **常量**：全大写下划线，如 `SYSTEM_PROMPT`, `BASE_URL`

### 注释规范

- 类级别使用多行注释，包含作者、版本、描述
- 方法级别说明功能、参数、返回值
- 关键业务逻辑添加行内注释

```java
/**
 * @author linlin
 * @version 1.0
 * @description: 工具注册类
 * @date 2026/3/7 16:16
 */
@Component
public class LoveApp {
    // 扮演深耕恋爱心理领域的专家
    private static final String SYSTEM_PROMPT = "...";
}
```

### 日志规范

- 使用 SLF4J + Logback
- 日志级别：DEBUG（开发）、INFO（生产）、ERROR（异常）
- 关键 AI 交互使用 PromptLoggingAdvisor 记录

---

## 测试策略

### 测试框架

- **JUnit 5**：单元测试基础
- **Spring Boot Test**：集成测试
- **Mockito**：Mock 测试

### 运行测试

```bash
# 运行所有测试
./mvnw test

# 运行单个测试类
./mvnw test -Dtest=WebSearchToolTest

# 运行并生成报告
./mvnw test surefire-report:report
```

### 测试类结构

```
src/test/java/com/casy/casyaiagent/
├── CasyAiAgentApplicationTests.java       # 上下文加载测试
├── BaiDuTranslateTest.java                # 百度翻译测试
├── LoveAppTest.java                       # 核心业务测试
├── PgVectorVectorStoreConfigTest.java     # 向量存储测试
├── PromptTemplateTest.java                # 提示词模板测试
├── rag/
│   └── DocumentFilterAndSearchTest.java   # 文档检索测试
└── tool/                                  # 工具类测试
    ├── DisableDefaultToolCallingTest.java
    ├── FileOperationToolTest.java
    ├── PDFGenerationToolTest.java
    ├── ResourceDownloadToolTest.java
    ├── TerminalOperationToolTest.java
    ├── WebScrapingToolTest.java
    └── WebSearchToolTest.java
```

---

## 架构设计

### 核心组件交互

```
用户请求 → LoveApp (ChatClient)
              ↓
    ┌─────────┼─────────┐
    ↓         ↓         ↓
Advisor   RAG/Vector   Tools
(记忆)     (知识库)    (工具调用)
    ↓         ↓         ↓
    └─────────┴─────────┘
              ↓
        AI Model (DashScope)
              ↓
         响应返回
```

### Advisor 链

1. **MessageChatMemoryAdvisor**：对话记忆管理
2. **PromptLoggingAdvisor**：请求/响应日志记录
3. **QuestionAnswerAdvisor / RetrievalAugmentationAdvisor**：RAG 增强

### 工具调用（Tool Calling）

通过 `ToolRegistration` 集中注册，支持：

- `WebSearchTool`：联网搜索（SearchAPI）
- `WebScrapingTool`：网页内容抓取
- `FileOperationTool`：本地文件读写
- `PDFGenerationTool`：PDF 文档生成
- `TerminalOperationTool`：终端命令执行
- `ResourceDownloadTool`：网络资源下载

---

## 安全注意事项

### 敏感信息保护

- API Keys 统一配置在 `application-local.yml`（已 gitignore）
- 生产环境应使用环境变量或配置中心
- 本地 JAR 依赖（lib/uapi-sdk-java）已配置为 system scope

### 工具调用安全

- `TerminalOperationTool` 可执行系统命令，生产环境慎用
- 文件操作工具限制在特定目录内
- 所有外部 API 调用应设置超时和重试机制

### 数据库安全

- 使用连接池管理数据库连接
- SQL 表结构自动初始化（`initialize-schema: always`）
- 敏感字段建议加密存储

---

## 部署说明

### 打包注意事项

由于项目包含本地 JAR 依赖（lib/uapi-sdk-java-0.1.10.jar），Maven 配置已处理：

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <includeSystemScope>true</includeSystemScope>  <!-- 包含本地 JAR -->
    </configuration>
</plugin>
```

### 环境变量配置（生产环境建议）

```bash
export DASHSCOPE_API_KEY=xxx
export BAIDU_TRANSLATE_APPID=xxx
export BAIDU_TRANSLATE_SECRET=xxx
export SEARCH_API_KEY=xxx
export UAPI_PRO_KEY=xxx
```

---

## 开发注意事项

### RAG 知识库

- Markdown 文档存放于 `src/main/resources/document/`
- 文档按婚姻状态分类（单身、恋爱、已婚、分类推荐）
- 使用 `LoveAppDocumentLoader` 加载并添加元数据

### 向量存储切换

```java
// 本地内存存储（开发环境）
@Bean VectorStore loveAppVectorStore()

// PostgreSQL pgVector（生产环境）
@Bean VectorStore pgVectorVectorStore()
```

### 对话记忆配置

支持 MySQL 和 PostgreSQL 方言切换，修改 `ChatMemoryConfig.java`：

```java
@Bean
public JdbcChatMemoryRepositoryDialect customDialect() {
    return new MysqlChatMemoryRepositoryDialect();  // MySQL
    // return new PostgresChatMemoryRepositoryDialect();  // PostgreSQL
}
```

---

## 常见问题

### 1. 本地 JAR 找不到

确保 `pom.xml` 中 `includeSystemScope` 设置为 true，且 JAR 文件存在于 `lib/` 目录。

### 2. API Key 无效

检查 `application-local.yml` 是否正确配置，且文件未被 git 跟踪。

### 3. 向量存储初始化失败

确认 DashScope API Key 有效，且网络可访问阿里云服务。

### 4. 数据库表未创建

检查 `initialize-schema: always` 配置，或手动执行 `sql/create.sql`。

---

## 扩展开发

### 添加新的 AI 工具

1. 创建工具类并使用 `@Tool` 注解
2. 在 `ToolRegistration` 中注册
3. 编写单元测试

### 添加新的 Advisor

1. 实现 `CallAdvisor` 接口
2. 在 `LoveApp` 构造函数中添加

### 切换 AI 模型

修改 `application-local.yml` 中的模型名称：

```yaml
spring:
  ai:
    dashscope:
      chat:
        options:
          model: qwen-max  # qwen-turbo / qwen-plus / qwen-max
```
