import { adminHttp, unwrap } from "@/api/http";
import type { ApiResponse, PageResult } from "@/types/api";
import type { AdminSupportMessage, DashboardOverview } from "@/types/admin";
import type { PostSummary } from "@/types/community";
import type { UserProfile } from "@/types/auth";

/**
 * 管理端 API 封装
 * 说明：由于当前前端页面大多以字段直出为主，且后端实现会在后续补齐，
 * 这里对未在 frontend types 中建模的实体统一用 `any`，以保证编译可通过。
 */

export const fetchAdminDashboard = async () => {
  const { data } = await adminHttp.get<ApiResponse<DashboardOverview>>("/dashboard");
  return unwrap(data);
};

export const fetchAdminUsers = async (params: Record<string, string | number | undefined>) => {
  const { data } = await adminHttp.get<ApiResponse<PageResult<UserProfile>>>("/users", { params });
  return unwrap(data);
};

export const fetchAdminUserDetail = async (userId: number) => {
  const { data } = await adminHttp.get<ApiResponse<UserProfile>>(`/users/${userId}`);
  return unwrap(data);
};

export const updateAdminUserStatus = async (userId: number, status: string, remark = "") => {
  const { data } = await adminHttp.put<ApiResponse<Record<string, unknown>>>(`/users/${userId}/status`, {
    status,
    remark
  });
  return unwrap(data);
};

export const fetchAdminPosts = async (params: Record<string, string | number | undefined>) => {
  const { data } = await adminHttp.get<ApiResponse<PageResult<PostSummary>>>("/posts", { params });
  return unwrap(data);
};

export const reviewAdminPost = async (postId: number, status: string, remark = "") => {
  const { data } = await adminHttp.put<ApiResponse<Record<string, unknown>>>(`/posts/${postId}/review`, {
    status,
    remark
  });
  return unwrap(data);
};

// -----------------------------
// Content（帖子/评论/Banner/标签/推荐位）
// -----------------------------

export const fetchAdminComments = async (params: Record<string, string | number | undefined>) => {
  const { data } = await adminHttp.get<ApiResponse<PageResult<any>>>("/comments", { params });
  return unwrap(data);
};

export const deleteAdminComment = async (commentId: number) => {
  const { data } = await adminHttp.delete<ApiResponse<null>>(`/comments/${commentId}`);
  return unwrap(data);
};

export const fetchAdminSupportMessages = async (params: Record<string, string | number | undefined>) => {
  const { data } = await adminHttp.get<ApiResponse<PageResult<AdminSupportMessage>>>("/support/messages", { params });
  return unwrap(data);
};

export const handleAdminSupportMessage = async (messageId: string | number, payload?: { reply_content?: string }) => {
  const { data } = await adminHttp.put<ApiResponse<AdminSupportMessage>>(`/support/messages/${messageId}/handle`, payload || {});
  return unwrap(data);
};

export const fetchAdminBanners = async () => {
  const { data } = await adminHttp.get<ApiResponse<any[]>>("/banners");
  return unwrap(data);
};

export const createAdminBanner = async (payload: Record<string, unknown>) => {
  const { data } = await adminHttp.post<ApiResponse<any>>("/banners", payload);
  return unwrap(data);
};

export const updateAdminBanner = async (bannerId: number, payload: Record<string, unknown>) => {
  const { data } = await adminHttp.put<ApiResponse<any>>(`/banners/${bannerId}`, payload);
  return unwrap(data);
};

export const deleteAdminBanner = async (bannerId: number) => {
  const { data } = await adminHttp.delete<ApiResponse<null>>(`/banners/${bannerId}`);
  return unwrap(data);
};

export const fetchAdminTags = async (params: Record<string, string | number | undefined>) => {
  const { data } = await adminHttp.get<ApiResponse<PageResult<any>>>("/tags", { params });
  return unwrap(data);
};

export const createAdminTag = async (payload: Record<string, unknown>) => {
  const { data } = await adminHttp.post<ApiResponse<any>>("/tags", payload);
  return unwrap(data);
};

export const updateAdminTag = async (tagId: number, payload: Record<string, unknown>) => {
  const { data } = await adminHttp.put<ApiResponse<any>>(`/tags/${tagId}`, payload);
  return unwrap(data);
};

// docs 中未明确声明 delete，这里按常规 REST pattern 预留
export const deleteAdminTag = async (tagId: number) => {
  const { data } = await adminHttp.delete<ApiResponse<null>>(`/tags/${tagId}`);
  return unwrap(data);
};

