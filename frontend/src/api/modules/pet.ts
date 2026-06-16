import { webHttp, unwrap } from "@/api/http";
import type { ApiResponse } from "@/types/api";
import type { EntityId, PetProfile, PetTimeline, SavePetPayload } from "@/types/pet";

export const fetchMyPets = async () => {
  const { data } = await webHttp.get<ApiResponse<PetProfile[]>>("/pets");
  return unwrap(data);
};

export const createPet = async (payload: SavePetPayload) => {
  const { data } = await webHttp.post<ApiResponse<PetProfile>>("/pets", payload);
  return unwrap(data);
};

export const updatePet = async (petId: EntityId, payload: SavePetPayload) => {
  const { data } = await webHttp.put<ApiResponse<PetProfile>>(`/pets/${petId}`, payload);
  return unwrap(data);
};

export const deletePetById = async (petId: EntityId) => {
  const { data } = await webHttp.delete<ApiResponse<void>>(`/pets/${petId}`);
  return unwrap(data);
};

export const fetchPetTimeline = async (petId: EntityId) => {
  const { data } = await webHttp.get<ApiResponse<PetTimeline>>(`/pets/${petId}/timeline`);
  return unwrap(data);
};
