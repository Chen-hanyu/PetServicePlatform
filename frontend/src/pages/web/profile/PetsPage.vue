<template>
  <section class="pets-page">
    <div class="page-hero card">
      <h1>我的宠物</h1>
      <p>记录爱宠的每一个温馨时刻</p>
    </div>

    <DataState :loading="loading" :empty="pets.length === 0" empty-text="还没有添加宠物哦，快来添加第一位家庭成员吧">
      <div class="pets-grid">
        <article v-for="pet in pets" :key="pet.id" class="pet-card" @click="openPetDetail(pet)">
          <div class="pet-cover">
            <img :src="pet.avatar || defaultPetAvatar" :alt="pet.name" />
            <div class="pet-type-badge">{{ pet.type }}</div>
          </div>
          <div class="pet-body">
            <div class="pet-name-row">
              <h3>{{ pet.name }}</h3>
              <span class="pet-age">{{ pet.age }}岁</span>
            </div>
            <p class="pet-breed">{{ pet.breed }}</p>
            <div class="pet-tags">
              <span v-if="pet.weight" class="tag">体重 {{ pet.weight }}kg</span>
              <span v-if="pet.gender" class="tag">{{ pet.gender === '公' ? '♂' : '♀' }}</span>
            </div>
          </div>
          <div class="pet-actions" @click.stop>
            <button class="action-btn edit" @click="editPet(pet)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
              编辑
            </button>
            <button class="action-btn delete" @click="deletePet(pet.id)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6"/><path d="M14 11v6"/></svg>
              删除
            </button>
          </div>
        </article>

        <!-- Add Pet Card -->
        <div class="pet-card add-card" @click="showAddForm = true">
          <div class="add-icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
          </div>
          <span>添加宠物</span>
        </div>
      </div>
    </DataState>

    <!-- Pet Detail Modal -->
    <div v-if="detailPet" class="modal-overlay" @click.self="detailPet = null">
      <div class="modal-content card">
        <button class="close-btn" @click="detailPet = null">×</button>
        <div class="detail-header">
          <div class="detail-cover">
            <img :src="detailPet.avatar || defaultPetAvatar" :alt="detailPet.name" />
          </div>
          <div class="detail-info">
            <h2>{{ detailPet.name }}</h2>
            <div class="detail-meta">
              <span class="meta-item">{{ detailPet.breed }}</span>
              <span class="meta-item">{{ detailPet.age }}岁</span>
              <span class="meta-item" v-if="detailPet.weight">{{ detailPet.weight }}kg</span>
            </div>
            <div class="detail-tags">
              <span class="tag-badge" v-if="detailPet.type">{{ detailPet.type }}</span>
              <span class="tag-badge" v-if="detailPet.gender">{{ detailPet.gender === '公' ? '♂ 公' : '♀ 母' }}</span>
            </div>
          </div>
        </div>
        <div class="detail-actions">
          <button class="btn btn-secondary" @click="editPet(detailPet); detailPet = null">编辑资料</button>
          <button class="btn btn-primary" @click="detailPet = null">关闭</button>
        </div>
      </div>
    </div>

    <!-- Add/Edit Pet Form -->
    <div v-if="showAddForm || editingPet" class="modal-overlay" @click.self="closeForm">
      <div class="modal-content card form-modal">
        <button class="close-btn" @click="closeForm">×</button>
        <h3>{{ editingPet ? '编辑宠物' : '添加新宠物' }}</h3>
        <div class="form-grid">
          <div class="form-group">
            <label>宠物名称 <span class="required">*</span></label>
            <input v-model="petForm.name" class="input" placeholder="给爱宠起个名字" />
          </div>
          <div class="form-group">
            <label>宠物类型 <span class="required">*</span></label>
            <select v-model="petForm.type" class="input">
              <option value="">选择类型</option>
              <option value="🐱 猫">🐱 猫</option>
              <option value="🐶 狗">🐶 狗</option>
              <option value="🐰 兔">🐰 兔</option>
              <option value="🐹 仓鼠">🐹 仓鼠</option>
              <option value="🐦 鸟">🐦 鸟</option>
              <option value="🐠 鱼">🐠 鱼</option>
              <option value="🐾 其他">🐾 其他</option>
            </select>
          </div>
          <div class="form-group">
            <label>品种</label>
            <input v-model="petForm.breed" class="input" placeholder="如：英短、金毛" />
          </div>
          <div class="form-group">
            <label>年龄（岁）</label>
            <input v-model.number="petForm.age" type="number" min="0" class="input" placeholder="年龄" />
          </div>
          <div class="form-group">
            <label>体重（kg）</label>
            <input v-model.number="petForm.weight" type="number" min="0" step="0.1" class="input" placeholder="体重" />
          </div>
          <div class="form-group">
            <label>性别</label>
            <select v-model="petForm.gender" class="input">
              <option value="">选择性别</option>
              <option value="公">♂ 公</option>
              <option value="母">♀ 母</option>
            </select>
          </div>
          <div class="form-group full-width">
            <label>头像 URL</label>
            <input v-model="petForm.avatar" class="input" placeholder="输入图片链接，或留空使用默认头像" />
          </div>
        </div>
        <div class="form-actions">
          <button class="btn btn-secondary" @click="closeForm">取消</button>
          <button class="btn btn-primary" @click="submitPet">{{ editingPet ? '保存修改' : '确认添加' }}</button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import DataState from "@/components/DataState.vue";

