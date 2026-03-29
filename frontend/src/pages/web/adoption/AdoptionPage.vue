<template>
  <section class="card page">
    <h2 class="section-title">领养中心</h2>
    <p class="muted">寻找有缘宠物，提交领养申请。</p>

    <DataState :loading="loading" :error="error" :empty="pets.length === 0" empty-text="暂无待领养宠物">
      <div class="grid">
        <article v-for="pet in pets" :key="pet.id" class="pet-card" @click="selectPet(pet.id)">
          <img v-if="pet.cover_url" :src="pet.cover_url" :alt="pet.name" />
          <div>
            <h3>{{ pet.name }}</h3>
            <p class="muted">{{ pet.breed }} · {{ pet.city }} · {{ pet.age_desc }}</p>
          </div>
          <StatusBadge variant="info">{{ pet.status }}</StatusBadge>
        </article>
      </div>
    </DataState>

    <section v-if="detail" class="card inner">
      <h3>{{ detail.name }}</h3>
      <p class="muted">{{ detail.story || "暂无故事介绍" }}</p>
      <p>领养要求：{{ detail.adoption_requirements || "暂无" }}</p>

      <form class="form" @submit.prevent="submitApplication">
        <input v-model.trim="form.contact_phone" class="input" placeholder="联系电话（11位手机号）" />
        <textarea v-model.trim="form.experience_desc" class="input" placeholder="养宠经验" />
        <textarea v-model.trim="form.living_condition_desc" class="input" placeholder="居住情况" />
        <button class="btn btn-primary" :disabled="submitting">{{ submitting ? "提交中..." : "提交领养申请" }}</button>
      </form>
    </section>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import DataState from "@/components/DataState.vue";
import StatusBadge from "@/components/StatusBadge.vue";
import { createAdoptionApplication, fetchAdoptionPetDetail, fetchAdoptionPets } from "@/services/modules/adoption";
import { toErrorMessage } from "@/services/http";
import type { AdoptionPetDetail, AdoptionPetSummary } from "@/types/adoption";

const loading = ref(false);
const submitting = ref(false);
const error = ref("");
const pets = ref<AdoptionPetSummary[]>([]);
const detail = ref<AdoptionPetDetail | null>(null);
const form = reactive({ contact_phone: "", experience_desc: "", living_condition_desc: "" });

const loadPets = async () => {
  loading.value = true;
  error.value = "";
  try {
    const data = await fetchAdoptionPets({ page: 1, page_size: 12 });
    pets.value = data.list || [];
  } catch (e) {
    error.value = toErrorMessage(e);
  } finally {
    loading.value = false;
  }
};

const selectPet = async (petId: number) => {
  try {
    detail.value = await fetchAdoptionPetDetail(petId);
  } catch (e) {
    error.value = toErrorMessage(e);
  }
};

const submitApplication = async () => {
  if (!detail.value) return;
  submitting.value = true;
  try {
    await createAdoptionApplication({ pet_id: detail.value.id, ...form });
    form.contact_phone = "";
    form.experience_desc = "";
    form.living_condition_desc = "";
  } catch (e) {
    error.value = toErrorMessage(e);
  } finally {
    submitting.value = false;
  }
};

onMounted(loadPets);
</script>

<style scoped lang="scss">
.page { display: grid; gap: 14px; }
.grid { display: grid; gap: 12px; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); }
.pet-card { border: 1px solid #e4eeea; border-radius: 14px; padding: 12px; display: grid; gap: 8px; cursor: pointer; background: #fbfefd; }
.pet-card img { width: 100%; height: 140px; object-fit: cover; border-radius: 10px; }
.pet-card h3 { margin: 0; }
.inner { border: 1px dashed #d6e9e2; }
.form { display: grid; gap: 8px; }
textarea { min-height: 72px; }
</style>
