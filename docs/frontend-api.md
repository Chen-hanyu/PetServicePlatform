# 前端 API 接口文档

本文档描述宠物综合服务平台前端与后端对接的 API 接口规范。

---

## 1. 基础配置

### 1.1 请求客户端

前端采用双客户端架构：

| 客户端 | Base URL | 用途 |
|--------|----------|------|
| `webHttp` | `/api/v1` | 用户端接口 |
| `adminHttp` | `/api/v1/admin` | 管理端接口 |

### 1.2 认证方式

- **认证机制**: JWT Bearer Token
- **请求头格式**:
  ```
  Authorization: Bearer <token>
  ```
- **Token 存储**: localStorage
  - `pet_platform_token`: 登录令牌
  - `pet_platform_user`: 用户信息

### 1.3 统一响应结构

```typescript
interface ApiResponse<T> {
  code: number;      // 0 表示成功，非 0 表示失败
  message: string;    // 响应消息
  data: T;           // 响应数据
}

interface PageResult<T> {
  list: T[];         // 数据列表
  total: number;     // 总数
  page: number;      // 当前页码
  page_size: number; // 每页数量
}
```

### 1.4 错误处理

```typescript
// 提取 data 并检查 code
export const unwrap = <T>(payload: ApiResponse<T>): T => {
  if (payload.code !== 0) {
    throw new Error(payload.message || "请求失败");
  }
  return payload.data;
};

// 错误消息提取
export const toErrorMessage = (error: unknown): string => {
  if (axios.isAxiosError(error)) {
    return (error.response?.data as { message?: string })?.message ||
           error.message || "网络请求失败";
  }
  return error instanceof Error ? error.message : "请求失败";
};
```

### 1.5 401 自动处理

响应拦截器会自动处理 401 错误，触发 logout 清除登录状态。

---

## 2. API 目录结构

```
frontend/src/api/
├── http.ts                      # HTTP 客户端配置、拦截器、工具函数
└── modules/
    ├── auth.ts                  # 用户认证
    ├── community.ts             # 社区帖子
    ├── home.ts                  # 首页
    ├── shop.ts                  # 商城商品
    ├── services.ts              # 服务预约
    ├── adoption.ts              # 领养
    ├── profile.ts               # 个人中心
    ├── pet.ts                   # 宠物档案
    ├── admin-auth.ts            # 管理员认证
    └── admin.ts                 # 管理端接口
```

---

## 3. 用户端 API 详解

### 3.1 用户认证 (`/api/v1/auth`)

#### 登录

```typescript
// 函数
loginUser(username: string, password: string): Promise<LoginResult>

// 请求
POST /api/v1/auth/login
{
  "phone": "13800138000",    // 手机号（注意：字段名为 phone）
  "password": "123456"
}

// 响应
{
  "code": 0,
  "message": "ok",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "token_type": "Bearer",
    "expires_in": 7200,
    "user": {
      "id": 1,
      "phone": "13800138000",
      "nickname": "用户名",
      "avatar": "https://...",
      "role": "USER"
    }
  }
}
```

#### 注册

```typescript
// 函数
registerUser(payload: RegisterPayload): Promise<void>

// 请求
POST /api/v1/auth/register
{
  "phone": "13800138000",    // 手机号
  "password": "123456",      // 密码，6-20位
  "nickname": "用户名"        // 昵称（可选）
}
```

#### 登出

```typescript
// 函数
logoutUser(): Promise<void>

// 请求
POST /api/v1/auth/logout
// 需要认证
```

---

### 3.2 首页 (`/api/v1/home`)

```typescript
// 函数
fetchHomeData(): Promise<HomeData>

// 请求
GET /api/v1/home
// 无需认证

// 响应
{
  "code": 0,
  "message": "ok",
  "data": {
    "banners": [
      {
        "id": 1,
        "title": "Banner标题",
        "image_url": "https://...",
        "link_url": "/community/post/1"
      }
    ],
    "quickEntries": [...],
    "recommendedPosts": [...],
    "recommendedServices": [...],
    "recommendedProducts": [...],
    "petTip": {...}
  }
}
```

