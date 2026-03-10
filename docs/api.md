# API 设计说明（宠友圈小程序）

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

## 2. 核心接口

### 2.1 登录
- 方法：`POST`
- 路径：`/api/v1/auth/login`
- 请求体：

```json
{
  "phone": "13800000000",
  "verify_code": "123456"
}
```

### 2.2 宠物档案
- 新增宠物：`POST /api/v1/pets`
- 宠物列表：`GET /api/v1/pets`
- 宠物详情：`GET /api/v1/pets/{pet_id}`
- 更新档案：`PUT /api/v1/pets/{pet_id}`

### 2.3 健康与免疫记录
- 新增记录：`POST /api/v1/pets/{pet_id}/health-records`
- 记录列表：`GET /api/v1/pets/{pet_id}/health-records`

### 2.4 服务预约
- 提交预约：`POST /api/v1/bookings`
- 预约列表：`GET /api/v1/bookings`
- 取消预约：`POST /api/v1/bookings/{booking_id}/cancel`

### 2.5 商城
- 商品列表：`GET /api/v1/products`
- 商品详情：`GET /api/v1/products/{product_id}`
- 创建订单：`POST /api/v1/orders`
- 订单列表：`GET /api/v1/orders`

### 2.6 社区
- 发布帖子：`POST /api/v1/posts`
- 帖子列表：`GET /api/v1/posts`
- 评论帖子：`POST /api/v1/posts/{post_id}/comments`
- 点赞帖子：`POST /api/v1/posts/{post_id}/like`

## 3. 状态码约定
- `200`：请求成功
- `400`：参数错误
- `401`：未认证
- `403`：无权限
- `404`：资源不存在
- `409`：业务冲突（如重复预约）
- `500`：服务器异常

## 4. 版本策略
- 非破坏性更新：在当前版本新增字段。
- 破坏性更新：升级至 `/api/v2`。
