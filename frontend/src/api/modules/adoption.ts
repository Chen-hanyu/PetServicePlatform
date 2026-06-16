import { webHttp, unwrap } from "@/api/http";
import type { ApiResponse, PageResult } from "@/types/api";
import type { AdoptionApplicationPayload, AdoptionPetDetail, AdoptionPetSummary, AdoptionProcess } from "@/types/adoption";

type EntityId = string | number;

export const fetchAdoptionPets = async (params: Record<string, string | number | undefined>) => {
  const { data } = await webHttp.get<ApiResponse<PageResult<AdoptionPetSummary>>>("/adoption/pets", { params });
  return unwrap(data);
};

export const fetchAdoptionPetDetail = async (petId: EntityId) => {
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

/** 获取我的领养申请列表 */
export const fetchMyApplications = async (params: Record<string, string | number | undefined>) => {
  const { data } = await webHttp.get<ApiResponse<PageResult<any>>>("/adoption/applications", { params });
  return unwrap(data);
};

/** 撤销领养申请 */
export const cancelApplication = async (applicationId: EntityId) => {
  const { data } = await webHttp.post<ApiResponse<void>>(`/adoption/applications/${applicationId}/cancel`);
  return unwrap(data);
};


