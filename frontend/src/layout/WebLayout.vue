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

    <!-- 右侧工具栏 -->
    <aside class="side-toolbar">
      <button class="tool-btn" title="回到顶部" @click="scrollToTop">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M18 15l-6-6-6 6"/>
        </svg>
        <span class="tool-label">顶部</span>
      </button>
      <button class="tool-btn has-badge" title="预约单" @click="toggleBooking">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
          <line x1="16" y1="2" x2="16" y2="6"/>
          <line x1="8" y1="2" x2="8" y2="6"/>
          <line x1="3" y1="10" x2="21" y2="10"/>
        </svg>
        <span class="tool-label">预约单</span>
        <span v-if="booking.hasDraft" class="tool-badge">{{ booking.badgeCount > 99 ? "99+" : booking.badgeCount }}</span>
      </button>
      <button id="cart-target" class="tool-btn has-badge" title="购物车" @click="toggleCart">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/>
          <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
        </svg>
        <span class="tool-label">购物车</span>
        <span v-if="cart.totalCount > 0" class="tool-badge">{{ cart.totalCount > 99 ? "99+" : cart.totalCount }}</span>
      </button>
      <button class="tool-btn" title="客服" @click="toggleService">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"/>
        </svg>
        <span class="tool-label">客服</span>
      </button>
      <button class="tool-btn" :title="isDark ? '日间模式' : '夜间模式'" @click="toggleTheme">
        <svg v-if="!isDark" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M21 12.79A9 9 0 1111.21 3 7 7 0 0021 12.79z"/>
        </svg>
        <svg v-else viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="12" cy="12" r="5"/>
          <path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42"/>
        </svg>
        <span class="tool-label">{{ isDark ? '日间' : '夜间' }}</span>
      </button>
      <button class="tool-btn ai-btn" title="AI助手" @click="openAIChat">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M12 2a10 10 0 0 1 10 10c0 5.52-4.48 10-10 10S2 17.52 2 12 6.48 2 12 2z"/>
          <path d="M12 8v4l3 3"/>
          <circle cx="12" cy="12" r="3"/>
        </svg>
        <span class="tool-label">AI助手</span>
      </button>
    </aside>

    <!-- 预约单侧边栏 -->
    <Teleport to="body">
      <Transition name="drawer-fade">
        <div v-if="bookingOpen" class="drawer-scrim" @click="bookingOpen = false" />
      </Transition>
      <Transition name="drawer-slide">
        <aside v-if="bookingOpen" class="drawer booking-drawer" aria-label="预约单">
          <div class="drawer-head">
            <h3>预约单</h3>
            <button type="button" class="drawer-close" aria-label="关闭" @click="bookingOpen = false">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M18 6L6 18M6 6l12 12"/>
              </svg>
            </button>
          </div>
          <div class="drawer-body">
            <div v-if="booking.hasDraft" class="booking-content">
              <div class="booking-merchant">
                <p class="merchant-name">{{ booking.merchantName }}</p>
                <img v-if="booking.merchantCover" :src="booking.merchantCover" class="merchant-cover" alt="" />
              </div>
              <ul class="booking-list">
                <li v-for="m in booking.mainServices" :key="m.id" class="booking-item">
                  <span class="item-name">{{ m.name }}</span>
                  <span class="item-price">¥{{ formatPrice(m.price) }}</span>
                  <button type="button" class="item-remove" @click="booking.toggleMainService(m)" title="移除">×</button>
                </li>
                <li v-for="a in booking.addons" :key="a.id" class="booking-item">
                  <span class="item-name">{{ a.name }}</span>
                  <span class="item-price">{{ a.price === 0 ? "免费" : `¥${formatPrice(a.price)}` }}</span>
                  <button type="button" class="item-remove" @click="booking.toggleAddon(a)" title="移除">×</button>
                </li>
              </ul>
              <p v-if="booking.bookingDate && booking.timeSlot" class="booking-schedule">
                {{ booking.bookingDate }} {{ booking.timeSlot }} · {{ booking.petLabel }}
              </p>
              <p v-else class="booking-schedule muted">尚未选择日期与时间</p>
            </div>
            <div v-else class="drawer-empty">
              <p>暂无预约内容</p>
              <p class="empty-hint">在商家详情点击「点击预约」后，可在此查看或继续编辑</p>
              <RouterLink to="/services" class="drawer-link" @click="bookingOpen = false">去选商家</RouterLink>
            </div>
          </div>
          <div v-if="booking.hasDraft" class="drawer-foot">
            <div class="total-row">
              <span>预估合计</span>
              <strong>¥{{ formatPrice(booking.totalAmount) }}</strong>
            </div>
            <div class="action-btns">
              <button type="button" class="btn-secondary" @click="goBookingEdit">继续编辑</button>
              <button type="button" class="btn-primary" @click="goBookingConfirm">去确认</button>
            </div>
          </div>
        </aside>
      </Transition>
    </Teleport>

    <!-- 购物车侧边栏 -->
    <Teleport to="body">
      <Transition name="drawer-fade">
        <div v-if="cartOpen" class="drawer-scrim" @click="cartOpen = false" />
      </Transition>
      <Transition name="drawer-slide">
        <aside v-if="cartOpen" class="drawer cart-drawer" aria-label="购物车">
          <div class="drawer-head">
            <h3>购物车</h3>
            <button type="button" class="drawer-close" aria-label="关闭" @click="cartOpen = false">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M18 6L6 18M6 6l12 12"/>
              </svg>
            </button>
          </div>
          <div class="drawer-body">
            <div v-if="cart.items.length > 0" class="cart-content">
              <div v-for="item in cart.items" :key="item.id" class="cart-item">
                <img :src="item.image" class="item-img" alt="" />
                <div class="item-info">
                  <span class="item-name">{{ item.name }}</span>
                  <div class="item-bottom">
                    <span class="item-price">¥{{ formatPrice(item.price) }}</span>
                    <div class="qty-control">
                      <button type="button" @click="cart.decreaseQty(item.id)">−</button>
                      <span>{{ item.quantity }}</span>
                      <button type="button" @click="cart.increaseQty(item.id)">+</button>
                    </div>
                  </div>
                </div>
                <button type="button" class="item-remove" @click="cart.removeItem(item.id)">×</button>
              </div>
            </div>
            <div v-else class="drawer-empty">
              <p>购物车是空的</p>
              <p class="empty-hint">去挑几件好物吧</p>
            </div>
          </div>
          <div v-if="cart.items.length > 0" class="drawer-foot">
            <div class="total-row">
              <span>合计</span>
              <strong>¥{{ formatPrice(cart.totalAmount) }}</strong>
            </div>
            <button type="button" class="btn-primary full" @click="goCheckout">去结算</button>
          </div>
        </aside>
      </Transition>
    </Teleport>

    <!-- 客服聊天窗口 -->
    <ServiceChat ref="serviceChatRef" />

    <!-- AI宠医助手 -->
    <AIPetDoctorChat ref="aiChatRef" v-model:open="aiChatOpen" />

    <!-- 飞入动画元素 -->
    <Teleport to="body">
      <div v-if="flyingItem.show" class="flying-item" :style="flyingItem.style">
        <img :src="flyingItem.image" alt="" />
      </div>
    </Teleport>

    <WebFooter />
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, reactive } from "vue";
import { useAuthStore } from "@/store/auth";
import { useThemeStore } from "@/store/theme";
import { useRouter } from "vue-router";
import WebFooter from "@/components/WebFooter.vue";
import ServiceChat from "@/components/ServiceChat.vue";
import AIPetDoctorChat from "@/components/ai/AIPetDoctorChat.vue";
import { useServiceBookingStore } from "@/store/serviceBooking";
import { useShopCartStore } from "@/store/shopCart";

