<template>
  <section class="admin-page">
    <div class="tabs">
      <button v-for="tab in tabs" :key="tab.key" :class="['tab-btn', { active: activeTab === tab.key }]" @click="activeTab = tab.key">
        {{ tab.label }}
      </button>
    </div>

    <!-- 服务分类 -->
    <div v-if="activeTab === 'categories'" class="card">
      <div class="top-row"><h2 class="section-title">服务分类</h2><button class="btn btn-primary" @click="openCategoryModal()">+ 新增</button></div>
      <DataState :loading="catLoading" :error="catError" :empty="categories.length === 0">
        <table class="table">
          <thead><tr><th>ID</th><th>名称</th><th>排序</th><th>状态</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-for="cat in categories" :key="cat.id">
              <td>{{ cat.id }}</td><td>{{ cat.name }}</td><td>{{ cat.sort }}</td>
              <td><StatusBadge :variant="cat.status === 'ACTIVE' ? 'success' : 'danger'">{{ cat.status }}</StatusBadge></td>
              <td class="ops"><button class="btn btn-xs" @click="openCategoryModal(cat)">编辑</button><button class="btn btn-xs btn-danger" @click="deleteCategory(cat.id)">删除</button></td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- 商家管理 -->
    <div v-if="activeTab === 'merchants'" class="card">
      <div class="top-row"><h2 class="section-title">商家管理</h2><button class="btn btn-primary" @click="openMerchantModal()">+ 新增商家</button></div>
      <div class="filter-bar">
        <input v-model="merchantKeyword" placeholder="商家名称" class="input" />
        <select v-model="merchantStatus" class="input"><option value="">全部状态</option><option>ACTIVE</option><option>INACTIVE</option></select>
        <button class="btn btn-secondary" @click="loadMerchants">查询</button>
      </div>
      <DataState :loading="merchantLoading" :error="merchantError" :empty="merchants.length === 0">
        <table class="table">
          <thead><tr><th>ID</th><th>名称</th><th>区域</th><th>状态</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-for="m in merchants" :key="m.id">
              <td>{{ m.id }}</td><td>{{ m.name }}</td><td>{{ m.district }}</td>
              <td><StatusBadge :variant="m.status === 'ACTIVE' ? 'success' : 'danger'">{{ m.status }}</StatusBadge></td>
              <td class="ops"><button class="btn btn-xs" @click="openMerchantModal(m)">编辑</button><button class="btn btn-xs btn-danger" @click="deleteMerchant(m.id)">删除</button></td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- 服务项目 -->
    <div v-if="activeTab === 'services'" class="card">
      <div class="top-row"><h2 class="section-title">服务项目</h2><button class="btn btn-primary" @click="openServiceItemModal()">+ 新增项目</button></div>
      <div class="filter-bar"><input v-model="serviceKeyword" placeholder="项目名称" class="input" /><button class="btn btn-secondary" @click="loadServiceItems">查询</button></div>
      <DataState :loading="serviceLoading" :error="serviceError" :empty="serviceItems.length === 0">
        <table class="table">
          <thead><tr><th>ID</th><th>商家名称</th><th>项目名称</th><th>价格</th><th>时长(min)</th><th>状态</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-for="item in serviceItems" :key="item.id">
              <td>{{ item.id }}</td><td>{{ item.merchant_name }}</td><td>{{ item.name }}</td><td>{{ item.price }}</td><td>{{ item.duration_minutes }}</td>
              <td><StatusBadge :variant="item.status === 'ACTIVE' ? 'success' : 'danger'">{{ item.status }}</StatusBadge></td>
              <td class="ops"><button class="btn btn-xs" @click="openServiceItemModal(item)">编辑</button><button class="btn btn-xs btn-danger" @click="deleteServiceItem(item.id)">删除</button></td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- 预约单管理 -->
    <div v-if="activeTab === 'bookings'" class="card">
      <div class="top-row"><h2 class="section-title">预约单管理</h2></div>
      <div class="filter-bar">
        <select v-model="bookingStatus" class="input"><option value="">全部状态</option><option>PENDING</option><option>CONFIRMED</option><option>COMPLETED</option><option>CANCELLED</option></select>
        <button class="btn btn-secondary" @click="loadBookings">查询</button>
      </div>
      <DataState :loading="bookingLoading" :error="bookingError" :empty="bookings.length === 0">
        <table class="table">
          <thead><tr><th>ID</th><th>用户</th><th>商家</th><th>项目</th><th>预约时间</th><th>状态</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-for="bk in bookings" :key="bk.id">
              <td>{{ bk.id }}</td><td>{{ bk.user_nickname }}</td><td>{{ bk.merchant_name }}</td><td>{{ bk.service_name }}</td><td>{{ bk.booking_time }}</td>
              <td><StatusBadge :variant="bookingStatusVariant(bk.status)">{{ bk.status }}</StatusBadge></td>
              <td class="ops">
                <button class="btn btn-xs" @click="updateBookingStatus(bk.id, 'CONFIRMED')">确认</button>
                <button class="btn btn-xs btn-danger" @click="updateBookingStatus(bk.id, 'CANCELLED')">取消</button>
                <button class="btn btn-xs" @click="viewBookingDetail(bk)">详情</button>
              </td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- 服务分类弹窗 -->
    <Teleport to="body">
      <div v-if="categoryModalVisible" class="modal" @click.self="closeCategoryModal">
        <div class="modal-content">
          <h3>{{ editingCategory ? '编辑分类' : '新增分类' }}</h3>
          <form @submit.prevent="saveCategory">
            <div><label>名称</label><input v-model="categoryForm.name" required /></div>
            <div><label>排序</label><input v-model.number="categoryForm.sort" type="number" /></div>
            <div><label>状态</label><select v-model="categoryForm.status"><option>ACTIVE</option><option>INACTIVE</option></select></div>
            <div class="modal-actions"><button type="button" class="btn btn-secondary" @click="closeCategoryModal">取消</button><button type="submit" class="btn btn-primary">保存</button></div>
          </form>
        </div>
      </div>
    </Teleport>

    <!-- 商家弹窗 -->
    <Teleport to="body">
      <div v-if="merchantModalVisible" class="modal" @click.self="closeMerchantModal">
        <div class="modal-content">
          <h3>{{ editingMerchant ? '编辑商家' : '新增商家' }}</h3>
          <form @submit.prevent="saveMerchant">
            <div><label>名称</label><input v-model="merchantForm.name" required /></div>
            <div><label>区域</label><input v-model="merchantForm.district" required /></div>
            <div><label>地址</label><input v-model="merchantForm.address" required /></div>
            <div><label>电话</label><input v-model="merchantForm.phone" required /></div>
            <div><label>营业时间</label><input v-model="merchantForm.business_hours" required /></div>
            <div><label>状态</label><select v-model="merchantForm.status"><option>ACTIVE</option><option>INACTIVE</option></select></div>
            <div class="modal-actions"><button type="button" class="btn btn-secondary" @click="closeMerchantModal">取消</button><button type="submit" class="btn btn-primary">保存</button></div>
          </form>
        </div>
      </div>
    </Teleport>

    <!-- 服务项目弹窗 -->
    <Teleport to="body">
      <div v-if="serviceItemModalVisible" class="modal" @click.self="closeServiceItemModal">
        <div class="modal-content">
          <h3>{{ editingServiceItem ? '编辑服务项目' : '新增服务项目' }}</h3>
          <form @submit.prevent="saveServiceItem">
            <div><label>商家ID</label><input v-model.number="serviceItemForm.merchant_id" type="number" required /></div>
            <div><label>分类ID</label><input v-model.number="serviceItemForm.category_id" type="number" required /></div>
            <div><label>名称</label><input v-model="serviceItemForm.name" required /></div>
            <div><label>价格</label><input v-model.number="serviceItemForm.price" step="0.01" required /></div>
            <div><label>时长(分钟)</label><input v-model.number="serviceItemForm.duration_minutes" type="number" required /></div>
            <div><label>状态</label><select v-model="serviceItemForm.status"><option>ACTIVE</option><option>INACTIVE</option></select></div>
            <div class="modal-actions"><button type="button" class="btn btn-secondary" @click="closeServiceItemModal">取消</button><button type="submit" class="btn btn-primary">保存</button></div>
          </form>
        </div>
      </div>
    </Teleport>

    <!-- 预约单详情弹窗 -->
    <Teleport to="body">
      <div v-if="bookingDetailVisible" class="modal" @click.self="bookingDetailVisible = false">
        <div class="modal-content">
          <h3>预约单详情</h3>
          <p><strong>用户：</strong>{{ currentBooking?.user_nickname }}</p>
          <p><strong>商家：</strong>{{ currentBooking?.merchant_name }}</p>
          <p><strong>项目：</strong>{{ currentBooking?.service_name }}</p>
          <p><strong>预约时间：</strong>{{ currentBooking?.booking_time }}</p>
          <p><strong>联系人：</strong>{{ currentBooking?.contact_name }}</p>
          <p><strong>联系电话：</strong>{{ currentBooking?.contact_phone }}</p>
          <p><strong>备注：</strong>{{ currentBooking?.remark }}</p>
          <div class="modal-actions"><button class="btn btn-primary" @click="bookingDetailVisible = false">关闭</button></div>
        </div>
      </div>
    </Teleport>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import DataState from '@/components/DataState.vue';