---

### 3.3 社区 (`/api/v1/community`)

#### 获取帖子列表

```typescript
// 函数
fetchPosts(params: {
  tab?: 'recommended' | 'latest',  // 标签：推荐/最新
  category?: string,                  // 分类
  tag?: string,                       // 标签
  page?: number,                      // 页码
  page_size?: number                  // 每页数量
}): Promise<PageResult<PostSummary>>

// 请求
GET /api/v1/community/posts?tab=latest&category=经验分享&page=1&page_size=10

// 响应
{
  "code": 0,
  "message": "ok",
  "data": {
    "list": [
      {
        "id": 1,
        "title": "帖子标题",
        "category": "经验分享",
        "cover_url": "https://...",
        "excerpt": "帖子摘要...",
        "status": "PUBLISHED",
        "like_count": 256,
        "favorite_count": 100,
        "comment_count": 32,
        "author": {
          "id": 1,
          "nickname": "用户名",
          "avatar_url": "https://..."
        },
        "tags": ["养猫", "经验"],
        "published_at": "2024-01-15T10:30:00Z"
      }
    ],
    "total": 100,
    "page": 1,
    "page_size": 10
  }
}
```

#### 获取帖子详情

```typescript
// 函数
fetchPostDetail(postId: number): Promise<PostDetail>

// 请求
GET /api/v1/community/posts/{postId}

// 响应
{
  "code": 0,
  "message": "ok",
  "data": {
    "id": 1,
    "title": "帖子标题",
    "content": "帖子正文内容...",
    "category": "经验分享",
    "cover_url": "https://...",
    "images": ["https://...", "https://..."],
    "like_count": 256,
    "favorite_count": 100,
    "comment_count": 32,
    "is_liked": true,
    "is_favorited": false,
    "author": {
      "id": 1,
      "nickname": "用户名",
      "avatar_url": "https://..."
    },
    "tags": ["养猫", "经验"],
    "published_at": "2024-01-15T10:30:00Z"
  }
}
```

#### 获取评论列表

```typescript
// 函数
fetchPostComments(postId: number): Promise<PageResult<PostComment>>

// 请求
GET /api/v1/community/posts/{postId}/comments?page=1&page_size=20

// 响应
{
  "code": 0,
  "message": "ok",
  "data": {
    "list": [
      {
        "id": 1,
        "content": "评论内容",
        "author": {
          "id": 2,
          "nickname": "评论者",
          "avatar_url": "https://..."
        },
        "created_at": "2024-01-15T11:00:00Z"
      }
    ],
    "total": 50,
    "page": 1,
    "page_size": 20
  }
}
```

#### 发布帖子

```typescript
// 函数
createPost(payload: CreatePostPayload): Promise<void>

// 请求
POST /api/v1/community/posts
{
  "title": "帖子标题",           // 必填
  "content": "帖子正文内容",       // 必填
  "category": "经验分享",         // 必填
  "images": ["https://..."],     // 可选，图片地址列表
  "tag_ids": [1, 2]             // 可选，标签ID列表
}
// 需要认证
```

#### 发表评论

```typescript
// 函数
createComment(postId: number, content: string): Promise<void>

// 请求
POST /api/v1/community/posts/{postId}/comments
{
  "content": "评论内容"           // 必填
}
// 需要认证
```

#### 点赞/取消点赞

```typescript
// 函数
toggleLike(postId: number): Promise<ToggleLikeResponse>

// 请求
POST /api/v1/community/posts/{postId}/like
// 需要认证

// 响应
{
  "code": 0,
  "message": "ok",
  "data": {
    "liked": true,               // 当前是否已点赞
    "like_count": 257            // 最新点赞数
  }
}
```

#### 收藏/取消收藏

```typescript
// 函数
toggleFavorite(postId: number): Promise<ToggleFavoriteResponse>

// 请求
POST /api/v1/community/posts/{postId}/favorite
// 需要认证

// 响应
{
  "code": 0,
  "message": "ok",
  "data": {
    "favorited": true,           // 当前是否已收藏
    "favorite_count": 101        // 最新收藏数
  }
}
```

