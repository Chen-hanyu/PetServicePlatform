# 宠物综合服务平台项目说明

本仓库用于项目主题为宠物综合服务平台，采用“用户 Web 前台 + 管理员后台 + Spring Boot 后端”三端协同架构。

## 团队成员
| 姓名 | 学号 | 分工 |
|------|------|-----|
| 屈天顺 | 2323040522 | 前端 |
| 陈涵予 | 2320100624 | 后端 |


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
| 部署方式 | `前后端分离 + Nginx + Spring Boot Jar + MySQL` | 轻量、易实施、便于本地联调和课程项目答辩展示，后续可扩展 Redis 与 MinIO。 |

## 部署方式

- 推荐部署方案：
- 前端执行 `npm run build` 生成静态资源，由 `Nginx` 托管
- 后端执行 `mvn clean package` 打包为 `jar`，通过 `java -jar` 启动
- `Nginx` 统一代理前端页面访问与 `/api/*` 接口请求
- 业务数据存储在 `MySQL`
- 缓存使用 `Redis` 作为可选增强组件
- 图片和附件开发阶段可存储在本地静态目录，后续可升级为 `MinIO`

- 本地部署方案：
- 前端：在 `frontend/` 目录执行 `npm install` 和 `npm run dev`，启动本地开发服务
- 后端：在 `backend/` 目录执行 `mvn clean install` 和 `mvn spring-boot:run`，启动 Spring Boot 服务
- 数据库：本地安装并启动 `MySQL`，按 `docs/database.md` 中的数据模型准备数据库
- 接口联调：前端通过开发环境配置将 `/api/*` 请求转发到本地后端服务
- 文件存储：开发阶段使用本地静态资源目录即可
- 缓存：`Redis` 为可选项，最小可运行版本可暂不启用
- 前端开发地址：Vite 默认开发地址
- 后端接口地址：`http://127.0.0.1:8080`
- 接口文档地址：`http://127.0.0.1:8080/swagger-ui.html`

对于当前课程项目，建议优先采用单机部署或本地部署，先保证主流程可运行，再考虑缓存和对象存储等增强能力。


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

