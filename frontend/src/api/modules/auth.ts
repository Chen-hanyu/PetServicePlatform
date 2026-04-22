import { webHttp, unwrap } from "@/api/http";
import type { ApiResponse } from "@/types/api";
import type { LoginResult } from "@/types/auth";

export interface RegisterPayload {
  phone: string;
  password: string;
  nickname?: string;
}

export const loginUser = async (username: string, password: string): Promise<LoginResult> => {
  const { data } = await webHttp.post<ApiResponse<LoginResult>>("/auth/login", {
    phone: username,
    password
  });
  return unwrap(data);
};

export const registerUser = async (payload: RegisterPayload): Promise<void> => {
  const { data } = await webHttp.post<ApiResponse<void>>("/auth/register", payload);
  return unwrap(data);
};
