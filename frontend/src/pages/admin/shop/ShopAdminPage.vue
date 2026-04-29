<template>
  <section class="admin-page">
    <div class="tabs">
      <button v-for="tab in tabs" :key="tab.key" :class="['tab-btn', { active: activeTab === tab.key }]" @click="activeTab = tab.key">
        {{ tab.label }}
      </button>
    </div>

    <!-- 商品分类 -->
    <div v-if="activeTab === 'categories'" class="card">
      <div class="top-row"><h2 class="section-title">商品分类</h2><button class="btn btn-primary" @click="openCategoryModal()">+ 新增分类</button></div>
      <DataState :loading="catLoading" :error="catError" :empty="categories.length === 0">
        <table class="table">
          <thead><tr><th>ID</th><th>名称</th><th>适用宠物</th><th>状态</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-for="cat in categories" :key="cat.id">
              <td>{{ cat.id }}</td><td>{{ cat.name }}</td><td>{{ cat.pet_type || '-' }}</td>
              <td><StatusBadge :variant="cat.status === 'ACTIVE' ? 'success' : 'danger'">{{ cat.status }}</StatusBadge></td>
              <td class="ops"><button class="btn btn-xs" @click="openCategoryModal(cat)">编辑</button><button class="btn btn-xs btn-danger" @click="deleteCategory(cat.id)">删除</button></td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- 商品管理 -->
    <div v-if="activeTab === 'products'" class="card">
      <div class="top-row"><h2 class="section-title">商品管理</h2><button class="btn btn-primary" @click="openProductModal()">+ 新增商品</button></div>
      <div class="filter-bar">
        <input v-model="productKeyword" placeholder="商品名称" class="input" />
        <select v-model="productStatus" class="input"><option value="">全部状态</option><option>ON_SALE</option><option>OFF_SHELF</option></select>
        <button class="btn btn-secondary" @click="loadProducts">查询</button>
      </div>
      <DataState :loading="productLoading" :error="productError" :empty="products.length === 0">
        <table class="table">
          <thead><tr><th>ID</th><th>商品图</th><th>名称</th><th>价格</th><th>库存</th><th>状态</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-for="prod in products" :key="prod.id">
              <td>{{ prod.id }}</td><td><img :src="prod.image_url" class="thumb" /></td><td>{{ prod.name }}</td><td>{{ prod.price }}</td><td>{{ prod.stock }}</td>
              <td><StatusBadge :variant="prod.status === 'ON_SALE' ? 'success' : 'danger'">{{ prod.status }}</StatusBadge></td>
              <td class="ops">
                <button class="btn btn-xs" @click="openProductModal(prod)">编辑</button>
                <button class="btn btn-xs btn-danger" @click="toggleProductStatus(prod)">{{ prod.status === 'ON_SALE' ? '下架' : '上架' }}</button>
                <button class="btn btn-xs btn-danger" @click="deleteProduct(prod.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- 订单管理 -->
    <div v-if="activeTab === 'orders'" class="card">
      <div class="top-row"><h2 class="section-title">订单管理</h2></div>
      <div class="filter-bar">
        <input v-model="orderKeyword" placeholder="订单号/收货人" class="input" />
        <select v-model="orderStatus" class="input"><option value="">全部状态</option><option>PENDING</option><option>PAID</option><option>SHIPPED</option><option>COMPLETED</option><option>CANCELLED</option></select>
        <button class="btn btn-secondary" @click="loadOrders">查询</button>
      </div>
      <DataState :loading="orderLoading" :error="orderError" :empty="orders.length === 0">
        <table class="table">
          <thead><tr><th>订单号</th><th>用户</th><th>总金额</th><th>收货人</th><th>状态</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-for="order in orders" :key="order.id">
              <td>{{ order.order_no }}</td><td>{{ order.user_nickname }}</td><td>{{ order.total_amount }}</td><td>{{ order.receiver_name }}</td>
              <td><StatusBadge :variant="orderStatusVariant(order.status)">{{ order.status }}</StatusBadge></td>
              <td class="ops">
                <button class="btn btn-xs" @click="viewOrderDetail(order)">详情</button>
                <button v-if="order.status === 'PAID'" class="btn btn-xs" @click="updateOrderStatus(order.id, 'SHIPPED')">发货</button>
                <button v-if="order.status === 'SHIPPED'" class="btn btn-xs" @click="updateOrderStatus(order.id, 'COMPLETED')">完成</button>
                <button v-if="order.status === 'PENDING'" class="btn btn-xs btn-danger" @click="updateOrderStatus(order.id, 'CANCELLED')">取消</button>
              </td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- 分类弹窗 -->
    <Teleport to="body">
      <div v-if="categoryModalVisible" class="modal" @click.self="closeCategoryModal">
        <div class="modal-content">
          <h3>{{ editingCategory ? '编辑分类' : '新增分类' }}</h3>
          <form @submit.prevent="saveCategory">
            <div><label>名称</label><input v-model="categoryForm.name" required /></div>
            <div><label>适用宠物</label><input v-model="categoryForm.pet_type" /></div>
            <div><label>排序</label><input v-model.number="categoryForm.sort" type="number" /></div>
            <div><label>状态</label><select v-model="categoryForm.status"><option>ACTIVE</option><option>INACTIVE</option></select></div>
            <div class="modal-actions"><button type="button" class="btn btn-secondary" @click="closeCategoryModal">取消</button><button type="submit" class="btn btn-primary">保存</button></div>
          </form>
        </div>
      </div>
    </Teleport>

    <!-- 商品弹窗 -->
    <Teleport to="body">
      <div v-if="productModalVisible" class="modal" @click.self="closeProductModal">
        <div class="modal-content">
          <h3>{{ editingProduct ? '编辑商品' : '新增商品' }}</h3>
          <form @submit.prevent="saveProduct">
            <div><label>分类ID</label><input v-model.number="productForm.category_id" type="number" required /></div>
            <div><label>名称</label><input v-model="productForm.name" required /></div>
            <div><label>副标题</label><input v-model="productForm.subtitle" /></div>
            <div><label>主图URL</label><input v-model="productForm.image_url" required /></div>
            <div><label>价格</label><input v-model.number="productForm.price" step="0.01" required /></div>
            <div><label>库存</label><input v-model.number="productForm.stock" type="number" required /></div>
            <div><label>适用宠物</label><input v-model="productForm.pet_type" /></div>
            <div><label>描述</label><textarea v-model="productForm.description"></textarea></div>
            <div><label>状态</label><select v-model="productForm.status"><option>ON_SALE</option><option>OFF_SHELF</option></select></div>
            <div class="modal-actions"><button type="button" class="btn btn-secondary" @click="closeProductModal">取消</button><button type="submit" class="btn btn-primary">保存</button></div>
          </form>
        </div>
      </div>
    </Teleport>

    <!-- 订单详情弹窗 -->
    <Teleport to="body">
      <div v-if="orderDetailVisible" class="modal" @click.self="orderDetailVisible = false">
        <div class="modal-content">
          <h3>订单详情</h3>
          <p><strong>订单号：</strong>{{ currentOrder?.order_no }}</p>
          <p><strong>用户：</strong>{{ currentOrder?.user_nickname }}</p>
          <p><strong>总金额：</strong>{{ currentOrder?.total_amount }}</p>
          <p><strong>收货人：</strong>{{ currentOrder?.receiver_name }}</p>
          <p><strong>收货电话：</strong>{{ currentOrder?.receiver_phone }}</p>
          <p><strong>收货地址：</strong>{{ currentOrder?.receiver_address }}</p>
          <p><strong>状态：</strong>{{ currentOrder?.status }}</p>
          <p><strong>创建时间：</strong>{{ currentOrder?.created_at }}</p>
          <div class="modal-actions"><button class="btn btn-primary" @click="orderDetailVisible = false">关闭</button></div>
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
  fetchAdminShopCategories, createAdminShopCategory, updateAdminShopCategory, deleteAdminShopCategory,
  fetchAdminProducts, createAdminProduct, updateAdminProduct, updateAdminProductStatus, deleteAdminProduct,
  fetchAdminOrders, updateAdminOrder,
} from '@/api/modules/admin';
import { toErrorMessage } from '@/api/http';

