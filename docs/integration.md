# 前后端联调文档

本文档详细描述宠物综合服务平台前后端 API 接口的联调情况，包括已匹配接口、缺失接口、字段映射关系及联调注意事项。

---

## 1. 文档说明

### 1.1 目的

本文档旨在帮助开发团队快速识别前后端 API 的差异，确保联调工作顺利进行。

### 1.2 分析范围

- 前端：`frontend/src/api/modules/` 目录下的所有 API 模块
- 后端：`backend/src/main/java/com/petplatform/controller/` 目录下的所有 Controller

### 1.3 联调状态说明

| 状态 | 说明 |
|------|------|
| ✅ 已匹配 | 前后端接口已对齐，可直接联调 |
| ⚠️ 需确认 | 存在差异，需要确认或调整 |
| ❌ 缺失 | 后端未实现该接口 |
| 🔧 需前端修改 | 前端代码需要调整 |

---

## 2. 基础配置对比

### 2.1 HTTP 客户端配置

| 项目 | 前端 | 后端 | 状态 |
|------|------|------|------|
| Base URL（用户端） | `/api/v1` | `/api/v1` | ✅ 一致 |
| Base URL（管理端） | `/api/v1/admin` | `/api/v1/admin` | ✅ 一致 |
| Token 存储 | localStorage | - | ✅ 配合 |
| Token 名称 | `pet_platform_token` | - | ✅ 配合 |
| 认证头 | `Bearer {token}` | JWT 验证 | ✅ 一致 |
| 响应格式 | `{code, message, data}` | `{code, message, data}` | ✅ 一致 |

### 2.2 响应结构

```typescript
// 前端期望
interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

interface PageResult<T> {
  list: T[];
  total: number;
  page: number;
  page_size: number;
}
```

---

## 3. 用户端接口联调状态

### 3.1 用户认证 `/api/v1/auth`

| 前端函数 | 后端接口 | 方法 | 状态 | 说明 |
|----------|----------|------|------|------|
| `loginUser(phone, password)` | `/api/v1/auth/login` | POST | ✅ 已匹配 | - |
| `registerUser(payload)` | `/api/v1/auth/register` | POST | ✅ 已匹配 | - |
| `logoutUser()` | `/api/v1/auth/logout` | POST | ✅ 已匹配 | - |
| - | `/api/v1/auth/verify-code` | POST | ⚠️ 可选 | 验证码接口（已实现，前端暂不使用） |

**✅ 已修复**：后端 `RegisterRequest` 使用 `phone` 字段，与前端保持一致。前端已补充 `registerUser` 函数并对接真实 API，移除验证码流程（注册成功后自动登录）。

---

### 3.2 首页 `/api/v1/home`

| 前端函数 | 后端接口 | 方法 | 状态 | 说明 |
|----------|----------|------|------|------|
| `fetchHomeData()` | `/api/v1/home` | GET | ✅ 已匹配 | - |

---

### 3.3 个人中心 `/api/v1/profile`

| 前端函数 | 后端接口 | 方法 | 状态 | 说明 |
|----------|----------|------|------|------|
| `fetchCurrentUser()` | `/api/v1/profile/me` | GET | ✅ 已匹配 | - |
| `fetchOverview()` | `/api/v1/profile/overview` | GET | ✅ 已匹配 | - |

**✅ 已修复**：前端已补充 `fetchCurrentUser` 函数。

---

### 3.4 社区 `/api/v1/community`

