# 项目规则

## 项目目标
- 本项目为宠物综合服务平台，采用“用户 Web 前台 + 管理员后台 + Spring Boot 后端”三端协同架构。
- AI 生成的代码、文档和配置必须与 `docs/` 中现有设计保持一致。

## 优先参考文档
- `README.md`
- `docs/frontend.md`
- `docs/backend.md`
- `docs/architecture.md`
- `docs/database.md`
- `docs/api.md`
- `docs/design-spec.md`
- `docs/design-spec-admin.md`

## 技术栈
- 前端：`Vue 3 + Vite + TypeScript + Vue Router + Pinia + Axios + SCSS`
- 后端：`Java 17 + Spring Boot 3 + Spring MVC + Spring Security + JWT + MyBatis-Plus`
- 数据库：`MySQL`
- 可选增强：`Redis`、`MinIO`
- 部署：`前后端分离 + Nginx + Spring Boot Jar + MySQL`

## 目录约定
- `frontend/`：前端工程
- `backend/`：后端工程
- `docs/`：需求、设计、架构、数据库、API、技术选型文档
- `frontend/src/components/`：公共组件
- `frontend/src/pages/web/`：用户前台页面
- `frontend/src/pages/admin/`：管理员后台页面
- `frontend/src/services/`：前端请求封装
- `frontend/src/types/`：前端类型定义
- `backend/src/main/java/com/petplatform/controller/`：用户端接口
- `backend/src/main/java/com/petplatform/admin/controller/`：管理端接口
- `backend/src/main/java/com/petplatform/service/`：业务逻辑
- `backend/src/main/java/com/petplatform/mapper/`：数据访问
- `backend/src/main/java/com/petplatform/entity/`：实体类
- `backend/src/main/java/com/petplatform/dto/`：请求响应对象
- `backend/src/main/java/com/petplatform/config/`：配置类
- `backend/src/main/java/com/petplatform/security/`：鉴权与权限
- `backend/src/main/java/com/petplatform/common/`：通用返回、异常、工具类

## 必须遵守
- 优先实现 MVP 主流程，不做超出当前文档范围的大幅扩展。
- 用户端与管理员端共用基础能力，但接口和权限必须隔离。
- API 前缀统一为 `/api/v1`，管理端前缀统一为 `/api/v1/admin`。
- 接口统一返回 `{ code, message, data }`。
- 列表接口统一支持分页；管理端接口优先支持筛选、排序、批量处理。
- 状态值使用明确枚举，如 `PENDING`、`APPROVED`、`REJECTED`、`CANCELLED`。
- 新增功能如影响接口、数据库或架构，需同步更新对应 `docs/` 文档。

## 前端规范
- 使用 Vue 3 组合式 API 和 TypeScript。
- 异步请求统一放在 `services/`，不要在页面中散写接口调用。
- 样式优先使用 SCSS 和设计 Token。
- 前台风格遵循“温暖治愈、日系软萌”，后台遵循“专业清晰、数据导向”。

## 后端规范
- 采用 `controller -> service -> mapper -> database` 分层。
- 用户端接口与管理端接口分开组织。
- DTO、实体、数据库字段命名尽量保持一致。
- 配置优先写入 `application.yml`。

## 禁止事项
- 不要擅自改技术栈为 React、FastAPI、PostgreSQL 等其他方案。
- 不要使用 `any` 逃避类型定义，除非有明确理由。
- 不要直接操作 DOM。
- 不要在页面中大量使用内联样式。
- 不要绕过统一请求层直接散写接口地址。
- 不要擅自修改核心配置、目录结构和技术选型，除非明确要求。
- 不要引入与课程项目规模不匹配的复杂基础设施，如微服务、Kubernetes、多节点集群。

## AI 输出要求
- 先对齐现有文档，再生成代码。
- 优先复用已有命名、结构和模块边界。
- 对不确定实现采用保守方案，避免过度设计。
