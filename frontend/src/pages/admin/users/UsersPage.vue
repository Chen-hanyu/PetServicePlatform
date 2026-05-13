<template>
  <div class="admin-page">
    <!-- 筛选栏 -->
    <div class="filter-card">
      <div class="filter-row">
        <div class="filter-group">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" class="filter-icon">
            <circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/>
          </svg>
          <input v-model.trim="keyword" class="input" placeholder="搜索手机号/昵称" />
        </div>
        <div class="filter-group">
          <select v-model="status" class="input">
            <option value="">全部状态</option>
            <option value="ACTIVE">正常</option>
            <option value="DISABLED">已禁用</option>
          </select>
        </div>
        <button class="btn btn-primary" @click="loadUsers">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
          查询
        </button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="data-card">
      <div class="data-header">
        <h3 class="data-title">用户列表</h3>
        <span class="data-count">共 {{ users.length }} 条</span>
      </div>

      <DataState :loading="loading" :error="error" :empty="users.length === 0" empty-text="暂无用户数据">
        <table class="table">
          <thead>
            <tr>
              <th class="col-id">ID</th>
              <th>用户信息</th>
              <th>手机号</th>
              <th>注册时间</th>
              <th>状态</th>
              <th class="col-ops">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id">
              <td class="col-id"><span class="id-tag">#{{ user.id }}</span></td>
              <td>
                <div class="user-cell">
                  <span class="user-avatar">{{ (user.nickname || '?').charAt(0) }}</span>
                  <span class="user-name">{{ user.nickname }}</span>
                </div>
              </td>
              <td><span class="phone-text">{{ user.phone || '-' }}</span></td>
              <td><span class="time-text">{{ user.created_at || '-' }}</span></td>
              <td>
                <StatusBadge :variant="user.status === 'ACTIVE' ? 'success' : 'warning'">
                  {{ user.status === 'ACTIVE' ? '正常' : '已禁用' }}
                </StatusBadge>
              </td>
              <td class="col-ops">
                <button
                  class="btn btn-xs"
                  :class="user.status === 'ACTIVE' ? 'btn-warning' : 'btn-primary'"
                  @click="toggleStatus(user.id, user.status)"
                >
                  {{ user.status === "ACTIVE" ? "禁用" : "启用" }}
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </DataState>
    </div>
  </div>
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
.admin-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 筛选卡片 */
.filter-card {
  background: #fff;
  border-radius: 16px;
  padding: 16px 20px;
  border: 1px solid #DDE6E3;
  box-shadow: 0 2px 8px rgba(37, 49, 47, 0.04);
}

.filter-row {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.filter-group {
  position: relative;
  flex: 1;
  min-width: 200px;

  .filter-icon {
    position: absolute;
    left: 12px;
    top: 50%;
    transform: translateY(-50%);
    width: 16px;
    height: 16px;
    color: #B0BAB7;
  }

  .input {
    padding-left: 36px;
  }
}

.input {
  border: 1px solid #DDE6E3;
  border-radius: 10px;
  background: #FAFCFB;
  min-height: 40px;
  padding: 8px 14px;
  outline: none;
  width: 100%;
  font-size: 14px;
  color: #25312F;
  transition: all 0.2s;

  &:focus {
    border-color: #7ECFBC;
    box-shadow: 0 0 0 3px rgba(126, 207, 188, 0.15);
    background: #fff;
  }
}

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

  svg {
    width: 16px;
    height: 16px;
  }

  &:hover {
    transform: translateY(-1px);
  }
}

.btn-primary {
  background: #7ECFBC;
  color: #fff;
  box-shadow: 0 4px 12px rgba(126, 207, 188, 0.3);

  &:hover {
    background: #6BC0AC;
  }
}

.btn-xs {
  padding: 6px 14px;
  font-size: 12px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;

  &:hover {
    transform: translateY(-1px);
  }
}

.btn-warning {
  background: #FFF8E6;
  color: #E6A23C;
  border: 1px solid #FFE8B0;

  &:hover {
    background: #FFF0CC;
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
}

.data-title {
  font-size: 16px;
  font-weight: 600;
  color: #25312F;
  margin: 0;
}

.data-count {
  font-size: 13px;
  color: #8B9794;
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

    &:hover {
      background: #FAFCFB;
    }

    &:last-child td {
      border-bottom: none;
    }
  }
}

.col-id {
  width: 80px;
}

.col-ops {
  width: 100px;
  text-align: center;
}

.id-tag {
  font-family: "Fira Sans", Consolas, monospace;
  font-size: 13px;
  color: #B0BAB7;
  font-weight: 500;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #E8F5F1;
  color: #7ECFBC;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  flex-shrink: 0;
}

.user-name {
  font-weight: 500;
  color: #25312F;
}

.phone-text {
  font-family: "Fira Sans", Consolas, monospace;
  font-size: 13px;
}

.time-text {
  font-size: 13px;
  color: #8B9794;
}

@media (max-width: 768px) {
  .filter-row {
    flex-direction: column;
  }

  .filter-group {
    width: 100%;
    min-width: unset;
  }

  .table {
    th, td {
      padding: 10px 12px;
    }
  }
}
</style>