const defaultPetAvatar = "https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=300&q=80";

const loading = ref(false);
const pets = ref<any[]>([]);
const showAddForm = ref(false);
const editingPet = ref<any>(null);
const detailPet = ref<any>(null);

const petForm = reactive({
  name: "",
  type: "",
  breed: "",
  age: null as number | null,
  weight: null as number | null,
  gender: "",
  avatar: ""
});

const loadPets = () => {
  // Mock data
  pets.value = [
    { id: 1, name: "小橘", type: "🐱 猫", breed: "中华田园猫", age: 2, weight: 4.5, gender: "母", avatar: defaultPetAvatar },
    { id: 2, name: "旺财", type: "🐶 狗", breed: "金毛寻回犬", age: 1, weight: 12, gender: "公", avatar: "https://images.unsplash.com/photo-1587300003388-59208cc962cb?auto=format&fit=crop&w=300&q=80" },
    { id: 3, name: "豆豆", type: "🐰 兔", breed: "荷兰垂耳兔", age: 1, weight: 1.2, gender: "母", avatar: "https://images.unsplash.com/photo-1585110396000-c9ffd4e87bba?auto=format&fit=crop&w=300&q=80" }
  ];
};

const openPetDetail = (pet: any) => {
  detailPet.value = pet;
};

const editPet = (pet: any) => {
  editingPet.value = pet;
  petForm.name = pet.name;
  petForm.type = pet.type;
  petForm.breed = pet.breed;
  petForm.age = pet.age;
  petForm.weight = pet.weight || null;
  petForm.gender = pet.gender || "";
  petForm.avatar = pet.avatar || "";
};

const closeForm = () => {
  showAddForm.value = false;
  editingPet.value = null;
  resetForm();
};

const resetForm = () => {
  petForm.name = "";
  petForm.type = "";
  petForm.breed = "";
  petForm.age = null;
  petForm.weight = null;
  petForm.gender = "";
  petForm.avatar = "";
};

const submitPet = () => {
  if (!petForm.name || !petForm.type) return;

  if (editingPet.value) {
    pets.value = pets.value.map(p =>
      p.id === editingPet.value.id
        ? { ...p, ...petForm, avatar: petForm.avatar || defaultPetAvatar }
        : p
    );
  } else {
    pets.value.push({
      id: Date.now(),
      ...petForm,
      avatar: petForm.avatar || defaultPetAvatar
    });
  }
  closeForm();
};

const deletePet = (id: number) => {
  if (confirm("确定要删除这只宠物吗？")) {
    pets.value = pets.value.filter(p => p.id !== id);
  }
};

onMounted(loadPets);
</script>

<style scoped lang="scss">
.pets-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
  max-width: 900px;
  margin: 0 auto;
}

.pets-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
}

