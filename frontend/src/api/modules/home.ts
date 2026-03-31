import { webHttp, unwrap } from "@/api/http";
import type { ApiResponse } from "@/types/api";
import type { HomeData } from "@/types/home";

export const fetchHomeData = async (): Promise<HomeData> => {
  const { data } = await webHttp.get<ApiResponse<HomeData>>("/home");
  return unwrap(data);
};
