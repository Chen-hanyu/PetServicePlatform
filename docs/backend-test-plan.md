# 后端测试计划（宠物综合服务平台）

## 1. 文档目标
- 统一后端测试范围、优先级和执行方式。
- 对齐当前实现与 `docs/api.md`、`docs/backend.md`、`docs/database.md` 的设计内容。
- 为后续编写 `backend/src/test/` 自动化测试和人工联调提供明确基线。

## 2. 测试原则
- 优先覆盖 MVP 主流程，不先追求大而全的低价值细节测试。
- 优先验证接口可用、权限隔离、状态流转正确、数据联动正确。
- 统一以 `{ code, message, data }` 返回结构作为接口断言基础。
- 用户端与管理端分开验证，不能混测权限。
- 先做冒烟和核心集成测试，再扩展到更细粒度异常与边界测试。

## 3. 测试范围

### 3.1 必测模块
- 认证与鉴权
- 首页与搜索
- 社区
- 领养
- 宠物档案
- 宠物服务
- 商城
- 管理端审核与管理接口
- 文件上传
- 消息中心

### 3.2 测试类型
- 冒烟测试：验证项目启动、数据库初始化、核心接口可用。
- 接口测试：验证路径、参数、返回结构和状态码语义。
- 集成测试：验证 `controller -> service -> mapper -> database` 联动。
- 权限测试：验证未登录、普通用户、管理员三类访问边界。
- 状态流转测试：验证审核、预约、订单、领养等状态变更链路。
- 数据联动测试：验证统计字段、库存、评分、聚合数据是否同步更新。
- 异常测试：验证非法参数、重复提交、越权访问、状态非法操作。

## 4. 不在本轮强制范围内
- Redis 真接入测试
- MinIO 真接入测试
- 第三方短信通道测试
- 压力测试与性能压测
- 安全渗透测试

说明：
- 当前主登录流程为手机号+密码；验证码发送接口作为可选增强能力保留，测试以接口行为为准，不以真实短信送达为准。
- 当前文件上传为本地存储，测试以上传成功和静态访问可用为准。

## 5. 测试环境
- JDK：17
- Spring Boot：3.x
- 数据库：MySQL 8.x
- 构建工具：Maven
- 建议测试方式：
  - 自动化：JUnit 5 + Spring Boot Test + MockMvc
  - 人工接口联调：Apifox / Postman / Swagger

## 6. 数据准备

### 6.1 初始化脚本
- 执行 [schema.sql](D:/Code/PetServicePlatform/backend/src/main/resources/sql/schema.sql)
- 执行 [seed.sql](D:/Code/PetServicePlatform/backend/src/main/resources/sql/seed.sql)

### 6.2 基础账号
- 普通用户：使用种子数据中的普通用户账号密码登录。
- 管理员：使用种子数据中的管理员账号登录。

### 6.3 关键前置数据
- 至少存在 1 条可展示帖子、1 个待领养宠物、1 个服务分类、1 个商家、1 个服务项目、1 个在售商品。
- 至少存在 1 个可用于后台审核和处理的待办数据。

## 7. 核心必测清单

### 7.1 认证与权限
- `POST /api/v1/auth/login`
  - 手机号和密码正确时登录成功
  - 手机号或密码错误时登录失败
- `POST /api/v1/admin/auth/login`
  - 管理员登录成功
  - 非管理员账号登录管理端失败
- `POST /api/v1/auth/verify-code`（可选增强）
  - 正常发送验证码
  - 手机号为空时返回错误
- 权限边界
  - 未登录访问受保护接口返回未授权
  - 普通用户访问 `/api/v1/admin/**` 返回无权限
  - 管理员可正常访问管理端接口

### 7.2 首页与搜索
- `GET /api/v1/home`
  - 返回 banners、推荐帖子、推荐服务、推荐商品、tips、pet_cards
- `GET /api/v1/search`
  - 不传模块时返回聚合搜索结果
  - 指定 `community / adoption / services / shop` 时返回对应模块结果

### 7.3 社区
- `GET /api/v1/community/posts`
  - 仅返回 `APPROVED` 帖子
  - 分页参数生效
