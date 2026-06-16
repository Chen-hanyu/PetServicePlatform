<template>
  <div class="admin-page">
    <div class="tabs-card">
      <button v-for="tab in tabs" :key="tab.key" :class="['tab-btn', { active: activeTab === tab.key }]" @click="activeTab = tab.key">
        <span class="tab-icon" v-html="tab.icon"></span>
        {{ tab.label }}
      </button>
    </div>

    <!-- 商品列表 -->
    <div v-if="activeTab === 'products'" class="data-card">
      <div class="data-header">
        <h3 class="data-title">商品管理</h3>
        <button class="btn btn-primary" @click="openProductModal()">+ 新增商品</button>
      </div>
      <div class="filter-bar">
        <input v-model="productKeyword" placeholder="商品名称" class="input input-sm" />
        <select v-model="productCategory" class="input input-sm">
          <option value="">全部分类</option>
          <option v-for="category in productCategories" :key="category.id" :value="category.id">{{ category.name }}</option>
        </select>
        <button class="btn btn-secondary" @click="loadProducts">查询</button>
      </div>
      <DataState :loading="productLoading" :error="productError" :empty="visibleProducts.length === 0">
        <table class="table">
          <thead><tr><th>ID</th><th>商品名称</th><th>分类</th><th>价格</th><th>库存</th><th>状态</th><th class="col-ops">操作</th></tr></thead>
          <tbody>
            <tr v-for="p in visibleProducts" :key="p.id">
              <td><span class="id-tag">#{{ p.id }}</span></td>
              <td><span class="product-name">{{ p.name }}</span></td>
              <td><span class="tag tag-light">{{ productCategoryName(p.category_id) }}</span></td>
              <td><span class="price-text">¥{{ p.price }}</span></td>
              <td><span class="stock-badge">{{ p.stock }}</span></td>
              <td><StatusBadge :variant="p.status === 'ON_SALE' ? 'success' : 'warning'">{{ productStatusLabel(p.status) }}</StatusBadge></td>
              <td class="col-ops ops-group">
                <button class="btn btn-xs" @click="openProductModal(p)">编辑</button>
                <button class="btn btn-xs" :class="p.status === 'ON_SALE' ? 'btn-warning' : 'btn-success'" @click="toggleProductStatus(p)">{{ p.status === 'ON_SALE' ? '下架' : '上架' }}</button>
                <button class="btn btn-xs btn-danger" @click="deleteProduct(p.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- 订单列表 -->
    <div v-if="activeTab === 'orders'" class="data-card">
      <div class="data-header">
        <h3 class="data-title">订单管理</h3>
        <div class="data-actions">
          <select v-model="orderStatus" class="input input-sm"><option value="">全部状态</option><option value="PENDING">待付款</option><option value="PAID">已付款</option><option value="SHIPPED">已发货</option><option value="DELIVERED">已送达</option><option value="CANCELLED">已取消</option></select>
          <button class="btn btn-secondary" @click="loadOrders">查询</button>
        </div>
      </div>
      <DataState :loading="orderLoading" :error="orderError" :empty="orders.length === 0">
        <table class="table">
          <thead><tr><th>ID</th><th>商品</th><th>数量</th><th>金额</th><th>状态</th><th class="col-ops">操作</th></tr></thead>
          <tbody>
            <tr v-for="o in orders" :key="o.id">
              <td><span class="id-tag">#{{ o.id }}</span></td>
              <td><span class="product-name">{{ formatOrderProducts(o) }}</span></td>
              <td>{{ formatOrderQuantity(o) }}</td>
              <td><span class="price-text">¥{{ o.total_amount }}</span></td>
              <td><StatusBadge :variant="orderStatusVariant(o.status)">{{ o.status }}</StatusBadge></td>
              <td class="col-ops ops-group">
                <button v-if="o.status === 'PAID'" class="btn btn-xs btn-success" @click="updateOrderStatus(o.id, 'SHIPPED')">发货</button>
                <button v-if="o.status === 'SHIPPED'" class="btn btn-xs btn-success" @click="updateOrderStatus(o.id, 'DELIVERED')">确认送达</button>
                <button v-if="o.status === 'PENDING'" class="btn btn-xs btn-danger" @click="updateOrderStatus(o.id, 'CANCELLED')">取消</button>
              </td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- 商品弹窗 -->
    <Teleport to="body">
      <div v-if="productModalVisible" class="modal-overlay" @click.self="closeProductModal">
        <div class="modal-content modal-lg">
          <div class="modal-header">
            <h3>{{ editingProduct ? '编辑商品' : '新增商品' }}</h3>
            <button class="modal-close" @click="closeProductModal">&times;</button>
          </div>
          <form @submit.prevent="saveProduct">
            <div class="form-grid cols-2">
              <div class="form-group"><label>商品名称</label><input v-model="productForm.name" required class="input" /></div>
              <div class="form-group">
                <label>分类</label>
                <select v-model.number="productForm.category_id" required class="input">
                  <option :value="0" disabled>请选择分类</option>
                  <option v-for="category in productCategories" :key="category.id" :value="category.id">{{ category.name }}</option>
                </select>
              </div>
              <div class="form-group"><label>副标题</label><input v-model="productForm.subtitle" class="input" /></div>
              <div class="form-group"><label>价格</label><input v-model.number="productForm.price" type="number" step="0.01" required class="input" /></div>
              <div class="form-group"><label>库存</label><input v-model.number="productForm.stock" type="number" required class="input" /></div>
              <div class="form-group"><label>适用宠物</label><select v-model="productForm.pet_type" class="input"><option value="ALL">通用</option><option value="CAT">猫咪</option><option value="DOG">狗狗</option></select></div>
              <div class="form-group"><label>状态</label><select v-model="productForm.status" class="input"><option value="ON_SALE">上架</option><option value="OFF_SHELF">下架</option></select></div>
              <div class="form-group full-width"><label>封面图URL</label><input v-model="productForm.image_url" required class="input" /></div>
              <div class="form-group full-width"><label>描述</label><textarea v-model="productForm.description" class="input" rows="3"></textarea></div>
            </div>
            <div class="modal-actions">
              <button type="button" class="btn btn-cancel" @click="closeProductModal">取消</button>
              <button type="submit" class="btn btn-primary">保存</button>
            </div>
          </form>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, reactive } from 'vue';
