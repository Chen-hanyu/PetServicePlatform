# API 设计文档

## 1. 文档说明

本文档描述宠物综合服务平台当前后端已实现的 RESTful API。

- 阅读版说明文档：`docs/api.md`
- OpenAPI 规范文档：`docs/api.yaml`

相关文档：

- [architecture.md](./architecture.md)
- [backend.md](./backend.md)
- [database.md](./database.md)

## 2. 通用约定

### 2.1 Base URL

- 用户端：`/api/v1`
- 管理端：`/api/v1/admin`

### 2.2 认证方式

- 认证机制：`JWT Bearer Token`
- 请求头格式：

```http
Authorization: Bearer <token>
```

### 2.3 统一响应结构

```json
{
  "code": 0,
  "message": "ok",
  "data": {}
}
```

分页响应：

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

### 2.4 常用状态码

- `200`：请求成功
- `201`：创建成功
- `400`：请求参数错误
- `401`：未登录或登录失效
- `403`：无权限
- `404`：资源不存在
- `422`：请求数据校验失败
- `500`：服务端异常

### 2.5 分页参数

列表接口统一支持：

| 参数 | 类型 | 默认值 | 说明 |
|---|---|---|---|
| `page` | int | `1` | 页码 |
| `page_size` | int | `10` | 每页数量 |

### 2.6 权限边界

- 用户端公开接口：首页、搜索、公开列表与详情
- 用户端写操作：默认需要登录
- 管理端接口：全部需要 `ADMIN` 权限

## 3. 用户端 API

### 3.1 首页与搜索

| 方法 | 路径 | 认证 | 说明 |
|---|---|---|---|
| `GET` | `/api/v1/home` | 否 | 获取首页聚合数据 |
| `GET` | `/api/v1/search` | 否 | 全站搜索 |

`/api/v1/search` 查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `keyword` | string | 是 | 搜索关键词 |
| `module` | string | 否 | `community` / `adoption` / `services` / `shop` |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

### 3.2 用户认证

| 方法 | 路径 | 认证 | 说明 |
|---|---|---|---|
| `POST` | `/api/v1/auth/register` | 否 | 用户注册 |
| `POST` | `/api/v1/auth/verify-code` | 否 | 发送验证码 |
| `POST` | `/api/v1/auth/login` | 否 | 用户密码登录 |
| `POST` | `/api/v1/auth/logout` | 是 | 用户登出 |

注册请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `phone` | string | 是 | 手机号 |
| `password` | string | 是 | 登录密码，6 到 20 位 |
| `nickname` | string | 否 | 用户昵称 |

登录请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `phone` | string | 是 | 手机号 |
| `password` | string | 是 | 登录密码 |

验证码请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `phone` | string | 是 | 手机号 |

说明：当前系统主登录方式为手机号加密码，验证码接口保留用于调试与联调。

### 3.3 个人中心

| 方法 | 路径 | 认证 | 说明 |
|---|---|---|---|
| `GET` | `/api/v1/profile/me` | 是 | 获取当前用户信息 |
| `GET` | `/api/v1/profile/overview` | 是 | 获取个人中心概览 |

### 3.4 社区

| 方法 | 路径 | 认证 | 说明 |
|---|---|---|---|
| `GET` | `/api/v1/community/posts` | 否 | 获取帖子列表 |
| `GET` | `/api/v1/community/posts/{postId}` | 否 | 获取帖子详情 |
| `POST` | `/api/v1/community/posts` | 是 | 发布帖子 |
| `GET` | `/api/v1/community/posts/{postId}/comments` | 否 | 获取评论列表 |
| `POST` | `/api/v1/community/posts/{postId}/comments` | 是 | 发表评论 |
| `POST` | `/api/v1/community/posts/{postId}/like` | 是 | 点赞或取消点赞 |
| `POST` | `/api/v1/community/posts/{postId}/favorite` | 是 | 收藏或取消收藏 |

帖子列表查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `tab` | string | 否 | `recommended` / `latest` |
| `category` | string | 否 | 帖子分类 |
| `tag` | string | 否 | 标签名 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

发帖请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `title` | string | 是 | 标题 |
| `content` | string | 是 | 正文 |
| `category` | string | 是 | 分类 |
| `images` | array<string> | 否 | 图片地址列表 |
| `tag_ids` | array<long> | 否 | 标签 ID 列表 |

评论请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `content` | string | 是 | 评论内容 |

