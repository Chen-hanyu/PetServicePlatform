import { adminHttp, unwrap } from "@/api/http";
import type { ApiResponse } from "@/types/api";
import type { LoginResult } from "@/types/auth";

export const loginAdmin = async (username: string, password: string): Promise<LoginResult> => {
  const { data } = await adminHttp.post<ApiResponse<LoginResult>>("/auth/login", {
    phone: username,
    password
  });
  return unwrap(data);
};
