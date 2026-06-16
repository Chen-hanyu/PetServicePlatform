import { beforeEach, describe, expect, it } from "vitest";
import type { AxiosRequestConfig } from "axios";
import { createPinia, setActivePinia } from "pinia";
import { adminHttp, toErrorMessage, unwrap, webHttp } from "@/api/http";
import {
  createPet,
  deletePetById,
  fetchMyPets,
  fetchPetTimeline,
  updatePet
} from "@/api/modules/pet";
import { useAuthStore } from "@/store/auth";
import { apiLogger, getState } from "@/utils/apiLogger";
import type { PetProfile } from "@/types/pet";

const pet: PetProfile = {
  id: 1,
  name: "Mochi",
  type: "CAT"
};

const ok = <T>(data: T) => ({
  code: 0,
  message: "ok",
  data
});

describe("http helpers and pet api", () => {
  const requests: AxiosRequestConfig[] = [];

  beforeEach(() => {
    requests.length = 0;
    localStorage.clear();
    setActivePinia(createPinia());
    const loggerState = getState();
    loggerState.logs = [];
    loggerState.isEnabled = true;
    loggerState.level = "ERROR";

    const adapter = async (config: AxiosRequestConfig) => {
      requests.push(config);
      const url = String(config.url || "");
      if (url.includes("timeline")) {
        return {
          data: ok({ pet, events: [{ type: "weight", title: "Weight", description: "4kg", occurred_at: "2026-06-01" }] }),
          status: 200,
          statusText: "OK",
          headers: {},
          config
        } as any;
      }
      if (config.method === "delete") {
        return { data: ok(undefined), status: 200, statusText: "OK", headers: {}, config } as any;
      }
      if (config.method === "get" && url === "/pets") {
        return { data: ok([pet]), status: 200, statusText: "OK", headers: {}, config } as any;
      }
      return { data: ok(pet), status: 200, statusText: "OK", headers: {}, config } as any;
    };

    webHttp.defaults.adapter = adapter as any;
    adminHttp.defaults.adapter = adapter as any;
  });

  it("unwraps successful payloads and throws api messages", () => {
    expect(unwrap(ok("done"))).toBe("done");
    expect(() => unwrap({ code: 400, message: "bad request", data: null })).toThrow("bad request");
    expect(() => unwrap({ code: 500, message: "", data: null })).toThrow("请求失败");
  });

  it("formats axios, error, and unknown failures", () => {
    expect(toErrorMessage({ isAxiosError: true, response: { data: { message: "server failed" } }, message: "fallback" })).toBe("server failed");
    expect(toErrorMessage({ isAxiosError: true, message: "network failed" })).toBe("network failed");
    expect(toErrorMessage(new Error("plain error"))).toBe("plain error");
    expect(toErrorMessage("bad")).toBe("请求失败");
  });

  it("calls pet endpoints and attaches auth headers", async () => {
    useAuthStore().setSession("token-123", { id: 1, nickname: "Tester", role: "USER" });

    await expect(fetchMyPets()).resolves.toEqual([pet]);
    await expect(createPet({ name: "Mochi", type: "CAT" })).resolves.toEqual(pet);
    await expect(updatePet(1, { name: "Mochi", type: "CAT" })).resolves.toEqual(pet);
    await expect(deletePetById(1)).resolves.toBeUndefined();
    await expect(fetchPetTimeline(1)).resolves.toMatchObject({ pet });

    expect(requests.map((request) => `${request.method}:${request.url}`)).toEqual([
      "get:/pets",
      "post:/pets",
      "put:/pets/1",
      "delete:/pets/1",
      "get:/pets/1/timeline"
    ]);
    expect(requests.every((request) => (request.headers as any).Authorization === "Bearer token-123")).toBe(true);
    expect(apiLogger.getMetrics().success).toBe(5);
  });

  it("does not attach auth header to auth entry endpoints", async () => {
    useAuthStore().setSession("token-123", { id: 1, nickname: "Tester", role: "USER" });

    await webHttp.post("/auth/login", { phone: "13800000001", password: "123456" });

    expect((requests[0].headers as any).Authorization).toBeUndefined();
  });
});
