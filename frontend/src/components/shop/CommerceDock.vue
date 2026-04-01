<template>
  <aside class="commerce-dock" aria-label="快捷入口">
    <button type="button" class="dock-btn dock-top" title="回到顶部" @click="scrollTop">
      <span class="dock-glyph" aria-hidden="true">⌂</span>
      <span class="dock-label">顶部</span>
    </button>
    <button
      id="commerce-cart-target"
      type="button"
      class="dock-btn dock-cart"
      title="购物车"
      @click="cartOpen = true"
    >
      <span class="dock-glyph" aria-hidden="true">🛒</span>
      <span v-if="cart.totalCount > 0" class="dock-badge">{{ cart.totalCount > 99 ? "99+" : cart.totalCount }}</span>
      <span class="dock-label">购物车</span>
    </button>
    <RouterLink to="/services" class="dock-btn" title="服务预约">
      <span class="dock-glyph" aria-hidden="true">💬</span>
      <span class="dock-label">客服</span>
    </RouterLink>
    <button type="button" class="dock-btn" title="反馈" @click="onFeedback">
      <span class="dock-glyph" aria-hidden="true">✎</span>
      <span class="dock-label">反馈</span>
    </button>
  </aside>

  <button v-show="dockCompact" type="button" class="fab-cart" aria-label="打开购物车" @click="cartOpen = true">
    <span aria-hidden="true">🛒</span>
    <span v-if="cart.totalCount > 0" class="fab-badge">{{ cart.totalCount > 99 ? "99+" : cart.totalCount }}</span>
  </button>

  <Teleport to="body">
    <Transition name="scrim-fade">
      <div v-if="cartOpen" class="cart-scrim" @click="cartOpen = false" />
    </Transition>
    <Transition name="drawer-slide">
      <aside v-if="cartOpen" class="cart-drawer" aria-label="购物车">
        <div class="drawer-head">
          <h3>购物车</h3>
          <button type="button" class="drawer-x" aria-label="关闭" @click="cartOpen = false">×</button>
        </div>
        <p class="drawer-hint">满 ¥99 包邮（演示）</p>

        <div v-if="cart.items.length > 0" class="drawer-list">
          <div v-for="item in cart.items" :key="item.id" class="drawer-row">
            <img :src="item.image" class="drawer-thumb" alt="" />
            <div class="drawer-main">
              <span class="drawer-name">{{ item.name }}</span>
              <div class="drawer-bottom">
                <span class="drawer-price">¥{{ formatPrice(item.price) }}</span>
                <div class="qty">
                  <button type="button" aria-label="减少" @click="cart.decreaseQty(item.id)">−</button>
                  <span>{{ item.quantity }}</span>
                  <button type="button" aria-label="增加" @click="cart.increaseQty(item.id)">+</button>
                </div>
              </div>
            </div>
            <button type="button" class="drawer-remove" aria-label="移除" @click="cart.removeItem(item.id)">×</button>
          </div>
        </div>

        <div v-else class="drawer-empty">
          <p>购物车是空的</p>
          <p class="drawer-empty-sub">去挑几件好物吧</p>
        </div>

        <div v-if="cart.items.length > 0" class="drawer-foot">
          <div class="drawer-total">
            <span>合计</span>
            <strong>¥{{ formatPrice(cart.totalAmount) }}</strong>
          </div>
          <button type="button" class="btn-checkout" @click="goCheckout">去结算</button>
        </div>
      </aside>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onBeforeUnmount } from "vue";
import { useRouter } from "vue-router";
import { useShopCartStore } from "@/store/shopCart";

const cart = useShopCartStore();
const router = useRouter();
const cartOpen = ref(false);
const dockCompact = ref(false);

const formatPrice = (n: number) => (Number.isInteger(n) ? String(n) : n.toFixed(2));

const scrollTop = () => window.scrollTo({ top: 0, behavior: "smooth" });

const goCheckout = () => {
  cartOpen.value = false;
  router.push({ path: "/shop/checkout" });
};

const onFeedback = () => {
  /* 演示：避免原生 alert */
};

const checkDock = () => {
  dockCompact.value = window.matchMedia("(max-width: 900px)").matches;
};

let mqListener: (() => void) | undefined;

watch(cartOpen, (open) => {
  document.body.style.overflow = open ? "hidden" : "";
});

onMounted(() => {
  checkDock();
  mqListener = () => checkDock();
  window.addEventListener("resize", mqListener);
});

onBeforeUnmount(() => {
  document.body.style.overflow = "";
  if (mqListener) window.removeEventListener("resize", mqListener);
});
</script>

<style scoped lang="scss">
.commerce-dock {
  position: fixed;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  z-index: 42;
  display: flex;
  flex-direction: column;
  width: 58px;
  padding: 8px 0;
  background: #fffefcf7;
  border: 1px solid var(--border-warm);
  border-right: none;
  border-radius: 10px 0 0 10px;
  box-shadow: -4px 0 20px rgba(61, 47, 40, 0.08);
}

