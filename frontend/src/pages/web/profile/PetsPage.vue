<template>
  <section class="pets-page">
    <!-- 顶部导航栏 -->
    <header class="page-header">
      <div class="header-nav">
        <button class="back-btn" @click="$router.back()">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M15 19l-7-7 7-7"/>
          </svg>
        </button>
        <h2 class="header-title">我的宠物</h2>
      </div>
      <button class="save-btn" @click="goToAddPet">
        <span>添加宠物</span>
      </button>
    </header>

    <!-- 主内容区 -->
    <main class="main-content">
      <!-- 宠物列表 -->
      <div class="pets-grid" v-if="pets.length > 0">
        <article v-for="pet in pets" :key="pet.id" class="pet-card" @click="openPetDetail(pet)">
          <div class="pet-cover">
            <img :src="pet.avatar || defaultPetAvatar" :alt="pet.name" />
          </div>
          <div class="pet-body">
            <div class="pet-name-row">
              <h3>{{ pet.name }}</h3>
              <span class="pet-age">{{ pet.ageText }}</span>
            </div>
            <p class="pet-breed">{{ pet.breed || pet.type }} · {{ pet.genderText }}</p>
            <div class="pet-tags">
              <span v-if="pet.weight" class="tag">体重 {{ pet.weight }}kg</span>
              <span class="tag tag-vaccine">档案</span>
            </div>
          </div>
          <div class="pet-actions" @click.stop>
            <button class="action-btn edit" @click="editPet(pet)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
            </button>
            <button class="action-btn delete" @click="deletePet(pet.id)">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6"/><path d="M14 11v6"/></svg>
            </button>
          </div>
        </article>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-state">
        <div class="empty-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M12 6v6m0 0v6m0-6h6m-6 0H6"/>
          </svg>
        </div>
        <p class="empty-text">还没有添加宠物哦</p>
        <p class="empty-desc">快来添加第一位家庭成员吧</p>
        <button class="add-btn-large" @click="showAddForm = true">
          <span>添加宠物</span>
        </button>
      </div>
    </main>

    <!-- 宠物详情弹窗 -->
    <div v-if="detailPet" class="modal-overlay" @click.self="detailPet = null">
      <div class="modal-content">
        <button class="close-btn" @click="detailPet = null">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M6 18L18 6M6 6l12 12"/>
          </svg>
        </button>
        
        <div class="detail-header">
          <div class="detail-cover">
            <img :src="detailPet.avatar || defaultPetAvatar" :alt="detailPet.name" />
          </div>
          <div class="detail-info">
            <h2>{{ detailPet.name }}</h2>
            <div class="detail-meta">
              <span class="meta-chip">{{ detailPet.breed }}</span>
              <span class="meta-chip">{{ detailPet.ageText }}</span>
              <span class="meta-chip">{{ detailPet.weight }}kg</span>
            </div>
            <div class="detail-tags">
              <span class="tag-badge primary" v-if="detailPet.type">{{ detailPet.type }}</span>
              <span class="tag-badge gender" v-if="detailPet.genderText">{{ detailPet.genderText }}</span>
              <span class="tag-badge health">档案</span>
            </div>
          </div>
        </div>

        <div class="detail-bio" v-if="detailPet.bio">
          <h4>宠物简介</h4>
          <p>{{ detailPet.bio }}</p>
        </div>

        <div class="detail-actions">
          <button class="btn btn-outline" @click="editPet(detailPet); detailPet = null">编辑资料</button>
          <button class="btn btn-primary-solid" @click="detailPet = null">关闭</button>
        </div>
      </div>
    </div>

    <!-- 添加/编辑宠物表单弹窗 -->
    <div v-if="showAddForm || editingPet" class="modal-overlay" @click.self="closeForm">
      <div class="modal-content form-modal">
        <button class="close-btn" @click="closeForm">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M6 18L18 6M6 6l12 12"/>
          </svg>
        </button>

        <h3 class="form-title">{{ editingPet ? '编辑宠物档案' : '添加宠物档案' }}</h3>

        <!-- 头像上传 -->
        <div class="avatar-upload-section">
          <div class="avatar-wrap" @click="triggerAvatarUpload">
            <img :src="petForm.avatar || defaultPetAvatar" alt="Pet Avatar" class="avatar-preview" />
            <div class="avatar-overlay">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M23 19a2 2 0 01-2 2H3a2 2 0 01-2-2V8a2 2 0 012-2h4l2-3h6l2 3h4a2 2 0 012 2z"/>
                <circle cx="12" cy="13" r="4"/>
              </svg>
            </div>
          </div>
          <div class="avatar-edit-badge" @click="triggerAvatarUpload">
            <svg viewBox="0 0 20 20" fill="currentColor">
              <path d="M13.586 3.586a2 2 0 112.828 2.828l-.793.793-2.828-2.828.793-.793zM11.379 5.793L3 14.172V17h2.828l8.38-8.379-2.83-2.828z"/>
            </svg>
          </div>
          <div class="upload-hint">
            <p class="hint-title">点击更换头像</p>
            <p class="hint-desc">让宝贝的可爱瞬间被记住</p>
          </div>
          <input type="file" ref="avatarInput" @change="handleAvatarChange" accept="image/*" style="display: none" />
        </div>

        <!-- 表单内容 -->
        <div class="form-content">
          <!-- 名字 -->
          <div class="form-group">
            <label>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
              </svg>
              宠物名字
            </label>
            <input v-model="petForm.name" class="input" placeholder="请输入宠物可爱的名字" />
          </div>

          <!-- 品种 -->
          <div class="form-group">
            <label>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M7 21a4 4 0 01-4-4V5a2 2 0 012-2h4a2 2 0 012 2v12a4 4 0 01-4 4zm0 0h12a2 2 0 002-2v-4a2 2 0 00-2-2h-2.343M11 7.343l1.657-1.657a2 2 0 012.828 0l2.829 2.829a2 2 0 010 2.828l-8.486 8.485M7 17h.01"/>
              </svg>
              宠物品种
            </label>
            <select v-model="petForm.breed" class="input">
              <option value="">选择品种</option>
              <option value="可爱猫咪">可爱猫咪 (狸花/英短/美短等)</option>
              <option value="忠诚狗狗">忠诚狗狗 (金毛/柯基/柴犬等)</option>
              <option value="软萌兔兔">软萌兔兔</option>
              <option value="迷你仓鼠">迷你仓鼠</option>
              <option value="其他萌宠">其他萌宠</option>
            </select>
          </div>

          <!-- 性别和生日 -->
          <div class="form-row">
            <div class="form-group">
              <label>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M12 4v16m8-8H4"/>
                </svg>
                性别
              </label>
              <div class="gender-toggle">
                <input type="radio" id="male" value="公" v-model="petForm.gender" class="hidden peer/male" />
                <label for="male" class="toggle-option male">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M12 4v16m8-8H4"/>
                  </svg>
                  男孩子
                </label>
                <input type="radio" id="female" value="母" v-model="petForm.gender" class="hidden peer/female" />
                <label for="female" class="toggle-option female">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M12 4v16m-8-8h16"/>
                  </svg>
                  女孩子
                </label>
              </div>
            </div>
            <div class="form-group">
              <label>
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"/>
                </svg>
                出生日期
              </label>
              <input v-model="petForm.birthday" type="date" class="input" />
            </div>
          </div>

          <!-- 体重 -->
          <div class="form-group">
            <label>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M3 6l3 1m0 0l-3 9a5.002 5.002 0 006.001 0M6 7l3 9M6 7l6-2m6 2l3-1m-3 1l-3 9a5.002 5.002 0 006.001 0M18 7l3 9m-3-9l-6-2m0-2v2m0 16V5m0 16H9m3 0h3"/>
              </svg>
              体重 (kg)
            </label>
            <input v-model.number="petForm.weight" type="number" step="0.1" class="input" placeholder="例如: 4.5" />
          </div>

          <!-- 健康状态 -->
          <div class="health-status">
            <div class="status-item" :class="{ active: petForm.vaccinated }">
              <div class="status-info">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"/>
                </svg>
                <span>已接种疫苗</span>
              </div>
              <label class="switch">
                <input type="checkbox" v-model="petForm.vaccinated" />
                <span class="slider"></span>
              </label>
            </div>
            <div class="status-item" :class="{ active: petForm.neutered }">
              <div class="status-info">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M14.121 14.121L19 19m-7-7l7-7m-7 7l-2.879 2.879M12 12L9.121 9.121m0 5.758a3 3 0 10-4.243 4.243 3 3 0 004.243-4.243zm0-5.758a3 3 0 10-4.243-4.243 3 3 0 004.243 4.243z"/>
                </svg>
                <span>已绝育</span>
              </div>
              <label class="switch">
                <input type="checkbox" v-model="petForm.neutered" />
                <span class="slider"></span>
              </label>
            </div>
          </div>

          <!-- 简介 -->
          <div class="form-group full-width">
            <label>
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/>
              </svg>
              宠物简介
            </label>
            <textarea v-model="petForm.bio" class="textarea" placeholder="分享一下宝贝的性格、喜好或者是你们之间的小故事吧..." rows="3"></textarea>
          </div>
        </div>

        <!-- 底部装饰 -->
        <div class="form-footer">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
          </svg>
          <span>每个生命都值得温柔以待</span>
        </div>

        <!-- 表单操作 -->
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
import { useRouter } from "vue-router";
import { createPet, deletePetById, fetchMyPets, updatePet } from "@/api/modules/pet";
import { toErrorMessage } from "@/api/http";
import type { PetProfile, SavePetPayload } from "@/types/pet";

