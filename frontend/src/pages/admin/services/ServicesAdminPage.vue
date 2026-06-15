<template>
  <div class="admin-page">
    <div class="tabs-card">
      <button v-for="tab in tabs" :key="tab.key" :class="['tab-btn', { active: activeTab === tab.key }]" @click="activeTab = tab.key">
        <span class="tab-icon" v-html="tab.icon"></span>
        {{ tab.label }}
      </button>
    </div>

    <!-- 服务列表 -->
    <div v-if="activeTab === 'services'" class="data-card">
      <div class="data-header">
        <h3 class="data-title">服务管理</h3>
        <button class="btn btn-primary" @click="openServiceModal()">+ 新增服务</button>
      </div>
      <div class="filter-bar">
        <input v-model="serviceKeyword" placeholder="服务名称" class="input input-sm" />
        <select v-model="serviceCategory" class="input input-sm"><option value="">全部分类</option><option>洗澡</option><option>美容</option><option>寄养</option><option>医疗</option><option>训练</option></select>
        <button class="btn btn-secondary" @click="loadServices">查询</button>
      </div>
      <DataState :loading="serviceLoading" :error="serviceError" :empty="services.length === 0">
        <table class="table">
          <thead><tr><th>ID</th><th>服务名称</th><th>分类</th><th>价格</th><th>商家</th><th>状态</th><th class="col-ops">操作</th></tr></thead>
          <tbody>
            <tr v-for="s in services" :key="s.id">
              <td><span class="id-tag">#{{ s.id }}</span></td>
              <td><span class="service-name">{{ s.name }}</span></td>
              <td><span class="tag tag-light">{{ s.category }}</span></td>
              <td><span class="price-text">¥{{ s.price }}</span></td>
              <td>{{ s.merchant_name || '-' }}</td>
              <td><StatusBadge :variant="s.status === 'ONLINE' ? 'success' : 'warning'">{{ s.status }}</StatusBadge></td>
              <td class="col-ops ops-group">
                <button class="btn btn-xs" @click="openServiceModal(s)">编辑</button>
                <button class="btn btn-xs" :class="s.status === 'ONLINE' ? 'btn-warning' : 'btn-success'" @click="toggleServiceStatus(s)">{{ s.status === 'ONLINE' ? '下架' : '上架' }}</button>
                <button class="btn btn-xs btn-danger" @click="deleteService(s.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- 商家列表 -->
    <div v-if="activeTab === 'merchants'" class="data-card">
      <div class="data-header">
        <h3 class="data-title">商家管理</h3>
        <button class="btn btn-primary" @click="openMerchantModal()">+ 新增商家</button>
      </div>
      <div class="filter-bar">
        <input v-model="merchantKeyword" placeholder="商家名称" class="input input-sm" />
        <button class="btn btn-secondary" @click="loadMerchants">查询</button>
      </div>
      <DataState :loading="merchantLoading" :error="merchantError" :empty="merchants.length === 0">
        <table class="table">
          <thead><tr><th>ID</th><th>商家名称</th><th>联系电话</th><th>地址</th><th>状态</th><th class="col-ops">操作</th></tr></thead>
          <tbody>
            <tr v-for="m in merchants" :key="m.id">
              <td><span class="id-tag">#{{ m.id }}</span></td>
              <td><span class="service-name">{{ m.name }}</span></td>
              <td><span class="phone-text">{{ m.phone || '-' }}</span></td>
              <td><span class="addr-text">{{ m.address || '-' }}</span></td>
              <td><StatusBadge :variant="m.status === 'ACTIVE' ? 'success' : 'warning'">{{ m.status }}</StatusBadge></td>
              <td class="col-ops ops-group">
                <button class="btn btn-xs" @click="openMerchantModal(m)">编辑</button>
                <button class="btn btn-xs btn-danger" @click="deleteMerchant(m.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- 预约处理 -->
    <div v-if="activeTab === 'bookings'" class="data-card">
      <div class="data-header">
        <h3 class="data-title">预约处理</h3>
        <div class="data-actions">
          <select v-model="bookingStatus" class="input input-sm" @change="loadBookings">
            <option value="">全部状态</option>
            <option value="PENDING">待确认</option>
            <option value="CONFIRMED">已确认</option>
            <option value="COMPLETED">已完成</option>
            <option value="CANCELLED">已取消</option>
          </select>
          <button class="btn btn-secondary" @click="loadBookings">刷新</button>
        </div>
      </div>
      <DataState :loading="bookingLoading" :error="bookingError" :empty="bookings.length === 0">
        <table class="table">
          <thead><tr><th>ID</th><th>用户</th><th>商家</th><th>服务</th><th>预约时间</th><th>联系电话</th><th>状态</th><th class="col-ops">操作</th></tr></thead>
          <tbody>
            <tr v-for="b in bookings" :key="b.id">
              <td><span class="id-tag">#{{ b.id }}</span></td>
              <td>{{ b.user?.nickname || '-' }}</td>
              <td>{{ b.merchant?.name || '-' }}</td>
              <td>{{ b.service_name }}</td>
              <td>{{ formatDateTime(b.booking_time) }}</td>
              <td><span class="phone-text">{{ b.contact_phone || '-' }}</span></td>
              <td><StatusBadge :variant="bookingStatusVariant(b.status)">{{ b.status }}</StatusBadge></td>
              <td class="col-ops ops-group">
                <button v-if="b.status === 'PENDING'" class="btn btn-xs btn-success" @click="changeBookingStatus(b.id, 'CONFIRMED')">确认</button>
                <button v-if="b.status === 'CONFIRMED'" class="btn btn-xs btn-success" @click="changeBookingStatus(b.id, 'COMPLETED')">完成</button>
                <button v-if="['PENDING', 'CONFIRMED'].includes(b.status)" class="btn btn-xs btn-warning" @click="changeBookingStatus(b.id, 'CANCELLED')">取消</button>
              </td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- 服务弹窗 -->
    <Teleport to="body">
      <div v-if="serviceModalVisible" class="modal-overlay" @click.self="closeServiceModal">
        <div class="modal-content modal-lg">
          <div class="modal-header">
            <h3>{{ editingService ? '编辑服务' : '新增服务' }}</h3>
            <button class="modal-close" @click="closeServiceModal">&times;</button>
          </div>
          <form @submit.prevent="saveService">
            <div class="form-grid cols-2">
              <div class="form-group"><label>服务名称</label><input v-model="serviceForm.name" required class="input" /></div>
              <div class="form-group"><label>分类</label><select v-model="serviceForm.category" class="input"><option>洗澡</option><option>美容</option><option>寄养</option><option>医疗</option><option>训练</option></select></div>
              <div class="form-group"><label>价格</label><input v-model.number="serviceForm.price" type="number" step="0.01" required class="input" /></div>
              <div class="form-group"><label>原价</label><input v-model.number="serviceForm.original_price" type="number" step="0.01" class="input" /></div>
              <div class="form-group"><label>商家ID</label><input v-model.number="serviceForm.merchant_id" type="number" class="input" /></div>
              <div class="form-group"><label>状态</label><select v-model="serviceForm.status" class="input"><option>ONLINE</option><option>OFFLINE</option></select></div>
              <div class="form-group full-width"><label>描述</label><textarea v-model="serviceForm.description" class="input" rows="3"></textarea></div>
              <div class="form-group full-width"><label>封面图URL</label><input v-model="serviceForm.cover_url" class="input" /></div>
            </div>
            <div class="modal-actions">
              <button type="button" class="btn btn-cancel" @click="closeServiceModal">取消</button>
              <button type="submit" class="btn btn-primary">保存</button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>

    <!-- 商家弹窗 -->
    <Teleport to="body">
      <div v-if="merchantModalVisible" class="modal-overlay" @click.self="closeMerchantModal">
        <div class="modal-content">
          <div class="modal-header">
            <h3>{{ editingMerchant ? '编辑商家' : '新增商家' }}</h3>
            <button class="modal-close" @click="closeMerchantModal">&times;</button>
          </div>
          <form @submit.prevent="saveMerchant">
            <div class="form-grid">
              <div class="form-group"><label>商家名称</label><input v-model="merchantForm.name" required class="input" /></div>
              <div class="form-group"><label>联系电话</label><input v-model="merchantForm.phone" class="input" /></div>
              <div class="form-group"><label>地址</label><input v-model="merchantForm.address" class="input" /></div>
              <div class="form-group"><label>状态</label><select v-model="merchantForm.status" class="input"><option>ACTIVE</option><option>INACTIVE</option></select></div>
            </div>
            <div class="modal-actions">
              <button type="button" class="btn btn-cancel" @click="closeMerchantModal">取消</button>
              <button type="submit" class="btn btn-primary">保存</button>
            </div>
          </form>
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
  fetchAdminServiceItems, createAdminServiceItem, updateAdminServiceItem, deleteAdminServiceItem,
  fetchAdminMerchants, createAdminMerchant, updateAdminMerchant, deleteAdminMerchant,
  fetchAdminBookings, updateAdminBooking,
} from '@/api/modules/admin';
import { toErrorMessage } from '@/api/http';

