# PetServicePlatform Frontend

本目录是宠物综合服务平台前端工程，基于 Vue 3 + Vite + TypeScript 实现，包含用户 Web 前台和管理员后台两套界面。

## 技术栈

- Vue 3
- Vite
- TypeScript
- Vue Router
- Pinia
- Axios
- SCSS
- Vitest + Testing Library

## 目录结构

```text
frontend/
├── src/
│   ├── api/                 # Axios 实例与前后端接口封装
│   ├── assets/              # 静态资源
│   ├── components/          # 公共组件、AI 助手、客服浮窗
│   ├── layout/              # WebLayout、AdminLayout
│   ├── pages/
│   │   ├── web/             # 用户端页面
│   │   └── admin/           # 管理端页面
│   ├── router/              # 路由配置
│   ├── store/               # Pinia 状态
│   ├── styles/              # 全局样式与设计变量
│   ├── types/               # TypeScript 类型
│   ├── utils/               # 工具函数、API 日志
│   └── __tests__/           # 前端单元测试
├── Dockerfile
├── nginx.conf
├── vite.config.ts
└── package.json
```

## 主要页面

### 用户端

- `/home`：首页聚合推荐
- `/community`：社区列表、分类、标签和搜索
- `/community/create`：发布帖子
- `/community/post/:id`：帖子详情、评论、点赞、收藏
- `/adoption`：领养宠物列表、流程说明、申请入口
- `/services`：服务商家与分类
- `/services/merchant/:id`：商家详情
- `/services/checkout`：服务预约确认
- `/shop`：商城列表与搜索
- `/shop/product/:id`：商品详情
- `/shop/checkout`：收货地址、优惠券、订单提交
- `/profile`：个人中心
- `/profile/orders`、`/profile/bookings`、`/profile/messages` 等个人业务页
- `/health`：系统状态页

### 管理端

- `/admin/dashboard`：运营仪表盘
- `/admin/users`：用户管理
- `/admin/content`：帖子、评论、Banner、标签、推荐位
- `/admin/adoption`：待领养宠物与申请审核
- `/admin/services`：商家、服务项目、预约处理
- `/admin/shop`：商品、订单管理
- `/admin/support`：客服咨询处理
- `/admin/monitoring`：接口访问与健康指标

## 本地开发

```bash
npm install
npm run dev
```

默认开发地址：`http://localhost:5173`。

开发环境通过 Vite proxy 将 `/api` 和 `/uploads` 转发到本地后端。

## 构建与预览

```bash
npm run build
npm run preview
```

## 测试

```bash
npm run lint
npm run test
npm run test:coverage
```

最近本地覆盖率：

- Statements：84.37%
- Lines：85.02%
- Functions：82.53%
- Branches：58.62%

## 部署

- Vercel 使用根目录 `vercel.json` 构建 `frontend`。
- Docker 使用 `frontend/Dockerfile` 构建静态文件，并由 Nginx 提供服务。
- Nginx 已配置 SPA fallback 和上传大小限制 `client_max_body_size 6m`。

生产 API 代理配置见根目录 `vercel.json` 和 `docs/deployment.md`。
