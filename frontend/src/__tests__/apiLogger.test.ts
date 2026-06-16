import { beforeEach, describe, expect, it, vi } from "vitest";
import { apiLogger, generateId, getState } from "@/utils/apiLogger";

describe("apiLogger", () => {
  beforeEach(() => {
    const state = getState();
    state.logs = [];
    state.isEnabled = true;
    state.maxLogs = 50;
    state.level = "DEBUG";
    vi.restoreAllMocks();
  });

  it("generates ids and stores successful logs", () => {
    const infoSpy = vi.spyOn(console, "info").mockImplementation(() => undefined);
    const id = apiLogger.addLog({
      method: "GET",
      url: "/pets",
      path: "/api/v1/pets",
      status: 200,
      duration: 24,
      isExpand: false
    });

    expect(id).toMatch(/^api-/);
    expect(apiLogger.getLogs()).toHaveLength(1);
    expect(apiLogger.getLogById(id)?.path).toBe("/api/v1/pets");
    expect(infoSpy).toHaveBeenCalledTimes(1);
  });

  it("trims logs to max count and can update entries", () => {
    const state = getState();
    state.maxLogs = 2;
    const first = apiLogger.addLog({ method: "GET", url: "/1", path: "/1", status: 200, duration: 1, isExpand: false });
    apiLogger.addLog({ method: "POST", url: "/2", path: "/2", status: 201, duration: 2, isExpand: false });
    const third = apiLogger.addLog({ method: "DELETE", url: "/3", path: "/3", status: 204, duration: 3, isExpand: false });

    expect(apiLogger.getLogById(first)).toBeUndefined();
    apiLogger.updateLog(third, { isExpand: true, status: 202 });
    expect(apiLogger.getLogById(third)?.isExpand).toBe(true);
    expect(apiLogger.getLogById(third)?.status).toBe(202);
  });

  it("records warning and error metrics", () => {
    vi.spyOn(console, "warn").mockImplementation(() => undefined);
    vi.spyOn(console, "error").mockImplementation(() => undefined);
    apiLogger.addLog({ method: "GET", url: "/bad", path: "/bad", status: 404, duration: 30, isExpand: false });
    apiLogger.addLog({ method: "POST", url: "/fail", path: "/fail", status: 500, duration: 70, error: "boom", isExpand: false });

    expect(apiLogger.getMetrics()).toEqual({
      total: 2,
      success: 0,
      failed: 2,
      avgDuration: 50,
      maxDuration: 70,
      errorRate: 100
    });
  });

  it("clears logs, toggles enabled state, and changes level", () => {
    const infoSpy = vi.spyOn(console, "info").mockImplementation(() => undefined);
    apiLogger.addLog({ method: "GET", url: "/pets", path: "/pets", status: 200, duration: 1, isExpand: false });

    apiLogger.clearLogs();
    expect(apiLogger.getLogs()).toEqual([]);

    apiLogger.toggleEnabled();
    expect(apiLogger.isEnabled()).toBe(false);

    apiLogger.setLevel("ERROR");
    expect(getState().level).toBe("ERROR");
    expect(infoSpy).toHaveBeenCalled();
  });

  it("suppresses lower level structured logs", () => {
    const infoSpy = vi.spyOn(console, "info").mockImplementation(() => undefined);
    apiLogger.setLevel("ERROR");
    infoSpy.mockClear();

    apiLogger.addLog({ method: "GET", url: "/pets", path: "/pets", status: 200, duration: 1, isExpand: false });

    expect(infoSpy).not.toHaveBeenCalled();
  });

  it("exposes deterministic id shape", () => {
    expect(generateId()).toMatch(/^api-\d+-\d+$/);
  });
});
