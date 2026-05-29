# 监控配置贡献说明

姓名：chenhanyu
学号：2320100624
日期：2026-05-29

## 我完成的工作

### 1. 日志配置

- [x] 结构化日志格式（Logback + Logstash Logback Encoder JSON 格式）
- [x] 日志级别配置（INFO / WARN / ERROR）
- [x] 日志文件滚动策略（按天滚动，保留 7 天）
- [x] 开发/生产环境日志格式分离

### 2. 健康检查

- [x] /health 端点实现（增强版）
- [x] 健康检查逻辑（服务状态、数据库连接检查、运行时长）
- [x] 返回版本号、时间戳等详细信息

### 3. 指标收集

- [x] 请求计数（总请求数、成功/失败请求数）
- [x] 响应时间（平均响应时间、最慢请求时间）
- [x] 错误率（失败请求占比）
- [x] 管理端指标查询接口（/api/v1/admin/monitoring/metrics）

## PR 链接

- PR #X: https://github.com/Chen-hanyu/PetServicePlatform/pull/X

## 遇到的问题和解决

1. 问题：Logstash Logback Encoder 依赖版本与 Spring Boot 3.5.x 的兼容性
   解决：使用 logstash-logback-encoder 8.0 版本，与 Spring Boot 3.5.x 兼容

2. 问题：健康检查端点需要检查数据库状态，但数据库不可用时不应导致应用崩溃
   解决：使用 try-catch 捕获数据库连接异常，返回 DOWN 状态而非抛出异常

3. 问题：指标收集需要线程安全，避免高并发下的数据竞争
   解决：使用 AtomicInteger 和 AtomicLong 原子类，ConcurrentHashMap 存储路径统计

## 心得体会

通过本次监控配置实践，我深入了解了后端可观测性的三个核心支柱：结构化日志、健康检查和指标收集。

在日志配置方面，我学习了 Logback 的 XML 配置方式，以及如何使用 Logstash Logback Encoder 实现 JSON 格式的结构化日志输出。JSON 格式的日志便于后续接入日志收集和分析系统（如 ELK Stack）。

在健康检查方面，我增强了原有的 /health 端点，增加了数据库连接状态检查和运行时长信息，使健康检查更加全面。这对于云平台（如 Railway）的自动健康检查非常重要。

在指标收集方面，我设计了一个线程安全的 MetricsCollector 组件，使用原子类确保高并发下的数据准确性。同时提供了管理端 REST 接口，方便管理员查看实时指标。

这次实践让我对 Spring Boot 的监控生态有了更全面的认识，也理解了可观测性在生产环境中的重要性。
