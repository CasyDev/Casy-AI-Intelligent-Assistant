# 前端部署说明

## 环境配置

### 1. 开发环境

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

开发服务器默认在 `http://localhost:3000` 启动，会自动代理 API 请求到 `http://localhost:8123`。

### 2. 生产环境

#### 方案一：同源部署（推荐）

前端打包后部署到后端静态资源目录：

```bash
# 打包（使用生产环境配置）
npm run build

# 将 dist 目录内容复制到后端 static 目录
```

此时 `.env.production` 中的 `VITE_API_BASE_URL` 保持为空：
```env
VITE_API_BASE_URL=
```

#### 方案二：独立部署（前后端分离）

如果前端和后端分别部署：

```bash
# 1. 修改生产环境配置
cp .env.production .env.production.local
```

编辑 `.env.production.local`：
```env
VITE_API_BASE_URL=http://your-api-domain.com:8123
```

```bash
# 2. 打包
npm run build

# 3. 部署 dist 目录到任意静态服务器
```

## 配置文件说明

| 文件 | 说明 |
|------|------|
| `.env.development` | 开发环境变量 |
| `.env.production` | 生产环境变量 |
| `src/config/index.js` | 配置文件入口 |

## 环境变量

| 变量名 | 说明 | 开发环境默认值 | 生产环境建议 |
|--------|------|----------------|--------------|
| `VITE_API_BASE_URL` | API 基础地址 | `http://localhost:8123` | 空（同源）或实际地址 |
| `VITE_API_PREFIX` | API 前缀 | `/api` | `/api` |
| `VITE_APP_TITLE` | 应用标题 | `CASY AI Agent - Dev` | `CASY AI Agent` |

## 注意事项

1. **生产环境使用 HTTP 还是 HTTPS**
   - 如果前端使用 HTTPS，后端 API 也必须使用 HTTPS，否则会被浏览器拦截
   - 修改 `VITE_API_BASE_URL` 为 `https://your-domain.com`

2. **跨域问题**
   - 同源部署：无跨域问题
   - 独立部署：需要后端配置 CORS，或前端使用代理

3. **WebSocket/SSE 连接**
   - 本应用使用 SSE（Server-Sent Events）与后端通信
   - 确保部署环境支持长连接

## Docker 部署示例

```dockerfile
# Dockerfile
FROM node:18-alpine as builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

```nginx
# nginx.conf - 方案一：反向代理到后端
server {
    listen 80;
    
    location / {
        root /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://backend:8123;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```
