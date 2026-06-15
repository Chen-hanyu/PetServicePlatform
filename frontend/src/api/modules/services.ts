import { webHttp, unwrap } from "@/api/http";
import type { ApiResponse, PageResult } from "@/types/api";
import type { CreateBookingPayload, MerchantDetail, MerchantSummary, ServiceCategory } from "@/types/service";

type EntityId = string | number;

export const fetchServiceCategories = async () => {
  const { data } = await webHttp.get<ApiResponse<ServiceCategory[]>>("/services/categories");
  return unwrap(data);
};

export const fetchMerchants = async (params: Record<string, string | number | undefined>) => {
  const { data } = await webHttp.get<ApiResponse<PageResult<MerchantSummary>>>("/services/merchants", { params });
  return unwrap(data);
};

export const fetchMerchantDetail = async (merchantId: EntityId) => {
  const { data } = await webHttp.get<ApiResponse<MerchantDetail>>(`/services/merchants/${merchantId}`);
  return unwrap(data);
};

export const createBooking = async (payload: CreateBookingPayload) => {
  const { data } = await webHttp.post<ApiResponse<Record<string, unknown>>>("/services/bookings", payload);
  return unwrap(data);
};

/** 获取我的预约列表 */
export const fetchMyBookings = async (params: Record<string, string | number | undefined>) => {
  const { data } = await webHttp.get<ApiResponse<PageResult<any>>>("/services/bookings", { params });
  return unwrap(data);
};

/** 取消预约 */
export const cancelMyBooking = async (bookingId: EntityId) => {
  const { data } = await webHttp.post<ApiResponse<any>>(`/services/bookings/${bookingId}/cancel`);
  return unwrap(data);
};
