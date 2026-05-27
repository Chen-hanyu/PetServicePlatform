# 部署说明文档

## 概述

本文档描述宠物综合服务平台的部署方案。项目采用前后端分离架构，后端部署在 **Railway** 平台，前端部署在 **Vercel** 平台。

## 部署架构

用户 -> Vercel（前端） -> Railway（后端 API） -> MySQL（数据库）

## 后端部署（Railway）

### 平台选择

- **平台**：Railway
- **部署方式**：Docker（基于现有 backend/Dockerfile）
- **数据库**：Railway 提供的 MySQL 插件 / 外部 MySQL 服务

### 配置文件

项目根目录下的 railway.toml 定义了 Railway 部署配置：

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

### 环境变量配置

在 Railway Dashboard 中配置以下环境变量：

| 变量名 | 说明 | 示例值 |
|--------|------|--------|
| SERVER_PORT | 服务端口 | 8080 |
| SPRING_DATASOURCE_URL | 数据库连接 URL | jdbc:mysql://host:3306/pet_service_platform |
| SPRING_DATASOURCE_USERNAME | 数据库用户名 | root |
| SPRING_DATASOURCE_PASSWORD | 数据库密码 | your-password |
| JWT_SECRET | JWT 签名密钥 | your-jwt-secret |
| JWT_EXPIRATION_SECONDS | JWT 过期时间（秒） | 7200 |
| FILE_STORAGE_TYPE | 文件存储类型 | local |
| FILE_STORAGE_LOCAL_PATH | 本地文件存储路径 | uploads/ |
| DEEPSEEK_API_KEY | DeepSeek AI API 密钥（可选） | your-api-key |
| AI_BASE_URL | AI API 基础 URL | https://api.deepseek.com |
| AI_MODEL | AI 模型 | deepseek-chat |

### 自动部署配置

1. 在 Railway 中连接 GitHub 仓库
2. 选择 PetServicePlatform 仓库
3. 配置部署分支为 main
4. Railway 会自动检测 railway.toml 并使用 Dockerfile 构建
5. 每次推送到 main 分支时自动触发部署

### 数据库配置

推荐使用 Railway 提供的 MySQL 插件：

1. 在 Railway Dashboard 中创建 MySQL 插件
2. 获取数据库连接信息（主机、端口、用户名、密码）
3. 在 Railway 中执行数据库初始化脚本：backend/src/main/resources/sql/schema.sql 和 seed.sql
4. 将数据库连接信息配置为环境变量

## 前端部署（Vercel）

### 环境变量配置

| 变量名 | 说明 | 示例值 |
|--------|------|--------|
| VITE_API_BASE_URL | 后端 API 基础地址 | https://your-app.railway.app |

## 本地部署

### 后端

cd backend
mvn clean install
mvn spring-boot:run

### 前端

cd frontend
npm install
npm run dev

### Docker 部署

开发环境：docker compose up -d
生产环境：docker compose -f compose.prod.yaml up -d

## 健康检查

- 后端健康检查端点：GET /health
- 返回示例：{ "status": "UP", "service": "pet-service-platform", "timestamp": "..." }

## 在线地址

- 后端 API：https://your-app.railway.app
- 接口文档：https://your-app.railway.app/swagger-ui.html
- 前端：https://your-app.vercel.app

## 注意事项

1. 数据库初始化：首次部署后需要手动执行 SQL 初始化脚本创建表结构和种子数据
2. 文件存储：Railway 的临时文件系统不支持持久化，如需文件上传功能，建议配置 MinIO 或云存储服务
3. 环境变量：敏感信息（数据库密码、JWT 密钥）务必通过 Railway Dashboard 配置，不要硬编码在代码中
4. 跨域配置：前端 Vercel 域名需要添加到后端的允许跨域列表中
