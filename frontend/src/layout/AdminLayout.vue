<template>
  <div class="admin-wrap">
    <aside class="side">
      <h3>运营后台</h3>
      <RouterLink v-for="item in menus" :key="item.path" :to="item.path" class="menu">{{ item.name }}</RouterLink>
    </aside>
    <section class="content">
      <router-view />
    </section>
  </div>
</template>

<script setup lang="ts">
const menus = [
  { name: "仪表盘", path: "/admin/dashboard" },
  { name: "用户管理", path: "/admin/users" },
  { name: "内容审核", path: "/admin/content" },
  { name: "领养管理", path: "/admin/adoption" },
  { name: "服务管理", path: "/admin/services" },
  { name: "商城管理", path: "/admin/shop" },
  { name: "监控面板", path: "/admin/monitoring" }
];
</script>

<style scoped lang="scss">
.admin-wrap {
  display: grid;
  grid-template-columns: 240px 1fr;
  min-height: 100vh;
}

.side {
  background: linear-gradient(180deg, #5b3d31 0%, #3f2e27 100%);
  color: #ffebdb;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.side h3 {
  margin: 4px 0 10px;
}

.menu {
  color: #f5d6c2;
  padding: 9px 12px;
  border-radius: 10px;
}

.menu.router-link-active {
  background: rgba(255, 214, 183, 0.16);
  color: #fff4ea;
}

.content {
  padding: 20px;
  background: linear-gradient(180deg, #fff9f4 0%, #fff4eb 100%);
}

@media (max-width: 900px) {
  .admin-wrap {
    grid-template-columns: 1fr;
  }

  .side {
    position: sticky;
    top: 0;
    z-index: 5;
    flex-direction: row;
    overflow-x: auto;
  }
}
</style>
