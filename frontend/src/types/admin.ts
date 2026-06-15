export interface TrendPoint {
  label: string;
  count: number;
}

export interface DashboardOverview {
  user_total: number;
  post_total: number;
  order_total: number;
  booking_total: number;
    pending_post_count: number;
    pending_adoption_count: number;
    pending_booking_count: number;
    order_trend: TrendPoint[];
  booking_trend: TrendPoint[];
}
