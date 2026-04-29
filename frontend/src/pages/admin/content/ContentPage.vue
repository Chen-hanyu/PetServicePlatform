<template>
  <section class="admin-page">
    <div class="tabs">
      <button v-for="tab in tabs" :key="tab.key" :class="['tab-btn', { active: activeTab === tab.key }]" @click="activeTab = tab.key">
        {{ tab.label }}
      </button>
    </div>

    <!-- 帖子审核 -->
    <div v-if="activeTab === 'posts'" class="card">
      <div class="top-row">
        <h2 class="section-title">帖子审核</h2>
        <div class="filters">
          <select v-model="postStatus" class="input"><option value="">全部</option><option>PENDING</option><option>APPROVED</option><option>REJECTED</option></select>
          <button class="btn btn-secondary" @click="loadPosts">刷新</button>
        </div>
      </div>
      <DataState :loading="postLoading" :error="postError" :empty="posts.length === 0">
        <table class="table">
          <thead><tr><th>标题</th><th>分类</th><th>状态</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-for="post in posts" :key="post.id">
              <td>{{ post.title }}</td><td>{{ post.category }}</td>
              <td><StatusBadge :variant="postStatusVariant(post.status)">{{ post.status }}</StatusBadge></td>
              <td class="ops">
                <button class="btn btn-xs" @click="reviewPost(post.id, 'APPROVED')">通过</button>
                <button class="btn btn-xs btn-danger" @click="reviewPost(post.id, 'REJECTED')">驳回</button>
              </td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- 评论管理 -->
    <div v-if="activeTab === 'comments'" class="card">
      <div class="top-row"><h2 class="section-title">评论管理</h2></div>
      <div class="filter-bar">
        <input v-model="commentKeyword" placeholder="评论内容" class="input" />
        <button class="btn btn-secondary" @click="loadComments">查询</button>
      </div>
      <DataState :loading="commentLoading" :error="commentError" :empty="comments.length === 0">
        <table class="table">
          <thead><tr><th>评论内容</th><th>所属帖子</th><th>评论人</th><th>时间</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-for="c in comments" :key="c.id">
              <td>{{ c.content }}</td><td>{{ c.post_title }}</td><td>{{ c.author_nickname }}</td><td>{{ c.created_at }}</td>
              <td class="ops"><button class="btn btn-xs btn-danger" @click="deleteComment(c.id)">删除</button></td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- Banner 管理 -->
    <div v-if="activeTab === 'banners'" class="card">
      <div class="top-row"><h2 class="section-title">Banner 管理</h2><button class="btn btn-primary" @click="openBannerModal()">+ 新增 Banner</button></div>
      <DataState :loading="bannerLoading" :error="bannerError" :empty="banners.length === 0">
        <table class="table">
          <thead><tr><th>标题</th><th>图片</th><th>链接</th><th>状态</th><th>排序</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-for="b in banners" :key="b.id">
              <td>{{ b.title }}</td><td><img :src="b.image_url" class="thumb" /></td><td>{{ b.link_url }}</td>
              <td><StatusBadge :variant="b.status === 'ACTIVE' ? 'success' : 'danger'">{{ b.status }}</StatusBadge></td><td>{{ b.sort }}</td>
              <td class="ops"><button class="btn btn-xs" @click="openBannerModal(b)">编辑</button><button class="btn btn-xs btn-danger" @click="deleteBanner(b.id)">删除</button></td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- 标签管理 -->
    <div v-if="activeTab === 'tags'" class="card">
      <div class="top-row"><h2 class="section-title">标签管理</h2><button class="btn btn-primary" @click="openTagModal()">+ 新增标签</button></div>
      <div class="filter-bar">
        <input v-model="tagKeyword" placeholder="标签名称" class="input" />
        <select v-model="tagType" class="input"><option value="">全部类型</option><option>community</option><option>knowledge</option></select>
        <button class="btn btn-secondary" @click="loadTags">查询</button>
      </div>
      <DataState :loading="tagLoading" :error="tagError" :empty="tags.length === 0">
        <table class="table">
          <thead><tr><th>ID</th><th>名称</th><th>类型</th><th>状态</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-for="t in tags" :key="t.id">
              <td>{{ t.id }}</td><td>{{ t.name }}</td><td>{{ t.type }}</td>
              <td><StatusBadge :variant="t.status === 'ACTIVE' ? 'success' : 'danger'">{{ t.status }}</StatusBadge></td>
              <td class="ops"><button class="btn btn-xs" @click="openTagModal(t)">编辑</button><button class="btn btn-xs btn-danger" @click="deleteTag(t.id)">删除</button></td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- 推荐位管理 -->
    <div v-if="activeTab === 'recommendations'" class="card">
      <div class="top-row"><h2 class="section-title">推荐位管理</h2><button class="btn btn-primary" @click="openRecModal()">+ 新增推荐</button></div>
      <div class="filter-bar">
        <input v-model="recKeyword" placeholder="业务名称" class="input" />
        <button class="btn btn-secondary" @click="loadRecommendations">查询</button>
      </div>
      <DataState :loading="recLoading" :error="recError" :empty="recommendations.length === 0">
        <table class="table">
          <thead><tr><th>ID</th><th>业务类型</th><th>业务ID</th><th>推荐位编码</th><th>状态</th><th>排序</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-for="r in recommendations" :key="r.id">
              <td>{{ r.id }}</td><td>{{ r.biz_type }}</td><td>{{ r.biz_id }}</td><td>{{ r.slot_code }}</td>
              <td><StatusBadge :variant="r.status === 'ACTIVE' ? 'success' : 'danger'">{{ r.status }}</StatusBadge></td><td>{{ r.sort }}</td>
              <td class="ops"><button class="btn btn-xs" @click="openRecModal(r)">编辑</button><button class="btn btn-xs btn-danger" @click="deleteRecommendation(r.id)">删除</button></td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- Banner 弹窗 -->
    <Teleport to="body">
      <div v-if="bannerModalVisible" class="modal" @click.self="closeBannerModal">
        <div class="modal-content">
          <h3>{{ editingBanner ? '编辑 Banner' : '新增 Banner' }}</h3>
          <form @submit.prevent="saveBanner">
            <div><label>标题</label><input v-model="bannerForm.title" required /></div>
            <div><label>图片URL</label><input v-model="bannerForm.image_url" required /></div>
            <div><label>链接URL</label><input v-model="bannerForm.link_url" /></div>
            <div><label>状态</label><select v-model="bannerForm.status"><option>ACTIVE</option><option>INACTIVE</option></select></div>
            <div><label>排序</label><input v-model.number="bannerForm.sort" type="number" /></div>
            <div class="modal-actions"><button type="button" class="btn btn-secondary" @click="closeBannerModal">取消</button><button type="submit" class="btn btn-primary">保存</button></div>
          </form>
        </div>
      </div>
    </Teleport>

    <!-- 标签弹窗 -->
    <Teleport to="body">
      <div v-if="tagModalVisible" class="modal" @click.self="closeTagModal">
        <div class="modal-content">
          <h3>{{ editingTag ? '编辑标签' : '新增标签' }}</h3>
          <form @submit.prevent="saveTag">
            <div><label>名称</label><input v-model="tagForm.name" required /></div>
            <div><label>类型</label><select v-model="tagForm.type"><option>community</option><option>knowledge</option></select></div>
            <div><label>状态</label><select v-model="tagForm.status"><option>ACTIVE</option><option>INACTIVE</option></select></div>
            <div><label>排序</label><input v-model.number="tagForm.sort" type="number" /></div>
            <div class="modal-actions"><button type="button" class="btn btn-secondary" @click="closeTagModal">取消</button><button type="submit" class="btn btn-primary">保存</button></div>
          </form>
        </div>
      </div>
    </Teleport>

    <!-- 推荐位弹窗 -->
    <Teleport to="body">
      <div v-if="recModalVisible" class="modal" @click.self="closeRecModal">
        <div class="modal-content">
          <h3>{{ editingRec ? '编辑推荐位' : '新增推荐位' }}</h3>
          <form @submit.prevent="saveRecommendation">
            <div><label>业务类型</label><input v-model="recForm.biz_type" required /></div>
            <div><label>业务ID</label><input v-model.number="recForm.biz_id" type="number" required /></div>
            <div><label>推荐位编码</label><input v-model="recForm.slot_code" required /></div>
            <div><label>状态</label><select v-model="recForm.status"><option>ACTIVE</option><option>INACTIVE</option></select></div>
            <div><label>排序</label><input v-model.number="recForm.sort" type="number" /></div>
            <div class="modal-actions"><button type="button" class="btn btn-secondary" @click="closeRecModal">取消</button><button type="submit" class="btn btn-primary">保存</button></div>
          </form>
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
  fetchAdminPosts, reviewAdminPost,
  fetchAdminComments, deleteAdminComment,
  fetchAdminBanners, createAdminBanner, updateAdminBanner, deleteAdminBanner,
  fetchAdminTags, createAdminTag, updateAdminTag, deleteAdminTag,
  fetchAdminRecommendations, createAdminRecommendation, updateAdminRecommendation, deleteAdminRecommendation,
} from '@/api/modules/admin';
import { toErrorMessage } from '@/api/http';

