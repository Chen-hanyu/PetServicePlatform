# 前端安全审查报告

审查日期：2026-05-06  
审查人：qutianshun（前端）  
审查范围：`PostDetailPage.vue`、`http.ts`、`auth.ts`、`package.json`

## AI 审查发现的问题

### 1. 依赖漏洞（可修复）
- **文件**：`package.json` 中 `axios` 版本 `1.8.1`
- **问题**：可能存在已知 CVE（如 SSRF/请求走私），建议升级到最新稳定版
- **危害等级**：低
- **修复**：升级至 `^1.9.0`

### 2. 缺少环境变量模板（可修复）
- **问题**：前端使用 `import.meta.env.VITE_API_BASE_URL`，但未提供 `.env.example`
- **危害等级**：低（影响团队协作）
- **修复**：创建 `.env.example` 模板文件

### 3. Token 存储风险（记录，暂不修复）
- **文件**：`store/auth.ts` 使用 `localStorage` 存储 JWT
- **危害等级**：中（XSS 攻击可窃取 token）
- **说明**：完全防护需后端配合使用 `httpOnly` cookie，前端已尽量限制 XSS（无 `v-html`），记录为待改进。

## 修复情况
- ✅ 升级 `axios` 至 `1.9.0`
- ✅ 创建 `.env.example`
- ⚠️ token 存储风险已记录，待后续优化

## 安全检查清单

| 检查项 | 状态 | 说明 |
|--------|------|------|
| 密码存储 | N/A | 前端不存储密码 |
| JWT/Session | ⚠️ | token 存于 localStorage，已记录风险 |
| 接口鉴权 | ✅ | 后端控制，前端携带 token |
| 越权访问 | N/A | 前端只展示数据 |
| SQL 注入 | N/A | 前端无 SQL |
| XSS 防护 | ✅ | 无 `v-html` 使用 |
| API Key 硬编码 | ✅ | 无硬编码 |
| .env 文件 | ✅ | 已创建 `.env.example`，`.env` 已忽略 |
| 依赖安全 | ✅ | CI 中配置 gitleaks 扫描密钥泄露 |

## CI 安全扫描配置
- 已集成 **Gitleaks**（选项 A），扫描提交历史中的密钥泄露。
- 工作流文件：`.github/workflows/security.yml`

## 结论
本次审查未发现高危漏洞，已修复依赖版本和配置缺失，并添加自动化密钥扫描。剩余 token 存储风险已记录。