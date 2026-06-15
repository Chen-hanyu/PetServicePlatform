export type EntityId = string | number;

export interface AdoptionPetSummary {
  id: EntityId;
  name: string;
  type: string;
  breed: string;
  gender: string;
  age_desc: string;
  city: string;
  health_status: string;
  status: string;
  cover_url?: string;
}

export interface AdoptionPetDetail extends AdoptionPetSummary {
  personality?: string;
  adoption_requirements?: string;
  story?: string;
}

export interface AdoptionProcess {
  title?: string;
  steps: string[];
}

export interface AdoptionApplicationPayload {
  pet_id: EntityId;
  contact_phone: string;
  experience_desc: string;
  living_condition_desc: string;
}
