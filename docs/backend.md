# 后端模块说明

## 1. 模块功能
- 提供业务接口与数据处理逻辑。
- 承担权限校验、参数校验、错误处理。
- 对接数据库并向前端提供统一 API。

## 2. 技术选型
- 语言：Python 3.11+
- 框架：FastAPI（可替换为课程要求框架）
- 数据库：MySQL / SQLite（开发阶段）
- ORM：SQLAlchemy

## 3. 目录结构

```text
backend/
├── app/
│   ├── api/          # 路由层
│   ├── services/     # 业务逻辑层
│   ├── models/       # 数据模型
│   ├── schemas/      # 请求/响应模型
│   └── core/         # 配置与中间件
├── tests/
└── README.md
```

## 4. 运行方式
1. 进入目录：`cd backend`
2. 创建虚拟环境：`python -m venv .venv`
3. 安装依赖：`pip install -r requirements.txt`
4. 启动服务：`uvicorn app.main:app --reload --port 8000`

## 5. 接口规范
- RESTful 风格命名。
- 统一错误码与日志格式。
- 关键接口要求基本单元测试。
