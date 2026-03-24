export type UserRole = "USER" | "ADMIN";

export interface UserProfile {
  id: number;
  role: UserRole;
  phone?: string;
  nickname: string;
  avatar_url?: string;
  gender?: string;
  bio?: string;
  status?: string;
}

export interface LoginResult {
  token: string;
  token_type: string;
  expires_in: number;
  user: UserProfile;
}

export interface ProfileOverview {
  user: UserProfile;
  pet_count: number;
  post_count: number;
  favorite_count: number;
  order_count: number;
  booking_count: number;
  adoption_application_count: number;
  unread_message_count: number;
}
