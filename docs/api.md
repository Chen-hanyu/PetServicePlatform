# API 设计说明

## 1. 通用约定
- Base URL：`/api/v1`
- 认证方式：`Bearer Token`
- 响应结构：

```json
{
  "code": 0,
  "message": "ok",
  "data": {}
}
```

## 2. 示例接口

### 2.1 用户登录
- 方法：`POST`
- 路径：`/api/v1/auth/login`
- 请求体：

```json
{
  "username": "string",
  "password": "string"
}
```

- 返回：token、用户基础信息

### 2.2 获取用户信息
- 方法：`GET`
- 路径：`/api/v1/users/me`
- 请求头：`Authorization: Bearer <token>`

### 2.3 任务列表
- 方法：`GET`
- 路径：`/api/v1/tasks`
- 查询参数：`page`、`page_size`、`status`

## 3. 状态码约定
- `200`：请求成功
- `400`：参数错误
- `401`：未认证
- `403`：无权限
- `404`：资源不存在
- `500`：服务器异常

## 4. 版本策略
- 小版本兼容新增字段。
- 大版本通过 `/api/v2` 升级，不破坏现有客户端。