---

### 3.4 商城 (`/api/v1/shop`)

#### 获取商品分类

```typescript
// 函数
fetchShopCategories(): Promise<ProductCategory[]>

// 请求
GET /api/v1/shop/categories

// 响应
{
  "code": 0,
  "message": "ok",
  "data": [
    {
      "id": 1,
      "name": "猫粮",
      "icon": "https://..."
    }
  ]
}
```

#### 获取商品列表

```typescript
// 函数
fetchProducts(params: {
  category?: number,      // 分类ID
  keyword?: string,       // 搜索关键词
  sort?: string,          // 排序方式
  pet_type?: string,      // 适用品类
  page?: number,
  page_size?: number
}): Promise<PageResult<ProductSummary>>

// 请求
GET /api/v1/shop/products?category=1&keyword=猫粮&page=1&page_size=10
```

#### 获取商品详情

```typescript
// 函数
fetchProduct(id: number): Promise<ProductDetail>

// 请求
GET /api/v1/shop/products/{productId}
```

#### 获取购物车

```typescript
// 函数
fetchCart(): Promise<CartData>

// 请求
GET /api/v1/shop/cart
// 需要认证
```

#### 添加购物车

```typescript
// 函数
addCartItem(productId: number, quantity = 1): Promise<CartData>

// 请求
POST /api/v1/shop/cart/items
{
  "product_id": 1,       // 商品ID
  "quantity": 1          // 数量
}
// 需要认证
```

#### 创建订单

```typescript
// 函数
createOrder(payload: CreateOrderPayload): Promise<OrderResult>

// 请求
POST /api/v1/shop/orders
{
  "item_ids": [1, 2, 3],            // 购物车条目ID列表
  "receiver_name": "收货人",        // 收货人
  "receiver_phone": "13800138000", // 收货电话
  "receiver_address": "详细地址",    // 收货地址
  "remark": "备注"                  // 备注（可选）
}
// 需要认证
```

---

### 3.5 服务预约 (`/api/v1/services`)

#### 获取服务分类

```typescript
// 函数
fetchServiceCategories(): Promise<ServiceCategory[]>

// 请求
GET /api/v1/services/categories
```

#### 获取商家列表

```typescript
// 函数
fetchMerchants(params: {
  category?: string,     // 分类
  district?: string,     // 区域
  sort?: string,         // 排序方式
  page?: number,
  page_size?: number
}): Promise<PageResult<MerchantSummary>>

// 请求
GET /api/v1/services/merchants?category=美容&district=朝阳区
```

#### 获取商家详情

```typescript
// 函数
fetchMerchantDetail(merchantId: number): Promise<MerchantDetail>

// 请求
GET /api/v1/services/merchants/{merchantId}
```

#### 创建服务预约

```typescript
// 函数
createBooking(payload: CreateBookingPayload): Promise<void>

// 请求
POST /api/v1/services/bookings
{
  "merchant_id": 1,                    // 商家ID
  "merchant_service_id": 1,            // 服务项目ID
  "booking_time": "2024-01-20 14:00", // 预约时间
  "contact_name": "联系人",             // 联系人
  "contact_phone": "13800138000",      // 联系电话
  "remark": "备注"                     // 备注（可选）
}
// 需要认证
```

---

### 3.6 领养 (`/api/v1/adoption`)

#### 获取待领养宠物列表

```typescript
// 函数
fetchAdoptionPets(params: {
  type?: string,       // 宠物类型：猫、狗
  city?: string,      // 城市
  gender?: string,    // 性别：MALE、FEMALE
  page?: number,
  page_size?: number
}): Promise<PageResult<AdoptionPetSummary>>

// 请求
GET /api/v1/adoption/pets?type=猫&city=北京
```

#### 获取领养宠物详情

```typescript
// 函数
fetchAdoptionPetDetail(petId: number): Promise<AdoptionPetDetail>

// 请求
GET /api/v1/adoption/pets/{petId}
```

