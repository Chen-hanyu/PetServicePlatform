# Backend Code Directory

此目录用于宠物综合服务平台的 Spring Boot 后端服务代码。

## 当前技术栈

- Java 17
- Spring Boot 3
- Spring MVC
- Spring Security + JWT
- MyBatis-Plus
- MySQL
- Springdoc OpenAPI
- Maven / Maven Wrapper

## 当前目录结构

```text
backend/
├── .mvn/                                  # Maven Wrapper 配置
├── mvnw
├── mvnw.cmd
├── src/
│   ├── main/
│   │   ├── java/com/petplatform/
│   │   │   ├── PetServicePlatformApplication.java
│   │   │   ├── controller/
│   │   │   ├── admin/controller/
│   │   │   ├── service/
│   │   │   ├── mapper/
│   │   │   ├── entity/
│   │   │   ├── dto/
│   │   │   ├── config/
│   │   │   ├── security/
│   │   │   └── common/
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

说明：

- `.idea/` 为本地 IDEA 工程文件，不属于项目源码。
- `target/` 为 Maven 构建产物，不属于项目源码。
- 旧的 `com/chenhanyu/...` 空目录如仍存在，可在确认无文件后清理。

## 部署方式

### 本地部署
1. 配置 `src/main/resources/application.yml` 中的数据库账号、密码和 JWT 密钥
2. 执行 `mvnw.cmd clean install` 或 `mvn clean install`
3. 执行 `mvnw.cmd spring-boot:run` 或 `mvn spring-boot:run`
4. 访问 `http://127.0.0.1:8080/swagger-ui.html` 查看接口文档

### Docker 部署
1. 在项目根目录准备 `docker-compose.yml`
2. 在 `backend/` 目录准备 `Dockerfile`
3. 使用 `docker-compose up -d` 启动 `backend` 与 `mysql`
4. 使用 `docker-compose ps` 检查容器状态
5. 访问 `http://127.0.0.1:8080/swagger-ui.html` 验证服务可用

## 开发说明
- `controller`：用户端接口
- `admin/controller`：管理员后台接口
- `service`：业务逻辑层
- `mapper`：数据访问层
- `entity`：实体类定义
- `dto`：请求响应对象
- `config`：配置类
- `security`：角色鉴权与 JWT 认证
- `common`：统一返回、异常处理和公共工具

## 当前状态

- 当前后端骨架已经完成 Spring Boot 3 基础初始化，可在 IDEA 中直接作为 Maven 项目打开。
- 依赖和目录结构已与项目设计文档对齐，但业务代码尚未开始实现。
- 下一步建议优先补充：统一返回结构、基础安全配置、数据库连接验证、首批用户端和管理端控制器。
