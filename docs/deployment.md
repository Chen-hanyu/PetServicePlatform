# 部署说明文档

## 概述

本项目采用前后端分离部署架构：
- **前端**：部署到 **Vercel**（自动构建、HTTPS、Git 推送自动部署）
- **后端**：部署到 **Railway**（Docker 容器化部署）
- **数据库**：由后端服务管理（如 Railway 内置数据库或外部 MySQL）

## 部署架构

```
用户浏览器
    │
    ▼
┌─────────────────────┐
│   Vercel (前端)      │
│   Vue 3 + Vite      │
│   vercel.json 配置   │
└─────────┬───────────┘
          │ /api/* 代理
          ▼
┌─────────────────────┐
│  Railway (后端)      │
│  Spring Boot 3      │
│  Docker 容器化       │
└─────────┬───────────┘
          │
          ▼
┌─────────────────────┐
│  MySQL 数据库        │
│  (Railway 或外部)    │
└─────────────────────┘
```

---

## 1. 前端部署（Vercel）

### 1.1 前置条件

- GitHub 仓库已推送本项目代码
- 拥有 Vercel 账号（可通过 GitHub 登录）

### 1.2 部署步骤

#### 方式一：通过 Vercel Dashboard 导入（推荐）

1. 打开 [Vercel Dashboard](https://vercel.com/dashboard)
2. 点击 **"Add New..." → "Project"**
3. 选择你的 GitHub 仓库（`PetServicePlatform`）
4. 在配置页面中：
   - **Framework Preset**: 选择 `Vue.js`
   - **Root Directory**: `frontend`（通过 `vercel.json` 中的 `rootDirectory` 指定）
   - **Build Command**: `npm run build`
   - **Output Directory**: `dist`
   - **Install Command**: `npm install`
5. 点击 **"Environment Variables"** 添加环境变量（见下方）
6. 点击 **"Deploy"** 开始部署

#### 方式二：通过 Vercel CLI 部署

```bash
# 安装 Vercel CLI
npm install -g vercel

# 登录 Vercel
vercel login

# 在项目根目录部署
vercel --prod
```

### 1.3 环境变量配置

在 Vercel 项目设置中添加以下环境变量：

| 变量名 | 说明 | 示例值 |
|--------|------|--------|
| `VITE_API_BASE_URL` | 后端 API 地址 | `https://your-backend.railway.app` |

> **注意**：`vercel.json` 中的 API 代理地址也需要更新为实际的后端 Railway 地址。

### 1.4 自动部署配置

Vercel 默认会自动部署：
- 推送代码到 `main` 分支时自动触发部署
- 创建 PR 时自动生成预览部署（Preview Deployment）
- 可在 Vercel 项目 Settings → Git 中调整部署分支

### 1.5 自定义域名（可选）

1. 在 Vercel 项目 Settings → Domains 中添加域名
2. 在域名 DNS 管理中添加 CNAME 记录指向 `cname.vercel-dns.com`
3. Vercel 会自动申请和续期 SSL 证书

---

## 2. 后端部署（Railway）

### 2.1 平台选择

- **平台**：Railway
- **部署方式**：Docker（基于现有 backend/Dockerfile）
- **数据库**：Railway 提供的 MySQL 插件 / 外部 MySQL 服务

### 2.2 配置文件

项目根目录下的 `railway.toml` 定义了 Railway 部署配置：

```toml
[build]
  builder = "DOCKERFILE"
  dockerfilePath = "backend/Dockerfile"
  buildContext = "backend"

[deploy]
  healthcheckPath = "/health"
  healthcheckTimeout = 30
  restartPolicyType = "ON_FAILURE"
  restartPolicyMaxRetries = 3
  port = 8080
```

### 2.3 部署步骤

1. 打开 [Railway Dashboard](https://railway.app/dashboard)
2. 点击 **"New Project" → "Deploy from GitHub repo"**
3. 选择仓库，Railway 会自动检测 `railway.toml` 并使用 Dockerfile 构建
4. 配置环境变量（见下方）
5. 部署完成后获取分配的域名

### 2.4 环境变量配置

在 Railway Dashboard 中配置以下环境变量：

| 变量名 | 说明 | 示例值 |
|--------|------|--------|
| `SERVER_PORT` | 服务端口 | 8080 |
| `SPRING_DATASOURCE_URL` | 数据库连接 URL | jdbc:mysql://host:3306/pet_service_platform |
| `SPRING_DATASOURCE_USERNAME` | 数据库用户名 | root |
| `SPRING_DATASOURCE_PASSWORD` | 数据库密码 | your-password |
| `JWT_SECRET` | JWT 签名密钥 | your-jwt-secret |
| `JWT_EXPIRATION_SECONDS` | JWT 过期时间（秒） | 7200 |
| `FILE_STORAGE_TYPE` | 文件存储类型 | local |
| `FILE_STORAGE_LOCAL_PATH` | 本地文件存储路径 | uploads/ |
| `DEEPSEEK_API_KEY` | DeepSeek AI API 密钥（可选） | your-api-key |
| `AI_BASE_URL` | AI API 基础 URL | https://api.deepseek.com |
| `AI_MODEL` | AI 模型 | deepseek-chat |
| `SPRING_SQL_INIT_MODE` | SQL 初始化模式，首次初始化可设 `always`，稳定生产建议 `never` | never |
| `VERIFY_CODE_ALLOW_DEFAULT_CODE` | 是否允许默认验证码，生产建议关闭 | false |
| `APP_CORS_ALLOWED_ORIGIN_PATTERNS` | 允许跨域来源，多个来源用英文逗号分隔 | https://pet-service-platform-7shx.vercel.app,https://*.vercel.app |

### 2.5 自动部署配置

1. 在 Railway 中连接 GitHub 仓库
2. 选择 PetServicePlatform 仓库
3. 配置部署分支为 main
4. Railway 会自动检测 railway.toml 并使用 Dockerfile 构建
5. 每次推送到 main 分支时自动触发部署

### 2.6 数据库配置

推荐使用 Railway 提供的 MySQL 插件：

1. 在 Railway Dashboard 中创建 MySQL 插件
2. 获取数据库连接信息（主机、端口、用户名、密码）
3. 在 Railway 中执行数据库初始化脚本：`backend/src/main/resources/sql/schema.sql` 和 `seed.sql`
4. 将数据库连接信息配置为环境变量

---

## 3. 前后端联调配置

### 3.1 更新前端 API 代理地址

部署后，需要将 `vercel.json` 中的 API 代理地址更新为实际的后端 Railway 地址：

```json
{
  "rewrites": [
    {
      "source": "/api/(.*)",
      "destination": "https://your-actual-backend.railway.app/api/$1"
    }
  ]
}
```

### 3.2 验证部署

1. 访问前端 Vercel 域名（如 `https://pet-service-platform.vercel.app`）
2. 确认页面正常加载
3. 测试登录、注册等需要调用后端 API 的功能

---

## 4. 本地部署

### 4.1 后端

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 4.2 前端

```bash
cd frontend
npm install
npm run dev
```

### 4.3 Docker 部署

```bash
# 开发环境
docker compose up -d

# 生产环境
docker compose -f compose.prod.yaml up -d
```

---

## 5. 健康检查

- 后端健康检查端点：`GET /health`
- 返回示例：`{ "status": "UP", "service": "pet-service-platform", "timestamp": "..." }`

---

## 6. 在线地址

- 后端 API：`https://petserviceplatform-production.up.railway.app`
- 接口文档：`https://petserviceplatform-production.up.railway.app/swagger-ui.html`
- 前端：`https://pet-service-platform-7shx.vercel.app/`

---

## 7. 常见问题

### Q: 部署后页面白屏/404
- 确认 `vercel.json` 中的 `rewrites` 配置正确，SPA 路由需要 fallback 到 `index.html`
- 确认构建命令和输出目录配置正确

### Q: API 请求失败
- 确认 `vercel.json` 中的 API 代理地址正确指向后端 Railway 域名
- 检查后端 Railway 服务是否正常运行
- 检查 CORS 配置是否允许前端域名访问

### Q: 环境变量不生效
- 在 Vercel Dashboard 中重新添加环境变量并重新部署
- 前端环境变量必须以 `VITE_` 开头才能在构建时被 Vite 识别

### Q: 数据库初始化
- 首次部署后需要手动执行 SQL 初始化脚本创建表结构和种子数据

### Q: 文件存储
- Railway 的临时文件系统不支持持久化，如需文件上传功能，建议配置 MinIO 或云存储服务

---

## 8. 注意事项

1. **敏感信息**：数据库密码、JWT 密钥等务必通过平台 Dashboard 配置，不要硬编码在代码中
2. **跨域配置**：前端 Vercel 域名需要添加到后端的允许跨域列表中
3. **环境变量**：敏感信息务必通过 Railway Dashboard 配置，不要硬编码在代码中

---

## 9. 相关链接

- [Vercel 文档](https://vercel.com/docs)
- [Railway 文档](https://docs.railway.app/)
- [项目 GitHub 仓库](https://github.com/Chen-hanyu/PetServicePlatform)

## CORS 线上配置补充

后端 Spring Security CORS 现在读取环境变量 `APP_CORS_ALLOWED_ORIGIN_PATTERNS`，多个来源用英文逗号分隔。Railway 部署时建议配置：

```env
APP_CORS_ALLOWED_ORIGIN_PATTERNS=https://pet-service-platform-7shx.vercel.app,https://*.vercel.app,http://localhost:*,http://127.0.0.1:*
```

如果 Vercel 后续绑定自定义域名，需要把自定义域名追加到该变量中，例如 `https://www.example.com`。