### 3.5 领养

| 方法 | 路径 | 认证 | 说明 |
|---|---|---|---|
| `GET` | `/api/v1/adoption/pets` | 否 | 获取待领养宠物列表 |
| `GET` | `/api/v1/adoption/pets/{petId}` | 否 | 获取待领养宠物详情 |
| `GET` | `/api/v1/adoption/process` | 否 | 获取领养流程说明 |
| `POST` | `/api/v1/adoption/applications` | 是 | 提交领养申请 |
| `GET` | `/api/v1/adoption/applications` | 是 | 获取我的领养申请列表 |

待领养宠物列表查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `type` | string | 否 | 宠物类型 |
| `city` | string | 否 | 城市 |
| `gender` | string | 否 | 性别 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

领养申请请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `pet_id` | long | 是 | 宠物 ID |
| `contact_phone` | string | 是 | 联系电话 |
| `experience_desc` | string | 是 | 养宠经验 |
| `living_condition_desc` | string | 是 | 居住条件 |

我的领养申请查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `status` | string | 否 | 申请状态 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

### 3.6 服务

| 方法 | 路径 | 认证 | 说明 |
|---|---|---|---|
| `GET` | `/api/v1/services/categories` | 否 | 获取服务分类 |
| `GET` | `/api/v1/services/merchants` | 否 | 获取商家列表 |
| `GET` | `/api/v1/services/merchants/{merchantId}` | 否 | 获取商家详情 |
| `POST` | `/api/v1/services/merchants/{merchantId}/reviews` | 是 | 发表商家评价 |
| `POST` | `/api/v1/services/bookings` | 是 | 创建服务预约 |
| `GET` | `/api/v1/services/bookings` | 是 | 获取我的预约列表 |
| `POST` | `/api/v1/services/bookings/{bookingId}/cancel` | 是 | 取消预约 |

商家列表查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `category` | string | 否 | 分类 |
| `district` | string | 否 | 区域 |
| `sort` | string | 否 | 排序方式 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

评价请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `score` | int | 是 | 评分，1 到 5 |
| `content` | string | 是 | 评价内容 |

预约请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `merchant_id` | long | 是 | 商家 ID |
| `merchant_service_id` | long | 是 | 服务项目 ID |
| `booking_time` | datetime | 是 | 预约时间 |
| `contact_name` | string | 是 | 联系人 |
| `contact_phone` | string | 是 | 联系电话 |
| `remark` | string | 否 | 备注 |

我的预约查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `status` | string | 否 | 预约状态 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

### 3.7 商城

| 方法 | 路径 | 认证 | 说明 |
|---|---|---|---|
| `GET` | `/api/v1/shop/categories` | 否 | 获取商品分类 |
| `GET` | `/api/v1/shop/products` | 否 | 获取商品列表 |
| `GET` | `/api/v1/shop/products/{productId}` | 否 | 获取商品详情 |
| `GET` | `/api/v1/shop/cart` | 是 | 获取购物车 |
| `POST` | `/api/v1/shop/cart/items` | 是 | 添加购物车商品 |
| `PUT` | `/api/v1/shop/cart/items/{itemId}` | 是 | 更新购物车商品 |
| `POST` | `/api/v1/shop/orders` | 是 | 创建订单 |
| `GET` | `/api/v1/shop/orders` | 是 | 获取我的订单列表 |
| `GET` | `/api/v1/shop/orders/{orderId}` | 是 | 获取订单详情 |

商品列表查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `category` | long | 否 | 商品分类 ID |
| `keyword` | string | 否 | 搜索关键词 |
| `sort` | string | 否 | 排序方式 |
| `pet_type` | string | 否 | 适用品类 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

添加购物车请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `product_id` | long | 是 | 商品 ID |
| `quantity` | int | 是 | 数量 |

更新购物车请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `quantity` | int | 是 | 数量 |
| `checked` | bool | 否 | 是否选中 |

创建订单请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `item_ids` | array<long> | 是 | 购物车条目 ID 列表 |
| `receiver_name` | string | 是 | 收货人 |
| `receiver_phone` | string | 是 | 收货电话 |
| `receiver_address` | string | 是 | 收货地址 |
| `remark` | string | 否 | 备注 |

订单列表查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `status` | string | 否 | 订单状态 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

### 3.8 消息

