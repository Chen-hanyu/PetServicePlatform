<template>
  <section class="services-page">
    <div class="card page-hero">
      <h1>宠物服务</h1>
      <p>专业、可靠的宠物服务，守护爱宠健康成长</p>
    </div>

    <div class="category-nav">
      <button 
        v-for="cat in categories" 
        :key="cat.id"
        :class="['cat-btn', { active: selectedCategory === cat.name }]"
        @click="selectCategory(cat.name)"
      >
        <span class="cat-icon">🏷️</span>
        {{ cat.name }}
      </button>
    </div>

    <div class="content-grid">
      <main class="merchant-list">
        <div class="list-header">
          <h2>{{ selectedCategory || '全部' }}商家</h2>
          <span class="count">共 {{ filteredMerchants.length }} 家</span>
        </div>

        <DataState :loading="loading" :error="error" :empty="filteredMerchants.length === 0" empty-text="暂无商家">
          <div class="merchants-grid">
            <article 
              v-for="merchant in filteredMerchants" 
              :key="merchant.id" 
              class="merchant-card"
              @click="selectMerchant(merchant)"
            >
              <div class="merchant-image">
                <img :src="merchant.cover_url" :alt="merchant.name" />
                <div class="rating-badge">⭐ {{ merchant.rating }}</div>
              </div>
              <div class="merchant-info">
                <h3>{{ merchant.name }}</h3>
                <p class="desc">{{ merchant.description }}</p>
                <div class="meta-row">
                  <span class="location">📍 {{ merchant.district }}</span>
                  <span class="status" :class="merchant.status === '营业中' ? 'open' : 'closed'">
                    {{ merchant.status }}
                  </span>
                </div>
              </div>
            </article>
          </div>
        </DataState>
      </main>

      <!-- Merchant Detail Sidebar -->
      <aside v-if="selectedMerchant" class="detail-sidebar card">
        <div class="detail-header">
          <button class="back-btn" @click="selectedMerchant = null">← 返回列表</button>
          <h3>{{ selectedMerchant.name }}</h3>
          <div class="detail-meta">
            <span>📍 {{ selectedMerchant.address }}</span>
            <span>📞 {{ selectedMerchant.phone }}</span>
            <span>🕘 {{ selectedMerchant.business_hours }}</span>
          </div>
        </div>

        <div class="services-list">
          <h4>服务项目</h4>
          <div v-for="service in selectedMerchant.services" :key="service.id" class="service-item">
            <div class="service-info">
              <span class="service-name">{{ service.name }}</span>
              <span class="service-duration">{{ service.duration }}</span>
            </div>
            <div class="service-price">
              ¥{{ service.price }}
              <button class="book-btn" @click="openBooking(service)">预约</button>
            </div>
          </div>
        </div>

        <div class="booking-form" v-if="bookingService">
          <h4>预约 {{ bookingService.name }}</h4>
          <div class="form-group">
            <label>预约时间</label>
            <input v-model="bookingForm.booking_time" type="datetime-local" class="input" />
          </div>
          <div class="form-group">
            <label>联系人</label>
            <input v-model="bookingForm.contact_name" class="input" placeholder="请输入您的姓名" />
          </div>
          <div class="form-group">
            <label>联系电话</label>
            <input v-model="bookingForm.contact_phone" class="input" placeholder="请输入您的手机号" />
          </div>
          <div class="form-group">
            <label>备注</label>
            <textarea v-model="bookingForm.remark" class="input textarea" placeholder="宠物年龄、品种等信息"></textarea>
          </div>
          <button class="btn btn-primary btn-block" @click="submitBooking">确认预约</button>
        </div>
      </aside>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, computed } from "vue";
import DataState from "@/components/DataState.vue";
import { fetchMerchants, fetchServiceCategories, createBooking } from "@/services/modules/services";
import { mockMerchants } from "@/mocks/services";
import { toErrorMessage } from "@/services/http";

const loading = ref(false);
const error = ref("");
const categories = ref<any[]>([]);
const merchants = ref<any[]>([]);
const selectedCategory = ref("");
const selectedMerchant = ref<any>(null);
const bookingService = ref<any>(null);

const bookingForm = reactive({
  booking_time: "",
  contact_name: "",
  contact_phone: "",
  remark: ""
});

const filteredMerchants = computed(() => {
  if (!selectedCategory.value) return merchants.value;
  return merchants.value.filter(m => m.category === selectedCategory.value);
});

const loadMerchants = async () => {
  loading.value = true;
  error.value = "";
  try {
    const data = await fetchMerchants({ page: 1, page_size: 20 });
    merchants.value = data.list || [];
  } catch (e) {
    console.warn("Failed to fetch merchants, using mock data", e);
    merchants.value = mockMerchants;
  } finally {
    loading.value = false;
  }
};

