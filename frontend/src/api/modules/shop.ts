import { webHttp, unwrap } from "@/api/http";
import type { ApiResponse, PageResult } from "@/types/api";
import type { CartData, CreateOrderPayload, ProductCategory, ProductDetail, ProductSummary } from "@/types/shop";

export const fetchShopCategories = async () => {
  const { data } = await webHttp.get<ApiResponse<ProductCategory[]>>("/shop/categories");
  return unwrap(data);
};

export const fetchProducts = async (params: Record<string, string | number | undefined>) => {
  const { data } = await webHttp.get<ApiResponse<PageResult<ProductSummary>>>("/shop/products", { params });
  return unwrap(data);
};

export const fetchProduct = async (id: number) => {
  const { data } = await webHttp.get<ApiResponse<ProductDetail>>(`/shop/products/${id}`);
  return unwrap(data);
};

export const fetchCart = async () => {
  const { data } = await webHttp.get<ApiResponse<CartData>>("/shop/cart");
  return unwrap(data);
};

export const addCartItem = async (productId: number, quantity = 1) => {
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
