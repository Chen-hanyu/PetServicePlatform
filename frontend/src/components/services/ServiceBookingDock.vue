<template>
  <aside class="service-booking-dock" aria-label="预约快捷入口">
    <button type="button" class="sb-dock-btn sb-dock-top" title="回到顶部" @click="scrollTop">
      <span class="sb-glyph" aria-hidden="true">⌂</span>
      <span class="sb-label">顶部</span>
    </button>
    <button
      id="service-booking-target"
      type="button"
      class="sb-dock-btn sb-dock-book"
      title="预约单"
      @click="open = true"
    >
      <span class="sb-glyph" aria-hidden="true">📋</span>
      <span v-if="booking.hasDraft" class="sb-badge">{{ booking.badgeCount > 99 ? "99+" : booking.badgeCount }}</span>
      <span class="sb-label">预约单</span>
    </button>
    <RouterLink to="/shop" class="sb-dock-btn" title="宠物商城">
      <span class="sb-glyph" aria-hidden="true">🛒</span>
      <span class="sb-label">商城</span>
    </RouterLink>
    <button type="button" class="sb-dock-btn" title="反馈" @click="onFeedback">
      <span class="sb-glyph" aria-hidden="true">✎</span>
      <span class="sb-label">反馈</span>
    </button>
  </aside>

  <button v-show="compact" type="button" class="sb-fab" aria-label="打开预约单" @click="open = true">
    <span aria-hidden="true">📋</span>
    <span v-if="booking.hasDraft" class="sb-fab-badge">{{ booking.badgeCount > 99 ? "99+" : booking.badgeCount }}</span>
  </button>

  <Teleport to="body">
    <Transition name="sb-scrim-fade">
      <div v-if="open" class="sb-scrim" @click="open = false" />
    </Transition>
    <Transition name="sb-drawer-slide">
      <aside v-if="open" class="sb-drawer" aria-label="预约单">
        <div class="sb-drawer-head">
          <h3>预约单</h3>
          <button type="button" class="sb-drawer-x" aria-label="关闭" @click="open = false">×</button>
        </div>
        <p class="sb-drawer-hint">与商城一致的右侧条体验：进入预约页后会自动记录草稿，可在此继续</p>

        <div v-if="booking.hasDraft" class="sb-drawer-body">
          <p class="sb-merchant">{{ booking.merchantName }}</p>
          <div v-if="booking.merchantCover" class="sb-cover-wrap">
            <img :src="booking.merchantCover" class="sb-cover" alt="" />
          </div>
          <ul class="sb-lines">
            <li v-for="m in booking.mainServices" :key="m.id">
              <span class="sb-line-name">{{ m.name }}</span>
              <span class="sb-line-price">¥{{ formatPrice(m.price) }}</span>
            </li>
            <li v-for="a in booking.addons" :key="a.id">
              <span class="sb-line-name">{{ a.name }}</span>
              <span class="sb-line-price">{{ a.price === 0 ? "免费" : `¥${formatPrice(a.price)}` }}</span>
            </li>
          </ul>
          <p v-if="booking.bookingDate && booking.timeSlot" class="sb-schedule">
            {{ booking.bookingDate }} {{ booking.timeSlot }} · {{ booking.petLabel }}
          </p>
          <p v-else class="sb-schedule muted">尚未选择日期与时间</p>
        </div>

        <div v-else class="sb-drawer-empty">
          <p>暂无预约内容</p>
          <p class="sb-sub">在商家详情点击「点击预约」后，可在此查看或继续编辑</p>
          <RouterLink to="/services" class="sb-link" @click="open = false">去选商家</RouterLink>
        </div>

        <div v-if="booking.hasDraft" class="sb-drawer-foot">
          <div class="sb-total">
            <span>预估合计</span>
            <strong>¥{{ formatPrice(booking.totalAmount) }}</strong>
          </div>
          <button type="button" class="sb-btn-secondary" @click="goEdit">继续编辑</button>
          <button type="button" class="sb-btn-primary" @click="goConfirm">去确认</button>
        </div>
      </aside>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
import { ref, watch, onMounted, onBeforeUnmount } from "vue";
import { useRouter } from "vue-router";
import { useServiceBookingStore } from "@/store/serviceBooking";

const booking = useServiceBookingStore();
const router = useRouter();
const open = ref(false);
const compact = ref(false);

const formatPrice = (n: number) => (Number.isInteger(n) ? String(n) : n.toFixed(2));

const scrollTop = () => window.scrollTo({ top: 0, behavior: "smooth" });

const onFeedback = () => {
  /* 演示环境 */
};

const goEdit = () => {
  if (!booking.merchantId) return;
  open.value = false;
  router.push({ path: `/services/book/${booking.merchantId}` });
};

