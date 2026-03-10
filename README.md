# 宠友圈小程序项目说明

本仓库用于《版本控制环境搭建》作业，项目主题为宠物服务类小程序（功能全面版）。

## 成员与分工
- 前端负责人：ccchy（2492764608@qq.com）
- 后端负责人：待组员补充

## 项目简介
宠友圈小程序面向养宠用户，核心功能包含：
- 宠物档案管理（品种、年龄、免疫记录、体重曲线）
- 在线问诊与门店预约（疫苗、洗护、体检）
- 宠物用品商城（商品浏览、下单、订单追踪）
- 宠物社区（发帖、评论、点赞、收藏）
- 领养与寻宠信息发布

## 项目目录结构

```text
.
├── README.md
├── docs/
│   ├── frontend.md   # 前端模块文档（当前由我维护）
│   ├── backend.md    # 后端模块文档（联调用草案）
│   └── api.md        # API 设计文档（联调用草案）
├── frontend/         # 小程序前端代码
├── backend/          # 后端服务代码
└── .gitignore
```

## 分支策略
- `main`：稳定版本，保证可演示与可提交
- `develop`：日常集成分支
- `feature/ccchy-frontend-doc`：前端文档分支
- `feature/*`：其他功能分支（由各负责人创建）

## 协作流程
1. 从 `develop` 创建个人功能分支。
2. 在功能分支完成开发并提交。
3. 发起 PR 合并到 `develop`。
4. 阶段验收后由 `develop` 合并到 `main`。

## 前端运行方式
1. `cd frontend`
2. `npm install`
3. `npm run dev:weapp`
4. 使用微信开发者工具打开 `dist` 目录预览

## 后端运行方式（示例）
1. `cd backend`
2. `python -m venv .venv`
3. `pip install -r requirements.txt`
4. `uvicorn app.main:app --reload --port 8000`

## 作业验收对照
- 已建立规范目录结构与文档
- 已使用 `main/develop/feature` 分支模型
- 已保留提交记录用于截图与验收