import StatusBadge from '@/components/StatusBadge.vue';
import {
  fetchAdminServiceCategories, createAdminServiceCategory, updateAdminServiceCategory, deleteAdminServiceCategory,
  fetchAdminMerchants, createAdminMerchant, updateAdminMerchant, deleteAdminMerchant,
  fetchAdminServiceItems, createAdminServiceItem, updateAdminServiceItem, deleteAdminServiceItem,
  fetchAdminBookings, updateAdminBooking,
} from '@/api/modules/admin';
import { toErrorMessage } from '@/api/http';

const tabs = [
  { key: 'categories', label: '服务分类' },
  { key: 'merchants', label: '商家管理' },
  { key: 'services', label: '服务项目' },
  { key: 'bookings', label: '预约单管理' },
];
const activeTab = ref('categories');

// 服务分类
const categories = ref<any[]>([]);
const catLoading = ref(false);
const catError = ref('');
const categoryModalVisible = ref(false);
const editingCategory = ref<any>(null);
const categoryForm = ref({ name: '', sort: 0, status: 'ACTIVE' });
const loadCategories = async () => {
  catLoading.value = true;
  try { categories.value = await fetchAdminServiceCategories(); } catch (e) { catError.value = toErrorMessage(e); } finally { catLoading.value = false; }
};
const openCategoryModal = (cat?: any) => {
  editingCategory.value = cat || null;
  categoryForm.value = cat ? { ...cat } : { name: '', sort: 0, status: 'ACTIVE' };
  categoryModalVisible.value = true;
};
const closeCategoryModal = () => { categoryModalVisible.value = false; };
const saveCategory = async () => {
  try {
    if (editingCategory.value) await updateAdminServiceCategory(editingCategory.value.id, categoryForm.value);
    else await createAdminServiceCategory(categoryForm.value);
    await loadCategories();
    closeCategoryModal();
  } catch (e) { catError.value = toErrorMessage(e); }
};
const deleteCategory = async (id: number) => {
  if (confirm('确定删除分类？')) {
    try { await deleteAdminServiceCategory(id); await loadCategories(); } catch (e) { catError.value = toErrorMessage(e); }
  }
};

