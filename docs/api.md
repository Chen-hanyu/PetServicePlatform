# API 设计文档（宠物综合服务平台）

## 1. 文档说明

本文档基于当前项目的需求说明、前后端模块说明、信息架构、架构设计和数据库设计整理而成，用于定义宠物综合服务平台的接口规范。  
当前版本为 **课程项目 / MVP 阶段接口设计文档**，重点保证：

- 用户端与管理员端接口边界清晰
- 核心业务主流程完整
- 请求与响应结构统一
- 便于后续生成 Swagger/OpenAPI 文档

相关文档：

- [architecture.md](D:\Code\PetServicePlatform\docs\architecture.md)
- [database.md](D:\Code\PetServicePlatform\docs\database.md)

---

## 2. 通用约定

### 2.1 Base URL

- 用户端基础路径：`/api/v1`
- 管理端基础路径：`/api/v1/admin`

### 2.2 认证方式

- 认证机制：`JWT Bearer Token`
- 请求头格式：

```http
Authorization: Bearer <token>
```

### 2.3 内容格式

- 请求格式：`application/json`
- 响应格式：`application/json`
- 文件上传场景：`multipart/form-data`

### 2.4 统一响应结构

#### 成功响应

```json
{
  "code": 0,
  "message": "ok",
  "data": {}
}
```

#### 分页响应

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

#### 失败响应

```json
{
  "code": 10001,
  "message": "参数错误",
  "data": null
}
```

### 2.5 通用 HTTP 状态码

- `200`：请求成功
- `400`：参数错误
- `401`：未登录或登录失效
- `403`：无权限
- `404`：资源不存在
- `409`：业务冲突
- `422`：请求数据校验失败
- `500`：服务器异常

### 2.6 通用业务码建议

| `code` | 含义 |
|---|---|
| `0` | 成功 |
| `10001` | 参数错误 |
| `10002` | 数据校验失败 |
| `10003` | 资源不存在 |
| `10004` | 登录失效 |
| `10005` | 无权限 |
| `10006` | 状态不允许当前操作 |
| `10007` | 数据重复 |
| `10008` | 库存不足 |
| `10009` | 审核已处理 |
| `10010` | 预约时间冲突 |

### 2.7 分页约定

列表接口统一支持以下参数：

| 参数 | 类型 | 是否必填 | 默认值 | 说明 |
|---|---|---|---|---|
| `page` | int | 否 | `1` | 页码 |
| `page_size` | int | 否 | `10` | 每页数量 |

分页响应统一返回：

| 字段 | 类型 | 说明 |
|---|---|---|
| `list` | array | 当前页数据 |
| `total` | int | 总条数 |
| `page` | int | 当前页码 |
| `page_size` | int | 每页数量 |

### 2.8 排序约定

列表接口若支持排序，统一采用：

| 参数 | 示例 | 说明 |
|---|---|---|
| `sort` | `created_at_desc` | 排序字段与方向 |

---

## 3. 鉴权与角色模型

### 3.1 角色定义

- `USER`：普通用户
- `ADMIN`：管理员

### 3.2 鉴权规则

- 用户端公开接口无需登录，如首页、搜索、帖子列表、商品列表、领养列表、服务列表
- 用户端写操作默认需要登录，如发帖、评论、收藏、点赞、加入购物车、提交订单、提交领养申请
- 管理端接口全部要求 `ADMIN` 角色

### 3.3 登录返回结构

| 字段 | 类型 | 说明 |
|---|---|---|
| `token` | string | JWT 令牌 |
| `token_type` | string | 固定为 `Bearer` |
| `expires_in` | int | 过期时间，单位秒 |
| `user` | object | 当前登录用户信息 |

---

## 4. 通用数据对象

为减少重复定义，以下为文档中复用的通用对象。

### 4.1 UserProfile

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | bigint | 用户 ID |
| `role` | string | `USER` / `ADMIN` |
| `phone` | string | 手机号 |
| `nickname` | string | 昵称 |
| `avatar_url` | string | 头像 |
| `gender` | string | 性别 |
| `bio` | string | 简介 |
| `status` | string | 用户状态 |

