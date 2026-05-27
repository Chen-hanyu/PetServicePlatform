export interface ApiLogItem {
  id: string;
  method: "GET" | "POST" | "PUT" | "DELETE" | "PATCH";
  url: string;
  path: string;
  requestData?: Record<string, unknown>;
  requestParams?: Record<string, unknown>;
  response?: unknown;
  status?: number;
  duration: number;
  timestamp: Date;
  error?: string;
  isExpand: boolean;
}

export type LogLevel = "DEBUG" | "INFO" | "WARN" | "ERROR";

interface ApiLoggerState {
  logs: ApiLogItem[];
  isEnabled: boolean;
  maxLogs: number;
  level: LogLevel;
}

const state: ApiLoggerState = {
  logs: [],
  isEnabled: import.meta.env.DEV,
  maxLogs: 50,
  level: import.meta.env.DEV ? "DEBUG" : "INFO"
};

let idCounter = 0;

export const generateId = (): string => {
  return `api-${Date.now()}-${++idCounter}`;
};

/**
 * 输出结构化 JSON 日志到控制台
 */
const structuredLog = (level: LogLevel, message: string, meta?: Record<string, unknown>) => {
  const logEntry = {
    time: new Date().toISOString(),
    level,
    message,
    module: "apiLogger",
    ...meta
  };

  const logString = JSON.stringify(logEntry);

  switch (level) {
    case "DEBUG":
      console.debug(logString);
      break;
    case "INFO":
      console.info(logString);
      break;
    case "WARN":
      console.warn(logString);
      break;
    case "ERROR":
      console.error(logString);
      break;
  }
};

/**
 * 检查当前级别是否允许输出
 */
const isLevelEnabled = (level: LogLevel): boolean => {
  const levels: LogLevel[] = ["DEBUG", "INFO", "WARN", "ERROR"];
  return levels.indexOf(level) >= levels.indexOf(state.level);
};

export const apiLogger = {
  addLog(log: Omit<ApiLogItem, "id" | "timestamp">): string {
    const id = generateId();
    const newLog: ApiLogItem = {
      ...log,
      id,
      timestamp: new Date()
    };

    state.logs.unshift(newLog);

    if (state.logs.length > state.maxLogs) {
      state.logs.pop();
    }

    // 输出结构化日志
    const level: LogLevel = log.error ? "ERROR" : log.status && log.status >= 400 ? "WARN" : "INFO";
    if (isLevelEnabled(level)) {
      structuredLog(level, log.error ? `API 请求失败: ${log.error}` : `API 请求成功`, {
        method: log.method,
        url: log.url,
        path: log.path,
        status: log.status,
        duration: log.duration,
        error: log.error
      });
    }

    return id;
  },

  updateLog(id: string, updates: Partial<ApiLogItem>) {
    const index = state.logs.findIndex((log) => log.id === id);
    if (index !== -1) {
      state.logs[index] = { ...state.logs[index], ...updates };
    }
  },

  clearLogs() {
    state.logs = [];
    structuredLog("INFO", "日志已清空");
  },

  toggleEnabled() {
    state.isEnabled = !state.isEnabled;
    structuredLog("INFO", `API 日志记录已${state.isEnabled ? "启用" : "禁用"}`);
  },

  setLevel(level: LogLevel) {
    state.level = level;
    structuredLog("INFO", `日志级别已设置为: ${level}`);
  },

  getLogs(): ApiLogItem[] {
    return state.logs;
  },

  isEnabled(): boolean {
    return state.isEnabled;
  },

  getLogById(id: string): ApiLogItem | undefined {
    return state.logs.find((log) => log.id === id);
  },

  /**
   * 获取指标统计
   */
  getMetrics() {
    const logs = state.logs;
    const total = logs.length;
    const success = logs.filter((l) => l.status && l.status < 400).length;
    const failed = logs.filter((l) => l.error || (l.status && l.status >= 400)).length;
    const durations = logs.filter((l) => l.duration > 0).map((l) => l.duration);
    const avgDuration = durations.length > 0
      ? Math.round(durations.reduce((a, b) => a + b, 0) / durations.length)
      : 0;
    const maxDuration = durations.length > 0 ? Math.max(...durations) : 0;
    const errorRate = total > 0 ? Math.round((failed / total) * 100) : 0;

    return {
      total,
      success,
      failed,
      avgDuration,
      maxDuration,
      errorRate
    };
  }
};

export const getState = () => state;
