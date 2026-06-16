export type EntityId = string | number;

export interface ProductCategory {
  id: EntityId;
  name: string;
  pet_type?: string;
  sort: number;
  status: string;
}

export interface ProductSummary {
  id: EntityId;
  category_id: EntityId;
  name: string;
  subtitle?: string;
  image_url?: string;
  price: number;
  stock: number;
  pet_type?: string;
  status: string;
}

export interface ProductDetail extends ProductSummary {
  images: string[];
  description?: string;
}

export interface CartItem {
  id: EntityId;
  product_id: EntityId;
  quantity: number;
  product: ProductSummary;
}

export interface CartData {
  items: CartItem[];
  total_amount: number;
}

export interface AddressInfo {
  id: EntityId;
  receiver_name: string;
  receiver_phone: string;
  province: string;
  city: string;
  district: string;
  detail_address: string;
  full_address: string;
  is_default: boolean;
}

export interface SaveAddressPayload {
  receiver_name: string;
  receiver_phone: string;
  province: string;
  city: string;
  district: string;
  detail_address: string;
  is_default?: boolean;
}

export interface CouponInfo {
  user_coupon_id: EntityId;
  coupon_id: EntityId;
  name: string;
  type: string;
  discount_amount: number;
  min_amount: number;
  start_at?: string;
  end_at?: string;
  status: string;
  available: boolean;
  reason?: string;
}

export interface OrderSummary {
  id: EntityId;
  order_no: string;
  total_amount: number;
  discount_amount?: number;
  pay_amount: number;
  status: string;
  created_at: string;
}

export interface CreateOrderPayload {
  item_ids: EntityId[];
  address_id?: EntityId;
  coupon_id?: EntityId;
  receiver_name: string;
  receiver_phone: string;
  receiver_address: string;
  remark?: string;
}

export interface DirectOrderPayload {
  items: Array<{
    product_id: EntityId;
    quantity: number;
  }>;
  address_id?: EntityId;
  coupon_id?: EntityId;
  receiver_name: string;
  receiver_phone: string;
  receiver_address: string;
  remark?: string;
}
