# 后端开发贡献说明
姓名：chenhanyu
学号：2320100624
日期：2026-04-04
补充更新：2026-04-08

## 我完成的工作
### API 实现
- [x] 用户认证 API（注册 / 登录）
- [x] 业务资源 1 CRUD：宠物档案 `pets`
- [x] 业务资源 2 CRUD：Banner 管理 `banners`
- [x] 统一错误响应

### 数据库
- [x] 数据模型定义（ER 图或模型文件）
- [x] ORM 配置
- [x] 数据库迁移脚本

说明：当前以 `backend/src/main/resources/sql/schema.sql` 与 `seed.sql` 作为初始化脚本交付，未额外引入 Flyway / Liquibase。

### 部署
- [x] Dockerfile 编写
- [x] docker-compose.yml 配置
- [x] 本地联调验证

说明：后端本地测试已通过，`docker-compose.yml` 已完成并通过 `docker compose config` 校验；容器实际启动需在 Docker 引擎可用的环境下执行 `docker compose up -d` 进一步确认。

## 具体说明
- 基于 Spring Boot 3、Spring Security、JWT 与 MyBatis-Plus 完成后端核心工程搭建，统一了 `controller -> service -> mapper -> database` 分层结构。
- 完成用户端与管理端认证接口，统一接口前缀为 `/api/v1` 与 `/api/v1/admin`，并通过 JWT 做角色隔离。
- 完成宠物档案模块 CRUD，以及后台 Banner 管理 CRUD，满足课程作业“至少 2 个业务资源 CRUD”的要求。
- 实现统一返回结构、参数校验、全局异常处理与分页响应，补齐 Swagger/OpenAPI 文档说明。
- 编写并维护数据库初始化脚本，覆盖用户、社区、领养、服务预约、商城、宠物档案、Banner、推荐位等核心表。
- 补充容器化交付文件：`backend/Dockerfile`、根目录 `docker-compose.yml`，并将后端配置调整为环境变量优先，兼容本地部署与 Docker 部署。
- 编写并维护 WebMvc、Service 与 H2 集成测试，当前后端全量测试已通过。

## 补充进展（2026-04-08）
- 完成商城下单并发安全优化：`products` 库存扣减改为原子更新（`stock >= quantity` 条件扣减），避免超卖。
- 完成服务预约并发安全优化：预约创建流程增加 `FOR UPDATE` 行锁，避免同一时段重复占位。
- 完成购物车并发加购优化：对同一购物车项加锁后刷新再累加，首次并发插入触发唯一键冲突时自动重试合并数量，避免 500 与丢增量。
- 完成关键索引增强并同步到建表脚本与测试建表脚本：`community_posts`、`products`、`adoption_pets`、`service_bookings`、`merchants`。
- 本次优化后再次执行后端全量测试，结果为 `Tests run: 152, Failures: 0, Errors: 0, Skipped: 0`。

## PR 链接
- https://github.com/Chen-hanyu/PetServicePlatform/pull/25

## 遇到的问题和解决
1. 问题：项目原始后端配置更偏向本地开发，数据库连接、JWT 密钥和文件存储路径未对容器化场景做兼容。
   解决：将 `application.yml` 改为环境变量优先，在保留本地默认值的前提下支持 `docker-compose` 注入数据库和文件存储配置。

2. 问题：课程作业要求至少 2 个业务资源 CRUD，而现有后端中明确闭环的完整 CRUD 资源不够直观。
   解决：在现有后台运营模块基础上补齐 Banner 删除接口与对应测试，最小改动满足作业要求。

3. 问题：Docker 启动时本机 `3306` 端口已被占用，导致容器数据库无法绑定宿主机端口。
   解决：移除 `docker-compose.yml` 中 MySQL 的宿主机端口映射，仅保留容器内网络访问，满足当前前后端联调与课程作业要求。

## 心得体会
这次后端开发让我更清楚地认识到，课程项目里“可运行”和“可交付”是两件相关但不完全相同的事情。除了把接口和数据库做出来，还需要补齐统一响应、测试、文档和容器化配置，才能真正满足阶段作业要求。基于现有代码做最小改动补齐缺口，比重新设计一套方案更稳，也更符合协作项目的实际节奏。
