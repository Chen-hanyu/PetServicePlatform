<template>
  <div class="admin-page">
    <!-- Tab 导航 -->
    <div class="tabs-card">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        :class="['tab-btn', { active: activeTab === tab.key }]"
        @click="activeTab = tab.key"
      >
        <span class="tab-icon" v-html="tab.icon"></span>
        {{ tab.label }}
      </button>
    </div>

    <!-- 帖子审核 -->
    <div v-if="activeTab === 'posts'" class="data-card">
      <div class="data-header">
        <h3 class="data-title">帖子审核</h3>
        <div class="data-actions">
          <select v-model="postStatus" class="input input-sm">
            <option value="">全部状态</option>
            <option value="PENDING">待审核</option>
            <option value="APPROVED">已通过</option>
            <option value="REJECTED">已驳回</option>
          </select>
          <button class="btn btn-secondary" @click="loadPosts">刷新</button>
        </div>
      </div>
      <DataState :loading="postLoading" :error="postError" :empty="posts.length === 0">
        <table class="table">
          <thead><tr><th>标题</th><th>分类</th><th>状态</th><th class="col-ops">操作</th></tr></thead>
          <tbody>
            <tr v-for="post in posts" :key="post.id">
              <td><span class="post-title">{{ post.title }}</span></td>
              <td><span class="tag tag-light">{{ post.category }}</span></td>
              <td><StatusBadge :variant="postStatusVariant(post.status)">{{ post.status }}</StatusBadge></td>
              <td class="col-ops ops-group">
                <button class="btn btn-xs btn-success" @click="reviewPost(post.id, 'APPROVED')">通过</button>
                <button class="btn btn-xs btn-danger" @click="reviewPost(post.id, 'REJECTED')">驳回</button>
              </td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- 评论管理 -->
    <div v-if="activeTab === 'comments'" class="data-card">
      <div class="data-header">
        <h3 class="data-title">评论管理</h3>
        <div class="data-actions">
          <div class="filter-group">
            <input v-model="commentKeyword" placeholder="搜索评论内容" class="input input-sm" />
            <button class="btn btn-secondary" @click="loadComments">查询</button>
          </div>
        </div>
      </div>
      <DataState :loading="commentLoading" :error="commentError" :empty="comments.length === 0">
        <table class="table">
          <thead><tr><th>评论内容</th><th>所属帖子</th><th>评论人</th><th>时间</th><th class="col-ops">操作</th></tr></thead>
          <tbody>
            <tr v-for="c in comments" :key="c.id">
              <td><span class="comment-text">{{ c.content }}</span></td>
              <td>{{ c.post_title }}</td>
              <td><span class="user-name-tag">{{ c.author_nickname }}</span></td>
              <td><span class="time-text">{{ c.created_at }}</span></td>
              <td class="col-ops">
                <button class="btn btn-xs btn-danger" @click="deleteComment(c.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- Banner 管理 -->
    <div v-if="activeTab === 'banners'" class="data-card">
      <div class="data-header">
        <h3 class="data-title">Banner 管理</h3>
        <button class="btn btn-primary" @click="openBannerModal()">+ 新增 Banner</button>
      </div>
      <DataState :loading="bannerLoading" :error="bannerError" :empty="banners.length === 0">
        <table class="table">
          <thead><tr><th>标题</th><th>图片</th><th>链接</th><th>状态</th><th>排序</th><th class="col-ops">操作</th></tr></thead>
          <tbody>
            <tr v-for="b in banners" :key="b.id">
              <td>{{ b.title }}</td>
              <td><img :src="b.image_url" class="thumb" /></td>
              <td><span class="link-text">{{ b.link_url }}</span></td>
              <td><StatusBadge :variant="b.status === 'ACTIVE' ? 'success' : 'danger'">{{ b.status }}</StatusBadge></td>
              <td><span class="sort-badge">{{ b.sort }}</span></td>
              <td class="col-ops ops-group">
                <button class="btn btn-xs" @click="openBannerModal(b)">编辑</button>
                <button class="btn btn-xs btn-danger" @click="deleteBanner(b.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- 标签管理 -->
    <div v-if="activeTab === 'tags'" class="data-card">
      <div class="data-header">
        <h3 class="data-title">标签管理</h3>
        <button class="btn btn-primary" @click="openTagModal()">+ 新增标签</button>
      </div>
      <div class="filter-bar">
        <input v-model="tagKeyword" placeholder="标签名称" class="input input-sm" />
        <select v-model="tagType" class="input input-sm"><option value="">全部类型</option><option>community</option><option>knowledge</option></select>
        <button class="btn btn-secondary" @click="loadTags">查询</button>
      </div>
      <DataState :loading="tagLoading" :error="tagError" :empty="tags.length === 0">
        <table class="table">
          <thead><tr><th>ID</th><th>名称</th><th>类型</th><th>状态</th><th class="col-ops">操作</th></tr></thead>
          <tbody>
            <tr v-for="t in tags" :key="t.id">
              <td><span class="id-tag">#{{ t.id }}</span></td>
              <td><span class="tag tag-light">{{ t.name }}</span></td>
              <td>{{ t.type }}</td>
              <td><StatusBadge :variant="t.status === 'ACTIVE' ? 'success' : 'danger'">{{ t.status }}</StatusBadge></td>
              <td class="col-ops ops-group">
                <button class="btn btn-xs" @click="openTagModal(t)">编辑</button>
                <button class="btn btn-xs btn-danger" @click="deleteTag(t.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- 推荐位管理 -->
    <div v-if="activeTab === 'recommendations'" class="data-card">
      <div class="data-header">
        <h3 class="data-title">推荐位管理</h3>
        <button class="btn btn-primary" @click="openRecModal()">+ 新增推荐</button>
      </div>
      <div class="filter-bar">
        <input v-model="recKeyword" placeholder="业务名称" class="input input-sm" />
        <button class="btn btn-secondary" @click="loadRecommendations">查询</button>
      </div>
      <DataState :loading="recLoading" :error="recError" :empty="recommendations.length === 0">
        <table class="table">
          <thead><tr><th>ID</th><th>业务类型</th><th>业务ID</th><th>推荐位编码</th><th>状态</th><th>排序</th><th class="col-ops">操作</th></tr></thead>
          <tbody>
            <tr v-for="r in recommendations" :key="r.id">
              <td><span class="id-tag">#{{ r.id }}</span></td>
              <td>{{ r.biz_type }}</td><td>{{ r.biz_id }}</td><td><span class="code-tag">{{ r.slot_code }}</span></td>
              <td><StatusBadge :variant="r.status === 'ACTIVE' ? 'success' : 'danger'">{{ r.status }}</StatusBadge></td>
              <td><span class="sort-badge">{{ r.sort }}</span></td>
              <td class="col-ops ops-group">
                <button class="btn btn-xs" @click="openRecModal(r)">编辑</button>
                <button class="btn btn-xs btn-danger" @click="deleteRecommendation(r.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>

    <!-- Banner 弹窗 -->
    <Teleport to="body">
      <div v-if="bannerModalVisible" class="modal-overlay" @click.self="closeBannerModal">
        <div class="modal-content">
          <div class="modal-header">
            <h3>{{ editingBanner ? '编辑 Banner' : '新增 Banner' }}</h3>
            <button class="modal-close" @click="closeBannerModal">&times;</button>
          </div>
          <form @submit.prevent="saveBanner">
            <div class="form-grid">
              <div class="form-group"><label>标题</label><input v-model="bannerForm.title" required class="input" /></div>
              <div class="form-group"><label>图片URL</label><input v-model="bannerForm.image_url" required class="input" /></div>
              <div class="form-group"><label>链接URL</label><input v-model="bannerForm.link_url" class="input" /></div>
              <div class="form-group"><label>状态</label><select v-model="bannerForm.status" class="input"><option>ACTIVE</option><option>INACTIVE</option></select></div>
              <div class="form-group"><label>排序</label><input v-model.number="bannerForm.sort" type="number" class="input" /></div>
            </div>
            <div class="modal-actions"><button type="button" class="btn btn-cancel" @click="closeBannerModal">取消</button><button type="submit" class="btn btn-primary">保存</button></div>
          </form>
        </div>
      </div>
    </Teleport>

    <!-- 标签弹窗 -->
    <Teleport to="body">
      <div v-if="tagModalVisible" class="modal-overlay" @click.self="closeTagModal">
        <div class="modal-content">
          <div class="modal-header">
            <h3>{{ editingTag ? '编辑标签' : '新增标签' }}</h3>
            <button class="modal-close" @click="closeTagModal">&times;</button>
          </div>
          <form @submit.prevent="saveTag">
            <div class="form-grid">
              <div class="form-group"><label>名称</label><input v-model="tagForm.name" required class="input" /></div>
              <div class="form-group"><label>类型</label><select v-model="tagForm.type" class="input"><option>community</option><option>knowledge</option></select></div>
              <div class="form-group"><label>状态</label><select v-model="tagForm.status" class="input"><option>ACTIVE</option><option>INACTIVE</option></select></div>
              <div class="form-group"><label>排序</label><input v-model.number="tagForm.sort" type="number" class="input" /></div>
            </div>
            <div class="modal-actions"><button type="button" class="btn btn-cancel" @click="closeTagModal">取消</button><button type="submit" class="btn btn-primary">保存</button></div>
          </form>
        </div>
      </div>
    </Teleport>

    <!-- 推荐位弹窗 -->
    <Teleport to="body">
      <div v-if="recModalVisible" class="modal-overlay" @click.self="closeRecModal">
        <div class="modal-content">
          <div class="modal-header">
            <h3>{{ editingRec ? '编辑推荐位' : '新增推荐位' }}</h3>
            <button class="modal-close" @click="closeRecModal">&times;</button>
          </div>
          <form @submit.prevent="saveRecommendation">
            <div class="form-grid">
              <div class="form-group"><label>业务类型</label><input v-model="recForm.biz_type" required class="input" /></div>
              <div class="form-group"><label>业务ID</label><input v-model.number="recForm.biz_id" type="number" required class="input" /></div>
              <div class="form-group"><label>推荐位编码</label><input v-model="recForm.slot_code" required class="input" /></div>
              <div class="form-group"><label>状态</label><select v-model="recForm.status" class="input"><option>ACTIVE</option><option>INACTIVE</option></select></div>
              <div class="form-group"><label>排序</label><input v-model.number="recForm.sort" type="number" class="input" /></div>
            </div>
            <div class="modal-actions"><button type="button" class="btn btn-cancel" @click="closeRecModal">取消</button><button type="submit" class="btn btn-primary">保存</button></div>
          </form>
        </div>
      </div>
    </Teleport>
  </div>
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
  { key: 'posts', label: '帖子审核', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/></svg>' },
  { key: 'comments', label: '评论管理', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/></svg>' },
  { key: 'banners', label: 'Banner管理', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"/><circle cx="8.5" cy="8.5" r="1.5"/><polyline points="21 15 16 10 5 21"/></svg>' },
  { key: 'tags', label: '标签管理', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.59 13.41l-7.17 7.17a2 2 0 01-2.83 0L2 12V2h10l8.59 8.59a2 2 0 010 2.82z"/><line x1="7" y1="7" x2="7.01" y2="7"/></svg>' },
  { key: 'recommendations', label: '推荐位管理', icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>' },
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
.admin-page { display: flex; flex-direction: column; gap: 16px; }

/* Tab 导航 */
.tabs-card {
  display: flex;
  gap: 4px;
  background: #fff;
  border-radius: 16px;
  padding: 6px;
  border: 1px solid #DDE6E3;
  box-shadow: 0 2px 8px rgba(37, 49, 47, 0.04);
  overflow-x: auto;
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 18px;
  border: none;
  background: transparent;
  cursor: pointer;
  font-weight: 500;
  font-size: 14px;
  color: #8B9794;
  border-radius: 10px;
  transition: all 0.2s;
  white-space: nowrap;

  .tab-icon {
    width: 16px;
    height: 16px;
    display: flex;
    align-items: center;

    :deep(svg) {
      width: 16px;
      height: 16px;
    }
  }

  &:hover {
    color: #5F6B68;
    background: #FAFCFB;
  }

  &.active {
    color: #fff;
    background: #7ECFBC;
    box-shadow: 0 2px 8px rgba(126, 207, 188, 0.3);
  }
}

/* 数据卡片 */
.data-card {
  background: #fff;
  border-radius: 16px;
  border: 1px solid #DDE6E3;
  box-shadow: 0 2px 8px rgba(37, 49, 47, 0.04);
  overflow: hidden;
}

.data-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #EEF2F0;
  flex-wrap: wrap;
  gap: 12px;
}

.data-title {
  font-size: 16px;
  font-weight: 600;
  color: #25312F;
  margin: 0;
}

.data-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.filter-bar {
  display: flex;
  gap: 8px;
  padding: 12px 20px;
  border-bottom: 1px solid #EEF2F0;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  gap: 8px;
  align-items: center;
}

/* 表格 */
.table {
  width: 100%;
  border-collapse: collapse;

  th {
    padding: 12px 16px;
    font-size: 12px;
    font-weight: 600;
    color: #8B9794;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    background: #FAFCFB;
    border-bottom: 1px solid #EEF2F0;
    text-align: left;
    white-space: nowrap;
  }

  td {
    padding: 14px 16px;
    font-size: 14px;
    color: #5F6B68;
    border-bottom: 1px solid #EEF2F0;
  }

  tbody tr {
    transition: background 0.2s;
    &:hover { background: #FAFCFB; }
    &:last-child td { border-bottom: none; }
  }
}

.col-ops { width: 140px; }
.ops-group { display: flex; gap: 6px; }

/* 输入框 */
.input {
  border: 1px solid #DDE6E3;
  border-radius: 10px;
  background: #FAFCFB;
  min-height: 40px;
  padding: 8px 14px;
  outline: none;
  font-size: 14px;
  color: #25312F;
  transition: all 0.2s;

  &:focus {
    border-color: #7ECFBC;
    box-shadow: 0 0 0 3px rgba(126, 207, 188, 0.15);
    background: #fff;
  }
}

.input-sm {
  min-height: 36px;
  padding: 6px 12px;
  font-size: 13px;
}

/* 按钮 */
.btn {
  border: none;
  border-radius: 10px;
  padding: 10px 20px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s;
  &:hover { transform: translateY(-1px); }
}

.btn-primary {
  background: #7ECFBC;
  color: #fff;
  box-shadow: 0 4px 12px rgba(126, 207, 188, 0.3);
  &:hover { background: #6BC0AC; }
}

.btn-secondary {
  background: #FAFCFB;
  color: #5F6B68;
  border: 1px solid #DDE6E3;
  &:hover { background: #F0F5F3; }
}

.btn-cancel {
  background: #FAFCFB;
  color: #8B9794;
  border: 1px solid #DDE6E3;
  &:hover { background: #F0F5F3; }
}

.btn-xs {
  padding: 6px 14px;
  font-size: 12px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
  background: #FAFCFB;
  color: #5F6B68;
  border: 1px solid #DDE6E3;
  &:hover { background: #F0F5F3; transform: translateY(-1px); }
}

.btn-success {
  background: #E8F5F1;
  color: #5BB98C;
  border: 1px solid #C8E8DE;
  &:hover { background: #D4EDE4; }
}

.btn-danger {
  background: #FFE8E8;
  color: #E97A7A;
  border: 1px solid #F5C8C8;
  &:hover { background: #F5D0D0; }
}

/* 标签 */
.tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.tag-light {
  background: #FAFCFB;
  color: #8B9794;
  border: 1px solid #EEF2F0;
}

.id-tag {
  font-family: "Fira Sans", Consolas, monospace;
  font-size: 13px;
  color: #B0BAB7;
  font-weight: 500;
}

.sort-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 28px;
  padding: 2px 8px;
  background: #FAFCFB;
  border-radius: 6px;
  font-size: 13px;
  color: #8B9794;
  font-family: "Fira Sans", Consolas, monospace;
}

.code-tag {
  font-family: "Fira Sans", Consolas, monospace;
  font-size: 12px;
  color: #7AACD1;
  background: #F0F6FA;
  padding: 2px 8px;
  border-radius: 4px;
}

.thumb {
  width: 48px;
  height: 32px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #EEF2F0;
}

.post-title {
  font-weight: 500;
  color: #25312F;
}

.comment-text {
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  max-width: 240px;
}

.user-name-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 2px 10px;
  background: #E8F5F1;
  color: #5BB98C;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.time-text {
  font-size: 13px;
  color: #8B9794;
}

.link-text {
  font-size: 12px;
  color: #7AACD1;
  max-width: 150px;
  display: inline-block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 弹窗 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(37, 49, 47, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  backdrop-filter: blur(4px);
}

.modal-content {
  background: #fff;
  border-radius: 16px;
  padding: 0;
  width: 520px;
  max-width: 90vw;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(37, 49, 47, 0.15);
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px 0;

  h3 {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    color: #25312F;
  }
}

.modal-close {
  width: 32px;
  height: 32px;
  border: none;
  background: #FAFCFB;
  border-radius: 8px;
  font-size: 20px;
  color: #8B9794;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;

  &:hover {
    background: #FFE8E8;
    color: #E97A7A;
  }
}

form {
  padding: 20px 24px 24px;
}

.form-grid {
  display: grid;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;

  label {
    font-size: 13px;
    font-weight: 500;
    color: #5F6B68;
  }
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #EEF2F0;
}

@media (max-width: 768px) {
  .data-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .table {
    th, td { padding: 10px 12px; }
  }
}
</style>
