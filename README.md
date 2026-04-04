# 宠物综合服务平台项目说明

本仓库用于项目主题为宠物综合服务平台，采用“用户 Web 前台 + 管理员后台 + Spring Boot 后端”三端协同架构。

## 团队成员
| 姓名 | 学号 | 分工 |
|------|------|-----|
| 屈天顺 | 2323040522 | 前端 |
| 陈涵予 | 2320100624 | 后端 |

## 团队分工
### 屈天顺（前端）
| 时间 | 产出类型 | 具体产出 |
|---|---|---|
| 2026-03-14 ~ 2026-03-17 | 文档 | 编写/完善 `docs/frontend.md`，补充前端模块说明、MVP 范围、路由与联调建议。 |
| 2026-03-14 ~ 2026-03-17 | 设计 | 完成用户端与管理端主要页面结构设计（登录注册、首页、宠物档案、预约、商城、宠物圈、个人中心、管理后台框架）。 |
| 2026-03-14 ~ 2026-03-17 | 规范 | 统一前端页面骨架与交互规范（列表区、筛选区、表单校验提示、状态反馈）。 |

### 陈涵予（后端）
| 时间 | 产出类型 | 具体产出 |
|---|---|---|
| 2026-03-10 | 文档基线 | 建立后端文档基线，完成 `docs/backend.md`、`docs/api.md` 初版整理。 |
| 2026-03-19 | 架构与数据库文档 | 完成 `docs/architecture.md`、`docs/database.md`、`docs/sql-schema.md`、`docs/technology-selection.md`，明确后端架构、数据模型与技术选型。 |
| 2026-03-19 | 后端工程搭建 | 初始化 Spring Boot 后端，完成 `application.yml` 配置，搭建分层目录：`controller`、`admin/controller`、`service`、`mapper`、`entity`、`dto`、`config`、`security`、`common`。 |
| 2026-03-19 ~ 2026-03-21 | 数据库落地 | 创建并对齐数据库脚本：`backend/src/main/resources/sql/schema.sql`、`seed.sql`，覆盖用户、社区、领养、服务预约、商城、消息、运营配置等核心表。 |
| 2026-03-21 | 业务实现 | 完成用户端与管理端核心接口实现：认证、首页、搜索、社区、领养、宠物档案、服务预约、商城、消息中心、后台管理。 |
| 2026-03-21 | 基础能力 | 落地统一返回结构 `{code,message,data}`、全局异常处理、JWT 鉴权与用户/管理员权限隔离、Swagger/OpenAPI 文档。 |
| 2026-03-21 | 测试体系 | 建立并完善自动化测试：WebMvc 接口与权限测试、Service 规则测试、H2 集成测试；覆盖认证权限、首页搜索、社区、领养、服务预约、商城下单、宠物档案、消息中心与后台核心流程。 |


## Figma 链接
https://www.figma.com/design/gu5MKdueh9c10smfcXSQV0/%E5%AE%A0%E7%89%A9%E9%A1%B5%E9%9D%A2?node-id=0-1&t=6aHvhBAU7GApTcti-1

## 项目简介
宠物综合服务平台面向养宠用户和平台运营人员，核心功能包括：
- 用户 Web 前台：社区交流、领养申请、宠物服务预约、商品购买、宠物档案管理、消息中心。
- 管理员后台：用户管理、帖子审核、领养审核、商家管理、商品管理、订单处理、Banner 与推荐位维护。
- 后端服务：统一权限、业务规则、数据存储与接口文档。

## 项目目录结构

```text
.
├── AGENTS.md                 # AI 辅助开发规则文件
├── README.md
├── docs/
│   ├── architecture.md
│   ├── api.md
│   ├── backend.md
│   ├── database.md
│   ├── design-spec.md
│   ├── design-spec-admin.md
│   ├── frontend.md
│   ├── information-architecture.md
│   ├── information-architecture-admin.md
│   ├── technology-selection.md
│   ├── user-research.md
│   ├── design/
│   └── contributions/
├── frontend/                 # 用户 Web 前台 + 管理员后台前端
├── backend/                  # Spring Boot 后端服务
└── .gitignore
```