// 商家管理
const merchants = ref<any[]>([]);
const merchantLoading = ref(false);
const merchantError = ref('');
const merchantKeyword = ref('');
const merchantStatus = ref('');
const merchantModalVisible = ref(false);
const editingMerchant = ref<any>(null);
const merchantForm = ref({ name: '', district: '', address: '', phone: '', business_hours: '', status: 'ACTIVE' });
const loadMerchants = async () => {
  merchantLoading.value = true;
  try {
    const res = await fetchAdminMerchants({ keyword: merchantKeyword.value || undefined, status: merchantStatus.value || undefined, page: 1, page_size: 50 });
    merchants.value = res.list || [];
  } catch (e) { merchantError.value = toErrorMessage(e); } finally { merchantLoading.value = false; }
};
const openMerchantModal = (m?: any) => {
  editingMerchant.value = m || null;
  merchantForm.value = m ? { ...m } : { name: '', district: '', address: '', phone: '', business_hours: '', status: 'ACTIVE' };
  merchantModalVisible.value = true;
};
const closeMerchantModal = () => { merchantModalVisible.value = false; };
const saveMerchant = async () => {
  try {
    if (editingMerchant.value) await updateAdminMerchant(editingMerchant.value.id, merchantForm.value);
    else await createAdminMerchant(merchantForm.value);
    await loadMerchants();
    closeMerchantModal();
  } catch (e) { merchantError.value = toErrorMessage(e); }
};
const deleteMerchant = async (id: number) => {
  if (confirm('确定删除商家？')) {
    try { await deleteAdminMerchant(id); await loadMerchants(); } catch (e) { merchantError.value = toErrorMessage(e); }
  }
};