const auth = useAuthStore();
const theme = useThemeStore();
const router = useRouter();
const booking = useServiceBookingStore();
const cart = useShopCartStore();

const isDark = computed(() => theme.isDark);
const toggleTheme = () => theme.toggle();

const bookingOpen = ref(false);
const cartOpen = ref(false);
const serviceChatRef = ref<InstanceType<typeof ServiceChat> | null>(null);
const aiChatRef = ref<InstanceType<typeof AIPetDoctorChat> | null>(null);
const aiChatOpen = ref(false);

interface FlyingItem {
  show: boolean;
  image: string;
  style: Record<string, string>;
}

const flyingItem = reactive<FlyingItem>({
  show: false,
  image: "",
  style: {}
});

const formatPrice = (n: number) => (Number.isInteger(n) ? String(n) : n.toFixed(2));

const toggleBooking = () => {
  bookingOpen.value = !bookingOpen.value;
  if (bookingOpen.value) cartOpen.value = false;
};

const toggleCart = () => {
  cartOpen.value = !cartOpen.value;
  if (cartOpen.value) bookingOpen.value = false;
};

const toggleService = () => {
  if (serviceChatRef.value) {
    serviceChatRef.value.toggle();
  }
};

const openServiceChat = (source = "在线客服") => {
  if (serviceChatRef.value) {
    serviceChatRef.value.open(source);
  }
};

