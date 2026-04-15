<template>
  <div class="booking-page">
    <nav class="breadcrumb card" aria-label="面包屑导航">
      <RouterLink to="/home">首页</RouterLink>
      <span class="sep">/</span>
      <RouterLink to="/services">宠物服务</RouterLink>
      <span class="sep">/</span>
      <RouterLink v-if="merchant" :to="`/services/merchant/${merchant.id}`">{{ merchant.name }}</RouterLink>
      <template v-if="merchant">
        <span class="sep">/</span>
      </template>
      <span class="current">创建预约</span>
    </nav>

    <DataState :loading="loading" :error="error" :empty="!merchant && !loading" empty-text="商家不存在">
      <template v-if="merchant">
        <header class="page-head card">
          <h1>创建新预约</h1>
          <p class="sub">{{ merchant.name }} · 先选择服务套餐或加项，再选择到店日期、时段与宠物信息</p>
        </header>

        <div class="booking-grid">
          <section class="col-services card">
            <h2>选择服务套餐</h2>
            <p class="section-tip">主服务（必选，可多选）</p>
            <div class="main-services">
              <button
                v-for="svc in merchant.services"
                :key="svc.id"
                type="button"
                :class="['main-card', { on: booking.isMainOn(svc.id) }]"
                @click="toggleMain(svc)"
              >
                <span class="main-name">{{ svc.name }}</span>
                <span class="main-meta">{{ svc.duration }}</span>
                <span class="main-price">¥{{ formatPrice(svc.price) }}</span>
              </button>
            </div>

            <template v-if="packages.length">
              <p class="section-tip">店铺优惠套餐（多服务组合价）</p>
              <div class="package-list">
                <article
                  v-for="pkg in packages"
                  :key="pkg.id"
                  :class="['package-card', { on: booking.selectedPackageId === pkg.id && booking.appliedPackage }]"
                >
                  <div class="pkg-head">
                    <h3>{{ pkg.name }}</h3>
                    <p class="pkg-save">
                      单买约 ¥{{ formatPrice(packageListSum(pkg)) }}，套餐
                      <strong>¥{{ formatPrice(pkg.price) }}</strong>
                    </p>
                  </div>
                  <button type="button" class="btn-pkg" @click="pickPackage(pkg)">选此套餐</button>
                </article>
              </div>
            </template>

            <p class="section-tip">护理加项（可选，随主服务组合变化可能清空）</p>
            <div class="addon-panel">
              <div v-for="group in addonGroups" :key="group.name" class="addon-group">
                <h3>{{ group.name }}</h3>
                <div class="addon-list">
                  <button
                    v-for="a in group.items"
                    :key="a.id"
                    type="button"
                    :class="['addon-chip', { on: booking.isAddonOn(a.id) }]"
                    @click="booking.toggleAddon(a)"
                  >
                    <span class="addon-chip-name">{{ a.name }}</span>
                    <span class="addon-chip-price" :class="{ free: a.price === 0 }">
                      {{ a.price === 0 ? "免费" : `¥${a.price}` }}
                    </span>
                  </button>
                </div>
              </div>
            </div>
          </section>

          <section class="col-schedule card">
            <h2>预约信息</h2>
            <ol class="schedule-steps" aria-label="预约流程">
              <li :class="{ done: booking.mainServices.length > 0 }">选择服务</li>
              <li :class="{ done: Boolean(booking.bookingDate && booking.timeSlot) }">日期与时间</li>
              <li :class="{ done: canNext }">确认费用</li>
            </ol>

            <div class="field">
              <label for="pet-select">选择宠物</label>
              <select id="pet-select" v-model="petLocal" class="input-like">
                <option v-for="p in petOptions" :key="p" :value="p">{{ p }}</option>
              </select>
            </div>

            <div class="field">
              <span class="label">选择预约日期</span>
              <div class="cal-head">
                <button type="button" class="cal-nav" aria-label="上月" @click="shiftMonth(-1)">‹</button>
                <span class="cal-title">{{ calendarTitle }}</span>
                <button type="button" class="cal-nav" aria-label="下月" @click="shiftMonth(1)">›</button>
              </div>
              <div class="cal-weekdays">
                <span v-for="w in weekdays" :key="w">{{ w }}</span>
              </div>
              <div class="cal-cells">
                <template v-for="(cell, i) in calendarCells" :key="i">
                  <button
                    v-if="cell.kind === 'in'"
                    type="button"
                    :disabled="cell.disabled"
                    :class="['cal-cell', { off: cell.disabled, on: cell.date === booking.bookingDate }]"
                    @click="pickDate(cell.date!)"
                  >
                    {{ cell.day }}
                  </button>
                  <span v-else class="cal-out">{{ cell.day > 0 ? cell.day : "" }}</span>
                </template>
              </div>
            </div>

            <div class="field">
              <span class="label">选择预约时间</span>
              <div class="slots">
                <button
                  v-for="t in timeSlots"
                  :key="t"
                  type="button"
                  :class="['slot', { on: booking.timeSlot === t }]"
                  @click="booking.setSchedule({ timeSlot: t })"
                >
                  {{ t }}
                </button>
              </div>
            </div>

            <div class="field">
              <label for="remark">特殊要求</label>
              <textarea id="remark" v-model="remarkLocal" class="textarea" rows="3" placeholder="如宠物性格、过敏史等" />
            </div>

            <div class="sticky-summary">
              <p class="sum-line">
                已选 <strong>{{ booking.badgeCount }}</strong> 项，合计
                <strong class="sum-price">¥{{ formatPrice(booking.totalAmount) }}</strong>
                <span v-if="booking.packageSavings > 0" class="sum-save"
                  >（套餐已省 ¥{{ formatPrice(booking.packageSavings) }}）</span
                >
              </p>
              <div class="sum-actions">
                <button type="button" class="btn-next" :disabled="!canNext" @click="goCheckout">下一步 · 确认费用</button>
              </div>
              <p v-if="!canNext" class="sum-hint">请完成：至少一项主服务、预约日期与时间段</p>
            </div>
          </section>
        </div>
      </template>
    </DataState>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import DataState from "@/components/DataState.vue";
