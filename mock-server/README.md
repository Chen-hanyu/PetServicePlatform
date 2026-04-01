# Mock API（本地假后端）

与前端 `vite.config.ts` 里的代理一致：请求 `/api` 会转发到 `http://127.0.0.1:8080`。

## 使用

1. 在本目录执行：`npm start`（或 `node server.mjs`）
2. 再启动前端：`frontend` 目录下 `npm run dev`
3. 浏览器 Network 里应看到 **200** 且 **Response** 有 JSON（`code: 0`）

默认端口 **8080**，可改环境变量：`set MOCK_API_PORT=9000` 后再启动（Windows）。
