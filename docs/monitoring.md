# 监控配置说明

## 1. 概述

本文档描述宠物综合服务平台前端的监控配置方案，涵盖结构化日志、健康检查、指标收集三个核心部分。

## 2. 前端结构化日志

### 2.1 日志系统架构

前端日志系统基于 `frontend/src/utils/apiLogger.ts` 实现，通过 Axios 拦截器自动捕获所有 API 请求并生成结构化日志。

### 2.2 日志格式

每条日志以 JSON 格式输出到浏览器控制台：

```json
{
  "time": "2026-05-27T16:00:00.000Z",
  "level": "INFO",
  "message": "API 请求成功",
  "module": "apiLogger",
  "method": "GET",
  "url": "/api/v1/home",
  "path": "/home",
  "status": 200,
  "duration": 320,
  "error": null
}
```

### 2.3 日志级别

| 级别 | 说明 | 控制台方法 |
|------|------|-----------|
| `DEBUG` | 调试信息，仅开发环境输出 | `console.debug` |
| `INFO` | 正常请求信息 | `console.info` |
| `WARN` | 警告（如 4xx 响应） | `console.warn` |
| `ERROR` | 错误（如网络异常、5xx 响应） | `console.error` |

### 2.4 日志配置

- 开发环境默认启用日志，级别为 `DEBUG`
- 生产环境默认启用日志，级别为 `INFO`
- 可通过 `apiLogger.setLevel()` 动态调整日志级别
- 可通过 `apiLogger.toggleEnabled()` 开关日志记录

### 2.5 日志拦截器

`frontend/src/utils/apiInterceptor.ts` 实现了 Axios 请求/响应拦截器，自动完成以下工作：

1. **请求拦截**：记录请求方法、URL、参数、请求体，记录开始时间
2. **响应拦截**：记录响应状态码、响应数据、计算耗时
3. **错误拦截**：记录错误信息、异常状态码

## 3. 健康检查

### 3.1 前端健康检查页面

- **路径**：`/health`
- **页面文件**：`frontend/src/pages/web/monitoring/HealthCheckPage.vue`
- **调用端点**：`GET /api/v1/health`

### 3.2 页面功能

- 展示服务运行状态（绿色=正常 / 红色=异常）
- 显示检查时间戳、服务版本号
- 显示响应耗时
- 每 30 秒自动刷新
- 支持手动刷新
- 显示原始 JSON 响应

### 3.3 预期响应格式

```json
{
  "status": "healthy",
  "timestamp": "2026-05-27T16:00:00.000Z",
  "version": "1.0.0"
}
```

### 3.4 导航入口

- 用户前台导航栏：**系统状态**
- 路由：`/health`

## 4. 指标收集

### 4.1 前端监控面板

- **路径**：`/admin/monitoring`
- **页面文件**：`frontend/src/pages/admin/monitoring/MonitorDashboard.vue`
- **管理后台菜单**：**监控面板**

### 4.2 收集的指标

| 指标 | 说明 | 计算方式 |
|------|------|---------|
| 总请求数 | 所有记录的 API 请求总数 | 日志计数 |
| 成功请求 | 状态码 < 400 的请求数 | 日志过滤 |
| 失败请求 | 状态码 >= 400 或有错误的请求数 | 日志过滤 |
| 错误率 | 失败请求占总请求的百分比 | `(失败 / 总数) × 100%` |
| 平均响应时间 | 所有请求的平均耗时 | `总耗时 / 请求数` |
| 最慢请求 | 耗时最长的请求 | `max(耗时)` |

### 4.3 监控面板功能

- 实时显示 6 项核心指标卡片
- 实时日志列表（每 2 秒自动刷新）
- 日志可展开查看详情（请求数据、响应数据、错误信息）
- 支持日志记录开关
- 支持清空日志
- 日志按状态着色（成功=绿、警告=橙、错误=红）

### 4.4 指标 API

通过 `apiLogger.getMetrics()` 方法获取统计指标：

```typescript
const metrics = apiLogger.getMetrics();
// {
//   total: number,      // 总请求数
//   success: number,    // 成功请求数
//   failed: number,     // 失败请求数
//   avgDuration: number, // 平均响应时间(ms)
//   maxDuration: number, // 最慢请求时间(ms)
//   errorRate: number   // 错误率(%)
// }
```

## 5. 目录结构

```
frontend/
├── src/
│   ├── pages/
│   │   ├── web/
│   │   │   └── monitoring/
│   │   │       └── HealthCheckPage.vue    # 健康检查页面
│   │   └── admin/
│   │       └── monitoring/
│   │           └── MonitorDashboard.vue   # 监控面板页面
│   ├── utils/
│   │   ├── apiLogger.ts                   # 结构化日志系统
│   │   └── apiInterceptor.ts              # API 拦截器
│   ├── router/
│   │   └── index.ts                       # 路由配置
│   ├── layout/
│   │   ├── WebLayout.vue                  # 前台布局（含系统状态入口）
│   │   └── AdminLayout.vue                # 后台布局（含监控面板菜单）
│   └── main.ts                            # 入口文件（初始化日志）
docs/
└── monitoring.md                          # 本文件
```

## 6. 使用说明

### 6.1 查看健康状态

1. 打开用户前台
2. 点击导航栏「系统状态」
3. 查看服务运行状态和详细信息

### 6.2 查看监控指标

1. 登录管理员账号
2. 进入管理后台
3. 点击侧边栏「监控面板」
4. 查看实时指标和日志

### 6.3 查看结构化日志

1. 打开浏览器开发者工具（F12）
2. 切换到 Console 面板
3. 发送 API 请求后，查看 JSON 格式的结构化日志输出
