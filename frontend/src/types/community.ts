export interface UserLite {
  id: number;
  nickname: string;
  avatar_url?: string;
  phone?: string;
}

export interface PostSummary {
  id: number;
  title: string;
  category: string;
  cover_url?: string;
  excerpt?: string;
  status: string;
  like_count: number;
  favorite_count: number;
  comment_count: number;
  author?: UserLite;
  tags?: string[];
  published_at?: string;
}

export interface PostComment {
  id: number;
  content: string;
  author?: UserLite;
  created_at?: string;
}

export interface PostDetail extends PostSummary {
  content: string;
  images?: string[];
  is_liked?: boolean;
  is_favorited?: boolean;
}

export interface CreatePostPayload {
  title: string;
  content: string;
  category: string;
  images?: string[];
  tag_ids?: number[];
}
