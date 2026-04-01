<template>
  <section class="card page">
    <div class="top-row">
      <h2 class="section-title">内容审核</h2>
      <div class="filters">
        <select v-model="status" class="input">
          <option value="">全部</option>
          <option value="PENDING">PENDING</option>
          <option value="APPROVED">APPROVED</option>
          <option value="REJECTED">REJECTED</option>
        </select>
        <button class="btn btn-secondary" @click="loadPosts">刷新</button>
      </div>
    </div>

    <DataState :loading="loading" :error="error" :empty="posts.length === 0" empty-text="暂无帖子">
      <table class="table">
        <thead><tr><th>标题</th><th>分类</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="post in posts" :key="post.id">
            <td>{{ post.title }}</td>
            <td>{{ post.category }}</td>
            <td><StatusBadge :variant="statusVariant(post.status)">{{ post.status }}</StatusBadge></td>
            <td class="ops">
              <button class="btn btn-secondary" @click="review(post.id, 'APPROVED')">通过</button>
              <button class="btn btn-secondary" @click="review(post.id, 'REJECTED')">驳回</button>
            </td>
          </tr>
        </tbody>
      </table>
    </DataState>
  </section>
</template>

<script setup lang="ts">
import { ref } from "vue";
import DataState from "@/components/DataState.vue";
import StatusBadge from "@/components/StatusBadge.vue";
import { fetchAdminPosts, reviewAdminPost } from "@/api/modules/admin";
import { toErrorMessage } from "@/api/http";
import type { PostSummary } from "@/types/community";

const loading = ref(false);
const error = ref("");
const status = ref("");
const posts = ref<PostSummary[]>([]);

const statusVariant = (value: string) => {
  if (value === "APPROVED") return "success";
  if (value === "REJECTED") return "danger";
  if (value === "PENDING") return "warning";
  return "neutral";
};

const loadPosts = async () => {
  loading.value = true;
  error.value = "";
  try {
    const data = await fetchAdminPosts({ status: status.value || undefined, page: 1, page_size: 20 });
    posts.value = data.list || [];
  } catch (e) {
    error.value = toErrorMessage(e);
  } finally {
    loading.value = false;
  }
};

const review = async (id: number, next: string) => {
  try {
    await reviewAdminPost(id, next, `后台审核为 ${next}`);
    await loadPosts();
  } catch (e) {
    error.value = toErrorMessage(e);
  }
};

loadPosts();
</script>

<style scoped lang="scss">
.page { display: grid; gap: 12px; }
.top-row { display: flex; justify-content: space-between; gap: 10px; }
.filters { display: flex; gap: 8px; }
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { padding: 10px; border-bottom: 1px solid #e3ece8; text-align: left; }
.ops { display: flex; gap: 8px; }
</style>
