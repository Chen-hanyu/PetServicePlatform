# 安全审查报告

审查日期：2026-05-06

## 前端审查（qutianshun）

审查范围：`PostDetailPage.vue`、`http.ts`、`auth.ts`、`package.json`

### AI 审查发现的问题

#### 1. 依赖漏洞（可修复）
- **文件**：`package.json` 中 `axios` 版本 `1.8.1`
- **问题**：可能存在已知 CVE（如 SSRF/请求走私），建议升级到最新稳定版
- **危害等级**：低
- **修复**：升级至 `^1.9.0`

#### 2. 缺少环境变量模板（可修复）
- **问题**：前端使用 `import.meta.env.VITE_API_BASE_URL`，但未提供 `.env.example`
- **危害等级**：低（影响团队协作）
- **修复**：创建 `.env.example` 模板文件

#### 3. Token 存储风险（记录，暂不修复）
- **文件**：`store/auth.ts` 使用 `localStorage` 存储 JWT
- **危害等级**：中（XSS 攻击可窃取 token）
- **说明**：完全防护需后端配合使用 `httpOnly` cookie，前端已尽量限制 XSS（无 `v-html`），记录为待改进。

### 前端修复情况
- 升级 `axios` 至 `1.9.0`
- 创建 `.env.example`
- token 存储风险已记录，待后续优化

---

## 后端审查（chenhanyu）

审查范围：`SecurityConfig.java`、`JwtTokenProvider.java`、`JwtProperties.java`、`AuthService.java`、`UserService.java`、`ServiceBookingService.java`、`CommunityService.java`、`application.yml`、`GlobalExceptionHandler.java`、`PetService.java`、`ShopService.java`、`MessageService.java`

### AI 审查发现的问题

#### 1. SQL 注入风险（已修复）
- **文件**：`ServiceBookingService.java`、`CommunityService.java`
- **问题**：使用 `inSql()` 方法时拼接了分类 ID 和标签 ID，存在 SQL 注入风险
- **危害等级**：中
- **修复**：改为先查询符合条件的 ID 列表，再使用 `in()` 参数化查询

#### 2. JWT 密钥硬编码默认值（已修复）
- **文件**：`application.yml`
- **问题**：`jwt.secret` 存在默认值 `replace-with-a-secure-secret-key-1234567890`
- **危害等级**：中
- **修复**：移除默认值，强制通过 `JWT_SECRET` 环境变量注入

#### 3. 验证码硬编码默认值（已修复）
- **文件**：`application.yml`
- **问题**：`verify-code.default-code=123456` 且 `allow-default-code=true`
- **危害等级**：中
- **修复**：将 `allow-default-code` 默认设为 `false`，移除 `default-code` 默认值

#### 4. CORS 配置较宽松（记录，暂不修复）
- **文件**：`SecurityConfig.java`
- **问题**：允许了内网 IP 段通配
- **危害等级**：低
- **说明**：当前为开发阶段配置，生产环境应限制为具体域名

### 后端已确认的安全措施
- 密码存储：使用 `BCryptPasswordEncoder` 哈希存储，不存明文
- JWT 过期：默认 7200 秒，可通过环境变量配置
- 接口鉴权：SecurityConfig 已配置，管理端需 `ADMIN` 角色
- 越权访问：PetService、ShopService、MessageService、ServiceBookingService 均有 userId 归属校验
- 全局异常处理：不暴露内部细节，统一返回 `{ code, message, data }`
- ORM 参数化查询：除已修复的两处外，全部使用 MyBatis-Plus 参数化查询
- API Key 环境变量：AI API Key 通过 `AI_API_KEY` 环境变量读取
- .env 文件：已加入 `.gitignore`

### 后端修复情况
- 修复 SQL 注入（`ServiceBookingService.java`、`CommunityService.java`）
- 移除 JWT secret 默认值，强制环境变量注入
- 禁用验证码默认值，`allow-default-code` 默认设为 `false`

---

## 安全检查清单

| 检查项 | 状态 | 说明 |
|--------|------|------|
| 密码存储 | 已做 | 后端使用 BCryptPasswordEncoder |
| JWT/Session | 已做 | 有过期时间（7200s），无状态 JWT |
| 接口鉴权 | 已做 | SecurityConfig 已配置，管理端需 ADMIN 角色 |
| 越权访问 | 已做 | 各 Service 层均有 userId 归属校验 |
| SQL 注入 | 已做 | 已修复两处字符串拼接，全部使用 ORM 参数化查询 |
| XSS 防护 | 不适用 | 后端返回 JSON，前端负责输出净化 |
| API Key 硬编码 | 已做 | 已移除默认值，强制通过环境变量读取 |
| .env 文件 | 已做 | 已加入 .gitignore，仓库中有 .env.example |
| 依赖安全 | 已做 | CI 中配置 gitleaks 扫描密钥泄露 |

## CI 安全扫描配置
- 已集成 Gitleaks（选项 A），扫描提交历史中的密钥泄露。
- 工作流文件：`.github/workflows/security.yml`

## 结论
本次安全审查覆盖了前端和后端核心代码。前端修复了依赖版本和配置缺失问题；后端修复了 2 处 SQL 注入风险，移除了 JWT 密钥和验证码的硬编码默认值。剩余 token 存储风险（前端）和 CORS 配置（后端）已记录，待后续优化。
