# 前端模块说明

## 1. 模块功能
- 实现用户界面展示与交互逻辑。
- 调用后端接口完成数据读取、提交与状态更新。
- 提供基础页面路由与错误提示。

## 2. 技术选型
- 框架：Vue 3（或 React，按课程项目最终实现）
- 构建工具：Vite
- 样式方案：CSS Modules / SCSS
- 请求库：Axios

## 3. 目录结构

```text
frontend/
├── src/
│   ├── pages/        # 页面级组件
│   ├── components/   # 通用组件
│   ├── services/     # API 调用封装
│   ├── router/       # 路由配置
│   └── styles/       # 全局样式
├── public/
└── README.md
```

## 4. 运行方式
1. 进入目录：`cd frontend`
2. 安装依赖：`npm install`
3. 开发启动：`npm run dev`
4. 生产构建：`npm run build`

## 5. 与后端联调约定
- 接口前缀：`/api`
- 统一响应格式：`{ code, message, data }`
- 开发环境通过代理转发到后端服务端口。
