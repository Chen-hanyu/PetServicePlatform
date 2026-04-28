# 软件测试贡献说明

姓名：qutianshun
学号：2323040522
角色：前端  
日期：2026-04-28

## 完成的测试工作

### 测试文件
- `frontend/src/__tests__/smoke.test.ts`
- `frontend/src/__tests__/StatusBadge.test.ts`
- `frontend/src/__tests__/DataState.test.ts`
- `frontend/src/__tests__/WebFooter.test.ts`
- `frontend/src/__tests__/AIPetDoctorChat.test.ts`

### 测试清单
- [x] 正常情况测试（10 个）
- [x] 边界/异常情况测试（4 个）
- [x] Mock 使用（API 请求模拟，含成功/失败/空输入场景）

测试总计：**组件渲染/交互测试 9 个（≥8） + Mock API 测试 4 个（≥4） = 14 个测试**，全部通过。

## 覆盖率
- **整体覆盖率**：语句 67.28%，分支 58.1%，函数 50%，行 66.99%
- **核心组件模块**：
  - `components/`：100% 语句覆盖率 ✅
  - `components/ai/`（AI宠医助手）：语句 88.46%，分支 84.84% ✅
- 符合作业要求（核心模块覆盖率 > 50%）。


## 遇到的问题和解决
1. Vitest 环境配置时报 CSS 解析错误 → 改用 `happy-dom` 环境。
2. `toBeInTheDocument` 和 `toBeDisabled` 匹配器缺失 → 改用 `toBeTruthy()` 和 `disabled` 属性检查。
3. 发送按钮无文字标签 → 使用 `getByRole("button", { name: "" })` 匹配。

## 心得体会
- 通过 Mock API 可以独立测试组件，不依赖后端。
- 使用 Testing Library 更关注用户行为而非实现细节。
- 覆盖率报告能直观看到未测试的代码分支。