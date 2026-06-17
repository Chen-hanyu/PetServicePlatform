# 后端模块说明

## 1. 文档说明

本文档说明宠物综合服务平台后端当前实现。后端由 `chenhanyu` 负责，采用 Spring Boot 单体服务，为用户前台、管理后台、AI 助手、文件上传、监控健康检查提供统一 API。

详细接口以 `docs/api.md` 和 `docs/api.yaml` 为准；本文件只描述后端模块边界、技术栈、目录结构、运行方式和当前能力。

## 2. 技术栈

| 类型 | 技术 |
|---|---|
| 语言 | Java 17 |
| 框架 | Spring Boot 3 |
| Web | Spring MVC |
| 安全 | Spring Security + JWT |
| ORM | MyBatis-Plus |
| 数据库 | MySQL |
| 接口文档 | Springdoc OpenAPI |
| AI 调用 | OpenAI 兼容 Chat Completions，默认 DeepSeek |
| 文件存储 | 本地静态资源目录，保留扩展到对象存储的接口边界 |
| 测试 | JUnit 5、MockMvc、H2 |
| 构建 | Maven |

## 3. 目录结构

```text
backend/
├── Dockerfile
├── pom.xml
├── mvnw / mvnw.cmd
└── src/
    ├── main/
    │   ├── java/com/petplatform/
    │   │   ├── PetServicePlatformApplication.java
    │   │   ├── controller/          # 用户端接口
    │   │   ├── admin/controller/    # 管理端接口
    │   │   ├── service/             # 业务逻辑
    │   │   ├── mapper/              # MyBatis-Plus Mapper
    │   │   ├── entity/              # 数据库实体
    │   │   ├── dto/                 # 请求与响应 DTO
    │   │   ├── config/              # 配置类
    │   │   ├── security/            # JWT、认证、授权
    │   │   └── common/              # 统一响应、异常、工具类
    │   └── resources/
    │       ├── application.yml
    │       ├── mapper/
    │       └── sql/
    │           ├── schema.sql
    │           └── seed.sql
    └── test/
        └── java/com/petplatform/
```

## 4. API 边界

| 类型 | 前缀 | 说明 |
|---|---|---|
| 用户端 | `/api/v1` | 首页、认证、社区、领养、服务、商城、个人中心、消息、宠物档案、AI、文件上传 |
| 管理端 | `/api/v1/admin` | 仪表盘、用户、内容、领养、服务、商城、客服、监控 |
| 健康检查 | `/health`、`/api/v1/health` | 部署健康检查和前端状态页使用 |
| Swagger | `/swagger-ui.html` | 本地接口调试入口 |

统一响应结构：

```json
{
  "code": 0,
  "message": "ok",
  "data": {}
}
```

分页响应结构：

```json
{
  "list": [],
  "total": 0,
  "page": 1,
  "page_size": 10
}
```

## 5. 已实现模块

### 5.1 用户端

- 认证：手机号登录、注册、登出、验证码接口保留。
- 首页：Banner、推荐帖子、推荐服务、推荐商品、萌宠展示聚合。
- 搜索：社区、领养、服务、商城的统一搜索。
- 社区：帖子列表、详情、发布、评论、点赞、收藏、我的收藏。
- 领养：宠物列表、详情、流程说明、申请、我的申请。
- 服务：分类、商家、商家详情、预约、我的预约、取消预约。
- 商城：分类、商品、购物车、收货地址、优惠券、下单、直购、支付、取消、确认收货。
- 个人中心：用户资料、概览、宠物档案、成长时间轴、消息中心。
- 客服消息：在线客服和领养咨询提交、管理员回复通知。
- AI 助手：`POST /api/v1/ai/chat`，默认对接 DeepSeek。
- 文件上传：图片上传、本地静态资源访问。

### 5.2 管理端

- 管理员认证。
- 仪表盘：统计数据、待处理事项、最近操作。
- 用户管理：列表、详情、状态更新。
- 社区管理：帖子审核、评论删除。
- 领养管理：待领养宠物维护、申请审核。
- 服务管理：分类、商家、服务项目、预约、评价。
- 商城管理：商品、分类、订单处理。
- 内容管理：Banner、标签、推荐位。
- 客服消息：查看咨询、回复并标记已处理。
- 监控面板：汇总指标、路径指标、重置指标。

## 6. 数据库初始化

SQL 文件位于：

- `backend/src/main/resources/sql/schema.sql`
- `backend/src/main/resources/sql/seed.sql`

本地联调可以使用：

```bash
SPRING_SQL_INIT_MODE=always
```

生产环境完成初始化后应使用：

```bash
SPRING_SQL_INIT_MODE=never
```

`schema.sql` 中包含部分兼容现有生产表的 `ALTER TABLE` 迁移语句，例如 `shop_orders.discount_amount` 和 `shop_orders.user_coupon_id`，用于避免 `CREATE TABLE IF NOT EXISTS` 跳过已有表时漏列。

## 7. 本地运行

```bash
cd backend
.\mvnw.cmd spring-boot:run
```

常用地址：

- 健康检查：`http://127.0.0.1:8080/health`
- API 健康检查：`http://127.0.0.1:8080/api/v1/health`
- Swagger：`http://127.0.0.1:8080/swagger-ui.html`

## 8. 环境变量

| 变量 | 说明 |
|---|---|
| `SPRING_DATASOURCE_URL` | MySQL JDBC 地址 |
| `SPRING_DATASOURCE_USERNAME` | 数据库用户名 |
| `SPRING_DATASOURCE_PASSWORD` | 数据库密码 |
| `SPRING_SQL_INIT_MODE` | SQL 初始化模式，本地 `always`，生产初始化后 `never` |
| `JWT_SECRET` | JWT 签名密钥 |
| `JWT_EXPIRATION_SECONDS` | JWT 过期秒数 |
| `VERIFY_CODE_ALLOW_DEFAULT_CODE` | 是否允许默认验证码，本地演示可 `true`，生产应 `false` |
| `APP_CORS_ALLOWED_ORIGIN_PATTERNS` | 允许的前端来源，如当前 Vercel 域名 |
| `AI_API_KEY` / `DEEPSEEK_API_KEY` | AI 接口密钥 |
| `AI_BASE_URL` | OpenAI 兼容接口地址 |
| `AI_MODEL` | AI 模型名 |
| `FILE_STORAGE_TYPE` | 文件存储类型 |
| `FILE_STORAGE_LOCAL_PATH` | 本地文件存储路径 |
| `FILE_STORAGE_ACCESS_PATH` | 文件访问前缀 |

## 9. 测试

```bash
cd backend
.\mvnw.cmd test
```

当前测试覆盖方向：

- Controller 与权限边界。
- Service 业务规则。
- H2 集成链路。
- AI、消息、社区、商城、服务、领养等核心模块。

覆盖率配置和 CI 以根目录 README、GitHub Actions、Codecov 报告为准。

## 10. 部署

后端支持 Docker 构建，Railway 部署使用 `backend/Dockerfile`，构建上下文应为 `backend`。生产健康检查路径为 `/health`。

部署细节见 `docs/deployment.md`。
