<template>
  <div class="admin-wrap">
    <!-- 侧边导航 -->
    <aside class="sidebar">
      <div class="sidebar-header">
        <div class="logo-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M4 7h16M4 12h16M4 17h16"/>
          </svg>
        </div>
        <h3 class="logo-text">宠物之家</h3>
        <span class="logo-badge">运营后台</span>
      </div>

      <nav class="nav-list">
        <RouterLink
          v-for="item in menus"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: isActive(item.path) }"
        >
          <span class="nav-icon" v-html="item.icon"></span>
          <span class="nav-label">{{ item.name }}</span>
          <span v-if="'badge' in item && item.badge" class="nav-badge">{{ item.badge }}</span>
        </RouterLink>
      </nav>

      <div class="sidebar-footer">
        <RouterLink to="/home" class="back-link">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M3 9l9-7 9 7v11a2 2 0 01-2 2H5a2 2 0 01-2-2z"/>
            <polyline points="9 22 9 12 15 12 15 22"/>
          </svg>
          返回前台
        </RouterLink>
      </div>
    </aside>

    <!-- 主内容区 -->
    <div class="main-area">
      <!-- 顶部栏 -->
      <header class="topbar">
        <div class="topbar-left">
          <h2 class="page-title">{{ currentPageTitle }}</h2>
        </div>
        <div class="topbar-right">
          <div class="admin-info">
            <span class="admin-avatar">{{ adminInitial }}</span>
            <span class="admin-name">{{ adminName }}</span>
          </div>
          <button class="logout-btn" @click="handleLogout" title="退出登录">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M9 21H5a2 2 0 01-2-2V5a2 2 0 012-2h4M16 17l5-5-5-5M21 12H9"/>
            </svg>
          </button>
        </div>
      </header>

      <!-- 内容区 -->
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "@/store/auth";

const route = useRoute();
const router = useRouter();
const auth = useAuthStore();

const menus = [
  {
    name: "仪表盘",
    path: "/admin/dashboard",
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/></svg>'
  },
  {
    name: "用户管理",
    path: "/admin/users",
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75"/></svg>'
  },
  {
    name: "内容审核",
    path: "/admin/content",
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>'
  },
  {
    name: "领养管理",
    path: "/admin/adoption",
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 7h16M4 12h16M4 17h16"/><circle cx="12" cy="12" r="3"/></svg>'
  },
  {
    name: "服务管理",
    path: "/admin/services",
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 010 2.83 2 2 0 01-2.83 0l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-2 2 2 2 0 01-2-2v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83 0 2 2 0 010-2.83l.06-.06A1.65 1.65 0 004.68 15a1.65 1.65 0 00-1.51-1H3a2 2 0 01-2-2 2 2 0 012-2h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 010-2.83 2 2 0 012.83 0l.06.06A1.65 1.65 0 009 4.68a1.65 1.65 0 001-1.51V3a2 2 0 012-2 2 2 0 012 2v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 0 2 2 0 010 2.83l-.06.06a1.65 1.65 0 00-.33 1.82V9a1.65 1.65 0 001.51 1H21a2 2 0 012 2 2 2 0 01-2 2h-.09a1.65 1.65 0 00-1.51 1z"/></svg>'
  },
  {
    name: "商城管理",
    path: "/admin/shop",
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 2L3 6v14a2 2 0 002 2h14a2 2 0 002-2V6l-3-4zM3 6h18M16 10a4 4 0 01-8 0"/></svg>'
  },
  {
    name: "监控面板",
    path: "/admin/monitoring",
    icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 20V10M12 20V4M6 20v-6"/></svg>'
  }
];

const pageTitles: Record<string, string> = {
  "/admin/dashboard": "仪表盘",
  "/admin/users": "用户管理",
  "/admin/content": "内容审核",
  "/admin/adoption": "领养管理",
  "/admin/services": "服务管理",
  "/admin/shop": "商城管理"
};

const currentPageTitle = computed(() => pageTitles[route.path] || "运营后台");
const adminName = computed(() => auth.user?.nickname || "管理员");
const adminInitial = computed(() => adminName.value.charAt(0));

const isActive = (path: string) => route.path.startsWith(path);

const handleLogout = () => {
  auth.logout();
  router.push("/admin/login");
};
</script>