const goConfirm = () => {
  if (!booking.merchantId) return;
  open.value = false;
  if (booking.scheduleReady) {
    router.push({ path: "/services/checkout" });
  } else {
    router.push({ path: `/services/book/${booking.merchantId}` });
  }
};

const checkCompact = () => {
  compact.value = window.matchMedia("(max-width: 900px)").matches;
};

let mqListener: (() => void) | undefined;

watch(open, (v) => {
  document.body.style.overflow = v ? "hidden" : "";
});

onMounted(() => {
  checkCompact();
  mqListener = () => checkCompact();
  window.addEventListener("resize", mqListener);
});

onBeforeUnmount(() => {
  document.body.style.overflow = "";
  if (mqListener) window.removeEventListener("resize", mqListener);
});
</script>

<style scoped lang="scss">
.service-booking-dock {
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

.sb-dock-top {
  border-radius: 10px 0 0 0;
  background: linear-gradient(180deg, #fff5ed 0%, transparent 100%);
}

.sb-dock-btn {
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

.sb-glyph {
  font-size: 20px;
  line-height: 1;
}

.sb-label {
  line-height: 1.2;
  text-align: center;
}

.sb-badge {
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

.sb-fab {
  display: none;
  position: fixed;
  right: 16px;
  bottom: 96px;
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

.sb-fab-badge {
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

.sb-scrim {
  position: fixed;
  inset: 0;
  z-index: 48;
  background: var(--overlay-scrim);
}

.sb-drawer {
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

.sb-drawer-head {
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

.sb-drawer-x {
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

.sb-drawer-hint {
  margin: 0;
  padding: 0 14px 10px;
  font-size: 14px;
  color: var(--muted);
}

.sb-drawer-body {
  flex: 1;
  overflow-y: auto;
  padding: 0 14px 12px;
}

.sb-merchant {
  margin: 0 0 10px;
  font-size: 17px;
  font-weight: 900;
  color: var(--text);
}

.sb-cover-wrap {
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 12px;
  border: 1px solid var(--border-warm);
}

.sb-cover {
  width: 100%;
  height: 120px;
  object-fit: cover;
  display: block;
}

.sb-lines {
  list-style: none;
  margin: 0;
  padding: 0;

  li {
    display: flex;
    justify-content: space-between;
    align-items: baseline;
    gap: 12px;
    padding: 10px 0;
    border-bottom: 1px solid var(--border-warm);
    font-size: 15px;
  }
}

.sb-line-name {
  font-weight: 600;
  color: var(--text);
}

.sb-line-price {
  font-weight: 800;
  color: #ff5000;
  flex-shrink: 0;
}

.sb-schedule {
  margin: 12px 0 0;
  font-size: 14px;
  font-weight: 600;
  color: var(--primary-strong);

  &.muted {
    color: var(--muted);
    font-weight: 500;
  }
}

.sb-drawer-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: var(--muted);
  font-weight: 700;
  font-size: 16px;

  .sb-sub {
    margin: 8px 0 16px;
    font-size: 14px;
    font-weight: 500;
    text-align: center;
    color: var(--muted-soft);
  }
}

.sb-link {
  color: var(--primary-strong);
  font-weight: 800;
  text-decoration: none;

  &:hover {
    text-decoration: underline;
  }
}

.sb-drawer-foot {
  padding: 14px;
  border-top: 1px solid var(--border-warm);
  background: var(--surface-tint);
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.sb-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  color: var(--muted);

  strong {
    font-size: 24px;
    color: #ff5000;
  }
}

.sb-btn-secondary {
  width: 100%;
  padding: 12px;
  border-radius: 12px;
  border: 1px solid var(--border-warm);
  background: var(--surface);
  font-size: 16px;
  font-weight: 800;
  color: var(--text);
  cursor: pointer;

  &:hover {
    background: var(--chip-bg);
  }
}

.sb-btn-primary {
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

.sb-scrim-fade-enter-active,
.sb-scrim-fade-leave-active {
  transition: opacity 0.25s ease;
}

.sb-scrim-fade-enter-from,
.sb-scrim-fade-leave-to {
  opacity: 0;
}

.sb-drawer-slide-enter-active,
.sb-drawer-slide-leave-active {
  transition: transform 0.28s cubic-bezier(0.33, 1, 0.68, 1);
}

.sb-drawer-slide-enter-from,
.sb-drawer-slide-leave-to {
  transform: translateX(100%);
}

@media (max-width: 900px) {
  .service-booking-dock {
    display: none;
  }

  .sb-fab {
    display: flex;
    align-items: center;
    justify-content: center;
  }
}
</style>

<style lang="scss">
#service-booking-target.service-booking-bump {
  animation: service-booking-bump 0.45s ease;
}

@keyframes service-booking-bump {
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
