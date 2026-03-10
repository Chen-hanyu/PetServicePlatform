# 后端模块说明（联调用草案）

> 说明：该文档当前由前端负责人整理，用于联调。最终内容由后端负责人确认与维护。

## 1. 模块功能
- 提供用户认证、宠物档案、预约服务、商城订单、社区内容等接口。
- 负责数据校验、权限控制、业务规则实现。
- 统一日志与异常处理，保障接口可追踪。

## 2. 技术选型（建议）
- 语言：Python 3.11+
- 框架：FastAPI
- 数据库：MySQL
- ORM：SQLAlchemy
- 缓存：Redis
- 文档：OpenAPI / Swagger

## 3. 目录结构（建议）

```text
backend/
├── app/
│   ├── api/                 # 路由层
│   ├── services/            # 业务逻辑层
│   ├── models/              # 数据模型
│   ├── schemas/             # 请求/响应模型
│   ├── repositories/        # 数据访问层
│   └── core/                # 配置、中间件、鉴权
├── scripts/                 # 运维与初始化脚本
├── tests/                   # 测试用例
└── README.md
```

## 4. 运行方式（建议）
1. `cd backend`
2. `python -m venv .venv`
3. `pip install -r requirements.txt`
4. `uvicorn app.main:app --reload --port 8000`

## 5. 与前端协作约定
- API 版本统一：`/api/v1`
- 字段命名：统一 `snake_case` 或统一 `camelCase`（二选一后全局一致）
- 错误码规范：业务错误码 + HTTP 状态码同时返回
- 每次接口变更需同步更新 `docs/api.md`