### 4.2 PostSummary

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | bigint | 帖子 ID |
| `title` | string | 标题 |
| `category` | string | 分类 |
| `cover_url` | string | 封面图 |
| `excerpt` | string | 摘要 |
| `status` | string | 帖子状态 |
| `like_count` | int | 点赞数 |
| `favorite_count` | int | 收藏数 |
| `comment_count` | int | 评论数 |
| `author` | object | 作者简要信息 |
| `tags` | array | 标签列表 |
| `published_at` | datetime | 发布时间 |

### 4.3 AdoptionPetSummary

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | bigint | 宠物 ID |
| `name` | string | 宠物名 |
| `type` | string | 猫/狗 |
| `breed` | string | 品种 |
| `gender` | string | 性别 |
| `age_desc` | string | 年龄描述 |
| `city` | string | 城市 |
| `health_status` | string | 健康情况 |
| `status` | string | 上下线/领养状态 |
| `cover_url` | string | 封面图 |

### 4.4 MerchantSummary

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | bigint | 商家 ID |
| `name` | string | 商家名称 |
| `district` | string | 区域 |
| `address` | string | 地址 |
| `score` | number | 评分 |
| `business_hours` | string | 营业时间 |
| `status` | string | 营业状态 |

### 4.5 ProductSummary

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | bigint | 商品 ID |
| `category_id` | bigint | 分类 ID |
| `name` | string | 商品名称 |
| `subtitle` | string | 副标题 |
| `image_url` | string | 主图 |
| `price` | number | 售价 |
| `stock` | int | 库存 |
| `pet_type` | string | 适用宠物类型 |
| `status` | string | 上架状态 |

### 4.6 PetProfile

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | bigint | 宠物 ID |
| `name` | string | 宠物名 |
| `type` | string | 类型 |
| `breed` | string | 品种 |
| `gender` | string | 性别 |
| `birthday` | date | 生日 |
| `weight` | number | 当前体重 |
| `avatar_url` | string | 头像 |
| `description` | string | 简介 |

### 4.7 OrderSummary

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | bigint | 订单 ID |
| `order_no` | string | 订单号 |
| `total_amount` | number | 订单总额 |
| `pay_amount` | number | 实付金额 |
| `status` | string | 订单状态 |
| `created_at` | datetime | 创建时间 |

---

## 5. 枚举与状态定义

### 5.1 用户状态

- `ACTIVE`
- `DISABLED`

### 5.2 帖子状态

- `PENDING`
- `APPROVED`
- `REJECTED`

### 5.3 领养申请状态

- `PENDING`
- `APPROVED`
- `REJECTED`

### 5.4 待领养宠物状态

- `ONLINE`
- `OFFLINE`
- `ADOPTED`

### 5.5 服务预约状态

- `PENDING`
- `CONFIRMED`
- `COMPLETED`
- `CANCELLED`

### 5.6 商品状态

- `ON_SALE`
- `OFF_SHELF`

### 5.7 订单状态

- `PENDING`
- `PAID`
- `SHIPPED`
- `COMPLETED`
- `CANCELLED`

---

## 6. 用户端 API

## 6.1 认证与个人资料

### 6.1.1 用户登录

- 方法：`POST`
- 路径：`/api/v1/auth/login`
- 认证：否

请求体：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `phone` | string | 是 | 手机号 |
| `verify_code` | string | 是 | 验证码 |

成功响应 `data`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `token` | string | JWT |
| `token_type` | string | `Bearer` |
| `expires_in` | int | 过期秒数 |
| `user` | UserProfile | 当前用户 |

示例请求：

```json
{
  "phone": "13800000000",
  "verify_code": "123456"
}
```

