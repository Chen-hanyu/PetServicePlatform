import { webHttp, unwrap } from "@/api/http";
import type { ApiResponse, PageResult } from "@/types/api";
import type {
  AddressInfo,
  CartData,
  CouponInfo,
  CreateOrderPayload,
  DirectOrderPayload,
  ProductCategory,
  ProductDetail,
  ProductSummary,
  SaveAddressPayload
} from "@/types/shop";

type EntityId = string | number;

export const fetchShopCategories = async () => {
  const { data } = await webHttp.get<ApiResponse<ProductCategory[]>>("/shop/categories");
  return unwrap(data);
};

export const fetchProducts = async (params: Record<string, string | number | undefined>) => {
  const { data } = await webHttp.get<ApiResponse<PageResult<ProductSummary>>>("/shop/products", { params });
  return unwrap(data);
};

export const fetchProduct = async (id: EntityId) => {
  const { data } = await webHttp.get<ApiResponse<ProductDetail>>(`/shop/products/${id}`);
  return unwrap(data);
};

export const fetchCart = async () => {
  const { data } = await webHttp.get<ApiResponse<CartData>>("/shop/cart");
  return unwrap(data);
};

export const fetchAddresses = async () => {
  const { data } = await webHttp.get<ApiResponse<AddressInfo[]>>("/shop/addresses");
  return unwrap(data);
};

export const createAddress = async (payload: SaveAddressPayload) => {
  const { data } = await webHttp.post<ApiResponse<AddressInfo>>("/shop/addresses", payload);
  return unwrap(data);
};

export const updateAddress = async (addressId: EntityId, payload: SaveAddressPayload) => {
  const { data } = await webHttp.put<ApiResponse<AddressInfo>>(`/shop/addresses/${addressId}`, payload);
  return unwrap(data);
};

export const fetchAvailableCoupons = async (amount?: number) => {
  const { data } = await webHttp.get<ApiResponse<CouponInfo[]>>("/shop/coupons/available", {
    params: { amount }
  });
  return unwrap(data);
};

export const addCartItem = async (productId: EntityId, quantity = 1) => {
  const { data } = await webHttp.post<ApiResponse<CartData>>("/shop/cart/items", {
    product_id: productId,
    quantity
  });
  return unwrap(data);
};

export const createOrder = async (payload: CreateOrderPayload) => {
  const { data } = await webHttp.post<ApiResponse<Record<string, unknown>>>("/shop/orders", payload);
  return unwrap(data);
};

export const createDirectOrder = async (payload: DirectOrderPayload) => {
  const { data } = await webHttp.post<ApiResponse<Record<string, unknown>>>("/shop/orders/direct", payload);
  return unwrap(data);
};

/** 获取我的订单列表 */
export const fetchOrders = async (params: Record<string, string | number | undefined>) => {
  const { data } = await webHttp.get<ApiResponse<PageResult<any>>>("/shop/orders", { params });
  return unwrap(data);
};

/** 获取订单详情 */
export const fetchOrderDetail = async (orderId: EntityId) => {
  const { data } = await webHttp.get<ApiResponse<any>>(`/shop/orders/${orderId}`);
  return unwrap(data);
};

export const payOrder = async (orderId: EntityId) => {
  const { data } = await webHttp.post<ApiResponse<any>>(`/shop/orders/${orderId}/pay`);
  return unwrap(data);
};

export const cancelOrder = async (orderId: EntityId) => {
  const { data } = await webHttp.post<ApiResponse<any>>(`/shop/orders/${orderId}/cancel`);
  return unwrap(data);
};

export const confirmOrder = async (orderId: EntityId) => {
  const { data } = await webHttp.post<ApiResponse<any>>(`/shop/orders/${orderId}/confirm`);
  return unwrap(data);
};
