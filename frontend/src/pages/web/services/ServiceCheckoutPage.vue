<template>
  <div class="svc-checkout">
    <nav class="breadcrumb card" aria-label="面包屑导航">
      <RouterLink to="/home">首页</RouterLink>
      <span class="sep">/</span>
      <RouterLink to="/services">宠物服务</RouterLink>
      <span class="sep">/</span>
      <span class="current">确认预约</span>
    </nav>

    <div v-if="done" class="success-card card">
      <p class="ok-ico" aria-hidden="true">✓</p>
      <h1>预约已提交</h1>
      <p class="sub">我们已收到您的预约（演示环境，未真实扣款）</p>
      <div class="actions">
        <RouterLink to="/services" class="btn-outline">返回服务</RouterLink>
        <RouterLink to="/profile/bookings" class="btn-primary">我的预约</RouterLink>
      </div>
    </div>

    <template v-else>
      <div v-if="!booking.hasDraft" class="empty-card card">
        <p>暂无可确认的预约</p>
        <RouterLink to="/services" class="link">去选择商家</RouterLink>
      </div>

      <template v-else-if="!booking.scheduleReady">
        <div class="empty-card card">
          <p>请先选择预约日期与时间段</p>
          <RouterLink v-if="booking.merchantId" :to="`/services/book/${booking.merchantId}`" class="link">
            去完善预约
          </RouterLink>
        </div>
      </template>

      <template v-else>
        <section class="fee-outer card">
          <div class="fee-inner">
            <h2 class="fee-title"><span aria-hidden="true">📋</span> 费用明细</h2>
            <div class="fee-rows">
              <div class="fee-row">
                <span>主服务（{{ booking.mainServices.length }}项）</span>
                <span>
                  <template v-if="booking.packageSavings > 0">
                    <s class="strike">¥{{ formatPrice(booking.mainServicesSubtotal) }}</s>
                    <span class="fee-after"> ¥{{ formatPrice(booking.mainTotal) }}</span>
                  </template>
                  <template v-else>¥{{ formatPrice(booking.mainTotal) }}</template>
                </span>
              </div>
              <div v-if="booking.packageSavings > 0" class="fee-row save">
                <span>套餐优惠</span>
                <span>−¥{{ formatPrice(booking.packageSavings) }}</span>
              </div>
              <div v-if="booking.addonsTotal > 0" class="fee-row">
                <span>护理加项</span>
                <span>¥{{ formatPrice(booking.addonsTotal) }}</span>
              </div>
              <div v-else class="fee-row muted">
                <span>护理加项</span>
                <span>¥0</span>
              </div>
            </div>
            <div class="fee-total">
              <span>总计</span>
              <strong>¥{{ formatPrice(booking.totalAmount) }}</strong>
            </div>
          </div>

          <div class="schedule-block">
            <p><strong>{{ booking.merchantName }}</strong></p>
            <p v-if="booking.mainServices.length">
              {{ booking.mainServices.map((m) => m.name).join("、") }} · {{ booking.bookingDate }}
              {{ booking.timeSlot }}
            </p>
            <p class="pet-line">{{ booking.petLabel }}</p>
          </div>

          <div class="contact-block">
            <label for="c-name">联系人</label>
            <input id="c-name" v-model="nameLocal" type="text" class="inp" placeholder="请输入姓名" autocomplete="name" />
            <label for="c-phone">联系电话</label>
            <input id="c-phone" v-model="phoneLocal" type="tel" class="inp" placeholder="请输入手机号" autocomplete="tel" />
          </div>

          <button type="button" class="btn-confirm" :disabled="submitting || !nameLocal.trim() || !phoneLocal.trim()" @click="submit">
            <span aria-hidden="true">📅</span>
            确认预约 ¥{{ formatPrice(booking.totalAmount) }}
          </button>
          <p v-if="errorMessage" class="checkout-error">{{ errorMessage }}</p>
          <p class="tip">演示收银台 · 不发起真实支付与短信</p>
        </section>
      </template>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { useServiceBookingStore } from "@/store/serviceBooking";
import { createBooking } from "@/api/modules/services";
import { toErrorMessage } from "@/api/http";
import { useAuthStore } from "@/store/auth";

const booking = useServiceBookingStore();
const auth = useAuthStore();

const done = ref(false);
const submitting = ref(false);
const nameLocal = ref(booking.contactName || auth.user?.nickname || "");
const phoneLocal = ref(booking.contactPhone || auth.user?.phone || "");
const errorMessage = ref("");

if (!booking.contactName || !booking.contactPhone) {
  booking.setContact(nameLocal.value, phoneLocal.value);
}

const formatPrice = (n: number) => (Number.isInteger(n) ? String(n) : n.toFixed(2));

watch(nameLocal, (v) => booking.setContact(v, phoneLocal.value));
watch(phoneLocal, (v) => booking.setContact(nameLocal.value, v));

