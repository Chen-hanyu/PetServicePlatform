import { defineStore } from "pinia";

interface ThemeState {
  isDark: boolean;
}

const THEME_KEY = "pet_platform_theme";

export const useThemeStore = defineStore("theme", {
  state: (): ThemeState => ({
    isDark: localStorage.getItem(THEME_KEY) === "dark"
  }),
  actions: {
    toggle() {
      this.isDark = !this.isDark;
      localStorage.setItem(THEME_KEY, this.isDark ? "dark" : "light");
      this.apply();
    },
    apply() {
      document.documentElement.setAttribute(
        "data-theme",
        this.isDark ? "dark" : "light"
      );
    }
  }
});
