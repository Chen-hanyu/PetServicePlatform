<template>
  <section class="admin-page">
    <div class="tabs">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        :class="['tab-btn', { active: activeTab === tab.key }]"
        @click="activeTab = tab.key"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- 宠物列表 -->
    <div v-if="activeTab === 'pets'" class="card">
      <div class="top-row">
        <h2 class="section-title">待领养宠物管理</h2>
        <button class="btn btn-primary" @click="openPetModal()">+ 新增宠物</button>
      </div>

      <DataState :loading="petLoading" :error="petError" :empty="pets.length === 0">
        <div class="filter-bar">
          <input v-model="petKeyword" placeholder="宠物名称" class="input" />
          <select v-model="petType" class="input">
            <option value="">全部类型</option>
            <option value="猫">猫</option>
            <option value="狗">狗</option>
          </select>
          <select v-model="petStatus" class="input">
            <option value="">全部状态</option>
            <option value="ONLINE">上架</option>
            <option value="OFFLINE">下架</option>
            <option value="ADOPTED">已领养</option>
          </select>
          <button class="btn btn-secondary" @click="loadPets">查询</button>
        </div>
        <table class="table">
          <thead>
            <tr><th>ID</th><th>名称</th><th>类型</th><th>城市</th><th>状态</th><th>操作</th></tr>
          </thead>
          <tbody>
            <tr v-for="pet in pets" :key="pet.id">
              <td>{{ pet.id }}</td>
              <td>{{ pet.name }}</td>
              <td>{{ pet.type }}</td>
              <td>{{ pet.city || '-' }}</td>
              <td><StatusBadge :variant="petStatusVariant(pet.status)">{{ pet.status }}</StatusBadge></td>
              <td class="ops">
                <button class="btn btn-xs" @click="openPetModal(pet)">编辑</button>
                <button class="btn btn-xs btn-danger" @click="togglePetStatus(pet)">{{ pet.status === 'ONLINE' ? '下架' : '上架' }}</button>
                <button class="btn btn-xs btn-danger" @click="deletePet(pet.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- 领养申请列表 -->
    <div v-if="activeTab === 'applications'" class="card">
      <div class="top-row">
        <h2 class="section-title">领养申请审核</h2>
      </div>
      <DataState :loading="appLoading" :error="appError" :empty="applications.length === 0">
        <div class="filter-bar">
          <select v-model="appStatus" class="input">
            <option value="">全部状态</option>
            <option value="PENDING">待审核</option>
            <option value="APPROVED">已通过</option>
            <option value="REJECTED">已驳回</option>
          </select>
          <button class="btn btn-secondary" @click="loadApplications">查询</button>
        </div>
        <table class="table">
          <thead><tr><th>ID</th><th>宠物名称</th><th>申请人</th><th>联系电话</th><th>申请状态</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-for="app in applications" :key="app.id">
              <td>{{ app.id }}</td>
              <td>{{ app.pet_name }}</td>
              <td>{{ app.user_nickname }}</td>
              <td>{{ app.contact_phone }}</td>
              <td><StatusBadge :variant="appStatusVariant(app.status)">{{ app.status }}</StatusBadge></td>
              <td class="ops">
                <button class="btn btn-xs" @click="viewApplicationDetail(app)">详情</button>
                <button v-if="app.status === 'PENDING'" class="btn btn-xs btn-primary" @click="reviewApplication(app.id, 'APPROVED')">通过</button>
                <button v-if="app.status === 'PENDING'" class="btn btn-xs btn-danger" @click="reviewApplication(app.id, 'REJECTED')">驳回</button>
              </td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- 宠物编辑弹窗 -->
    <Teleport to="body">
      <div v-if="petModalVisible" class="modal" @click.self="closePetModal">
        <div class="modal-content">
          <h3>{{ editingPet ? '编辑宠物' : '新增宠物' }}</h3>
          <form @submit.prevent="savePet">
            <div><label>名称</label><input v-model="petForm.name" required /></div>
            <div><label>类型</label><select v-model="petForm.type"><option>猫</option><option>狗</option></select></div>
            <div><label>品种</label><input v-model="petForm.breed" /></div>
            <div><label>性别</label><select v-model="petForm.gender"><option>MALE</option><option>FEMALE</option></select></div>
            <div><label>年龄描述</label><input v-model="petForm.age_desc" /></div>
            <div><label>城市</label><input v-model="petForm.city" /></div>
            <div><label>健康情况</label><textarea v-model="petForm.health_status"></textarea></div>
            <div><label>性格说明</label><textarea v-model="petForm.personality"></textarea></div>
            <div><label>领养要求</label><textarea v-model="petForm.adoption_requirements"></textarea></div>
            <div><label>宠物故事</label><textarea v-model="petForm.story"></textarea></div>
            <div><label>封面图URL</label><input v-model="petForm.cover_url" /></div>
            <div><label>状态</label><select v-model="petForm.status"><option>ONLINE</option><option>OFFLINE</option><option>ADOPTED</option></select></div>
            <div class="modal-actions">
              <button type="button" class="btn btn-secondary" @click="closePetModal">取消</button>
              <button type="submit" class="btn btn-primary">保存</button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>

    <!-- 申请详情弹窗 -->
    <Teleport to="body">
      <div v-if="appDetailVisible" class="modal" @click.self="appDetailVisible = false">
        <div class="modal-content">
          <h3>领养申请详情</h3>
          <p><strong>申请人：</strong>{{ currentApp?.user_nickname }}</p>
          <p><strong>联系电话：</strong>{{ currentApp?.contact_phone }}</p>
          <p><strong>养宠经验：</strong>{{ currentApp?.experience_desc }}</p>
          <p><strong>居住条件：</strong>{{ currentApp?.living_condition_desc }}</p>
          <p><strong>申请状态：</strong>{{ currentApp?.status }}</p>
          <div class="modal-actions"><button class="btn btn-primary" @click="appDetailVisible = false">关闭</button></div>
        </div>
      </div>
    </Teleport>
  </section>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import DataState from '@/components/DataState.vue';
