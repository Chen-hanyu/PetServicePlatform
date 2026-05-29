import { apiLogger, generateId } from "./apiLogger";
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from "axios";

const requestIdMap = new WeakMap<AxiosRequestConfig, string>();

const extractPath = (url: string, baseURL?: string): string => {
  if (!baseURL) return url;
  return url.replace(baseURL, "");
};

const normalizeMethod = (method?: string): "GET" | "POST" | "PUT" | "DELETE" | "PATCH" => {
  return (method?.toUpperCase() as "GET" | "POST" | "PUT" | "DELETE" | "PATCH") || "GET";
};

export const setupApiInterceptors = (client: AxiosInstance) => {
  client.interceptors.request.use(
    (config) => {
      if (!apiLogger.isEnabled()) return config;

      const id = generateId();
      requestIdMap.set(config, id);
      (config as AxiosRequestConfig & { metadata?: { startTime: number } }).metadata = { startTime: Date.now() };

      apiLogger.addLog({
        method: normalizeMethod(config.method),
        url: config.url || "",
        path: extractPath(config.url || "", config.baseURL),
        requestData: config.data,
        requestParams: config.params,
        duration: 0,
        isExpand: false
      });

      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );

  client.interceptors.response.use(
    (response: AxiosResponse) => {
      if (!apiLogger.isEnabled()) return response;

      const config = response.config;
      const id = requestIdMap.get(config);
      const startTime = (config as AxiosRequestConfig & { metadata?: { startTime: number } }).metadata?.startTime;
      const duration = startTime ? Date.now() - startTime : 0;

      if (id) {
        apiLogger.updateLog(id, {
          response: response.data,
          status: response.status,
          duration,
          error: undefined
        });
      }

      return response;
    },
    (error) => {
      if (!apiLogger.isEnabled()) return Promise.reject(error);

      const config = error.config;
      const id = requestIdMap.get(config);
      const startTime = (config as AxiosRequestConfig & { metadata?: { startTime: number } }).metadata?.startTime;
      const duration = startTime ? Date.now() - startTime : 0;

      if (id) {
        apiLogger.updateLog(id, {
          response: error.response?.data,
          status: error.response?.status,
          duration,
          error: error.message
        });
      }

      return Promise.reject(error);
    }
  );
};

export const setupAllInterceptors = (clients: Record<string, AxiosInstance>) => {
  Object.values(clients).forEach((client) => {
    if (client && typeof client.interceptors !== "undefined") {
      setupApiInterceptors(client);
    }
  });
};
