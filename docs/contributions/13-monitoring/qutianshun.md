# 监控配置贡献说明

姓名：qutianshun
学号：2323040522
日期：2026-05-27

## 我完成的工作

### 1. 日志配置
- [x] 结构化日志格式
  - 增强 `frontend/src/utils/apiLogger.ts`，添加结构化 JSON 日志输出到浏览器控制台
  - 日志格式：`{"time":"...","level":"INFO","message":"...","module":"apiLogger","method":"GET","url":"/api/v1/home","duration":320}`
  - 支持 DEBUG / INFO / WARN / ERROR 四级日志级别
  - 开发环境默认 DEBUG 级别，生产环境默认 INFO 级别
- [x] 日志级别配置
  - 提供 `apiLogger.setLevel()` 动态调整日志级别
  - 提供 `apiLogger.toggleEnabled()` 开关日志记录
  - 通过 Axios 拦截器（`apiInterceptor.ts`）自动捕获所有 API 请求

### 2. 健康检查
- [x] /health 端点前端展示
  - 创建 `frontend/src/pages/web/monitoring/HealthCheckPage.vue`
  - 调用 `GET /api/v1/health` 端点展示服务状态
  - 状态指示灯（绿色=healthy / 红色=unhealthy）
  - 显示检查时间戳、服务版本号、响应耗时
  - 每 30 秒自动刷新，支持手动刷新
  - 显示原始 JSON 响应
- [x] 健康检查路由与入口
  - 路由配置：`/health`
  - 用户前台导航栏添加「系统状态」入口

### 3. 指标收集
- [x] 请求计数
  - 通过 `apiLogger.getMetrics()` 统计总请求数、成功/失败请求数
- [x] 响应时间
  - 统计平均响应时间和最慢请求耗时
- [x] 错误率
  - 计算失败请求占总请求的百分比
  - 创建 `frontend/src/pages/admin/monitoring/MonitorDashboard.vue` 监控面板
  - 管理后台侧边栏添加「监控面板」菜单
  - 实时日志列表（每 2 秒自动刷新），可展开查看详情


## 遇到的问题和解决
1. 问题：apiLogger 原有的日志记录功能没有结构化 JSON 输出，无法满足作业要求
   解决：在 `apiLogger.addLog()` 方法中添加 `structuredLog()` 函数，每次记录日志时同时输出结构化 JSON 到控制台，包含时间、级别、模块名、请求详情等字段

2. 问题：健康检查页面需要调用后端 `/api/v1/health` 端点，但后端尚未实现
   解决：前端页面已做好完整调用逻辑，后端实现后即可正常工作；页面也处理了请求失败的情况，显示错误状态

3. 问题：监控面板需要实时刷新日志数据
   解决：使用 `setInterval` 每 2 秒轮询 `apiLogger.getLogs()` 获取最新日志，组件卸载时清除定时器避免内存泄漏

## 心得体会
通过本次监控配置作业，我学会了：
1. 前端结构化日志的设计与实现，包括日志级别控制、JSON 格式化输出
2. 健康检查端点的前端展示方案，包括状态指示灯、自动刷新、错误处理
3. 前端指标收集的方法，通过拦截器自动采集请求数据并计算统计指标
4. 监控面板的设计，将日志数据可视化展示为指标卡片和日志列表
5. 前端监控与后端监控的分工协作模式

前端监控虽然不能替代后端监控，但可以提供用户侧的请求性能数据和错误信息，对排查前端问题、优化用户体验有重要价值。