<style scoped lang="scss">
.admin-wrap {
  display: flex;
  min-height: 100vh;
  background: #F6F8F8;
  font-family: "PingFang SC", "Microsoft YaHei", sans-serif;
}

/* ===== 侧边导航 ===== */
.sidebar {
  width: 240px;
  min-height: 100vh;
  background: #2F3A38;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  position: sticky;
  top: 0;
  height: 100vh;
}

.sidebar-header {
  padding: 24px 20px 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;

  .logo-icon {
    width: 36px;
    height: 36px;
    background: #7ECFBC;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;

    svg {
      width: 20px;
      height: 20px;
    }
  }

  .logo-text {
    font-size: 18px;
    font-weight: 700;
    color: #fff;
    margin: 0;
    letter-spacing: 1px;
  }

  .logo-badge {
    font-size: 10px;
    color: rgba(255, 255, 255, 0.5);
    background: rgba(255, 255, 255, 0.08);
    padding: 2px 8px;
    border-radius: 4px;
    margin-left: auto;
  }
}

.nav-list {
  flex: 1;
  padding: 12px 12px;
  display: flex;
  flex-direction: column;
  gap: 2px;
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border-radius: 10px;
  color: rgba(255, 255, 255, 0.65);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
  position: relative;

  &:hover {
    color: #fff;
    background: rgba(255, 255, 255, 0.06);
  }

  &.active {
    color: #fff;
    background: rgba(126, 207, 188, 0.2);

    &::before {
      content: '';
      position: absolute;
      left: -12px;
      top: 50%;
      transform: translateY(-50%);
      width: 3px;
      height: 20px;
      background: #7ECFBC;
      border-radius: 0 3px 3px 0;
    }
  }

  .nav-icon {
    width: 20px;
    height: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    :deep(svg) {
      width: 20px;
      height: 20px;
    }
  }

  .nav-label {
    flex: 1;
  }

  .nav-badge {
    background: #E97A7A;
    color: #fff;
    font-size: 10px;
    padding: 2px 6px;
    border-radius: 8px;
    min-width: 18px;
    text-align: center;
  }
}

.sidebar-footer {
  padding: 16px 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);

  .back-link {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 14px;
    border-radius: 10px;
    color: rgba(255, 255, 255, 0.45);
    text-decoration: none;
    font-size: 13px;
    transition: all 0.2s;

    svg {
      width: 16px;
      height: 16px;
    }

    &:hover {
      color: #fff;
      background: rgba(255, 255, 255, 0.06);
    }
  }
}

/* ===== 主区域 ===== */
.main-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

/* ===== 顶部栏 ===== */
.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 32px;
  background: #fff;
  border-bottom: 1px solid #DDE6E3;
  position: sticky;
  top: 0;
  z-index: 50;

  .page-title {
    font-size: 20px;
    font-weight: 600;
    color: #25312F;
    margin: 0;
  }

  .topbar-right {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  .admin-info {
    display: flex;
    align-items: center;
    gap: 10px;

    .admin-avatar {
      width: 36px;
      height: 36px;
      border-radius: 50%;
      background: #7ECFBC;
      color: #fff;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 14px;
      font-weight: 600;
    }

    .admin-name {
      font-size: 14px;
      font-weight: 500;
      color: #25312F;
    }
  }

  .logout-btn {
    width: 36px;
    height: 36px;
    border: 1px solid #DDE6E3;
    border-radius: 10px;
    background: #fff;
    color: #8B9794;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;

    svg {
      width: 18px;
      height: 18px;
    }

    &:hover {
      border-color: #E97A7A;
      color: #E97A7A;
      background: #FFF5F5;
    }
  }
}

/* ===== 内容区 ===== */
.content {
  flex: 1;
  padding: 24px 32px;
  overflow-y: auto;
}

/* ===== 响应式 ===== */
@media (max-width: 900px) {
  .sidebar {
    width: 64px;

    .sidebar-header {
      padding: 16px 12px;
      justify-content: center;

      .logo-text,
      .logo-badge {
        display: none;
      }
    }

    .nav-item {
      justify-content: center;
      padding: 12px;

      .nav-label,
      .nav-badge {
        display: none;
      }

      &.active::before {
        left: -12px;
      }
    }

    .sidebar-footer {
      padding: 12px;

      .back-link span {
        display: none;
      }
    }
  }

  .topbar {
    padding: 12px 16px;
  }

  .content {
    padding: 16px;
  }
}
</style>
