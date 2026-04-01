import { apiLogger, generateId } from "./apiLogger";
import type { AxiosRequestConfig, AxiosResponse } from "axios";
import axios from "axios";

type RequestLogIdMap = WeakMap<AxiosRequestConfig, string>;

const requestIdMap = new RequestLogIdMap();

const extractPath = (url: string, baseURL?: string): string => {
  if (!baseURL) return url;
  return url.replace(baseURL, "");
};

const normalizeMethod = (method?: string): "GET" | "POST" | "PUT" | "DELETE" | "PATCH" => {
  return (method?.toUpperCase() as "GET" | "POST" | "PUT" | "DELETE" | "PATCH") || "GET";
};

export const setupApiInterceptors = (httpClient: typeof import("axios").default.create) => {
  const client = typeof httpClient === "function" ? httpClient : httpClient;

  client.interceptors.request.use(
    (config) => {
      if (!apiLogger.isEnabled()) return config;

      const id = generateId();
      requestIdMap.set(config, id);
      const startTime = Date.now();
      
      (config as AxiosRequestConfig & { metadata?: { startTime: number } }).metadata = { startTime };

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

export const setupAllInterceptors = (httpClients: typeof import("axios").default) => {
  Object.values(httpClients).forEach((client) => {
    if (axios.isAxiosInstance(client)) {
      setupApiInterceptors(client as unknown as typeof axios.default.create);
    }
  });
};