import { fetchMerchantDetail } from "@/api/modules/services";
import { getMockMerchantById, getMockPackagesByMerchantId, mockServiceAddons } from "@/mocks/services";
import { useServiceBookingStore } from "@/store/serviceBooking";
import type { ServiceBookingAddon, ServiceBookingMain } from "@/store/serviceBooking";

type MerchantVm = {
  id: number;
  name: string;
  cover_url?: string;
  services: Array<{ id: number; name: string; price: number; duration: string }>;
};

const route = useRoute();
const router = useRouter();
const booking = useServiceBookingStore();

const loading = ref(true);
const error = ref("");
const merchant = ref<MerchantVm | null>(null);

const petOptions = ["小小黑 (金毛)", "二毛 (英短)", "憨憨 (柯基)"];
const petLocal = ref(booking.petLabel);
const remarkLocal = ref(booking.remark);

const timeSlots = ["09:00", "10:30", "14:00", "15:30", "17:00"];
const weekdays = ["日", "一", "二", "三", "四", "五", "六"];

const cursor = ref(new Date());

const calendarTitle = computed(() => {
  const d = cursor.value;
  return `${d.getFullYear()}年${d.getMonth() + 1}月`;
});

function startOfMonth(d: Date) {
  return new Date(d.getFullYear(), d.getMonth(), 1);
}

function shiftMonth(delta: number) {
  const d = cursor.value;
  cursor.value = new Date(d.getFullYear(), d.getMonth() + delta, 1);
}

