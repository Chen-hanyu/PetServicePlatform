# 部署说明文档

## 概述

本项目采用前后端分离部署架构：
- **前端**：部署到 **Vercel**（自动构建、HTTPS、Git 推送自动部署）
- **后端**：部署到 **Railway**（Docker 容器化部署）
- **数据库**：由后端服务管理（如 Railway 内置数据库或外部 MySQL）

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
   - **Root Directory**: 保持默认（项目根目录）
   - **Build Command**: `cd frontend && npm install && npm run build`
   - **Output Directory**: `frontend/dist`
   - **Install Command**: `cd frontend && npm install`
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

> 后端部署由后端开发同学负责，此处仅作简要说明。

### 2.1 部署步骤

1. 打开 [Railway Dashboard](https://railway.app/dashboard)
2. 点击 **"New Project" → "Deploy from GitHub repo"**
3. 选择仓库，Railway 会自动检测 `Dockerfile`
4. 配置环境变量（见下方）
5. 部署完成后获取分配的域名

### 2.2 环境变量

| 变量名 | 说明 |
|--------|------|
| `DATABASE_URL` | 数据库连接字符串 |
| `JWT_SECRET` | JWT 签名密钥 |
| `NODE_ENV` | `production` |

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

## 4. 部署架构图

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

## 5. 常见问题

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

---

## 6. 相关链接

- [Vercel 文档](https://vercel.com/docs)
- [Railway 文档](https://docs.railway.app/)
- [项目 GitHub 仓库](https://github.com/Chen-hanyu/PetServicePlatform)
