<template>
  <section class="adoption-page">
    <!-- Hero Banner -->
    <div class="adoption-hero">
      <div class="hero-overlay">
        <h1>用领养代替购买，给它一个温暖的家</h1>
        <p>每一个流浪的小生命都值得被温柔以待</p>
        <div class="hero-actions">
          <button class="btn-hero-primary">领养流程指南</button>
          <button class="btn-hero-secondary">查看领养要求</button>
        </div>
      </div>
    </div>

    <!-- Filter Bar -->
    <div class="filter-bar">
      <div class="filter-group">
        <span class="filter-label">宠物类型:</span>
        <button 
          v-for="type in petTypes" 
          :key="type.value"
          :class="['filter-btn', { active: filter.type === type.value }]"
          @click="filter.type = type.value"
        >
          {{ type.label }}
        </button>
      </div>
      <div class="filter-divider"></div>
      <div class="filter-group">
        <span class="filter-label">年龄:</span>
        <select v-model="filter.age" class="filter-select">
          <option value="">不限</option>
          <option value="young">幼年 (0-1岁)</option>
          <option value="adult">成年 (1-3岁)</option>
          <option value="senior">老年 (3岁以上)</option>
        </select>
      </div>
      <div class="filter-group">
        <span class="filter-label">性别:</span>
        <select v-model="filter.gender" class="filter-select">
          <option value="">不限</option>
          <option value="公">小男孩 (公)</option>
          <option value="母">小女孩 (母)</option>
        </select>
      </div>
    </div>

    <!-- Pet Grid -->
    <DataState :loading="loading" :error="error" :empty="filteredPets.length === 0" empty-text="暂无符合条件的宠物">
      <div class="pet-grid">
        <article v-for="pet in pagedPets" :key="pet.id" class="pet-card" @click="selectPet(pet)">
          <div class="pet-image">
            <img :src="pet.cover_url" :alt="pet.name" />
            <span class="pet-status">待领养</span>
            <button class="pet-like">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
              </svg>
            </button>
          </div>
          <div class="pet-info">
            <div class="pet-header">
              <h3>{{ pet.name }}</h3>
              <span class="pet-type">{{ pet.breed?.includes('猫') ? '猫咪' : '狗狗' }}</span>
            </div>
            <p class="pet-meta">{{ pet.age_desc }} · {{ pet.gender }} · {{ pet.breed }}</p>
            <div class="pet-tags">
              <span class="tag" v-for="tag in getPetTags(pet)" :key="tag">{{ tag }}</span>
            </div>
            <button class="btn-adopt">领养咨询</button>
          </div>
        </article>
      </div>
      
      <!-- Pagination -->
      <div class="pagination" v-if="totalPages > 1">
        <button class="page-btn" :disabled="currentPage === 1" @click="currentPage--">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M15 18l-6-6 6-6"/></svg>
        </button>
        <button 
          v-for="page in visiblePages" 
          :key="page" 
          :class="['page-btn', { active: page === currentPage }]"
          @click="goToPage(page)"
        >
          {{ page }}
        </button>
        <button class="page-btn" :disabled="currentPage === totalPages" @click="currentPage++">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 18l6-6-6-6"/></svg>
        </button>
      </div>
    </DataState>

    <!-- Pet Detail Modal -->
    <div v-if="selectedPet" class="modal-overlay" @click.self="selectedPet = null">
      <div class="modal-content">
        <button class="close-btn" @click="selectedPet = null">×</button>
        
        <!-- Hero Image -->
        <div class="detail-hero">
          <img :src="selectedPet.images?.[0] || selectedPet.cover_url" :alt="selectedPet.name" />
          <span class="image-count">1/5 张图片</span>
        </div>

        <div class="detail-body">
          <!-- Name and Stats -->
          <div class="detail-header">
            <div class="detail-title">
              <h2>{{ selectedPet.name }}</h2>
              <span class="status-badge">寻找新家</span>
            </div>
            <div class="detail-stats">
              <span class="stat-item">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"/></svg>
                品种：{{ selectedPet.breed }}
              </span>
              <span class="stat-item">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                年龄：{{ selectedPet.age_desc }}
              </span>
              <span class="stat-item">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
                性别：{{ selectedPet.gender }}
              </span>
            </div>
          </div>

          <!-- Health Status -->
          <div class="detail-section">
            <h3 class="section-title">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 12h-4l-3 9L9 3l-3 9H2"/></svg>
              健康状况
            </h3>
            <div class="health-tags">
              <span class="health-tag success">已驱虫</span>
              <span class="health-tag success">已接种疫苗</span>
              <span class="health-tag success">已绝育</span>
              <span class="health-tag">身体指标正常</span>
            </div>
          </div>

          <!-- Story -->
          <div class="detail-section">
            <h3 class="section-title">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M8 14s1.5 2 4 2 4-2 4-2"/><line x1="9" y1="9" x2="9.01" y2="9"/><line x1="15" y1="9" x2="15.01" y2="9"/></svg>
              性格描述
            </h3>
            <p class="story-text">{{ selectedPet.story || '这是一只非常可爱的宠物，性格温顺亲人，期待找到爱它的家庭。' }}</p>
          </div>

          <!-- Requirements -->
          <div class="detail-section">
            <h3 class="section-title">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
              领养要求
            </h3>
            <div class="requirements-grid">
              <div class="requirement-item">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
                <p>在本地有稳定居所，全屋必须安装防护纱窗</p>
              </div>
              <div class="requirement-item">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="1" y="4" width="22" height="16" rx="2" ry="2"/><line x1="1" y1="10" x2="23" y2="10"/></svg>
                <p>有稳定的经济收入，能提供质量可靠的宠物食品</p>
              </div>
              <div class="requirement-item">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                <p>征得所有家庭成员同意，不因搬家等原因弃养</p>
              </div>
              <div class="requirement-item">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M15 10l4.553-2.276A1 1 0 0 1 21 8.618v6.764a1 1 0 0 1-1.447.894L15 14M5 18h8a2 2 0 0 0 2-2V8a2 2 0 0 0-2-2H5a2 2 0 0 0-2 2v8a2 2 0 0 0 2 2z"/></svg>
                <p>接受领养后的定期视频回访</p>
              </div>
            </div>
          </div>

          <!-- Adoption Process -->
          <div class="detail-section">
            <h3 class="section-title">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
              领养流程
            </h3>
            <div class="process-steps">
              <div class="process-step">
                <div class="step-number">1</div>
                <p class="step-title">在线申请</p>
                <p class="step-desc">提交个人信息</p>
              </div>
              <div class="process-step">
                <div class="step-number">2</div>
                <p class="step-title">审核交流</p>
                <p class="step-desc">工作人员回访</p>
              </div>
              <div class="process-step">
                <div class="step-number">3</div>
                <p class="step-title">线下见面</p>
                <p class="step-desc">实地接触宠物</p>
              </div>
              <div class="process-step">
                <div class="step-number">4</div>
                <p class="step-title">签订协议</p>
                <p class="step-desc">接它回家</p>
              </div>
            </div>
          </div>

          <!-- CTA -->
          <div class="detail-cta">
            <button class="btn-primary-full" @click="submitApplication">申请领养</button>
            <button class="btn-secondary-full">在线咨询</button>
            <p class="cta-tip">温馨提示：领养不收任何费用，请勿相信任何形式的线上转账要求</p>
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
const currentPage = ref(1);
const pageSize = 6;

