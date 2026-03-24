import { adminHttp, unwrap } from "@/services/http";
import type { ApiResponse, PageResult } from "@/types/api";
import type { DashboardOverview } from "@/types/admin";
import type { PostSummary } from "@/types/community";
import type { UserProfile } from "@/types/auth";

export const fetchAdminDashboard = async () => {
  const { data } = await adminHttp.get<ApiResponse<DashboardOverview>>("/dashboard");
  return unwrap(data);
};

export const fetchAdminUsers = async (params: Record<string, string | number | undefined>) => {
  const { data } = await adminHttp.get<ApiResponse<PageResult<UserProfile>>>("/users", { params });
  return unwrap(data);
};

export const updateAdminUserStatus = async (userId: number, status: string, remark = "") => {
  const { data } = await adminHttp.put<ApiResponse<Record<string, unknown>>>(`/users/${userId}/status`, { status, remark });
  return unwrap(data);
};

export const fetchAdminPosts = async (params: Record<string, string | number | undefined>) => {
  const { data } = await adminHttp.get<ApiResponse<PageResult<PostSummary>>>("/posts", { params });
  return unwrap(data);
};

export const reviewAdminPost = async (postId: number, status: string, remark = "") => {
  const { data } = await adminHttp.put<ApiResponse<Record<string, unknown>>>(`/posts/${postId}/review`, { status, remark });
  return unwrap(data);
};
