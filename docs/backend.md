# 后端模块说明（宠物综合服务平台）

> 测试文档入口：[backend-test-plan.md](D:/Code/PetServicePlatform/docs/backend-test-plan.md)
> 简要说明：当前后端已建立 `MockMvc` 接口/权限测试、`service` 规则测试以及 H2 集成测试，用于验证主流程、权限边界和数据联动。

## 当前状态总览
- 当前后端接口覆盖清单、实现状态标记与最小自测说明见 [backend-status.md](D:/Code/PetServicePlatform/docs/backend-status.md)。

## 1. 模块功能
后端采用 `Java + Spring Boot` 构建，负责为用户 Web 前台和管理员后台提供统一的业务服务、权限控制和数据持久化能力。

后端承担的核心职责如下：
- 用户与鉴权：登录、角色区分、用户资料、消息中心、令牌校验。
- 社区内容：帖子发布、评论、点赞、收藏、内容审核、标签管理、推荐位管理。
- 领养业务：待领养宠物信息维护、领养申请、审核流转、状态追踪。
- 宠物服务：服务分类、商家、服务项目、预约单、商家评价、营业状态。
- 商城交易：商品分类、商品详情、购物车、订单、库存与上下架管理。
- 宠物档案：宠物基本信息、疫苗记录、体重记录、宠物相册、成长时间轴。
- 后台管理：仪表盘统计、用户管理、内容审核、订单处理、推荐内容配置。

## 2. 技术选型
- 语言：Java 17
- 框架：Spring Boot 3
- Web 框架：Spring MVC
- 权限认证：Spring Security + JWT
- 数据持久化：MyBatis-Plus
- 数据库：MySQL
- 缓存：Redis
- 文件存储：MinIO / 本地静态资源目录
- 接口文档：Springdoc OpenAPI
- 构建工具：Maven

选择 Spring Boot 的原因：
- 适合课程项目展示标准后端分层结构。
- 生态成熟，便于接入权限、日志、校验、文档和文件上传。
- 能自然支撑用户端和管理端共用一套服务、按角色授权的模式。

## 3. 当前目录结构

```text
backend/
├── .mvn/                                  # Maven Wrapper 配置
├── mvnw
├── mvnw.cmd
├── src/
│   ├── main/
│   │   ├── java/com/petplatform/
│   │   │   ├── PetServicePlatformApplication.java
│   │   │   ├── controller/          # 用户端接口
│   │   │   ├── admin/controller/    # 管理端接口
│   │   │   ├── service/             # 业务逻辑层
│   │   │   ├── mapper/              # 数据访问层
│   │   │   ├── entity/              # 实体类
│   │   │   ├── dto/                 # 请求响应对象
│   │   │   ├── config/              # 配置类
│   │   │   ├── security/            # 鉴权与权限
│   │   │   └── common/              # 通用返回、异常、工具类
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── mapper/
│   │       └── sql/
│   └── test/
│       └── java/com/petplatform/
│           └── PetServicePlatformApplicationTests.java
├── pom.xml
└── README.md
```

补充说明：

- `.idea/` 为本地开发环境文件，不作为项目结构的一部分。
- `target/` 为编译输出目录，不作为源码目录。
- 当前目录骨架已经与项目设计文档对齐，但各业务包下仍主要是占位结构，等待后续实现。

## 4. 部署方式

### 4.1 本地部署
1. 进入后端目录：`cd backend`
2. 按本地环境修改 `src/main/resources/application.yml`
3. 安装依赖并编译：`mvnw.cmd clean install` 或 `mvn clean install`
4. 启动开发环境：`mvnw.cmd spring-boot:run` 或 `mvn spring-boot:run`
5. 默认访问地址：`http://127.0.0.1:8080`
6. 接口文档地址：`http://127.0.0.1:8080/swagger-ui.html`

### 4.2 Docker 部署
1. 在项目根目录准备 `docker-compose.yml`
2. 在 `backend/` 目录准备 `Dockerfile`
3. 通过环境变量注入数据库连接、账号密码和 JWT 密钥
4. 使用 `docker-compose up -d` 启动 `backend` 与 `mysql`
5. 使用 `docker-compose ps` 检查服务状态
6. 服务启动后访问 `http://127.0.0.1:8080/swagger-ui.html`

## 5. 业务模块设计
### 5.1 用户端业务
- 首页聚合：Banner、推荐帖子、推荐服务、推荐商品、宠物小贴士。
- 社区：帖子、评论、点赞、收藏、发布。
- 领养：待领养宠物、领养详情、领养申请、申请状态。
- 宠物服务：商家、服务项目、预约、预约记录。
- 商城：商品、购物车、订单、订单状态。
- 个人中心：个人信息、我的宠物、宠物档案、我的内容、我的订单、消息中心。

### 5.2 管理端业务
- 仪表盘：用户数、帖子数、待审核申请数、订单统计、预约统计。
- 用户管理：查询用户、禁用账号、查看行为记录。
- 内容管理：帖子审核、评论处理、Banner 管理、推荐位管理、标签管理。
- 领养管理：待领养宠物录入、宠物状态维护、领养申请审核。
- 服务管理：商家信息维护、服务分类、服务项目配置、预约单处理。
- 商城管理：商品分类、商品上下架、库存管理、订单处理、折扣活动配置。

## 6. 数据与接口约定
- API 基础路径：`/api/v1`
- 管理端路径前缀：`/api/v1/admin`
- 统一响应结构：`{ code, message, data }`
- 列表响应统一返回：`list`、`total`、`page`、`page_size`
- 时间字段使用 ISO 8601 格式
- 用户角色至少包含：`USER`、`ADMIN`
- 业务状态使用明确枚举值，例如 `PENDING`、`APPROVED`、`REJECTED`、`CANCELLED`

## 7. 与前端协作约定
- 用户端和管理端登录后返回不同角色信息，前端据此控制路由与菜单。
- 推荐位、标签、状态角标、仪表盘统计数据由后端统一提供。
- 管理端接口必须支持分页、筛选、排序、批量操作。
- 所有审核类接口都需要返回清晰的状态流转结果和备注信息。

## 8. 当前开发状态

- 后端 Maven 工程已可正常编译和运行。
- 后端已完成课程项目 MVP 主流程，用户端与管理端核心业务链路可联调。
- 统一返回结构、全局异常处理、JWT 鉴权、角色隔离与分页能力均已落地。
- 自动化测试已覆盖接口、服务规则与 H2 集成链路（当前本地测试报告为 142 个用例全部通过）。
- 文件上传与验证码当前为 MVP 占位方案（本地存储、内存验证码）；Redis、MinIO、批量操作等为后续增强项。
- 详细接口覆盖与状态说明以 `docs/backend-status.md` 为准。
