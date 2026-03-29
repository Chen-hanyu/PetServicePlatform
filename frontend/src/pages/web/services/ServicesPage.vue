<template>
  <section class="card page">
    <div class="top-row">
      <h2 class="section-title">宠物服务预约</h2>
      <select v-model="selectedCategory" class="input" @change="loadMerchants">
        <option value="">全部分类</option>
        <option v-for="item in categories" :key="item.id" :value="item.name">{{ item.name }}</option>
      </select>
    </div>

    <DataState :loading="loading" :error="error" :empty="merchants.length === 0" empty-text="暂无商家">
      <div class="list">
        <article v-for="merchant in merchants" :key="merchant.id" class="row" @click="chooseMerchant(merchant.id)">
          <div>
            <h3>{{ merchant.name }}</h3>
            <p class="muted">{{ merchant.district }} · {{ merchant.address }}</p>
          </div>
          <StatusBadge variant="success">{{ merchant.status }}</StatusBadge>
        </article>
      </div>
    </DataState>

    <section v-if="detail" class="card inner">
      <h3>{{ detail.name }}</h3>
      <p class="muted">{{ detail.business_hours }} · {{ detail.phone }}</p>
      <form class="booking" @submit.prevent="submitBooking">
        <select v-model.number="booking.merchant_service_id" class="input">
          <option :value="0">选择服务项目</option>
          <option v-for="item in detail.services" :key="item.id" :value="item.id">{{ item.name }}（¥{{ item.price }}）</option>
        </select>
        <input v-model="booking.booking_time" type="datetime-local" class="input" />
        <input v-model.trim="booking.contact_name" placeholder="联系人" class="input" />
        <input v-model.trim="booking.contact_phone" placeholder="联系电话" class="input" />
        <textarea v-model.trim="booking.remark" class="input" placeholder="备注" />
        <button class="btn btn-primary" :disabled="submitting">{{ submitting ? "提交中..." : "提交预约" }}</button>
      </form>
    </section>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import DataState from "@/components/DataState.vue";
import StatusBadge from "@/components/StatusBadge.vue";
import { createBooking, fetchMerchantDetail, fetchMerchants, fetchServiceCategories } from "@/services/modules/services";
import { toErrorMessage } from "@/services/http";
import type { MerchantDetail, MerchantSummary, ServiceCategory } from "@/types/service";

const loading = ref(false);
const submitting = ref(false);
const error = ref("");
const categories = ref<ServiceCategory[]>([]);
const merchants = ref<MerchantSummary[]>([]);
const selectedCategory = ref("");
const detail = ref<MerchantDetail | null>(null);
const booking = reactive({ merchant_service_id: 0, booking_time: "", contact_name: "", contact_phone: "", remark: "" });

const loadMerchants = async () => {
  loading.value = true;
  try {
    const data = await fetchMerchants({ category: selectedCategory.value || undefined, page: 1, page_size: 10 });
    merchants.value = data.list || [];
  } catch (e) {
    error.value = toErrorMessage(e);
  } finally {
    loading.value = false;
  }
};

const chooseMerchant = async (id: number) => {
  try {
    detail.value = await fetchMerchantDetail(id);
  } catch (e) {
    error.value = toErrorMessage(e);
  }
};

const submitBooking = async () => {
  if (!detail.value || booking.merchant_service_id === 0) return;
  submitting.value = true;
  try {
    await createBooking({ merchant_id: detail.value.id, merchant_service_id: booking.merchant_service_id, booking_time: booking.booking_time, contact_name: booking.contact_name, contact_phone: booking.contact_phone, remark: booking.remark });
    booking.merchant_service_id = 0;
    booking.booking_time = "";
    booking.contact_name = "";
    booking.contact_phone = "";
    booking.remark = "";
  } catch (e) {
    error.value = toErrorMessage(e);
  } finally {
    submitting.value = false;
  }
};

onMounted(async () => {
  try { categories.value = await fetchServiceCategories(); } catch { categories.value = []; }
  await loadMerchants();
});
</script>

<style scoped lang="scss">
.page { display: grid; gap: 14px; }
.top-row { display: flex; justify-content: space-between; gap: 10px; }
.list { display: grid; gap: 10px; }
.row { border: 1px solid #e4eeea; border-radius: 12px; padding: 10px; display: flex; justify-content: space-between; align-items: center; cursor: pointer; }
.row h3 { margin: 0; }
.inner { border: 1px dashed #d5e8e1; }
.booking { display: grid; gap: 8px; }
textarea { min-height: 70px; }
</style>