import StatusBadge from '@/components/StatusBadge.vue';
import {
  fetchAdminAdoptionPets,
  createAdminAdoptionPet,
  updateAdminAdoptionPet,
  deleteAdminAdoptionPet,
  fetchAdminAdoptionApplications,
  reviewAdminAdoptionApplication,
} from '@/api/modules/admin';
import { toErrorMessage } from '@/api/http';

const tabs = [
  { key: 'pets', label: '待领养宠物管理' },
  { key: 'applications', label: '领养申请审核' },
];
const activeTab = ref('pets');

// 宠物列表相关
const pets = ref<any[]>([]);
const petLoading = ref(false);
const petError = ref('');
const petKeyword = ref('');
const petType = ref('');
const petStatus = ref('');
const loadPets = async () => {
  petLoading.value = true;
  try {
    const params = {
      keyword: petKeyword.value || undefined,
      type: petType.value || undefined,
      status: petStatus.value || undefined,
      page: 1,
      page_size: 50,
    };
    const res = await fetchAdminAdoptionPets(params);
    pets.value = res.list || [];
  } catch (e) {
    petError.value = toErrorMessage(e);
  } finally {
    petLoading.value = false;
  }
};
const petStatusVariant = (s: string) => {
  if (s === 'ONLINE') return 'success';
  if (s === 'OFFLINE') return 'warning';
  if (s === 'ADOPTED') return 'info';
  return 'neutral';
};