| 前端函数 | 后端接口 | 方法 | 状态 | 说明 |
|----------|----------|------|------|------|
| `fetchPosts(params)` | `/api/v1/community/posts` | GET | ✅ 已匹配 | - |
| `fetchPostDetail(postId)` | `/api/v1/community/posts/{postId}` | GET | ✅ 已匹配 | - |
| `createPost(payload)` | `/api/v1/community/posts` | POST | ✅ 已匹配 | - |
| `fetchPostComments(postId)` | `/api/v1/community/posts/{postId}/comments` | GET | ✅ 已匹配 | - |
| `createComment(postId, content)` | `/api/v1/community/posts/{postId}/comments` | POST | ✅ 已匹配 | - |
| `toggleLike(postId)` | `/api/v1/community/posts/{postId}/like` | POST | ✅ 已匹配 | - |
| `toggleFavorite(postId)` | `/api/v1/community/posts/{postId}/favorite` | POST | ✅ 已匹配 | - |
| `fetchMyFavorites(params)` | `/api/v1/community/favorites` | GET | ✅ 已匹配 | - |
| `removeFavorite(postId)` | `/api/v1/community/favorites/{postId}` | DELETE | ✅ 已匹配 | - |

**✅ 已修复**：后端已实现收藏列表接口 `GET /api/v1/community/favorites` 和 `DELETE /api/v1/community/favorites/{postId}`，前端已对接真实 API。

---

### 3.5 商城 `/api/v1/shop`

| 前端函数 | 后端接口 | 方法 | 状态 | 说明 |
|----------|----------|------|------|------|
| `fetchShopCategories()` | `/api/v1/shop/categories` | GET | ✅ 已匹配 | - |
| `fetchProducts(params)` | `/api/v1/shop/products` | GET | ✅ 已匹配 | - |
| `fetchProduct(id)` | `/api/v1/shop/products/{id}` | GET | ✅ 已匹配 | - |
| `fetchCart()` | `/api/v1/shop/cart` | GET | ✅ 已匹配 | - |
| `addCartItem(productId, quantity)` | `/api/v1/shop/cart/items` | POST | ✅ 已匹配 | - |
| `updateCartItem(itemId, payload)` | `/api/v1/shop/cart/items/{itemId}` | PUT | ✅ 已匹配 | - |
| `deleteCartItem(itemId)` | `/api/v1/shop/cart/items/{itemId}` | DELETE | ✅ 已匹配 | - |
| `createOrder(payload)` | `/api/v1/shop/orders` | POST | ✅ 已匹配 | - |
| `fetchOrders(params)` | `/api/v1/shop/orders` | GET | ✅ 已匹配 | - |
| `fetchOrderDetail(orderId)` | `/api/v1/shop/orders/{orderId}` | GET | ✅ 已匹配 | - |

**商城接口全部匹配** ✅

---

### 3.6 服务预约 `/api/v1/services`

| 前端函数 | 后端接口 | 方法 | 状态 | 说明 |
|----------|----------|------|------|------|
| `fetchServiceCategories()` | `/api/v1/services/categories` | GET | ✅ 已匹配 | - |
| `fetchMerchants(params)` | `/api/v1/services/merchants` | GET | ✅ 已匹配 | - |
| `fetchMerchantDetail(merchantId)` | `/api/v1/services/merchants/{merchantId}` | GET | ✅ 已匹配 | - |
| `createBooking(payload)` | `/api/v1/services/bookings` | POST | ✅ 已匹配 | - |
| `fetchMyBookings(params)` | `/api/v1/services/bookings` | GET | ✅ 已匹配 | - |
| `cancelBooking(bookingId)` | `/api/v1/services/bookings/{bookingId}/cancel` | POST | ✅ 已匹配 | - |

**服务接口全部匹配** ✅

---

### 3.7 领养 `/api/v1/adoption`

| 前端函数 | 后端接口 | 方法 | 状态 | 说明 |
|----------|----------|------|------|------|
| `fetchAdoptionPets(params)` | `/api/v1/adoption/pets` | GET | ✅ 已匹配 | - |
| `fetchAdoptionPetDetail(petId)` | `/api/v1/adoption/pets/{petId}` | GET | ✅ 已匹配 | - |
| `fetchAdoptionProcess()` | `/api/v1/adoption/process` | GET | ✅ 已匹配 | - |
| `createAdoptionApplication(payload)` | `/api/v1/adoption/applications` | POST | ✅ 已匹配 | - |
| `fetchMyAdoptionApplications(params)` | `/api/v1/adoption/applications` | GET | ✅ 已匹配 | - |