#### 获取领养流程说明

```typescript
// 函数
fetchAdoptionProcess(): Promise<AdoptionProcess>

// 请求
GET /api/v1/adoption/process
```

#### 提交领养申请

```typescript
// 函数
createAdoptionApplication(payload: AdoptionApplicationPayload): Promise<void>

// 请求
POST /api/v1/adoption/applications
{
  "pet_id": 1,                       // 宠物ID
  "contact_phone": "13800138000",     // 联系电话
  "experience_desc": "养宠经验描述",    // 养宠经验
  "living_condition_desc": "居住条件"   // 居住条件
}
// 需要认证
```

---

### 3.7 个人中心 (`/api/v1/profile`)

#### 获取个人中心概览

```typescript
// 函数
fetchOverview(): Promise<ProfileOverview>

// 请求
GET /api/v1/profile/overview
// 需要认证

// 响应
{
  "code": 0,
  "message": "ok",
  "data": {
    "pet_count": 2,
    "post_count": 5,
    "favorite_count": 10,
    "order_count": 3,
    "booking_count": 1,
    "adoption_application_count": 0
  }
}
```

#### 获取当前用户信息

```typescript
// 请求
GET /api/v1/profile/me
// 需要认证
```

---

### 3.8 宠物档案 (`/api/v1/pets`)

#### 获取我的宠物列表

```typescript
// 函数
fetchMyPets(): Promise<PetProfile[]>

// 请求
GET /api/v1/pets
// 需要认证
```

#### 创建宠物档案

```typescript
// 函数
createPet(payload: SavePetPayload): Promise<PetProfile>

// 请求
POST /api/v1/pets
{
  "name": "咪咪",           // 宠物名称
  "type": "猫",             // 类型
  "breed": "英短",          // 品种（可选）
  "gender": "MALE",        // 性别（可选）
  "birthday": "2023-01-01",// 生日（可选）
  "weight": 4.5,           // 体重（可选）
  "avatar_url": "https://...", // 头像（可选）
  "description": "描述"     // 描述（可选）
}
// 需要认证
```

---

### 3.9 AI 宠医助手 (`/api/v1/ai`)

#### AI 对话

```typescript
// 函数
sendChatMessage(messages: ChatMessage[]): Promise<AIChatResponse>

interface ChatMessage {
  role: "user" | "assistant";
  content: string;
}

interface AIChatResponse {
  reply: string;
  suggestions?: string[];
}

// 请求
POST /api/v1/ai/chat
{
  "messages": [
    {
      "role": "user",
      "content": "我家猫咪一直打喷嚏是怎么回事？"
    }
  ]
}
// 无需认证

// 响应
{
  "code": 0,
  "message": "ok",
  "data": {
    "reply": "先观察猫咪是否还伴随流鼻涕、食欲下降或精神差，如果持续超过 24 小时，建议尽快就医。",
    "suggestions": [
      "观察症状持续时间",
      "留意是否有食欲下降",
      "持续加重时尽快就医"
    ]
  }
}
```

联调约定：

- 前端会传入当前会话的 `messages` 数组，后端负责拼接系统提示词并保留最近上下文。
- `role` 仅支持 `user` 和 `assistant`。
- 当未配置 `AI_API_KEY` / `DEEPSEEK_API_KEY` 或上游模型服务异常时，后端返回 `{ code, message, data }` 统一错误结构。

---

## 4. 前后端字段命名对照

### 4.1 通用字段

| 后端 (snake_case) | 前端 (snake_case) | 说明 |
|--------------------|-------------------|------|
| `page_size` | `page_size` | 每页数量 ✅ 一致 |
| `page` | `page` | 页码 ✅ 一致 |
| `total` | `total` | 总数 ✅ 一致 |

### 4.2 用户相关

| 后端 | 前端 | 说明 |
|------|------|------|
| `id` | `id` | ✅ 一致 |
| `phone` | `phone` | ✅ 一致 |
| `nickname` | `nickname` | ✅ 一致 |
| `avatar` / `avatar_url` | `avatar_url` | ⚠️ 注意兼容 |
| `role` | `role` | ✅ 一致 |

