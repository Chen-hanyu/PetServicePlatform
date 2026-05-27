# Docker 部署贡献说明

姓名：chenhanyu
学号：2320100624
日期：2026-05-13

## 我完成的工作

### 1. Dockerfile 编写
- [x] 后端 Dockerfile（多阶段构建）
  - 第一阶段：Maven 构建（`maven:3.9.9-eclipse-temurin-17`）
  - 第二阶段：JRE 运行（`eclipse-temurin:17-jre`）
  - 非 root 用户运行（创建 `appuser`）
  - 健康检查配置（`HEALTHCHECK` 指令）
- [x] 后端 .dockerignore 文件（排除 target、.idea 等）

### 2. Compose 配置
- [x] 开发环境 compose.yaml（docker-compose.yml）
  - MySQL 8.4 数据库服务，含健康检查和初始化脚本
  - 后端服务，环境变量注入，数据卷持久化
  - 前端服务，nginx 反向代理后端 API
- [x] 生产环境 compose.prod.yaml
  - 资源限制（memory: 512M）
  - Docker Secrets 密钥管理（数据库密码、JWT 密钥）
  - 健康检查配置
- [x] 健康检查配置（MySQL: mysqladmin ping，后端: /actuator/health）

### 3. 自动化部署
- 选择了选项 A：GitHub Actions 构建并推送镜像到 GHCR
- 具体内容：
  - 创建 `.github/workflows/docker.yml`，在 push 到 main 分支时自动构建并推送前后端镜像到 GHCR
  - 集成 Trivy 安全扫描（CRITICAL/HIGH 级别）
  - 使用 Docker BuildKit 缓存加速构建

### 4. 其他
- [x] 创建 `.env.example` 环境变量模板
- [x] 创建 `deploy.sh` 一键部署脚本
- [x] 创建后端健康检查端点 `/health`（`HealthController.java`）
- [x] 非 root 用户运行容器
- [x] secrets 密钥管理（生产环境）

## PR 链接
- PR #X: https://github.com/Chen-hanyu/PetServicePlatform/pull/X

## 遇到的问题和解决

1. **问题**：后端 Dockerfile 使用 root 用户运行，存在安全风险
   **解决**：添加 `groupadd` 和 `useradd` 创建非 root 用户 `appuser`，并通过 `USER appuser` 切换运行

2. **问题**：缺少健康检查端点，Docker Compose 无法检测服务状态
   **解决**：创建 `HealthController.java`，提供 `/health` 端点返回服务状态信息

3. **问题**：生产环境密码硬编码在配置文件中
   **解决**：使用 Docker Secrets 管理敏感信息，通过 `MYSQL_ROOT_PASSWORD_FILE` 和 `_FILE` 后缀读取密钥文件

4. **问题**：`.env.example` 被 `.gitignore` 中的 `.env.*` 规则误拦截
   **解决**：在 `.gitignore` 中添加 `!.env.example` 例外规则，确保模板文件能被跟踪

## AI 使用情况

- 使用了哪些 Prompt：
  1. "为 Spring Boot 后端创建生产级 Dockerfile，使用多阶段构建、非 root 用户、健康检查"
  2. "创建 Docker Compose 生产环境配置，包含资源限制和密钥管理"
  3. "创建 GitHub Actions 工作流，构建并推送镜像到 GHCR，集成安全扫描"

- AI 帮助解决了哪些问题：
  1. Dockerfile 多阶段构建的最佳实践
  2. Docker Compose 生产环境配置的 secrets 管理方案
  3. GitHub Actions 工作流配置
  4. 健康检查端点的实现

## 心得体会

通过本次 Docker 容器化部署实践，我深入理解了以下内容：

1. **多阶段构建**：将构建环境和运行环境分离，有效减小镜像体积。后端从 Maven 构建镜像到 JRE 运行镜像，镜像体积大幅缩减。

2. **安全最佳实践**：非 root 用户运行容器、使用 Docker Secrets 管理敏感信息、健康检查配置，这些都是生产环境部署的重要安全措施。

3. **环境一致性**：Docker Compose 确保开发环境和生产环境的一致性，避免了"在我机器上能运行"的问题。

4. **CI/CD 自动化**：GitHub Actions 配合 GHCR 实现了自动构建、推送和安全扫描的完整 CI 流程。