const calendarCells = computed(() => {
  const first = startOfMonth(cursor.value);
  const startPad = first.getDay();
  const year = first.getFullYear();
  const month = first.getMonth();
  const daysInMonth = new Date(year, month + 1, 0).getDate();
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  const cells: Array<{ kind: "in" | "out"; day: number; date?: string; disabled?: boolean }> = [];
  const prevLast = new Date(year, month, 0).getDate();
  for (let i = startPad - 1; i >= 0; i--) {
    cells.push({ kind: "out", day: prevLast - i });
  }
  for (let day = 1; day <= daysInMonth; day++) {
    const dt = new Date(year, month, day);
    const dateStr = `${year}-${String(month + 1).padStart(2, "0")}-${String(day).padStart(2, "0")}`;
    const disabled = dt < today;
    cells.push({ kind: "in", day, date: dateStr, disabled });
  }
  while (cells.length % 7 !== 0) {
    cells.push({ kind: "out", day: 0 });
  }
  return cells;
});

const addonGroups = computed(() => {
  const map = new Map<string, ServiceBookingAddon[]>();
  for (const a of mockServiceAddons) {
    const g = a.group || "其他";
    if (!map.has(g)) map.set(g, []);
    map.get(g)!.push(a);
  }
  return Array.from(map.entries()).map(([name, items]) => ({ name, items }));
});

const packages = computed(() => {
  if (!merchant.value) return [];
  return getMockPackagesByMerchantId(merchant.value.id);
});

function packageListSum(pkg: { serviceIds: readonly number[] }) {
  if (!merchant.value) return 0;
  let s = 0;
  for (const id of pkg.serviceIds) {
    const svc = merchant.value.services.find((x) => x.id === id);
    if (svc) s += svc.price;
  }
  return s;
}

function pickPackage(pkg: { id: string; serviceIds: readonly number[] }) {
  if (!merchant.value) return;
  const catalog = merchant.value.services.map(toMain);
  booking.selectPackage([...pkg.serviceIds], pkg.id, catalog);
}

const canNext = computed(
  () => booking.hasDraft && Boolean(booking.bookingDate && booking.timeSlot && booking.mainServices.length > 0)
);

const formatPrice = (n: number) => (Number.isInteger(n) ? String(n) : n.toFixed(2));

function pickDate(date: string) {
  if (!date) return;
  booking.setSchedule({ bookingDate: date });
}

function toMain(svc: MerchantVm["services"][0]): ServiceBookingMain {
  return {
    id: svc.id,
    name: svc.name,
    price: svc.price,
    duration: svc.duration
  };
}

function toggleMain(svc: MerchantVm["services"][0]) {
  booking.toggleMainService(toMain(svc));
}

function mergeMerchant(api: Record<string, unknown>, mock: MerchantVm | null): MerchantVm | null {
  if (!api && !mock) return null;
  const m = mock || ({} as MerchantVm);
  const services = (api?.services as MerchantVm["services"]) || m.services || [];
  return {
    id: Number(api?.id ?? m.id),
    name: String(api?.name ?? m.name),
    cover_url: String((api as { cover_url?: string })?.cover_url ?? m.cover_url ?? ""),
    services: services.map((s: { id: number; name: string; price: number; duration?: string }) => ({
      id: s.id,
      name: s.name,
      price: s.price,
      duration: s.duration || m.services?.find((x) => x.id === s.id)?.duration || "—"
    }))
  };
}

async function load() {
  loading.value = true;
  error.value = "";
  const mid = Number(route.params.merchantId);
  if (!Number.isFinite(mid) || mid < 1) {
    loading.value = false;
    router.replace("/services");
    return;
  }

  const mock = getMockMerchantById(mid);
  try {
    const data = await fetchMerchantDetail(mid);
    merchant.value = mergeMerchant(data as unknown as Record<string, unknown>, mock as MerchantVm | null);
  } catch {
    merchant.value = mock as MerchantVm | null;
  } finally {
    loading.value = false;
  }

  if (!merchant.value) return;

  booking.setMerchant({
    id: merchant.value.id,
    name: merchant.value.name,
    cover_url: merchant.value.cover_url
  });

  const svcs = merchant.value.services;
  const qRaw = route.query.serviceId;
  const qSid = Number(Array.isArray(qRaw) ? qRaw[0] : qRaw);

  if (Number.isFinite(qSid) && qSid > 0) {
    const svc = svcs.find((s) => s.id === qSid);
    if (svc && !booking.isMainOn(svc.id)) {
      booking.toggleMainService(toMain(svc));
    }
  }

  const keepAll = booking.mainServices.every((m) => svcs.some((s) => s.id === m.id));
  if (!keepAll) {
    booking.setMainServices(booking.mainServices.filter((m) => svcs.some((s) => s.id === m.id)));
  }

  petLocal.value = booking.petLabel;
  remarkLocal.value = booking.remark;
}