**领养接口全部匹配** ✅

---

### 3.8 宠物档案 `/api/v1/pets`

| 前端函数 | 后端接口 | 方法 | 状态 | 说明 |
|----------|----------|------|------|------|
| `fetchMyPets()` | `/api/v1/pets` | GET | ✅ 已匹配 | - |
| `createPet(payload)` | `/api/v1/pets` | POST | ✅ 已匹配 | - |
| `fetchPetDetail(petId)` | `/api/v1/pets/{petId}` | GET | ✅ 已匹配 | - |
| `updatePet(petId, payload)` | `/api/v1/pets/{petId}` | PUT | ✅ 已匹配 | - |
| `deletePet(petId)` | `/api/v1/pets/{petId}` | DELETE | ✅ 已匹配 | - |
| `fetchPetVaccines(petId)` | `/api/v1/pets/{petId}/vaccines` | GET | ✅ 已匹配 | - |
| `createPetVaccine(petId, payload)` | `/api/v1/pets/{petId}/vaccines` | POST | ✅ 已匹配 | - |
| `fetchPetWeights(petId)` | `/api/v1/pets/{petId}/weights` | GET | ✅ 已匹配 | - |
| `createPetWeight(petId, payload)` | `/api/v1/pets/{petId}/weights` | POST | ✅ 已匹配 | - |
| `fetchPetTimeline(petId)` | `/api/v1/pets/{petId}/timeline` | GET | ✅ 已匹配 | - |
| `createPetAlbum(petId, payload)` | `/api/v1/pets/{petId}/albums` | POST | ✅ 已匹配 | - |

**宠物档案接口全部匹配** ✅

---

### 3.9 消息 `/api/v1/messages`

| 前端函数 | 后端接口 | 方法 | 状态 | 说明 |
|----------|----------|------|------|------|
| `fetchMessages(params)` | `/api/v1/messages` | GET | ✅ 已匹配 | - |
| `markAsRead(messageId)` | `/api/v1/messages/{messageId}/read` | POST | ✅ 已匹配 | - |

---

### 3.10 文件上传 `/api/v1/files`

| 前端函数 | 后端接口 | 方法 | 状态 | 说明 |
|----------|----------|------|------|------|
| `uploadFile(file)` | `/api/v1/files/upload` | POST | ✅ 已匹配 | - |

---

### 3.11 AI 宠医助手 `/api/v1/ai`

| 前端函数 | 后端接口 | 方法 | 状态 | 说明 |
|----------|----------|------|------|------|
| `sendChatMessage(messages)` | `/api/v1/ai/chat` | POST | ✅ 已实现 | 后端已实现 DeepSeek 接入 |

**✅ 已修复**：后端已实现 `AiController`，接入 DeepSeek API，支持：
- OpenAI 兼容接口
- 系统提示词（宠物健康顾问角色）
- 智能建议生成
- 未配置 API Key 时返回友好提示

配置方式：在 `.env` 或环境变量中设置 `DEEPSEEK_API_KEY` 或 `AI_API_KEY`。

---

## 4. 管理端接口联调状态

### 4.1 管理员认证 `/api/v1/admin/auth`

| 前端函数 | 后端接口 | 方法 | 状态 | 说明 |
|----------|----------|------|------|------|
| `loginAdmin(phone, password)` | `/api/v1/admin/auth/login` | POST | ✅ 已匹配 | - |
| - | `/api/v1/admin/auth/verify-code` | POST | ⚠️ 可选 | 验证码接口 |
| `logoutAdmin()` | `/api/v1/admin/auth/logout` | POST | ✅ 已匹配 | - |

---

### 4.2 管理端仪表盘 `/api/v1/admin/dashboard`

| 前端函数 | 后端接口 | 方法 | 状态 | 说明 |
|----------|----------|------|------|------|
| `fetchAdminDashboard()` | `/api/v1/admin/dashboard` | GET | ✅ 已匹配 | - |

---

### 4.3 用户管理 `/api/v1/admin/users`

