# 技术选型确认

## 1. 文档说明

本文档用于确认宠物综合服务平台当前阶段的核心技术选型，并说明选择理由。  
项目定位为 2 人课程项目，因此技术方案优先考虑：

- 上手成本低
- 开发效率高
- 生态成熟
- 便于展示和答辩
- 能支撑用户端、管理员端和后端三端协同

---

## 2. 技术选型确认

| 层级 | 选择 | 理由 |
|---|---|---|
| 前端框架 | `Vue 3 + Vite + TypeScript` | Vue 3 组件化能力成熟，适合用户端和管理端共用一套工程；Vite 启动和构建速度快；TypeScript 便于接口类型约束和多人协作。 |
| 后端框架 | `Java 17 + Spring Boot 3 + Spring Security + JWT` | Spring Boot 生态完善，适合课程项目的标准后端分层；Spring Security 与 JWT 便于实现用户端和管理员端的角色鉴权。 |
| 数据库 | `MySQL` | 关系型建模清晰，适合用户、帖子、领养、预约、订单等结构化业务数据；学习和部署成本较低。 |
| 部署方式 | `前后端分离 + Nginx + Spring Boot Jar + MySQL` | 方案轻量、易实现、便于排错；适合课程项目演示和 MVP 原型落地；后续可按需扩展 Redis 与 MinIO。 |

---

## 3. 分项说明

### 3.1 前端框架

当前前端建议采用：

- `Vue 3`
- `Vite`
- `TypeScript`
- `Vue Router`
- `Pinia`
- `Axios`
- `SCSS + Design Token`

选择原因：

- 可同时承载用户前台和管理员后台
- 组件化开发效率高，适合页面较多的项目
- 路由、状态管理、请求层生态完整
- 与当前文档中规划的页面结构和组件结构一致

### 3.2 后端框架

当前后端建议采用：

- `Java 17`
- `Spring Boot 3`
- `Spring MVC`
- `Spring Security`
- `JWT`
- `MyBatis-Plus` 或 `JPA`

选择原因：

- 分层清晰，便于实现 `controller / service / mapper / entity / dto`
- 易于接入鉴权、参数校验、统一异常处理、接口文档
- 可以自然支持用户端和管理端共用一套服务、按角色隔离接口

### 3.3 数据库

当前数据库建议采用：

- 主数据库：`MySQL`
- 缓存：`Redis`（可选增强）
- 文件存储：`MinIO` 或本地静态资源目录

选择原因：

- MySQL 适合当前项目的结构化业务模型
- Redis 可用于首页聚合、推荐内容、验证码等热点数据缓存
- MinIO 或本地目录适合存储帖子图片、宠物相册、Banner 图等文件资源

### 3.4 部署方式

推荐部署方式：

- 前端与后端分离部署
- 前端执行 `npm run build` 后生成静态资源，由 `Nginx` 托管
- 后端执行 `mvn clean package` 后打包为 `jar`，使用 `java -jar` 启动
- `Nginx` 负责反向代理 `/api/*` 请求到 Spring Boot 服务
- 数据库使用 `MySQL`
- 开发阶段文件可存储在本地目录，后续按需升级到 `MinIO`
- `Redis` 作为可选增强组件，不作为最小可运行版本的强依赖

推荐理由：

- 部署成本低
- 架构足够清晰
- 适合课程项目演示
- 便于本地开发、联调和后续答辩展示

---

## 4. 推荐落地方案

### 4.1 MVP 阶段

- 前端：`Vue 3 + Vite`
- 后端：`Spring Boot 3`
- 数据库：`MySQL`
- 部署：`Nginx + Spring Boot Jar`
- 文件：本地静态资源目录
- 缓存：可暂不启用 Redis

### 4.2 增强阶段

- 在 MVP 方案基础上引入 `Redis`
- 将图片和附件从本地目录升级为 `MinIO`
- 根据需要增加日志、监控和更细粒度的后台权限控制

---

## 5. 结论

当前项目最合适的技术选型是：

- 前端：`Vue 3 + Vite + TypeScript`
- 后端：`Spring Boot 3 + Spring Security + JWT`
- 数据库：`MySQL`
- 部署：`前后端分离 + Nginx + Spring Boot Jar + MySQL`

该方案与当前已有的设计文档、API 文档、数据库设计和信息架构保持一致，能够较好支撑课程项目的开发、展示与后续扩展。