function goCheckout() {
  if (!canNext.value) return;
  router.push({ path: "/services/checkout" });
}

watch(petLocal, (v) => booking.setSchedule({ petLabel: v }));
watch(remarkLocal, (v) => booking.setSchedule({ remark: v }));

watch(
  () => [route.params.merchantId, route.query.serviceId],
  () => load(),
  { immediate: true }
);
</script>

<style scoped lang="scss">
.booking-page {
  max-width: 1120px;
  margin: 0 auto;
  padding-bottom: 48px;
}

.breadcrumb {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  margin-bottom: 14px;
  font-size: 15px;
  color: var(--muted);

  a {
    color: var(--primary-strong);
    font-weight: 600;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }

  .sep {
    opacity: 0.45;
  }

  .current {
    color: var(--text);
    font-weight: 600;
  }
}

.page-head {
  padding: 20px 22px;
  margin-bottom: 16px;

  h1 {
    margin: 0 0 8px;
    font-size: 24px;
    font-weight: 900;
    color: var(--text);
  }

  .sub {
    margin: 0;
    font-size: 15px;
    color: var(--muted);
  }
}

.booking-grid {
  display: grid;
  grid-template-columns: minmax(300px, 1fr) minmax(300px, 1fr);
  gap: 20px;
  align-items: stretch;
  min-height: 600px;
}

