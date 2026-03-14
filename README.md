# 宠物综合服务平台项目说明

本仓库用于项目主题为宠物综合服务平台，采用“用户 Web 前台 + 管理员后台 + Spring Boot 后端”三端协同架构。

## 成员与分工
- 前端负责人：ccchy（2492764608@qq.com）
- 后端负责人：待组员补充

## 项目简介
宠物综合服务平台面向养宠用户和平台运营人员，核心功能包括：
- 用户 Web 前台：社区交流、领养申请、宠物服务预约、商品购买、宠物档案管理、消息中心。
- 管理员后台：用户管理、帖子审核、领养审核、商家管理、商品管理、订单处理、Banner 与推荐位维护。
- 后端服务：统一权限、业务规则、数据存储与接口文档。

## 项目目录结构

```text
.
├── README.md
├── docs/
│   ├── frontend.md
│   ├── backend.md
│   ├── api.md
│   ├── design-spec.md
│   └── information-architecture.md
├── frontend/                 # 用户 Web 前台 + 管理员后台前端
├── backend/                  # Spring Boot 后端服务
└── .gitignore
```

## 系统架构
- `frontend`：基于 Vue 3 + Vite 的 Web 前端工程，包含用户前台与管理员后台。
- `backend`：基于 Spring Boot 3 的后端服务，统一提供用户端与管理端 API。
- `docs`：需求、设计、信息架构、前后端模块与 API 文档。

## Figma 设计稿
- 设计链接：https://www.figma.com/design/gu5MKdueh9c10smfcXSQV0/%E5%AE%A0%E7%89%A9%E9%A1%B5%E9%9D%A2?node-id=0-1&t=6aHvhBAU7GApTcti-1

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

## 后端运行方式
1. `cd backend`
2. `mvn clean install`
3. `mvn spring-boot:run`
4. 打开 `http://127.0.0.1:8080/swagger-ui.html` 查看接口文档