const tabs = [
  { key: 'posts', label: '帖子审核' },
  { key: 'comments', label: '评论管理' },
  { key: 'banners', label: 'Banner管理' },
  { key: 'tags', label: '标签管理' },
  { key: 'recommendations', label: '推荐位管理' },
];
const activeTab = ref('posts');

// 帖子审核
const posts = ref<any[]>([]);
const postLoading = ref(false);
const postError = ref('');
const postStatus = ref('');
const loadPosts = async () => {
  postLoading.value = true;
  try {
    const res = await fetchAdminPosts({ status: postStatus.value || undefined, page: 1, page_size: 20 });
    posts.value = res.list || [];
  } catch (e) { postError.value = toErrorMessage(e); } finally { postLoading.value = false; }
};
const reviewPost = async (id: number, status: string) => {
  try {
    await reviewAdminPost(id, status, `管理员${status === 'APPROVED' ? '通过' : '驳回'}`);
    await loadPosts();
  } catch (e) { postError.value = toErrorMessage(e); }
};
const postStatusVariant = (s: string) => ({ APPROVED: 'success', REJECTED: 'danger', PENDING: 'warning' }[s] || 'neutral');

// 评论管理
const comments = ref<any[]>([]);
const commentLoading = ref(false);
const commentError = ref('');
const commentKeyword = ref('');
const loadComments = async () => {
  commentLoading.value = true;
  try {
    const res = await fetchAdminComments({ keyword: commentKeyword.value || undefined, page: 1, page_size: 20 });
    comments.value = res.list || [];
  } catch (e) { commentError.value = toErrorMessage(e); } finally { commentLoading.value = false; }
};
const deleteComment = async (id: number) => {
  if (confirm('删除评论不可恢复')) {
    try { await deleteAdminComment(id); await loadComments(); } catch (e) { commentError.value = toErrorMessage(e); }
  }
};