| 前端函数 | 后端接口 | 方法 | 状态 | 说明 |
|----------|----------|------|------|------|
| `fetchAdminUsers(params)` | `/api/v1/admin/users` | GET | ✅ 已匹配 | - |
| `fetchAdminUserDetail(userId)` | `/api/v1/admin/users/{userId}` | GET | ✅ 已匹配 | - |
| `updateAdminUserStatus(userId, payload)` | `/api/v1/admin/users/{userId}/status` | PUT | ✅ 已匹配 | - |

**✅ 已修复**：前端已补充 `fetchAdminUserDetail` 函数。

---

### 4.4 帖子审核 `/api/v1/admin/posts`

| 前端函数 | 后端接口 | 方法 | 状态 | 说明 |
|----------|----------|------|------|------|
| `fetchAdminPosts(params)` | `/api/v1/admin/posts` | GET | ✅ 已匹配 | - |
| `reviewAdminPost(postId, payload)` | `/api/v1/admin/posts/{postId}/review` | PUT | ✅ 已匹配 | - |

---

### 4.5 评论管理 `/api/v1/admin/comments`

| 前端函数 | 后端接口 | 方法 | 状态 | 说明 |
|----------|----------|------|------|------|
| `fetchAdminComments(params)` | `/api/v1/admin/comments` | GET | ✅ 已匹配 | - |
| `deleteAdminComment(commentId)` | `/api/v1/admin/comments/{commentId}` | DELETE | ✅ 已匹配 | - |

---

### 4.6 订单管理 `/api/v1/admin/shop/orders`

| 前端函数 | 后端接口 | 方法 | 状态 | 说明 |
|----------|----------|------|------|------|
| `fetchAdminOrders(params)` | `/api/v1/admin/shop/orders` | GET | ✅ 已匹配 | - |
| `updateAdminOrder(orderId, payload)` | `/api/v1/admin/shop/orders/{orderId}` | PUT | ✅ 已匹配 | - |

---

### 4.7 预约管理 `/api/v1/admin/services/bookings`

| 前端函数 | 后端接口 | 方法 | 状态 | 说明 |
|----------|----------|------|------|------|
| `fetchAdminBookings(params)` | `/api/v1/admin/services/bookings` | GET | ✅ 已匹配 | - |
| `updateAdminBooking(bookingId, payload)` | `/api/v1/admin/services/bookings/{bookingId}` | PUT | ✅ 已匹配 | - |

---

### 4.8 评价管理 `/api/v1/admin/services/reviews`

| 前端函数 | 后端接口 | 方法 | 状态 | 说明 |
|----------|----------|------|------|------|
| `fetchAdminReviews(params)` | `/api/v1/admin/services/reviews` | GET | ✅ 已匹配 | - |
| `deleteAdminReview(reviewId)` | `/api/v1/admin/services/reviews/{reviewId}` | DELETE | ✅ 已匹配 | - |

---

### 4.9 领养管理 `/api/v1/admin/adoption`

| 前端函数 | 后端接口 | 方法 | 状态 | 说明 |
|----------|----------|------|------|------|
| `fetchAdminAdoptionApplications(params)` | `/api/v1/admin/adoption/applications` | GET | ✅ 已匹配 | - |
| `reviewAdminAdoptionApplication(applicationId, payload)` | `/api/v1/admin/adoption/applications/{applicationId}/review` | PUT | ✅ 已匹配 | - |
| `fetchAdminAdoptionPets(params)` | `/api/v1/admin/adoption/pets` | GET | ✅ 已匹配 | - |
| `createAdminAdoptionPet(payload)` | `/api/v1/admin/adoption/pets` | POST | ✅ 已匹配 | - |
| `updateAdminAdoptionPet(petId, payload)` | `/api/v1/admin/adoption/pets/{petId}` | PUT | ✅ 已匹配 | - |

---

### 4.10 商品管理 `/api/v1/admin/shop`