.col-services,
.col-schedule {
  padding: 22px 24px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.col-services h2,
.col-schedule h2 {
  margin: 0 0 16px;
  font-size: 18px;
  font-weight: 900;
  color: var(--text);
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 12px;
  border-bottom: 2px solid rgba(241, 124, 83, 0.15);
}

.schedule-steps {
  display: flex;
  align-items: center;
  gap: 0;
  margin: 0 0 18px;
  padding: 10px 14px;
  background: var(--surface-muted);
  border-radius: 10px;
  list-style: none;

  li {
    flex: 1;
    font-size: 12px;
    font-weight: 700;
    color: var(--muted);
    text-align: center;
    position: relative;
    padding: 4px 0;

    &::after {
      content: "›";
      position: absolute;
      right: -2px;
      top: 50%;
      transform: translateY(-50%);
      color: var(--border-warm);
      font-size: 16px;
    }

    &:last-child::after {
      display: none;
    }

    &.done {
      color: var(--primary-strong);

      &::before {
        content: "✓ ";
      }
    }
  }
}

.field {
  margin-bottom: 18px;

  label,
  .label {
    display: block;
    font-size: 13px;
    font-weight: 700;
    color: var(--muted);
    margin-bottom: 7px;
    letter-spacing: 0.2px;
  }
}

.input-like,
.textarea {
  width: 100%;
  padding: 10px 12px;
  border-radius: 12px;
  border: 1px solid var(--border-warm);
  background: var(--surface);
  font-size: 15px;
  font-family: inherit;
}

.textarea {
  resize: vertical;
  min-height: 80px;
}

.cal-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.cal-nav {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  border: 1px solid var(--border-warm);
  background: var(--surface);
  font-size: 20px;
  cursor: pointer;

  &:hover {
    background: var(--chip-bg);
  }
}

.cal-title {
  font-weight: 800;
  color: var(--text);
}

.cal-weekdays {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
  margin-bottom: 6px;
  font-size: 12px;
  font-weight: 700;
  color: var(--muted);
  text-align: center;
}

.cal-cells {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 6px;
}

.cal-out {
  aspect-ratio: 1;
  max-height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  color: var(--muted);
  opacity: 0.45;
}

.cal-cell {
  aspect-ratio: 1;
  max-height: 40px;
  border-radius: 10px;
  border: 1px solid transparent;
  background: var(--surface-muted);
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  color: var(--text);

  &.off {
    opacity: 0.3;
    cursor: not-allowed;
    text-decoration: line-through;
  }

  &.on {
    background: linear-gradient(135deg, var(--primary) 0%, var(--primary-strong) 100%);
    color: #fff;
    border-color: var(--primary-strong);
  }

  &:not(:disabled):not(.off):hover {
    border-color: var(--primary);
  }
}

.slots {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}

.slot {
  padding: 11px 8px;
  border-radius: 10px;
  border: 1.5px solid var(--border-warm);
  background: var(--surface);
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  text-align: center;
  transition: border-color 0.15s, background 0.15s, box-shadow 0.15s, transform 0.12s;

  &:hover {
    border-color: rgba(241, 124, 83, 0.4);
    transform: translateY(-1px);
    box-shadow: 0 2px 8px rgba(241, 124, 83, 0.1);
  }

  &.on {
    background: linear-gradient(135deg, var(--primary) 0%, var(--primary-strong) 100%);
    color: #fff;
    border-color: transparent;
    box-shadow: 0 3px 10px rgba(241, 124, 83, 0.3);
  }
}

.section-tip {
  margin: 0 0 10px;
  font-size: 13px;
  font-weight: 700;
  color: var(--text-heading-soft);
  letter-spacing: 0.2px;
}

.main-services {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
  margin-bottom: 22px;
}

.main-card {
  text-align: left;
  padding: 14px 16px;
  border-radius: 14px;
  border: 2px solid var(--border-warm);
  background: var(--surface);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  gap: 3px;
  transition: border-color 0.18s, box-shadow 0.18s, transform 0.15s;
  min-height: 88px;
  justify-content: center;
  position: relative;
  overflow: hidden;

  &::before {
    content: "";
    position: absolute;
    inset: 0;
    border-radius: 12px;
    opacity: 0;
    transition: opacity 0.18s;
    background: linear-gradient(135deg, rgba(241, 124, 83, 0.04), rgba(241, 124, 83, 0.08));
  }

  &:hover {
    border-color: rgba(241, 124, 83, 0.45);
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba(241, 124, 83, 0.12);
  }

  &.on {
    border-color: var(--primary);
    background: #fff8f3;
    box-shadow: 0 0 0 3px rgba(241, 124, 83, 0.18), 0 4px 16px rgba(241, 124, 83, 0.1);
    transform: translateY(-1px);

    &::before {
      opacity: 1;
    }

    .main-price {
      color: var(--primary-strong);
    }
  }
}

.main-name {
  font-size: 15px;
  font-weight: 800;
  color: var(--text);
  line-height: 1.3;
}

.main-meta {
  font-size: 12px;
  color: var(--muted);
  font-weight: 600;
}

.main-price {
  font-size: 18px;
  font-weight: 900;
  color: #ff5000;
  margin-top: 4px;
  transition: color 0.18s;
}

.package-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 22px;
}

.package-card {
  border: 2px solid var(--border-warm);
  border-radius: 14px;
  padding: 14px 16px;
  background: var(--surface);
  display: flex;
  flex-direction: column;
  gap: 10px;
  transition: border-color 0.18s, box-shadow 0.18s, transform 0.15s;

  &:hover {
    border-color: rgba(241, 124, 83, 0.35);
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba(241, 124, 83, 0.1);
  }

  &.on {
    border-color: var(--primary);
    background: #fff8f3;
    box-shadow: 0 0 0 3px rgba(241, 124, 83, 0.18);
    transform: translateY(-1px);
  }
}

.pkg-head h3 {
  margin: 0 0 5px;
  font-size: 15px;
  font-weight: 900;
  color: var(--text);
}

