# 安全审查贡献说明

姓名：chenhanyu
学号：2320100624
日期：2026-05-06

## 我完成的工作

### AI 安全审查

- 审查了以下后端模块：
  - SecurityConfig.java（接口鉴权配置）
  - JwtTokenProvider.java / JwtProperties.java（JWT 认证）
  - AuthService.java / UserService.java（认证与密码存储）
  - ServiceBookingService.java / CommunityService.java（SQL 注入风险）
  - application.yml（密钥与敏感信息配置）
  - GlobalExceptionHandler.java（错误信息暴露）
  - PetService.java / ShopService.java / MessageService.java（越权访问控制）
- AI 发现的主要问题：
  1. SQL 注入风险：ServiceBookingService.java 和 CommunityService.java 中存在字符串拼接 SQL
  2. JWT 密钥和验证码存在硬编码默认值
  3. CORS 配置较宽松
- 我修复了以下问题：
  1. 修复 SQL 注入：将字符串拼接改为参数化查询
  2. 移除 JWT secret 和验证码的默认值，强制通过环境变量配置

### 安全检查清单

- [X] 密码存储：使用 BCryptPasswordEncoder
- [X] JWT/Session：有过期时间（7200s），无状态 JWT
- [X] 接口鉴权：SecurityConfig 已配置，管理端需 ADMIN 角色
- [X] 越权访问：PetService、ShopService、MessageService、ServiceBookingService 均有 userId 归属校验
- [X] SQL 注入：已修复两处字符串拼接，全部使用 ORM 参数化查询
- [X] XSS 防护：N/A（后端返回 JSON）
- [X] API Key 硬编码：已移除默认值，强制通过环境变量读取
- [X] .env 文件：已加入 .gitignore
- [X] 依赖安全：CI 已集成 Gitleaks

### CI 安全扫描

- 配置了选项 A：密钥泄露扫描（Gitleaks）
- 工作流文件：.github/workflows/security.yml
- 扫描结果：通过

### 选做完成情况

#### 1. 安全 HTTP 头 ✅
在 SecurityConfig.java 中配置了以下安全响应头：
- CSP（Content-Security-Policy）：限制资源加载来源，防止 XSS 和数据注入
- HSTS（HTTP Strict-Transport-Security）：强制 HTTPS 连接，有效期 1 年，包含子域名
- X-Frame-Options：设置为 SAMEORIGIN，防止点击劫持
- X-XSS-Protection：启用浏览器 XSS 过滤器并设置为 block 模式

#### 2. CodeQL 扫描 ✅
- 配置了 GitHub CodeQL 静态分析，覆盖 Java 和 JavaScript/TypeScript 代码
- 在 push、pull_request 和每周定时触发
- 工作流文件：.github/workflows/codeql.yml

## PR 链接

- https://github.com/Chen-hanyu/PetServicePlatform/pull/43


## 遇到的问题和解决

1. 问题：ServiceBookingService.java 中 inSql 方法拼接了分类 ID，存在 SQL 注入风险。
   解决：改为先查询符合条件的商家 ID 列表，再用 in 条件查询，避免字符串拼接。
2. 问题：application.yml 中 JWT secret 和验证码有默认值，生产环境存在安全隐患。
   解决：移除默认值，强制通过环境变量注入。

## 心得体会

- 安全审查不能只关注功能实现，配置文件的默认值同样可能成为安全短板。
- ORM 框架虽然能防范大部分 SQL 注入，但 inSql 等动态 SQL 方法仍需谨慎使用。
- 环境变量注入敏感信息是最基本的安全实践，应避免任何形式的硬编码默认值。
- Vibe Coding 场景下，AI 辅助审查能快速发现常见漏洞，但人工确认和修复仍是关键。
