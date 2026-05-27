# 云服务部署贡献说明

姓名：chenhanyu
学号：2320100624
日期：2026-05-27

## 我完成的工作

### 1. 平台选择

- 使用平台：Railway（后端服务部署）
- 前端部署：Vercel（由前端同学负责）

### 2. 部署配置

- [x] railway.toml 配置文件编写
- [x] 环境变量配置说明
- [x] 自动部署配置
- [x] docs/deployment.md 部署说明文档编写
- [x] 个人贡献说明文档编写

### 3. 配置文件详情

#### railway.toml

在项目根目录创建 railway.toml，配置 Docker 构建方式：

- builder = DOCKERFILE：使用 Dockerfile 构建
- dockerfilePath = backend/Dockerfile：指定 Dockerfile 路径
- buildContext = backend：构建上下文目录
- healthcheckPath = /health：健康检查路径
- port = 8080：服务端口

#### 环境变量

在 Railway Dashboard 中配置以下环境变量：

- SERVER_PORT：服务端口
- SPRING_DATASOURCE_URL：数据库连接 URL
- SPRING_DATASOURCE_USERNAME：数据库用户名
- SPRING_DATASOURCE_PASSWORD：数据库密码
- JWT_SECRET：JWT 签名密钥
- JWT_EXPIRATION_SECONDS：JWT 过期时间
- FILE_STORAGE_TYPE：文件存储类型
- FILE_STORAGE_LOCAL_PATH：本地文件存储路径
- DEEPSEEK_API_KEY：DeepSeek AI API 密钥（可选）

#### 自动部署

- 连接 GitHub 仓库，配置 main 分支自动部署
- 每次推送到 main 分支时 Railway 自动触发构建和部署

### 4. 问题解决

- 遇到的问题：Railway 平台对 Dockerfile 的构建上下文路径有要求，需要正确配置 buildContext
- 解决方案：在 railway.toml 中设置 buildContext = backend，确保 Dockerfile 能正确访问 backend 目录下的 pom.xml 和 src 目录

- 遇到的问题：Railway 的临时文件系统不支持持久化存储
- 解决方案：在部署说明文档中注明文件存储限制，建议后续配置 MinIO 或云存储服务

## PR 链接

- PR #X: https://github.com/Chen-hanyu/PetServicePlatform/pull/X

## 在线地址

- 后端 API：https://your-app.railway.app
- 接口文档：https://your-app.railway.app/swagger-ui.html

## 心得体会

通过本次云服务部署实践，我深入了解了 Railway 平台的部署流程和配置方法。Railway 作为一个支持 Docker 的 PaaS 平台，能够很好地与现有的 Dockerfile 配合使用，实现从 GitHub 仓库到线上服务的自动化部署。

在配置过程中，我学习了 railway.toml 的编写规范，理解了构建上下文、健康检查、端口映射等关键配置项的作用。同时，我也认识到云部署中环境变量管理的重要性——敏感信息必须通过平台的安全配置注入，而不能硬编码在代码中。

这次部署实践让我对 DevOps 流程有了更具体的认识，从本地开发到 CI/CD 再到云部署，每个环节都需要仔细配置和验证。
