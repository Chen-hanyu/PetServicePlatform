import { webHttp, unwrap } from "@/api/http";
import type { ApiResponse } from "@/types/api";
import type { LoginResult } from "@/types/auth";

export const loginUser = async (username: string, password: string): Promise<LoginResult> => {
  const { data } = await webHttp.post<ApiResponse<LoginResult>>("/auth/login", {
    phone: username,
    password
  });
  return unwrap(data);
};