.pkg-save {
  margin: 0;
  font-size: 13px;
  color: var(--muted);
  line-height: 1.45;

  strong {
    color: #ff5000;
    font-size: 17px;
    font-weight: 900;
  }
}

.btn-pkg {
  align-self: flex-end;
  padding: 8px 18px;
  border-radius: 10px;
  border: 1.5px solid var(--primary);
  background: var(--surface);
  font-size: 13px;
  font-weight: 800;
  color: var(--primary-strong);
  cursor: pointer;
  transition: background 0.15s, box-shadow 0.15s;

  &:hover {
    background: var(--chip-bg);
    box-shadow: 0 2px 8px rgba(241, 124, 83, 0.15);
  }
}

.addon-panel {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.addon-group {
  &:last-child {
    margin-top: 14px;
  }

  h3 {
    margin: 0 0 10px;
    font-size: 13px;
    font-weight: 700;
    color: var(--muted);
    letter-spacing: 0.5px;
    display: flex;
    align-items: center;
    gap: 6px;

    &::before {
      content: "";
      display: inline-block;
      width: 3px;
      height: 12px;
      border-radius: 2px;
      background: linear-gradient(180deg, var(--primary), var(--primary-strong));
      flex-shrink: 0;
    }
  }
}

.addon-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}

.addon-chip {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
  padding: 10px 12px 9px;
  border-radius: 10px;
  border: 1.5px solid var(--border-warm);
  background: var(--surface);
  cursor: pointer;
  transition: border-color 0.15s, background 0.15s, box-shadow 0.15s, transform 0.12s;
  min-height: 58px;
  justify-content: center;

  &:hover {
    border-color: rgba(241, 124, 83, 0.45);
    transform: translateY(-1px);
    box-shadow: 0 3px 12px rgba(241, 124, 83, 0.12);
  }

  &.on {
    background: #fff8f4;
    border-color: var(--primary);
    box-shadow: 0 0 0 2px rgba(241, 124, 83, 0.18);
    transform: translateY(-1px);
  }
}

.addon-chip-name {
  font-size: 13px;
  font-weight: 700;
  color: var(--text);
  line-height: 1.3;
  white-space: normal;
  word-break: break-word;
}

.addon-chip-price {
  font-size: 13px;
  font-weight: 900;
  color: #ff5000;
  line-height: 1;

  &.free {
    color: #2e7d32;
    font-weight: 800;
  }
}

.addon-group.on .addon-chip-price {
  color: var(--primary-strong);
}

.sticky-summary {
  margin-top: auto;
  padding: 16px 0 4px;
  border-top: 1px solid var(--border-warm);
  flex-shrink: 0;
}

.sum-line {
  margin: 0 0 12px;
  font-size: 15px;
  color: var(--muted);

  strong {
    color: var(--text);
  }
}

.sum-price {
  font-size: 22px;
  color: #ff5000 !important;
  margin-left: 4px;
}

.sum-save {
  display: inline-block;
  margin-left: 8px;
  font-size: 13px;
  font-weight: 700;
  color: #2e7d32;
}

.sum-actions {
  display: flex;
  justify-content: flex-end;
}

.btn-next {
  min-width: 200px;
  width: auto;
  max-width: 100%;
  padding: 14px;
  border: none;
  border-radius: 14px;
  background: linear-gradient(135deg, #ff6b4a 0%, var(--primary-strong) 100%);
  color: #fff;
  font-size: 17px;
  font-weight: 900;
  cursor: pointer;

  &:disabled {
    opacity: 0.45;
    cursor: not-allowed;
  }

  &:not(:disabled):hover {
    filter: brightness(1.05);
  }
}

.sum-hint {
  margin: 8px 0 0;
  font-size: 13px;
  color: var(--muted);
}

@media (max-width: 900px) {
  .booking-grid {
    grid-template-columns: 1fr;
  }

  .main-services {
    grid-template-columns: 1fr;
  }

  .slots {
    grid-template-columns: repeat(3, 1fr);
  }

  .addon-list {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>
