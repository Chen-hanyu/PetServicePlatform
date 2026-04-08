# 数据库建表 SQL 说明

## 1. 文档说明

本文档用于补充宠物综合服务平台的数据库落地脚本说明，对应实际建表文件：

- [schema.sql](/D:/Code/PetServicePlatform/backend/src/main/resources/sql/schema.sql)
- [seed.sql](/D:/Code/PetServicePlatform/backend/src/main/resources/sql/seed.sql)

当前版本为 **MVP 阶段核心表建表脚本**，主要覆盖以下业务闭环：

- 用户与权限
- 宠物档案
- 社区内容
- 领养申请
- 宠物服务预约
- 商城下单

完整字段设计依据见：

- [database.md](/D:/Code/PetServicePlatform/docs/database.md)

---

## 2. 当前已覆盖的核心表

本次 `schema.sql` 已包含以下表：

- `users`
- `messages`
- `pets`
- `pet_vaccines`
- `pet_weights`
- `pet_albums`
- `community_posts`
- `post_comments`
- `post_likes`
- `post_favorites`
- `tags`
- `post_tags`
- `adoption_pets`
- `adoption_applications`
- `service_categories`
- `merchants`
- `merchant_services`
- `merchant_reviews`
- `service_bookings`
- `product_categories`
- `products`
- `cart_items`
- `shop_orders`
- `shop_order_items`
- `banners`
- `recommendations`

这些表已经满足“至少 3 个核心表”的课程要求，并且超过最小要求。

---

## 3. 脚本设计原则

- 数据库类型：`MySQL 8.x`
- 字符集：`utf8mb4`
- 排序规则：`utf8mb4_unicode_ci`
- 主键统一使用 `BIGINT`
- 主流程表统一包含 `created_at`、`updated_at`
- 对高频查询字段和业务唯一约束建立索引
- 对订单号、手机号、点赞/收藏/购物车等场景建立唯一约束

---

## 4. 执行方式

### 4.1 本地执行

可在本地 MySQL 中执行：

```sql
SOURCE backend/src/main/resources/sql/schema.sql;
SOURCE backend/src/main/resources/sql/seed.sql;
```

或者直接在数据库客户端打开并运行 [schema.sql](/D:/Code/PetServicePlatform/backend/src/main/resources/sql/schema.sql)。

### 4.2 执行前准备

- 确保本地 MySQL 已启动
- 确保具备创建数据库与建表权限
- 脚本默认会创建数据库 `pet_service_platform`

---

## 5. 与后续 Migration 的关系

当前项目尚未引入 Flyway 或 Liquibase。  
因此目前采用：

- 一份完整初始化脚本：`schema.sql`

后续如果进入持续迭代开发阶段，建议演进为：

- `V1__init_schema.sql`
- `V2__add_indexes.sql`
- `V3__add_security_tables.sql`

即迁移到标准 Migration 模式。

---

## 6. 结论

当前数据库设计要求已经补齐：

- 已设计核心数据表
- 已绘制 ER 图
- 已提供建表 SQL
- 社区帖子表已包含审核备注字段 `review_remark`，以对齐管理员审核接口文档
- 已于 2026-04-08 增补首页聚合与预约冲突相关索引（详见 `schema.sql`）
- 已提供演示初始化脚本 `seed.sql`

如后续需要，我可以继续补：

1. 初始化演示数据脚本 `seed.sql`
2. Flyway 版本化 Migration 脚本
3. 针对 MyBatis-Plus 的实体类与 Mapper 初稿