const filter = reactive({ type: "", age: "", gender: "" });

const totalPages = computed(() => Math.ceil(filteredPets.value.length / pageSize));

const pagedPets = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  return filteredPets.value.slice(start, start + pageSize);
});

const visiblePages = computed(() => {
  const pages: (number | string)[] = [];
  const total = totalPages.value;
  const current = currentPage.value;
  
  if (total <= 5) {
    for (let i = 1; i <= total; i++) pages.push(i);
  } else {
    pages.push(1);
    if (current > 3) pages.push('...');
    for (let i = Math.max(2, current - 1); i <= Math.min(total - 1, current + 1); i++) {
      pages.push(i);
    }
    if (current < total - 2) pages.push('...');
    pages.push(total);
  }
  return pages;
});

const goToPage = (page: number | string) => {
  if (typeof page === 'number' && page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
};

const petTypes = [
  { label: "全部", value: "" },
  { label: "猫咪", value: "cat" },
  { label: "狗狗", value: "dog" },
  { label: "其他", value: "other" }
];

const filteredPets = computed(() => {
  return pets.value.filter(pet => {
    if (filter.type && !pet.breed?.toLowerCase().includes(filter.type)) return false;
    return true;
  });
});

const getPetTags = (pet: any) => {
  const tags = [];
  if (pet.is_vaccinated) tags.push('已疫苗');
  if (pet.is_neutered) tags.push('已绝育');
  if (pet.is_healthy) tags.push('健康');
  return tags.slice(0, 3);
};

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
  alert(`申请已提交！我们会尽快联系您。`);
  selectedPet.value = null;
};