.pet-card {
  background: var(--surface);
  border-radius: 20px;
  overflow: hidden;
  border: 1px solid var(--border-warm);
  box-shadow: var(--shadow);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;

  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 16px 40px rgba(102, 72, 48, 0.14);
  }
}

.pet-cover {
  position: relative;
  height: 200px;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.4s ease;
  }

  .pet-card:hover & img {
    transform: scale(1.05);
  }
}

.pet-type-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(255, 255, 255, 0.92);
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 700;
  color: var(--primary-strong);
  backdrop-filter: blur(4px);
}

.pet-body {
  padding: 16px;
}

.pet-name-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;

  h3 {
    margin: 0;
    font-size: 18px;
    font-weight: 800;
    color: var(--text);
  }

  .pet-age {
    font-size: 13px;
    color: var(--primary);
    font-weight: 600;
  }
}

.pet-breed {
  margin: 0 0 10px;
  font-size: 13px;
  color: var(--muted);
}

.pet-tags {
  display: flex;
  gap: 6px;

  .tag {
    padding: 3px 10px;
    background: var(--chip-bg);
    border-radius: 10px;
    font-size: 12px;
    color: var(--text-heading-soft);
  }
}

.pet-actions {
  display: flex;
  gap: 8px;
  padding: 0 16px 16px;

  .action-btn {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 4px;
    padding: 8px;
    border-radius: 10px;
    border: 1px solid var(--border-warm);
    background: var(--surface);
    font-size: 13px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;

    svg {
      width: 14px;
      height: 14px;
    }

    &.edit {
      color: var(--primary-strong);
      &:hover {
        background: var(--chip-active-bg);
        border-color: var(--chip-border);
      }
    }

    &.delete {
      color: var(--danger);
      &:hover {
        background: var(--status-danger-bg);
        border-color: #f8b8b8;
      }
    }
  }
}

.add-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  min-height: 300px;
  border: 2px dashed var(--border-warm-mid);
  background: var(--surface-tint);
  cursor: pointer;
  transition: all 0.2s;

  &:hover {
    border-color: var(--primary);
    background: var(--chip-active-bg);
  }

  .add-icon {
    width: 56px;
    height: 56px;
    border-radius: 50%;
    background: linear-gradient(135deg, var(--primary) 0%, var(--primary-strong) 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 6px 16px rgba(241, 124, 83, 0.3);

    svg {
      width: 28px;
      height: 28px;
      color: #fff;
    }
  }

  span {
    font-size: 15px;
    font-weight: 700;
    color: var(--text-heading-soft);
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
}

.modal-content {
  width: 100%;
  max-width: 520px;
  border-radius: 24px;
  position: relative;
  padding: 28px;
}

.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: none;
  background: var(--surface-muted);
  color: var(--muted);
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;

  &:hover {
    background: var(--surface-muted-hover);
  }
}

.detail-header {
  display: flex;
  gap: 20px;
  align-items: flex-start;
  margin-bottom: 20px;
}

.detail-cover {
  width: 120px;
  height: 120px;
  border-radius: 20px;
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
    margin: 0 0 8px;
    font-size: 24px;
    font-weight: 800;
    color: var(--text);
  }
}

.detail-meta {
  display: flex;
  gap: 12px;
  margin-bottom: 10px;

  .meta-item {
    padding: 3px 10px;
    background: var(--chip-bg);
    border-radius: 8px;
    font-size: 12px;
    color: var(--text-heading-soft);
  }
}

.detail-tags {
  display: flex;
  gap: 8px;

  .tag-badge {
    padding: 4px 12px;
    background: var(--primary);
    color: #fff;
    border-radius: 12px;
    font-size: 13px;
    font-weight: 600;
  }
}

.detail-actions {
  display: flex;
  gap: 12px;

  .btn {
    flex: 1;
  }
}

// Form Modal
.form-modal {
  max-width: 540px;

  h3 {
    margin: 0 0 20px;
    font-size: 20px;
    font-weight: 800;
    color: var(--text-heading-soft);
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
  gap: 6px;

  &.full-width {
    grid-column: 1 / -1;
  }

  label {
    font-size: 13px;
    font-weight: 600;
    color: var(--muted);

    .required {
      color: var(--danger);
    }
  }
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .pets-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  }

  .detail-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>