import DataState from '@/components/DataState.vue';
import StatusBadge from '@/components/StatusBadge.vue';
import {
  fetchAdminProducts, createAdminProduct, updateAdminProduct, deleteAdminProduct,
  fetchAdminOrders, updateAdminOrder,
  fetchAdminShopCategories,
} from '@/api/modules/admin';
import { toErrorMessage } from '@/api/http';

const tabs = [
  { key: 'products', label: '商品管理', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 2L3 6v14a2 2 0 002 2h14a2 2 0 002-2V6l-3-4zM3 6h18M16 10a4 4 0 01-8 0"/></svg>' },
  { key: 'orders', label: '订单管理', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"/></svg>' },
];
const activeTab = ref('products');

// 商品列表
const products = ref<any[]>([]);
const productCategories = ref<any[]>([]);
const productLoading = ref(false);
const productError = ref('');
const productKeyword = ref('');
const productCategory = ref('');
const visibleProducts = computed(() => {
  if (!productCategory.value) return products.value;
  return products.value.filter((product) => Number(product.category_id || product.categoryId) === Number(productCategory.value));
});
const loadProductCategories = async () => {
  try {
    productCategories.value = await fetchAdminShopCategories();
  } catch (e) {
    productError.value = toErrorMessage(e);
  }
};
const loadProducts = async () => {
  productLoading.value = true;
  try {
    const res = await fetchAdminProducts({ keyword: productKeyword.value || undefined, page: 1, page_size: 50 });
    products.value = res.list || [];
  } catch (e) { productError.value = toErrorMessage(e); } finally { productLoading.value = false; }
};
const productModalVisible = ref(false);
const editingProduct = ref<any>(null);
const productForm = reactive({
  name: '',
  category_id: 0,
  subtitle: '',
  image_url: '',
  price: 0,
  stock: 0,
  pet_type: 'ALL',
  description: '',
  status: 'ON_SALE'
});
const productCategoryName = (categoryId?: number) => productCategories.value.find((item) => Number(item.id) === Number(categoryId))?.name || '-';
const productStatusLabel = (status: string) => status === 'ON_SALE' ? '上架' : '下架';
const defaultProductCategoryId = () => Number(productCategories.value.find((item) => item.status !== 'DISABLED')?.id || productCategories.value[0]?.id || 0);
const normalizeImageUrl = (value: string) => {
  const raw = value.trim();
  if (!raw) return '';
  try {
    const url = new URL(raw);
    const imageUrl = url.searchParams.get('objurl') || url.searchParams.get('imgurl');
    return imageUrl ? decodeURIComponent(imageUrl) : raw;
  } catch {
    return raw;
  }
};
const openProductModal = (p?: any) => {
  if (p) {
    editingProduct.value = p;
    Object.assign(productForm, {
      name: p.name || '',
      category_id: Number(p.category_id || p.categoryId || 0),
      subtitle: p.subtitle || '',
      image_url: p.image_url || p.imageUrl || '',
      price: Number(p.price || 0),
      stock: Number(p.stock || 0),
      pet_type: p.pet_type || p.petType || 'ALL',
      description: p.description || '',
      status: p.status || 'ON_SALE'
    });
  }
  else {
    editingProduct.value = null;
    Object.assign(productForm, {
      name: '',
      category_id: defaultProductCategoryId(),
      subtitle: '',
      image_url: '',
      price: 0,
      stock: 0,
      pet_type: 'ALL',
      description: '',
      status: 'ON_SALE'
    });
  }
  productModalVisible.value = true;
};
const closeProductModal = () => { productModalVisible.value = false; };
const buildProductPayload = () => {
  const imageUrl = normalizeImageUrl(productForm.image_url);
  if (!productForm.category_id) throw new Error('请先选择商品分类');
  if (!imageUrl) throw new Error('请填写商品封面图 URL');
  if (imageUrl.length > 1000) throw new Error('图片 URL 过长，请使用直接图片地址或上传后的图片地址');
  return {
    name: productForm.name.trim(),
    category_id: Number(productForm.category_id),
    subtitle: productForm.subtitle.trim(),
    image_url: imageUrl,
    price: Number(productForm.price),
    stock: Number(productForm.stock),
    pet_type: productForm.pet_type,
    description: productForm.description.trim(),
    status: productForm.status
  };
};
const saveProduct = async () => {
  try {
    const payload = buildProductPayload();
    if (editingProduct.value) await updateAdminProduct(editingProduct.value.id, payload);
    else await createAdminProduct(payload);
    await loadProducts(); closeProductModal();
  } catch (e) { productError.value = toErrorMessage(e); }
};
const toggleProductStatus = async (p: any) => {
  const ns = p.status === 'ON_SALE' ? 'OFF_SHELF' : 'ON_SALE';
  const payload = {
    name: p.name,
    category_id: Number(p.category_id || p.categoryId),
    subtitle: p.subtitle || '',
    image_url: p.image_url || p.imageUrl || '',
    price: Number(p.price),
    stock: Number(p.stock || 0),
    pet_type: p.pet_type || p.petType || 'ALL',
    description: p.description || '',
    status: ns
  };
  try { await updateAdminProduct(p.id, payload); await loadProducts(); } catch (e) { productError.value = toErrorMessage(e); }
};
const deleteProduct = async (id: number) => {
  if (confirm('确定删除该商品吗？')) { try { await deleteAdminProduct(id); await loadProducts(); } catch (e) { productError.value = toErrorMessage(e); } }
};

// 订单列表
const orders = ref<any[]>([]);
const orderLoading = ref(false);
const orderError = ref('');
const orderStatus = ref('');
const loadOrders = async () => {
  orderLoading.value = true;
  try { const res = await fetchAdminOrders({ status: orderStatus.value || undefined, page: 1, page_size: 50 }); orders.value = res.list || []; } catch (e) { orderError.value = toErrorMessage(e); } finally { orderLoading.value = false; }
};
const formatOrderProducts = (order: any) => {
  if (Array.isArray(order.items) && order.items.length > 0) {
    return order.items.map((item: any) => item.product_name || item.productName || item.name).filter(Boolean).join('、') || '-';
  }
  return order.product_name || order.productName || '-';
};
const formatOrderQuantity = (order: any) => {
  if (Array.isArray(order.items) && order.items.length > 0) {
    return order.items.reduce((sum: number, item: any) => sum + Number(item.quantity || 0), 0);
  }
  return order.quantity ?? '-';
};
const orderStatusVariant = (s: string) => ({ PENDING: 'warning', PAID: 'info', SHIPPED: 'info', DELIVERED: 'success', COMPLETED: 'success', CANCELLED: 'danger' }[s] || 'neutral');
const updateOrderStatus = async (id: number, status: string) => {
  try { await updateAdminOrder(id, { status }); await loadOrders(); } catch (e) { orderError.value = toErrorMessage(e); }
};

loadProductCategories();
loadProducts();
loadOrders();
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
.product-name { font-weight: 500; color: #25312F; }
.price-text { font-family: "Fira Sans", Consolas, monospace; font-size: 14px; font-weight: 600; color: #E97A7A; }
.stock-badge { display: inline-flex; align-items: center; justify-content: center; min-width: 28px; padding: 2px 8px; background: #FAFCFB; border-radius: 6px; font-size: 13px; color: #8B9794; font-family: "Fira Sans", Consolas, monospace; }

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