// 服务项目
const serviceItems = ref<any[]>([]);
const serviceLoading = ref(false);
const serviceError = ref('');
const serviceKeyword = ref('');
const serviceItemModalVisible = ref(false);
const editingServiceItem = ref<any>(null);
const serviceItemForm = ref({ merchant_id: 0, category_id: 0, name: '', price: 0, duration_minutes: 0, status: 'ACTIVE' });
const loadServiceItems = async () => {
  serviceLoading.value = true;
  try {
    const res = await fetchAdminServiceItems({ keyword: serviceKeyword.value || undefined, page: 1, page_size: 50 });
    serviceItems.value = res.list || [];
  } catch (e) { serviceError.value = toErrorMessage(e); } finally { serviceLoading.value = false; }
};
const openServiceItemModal = (s?: any) => {
  editingServiceItem.value = s || null;
  serviceItemForm.value = s ? { ...s } : { merchant_id: 0, category_id: 0, name: '', price: 0, duration_minutes: 0, status: 'ACTIVE' };
  serviceItemModalVisible.value = true;
};
const closeServiceItemModal = () => { serviceItemModalVisible.value = false; };
const saveServiceItem = async () => {
  try {
    if (editingServiceItem.value) await updateAdminServiceItem(editingServiceItem.value.id, serviceItemForm.value);
    else await createAdminServiceItem(serviceItemForm.value);
    await loadServiceItems();
    closeServiceItemModal();
  } catch (e) { serviceError.value = toErrorMessage(e); }
};
const deleteServiceItem = async (id: number) => {
  if (confirm('确定删除服务项目？')) {
    try { await deleteAdminServiceItem(id); await loadServiceItems(); } catch (e) { serviceError.value = toErrorMessage(e); }
  }
};

// 预约单管理
const bookings = ref<any[]>([]);
const bookingLoading = ref(false);
const bookingError = ref('');
const bookingStatus = ref('');
const bookingDetailVisible = ref(false);
const currentBooking = ref<any>(null);
const loadBookings = async () => {
  bookingLoading.value = true;
  try {
    const res = await fetchAdminBookings({ status: bookingStatus.value || undefined, page: 1, page_size: 50 });
    bookings.value = res.list || [];
  } catch (e) { bookingError.value = toErrorMessage(e); } finally { bookingLoading.value = false; }
};
const bookingStatusVariant = (s: string) => ({ PENDING: 'warning', CONFIRMED: 'success', COMPLETED: 'info', CANCELLED: 'danger' }[s] || 'neutral');
const updateBookingStatus = async (id: number, status: string) => {
  try {
    await updateAdminBooking(id, { status, remark: '管理员操作' });
    await loadBookings();
  } catch (e) { bookingError.value = toErrorMessage(e); }
};
const viewBookingDetail = (bk: any) => {
  currentBooking.value = bk;
  bookingDetailVisible.value = true;
};

loadCategories();
loadMerchants();
loadServiceItems();
loadBookings();
</script>

<style scoped lang="scss">
/* 复用之前的样式即可 */
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