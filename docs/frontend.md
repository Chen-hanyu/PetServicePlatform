# 前端模块说明

## 1. 文档说明

本文档说明宠物综合服务平台前端当前实现。前端由 `qutianshun` 负责，基于 Vue 3 + Vite + TypeScript 实现用户前台和管理后台两套界面。

运行、构建和部署细节见 `frontend/README.md`；本文件重点描述模块边界、目录结构、页面范围、API 调用约定和联调要求。

## 2. 技术栈

| 类型 | 技术 |
|---|---|
| 框架 | Vue 3 |
| 构建 | Vite |
| 语言 | TypeScript |
| 路由 | Vue Router |
| 状态管理 | Pinia |
| 网络请求 | Axios |
| 样式 | SCSS、Design Token |
| 图标 | lucide-vue-next |
| 测试 | Vitest、Vue Testing Library |

## 3. 目录结构

```text
frontend/
├── Dockerfile
├── nginx.conf
├── package.json
├── vite.config.ts
└── src/
    ├── api/
    │   ├── http.ts              # webHttp/adminHttp 与响应解包
    │   └── modules/             # 各业务 API 封装
    ├── assets/                  # 前端静态资源
    ├── components/              # 公共组件
    │   └── ai/                  # AI 助手组件
    ├── composables/             # 组合式逻辑
    ├── layout/                  # 用户端/管理端布局
    ├── mocks/                   # 少量静态兜底数据
    ├── pages/
    │   ├── web/                 # 用户前台页面
    │   └── admin/               # 管理后台页面
    ├── router/                  # 路由与权限守卫
    ├── store/                   # 登录态、购物车、消息等状态
    ├── styles/                  # 全局样式与主题变量
    ├── types/                   # TypeScript 类型
    ├── utils/                   # 通用工具
    └── __tests__/               # 前端测试
```

历史文档中出现过的 `src/services/` 已不再作为当前 API 访问层；当前统一使用 `src/api/http.ts` 和 `src/api/modules/*.ts`。

## 4. 页面与功能范围

### 4.1 用户前台

| 模块 | 路由示例 | 说明 |
|---|---|---|
| 首页 | `/home` | 聚合 Banner、推荐内容、服务、商品、萌宠展示 |
| 社区 | `/community`、`/community/post/:id`、`/community/create` | 分类、搜索、话题、帖子详情、发布、互动 |
| 领养 | `/adoption` | 宠物筛选、详情弹窗、流程说明、申请、在线咨询 |
| 服务 | `/services`、`/services/merchant/:id`、`/services/checkout` | 服务分类、商家、预约、评价、咨询 |
| 商城 | `/shop`、`/shop/product/:id`、`/shop/cart`、`/shop/checkout` | 商品、购物车、地址、优惠券、订单 |
| 个人中心 | `/profile`、`/profile/orders`、`/profile/bookings`、`/profile/messages` | 资料、宠物、动态、收藏、订单、预约、消息 |
| 系统状态 | `/health` | 调用后端健康检查端点 |
| AI/客服 | 侧边停靠栏 | AI 宠医助手、在线客服会话 |

### 4.2 管理后台

| 模块 | 路由示例 | 说明 |
|---|---|---|
| 登录 | `/admin/login` | 管理员登录 |
| 仪表盘 | `/admin/dashboard` | 统计、待处理事项、最近操作 |
| 用户管理 | `/admin/users` | 用户查询、状态管理 |
| 内容管理 | `/admin/content` | 帖子、评论、Banner、标签、推荐位 |
| 领养管理 | `/admin/adoption` | 宠物维护、申请审核 |
| 服务管理 | `/admin/services` | 商家、服务项目、预约、评价 |
| 商城管理 | `/admin/shop` | 商品、订单处理 |
| 客服消息 | `/admin/support` | 用户咨询查看和回复 |
| 监控面板 | `/admin/monitoring` | 请求指标和路径统计 |

## 5. API 调用约定

统一入口：

- `frontend/src/api/http.ts`
  - `webHttp`：用户端接口，baseURL 为 `/api/v1`
  - `adminHttp`：管理端接口，baseURL 为 `/api/v1/admin`
  - `unwrap`：统一解包 `{ code, message, data }`

模块封装：

| 文件 | 说明 |
|---|---|
| `auth.ts` | 用户登录、注册、登出 |
| `admin-auth.ts` | 管理员登录、登出 |
| `home.ts` | 首页聚合数据 |
| `community.ts` | 社区帖子、评论、点赞、收藏 |
| `adoption.ts` | 领养宠物和申请 |
| `services.ts` | 服务商家、服务项目、预约 |
| `shop.ts` | 商品、购物车、地址、优惠券、订单 |
| `profile.ts` | 用户资料和个人中心概览 |
| `pet.ts` | 宠物档案和成长时间轴 |
| `messages.ts` | 消息中心和在线客服 |
| `ai.ts` | AI 宠医助手 |
| `files.ts` | 文件上传 |
| `admin.ts` | 管理后台业务接口 |

页面中应只调用模块函数，不直接写完整 URL。

## 6. 状态与权限

- 登录态由 Pinia 管理，并持久化 token 与用户信息。
- Axios 请求拦截器统一挂载 `Authorization`。
- 401 响应触发登录态清理或登录跳转。
- 用户端和管理端通过路由 meta 与角色判断隔离。
- 管理后台接口仍由后端 `ADMIN` 权限二次校验，不能只依赖前端路由守卫。

## 7. 视觉与交互要求

- 用户前台保持温暖、柔和、宠物服务场景化的视觉风格。
- 管理后台保持高信息密度、表格化、筛选优先的工作台风格。
- 用户端主要流程必须使用后端真实数据；Mock 只能作为空状态或接口失败时的明确兜底。
- 图片资源应与业务内容匹配，不使用泛化占位图作为正式展示内容。
- 表单提交后要刷新对应列表或详情，保证“下单、预约、申请、发布、回复”能在相关页面看到结果。

## 8. 本地运行

```bash
cd frontend
npm install
npm run dev
```

常用命令：

```bash
npm run build
npm run test
npm run test:coverage
```

本地默认访问：

- 用户前台：`http://localhost:8081/home`
- 管理后台：`http://localhost:8081/admin/login`

## 9. 联调重点

- 用户端主链路：登录、社区互动、领养申请、服务预约、商城下单、个人中心查看。
- 管理端主链路：审核、处理、上下架、回复、监控。
- 跨端同步：前台提交的数据应能在后台处理；后台处理后的状态和消息应回到前台。
- 文件上传：帖子图片、后台商品/宠物图片需要限制大小并展示清晰错误。
- 部署环境：Vercel 需要配置正确的 `VITE_API_BASE_URL`，后端需要允许当前前端域名 CORS。
