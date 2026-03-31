<template>
  <section class="page">
    <h2 class="section-title">后台仪表盘</h2>
    <DataState :loading="loading" :error="error" :empty="!overview" empty-text="暂无统计数据">
      <div class="cards" v-if="overview">
        <article class="metric card"><span>用户总数</span><strong>{{ overview.user_total }}</strong></article>
        <article class="metric card"><span>帖子总数</span><strong>{{ overview.post_total }}</strong></article>
        <article class="metric card"><span>订单总数</span><strong>{{ overview.order_total }}</strong></article>
        <article class="metric card"><span>预约总数</span><strong>{{ overview.booking_total }}</strong></article>
      </div>
      <div class="cards" v-if="overview">
        <article class="metric card warn"><span>待审帖子</span><strong>{{ overview.pending_post_count }}</strong></article>
        <article class="metric card warn"><span>待审领养</span><strong>{{ overview.pending_adoption_count }}</strong></article>
      </div>
    </DataState>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import DataState from "@/components/DataState.vue";
import { fetchAdminDashboard } from "@/api/modules/admin";
import { toErrorMessage } from "@/api/http";
import type { DashboardOverview } from "@/types/admin";

const loading = ref(false);
const error = ref("");
const overview = ref<DashboardOverview | null>(null);

onMounted(async () => {
  loading.value = true;
  try {
    overview.value = await fetchAdminDashboard();
  } catch (e) {
    error.value = toErrorMessage(e);
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped lang="scss">
.page { display: grid; gap: 12px; }
.cards { display: grid; gap: 10px; grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); }
.metric { border: 1px solid #dbe7e3; }
.metric span { color: #617771; }
.metric strong { display: block; margin-top: 6px; font-size: 28px; }
.warn { background: #fff9ec; }
</style>
