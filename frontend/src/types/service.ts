export interface ServiceCategory {
  id: number;
  name: string;
  sort: number;
  status: string;
}

export interface MerchantSummary {
  id: number;
  name: string;
  district?: string;
  address?: string;
  score?: number;
  business_hours?: string;
  status: string;
}

export interface MerchantServiceItem {
  id: number;
  name: string;
  price: number;
}

export interface MerchantDetail extends MerchantSummary {
  phone?: string;
  services: MerchantServiceItem[];
  reviews: Array<Record<string, unknown>>;
}

export interface ServiceBookingSummary {
  id: number;
  merchant: MerchantSummary;
  service_name: string;
  booking_time: string;
  status: string;
}

export interface CreateBookingPayload {
  merchant_id: number;
  merchant_service_id: number;
  booking_time: string;
  contact_name: string;
  contact_phone: string;
  remark?: string;
}
