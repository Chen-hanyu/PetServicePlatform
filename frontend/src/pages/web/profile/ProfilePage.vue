<template>
  <section class="card page">
    <h2 class="section-title">个人中心</h2>
    <DataState :loading="loading" :error="error" :empty="!overview && pets.length === 0" empty-text="暂无个人数据">
      <div class="stats" v-if="overview">
        <article class="stat-card"><span>宠物</span><strong>{{ overview.pet_count }}</strong></article>
        <article class="stat-card"><span>订单</span><strong>{{ overview.order_count }}</strong></article>
        <article class="stat-card"><span>预约</span><strong>{{ overview.booking_count }}</strong></article>
        <article class="stat-card"><span>未读消息</span><strong>{{ overview.unread_message_count }}</strong></article>
      </div>

      <section class="card inner" v-if="overview?.user">
        <h3>{{ overview.user.nickname }}</h3>
        <p class="muted">手机号：{{ overview.user.phone || "未填写" }}</p>
      </section>

      <section class="card inner">
        <h3>我的宠物</h3>
        <ul>
          <li v-for="pet in pets" :key="pet.id">{{ pet.name }}（{{ pet.type }}）</li>
        </ul>
        <form class="pet-form" @submit.prevent="submitPet">
          <input v-model.trim="petForm.name" class="input" placeholder="宠物名" />
          <input v-model.trim="petForm.type" class="input" placeholder="类型（猫/狗）" />
          <input v-model.trim="petForm.breed" class="input" placeholder="品种" />
          <button class="btn btn-primary" :disabled="saving">{{ saving ? "保存中..." : "新增宠物" }}</button>
        </form>
      </section>
    </DataState>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import DataState from "@/components/DataState.vue";
import { createPet, fetchMyPets } from "@/services/modules/pet";
import { fetchOverview } from "@/services/modules/profile";
import { toErrorMessage } from "@/services/http";
import type { ProfileOverview } from "@/types/auth";
import type { PetProfile } from "@/types/pet";

const loading = ref(false);
const saving = ref(false);
const error = ref("");
const overview = ref<ProfileOverview | null>(null);
const pets = ref<PetProfile[]>([]);
const petForm = reactive({ name: "", type: "", breed: "" });

const loadData = async () => {
  loading.value = true;
  try {
    overview.value = await fetchOverview();
    pets.value = await fetchMyPets();
  } catch (e) {
    error.value = toErrorMessage(e);
  } finally {
    loading.value = false;
  }
};

const submitPet = async () => {
  if (!petForm.name || !petForm.type) return;
  saving.value = true;
  try {
    await createPet({ ...petForm });
    petForm.name = "";
    petForm.type = "";
    petForm.breed = "";
    pets.value = await fetchMyPets();
  } catch (e) {
    error.value = toErrorMessage(e);
  } finally {
    saving.value = false;
  }
};

onMounted(loadData);
</script>

<style scoped lang="scss">
.page { display: grid; gap: 14px; }
.stats { display: grid; gap: 10px; grid-template-columns: repeat(auto-fit, minmax(130px, 1fr)); }
.stat-card { border: 1px solid #e4efea; background: #f8fcfa; border-radius: 12px; padding: 12px; display: grid; gap: 4px; }
.stat-card strong { font-size: 20px; }
.inner { border: 1px dashed #d5e8e1; }
.pet-form { margin-top: 10px; display: grid; gap: 8px; }
</style>