const goBookingEdit = () => {
  if (!booking.merchantId) return;
  bookingOpen.value = false;
  router.push({ path: `/services/book/${booking.merchantId}` });
};

const goBookingConfirm = () => {
  if (!booking.merchantId) return;
  bookingOpen.value = false;
  if (booking.scheduleReady) {
    router.push({ path: "/services/checkout" });
  } else {
    router.push({ path: `/services/book/${booking.merchantId}` });
  }
};

const goCheckout = () => {
  cartOpen.value = false;
  router.push({ path: "/shop/checkout" });
};

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: "smooth" });
};

const openAIChat = () => {
  aiChatOpen.value = true;
};

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

let serviceChatListener: ((event: Event) => void) | undefined;

onMounted(() => {
  serviceChatListener = (event: Event) => {
    const detail = (event as CustomEvent<{ source?: string }>).detail;
    openServiceChat(detail?.source || "在线客服");
  };
  document.addEventListener("open-service-chat", serviceChatListener);
});

onBeforeUnmount(() => {
  clearMenuLeaveTimer();
  if (serviceChatListener) {
    document.removeEventListener("open-service-chat", serviceChatListener);
  }
});

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
  { name: "我的", path: "/profile" },
  { name: "系统状态", path: "/health" }
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

// Expose fly animation for child components
defineExpose({ flyingItem });
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
  background: var(--surface);
  border-bottom: 1px solid var(--border-warm);
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
  background: var(--hero-gradient);
}

.brand {
  font-weight: 900;
  color: var(--text-heading);
  letter-spacing: 0.3px;
  font-size: 30px;
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
  color: var(--text);
  border: 1px solid transparent;
  font-size: 20px;
}

.link.router-link-active {
  background: var(--chip-bg);
  color: var(--primary);
  border-color: var(--chip-border);
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
  background: var(--surface);
  border: 1px solid var(--border-warm);
  box-shadow: 0 10px 28px rgba(92, 64, 51, 0.12);
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
  color: var(--text);
  text-align: left;
  background: transparent;
  cursor: pointer;
  transition: background 0.12s ease, color 0.12s ease;

  &:hover {
    background: var(--chip-bg);
    color: var(--primary);
  }
}

.user-pill {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 4px 16px 4px 4px;
  border-radius: 999px;
  background: var(--surface);
  border: 1px solid var(--border-warm);
  box-shadow: 0 1px 2px rgba(92, 64, 51, 0.06);
  text-decoration: none;
  color: inherit;
  cursor: pointer;
  transition: border-color 0.15s ease, box-shadow 0.15s ease;

  &:hover {
    border-color: var(--chip-border);
    box-shadow: 0 2px 8px rgba(255, 155, 122, 0.12);
  }

  &:focus-visible {
    outline: 2px solid var(--primary);
    outline-offset: 2px;
  }
}

