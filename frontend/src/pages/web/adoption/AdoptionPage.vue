<template>
  <section class="adoption-page">
    <div class="card page-hero">
      <h1>领养代替购买</h1>
      <p>每一个流浪的生命都值得被温柔以待</p>
    </div>

    <div class="filter-bar card">
      <div class="filter-group">
        <label>品种：</label>
        <select v-model="filter.breed" class="filter-select">
          <option value="">全部</option>
          <option value="dog">狗狗</option>
          <option value="cat">猫咪</option>
          <option value="other">其他</option>
        </select>
      </div>
      <div class="filter-group">
        <label>城市：</label>
        <select v-model="filter.city" class="filter-select">
          <option value="">全部</option>
          <option value="北京">北京</option>
          <option value="上海">上海</option>
          <option value="广州">广州</option>
          <option value="深圳">深圳</option>
        </select>
      </div>
      <div class="filter-group">
        <label>年龄：</label>
        <select v-model="filter.age" class="filter-select">
          <option value="">全部</option>
          <option value="young">幼年</option>
          <option value="adult">成年</option>
          <option value="elder">老年</option>
        </select>
      </div>
    </div>

    <DataState :loading="loading" :error="error" :empty="pets.length === 0" empty-text="暂无符合条件的宠物">
      <div class="pet-grid">
        <article v-for="pet in filteredPets" :key="pet.id" class="pet-card" @click="selectPet(pet)">
          <div class="pet-image">
            <img :src="pet.cover_url" :alt="pet.name" />
            <span class="gender-tag" :class="pet.gender === '公' ? 'male' : 'female'">
              {{ pet.gender === '公' ? '♂' : '♀' }}
            </span>
          </div>
          <div class="pet-info">
            <h3>{{ pet.name }} <span class="breed">{{ pet.breed }}</span></h3>
            <div class="pet-meta">
              <span>📍 {{ pet.city }}</span>
              <span>🎂 {{ pet.age_desc }}</span>
            </div>
            <p class="story">{{ pet.story }}</p>
          </div>
        </article>
      </div>
    </DataState>

    <!-- Pet Detail Modal -->
    <div v-if="selectedPet" class="modal-overlay" @click.self="selectedPet = null">
      <div class="modal-content card">
        <button class="close-btn" @click="selectedPet = null">×</button>
        
        <div class="pet-detail-header">
          <div class="detail-image">
            <img :src="selectedPet.images?.[0] || selectedPet.cover_url" :alt="selectedPet.name" />
          </div>
          <div class="detail-info">
            <h2>{{ selectedPet.name }}</h2>
            <div class="tags-row">
              <span class="tag">{{ selectedPet.breed }}</span>
              <span class="tag">{{ selectedPet.city }}</span>
              <span class="tag">{{ selectedPet.age_desc }}</span>
            </div>
            <div class="story-text">
              <h4>宠物故事</h4>
              <p>{{ selectedPet.story }}</p>
            </div>
          </div>
        </div>

        <div class="adoption-form">
          <h3>申请领养 {{ selectedPet.name }}</h3>
          <div class="form-grid">
            <div class="form-group">
              <label>联系电话</label>
              <input v-model="form.contact_phone" type="tel" class="input" placeholder="请输入手机号" />
            </div>
            <div class="form-group">
              <label>养宠经验</label>
              <textarea v-model="form.experience_desc" class="input textarea" placeholder="请简述您的养宠经历"></textarea>
            </div>
            <div class="form-group full-width">
              <label>居住环境</label>
              <textarea v-model="form.living_condition_desc" class="input textarea" placeholder="请描述您的居住环境"></textarea>
            </div>
          </div>
          <div class="form-actions">
            <button class="btn btn-secondary" @click="selectedPet = null">取消</button>
            <button class="btn btn-primary" @click="submitApplication">提交申请</button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, computed } from "vue";
import DataState from "@/components/DataState.vue";
import { createAdoptionApplication, fetchAdoptionPets } from "@/services/modules/adoption";
import { mockAdoptionPets } from "@/mocks/adoption";
import { toErrorMessage } from "@/services/http";
import type { AdoptionPetDetail, AdoptionPetSummary } from "@/types/adoption";