async function submit() {
  if (!booking.mainServices.length || !booking.merchantId) return;
  submitting.value = true;
  errorMessage.value = "";
  const mainLine = `主服务：${booking.mainServices.map((m) => m.name).join("、")}`;
  const pkgLine = booking.appliedPackage ? `套餐：${booking.appliedPackage.name}` : "";
  const addonText = booking.addons.map((a) => `${a.name}(¥${a.price})`).join("、");
  const remark = [booking.remark, mainLine, pkgLine, addonText ? `加项：${addonText}` : ""]
    .filter(Boolean)
    .join(" | ");
  const booking_time = `${booking.bookingDate}T${booking.timeSlot}:00`;
  try {
    await createBooking({
      merchant_id: booking.merchantId,
      merchant_service_id: booking.mainServices[0].id,
      booking_time,
      contact_name: nameLocal.value.trim(),
      contact_phone: phoneLocal.value.trim(),
      remark: remark || undefined
    });
    booking.clear();
    done.value = true;
  } catch (error) {
    errorMessage.value = toErrorMessage(error);
  } finally {
    submitting.value = false;
  }
}
</script>

<style scoped lang="scss">
.svc-checkout {
  max-width: 560px;
  margin: 0 auto;
  padding-bottom: 40px;
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

.success-card {
  text-align: center;
  padding: 40px 24px;

  .ok-ico {
    width: 56px;
    height: 56px;
    margin: 0 auto 16px;
    border-radius: 50%;
    background: #e8f5e9;
    color: #2e7d32;
    font-size: 32px;
    line-height: 56px;
    font-weight: 800;
  }

  h1 {
    margin: 0 0 8px;
    font-size: 24px;
    font-weight: 900;
  }

  .sub {
    margin: 0 0 24px;
    color: var(--muted);
    font-size: 15px;
  }

  .actions {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    justify-content: center;
  }
}

.btn-outline {
  padding: 12px 20px;
  border-radius: 12px;
  border: 1px solid var(--border-warm);
  background: var(--surface);
  font-weight: 800;
  color: var(--text);
  text-decoration: none;

  &:hover {
    background: var(--chip-bg);
  }
}

.btn-primary {
  padding: 12px 20px;
  border-radius: 12px;
  border: none;
  background: linear-gradient(135deg, #ff6b4a 0%, var(--primary-strong) 100%);
  font-weight: 900;
  color: #fff;
  text-decoration: none;

  &:hover {
    filter: brightness(1.05);
  }
}

.empty-card {
  padding: 32px 20px;
  text-align: center;
  font-size: 16px;
  font-weight: 700;
  color: var(--muted);

  .link {
    display: inline-block;
    margin-top: 12px;
    color: var(--primary-strong);
    font-weight: 800;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
}

.fee-outer {
  padding: 28px 24px 32px;
}

.fee-inner {
  background: linear-gradient(180deg, #fff9ed 0%, #fff4e0 100%);
  border: 1px solid #f5d9a8;
  border-radius: 16px;
  padding: 20px 22px;
  margin-bottom: 20px;
}

.fee-title {
  margin: 0 0 16px;
  font-size: 18px;
  font-weight: 900;
  color: var(--primary-strong);
  display: flex;
  align-items: center;
  gap: 8px;
}

.fee-rows {
  border-top: 1px solid rgba(212, 165, 116, 0.35);
  border-bottom: 1px solid rgba(212, 165, 116, 0.35);
}

.fee-row {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  font-size: 15px;
  font-weight: 600;
  color: var(--text);

  &.muted {
    color: var(--muted);
    font-weight: 500;
  }

  &.save {
    color: #2e7d32;
    font-weight: 700;
  }
}

.strike {
  opacity: 0.55;
  font-weight: 500;
  margin-right: 6px;
}

.fee-after {
  font-weight: 800;
  color: #ff5000;
}

.fee-total {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  margin-top: 14px;
  font-size: 16px;
  font-weight: 800;
  color: var(--text);

  strong {
    font-size: 26px;
    color: #ff5000;
  }
}

.schedule-block {
  margin-bottom: 20px;
  padding: 14px 16px;
  border-radius: 12px;
  background: var(--surface-tint);
  border: 1px solid var(--border-warm);
  font-size: 14px;
  line-height: 1.5;
  color: var(--text);

  p {
    margin: 0 0 6px;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .pet-line {
    color: var(--muted);
    font-weight: 600;
  }
}

.contact-block {
  margin-bottom: 20px;

  label {
    display: block;
    font-size: 13px;
    font-weight: 700;
    color: var(--muted);
    margin-bottom: 6px;
  }

  .inp {
    width: 100%;
    margin-bottom: 12px;
    padding: 10px 12px;
    border-radius: 12px;
    border: 1px solid var(--border-warm);
    font-size: 15px;
    font-family: inherit;
  }
}

.btn-confirm {
  width: 100%;
  padding: 16px;
  border: none;
  border-radius: 14px;
  background: linear-gradient(90deg, #ff8a4c 0%, #c45c2a 100%);
  color: #fff;
  font-size: 18px;
  font-weight: 900;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;

  &:disabled {
    opacity: 0.45;
    cursor: not-allowed;
  }

  &:not(:disabled):hover {
    filter: brightness(1.05);
  }
}

.tip {
  margin: 12px 0 0;
  text-align: center;
  font-size: 13px;
  color: var(--muted);
}

.checkout-error {
  margin: 12px 0 0;
  text-align: center;
  font-size: 13px;
  font-weight: 700;
  color: #e65f4f;
}
</style>
