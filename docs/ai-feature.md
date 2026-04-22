# AI 功能说明

## 1. 功能概述

本期集成的 AI 功能为 **AI 宠医助手**。它面向用户前台提供宠物健康问答能力，支持用户围绕宠物症状、饮食、护理和行为习惯进行连续对话咨询。

该功能属于课程作业要求中的 **智能客服 / 基于上下文问答** 场景，当前采用轻量实现，不引入知识库、向量检索或复杂编排，优先满足 MVP 联调与演示需要。

## 2. 使用模型

- 默认模型：`deepseek-chat`
- 调用方式：OpenAI 兼容 Chat Completions API
- 默认 Base URL：`https://api.deepseek.com`
- 兼容方式：
  - 云端模型：DeepSeek、Qwen 等 OpenAI 兼容接口
  - 本地模型：可通过修改 `AI_BASE_URL` 和 `AI_MODEL` 适配 Ollama

当前后端默认按 DeepSeek 官方调用形式对齐：

```http
POST https://api.deepseek.com/chat/completions
Authorization: Bearer ${DEEPSEEK_API_KEY}
Content-Type: application/json
```

## 3. 实现内容

### 3.1 前端

- `frontend/src/api/modules/ai.ts`
  - 封装 `POST /api/v1/ai/chat`
- `frontend/src/components/ai/AIPetDoctorChat.vue`
  - 对话抽屉、快捷问题、消息流展示
- `frontend/src/components/ai/AIPetDoctorDock.vue`
  - AI 功能入口
- `frontend/src/layout/WebLayout.vue`
  - 集成到用户端主布局

### 3.2 后端

- `backend/src/main/java/com/petplatform/controller/AiController.java`
  - 暴露 `POST /api/v1/ai/chat`
- `backend/src/main/java/com/petplatform/service/AiService.java`
  - 封装 DeepSeek/OpenAI 兼容接口调用
  - 追加系统提示词
  - 保留最近上下文消息
  - 生成补充建议 `suggestions`
- `backend/src/main/java/com/petplatform/config/AiConfig.java`
  - 创建 AI 专用 `RestClient`
- `backend/src/main/java/com/petplatform/config/AiProperties.java`
  - 读取 `ai.*` 配置
- `backend/src/main/java/com/petplatform/dto/ai/*`
  - 定义请求与响应 DTO

### 3.3 接口契约

接口路径：`POST /api/v1/ai/chat`

请求示例：

```json
{
  "messages": [
    { "role": "user", "content": "我家猫咪一直打喷嚏是怎么回事？" }
  ]
}
```

响应示例：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "reply": "先观察猫咪是否还伴随流鼻涕、食欲下降或精神差，如果持续超过 24 小时，建议尽快就医。",
    "suggestions": [
      "观察症状持续时间",
      "留意是否有食欲下降",
      "持续加重时尽快就医"
    ]
  }
}
```

错误处理：

- 未配置 `AI_API_KEY` / `DEEPSEEK_API_KEY` 时，返回统一错误结构，错误码为 `10011`
- 上游 AI 服务不可用或调用失败时，返回统一错误结构，错误码为 `10012`
- 请求体为空、消息格式非法时，走现有参数校验与全局异常处理

## 4. 提示词设计

后端会在用户消息前追加系统提示词，约束模型扮演“专业、温和的宠物健康顾问”，并补充以下规则：

- 回答要清晰、易懂、可执行
- 不编造医学检查结果或诊断结论
- 遇到持续呕吐、便血、呼吸困难、高烧、抽搐、误食有毒物等严重情况时，明确建议尽快前往正规宠物医院

## 5. 配置说明

`backend/src/main/resources/application.yml` 中新增：

```yaml
ai:
  api-key: ${AI_API_KEY:${DEEPSEEK_API_KEY:}}
  base-url: ${AI_BASE_URL:https://api.deepseek.com}
  model: ${AI_MODEL:deepseek-chat}
  system-prompt: ${AI_SYSTEM_PROMPT:...}
```

说明：

- API Key 仅通过环境变量提供，不提交到代码仓库
- 本地开发可将密钥放在项目根目录 `.env`，Spring Boot 会自动尝试读取根目录 `.env` 与 `backend/.env`
- `docker compose` 默认更适合使用项目根目录 `.env`
- 若切换到 Ollama，可将 `AI_BASE_URL` 改为本地地址，并把 `AI_MODEL` 改为对应模型名

## 6. 测试情况

已补充后端 WebMvc 测试：

- 匿名访问 `POST /api/v1/ai/chat`
- 请求体参数校验
- AI 未配置时的统一错误返回

## 7. 后续优化方向

- 持久化会话历史
- 支持图片问诊和 OCR
- 接入更细的宠物知识库或 FAQ
- 增加免责声明与风险分级提示
