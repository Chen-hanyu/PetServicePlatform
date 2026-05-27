import { createApp } from "vue";
import { createPinia } from "pinia";
import App from "./App.vue";
import router from "./router";
import "./styles/global.scss";
import { useThemeStore } from "./store/theme";
import { apiLogger } from "./utils/apiLogger";
import { setupAllInterceptors } from "./utils/apiInterceptor";
import { webHttp, adminHttp } from "./api/http";

// 初始化前端结构化日志系统
apiLogger.addLog({
  method: "INFO" as any,
  url: "",
  path: "",
  duration: 0,
  isExpand: false
});
apiLogger.getLogs().length = 0; // 清除初始化日志

// 安装 API 拦截器（自动捕获请求/响应并输出结构化日志）
setupAllInterceptors({ webHttp, adminHttp });

const app = createApp(App);
app.use(createPinia());
app.use(router);

// Initialize theme
const theme = useThemeStore();
theme.apply();

app.mount("#app");