| 前端函数 | 后端接口 | 方法 | 状态 | 说明 |
|----------|----------|------|------|------|
| `fetchAdminShopCategories()` | `/api/v1/admin/shop/categories` | GET | ✅ 已匹配 | - |
| `fetchAdminProducts(params)` | `/api/v1/admin/shop/products` | GET | ✅ 已匹配 | - |
| `createAdminProduct(payload)` | `/api/v1/admin/shop/products` | POST | ✅ 已匹配 | - |
| `updateAdminProduct(productId, payload)` | `/api/v1/admin/shop/products/{productId}` | PUT | ✅ 已匹配 | - |
| `updateAdminProductStatus(productId, payload)` | `/api/v1/admin/shop/products/{productId}/status` | PUT | ✅ 已匹配 | - |

---

### 4.11 服务管理 `/api/v1/admin/services`

| 前端函数 | 后端接口 | 方法 | 状态 | 说明 |
|----------|----------|------|------|------|
| `fetchAdminServiceCategories()` | `/api/v1/admin/services/categories` | GET | ✅ 已匹配 | - |
| `createAdminServiceCategory(payload)` | `/api/v1/admin/services/categories` | POST | ✅ 已匹配 | - |
| `updateAdminServiceCategory(categoryId, payload)` | `/api/v1/admin/services/categories/{categoryId}` | PUT | ✅ 已匹配 | - |
| `fetchAdminMerchants(params)` | `/api/v1/admin/services/merchants` | GET | ✅ 已匹配 | - |
| `createAdminMerchant(payload)` | `/api/v1/admin/services/merchants` | POST | ✅ 已匹配 | - |
| `updateAdminMerchant(merchantId, payload)` | `/api/v1/admin/services/merchants/{merchantId}` | PUT | ✅ 已匹配 | - |
| `fetchAdminServiceItems(params)` | `/api/v1/admin/services/items` | GET | ✅ 已匹配 | - |
| `createAdminServiceItem(payload)` | `/api/v1/admin/services/items` | POST | ✅ 已匹配 | - |
| `updateAdminServiceItem(serviceId, payload)` | `/api/v1/admin/services/items/{serviceId}` | PUT | ✅ 已匹配 | - |

---

### 4.12 内容管理 `/api/v1/admin`

| 前端函数 | 后端接口 | 方法 | 状态 | 说明 |
|----------|----------|------|------|------|
| `fetchAdminBanners()` | `/api/v1/admin/banners` | GET | ✅ 已匹配 | - |
| `createAdminBanner(payload)` | `/api/v1/admin/banners` | POST | ✅ 已匹配 | - |
| `updateAdminBanner(bannerId, payload)` | `/api/v1/admin/banners/{bannerId}` | PUT | ✅ 已匹配 | - |
| `deleteAdminBanner(bannerId)` | `/api/v1/admin/banners/{bannerId}` | DELETE | ✅ 已匹配 | - |
| `fetchAdminTags(params)` | `/api/v1/admin/tags` | GET | ✅ 已匹配 | - |
| `createAdminTag(payload)` | `/api/v1/admin/tags` | POST | ✅ 已匹配 | - |
| `updateAdminTag(tagId, payload)` | `/api/v1/admin/tags/{tagId}` | PUT | ✅ 已匹配 | - |
| `fetchAdminRecommendations(params)` | `/api/v1/admin/recommendations` | GET | ✅ 已匹配 | - |
| `createAdminRecommendation(payload)` | `/api/v1/admin/recommendations` | POST | ✅ 已匹配 | - |
| `updateAdminRecommendation(recommendationId, payload)` | `/api/v1/admin/recommendations/{recommendationId}` | PUT | ✅ 已匹配 | - |

---

## 5. 问题汇总

### 5.1 ✅ 已修复问题

| 序号 | 问题 | 解决方案 | 状态 |
|------|------|----------|------|
| 1 | 注册接口字段不一致 | 后端使用 `phone`，前端对接真实 API | ✅ 已修复 |
| 2 | 用户收藏列表接口缺失 | 后端实现 `GET /api/v1/community/favorites` | ✅ 已修复 |
| 3 | AI 接口缺失 | 后端实现 `AiController` 接入 DeepSeek | ✅ 已修复 |
| 4 | 前端未封装接口 | 补充 `fetchCurrentUser` 和 `fetchAdminUserDetail` | ✅ 已修复 |