const router = useRouter();
const defaultPetAvatar = "https://images.unsplash.com/photo-1543466835-00a7907e9de1?auto=format&fit=crop&w=300&q=80";

const loading = ref(false);
const error = ref("");
const pets = ref<PetView[]>([]);
const showAddForm = ref(false);
const editingPet = ref<PetView | null>(null);
const detailPet = ref<PetView | null>(null);
const avatarInput = ref<HTMLInputElement | null>(null);

type PetView = PetProfile & {
  avatar?: string;
  ageText: string;
  genderText: string;
  vaccinated?: boolean;
  neutered?: boolean;
  bio?: string;
};

const petForm = reactive({
  name: "",
  breed: "",
  gender: "",
  age: null as number | null,
  weight: null as number | null,
  birthday: "",
  vaccinated: false,
  neutered: false,
  bio: "",
  avatar: ""
});

const loadPets = async () => {
  loading.value = true;
  error.value = "";
  try {
    const list = await fetchMyPets();
    pets.value = list.map(toPetView);
  } catch (e) {
    error.value = toErrorMessage(e);
    pets.value = [];
  } finally {
    loading.value = false;
  }
};

const openPetDetail = (pet: PetView) => {
  detailPet.value = pet;
};

const editPet = (pet: PetView) => {
  editingPet.value = pet;
  petForm.name = pet.name;
  petForm.breed = pet.breed || "";
  petForm.age = null;
  petForm.weight = pet.weight || null;
  petForm.gender = pet.gender || "";
  petForm.birthday = pet.birthday || "";
  petForm.vaccinated = false;
  petForm.neutered = false;
  petForm.bio = pet.description || pet.bio || "";
  petForm.avatar = pet.avatar_url || pet.avatar || "";
};