.user-pill-avatar {
  flex-shrink: 0;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--hero-gradient);
  color: var(--hero-text);
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
  color: var(--text-heading);
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
  background: var(--danger);
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

// 右侧工具栏
.side-toolbar {
  position: fixed;
  right: 24px;
  bottom: 80px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  z-index: 40;
}

.tool-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  width: 56px;
  padding: 12px 8px;
  background: var(--surface);
  border: 1px solid var(--border-warm);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: var(--shadow);

  svg {
    width: 22px;
    height: 22px;
    color: var(--text-heading);
    transition: color 0.2s ease;
  }

  .tool-label {
    font-size: 10px;
    font-weight: 600;
    color: var(--muted);
    white-space: nowrap;
  }

  &:hover {
    transform: translateY(-4px);
    border-color: var(--chip-border);
    box-shadow: 0 8px 24px rgba(255, 155, 122, 0.2);

    svg {
      color: var(--primary);
    }

    .tool-label {
      color: var(--primary);
    }
  }

  &:active {
    transform: translateY(-2px);
  }

  &.ai-btn {
    background: var(--hero-gradient);
    border-color: transparent;

    svg {
      color: #fff;
    }

    .tool-label {
      color: rgba(255, 255, 255, 0.9);
    }

    &:hover {
      box-shadow: 0 8px 24px rgba(255, 155, 122, 0.4);
    }
  }
}

@media (max-width: 768px) {
  .side-toolbar {
    right: 12px;
    bottom: 60px;
    gap: 8px;
  }

  .tool-btn {
    width: 48px;
    padding: 10px 6px;

    svg {
      width: 18px;
      height: 18px;
    }

    .tool-label {
      font-size: 9px;
    }
  }
}

// Tool badge
.tool-badge {
  position: absolute;
  top: 2px;
  right: 2px;
  min-width: 16px;
  height: 16px;
  padding: 0 4px;
  border-radius: 8px;
  background: var(--danger);
  color: #fff;
  font-size: 10px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
}

.tool-btn.has-badge {
  position: relative;
}

.tool-btn.bump {
  animation: bump 0.4s ease;
}

@keyframes bump {
  0% { transform: translateY(-4px) scale(1); }
  50% { transform: translateY(-4px) scale(1.2); }
  100% { transform: translateY(-4px) scale(1); }
}

// Drawer styles
.drawer-scrim {
  position: fixed;
  inset: 0;
  background: var(--overlay-scrim);
  z-index: 45;
}

.drawer {
  position: fixed;
  right: 0;
  top: 0;
  bottom: 0;
  width: 380px;
  max-width: 100vw;
  background: var(--surface);
  border-left: 1px solid var(--border-warm);
  z-index: 50;
  display: flex;
  flex-direction: column;
  box-shadow: -4px 0 24px rgba(0, 0, 0, 0.15);
}

.drawer-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-warm);
  flex-shrink: 0;

  h3 {
    font-size: 18px;
    font-weight: 600;
    color: var(--text-heading);
    margin: 0;
  }
}

.drawer-close {
  background: none;
  border: none;
  padding: 4px;
  cursor: pointer;
  color: var(--muted);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;

  svg {
    width: 20px;
    height: 20px;
  }

  &:hover {
    background: var(--surface-muted);
    color: var(--text-heading);
  }
}