- `GET /api/v1/community/posts/{postId}`
  - 正常返回帖子详情和评论聚合信息
- `POST /api/v1/community/posts`
  - 登录用户可发帖
  - 发帖后状态为 `PENDING`
- `POST /api/v1/community/posts/{postId}/comments`
  - 登录用户可评论
  - 评论后帖子 `comment_count` 更新
- `POST /api/v1/community/posts/{postId}/like`
- `POST /api/v1/community/posts/{postId}/favorite`
  - 点赞/收藏计数正确变化
- 管理端帖子审核
  - `GET /api/v1/admin/posts`
  - `PUT /api/v1/admin/posts/{postId}/review`
  - 验证 `PENDING -> APPROVED / REJECTED`
- 管理端评论管理
  - `GET /api/v1/admin/comments`
  - `DELETE /api/v1/admin/comments/{commentId}`
  - 删除后帖子评论数回滚

### 7.4 领养
- `GET /api/v1/adoption/pets`
  - 仅展示 `ONLINE` 宠物
- `GET /api/v1/adoption/pets/{petId}`
- `GET /api/v1/adoption/process`
- `POST /api/v1/adoption/applications`
  - 登录用户可提交申请
  - 同一用户同一宠物不可重复提交 `PENDING` 申请
- `GET /api/v1/adoption/applications`
  - 仅返回当前用户自己的申请
- 管理端审核
  - `GET /api/v1/admin/adoption/applications`
  - `PUT /api/v1/admin/adoption/applications/{applicationId}/review`
  - 审核通过后宠物状态更新为 `ADOPTED`

### 7.5 宠物档案
- `GET /api/v1/pets`
- `POST /api/v1/pets`
- `GET /api/v1/pets/{petId}`
- `PUT /api/v1/pets/{petId}`
  - 用户只能访问和修改自己的宠物
- `GET /api/v1/pets/{petId}/vaccines`
- `POST /api/v1/pets/{petId}/vaccines`
- `GET /api/v1/pets/{petId}/weights`
- `POST /api/v1/pets/{petId}/weights`
  - 新增体重后 `pets.weight` 同步更新
- `GET /api/v1/pets/{petId}/timeline`
  - 聚合疫苗、体重、相册时间线
- `POST /api/v1/pets/{petId}/albums`
  - 只能给自己的宠物新增相册

### 7.6 宠物服务
- `GET /api/v1/services/categories`
- `GET /api/v1/services/merchants`
- `GET /api/v1/services/merchants/{merchantId}`
  - 商家详情正确返回服务项目与评价
- `POST /api/v1/services/bookings`
  - 正常创建预约
  - 商家、服务项目、状态校验生效
  - 同时段冲突校验生效
- `GET /api/v1/services/bookings`
- `POST /api/v1/services/bookings/{bookingId}/cancel`
  - 用户只能取消自己的预约
- `POST /api/v1/services/merchants/{merchantId}/reviews`
  - 只有完成预约的用户可评价
  - 同一用户同一商家不能重复评价
- 管理端
  - `GET /api/v1/admin/services/bookings`
  - `PUT /api/v1/admin/services/bookings/{bookingId}`
  - `GET /api/v1/admin/services/categories`
  - `POST /api/v1/admin/services/categories`
  - `PUT /api/v1/admin/services/categories/{categoryId}`
  - `GET /api/v1/admin/services/merchants`
  - `POST /api/v1/admin/services/merchants`
  - `PUT /api/v1/admin/services/merchants/{merchantId}`
  - `GET /api/v1/admin/services/items`
  - `POST /api/v1/admin/services/items`
  - `PUT /api/v1/admin/services/items/{serviceId}`
  - `GET /api/v1/admin/services/reviews`
  - `DELETE /api/v1/admin/services/reviews/{reviewId}`
  - 删除评价后商家平均分重算

### 7.7 商城
- `GET /api/v1/shop/categories`
- `GET /api/v1/shop/products`
- `GET /api/v1/shop/products/{productId}`
- `GET /api/v1/shop/cart`
- `POST /api/v1/shop/cart/items`
- `PUT /api/v1/shop/cart/items/{itemId}`
- `POST /api/v1/shop/orders`
  - 校验购物车勾选状态
  - 校验商品状态和库存
  - 下单成功后扣减库存