const tabs = [
  { key: 'services', label: '服务管理', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 010 2.83 2 2 0 01-2.83 0l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-2 2 2 2 0 01-2-2v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83 0 2 2 0 010-2.83l.06-.06A1.65 1.65 0 004.68 15a1.65 1.65 0 00-1.51-1H3a2 2 0 01-2-2 2 2 0 012-2h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 010-2.83 2 2 0 012.83 0l.06.06A1.65 1.65 0 009 4.68a1.65 1.65 0 001-1.51V3a2 2 0 012-2 2 2 0 012 2v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 0 2 2 0 010 2.83l-.06.06a1.65 1.65 0 00-.33 1.82V9a1.65 1.65 0 001.51 1H21a2 2 0 012 2 2 2 0 01-2 2h-.09a1.65 1.65 0 00-1.51 1z"/></svg>' },
  { key: 'merchants', label: '商家管理', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>' },
  { key: 'bookings', label: '预约处理', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4M8 2v4M3 10h18"/><path d="M9 16l2 2 4-4"/></svg>' },
];
const activeTab = ref('services');

// 服务列表
const services = ref<any[]>([]);
const serviceLoading = ref(false);
const serviceError = ref('');
const serviceKeyword = ref('');
const serviceCategory = ref('');
const loadServices = async () => {
  serviceLoading.value = true;
  try {
    const res = await fetchAdminServiceItems({ keyword: serviceKeyword.value || undefined, category: serviceCategory.value || undefined, page: 1, page_size: 50 });
    services.value = res.list || [];
  } catch (e) { serviceError.value = toErrorMessage(e); } finally { serviceLoading.value = false; }
};
const serviceModalVisible = ref(false);
const editingService = ref<any>(null);
const serviceForm = reactive({ name: '', category: '洗澡', price: 0, original_price: 0, merchant_id: 0, description: '', cover_url: '', status: 'ONLINE' });
const openServiceModal = (s?: any) => {
  if (s) { editingService.value = s; Object.assign(serviceForm, s); }
  else { editingService.value = null; Object.assign(serviceForm, { name: '', category: '洗澡', price: 0, original_price: 0, merchant_id: 0, description: '', cover_url: '', status: 'ONLINE' }); }
  serviceModalVisible.value = true;
};
const closeServiceModal = () => { serviceModalVisible.value = false; };
const saveService = async () => {
  try {
    if (editingService.value) await updateAdminServiceItem(editingService.value.id, serviceForm);
    else await createAdminServiceItem(serviceForm);
    await loadServices(); closeServiceModal();
  } catch (e) { serviceError.value = toErrorMessage(e); }
};
const toggleServiceStatus = async (s: any) => {
  const ns = s.status === 'ONLINE' ? 'OFFLINE' : 'ONLINE';
  try { await updateAdminServiceItem(s.id, { ...s, status: ns }); await loadServices(); } catch (e) { serviceError.value = toErrorMessage(e); }
};
const deleteService = async (id: number) => {
  if (confirm('确定删除该服务吗？')) { try { await deleteAdminServiceItem(id); await loadServices(); } catch (e) { serviceError.value = toErrorMessage(e); } }
};

// 商家列表
const merchants = ref<any[]>([]);
const merchantLoading = ref(false);
const merchantError = ref('');
const merchantKeyword = ref('');
const loadMerchants = async () => {
  merchantLoading.value = true;
  try { const res = await fetchAdminMerchants({ keyword: merchantKeyword.value || undefined, page: 1, page_size: 50 }); merchants.value = res.list || []; } catch (e) { merchantError.value = toErrorMessage(e); } finally { merchantLoading.value = false; }
};
const merchantModalVisible = ref(false);
const editingMerchant = ref<any>(null);
const merchantForm = reactive({ name: '', phone: '', address: '', status: 'ACTIVE' });
const openMerchantModal = (m?: any) => {
  if (m) { editingMerchant.value = m; Object.assign(merchantForm, m); }
  else { editingMerchant.value = null; Object.assign(merchantForm, { name: '', phone: '', address: '', status: 'ACTIVE' }); }
  merchantModalVisible.value = true;
};
const closeMerchantModal = () => { merchantModalVisible.value = false; };
const saveMerchant = async () => {
  try {
    if (editingMerchant.value) await updateAdminMerchant(editingMerchant.value.id, merchantForm);
    else await createAdminMerchant(merchantForm);
    await loadMerchants(); closeMerchantModal();
  } catch (e) { merchantError.value = toErrorMessage(e); }
};
const deleteMerchant = async (id: number) => {
  if (confirm('确定删除该商家吗？')) { try { await deleteAdminMerchant(id); await loadMerchants(); } catch (e) { merchantError.value = toErrorMessage(e); } }
};

const bookings = ref<any[]>([]);
const bookingLoading = ref(false);
const bookingError = ref('');
const bookingStatus = ref('');
const loadBookings = async () => {
  bookingLoading.value = true;
  try {
    const res = await fetchAdminBookings({ status: bookingStatus.value || undefined, page: 1, page_size: 50 });
    bookings.value = res.list || [];
  } catch (e) {
    bookingError.value = toErrorMessage(e);
  } finally {
    bookingLoading.value = false;
  }
};
const changeBookingStatus = async (id: number, status: string) => {
  try {
    await updateAdminBooking(id, { status });
    await loadBookings();
  } catch (e) {
    bookingError.value = toErrorMessage(e);
  }
};
const bookingStatusVariant = (status: string) => {
  if (status === 'COMPLETED') return 'success';
  if (status === 'CANCELLED') return 'danger';
  if (status === 'CONFIRMED') return 'info';
  return 'warning';
};
const formatDateTime = (value?: string) => {
  if (!value) return '-';
  return value.replace('T', ' ').slice(0, 16);
};

loadServices();
loadMerchants();
loadBookings();
</script>

<style scoped lang="scss">
.admin-page { display: flex; flex-direction: column; gap: 16px; }

.tabs-card { display: flex; gap: 4px; background: #fff; border-radius: 16px; padding: 6px; border: 1px solid #DDE6E3; box-shadow: 0 2px 8px rgba(37, 49, 47, 0.04); overflow-x: auto; }
.tab-btn { display: flex; align-items: center; gap: 6px; padding: 10px 18px; border: none; background: transparent; cursor: pointer; font-weight: 500; font-size: 14px; color: #8B9794; border-radius: 10px; transition: all 0.2s; white-space: nowrap; .tab-icon { width: 16px; height: 16px; display: flex; align-items: center; :deep(svg) { width: 16px; height: 16px; } } &:hover { color: #5F6B68; background: #FAFCFB; } &.active { color: #fff; background: #7ECFBC; box-shadow: 0 2px 8px rgba(126, 207, 188, 0.3); } }

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

.input { border: 1px solid #DDE6E3; border-radius: 10px; background: #FAFCFB; min-height: 40px; padding: 8px 14px; outline: none; font-size: 14px; color: #25312F; transition: all 0.2s; width: 100%; &:focus { border-color: #7ECFBC; box-shadow: 0 0 0 3px rgba(126, 207, 188, 0.15); background: #fff; } }
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
.service-name { font-weight: 500; color: #25312F; }
.price-text { font-family: "Fira Sans", Consolas, monospace; font-size: 14px; font-weight: 600; color: #E97A7A; }
.phone-text { font-family: "Fira Sans", Consolas, monospace; font-size: 13px; }
.addr-text { font-size: 13px; color: #8B9794; max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; display: inline-block; }

.modal-overlay { position: fixed; inset: 0; background: rgba(37, 49, 47, 0.5); display: flex; align-items: center; justify-content: center; z-index: 100; backdrop-filter: blur(4px); }
.modal-content { background: #fff; border-radius: 16px; padding: 0; width: 520px; max-width: 90vw; max-height: 80vh; overflow-y: auto; box-shadow: 0 20px 60px rgba(37, 49, 47, 0.15); }
.modal-lg { width: 680px; }
.modal-header { display: flex; align-items: center; justify-content: space-between; padding: 20px 24px 0; h3 { margin: 0; font-size: 18px; font-weight: 600; color: #25312F; } }
.modal-close { width: 32px; height: 32px; border: none; background: #FAFCFB; border-radius: 8px; font-size: 20px; color: #8B9794; cursor: pointer; display: flex; align-items: center; justify-content: center; transition: all 0.2s; &:hover { background: #FFE8E8; color: #E97A7A; } }
form { padding: 20px 24px 24px; }
.form-grid { display: grid; gap: 16px; &.cols-2 { grid-template-columns: 1fr 1fr; } .full-width { grid-column: 1 / -1; } }
.form-group { display: flex; flex-direction: column; gap: 6px; label { font-size: 13px; font-weight: 500; color: #5F6B68; } }
.modal-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px; padding-top: 16px; border-top: 1px solid #EEF2F0; }

@media (max-width: 768px) {
  .data-header { flex-direction: column; align-items: flex-start; }
  .table { th, td { padding: 10px 12px; } }
  .form-grid.cols-2 { grid-template-columns: 1fr; }
}
</style>
