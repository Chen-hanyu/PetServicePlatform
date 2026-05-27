# Docker 部署贡献说明

姓名：qutianshun 
学号：2323040522  
角色：前端
日期：2026-05-16

## 我完成的工作

### 1. Dockerfile 编写
- [x] 前端 Dockerfile（多阶段构建）
  - 构建阶段：使用 `node:20-alpine` 安装依赖并构建
  - 运行阶段：使用 `nginx:alpine` 提供静态文件
  - 非 root 用户运行（`appuser`）
  - 健康检查端点 `http://localhost:80/`
  - 暴露端口 80
- [x] .dockerignore 文件
  - 排除 `node_modules`、`dist`、`.git`、`.env` 等无关文件

### 2. Compose 配置
- [x] 开发环境 compose.yaml
  - 前端热重载（挂载源码卷）
  - 后端服务
  - MySQL 数据库（含健康检查、初始化脚本）
- [x] 生产环境 compose.prod.yaml
  - 资源限制（前端 128M、后端 512M、MySQL 256M）
  - 健康检查配置
  - 密钥管理（Docker Secrets）
  - 重启策略 `unless-stopped`
- [x] 健康检查配置
  - 前端：`wget -qO- http://localhost:80/`
  - 后端：`wget -qO- http://localhost:8080/actuator/health`
  - MySQL：`mysqladmin ping`

### 3. 安全配置
- [x] 非 root 用户运行容器（前端使用 `appuser`）
- [x] `.env.example` 已创建，敏感信息不硬编码
- [x] 生产环境使用 Docker Secrets 管理密码

## 遇到的问题和解决
1. 问题：前端开发环境需要热重载，但生产环境需要 nginx 提供静态文件
   解决：使用多阶段构建 + Docker Compose 的 `target: builder` 实现开发/生产分离

2. 问题：生产环境密码不能硬编码在配置文件中
   解决：使用 Docker Secrets 管理数据库密码和 JWT 密钥

## AI 使用情况
- 使用了哪些 Prompt：
  - "为 Vue 3 + Vite 前端创建生产级 Dockerfile，多阶段构建，非 root 用户，健康检查"
  - "创建 Docker Compose 开发环境配置，支持前端热重载"
- AI 帮助解决了哪些问题：
  - 生成多阶段构建的 Dockerfile 模板
  - 配置 nginx 反向代理和 SPA 路由支持
  - 设计开发/生产两套 Compose 配置

## 心得体会
通过本次 Docker 容器化部署作业，我学会了：
1. 多阶段构建的原理和实践，如何有效减小镜像体积
2. Docker Compose 编排多服务（前端 + 后端 + 数据库）
3. 开发环境与生产环境的配置分离
4. 容器安全最佳实践（非 root 用户、密钥管理、健康检查）
5. 前端容器化部署的完整流程（构建 → nginx 托管 → 反向代理）