- `GET /api/v1/shop/orders`
- `GET /api/v1/shop/orders/{orderId}`
  - 用户只能查看自己的订单
- 管理端
  - `GET /api/v1/admin/shop/orders`
  - `PUT /api/v1/admin/shop/orders/{orderId}`
  - `GET /api/v1/admin/shop/categories`
  - `GET /api/v1/admin/shop/products`
  - `POST /api/v1/admin/shop/products`
  - `PUT /api/v1/admin/shop/products/{productId}`
  - `PUT /api/v1/admin/shop/products/{productId}/status`

### 7.8 管理端通用管理
- `GET /api/v1/admin/dashboard`
- `GET /api/v1/admin/users`
- `GET /api/v1/admin/users/{userId}`
- `PUT /api/v1/admin/users/{userId}/status`
- `GET /api/v1/admin/banners`
- `POST /api/v1/admin/banners`
- `PUT /api/v1/admin/banners/{bannerId}`
- `GET /api/v1/admin/tags`
- `POST /api/v1/admin/tags`
- `PUT /api/v1/admin/tags/{tagId}`
- `GET /api/v1/admin/recommendations`
- `POST /api/v1/admin/recommendations`
- `PUT /api/v1/admin/recommendations/{recommendationId}`

### 7.9 文件与消息
- `POST /api/v1/files/upload`
  - 图片上传成功
  - 返回 URL 可访问
- 消息中心接口
  - 返回当前用户消息列表
  - 标记已读或状态更新行为正确

## 8. 关键状态流转测试
- 帖子：`PENDING -> APPROVED / REJECTED`
- 领养申请：`PENDING -> APPROVED / REJECTED`
- 宠物领养状态：`ONLINE -> ADOPTED`
- 预约单：`PENDING -> CONFIRMED -> COMPLETED`
- 预约单：`PENDING / CONFIRMED -> CANCELLED`
- 订单：按当前实现支持的状态流转验证 `PAID / SHIPPED / COMPLETED / CANCELLED`

## 9. 关键数据联动测试
- 评论删除后，帖子 `comment_count` 回滚
- 新增体重后，宠物当前 `weight` 更新
- 下单成功后，商品库存扣减
- 删除商家评价后，商家平均分重新计算
- 首页推荐位配置后，`/api/v1/home` 的推荐内容优先受推荐位控制

## 10. 异常与边界测试
- 非法 ID 访问
- 参数缺失
- 参数格式错误
- 重复提交领养申请
- 重复评价商家
- 库存不足下单
- 越权访问他人资源
- 已处理状态重复审核或重复取消

## 11. 自动化测试落地建议

### 11.1 第一批必须自动化
- 认证与权限
- 社区主流程
- 领养主流程
- 宠物服务预约主流程
- 商城下单主流程
- 宠物档案核心联动

### 11.2 第二批建议自动化
- 首页聚合
- 搜索
- 后台运营配置
- 文件上传
- 消息中心

### 11.3 推荐测试组织
- `backend/src/test/java/com/petplatform/controller/`
  - 接口与权限测试
- `backend/src/test/java/com/petplatform/service/`
  - 状态流转与数据联动测试
- `backend/src/test/resources/`
  - 测试配置、SQL 初始化脚本

## 12. 最小执行顺序
1. 初始化数据库：执行 `schema.sql` 和 `seed.sql`
2. 启动后端服务
3. 先验证密码登录（可选再验证验证码接口）
4. 再按以下顺序做冒烟：
   - 首页与搜索
   - 社区
   - 领养
   - 宠物档案
   - 宠物服务
   - 商城
   - 管理端
5. 最后执行异常与越权场景

## 13. 验收标准
- 所有核心主流程接口可成功联调
- 权限边界无明显越权问题
- 关键状态流转符合设计文档
- 关键聚合字段和统计字段联动正确
- 接口返回结构统一
- 文档中已标注为“已实现”的能力均至少通过冒烟测试
