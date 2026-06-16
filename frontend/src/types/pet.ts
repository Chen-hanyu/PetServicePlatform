export type EntityId = string | number;

export interface PetProfile {
  id: EntityId;
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

export interface PetTimelineEvent {
  type: string;
  title: string;
  description: string;
  occurred_at: string;
  image_url?: string;
}

export interface PetTimeline {
  pet: PetProfile;
  events: PetTimelineEvent[];
}