// 宠物新增/编辑弹窗
const petModalVisible = ref(false);
const editingPet = ref<any>(null);
const petForm = reactive({
  name: '',
  type: '猫',
  breed: '',
  gender: 'MALE',
  age_desc: '',
  city: '',
  health_status: '',
  personality: '',
  adoption_requirements: '',
  story: '',
  cover_url: '',
  status: 'ONLINE',
});
const openPetModal = (pet?: any) => {
  if (pet) {
    editingPet.value = pet;
    Object.assign(petForm, pet);
  } else {
    editingPet.value = null;
    Object.assign(petForm, {
      name: '',
      type: '猫',
      breed: '',
      gender: 'MALE',
      age_desc: '',
      city: '',
      health_status: '',
      personality: '',
      adoption_requirements: '',
      story: '',
      cover_url: '',
      status: 'ONLINE',
    });
  }
  petModalVisible.value = true;
};
const closePetModal = () => {
  petModalVisible.value = false;
  editingPet.value = null;
};
const savePet = async () => {
  try {
    if (editingPet.value) {
      await updateAdminAdoptionPet(editingPet.value.id, petForm);
    } else {
      await createAdminAdoptionPet(petForm);
    }
    await loadPets();
    closePetModal();
  } catch (e) {
    petError.value = toErrorMessage(e);
  }
};
const togglePetStatus = async (pet: any) => {
  const newStatus = pet.status === 'ONLINE' ? 'OFFLINE' : 'ONLINE';
  try {
    await updateAdminAdoptionPet(pet.id, { ...pet, status: newStatus });
    await loadPets();
  } catch (e) {
    petError.value = toErrorMessage(e);
  }
};
const deletePet = async (id: number) => {
  if (confirm('确定删除该宠物吗？')) {
    try {
      await deleteAdminAdoptionPet(id);
      await loadPets();
    } catch (e) {
      petError.value = toErrorMessage(e);
    }
  }
};

// 领养申请相关
const applications = ref<any[]>([]);
const appLoading = ref(false);
const appError = ref('');
const appStatus = ref('');
const loadApplications = async () => {
  appLoading.value = true;
  try {
    const res = await fetchAdminAdoptionApplications({ status: appStatus.value || undefined, page: 1, page_size: 50 });
    applications.value = res.list || [];
  } catch (e) {
    appError.value = toErrorMessage(e);
  } finally {
    appLoading.value = false;
  }
};
const appStatusVariant = (s: string) => {
  if (s === 'PENDING') return 'warning';
  if (s === 'APPROVED') return 'success';
  if (s === 'REJECTED') return 'danger';
  return 'neutral';
};
const appDetailVisible = ref(false);
const currentApp = ref<any>(null);
const viewApplicationDetail = (app: any) => {
  currentApp.value = app;
  appDetailVisible.value = true;
};
const reviewApplication = async (id: number, status: string) => {
  try {
    await reviewAdminAdoptionApplication(id, status, `管理员${status === 'APPROVED' ? '通过' : '驳回'}`);
    await loadApplications();
  } catch (e) {
    appError.value = toErrorMessage(e);
  }
};

loadPets();
loadApplications();
</script>

<style scoped lang="scss">
/* 你原样保留即可 */
.admin-page { display: grid; gap: 20px; }
.tabs { display: flex; gap: 8px; border-bottom: 1px solid var(--border-warm); }
.tab-btn { padding: 8px 16px; border: none; background: transparent; cursor: pointer; font-weight: 600; color: var(--muted); }
.tab-btn.active { color: var(--primary); border-bottom: 2px solid var(--primary); }
.top-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.filter-bar { display: flex; gap: 12px; margin-bottom: 20px; flex-wrap: wrap; }
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { padding: 12px 8px; border-bottom: 1px solid #e3ece8; text-align: left; }
.ops { display: flex; gap: 8px; }
.btn-xs { padding: 4px 8px; font-size: 12px; }
.btn-danger { background: var(--danger); color: white; }
.modal { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 100; }
.modal-content { background: var(--surface); border-radius: 16px; padding: 24px; width: 500px; max-width: 90vw; max-height: 80vh; overflow-y: auto; }
.modal-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 20px; }
</style>