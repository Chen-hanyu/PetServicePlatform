import pathlib
import subprocess

# Read the original frontend content from git (previous commit)
result = subprocess.run(['git', 'show', 'HEAD~1:docs/monitoring.md'], capture_output=True)
frontend_content = result.stdout.decode('utf-8', errors='replace')

# Backend additions
backend_content = """

## 5. 后端结构化日志

### 5.1 日志系统架构

后端日志系统基于 **Logback + Logstash Logback Encoder** 实现，所有日志以 JSON 格式输出到控制台和文件。

### 5.2 日志配置

日志配置文件：`backend/src/main/resources/logback-spring.xml`

#### 日志输出目标

| 目标 | 说明 | 格式 |
|------|------|------|
| JSON_CONSOLE | 控制台输出（生产环境） | JSON |
| CONSOLE | 控制台输出（开发环境） | 文本 |
| JSON_FILE | 滚动文件输出 | JSON |
| API_LOG_FILE | API 访问日志独立文件 | JSON |

#### 日志格式

每条日志以 JSON 格式输出：

```json
{
  "@timestamp": "2026-05-29T07:00:00.000Z",
  "level": "INFO",
  "logger": "com.petplatform.common.filter.ApiAccessLogFilter",
  "message": "{type=api_access, method=GET, path=/api/v1/home, status=200, durationMs=320}",
  "thread": "http-nio-8080-exec-1"
}
```

#### 日志级别

| 级别 | 说明 |
|------|------|
| INFO | 正常请求信息、业务日志 |
| WARN | 警告（如 4xx 响应） |
| ERROR | 错误（如 5xx 响应、未处理异常） |

#### 日志文件管理

- 日志文件路径：`logs/app.json`（当前）、`logs/app.YYYY-MM-DD.json`（历史）
- API 日志文件：`logs/api.log`（当前）、`logs/api.YYYY-MM-DD.log`（历史）
- 保留最近 7 天的日志文件
- 开发环境使用文本格式，方便本地调试

### 5.3 API 访问日志

`ApiAccessLogFilter` 拦截所有 `/api/*` 请求，自动记录：

- HTTP 方法（GET、POST 等）
- 请求路径
- 响应状态码
- 响应时间（毫秒）

### 5.4 异常日志

`GlobalExceptionHandler` 统一捕获并记录异常日志：

- 业务异常（BusinessException）：记录警告日志
- 参数校验异常：记录警告日志
- 未处理异常：记录错误日志（含堆栈信息）

## 6. 后端健康检查

### 6.1 健康检查端点

- **路径**：`GET /health`
- **控制器**：`backend/src/main/java/com/petplatform/controller/HealthController.java`

### 6.2 响应格式

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "status": "UP",
    "timestamp": "2026-05-29T15:00:00",
    "service": "PetServicePlatform",
    "version": "0.0.1-SNAPSHOT",
    "uptime": "1h 23m 45s",
    "database": "UP"
  }
}
```

### 6.3 检查内容

| 字段 | 说明 |
|------|------|
| status | 服务运行状态（UP / DOWN） |
| timestamp | 当前时间戳 |
| service | 服务名称 |
| version | 应用版本号 |
| uptime | 应用运行时长 |
| database | 数据库连接状态（UP / DOWN） |

### 6.4 健康检查配置

在 `railway.toml` 中配置了健康检查：

```toml
[deploy]
  healthcheckPath = "/health"
  healthcheckTimeout = 30
```

在 Dockerfile 中配置了容器健康检查：

```dockerfile
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \\
  CMD wget -qO- http://localhost:8080/health || exit 1
```

## 7. 后端指标收集

### 7.1 指标收集器

`MetricsCollector` 是一个 Spring 组件，使用线程安全的原子类收集 API 请求指标。

- **文件**：`backend/src/main/java/com/petplatform/common/metrics/MetricsCollector.java`

### 7.2 收集的指标

| 指标 | 说明 | 计算方式 |
|------|------|---------|
| 总请求数 | 所有 API 请求总数 | 原子计数器 |
| 成功请求 | 状态码 < 400 的请求数 | 原子计数器 |
| 失败请求 | 状态码 >= 400 的请求数 | 原子计数器 |
| 错误率 | 失败请求占总请求的百分比 | (失败 / 总数) x 100% |
| 平均响应时间 | 所有请求的平均耗时 | 总耗时 / 请求数 |
| 最慢请求时间 | 耗时最长的请求耗时 | max(耗时) |
| 最慢请求路径 | 耗时最长的请求路径 | 记录路径 |

### 7.3 管理端指标接口

管理员可通过以下接口查看指标数据：

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/v1/admin/monitoring/metrics | GET | 获取汇总指标 |
| /api/v1/admin/monitoring/metrics/paths | GET | 获取各路径统计 |
| /api/v1/admin/monitoring/metrics/reset | POST | 重置指标数据 |

所有指标接口需要 ADMIN 角色权限。

### 7.4 指标集成

`ApiAccessLogFilter` 在记录日志的同时，调用 `MetricsCollector.record()` 收集指标数据，实现日志与指标的自动关联。

## 8. 目录结构

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
backend/
├── src/main/
│   ├── java/com/petplatform/
│   │   ├── common/
│   │   │   ├── filter/
│   │   │   │   └── ApiAccessLogFilter.java    # API 访问日志过滤器
│   │   │   ├── metrics/
│   │   │   │   └── MetricsCollector.java       # 指标收集器
│   │   │   └── exception/
│   │   │       └── GlobalExceptionHandler.java # 全局异常处理
│   │   ├── controller/
│   │   │   └── HealthController.java           # 健康检查端点
│   │   └── admin/controller/
│   │       └── MonitoringController.java       # 管理端指标接口
│   └── resources/
│       └── logback-spring.xml                  # 日志配置
docs/
└── monitoring.md                               # 本文件
```

## 9. 使用说明

### 9.1 查看健康状态

1. 打开用户前台，点击导航栏「系统状态」
2. 或直接访问后端健康检查端点：
```bash
curl https://petserviceplatform-production.up.railway.app/health
```

### 9.2 查看监控指标

1. 登录管理员账号，进入管理后台，点击侧边栏「监控面板」
2. 或直接调用后端指标接口：
```bash
curl -H "Authorization: Bearer <admin-token>" \\
  https://petserviceplatform-production.up.railway.app/api/v1/admin/monitoring/metrics
```

### 9.3 查看结构化日志

- **前端**：打开浏览器开发者工具（F12），切换到 Console 面板
- **后端**：在 Railway Dashboard 中查看后端服务的日志输出
"""

full = frontend_content.rstrip() + backend_content
pathlib.Path('docs/monitoring.md').write_text(full, encoding='utf-8')
print('Done! Length:', len(full))
