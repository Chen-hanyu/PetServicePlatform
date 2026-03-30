import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "@/store/auth";

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/", component: () => import("@/layout/WebLayout.vue"), children: [
      { path: "", redirect: "/home" },
      { path: "home", component: () => import("@/pages/web/home/HomePage.vue") },
      { path: "community", component: () => import("@/pages/web/community/CommunityPage.vue") },
      { path: "adoption", component: () => import("@/pages/web/adoption/AdoptionPage.vue") },
      { path: "services", component: () => import("@/pages/web/services/ServicesPage.vue") },
      { path: "shop", component: () => import("@/pages/web/shop/ShopPage.vue") },
      { path: "profile", component: () => import("@/pages/web/profile/ProfilePage.vue"), meta: { requiresAuth: true } },
      { path: "profile/pets", component: () => import("@/pages/web/profile/PetsPage.vue"), meta: { requiresAuth: true } },
      { path: "profile/orders", component: () => import("@/pages/web/profile/OrdersPage.vue"), meta: { requiresAuth: true } },
      { path: "profile/bookings", component: () => import("@/pages/web/profile/BookingsPage.vue"), meta: { requiresAuth: true } },
      { path: "profile/settings", component: () => import("@/pages/web/profile/SettingsPage.vue"), meta: { requiresAuth: true } }
    ] },
    { path: "/login", component: () => import("@/pages/web/auth/LoginPage.vue") },
    { path: "/register", component: () => import("@/pages/web/auth/RegisterPage.vue") },
    { path: "/admin/login", component: () => import("@/pages/admin/auth/AdminLoginPage.vue") },
    { path: "/admin", component: () => import("@/layout/AdminLayout.vue"), meta: { requiresAuth: true, role: "ADMIN" }, children: [
      { path: "", redirect: "/admin/dashboard" },
      { path: "dashboard", component: () => import("@/pages/admin/dashboard/DashboardPage.vue") },
      { path: "users", component: () => import("@/pages/admin/users/UsersPage.vue") },
      { path: "content", component: () => import("@/pages/admin/content/ContentPage.vue") },
      { path: "adoption", component: () => import("@/pages/admin/adoption/AdoptionAdminPage.vue") },
      { path: "services", component: () => import("@/pages/admin/services/ServicesAdminPage.vue") },
      { path: "shop", component: () => import("@/pages/admin/shop/ShopAdminPage.vue") }
    ] }
  ]
});

router.beforeEach((to) => {
  const auth = useAuthStore();
  const requiresAuth = Boolean(to.meta.requiresAuth);
  const role = to.meta.role as string | undefined;
  if (!requiresAuth) return true;
  if (!auth.isLoggedIn) return role === "ADMIN" ? "/admin/login" : "/login";
  if (role && auth.role !== role) return "/home";
  return true;
});

export default router;