const closeForm = () => {
  showAddForm.value = false;
  editingPet.value = null;
  resetForm();
};

const resetForm = () => {
  petForm.name = "";
  petForm.breed = "";
  petForm.age = null;
  petForm.weight = null;
  petForm.gender = "";
  petForm.birthday = "";
  petForm.vaccinated = false;
  petForm.neutered = false;
  petForm.bio = "";
  petForm.avatar = "";
};

const triggerAvatarUpload = () => {
  avatarInput.value?.click();
};

const handleAvatarChange = (event: Event) => {
  const target = event.target as HTMLInputElement;
  const file = target.files?.[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = (e) => {
      petForm.avatar = e.target?.result as string;
    };
    reader.readAsDataURL(file);
  }
};

const submitPet = async () => {
  if (!petForm.name || !petForm.breed) {
    alert("请填写宠物名称和品种");
    return;
  }

  const payload: SavePetPayload = {
    name: petForm.name,
    type: petForm.breed.includes("猫") ? "CAT" : petForm.breed.includes("狗") ? "DOG" : "OTHER",
    breed: petForm.breed,
    gender: petForm.gender,
    birthday: petForm.birthday || undefined,
    weight: petForm.weight || undefined,
    avatar_url: petForm.avatar || undefined,
    description: petForm.bio || undefined
  };

  try {
    if (editingPet.value) {
      await updatePet(editingPet.value.id, payload);
    } else {
      await createPet(payload);
    }
    closeForm();
    await loadPets();
  } catch (e) {
    alert(toErrorMessage(e));
  }
};

