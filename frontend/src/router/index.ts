import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "@/store/auth";

const router = createRouter({
  history: createWebHistory(),
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    }
    return { top: 0 };
  },
  routes: [
    { path: "/", component: () => import("@/layout/WebLayout.vue"), children: [
      { path: "", redirect: "/home" },
      { path: "home", component: () => import("@/pages/web/home/HomePage.vue") },
      { path: "health", component: () => import("@/pages/web/monitoring/HealthCheckPage.vue") },
      { path: "community", component: () => import("@/pages/web/community/CommunityPage.vue") },
      { path: "community/create", component: () => import("@/pages/web/community/PostCreatePage.vue"), meta: { requiresAuth: true } },
      { path: "community/post/:id", component: () => import("@/pages/web/community/PostDetailPage.vue") },
      { path: "adoption", component: () => import("@/pages/web/adoption/AdoptionPage.vue") },
      { path: "services/checkout", component: () => import("@/pages/web/services/ServiceCheckoutPage.vue") },
      { path: "services/book/:merchantId", component: () => import("@/pages/web/services/ServiceBookingPage.vue") },
      { path: "services/merchant/:id", component: () => import("@/pages/web/services/MerchantDetailPage.vue") },
      { path: "services", component: () => import("@/pages/web/services/ServicesPage.vue") },
      { path: "shop/checkout", component: () => import("@/pages/web/shop/CheckoutPage.vue") },
      { path: "shop/product/:id", component: () => import("@/pages/web/shop/ProductDetailPage.vue") },
      { path: "shop", component: () => import("@/pages/web/shop/ShopPage.vue") },
      { path: "profile", component: () => import("@/pages/web/profile/ProfilePage.vue"), meta: { requiresAuth: true } },
      { path: "profile/pets", component: () => import("@/pages/web/profile/PetsPage.vue"), meta: { requiresAuth: true } },
      { path: "profile/posts", component: () => import("@/pages/web/profile/MyPostsPage.vue"), meta: { requiresAuth: true } },
      { path: "profile/favorites", component: () => import("@/pages/web/profile/MyFavoritesPage.vue"), meta: { requiresAuth: true } },
      { path: "profile/orders", component: () => import("@/pages/web/profile/MyOrdersPage.vue"), meta: { requiresAuth: true } },
      { path: "profile/applications", component: () => import("@/pages/web/profile/MyApplicationsPage.vue"), meta: { requiresAuth: true } },
      { path: "profile/messages", component: () => import("@/pages/web/profile/MyMessagesPage.vue"), meta: { requiresAuth: true } },
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
      { path: "support", component: () => import("@/pages/admin/support/SupportAdminPage.vue") },
      { path: "adoption", component: () => import("@/pages/admin/adoption/AdoptionAdminPage.vue") },
      { path: "services", component: () => import("@/pages/admin/services/ServicesAdminPage.vue") },
      { path: "shop", component: () => import("@/pages/admin/shop/ShopAdminPage.vue") },
      { path: "monitoring", component: () => import("@/pages/admin/monitoring/MonitorDashboard.vue") }
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
