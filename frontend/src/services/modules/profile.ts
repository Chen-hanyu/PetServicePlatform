import { webHttp, unwrap } from "@/services/http";
import type { ApiResponse } from "@/types/api";
import type { ProfileOverview } from "@/types/auth";

export const fetchOverview = async () => {
  const { data } = await webHttp.get<ApiResponse<ProfileOverview>>("/profile/overview");
  return unwrap(data);
};
