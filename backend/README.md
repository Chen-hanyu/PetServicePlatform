# Backend Code Directory

此目录用于宠物综合服务平台的 Spring Boot 后端服务代码。

## 建议结构

```text
backend/
├── src/
│   ├── main/
│   │   ├── java/com/petplatform/
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
│   └── test/
├── pom.xml
└── README.md
```

## 本地开发
1. `mvn clean install`
2. `mvn spring-boot:run`
3. 访问 `http://127.0.0.1:8080/swagger-ui.html` 查看接口文档

## 开发说明
- `controller`：用户端接口
- `admin/controller`：管理员后台接口
- `security`：角色鉴权与 JWT 认证
