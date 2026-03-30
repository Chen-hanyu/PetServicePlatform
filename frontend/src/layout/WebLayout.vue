<template>
  <div>
    <header class="web-header">
      <div class="brand-wrap">
        <div class="brand-dot"></div>
        <div class="brand">宠物之家</div>
      </div>
      <nav>
        <RouterLink v-for="item in links" :key="item.path" :to="item.path" class="link">{{ item.name }}</RouterLink>
      </nav>
      <div class="auth-actions" v-if="auth.isLoggedIn">
        <div
          class="user-menu"
          @mouseenter="onUserMenuEnter"
          @mouseleave="onUserMenuLeave"
          @pointerenter="onUserMenuEnter"
        >
          <div
            class="user-pill"
            role="link"
            tabindex="0"
            title="个人信息"
            @click="goProfile"
            @keydown.enter.prevent="goProfile"
            @keydown.space.prevent="goProfile"
          >
            <span class="user-pill-avatar" aria-hidden="true">
              <img v-if="auth.user?.avatar_url" :src="auth.user.avatar_url" alt="" class="user-pill-img" />
              <template v-else>{{ userInitial }}</template>
            </span>
            <span class="user-pill-name">{{ auth.user?.nickname }}</span>
          </div>
          <div v-show="userMenuOpen" class="user-dropdown-wrap">
            <div class="user-dropdown" role="menu" aria-label="个人中心快捷入口">
              <button
                v-for="item in profileQuickLinks"
                :key="item.label"
                type="button"
                class="dropdown-item"
                role="menuitem"
                @click="goProfileSection(item.tab)"
              >
                {{ item.label }}
              </button>
            </div>
          </div>
        </div>
        <button type="button" class="logout-circle" aria-label="退出登录" title="退出登录" @click="logout">
          <svg class="logout-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
            <path
              d="M10 7V5a2 2 0 0 1 2-2h7a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-7a2 2 0 0 1-2-2v-2M15 12H3m0 0 3-3m-3 3 3 3"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
        </button>
      </div>
      <div class="auth-actions" v-else>
        <RouterLink class="btn btn-secondary" to="/login">登录</RouterLink>
        <RouterLink class="btn btn-primary" to="/register">注册</RouterLink>
      </div>
    </header>
    <main class="page-shell">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, ref } from "vue";
import { useAuthStore } from "@/store/auth";
import { useRouter } from "vue-router";

const auth = useAuthStore();
const router = useRouter();

type ProfileSectionTab = "pets" | "orders" | "bookings" | "settings";

const userMenuOpen = ref(false);
let menuLeaveTimer: ReturnType<typeof setTimeout> | undefined;

const clearMenuLeaveTimer = () => {
  if (menuLeaveTimer !== undefined) {
    clearTimeout(menuLeaveTimer);
    menuLeaveTimer = undefined;
  }
};

const onUserMenuEnter = () => {
  clearMenuLeaveTimer();
  userMenuOpen.value = true;
};

const onUserMenuLeave = () => {
  clearMenuLeaveTimer();
  menuLeaveTimer = window.setTimeout(() => {
    userMenuOpen.value = false;
    menuLeaveTimer = undefined;
  }, 180);
};

onBeforeUnmount(() => clearMenuLeaveTimer());

const goProfile = () => {
  userMenuOpen.value = false;
  void router.push("/profile");
};

const goProfileSection = (tab?: ProfileSectionTab) => {
  userMenuOpen.value = false;
  const routes: Record<ProfileSectionTab, string> = {
    pets: "/profile/pets",
    orders: "/profile/orders",
    bookings: "/profile/bookings",
    settings: "/profile/settings"
  };
  if (!tab) {
    void router.push("/profile");
    return;
  }
  void router.push(routes[tab] ?? "/profile");
};

const userInitial = computed(() => {
  const n = auth.user?.nickname?.trim();
  if (!n) return "?";
  return n.charAt(0).toUpperCase();
});

const links = [
  { name: "首页", path: "/home" },
  { name: "社区", path: "/community" },
  { name: "领养", path: "/adoption" },
  { name: "服务", path: "/services" },
  { name: "商城", path: "/shop" },
  { name: "我的", path: "/profile" }
];