### 4.3 帖子相关

| 后端 (camelCase) | 前端 (snake_case) | 说明 |
|-------------------|-------------------|------|
| `postId` | `id` | ✅ 路径参数 |
| `likeCount` | `like_count` | ✅ 命名风格 |
| `favoriteCount` | `favorite_count` | ✅ 命名风格 |
| `commentCount` | `comment_count` | ✅ 命名风格 |
| `coverUrl` | `cover_url` | ✅ 命名风格 |
| `avatarUrl` | `avatar_url` | ✅ 命名风格 |
| `isLiked` | `is_liked` | ✅ 命名风格 |
| `isFavorited` | `is_favorited` | ✅ 命名风格 |
| `publishedAt` | `published_at` | ✅ 命名风格 |
| `createdAt` | `created_at` | ✅ 命名风格 |
| `productId` | `product_id` | ✅ 命名风格 |

---

## 5. 状态码说明

| code | 说明 |
|------|------|
| `0` | 成功 |
| 非 `0` | 失败，具体含义由 `message` 描述 |

HTTP 状态码：

| 状态码 | 说明 |
|--------|------|
| `200` | 请求成功 |
| `201` | 创建成功 |
| `400` | 请求参数错误 |
| `401` | 未登录或登录失效 |
| `403` | 无权限 |
| `404` | 资源不存在 |
| `422` | 请求数据校验失败 |
| `500` | 服务端异常 |

---

## 6. 联调注意事项

### 6.1 需要后端补充的接口

| 接口 | 说明 | 优先级 |
|------|------|--------|
| `GET /api/v1/community/favorites` | 获取用户收藏的帖子列表 | 高 |
| `PUT /api/v1/shop/cart/items/{itemId}` | 更新购物车商品数量 | 中 |
| `DELETE /api/v1/shop/cart/items/{itemId}` | 删除购物车商品 | 中 |
| `GET /api/v1/shop/orders` | 获取我的订单列表 | 高 |
| `GET /api/v1/shop/orders/{orderId}` | 获取订单详情 | 高 |
| `GET /api/v1/services/bookings` | 获取我的预约列表 | 高 |
| `POST /api/v1/services/bookings/{bookingId}/cancel` | 取消预约 | 中 |
| `GET /api/v1/adoption/applications` | 获取我的领养申请 | 高 |

### 6.2 前端已实现但需后端确认的接口

以下接口前端已调用真实 API，但需确认后端返回数据格式：

| 接口 | 前端文件 | 状态 |
|------|----------|------|
| 收藏功能 | `MyFavoritesPage.vue` | ⚠️ 使用 mock 数据，需等后端提供收藏列表接口 |
| 订单列表 | `OrdersPage.vue` | ⚠️ 使用 mock 数据 |
| 预约列表 | `MyBookingsPage.vue` | ⚠️ 使用 mock 数据 |

### 6.3 跨域配置

开发环境下，前端通过 Vite 代理解决跨域问题：

```typescript
// vite.config.ts
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

生产环境通过 Nginx 反向代理。

---

## 7. 类型定义

类型定义统一放在 `frontend/src/types/` 目录下：

| 文件 | 类型 | 说明 |
|------|------|------|
| `api.ts` | `ApiResponse`, `PageResult` | 通用响应类型 |
| `auth.ts` | `LoginResult`, `UserProfile`, `ProfileOverview` | 认证相关 |
| `community.ts` | `PostSummary`, `PostDetail`, `PostComment` | 社区相关 |
| `home.ts` | `HomeData`, `Banner`, `QuickEntry` | 首页相关 |
| `shop.ts` | `ProductSummary`, `ProductDetail`, `CartData` | 商城相关 |
| `service.ts` | `MerchantSummary`, `MerchantDetail` | 服务相关 |
| `adoption.ts` | `AdoptionPetSummary`, `AdoptionPetDetail` | 领养相关 |
| `pet.ts` | `PetProfile`, `SavePetPayload` | 宠物相关 |