| 方法 | 路径 | 认证 | 说明 |
|---|---|---|---|
| `GET` | `/api/v1/messages` | 是 | 获取消息列表 |
| `POST` | `/api/v1/messages/{messageId}/read` | 是 | 标记消息已读 |

### 3.9 宠物档案

| 方法 | 路径 | 认证 | 说明 |
|---|---|---|---|
| `GET` | `/api/v1/pets` | 是 | 获取我的宠物列表 |
| `POST` | `/api/v1/pets` | 是 | 创建宠物档案 |
| `GET` | `/api/v1/pets/{petId}` | 是 | 获取宠物档案详情 |
| `PUT` | `/api/v1/pets/{petId}` | 是 | 更新宠物档案 |
| `DELETE` | `/api/v1/pets/{petId}` | 是 | 删除宠物档案 |
| `GET` | `/api/v1/pets/{petId}/vaccines` | 是 | 获取疫苗记录列表 |
| `POST` | `/api/v1/pets/{petId}/vaccines` | 是 | 新增疫苗记录 |
| `GET` | `/api/v1/pets/{petId}/weights` | 是 | 获取体重记录列表 |
| `POST` | `/api/v1/pets/{petId}/weights` | 是 | 新增体重记录 |
| `GET` | `/api/v1/pets/{petId}/timeline` | 是 | 获取成长时间轴 |
| `POST` | `/api/v1/pets/{petId}/albums` | 是 | 新增相册记录 |

创建或更新宠物档案请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `name` | string | 是 | 宠物名称 |
| `type` | string | 是 | 类型 |
| `breed` | string | 否 | 品种 |
| `gender` | string | 否 | 性别 |
| `birthday` | date | 否 | 生日 |
| `weight` | number | 否 | 当前体重 |
| `avatar_url` | string | 否 | 头像地址 |
| `description` | string | 否 | 描述 |

新增疫苗记录请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `vaccine_name` | string | 是 | 疫苗名称 |
| `vaccinated_at` | date | 是 | 接种日期 |
| `next_due_at` | date | 否 | 下次接种日期 |
| `remark` | string | 否 | 备注 |

新增体重记录请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `weight` | number | 是 | 体重 |
| `recorded_at` | datetime | 否 | 记录时间 |

新增相册记录请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `image_url` | string | 是 | 图片地址 |
| `caption` | string | 否 | 图片说明 |

### 3.10 文件上传

| 方法 | 路径 | 认证 | 说明 |
|---|---|---|---|
| `POST` | `/api/v1/files/upload` | 是 | 上传图片文件 |

表单字段：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `file` | file | 是 | 图片文件 |

## 4. 管理端 API

### 4.1 管理员认证与仪表盘

| 方法 | 路径 | 认证 | 说明 |
|---|---|---|---|
| `POST` | `/api/v1/admin/auth/verify-code` | 否 | 发送管理员验证码 |
| `POST` | `/api/v1/admin/auth/login` | 否 | 管理员密码登录 |
| `POST` | `/api/v1/admin/auth/logout` | 是 | 管理员登出 |
| `GET` | `/api/v1/admin/dashboard` | 是 | 获取仪表盘概览 |

管理员登录请求体与用户登录一致。

### 4.2 用户管理

| 方法 | 路径 | 认证 | 说明 |
|---|---|---|---|
| `GET` | `/api/v1/admin/users` | 是 | 获取用户列表 |
| `GET` | `/api/v1/admin/users/{userId}` | 是 | 获取用户详情 |
| `PUT` | `/api/v1/admin/users/{userId}/status` | 是 | 更新用户状态 |

用户列表查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `keyword` | string | 否 | 手机号或昵称 |
| `status` | string | 否 | 用户状态 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

更新用户状态请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `status` | string | 是 | 用户状态 |
| `remark` | string | 否 | 备注 |

### 4.3 社区内容管理

| 方法 | 路径 | 认证 | 说明 |
|---|---|---|---|
| `GET` | `/api/v1/admin/posts` | 是 | 获取帖子审核列表 |
| `PUT` | `/api/v1/admin/posts/{postId}/review` | 是 | 审核帖子 |
| `GET` | `/api/v1/admin/comments` | 是 | 获取评论列表 |
| `DELETE` | `/api/v1/admin/comments/{commentId}` | 是 | 删除评论 |

帖子审核列表查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `status` | string | 否 | 帖子状态 |
| `category` | string | 否 | 帖子分类 |
| `keyword` | string | 否 | 关键词 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

