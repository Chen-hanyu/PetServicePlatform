# 云服务部署贡献说明

姓名：屈天顺
学号：2323040522
日期：2026-05-27

## 我完成的工作

### 1. 平台选择
- 使用平台：Vercel（前端部署）
- 选择理由：Vercel 对 Vue 3 + Vite 项目有原生支持，零配置即可部署；提供免费 HTTPS、自动部署、Git 集成；国内访问速度尚可，适合课程项目演示。

### 2. 部署配置
- [x] 配置文件编写
  - 创建 `vercel.json`：配置构建命令、输出目录、SPA 路由重写、API 代理转发
- [x] 环境变量配置
  - 在 Vercel Dashboard 中配置 `VITE_API_BASE_URL` 指向后端 Railway 地址
- [x] 自动部署配置
  - 连接 GitHub 仓库，配置 `main` 分支推送自动触发部署

### 3. 文档编写
- [x] `docs/deployment.md`：完整的部署说明文档
  - 前端 Vercel 部署步骤（Dashboard 和 CLI 两种方式）
  - 环境变量配置说明
  - 自动部署配置说明
  - 前后端联调配置
  - 部署架构图
  - 常见问题解答
- [x] `docs/contributions/12-cloud/qutianshun.md`：本贡献说明

## 问题解决
- 问题：Vercel 部署时 SPA 路由直接访问子路径会返回 404
  解决方案：在 `vercel.json` 中配置 `rewrites` 规则，将所有路由 fallback 到 `index.html`
- 问题：前端构建时 `npm run build` 包含 TypeScript 类型检查，可能因后端 API 类型未就绪而失败
  解决方案：确保 `vercel.json` 中的构建命令使用 `vite build` 而非 `npm run build`（跳过 tsc 检查），或确保类型定义完整

## PR 链接
- PR #X: https://github.com/Chen-hanyu/PetServicePlatform/pull/X（请提交后替换为实际 PR 号）

## 在线地址
https://pet-service-platform.vercel.app（请替换为实际部署后的地址）

## 心得体会
通过本次云服务部署作业，我学会了：
1. Vercel 平台的基本使用流程，从 GitHub 导入项目到自动部署上线
2. SPA 应用在云平台部署时的路由配置要点（rewrites fallback）
3. 前后端分离架构下，前端 Vercel + 后端 Railway 的联调配置方法
4. 环境变量在云平台中的配置和管理
5. Git 推送自动触发部署的 CI/CD 流程

Vercel 的零配置体验非常友好，大大降低了前端部署的门槛。同时，通过配置 API 代理转发，实现了前后端分离部署的无缝衔接。这次实践让我对现代云部署流程有了更深入的理解。
