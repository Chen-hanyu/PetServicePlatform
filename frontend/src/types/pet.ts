export interface PetProfile {
  id: number;
  name: string;
  type: string;
  breed?: string;
  gender?: string;
  birthday?: string;
  weight?: number;
  avatar_url?: string;
  description?: string;
}

export interface SavePetPayload {
  name: string;
  type: string;
  breed?: string;
  gender?: string;
  birthday?: string;
  weight?: number;
  avatar_url?: string;
  description?: string;
}
