# API 设计说明（宠物综合服务平台）

## 1. 通用约定
- Base URL：`/api/v1`
- 管理端前缀：`/api/v1/admin`
- 认证方式：JWT Bearer Token
- 响应结构：

```json
{
  "code": 0,
  "message": "ok",
  "data": {}
}
```

- 分页结构：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "list": [],
    "total": 0,
    "page": 1,
    "page_size": 10
  }
}
```

## 2. 用户端 API

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

### 2.2 当前用户信息
- 方法：`GET`
- 路径：`/api/v1/profile/me`

### 2.3 首页聚合数据
- 方法：`GET`
- 路径：`/api/v1/home`
- 返回：Banner、快捷入口、推荐帖子、推荐服务、推荐商品、宠物小贴士、萌宠卡片

### 2.4 全站搜索
- 方法：`GET`
- 路径：`/api/v1/search`
- 查询参数：`keyword`、`module`

## 3. 社区 API

### 3.1 帖子列表
- 方法：`GET`
- 路径：`/api/v1/community/posts`
- 查询参数：`tab`、`category`、`tag`、`page`、`page_size`

### 3.2 帖子详情
- 方法：`GET`
- 路径：`/api/v1/community/posts/{post_id}`

### 3.3 发布帖子
- 方法：`POST`
- 路径：`/api/v1/community/posts`

### 3.4 评论列表
- 方法：`GET`
- 路径：`/api/v1/community/posts/{post_id}/comments`

### 3.5 发表评论
- 方法：`POST`
- 路径：`/api/v1/community/posts/{post_id}/comments`

### 3.6 点赞帖子
- 方法：`POST`
- 路径：`/api/v1/community/posts/{post_id}/like`

### 3.7 收藏帖子
- 方法：`POST`
- 路径：`/api/v1/community/posts/{post_id}/favorite`

## 4. 领养 API

### 4.1 待领养宠物列表
- 方法：`GET`
- 路径：`/api/v1/adoption/pets`
- 查询参数：`type`、`city`、`gender`、`page`、`page_size`

### 4.2 待领养宠物详情
- 方法：`GET`
- 路径：`/api/v1/adoption/pets/{pet_id}`

### 4.3 领养流程说明
- 方法：`GET`
- 路径：`/api/v1/adoption/process`

### 4.4 提交领养申请
- 方法：`POST`
- 路径：`/api/v1/adoption/applications`

### 4.5 我的领养申请
- 方法：`GET`
- 路径：`/api/v1/adoption/applications`

## 5. 宠物服务 API

### 5.1 服务分类
- 方法：`GET`
- 路径：`/api/v1/services/categories`

### 5.2 商家列表
- 方法：`GET`
- 路径：`/api/v1/services/merchants`
- 查询参数：`category`、`district`、`sort`、`page`、`page_size`

### 5.3 商家详情
- 方法：`GET`
- 路径：`/api/v1/services/merchants/{merchant_id}`

### 5.4 创建预约
- 方法：`POST`
- 路径：`/api/v1/services/bookings`

### 5.5 我的预约记录
- 方法：`GET`
- 路径：`/api/v1/services/bookings`

### 5.6 取消预约
- 方法：`POST`
- 路径：`/api/v1/services/bookings/{booking_id}/cancel`

## 6. 商城 API

### 6.1 商品分类
- 方法：`GET`
- 路径：`/api/v1/shop/categories`

### 6.2 商品列表
- 方法：`GET`
- 路径：`/api/v1/shop/products`
- 查询参数：`category`、`keyword`、`sort`、`pet_type`、`page`、`page_size`

### 6.3 商品详情
- 方法：`GET`
- 路径：`/api/v1/shop/products/{product_id}`

### 6.4 购物车列表
- 方法：`GET`
- 路径：`/api/v1/shop/cart`

### 6.5 加入购物车
- 方法：`POST`
- 路径：`/api/v1/shop/cart/items`

### 6.6 更新购物车数量
- 方法：`PUT`
- 路径：`/api/v1/shop/cart/items/{item_id}`

### 6.7 创建订单
- 方法：`POST`
- 路径：`/api/v1/shop/orders`

### 6.8 订单列表
- 方法：`GET`
- 路径：`/api/v1/shop/orders`

### 6.9 订单详情
- 方法：`GET`
- 路径：`/api/v1/shop/orders/{order_id}`

## 7. 宠物档案与个人中心 API

### 7.1 我的宠物列表
- 方法：`GET`
- 路径：`/api/v1/pets`

### 7.2 新增宠物
- 方法：`POST`
- 路径：`/api/v1/pets`

### 7.3 宠物详情
- 方法：`GET`
- 路径：`/api/v1/pets/{pet_id}`

### 7.4 更新宠物档案
- 方法：`PUT`
- 路径：`/api/v1/pets/{pet_id}`

### 7.5 疫苗记录列表
- 方法：`GET`
- 路径：`/api/v1/pets/{pet_id}/vaccines`

### 7.6 新增疫苗记录
- 方法：`POST`
- 路径：`/api/v1/pets/{pet_id}/vaccines`

### 7.7 体重记录列表
- 方法：`GET`
- 路径：`/api/v1/pets/{pet_id}/weights`

### 7.8 新增体重记录
- 方法：`POST`
- 路径：`/api/v1/pets/{pet_id}/weights`

### 7.9 宠物成长时间轴
- 方法：`GET`
- 路径：`/api/v1/pets/{pet_id}/timeline`

### 7.10 个人中心聚合
- 方法：`GET`
- 路径：`/api/v1/profile/overview`

## 8. 管理员端 API

### 8.1 管理员登录
- 方法：`POST`
- 路径：`/api/v1/admin/auth/login`

### 8.2 仪表盘统计
- 方法：`GET`
- 路径：`/api/v1/admin/dashboard`

### 8.3 用户管理
- 用户列表：`GET /api/v1/admin/users`
- 用户详情：`GET /api/v1/admin/users/{user_id}`
- 禁用/启用用户：`PUT /api/v1/admin/users/{user_id}/status`

### 8.4 内容管理
- 帖子审核列表：`GET /api/v1/admin/posts`
- 审核帖子：`PUT /api/v1/admin/posts/{post_id}/review`
- 评论管理：`GET /api/v1/admin/comments`
- 删除评论：`DELETE /api/v1/admin/comments/{comment_id}`
- Banner 管理：`GET /api/v1/admin/banners`
- 新增 Banner：`POST /api/v1/admin/banners`
- 更新 Banner：`PUT /api/v1/admin/banners/{banner_id}`

### 8.5 领养管理
- 待领养宠物列表：`GET /api/v1/admin/adoption/pets`
- 新增待领养宠物：`POST /api/v1/admin/adoption/pets`
- 更新待领养宠物：`PUT /api/v1/admin/adoption/pets/{pet_id}`
- 领养申请列表：`GET /api/v1/admin/adoption/applications`
- 审核领养申请：`PUT /api/v1/admin/adoption/applications/{application_id}/review`

### 8.6 服务管理
- 服务分类列表：`GET /api/v1/admin/services/categories`
- 商家列表：`GET /api/v1/admin/services/merchants`
- 新增商家：`POST /api/v1/admin/services/merchants`
- 更新商家：`PUT /api/v1/admin/services/merchants/{merchant_id}`
- 预约单列表：`GET /api/v1/admin/services/bookings`
- 处理预约单：`PUT /api/v1/admin/services/bookings/{booking_id}`

### 8.7 商城管理
- 商品分类列表：`GET /api/v1/admin/shop/categories`
- 商品列表：`GET /api/v1/admin/shop/products`
- 新增商品：`POST /api/v1/admin/shop/products`
- 更新商品：`PUT /api/v1/admin/shop/products/{product_id}`
- 商品上下架：`PUT /api/v1/admin/shop/products/{product_id}/status`
- 订单列表：`GET /api/v1/admin/shop/orders`
- 处理订单：`PUT /api/v1/admin/shop/orders/{order_id}`

## 9. 状态码约定
- `200`：请求成功
- `400`：参数错误
- `401`：未登录或登录失效
- `403`：无权限
- `404`：资源不存在
- `409`：业务冲突
- `422`：请求数据校验失败
- `500`：服务器异常

## 10. 版本策略
- 非破坏性改动在当前版本追加字段，不删除旧字段。
- 破坏性改动通过 `/api/v2` 升级。
- 用户端与管理员端共用业务模型，但接口职责分离，避免前台接口暴露后台能力。
