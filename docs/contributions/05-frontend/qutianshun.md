# 前端开发贡献说明

**姓名：** 屈天顺  
**学号：** 2323040522
**技术栈：** Vue 3 + Vite + TypeScript + Vue Router + Pinia + Axios + SCSS  
**日期：** 2026-04-02  

## 我完成的工作

### 页面开发

- [x] 登录/注册页面（与全站样式、路由衔接，按项目既有结构维护）
- [x] 首页/列表页面（首页信息架构与展示优化；社区列表为瀑布/卡片网格布局）
- [x] 详情页面（社区帖子详情、领养等页面与统一配色一致）
- [x] 个人中心（个人资料、消息、订单等相关前台页面样式与交互）
- [x] 其他：宠物社区页（顶栏搜索、分类 Tab、内容卡片、侧栏热门话题与关注列表）；全站暖色系主题（`tokens.scss` 与多页样式统一，去除绿色主色）

### 组件/模块封装

- **WebFooter.vue**：站点页脚品牌区、多列链接、二维码占位与响应式布局。
- **DataState**：社区等内容区加载/空态/错误态复用展示。
- **全局设计 Token**：在 `tokens.scss` 中集中维护背景、主色、边框、Hero 渐变等变量，便于全站换肤与与商城页视觉对齐。

### API 对接

- [x] 封装网络请求层（使用项目既有 `@/api/http` 与 `@/api/modules/community` 等模块）
- [x] 社区列表：`fetchPosts` 请求失败或返回空列表时，回退到 `mocks/community.ts` 中的本地数据，保证离线/后端未启动时仍可演示

## PR 链接

- PR #X：https://github.com/xxx/xxx/pull/X（请提交后替换为实际仓库与 PR 号）

## 遇到的问题和解决

1. 问题：合并 `feature/qts-frontend-develop` 到 `develop` 时，`CommunityPage.vue` 残留 Git 冲突标记（`=======`、`>>>>>>>`），导致 Vue 模板解析报错「缺少结束标签」。  
   解决：删除冲突片段，保留一套完整的社区页模板结构，并重新检查 `</template>` 内标签成对闭合。

2. 问题：社区页报错无法解析 `@/services/modules/community`，与项目实际目录 `@/api/modules/community` 不一致。  
   解决：将 `import` 改为 `@/api/modules/community`，与 `AGENTS.md` 中「请求封装在 services/api 约定」及仓库现有结构对齐。

3. 问题：后端未启动时接口代理失败，社区列表为空；模板使用 `post.cover` 而 Mock 与类型为 `cover_url`，封面不显示。  
   解决：补充 `mockPosts` 导入与 `mockListForTab()`，在请求失败或列表为空时使用 Mock；模板改为绑定 `post.cover_url`；按 Tab（推荐/晒宠/问答等）过滤 Mock；对 `activeCategory`、`currentPage` 增加 `watch` 重新加载。

4. 问题：全站多处硬编码薄荷绿，与「宠物商城」参考的暖色（珊瑚橙、米杏色）不一致。  
   解决：在 `tokens.scss` 中统一主色与渐变，并将各页 `rgba(126,207,188…)`、`#7ECFBC` 等替换为 Token 或 `rgba(255,155,122,…)` 等暖色；领养页等处错误替换的 `linear-gradient` 修正为 `var(--hero-gradient)`。

## 心得体会

在前端协作中，优先把「设计 Token + 页面引用变量」做扎实，后期换主题成本会低很多；合并分支后务必全文搜索冲突标记并本地 `npm run dev` 过一遍关键路由。Mock 数据不仅要「有」，还要与类型字段（如 `cover_url`）和 UI 绑定一致，并在接口不可用时有明确降级策略，演示与联调都会更顺畅。宠物类 C 端产品用暖色、大圆角和清晰的信息层级，能更好传达友好、可信赖的视觉感受。
