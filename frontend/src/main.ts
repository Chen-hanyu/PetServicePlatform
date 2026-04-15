import { createApp } from "vue";
import { createPinia } from "pinia";
import App from "./App.vue";
import router from "./router";
import "./styles/global.scss";
import { useThemeStore } from "./store/theme";

const app = createApp(App);
app.use(createPinia());
app.use(router);

// Initialize theme
const theme = useThemeStore();
theme.apply();

app.mount("#app");
