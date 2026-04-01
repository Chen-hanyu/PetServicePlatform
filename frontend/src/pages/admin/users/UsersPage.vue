<template>
  <section class="card page">
    <div class="top-row">
      <h2 class="section-title">用户管理</h2>
      <div class="filters">
        <input v-model.trim="keyword" class="input" placeholder="搜索手机号/昵称" />
        <select v-model="status" class="input">
          <option value="">全部状态</option>
          <option value="ACTIVE">ACTIVE</option>
          <option value="DISABLED">DISABLED</option>
        </select>
        <button class="btn btn-secondary" @click="loadUsers">查询</button>
      </div>
    </div>

    <DataState :loading="loading" :error="error" :empty="users.length === 0" empty-text="暂无用户数据">
      <table class="table">
        <thead><tr><th>ID</th><th>昵称</th><th>手机号</th><th>状态</th><th>操作</th></tr></thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.nickname }}</td>
            <td>{{ user.phone || "-" }}</td>
            <td><StatusBadge :variant="user.status === 'ACTIVE' ? 'success' : 'warning'">{{ user.status }}</StatusBadge></td>
            <td>
              <button class="btn btn-secondary" @click="toggleStatus(user.id, user.status)">{{ user.status === "ACTIVE" ? "禁用" : "启用" }}</button>
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
import { fetchAdminUsers, updateAdminUserStatus } from "@/api/modules/admin";
import { toErrorMessage } from "@/api/http";
import type { UserProfile } from "@/types/auth";

const loading = ref(false);
const error = ref("");
const users = ref<UserProfile[]>([]);
const keyword = ref("");
const status = ref("");

const loadUsers = async () => {
  loading.value = true;
  error.value = "";
  try {
    const data = await fetchAdminUsers({ keyword: keyword.value || undefined, status: status.value || undefined, page: 1, page_size: 20 });
    users.value = data.list || [];
  } catch (e) {
    error.value = toErrorMessage(e);
  } finally {
    loading.value = false;
  }
};

const toggleStatus = async (id: number, current: string) => {
  try {
    const next = current === "ACTIVE" ? "DISABLED" : "ACTIVE";
    await updateAdminUserStatus(id, next, `由前端后台页面切换为 ${next}`);
    await loadUsers();
  } catch (e) {
    error.value = toErrorMessage(e);
  }
};

loadUsers();
</script>

<style scoped lang="scss">
.page { display: grid; gap: 12px; }
.top-row { display: flex; justify-content: space-between; gap: 8px; }
.filters { display: flex; gap: 8px; }
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { border-bottom: 1px solid #e3ece8; padding: 10px; text-align: left; }
@media (max-width: 900px) { .top-row { flex-direction: column; align-items: flex-start; } .filters { width: 100%; display: grid; grid-template-columns: 1fr; }}
</style>
