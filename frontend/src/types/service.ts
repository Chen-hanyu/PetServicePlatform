export type EntityId = string | number;

export interface ServiceCategory {
  id: EntityId;
  name: string;
  sort: number;
  status: string;
}

export interface MerchantSummary {
  id: EntityId;
  name: string;
  district?: string;
  address?: string;
  score?: number;
  business_hours?: string;
  status: string;
}

export interface MerchantServiceItem {
  id: EntityId;
  name: string;
  price: number;
}

export interface MerchantDetail extends MerchantSummary {
  phone?: string;
  services: MerchantServiceItem[];
  reviews: Array<Record<string, unknown>>;
}

export interface ServiceBookingSummary {
  id: EntityId;
  merchant: MerchantSummary;
  service_name: string;
  booking_time: string;
  status: string;
}

export interface CreateBookingPayload {
  merchant_id: EntityId;
  merchant_service_id: EntityId;
  booking_time: string;
  contact_name: string;
  contact_phone: string;
  remark?: string;
}
