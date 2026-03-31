# Frontend Code Directory

当前目录用于宠物综合服务平台的 Web 前端代码，包含用户前台和管理员后台两套界面。

## 建议结构

```text
frontend/
├── src/
│   ├── assets/
│   ├── components/
│   ├── layout/
│   ├── pages/
│   │   ├── web/
│   │   └── admin/
│   ├── router/
│   ├── store/
│   ├── mocks/
│   ├── services/
│   ├── styles/
│   ├── utils/
│   └── types/
├── public/
├── vite.config.ts
└── README.md
```

## 本地开发
1. `npm install`
2. `npm run dev`
3. 访问本地开发地址进行前台和后台联调

## 开发说明
- `pages/web`：普通用户访问页面
- `pages/admin`：管理员后台页面
- `mocks`：前端静态复现或联调前阶段的本地模拟数据
- 前后台共用请求层、权限模型和设计 Token
