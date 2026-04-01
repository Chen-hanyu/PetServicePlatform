import { webHttp, unwrap } from "@/api/http";
import type { ApiResponse, PageResult } from "@/types/api";
import type { AdoptionApplicationPayload, AdoptionPetDetail, AdoptionPetSummary, AdoptionProcess } from "@/types/adoption";

export const fetchAdoptionPets = async (params: Record<string, string | number | undefined>) => {
  const { data } = await webHttp.get<ApiResponse<PageResult<AdoptionPetSummary>>>("/adoption/pets", { params });
  return unwrap(data);
};

export const fetchAdoptionPetDetail = async (petId: number) => {
  const { data } = await webHttp.get<ApiResponse<AdoptionPetDetail>>(`/adoption/pets/${petId}`);
  return unwrap(data);
};

export const fetchAdoptionProcess = async () => {
  const { data } = await webHttp.get<ApiResponse<AdoptionProcess>>("/adoption/process");
  return unwrap(data);
};

export const createAdoptionApplication = async (payload: AdoptionApplicationPayload) => {
  const { data } = await webHttp.post<ApiResponse<Record<string, unknown>>>("/adoption/applications", payload);
  return unwrap(data);
};