const deletePet = async (id: number) => {
  if (confirm("确定要删除这只宠物吗？")) {
    try {
      await deletePetById(id);
      await loadPets();
    } catch (e) {
      alert(toErrorMessage(e));
    }
  }
};

const goToAddPet = () => {
  showAddForm.value = true;
};

function getAgeText(birthday?: string) {
  if (!birthday) return "年龄未知";
  const birth = new Date(birthday);
  if (Number.isNaN(birth.getTime())) return "年龄未知";
  const now = new Date();
  let years = now.getFullYear() - birth.getFullYear();
  const hasHadBirthday =
    now.getMonth() > birth.getMonth() ||
    (now.getMonth() === birth.getMonth() && now.getDate() >= birth.getDate());
  if (!hasHadBirthday) years -= 1;
  return years > 0 ? `${years}岁` : "未满1岁";
}

function getGenderText(gender?: string) {
  if (gender === "MALE" || gender === "公") return "♂ 公";
  if (gender === "FEMALE" || gender === "母") return "♀ 母";
  return "性别未知";
}

function toPetView(pet: PetProfile): PetView {
  return {
    ...pet,
    avatar: pet.avatar_url,
    ageText: getAgeText(pet.birthday),
    genderText: getGenderText(pet.gender),
    bio: pet.description
  };
}

onMounted(() => {
  void loadPets();
});
</script>

<style scoped lang="scss">
.pets-page {
  min-height: 100vh;
  background-color: var(--bg);
  display: flex;
  flex-direction: column;
}

// 顶部导航栏
.page-header {
  background: var(--surface);
  border-bottom: 1px solid var(--border-warm);
  position: sticky;
  top: 0;
  z-index: 50;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;

  .header-nav {
    display: flex;
    align-items: center;
    gap: 12px;

    .back-btn {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      border: none;
      background: var(--surface-muted);
      color: var(--muted);
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.2s;

      &:hover {
        background: var(--surface-muted-hover);
        color: var(--text-heading);
      }

      svg {
        width: 20px;
        height: 20px;
      }
    }

    .header-title {
      font-size: 18px;
      font-weight: 700;
      color: var(--text-heading);
      margin: 0;
    }
  }

  .save-btn {
    padding: 8px 20px;
    border-radius: 20px;
    border: none;
    background: var(--primary);
    color: #fff;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
    box-shadow: 0 4px 12px rgba(255, 155, 122, 0.3);

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 6px 16px rgba(255, 155, 122, 0.4);
    }
  }
}

// 主内容区
.main-content {
  flex: 1;
  padding: 24px 20px;
  max-width: 800px;
  margin: 0 auto;
  width: 100%;
}

// 宠物网格
.pets-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.pet-card {
  background: var(--surface);
  border-radius: var(--radius-xl);
  overflow: hidden;
  border: 1px solid var(--border-warm);
  box-shadow: var(--shadow);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;

  &:hover {
    transform: translateY(-6px);
    box-shadow: 0 16px 40px rgba(34, 60, 52, 0.12);
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
    color: var(--text-heading);
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
  flex-wrap: wrap;
  gap: 6px;

  .tag {
    padding: 3px 10px;
    background: var(--surface-muted);
    border-radius: 10px;
    font-size: 12px;
    color: var(--text-heading-soft);

    &.tag-vaccine {
      background: rgba(255, 155, 122, 0.15);
      color: var(--primary);
    }

    &.tag-neutered {
      background: rgba(255, 214, 107, 0.15);
      color: var(--accent);
    }
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

// 空状态
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;

  .empty-icon {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    background: var(--surface-muted);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 24px;

    svg {
      width: 40px;
      height: 40px;
      color: var(--muted-soft);
    }
  }

  .empty-text {
    font-size: 18px;
    font-weight: 600;
    color: var(--text-heading);
    margin: 0 0 8px;
  }

  .empty-desc {
    font-size: 14px;
    color: var(--muted);
    margin: 0 0 24px;
  }

  .add-btn-large {
    padding: 12px 32px;
    border-radius: 24px;
    border: none;
    background: var(--primary);
    color: #fff;
    font-size: 15px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
    box-shadow: 0 4px 12px rgba(255, 155, 122, 0.3);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 20px rgba(255, 155, 122, 0.4);
    }
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
  max-width: 480px;
  background: var(--surface);
  border-radius: var(--radius-xl);
  position: relative;
  padding: 28px;
  max-height: 90vh;
  overflow-y: auto;
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
  z-index: 10;

  &:hover {
    background: var(--surface-muted-hover);
  }

  svg {
    width: 18px;
    height: 18px;
  }
}

// 详情头部
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
  padding-top: 8px;

  h2 {
    margin: 0 0 12px;
    font-size: 24px;
    font-weight: 800;
    color: var(--text-heading);
  }
}

.detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;

  .meta-chip {
    padding: 4px 12px;
    background: var(--surface-muted);
    border-radius: 8px;
    font-size: 13px;
    color: var(--text-heading-soft);
  }
}