// Banner 管理
const banners = ref<any[]>([]);
const bannerLoading = ref(false);
const bannerError = ref('');
const bannerModalVisible = ref(false);
const editingBanner = ref<any>(null);
const bannerForm = ref({ title: '', image_url: '', link_url: '', status: 'ACTIVE', sort: 0 });
const loadBanners = async () => {
  bannerLoading.value = true;
  try { banners.value = await fetchAdminBanners(); } catch (e) { bannerError.value = toErrorMessage(e); } finally { bannerLoading.value = false; }
};
const openBannerModal = (b?: any) => {
  if (b) { editingBanner.value = b; bannerForm.value = { ...b }; }
  else { editingBanner.value = null; bannerForm.value = { title: '', image_url: '', link_url: '', status: 'ACTIVE', sort: 0 }; }
  bannerModalVisible.value = true;
};
const closeBannerModal = () => { bannerModalVisible.value = false; };
const saveBanner = async () => {
  try {
    if (editingBanner.value) await updateAdminBanner(editingBanner.value.id, bannerForm.value);
    else await createAdminBanner(bannerForm.value);
    await loadBanners();
    closeBannerModal();
  } catch (e) { bannerError.value = toErrorMessage(e); }
};
const deleteBanner = async (id: number) => {
  if (confirm('确定删除')) { try { await deleteAdminBanner(id); await loadBanners(); } catch (e) { bannerError.value = toErrorMessage(e); } }
};