.dock-top {
  border-radius: 10px 0 0 0;
  background: linear-gradient(180deg, #fff5ed 0%, transparent 100%);
}

.dock-btn {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3px;
  padding: 11px 4px;
  border: none;
  background: transparent;
  color: var(--text-heading-soft);
  text-decoration: none;
  cursor: pointer;
  font-size: 12px;
  font-weight: 700;
  transition: background 0.15s;

  &:hover {
    background: var(--chip-bg);
  }
}

.dock-glyph {
  font-size: 20px;
  line-height: 1;
}

.dock-label {
  line-height: 1.2;
  text-align: center;
}

.dock-badge {
  position: absolute;
  top: 4px;
  right: 2px;
  min-width: 18px;
  height: 18px;
  padding: 0 4px;
  border-radius: 9px;
  background: #ff5000;
  color: #fff;
  font-size: 11px;
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: center;
}

.fab-cart {
  display: none;
  position: fixed;
  right: 16px;
  bottom: 24px;
  z-index: 42;
  width: 52px;
  height: 52px;
  border: none;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary), var(--primary-strong));
  color: #fff;
  font-size: 22px;
  cursor: pointer;
  box-shadow: 0 6px 20px rgba(241, 124, 83, 0.45);
}

.fab-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  min-width: 18px;
  height: 18px;
  padding: 0 4px;
  border-radius: 9px;
  background: #ff5000;
  color: #fff;
  font-size: 10px;
  font-weight: 800;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cart-scrim {
  position: fixed;
  inset: 0;
  z-index: 48;
  background: var(--overlay-scrim);
}

.cart-drawer {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  z-index: 50;
  width: min(380px, 100vw);
  background: var(--surface);
  box-shadow: -8px 0 32px rgba(61, 47, 40, 0.18);
  display: flex;
  flex-direction: column;
  border-left: 1px solid var(--border-warm);
}

.drawer-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 14px 8px;
  border-bottom: 1px solid var(--border-warm);

  h3 {
    margin: 0;
    font-size: 20px;
    font-weight: 900;
    color: var(--text);
  }
}

.drawer-x {
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 10px;
  background: var(--surface-muted);
  font-size: 22px;
  line-height: 1;
  cursor: pointer;
  color: var(--muted);

  &:hover {
    background: var(--chip-bg);
    color: var(--text);
  }
}

.drawer-hint {
  margin: 0;
  padding: 0 14px 10px;
  font-size: 14px;
  color: var(--muted);
}

.drawer-list {
  flex: 1;
  overflow-y: auto;
  padding: 0 12px;
}

.drawer-row {
  display: flex;
  gap: 10px;
  padding: 12px 0;
  border-bottom: 1px solid var(--border-warm);
}

.drawer-thumb {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  object-fit: cover;
  border: 1px solid var(--border-warm);
}

.drawer-main {
  flex: 1;
  min-width: 0;
}

.drawer-name {
  font-size: 15px;
  font-weight: 700;
  color: var(--text);
  line-height: 1.35;
  margin-bottom: 6px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.drawer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.drawer-price {
  font-size: 17px;
  font-weight: 900;
  color: #ff5000;
}

.qty {
  display: flex;
  align-items: center;
  gap: 6px;

  button {
    width: 26px;
    height: 26px;
    border-radius: 6px;
    border: 1px solid var(--border-warm);
    background: var(--surface);
    font-size: 15px;
    line-height: 1;
    cursor: pointer;

    &:hover {
      background: var(--chip-bg);
    }
  }

  span {
    min-width: 18px;
    text-align: center;
    font-size: 15px;
    font-weight: 700;
  }
}

.drawer-remove {
  align-self: flex-start;
  width: 28px;
  height: 28px;
  border: none;
  border-radius: 6px;
  background: transparent;
  color: var(--muted);
  font-size: 20px;
  cursor: pointer;

  &:hover {
    background: var(--status-danger-bg);
    color: var(--danger);
  }
}

.drawer-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: var(--muted);
  font-weight: 700;
  font-size: 16px;

  .drawer-empty-sub {
    margin: 8px 0 0;
    font-size: 14px;
    font-weight: 500;
    color: var(--muted-soft);
  }
}

.drawer-foot {
  padding: 14px;
  border-top: 1px solid var(--border-warm);
  background: var(--surface-tint);
}

.drawer-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 16px;
  color: var(--muted);

  strong {
    font-size: 24px;
    color: #ff5000;
  }
}

.btn-checkout {
  width: 100%;
  padding: 14px;
  border: none;
  border-radius: 12px;
  background: linear-gradient(135deg, #ff6b4a 0%, var(--primary-strong) 100%);
  color: #fff;
  font-size: 17px;
  font-weight: 900;
  cursor: pointer;

  &:hover {
    filter: brightness(1.05);
  }
}

.scrim-fade-enter-active,
.scrim-fade-leave-active {
  transition: opacity 0.25s ease;
}

.scrim-fade-enter-from,
.scrim-fade-leave-to {
  opacity: 0;
}

.drawer-slide-enter-active,
.drawer-slide-leave-active {
  transition: transform 0.28s cubic-bezier(0.33, 1, 0.68, 1);
}

.drawer-slide-enter-from,
.drawer-slide-leave-to {
  transform: translateX(100%);
}

@media (max-width: 900px) {
  .commerce-dock {
    display: none;
  }

  .fab-cart {
    display: flex;
    align-items: center;
    justify-content: center;
  }
}
</style>

<!-- 飞入动效在 composable 里给 #commerce-cart-target 加 class，需非 scoped -->
<style lang="scss">
#commerce-cart-target.commerce-cart-bump {
  animation: commerce-cart-bump 0.45s ease;
}

@keyframes commerce-cart-bump {
  0%,
  100% {
    transform: scale(1);
  }
  40% {
    transform: scale(1.12);
  }
  60% {
    transform: scale(1.06);
  }
}
</style>
