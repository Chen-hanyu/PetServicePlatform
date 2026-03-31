import { webHttp, unwrap } from "@/api/http";
import type { ApiResponse, PageResult } from "@/types/api";
import type { CreatePostPayload, PostComment, PostDetail, PostSummary } from "@/types/community";

export const fetchPosts = async (params: Record<string, string | number | undefined>) => {
  const { data } = await webHttp.get<ApiResponse<PageResult<PostSummary>>>("/community/posts", { params });
  return unwrap(data);
};

export const fetchPostDetail = async (postId: number) => {
  const { data } = await webHttp.get<ApiResponse<PostDetail>>(`/community/posts/${postId}`);
  return unwrap(data);
};

export const fetchPostComments = async (postId: number) => {
  const { data } = await webHttp.get<ApiResponse<PageResult<PostComment>>>(`/community/posts/${postId}/comments`, {
    params: { page: 1, page_size: 20 }
  });
  return unwrap(data);
};

export const createPost = async (payload: CreatePostPayload) => {
  const { data } = await webHttp.post<ApiResponse<Record<string, unknown>>>("/community/posts", payload);
  return unwrap(data);
};

export const createComment = async (postId: number, content: string) => {
  const { data } = await webHttp.post<ApiResponse<Record<string, unknown>>>(`/community/posts/${postId}/comments`, { content });
  return unwrap(data);
};

export const toggleLike = async (postId: number) => {
  const { data } = await webHttp.post<ApiResponse<Record<string, unknown>>>(`/community/posts/${postId}/like`);
  return unwrap(data);
};