.detail-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;

  .tag-badge {
    padding: 4px 12px;
    border-radius: 12px;
    font-size: 13px;
    font-weight: 600;

    &.primary {
      background: var(--primary);
      color: #fff;
    }

    &.gender {
      background: rgba(255, 155, 122, 0.15);
      color: var(--primary-strong);
    }

    &.health {
      background: rgba(255, 214, 107, 0.15);
      color: #C9A227;
    }
  }
}

.detail-bio {
  margin-bottom: 20px;

  h4 {
    font-size: 14px;
    font-weight: 600;
    color: var(--text-heading);
    margin: 0 0 8px;
  }

  p {
    font-size: 14px;
    color: var(--muted);
    margin: 0;
    line-height: 1.6;
  }
}

.detail-actions {
  display: flex;
  gap: 12px;

  .btn {
    flex: 1;
  }
}

// 按钮样式
.btn {
  padding: 12px 24px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  border: none;

  &.btn-outline {
    background: var(--surface);
    border: 1px solid var(--border-warm);
    color: var(--text-heading);

    &:hover {
      background: var(--surface-muted);
    }
  }

  &.btn-primary-solid {
    background: var(--primary);
    color: #fff;

    &:hover {
      background: var(--primary-strong);
    }
  }

  &.btn-secondary {
    background: var(--surface-muted);
    color: var(--muted);

    &:hover {
      background: var(--surface-muted-hover);
    }
  }

  &.btn-primary {
    background: var(--primary);
    color: #fff;
    box-shadow: 0 4px 12px rgba(255, 155, 122, 0.3);

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 6px 16px rgba(255, 155, 122, 0.4);
    }
  }
}

// 表单弹窗
.form-modal {
  max-width: 540px;
  padding: 0;

  .close-btn {
    top: 12px;
    right: 12px;
  }

  .form-title {
    font-size: 18px;
    font-weight: 700;
    color: var(--text-heading);
    margin: 0;
    padding: 20px 20px 0;
  }
}

// 头像上传区域
.avatar-upload-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 24px 20px;
  border-bottom: 1px solid var(--border-warm);

  .avatar-wrap {
    position: relative;
    width: 100px;
    height: 100px;
    border-radius: 50%;
    overflow: hidden;
    cursor: pointer;

    .avatar-preview {
      width: 100%;
      height: 100%;
      object-fit: cover;
      background: var(--surface-muted);
    }

    .avatar-overlay {
      position: absolute;
      inset: 0;
      background: rgba(0, 0, 0, 0.2);
      display: flex;
      align-items: center;
      justify-content: center;
      opacity: 0;
      transition: opacity 0.2s;

      svg {
        width: 28px;
        height: 28px;
        color: #fff;
      }
    }

    &:hover .avatar-overlay {
      opacity: 1;
    }
  }

  .avatar-edit-badge {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    margin-top: 40px;
    margin-left: 40px;
    width: 32px;
    height: 32px;
    border-radius: 50%;
    background: var(--accent);
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    border: 2px solid var(--surface);

    svg {
      width: 14px;
      height: 14px;
      color: #fff;
    }
  }

  .upload-hint {
    text-align: center;

    .hint-title {
      font-size: 15px;
      font-weight: 600;
      color: var(--text-heading);
      margin: 0 0 4px;
    }

    .hint-desc {
      font-size: 13px;
      color: var(--muted);
      margin: 0;
    }
  }
}

