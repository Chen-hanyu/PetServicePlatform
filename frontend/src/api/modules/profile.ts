import { webHttp, unwrap } from "@/api/http";
import type { ApiResponse } from "@/types/api";
import type { ProfileOverview, UserProfile } from "@/types/auth";

export const fetchOverview = async () => {
  const { data } = await webHttp.get<ApiResponse<ProfileOverview>>("/profile/overview");
  return unwrap(data);
};

export const fetchCurrentUser = async () => {
  const { data } = await webHttp.get<ApiResponse<UserProfile>>("/profile/me");
  return unwrap(data);
};
