<template>
  <div class="admin-page">
    <div class="tabs-card">
      <button v-for="tab in tabs" :key="tab.key" :class="['tab-btn', { active: activeTab === tab.key }]" @click="activeTab = tab.key">
        <span class="tab-icon" v-html="tab.icon"></span>
        {{ tab.label }}
      </button>
    </div>

    <!-- 宠物列表 -->
    <div v-if="activeTab === 'pets'" class="data-card">
      <div class="data-header">
        <h3 class="data-title">待领养宠物管理</h3>
        <button class="btn btn-primary" @click="openPetModal()">+ 新增宠物</button>
      </div>
      <div class="filter-bar">
        <input v-model="petKeyword" placeholder="宠物名称" class="input input-sm" />
        <select v-model="petType" class="input input-sm"><option value="">全部类型</option><option value="猫">猫</option><option value="狗">狗</option></select>
        <select v-model="petStatus" class="input input-sm"><option value="">全部状态</option><option value="ONLINE">上架</option><option value="OFFLINE">下架</option><option value="ADOPTED">已领养</option></select>
        <button class="btn btn-secondary" @click="loadPets">查询</button>
      </div>
      <DataState :loading="petLoading" :error="petError" :empty="pets.length === 0">
        <table class="table">
          <thead><tr><th>ID</th><th>名称</th><th>类型</th><th>城市</th><th>状态</th><th class="col-ops">操作</th></tr></thead>
          <tbody>
            <tr v-for="pet in pets" :key="pet.id">
              <td><span class="id-tag">#{{ pet.id }}</span></td>
              <td><span class="pet-name">{{ pet.name }}</span></td>
              <td><span class="tag tag-light">{{ pet.type }}</span></td>
              <td>{{ pet.city || '-' }}</td>
              <td><StatusBadge :variant="petStatusVariant(pet.status)">{{ pet.status }}</StatusBadge></td>
              <td class="col-ops ops-group">
                <button class="btn btn-xs" @click="openPetModal(pet)">编辑</button>
                <button class="btn btn-xs" :class="pet.status === 'ONLINE' ? 'btn-warning' : 'btn-success'" @click="togglePetStatus(pet)">{{ pet.status === 'ONLINE' ? '下架' : '上架' }}</button>
                <button class="btn btn-xs btn-danger" @click="deletePet(pet.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- 领养申请列表 -->
    <div v-if="activeTab === 'applications'" class="data-card">
      <div class="data-header">
        <h3 class="data-title">领养申请审核</h3>
        <div class="data-actions">
          <select v-model="appStatus" class="input input-sm"><option value="">全部状态</option><option value="PENDING">待审核</option><option value="APPROVED">已通过</option><option value="REJECTED">已驳回</option></select>
          <button class="btn btn-secondary" @click="loadApplications">查询</button>
        </div>
      </div>
      <DataState :loading="appLoading" :error="appError" :empty="applications.length === 0">
        <table class="table">
          <thead><tr><th>ID</th><th>宠物名称</th><th>申请人</th><th>联系电话</th><th>申请状态</th><th class="col-ops">操作</th></tr></thead>
          <tbody>
            <tr v-for="app in applications" :key="app.id">
              <td><span class="id-tag">#{{ app.id }}</span></td>
              <td><span class="pet-name">{{ app.pet_name }}</span></td>
              <td><span class="user-name-tag">{{ app.user_nickname }}</span></td>
              <td><span class="phone-text">{{ app.contact_phone }}</span></td>
              <td><StatusBadge :variant="appStatusVariant(app.status)">{{ app.status }}</StatusBadge></td>
              <td class="col-ops ops-group">
                <button class="btn btn-xs" @click="viewApplicationDetail(app)">详情</button>
                <button v-if="app.status === 'PENDING'" class="btn btn-xs btn-success" @click="reviewApplication(app.id, 'APPROVED')">通过</button>
                <button v-if="app.status === 'PENDING'" class="btn btn-xs btn-danger" @click="reviewApplication(app.id, 'REJECTED')">驳回</button>
              </td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- 宠物编辑弹窗 -->
    <Teleport to="body">
      <div v-if="petModalVisible" class="modal-overlay" @click.self="closePetModal">
        <div class="modal-content modal-lg">
          <div class="modal-header">
            <h3>{{ editingPet ? '编辑宠物' : '新增宠物' }}</h3>
            <button class="modal-close" @click="closePetModal">&times;</button>
          </div>
          <form @submit.prevent="savePet">
            <div class="form-grid cols-2">
              <div class="form-group"><label>名称</label><input v-model="petForm.name" required class="input" /></div>
              <div class="form-group"><label>类型</label><select v-model="petForm.type" class="input"><option>猫</option><option>狗</option></select></div>
              <div class="form-group"><label>品种</label><input v-model="petForm.breed" class="input" /></div>
              <div class="form-group"><label>性别</label><select v-model="petForm.gender" class="input"><option>MALE</option><option>FEMALE</option></select></div>
              <div class="form-group"><label>年龄描述</label><input v-model="petForm.age_desc" class="input" /></div>
              <div class="form-group"><label>城市</label><input v-model="petForm.city" class="input" /></div>
              <div class="form-group"><label>封面图URL</label><input v-model="petForm.cover_url" class="input" /></div>
              <div class="form-group"><label>状态</label><select v-model="petForm.status" class="input"><option>ONLINE</option><option>OFFLINE</option><option>ADOPTED</option></select></div>
              <div class="form-group full-width"><label>健康情况</label><textarea v-model="petForm.health_status" class="input" rows="2"></textarea></div>
              <div class="form-group full-width"><label>性格说明</label><textarea v-model="petForm.personality" class="input" rows="2"></textarea></div>
              <div class="form-group full-width"><label>领养要求</label><textarea v-model="petForm.adoption_requirements" class="input" rows="2"></textarea></div>
              <div class="form-group full-width"><label>宠物故事</label><textarea v-model="petForm.story" class="input" rows="3"></textarea></div>
            </div>
            <div class="modal-actions">
              <button type="button" class="btn btn-cancel" @click="closePetModal">取消</button>
              <button type="submit" class="btn btn-primary">保存</button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>

    <!-- 申请详情弹窗 -->
    <Teleport to="body">
      <div v-if="appDetailVisible" class="modal-overlay" @click.self="appDetailVisible = false">
        <div class="modal-content">
          <div class="modal-header">
            <h3>领养申请详情</h3>
            <button class="modal-close" @click="appDetailVisible = false">&times;</button>
          </div>
          <div class="detail-body">
            <div class="detail-row"><span class="detail-label">申请人</span><span>{{ currentApp?.user_nickname }}</span></div>
            <div class="detail-row"><span class="detail-label">联系电话</span><span>{{ currentApp?.contact_phone }}</span></div>
            <div class="detail-row"><span class="detail-label">养宠经验</span><span>{{ currentApp?.experience_desc || '未填写' }}</span></div>
            <div class="detail-row"><span class="detail-label">居住条件</span><span>{{ currentApp?.living_condition_desc || '未填写' }}</span></div>
            <div class="detail-row"><span class="detail-label">申请状态</span><StatusBadge :variant="appStatusVariant(currentApp?.status || '')">{{ currentApp?.status }}</StatusBadge></div>
          </div>
          <div class="modal-actions"><button class="btn btn-primary" @click="appDetailVisible = false">关闭</button></div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import DataState from '@/components/DataState.vue';