// 表单内容
.form-content {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
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
    color: var(--text-heading);
    display: flex;
    align-items: center;
    gap: 8px;

    svg {
      width: 18px;
      height: 18px;
      color: var(--primary);
    }
  }
}

.input {
  width: 100%;
  padding: 12px 16px;
  border-radius: 12px;
  border: 1px solid var(--border-warm);
  background: var(--surface-muted);
  font-size: 14px;
  color: var(--text-heading);
  transition: all 0.2s;
  box-sizing: border-box;

  &:focus {
    outline: none;
    border-color: var(--primary);
    background: var(--surface);
    box-shadow: 0 0 0 3px rgba(255, 155, 122, 0.1);
  }

  &::placeholder {
    color: var(--muted-soft);
  }
}

.textarea {
  width: 100%;
  padding: 12px 16px;
  border-radius: 12px;
  border: 1px solid var(--border-warm);
  background: var(--surface-muted);
  font-size: 14px;
  color: var(--text-heading);
  transition: all 0.2s;
  resize: none;
  box-sizing: border-box;

  &:focus {
    outline: none;
    border-color: var(--primary);
    background: var(--surface);
    box-shadow: 0 0 0 3px rgba(255, 155, 122, 0.1);
  }

  &::placeholder {
    color: var(--muted-soft);
  }
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

// 性别切换
.gender-toggle {
  display: flex;
  gap: 8px;

  .toggle-option {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    padding: 12px;
    border-radius: 12px;
    border: 1px solid var(--border-warm);
    background: var(--surface-muted);
    font-size: 14px;
    font-weight: 500;
    color: var(--muted);
    cursor: pointer;
    transition: all 0.2s;

    svg {
      width: 18px;
      height: 18px;
    }

    &.male {
      &:hover {
        border-color: var(--primary);
        color: var(--primary);
      }
    }

    &.female {
      &:hover {
        border-color: var(--gender-female);
        color: var(--gender-female);
      }
    }
  }

  // 选中状态通过 peer 实现
  input:checked + label.male {
    background: rgba(255, 155, 122, 0.15);
    border-color: var(--primary);
    color: var(--primary);
  }

  input:checked + label.female {
    background: rgba(255, 159, 170, 0.15);
    border-color: var(--gender-female);
    color: var(--gender-female);
  }
}

// 健康状态
.health-status {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .status-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 14px 16px;
    border-radius: 12px;
    background: var(--surface-muted);
    border: 1px solid var(--border-warm);
    transition: all 0.2s;

    &.active {
      border-color: var(--primary);
      background: rgba(255, 155, 122, 0.05);

      .status-info {
        color: var(--primary);
      }
    }

    .status-info {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 14px;
      font-weight: 500;
      color: var(--muted);

      svg {
        width: 20px;
        height: 20px;
      }
    }
  }
}

// Switch 开关
.switch {
  position: relative;
  display: inline-block;
  width: 44px;
  height: 24px;

  input {
    opacity: 0;
    width: 0;
    height: 0;

    &:checked + .slider {
      background-color: var(--primary);

      &:before {
        transform: translateX(20px);
      }
    }

    &:focus + .slider {
      box-shadow: 0 0 1px var(--primary);
    }
  }

  .slider {
    position: absolute;
    cursor: pointer;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: #ccc;
    transition: 0.3s;
    border-radius: 24px;

    &:before {
      position: absolute;
      content: "";
      height: 18px;
      width: 18px;
      left: 3px;
      bottom: 3px;
      background-color: white;
      transition: 0.3s;
      border-radius: 50%;
    }
  }
}

// 表单底部
.form-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 16px;
  color: var(--muted-soft);
  border-top: 1px solid var(--border-warm);

  svg {
    width: 18px;
    height: 18px;
  }

  span {
    font-size: 13px;
  }
}

// 表单操作
.form-actions {
  display: flex;
  gap: 12px;
  padding: 0 20px 20px;

  .btn {
    flex: 1;
  }
}

@media (max-width: 768px) {
  .detail-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