.drawer-body {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.drawer-foot {
  padding: 16px 20px;
  border-top: 1px solid var(--border-warm);
  background: var(--surface-muted);
  flex-shrink: 0;
}

.drawer-empty {
  text-align: center;
  padding: 40px 20px;
  color: var(--muted);

  p {
    margin: 8px 0;
  }

  .empty-hint {
    font-size: 13px;
    color: var(--muted-soft);
  }
}

.drawer-link {
  display: inline-block;
  margin-top: 12px;
  padding: 8px 20px;
  background: var(--primary);
  color: #fff;
  border-radius: 20px;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: background 0.2s;

  &:hover {
    background: var(--primary-strong);
  }
}

// Booking drawer content
.booking-merchant {
  margin-bottom: 16px;

  .merchant-name {
    font-size: 16px;
    font-weight: 600;
    color: var(--text-heading);
    margin: 0 0 8px;
  }

  .merchant-cover {
    width: 100%;
    height: 120px;
    object-fit: cover;
    border-radius: var(--radius-md);
  }
}

.booking-list {
  list-style: none;
  padding: 0;
  margin: 0 0 16px;
}

.booking-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid var(--border-warm);

  .item-name {
    color: var(--text);
    font-size: 14px;
  }

  .item-price {
    color: var(--primary);
    font-weight: 600;
    font-size: 14px;
  }
}

.booking-schedule {
  font-size: 13px;
  color: var(--muted);
  padding: 10px;
  background: var(--surface-muted);
  border-radius: var(--radius-sm);

  &.muted {
    color: var(--muted-soft);
    font-style: italic;
  }
}

.total-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;

  span {
    color: var(--muted);
    font-size: 14px;
  }

  strong {
    font-size: 20px;
    color: var(--primary);
  }
}

.action-btns {
  display: flex;
  gap: 12px;

  .btn-secondary, .btn-primary {
    flex: 1;
    padding: 10px;
    border-radius: var(--radius-md);
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s;
  }

  .btn-secondary {
    background: var(--surface-muted);
    border: 1px solid var(--border-warm);
    color: var(--text);

    &:hover {
      border-color: var(--chip-border);
    }
  }

  .btn-primary {
    background: var(--primary);
    border: 1px solid var(--primary);
    color: #fff;

    &:hover {
      background: var(--primary-strong);
    }
  }
}

.btn-primary.full {
  width: 100%;
  padding: 12px;
  border-radius: var(--radius-md);
  background: var(--primary);
  border: none;
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: var(--primary-strong);
  }
}

// Cart drawer content
.cart-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.cart-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: var(--surface-muted);
  border-radius: var(--radius-md);
  position: relative;

  .item-img {
    width: 64px;
    height: 64px;
    border-radius: var(--radius-sm);
    object-fit: cover;
    flex-shrink: 0;
  }

  .item-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    min-width: 0;

    .item-name {
      font-size: 14px;
      color: var(--text-heading);
      font-weight: 500;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .item-bottom {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .item-price {
      color: var(--primary);
      font-weight: 600;
      font-size: 15px;
    }
  }

  .item-remove {
    position: absolute;
    top: 8px;
    right: 8px;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    border: none;
    background: var(--muted-soft);
    color: #fff;
    font-size: 12px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0.6;
    transition: opacity 0.2s;

    &:hover {
      opacity: 1;
    }
  }
}

.qty-control {
  display: flex;
  align-items: center;
  gap: 8px;

  button {
    width: 24px;
    height: 24px;
    border-radius: 50%;
    border: 1px solid var(--border-warm);
    background: var(--surface);
    color: var(--text-heading);
    font-size: 14px;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;

    &:hover {
      border-color: var(--primary);
      color: var(--primary);
    }
  }

  span {
    min-width: 24px;
    text-align: center;
    font-size: 14px;
    color: var(--text-heading);
  }
}

// Flying item animation
.flying-item {
  position: fixed;
  width: 48px;
  height: 48px;
  border-radius: var(--radius-md);
  overflow: hidden;
  z-index: 200;
  pointer-events: none;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

// Drawer transitions
.drawer-fade-enter-active,
.drawer-fade-leave-active {
  transition: opacity 0.3s ease;
}

.drawer-fade-enter-from,
.drawer-fade-leave-to {
  opacity: 0;
}

.drawer-slide-enter-active,
.drawer-slide-leave-active {
  transition: transform 0.3s ease;
}

.drawer-slide-enter-from,
.drawer-slide-leave-to {
  transform: translateX(100%);
}

@media (max-width: 480px) {
  .drawer {
    width: 100vw;
  }
}
</style>
