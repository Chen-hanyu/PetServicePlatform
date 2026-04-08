# 后端未完成项与必要性评估（2026-04-08）

## 1. 目的

本文档用于明确当前后端“还没做完”的事项，并回答以下问题：

- 数据库索引是否需要现在做？
- 缓存（Redis）是否需要现在做？
- 消息队列（MQ）是否需要现在做？

适用范围：课程项目当前 MVP 阶段。

---

## 2. 当前未完成项（按优先级）

| 优先级 | 项目 | 当前状态 | 建议阶段 |
|---|---|---|---|
| P0 | 下单库存并发安全（防超卖） | 已完成（2026-04-08） | 已落地 |
| P0 | 预约冲突并发安全（防重复占位） | 已完成（2026-04-08） | 已落地 |
| P0 | 购物车并发加购安全（防重复插入/丢增量） | 已完成（2026-04-08） | 已落地 |
| P1 | 管理端批量操作接口 | 未完成 | 下一阶段 |
| P1 | 搜索性能优化（避免全量拉取后内存过滤） | 未完成 | 下一阶段 |
| P1 | 部分查询索引增强（见第 3 节） | 已完成（2026-04-08） | 已落地 |
| P2 | Redis 缓存（首页/热点/搜索） | 未接入 | 下一阶段 |
| P2 | MinIO 对象存储 | 未接入 | 下一阶段 |
| P2 | MQ（通知、异步解耦） | 未接入 | 下一阶段 |
| P2 | 本地 MySQL 集成测试链路 | 未完成 | 下一阶段 |

---

## 3. 数据库索引：现在要不要做？

结论：**要做“小步增强”，不用大规模重构。**

原因：
- 现有 `schema.sql` 已有较完整基础索引，整体并不差。
- 但有几类高频查询与排序场景，当前索引覆盖不够精确，数据量上来后会有慢查询风险。
- 补 3~5 个索引成本低、风险小、收益直接，适合现在做。

### 3.1 建议现在补齐的索引（低风险）

```sql
-- 首页帖子推荐（按点赞+发布时间）
ALTER TABLE community_posts
ADD KEY idx_community_posts_status_like_published (status, like_count, published_at);

-- 首页商品推荐（按上架状态+创建时间）
ALTER TABLE products
ADD KEY idx_products_status_created (status, created_at);

-- 首页领养卡片（按状态+创建时间）
ALTER TABLE adoption_pets
ADD KEY idx_adoption_pets_status_created (status, created_at);

-- 预约冲突校验（商家+服务项+时间+状态）
ALTER TABLE service_bookings
ADD KEY idx_service_bookings_merchant_service_time_status
(merchant_id, merchant_service_id, booking_time, status);

-- 服务列表评分排序（状态+评分）
ALTER TABLE merchants
ADD KEY idx_merchants_status_score (status, score);
```

### 3.2 执行原则

- 先在测试库执行 `EXPLAIN`，确认走索引后再进主库。
- 每次只加 1~2 个索引，避免一次性改动过大。
- 若某索引命中率低或与现有索引高度重叠，可回滚该索引。

### 3.3 本次已落地（2026-04-08）

- `community_posts(status, like_count, published_at)`
- `products(status, created_at)`
- `adoption_pets(status, created_at)`
- `service_bookings(merchant_id, merchant_service_id, booking_time, status)`
- `merchants(status, score)`

---

## 4. Redis 缓存：现在要不要做？

结论：**当前课程 MVP 不强制，现在可不做；若准备答辩演示性能，可做最小版。**

建议：
- 不做也能交付：当前主流程已闭环，功能正确性优先。
- 若做，优先做“最小缓存闭环”：
  1. 首页聚合缓存（短 TTL，5~10 分钟）。
  2. 热门列表缓存（帖子/商家/商品）。
  3. 验证码存储迁移到 Redis（保留接口但改存储）。

不建议现在做的内容：
- 复杂多级缓存、分布式锁全覆盖、缓存一致性复杂策略。

---

## 5. 消息队列（MQ）：现在要不要做？

结论：**现在不建议做，优先级低于并发安全和查询性能。**

原因：
- 当前系统还没有“必须异步化才能成立”的重任务链路。
- 引入 MQ 会增加运维与一致性复杂度（重试、死信、幂等、消费监控）。
- 对课程阶段来说，收益不如先把并发安全和索引补齐。

适合后续引入 MQ 的场景：
- 下单后异步通知、站内消息投递、审核结果推送、行为日志异步落库。

---

## 6. 推荐落地顺序

1. 先做并发安全（库存扣减、预约冲突、购物车并发加购）。
2. 补 3~5 个关键索引并用 `EXPLAIN` 验证。
3. 视答辩/演示需要决定是否接入最小 Redis 缓存。
4. MQ 放到下一阶段，不阻塞当前交付。

---

## 7. 最终判断（简版）

- 数据库索引：**有必要现在做一小步**。
- 缓存（Redis）：**现在可选，不是必须**。
- 消息队列（MQ）：**现在不建议做**。