export const fetchAdminRecommendations = async (params: Record<string, string | number | undefined>) => {
  const { data } = await adminHttp.get<ApiResponse<PageResult<any>>>("/recommendations", { params });
  return unwrap(data);
};

export const createAdminRecommendation = async (payload: Record<string, unknown>) => {
  const { data } = await adminHttp.post<ApiResponse<any>>("/recommendations", payload);
  return unwrap(data);
};

export const updateAdminRecommendation = async (recommendationId: number, payload: Record<string, unknown>) => {
  const { data } = await adminHttp.put<ApiResponse<any>>(`/recommendations/${recommendationId}`, payload);
  return unwrap(data);
};

// docs 中未明确声明 delete，这里按常规 REST pattern 预留
export const deleteAdminRecommendation = async (recommendationId: number) => {
  const { data } = await adminHttp.delete<ApiResponse<null>>(`/recommendations/${recommendationId}`);
  return unwrap(data);
};

// -----------------------------
type EntityId = string | number;

// Adoption（领养宠物/申请审核）
// -----------------------------

export const fetchAdminAdoptionPets = async (params: Record<string, string | number | undefined>) => {
  const { data } = await adminHttp.get<ApiResponse<PageResult<any>>>("/adoption/pets", { params });
  return unwrap(data);
};

export const createAdminAdoptionPet = async (payload: Record<string, unknown>) => {
  const { data } = await adminHttp.post<ApiResponse<any>>("/adoption/pets", payload);
  return unwrap(data);
};

export const updateAdminAdoptionPet = async (petId: EntityId, payload: Record<string, unknown>) => {
  const { data } = await adminHttp.put<ApiResponse<any>>(`/adoption/pets/${petId}`, payload);
  return unwrap(data);
};

// docs 中未明确声明 delete，这里按常规 REST pattern 预留
export const deleteAdminAdoptionPet = async (petId: EntityId) => {
  const { data } = await adminHttp.delete<ApiResponse<null>>(`/adoption/pets/${petId}`);
  return unwrap(data);
};

export const fetchAdminAdoptionApplications = async (params: Record<string, string | number | undefined>) => {
  const { data } = await adminHttp.get<ApiResponse<PageResult<any>>>("/adoption/applications", { params });
  return unwrap(data);
};

export const reviewAdminAdoptionApplication = async (applicationId: EntityId, status: string, remark = "") => {
  const { data } = await adminHttp.put<ApiResponse<any>>(`/adoption/applications/${applicationId}/review`, {
    status,
    review_remark: remark
  });
  return unwrap(data);
};

// -----------------------------
// Services（服务分类/商家/项目/预约）
// -----------------------------

export const fetchAdminServiceCategories = async () => {
  const { data } = await adminHttp.get<ApiResponse<any[]>>("/services/categories");
  return unwrap(data);
};

export const createAdminServiceCategory = async (payload: Record<string, unknown>) => {
  const { data } = await adminHttp.post<ApiResponse<any>>("/services/categories", payload);
  return unwrap(data);
};

export const updateAdminServiceCategory = async (categoryId: number, payload: Record<string, unknown>) => {
  const { data } = await adminHttp.put<ApiResponse<any>>(`/services/categories/${categoryId}`, payload);
  return unwrap(data);
};

// docs 中未明确声明 delete，这里按常规 REST pattern 预留
export const deleteAdminServiceCategory = async (categoryId: number) => {
  const { data } = await adminHttp.delete<ApiResponse<null>>(`/services/categories/${categoryId}`);
  return unwrap(data);
};

export const fetchAdminMerchants = async (params: Record<string, string | number | undefined>) => {
  const { data } = await adminHttp.get<ApiResponse<PageResult<any>>>("/services/merchants", { params });
  return unwrap(data);
};

export const createAdminMerchant = async (payload: Record<string, unknown>) => {
  const { data } = await adminHttp.post<ApiResponse<any>>("/services/merchants", payload);
  return unwrap(data);
};

export const updateAdminMerchant = async (merchantId: number, payload: Record<string, unknown>) => {
  const { data } = await adminHttp.put<ApiResponse<any>>(`/services/merchants/${merchantId}`, payload);
  return unwrap(data);
};

// docs 中未明确声明 delete，这里按常规 REST pattern 预留
export const deleteAdminMerchant = async (merchantId: number) => {
  const { data } = await adminHttp.delete<ApiResponse<null>>(`/services/merchants/${merchantId}`);
  return unwrap(data);
};

