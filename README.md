[![CI](https://github.com/Chen-hanyu/PetServicePlatform/actions/workflows/ci.yml/badge.svg)](https://github.com/Chen-hanyu/PetServicePlatform/actions)
[![Backend Coverage](https://codecov.io/gh/Chen-hanyu/PetServicePlatform/branch/main/graph/badge.svg?flag=backend)](https://codecov.io/gh/Chen-hanyu/PetServicePlatform)
[![Frontend Coverage](https://codecov.io/gh/Chen-hanyu/PetServicePlatform/branch/main/graph/badge.svg?flag=frontend)](https://codecov.io/gh/Chen-hanyu/PetServicePlatform)

# 宠物之家：宠物综合服务平台

本项目是一个“用户 Web 前台 + 管理员后台 + Spring Boot 后端”的宠物综合服务平台。用户端覆盖社区、领养、服务预约、商城、宠物档案、消息通知和 AI 宠医助手；管理端覆盖用户、内容、领养、服务、商城、客服、监控等运营能力。

## 团队成员

| 姓名 | 学号 | 主要分工 |
|---|---|---|
| qutianshun | 2323040522 | 前端、用户端与管理端页面、前端测试、Vercel 部署 |
| chenhanyu | 2320100624 | 后端、数据库、接口、安全、测试、Railway 部署 |

每周个人贡献记录位于 `docs/contributions/`，按阶段和成员姓名归档。

## 在线与设计

- Figma：https://www.figma.com/design/gu5MKdueh9c10smfcXSQV0/%E5%AE%A0%E7%89%A9%E9%A1%B5%E9%9D%A2?node-id=0-1&t=6aHvhBAU7GApTcti-1
- 前端部署：`https://pet-service-platform-7shx.vercel.app/`
- 后端部署：`https://petserviceplatform-production.up.railway.app`
- 健康检查：`https://petserviceplatform-production.up.railway.app/health`

## 最终提交材料

- [最终开发文档](docs/project-report.md)
- [老师文档模板](docs/文档.md)
- [期末答辩 PPT](docs/ppt/宠物之家-期末.pptx)
- [中期验收 PPT](docs/ppt/宠物之家-中期.pptx)
- [功能演示视频](docs/video/运行视频.mp4)

## 演示账号

普通用户：

- 手机号：`13800000001`
- 密码：`123456`

管理员：

- 手机号：`13900000000`
- 密码：`admin123`

## 技术栈

| 层级 | 技术 |
|---|---|
| 前端 | Vue 3、Vite、TypeScript、Vue Router、Pinia、Axios、SCSS |
| 后端 | Java 17、Spring Boot 3、Spring MVC、Spring Security、JWT、MyBatis-Plus |
| 数据库 | MySQL |
| 测试 | Vitest、Testing Library、JUnit 5、Mockito、Spring Boot Test、JaCoCo |
| CI/CD | GitHub Actions、Codecov、Docker、Vercel、Railway |
| AI | DeepSeek / OpenAI 兼容 Chat Completions API |

## 目录结构

```text
.
├── CODEX.md                        # Codex 协作与项目规则
├── README.md
├── frontend/                       # Vue 用户前台 + 管理员后台
├── backend/                        # Spring Boot 后端
├── docs/
│   ├── design/                     # 设计稿与页面截图
│   ├── contributions/              # 每周个人贡献记录
│   ├── ppt/                        # 中期与期末答辩 PPT
│   ├── video/                      # 功能演示视频
│   ├── project-report.md           # 最终开发文档
│   ├── 文档.md                     # 老师文档模板
│   ├── architecture.md
│   ├── frontend.md
│   ├── backend.md
│   ├── database.md
│   ├── api.md
│   ├── api.yaml
│   ├── integration.md
│   ├── design-spec.md
│   ├── ai-feature.md
│   ├── security-review.md
│   ├── deployment.md
│   └── monitoring.md
├── compose.yaml                    # Docker 开发环境
├── compose.prod.yaml               # Docker 生产环境
├── docker-compose.yml              # 兼容启动配置
├── railway.toml
└── vercel.json
```

## 核心功能

- 用户认证：手机号密码登录、验证码调试入口、JWT 鉴权。
- 社区：帖子列表、分类/标签/关键词搜索、发帖、评论、点赞、收藏、个人动态。
- 领养：宠物浏览、领养流程、申请提交、申请记录、后台审核。
- 服务：商家分类、服务预约、预约记录、后台预约处理。
- 商城：商品搜索、购物车、收货地址、优惠券、下单、支付模拟、订单流转。
- 宠物档案：宠物资料、疫苗、体重、相册、成长时间轴。
- 消息与客服：系统消息、在线客服咨询、后台客服回复。
- AI 助手：宠物健康问答、宠物档案上下文选择、快捷咨询。
- 管理后台：仪表盘、用户、内容、领养、服务、商城、客服、监控。

## 本地运行

### 前端

```bash
cd frontend
npm install
npm run dev
```

默认访问 `http://localhost:5173`。

### 后端

```bash
cd backend
./mvnw spring-boot:run
```

默认访问：

- API：`http://localhost:8080/api/v1`
- Swagger：`http://localhost:8080/swagger-ui.html`
- 健康检查：`http://localhost:8080/health`

### Docker

```bash
docker compose up -d --build
```

生产配置：

```bash
docker compose -f compose.prod.yaml up -d --build
```

## 测试与覆盖率

```bash
# 后端
cd backend
./mvnw test

# 前端
cd frontend
npm run test:coverage
```

最近本地覆盖率：

| 端 | 主要覆盖率 |
|---|---|
| 前端 | Statements 84.37%，Lines 85.02% |
| 后端 | Instructions 75.46%，Lines 76.84% |

## 核心文档

- [docs/project-report.md](docs/project-report.md)：最终开发文档
- [docs/文档.md](docs/文档.md)：老师给定文档模板
- [docs/design-spec.md](docs/design-spec.md)：用户端 UI/UX 设计说明
- [docs/architecture.md](docs/architecture.md)：软件架构设计
- [docs/frontend.md](docs/frontend.md)：前端实现说明
- [docs/backend.md](docs/backend.md)：后端实现说明
- [docs/database.md](docs/database.md)：数据库设计与 ER 关系
- [docs/api.md](docs/api.md)：API 使用说明
- [docs/api.yaml](docs/api.yaml)：OpenAPI 规范
- [docs/integration.md](docs/integration.md)：前后端联调记录
- [docs/ai-feature.md](docs/ai-feature.md)：AI 功能说明
- [docs/security-review.md](docs/security-review.md)：安全审查记录
- [docs/deployment.md](docs/deployment.md)：云服务部署说明
- [docs/monitoring.md](docs/monitoring.md)：监控与健康检查说明

## 部署说明

- 前端部署在 Vercel，配置文件为 `vercel.json`。
- 后端部署在 Railway，配置文件为 `railway.toml`。
- 数据库使用 MySQL，表结构和示例数据位于 `backend/src/main/resources/sql/`。
- 生产环境变量模板见 `.env.example`。

详细步骤见 [docs/deployment.md](docs/deployment.md)。