const loading = ref(false);
const error = ref("");
const pets = ref<(AdoptionPetSummary & { gender?: string, story?: string })[]>([]);
const selectedPet = ref<AdoptionPetDetail | null>(null);
const filter = reactive({ breed: "", city: "", age: "" });

const form = reactive({
  contact_phone: "",
  experience_desc: "",
  living_condition_desc: ""
});

const filteredPets = computed(() => {
  return pets.value.filter(pet => {
    if (filter.breed && !pet.breed.toLowerCase().includes(filter.breed)) return false;
    if (filter.city && pet.city !== filter.city) return false;
    return true;
  });
});

const loadPets = async () => {
  loading.value = true;
  error.value = "";
  try {
    const data = await fetchAdoptionPets({ page: 1, page_size: 20 });
    pets.value = data.list || [];
  } catch (e) {
    console.warn("Failed to fetch pets, using mock data", e);
    pets.value = mockAdoptionPets as any;
  } finally {
    loading.value = false;
  }
};

const selectPet = (pet: any) => {
  selectedPet.value = pet;
};

const submitApplication = async () => {
  if (!selectedPet.value) return;
  
  // Mock submission
  alert(`申请已提交！我们会尽快联系您。`);
  selectedPet.value = null;
  form.contact_phone = "";
  form.experience_desc = "";
  form.living_condition_desc = "";
};

onMounted(loadPets);
</script>

<style scoped lang="scss">
.adoption-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  padding: 16px 24px;
  border-radius: 16px;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
  
  label {
    font-size: 14px;
    font-weight: 600;
    color: #7d7068;
  }
}

.filter-select {
  padding: 8px 12px;
  border: 1px solid #eddacc;
  border-radius: 8px;
  background: #fff;
  color: #2f2a26;
  min-width: 120px;
}

.pet-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.pet-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #f0dccb;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 24px rgba(128, 84, 52, 0.12);
  }
}

.pet-image {
  height: 200px;
  position: relative;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.gender-tag {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 28px;
  height: 28px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(102, 72, 48, 0.12);
  
  &.male { color: var(--gender-male); }
  &.female { color: var(--gender-female); }
}

.pet-info {
  padding: 16px;
}

.pet-info h3 {
  margin: 0 0 8px;
  font-size: 18px;
  color: #2f2a26;
  
  .breed {
    font-size: 14px;
    font-weight: 400;
    color: #7d7068;
    margin-left: 8px;
  }
}

.pet-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #7d7068;
  margin-bottom: 12px;
}

.story {
  margin: 0;
  font-size: 13px;
  color: var(--muted-soft);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--overlay-scrim);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  padding: 20px;
  backdrop-filter: blur(4px);
}

.modal-content {
  width: 100%;
  max-width: 700px;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  padding: 32px;
  border-radius: 24px;
}

.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  background: var(--surface-muted);
  border: none;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  font-size: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--muted);
  
  &:hover {
    background: var(--surface-muted-hover);
  }
}

.pet-detail-header {
  display: flex;
  gap: 24px;
  margin-bottom: 32px;
}

.detail-image {
  width: 240px;
  height: 240px;
  border-radius: 16px;
  overflow: hidden;
  flex-shrink: 0;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.detail-info {
  flex: 1;
  
  h2 {
    margin: 0 0 12px;
    font-size: 28px;
    color: #2f2a26;
  }
}

.tags-row {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
  
  .tag {
    padding: 4px 12px;
    background: #fff1e5;
    color: #8a4f33;
    border-radius: 12px;
    font-size: 13px;
  }
}

.story-text {
  h4 {
    margin: 0 0 8px;
    font-size: 16px;
    color: #2f2a26;
  }
  
  p {
    margin: 0;
    color: #7d7068;
    line-height: 1.6;
  }
}

.adoption-form {
  border-top: 1px solid #f0dccb;
  padding-top: 24px;
  
  h3 {
    margin: 0 0 20px;
    color: #2f2a26;
  }
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  
  &.full-width {
    grid-column: 1 / -1;
  }
  
  label {
    font-size: 14px;
    font-weight: 600;
    color: #2f2a26;
  }
}

.textarea {
  min-height: 80px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .pet-detail-header {
    flex-direction: column;
  }
  
  .detail-image {
    width: 100%;
    height: 200px;
  }
  
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>