---

### 5.2 ✅ 当前所有接口已匹配

经过本次联调对齐，前后端所有接口已完全匹配，可以开始联调测试。

---

### 5.3 前端未封装但后端已实现的接口

| 序号 | 接口 | 说明 | 状态 |
|------|------|------|------|
| - | - | 所有必要接口已封装 | ✅ 全部完成 |

---

## 6. 联调建议

### 6.1 联调顺序建议

所有接口已匹配完成，可以直接进行联调测试。建议按以下顺序测试：

1. **第一阶段：核心流程**
   - 用户登录/注册 ✅
   - 首页数据加载 ✅
   - 帖子列表/详情 ✅

2. **第二阶段：业务功能**
   - 商城购物流程 ✅
   - 服务预约流程 ✅
   - 领养流程 ✅

3. **第三阶段：用户中心**
   - 订单管理 ✅
   - 预约管理 ✅
   - 宠物档案 ✅
   - **我的收藏 ✅（新完成）**

4. **第四阶段：管理后台**
   - 用户管理 ✅
   - 内容审核 ✅
   - 商品/服务管理 ✅

5. **第五阶段：高级功能**
   - **AI 宠医助手 ✅（新完成）**
   - 收藏功能 ✅

### 6.2 联调检查清单

- [ ] 前端开发服务器已启动 (`npm run dev`)
- [ ] 后端服务已启动 (`mvn spring-boot:run`)
- [ ] 数据库连接正常
- [ ] 测试账号准备完毕
- [ ] API 文档已同步
- [ ] **AI 功能：配置 `DEEPSEEK_API_KEY` 环境变量（如需使用）**

### 6.3 常见问题排查

| 问题 | 可能原因 | 解决方案 |
|------|----------|----------|
| 401 未授权 | Token 未传递/已过期 | 检查 Authorization 头 |
| 404 接口不存在 | 路径不匹配 | 核对 API 路径 |
| 500 服务器错误 | 后端异常 | 检查后端日志 |
| 数据为空 | 分页参数错误 | 检查 page/page_size |
| AI 无响应 | 未配置 API Key | 配置 `DEEPSEEK_API_KEY` |

---

## 7. 接口统计

### 7.1 用户端接口

| 状态 | 数量 |
|------|------|
| ✅ 已匹配 | 48 |
| ⚠️ 可选（暂不使用） | 1（验证码） |
| **合计** | **49** |

### 7.2 管理端接口

| 状态 | 数量 |
|------|------|
| ✅ 已匹配 | 37 |
| **合计** | **37** |

### 7.3 总计

| 状态 | 数量 | 占比 |
|------|------|------|
| ✅ 已匹配 | 85 | 100% |
| ⚠️ 可选 | 1 | - |

**🎉 所有接口已完全匹配，前后端联调就绪！**

---

## 8. 新增功能说明

### 8.1 AI 宠医助手配置

后端新增 AI 功能，支持 DeepSeek 或 OpenAI 兼容 API。

**配置方式**：

在项目根目录 `.env` 或环境变量中配置：

```bash
# DeepSeek API（推荐）
DEEPSEEK_API_KEY=your_deepseek_api_key

# 或使用通用配置
AI_API_KEY=your_api_key
AI_BASE_URL=https://api.deepseek.com
AI_MODEL=deepseek-chat
```

**接口信息**：

| 项目 | 说明 |
|------|------|
| 路径 | `POST /api/v1/ai/chat` |
| 认证 | 无需认证（公开接口） |
| 响应 | `{ reply: string, suggestions: string[] }` |

---

## 9. 后续更新

本文档应随着项目开发持续更新，建议：

1. 每次新增接口后更新本文档
2. 发现问题时在本文档中记录
3. 定期梳理已匹配和待处理接口

---

*文档更新时间：2026-04-16*
*本文档已完成前后端全部接口对齐*
