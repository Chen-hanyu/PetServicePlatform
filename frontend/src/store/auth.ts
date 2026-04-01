import { defineStore } from "pinia";
import type { UserProfile } from "@/types/auth";

interface AuthState {
  token: string;
  user: UserProfile | null;
}

const TOKEN_KEY = "pet_platform_token";
const USER_KEY = "pet_platform_user";

export const useAuthStore = defineStore("auth", {
  state: (): AuthState => ({
    token: localStorage.getItem(TOKEN_KEY) || "",
    user: (() => {
      const raw = localStorage.getItem(USER_KEY);
      return raw ? (JSON.parse(raw) as UserProfile) : null;
    })()
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.token),
    role: (state) => state.user?.role || ""
  },
  actions: {
    setSession(token: string, user: UserProfile) {
      this.token = token;
      this.user = user;
      localStorage.setItem(TOKEN_KEY, token);
      localStorage.setItem(USER_KEY, JSON.stringify(user));
    },
    logout() {
      this.token = "";
      this.user = null;
      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem(USER_KEY);
    }
  }
});
