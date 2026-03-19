# 软件架构设计贡献说明
姓名：chenhanyu
学号：2320100624
日期：2026-03-19

## 我完成的工作
### 1. 架构设计
- [ ] 前端架构设计
- [x] 后端架构设计
- [x] 数据库设计
- [x] 系统交互流程设计

### 2. 技术选型
- 前端框架选择及理由：与前端方案保持一致，采用 `Vue 3 + Vite + TypeScript`，便于用户端和管理员端共用一套工程结构，也方便后端接口联调。
- 后端框架选择及理由：采用 `Java 17 + Spring Boot 3 + Spring Security + JWT + MyBatis-Plus`，满足课程项目对标准后端分层、接口规范、角色鉴权和后续扩展的要求。
- 数据库选择及理由：采用 `MySQL` 作为主数据库，适合用户、帖子、领养、预约、订单等结构化业务数据；缓存与文件存储预留 `Redis` 和 `MinIO / 本地目录` 扩展位。
- 部署方式选择及理由：采用 `前后端分离 + Nginx + Spring Boot Jar + MySQL`，方案轻量、部署成本低，适合课程项目演示与本地联调。

### 3. 环境搭建
- [ ] 前端项目初始化
- [x] 后端项目初始化
- [x] 数据库连接配置
- [x] AGENTS.md 编写

### 4. 文档编写
- [x] `docs/architecture.md`
- [x] `docs/database.md`
- [x] `docs/sql-schema.md`
- [x] 其他文档：`docs/api.md`、`docs/technology-selection.md`、`docs/backend.md`、`backend/README.md`

## 我完成的具体内容
- 将 IntelliJ IDEA 初始化生成的 Spring Boot 模板工程收敛为符合项目设计的后端骨架，统一为 `com.petplatform` 包结构。
- 调整 `backend/pom.xml`，将后端依赖整理为 Spring Boot 3、Spring MVC、Spring Security、MyBatis-Plus、MySQL、Springdoc OpenAPI、JJWT 等基础组合。
- 将 `application.properties` 转换为 `application.yml`，补充应用名、端口、MySQL、JWT、Swagger 和本地文件存储配置。
- 按设计文档建立 `controller`、`admin/controller`、`service`、`mapper`、`entity`、`dto`、`config`、`security`、`common` 等基础分层目录。
- 编写系统架构设计文档，补充总体架构图、前端架构图、后端架构图、登录流程、领养审核流程、商城下单流程等 Mermaid 图。
- 编写数据库设计文档，整理核心实体、状态枚举、索引建议，并绘制 ER 图。
- 生成数据库初始化脚本 `backend/src/main/resources/sql/schema.sql`，覆盖用户、宠物档案、社区、领养、服务预约、商城、运营配置等核心表。
- 编写 `docs/sql-schema.md`，说明建表 SQL 的范围、执行方式和后续迁移方向。
- 补全 `docs/api.md`，将原接口清单扩展为课程版 API 文档，加入统一响应结构、鉴权规则、参数表、状态枚举和关键示例。
- 编写并精简根目录 `AGENTS.md`，为后续 AI 辅助开发约束技术栈、目录结构、接口规范和禁止事项。

## PR 链接
- PR #14: https://github.com/Chen-hanyu/PetServicePlatform/pull/14

## 遇到的问题和解决
1. 问题：后端最初只有 IDEA 默认生成的最小 Spring Boot 模板，和设计文档中的结构、依赖、包名不一致。  
解决：以现有文档为基线，统一调整 `pom.xml`、包名、配置文件和目录结构，让工程骨架先与设计对齐，再继续开发。

2. 问题：数据库设计最初只有 ER 图和字段说明，没有实际可执行的建表脚本。  
解决：基于 `docs/database.md` 中的字段设计，补充 `schema.sql` 和 `docs/sql-schema.md`，将逻辑设计转成可落地的初始化 SQL。

3. 问题：API 文档最初更像接口清单，不足以支持后续后端实现和前后端联调。  
解决：补充统一响应规范、接口参数、返回字段、状态枚举和关键示例，形成更完整的课程版 API 文档。

4. 问题：AI 辅助开发如果没有统一规则，容易偏离当前项目技术栈和文档约束。  
解决：编写并精简 `AGENTS.md`，明确技术栈、目录、接口规范、禁止事项和 AI 输出要求。

## 心得体会
这次软件架构设计让我更清楚地理解了“先统一设计基线，再落工程骨架”的重要性。对课程项目来说，真正影响后续开发效率的，不只是技术选型本身，而是文档、目录、依赖、数据库和接口口径是否一致。通过先补齐架构文档、数据库设计、API 文档和初始化 SQL，再去整理 Spring Boot 工程，我能更清楚地把需求、文档和代码骨架连接起来，也更意识到 AI 工具在结构整理和文档生成方面很高效，但关键决策仍然需要人工把控一致性和边界。