const tabs = [
  { key: 'categories', label: '商品分类' },
  { key: 'products', label: '商品管理' },
  { key: 'orders', label: '订单管理' },
];
const activeTab = ref('categories');

// 商品分类
const categories = ref<any[]>([]);
const catLoading = ref(false);
const catError = ref('');
const categoryModalVisible = ref(false);
const editingCategory = ref<any>(null);
const categoryForm = ref({ name: '', pet_type: '', sort: 0, status: 'ACTIVE' });
const loadCategories = async () => {
  catLoading.value = true;
  try { categories.value = await fetchAdminShopCategories(); } catch (e) { catError.value = toErrorMessage(e); } finally { catLoading.value = false; }
};
const openCategoryModal = (cat?: any) => {
  editingCategory.value = cat || null;
  categoryForm.value = cat ? { ...cat } : { name: '', pet_type: '', sort: 0, status: 'ACTIVE' };
  categoryModalVisible.value = true;
};
const closeCategoryModal = () => { categoryModalVisible.value = false; };
const saveCategory = async () => {
  try {
    if (editingCategory.value) await updateAdminShopCategory(editingCategory.value.id, categoryForm.value);
    else await createAdminShopCategory(categoryForm.value);
    await loadCategories();
    closeCategoryModal();
  } catch (e) { catError.value = toErrorMessage(e); }
};
const deleteCategory = async (id: number) => {
  if (confirm('确定删除分类？')) {
    try { await deleteAdminShopCategory(id); await loadCategories(); } catch (e) { catError.value = toErrorMessage(e); }
  }
};

