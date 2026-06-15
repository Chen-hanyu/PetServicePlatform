import axios from "axios";
import type { ApiResponse } from "@/types/api";
import { useAuthStore } from "@/store/auth";
import { apiLogger } from "@/utils/apiLogger";

const webHttp = axios.create({
  baseURL: "/api/v1",
  timeout: 12000
});

const adminHttp = axios.create({
  baseURL: "/api/v1/admin",
  timeout: 12000
});

const attachAuth = (client: typeof webHttp) => {
  client.interceptors.request.use((config) => {
    const store = useAuthStore();
    const path = `${config.baseURL || ""}${config.url || ""}`;
    (config as any).metadata = { startedAt: Date.now() };
    const isAuthEntry = /\/api\/v1(\/admin)?\/auth\/(login|register|verify-code)$/.test(path);
    if (store.token && !isAuthEntry) {
      config.headers.Authorization = `Bearer ${store.token}`;
    }
    return config;
  });

  client.interceptors.response.use(
    (response) => {
      const meta = (response.config as any).metadata;
      if (apiLogger.isEnabled()) {
        apiLogger.addLog({
          method: String(response.config.method || "GET").toUpperCase() as any,
          url: response.config.url || "",
          path: `${response.config.baseURL || ""}${response.config.url || ""}`,
          requestData: response.config.data,
          requestParams: response.config.params,
          response: response.data,
          status: response.status,
          duration: Date.now() - (meta?.startedAt || Date.now()),
          isExpand: false
        });
      }
      return response;
    },
    (error) => {
      const config = error?.config || {};
      const meta = (config as any).metadata;
      if (apiLogger.isEnabled()) {
        apiLogger.addLog({
          method: String(config.method || "GET").toUpperCase() as any,
          url: config.url || "",
          path: `${config.baseURL || ""}${config.url || ""}`,
          requestData: config.data,
          requestParams: config.params,
          status: error?.response?.status,
          duration: Date.now() - (meta?.startedAt || Date.now()),
          error: (error?.response?.data as { message?: string })?.message || error.message,
          isExpand: false
        });
      }
      if (error?.response?.status === 401) {
        const store = useAuthStore();
        store.logout();
      }
      return Promise.reject(error);
    }
  );
};

attachAuth(webHttp);
attachAuth(adminHttp);

export const unwrap = <T>(payload: ApiResponse<T>): T => {
  if (payload.code !== 0) {
    throw new Error(payload.message || "请求失败");
  }
  return payload.data;
};

export const toErrorMessage = (error: unknown): string => {
  if (axios.isAxiosError(error)) {
    return (error.response?.data as { message?: string })?.message || error.message || "网络请求失败";
  }
  if (error instanceof Error) {
    return error.message;
  }
  return "请求失败";
};

export { webHttp, adminHttp };
