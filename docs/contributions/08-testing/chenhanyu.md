# 软件测试贡献说明

姓名：chenhanyu  
学号：2320100624  
角色：后端  
日期：2026-04-24

## 完成的测试工作

### 测试文件

- `backend/src/test/java/com/petplatform/service/AdminOpsServiceTest.java`
- `backend/src/test/java/com/petplatform/service/AdminAdoptionServiceTest.java`
- `backend/src/test/java/com/petplatform/service/AdminWorkflowServiceTest.java`
- `backend/src/test/java/com/petplatform/service/CommunityServiceTest.java`
- `backend/src/test/java/com/petplatform/service/PetServiceTest.java`
- `backend/src/test/java/com/petplatform/service/ServiceBookingServiceTest.java`
- `backend/src/test/java/com/petplatform/service/ShopServiceTest.java`
- `backend/src/test/java/com/petplatform/service/VerifyCodeServiceTest.java`
- `backend/src/test/java/com/petplatform/controller/*WebMvcTest.java`
- `backend/src/test/java/com/petplatform/integration/*IntegrationTest.java`

### 测试清单

- [x] 正常情况测试（约 130 个）：覆盖登录注册、首页搜索、社区、领养、宠物档案、服务预约、商城下单、管理端审核和运营配置等主流程
- [x] 边界 / 异常情况测试（约 65 个）：覆盖参数校验失败、未登录、无权限、资源不存在、重复提交、库存不足、预约冲突、审核状态冲突等场景
- [x] Mock 使用（数据库 / 外部依赖）：Service 单元测试使用 Mockito Mock Mapper；WebMvc 测试使用 `@MockitoBean` Mock Service / JWT 依赖；集成测试使用 H2 隔离真实 MySQL

### 覆盖率

- 核心模块覆盖率：85.62%（JaCoCo Instruction Coverage）
- 行覆盖率：87.69%
- 分支覆盖率：48.62%
- 覆盖率报告：`backend/target/site/jacoco/index.html`
- 覆盖率 XML：`backend/target/site/jacoco/jacoco.xml`

测试运行结果：

```text
Tests run: 195, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### AI 辅助（如有）

- 使用工具：ChatGPT / Codex
- Prompt 示例：

```text
请基于 Spring Boot 3 + MyBatis-Plus 项目的核心 Service，使用 JUnit 5 和 Mockito 编写后端单元测试。
要求 Mock 所有 Mapper，不访问真实数据库；覆盖分页查询、DTO 组装、状态流转、资源不存在、重复提交、权限校验和 BusinessException 的 ResultCode。
优先补齐低覆盖率的管理端 Service、社区、商城、预约、宠物档案和领养流程，使 JaCoCo 核心模块覆盖率超过 80%。
```

- AI 生成 + 人工修改的测试数量：约 25 个
- 人工修改过程：
  - 对齐项目已有 `@ExtendWith(MockitoExtension.class)`、AssertJ 和 Mockito 写法
  - 按现有 DTO、Entity、Mapper 方法名修正测试数据和断言
  - 使用 Mock Mapper 隔离数据库访问，验证核心业务分支
  - 补充 `BusinessException` 和 `ResultCode` 断言
  - 运行 `./mvnw test` 和 JaCoCo 报告确认覆盖率超过 80%

## PR 链接

- PR #X: 待创建 / 合并后补充

## 遇到的问题和解决

1. 问题：初次接入 JaCoCo 时会尝试插桩第三方 `JSqlParser`，出现 `MethodTooLargeException` 日志。  
   解决：在 `jacoco-maven-plugin` 中将插桩范围限定为 `com.petplatform.*`，只统计本项目后端代码。
2. 问题：全量覆盖率初始低于 80%，主要由多个 Service 的分页组装、状态流转和异常分支未覆盖导致。  
   解决：补充 `PetServiceTest`，扩展 `CommunityServiceTest`、`ServiceBookingServiceTest`、`ShopServiceTest`，并新增 `AdminWorkflowServiceTest` 覆盖管理端评价、评论、订单、社区、预约、用户和领养流程。
3. 问题：MyBatis-Plus `BaseMapper` 存在单条和批量方法重载，Mockito 使用裸 `any()` 时编译无法判断重载方法。  
   解决：改用 `ArgumentMatchers.any(Entity.class)` 和带类型的 `argThat`，明确 Mock 方法签名。

## 心得体会

这次测试工作让我更清楚地理解了后端测试分层：Service 单元测试适合用 Mock 隔离数据库并验证业务规则；MockMvc 适合验证接口响应、参数校验和权限边界；H2 集成测试适合验证真实 SQL 与主流程联动。相比只追求测试数量，围绕核心业务风险补正常、异常和边界场景，更能提升项目交付质量。