const selectCategory = (name: string) => {
  selectedCategory.value = selectedCategory.value === name ? "" : name;
};

const selectMerchant = (merchant: any) => {
  selectedMerchant.value = merchant;
  bookingService.value = null;
  resetForm();
};

const openBooking = (service: any) => {
  bookingService.value = service;
};

const submitBooking = () => {
  alert(`预约成功！\n服务：${bookingService.value.name}\n时间：${bookingForm.booking_time}\n我们将尽快与您联系确认。`);
  bookingService.value = null;
  resetForm();
};

const resetForm = () => {
  bookingForm.booking_time = "";
  bookingForm.contact_name = "";
  bookingForm.contact_phone = "";
  bookingForm.remark = "";
};

onMounted(async () => {
  // Mock categories
  categories.value = [
    { id: 1, name: "宠物美容" },
    { id: 2, name: "宠物医院" },
    { id: 3, name: "宠物训练" },
    { id: 4, name: "宠物寄养" }
  ];
  await loadMerchants();
});
</script>

<style scoped lang="scss">
.services-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.category-nav {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 8px;
}

.cat-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: var(--surface-tint);
  border: 1px solid var(--border-warm-mid);
  border-radius: 30px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.2s;
  color: var(--muted);
  font-weight: 600;
  
  &:hover {
    background: var(--chip-active-bg);
    border-color: var(--chip-border);
  }
  
  &.active {
    background: linear-gradient(135deg, var(--primary) 0%, var(--primary-strong) 100%);
    color: #fff;
    border-color: var(--primary-strong);
    box-shadow: 0 6px 14px rgba(241, 124, 83, 0.22);
  }
}

.content-grid {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 24px;
  align-items: start;
}

.merchants-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.merchant-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #f0dccb;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 24px rgba(128, 84, 52, 0.1);
  }
}

.merchant-image {
  height: 180px;
  position: relative;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.rating-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(255, 255, 255, 0.95);
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 700;
  color: #f3b64f;
}

.merchant-info {
  padding: 16px;
  
  h3 {
    margin: 0 0 8px;
    font-size: 18px;
    color: #2f2a26;
  }
  
  .desc {
    margin: 0 0 12px;
    font-size: 13px;
    color: #7d7068;
    line-height: 1.4;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
}

.meta-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  
  .location {
    color: var(--on-white-text);
  }
  
  .status {
    padding: 2px 8px;
    border-radius: 8px;
    font-size: 12px;
    font-weight: 600;
    
    &.open {
      background: #e8f5e9;
      color: #4caf50;
    }
    
    &.closed {
      background: #ffebee;
      color: #f44336;
    }
  }
}

.detail-sidebar {
  position: sticky;
  top: 20px;
  padding: 20px;
}

.detail-header {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0dccb;
  
  .back-btn {
    background: none;
    border: none;
    color: #ff9d7a;
    cursor: pointer;
    font-size: 14px;
    padding: 0;
    margin-bottom: 8px;
  }
  
  h3 {
    margin: 0 0 12px;
    font-size: 20px;
    color: #2f2a26;
  }
  
  .detail-meta {
    display: flex;
    flex-direction: column;
    gap: 6px;
    font-size: 13px;
    color: #7d7068;
  }
}

.services-list {
  margin-bottom: 24px;
  
  h4 {
    margin: 0 0 12px;
    font-size: 16px;
    color: #2f2a26;
  }
}

.service-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f5f0eb;
  
  &:last-child {
    border-bottom: none;
  }
}

.service-info {
  display: flex;
  flex-direction: column;
  
  .service-name {
    font-weight: 600;
    color: #2f2a26;
  }
  
  .service-duration {
    font-size: 12px;
    color: var(--on-white-text);
  }
}

.service-price {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 700;
  color: #ff9d7a;
}

.book-btn {
  padding: 4px 12px;
  background: #fff1e5;
  color: #8a4f33;
  border: 1px solid #ffd5b8;
  border-radius: 12px;
  font-size: 12px;
  cursor: pointer;
  
  &:hover {
    background: #ffe9d7;
  }
}

.booking-form {
  background: #fffaf5;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid #ffd9bc;
  
  h4 {
    margin: 0 0 16px;
    color: #8d4d30;
  }
}

.form-group {
  margin-bottom: 12px;
  
  label {
    display: block;
    font-size: 13px;
    color: #7d7068;
    margin-bottom: 6px;
  }
}

.textarea {
  min-height: 60px;
}

.btn-block {
  width: 100%;
  margin-top: 12px;
}

@media (max-width: 1024px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
  
  .detail-sidebar {
    position: fixed;
    top: 0;
    right: 0;
    bottom: 0;
    width: 100%;
    max-width: 400px;
    z-index: 100;
    border-radius: 0;
    overflow-y: auto;
    box-shadow: -4px 0 20px rgba(102, 72, 48, 0.12);
  }
}
</style>