帖子审核请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `status` | string | 是 | 审核状态 |
| `remark` | string | 否 | 备注 |

评论列表查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `keyword` | string | 否 | 关键词 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

### 4.4 领养管理

| 方法 | 路径 | 认证 | 说明 |
|---|---|---|---|
| `GET` | `/api/v1/admin/adoption/applications` | 是 | 获取领养申请列表 |
| `PUT` | `/api/v1/admin/adoption/applications/{applicationId}/review` | 是 | 审核领养申请 |
| `GET` | `/api/v1/admin/adoption/pets` | 是 | 获取待领养宠物列表 |
| `POST` | `/api/v1/admin/adoption/pets` | 是 | 新增待领养宠物 |
| `PUT` | `/api/v1/admin/adoption/pets/{petId}` | 是 | 更新待领养宠物 |

领养申请列表查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `status` | string | 否 | 申请状态 |
| `pet_id` | long | 否 | 宠物 ID |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

领养申请审核请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `status` | string | 是 | 审核状态 |
| `review_remark` | string | 否 | 审核备注 |

待领养宠物列表查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `status` | string | 否 | 宠物状态 |
| `type` | string | 否 | 宠物类型 |
| `city` | string | 否 | 城市 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

待领养宠物保存请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `name` | string | 是 | 宠物名称 |
| `type` | string | 是 | 宠物类型 |
| `breed` | string | 否 | 品种 |
| `gender` | string | 否 | 性别 |
| `age_desc` | string | 否 | 年龄描述 |
| `city` | string | 否 | 城市 |
| `health_status` | string | 否 | 健康状态 |
| `personality` | string | 否 | 性格 |
| `adoption_requirements` | string | 否 | 领养要求 |
| `story` | string | 否 | 宠物故事 |
| `cover_url` | string | 否 | 封面图 |
| `status` | string | 是 | 宠物状态 |

### 4.5 服务管理

| 方法 | 路径 | 认证 | 说明 |
|---|---|---|---|
| `GET` | `/api/v1/admin/services/bookings` | 是 | 获取预约单列表 |
| `PUT` | `/api/v1/admin/services/bookings/{bookingId}` | 是 | 更新预约单状态 |
| `GET` | `/api/v1/admin/services/reviews` | 是 | 获取商家评价列表 |
| `DELETE` | `/api/v1/admin/services/reviews/{reviewId}` | 是 | 删除商家评价 |
| `GET` | `/api/v1/admin/services/categories` | 是 | 获取服务分类 |
| `POST` | `/api/v1/admin/services/categories` | 是 | 创建服务分类 |
| `PUT` | `/api/v1/admin/services/categories/{categoryId}` | 是 | 更新服务分类 |
| `GET` | `/api/v1/admin/services/merchants` | 是 | 获取商家列表 |
| `POST` | `/api/v1/admin/services/merchants` | 是 | 创建商家 |
| `PUT` | `/api/v1/admin/services/merchants/{merchantId}` | 是 | 更新商家 |
| `GET` | `/api/v1/admin/services/items` | 是 | 获取服务项目列表 |
| `POST` | `/api/v1/admin/services/items` | 是 | 创建服务项目 |
| `PUT` | `/api/v1/admin/services/items/{serviceId}` | 是 | 更新服务项目 |

预约单列表查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `status` | string | 否 | 预约状态 |
| `merchant_id` | long | 否 | 商家 ID |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

更新预约单请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `status` | string | 是 | 预约状态 |
| `remark` | string | 否 | 备注 |

商家评价列表查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `merchant_id` | long | 否 | 商家 ID |
| `keyword` | string | 否 | 关键词 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

服务分类保存请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `name` | string | 是 | 分类名称 |
| `sort` | int | 否 | 排序值 |
| `status` | string | 是 | 分类状态 |

商家保存请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `name` | string | 是 | 商家名称 |
| `district` | string | 是 | 区域 |
| `address` | string | 是 | 地址 |
| `phone` | string | 是 | 联系电话 |
| `business_hours` | string | 是 | 营业时间 |
| `status` | string | 是 | 状态 |

服务项目保存请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `merchant_id` | long | 是 | 商家 ID |
| `category_id` | long | 是 | 分类 ID |
| `name` | string | 是 | 项目名称 |
| `price` | number | 是 | 价格 |
| `duration_minutes` | int | 是 | 时长，分钟 |
| `status` | string | 是 | 状态 |