// 标签管理
const tags = ref<any[]>([]);
const tagLoading = ref(false);
const tagError = ref('');
const tagKeyword = ref('');
const tagType = ref('');
const tagModalVisible = ref(false);
const editingTag = ref<any>(null);
const tagForm = ref({ name: '', type: 'community', status: 'ACTIVE', sort: 0 });
const loadTags = async () => {
  tagLoading.value = true;
  try {
    const res = await fetchAdminTags({ keyword: tagKeyword.value || undefined, type: tagType.value || undefined, page: 1, page_size: 50 });
    tags.value = res.list || [];
  } catch (e) { tagError.value = toErrorMessage(e); } finally { tagLoading.value = false; }
};
const openTagModal = (t?: any) => {
  if (t) { editingTag.value = t; tagForm.value = { ...t }; }
  else { editingTag.value = null; tagForm.value = { name: '', type: 'community', status: 'ACTIVE', sort: 0 }; }
  tagModalVisible.value = true;
};
const closeTagModal = () => { tagModalVisible.value = false; };
const saveTag = async () => {
  try {
    if (editingTag.value) await updateAdminTag(editingTag.value.id, tagForm.value);
    else await createAdminTag(tagForm.value);
    await loadTags();
    closeTagModal();
  } catch (e) { tagError.value = toErrorMessage(e); }
};
const deleteTag = async (id: number) => {
  if (confirm('确定删除')) { try { await deleteAdminTag(id); await loadTags(); } catch (e) { tagError.value = toErrorMessage(e); } }
};

// 推荐位管理
const recommendations = ref<any[]>([]);
const recLoading = ref(false);
const recError = ref('');
const recKeyword = ref('');
const recModalVisible = ref(false);
const editingRec = ref<any>(null);
const recForm = ref({ biz_type: '', biz_id: 0, slot_code: '', status: 'ACTIVE', sort: 0 });
const loadRecommendations = async () => {
  recLoading.value = true;
  try {
    const res = await fetchAdminRecommendations({ keyword: recKeyword.value || undefined, page: 1, page_size: 50 });
    recommendations.value = res.list || [];
  } catch (e) { recError.value = toErrorMessage(e); } finally { recLoading.value = false; }
};
const openRecModal = (r?: any) => {
  if (r) { editingRec.value = r; recForm.value = { ...r }; }
  else { editingRec.value = null; recForm.value = { biz_type: '', biz_id: 0, slot_code: '', status: 'ACTIVE', sort: 0 }; }
  recModalVisible.value = true;
};
const closeRecModal = () => { recModalVisible.value = false; };
const saveRecommendation = async () => {
  try {
    if (editingRec.value) await updateAdminRecommendation(editingRec.value.id, recForm.value);
    else await createAdminRecommendation(recForm.value);
    await loadRecommendations();
    closeRecModal();
  } catch (e) { recError.value = toErrorMessage(e); }
};
const deleteRecommendation = async (id: number) => {
  if (confirm('确定删除')) { try { await deleteAdminRecommendation(id); await loadRecommendations(); } catch (e) { recError.value = toErrorMessage(e); } }
};

// 初始化加载
loadPosts();
loadComments();
loadBanners();
loadTags();
loadRecommendations();
</script>

<style scoped lang="scss">
/* 复用之前的样式，并补充 .thumb */
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