## 系统架构
- `frontend`：基于 Vue 3 + Vite 的 Web 前端工程，包含用户前台与管理员后台。
- `backend`：基于 Spring Boot 3 的后端服务，统一提供用户端与管理端 API。
- `docs`：需求、设计、信息架构、架构设计、数据库设计、技术选型与 API 文档。

## 核心文档
- `AGENTS.md`：AI 辅助开发规则文件
- `docs/user-research.md`：目标用户、核心场景与竞品分析
- `docs/design-spec.md`：用户端视觉与交互设计规范
- `docs/design-spec-admin.md`：管理员后台设计规范
- `docs/information-architecture.md`：用户端信息架构
- `docs/information-architecture-admin.md`：管理员后台信息架构
- `docs/frontend.md`：前端模块说明与工程结构
- `docs/backend.md`：后端模块说明与服务结构
- `docs/architecture.md`：系统架构设计文档
- `docs/database.md`：数据库设计文档与 ER 图
- `docs/api.md`：API 设计文档
- `docs/technology-selection.md`：技术选型确认与部署建议

## 技术选型

| 层级 | 选择 | 理由 |
|---|---|---|
| 前端框架 | `Vue 3 + Vite + TypeScript` | 组件化能力成熟，适合用户端与管理员端共用一套工程；构建速度快，便于课程项目开发与演示。 |
| 后端框架 | `Java 17 + Spring Boot 3 + Spring Security + JWT` | 生态成熟，便于实现分层架构、统一鉴权、接口规范和后续扩展。 |
| 数据库 | `MySQL` | 适合帖子、订单、预约、领养、宠物档案等结构化业务数据，学习和部署成本较低。 |
| 部署方式 | `本地部署 / Docker 部署 + Spring Boot + MySQL` | 轻量、易实施、便于本地联调和课程项目答辩展示，后续可扩展 Redis 与 MinIO。 |

## 部署方式

- 推荐保留两种部署口径：`本地部署` 与 `Docker 部署`
- 前端与后端继续保持前后端分离，业务数据存储在 `MySQL`
- 文件存储开发阶段使用本地目录即可，后续可按需扩展 `MinIO`
- `Redis` 为可选增强组件，不作为当前阶段最小可运行依赖

- 本地部署：
- 前端：在 `frontend/` 目录执行 `npm install` 和 `npm run dev`
- 后端：在 `backend/` 目录执行 `mvn clean install` 和 `mvn spring-boot:run`
- 数据库：本地安装并启动 `MySQL`，按 `docs/database.md` 中的数据模型准备数据库
- 接口联调：前端通过开发环境配置将 `/api/*` 请求转发到本地后端服务
- 后端接口地址：`http://127.0.0.1:8080`
- 接口文档地址：`http://127.0.0.1:8080/swagger-ui.html`

- Docker 部署：
- 根目录提供 `docker-compose.yml`，统一启动 `backend` 与 `mysql`
- 后端通过 `backend/Dockerfile` 构建 Spring Boot 服务镜像
- 数据库初始化通过挂载 SQL 脚本或启动后手动导入完成
- 启动命令为 `docker-compose up -d`
- 服务启动后通过 `docker-compose ps` 检查容器状态

对于当前课程项目，建议优先保证本地部署和 Docker 部署都可运行，再考虑缓存和对象存储等增强能力。


## 分支策略
- `main`：稳定版本，保证可演示与可提交。
- `develop`：日常集成分支。
- `feature/ccchy-frontend-doc`：前端文档分支。
- `feature/*`：其他功能分支。

## 协作流程
1. 从 `develop` 创建个人功能分支。
2. 在功能分支完成开发和文档编写。
3. 发起 PR 合并到 `develop`。
4. 阶段验收后由 `develop` 合并到 `main`。

## 前端运行方式
1. `cd frontend`
2. `npm install`
3. `npm run dev`
4. `npm run build`
5. 可通过 `npm run preview` 本地预览构建结果

## 后端运行方式
1. `cd backend`
2. `mvn clean install`
3. `mvn spring-boot:run`
4. 打开 `http://127.0.0.1:8080/swagger-ui.html` 查看接口文档