import StatusBadge from '@/components/StatusBadge.vue';
import {
  fetchAdminAdoptionPets, createAdminAdoptionPet, updateAdminAdoptionPet, deleteAdminAdoptionPet,
  fetchAdminAdoptionApplications, reviewAdminAdoptionApplication,
} from '@/api/modules/admin';
import { toErrorMessage } from '@/api/http';

const tabs = [
  { key: 'pets', label: '待领养宠物管理', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 7h16M4 12h16M4 17h16"/><circle cx="12" cy="12" r="3"/></svg>' },
  { key: 'applications', label: '领养申请审核', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/></svg>' },
];
const activeTab = ref('pets');

// 宠物列表
const pets = ref<any[]>([]);
const petLoading = ref(false);
const petError = ref('');
const petKeyword = ref('');
const petType = ref('');
const petStatus = ref('');
const loadPets = async () => {
  petLoading.value = true;
  try {
    const res = await fetchAdminAdoptionPets({ keyword: petKeyword.value || undefined, type: petType.value || undefined, status: petStatus.value || undefined, page: 1, page_size: 50 });
    pets.value = res.list || [];
  } catch (e) { petError.value = toErrorMessage(e); } finally { petLoading.value = false; }
};
const petStatusVariant = (s: string) => ({ ONLINE: 'success', OFFLINE: 'warning', ADOPTED: 'info' }[s] || 'neutral');

// 宠物弹窗
const petModalVisible = ref(false);
const editingPet = ref<any>(null);
const petForm = reactive({ name: '', type: '猫', breed: '', gender: 'MALE', age_desc: '', city: '', health_status: '', personality: '', adoption_requirements: '', story: '', cover_url: '', status: 'ONLINE' });
const openPetModal = (pet?: any) => {
  if (pet) { editingPet.value = pet; Object.assign(petForm, pet); }
  else { editingPet.value = null; Object.assign(petForm, { name: '', type: '猫', breed: '', gender: 'MALE', age_desc: '', city: '', health_status: '', personality: '', adoption_requirements: '', story: '', cover_url: '', status: 'ONLINE' }); }
  petModalVisible.value = true;
};
const closePetModal = () => { petModalVisible.value = false; editingPet.value = null; };
const savePet = async () => {
  try {
    if (editingPet.value) await updateAdminAdoptionPet(editingPet.value.id, petForm);
    else await createAdminAdoptionPet(petForm);
    await loadPets(); closePetModal();
  } catch (e) { petError.value = toErrorMessage(e); }
};
const togglePetStatus = async (pet: any) => {
  const newStatus = pet.status === 'ONLINE' ? 'OFFLINE' : 'ONLINE';
  try { await updateAdminAdoptionPet(pet.id, { ...pet, status: newStatus }); await loadPets(); } catch (e) { petError.value = toErrorMessage(e); }
};
const deletePet = async (id: number) => {
  if (confirm('确定删除该宠物吗？')) { try { await deleteAdminAdoptionPet(id); await loadPets(); } catch (e) { petError.value = toErrorMessage(e); } }
};

// 领养申请
const applications = ref<any[]>([]);
const appLoading = ref(false);
const appError = ref('');
const appStatus = ref('');
const loadApplications = async () => {
  appLoading.value = true;
  try { const res = await fetchAdminAdoptionApplications({ status: appStatus.value || undefined, page: 1, page_size: 50 }); applications.value = res.list || []; } catch (e) { appError.value = toErrorMessage(e); } finally { appLoading.value = false; }
};
const appStatusVariant = (s: string) => ({ PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger' }[s] || 'neutral');
const appDetailVisible = ref(false);
const currentApp = ref<any>(null);
const viewApplicationDetail = (app: any) => { currentApp.value = app; appDetailVisible.value = true; };
const reviewApplication = async (id: number, status: string) => {
  try { await reviewAdminAdoptionApplication(id, status, `管理员${status === 'APPROVED' ? '通过' : '驳回'}`); await loadApplications(); } catch (e) { appError.value = toErrorMessage(e); }
};

loadPets();
loadApplications();
</script>

<style scoped lang="scss">
.admin-page { display: flex; flex-direction: column; gap: 16px; }

.tabs-card {
  display: flex; gap: 4px; background: #fff; border-radius: 16px; padding: 6px;
  border: 1px solid #DDE6E3; box-shadow: 0 2px 8px rgba(37, 49, 47, 0.04); overflow-x: auto;
}
.tab-btn {
  display: flex; align-items: center; gap: 6px; padding: 10px 18px; border: none;
  background: transparent; cursor: pointer; font-weight: 500; font-size: 14px; color: #8B9794;
  border-radius: 10px; transition: all 0.2s; white-space: nowrap;
  .tab-icon { width: 16px; height: 16px; display: flex; align-items: center; :deep(svg) { width: 16px; height: 16px; } }
  &:hover { color: #5F6B68; background: #FAFCFB; }
  &.active { color: #fff; background: #7ECFBC; box-shadow: 0 2px 8px rgba(126, 207, 188, 0.3); }
}

.data-card { background: #fff; border-radius: 16px; border: 1px solid #DDE6E3; box-shadow: 0 2px 8px rgba(37, 49, 47, 0.04); overflow: hidden; }
.data-header { display: flex; align-items: center; justify-content: space-between; padding: 16px 20px; border-bottom: 1px solid #EEF2F0; flex-wrap: wrap; gap: 12px; }
.data-title { font-size: 16px; font-weight: 600; color: #25312F; margin: 0; }
.data-actions { display: flex; gap: 8px; align-items: center; }
.filter-bar { display: flex; gap: 8px; padding: 12px 20px; border-bottom: 1px solid #EEF2F0; flex-wrap: wrap; }

.table { width: 100%; border-collapse: collapse;
  th { padding: 12px 16px; font-size: 12px; font-weight: 600; color: #8B9794; text-transform: uppercase; letter-spacing: 0.5px; background: #FAFCFB; border-bottom: 1px solid #EEF2F0; text-align: left; white-space: nowrap; }
  td { padding: 14px 16px; font-size: 14px; color: #5F6B68; border-bottom: 1px solid #EEF2F0; }
  tbody tr { transition: background 0.2s; &:hover { background: #FAFCFB; } &:last-child td { border-bottom: none; } }
}
.col-ops { width: 180px; }
.ops-group { display: flex; gap: 6px; }

.input { border: 1px solid #DDE6E3; border-radius: 10px; background: #FAFCFB; min-height: 40px; padding: 8px 14px; outline: none; font-size: 14px; color: #25312F; transition: all 0.2s; width: 100%;
  &:focus { border-color: #7ECFBC; box-shadow: 0 0 0 3px rgba(126, 207, 188, 0.15); background: #fff; }
}
.input-sm { min-height: 36px; padding: 6px 12px; font-size: 13px; }

.btn { border: none; border-radius: 10px; padding: 10px 20px; cursor: pointer; font-size: 14px; font-weight: 600; display: inline-flex; align-items: center; gap: 6px; transition: all 0.2s; &:hover { transform: translateY(-1px); } }
.btn-primary { background: #7ECFBC; color: #fff; box-shadow: 0 4px 12px rgba(126, 207, 188, 0.3); &:hover { background: #6BC0AC; } }
.btn-secondary { background: #FAFCFB; color: #5F6B68; border: 1px solid #DDE6E3; &:hover { background: #F0F5F3; } }
.btn-cancel { background: #FAFCFB; color: #8B9794; border: 1px solid #DDE6E3; &:hover { background: #F0F5F3; } }
.btn-xs { padding: 6px 14px; font-size: 12px; border-radius: 8px; border: none; cursor: pointer; font-weight: 500; transition: all 0.2s; background: #FAFCFB; color: #5F6B68; border: 1px solid #DDE6E3; &:hover { background: #F0F5F3; transform: translateY(-1px); } }
.btn-success { background: #E8F5F1; color: #5BB98C; border: 1px solid #C8E8DE; &:hover { background: #D4EDE4; } }
.btn-warning { background: #FFF8E6; color: #E6A23C; border: 1px solid #FFE8B0; &:hover { background: #FFF0CC; } }
.btn-danger { background: #FFE8E8; color: #E97A7A; border: 1px solid #F5C8C8; &:hover { background: #F5D0D0; } }

.tag { display: inline-flex; align-items: center; padding: 2px 10px; border-radius: 6px; font-size: 12px; font-weight: 500; }
.tag-light { background: #FAFCFB; color: #8B9794; border: 1px solid #EEF2F0; }
.id-tag { font-family: "Fira Sans", Consolas, monospace; font-size: 13px; color: #B0BAB7; font-weight: 500; }
.pet-name { font-weight: 500; color: #25312F; }
.user-name-tag { display: inline-flex; align-items: center; gap: 6px; padding: 2px 10px; background: #E8F5F1; color: #5BB98C; border-radius: 6px; font-size: 12px; font-weight: 500; }
.phone-text { font-family: "Fira Sans", Consolas, monospace; font-size: 13px; }

.modal-overlay { position: fixed; inset: 0; background: rgba(37, 49, 47, 0.5); display: flex; align-items: center; justify-content: center; z-index: 100; backdrop-filter: blur(4px); }
.modal-content { background: #fff; border-radius: 16px; padding: 0; width: 520px; max-width: 90vw; max-height: 80vh; overflow-y: auto; box-shadow: 0 20px 60px rgba(37, 49, 47, 0.15); }
.modal-lg { width: 680px; }
.modal-header { display: flex; align-items: center; justify-content: space-between; padding: 20px 24px 0; h3 { margin: 0; font-size: 18px; font-weight: 600; color: #25312F; } }
.modal-close { width: 32px; height: 32px; border: none; background: #FAFCFB; border-radius: 8px; font-size: 20px; color: #8B9794; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: all 0.2s; &:hover { background: #FFE8E8; color: #E97A7A; } }
form { padding: 20px 24px 24px; }
.form-grid { display: grid; gap: 16px; &.cols-2 { grid-template-columns: 1fr 1fr; } .full-width { grid-column: 1 / -1; } }
.form-group { display: flex; flex-direction: column; gap: 6px; label { font-size: 13px; font-weight: 500; color: #5F6B68; } }
.modal-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px; padding-top: 16px; border-top: 1px solid #EEF2F0; }

.detail-body { padding: 20px 24px; display: flex; flex-direction: column; gap: 16px; }
.detail-row { display: flex; align-items: center; gap: 16px; .detail-label { min-width: 80px; font-size: 13px; font-weight: 500; color: #8B9794; } }

@media (max-width: 768px) {
  .data-header { flex-direction: column; align-items: flex-start; }
  .table { th, td { padding: 10px 12px; } }
  .form-grid.cols-2 { grid-template-columns: 1fr; }
}
</style>
