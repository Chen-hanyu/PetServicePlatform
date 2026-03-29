import { webHttp, unwrap } from "@/services/http";
import type { ApiResponse } from "@/types/api";
import type { PetProfile, SavePetPayload } from "@/types/pet";

export const fetchMyPets = async () => {
  const { data } = await webHttp.get<ApiResponse<PetProfile[]>>("/pets");
  return unwrap(data);
};

export const createPet = async (payload: SavePetPayload) => {
  const { data } = await webHttp.post<ApiResponse<PetProfile>>("/pets", payload);
  return unwrap(data);
};