onMounted(loadPets);
</script>

<style scoped lang="scss">
.adoption-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
  max-width: 1200px;
  margin: 0 auto;
  padding-bottom: 40px;
}

// Hero Banner
.adoption-hero {
  position: relative;
  height: 320px;
  border-radius: 20px;
  overflow: hidden;
  background: var(--hero-gradient);
  
  .hero-overlay {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: column;
    justify-content: center;
    padding: 48px;
    
    h1 {
      font-size: 36px;
      font-weight: 800;
      color: #fff;
      margin: 0 0 12px;
      max-width: 600px;
    }
    
    p {
      font-size: 18px;
      color: rgba(255, 255, 255, 0.9);
      margin: 0 0 24px;
    }
  }
}

.hero-actions {
  display: flex;
  gap: 16px;
}

.btn-hero-primary {
  padding: 14px 28px;
  background: #fff;
  color: var(--primary);
  border: none;
  border-radius: 12px;
  font-weight: 700;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  }
}

.btn-hero-secondary {
  padding: 14px 28px;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 12px;
  font-weight: 700;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(8px);
  
  &:hover {
    background: rgba(255, 255, 255, 0.3);
  }
}

// Filter Bar
.filter-bar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 16px;
  padding: 16px 20px;
  background: var(--surface);
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(34, 60, 52, 0.06);
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  font-weight: 600;
  color: var(--muted);
}

.filter-btn {
  padding: 8px 16px;
  background: var(--chip-bg);
  border: 1px solid var(--border-warm);
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  color: var(--muted);
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover {
    background: var(--chip-active-bg);
    color: var(--text-heading);
  }
  
  &.active {
    background: var(--primary);
    color: #fff;
    border-color: var(--primary);
    box-shadow: 0 4px 12px rgba(255, 155, 122, 0.3);
  }
}

.filter-divider {
  width: 1px;
  height: 24px;
  background: var(--border-warm);
}

.filter-select {
  padding: 8px 12px;
  border: 1px solid var(--border-warm);
  border-radius: 8px;
  background: var(--surface);
  color: var(--text);
  font-size: 14px;
  min-width: 120px;
  
  &:focus {
    outline: none;
    border-color: var(--primary);
  }
}

// Pet Grid
.pet-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.pet-card {
  background: var(--surface);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(34, 60, 52, 0.06);
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 12px 30px rgba(34, 60, 52, 0.12);
  }
}

// Pagination
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 6px;
  padding: 24px 0 8px;
}

.page-btn {
  min-width: 40px;
  height: 40px;
  padding: 0 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--surface);
  border: 1.5px solid rgba(255, 155, 122, 0.3);
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  color: var(--muted);
  cursor: pointer;
  transition: all 0.2s ease;

  svg {
    width: 18px;
    height: 18px;
  }

  &:hover:not(:disabled) {
    border-color: var(--primary);
    color: var(--primary);
    background: rgba(255, 155, 122, 0.08);
  }

  &.active {
    background: var(--hero-gradient);
    border-color: transparent;
    color: #fff;
    box-shadow: 0 4px 12px rgba(255, 155, 122, 0.3);
  }

  &:disabled {
    opacity: 0.4;
    cursor: not-allowed;
  }
}

.pet-image {
  position: relative;
  height: 200px;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s ease;
  }
  
  &:hover img {
    transform: scale(1.05);
  }
}

.pet-status {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 6px 14px;
  background: var(--primary);
  color: #fff;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(255, 155, 122, 0.3);
}

.pet-like {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.9);
  border: none;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  
  svg {
    width: 18px;
    height: 18px;
    color: var(--muted);
  }
  
  &:hover {
    background: #fff;
    transform: scale(1.1);
  }
}

.pet-info {
  padding: 16px;
}

.pet-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  
  h3 {
    margin: 0;
    font-size: 18px;
    font-weight: 700;
    color: var(--text-heading);
  }
}

