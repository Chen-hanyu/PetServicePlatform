# CI/CD 配置贡献说明

姓名：陈涵宇 学号：2320100624 角色：后端 日期：2026-04-29

## 完成的工作

### 工作流相关
- [x] 参与编写 / 审查 `.github/workflows/ci.yml` 中的 backend job
- [x] 配置 Codecov 覆盖率上传，后端 flag 为 `backend`
- [x] 添加 README 中的 CI 状态徽章

### 代码适配
- [x] 后端本地测试命令与 CI 一致：`./mvnw -B checkstyle:check test`
- [x] 后端代码通过 Checkstyle 检查，0 violations
- [x] 后端核心覆盖率达标，JaCoCo 行覆盖率约 87.85%，超过 60%
- [x] CI 测试环境使用 H2 in-memory 数据库，不依赖本地 MySQL

### 可选项
- [x] 配置 Dependabot 自动更新依赖
- [x] 集成 CodeRabbit AI 代码审查
- [ ] 使用 act 本地验证工作流

## PR 链接

- https://github.com/Chen-hanyu/PetServicePlatform/pull/33

## CI 运行链接

- 待 workflow 在 GitHub Actions 运行后补充

## 遇到的问题和解决

1. 问题：作业模板给的是 Python 后端示例，包含 `pytest`、`ruff`、`pyproject.toml`，但本项目后端实际技术栈是 Spring Boot 3 + Java 17 + Maven。
   解决：按项目文档和后端工程实际情况，使用 Maven、JUnit、H2、JaCoCo 和 Checkstyle 完成后端 CI 适配。
2. 问题：CI 环境没有本地 MySQL。
   解决：沿用 `src/test/resources/application-test.yml` 中的 H2 in-memory 测试库配置，集成测试不需要启动外部数据库服务。
3. 问题：新增 Checkstyle 后发现部分长行和一个未使用导入。
   解决：拆分长行并删除未使用导入，不改变业务逻辑。
4. 问题：可选项需要兼顾后端职责边界。
   解决：Dependabot 仅配置后端 Maven 依赖和 GitHub Actions 依赖更新；CodeRabbit 配置仅关注 `backend/**`、CI 配置和本次贡献说明相关文件，不扩展到前端任务。

## 心得体会

本次后端 CI/CD 配置的重点是根据项目真实技术栈调整作业模板。后端最终使用 Java 17 + Maven 执行静态检查、自动化测试和 JaCoCo 覆盖率生成，保证 CI 环境与本地验证命令一致。
