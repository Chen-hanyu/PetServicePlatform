# API 设计与前端集成贡献说明

姓名：OrangeQu
学号：（请补充你的学号）
日期：2026-03-31

## 我完成的工作

### 1. 前端页面开发
- [x] 用户端页面实现（登录、注册、个人中心概览）
- [x] 商城模块页面（商品列表、商品详情、购物车、订单）
- [x] 服务预约模块页面（商家列表、商家详情、预约表单、预约确认）
- [x] 社区模块页面（帖子列表、帖子详情、评论）
- [x] 领养模块页面（宠物列表、宠物详情、申请表单）
- [x] 个人中心页面（宠物档案、预约记录、订单记录、设置）

### 2. 前端组件开发
- [x] 认证组件（AuthSplitShell）
- [x] 服务预约组件（ServiceBookingDock）
- [x] 商城组件（CommerceDock）
- [x] 状态徽章组件（StatusBadge）

### 3. 前端访问层（Mock 模式）
- [x] HTTP 客户端配置（src/api/http.ts）
- [x] API 调用封装（src/api/modules/）
- [x] Mock 数据配置（src/mocks/）
- [x] 购物车状态管理（src/store/shopCart.ts）
- [x] 服务预约状态管理（src/store/serviceBooking.ts）
- [x] 飞购物车动画逻辑（src/composables/useFlyToCart.ts）

### 4. 文档与规范
- [x] 前端路由与页面结构维护（src/router/index.ts）
- [x] 全局样式与设计令牌（src/styles/）
- [x] 配合后端 API 文档（docs/api.md）进行 Mock 数据对齐

### 5. Git 工作流
- [x] 在功能分支 feature/qts-frontend-develop 上开发
- [x] 合并至 develop 分支并推送至远程仓库

## PR 链接

合并提交：https://github.com/Chen-hanyu/PetServicePlatform/commit/ea429b6

> 注：本次采用直接合并方式，commit hash 为合并提交 ID，可通过 GitHub 查看完整 diff。

## 遇到的问题和解决

1. **问题：Mock 数据结构与 api.yaml 不一致导致部分页面无法正常渲染**  
   解决：逐一对照 docs/api.yaml 中的响应字段定义（如分页结构 {list, total, page, page_size}），修正 src/mocks/ 下的所有 Mock 数据文件，确保与 OpenAPI 文档保持一致。

2. **问题：商城模块涉及多个页面的数据联动（列表 → 详情 → 购物车 → 结算 → 订单）**  
   解决：引入 Pinia store（shopCart.ts）统一管理购物车状态，配合 useFlyToCart.ts 实现加入购物车时的抛物线动画效果，提升交互体验。

3. **问题：服务预约流程涉及商家、服务项目、预约时间等多个实体，表单校验逻辑复杂**  
   解决：将预约表单拆分为多步填写（选择商家 → 选择服务 → 选择时间 → 填写信息），通过 serviceBooking.ts store 管理临时预约状态，降低组件间耦合。

4. **问题：Git 合并前 develop 分支有 25 个新提交，需要拉取最新代码**  
   解决：先执行 git pull origin develop 拉取最新代码，再执行合并，确保合并提交干净无冲突。

5. **问题：老师要求前端 API 访问层必须位于 src/api/ 目录，与当前 src/services/ 不一致**  
   解决：将 src/services/ 目录整体重命名为 src/api/，同时更新所有相关引用路径，确保路径规范与老师要求一致。

## 心得体会

本次开发让我深入实践了 Vue 3 + TypeScript 的前端工程化流程，包括组件化开发、Pinia 状态管理、Mock 数据与 OpenAPI 文档的协同工作。在多模块（商城、服务预约、社区、领养）的并行开发中，清晰的接口约定和规范化的文档（api.yaml）是保证团队协作效率的关键。未来可以进一步优化代码结构，抽取公共业务逻辑，提升项目的可维护性。
