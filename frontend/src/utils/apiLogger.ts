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

interface ApiLoggerState {
  logs: ApiLogItem[];
  isEnabled: boolean;
  maxLogs: number;
}

const state: ApiLoggerState = {
  logs: [],
  isEnabled: import.meta.env.DEV,
  maxLogs: 50
};

let idCounter = 0;

export const generateId = (): string => {
  return `api-${Date.now()}-${++idCounter}`;
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
  },

  toggleEnabled() {
    state.isEnabled = !state.isEnabled;
  },

  getLogs(): ApiLogItem[] {
    return state.logs;
  },

  isEnabled(): boolean {
    return state.isEnabled;
  },

  getLogById(id: string): ApiLogItem | undefined {
    return state.logs.find((log) => log.id === id);
  }
};

export const getState = () => state;
