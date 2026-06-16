import { beforeEach, describe, expect, it } from "vitest";
import { createPinia, setActivePinia } from "pinia";
import { useAuthStore } from "@/store/auth";
import type { UserProfile } from "@/types/auth";

const user: UserProfile = {
  id: 1,
  phone: "13800000001",
  nickname: "Tester",
  avatar_url: "/avatar.png",
  role: "USER"
};

describe("auth store", () => {
  beforeEach(() => {
    localStorage.clear();
    setActivePinia(createPinia());
  });

  it("starts empty when local storage has no session", () => {
    const store = useAuthStore();

    expect(store.token).toBe("");
    expect(store.user).toBeNull();
    expect(store.isLoggedIn).toBe(false);
    expect(store.role).toBe("");
  });

  it("hydrates session from local storage", () => {
    localStorage.setItem("pet_platform_token", "token-from-storage");
    localStorage.setItem("pet_platform_user", JSON.stringify(user));
    setActivePinia(createPinia());

    const store = useAuthStore();

    expect(store.token).toBe("token-from-storage");
    expect(store.user?.nickname).toBe("Tester");
    expect(store.isLoggedIn).toBe(true);
    expect(store.role).toBe("USER");
  });

  it("sets and clears session", () => {
    const store = useAuthStore();

    store.setSession("token-1", user);
    expect(store.token).toBe("token-1");
    expect(localStorage.getItem("pet_platform_token")).toBe("token-1");
    expect(JSON.parse(localStorage.getItem("pet_platform_user") || "{}").phone).toBe("13800000001");

    store.logout();
    expect(store.token).toBe("");
    expect(store.user).toBeNull();
    expect(localStorage.getItem("pet_platform_token")).toBeNull();
    expect(localStorage.getItem("pet_platform_user")).toBeNull();
  });
});
