# 版本控制环境搭建作业仓库

本仓库用于完成《版本控制环境搭建》作业，目标是建立规范的 Git 协作流程与项目结构。

## 作者信息
- 姓名：ccchy
- 邮箱：2492764608@qq.com

## 项目目录结构

```text
.
├── README.md
├── docs/
│   ├── frontend.md
│   ├── backend.md
│   └── api.md
├── frontend/
├── backend/
└── .gitignore
```

## 分支策略
- `main`：稳定可交付版本
- `develop`：日常开发集成分支
- `feature/ccchy-frontend-doc`：前端文档开发分支
- `feature/ccchy-backend-doc`：后端/API 文档开发分支

## 协作流程
1. 从 `develop` 拉取并创建功能分支。
2. 在功能分支提交文档和代码。
3. 发起 PR 合并到 `develop`。
4. 阶段完成后再从 `develop` 合并到 `main`。

## 运行方式（示例）
- 前端：进入 `frontend/` 后按项目实际使用 `npm install`、`npm run dev`
- 后端：进入 `backend/` 后按项目实际使用 `pip install -r requirements.txt`、`python app.py`

## 作业验收对照
- 已包含规范项目结构
- 已包含 docs 文档
- 已采用 main/develop/feature 分支模型
- 已提供可用于截图的 Git 日志命令