// 商品管理
const products = ref<any[]>([]);
const productLoading = ref(false);
const productError = ref('');
const productKeyword = ref('');
const productStatus = ref('');
const productModalVisible = ref(false);
const editingProduct = ref<any>(null);
const productForm = ref({ category_id: 0, name: '', subtitle: '', image_url: '', price: 0, stock: 0, pet_type: '', description: '', status: 'ON_SALE' });
const loadProducts = async () => {
  productLoading.value = true;
  try {
    const res = await fetchAdminProducts({ keyword: productKeyword.value || undefined, status: productStatus.value || undefined, page: 1, page_size: 20 });
    products.value = res.list || [];
  } catch (e) { productError.value = toErrorMessage(e); } finally { productLoading.value = false; }
};
const openProductModal = (p?: any) => {
  editingProduct.value = p || null;
  productForm.value = p ? { ...p } : { category_id: 0, name: '', subtitle: '', image_url: '', price: 0, stock: 0, pet_type: '', description: '', status: 'ON_SALE' };
  productModalVisible.value = true;
};
const closeProductModal = () => { productModalVisible.value = false; };
const saveProduct = async () => {
  try {
    if (editingProduct.value) await updateAdminProduct(editingProduct.value.id, productForm.value);
    else await createAdminProduct(productForm.value);
    await loadProducts();
    closeProductModal();
  } catch (e) { productError.value = toErrorMessage(e); }
};
const toggleProductStatus = async (prod: any) => {
  const newStatus = prod.status === 'ON_SALE' ? 'OFF_SHELF' : 'ON_SALE';
  try {
    await updateAdminProductStatus(prod.id, newStatus);
    await loadProducts();
  } catch (e) { productError.value = toErrorMessage(e); }
};
const deleteProduct = async (id: number) => {
  if (confirm('确定删除商品？')) {
    try { await deleteAdminProduct(id); await loadProducts(); } catch (e) { productError.value = toErrorMessage(e); }
  }
};

// 订单管理
const orders = ref<any[]>([]);
const orderLoading = ref(false);
const orderError = ref('');
const orderKeyword = ref('');
const orderStatus = ref('');
const orderDetailVisible = ref(false);
const currentOrder = ref<any>(null);
const loadOrders = async () => {
  orderLoading.value = true;
  try {
    const res = await fetchAdminOrders({ keyword: orderKeyword.value || undefined, status: orderStatus.value || undefined, page: 1, page_size: 20 });
    orders.value = res.list || [];
  } catch (e) { orderError.value = toErrorMessage(e); } finally { orderLoading.value = false; }
};
const orderStatusVariant = (s: string) => {
  const map = { PENDING: 'warning', PAID: 'info', SHIPPED: 'info', COMPLETED: 'success', CANCELLED: 'danger' };
  return map[s] || 'neutral';
};
const updateOrderStatus = async (id: number, status: string) => {
  try {
    await updateAdminOrder(id, { status, remark: '管理员操作' });
    await loadOrders();
  } catch (e) { orderError.value = toErrorMessage(e); }
};
const viewOrderDetail = (order: any) => {
  currentOrder.value = order;
  orderDetailVisible.value = true;
};

loadCategories();
loadProducts();
loadOrders();
</script>

<style scoped lang="scss">
/* 复用之前的样式，此处省略 */
.admin-page { display: grid; gap: 20px; }
.tabs { display: flex; gap: 8px; border-bottom: 1px solid var(--border-warm); }
.tab-btn { padding: 8px 16px; border: none; background: transparent; cursor: pointer; font-weight: 600; color: var(--muted); }
.tab-btn.active { color: var(--primary); border-bottom: 2px solid var(--primary); }
.top-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.filter-bar { display: flex; gap: 12px; margin-bottom: 20px; flex-wrap: wrap; }
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { padding: 12px 8px; border-bottom: 1px solid #e3ece8; text-align: left; }
.thumb { width: 40px; height: 40px; object-fit: cover; border-radius: 4px; }
.ops { display: flex; gap: 8px; }
.btn-xs { padding: 4px 8px; font-size: 12px; }
.btn-danger { background: var(--danger); color: white; }
.modal { position: fixed; inset: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 100; }
.modal-content { background: var(--surface); border-radius: 16px; padding: 24px; width: 500px; max-width: 90vw; max-height: 80vh; overflow-y: auto; }
.modal-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 20px; }
</style>