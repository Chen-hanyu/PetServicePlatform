<template>
  <div>
    <header class="web-header">
      <div class="brand-wrap">
        <div class="brand-dot"></div>
        <div class="brand">宠物之家</div>
      </div>
      <nav>
        <RouterLink v-for="item in links" :key="item.path" :to="item.path" class="link">{{ item.name }}</RouterLink>
      </nav>
      <RouterLink class="btn btn-secondary" to="/login">登录</RouterLink>
    </header>
    <main class="page-shell">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
const links = [
  { name: "首页", path: "/home" },
  { name: "社区", path: "/community" },
  { name: "领养", path: "/adoption" },
  { name: "服务", path: "/services" },
  { name: "商城", path: "/shop" },
  { name: "我的", path: "/profile" }
];
</script>

<style scoped lang="scss">
.web-header {
  position: sticky;
  top: 0;
  z-index: 20;
  display: grid;
  grid-template-columns: auto 1fr auto;
  align-items: center;
  gap: 16px;
  padding: 14px 20px;
  background: rgba(255, 248, 241, 0.9);
  border-bottom: 1px solid #f2dbc6;
  backdrop-filter: blur(10px);
}

.brand-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
}

.brand-dot {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ffb089, #f17c53);
}

.brand {
  font-weight: 900;
  color: #8d4d30;
  letter-spacing: 0.3px;
}

nav {
  display: flex;
  justify-content: center;
  gap: 10px;
  overflow-x: auto;
}

.link {
  padding: 8px 12px;
  border-radius: 999px;
  white-space: nowrap;
  color: #7b5c4d;
  border: 1px solid transparent;
}

.link.router-link-active {
  background: #ffe9d7;
  color: #8d4d30;
  border-color: #ffd4b7;
}

@media (max-width: 768px) {
  .web-header {
    grid-template-columns: 1fr;
  }

  nav {
    justify-content: flex-start;
  }
}
</style>
