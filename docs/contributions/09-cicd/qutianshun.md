# CI/CD 配置贡献说明

姓名：qutianshun 
学号：2323040522  
角色：前端  
日期：2026-04-29

## 完成的工作

### 工作流相关
- [x] 参与编写 `.github/workflows/ci.yml`（负责前端 job，包含依赖安装、ESLint、测试与覆盖率上传）
- [x] 配置 Codecov 前端覆盖率上传（`frontend` flag）
- [x] 在 README.md 顶部添加 CI 徽章及覆盖率徽章

### 代码适配
- [x] 本地测试命令与 CI 一致：`npm test` 与 `npm run test:coverage` 均通过
- [x] 配置 ESLint（`.eslintrc.cjs`），关闭冗余格式规则，使本地 `npm run lint` 零警告通过
- [x] 修复 `HomePage.vue` 中 `banners` 变量未定义的错误
- [x] 核心模块测试覆盖率 > 60%（整体 67.28%，核心组件 100%）

## 前端 CI 关键配置
- 工作流触发分支：`main`, `develop`
- Node.js 版本：20，使用 npm ci 快速安装依赖
- 执行步骤：`npm ci` → `npm run lint` → `npm run test:coverage` → 上传 `lcov.info` 到 Codecov
- 命令示例：
  ```yaml
  - name: Install frontend dependencies
    run: npm ci --prefix frontend
  - name: Run ESLint
    run: npm run lint --prefix frontend
  - name: Run tests with coverage
    run: npm run test:coverage --prefix frontend

## CI 运行链接
https://github.com/Chen-hanyu/PetServicePlatform/actions

## 遇到的问题和解决
#### ESLint 9 兼容性问题
现象：eslint-plugin-vue@9 不支持 ESLint 9，导致大量警告。
解决：降级到 ESLint 8，使用 .eslintrc.cjs 并关闭格式相关规则（vue/max-attributes-per-line、vue/html-self-closing 等），仅保留关键质量规则。
#### CI 中 Vitest 报错 "Unknown option --ci"
现象：在 CI 中直接传递 --ci 参数导致 Vitest 无法识别。
解决：改用 package.json 中已定义的 test:coverage 脚本，该脚本已包含 vitest --coverage，CI 环境中 Vitest 自动退出 watch 模式。
#### HomePage.vue 中 banners is not defined 错误
现象：ESLint 报 no-undef 错误，页面也会运行失败。
解决：在 <script setup> 中添加 const banners = ref<HomeBanner[]>([]); 定义。
toBeInTheDocument 匹配器缺失
现象：测试中使用了 expect(...).toBeInTheDocument()，但在 Vitest 中默认不可用。
解决：改为 expect(...).toBeTruthy()，利用 getByText 等查询方法本身会抛出错误来断言元素存在。

## 心得体会
通过配置 CI 工作流，每次提交都会自动运行 lint 和测试，可以及早发现代码风格问题和功能回归，大大提高了协作效率。
Codecov 徽章能直观反映测试覆盖率变化，激励团队保持或提高测试质量。
合理配置 ESLint（关闭过于严格的格式规则）使项目既保持代码质量，又不因格式化细节阻碍 CI 通过。
本次实践让我熟悉了 GitHub Actions 的基本语法、npm ci 的使用场景以及 Vitest 在 CI 中的运行方式。