示例响应：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.xxx",
    "token_type": "Bearer",
    "expires_in": 7200,
    "user": {
      "id": 1001,
      "role": "USER",
      "phone": "13800000000",
      "nickname": "团子妈",
      "avatar_url": "https://example.com/avatar.jpg",
      "gender": "FEMALE",
      "bio": "两只猫的铲屎官",
      "status": "ACTIVE"
    }
  }
}
```

### 6.1.2 当前用户信息

- 方法：`GET`
- 路径：`/api/v1/profile/me`
- 认证：是

成功响应 `data`：`UserProfile`

### 6.1.3 个人中心聚合

- 方法：`GET`
- 路径：`/api/v1/profile/overview`
- 认证：是

成功响应 `data`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `user` | UserProfile | 用户信息 |
| `pet_count` | int | 宠物数量 |
| `post_count` | int | 我的帖子数 |
| `favorite_count` | int | 收藏数 |
| `order_count` | int | 订单数 |
| `booking_count` | int | 预约数 |
| `adoption_application_count` | int | 领养申请数 |
| `unread_message_count` | int | 未读消息数 |

## 6.2 首页与搜索

### 6.2.1 首页聚合数据

- 方法：`GET`
- 路径：`/api/v1/home`
- 认证：否

成功响应 `data`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `banners` | array | Banner 列表 |
| `quick_entries` | array | 快捷入口 |
| `recommended_posts` | array<PostSummary> | 推荐帖子 |
| `recommended_services` | array<MerchantSummary> | 推荐服务 |
| `recommended_products` | array<ProductSummary> | 推荐商品 |
| `tips` | array | 宠物小贴士 |
| `pet_cards` | array | 萌宠卡片 |

### 6.2.2 全站搜索

- 方法：`GET`
- 路径：`/api/v1/search`
- 认证：否

查询参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `keyword` | string | 是 | 搜索关键词 |
| `module` | string | 否 | 模块范围：`community` / `adoption` / `services` / `shop` |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

成功响应 `data`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `list` | array | 结果列表 |
| `total` | int | 总数 |
| `page` | int | 当前页 |
| `page_size` | int | 每页数 |

## 6.3 社区 API

### 6.3.1 帖子列表

- 方法：`GET`
- 路径：`/api/v1/community/posts`
- 认证：否

查询参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `tab` | string | 否 | `recommended` / `latest` |
| `category` | string | 否 | `daily` / `help` / `knowledge` / `guide` |
| `tag` | string | 否 | 标签名 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

成功响应 `data.list`：`PostSummary[]`

### 6.3.2 帖子详情

- 方法：`GET`
- 路径：`/api/v1/community/posts/{post_id}`
- 认证：否

路径参数：

| 参数 | 类型 | 说明 |
|---|---|---|
| `post_id` | bigint | 帖子 ID |

成功响应 `data`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | bigint | 帖子 ID |
| `title` | string | 标题 |
| `content` | string | 正文 |
| `images` | array | 图片列表 |
| `category` | string | 分类 |
| `tags` | array | 标签 |
| `author` | object | 作者信息 |
| `like_count` | int | 点赞数 |
| `favorite_count` | int | 收藏数 |
| `comment_count` | int | 评论数 |
| `is_liked` | bool | 当前用户是否已点赞 |
| `is_favorited` | bool | 当前用户是否已收藏 |
| `published_at` | datetime | 发布时间 |

### 6.3.3 发布帖子

- 方法：`POST`
- 路径：`/api/v1/community/posts`
- 认证：是

请求体：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `title` | string | 是 | 标题 |
| `content` | string | 是 | 正文 |
| `category` | string | 是 | 分类 |
| `images` | array<string> | 否 | 图片 URL 列表 |
| `tag_ids` | array<bigint> | 否 | 标签 ID 列表 |

成功响应 `data`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | bigint | 新建帖子 ID |
| `status` | string | 初始状态，通常为 `PENDING` |

### 6.3.4 评论列表

- 方法：`GET`
- 路径：`/api/v1/community/posts/{post_id}/comments`
- 认证：否

查询参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

成功响应 `data.list`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | bigint | 评论 ID |
| `content` | string | 评论内容 |
| `author` | object | 评论用户 |
| `created_at` | datetime | 创建时间 |

### 6.3.5 发表评论

- 方法：`POST`
- 路径：`/api/v1/community/posts/{post_id}/comments`
- 认证：是

请求体：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `content` | string | 是 | 评论内容 |

### 6.3.6 点赞帖子

- 方法：`POST`
- 路径：`/api/v1/community/posts/{post_id}/like`
- 认证：是

成功响应 `data`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `liked` | bool | 当前是否点赞成功 |
| `like_count` | int | 最新点赞数 |

### 6.3.7 收藏帖子

- 方法：`POST`
- 路径：`/api/v1/community/posts/{post_id}/favorite`
- 认证：是

成功响应 `data`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `favorited` | bool | 当前是否收藏成功 |
| `favorite_count` | int | 最新收藏数 |

## 6.4 领养 API

### 6.4.1 待领养宠物列表

- 方法：`GET`
- 路径：`/api/v1/adoption/pets`
- 认证：否

查询参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `type` | string | 否 | 猫/狗 |
| `city` | string | 否 | 城市 |
| `gender` | string | 否 | 性别 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

成功响应 `data.list`：`AdoptionPetSummary[]`

### 6.4.2 待领养宠物详情

- 方法：`GET`
- 路径：`/api/v1/adoption/pets/{pet_id}`
- 认证：否

成功响应 `data`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | bigint | 宠物 ID |
| `name` | string | 宠物名 |
| `type` | string | 类型 |
| `breed` | string | 品种 |
| `gender` | string | 性别 |
| `age_desc` | string | 年龄描述 |
| `city` | string | 所在城市 |
| `health_status` | string | 健康情况 |
| `personality` | string | 性格 |
| `adoption_requirements` | string | 领养要求 |
| `story` | string | 宠物故事 |
| `cover_url` | string | 封面图 |
| `status` | string | 上下线状态 |

### 6.4.3 领养流程说明

- 方法：`GET`
- 路径：`/api/v1/adoption/process`
- 认证：否

成功响应 `data`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `steps` | array | 流程步骤 |
| `notes` | array | 领养说明 |

### 6.4.4 提交领养申请

- 方法：`POST`
- 路径：`/api/v1/adoption/applications`
- 认证：是

请求体：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `pet_id` | bigint | 是 | 宠物 ID |
| `contact_phone` | string | 是 | 联系电话 |
| `experience_desc` | string | 是 | 养宠经验 |
| `living_condition_desc` | string | 是 | 居住情况 |

成功响应 `data`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | bigint | 申请 ID |
| `status` | string | `PENDING` |

示例请求：

```json
{
  "pet_id": 3001,
  "contact_phone": "13800000000",
  "experience_desc": "有两年养猫经验",
  "living_condition_desc": "自有住房，可长期照顾"
}
```

### 6.4.5 我的领养申请

- 方法：`GET`
- 路径：`/api/v1/adoption/applications`
- 认证：是

查询参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `status` | string | 否 | 申请状态 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

成功响应 `data.list`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | bigint | 申请 ID |
| `pet` | AdoptionPetSummary | 宠物信息 |
| `status` | string | 申请状态 |
| `review_remark` | string | 审核备注 |
| `created_at` | datetime | 申请时间 |

## 6.5 宠物服务 API

### 6.5.1 服务分类

- 方法：`GET`
- 路径：`/api/v1/services/categories`
- 认证：否

成功响应 `data`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `list` | array | 分类列表 |

### 6.5.2 商家列表

- 方法：`GET`
- 路径：`/api/v1/services/merchants`
- 认证：否

查询参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `category` | string | 否 | 服务分类 |
| `district` | string | 否 | 区域 |
| `sort` | string | 否 | `score_desc` / `distance_asc` |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

成功响应 `data.list`：`MerchantSummary[]`

### 6.5.3 商家详情

- 方法：`GET`
- 路径：`/api/v1/services/merchants/{merchant_id}`
- 认证：否

成功响应 `data`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | bigint | 商家 ID |
| `name` | string | 商家名称 |
| `district` | string | 区域 |
| `address` | string | 地址 |
| `phone` | string | 电话 |
| `business_hours` | string | 营业时间 |
| `score` | number | 评分 |
| `services` | array | 服务项目列表 |
| `reviews` | array | 评价列表 |

### 6.5.4 创建预约

- 方法：`POST`
- 路径：`/api/v1/services/bookings`
- 认证：是

请求体：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `merchant_id` | bigint | 是 | 商家 ID |
| `merchant_service_id` | bigint | 是 | 服务项目 ID |
| `booking_time` | datetime | 是 | 预约时间 |
| `contact_name` | string | 是 | 联系人 |
| `contact_phone` | string | 是 | 联系方式 |
| `remark` | string | 否 | 备注 |

成功响应 `data`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | bigint | 预约 ID |
| `status` | string | `PENDING` |

### 6.5.5 我的预约记录

- 方法：`GET`
- 路径：`/api/v1/services/bookings`
- 认证：是

查询参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `status` | string | 否 | 预约状态 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

成功响应 `data.list`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | bigint | 预约 ID |
| `merchant` | MerchantSummary | 商家信息 |
| `service_name` | string | 服务项目名 |
| `booking_time` | datetime | 预约时间 |
| `status` | string | 预约状态 |

### 6.5.6 取消预约

- 方法：`POST`
- 路径：`/api/v1/services/bookings/{booking_id}/cancel`
- 认证：是

成功响应 `data`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | bigint | 预约 ID |
| `status` | string | `CANCELLED` |

## 6.6 商城 API

### 6.6.1 商品分类

- 方法：`GET`
- 路径：`/api/v1/shop/categories`
- 认证：否

### 6.6.2 商品列表

- 方法：`GET`
- 路径：`/api/v1/shop/products`
- 认证：否

查询参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `category` | bigint | 否 | 分类 ID |
| `keyword` | string | 否 | 搜索关键词 |
| `sort` | string | 否 | `price_asc` / `price_desc` / `latest` |
| `pet_type` | string | 否 | 适用宠物类型 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

成功响应 `data.list`：`ProductSummary[]`

### 6.6.3 商品详情

- 方法：`GET`
- 路径：`/api/v1/shop/products/{product_id}`
- 认证：否

成功响应 `data`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | bigint | 商品 ID |
| `category_id` | bigint | 分类 ID |
| `name` | string | 名称 |
| `subtitle` | string | 副标题 |
| `image_url` | string | 主图 |
| `images` | array | 轮播图 |
| `price` | number | 售价 |
| `stock` | int | 库存 |
| `pet_type` | string | 宠物类型 |
| `description` | string | 图文详情 |
| `status` | string | 商品状态 |

### 6.6.4 购物车列表

- 方法：`GET`
- 路径：`/api/v1/shop/cart`
- 认证：是

成功响应 `data`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `items` | array | 购物车项 |
| `total_amount` | number | 已勾选总价 |

### 6.6.5 加入购物车

- 方法：`POST`
- 路径：`/api/v1/shop/cart/items`
- 认证：是

请求体：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `product_id` | bigint | 是 | 商品 ID |
| `quantity` | int | 是 | 数量 |

### 6.6.6 更新购物车数量

- 方法：`PUT`
- 路径：`/api/v1/shop/cart/items/{item_id}`
- 认证：是

请求体：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `quantity` | int | 是 | 新数量 |
| `checked` | bool | 否 | 是否勾选 |

### 6.6.7 创建订单

- 方法：`POST`
- 路径：`/api/v1/shop/orders`
- 认证：是

请求体：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `item_ids` | array<bigint> | 是 | 购物车项 ID 列表 |
| `receiver_name` | string | 是 | 收货人 |
| `receiver_phone` | string | 是 | 收货电话 |
| `receiver_address` | string | 是 | 收货地址 |
| `remark` | string | 否 | 备注 |

成功响应 `data`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | bigint | 订单 ID |
| `order_no` | string | 订单号 |
| `total_amount` | number | 订单总额 |
| `pay_amount` | number | 实付金额 |
| `status` | string | `PENDING` |

示例请求：

```json
{
  "item_ids": [9001, 9002],
  "receiver_name": "张三",
  "receiver_phone": "13800000000",
  "receiver_address": "上海市浦东新区示例路 99 号",
  "remark": "工作日送达"
}
```

### 6.6.8 订单列表

- 方法：`GET`
- 路径：`/api/v1/shop/orders`
- 认证：是

查询参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `status` | string | 否 | 订单状态 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

成功响应 `data.list`：`OrderSummary[]`

### 6.6.9 订单详情

- 方法：`GET`
- 路径：`/api/v1/shop/orders/{order_id}`
- 认证：是

成功响应 `data`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `id` | bigint | 订单 ID |
| `order_no` | string | 订单号 |
| `status` | string | 状态 |
| `total_amount` | number | 总额 |
| `pay_amount` | number | 实付金额 |
| `receiver_name` | string | 收货人 |
| `receiver_phone` | string | 收货电话 |
| `receiver_address` | string | 收货地址 |
| `items` | array | 订单项 |
| `created_at` | datetime | 创建时间 |

## 6.7 宠物档案 API

### 6.7.1 我的宠物列表

- 方法：`GET`
- 路径：`/api/v1/pets`
- 认证：是

成功响应 `data.list`：`PetProfile[]`

### 6.7.2 新增宠物

- 方法：`POST`
- 路径：`/api/v1/pets`
- 认证：是

请求体：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `name` | string | 是 | 宠物名 |
| `type` | string | 是 | 类型 |
| `breed` | string | 否 | 品种 |
| `gender` | string | 否 | 性别 |
| `birthday` | date | 否 | 生日 |
| `weight` | number | 否 | 当前体重 |
| `avatar_url` | string | 否 | 头像 |
| `description` | string | 否 | 简介 |

### 6.7.3 宠物详情

- 方法：`GET`
- 路径：`/api/v1/pets/{pet_id}`
- 认证：是

成功响应 `data`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `pet` | PetProfile | 宠物信息 |
| `vaccines` | array | 疫苗记录 |
| `weights` | array | 体重记录 |
| `albums` | array | 相册 |

### 6.7.4 更新宠物档案

- 方法：`PUT`
- 路径：`/api/v1/pets/{pet_id}`
- 认证：是

请求体：同“新增宠物”

### 6.7.5 疫苗记录列表

- 方法：`GET`
- 路径：`/api/v1/pets/{pet_id}/vaccines`
- 认证：是

### 6.7.6 新增疫苗记录

- 方法：`POST`
- 路径：`/api/v1/pets/{pet_id}/vaccines`
- 认证：是

请求体：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `vaccine_name` | string | 是 | 疫苗名称 |
| `vaccinated_at` | date | 是 | 接种日期 |
| `next_due_at` | date | 否 | 下次接种日期 |
| `remark` | string | 否 | 备注 |

### 6.7.7 体重记录列表

- 方法：`GET`
- 路径：`/api/v1/pets/{pet_id}/weights`
- 认证：是

### 6.7.8 新增体重记录

- 方法：`POST`
- 路径：`/api/v1/pets/{pet_id}/weights`
- 认证：是

请求体：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `weight` | number | 是 | 体重 |
| `recorded_at` | datetime | 否 | 记录时间，不传则默认当前时间 |

### 6.7.9 宠物成长时间轴

- 方法：`GET`
- 路径：`/api/v1/pets/{pet_id}/timeline`
- 认证：是

成功响应 `data`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `pet` | PetProfile | 宠物信息 |
| `events` | array | 时间轴事件，来源于疫苗、体重、相册等记录 |

---

## 7. 管理员端 API

## 7.1 管理员认证与仪表盘

### 7.1.1 管理员登录

- 方法：`POST`
- 路径：`/api/v1/admin/auth/login`
- 认证：否

请求体：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `phone` | string | 是 | 账号手机号或登录账号 |
| `verify_code` | string | 是 | 验证码或登录口令占位字段 |

成功响应：同用户登录，但 `user.role = ADMIN`

### 7.1.2 仪表盘统计

- 方法：`GET`
- 路径：`/api/v1/admin/dashboard`
- 认证：管理员

成功响应 `data`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `user_total` | int | 用户总数 |
| `post_total` | int | 帖子总数 |
| `order_total` | int | 订单总数 |
| `booking_total` | int | 预约总数 |
| `pending_post_count` | int | 待审核帖子数 |
| `pending_adoption_count` | int | 待审核领养申请数 |
| `order_trend` | array | 订单趋势 |
| `booking_trend` | array | 预约趋势 |

## 7.2 用户管理

### 7.2.1 用户列表

- 方法：`GET`
- 路径：`/api/v1/admin/users`
- 认证：管理员

查询参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `keyword` | string | 否 | 手机号/昵称 |
| `status` | string | 否 | 用户状态 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

成功响应 `data.list`：`UserProfile[]`

### 7.2.2 用户详情

- 方法：`GET`
- 路径：`/api/v1/admin/users/{user_id}`
- 认证：管理员

成功响应 `data`：

| 字段 | 类型 | 说明 |
|---|---|---|
| `user` | UserProfile | 用户信息 |
| `pet_count` | int | 宠物数 |
| `post_count` | int | 发帖数 |
| `order_count` | int | 订单数 |
| `adoption_application_count` | int | 领养申请数 |

### 7.2.3 禁用/启用用户

- 方法：`PUT`
- 路径：`/api/v1/admin/users/{user_id}/status`
- 认证：管理员

请求体：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `status` | string | 是 | `ACTIVE` / `DISABLED` |
| `remark` | string | 否 | 操作备注 |

## 7.3 内容管理

### 7.3.1 帖子审核列表

- 方法：`GET`
- 路径：`/api/v1/admin/posts`
- 认证：管理员

查询参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `status` | string | 否 | 审核状态 |
| `category` | string | 否 | 帖子分类 |
| `keyword` | string | 否 | 标题/作者 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

### 7.3.2 审核帖子

- 方法：`PUT`
- 路径：`/api/v1/admin/posts/{post_id}/review`
- 认证：管理员

请求体：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `status` | string | 是 | `APPROVED` / `REJECTED` |
| `remark` | string | 否 | 审核备注 |

示例请求：

```json
{
  "status": "APPROVED",
  "remark": "内容符合社区规范"
}
```

### 7.3.3 评论管理

- 方法：`GET`
- 路径：`/api/v1/admin/comments`
- 认证：管理员

查询参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `keyword` | string | 否 | 评论内容/用户 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

### 7.3.4 删除评论

- 方法：`DELETE`
- 路径：`/api/v1/admin/comments/{comment_id}`
- 认证：管理员

### 7.3.5 Banner 管理

- Banner 列表：`GET /api/v1/admin/banners`
- 新增 Banner：`POST /api/v1/admin/banners`
- 更新 Banner：`PUT /api/v1/admin/banners/{banner_id}`

Banner 新增/更新请求体建议：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `title` | string | 是 | 标题 |
| `image_url` | string | 是 | 图片地址 |
| `link_url` | string | 否 | 跳转链接 |
| `status` | string | 是 | 上下线状态 |
| `sort` | int | 否 | 排序 |

## 7.4 领养管理

### 7.4.1 待领养宠物列表

- 方法：`GET`
- 路径：`/api/v1/admin/adoption/pets`
- 认证：管理员

### 7.4.2 新增待领养宠物

- 方法：`POST`
- 路径：`/api/v1/admin/adoption/pets`
- 认证：管理员

请求体字段与 [adoption_pets](D:\Code\PetServicePlatform\docs\database.md) 设计一致，建议包含：

- `name`
- `type`
- `breed`
- `gender`
- `age_desc`
- `city`
- `health_status`
- `personality`
- `adoption_requirements`
- `story`
- `cover_url`
- `status`

### 7.4.3 更新待领养宠物

- 方法：`PUT`
- 路径：`/api/v1/admin/adoption/pets/{pet_id}`
- 认证：管理员

### 7.4.4 领养申请列表

- 方法：`GET`
- 路径：`/api/v1/admin/adoption/applications`
- 认证：管理员

查询参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `status` | string | 否 | 审核状态 |
| `pet_id` | bigint | 否 | 宠物 ID |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

### 7.4.5 审核领养申请

- 方法：`PUT`
- 路径：`/api/v1/admin/adoption/applications/{application_id}/review`
- 认证：管理员

请求体：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `status` | string | 是 | `APPROVED` / `REJECTED` |
| `review_remark` | string | 否 | 审核备注 |

## 7.5 服务管理

### 7.5.1 服务分类列表

- 方法：`GET`
- 路径：`/api/v1/admin/services/categories`
- 认证：管理员

### 7.5.2 商家列表

- 方法：`GET`
- 路径：`/api/v1/admin/services/merchants`
- 认证：管理员

### 7.5.3 新增商家

- 方法：`POST`
- 路径：`/api/v1/admin/services/merchants`
- 认证：管理员

请求体建议字段：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `name` | string | 是 | 商家名称 |
| `district` | string | 是 | 区域 |
| `address` | string | 是 | 地址 |
| `phone` | string | 是 | 联系电话 |
| `business_hours` | string | 是 | 营业时间 |
| `status` | string | 是 | 状态 |

### 7.5.4 更新商家

- 方法：`PUT`
- 路径：`/api/v1/admin/services/merchants/{merchant_id}`
- 认证：管理员

### 7.5.5 预约单列表

- 方法：`GET`
- 路径：`/api/v1/admin/services/bookings`
- 认证：管理员

查询参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `status` | string | 否 | 预约状态 |
| `merchant_id` | bigint | 否 | 商家 ID |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

### 7.5.6 处理预约单

- 方法：`PUT`
- 路径：`/api/v1/admin/services/bookings/{booking_id}`
- 认证：管理员

请求体：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `status` | string | 是 | `CONFIRMED` / `COMPLETED` / `CANCELLED` |
| `remark` | string | 否 | 处理备注 |

## 7.6 商城管理

### 7.6.1 商品分类列表

- 方法：`GET`
- 路径：`/api/v1/admin/shop/categories`
- 认证：管理员

### 7.6.2 商品列表

- 方法：`GET`
- 路径：`/api/v1/admin/shop/products`
- 认证：管理员

### 7.6.3 新增商品

- 方法：`POST`
- 路径：`/api/v1/admin/shop/products`
- 认证：管理员

请求体建议字段：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `category_id` | bigint | 是 | 分类 ID |
| `name` | string | 是 | 商品名称 |
| `subtitle` | string | 否 | 副标题 |
| `image_url` | string | 是 | 主图 |
| `price` | number | 是 | 售价 |
| `stock` | int | 是 | 库存 |
| `pet_type` | string | 否 | 适用宠物 |
| `description` | string | 否 | 商品描述 |
| `status` | string | 是 | 上下架状态 |

### 7.6.4 更新商品

- 方法：`PUT`
- 路径：`/api/v1/admin/shop/products/{product_id}`
- 认证：管理员

### 7.6.5 商品上下架

- 方法：`PUT`
- 路径：`/api/v1/admin/shop/products/{product_id}/status`
- 认证：管理员

请求体：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `status` | string | 是 | `ON_SALE` / `OFF_SHELF` |

### 7.6.6 订单列表

- 方法：`GET`
- 路径：`/api/v1/admin/shop/orders`
- 认证：管理员

查询参数：

| 参数 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `status` | string | 否 | 订单状态 |
| `keyword` | string | 否 | 订单号/收货人 |
| `page` | int | 否 | 页码 |
| `page_size` | int | 否 | 每页数量 |

### 7.6.7 处理订单

- 方法：`PUT`
- 路径：`/api/v1/admin/shop/orders/{order_id}`
- 认证：管理员

请求体：

| 字段 | 类型 | 是否必填 | 说明 |
|---|---|---|---|
| `status` | string | 是 | `PAID` / `SHIPPED` / `COMPLETED` / `CANCELLED` |
| `remark` | string | 否 | 处理备注 |

---

## 8. 关键接口示例

### 8.1 获取帖子列表

请求：

```http
GET /api/v1/community/posts?tab=recommended&category=knowledge&page=1&page_size=10
```

响应：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "list": [
      {
        "id": 2001,
        "title": "幼猫疫苗时间表整理",
        "category": "knowledge",
        "cover_url": "https://example.com/post-cover.jpg",
        "excerpt": "整理了幼猫常见疫苗接种节点和注意事项",
        "status": "APPROVED",
        "like_count": 32,
        "favorite_count": 18,
        "comment_count": 6,
        "author": {
          "id": 1001,
          "nickname": "团子妈",
          "avatar_url": "https://example.com/avatar.jpg"
        },
        "tags": ["新手养宠", "疫苗"],
        "published_at": "2026-03-18T10:00:00+08:00"
      }
    ],
    "total": 1,
    "page": 1,
    "page_size": 10
  }
}
```

