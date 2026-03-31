import { webHttp, unwrap } from "@/services/http";
import type { ApiResponse, PageResult } from "@/types/api";
import type { CreateBookingPayload, MerchantDetail, MerchantSummary, ServiceCategory } from "@/types/service";

export const fetchServiceCategories = async () => {
  const { data } = await webHttp.get<ApiResponse<ServiceCategory[]>>("/services/categories");
  return unwrap(data);
};

export const fetchMerchants = async (params: Record<string, string | number | undefined>) => {
  const { data } = await webHttp.get<ApiResponse<PageResult<MerchantSummary>>>("/services/merchants", { params });
  return unwrap(data);
};

export const fetchMerchantDetail = async (merchantId: number) => {
  const { data } = await webHttp.get<ApiResponse<MerchantDetail>>(`/services/merchants/${merchantId}`);
  return unwrap(data);
};

export const createBooking = async (payload: CreateBookingPayload) => {
  const { data } = await webHttp.post<ApiResponse<Record<string, unknown>>>("/services/bookings", payload);
  return unwrap(data);
};