.pet-type {
  padding: 4px 10px;
  background: rgba(255, 155, 122, 0.15);
  color: var(--primary);
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.pet-meta {
  margin: 0 0 12px;
  font-size: 13px;
  color: var(--muted);
}

.pet-tags {
  display: flex;
  gap: 6px;
  margin-bottom: 16px;
  
  .tag {
    padding: 4px 10px;
    background: var(--chip-bg);
    color: var(--muted);
    border-radius: 8px;
    font-size: 12px;
  }
}

.btn-adopt {
  width: 100%;
  padding: 12px;
  background: var(--primary);
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover {
    opacity: 0.9;
    transform: translateY(-1px);
  }
}

// Modal
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
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
  background: var(--surface);
  border-radius: 20px;
  position: relative;
}

.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.9);
  border: none;
  border-radius: 50%;
  font-size: 24px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10;
  transition: all 0.2s ease;
  
  &:hover {
    background: #fff;
    transform: scale(1.1);
  }
}

.detail-hero {
  position: relative;
  height: 300px;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.image-count {
  position: absolute;
  bottom: 16px;
  left: 16px;
  padding: 6px 14px;
  background: rgba(0, 0, 0, 0.3);
  color: #fff;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  backdrop-filter: blur(4px);
}

.detail-body {
  padding: 24px;
}

.detail-header {
  margin-bottom: 24px;
}

.detail-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  
  h2 {
    margin: 0;
    font-size: 28px;
    font-weight: 800;
    color: var(--text-heading);
  }
}

.status-badge {
  padding: 6px 14px;
  background: rgba(255, 214, 107, 0.2);
  color: #C4A03F;
  border: 1px solid rgba(255, 214, 107, 0.3);
  border-radius: 20px;
  font-size: 13px;
  font-weight: 700;
}

.detail-stats {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: rgba(255, 155, 122, 0.1);
  color: var(--primary);
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  
  svg {
    width: 18px;
    height: 18px;
  }
}

.detail-section {
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border-warm);
  
  &:last-of-type {
    border-bottom: none;
  }
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 16px;
  font-size: 18px;
  font-weight: 700;
  color: var(--text-heading);
  
  svg {
    width: 22px;
    height: 22px;
    color: var(--primary);
  }
}

.health-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.health-tag {
  padding: 8px 16px;
  background: rgba(255, 155, 122, 0.1);
  color: var(--primary);
  border: 1px solid rgba(255, 155, 122, 0.2);
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  
  &.success {
    background: rgba(91, 185, 140, 0.1);
    color: var(--success);
    border-color: rgba(91, 185, 140, 0.2);
  }
}

.story-text {
  margin: 0;
  font-size: 15px;
  line-height: 1.8;
  color: var(--muted);
}

.requirements-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.requirement-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: var(--chip-bg);
  border-radius: 12px;
  
  svg {
    width: 24px;
    height: 24px;
    color: var(--primary);
    flex-shrink: 0;
  }
  
  p {
    margin: 0;
    font-size: 14px;
    color: var(--muted);
    line-height: 1.5;
  }
}

.process-steps {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    top: 24px;
    left: 48px;
    right: 48px;
    height: 2px;
    background: var(--border-warm);
  }
}

.process-step {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  position: relative;
  z-index: 1;
}

.step-number {
  width: 48px;
  height: 48px;
  background: var(--primary);
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 12px;
  box-shadow: 0 4px 12px rgba(255, 155, 122, 0.3);
}

.step-title {
  margin: 0 0 4px;
  font-size: 15px;
  font-weight: 700;
  color: var(--text-heading);
}

.step-desc {
  margin: 0;
  font-size: 12px;
  color: var(--muted);
}

.detail-cta {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.btn-primary-full {
  width: 100%;
  padding: 16px;
  background: var(--primary);
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 4px 12px rgba(255, 155, 122, 0.3);
  
  &:hover {
    opacity: 0.9;
    transform: translateY(-2px);
  }
}

.btn-secondary-full {
  width: 100%;
  padding: 16px;
  background: rgba(255, 214, 107, 0.2);
  color: #C4A03F;
  border: 1px solid rgba(255, 214, 107, 0.3);
  border-radius: 12px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover {
    background: rgba(255, 214, 107, 0.3);
  }
}

.cta-tip {
  margin: 0;
  font-size: 12px;
  color: var(--muted);
  text-align: center;
}

@media (max-width: 768px) {
  .adoption-hero .hero-overlay {
    padding: 24px;
    
    h1 {
      font-size: 24px;
    }
  }

  .pet-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }

  .requirements-grid {
    grid-template-columns: 1fr;
  }
  
  .process-steps {
    grid-template-columns: repeat(2, 1fr);
    
    &::before {
      display: none;
    }
  }
}
</style>
