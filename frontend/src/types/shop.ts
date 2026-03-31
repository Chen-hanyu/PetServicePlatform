export interface ProductCategory {
  id: number;
  name: string;
  pet_type?: string;
  sort: number;
  status: string;
}

export interface ProductSummary {
  id: number;
  category_id: number;
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
  id: number;
  product_id: number;
  quantity: number;
  product: ProductSummary;
}

export interface CartData {
  items: CartItem[];
  total_amount: number;
}

export interface OrderSummary {
  id: number;
  order_no: string;
  total_amount: number;
  pay_amount: number;
  status: string;
  created_at: string;
}

export interface CreateOrderPayload {
  item_ids: number[];
  receiver_name: string;
  receiver_phone: string;
  receiver_address: string;
  remark?: string;
}