const profileQuickLinks: { label: string; tab?: ProfileSectionTab }[] = [
  { label: "个人信息" },
  { label: "我的宠物", tab: "pets" },
  { label: "我的订单", tab: "orders" },
  { label: "我的预约", tab: "bookings" },
  { label: "账户设置", tab: "settings" }
];

const logout = () => {
  auth.logout();
  router.push("/home");
};
</script>

<style scoped lang="scss">
.web-header {
  position: sticky;
  top: 0;
  z-index: 50;
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: 16px;
  padding: 14px 20px;
  background: rgba(255, 248, 241, 0.9);
  border-bottom: 1px solid #f2dbc6;
  backdrop-filter: blur(10px);
}

.brand-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
}

.brand-dot {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ffb089, #f17c53);
}

.brand {
  font-weight: 900;
  color: #8d4d30;
  letter-spacing: 0.3px;
}

nav {
  display: flex;
  justify-content: center;
  gap: 10px;
  overflow-x: auto;
  min-width: 0;
}

.link {
  padding: 8px 12px;
  border-radius: 999px;
  white-space: nowrap;
  color: #7b5c4d;
  border: 1px solid transparent;
}

.link.router-link-active {
  background: #ffe9d7;
  color: #8d4d30;
  border-color: #ffd4b7;
}

.auth-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  position: relative;
  z-index: 5;
  justify-self: end;
}

.user-menu {
  position: relative;
  z-index: 10;
}

.user-dropdown-wrap {
  position: absolute;
  top: 100%;
  left: 0;
  z-index: 60;
  padding-top: 8px;
  min-width: 100%;
}

.user-dropdown {
  min-width: 168px;
  padding: 8px;
  border-radius: 14px;
  background: #fff;
  border: 1px solid #f0e4d8;
  box-shadow: 0 10px 28px rgba(61, 47, 40, 0.12);
}

.dropdown-item {
  display: block;
  width: 100%;
  padding: 10px 12px;
  border: none;
  border-radius: 10px;
  font: inherit;
  font-size: 14px;
  font-weight: 500;
  color: #5c4a42;
  text-align: left;
  background: transparent;
  cursor: pointer;
  transition: background 0.12s ease, color 0.12s ease;

  &:hover {
    background: #fff5ec;
    color: #8d4d30;
  }
}

.user-pill {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 4px 16px 4px 4px;
  border-radius: 999px;
  background: #fff;
  border: 1px solid #f0e4d8;
  box-shadow: 0 1px 2px rgba(141, 77, 48, 0.06);
  text-decoration: none;
  color: inherit;
  cursor: pointer;
  transition: border-color 0.15s ease, box-shadow 0.15s ease;

  &:hover {
    border-color: #ffd4b7;
    box-shadow: 0 2px 8px rgba(241, 124, 83, 0.12);
  }

  &:focus-visible {
    outline: 2px solid #f17c53;
    outline-offset: 2px;
  }
}

.user-pill-avatar {
  flex-shrink: 0;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: linear-gradient(145deg, #ffb089, #f17c53);
  color: #fff;
  font-weight: 800;
  font-size: 15px;
  letter-spacing: -0.02em;
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
  overflow: hidden;
}

.user-pill-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  pointer-events: none;
  user-select: none;
}

.user-pill-name {
  font-size: 14px;
  font-weight: 600;
  color: #3d2f28;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.logout-circle {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: none;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #e74c3c;
  color: #fff;
  cursor: pointer;
  transition: background 0.15s ease, transform 0.12s ease;

  &:hover {
    background: #d63c2e;
  }

  &:active {
    transform: scale(0.96);
  }
}

.logout-icon {
  width: 20px;
  height: 20px;
}

@media (max-width: 768px) {
  .web-header {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  nav {
    justify-content: flex-start;
    order: 3;
    width: 100%;
    padding: 8px 0;
  }
  
  .auth-actions {
    justify-content: flex-end;
  }
}
</style>
