# 前端模块说明（宠友圈小程序）

## 1. 模块功能
前端模块负责小程序端的页面展示、交互逻辑、状态管理与接口联调，覆盖以下业务：
- 首页：推荐内容、宠物服务入口、活动 Banner
- 宠物档案：宠物信息新增/编辑、免疫记录、健康数据展示
- 服务预约：门店选择、时间段预约、预约状态查询
- 商城：商品列表、详情、购物车、订单流程
- 社区：帖子发布、图文浏览、评论点赞收藏
- 我的：个人信息、我的宠物、我的订单、我的预约

## 2. 技术选型
- 框架：Taro + React + TypeScript
- 状态管理：Zustand
- 网络请求：Axios
- 样式方案：SCSS + BEM 命名
- 组件库：NutUI（Taro 版本）
- 代码规范：ESLint + Prettier

## 3. 目录结构

```text
frontend/
├── src/
│   ├── pages/               # 页面（home/pet/booking/shop/community/profile）
│   ├── components/          # 通用组件（卡片、弹窗、表单项等）
│   ├── services/            # API 请求封装
│   ├── store/               # 全局状态管理
│   ├── hooks/               # 复用逻辑
│   ├── utils/               # 工具函数
│   ├── styles/              # 全局样式与主题变量
│   └── app.config.ts        # 小程序全局配置
├── config/                  # 各环境配置
└── README.md
```

## 4. 运行方式
1. 进入目录：`cd frontend`
2. 安装依赖：`npm install`
3. 开发模式（微信小程序）：`npm run dev:weapp`
4. 生产构建：`npm run build:weapp`
5. 使用微信开发者工具导入 `dist` 目录预览

## 5. 前后端联调约定
- 接口前缀：`/api/v1`
- 统一响应：`{ code, message, data }`
- 鉴权方式：`Authorization: Bearer <token>`
- 错误处理：前端统一拦截器处理 `401/403/500`
- 环境变量：开发、测试、生产分别配置 API 域名
