# AI 功能说明

## 1. 功能概述

**AI 宠医助手** - 基于 DeepSeek 大语言模型的智能宠物健康咨询助手。

### 功能特点

- **智能问答**：基于宠物医疗知识的智能问答
- **多场景覆盖**：支持宠物健康、习性、饮食、护理等多种咨询场景
- **快捷问题**：提供常见问题快捷入口，提升用户体验
- **实时对话**：支持连续对话，保持上下文理解

### 适用场景

| 场景 | 示例问题 |
|------|----------|
| 健康咨询 | "我家猫咪一直打喷嚏是怎么回事？" |
| 习性问答 | "狗狗半夜叫是什么原因？" |
| 饮食建议 | "3个月的小狗适合吃什么狗粮？" |
| 日常护理 | "如何给猫咪清理耳螨？" |

---

## 2. 技术实现

### 使用模型

- **模型名称**：DeepSeek Chat
- **API 接口**：DeepSeek API
- **Base URL**：`https://api.deepseek.com/v1`

### 前端实现

| 文件 | 说明 |
|------|------|
| `frontend/src/types/ai.ts` | AI 相关类型定义 |
| `frontend/src/api/modules/ai.ts` | AI API 调用封装 |
| `frontend/src/components/ai/AIPetDoctorChat.vue` | AI 对话组件 |
| `frontend/src/components/ai/AIPetDoctorDock.vue` | AI 浮动按钮组件 |
| `frontend/src/layout/WebLayout.vue` | 集成到主布局 |

### 后端接口（待实现）

**接口路径**：`POST /api/v1/ai/chat`

**请求格式**：
```json
{
  "messages": [
    { "role": "user", "content": "我家猫咪一直打喷嚏是怎么回事？" }
  ]
}
```

**响应格式**：
```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "reply": "猫咪打喷嚏可能由以下原因造成...",
    "suggestions": ["建议观察猫咪是否有其他症状"]
  }
}
```

### 提示词设计

AI 助手以宠物医生的角色回答问题，系统提示词设计如下：

```
你是一个专业的宠物健康顾问。请根据用户的问题，提供专业、温暖的宠物健康建议。
如果问题涉及严重症状，请建议用户及时就医。
回答要简洁、易懂，适合普通宠物主人理解。
```

---

## 3. 配置说明

### 环境变量

在 `backend/src/main/resources/application.yml` 或 `.env` 中配置：

```yaml
ai:
  api-key: ${AI_API_KEY:your-api-key}
  base-url: https://api.deepseek.com/v1
  model: deepseek-chat
```

### API Key 管理

- API Key 存储在环境变量中，不提交到代码仓库
- 建议使用 `.env` 文件管理本地开发密钥
- 生产环境使用服务器环境变量

---

## 4. 界面设计

### AI 助手入口

- 位于页面右侧工具栏底部，紫色渐变按钮
- 带有 "NEW" 角标提示新功能

### 对话窗口

- 右侧滑出式抽屉设计
- 宽度 400px，移动端全屏
- 顶部紫色渐变标题栏
- 欢迎页展示功能介绍和快捷问题
- 消息气泡区分用户和 AI

### 样式规范

- 主题色：紫色渐变 (`#667eea` → `#764ba2`)
- 字体：系统默认字体
- 圆角：18px 大圆角设计
- 动画：300ms 滑入滑出

---

## 5. 后续优化方向

- [ ] 支持图片上传（拍照问诊）
- [ ] 支持语音输入
- [ ] 保存对话历史
- [ ] 接入宠物医疗知识库
- [ ] 添加免责声明

---

## 6. 相关文档

- [前端 API 文档](./frontend-api.md)
- [后端 API 文档](./api.md)