服务项目列表查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `merchant_id` | long | 否 | 商家 ID |
| `category_id` | long | 否 | 分类 ID |
| `status` | string | 否 | 状态 |
| `keyword` | string | 否 | 关键词 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

### 4.6 商城管理

| 方法 | 路径 | 认证 | 说明 |
|---|---|---|---|
| `GET` | `/api/v1/admin/shop/orders` | 是 | 获取订单列表 |
| `PUT` | `/api/v1/admin/shop/orders/{orderId}` | 是 | 更新订单状态 |
| `GET` | `/api/v1/admin/shop/categories` | 是 | 获取商品分类 |
| `GET` | `/api/v1/admin/shop/products` | 是 | 获取商品列表 |
| `POST` | `/api/v1/admin/shop/products` | 是 | 创建商品 |
| `PUT` | `/api/v1/admin/shop/products/{productId}` | 是 | 更新商品 |
| `PUT` | `/api/v1/admin/shop/products/{productId}/status` | 是 | 更新商品上架状态 |

订单列表查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `status` | string | 否 | 订单状态 |
| `keyword` | string | 否 | 关键词 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

更新订单请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `status` | string | 是 | 订单状态 |
| `remark` | string | 否 | 备注 |

商品保存请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `category_id` | long | 是 | 分类 ID |
| `name` | string | 是 | 商品名称 |
| `subtitle` | string | 否 | 副标题 |
| `image_url` | string | 是 | 图片地址 |
| `price` | number | 是 | 售价 |
| `stock` | int | 是 | 库存 |
| `pet_type` | string | 否 | 适用品类 |
| `description` | string | 否 | 描述 |
| `status` | string | 是 | 状态 |

商品状态更新请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `status` | string | 是 | 商品状态 |

商品列表查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `status` | string | 否 | 商品状态 |
| `keyword` | string | 否 | 关键词 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

### 4.7 Banner、标签与推荐位

| 方法 | 路径 | 认证 | 说明 |
|---|---|---|---|
| `GET` | `/api/v1/admin/banners` | 是 | 获取 Banner 列表 |
| `POST` | `/api/v1/admin/banners` | 是 | 创建 Banner |
| `PUT` | `/api/v1/admin/banners/{bannerId}` | 是 | 更新 Banner |
| `DELETE` | `/api/v1/admin/banners/{bannerId}` | 是 | 删除 Banner |
| `GET` | `/api/v1/admin/tags` | 是 | 获取标签列表 |
| `POST` | `/api/v1/admin/tags` | 是 | 创建标签 |
| `PUT` | `/api/v1/admin/tags/{tagId}` | 是 | 更新标签 |
| `GET` | `/api/v1/admin/recommendations` | 是 | 获取推荐位列表 |
| `POST` | `/api/v1/admin/recommendations` | 是 | 创建推荐位 |
| `PUT` | `/api/v1/admin/recommendations/{recommendationId}` | 是 | 更新推荐位 |

Banner 请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `title` | string | 是 | 标题 |
| `image_url` | string | 是 | 图片地址 |
| `link_url` | string | 否 | 跳转链接 |
| `status` | string | 是 | 状态 |
| `sort` | int | 否 | 排序值 |

标签请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `name` | string | 是 | 标签名称 |
| `type` | string | 是 | 标签类型 |
| `status` | string | 是 | 标签状态 |
| `sort` | int | 否 | 排序值 |

推荐位请求体：

| 字段 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `biz_type` | string | 是 | 业务类型 |
| `biz_id` | long | 是 | 业务对象 ID |
| `slot_code` | string | 是 | 推荐位编码 |
| `status` | string | 是 | 状态 |
| `sort` | int | 否 | 排序值 |

标签列表查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `type` | string | 否 | 标签类型 |
| `status` | string | 否 | 状态 |
| `keyword` | string | 否 | 关键词 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

推荐位列表查询参数：

| 参数 | 类型 | 必填 | 说明 |
|---|---|---|---|
| `slot_code` | string | 否 | 推荐位编码 |
| `biz_type` | string | 否 | 业务类型 |
| `status` | string | 否 | 状态 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

## 5. Swagger 访问入口

- Swagger UI：`http://127.0.0.1:8080/swagger-ui.html`
- OpenAPI JSON：`http://127.0.0.1:8080/v3/api-docs`
- OpenAPI YAML：`docs/api.yaml`