export const fetchAdminServiceItems = async (params: Record<string, string | number | undefined>) => {
  const { data } = await adminHttp.get<ApiResponse<PageResult<any>>>("/services/items", { params });
  return unwrap(data);
};

export const createAdminServiceItem = async (payload: Record<string, unknown>) => {
  const { data } = await adminHttp.post<ApiResponse<any>>("/services/items", payload);
  return unwrap(data);
};

export const updateAdminServiceItem = async (serviceId: number, payload: Record<string, unknown>) => {
  const { data } = await adminHttp.put<ApiResponse<any>>(`/services/items/${serviceId}`, payload);
  return unwrap(data);
};

// docs 中未明确声明 delete，这里按常规 REST pattern 预留
export const deleteAdminServiceItem = async (serviceId: number) => {
  const { data } = await adminHttp.delete<ApiResponse<null>>(`/services/items/${serviceId}`);
  return unwrap(data);
};

export const fetchAdminBookings = async (params: Record<string, string | number | undefined>) => {
  const { data } = await adminHttp.get<ApiResponse<PageResult<any>>>("/services/bookings", { params });
  return unwrap(data);
};

export const updateAdminBooking = async (bookingId: number, payload: Record<string, unknown>) => {
  const { data } = await adminHttp.put<ApiResponse<any>>(`/services/bookings/${bookingId}`, payload);
  return unwrap(data);
};

// -----------------------------
// Shop（商品分类/商品/订单）
// -----------------------------

export const fetchAdminShopCategories = async () => {
  const { data } = await adminHttp.get<ApiResponse<any[]>>("/shop/categories");
  return unwrap(data);
};

// docs 中未明确声明 create/update/delete，这里按常规 REST pattern 预留
export const createAdminShopCategory = async (payload: Record<string, unknown>) => {
  const { data } = await adminHttp.post<ApiResponse<any>>("/shop/categories", payload);
  return unwrap(data);
};

export const updateAdminShopCategory = async (categoryId: number, payload: Record<string, unknown>) => {
  const { data } = await adminHttp.put<ApiResponse<any>>(`/shop/categories/${categoryId}`, payload);
  return unwrap(data);
};

export const deleteAdminShopCategory = async (categoryId: number) => {
  const { data } = await adminHttp.delete<ApiResponse<null>>(`/shop/categories/${categoryId}`);
  return unwrap(data);
};

export const fetchAdminProducts = async (params: Record<string, string | number | undefined>) => {
  const { data } = await adminHttp.get<ApiResponse<PageResult<any>>>("/shop/products", { params });
  return unwrap(data);
};

export const createAdminProduct = async (payload: Record<string, unknown>) => {
  const { data } = await adminHttp.post<ApiResponse<any>>("/shop/products", payload);
  return unwrap(data);
};

export const updateAdminProduct = async (productId: number, payload: Record<string, unknown>) => {
  const { data } = await adminHttp.put<ApiResponse<any>>(`/shop/products/${productId}`, payload);
  return unwrap(data);
};

export const updateAdminProductStatus = async (productId: number, status: string) => {
  const { data } = await adminHttp.put<ApiResponse<any>>(`/shop/products/${productId}/status`, { status });
  return unwrap(data);
};

// docs 中未明确声明 delete，这里按常规 REST pattern 预留
export const deleteAdminProduct = async (productId: number) => {
  const { data } = await adminHttp.delete<ApiResponse<null>>(`/shop/products/${productId}`);
  return unwrap(data);
};

export const fetchAdminOrders = async (params: Record<string, string | number | undefined>) => {
  const { data } = await adminHttp.get<ApiResponse<PageResult<any>>>("/shop/orders", { params });
  return unwrap(data);
};

export const updateAdminOrder = async (orderId: number, payload: Record<string, unknown>) => {
  const { data } = await adminHttp.put<ApiResponse<any>>(`/shop/orders/${orderId}`, payload);
  return unwrap(data);
};

// -----------------------------
// Monitoring
// -----------------------------

export const fetchAdminMonitoringMetrics = async () => {
  const { data } = await adminHttp.get<ApiResponse<Record<string, any>>>("/monitoring/metrics");
  return unwrap(data);
};

export const fetchAdminMonitoringPathStats = async () => {
  const { data } = await adminHttp.get<ApiResponse<Record<string, any>>>("/monitoring/metrics/paths");
  return unwrap(data);
};

export const resetAdminMonitoringMetrics = async () => {
  const { data } = await adminHttp.post<ApiResponse<null>>("/monitoring/metrics/reset");
  return unwrap(data);
};
