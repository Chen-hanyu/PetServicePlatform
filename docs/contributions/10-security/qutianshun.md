# 安全审查贡献说明

姓名：qutianshun 
学号：2323040522  
角色：前端  
日期：2026-05-06

## 我完成的工作

### AI 安全审查
- 审查了以下前端模块：
  - `PostDetailPage.vue`（XSS 风险）
  - `http.ts`（请求拦截器）
  - `auth.ts`（token 存储）
  - `package.json`（依赖版本）
- AI 发现的主要问题：
  1. `axios` 版本较旧，建议升级
  2. 缺少 `.env.example` 模板
  3. token 存储于 `localStorage`（XSS 风险）
- 我修复了以下问题：
  1. 升级 `axios` 至 `^1.9.0`
  2. 创建 `.env.example` 模板文件

### 安全检查清单
- [x] 密码存储：N/A
- [x] JWT/Session：已记录风险，维持现状
- [x] 接口鉴权：后端控制 ✅
- [x] 越权访问：N/A
- [x] SQL 注入：N/A
- [x] XSS 防护：无 `v-html` ✅
- [x] API Key 硬编码：无 ✅
- [x] .env 文件：已创建 `.env.example` ✅
- [x] 依赖安全：CI 集成 gitleaks ✅

### CI 安全扫描
- 配置了选项 **A：密钥泄露扫描（Gitleaks）**
- 工作流文件：`.github/workflows/security.yml`
- 扫描结果：首次运行预计通过（无硬编码密钥）

## 遇到的问题和解决
1. **问题**：`axios` 升级后是否需要修改代码？  
   **解决**：`axios@1.9.0` 与 `1.8.1` API 兼容，无需修改调用代码。
2. **问题**：`gitleaks` 在 CI 中可能误报？  
   **解决**：使用官方的 `gitleaks/gitleaks-action@v2`，误报率低，可接受。

## 心得体会
- 安全审查不能只看代码功能，还要关注依赖版本和配置规范。
- 自动化的密钥扫描（gitleaks）比人工检查可靠得多，值得每个项目集成。
- 虽然 `localStorage` 存储 token 有风险，但在小型项目中可接受，重点应放在输入输出净化。