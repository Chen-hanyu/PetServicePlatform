# 演示数据说明

## 1. 文档说明

本文档说明项目后端演示初始化数据脚本的用途与默认联调账号，对应文件：

- [seed.sql](/D:/Code/PetServicePlatform/backend/src/main/resources/sql/seed.sql)

该脚本基于 `schema.sql` 已创建完成的表结构，写入一批可直接用于联调和演示的样例数据，覆盖：

- 用户与管理员账号
- 宠物档案
- 社区帖子、评论、点赞、收藏
- 领养宠物与领养申请
- 服务分类、商家、预约单
- 商品分类、商品、购物车、订单
- Banner、推荐位与消息中心

## 2. 执行顺序

建议按以下顺序执行：

```sql
SOURCE backend/src/main/resources/sql/schema.sql;
SOURCE backend/src/main/resources/sql/seed.sql;
```

## 3. 默认联调账号

- 管理员账号：`13900000000`
- 管理员密码：`admin123`
- 普通用户账号：`13800000001`
- 普通用户密码：`123456`
- 默认验证码兜底值：`123456`

说明：

- `POST /api/v1/auth/login` 和 `POST /api/v1/admin/auth/login` 使用密码登录。
- `POST /api/v1/auth/verify-code` 和 `POST /api/v1/admin/auth/verify-code` 用于发送验证码。
- 验证码接口返回的 `debug_code` 可直接用于本地联调。
- 为兼容现有联调流程，验证码服务保留固定兜底值 `123456`。

## 4. 演示覆盖

执行脚本后，可直接验证以下场景：

1. 首页聚合与全站搜索
2. 社区帖子浏览、发帖、评论、点赞、收藏、后台审核
3. 领养列表、申请提交、后台审核
4. 宠物档案、疫苗记录、体重记录
5. 服务预约、取消预约、后台处理预约单
6. 商品浏览、购物车、下单、后台处理订单
7. 消息中心列表与已读
8. 后台标签管理与首页推荐位管理
