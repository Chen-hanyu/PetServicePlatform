# PetServicePlatform Backend

本目录是宠物综合服务平台后端工程，基于 Java 17 + Spring Boot 3 实现，为用户 Web 前台和管理员后台提供统一 REST API。

## 技术栈

- Java 17
- Spring Boot 3
- Spring MVC
- Spring Security + JWT
- MyBatis-Plus
- MySQL
- Springdoc OpenAPI
- JUnit 5、Mockito、Spring Boot Test、H2、JaCoCo
- Maven Wrapper

## 目录结构

```text
backend/
├── src/
│   ├── main/
│   │   ├── java/com/petplatform/
│   │   │   ├── admin/controller/       # 管理端接口
│   │   │   ├── common/                 # 统一响应、异常、日志、指标
│   │   │   ├── config/                 # 安全、AI、JSON、文件配置
│   │   │   ├── controller/             # 用户端接口
│   │   │   ├── dto/                    # 请求/响应 DTO
│   │   │   ├── entity/                 # 实体类
│   │   │   ├── mapper/                 # MyBatis-Plus Mapper
│   │   │   ├── security/               # JWT、当前用户、鉴权过滤器
│   │   │   └── service/                # 业务逻辑
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── mapper/
│   │       └── sql/
│   │           ├── schema.sql
│   │           └── seed.sql
│   └── test/                           # 单元测试、WebMvc 测试、集成测试
├── Dockerfile
├── .dockerignore
├── pom.xml
├── mvnw
└── mvnw.cmd
```

## 已实现模块

- 认证：用户/管理员登录、注册、验证码调试入口、JWT 鉴权。
- 首页与搜索：首页聚合数据、全站搜索。
- 社区：帖子列表、关键词/分类/标签筛选、详情、发帖、删除、评论、点赞、收藏。
- 领养：待领养宠物、流程说明、申请提交、我的申请、后台审核。
- 服务：服务分类、商家列表、商家详情、预约、取消预约、后台预约处理和评价管理。
- 商城：分类、商品、购物车、收货地址、优惠券、订单、支付、取消、确认收货、后台商品/订单管理。
- 宠物档案：宠物资料、疫苗、体重、相册、成长时间轴。
- 消息与客服：消息列表、已读、删除、在线客服提交、后台客服回复。
- AI：`POST /api/v1/ai/chat`，兼容 DeepSeek/OpenAI Chat Completions。
- 运维：健康检查、API 访问日志、管理端监控指标。

## 本地运行

1. 准备 MySQL 数据库 `pet_service_platform`。
2. 按 `.env.example` 或 `application.yml` 配置数据库、JWT、AI 等环境变量。
3. 首次初始化数据库时可设置：

```env
SPRING_SQL_INIT_MODE=always
```

初始化完成后建议改回：

```env
SPRING_SQL_INIT_MODE=never
```

4. 启动后端：

```bash
./mvnw spring-boot:run
```

Windows 可使用：

```powershell
.\mvnw.cmd spring-boot:run
```

默认地址：

- API：`http://localhost:8080/api/v1`
- Swagger：`http://localhost:8080/swagger-ui.html`
- 健康检查：`http://localhost:8080/health`

## 测试

```bash
./mvnw test
```

最近本地测试结果：

- Tests：213 passed
- JaCoCo Instructions：75.46%
- JaCoCo Lines：76.84%
- JaCoCo Methods：82.21%

## Docker

```bash
docker build -f backend/Dockerfile backend
```

项目根目录可使用：

```bash
docker compose up -d --build
```

## 关键环境变量

| 变量 | 说明 |
|---|---|
| `SPRING_DATASOURCE_URL` | MySQL JDBC 地址 |
| `SPRING_DATASOURCE_USERNAME` | 数据库用户名 |
| `SPRING_DATASOURCE_PASSWORD` | 数据库密码 |
| `SPRING_SQL_INIT_MODE` | SQL 初始化模式，首次初始化 `always`，生产稳定后 `never` |
| `JWT_SECRET` | JWT 签名密钥 |
| `VERIFY_CODE_ALLOW_DEFAULT_CODE` | 是否允许默认验证码，生产建议 `false` |
| `FILE_STORAGE_LOCAL_PATH` | 上传文件存储目录 |
| `FILE_STORAGE_ACCESS_PATH` | 上传文件访问路径 |
| `APP_CORS_ALLOWED_ORIGIN_PATTERNS` | 允许跨域来源 |
| `DEEPSEEK_API_KEY` / `AI_BASE_URL` / `AI_MODEL` | AI 服务配置 |

## 文档

- API 使用说明：`docs/api.md`
- OpenAPI 规范：`docs/api.yaml`
- 数据库设计：`docs/database.md`
- 部署说明：`docs/deployment.md`
- 监控说明：`docs/monitoring.md`
- 安全审查：`docs/security-review.md`