### 8.2 创建预约

请求：

```json
{
  "merchant_id": 5001,
  "merchant_service_id": 5101,
  "booking_time": "2026-03-20T15:00:00+08:00",
  "contact_name": "张三",
  "contact_phone": "13800000000",
  "remark": "猫咪胆小，请尽量安静处理"
}
```

响应：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "id": 7001,
    "status": "PENDING"
  }
}
```

### 8.3 管理员审核领养申请

请求：

```json
{
  "status": "APPROVED",
  "review_remark": "申请资料完整，沟通情况良好"
}
```

响应：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "id": 4001,
    "status": "APPROVED"
  }
}
```

---

## 9. 版本策略

- 非破坏性改动：在当前版本追加字段，不删除旧字段
- 破坏性改动：通过 `/api/v2` 升级
- 用户端与管理端共用业务模型，但接口职责分离

---

## 10. 当前阶段说明

本文档已覆盖当前设计中的主要用户端与管理端接口，并补充了：

- 统一响应规范
- 鉴权规则
- 通用对象结构
- 主要接口参数定义
- 关键接口返回字段
- 关键业务状态枚举
- 核心接口示例

如果后续进入编码和联调阶段，建议下一步补充：

1. Swagger/OpenAPI 实际输出文档
2. 文件上传接口
3. 验证码发送接口
4. 更细粒度的错误码表
5. 每个列表接口的完整筛选字段定义
